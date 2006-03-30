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
 * Stores info about a single website.
 */
public class WebSite implements Finals {
  public static final String ADMIN_DIR = "admin";
  public static final String MODULES_DIR = "modules";
  public static final String THEMES_DIR = "themes";
  public static final String THUMBNAILS_DIR = "thumbnails";
  
  private WebApp webApp;
  private Path adminPath;
  private File rootFile;
  private long statsZero;
  private int statsLength;
  private Configuration configuration;
  private SiteInfo siteInfo;
  private SiteMap siteMap;
  private String location;
  
  public WebSite(WebApp webApp) {
    this.webApp = webApp;
    configuration = Configuration.load(this);
    statsLength = configuration.getStatsLength();
    siteInfo = new SiteInfo(this);
    updateSiteMap(true);
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
      new RepositoryCleaner(this).start();
      new ThumbnailsCleaner(this).start();
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
   * Returns an instance of the class used to manage file types.
   */
  public FileTypes getFileTypes() {
    return fileTypes;
  }
  
  /**
   * Returns the context root directory.
   */
  public File getContextRoot() {
    return contextRoot;
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

  /**
   * Returns the length of stats (hit counts) measured in days.
   */
  public int getStatsLength() {
    return statsLength;
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
   *  <li>the MeshCMS directory (/admin)</li>
   *  <li>the standard CGI-BIN directory (/cgi-bin)</li>
   * </ul>
   */
  public boolean isSystem(Path path) {
    if (path == null || path.isRoot()) {
      return false;
    }
    
    if (path.isContainedIn(getAdminPath()) || path.isRelative()) {
      return true;
    }
    
    String level1 = path.getElementAt(0).toLowerCase();
    
    return level1.equals("web-inf") || level1.equals("meta-inf") ||
        level1.equals("cgi-bin");
  }

  /**
   * Returns the path of the module template file with the given name. The file
   * is first searched in the custom module templates folder, then (if not
   * found there) it is searched in the default module templates folder.
   */
  public Path getModuleTemplatePath(String moduleTemplateName) {
    if (!moduleTemplateName.endsWith(".jsp")) {
      moduleTemplateName += ".jsp";
    }

    if (configuration.getModuleTemplatesDir() != null) {
      Path tPath = new Path(configuration.getModuleTemplatesDir(),
          moduleTemplateName);

      if (getFile(tPath).exists()) {
        return tPath;
      }
    }

    Path tPath = getAdminPath().add(MODULE_TEMPLATES_DIR, moduleTemplateName);
    return getFile(tPath).exists() ? tPath : null;
  }

  /**
   * Returns the path of the admin directory.
   */
  public Path getAdminPath() {
    return adminPath;
  }
  
  /**
   * Sets the path of the admin directory.
   */
  public void setAdminPath(Path adminPath) {
    this.adminPath = adminPath;
  }
  
  /**
   * Returns the complete tag used by pages in the admin folder. This way those
   * pages can set to be themed according to the site preferences (i.e. using
   * a custom theme or the default admin theme).
   */
  public String getAdminMetaThemeTag() {
    Path themePath = getSiteInfo().getThemePath(getAdminPath());
    return "<meta name=\"decorator\" content=\"/" + themePath + "/" + 
           THEME_DECORATOR + "\">";
  }
  
  public String getDummyMetaThemeTag() {
    Path themePath = getSiteInfo().getThemePath(getAdminPath());
    return "<meta name=\"decorator\" content=\"/" + themePath + "/dummy.jsp\">";
  }

  public WebApp getWebApp() {
    return webApp;
  }

  public void setWebApp(WebApp webApp) {
    this.webApp = webApp;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
  
  public File getFile(Path path) {
    return webApp.getFile(new Path(location, path));
  }
  
  public Path getDirectory(Path path) {
    return webApp.getDirectory(new Path(location, path));
  }
  
  public boolean isDirectory(Path path) {
    return webApp.isDirectory(new Path(location, path));
  }
  
  public void log(String s) {
    webApp.log("[Site: " + location + "] " + s);
  }
  
  public void log(String message, Throwable throwable) {
    webApp.log("[Site: " + location + "] " + message, throwable);
  }
  
  /**
   * Converts the requested path into a Path object that points to the file
   * within the website.
   */
  public Path getPath(String requestPath) {
    return getPath(new Path(requestPath));
  }

  /**
   * Converts the requested path into a Path object that points to the file
   * within the website.
   */
  public Path getPath(Path requestPath) {
    if (requestPath.isContainedIn(configuration.getCmsPath())) {
      requestPath = requestPath.getRelativeTo(configuration.getCmsPath());
      String topElement = requestPath.getElementAt(0);
      
      if (topElement.equals("admin")) {
        
      }
    }
  }
}
