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

import com.cromoteca.meshcms.client.server.FileTypes;
import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.client.server.SiteConfiguration;
import com.cromoteca.meshcms.client.server.Theme;
import com.cromoteca.meshcms.client.toolbox.Pair;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context.RequestContext;
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.InMemoryResponseWrapper;
import com.cromoteca.meshcms.server.toolbox.Locales;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.toolbox.Time;
import com.cromoteca.meshcms.server.toolbox.Web;
import com.cromoteca.meshcms.server.webview.Scope;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.htmlparser.jericho.Source;

public class HitFilter implements Filter {
	public static final String PAGE_VIEW_ATTRIBUTE = "mesh";
	public static final String THEME_DIR_ATTRIBUTE = "mesh-theme-path";
	public static final String LAST_MODIFIED_ATTRIBUTE = "meshcmslastmodified";
	private static String htmlMimeType;

	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext servletContext = filterConfig.getServletContext();
		htmlMimeType = servletContext.getMimeType("index.html");
		Context.setServletContext(servletContext);
	}

	public void doFilter(ServletRequest req, ServletResponse resp,
		FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		Context.setRequestContext(request, response);

		// TODO: find a better way
		response.setBufferSize(256 * 1024);

		/* SessionUser user = SessionUser.load(User.DEFAULT_ADMIN_USERNAME,
		    User.DEFAULT_ADMIN_PASSWORD);
		
		if (user != null) {
		  Context.setUser(user);
		} */
		try {
			new Hit(request, response, chain).doFilter();
		} finally {
			Context.remove();
		}
	}

	public void destroy() {}

	public static Path getPreferredLanguage(HttpServletRequest request)
		throws IOException {
		RequestContext rc = Context.getRequestContext();
		List<Pair<String, Locale>> available = rc.getSiteMap().getLangList();
		String[] accepted = Web.getAcceptedLanguages(request);
		Pair<String, Locale> chosen = null;

		if (available != null && available.size() > 0) {
			for (int i = 0; chosen == null && i < accepted.length; i++) {
				for (Pair<String, Locale> pair : available) {
					if (pair.getFirstObject().equalsIgnoreCase(accepted[i])) {
						chosen = pair;

						break;
					}
				}
			}

			if (chosen == null) {
				chosen = available.get(0);
			}

			return new Path(chosen.getFirstObject());
		}

		return null;
	}

	private class Hit {
		private final FilterChain chain;
		private final HttpServletRequest request;
		private final HttpServletResponse response;
		private final RequestContext rc;
		private final Server server;
		private final SessionUser user;
		private File cacheFile;
		private boolean gzip;

		private Hit(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) {
			this.request = request;
			this.response = response;
			this.chain = chain;
			rc = Context.getRequestContext();
			server = Context.getServer();
			user = Context.getUser();
		}

		private void doFilter() throws IOException, ServletException {
			if (rc.getWebSite() == null) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Site not found");

				return;
			}

			if (!rc.isRedirectVerified()) {
				rc.setRedirectVerified(true);

				if (redirect()) {
					return;
				}
			}

			if (!rc.isHeadersSet()) {
				rc.setHeadersSet(true);
				setHeaders();
			}

			if (!rc.isCacheVerified()) {
				rc.setCacheVerified(true);

				cacheFile = getCacheFile();

				if (cacheFile != null) {
					String ae = request.getHeader("Accept-Encoding");
					gzip = ae != null && ae.toLowerCase().indexOf("gzip") > -1;

					if (gzip) {
						response.setHeader("Content-Encoding", "gzip");
					}

					if (getFromCache()) {
						return;
					}
				}
			}

			if (!rc.isCMSDone()) {
				rc.setCMSDone(true);
				doCMS();
			} else {
				chain.doFilter(request, response);
			}
		}

		private boolean redirect() throws IOException, ServletException {
			String redirect = null;
			boolean forward = false;
			String requestPath = Strings.noNull(request.getServletPath())
				+ Strings.noNull(request.getPathInfo());

			if (rc.getWebSite().isDirectory(new Path(requestPath))
						&& !requestPath.endsWith("/")) {
				redirect = requestPath + '/';
			}

			if (redirect == null
						&& !(rc.getPagePath()
						.isContainedIn(Context.getServer().getCMSServerPath())
						|| rc.getPagePath().isContainedIn(Context.MESHCMS_PATH))) {
				String virtual = rc.getWebSite().getRootPath().add(rc.getFilePath())
							.asAbsolute();

				if (!requestPath.equals(virtual)) {
					redirect = virtual;
					forward = true;
				}
			}

			// users are always null in websites without cms
			if (redirect == null
						&& user == null
						&& rc.getWebSite().getSiteConfiguration().isRedirectRoot()
						&& rc.getPagePath().isRoot()) {
				Path rp = getPreferredLanguage(request);

				if (rp != null) {
					redirect = rc.getWebSite().getAbsoluteLink(rp);
				}
			}

			if (redirect == null
						&& rc.getWebSite().isCMSEnabled()
						&& rc.getWebSite().getSiteConfiguration().isSearchMovedPages()
						&& !rc.getWebSite().getFile(rc.getFilePath()).isFile()) {
				Path redirPath = rc.getSiteMap().getRedirMatch(rc.getPagePath());

				if (redirPath != null) {
					response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
					response.setHeader("Location",
						request.getContextPath() + '/' + redirPath);

					return true;
				}
			}

			if (redirect == null && rc.getPagePath().equals(Context.MESHCMS_PATH)) {
				redirect = Context.MESHCMS_PATH.asAbsolute()
					+ "/resources/host.jsp?mode=file_manager";
			}

			if (redirect != null) {
				if (forward) {
					request.getRequestDispatcher(redirect).forward(request, response);
				} else {
					Context.redirect(redirect, true);
				}

				return true;
			}

			return false;
		}

		private boolean getFromCache() {
			if (cacheFile != null
						&& cacheFile.isFile()
						&& cacheFile.getLength() > 256) {
				if (cacheFile.getLastModified() < rc.getWebSite()
							.getFile(rc.getFilePath()).getLastModified()) {
					return false;
				}

				if ( /* server.isPage(rc.getFilePath().getLastElement()) && */
						cacheFile.getLastModified() < rc.getSiteMap().getLastModified()) {
					return false;
				}

				try {
					InputStream inputStream = cacheFile.getInputStream();

					if (gzip) {
						response.setContentLength((int) cacheFile.getLength());
					} else {
						response.setContentLength(-1);
						inputStream = new GZIPInputStream(inputStream);
					}

					OutputStream outputStream = response.getOutputStream();
					IO.copyStream(inputStream, outputStream, false);
					outputStream.flush();

					return true;
				} catch (IOException ex) {
					// avoid occasional errors like reading while writing
					Context.log(ex);
				}
			}

			return false;
		}

		private void doCMS() throws IOException, ServletException {
			Theme theme = null;

			if (rc.getPageInfo() == null) {
				if (user != null) {
					Context.setLocale(Locales.getLocale(user.getLocale()));
				}
			} else {
				if (user == null) {
					rc.getPageInfo().addHit();
				}

				SiteConfiguration configuration = rc.getWebSite().getSiteConfiguration();
				Locale locale = null;

				if (configuration.isOverrideLocale() && !rc.getPagePath().isRoot()) {
					locale = Locales.getLocale(rc.getPagePath().getElementAt(0));
				}

				if (locale == null) {
					locale = Locales.getLocale(configuration.getLocale());
				}

				if (locale != null) {
					Context.setLocale(locale);
				}

				WebUtils.updateLastModifiedTime(request,
					rc.getWebSite().getFile(rc.getFilePath()));

				if (!(rc.getPagePath().isContainedIn(server.getCMSPath())
							|| rc.getPagePath().isContainedIn(Context.MESHCMS_PATH))) {
					theme = rc.getSiteMap().getTheme(rc.getPagePath());
				}
			}

			HttpServletResponse cachedResponse;

			if (cacheFile == null) {
				cachedResponse = response;
			} else {
				cacheFile.getParent().create(true);
				cachedResponse = new CacheResponseWrapper(response,
						cacheFile.getOutputStream(), gzip);
			}

			if (theme == null) {
				chain.doFilter(request, cachedResponse);
			} else {
				InMemoryResponseWrapper responseWrapper = new InMemoryResponseWrapper(cachedResponse);
				chain.doFilter(request, responseWrapper);

				Source source = null;
				String pageMimeType = responseWrapper.getContentType();

				if (pageMimeType != null && pageMimeType.contains(htmlMimeType)) {
					source = responseWrapper.getSource();
				}

				if (source == null) {
					byte[] b = responseWrapper.getAsBytes();
					IO.copyStream(new ByteArrayInputStream(b),
						cachedResponse.getOutputStream(), false);
				} else {
					Path themePath = theme.getPath().add(theme.getTemplate());
					boolean searchDraft = !(user == null || rc.isNoDraft());
					Page page = new PageParser().parse(source, searchDraft);
					Context.setScopedSingleton(page, Scope.REQUEST);
					Context.setScopedAttribute(THEME_DIR_ATTRIBUTE,
						themePath.getParent(), Scope.REQUEST);

					if (!themePath.isContainedIn(server.getCMSServerPath())) {
						themePath = rc.getWebSite().getRootPath().add(themePath);
					}

					PageView pageView = new PageView(page);
					Context.setScopedAttribute(PAGE_VIEW_ATTRIBUTE, pageView,
						Scope.REQUEST);
					request.getRequestDispatcher(themePath.asLink())
							.forward(request, cachedResponse);
				}
			}

			if (cachedResponse instanceof CacheResponseWrapper) {
				((CacheResponseWrapper) cachedResponse).finishResponse();
			}

			rc.getWebSite().updateSiteMap(false); // better here than nowhere :)
		}

		private File getCacheFile() {
			if (rc.getFlashObject() == null
						&& (rc.getPageInfo() == null || user == null)
						&& "get".equalsIgnoreCase(request.getMethod())
						&& (FileTypes.isCompressible(rc.getFilePath().getLastElement())
						|| Web.getRequestPath(request).indexOf(".cache.") >= 0)) {
				String fileName = rc.getPagePath().toString();
				String qs = rc.getURL().getQuery();

				if (!Strings.isNullOrEmpty(qs)) {
					fileName += '?' + qs;
				}

				// fileName = '_' + IO.fixFileName(fileName, true) + ".gz";
				fileName = '_' + Strings.getMD5(fileName) + ".gz";

				Path cachePath = rc.getWebSite()
							.getRepositoryPath(new Path("cache", fileName));

				return rc.getWebSite().getFile(cachePath);
			}

			return null;
		}

		private void setHeaders() throws UnsupportedEncodingException {
			String fileName = rc.getFilePath().getLastElement();

			if (fileName.indexOf(".cache.") >= 0) {
				long expiration = System.currentTimeMillis() + Time.YEAR;
				response.setDateHeader("Expires", expiration);
				response.setHeader("ETag", '"' + Long.toString(expiration) + '"');
				response.setHeader("Cache-Control", "max-age=" + Time.YEAR_SECONDS);
			} else if (server.isPage(fileName) || fileName.indexOf(".nocache.") >= 0) {
				response.setDateHeader("Expires", -1);
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Cache-Control",
					"no-store, no-cache, must-revalidate");
			}

			if (rc.getPageInfo() == null) {
				response.setContentType(Context.getServletContext().getMimeType(fileName));
				response.setDateHeader("Last-Modified",
					rc.getWebSite().getFile(rc.getFilePath()).getLastModified());
				response.setCharacterEncoding("utf-8");
				request.setCharacterEncoding("utf-8");
			} else {
				response.setContentType(htmlMimeType);
				response.setDateHeader("Last-Modified",
					rc.getSiteMap().getLastModified());
				response.setCharacterEncoding(IO.SYSTEM_CHARSET);
				request.setCharacterEncoding(IO.SYSTEM_CHARSET);
			}
		}
	}
}
