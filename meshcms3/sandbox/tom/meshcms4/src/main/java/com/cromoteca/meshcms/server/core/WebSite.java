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
import com.cromoteca.meshcms.client.server.PersistentPageData;
import com.cromoteca.meshcms.client.server.SiteConfiguration;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.toolbox.DirectoryRemover;
import com.cromoteca.meshcms.server.toolbox.IO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WebSite {
	public static final String APP_NAME = "MeshCMS";
	public static final String APP_URL = "http://www.cromoteca.com/meshcms";
	public static final String APP_VERSION = "4.0 Alpha";
	public static final String APP_FULL_NAME = APP_NAME + ' ' + APP_VERSION;
	public static final String APP_FULL_NAME_WITH_URL = APP_FULL_NAME + " ("
		+ APP_URL + ')';

	/**
	 * A prefix to be used for every temporary file created in the repository.
	 */
	public static final String TEMP_PREFIX = "_tmp_";
	private File rootFile;
	private Map<Integer, CMSDirectoryItem> cmsDirectoryItemMap;
	private Path rootPath;
	private PersistentPageData persistentPageData;
	private Set<CMSDirectoryItem> hiddenCMSDirectoryItems;
	private SiteConfiguration siteConfiguration;
	private SiteMap siteMap;
	private WebSiteType type;

	public WebSite(File rootFile, Path rootPath, boolean setUpCMS)
		throws IOException {
		this.rootFile = rootFile;
		this.rootPath = rootPath;
		rootFile.mkdirs();

		if (rootPath == null) {
			type = WebSiteType.EXTERNAL;
		} else if (rootPath.isRoot()) {
			type = WebSiteType.MAIN;
		} else {
			type = WebSiteType.VIRTUAL;
		}

		Server server = Context.getServer();

		if (setUpCMS || isDirectory(server.getCMSPath())) {
			cmsDirectoryItemMap = new HashMap<Integer, CMSDirectoryItem>();
			hiddenCMSDirectoryItems = new HashSet<CMSDirectoryItem>();

			for (CMSDirectoryItem item : server.getCMSDirectoryItems()) {
				cmsDirectoryItemMap.put(item.getCode(), item);

				if (item.isDirectory()
							&& (type == WebSiteType.MAIN
							|| !item.getPath().isContainedIn(server.getCMSServerPath()))) {
					getFile(item.getPath()).mkdirs();
				}

				if (item.isHidden()) {
					hiddenCMSDirectoryItems.add(item);
				}
			}

			siteConfiguration = loadSiteConfiguration();
			persistentPageData = loadPersistentPageData();
			updateSiteMap(true);
		}
	}

	/**
	 * Copies a file (or directory) to another file (or directory). Existing files
	 * won't be overwritten.
	 *
	 * @param oldPath the location of the existing file
	 * @param newPath the location of the new copy of the file
	 *
	 * @return true if the new file has been copied, false otherwise
	 */
	public boolean copyFile(Path oldPath, Path newPath) {
		File oldFile = getFile(oldPath);

		if (!oldFile.exists()) {
			return false;
		}

		File newFile = getFile(newPath);

		if (oldFile.isDirectory()) {
			return IO.copyDirectory(oldFile, newFile, false, false, false);
		} else {
			try {
				return IO.copyFile(oldFile, newFile, false, false);
			} catch (IOException ex) {
				Context.log("Can't copy file " + oldFile + " to file " + newFile, ex);
			}
		}

		return false;
	}

	/**
	 * Copies a file to another file in the same directory. An existing file won't
	 * be overwritten.
	 *
	 * @param filePath the path of the old file
	 * @param newName  the name of the new file
	 *
	 * @return true if the new file has been copied, false otherwise
	 */
	public boolean copyFile(Path filePath, String newName) {
		return copyFile(filePath, filePath.getParent().add(newName));
	}

	/**
	 * Creates a new directory.
	 *
	 * @param dirPath the path of the new directory
	 *
	 * @return true if the directory has been created, false
	 *         otherwise
	 */
	public boolean createDirectory(Path dirPath) {
		File newDir = getFile(dirPath);

		if (newDir.exists()) {
			return false;
		}

		return newDir.mkdirs();
	}

	/**
	 * Creates a new file. If the extension of the file denotes a web page, the
	 * basic template is copied into the file, otherwise an empty file is created.
	 *
	 * @param filePath the path of the new file
	 *
	 * @return true if the new file has been created, false
	 *         otherwise
	 */
	public boolean createFile(Path filePath) {
		File newFile = getFile(filePath);

		if (!newFile.exists()) {
			try {
				if (Context.getServer().isPage(filePath.getLastElement())) {
					writeNewHTMLFile(filePath, "");

					return true;
				} else {
					return newFile.createNewFile();
				}
			} catch (IOException ex) {
				Context.log("Can't create file " + newFile, ex);
			}
		}

		return false;
	}

	/**
	 * Deletes a file or directory.
	 *
	 * @param filePath                  the path of the file
	 * @param deleteNonEmptyDirectories if true, non-empty directories will be
	 *                                  deleted too
	 *
	 * @return true if the file has been deleted, false otherwise
	 */
	public boolean delete(Path filePath, boolean deleteNonEmptyDirectories) {
		// TODO: avoid deletion of required CMS items
		File file = getFile(filePath);

		if (!file.exists()) {
			return false;
		}

		if (file.isDirectory() && deleteNonEmptyDirectories) {
			DirectoryRemover dr = new DirectoryRemover(file);
			dr.process();

			return dr.getResult();
		} else {
			return file.delete();
		}
	}

	/**
	 * Returns the current welcome file path for the given folder. If there is no
	 * welcome file in that folder, this method returns null.
	 *
	 * @param dirPath the folder where to search the welcome file
	 *
	 * @return the welcome file as a Path object of null if not found.
	 */
	public Path findCurrentWelcome(Path dirPath) {
		if (isDirectory(dirPath)) {
			for (String welcomeFile : Context.getServer().getWelcomeFiles()) {
				Path wPath = dirPath.add(welcomeFile);

				if (getFile(wPath).exists()) {
					return wPath;
				}
			}
		}

		return null;
	}

	public String getAbsoluteLink(PageInfo pageInfo) {
		return getAbsoluteLink(pageInfo.getPath());
	}

	public String getAbsoluteLink(Path path) {
		return isDirectory(path) ? path.asLink() + '/' : path.asLink();
	}

	/**
	 * Returns the current configuration of the web application.
	 */
	public SiteConfiguration getSiteConfiguration() {
		return siteConfiguration;
	}

	public PersistentPageData getPersistentPageData() {
		return persistentPageData;
	}

	/**
	 * Returns the directory that contains the given path. This is different from
	 * {@link com.cromoteca.meshcms.client.toolbox.Path#getParent}, since if the path is
	 * known to be a directory in the web application, the path itself is
	 * returned.
	 *
	 * @param path the Path to check
	 *
	 * @return a directory(that contains the given path) as a Path object.
	 */
	public Path getDirectory(Path path) {
		if (path == null) {
			return null;
		}

		if (isDirectory(path)) {
			return path;
		}

		path = path.getParent();

		if (isDirectory(path)) {
			return path;
		}

		return null;
	}

	/**
	 * Returns the file object for a given path in the web application. The file is
	 * not checked for existance.
	 *
	 * @param path the path representation of the file
	 *
	 * @return the file object for this path, or null if it's not found
	 */
	public File getFile(Path path) {
		if (path == null || path.isRelative()) {
			return null;
		}

		if (type != WebSiteType.MAIN) {
			Server server = Context.getServer();

			if (path.isContainedIn(Context.MESHCMS_PATH)
						|| path.isContainedIn(server.getCMSServerPath())) {
				return server.getMainWebSite().getFile(path);
			}
		}

		return new File(rootFile, path.toString());
	}

	/**
	 * Returns a string containing a basic HTML page.
	 *
	 * @return a basic "empty" HTML page
	 */
	public void writeNewHTMLFile(Path path, String title)
		throws IOException {
		File templateFile = getFile(new Path("meshcms/resources/template.html"));
		String template = IO.readFully(templateFile);
		template = template.replace("{0}", title);
		IO.write(template, getFile(path));
	}

	public Path getLink(PageInfo pageInfo, Path pagePath) {
		return getLink(pageInfo.getPath(), pagePath);
	}

	public Path getLink(Path path, Path pagePath) {
		return path.getRelativeTo(getDirectory(pagePath));
	}

	public Path getRepositoryPath(Path filePath) {
		return getCMSPath(CMSDirectoryItem.SITE_REPOSITORY_DIR).add(filePath);
	}

	public File getRootFile() {
		return rootFile;
	}

	public Path getRootPath() {
		Path p;

		switch (type) {
			case VIRTUAL:
				p = rootPath;

				break;

			case EXTERNAL:
				p = new Path("/meshcms/external/",
						Context.getRequestContext().getURL().getHost());

				break;

			default:
				p = Path.ROOT;
		}

		return p;
	}

	/**
	 * Returns the instance of the <code>SiteMap</code> that is currently manage
	 * the site map. Since this object can be replaced with a new one at any
	 * moment, a class that wants to use it should store it in a local variable and
	 * use it for all the operation/method.
	 *
	 * @return the current instance of SiteMap
	 */
	public SiteMap getSiteMap() {
		return siteMap;
	}

	/**
	 * Checks if the given path is a directory in the file system.
	 *
	 * @param path the Path to check
	 *
	 * @return true if the path is a directory.
	 */
	public boolean isDirectory(Path path) {
		File file = getFile(path);

		return file != null && file.isDirectory();
	}

	/**
	 * Moves (or renames) a file.
	 *
	 * @param oldPath the current location of the file
	 * @param newPath the new location of the file
	 *
	 * @return true if the new file has been moved, false otherwise
	 */
	public boolean move(Path oldPath, Path newPath) {
		File oldFile = getFile(oldPath);

		if (!oldFile.exists()) {
			return false;
		}

		if (newPath.isContainedIn(oldPath)) {
			return false;
		}

		File newFile = getFile(newPath);

		if (IO.forceRenameTo(oldFile, newFile, false)) {
			return true;
		} else {
			Context.log("Can't move file " + oldFile + " to file " + newFile);
		}

		return false;
	}

	/**
	 * Stores an object into a file. Supported objects are: <ul> <li>byte
	 * arrays</li> <li>input streams</li> <li><code>org.apache.commons.fileupload.FileItem</code>
	 * (uploaded files)</li> <li>generic objects. The <code>toString()</code>
	 * method is used in this cases. This is compatible with many kinds of objects:
	 * strings, string buffers and so on.</li> </ul>
	 *
	 * @param saveThis the object to be stored in the file
	 * @param filePath the path of the file to be written. If the file exists, it
	 *                 will be backed up and overwritten
	 *
	 * @return true if the operation has been completed successfully, false
	 *         otherwise
	 */
	public boolean saveToFile(Object saveThis, Path filePath) {
		File file = getFile(filePath);
		File dir = file.getParentFile();
		dir.mkdirs();

		File tempFile = null;
		String fileName = file.getName();
		int dot = fileName.lastIndexOf('.');
		String fileExt = dot == -1 ? ".bak" : fileName.substring(dot);

		if (file.exists()) {
			File tempDir = getFile(getRepositoryPath(filePath));
			tempDir.mkdirs();
			tempFile = new File(tempDir,
					TEMP_PREFIX + System.currentTimeMillis() + fileExt);
		}

		File writeTo = tempFile == null ? file : tempFile;

		if (saveThis instanceof byte[]) {
			byte[] b = (byte[]) saveThis;
			OutputStream fos = null;

			try {
				fos = new FileOutputStream(writeTo);
				fos.write(b);
			} catch (IOException ex) {
				Context.log("Can't write byte array to file " + writeTo, ex);

				return false;
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException ex) {
						Context.log("Can't close file " + writeTo, ex);
					}
				}
			}
		} else if (saveThis instanceof InputStream) {
			try {
				InputStream is = (InputStream) saveThis;
				IO.copyStream(is, new FileOutputStream(writeTo), true);
				is.close();
			} catch (Exception ex) {
				Context.log("Can't write stream to file " + writeTo, ex);

				return false;
			}
		} else {
			Writer writer = null;

			try {
				writer = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(writeTo),
							IO.SYSTEM_CHARSET));
				writer.write(saveThis.toString());
			} catch (IOException ex) {
				Context.log("Can't write generic object to file " + writeTo, ex);

				return false;
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ex) {
						Context.log("Can't close file " + writeTo, ex);
					}
				}
			}
		}

		if (tempFile != null) {
			if (!IO.forceRenameTo(tempFile, file, true)) {
				Context.log("Can't rename temporary file " + tempFile + " to file "
					+ file);

				return false;
			}
		}

		return true;
	}

	public boolean setFileTime(Path filePath, long time) {
		File file = getFile(filePath);

		if (!file.exists()) {
			return false;
		}

		file.setLastModified(time);

		return true;
	}

	void setSiteMap(SiteMap siteMap) {
		this.siteMap = siteMap;
	}

	/**
	 * Creates another instance of <code>SiteMap</code>. If <code>force</code> is
	 * true, a new site map is always created and the method returns after the new
	 * site map is completed. If it is false, a new site map is created only if the
	 * current one is too old. In this case, the site map is created asynchronously
	 * and the method returns immediately. The repository will be cleaned too.
	 *
	 * @param force it to force the SiteMap creation.
	 */
	public void updateSiteMap(boolean force) {
		if (isCMSEnabled()) {
			if (force) {
				new SiteMap(this).process();
			} else if (System.currentTimeMillis() - siteMap.getLastModified() > siteConfiguration
						.getUpdateIntervalMillis()) {
				new SiteMap(this).process();

				// TODO: restore cleaning
				//			new DirectoryCleaner(getFile(repositoryPath),
				//				configuration.getBackupLifeMillis()).start();
				//			new DirectoryCleaner(getFile(generatedFilesPath),
				//				configuration.getBackupLifeMillis()).start();
				//			new DirectoryCleaner(getFile(moduleDataPath)).start();
			}
		}
	}

	public boolean isCMSEnabled() {
		return cmsDirectoryItemMap != null;
	}

	public Path getCMSPath(int code) {
		return cmsDirectoryItemMap.get(code).getPath();
	}

	public File getCMSFile(int code) {
		return getFile(getCMSPath(code));
	}

	/**
	 * Hidden paths are those that must not be shown in the file manager
	 * or parsed by the site map.
	 * @param path
	 * @return
	 */
	public boolean isHidden(Path path, boolean deepCheck) {
		if (deepCheck) {
			for (CMSDirectoryItem hiddenItem : hiddenCMSDirectoryItems) {
				if (path.isContainedIn(hiddenItem.getPath())) {
					return true;
				}
			}
		} else {
			for (CMSDirectoryItem hiddenItem : hiddenCMSDirectoryItems) {
				if (path.equals(hiddenItem.getPath())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * System paths are those that must not be served to the browser.
	 * @param path
	 * @return
	 */
	public boolean isSystem(Path path) {
		if (path.isRoot()) {
			return false;
		}

		if (path.isContainedIn(Context.MESHCMS_PATH)) {
			return true;
		}

		String first = path.getElementAt(0);

		return "web-inf".equalsIgnoreCase(first)
				|| "meta-inf".equalsIgnoreCase(first);
	}

	public WebSiteType getType() {
		return type;
	}

	private PersistentPageData loadPersistentPageData()
		throws IOException {
		PersistentPageData c = null;
		File file = getCMSFile(CMSDirectoryItem.SITE_PAGES_DATA_FILE);

		try {
			c = Context.loadFromJSON(PersistentPageData.class, file);
		} catch (Exception ex) {}

		if (c == null) {
			c = new PersistentPageData();
		}

		return c;
	}

	public void storePersistentPageData() throws IOException {
		Context.storeToJSON(persistentPageData,
			getCMSFile(CMSDirectoryItem.SITE_PAGES_DATA_FILE));
	}

	/**
	 * Loads the configuration from file or creates a new configuration with
	 * default values if the file doesn't exist.
	 */
	private SiteConfiguration loadSiteConfiguration()
		throws IOException {
		SiteConfiguration c;
		File file = getCMSFile(CMSDirectoryItem.SITE_CONFIGURATION_FILE);
		c = Context.loadFromJSON(SiteConfiguration.class, file);

		if (c == null) {
			c = new SiteConfiguration();
		}

		return c;
	}

	/**
	 * Saves the current configuration to file.
	 */
	public void storeSiteConfiguration(SiteConfiguration configuration)
		throws IOException {
		siteConfiguration = configuration;
		Context.storeToJSON(siteConfiguration,
			getCMSFile(CMSDirectoryItem.SITE_CONFIGURATION_FILE));
	}
}
