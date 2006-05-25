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

package org.meshcms.core;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.meshcms.util.*;

/**
 * Filter used to handle the requests for web pages.
 */
public final class HitFilter implements Filter {
  /**
   * Name of a cache file in the repository.
   */
  public static final String CACHE_FILE_NAME = "_cache.gz";

  /**
   * Name of the request attribute that contains the name of the current theme
   * file.
   *
   * @see RequestDecoratorMapper
   */
  public static final String THEME_FILE_ATTRIBUTE = "meshcmstheme";

  /**
   * Name of the request attribute that contains the name of the current theme
   * folder.
   */
  public static final String THEME_PATH_ATTRIBUTE = "meshcmsthemepath";

  public static final String LOCALE_ATTRIBUTE = "meshcmslocale";

  public static final String LAST_MODIFIED_ATTRIBUTE = "meshcmslastmodified";

  public static final String BLOCK_CACHE_ATTRIBUTE = "MeshCMS-No-Cache-Please";

  /**
   * Name of the session attribute that allows hotlinking within the session
   * itself.
   */
  public static final String HOTLINKING_ALLOWED = "MeshCMS-Hotlinking-Allowed";

  private FilterConfig filterConfig = null;

  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
  }

  public void destroy() {
    this.filterConfig = null;
  }

  /**
   * This filter manages a page to make sure it is served correctly.
   */
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    if (filterConfig != null && (request instanceof HttpServletRequest) /* &&
        FileTypes.isPage(((HttpServletRequest) request).getServletPath()) */) {
      ServletContext sc = filterConfig.getServletContext();
      HttpServletResponse httpRes = (HttpServletResponse) response;
      WebSite rootSite = getRootSite(sc, false);
      WebSite webSite = rootSite.getWebSite(request);
      
      if (webSite == null) {
        httpRes.sendError(HttpServletResponse.SC_FORBIDDEN, "Site not found");
        return;
      }
      
      request.setAttribute("webSite", webSite);
      HttpServletRequest httpReq = webSite.wrapRequest(request);
      Path pagePath = webSite.getRequestedPath(httpReq);

      if (webSite instanceof VirtualWebSite &&
          httpReq.getRequestURI().endsWith(".jsp/")) {
        httpRes.sendError(HttpServletResponse.SC_FORBIDDEN,
            "You are not allowed to request this file");
      }

      if (webSite.getConfiguration().isAlwaysRedirectWelcomes()) {
        Path wPath = webSite.findCurrentWelcome(pagePath);

        if (wPath != null) {
          String q = httpReq.getQueryString();

          if (Utils.isNullOrEmpty(q)) {
            httpRes.sendRedirect(httpReq.getContextPath() + "/" + wPath);
          } else {
            httpRes.sendRedirect(httpReq.getContextPath() + "/" + wPath + '?' + q);
          }

          return;
        }
      }
      
      SiteMap siteMap = null;
      PageInfo pageInfo = null;
      String pageCharset = null;
      boolean isAdminPage = false;
      boolean isGuest = true;

      if (webSite.getCMSPath() != null) {
        if (pagePath.isContainedIn(webSite.getVirtualSitesPath()) ||
            pagePath.isContainedIn(webSite.getPrivatePath())) {
          httpRes.sendError(HttpServletResponse.SC_FORBIDDEN,
              "You are not allowed to request this file");
        }
        
        siteMap = webSite.getSiteMap();
        isAdminPage = pagePath.isContainedIn(webSite.getAdminPath());
        httpReq.setCharacterEncoding(webSite.getConfiguration().getPreferredCharset());
        HttpSession session = httpReq.getSession(true);

        UserInfo userInfo = (UserInfo) httpReq.getSession().getAttribute("userInfo");
        isGuest = userInfo == null || userInfo.isGuest();

        // Deal with all pages
        if (FileTypes.isPage(pagePath)) {
          // Block direct requests of modules from non authenticated users
          if (isGuest && webSite.isInsideModules(pagePath) &&
              pagePath.getLastElement().equalsIgnoreCase(SiteMap.MODULE_INCLUDE_FILE)) {
            httpRes.sendError(HttpServletResponse.SC_FORBIDDEN,
                "You are not allowed to request this file");
            return;
          }

          WebUtils.updateLastModifiedTime(httpReq, webSite.getFile(pagePath));

          // HTTP 1.1
          httpRes.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
          // HTTP 1.0
          httpRes.setHeader("Pragma", "no-cache");
          // prevents caching at the proxy server
          httpRes.setDateHeader("Expires", -1);

          // Find a theme for this page
          Path themePath = null;
          String themeParameter = request.getParameter(THEME_FILE_ATTRIBUTE);

          if (themeParameter != null) {
            themePath = (Path) siteMap.getThemesMap().get(themeParameter);
          }

          if (themePath == null || !webSite.getFile(themePath).exists()) {
            themePath = webSite.getSiteInfo().getThemePath(pagePath);
          }

          if (themePath != null) { // there is a theme for this page
            request.setAttribute(THEME_PATH_ATTRIBUTE, themePath);

            // pages in /admin do not need a decorator to be specified:
            if (!isAdminPage || themeParameter != null) {
              request.setAttribute(THEME_FILE_ATTRIBUTE, "/" +
                  webSite.getServedPath(themePath) + "/" +
                  SiteMap.THEME_DECORATOR);
            }
          }

          /* Since a true page has been requested, disable hotlinking prevention
             for this session */
          if (webSite.getConfiguration().isPreventHotlinking() &&
              session.getAttribute(HOTLINKING_ALLOWED) == null && !isAdminPage) {
            session.setAttribute(HOTLINKING_ALLOWED, HOTLINKING_ALLOWED);
          }
        }

        if (webSite.getConfiguration().isPreventHotlinking() &&
            FileTypes.isPreventHotlinking(pagePath) &&
            session.getAttribute(HOTLINKING_ALLOWED) == null) {
          String agent = httpReq.getHeader("user-agent");

          if (agent == null || agent.toLowerCase().indexOf("java") < 0) {
            try {
              String domain = WebUtils.get2ndLevelDomain(httpReq);

              if (domain != null) {
                String referrer = httpReq.getHeader("referer");

                try {
                  referrer = new URL(referrer).getHost();
                } catch (Exception ex) {
                  referrer = null;
                }

                if (referrer == null || referrer.indexOf(domain) < 0) {
                  httpRes.sendRedirect(httpReq.getContextPath() + "/" +
                      webSite.getAdminPath() + "/hotlinking.jsp?path=" + pagePath);
                  return;
                }
              }
            } catch (MalformedURLException ex) {}
          }
        }

        pageInfo = siteMap.getPageInfo(pagePath);

        if (pageInfo != null) { // this page is contained in the site map
          pageCharset = pageInfo.getCharset();

          if (pageCharset != null) {
            httpRes.setHeader("Content-Type", "text/html; charset=" + pageCharset);
          }

          if (isGuest) {
            pageInfo.addHit();
          }

          // If it is a static page, try to get it from the cache
          if (isGuest && Utils.isNullOrEmpty(httpReq.getQueryString()) &&
              FileTypes.isLike(pagePath, "html")) {
            int cacheType = webSite.getConfiguration().getCacheType();

            // Let's see if the browser supports GZIP
            String ae = httpReq.getHeader("Accept-Encoding");
            boolean gzip = ae != null && ae.toLowerCase().indexOf("gzip") > -1;

            InputStream in = null;

            if (cacheType == Configuration.IN_MEMORY_CACHE) {
              byte[] pageBytes = siteMap.getCached(pageInfo.getPath());

              // a cached page too small is suspicious
              if (pageBytes != null && pageBytes.length > 256) {
                in = new ByteArrayInputStream(pageBytes);
              }
            } else if (cacheType == Configuration.ON_DISK_CACHE) {
              File cacheFile =
                  webSite.getRepositoryFile(pagePath, CACHE_FILE_NAME);
              // a cached page too small is suspicious
              if (cacheFile.exists() && cacheFile.length() > 256 &&
                  cacheFile.lastModified() > siteMap.getLastModified()) {
                // file exists and is not too old
                in = new FileInputStream(cacheFile);
              }
            }

            if (in != null) {
              ServletOutputStream sos = response.getOutputStream();

              if (gzip) {
                httpRes.setHeader("Content-Encoding", "gzip");
              } else {
                // uncompress the page on the fly for that spider or old browser
                in = new GZIPInputStream(in);
              }

              Utils.copyStream(in, sos, false);
              sos.flush();
              return;
            }
          } // cache control finished
        } else { // not a page in the site map
          // Let's try to apply the right charset to TinyMCE lang files
          if (isAdminPage && pagePath.getLastElement().endsWith(".js") &&
              userInfo != null && userInfo.canDo(UserInfo.CAN_EDIT_PAGES)) {
            Locale locale = Utils.getLocale(userInfo.getPreferredLocaleCode());
            ResourceBundle bundle =
                ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);
            String s = bundle.getString("TinyMCELangCode");

            if (!Utils.isNullOrEmpty(s) && pagePath.getLastElement().equals(s + ".js")) {
              s = bundle.getString("TinyMCELangCharset");

              if (!Utils.isNullOrEmpty(s)) {
                httpRes.setHeader("Content-Type", "text/javascript; charset=" + s);
              }
            }
          } // end of TinyMCE stuff
        }
      } // end of CMS stuff
      
      try {
        if (!(pageInfo == null || webSite.getCMSPath() == null ||
            WebUtils.isCacheBlocked(httpReq)) && isGuest) {
          int cacheType = webSite.getConfiguration().getCacheType();
          
          if (cacheType != Configuration.NO_CACHE) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream os = new GZIPOutputStream(baos);
            CacheResponseWrapper wrapper = new CacheResponseWrapper(httpRes, os,
                pageCharset == null ? 
                webSite.getConfiguration().getPreferredCharset() : pageCharset);
            chain.doFilter(httpReq, wrapper);
            wrapper.finishResponse();
            // os.flush();

            /* If WebUtils.setBlockCache has not been called while creating
               the page, it can be cached */
            if (cacheType == Configuration.IN_MEMORY_CACHE) {
              siteMap.cache(pageInfo.getPath(), baos.toByteArray());
            } else if (cacheType == Configuration.ON_DISK_CACHE) {
              File cacheFile =
                  webSite.getRepositoryFile(pagePath, CACHE_FILE_NAME);
              cacheFile.getParentFile().mkdirs();
              Utils.writeFully(cacheFile, baos.toByteArray());
            }
            
            return;
          }
        }

        chain.doFilter(httpReq, httpRes);
        webSite.updateSiteMap(false); // better here than nowhere :)
      } catch (Exception ex) {
        if (isAdminPage) {
          webSite.getConfiguration().setUseAdminTheme(true);
        }
        
        Path wPath = webSite.findCurrentWelcome(pagePath);

        if (wPath != null) {
          String q = httpReq.getQueryString();

          if (Utils.isNullOrEmpty(q)) {
            httpRes.sendRedirect(httpReq.getContextPath() + "/" + wPath);
          } else {
            httpRes.sendRedirect(httpReq.getContextPath() + "/" + wPath + '?' + q);
          }

          return;
        }
        
        sc.log("--------\n\nIMPORTANT: an exception has been caught while serving " +
            httpReq.getRequestURI(), ex);
        httpRes.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
      
      return;
    }

    // should never be reached
    chain.doFilter(request, response);
  }
  
  public static WebSite getRootSite(ServletContext sc, boolean alwaysCreate) {
    WebSite rootSite = (WebSite) sc.getAttribute("MeshCMS-Root-Site");

    if (rootSite == null || alwaysCreate) {
      File rootFile = new File(sc.getRealPath("/"));
      Path cmsPath = new CMSDirectoryFinder(rootFile).getCMSPath();
      boolean multisite = false;
      File sitesDir = new File(rootFile, cmsPath + "/sites");

      if (sitesDir.isDirectory()) {
        File[] dirs = sitesDir.listFiles();

        for (int i = 0; i < dirs.length; i++) {
          if (dirs[i].isDirectory()) {
            multisite = true;
          }
        }
      }

      rootSite = multisite ?
          MainWebSite.create(sc, WebUtils.getWelcomeFiles(sc), rootFile,
              Path.ROOT, cmsPath) :
          WebSite.create(sc, WebUtils.getWelcomeFiles(sc), rootFile,
              Path.ROOT, cmsPath);
      sc.setAttribute("MeshCMS-Root-Site", rootSite);
    }
    
    return rootSite;
  }
}