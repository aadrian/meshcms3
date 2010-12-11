/*
 * Copyright 2004-2010 Luciano Vernaschi
 *
 * This file is part of MeshCMS.
 *
 * MeshCMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MeshCMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cromoteca.meshcms.server.core;

import com.cromoteca.meshcms.client.server.CMSDirectoryItem;
import com.cromoteca.meshcms.client.server.MenuPolicy;
import com.cromoteca.meshcms.client.server.Module;
import com.cromoteca.meshcms.client.server.Module.Parameter;
import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.client.server.PersistentPageData;
import com.cromoteca.meshcms.client.server.SiteInfo;
import com.cromoteca.meshcms.client.server.Theme;
import com.cromoteca.meshcms.client.server.Zone;
import com.cromoteca.meshcms.client.server.ZoneItem;
import com.cromoteca.meshcms.client.toolbox.Pair;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.storage.ItemFilter;
import com.cromoteca.meshcms.server.toolbox.DirectoryParser;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.JSON;
import com.cromoteca.meshcms.server.toolbox.Locales;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.glaforge.i18n.io.SmartEncodingInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import net.htmlparser.jericho.Source;

/**
 * Contains the site map.
 */
public class SiteMap extends DirectoryParser {
	private List<Pair<String, Locale>> langList;
	private List<PageInfo> pageList;
	private Map<Path, Path> currentWelcomes;
	private Map<Path, Path> redirCache;
	private Server server;
	private Set<Module> serverModules;
	private Set<Theme> serverThemes;
	private SiteInfo siteInfo;
	private SiteMap oldSiteMap;
	private SortedMap<Path, PageInfo> pageMap;
	private WebSite webSite;
	private boolean obsolete;
	private long lastModified;

	/**
	 * Creates a new instance of SiteMap
	 */
	public SiteMap(WebSite webSite) {
		this.webSite = webSite;
		setRecursive(true);
		setSorted(true);
		setProcessStartDir(true);
		setInitialDir(webSite.getRootFile());
		server = Context.getServer();
		siteInfo = new SiteInfo();
		siteInfo.setCMSPath(server.getCMSPath());
		siteInfo.setContextPath(Context.getContextPath());
		siteInfo.setCMSDirectoryItems(server.getCMSDirectoryItems());
		redirCache = new HashMap<Path, Path>();
	}

	/**
	 * Returns the breadcrumbs from the root path (included) to the given path (
	 * <em>not</em> included).
	 */
	public List<PageInfo> getBreadcrumbs(Path path) {
		path = getPathInMenu(path);

		List<PageInfo> list = new ArrayList<PageInfo>();

		for (int i = 0; i < path.getElementCount(); i++) {
			Path partial = path.getPartial(i);
			PageInfo pi = getPageInfo(partial);

			if (pi != null) {
				list.add(pi);
			}
		}

		if (list.size() != 0) {
			return list;
		}

		return null;
	}

	/**
	 * Returns the path of the welcome file for the given directory path. This
	 * method returns null if the path is not a directory or if there is no welcome
	 * file into it.
	 */
	public Path getCurrentWelcome(Path dirPath) {
		return currentWelcomes.get(dirPath);
	}

	public List<Pair<String, Locale>> getLangList() {
		return langList;
	}

	/**
	 * Returns the last modification time.
	 */
	public long getLastModified() {
		return lastModified;
	}

	public Set<Module> getServerModules() {
		return serverModules;
	}

	public Set<Theme> getServerThemes() {
		return serverThemes;
	}

	public SiteInfo getSiteInfo() {
		return siteInfo;
	}

	/**
	 * Returns the <code>PageInfo</code> for the given path.
	 */
	public PageInfo getPageInfo(Path path) {
		return pageMap.get(getPathInMenu(path));
	}

	public boolean isEmpty() {
		return pageMap.isEmpty();
	}

	/**
	 * Returns a list of pages contained in the directory that contains the given
	 * path; if the path denotes a directory, its contents are returned.
	 *
	 * @param includeDir if true, the directory itself is included in the list
	 */
	public List<PageInfo> getPagesInDirectory(Path path, boolean includeDir) {
		PageInfo rootPage = getPageInfo(webSite.getDirectory(path));
		int idx = pageList.indexOf(rootPage);

		if (idx < 0) {
			return null;
		}

		List<PageInfo> list = new ArrayList<PageInfo>();

		if (includeDir) {
			list.add(rootPage);
		}

		for (int i = idx + 1; i < pageList.size(); i++) {
			PageInfo pi = pageList.get(i);
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
	 * Returns the pages contained in the menu as a unmodifiable List, using the
	 * given path as root path. All members of the list are of type
	 * <code>PageInfo</code>. Pages are sorted using a {@link PageInfoComparator}.
	 */
	public List<PageInfo> getPageList(Path root) {
		root = webSite.getDirectory(root);

		if (root.isRoot()) {
			return pageList;
		}

		PageInfo rootPage = getPageInfo(root);
		int idx = pageList.indexOf(rootPage);

		if (idx < 0) {
			return new ArrayList<PageInfo>();
		}

		int rootLevel = rootPage.getLevel();

		for (int i = idx + 1; i < pageList.size(); i++) {
			if (pageList.get(i).getLevel() <= rootLevel) {
				return pageList.subList(idx, i);
			}
		}

		return pageList.subList(idx, pageList.size());
	}

	/**
	 * Returns the given path unless it is the current welcome file in its folder;
	 * in this case the folder path is returned.
	 */
	public Path getPathInMenu(Path path) {
		return currentWelcomes.containsValue(path) ? path.getParent() : path;
	}

	public Path getRedirMatch(Path requestedPath) {
		Path result = null;
		int best = 0;

		if (Strings.searchString(server.getWelcomeFiles(),
						requestedPath.getLastElement(), false) >= 0) {
			requestedPath = requestedPath.getPartial(requestedPath.getElementCount()
					- 1);
		}

		if (redirCache.containsKey(requestedPath)) {
			result = redirCache.get(requestedPath);
		} else {
			Path match = removePageExtension(requestedPath);

			for (Path redirPath : pageMap.keySet()) {
				String[] commonPart = Strings.commonPart(match.getElements(),
						removePageExtension(redirPath).getElements(), true);

				if (commonPart != null && commonPart.length > best) {
					result = redirPath;
					best = commonPart.length;
				}
			}

			redirCache.put(requestedPath, result);
		}

		return result;
	}

	/**
	 * Returns the given path unless it is a folder with a welcome file; in this
	 * case the welcome file path is returned.
	 */
	public Path getServedPath(Path path) {
		Path welcome = currentWelcomes.get(path);

		return welcome == null ? path : welcome;
	}

	public File getServedFile(Path path) {
		return webSite.getFile(getServedPath(path));
	}

	/**
	 * Checks if the given path is the welcome file for its directory.
	 */
	public boolean isCurrentWelcome(Path path) {
		return currentWelcomes.containsValue(path);
	}

	public boolean isHidden(Path path) {
		path = getPathInMenu(path);

		PageInfo pageInfo = getPageInfo(path);

		if (pageInfo != null) {
			if (pageInfo.getMenuPolicy() == MenuPolicy.SKIP) {
				return true;
			}

			while (!(path = path.getParent()).isRelative()) {
				pageInfo = getPageInfo(path);

				if (pageInfo == null || pageInfo.getMenuPolicy() != MenuPolicy.INSERT) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @see #setObsolete(boolean)
	 */
	public boolean isObsolete() {
		return obsolete;
	}

	@Override
	protected void postProcess() {
		pageMap = Collections.unmodifiableSortedMap(pageMap);
		oldSiteMap = null;
		pageList = new ArrayList<PageInfo>(pageMap.values());
		Collections.sort(pageList, new PageInfoComparator(webSite, this));
		pageList = Collections.unmodifiableList(pageList);
		langList = new ArrayList<Pair<String, Locale>>();

		List<PageInfo> firstLevelPages = getPagesInDirectory(Path.ROOT, false);

		if (firstLevelPages != null) {
			for (PageInfo pageInfo : firstLevelPages) {
				String name = pageInfo.getPath().getElementAt(0);
				Locale locale = Locales.getLocale(name);

				if (locale != null) {
					langList.add(new Pair<String, Locale>(name, locale));
				}
			}
		}

		langList = Collections.unmodifiableList(langList);
		setLastModified();
		webSite.setSiteMap(this);
	}

	@Override
	protected boolean preProcess() {
		oldSiteMap = webSite.getSiteMap();

		if (oldSiteMap != null && oldSiteMap.isObsolete()) {
			oldSiteMap = null;
		}

		pageMap = new TreeMap<Path, PageInfo>();
		currentWelcomes = new TreeMap<Path, Path>();

		if (webSite.getType() == WebSiteType.MAIN) {
			Path serverModulesPath = webSite.getCMSPath(CMSDirectoryItem.SERVER_MODULES_DIR);
			serverModules = new ModuleLoader(serverModulesPath).load();

			Path serverThemesPath = webSite.getCMSPath(CMSDirectoryItem.SERVER_THEMES_DIR);
			serverThemes = new ThemeLoader(serverThemesPath).load();
		} else {
			SiteMap mainSiteMap = server.getMainWebSite().getSiteMap();
			serverModules = mainSiteMap.getServerModules();
			serverThemes = mainSiteMap.getServerThemes();
		}

		Path siteModulesPath = webSite.getCMSPath(CMSDirectoryItem.SITE_MODULES_DIR);
		Set<Module> siteModules = new ModuleLoader(siteModulesPath).load();
		Path siteThemesPath = webSite.getCMSPath(CMSDirectoryItem.SITE_THEMES_DIR);
		Set<Theme> siteThemes = new ThemeLoader(siteThemesPath).load();
		Map<String, Module> moduleMap = new HashMap<String, Module>();

		for (Module module : server.getSystemModules()) {
			moduleMap.put(module.getName(), module);
		}

		for (Module module : serverModules) {
			moduleMap.put(module.getName(), module);
		}

		for (Module module : siteModules) {
			moduleMap.put(module.getName(), module);
		}

		Map<String, Theme> themeMap = new HashMap<String, Theme>();

		for (Theme theme : serverThemes) {
			themeMap.put(theme.getName(), theme);
		}

		for (Theme theme : siteThemes) {
			themeMap.put(theme.getName(), theme);
		}

		siteInfo.setModules(moduleMap);
		siteInfo.setThemes(themeMap);

		Path editorsDir = webSite.getCMSPath(CMSDirectoryItem.EDITORS_DIR);
		siteInfo.setEditArea(webSite.getFile(editorsDir.add("edit_area")).exists());
		siteInfo.setTinyMCE(webSite.getFile(editorsDir.add("tiny_mce")).exists());
		siteInfo.setCKEditor(webSite.getFile(editorsDir.add("ckeditor")).exists());
		siteInfo.setUserCSS(webSite.getSiteConfiguration().getUserCSS());

		return true;
	}

	@Override
	protected boolean preProcessDirectory(File file, Path path) {
		if (webSite.isSystem(path) || path.isContainedIn(server.getCMSPath())) {
			return false;
		}

		Path wPath = webSite.findCurrentWelcome(path);

		if (wPath == null && path.isRoot()) {
			String wName = server.getWelcomeFiles()[0];

			try {
				wPath = new Path(wName);
				webSite.writeNewHTMLFile(wPath);

				Theme[] themes = siteInfo.getThemes().values().toArray(new Theme[0]);
				Theme theme = themes[new Random().nextInt(themes.length)];
				webSite.getPersistentPageData().setTheme(path, theme.getName());
				webSite.storePersistentPageData();
			} catch (IOException ex) {
				Context.log(ex);

				return false;
			}
		}

		if (wPath != null) {
			currentWelcomes.put(path, wPath);
		}

		return true;
	}

	@Override
	protected void processFile(File file, Path path) {
		if (!server.isPage(path.getLastElement())) {
			return;
		}

		Path dirPath = path.getParent();
		Path welcome = currentWelcomes.get(dirPath);

		if (welcome == null) {
			return;
		}

		path = welcome.equals(path) ? dirPath : path;

		PageInfo pageInfo = null;

		if (oldSiteMap != null) {
			pageInfo = oldSiteMap.getPageInfo(path);
		}

		if (pageInfo == null) {
			pageInfo = new PageInfo(path);
		}

		long fileLastModified = file.getLastModified();

		if (pageInfo.getLastModified() != fileLastModified) {
			InputStream inputStream = null;

			try {
				inputStream = file.getInputStream();

				SmartEncodingInputStream seis = new SmartEncodingInputStream(inputStream,
						SmartEncodingInputStream.BUFFER_LENGTH_8KB, IO.ISO_8859_1);
				Source source = new Source(seis.getReader());
				inputStream.close();

				Page meshPage = new PageParser().parse(source, false);
				String menuTitle = meshPage.getMenuTitle();

				if (Strings.isNullOrWhitespace(menuTitle)) {
					menuTitle = Strings.beautify(Strings.removeExtension(
								path.getLastElement()), true);
				}

				pageInfo.setMenuTitle(menuTitle);
				pageInfo.setTitle(meshPage.getTitle());
				pageInfo.setKeywords(meshPage.getKeywords());
				pageInfo.setMenuPolicy(meshPage.getPageConfiguration().getMenuPolicy());
				pageInfo.getHeadProperties().clear();
				pageInfo.getInheritableZones().clear();

				Date creationDate = meshPage.getPageConfiguration().getCreationDate();
				pageInfo.setCreationDate(creationDate == null ? fileLastModified
					: creationDate.getTime());
				pageInfo.setLastModified(fileLastModified);

				String excerpt = meshPage.getDescription();

				if (Strings.isNullOrWhitespace(excerpt)) {
					excerpt = WebUtils.createExcerpt(Strings.stripHTMLTags(
								meshPage.getBody()),
							webSite.getSiteConfiguration().getExcerptLength());
				}

				pageInfo.setExcerpt(excerpt);

				for (Zone zone : meshPage.getPageConfiguration().getZones()) {
					if (Zone.HEAD_ZONE.equals(zone.getName())) {
						for (ZoneItem item : zone.getItems()) {
							Module module = siteInfo.getModules().get(item.getModuleName());

							for (Parameter parameter : module.getParameters()) {
								if (parameter.isPageProperty()) {
									String value = item.getParameters().get(parameter.getName());

									if (value != null) {
										pageInfo.getHeadProperties()
												.put(item.getModuleName() + ":" + parameter.getName(),
													value);
									}
								}
							}
						}
					} else if (zone.isInheritable()) {
						pageInfo.getInheritableZones().put(zone.getName(), zone);
					}
				}
			} catch (Exception ex) {
				Context.log(ex);

				if (pageInfo != null) {
					pageInfo.setMenuTitle(Strings.beautify(Strings.removeExtension(
								path.getLastElement()), true));
					pageInfo.setLastModified(0L);
				}
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException ex) {
						Context.log(ex);
					}
				}
			}
		}

		if (pageInfo != null) {
			pageMap.put(path, pageInfo);
		}
	}

	/**
	 * Returns the path of the page whose theme is inherited.
	 */
	public Path getThemeRoot(Path pagePath) {
		PersistentPageData pageData = webSite.getPersistentPageData();

		do {
			String themeName = pageData.getTheme(pagePath);

			if (!Strings.isNullOrEmpty(themeName)
						&& siteInfo.getThemes().get(themeName) != null) {
				return pagePath;
			}

			pagePath = pagePath.getParent();
		} while (!pagePath.isRelative());

		return null;
	}

	public Theme getTheme(Path pagePath) {
		PersistentPageData pageData = webSite.getPersistentPageData();

		do {
			String themeName = pageData.getTheme(pagePath);

			if (!Strings.isNullOrEmpty(themeName)) {
				Theme theme = siteInfo.getThemes().get(themeName);

				if (theme != null) {
					return theme;
				}
			}

			pagePath = pagePath.getParent();
		} while (!pagePath.isRelative());

		return null;
	}

	private Path removePageExtension(Path path) {
		String name = path.getLastElement();

		if (server.isPage(name)) {
			path = path.getParent().add(Strings.removeExtension(name));
		}

		return path;
	}

	/**
	 * Sets the last modification time to the current time.
	 */
	void setLastModified() {
		lastModified = System.currentTimeMillis();
	}

	/**
	 * When obsolete, info contained in this site map will be discarded when a new
	 * site map is created.
	 */
	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}

	private abstract class DirectoryLoader<T> {
		private Path directoryPath;

		public DirectoryLoader(Path directoryPath) {
			this.directoryPath = directoryPath;
		}

		public Path getDirectoryPath() {
			return directoryPath;
		}

		public Set<T> load() {
			Set<T> set = new HashSet<T>();
			List<File> mDirs = webSite.getFile(directoryPath).getChildren(new ItemFilter() {
						public boolean accept(File f) {
							return f.isDirectory();
						}
					});

			if (mDirs != null) {
				for (File mDir : mDirs) {
					try {
						File json = mDir.getDescendant(mDir.getName()
								+ Server.JSON_EXTENSION);

						if (json.exists()) {
							set.add(load(json));
						}
					} catch (Exception ex) {
						Context.log(ex);
					}
				}
			}

			return set;
		}

		protected abstract T load(File dir) throws IOException;
	}

	private class ModuleLoader extends DirectoryLoader<Module> {
		public ModuleLoader(Path directoryPath) {
			super(directoryPath);
		}

		@Override
		public Module load(File json) throws IOException {
			InputStream is = json.getInputStream();
			Module module = JSON.getGson()
						.fromJson(new InputStreamReader(is), Module.class);
			module.setName(json.getParent().getName());
			module.setBasePath(getDirectoryPath());

			return module;
		}
	}

	private class ThemeLoader extends DirectoryLoader<Theme> {
		public ThemeLoader(Path directoryPath) {
			super(directoryPath);
		}

		@Override
		public Theme load(File json) throws IOException {
			InputStream is = json.getInputStream();
			Theme theme = JSON.getGson()
						.fromJson(new InputStreamReader(is), Theme.class);
			theme.setName(json.getParent().getName());
			theme.setBasePath(getDirectoryPath());

			return theme;
		}
	}
}
