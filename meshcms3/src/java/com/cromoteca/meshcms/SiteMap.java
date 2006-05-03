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
import com.cromoteca.util.*;
import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.parser.*;

/**
 * Contains the site map.
 */
public class SiteMap extends DirectoryParser implements Finals {
  private WebApp webApp;
  private SortedMap pagesMap;
  private SiteMap oldSiteMap;
  private long lastModified;
  private Map currentWelcomes;
  private List pagesList;
  
  private String[] themeNames;
  private String[] moduleNames;
  private Map pageCache;
  
  /**
   * Creates a new instance of SiteMap
   */
  public SiteMap(WebApp webApp) {
    this.webApp = webApp;
    setRecursive(true);
    setSorted(true);
    setProcessStartDir(true);
    setInitialDir(webApp.getContextRoot());
    setDaemon(true);
    setName("Site map parser");
    pageCache = new HashMap();
  }
  
  protected boolean preProcess() {
    oldSiteMap = webApp.getSiteMap();
    pagesMap = new TreeMap();
    currentWelcomes = new TreeMap();
    return true;
  }
  
  protected boolean processDirectory(File file, Path path) {
    if (!webApp.isSystem(path, false)) {
      Path wPath = path.add(ID_FILE);
      
      if (webApp.getFile(wPath).exists()) {
        webApp.setCMSPath(path);
        return false;
      }
      
      wPath = findCurrentWelcome(path);
      
      if (wPath != null) {
        currentWelcomes.put(path, wPath);
        return true;
      }
    }

    return false;
  }

  protected void processFile(File file, Path path) {
    if (!webApp.getFileTypes().isPage(path)) {
      return;
    }

    Path dirPath = path.getParent();
    Path welcome = (Path) currentWelcomes.get(dirPath);
    
    if (welcome == null) {
      return;
    }
    
    Path parentPath = dirPath.getParent();
    
    if (!parentPath.isRelative() && currentWelcomes.get(parentPath) == null) {
      return;
    }
    
    path = welcome.equals(path) ? dirPath : path;
    PageInfo pageInfo = null;

    if (oldSiteMap != null) {
      pageInfo = oldSiteMap.getPageInfo(path);
    }

    if (pageInfo == null) {
      pageInfo = new PageInfo(webApp, path);
    }

    if (pageInfo.getLastModified() != file.lastModified()) {
      FastPageParser fpp = new FastPageParser();
      String charset = pageInfo.getCharset();
      
      if (charset == null) {
        charset = webApp.getConfiguration().getPreferredCharset();
      }
      
      Reader reader = null;

      try {
        reader = new BufferedReader
            (new InputStreamReader(new FileInputStream(file), charset));
        Page page = fpp.parse(reader);
        String title = page.getTitle();

        if (Utils.isNullOrWhitespace(title)) {
          title = Utils.beautify(Utils.removeExtension(path), true);
        }

        pageInfo.setTitle(title);
        pageInfo.setLastModified(file.lastModified());

        String[] pKeys = page.getPropertyKeys();
        String pageCharset = null;
        
        for (int i = 0; i < pKeys.length; i++) {
          if (pKeys[i].toLowerCase().indexOf("content-type") != -1) {
            pageCharset = WebUtils.parseCharset(page.getProperty(pKeys[i]));
          }
        }
        
        pageInfo.setCharset(pageCharset);
      } catch (Exception ex) {
        pageInfo.setTitle(Utils.beautify(path.getLastElement(), true));
        pageInfo.setLastModified(0L);
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException ex) {
            webApp.log("Can't close file " + file, ex);
          }
        }
      }
    }

    pagesMap.put(path, pageInfo);
  }

  protected void postProcess() {
    pagesMap = Collections.unmodifiableSortedMap(pagesMap);
    oldSiteMap = null;
    setLastModified();
    webApp.setSiteMap(this);
    
    pagesList = new ArrayList(pagesMap.values());
    Collections.sort(pagesList, new PageInfoComparator(webApp));
    pagesList = Collections.unmodifiableList(pagesList);
  }
  
  /**
   * Returns the <code>PageInfo</code> for the given path.
   */
  public PageInfo getPageInfo(Path path) {
    return (PageInfo) pagesMap.get(getPathInMenu(path));
  }
  
  /**
   * Returns the given path unless it is the current welcome file in its
   * folder; in this case the folder path is returned.
   */
  public Path getPathInMenu(Path path) {
    return currentWelcomes.containsValue(path) ? path.getParent() : path;
  }
  
  /**
   * Returns the given path unless it is a folder with a welcome file; in this
   * case the welcome file path is returned.
   */
  public Path getServedPath(Path path) {
    Path welcome = (Path) currentWelcomes.get(path);
    return (welcome == null) ? path : welcome;
  }

  /**
   * Returns the path of the welcome file for the given directory path. This
   * method returns null if the path is not a directory or if there is no
   * welcome file into it.
   */
  public Path getCurrentWelcome(Path dirPath) {
    return (Path) currentWelcomes.get(dirPath);
  }

  /**
   * Checks if the given path is the welcome file for its directory.
   */
  public boolean isCurrentWelcome(Path path) {
    return currentWelcomes.containsValue(path);
  }

  /**
   * Returns the current welcome file path for the given folder. If there is no
   * welcome file in that folder, this method returns null.
   */
  public Path findCurrentWelcome(Path dirPath) {
    File dirFile = webApp.getFile(dirPath);

    if (dirFile.isDirectory()) {
      for (int i = 0; i < webApp.welcomeFiles.length; i++) {
        Path wPath = dirPath.add(webApp.welcomeFiles[i]);

        if (webApp.getFile(wPath).exists()) {
          return wPath;
        }
      }
    }

    return null;
  }

  /**
   * Sets the last modification time to the current time.
   */
  void setLastModified() {
    lastModified = System.currentTimeMillis();
  }
  
  /**
   * Returns the last modification time.
   */
  public long getLastModified() {
    return lastModified;
  }
  
  /**
   * Returns the code needed to create a menu or a tree with the scripts
   * created by <a href="http://www.softcomplex.com/">SoftComplex</a>.
   *
   * @param contextPath the context path as returned from
   * <code>HttpServletRequest.getContextPath()</code>
   * @param path the root path for the menu (if null, the root path is used)
   * @param tree true to get the items for a tree, false to get
   * the items for a menu
   *
   * @return a string that can be used as content for the menu_items.js file
   * needed by those scripts
   */
  public String getTigraItems(String contextPath, Path path, boolean tree) {
    if (path == null) {
      path = new Path();
    }
    
    StringBuffer sb = new StringBuffer();

    sb.append("var ").append(tree ? "TREE" : "MENU").append("_ITEMS = [");

    int baseLevel = path.getElementCount() + 1;
    SiteInfo siteInfo = webApp.getSiteInfo();
    Iterator iter = getPagesList(path).iterator();
    PageInfo current;
    PageInfo previous = null;
    int level;

    do {
      if (iter.hasNext()) {
        current = (PageInfo) iter.next();
        level = Math.max(current.getLevel(), baseLevel);
      } else {
        current = null;
        level = baseLevel;
      }

      if (previous != null) {
        int previousLevel = Math.max(previous.getLevel(), baseLevel);

        for (int j = level; j > previousLevel; j--) {
          // qui niente
        }

        if (level <= previousLevel) {
          sb.append(", null]");
        }

        for (int j = previousLevel - 1; j >= level; j--) {
          sb.append("\n");

          for (int k = baseLevel; k <= j; k++) {
            sb.append("  ");
          }

          sb.append("]");
        }

      }

      if (current != null) {
        if (previous != null) {
          sb.append(",");
        }

        sb.append("\n");

        for (int j = baseLevel; j <= level; j++) {
          sb.append("  ");
        }

        sb.append("['");
        sb.append(Utils.escapeSingleQuotes(siteInfo.getPageTitle(current)));
        sb.append("', ");
        String link = current.getLink();
        sb.append(link == null ? "null" : "'" + contextPath + link + "'");

        if (!tree) {
          sb.append(", null");
        }

        previous = current;
      }
    } while (current != null);

    sb.append("\n];");
    return sb.toString();
  }

  /**
   * Returns the pages contained in the menu as a SortedMap, using the given
   * path as root. All keys are of type <code>Path</code> and all values are of
   * type <code>PageInfo</code>. Note that the ordering of the map is the
   * natural order of <code>Path</code>.
   *
   * @see com.cromoteca.util.Path
   */
  public SortedMap getPagesMap(Path root) {
    if (root == null) {
      root = new Path();
    }
    
    return root.isRoot() ? pagesMap : pagesMap.subMap(root, root.successor());
  }
  
  /**
   * Returns true if there is at least one page whose parent path is the
   * given one.
   */
  public boolean hasChildrenPages(Path path) {
    if (!webApp.isDirectory(path)) {
      return false;
    }
    
    int n = path.getElementCount() + 1;
    SortedMap map = getPagesMap(path);
    
    if (map != null) {
      Iterator iter = map.keySet().iterator();
      
      while (iter.hasNext()) {
        Path p = (Path) iter.next();
        
        if (p.getElementCount() == n) {
          return true;
        }
      }
    }
    
    return false;
  }

  /**
   * Returns a list of pages contained in the directory that contains the given
   * path; if the path denotes a directory, its contents are returned.
   *
   * @param includeDir if true, the directory itself is included in the list
   */
  public List getPagesInDirectory(Path path, boolean includeDir) {
    PageInfo rootPage = getPageInfo(webApp.getDirectory(path));
    int idx = pagesList.indexOf(rootPage);
    
    if (idx < 0) {
      return null;
    }
    
    List list = new ArrayList();
    
    if (includeDir) {
      list.add(rootPage);
    }
    
    for (int i = idx + 1; i < pagesList.size(); i++) {
      PageInfo pi = (PageInfo) pagesList.get(i);
      int n = pi.getLevel() - rootPage.getLevel();
      
      if (n <= 0) {
        break;
      } else if (n == 1) {
        list.add(pi);
      }
    }
    
    return list;
  }
  
  /**
   * Returns the pages contained in the menu as a unmodifiable List.
   * All members of the list are of type <code>PageInfo</code>.
   * Pages are sorted using a {@link PageInfoComparator}.
   */
  public List getPagesList() {
    return pagesList;
  }
  
  /**
   * Returns the pages contained in the menu as a unmodifiable List, using the given path as
   * root path. All members of the list are of type <code>PageInfo</code>.
   * Pages are sorted using a {@link PageInfoComparator}.
   */
  public List getPagesList(Path root) {
    root = webApp.getDirectory(root);

    if (root.isRoot()) {
      return pagesList;
    }

    PageInfo rootPage = getPageInfo(root);
    int idx = pagesList.indexOf(rootPage);
    
    if (idx < 0) {
      return null;
    }
    
    int rootLevel = rootPage.getLevel();
    
    for (int i = idx + 1; i < pagesList.size(); i++) {
      if (((PageInfo) pagesList.get(i)).getLevel() <= rootLevel) {
        return pagesList.subList(idx, i);
      }
    }
    
    return pagesList.subList(idx, pagesList.size());
  }

  /**
   * Returns the breadcrumbs from the root path (included) to the given path
   * (<em>not</em> included).
   */
  public PageInfo[] getBreadcrumbs(Path path) {
    path = getPathInMenu(path);
    List list = new ArrayList();

    for (int i = 0; i < path.getElementCount(); i++) {
      Path partial = path.getPartial(i);
      PageInfo pi = getPageInfo(partial);

      if (pi != null) {
        list.add(pi);
      } else if (partial.equals(webApp.getAdminPath())) {
        PageInfo api = new PageInfo(webApp, webApp.getAdminPath());
        api.setTitle("MeshCMS");
        list.add(api);
      }
    }

    if (list.size() != 0) {
      return (PageInfo[]) list.toArray(new PageInfo[list.size()]);
    }
    
    return null;
  }
  
  /**
   * Returns an array of the names of all available themes.
   */
  public String[] getThemeNames() {
    if (themeNames == null) {
      themeNames = webApp.getFile(webApp.getThemesPath()).list();
      
      if (themeNames == null) {
        themeNames = new String[0];
      } else {
        Arrays.sort(themeNames);
      }
    }

    return themeNames;
  }

  /**
   * Returns an array of the names of all available modules.
   */
  public String[] getModuleNames() {
    if (moduleNames == null) {
      List paths = new ArrayList();
      File[] customModules =
          webApp.getFile(webApp.getModulesPath()).listFiles();

      if (customModules != null) {
        for (int i = 0; i < customModules.length; i++) {
          if (new File(customModules[i], MODULE_INCLUDE_FILE).exists()) {
            paths.add(customModules[i].getName());
          }
        }
      }

      moduleNames = (String[]) paths.toArray(new String[paths.size()]);
      Arrays.sort(moduleNames);
    }
    
    return moduleNames;
  }

  /**
   * Caches a page.
   */
  public void cache(Path path, byte[] b) {
    pageCache.put(path, b);
  }
  
  /**
   * Gets a page from the cache.
   */
  public byte[] getCached(Path path) {
    return (byte[]) pageCache.get(path);
  }
  
  /**
   * Check if a page is available in the cache.
   */
  public boolean isCached(Path path) {
    return pageCache.containsKey(path);
  }
}
