/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2006 Luciano Vernaschi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * You can contact the author at http://www.cromoteca.com
 * and at info@cromoteca.com
 */

package com.cromoteca.meshcms;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.fileupload.*;
import com.cromoteca.util.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.*;

public class WebSite implements Finals {
  public static final String VERSION_ID = "3.0 alpha";
  public static final String SYSTEM_CHARSET =
      System.getProperty("file.encoding", "UTF-8");

  protected ServletContext sc;
  protected String[] welcomeFiles;

  protected Path rootPath;
  protected Path adminPath;
  protected Path cmsPath;
  protected File rootFile;
  protected long statsZero;
  protected int statsLength;
  protected Configuration configuration;
  protected SiteInfo siteInfo;
  protected SiteMap siteMap;

  protected WebSite() {
    //
  }
  
  protected static WebSite create(ServletContext sc,
      String[] welcomeFiles, File rootFile, Path rootPath, Path cmsPath) {
    WebSite webSite = new WebSite();
    webSite.init(sc, welcomeFiles, rootFile, rootPath, cmsPath);
    return webSite;
  }
  
  protected void init(ServletContext sc, String[] welcomeFiles, File rootFile,
      Path rootPath, Path cmsPath) {
    this.sc = sc;
    this.welcomeFiles = welcomeFiles;
    this.rootFile = rootFile;
    this.rootPath = rootPath;
    this.cmsPath = cmsPath;
    
    // PLEASE NOTE: the initialization order is important.
    if (cmsPath != null) {
      adminPath = cmsPath.add(ADMIN_SUBDIRECTORY);
      configuration = Configuration.load(this);
      statsLength = configuration.getStatsLength();
      updateSiteMap(true);
      siteInfo = SiteInfo.load(this);
    }
  }
  
  protected ServletContext getServletContext() {
    return sc;
  }
  
  /**
   * Creates another instance of <code>SiteMap</code>. If <code>force</code>
   * is true, a new site map is always created and the method
   * returns after the new site map is completed. If it is false, a new site map
   * is created only if the current one is too old. In this case, the site map
   * is created asynchronously and the method returns immediately. The
   * repository will be cleaned too.
   */
  public void updateSiteMap(boolean force) {
    if (force) {
      new SiteMap(this).process();
    } else if (System.currentTimeMillis() - siteMap.getLastModified() >
               configuration.getUpdateIntervalMillis()) {
      new SiteMap(this).start();
      new DirectoryCleaner(getFile(cmsPath.add(REPOSITORY_SUBDIRECTORY)),
          configuration.getBackupLifeMillis()).start();
      new DirectoryCleaner(getFile(cmsPath.add(GENERATED_FILES_SUBDIRECTORY)),
          configuration.getBackupLifeMillis()).start();
    }
  }

  void setSiteMap(SiteMap siteMap) {
    this.siteMap = siteMap;
  }
  
  /**
   * Returns the instance of the <code>SiteMap</code> that is currently manage
   * the site map. Since this object can be replaced with a new one at any
   * moment, a class that wants to use it should store it in a local variable
   * and use it for all the operation/method.
   */
  public SiteMap getSiteMap() {
    return siteMap;
  }
  
  /**
   * Returns the current configuration of the web application.
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Returns the instance of the <code>SiteInfo</code> class that is managing
   * the site information.
   *
   * @see SiteInfo
   */
  public SiteInfo getSiteInfo() {
    return siteInfo;
  }

  /**
   * Returns the index of the current day in the array of stats included in any
   * PageInfo instance.
   */
  public int getStatsIndex() {
    long now = System.currentTimeMillis();
    long days = (now - statsZero) / LENGTH_OF_DAY;

    if (days >= statsLength) {
      statsZero = now;
      return 0;
    } else {
      return (int) days;
    }
  }

/* TODO: rimuovere questi 4 metodi */
  /**
   * Returns the length of stats (hit counts) measured in days.
   */
  public int getStatsLength() {
    return statsLength;
  }
  
  /**
   * Returns the File object of the user profiles directory.
   */
  public File getUsersDirectory() {
    return getFile(cmsPath.add(USERS_SUBDIRECTORY));
  }

  /**
   * Creates a new directory.
   *
   * @param user the user that requests the creation of the directory
   * @param dirPath the path of the new directory
   *
   * @return true if the directory has been created or already
   * existed, false otherwise
   */
  public boolean createDirectory(UserInfo user, Path dirPath) {
    File newDir = getFile(dirPath);

    if (newDir.isDirectory()) {
      return true;
    }

    return user != null && user.canWrite(this, dirPath) && newDir.mkdir();
  }

  /**
   * Creates a new file. If the extension of the file denotes a web page, the
   * basic template is copied into the file, otherwise an empty file is created.
   *
   * @param user the user that requests the file creation
   * @param filePath the path of the new file
   *
   * @return true if the new file has been created or already existed,
   * false otherwise
   */
  public boolean createFile(UserInfo user, Path filePath) {
    if (user == null || !user.canWrite(this, filePath)) {
      return false;
    }

    File newFile = getFile(filePath);

    if (newFile.exists()) {
      return !newFile.isDirectory();
    }

    try {
      if (FileTypes.isPage(filePath)) {
        if (newFile.exists()) {
          return false;
        }
        
        return saveToFile(user, getHTMLTemplate(null), filePath, null);
      } else {
        return newFile.createNewFile();
      }
    } catch (IOException ex) {
      sc.log("Can't create file " + newFile, ex);
    }

    return false;
  }

  /**
   * Copies a file to another file in the same directory. An existing file won't
   * be overwritten.
   *
   * @param user the user that requests the operation
   * @param filePath the path of the old file
   * @param newName the name of the new file
   *
   * @return true if the new file has been copied, false otherwise
   */
  public boolean copyFile(UserInfo user, Path filePath, String newName) {
    return copyFile(user, filePath, filePath.getParent().add(newName));
  }

  /**
   * Copies a file to another file. An existing file won't be overwritten.
   *
   * @param user the user that requests the operation
   * @param oldPath the location of the existing file
   * @param newPath the location of the new copy of the file
   *
   * @return true if the new file has been copied, false otherwise
   */
  public boolean copyFile(UserInfo user, Path oldPath, Path newPath) {
    File oldFile = getFile(oldPath);

    if (!oldFile.exists()) {
      return false;
    }

    File newFile = getFile(newPath);

    if (user == null || !user.canWrite(this, newPath)) {
      return false;
    }

    try {
      return Utils.copyFile(oldFile, newFile, false, false);
    } catch (IOException ex) {
      sc.log("Can't copy file " + oldFile + " to file " + newFile, ex);
    }

    return false;
  }

  /**
   * Renames a file.
   *
   * @param user the user that requests the operation
   * @param filePath the path of the file
   * @param newName the name of the new file
   *
   * @return true if the new file has been renamed, false otherwise
   */
  public boolean rename(UserInfo user, Path filePath, String newName) {
    return move(user, filePath, filePath.getParent().add(newName));
  }

  /**
   * Moves (or renames) a file.
   *
   * @param user the user that requests the operation
   * @param oldPath the current location of the file
   * @param newPath the new location of the file
   *
   * @return true if the new file has been moved, false otherwise
   */
  public boolean move(UserInfo user, Path oldPath, Path newPath) {
    File oldFile = getFile(oldPath);

    if (!oldFile.exists()) {
      return false;
    }

    if (newPath.isContainedIn(oldPath)) {
      return false;
    }

    File newFile = getFile(newPath);

    if (user == null ||
        !(user.canWrite(this, oldPath) && user.canWrite(this, newPath))) {
      return false;
    }

    if (Utils.forceRenameTo(oldFile, newFile, false)) {
      return true;
    } else {
      sc.log("Can't move file " + oldFile + " to file " + newFile);
    }

    return false;
  }

  /**
   * Deletes a file.
   *
   * @param user the user that requests the operation
   * @param filePath the path of the file
   *
   * @return true if the file has been deleted, false otherwise
   */
  public boolean delete(UserInfo user, Path filePath) {
    if (user == null || !user.canWrite(this, filePath)) {
      return false;
    }

    File file = getFile(filePath);

    if (!file.exists()) {
      return false;
    }

    if (file.isDirectory()) {
      return file.delete();
    } else {
      return backupFile(user, filePath);
    }
  }
  
  /**
   * Sets the last modified date of the file to the current time.
   *
   * @param user the user that requests the operation
   * @param filePath the path of the file
   *
   * @return true if the date has been changed, false otherwise
   */
  public boolean touch(UserInfo user, Path filePath) {
    if (user == null || !user.canWrite(this, filePath)) {
      return false;
    }

    File file = getFile(filePath);

    if (!file.exists()) {
      return false;
    }
    
    file.setLastModified(System.currentTimeMillis());
    return true;
  }

  /**
   * Stores an object into a file. Supported objects are:
   *
   * <ul>
   *  <li>byte arrays</li>
   *  <li>input streams</li>
   *  <li><code>org.apache.commons.fileupload.FileItem</code>
   *      (uploaded files)</li>
   *  <li>generic objects. The <code>toString()</code> method is used in this
   *      cases. This is compatible with many kinds of objects: strings,
   *      string buffers and so on.</li>
   * </ul>
   *
   * @param user the user that requests the operation
   * @param saveThis the object to be stored in the file
   * @param filePath the path of the file to be written. If the file exists, it
   * will be backed up and overwritten
   *
   * @return true if the operation has been completed successfully,
   * false otherwise
   */
  public boolean saveToFile(UserInfo user, Object saveThis, Path filePath,
      String charset) {
    if (user == null || !user.canWrite(this, filePath)) {
      return false;
    }

    File file = getFile(filePath);
    File dir = file.getParentFile();
    dir.mkdirs();
    File tempFile = null;

    String fileName = file.getName();
    int dot = fileName.lastIndexOf('.');
    String fileExt = (dot == -1) ? ".bak" : fileName.substring(dot);

    if (file.exists()) {
      tempFile = getRepositoryFile(filePath, TEMP_PREFIX +
          System.currentTimeMillis() + fileExt);
    }

    File writeTo = tempFile == null ? file : tempFile;

    if (saveThis instanceof byte[]) {
      byte[] b = (byte[]) saveThis;
      FileOutputStream fos = null;

      try {
        fos = new FileOutputStream(writeTo);
        fos.write(b);
      } catch (IOException ex) {
        sc.log("Can't write byte array to file " + writeTo, ex);
        return false;
      } finally {
        if (fos != null) {
          try {
            fos.close();
          } catch (IOException ex) {
            sc.log("Can't close file " + writeTo, ex);
          }
        }
      }
    } else if (saveThis instanceof InputStream) {
      try {
        InputStream is = (InputStream) saveThis;
        Utils.copyStream(is, new FileOutputStream(writeTo), true);
        is.close();
      } catch (Exception ex) {
        sc.log("Can't write stream to file " + writeTo, ex);
        return false;
      }
    } else if (saveThis instanceof FileItem) {
      try {
        ((FileItem) saveThis).write(writeTo);
      } catch (Exception ex) {
        sc.log("Can't write uploaded file to file " + writeTo, ex);
        return false;
      }
    } else {
      Writer writer = null;
      
      if (Utils.isNullOrEmpty(charset)) {
        charset = getConfiguration().getPreferredCharset();
      }

      try {
        writer = new OutputStreamWriter(new FileOutputStream(writeTo), charset);
        writer.write(saveThis.toString());
      } catch (IOException ex) {
        sc.log("Can't write generic object to file " + writeTo, ex);
        return false;
      } finally {
        if (writer != null) {
          try {
            writer.close();
          } catch (IOException ex) {
            sc.log("Can't close file " + writeTo, ex);
          }
        }
      }
    }

    if (tempFile != null) {
      if (!backupFile(user, filePath)) {
        return false;
      }

      if (!Utils.forceRenameTo(tempFile, file, true)) {
        sc.log("Can't rename temporary file " + tempFile + " to file " + file);
        return false;
      }
    }

    return true;
  }
  
  /**
   * Returns the correct file in the repository. For example, if you need to
   * create a temporary copy of /somedir/index.html, you could use
   * <code>filePath = /somedir/index.html</code> and
   * <code>fileName = tmp.html</code>.
   */
  public File getRepositoryFile(Path filePath, String fileName) {
    File backupDir = getFile(cmsPath.add(REPOSITORY_SUBDIRECTORY, filePath));
    backupDir.mkdirs();
    return new File(backupDir, fileName);
  }

  private boolean backupFile(UserInfo user, Path filePath) {
    /* permissions already checked in methods that call this one
    if (user == null || !user.canWrite(this, filePath)) {
      return false;
    }
    */
    
    File file = getFile(filePath);
    String fileName = file.getName();
    int dot = fileName.lastIndexOf('.');
    String fileExt = (dot == -1) ? ".bak" : fileName.substring(dot);

    File bakFile = getRepositoryFile(filePath, BACKUP_PREFIX +
        user.getUsername() + "_" +
        WebUtils.numericDateFormatter.format(new Date()) + fileExt);

    if (Utils.forceRenameTo(file, bakFile, true)) {
      return true;
    }

    sc.log("Can't backup path " + filePath);
    return false;
  }

  /**
   * Returns the file object for a given path in the web application. The file
   * is not checked for existance.
   */
  public File getFile(Path path) {
    return (path == null || path.isRelative()) ? null :
        new File(rootFile, path.toString());
  }
  
  public File getRootFile() {
    return rootFile;
  }
  
  /**
   * Returns the site root path.
   */
  public Path getRootPath() {
    return rootPath;
  }

  /**
   * Returns the <code>Path</code> of a file in the context.
   *
   * @see #getFile
   */
  public Path getPath(File file) {
    return new Path(Utils.getRelativePath(rootFile, file, "/"));
  }
  
  public Path getRequestedPath(HttpServletRequest request) {
    return new Path(request.getServletPath());
  }
  
  public Path getServedPath(HttpServletRequest request) {
    return new Path(request.getServletPath());
  }
  
  public Path getServedPath(Path requestedPath) {
    return requestedPath;
  }
  
  /**
   * Checks if the given path is a directory in the file system.
   */
  public boolean isDirectory(Path path) {
    return getFile(path).isDirectory();
  }

  /**
   * Returns the directory that contains the given path. This is different from
   * {@link com.cromoteca.util.Path#getParent}, since if the path is known to
   * be a directory in the web application, the path itself is returned.
   */
  public Path getDirectory(Path path) {
    // PageInfo pageInfo = getPageInfo(path);
    // return pageInfo.isDirectory() ? path : pageInfo.getPath().getParent();
    if (path == null) {
      return null;
    }

    if (getFile(path).isDirectory()) {
      return path;
    }

    path = path.getParent();

    if (!path.isRelative() && getFile(path).isDirectory()) {
      return path;
    }

    return null;
  }

  /**
   * Returns an array of links to the given pages. The strings are in the form
   * <pre>&lt;a href="(page link)" [target] [style]&gt;(page title)&lt;/a&gt;</pre>
   *
   * @param pages the array of pages
   * @param contextPath the context path of the web application (used to build
   * the links)
   * @param target the target frame for the links. If a value is given, the
   * <code>target</code> attribute is added to the <code>a</code> tag
   * @param style the CSS style for the links. If a value is given, the
   * <code>class</code> attribute is added to the <code>a</code> tag
   *
   * @return an array of strings that contain the <code>a</code> tags
   */
  public String[] getLinkList(PageInfo[] pages, String contextPath,
                              String target, String style) {
    if (pages == null) {
      return null;
    }

    String[] links = new String[pages.length];
    target = Utils.isNullOrEmpty(target) ? "" : " target=\"" + target + "\"";
    style = Utils.isNullOrEmpty(style) ? "" : " class=\"" + style + "\"";

    for (int i = 0; i < pages.length; i++) {
      if (pages[i] == null) {
        links[i] = "...";
      } else {
        links[i] = "<a href=\"" + contextPath + pages[i].getLink() +
          "\"" + target + style + ">" + siteInfo.getPageTitle(pages[i]) + "</a>";
      }
    }

    return links;
  }

  /**
   * Returns an array of menu titles for the given pages.
   * {@link SiteInfo} is used to get the titles.
   */
  public String[] getTitles(PageInfo[] pages) {
    if (pages == null) {
      return null;
    }

    String[] titles = new String[pages.length];

    for (int i = 0; i < pages.length; i++) {
      titles[i] = siteInfo.getPageTitle(pages[i]);
    }

    return titles;
  }

  /**
   * Determines if the given path is a system directory, or is contained in a
   * system directory. System directories are:
   *
   * <ul>
   *  <li>the WEB-INF directory (/WEB-INF)</li>
   *  <li>the META-INF directory (/META-INF)</li>
   *  <li>the standard CGI-BIN directory (/cgi-bin)</li>
   *  <li>the MeshCMS admin directory (if <code>checkAdmin</code> is true</li>
   * </ul>
   */
  public boolean isSystem(Path path, boolean checkAdmin) {
    if (path == null || path.isRoot()) {
      return false;
    }
    
    if (path.isRelative() || (checkAdmin && path.isContainedIn(getAdminPath()))) {
      return true;
    }
    
    String level1 = path.getElementAt(0).toLowerCase();
    
    return level1.equals("web-inf") || level1.equals("meta-inf") ||
        level1.equals("cgi-bin");
  }

  /**
   * Returns true if the extension of the path is known to denote a type of
   * file that can be edited using the wysiwyg editor.
   */
  public boolean isVisuallyEditable(Path path) {
    return Utils.searchString(configuration.getVisualExtensions(),
        Utils.getExtension(path, false), true) != -1;
  }
  
  /**
   * Returns the path of the module file with the given name.
   */
  public Path getModulePath(String moduleName) {
    if (moduleName.endsWith(".jsp")) {
      // old module names were in the form module_name.jsp
      moduleName.substring(0, moduleName.length() - 4);
    }
    
    return (Path) siteMap.getModulesMap().get(moduleName);
  }

  /**
   * Returns the path of the admin directory.
   */
  public Path getAdminPath() {
    return adminPath;
  }
  
  public Path getCMSPath() {
    return cmsPath;
  }
  
  public boolean isMainSite() {
    return rootPath.isRoot();
  }
  
  /**
   * Returns the complete tag used by pages in the admin folder. This way those
   * pages can set to be themed according to the site preferences (i.e. using
   * a custom theme or the default admin theme).
   */
  public String getAdminMetaThemeTag() {
    Path themePath = getSiteInfo().getThemePath(getAdminPath());
    return "<meta name=\"decorator\" content=\"/" + themePath + "/" + 
        THEME_DECORATOR + "\" />";
  }
  
  public String getDummyMetaThemeTag() {
    return "<meta name=\"decorator\" content=\"/" + adminPath + "/theme/dummy.jsp\" />";
  }
  
  /**
   * Creates the HTML used to display the help icon in the admin pages.
   */
  public String helpIcon(String contextPath, String anchor, UserInfo userInfo) {
    Path helpPath = adminPath.add("help");
    String lang = "en";
    
    if (userInfo != null) {
      String otherLang = userInfo.getPreferredLocaleCode();
      
      if (getFile(helpPath.add(otherLang)).exists()) {
        lang = otherLang;
      }
    }
    
    return "<img src='" + contextPath + '/' + adminPath +
        "/tiny_mce/themes/advanced/images/help.gif' title='Help: " + anchor +
        "' alt='Help Icon' onclick=\"javascript:window.open('" + 
        contextPath + '/' + helpPath + '/' + lang + "/userguide.html#" + anchor +
        "', 'meshcmshelp', 'width=740,height=560,menubar=no,status=yes,toolbar=no,resizable=yes,scrollbars=yes').focus();\" \\>";
  }
  
  /**
   * Returns a string containing a basic HTML page.
   *
   * @param pageTitle the content of the &lt;title&gt; tag (if null, the title
   * will be &quot;New Page&quot;)
   */
  public String getHTMLTemplate(String pageTitle) throws IOException {
    String text = Utils.readFully(getFile(getAdminPath().add("template.html")));
    
    if (pageTitle != null) {
      int idx = text.indexOf("New Page");

      if (idx != -1) {
        text = text.substring(0, idx) + pageTitle + text.substring(idx + 8);
      }
    }
    
    int idx = text.indexOf("utf-8");
    
    if (idx != -1) {
      text = text.substring(0, idx) + 
          getConfiguration().getPreferredCharset() +
          text.substring(idx + 5);
    }

    return text;
  }

  public boolean isInsideModules(Path path) {
    return path.isContainedIn(cmsPath.add(MODULES_SUBDIRECTORY)) ||
        path.isContainedIn(adminPath.add(MODULES_SUBDIRECTORY));
  }
  
  public boolean isInsideThemes(Path path) {
    return path.isContainedIn(cmsPath.add(THEMES_SUBDIRECTORY)) ||
        path.isContainedIn(adminPath.add(THEMES_SUBDIRECTORY));
  }

  /**
   * Logs a string by calling <code>ServletContext.log(s)</code>
   */
  protected void log(String s) {
    sc.log(s);
  }

  /**
   * Logs an exception by calling
   * <code>ServletContext.log(message, throwable)</code>
   */
  public void log(String message, Throwable throwable) {
    sc.log(message, throwable);
  }

  /**
   * Returns true if the given file name is known to be a welcome file name.
   */
  public boolean isWelcomeFileName(String fileName) {
    return Utils.searchString(welcomeFiles, fileName, false) != -1;
  }
  
  /**
   * Returns an array of welcome file names for the current web application.
   * Values are fetched from the web.xml file.
   */
  public String[] getWelcomeFileNames() {
    return welcomeFiles;
  }
  
  public WebSite getWebSite(ServletRequest request) {
    return this;
  }

  public HttpServletRequest wrapRequest(ServletRequest request) {
    return (HttpServletRequest) request;
  }
  
  public String getName() {
    return "single web site";
  }
  
  public String toString() {
    return "Name: " + getName() + "; path: /" + rootPath + "; CMS: " +
        (cmsPath == null ? "disabled" : "enabled on path /" + cmsPath);
  }

  protected Object loadFromXML(Path path) {
    File file = getFile(path);
    
    if (file.exists()) {
      InputStream is = null;
      
      try {
        is = new BufferedInputStream(new FileInputStream(file));
        XStream xStream = new XStream(new DomDriver());
        XStreamPathConverter pConv = new XStreamPathConverter();
        pConv.setPrependSlash(true);
        xStream.registerConverter(pConv);
        return xStream.fromXML(is);
      } catch (IOException ex) {
        ex.printStackTrace();
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    }
    
    return null;
  }

  protected boolean storeToXML(Object o, Path path) {
    File file = getFile(path);
    OutputStream os = null;

    try {
      os = new BufferedOutputStream(new FileOutputStream(file));
      XStream xStream = new XStream(new DomDriver());
      XStreamPathConverter pConv = new XStreamPathConverter();
      pConv.setPrependSlash(true);
      xStream.registerConverter(pConv);
      xStream.toXML(o, os);
      return true;
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
    
    return false;
  }
}
