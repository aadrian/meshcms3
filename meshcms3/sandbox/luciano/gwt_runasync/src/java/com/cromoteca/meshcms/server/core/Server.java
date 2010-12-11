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
import com.cromoteca.meshcms.client.server.Module;
import com.cromoteca.meshcms.client.server.ServerConfiguration;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.toolbox.Time;
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.storage.FileSystemStorageManager;
import com.cromoteca.meshcms.server.storage.StorageManager;
import com.cromoteca.meshcms.server.toolbox.JSON;
import com.cromoteca.meshcms.server.toolbox.Web;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

public class Server {
	// TODO: remove static path /meshcms
	public static final Path SYSTEM_MODULES_PATH = new Path(
			"meshcms/resources/modules");
	public static final String JSON_EXTENSION = ".json";
	public static final String DEFAULT_LOCALE = "en_US";
	static StorageManager storage;

	static {
		storage = new FileSystemStorageManager();
	}

	private File rootFile;
	private MultiSiteManager multiSiteManager;
	private Path cmsPath;
	private Path cmsServerPath;
	private ServerConfiguration serverConfiguration;
	private Set<CMSDirectoryItem> cmsDirectoryItems;
	private Set<String> pageExtensions;
	private Set<Module> systemModules;
	private WebSite mainWebSite;
	private String[] welcomeFiles;
	private long startupTime;
	private long statsZero;

	static Server init(Path cmsPath) throws IOException {
		Server s = new Server();
		s.startupTime = System.currentTimeMillis();

		ServletContext sc = Context.getServletContext();
		s.rootFile = storage.getFile(sc.getRealPath("/"));
		s.getWelcomeFilesFromWebConfig(sc);
		s.cmsDirectoryItems = new HashSet<CMSDirectoryItem>();
		s.cmsPath = cmsPath;
		s.cmsServerPath = cmsPath.add("server");

		Path serverConfigurationFilePath = s.initCMSDirectoryItems();
		Context.setServer(s);
		s.systemModules = new HashSet<Module>();
		s.initModules();
		s.serverConfiguration = Context.loadServerConfiguration(s.rootFile
						.getDescendant(serverConfigurationFilePath.toString()));
		s.mainWebSite = new WebSite(s.rootFile, Path.ROOT, true);
		s.multiSiteManager = MultiSiteManager.load(s.mainWebSite);
		s.multiSiteManager.initDomainMap();

		return s;
	}

	public static StorageManager getStorage() {
		return storage;
	}

	public static void setStorage(StorageManager storage) {
		Server.storage = storage;
	}

	public long getStartupTime() {
		return startupTime;
	}

	public Path getCMSPath() {
		return cmsPath;
	}

	public Path getCMSServerPath() {
		return cmsServerPath;
	}

	public Set<CMSDirectoryItem> getCMSDirectoryItems() {
		return cmsDirectoryItems;
	}

	public Set<Module> getSystemModules() {
		return systemModules;
	}

	public File getRootFile() {
		return rootFile;
	}

	/**
	 * Returns the array of welcome file names.
	 *
	 * @return an array of welcome file names for the current web application.
	 *         Values are fetched from the web.xml file.
	 */
	public String[] getWelcomeFiles() {
		return welcomeFiles;
	}

	/**
	 * Returns the MultiSiteManager instance.
	 */
	public MultiSiteManager getMultiSiteManager() {
		return multiSiteManager;
	}

	/**
	 * Returns the right webSite for the given request. Since this is a main
	 * webSite, it will return the webSite itself or a virtual webSite, according
	 * to the requested host name.
	 */
	public WebSite getWebSite(ServletRequest request) {
		return multiSiteManager.getWebSite(request.getServerName());
	}

	public ServerConfiguration getServerConfiguration() {
		return serverConfiguration;
	}

	public void setServerConfiguration(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	public Module getModule(String name) throws IOException {
		// TODO: remove static path /meshcms
		InputStream jsonStream = rootFile.getDescendant(
				"meshcms/resources/modules/" + name + '/' + name + JSON_EXTENSION)
					.getInputStream();

		Module module = JSON.getGson()
					.fromJson(new InputStreamReader(jsonStream), Module.class);
		module.setName(name);
		module.setBasePath(SYSTEM_MODULES_PATH);

		return module;
	}

	/**
	 * Returns the index of the current day in the array of stats included in any
	 * PageInfo instance.
	 */
	public int getStatsIndex() {
		long now = System.currentTimeMillis();
		long days = (now - statsZero) / Time.DAY;

		if (days >= serverConfiguration.getStatsLength()) {
			statsZero = now;

			return 0;
		} else {
			return (int) days;
		}
	}

	/**
	 * Returns true if the extension of the path is known to denote a type of file
	 * that can be edited using the wysiwyg editor.
	 */
	public boolean isVisuallyEditable(Path path) {
		return Strings.searchString(serverConfiguration.getVisualExtensions(),
			Strings.getExtension(path, false), true) != -1;
	}

	/**
	 * Checks if the file name is one of the welcome files.
	 *
	 * @param fileName the file name to check
	 *
	 * @return true if the given file name is known to be a welcome file name.
	 */
	public boolean isWelcomeFileName(String fileName) {
		return Strings.searchString(welcomeFiles, fileName, false) != -1;
	}

	public WebSite getMainWebSite() {
		return mainWebSite;
	}

	private Path initCMSDirectoryItems() {
		cmsDirectoryItems.add(new CMSDirectoryItem(CMSDirectoryItem.CMS_DIR,
				"cms_dir", cmsPath, true, false, false));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.USER_PROFILE_DIR,
				"user_profile_dir",
				cmsPath.add("user_profile"),
				true,
				true,
				false));
		cmsDirectoryItems.add(new CMSDirectoryItem(CMSDirectoryItem.SITE_DIR,
				"site_dir", cmsPath.add("site"), true, false, false));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SITE_CONFIGURATION_DIR,
				"site_configuration_dir",
				cmsPath.add("site/configuration"),
				true,
				true,
				true));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SITE_CONFIGURATION_FILE,
				"site_configuration_file",
				cmsPath.add("site/configuration/site.json"),
				false,
				true,
				true));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SITE_PAGES_DATA_FILE,
				"pages_data_file",
				cmsPath.add("site/configuration/pages.json"),
				false,
				true,
				true));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SITE_THEMES_DIR,
				"site_themes_dir",
				cmsPath.add("site/themes"),
				true,
				false,
				false));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SITE_MODULES_DIR,
				"site_modules_dir",
				cmsPath.add("site/modules"),
				true,
				false,
				false));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SITE_REPOSITORY_DIR,
				"site_repository_dir",
				cmsPath.add("site/repository"),
				true,
				false,
				false));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SITE_USERS_DIR,
				"site_users_dir",
				cmsPath.add("site/users"),
				true,
				true,
				true));
		cmsDirectoryItems.add(new CMSDirectoryItem(CMSDirectoryItem.SERVER_DIR,
				"server_dir", cmsServerPath, true, false, true));
		cmsDirectoryItems.add(new CMSDirectoryItem(CMSDirectoryItem.EDITORS_DIR,
				"editors_dir", cmsPath.add("server/editors"), true, false, true));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SERVER_CONFIGURATION_DIR,
				"server_configuration_dir",
				cmsPath.add("server/configuration"),
				true,
				true,
				true));

		Path serverConfigurationFilePath = cmsPath.add(
				"server/configuration/server.json");
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SERVER_CONFIGURATION_FILE,
				"server_configuration_file",
				serverConfigurationFilePath,
				false,
				true,
				true));
		cmsDirectoryItems.add(new CMSDirectoryItem(CMSDirectoryItem.SITES_DIR,
				"sites_dir", cmsPath.add("server/sites"), true, true, true));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SITES_CONFIGURATION_FILE,
				"sites_configuration_file",
				cmsPath.add("server/sites/sites.json"),
				false,
				true,
				true));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SERVER_THEMES_DIR,
				"server_themes_dir",
				cmsPath.add("server/themes"),
				true,
				false,
				true));
		cmsDirectoryItems.add(new CMSDirectoryItem(
				CMSDirectoryItem.SERVER_MODULES_DIR,
				"server_modules_dir",
				cmsPath.add("server/modules"),
				true,
				false,
				true));

		return serverConfigurationFilePath;
	}

	private void getWelcomeFilesFromWebConfig(ServletContext sc) {
		welcomeFiles = Web.getWelcomeFiles(sc);
		pageExtensions = new HashSet<String>();

		for (String welcomeFile : welcomeFiles) {
			pageExtensions.add(Strings.getExtension(welcomeFile, false));
		}
	}

	public boolean isPage(String fileName) {
		return pageExtensions.contains(Strings.getExtension(fileName, false));
	}

	private void initModules() throws IOException {
		systemModules.add(getModule("blog"));
		systemModules.add(getModule("body"));
		systemModules.add(getModule("checkbox"));
		systemModules.add(getModule("comments"));
		systemModules.add(getModule("content"));
		systemModules.add(getModule("downloads"));
		systemModules.add(getModule("editor"));
		systemModules.add(getModule("feed"));
		systemModules.add(getModule("form"));
		systemModules.add(getModule("formsubmit"));
		systemModules.add(getModule("gallery"));
		systemModules.add(getModule("include"));
		systemModules.add(getModule("langmenu"));
		systemModules.add(getModule("link"));
		systemModules.add(getModule("login"));
		systemModules.add(getModule("mediaplayer"));
		systemModules.add(getModule("menu"));
		systemModules.add(getModule("pageimage"));
		systemModules.add(getModule("pagelist"));
		systemModules.add(getModule("passwordfield"));
		systemModules.add(getModule("recaptcha"));
		systemModules.add(getModule("textarea"));
		systemModules.add(getModule("textfield"));
	}
}
