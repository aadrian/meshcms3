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

import com.cromoteca.meshcms.client.server.ServerConfiguration;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.toolbox.JSON;
import com.cromoteca.meshcms.server.toolbox.Locales;
import com.cromoteca.meshcms.server.toolbox.Web;
import com.cromoteca.meshcms.server.webview.Expiring;
import com.cromoteca.meshcms.server.webview.Scope;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Context extends com.cromoteca.meshcms.server.webview.Context {
	/**
	 * Name of the request parameter that is used to specify some actions.
	 * Currently only {@link #ACTION_NO_DRAFT} is used as value. This parameter is
	 * read by custom JSP tags.
	 */
	public static final String ACTION_NAME = "meshcmsaction";
	public static final String ACTION_NO_DRAFT = "nodraft";
	public static final String ACTION_COMPARE = "compare";
	public static final String JSTL_FMT_LOCALE = "javax.servlet.jsp.jstl.fmt.locale.request";
	public static final Path MESHCMS_PATH = new Path("meshcms");

	static void setWebSite(WebSite webSite) {
		setScopedSingleton(webSite, Scope.REQUEST);
	}

	private static Server initServer() {
		ServletContext servletContext = getServletContext();
		String cmsDir = servletContext.getInitParameter("cmsDir");
		Context.log("Init " + WebSite.APP_FULL_NAME + " with CMS directory "
			+ cmsDir + " on server " + servletContext.getServerInfo());

		Path path = new Path(cmsDir);

		if (path.isRoot() || path.isRelative()) {
			Context.log("CMS directory not valid: " + cmsDir);
		}

		try {
			return Server.init(path);
		} catch (IOException ex) {
			Context.log(ex);

			return null;
		}
	}

	public static Server getServer() {
		return getScopedSingleton(Server.class, Scope.APPLICATION);
	}

	static void setServer(Server server) {
		setScopedSingleton(server, Scope.APPLICATION);
	}

	public static SessionUser getUser() {
		return getScopedSingleton(SessionUser.class, Scope.SESSION);
	}

	public static void setUser(SessionUser user) {
		setScopedSingleton(user, Scope.SESSION);
	}

	public static void removeUser() {
		removeScopedSingleton(SessionUser.class, Scope.SESSION);
	}

	public static Locale getLocale() {
		boolean mustSet = true;
		Locale locale = null;
		Object jstlLocale = getScopedAttribute(JSTL_FMT_LOCALE, Object.class,
				Scope.REQUEST);

		if (jstlLocale instanceof Locale) {
			locale = (Locale) jstlLocale;
			mustSet = false;
		} else if (jstlLocale instanceof String) {
			locale = Locales.getLocale((String) jstlLocale);
		}

		if (locale == null) {
			locale = getRequest().getLocale();

			if (locale == null) {
				locale = Locale.getDefault();
			}
		}

		if (mustSet) {
			setLocale(locale);
		}

		return locale;
	}

	public static void setLocale(Locale locale) {
		setScopedAttribute(JSTL_FMT_LOCALE, locale, Scope.REQUEST);
		getResponse().setLocale(locale);
	}

	public static ZoneOutput getZoneOutput() {
		return getScopedSingleton(ZoneOutput.class, Scope.REQUEST);
	}

	public static void setZoneOutput(ZoneOutput zoneOutput) {
		setScopedSingleton(zoneOutput, Scope.REQUEST);
	}

	public static <T> T loadFromJSON(Class<T> cls, File file)
		throws IOException {
		if (file.exists()) {
			Reader reader = null;

			try {
				reader = new BufferedReader(new InputStreamReader(
							new FileInputStream(file)));

				return JSON.getGson().fromJson(reader, cls);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		}

		return null;
	}

	public static void storeToJSON(Object o, File file)
		throws IOException {
		Writer writer = null;

		try {
			writer = new OutputStreamWriter(new FileOutputStream(file));
			JSON.getGson().toJson(o, writer);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * Loads the configuration from file or creates a new configuration with
	 * default values if the file doesn't exist.
	 */
	public static ServerConfiguration loadServerConfiguration(File file)
		throws IOException {
		ServerConfiguration c = null;

		try {
			c = loadFromJSON(ServerConfiguration.class, file);
		} catch (Exception ex) {}

		if (c == null) {
			c = new ServerConfiguration();
		}

		return c;
	}

	/**
	 * Saves the current configuration to file.
	 */
	public static void storeServerConfiguration(
		ServerConfiguration configuration,
		File file) throws IOException {
		storeToJSON(configuration, file);
	}

	public static ResourceBundle getConstants() {
		return getBundle("Constants", false);
	}

	public static ResourceBundle getMessages() {
		return getBundle("Messages", false);
	}

	public static ResourceBundle getConstantsInUserLocale() {
		return getBundle("Constants", true);
	}

	public static ResourceBundle getMessagesInUserLocale() {
		return getBundle("Messages", true);
	}

	private static ResourceBundle getBundle(String name, boolean inUserLocale) {
		inUserLocale = inUserLocale && getUser() != null;

		String key = "b:" + name + inUserLocale;
		ResourceBundle bundle = Context.getScopedAttribute(key,
				ResourceBundle.class, Scope.REQUEST);

		if (bundle == null) {
			Locale locale = inUserLocale ? Locales.getLocale(getUser().getLocale())
				: getLocale();
			bundle = ResourceBundle.getBundle("com.cromoteca.meshcms.client.i18n."
					+ name, locale);
			Context.setScopedAttribute(key, bundle, Scope.REQUEST);
		}

		return bundle;
	}

	public static RequestContext getRequestContext() {
		return getScopedSingleton(RequestContext.class, Scope.REQUEST);
	}

	static void setRequestContext(HttpServletRequest request,
		HttpServletResponse response) {
		add(request, response);

		Server server = getScopedSingleton(Server.class, Scope.APPLICATION);

		if (server == null) {
			server = initServer();
		}

		if (!hasScopedSingleton(RequestContext.class, Scope.REQUEST)) {
			WebSite webSite = server.getWebSite(request);

			if (webSite != null) {
				Path path = new Path(Web.getRequestPath(request));
				Path filePath = path;
				PageInfo pageInfo = null;
				SiteMap siteMap = webSite.getSiteMap();

				if (siteMap == null) {
					if (webSite.isDirectory(path)) {
						for (String welcome : server.getWelcomeFiles()) {
							Path welcomePath = path.add(welcome);

							if (webSite.getFile(welcomePath).isFile()) {
								filePath = welcomePath;
							}
						}
					}
				} else {
					if (siteMap.isCurrentWelcome(path)) {
						path = path.getParent();
					} else if (webSite.isDirectory(path)) {
						Path welcomePath = siteMap.getCurrentWelcome(path);

						if (welcomePath != null) {
							filePath = welcomePath;
						}
					}

					pageInfo = siteMap.getPageInfo(path);
				}

				Path directoryPath = webSite.getDirectory(path);

				if (directoryPath == null) {
					directoryPath = filePath.getParent();
				}

				RequestContext rc = new RequestContext(getURL(), webSite, siteMap,
						pageInfo, path, directoryPath, filePath);
				setScopedSingleton(rc, Scope.REQUEST);
			}
		}
	}

	public static final class RequestContext {
		private final PageInfo pageInfo;
		private final Path directoryPath;
		private final Path filePath;
		private final Path pagePath;
		private final SiteMap siteMap;
		private final URL url;
		private final WebSite webSite;
		private Expiring flashObject;
		private Path adminPath;
		private Path meshPath;
		private boolean cacheVerified;
		private boolean cmsDone;
		private boolean headersSet;
		private boolean redirectVerified;

		public RequestContext(URL url, WebSite webSite, SiteMap siteMap,
			PageInfo pageInfo, Path pagePath, Path directoryPath, Path filePath) {
			this.url = url;
			this.webSite = webSite;
			this.siteMap = siteMap;
			this.pageInfo = pageInfo;
			this.pagePath = pagePath;
			this.directoryPath = directoryPath;
			this.filePath = filePath;
			cmsDone = !webSite.isCMSEnabled();
			flashObject = getExpiringAttribute(pagePath.toString(), Expiring.class,
					Scope.SESSION);
			adminPath = adjustPath(getServer().getCMSPath().asAbsolute());
			meshPath = adjustPath(MESHCMS_PATH.add("resources").asAbsolute());
		}

		public Path getAdminPath() {
			return adminPath;
		}

		public Path getMeshPath() {
			return meshPath;
		}

		public boolean isCMSDone() {
			return cmsDone;
		}

		public void setCMSDone(boolean cmsDone) {
			this.cmsDone = cmsDone;
		}

		public boolean isHeadersSet() {
			return headersSet;
		}

		public void setHeadersSet(boolean headersSet) {
			this.headersSet = headersSet;
		}

		public boolean isRedirectVerified() {
			return redirectVerified;
		}

		public void setRedirectVerified(boolean redirectVerified) {
			this.redirectVerified = redirectVerified;
		}

		public boolean isCacheVerified() {
			return cacheVerified;
		}

		public void setCacheVerified(boolean cacheVerified) {
			this.cacheVerified = cacheVerified;
		}

		public PageInfo getPageInfo() {
			return pageInfo;
		}

		public SiteMap getSiteMap() {
			return siteMap;
		}

		public URL getURL() {
			return url;
		}

		public Path getDirectoryPath() {
			return directoryPath;
		}

		public Path getFilePath() {
			return filePath;
		}

		public Path getPagePath() {
			return pagePath;
		}

		public WebSite getWebSite() {
			return webSite;
		}

		public Path adjustPath(String path) {
			return adjustPath(path, directoryPath);
		}

		public Path adjustPath(String path, Path root) {
			Path realPath;

			if (root == null || path.startsWith("/")) {
				realPath = new Path(path);
			} else {
				realPath = root.add(path);
			}

			return realPath.getRelativeTo(directoryPath == null ? root : directoryPath);
		}

		public boolean isNoDraft() {
			return ACTION_NO_DRAFT.equals(Web.getURLParameter(url.toString(),
					ACTION_NAME, false));
		}

		public boolean isCompare() {
			return ACTION_COMPARE.equals(Web.getURLParameter(url.toString(),
					ACTION_NAME, false));
		}

		public String getURL(Path path) throws MalformedURLException {
			return new URL(url,
				Context.getContextPath() + webSite.getAbsoluteLink(path)).toString();
		}

		public boolean isEditable() {
			SessionUser user = getUser();

			return user != null && user.getUser().canWrite(filePath);
		}

		public Expiring getFlashObject() {
			return flashObject;
		}

		public void storeFlashObject(Expiring value) {
			setExpiringAttribute(pagePath.toString(), value, Scope.SESSION);
		}
	}
}
