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
import org.apache.commons.fileupload.*;
import com.cromoteca.util.*;

/**
 * Stores info about the web application and provides methods used widely in
 * many classes.
 */
public class WebApp implements Finals {
  /**
   * Array of the welcome files defined for this web application. Values are
   * fetched from the web.xml file.
   */
  protected String[] welcomeFiles;

  private String userAgent = "MeshCMS";
  private File contextRoot;
  private ServletContext sc;
  private FileTypes fileTypes;
  
  public String versionId;
  public String systemCharset;

  /**
   * Creates an instance to be used for the servlet context specified.
   */
  public WebApp(ServletContext sc) {
    // PLEASE NOTE: the initialization order is important.
    this.sc = sc;
    contextRoot = new File(sc.getRealPath("/"));
    welcomeFiles = WebUtils.getWelcomeFiles(sc);
    fileTypes = new FileTypes(this);
    new AdminDirectoryFinder(this).process();
  }

  /**
   * Returns the File object of the user profiles directory.
   */
  public File getUsersDirectory() {
    return getFile(USERS_PATH);
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
      if (fileTypes.isPage(filePath)) {
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
            log("Can't close file " + writeTo, ex);
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
            log("Can't close file " + writeTo, ex);
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
    File backupDir = getFile(REPOSITORY_PATH.add(filePath));
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
        new File(contextRoot, path.toString());
  }
  
  /**
   * Returns the <code>Path</code> of a file in the context.
   *
   * @see #getFile
   */
  public Path getPath(File file) {
    return new Path(Utils.getRelativePath(contextRoot, file, "/"));
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
   * Returns the real path of a path in the web application.
   * <code>ServletContext.getRealPath()</code> is used to find the real path.
   */
  public String getRealPath(Path path) {
    return sc.getRealPath("/" + path.toString());
  }

  /**
   * Returns true if the given file name is known to be a welcome file name.
   */
  public boolean isWelcomeFileName(String fileName) {
    return Utils.searchString(welcomeFiles, fileName, false) != -1;
  }
  
  /**
   * Returns an array of welcome file names for the current web application.
   */
  public String[] getWelcomeFileNames() {
    return welcomeFiles;
  }

  /**
   * Logs a string by calling <code>ServletContext.log(s)</code>
   */
  public void log(String s) {
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
   * Returns true if the extension of the path is known to denote a type of
   * file that can be edited using the wysiwyg editor.
   */
  public boolean isVisuallyEditable(Path path) {
    return Utils.searchString(configuration.getVisualExtensions(),
        Utils.getExtension(path, false), true) != -1;
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
        "', 'meshcmshelp', 'width=740,height=560,menubar=no,status=yes,toolbar=no,resizable=yes,scrollbars=yes').focus();\">";
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
}
