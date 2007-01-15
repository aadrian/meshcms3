/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2007 Luciano Vernaschi
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

package org.meshcms.core;

import java.io.*;
import java.util.*;
import org.meshcms.util.*;
import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.parser.*;

/**
 * Contains the site map.
 */
public class SiteMap extends DirectoryParser {
  /**
   * Name of the main file of a theme.
   */
  public static final String THEME_DECORATOR = "main.jsp";
  
  /**
   * Name of the stylesheet of a theme.
   */
  public static final String THEME_CSS = "main.css";
  
  /**
   * Name of the CSS that contains styles for elements of the MeshCMS interface
   * (mail forms, editor and so on).
   */
  public static final String MESHCMS_CSS = "meshcms.css";
  
  public static final String MODULE_INCLUDE_FILE = "include.jsp";
  
  private WebSite webSite;
  private SortedMap pagesMap;
  private SiteMap oldSiteMap;
  private long lastModified;
  private Map currentWelcomes;
  private List pagesList;
  
  private SortedMap themesMap;
  private SortedMap modulesMap;
  private List langList;
  private Map pageCache;
  
  private Map redirCache;
  private Path[] redirPaths;
  
  private boolean obsolete;
  
  /**
   * Creates a new instance of SiteMap
   */
  public SiteMap(WebSite webSite) {
    this.webSite = webSite;
    setRecursive(true);
    setSorted(true);
    setProcessStartDir(true);
    setInitialDir(webSite.getRootFile());
    setDaemon(true);
    setName("Site map parser for \"" + webSite.getTypeDescription() + '"');
    pageCache = new HashMap();
  }
  
  protected boolean preProcess() {
    oldSiteMap = webSite.getSiteMap();
    
    if (oldSiteMap != null && oldSiteMap.isObsolete()) {
      oldSiteMap = null;
    }
    
    pagesMap = new TreeMap();
    currentWelcomes = new TreeMap();
    return true;
  }
  
  protected boolean preProcessDirectory(File file, Path path) {
    if (!webSite.isSystem(path)) {
      Path wPath = webSite.findCurrentWelcome(path);
      
      if (wPath == null && path.isRoot()) {
        String wName = webSite.getWelcomeFileNames()[0];
        
        try {
          Utils.writeFully(new File(file, wName),
              webSite.getHTMLTemplate("Home Page"));
          webSite.getSiteInfo().setPageTheme(Path.ROOT, "default");
          webSite.getSiteInfo().store();
          wPath = new Path(wName);
        } catch (IOException ex) {
          webSite.log("Can't create home page for empty site", ex);
        }
      }
      
      if (wPath != null) {
        currentWelcomes.put(path, wPath);
        return true;
      }
    }
    
    return false;
  }
  
  protected void processFile(File file, Path path) {
    if (!FileTypes.isPage(path.getLastElement())) {
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
      pageInfo = new PageInfo(webSite, path);
    }
    
    if (pageInfo.getLastModified() != file.lastModified()) {
      HTMLPageParser fpp = new HTMLPageParser();
      Reader reader = null;
      
      try {
        reader = new BufferedReader(new FileReader(file));
        Page page = fpp.parse(Utils.readAllChars(reader));
        reader.close();
        String title = page.getTitle();
        
        if (Utils.isNullOrWhitespace(title)) {
          title = Utils.beautify(Utils.removeExtension(path), true);
        }
        
        pageInfo.setTitle(title);
        pageInfo.setLastModified(file.lastModified());
        
        /*
        String[] pKeys = page.getPropertyKeys();
        String pageCharset = null;
         
        for (int i = 0; i < pKeys.length; i++) {
          if (pKeys[i].toLowerCase().indexOf("content-type") != -1) {
            pageCharset = WebUtils.parseCharset(page.getProperty(pKeys[i]));
          }
        }
         */
      } catch (Exception ex) {
        pageInfo.setTitle(Utils.beautify(path.getLastElement(), true));
        pageInfo.setLastModified(0L);
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException ex) {
            webSite.log("Can't close file " + file, ex);
          }
        }
      }
    }
    
    pagesMap.put(path, pageInfo);
  }
  
  protected void postProcess() {
    pagesMap = Collections.unmodifiableSortedMap(pagesMap);
    oldSiteMap = null;
    
    pagesList = new ArrayList(pagesMap.values());
    Collections.sort(pagesList, new PageInfoComparator(this, webSite.getSiteInfo()));
    pagesList = Collections.unmodifiableList(pagesList);
    
    langList = new ArrayList();
    Iterator iter = getPagesInDirectory(Path.ROOT, false).iterator();
    
    while (iter.hasNext()) {
      Path path = ((PageInfo) iter.next()).getPath();
      
      if (path.getElementCount() == 1) {
        Locale locale = Utils.getLocale(path.getElementAt(0));
        
        if (locale != null) {
          langList.add(new CodeLocalePair(path.getElementAt(0), locale));
        }
      }
    }
    
    langList = Collections.unmodifiableList(langList);
    setLastModified();
    webSite.setSiteMap(this);
    // webSite.getSiteInfo().cleanupSiteInfo(); // deprecated
  }
  
  /**
   * @return the <code>PageInfo</code> for the given path.
   */
  public PageInfo getPageInfo(Path path) {
    return (PageInfo) pagesMap.get(getPathInMenu(path));
  }
  
  /**
   * @return the <code>PageInfo</code> for parent of the page at the given path.
   */
  public PageInfo getParentPageInfo(Path path) {
    path = getPathInMenu(path).getParent();
    return path.isRelative() ? null : (PageInfo) pagesMap.get(path);
  }
  
  /**
   * @return the given path unless it is the current welcome file in its
   * folder; in this case the folder path is returned.
   */
  public Path getPathInMenu(Path path) {
    return currentWelcomes.containsValue(path) ? path.getParent() : path;
  }
  
  /**
   * @return the given path unless it is a folder with a welcome file; in this
   * case the welcome file path is returned.
   */
  public Path getServedPath(Path path) {
    Path welcome = (Path) currentWelcomes.get(path);
    return (welcome == null) ? path : welcome;
  }
  
  /**
   * @return the path of the welcome file for the given directory path. This
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
   * Sets the last modification time to the current time.
   */
  void setLastModified() {
    lastModified = System.currentTimeMillis();
  }
  
  /**
   * @return the last modification time.
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
    return getTigraItems(contextPath, path, tree, false);
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
   * @param allowHiding honours the "hide submenu" option
   *
   * @return a string that can be used as content for the menu_items.js file
   * needed by those scripts
   */
  public String getTigraItems(String contextPath, Path path, boolean tree, boolean allowHiding) {
    if (path == null) {
      path = Path.ROOT;
    }
    
    StringBuffer sb = new StringBuffer();
    
    sb.append("var ").append(tree ? "TREE" : "MENU").append("_ITEMS = [");
    
    int baseLevel = path.getElementCount() + 1;
    SiteInfo siteInfo = webSite.getSiteInfo();
    SiteMapIterator iter = new SiteMapIterator(webSite, path);
    iter.setSkipHiddenSubPages(allowHiding);
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
          // nothing here
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
        String link = webSite.getLink(current);
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
   * @return the pages contained in the menu as a SortedMap, using the given
   * path as root. All keys are of type <code>Path</code> and all values are of
   * type <code>PageInfo</code>. Note that the ordering of the map is the
   * natural order of <code>Path</code>.
   *
   * @see org.meshcms.util.Path
   */
  public SortedMap getPagesMap(Path root) {
    if (root == null) {
      root = Path.ROOT;
    }
    
    return root.isRoot() ? pagesMap : pagesMap.subMap(root, root.successor());
  }
  
  public List getLangList() {
    return langList;
  }
  
  /**
   * @return true if there is at least one page whose parent path is the
   * given one.
   */
  public boolean hasChildrenPages(Path path) {
    if (!webSite.isDirectory(path)) {
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
   * @return a list of pages contained in the directory that contains the given
   * path; if the path denotes a directory, its contents are returned.
   *
   * @param includeDir if true, the directory itself is included in the list
   */
  public List getPagesInDirectory(Path path, boolean includeDir) {
    PageInfo rootPage = getPageInfo(webSite.getDirectory(path));
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
   * @return the pages contained in the menu as a unmodifiable List.
   * All members of the list are of type <code>PageInfo</code>.
   * Pages are sorted using a {@link PageInfoComparator}.
   */
  public List getPagesList() {
    return pagesList;
  }
  
  /**
   * @return the pages contained in the menu as a unmodifiable List, using the given path as
   * root path. All members of the list are of type <code>PageInfo</code>.
   * Pages are sorted using a {@link PageInfoComparator}.
   */
  public List getPagesList(Path root) {
    root = webSite.getDirectory(root);
    
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
   * Returns the pages contained in the menu as a unmodifiable List, using the given path as
   * root path. All members of the list are of type <code>PageInfo</code>.
   * Pages are sorted using a {@link PageInfoComparator}.
   * NB: This method excludes hidden submenus.
   *
   * @deprecated use a {@link org.meshcms.core.SiteMapIterator} and set
   * {@link org.meshcms.core.SiteMapIterator#setSkipHiddenSubPages} to
   * <code>true</code> instead.
   */
  public List getPagesListNoHiddenSubmenus(Path root) {
    SiteInfo siteInfo = webSite.getSiteInfo();
    List pagesListHiddenSubmenus = new ArrayList(this.getPagesList(root));
    Path currentHiddenSubmenuPath = null;
    int i = 0;
    while (i < pagesListHiddenSubmenus.size()) {
      Path currentPath = ((PageInfo)pagesListHiddenSubmenus.get(i)).getPath();
      if (currentHiddenSubmenuPath != null) {
        if (currentPath.isContainedIn(currentHiddenSubmenuPath)) {
          pagesListHiddenSubmenus.remove(i);
        } else {
          currentHiddenSubmenuPath = null;
        }
      }
      if (currentHiddenSubmenuPath == null && siteInfo.getHideSubmenu(currentPath)) {
        currentHiddenSubmenuPath = currentPath;
      }
      i++;
    }
    return Collections.unmodifiableList(pagesListHiddenSubmenus);
  }
  
  /**
   * @return the breadcrumbs from the root path (included) to the given path
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
      } else if (partial.equals(webSite.getAdminPath())) {
        PageInfo api = new PageInfo(webSite, webSite.getAdminPath());
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
   * @return an array of the names of all available themes.
   */
  public String[] getThemeNames() {
    Set keys = getThemesMap().keySet();
    return (String[]) keys.toArray(new String[keys.size()]);
  }
  
  /**
   * @return an array of the names of all available modules.
   */
  public String[] getModuleNames() {
    Set keys = getModulesMap().keySet();
    return (String[]) keys.toArray(new String[keys.size()]);
  }
  
  /**
   * Caches a page.
   */
  public void cache(Path path, byte[] b) {
    pageCache.put(path, b);
  }
  
  /**
   * Removes a page from the cache.
   */
  public void removeFromCache(Path path) {
    pageCache.remove(path);
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
  
  public SortedMap getThemesMap() {
    if (themesMap == null) {
      themesMap = new TreeMap();
      addDirItemsToMap(themesMap, webSite.getAdminThemesPath(), THEME_DECORATOR);
      addDirItemsToMap(themesMap, webSite.getCustomThemesPath(), THEME_DECORATOR);
      themesMap = Collections.unmodifiableSortedMap(themesMap);
    }
    
    return themesMap;
  }
  
  public SortedMap getModulesMap() {
    if (modulesMap == null) {
      modulesMap = new TreeMap();
      addDirItemsToMap(modulesMap, webSite.getAdminModulesPath(),
          MODULE_INCLUDE_FILE);
      addDirItemsToMap(modulesMap, webSite.getCustomModulesPath(),
          MODULE_INCLUDE_FILE);
      modulesMap = Collections.unmodifiableSortedMap(modulesMap);
    }
    
    return modulesMap;
  }
  
  private void addDirItemsToMap(Map map, Path path, String insideDir) {
    File dir = webSite.getFile(path);
    
    if (dir.isDirectory()) {
      String[] files = dir.list();
      
      if (files != null) {
        for (int i = 0; i < files.length; i++) {
          Path subPath = path.add(files[i]);
          
          if (insideDir == null || webSite.getFile(subPath.add(insideDir)).exists()) {
            map.put(files[i], subPath);
          }
        }
      }
    }
  }
  
  /**
   * @see #setObsolete(boolean)
   */
  public boolean isObsolete() {
    return obsolete;
  }
  
  /**
   * When obsolete, info contained in this site map will be discarded when a
   * new site map is created.
   */
  public void setObsolete(boolean obsolete) {
    this.obsolete = obsolete;
  }
  
  public Path getRedirMatch(Path requestedPath) {
    if (redirCache == null) {
      redirCache = new HashMap();
      List list = getPagesList();
      redirPaths = new Path[list.size()];
      Iterator iter = list.iterator();
      
      for (int i = 0; iter.hasNext(); i++) {
        redirPaths[i] = ((PageInfo) iter.next()).getPath();
      }
    }
    
    Path result = null;
    int best = 0;
    
    if (Utils.searchString(WebUtils.DEFAULT_WELCOME_FILES,
        requestedPath.getLastElement(), false) >= 0) {
      requestedPath = requestedPath.getPartial(requestedPath.getElementCount() - 1);
    }
    
    if (redirCache.containsKey(requestedPath)) {
      result = (Path) redirCache.get(requestedPath);
    } else {
      for (int i = 0; i < redirPaths.length; i++) {
        String[] commonPart = Utils.commonPart(requestedPath.getElements(),
            redirPaths[i].getElements(), true);
        
        if (commonPart != null && commonPart.length > best) {
          result = redirPaths[i];
          best = commonPart.length;
        }
      }
      
      redirCache.put(requestedPath, result);
    }
    
    return result;
  }
  
  public static class CodeLocalePair {
    private String code;
    private Locale locale;
    private String name;
    
    public CodeLocalePair(String code, Locale locale) {
      this.code = code;
      this.locale = locale;
      name = Utils.toTitleCase(locale.getDisplayName(locale));
    }
    
    public String getCode() {
      return code;
    }
    
    public Locale getLocale() {
      return locale;
    }
    
    public String getName() {
      return name;
    }
  }
}
