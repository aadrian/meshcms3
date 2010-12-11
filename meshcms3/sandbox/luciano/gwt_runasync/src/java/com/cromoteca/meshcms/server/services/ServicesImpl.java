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
package com.cromoteca.meshcms.server.services;

import com.cromoteca.meshcms.client.core.Outcome;
import com.cromoteca.meshcms.client.core.Services;
import com.cromoteca.meshcms.client.server.AuthorizationException;
import com.cromoteca.meshcms.client.server.CMSDirectoryItem;
import com.cromoteca.meshcms.client.server.FileInfo;
import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.client.server.PersistentPageData;
import com.cromoteca.meshcms.client.server.ServerConfiguration;
import com.cromoteca.meshcms.client.server.ServerException;
import com.cromoteca.meshcms.client.server.ServerInfo;
import com.cromoteca.meshcms.client.server.SiteConfiguration;
import com.cromoteca.meshcms.client.server.SiteInfo;
import com.cromoteca.meshcms.client.server.SitesConfiguration;
import com.cromoteca.meshcms.client.server.UserProfile;
import com.cromoteca.meshcms.client.toolbox.Function;
import com.cromoteca.meshcms.client.toolbox.Null;
import com.cromoteca.meshcms.client.toolbox.Pair;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Triple;
import com.cromoteca.meshcms.server.core.AuthorizationVerifier;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.Context.RequestContext;
import com.cromoteca.meshcms.server.core.MultiSiteManager;
import com.cromoteca.meshcms.server.core.PageInfo;
import com.cromoteca.meshcms.server.core.PageParser;
import com.cromoteca.meshcms.server.core.PageView;
import com.cromoteca.meshcms.server.core.Server;
import com.cromoteca.meshcms.server.core.SessionUser;
import com.cromoteca.meshcms.server.core.SiteMap;
import com.cromoteca.meshcms.server.core.User;
import com.cromoteca.meshcms.server.core.WebSite;
import com.cromoteca.meshcms.server.core.WebUtils;
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.toolbox.Exceptions;
import com.cromoteca.meshcms.server.toolbox.FileNameComparator;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.Locales;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.glaforge.i18n.io.SmartEncodingInputStream;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import dk.contix.gwt.annotations.Service;
import dk.contix.gwt.annotations.ServiceMethod;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletException;
import net.htmlparser.jericho.Source;

@Service(service = "com.cromoteca.meshcms.client.core.Services", relPath = "admin")
public class ServicesImpl extends RemoteServiceServlet implements Services {
	private static final FileNameComparator FILE_NAME_COMPARATOR;

	static {
		FILE_NAME_COMPARATOR = new FileNameComparator();
		FILE_NAME_COMPARATOR.setCaseSensitive(false);
	}

	private static void addDirectoryToMap(Map<Path, SortedSet<FileInfo>> map,
		Path dirPath, int depth, boolean includeFiles)
		throws AuthorizationException {
		if (map.containsKey(dirPath)) {
			return;
		}

		RequestContext rc = Context.getRequestContext();
		Server server = Context.getServer();
		PersistentPageData persistentPageData = rc.getWebSite()
					.getPersistentPageData();
		SiteMap siteMap = rc.getSiteMap();
		Path welcomePath = siteMap.getCurrentWelcome(dirPath);
		SortedSet<FileInfo> fileInfoList = new TreeSet<FileInfo>();
		File dirFile = rc.getWebSite().getFile(dirPath);
		List<File> files = dirFile.getChildren();

		if (files != null && files.size() > 0) {
			SessionUser user = null;
			Collections.sort(files, FILE_NAME_COMPARATOR);

			for (File file : files) {
				boolean include = includeFiles || file.isDirectory();
				String fileName = file.getName();
				Path filePath = dirPath.add(fileName);

				if (rc.getWebSite().isSystem(filePath)) {
					include = false;
				}

				if (include && filePath.isContainedIn(server.getCMSPath())) {
					if (user == null) {
						user = Context.getUser();
					}

					if (!user.getUser().isAdmin()) {
						for (CMSDirectoryItem cmsDirectoryItem : server.getCMSDirectoryItems()) {
							if (cmsDirectoryItem.isAdmin()
										&& filePath.isContainedIn(cmsDirectoryItem.getPath())) {
								include = false;

								break;
							}
						}
					}
				}

				if (include) {
					FileInfo fileInfo = new FileInfo(filePath);
					fileInfo.setDirectory(file.isDirectory());
					fileInfo.setLength(WebUtils.formatFileLength(file.getLength()));
					fileInfo.setLastModified(new Date(file.getLastModified()));
					fileInfoList.add(fileInfo);

					if (welcomePath != null) {
						PageInfo pageInfo = siteMap.getPageInfo(filePath);

						if (pageInfo != null) {
							boolean welcome = welcomePath.equals(filePath);
							Path p = welcome ? dirPath : filePath;
							fileInfo.setPageInfoValues(pageInfo.getMenuTitle(),
								persistentPageData.getScore(p), welcome,
								persistentPageData.getTheme(p));
						}
					}

					if (depth > 0 && file.isDirectory()) {
						addDirectoryToMap(map, filePath, depth - 1, includeFiles);
					}
				}
			}
		}

		map.put(dirPath, fileInfoList);
	}

	@ServiceMethod
	public Outcome<List<FileInfo>> getZipContents(Path zipPath)
		throws IOException, AuthorizationException {
		new AuthorizationVerifier();

		WebSite webSite = Context.getRequestContext().getWebSite();
		List<FileInfo> files = new ArrayList<FileInfo>();
		InputStream in = null;

		try {
			File zipFile = webSite.getFile(zipPath);
			in = new BufferedInputStream(zipFile.getInputStream());

			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry e;

			while ((e = zin.getNextEntry()) != null) {
				FileInfo fileInfo = new FileInfo(new Path(e.getName()));
				fileInfo.setDirectory(e.isDirectory());
				fileInfo.setLastModified(new Date(e.getTime()));
				fileInfo.setLength(WebUtils.formatFileLength(e.getSize()));
				files.add(fileInfo);
			}
		} finally {
			in.close();
		}

		return new Outcome<List<FileInfo>>(files);
	}

	@ServiceMethod
	public Outcome<Boolean> createDirectory(Path parentPath, String dirName)
		throws AuthorizationException {
		boolean ok = Context.getRequestContext().getWebSite()
					.createDirectory(parentPath.add(dirName));
		Outcome<Boolean> outcome = new Outcome<Boolean>(ok);

		if (!ok) {
			outcome.setMessage(Context.getMessages().getString("fmErrorNoNewDir")
						.replace("{0}", dirName));
			outcome.setError(true);
		}

		return outcome;
	}

	@ServiceMethod
	public Outcome<Boolean> createFile(Path parentPath, String fileName)
		throws AuthorizationException {
		WebSite webSite = Context.getRequestContext().getWebSite();
		boolean ok = webSite.createFile(parentPath.add(fileName));

		if (ok && Context.getServer().isPage(fileName)) {
			webSite.updateSiteMap(true);
		}

		Outcome<Boolean> outcome = new Outcome<Boolean>();

		if (!ok) {
			outcome.setMessage(Context.getMessages().getString("fmErrorNoNewFile")
						.replace("{0}", fileName));
			outcome.setError(true);
		}

		return outcome;
	}

	/**
	 * Tries to delete a list of files.
	 *
	 * @return a list that contains all not deleted files
	 */
	@ServiceMethod
	public Outcome<Boolean> deleteFiles(List<Path> files)
		throws AuthorizationException {
		WebSite webSite = Context.getRequestContext().getWebSite();
		List<Path> failed = new ArrayList<Path>();

		boolean update = false;

		for (Path path : files) {
			if (webSite.isDirectory(path)
						|| Context.getServer().isPage(path.getLastElement())) {
				update = true;
			}

			if (!webSite.delete(path, true)) {
				failed.add(path);
			}
		}

		if (update) {
			webSite.updateSiteMap(true);
		}

		Outcome<Boolean> outcome = new Outcome<Boolean>(failed.size() < files.size());

		if (!failed.isEmpty()) {
			outcome.setMessage(Context.getMessages().getString("fmErrorNotDel")
						.replace("{0}", getPathListAsString(failed)));
			outcome.setError(true);
		}

		return outcome;
	}

	private String getPathListAsString(List<Path> paths) {
		return Strings.generateList(paths, ", ",
			new Function<String, Path>() {
				public String execute(Path param) {
					return param.getLastElement();
				}
			});
	}

	@ServiceMethod
	public Outcome<Boolean> moveFiles(List<Path> files, Path newDir)
		throws AuthorizationException {
		WebSite webSite = Context.getRequestContext().getWebSite();
		List<Path> failed = new ArrayList<Path>();
		boolean update = false;

		for (Path path : files) {
			if (webSite.isDirectory(path)
						|| Context.getServer().isPage(path.getLastElement())) {
				update = true;
			}

			if (!webSite.move(path, newDir.add(path.getLastElement()))) {
				failed.add(path);
			}
		}

		if (update) {
			webSite.updateSiteMap(true);
		}

		Outcome<Boolean> outcome = new Outcome<Boolean>(failed.size() < files.size());

		if (!failed.isEmpty()) {
			outcome.setMessage(Context.getMessages().getString("fmErrorNotMoved")
						.replace("{0}", getPathListAsString(failed))
						.replace("{1}", newDir.getLastElement()));
			outcome.setError(true);
		}

		return outcome;
	}

	@ServiceMethod
	public Outcome<Boolean> duplicateFile(Path filePath, String newName)
		throws AuthorizationException {
		WebSite webSite = Context.getRequestContext().getWebSite();
		boolean ok = webSite.copyFile(filePath, newName);

		if (ok
					&& (webSite.isDirectory(filePath)
					|| Context.getServer().isPage(filePath.getLastElement())
					|| Context.getServer().isPage(newName))) {
			webSite.updateSiteMap(true);
		}

		Outcome<Boolean> outcome = new Outcome<Boolean>(ok);

		if (!ok) {
			outcome.setMessage(Context.getMessages().getString("fmErrorNotDuplicated")
						.replace("{0}", filePath.getLastElement()).replace("{1}", newName));
			outcome.setError(true);
		}

		return outcome;
	}

	@ServiceMethod
	public Outcome<Boolean> copyFiles(List<Path> files, Path newDir)
		throws AuthorizationException {
		WebSite webSite = Context.getRequestContext().getWebSite();
		List<Path> failed = new ArrayList<Path>();

		for (Path path : files) {
			if (!webSite.copyFile(path, newDir.add(path.getLastElement()))) {
				failed.add(path);
			}
		}

		Outcome<Boolean> outcome = new Outcome<Boolean>(failed.size() < files.size());

		if (!failed.isEmpty()) {
			outcome.setMessage(Context.getMessages().getString("fmErrorNotCopied")
						.replace("{0}", getPathListAsString(failed))
						.replace("{1}", newDir.getLastElement()));
			outcome.setError(true);
		}

		return outcome;
	}

	@ServiceMethod
	public Outcome<Boolean> changeDates(List<Path> files, Date date)
		throws AuthorizationException {
		Calendar newDate = GregorianCalendar.getInstance();
		Calendar oldDate = GregorianCalendar.getInstance();
		WebSite webSite = Context.getRequestContext().getWebSite();
		List<Path> failed = new ArrayList<Path>();
		boolean update = false;

		for (Path path : files) {
			newDate.setTime(date);

			File file = webSite.getFile(path);
			oldDate.setTimeInMillis(file.getLastModified());
			newDate.set(Calendar.HOUR_OF_DAY, oldDate.get(Calendar.HOUR_OF_DAY));
			newDate.set(Calendar.MINUTE, oldDate.get(Calendar.MINUTE));
			newDate.set(Calendar.SECOND, oldDate.get(Calendar.SECOND));
			newDate.set(Calendar.MILLISECOND, oldDate.get(Calendar.MILLISECOND));

			if (Context.getServer().isPage(path.getLastElement())) {
				update = true;
			}

			if (!webSite.setFileTime(path, newDate.getTimeInMillis())) {
				failed.add(path);
			}
		}

		if (update) {
			webSite.updateSiteMap(true);
		}

		Outcome<Boolean> outcome = new Outcome<Boolean>(failed.size() < files.size());

		if (!failed.isEmpty()) {
			outcome.setMessage(Context.getMessages().getString("fmErrorNotTouched")
						.replace("{0}", getPathListAsString(failed)));
			outcome.setError(true);
		}

		return outcome;
	}

	@ServiceMethod
	public Outcome<Boolean> renameFile(Path filePath, String newName)
		throws AuthorizationException {
		WebSite webSite = Context.getRequestContext().getWebSite();
		boolean ok = webSite.move(filePath, filePath.replaceLast(newName));
		Outcome<Boolean> outcome = new Outcome<Boolean>(ok);

		if (ok
					&& (Context.getServer().isPage(filePath.getLastElement())
					|| Context.getServer().isPage(newName))) {
			webSite.updateSiteMap(true);
		}

		if (!ok) {
			outcome.setMessage(Context.getMessages().getString("fmErrorNotRenamed")
						.replace("{0}", filePath.getLastElement()).replace("{0}", newName));
			outcome.setError(true);
		}

		return outcome;
	}

	@ServiceMethod
	public Outcome<Map<Path, SortedSet<FileInfo>>> getDirContents(
		SortedSet<Path> directories,
		int depth,
		boolean includeFiles) throws AuthorizationException {
		new AuthorizationVerifier();

		Map<Path, SortedSet<FileInfo>> results = new HashMap<Path, SortedSet<FileInfo>>();

		for (Path dirPath : directories) {
			addDirectoryToMap(results, dirPath, depth, includeFiles);
		}

//		System.out.println("RETURNING RESULTS:");
//
//		for (Path p : results.keySet()) {
//			String s = "                ".substring(0, p.getElementCount() * 2);
//			System.out.print(s);
//			System.out.print("PATH: ");
//			System.out.println(p.asAbsolute());
//			System.out.print(s);
//			System.out.print("  FILES: ");
//
//			SortedSet<FileInfo> sf = results.get(p);
//
//			for (FileInfo fi : sf) {
//				System.out.print(fi.getName());
//				System.out.print(", ");
//			}
//
//			System.out.println("");
//		}
		return new Outcome<Map<Path, SortedSet<FileInfo>>>(results);
	}

	@ServiceMethod
	public Outcome<Null> unzipFile(Path zipPath, String dirName)
		throws IOException, AuthorizationException {
		// TODO: verify user permissions as in other dist writes
		Path dir = zipPath.getParent();
		new AuthorizationVerifier().verify(dir);

		if (!Strings.isNullOrEmpty(dirName)) {
			dir = dir.add(dirName);
		}

		WebSite webSite = Context.getRequestContext().getWebSite();
		IO.unzip(webSite.getFile(zipPath), webSite.getFile(dir));
		webSite.updateSiteMap(true);

		return new Outcome<Null>();
	}

	@ServiceMethod
	public Outcome<SiteInfo> getSiteInfo(boolean refreshSiteMap)
		throws IOException, AuthorizationException {
		new AuthorizationVerifier();

		RequestContext rc = Context.getRequestContext();

		if (refreshSiteMap) {
			rc.getWebSite().updateSiteMap(true);
		}

		return new Outcome<SiteInfo>(rc.getSiteMap().getSiteInfo());
	}

	@ServiceMethod
	public Outcome<Page> getPage(Path pagePath, boolean searchDraft)
		throws IOException, AuthorizationException {
		new AuthorizationVerifier();

		Page page = fetchPage(pagePath, searchDraft);

		return new Outcome<Page>(page);
	}

	private Page fetchPage(Path pagePath, boolean searchDraft)
		throws IOException {
		Page page = null;
		File pageFile = Context.getRequestContext().getSiteMap()
					.getServedFile(pagePath);

		if (pageFile.isFile()) {
			InputStream inputStream = pageFile.getInputStream();
			SmartEncodingInputStream seis = new SmartEncodingInputStream(inputStream,
					SmartEncodingInputStream.BUFFER_LENGTH_8KB, IO.ISO_8859_1);
			Source source = new Source(seis.getReader());
			inputStream.close();

			PageParser pageParser = new PageParser();
			page = pageParser.parse(source, searchDraft);
		}

		return page;
	}

	@ServiceMethod
	public Outcome<Null> savePage(Path pagePath, Page page, boolean asDraft)
		throws IOException, AuthorizationException {
		WebSite webSite = Context.getRequestContext().getWebSite();

		try {
			pagePath = webSite.getSiteMap().getServedPath(pagePath);
			webSite.getFile(pagePath.getParent()).create(true);
			new PageView(page).save(pagePath, asDraft);
			webSite.updateSiteMap(true);
		} catch (ServletException ex) {
			Exceptions.throwAsRuntime(ex);
		}

		return new Outcome<Null>();
	}

	@ServiceMethod
	public Outcome<Null> deleteDraft(Path pagePath)
		throws IOException, AuthorizationException {
		new AuthorizationVerifier().verify(pagePath);

		Page page = fetchPage(pagePath, false);
		page.getPageConfiguration().setDraft(null);
		savePage(pagePath, page, false);

		return new Outcome<Null>();
	}

	@ServiceMethod
	public Outcome<Null> updatePersistentPageData(List<FileInfo> files)
		throws IOException, AuthorizationException {
		AuthorizationVerifier verifier = new AuthorizationVerifier();

		for (FileInfo fileInfo : files) {
			verifier.verify(fileInfo.getPath());
		}

		WebSite webSite = Context.getRequestContext().getWebSite();
		SiteMap siteMap = webSite.getSiteMap();
		PersistentPageData ppd = webSite.getPersistentPageData();

		for (FileInfo fileInfo : files) {
			Path path = siteMap.getPathInMenu(fileInfo.getPath());
			ppd.setScore(path, fileInfo.getPageInfo().getScore());
			ppd.setTheme(path, fileInfo.getPageInfo().getTheme());
		}

		webSite.storePersistentPageData();
		webSite.updateSiteMap(true);

		return new Outcome<Null>();
	}

	@ServiceMethod
	public Outcome<Null> logout() {
		Context.removeUser();

		return new Outcome<Null>();
	}

	@ServiceMethod
	public Outcome<SitesConfiguration> getVirtualSites()
		throws AuthorizationException {
		new AuthorizationVerifier().verifyAdmin();

		return new Outcome<SitesConfiguration>(Context.getServer()
					.getMultiSiteManager().getSitesConfiguration());
	}

	@ServiceMethod
	public Outcome<Null> saveVirtualSites(SitesConfiguration configuration)
		throws IOException, AuthorizationException {
		new AuthorizationVerifier().verifyAdmin();

		MultiSiteManager msm = Context.getServer().getMultiSiteManager();
		msm.setSitesConfiguration(configuration);
		msm.initDomainMap();
		msm.store();

		Outcome<Null> outcome = new Outcome<Null>();
		outcome.setMessage(Context.getConstants().getString("sitesSaveOk"));

		return outcome;
	}

	@ServiceMethod
	public Outcome<SiteConfiguration> getSiteConfiguration()
		throws AuthorizationException {
		new AuthorizationVerifier().verifyAdmin();

		return new Outcome<SiteConfiguration>(Context.getRequestContext()
					.getWebSite().getSiteConfiguration());
	}

	@ServiceMethod
	public Outcome<Pair<SiteConfiguration, HashMap<String, String>>> getSiteConfigurationAndLocales()
		throws AuthorizationException {
		AuthorizationVerifier verifier = new AuthorizationVerifier().verifyAdmin();
		SiteConfiguration siteConfiguration = Context.getRequestContext()
					.getWebSite().getSiteConfiguration();
		HashMap<String, String> availableLocales = getAvailableLocales(verifier.getUser()
						.getUser().getLocale());
		Pair<SiteConfiguration, HashMap<String, String>> result = new Pair<SiteConfiguration, HashMap<String, String>>(siteConfiguration,
				availableLocales);

		return new Outcome<Pair<SiteConfiguration, HashMap<String, String>>>(result);
	}

	@ServiceMethod
	public Outcome<Null> saveSiteConfiguration(SiteConfiguration configuration)
		throws IOException, AuthorizationException {
		new AuthorizationVerifier().verifyAdmin();
		Context.getRequestContext().getWebSite()
				.storeSiteConfiguration(configuration);

		Outcome<Null> outcome = new Outcome<Null>();
		outcome.setMessage(Context.getConstants().getString("configSaveOk"));

		return outcome;
	}

	@ServiceMethod
	public Outcome<ServerConfiguration> getServerConfiguration()
		throws AuthorizationException {
		new AuthorizationVerifier().verifyAdmin();

		return new Outcome<ServerConfiguration>(Context.getServer()
					.getServerConfiguration());
	}

	@ServiceMethod
	public Outcome<Null> saveServerConfiguration(
		ServerConfiguration configuration)
		throws IOException, AuthorizationException {
		new AuthorizationVerifier().verifyAdmin();

		Server server = Context.getServer();
		Context.storeServerConfiguration(configuration,
			server.getMainWebSite()
					.getCMSFile(CMSDirectoryItem.SERVER_CONFIGURATION_FILE));
		server.setServerConfiguration(configuration);

		Outcome<Null> outcome = new Outcome<Null>();
		outcome.setMessage(Context.getConstants().getString("configSaveOk"));

		return outcome;
	}

	@ServiceMethod
	public HashMap<String, String> getAvailableLocales(String preferredLocaleName) {
		Locale preferredLocale = preferredLocaleName == null ? null
			: Locales.getLocale(preferredLocaleName);
		HashMap<String, String> locales = new HashMap<String, String>();

		for (Locale locale : Locale.getAvailableLocales()) {
			locales.put(locale.toString(),
				locale.getDisplayName(preferredLocale == null ? locale : preferredLocale));
		}

		return locales;
	}

	@ServiceMethod
	public Outcome<Pair<String, HashMap<String, String>>> getAvailableLocalesInUserLocale()
		throws AuthorizationException {
		AuthorizationVerifier verifier = new AuthorizationVerifier();
		String userLocale = verifier.getUser().getUser().getLocale();
		HashMap<String, String> availableLocales = getAvailableLocales(userLocale);
		Pair<String, HashMap<String, String>> result = new Pair<String, HashMap<String, String>>(userLocale,
				availableLocales);

		return new Outcome<Pair<String, HashMap<String, String>>>(result);
	}

	@ServiceMethod
	public Outcome<Pair<UserProfile, HashMap<String, String>>> getCurrentUserProfileAndLocales()
		throws AuthorizationException {
		AuthorizationVerifier verifier = new AuthorizationVerifier();
		UserProfile profile = verifier.getUser().getUserProfile();
		HashMap<String, String> locales = getAvailableLocales(null);
		Pair<UserProfile, HashMap<String, String>> result = new Pair<UserProfile, HashMap<String, String>>(profile,
				locales);

		return new Outcome<Pair<UserProfile, HashMap<String, String>>>(result);
	}

	@ServiceMethod
	public Outcome<Null> saveUserProfile(UserProfile profile)
		throws IOException, AuthorizationException {
		AuthorizationVerifier verifier = new AuthorizationVerifier();
		SessionUser user = verifier.getUser();
		user.setUserProfile(profile);
		user.store();

		Outcome<Null> outcome = new Outcome<Null>();
		outcome.setMessage(Context.getMessages().getString("userOk")
					.replace("{0}", user.getUsername()));

		return outcome;
	}

	@ServiceMethod
	public Outcome<Null> saveNewUser(UserProfile profile)
		throws IOException, ServerException, AuthorizationException {
		new AuthorizationVerifier().verifyAdmin();

		if (User.getFile(Context.getRequestContext().getWebSite(),
						profile.getUsername()).exists()) {
			throw new ServerException(Context.getConstants().getString("userExists"));
		}

		SessionUser user = new SessionUser(profile);
		user.store();

		Outcome<Null> outcome = new Outcome<Null>();
		outcome.setMessage(Context.getConstants().getString("userOk"));

		return outcome;
	}

	@ServiceMethod
	public Outcome<UserProfile> login(String username, String password)
		throws IOException {
		Outcome<UserProfile> outcome = new Outcome<UserProfile>();
		SessionUser user = SessionUser.load(username, password);

		if (user != null) {
			Context.setUser(user);
			outcome.setValue(user.getUserProfile());
		} else {
			outcome.setMessage(Context.getConstants().getString("loginError"));
			outcome.setError(true);
		}

		return outcome;
	}

	@ServiceMethod
	public Outcome<Triple<String, String, String>> getFile(Path filePath)
		throws IOException {
		String text = IO.readFully(Context.getRequestContext().getWebSite()
						.getFile(filePath));
		String lang = "en";

		//basic,brainfuck,c,coldfusion,cpp,css,html,java,js,pas,perl,php,python,ruby,robotstxt,sql,tsql,vb,xml
		String ext = Strings.getExtension(filePath.getLastElement(), false);
		String syntax;

		if ("css".equals(ext)) {
			syntax = "css";
		} else if ("htm".equals(ext)) {
			syntax = "html";
		} else if ("html".equals(ext)) {
			syntax = "html";
		} else if ("css".equals(ext)) {
			syntax = "jsp";
		} else if ("css".equals(ext)) {
			syntax = "html";
		} else if ("js".equals(ext)) {
			syntax = "js";
		} else if ("json".equals(ext)) {
			syntax = "js";
		} else if ("xml".equals(ext)) {
			syntax = "xml";
		} else {
			syntax = "";
		}

		return new Outcome<Triple<String, String, String>>(new Triple<String, String, String>(
				text,
				lang,
				syntax));
	}

	@ServiceMethod
	public Outcome<Boolean> saveFile(Path filePath, String contents)
		throws AuthorizationException {
		WebSite webSite = Context.getRequestContext().getWebSite();
		boolean ok = webSite.saveToFile(contents, filePath);
		Outcome<Boolean> outcome = new Outcome<Boolean>(ok);

		if (ok && Context.getServer().isPage(filePath.getLastElement())) {
			webSite.updateSiteMap(true);
		}

		if (!ok) {
			outcome.setError(true);
			outcome.setMessage(Context.getConstants().getString("saveError"));
		}

		return outcome;
	}

	@ServiceMethod
	public Outcome<Path> getNewPagePath(Path dirPath)
		throws AuthorizationException {
		new AuthorizationVerifier().verify(dirPath);

		Outcome<Path> outcome = new Outcome<Path>();

		if (Context.getRequestContext().getWebSite().getFile(dirPath).exists()) {
			outcome.setMessage(Context.getConstants().getString("newpageAlreadyExists"));
			outcome.setError(true);
		} else {
			outcome.setValue(dirPath.add(Context.getServer().getWelcomeFiles()[0]));
		}

		return outcome;
	}

	@ServiceMethod
	public Outcome<String> createFileName(String text) {
		return new Outcome<String>(IO.fixFileName(text, true));
	}

	@ServiceMethod
	public Outcome<ServerInfo> getServerInfo() throws AuthorizationException {
		Runtime runtime = Runtime.getRuntime();
		ServerInfo info = new ServerInfo();
		info.setUser(new AuthorizationVerifier().getUser().getUserProfile());
		info.setVersion(WebSite.APP_VERSION);
		info.setCharset(IO.SYSTEM_CHARSET);
		info.setTotalMB((int) (runtime.maxMemory() / IO.MBYTE));
		info.setUsedMB((int) ((runtime.totalMemory() - runtime.freeMemory()) / IO.MBYTE));
		info.setPageCount(Context.getRequestContext().getSiteMap()
					.getPageList(Path.ROOT).size());

		return new Outcome<ServerInfo>(info);
	}

	public String htmlToText(String html) {
		if (html == null) {
			return null;
		}

		html = html.replaceAll("</[pP]>", "\n\n");
		html = html.replaceAll("<[bB][rR] ?/?>", "\n");
		html = html.replaceAll("</?\\S+?[\\s\\S+]*?>", " ");

		return html;
	}

	@ServiceMethod
	public Outcome<Date> getFileDate(Path path) throws AuthorizationException {
		new AuthorizationVerifier().verify(path);

		RequestContext rc = Context.getRequestContext();
		path = rc.getSiteMap().getServedPath(path);

		return new Outcome<Date>(new Date(rc.getWebSite().getFile(path)
						.getLastModified()));
	}
}
