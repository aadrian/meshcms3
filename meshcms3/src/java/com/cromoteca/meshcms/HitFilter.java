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
import java.net.*;
import java.util.*;
import java.util.zip.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.cromoteca.util.*;

/**
 * Filter used to handle the requests for web pages.
 */
public final class HitFilter implements Filter, Finals {
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
    HttpServletRequest httpReq = (HttpServletRequest) request;
    HttpServletResponse httpRes = (HttpServletResponse) response;

    if (filterConfig != null && (request instanceof HttpServletRequest)) {
      ServletContext sc = filterConfig.getServletContext();
      WebApp webApp = (WebApp) sc.getAttribute("webApp");
      
      if (webApp == null) {
        webApp = new WebApp(sc);
        sc.setAttribute("webApp", webApp);
      }
      
      Path pagePath = new Path(httpReq.getServletPath());
      httpReq.setCharacterEncoding(webApp.getConfiguration().getPreferredCharset());
      HttpSession session = httpReq.getSession(true);

      UserInfo userInfo = (UserInfo)
          httpReq.getSession().getAttribute("userInfo");
      boolean isGuest = userInfo == null || userInfo.isGuest();

      // Deal with all pages
      if (webApp.getFileTypes().isPage(pagePath)) {
        // Block direct requests of modules from non authenticated users
        if (isGuest && pagePath.isContainedIn(webApp.getModulesPath()) &&
            pagePath.getLastElement().equalsIgnoreCase(MODULE_INCLUDE_FILE)) {
          httpRes.sendError(HttpServletResponse.SC_FORBIDDEN,
            "You are not allowed to request this file");
          return;
        }
        
        WebUtils.updateLastModifiedTime(httpReq, webApp.getFile(pagePath));
        
        // HTTP 1.1
        httpRes.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // HTTP 1.0
        httpRes.setHeader("Pragma", "no-cache");
        // prevents caching at the proxy server
        httpRes.setDateHeader("Expires", -1);

        // Find a theme for this page
        Path themePath = null;
        String themeParameter = request.getParameter(THEME_FILE_ATTRIBUTE);

        if (!Utils.isNullOrEmpty(themeParameter)) {
          themePath = webApp.getThemesPath().add(themeParameter);
        }
        
        if (themePath == null || !webApp.getFile(themePath).exists()) {
          themePath = webApp.getSiteInfo().getThemePath(pagePath);
        }

        if (themePath != null) { // there is a theme for this page
          request.setAttribute(THEME_PATH_ATTRIBUTE, themePath);
          
          // pages in /admin do not need a decorator to be specified:
          if (!pagePath.isContainedIn(webApp.getAdminPath())) {
            request.setAttribute(THEME_FILE_ATTRIBUTE, "/" +
                themePath + "/" + THEME_DECORATOR);
          }
        }

        // Since a true page has been requested, disable hotlinking prevention
        // for this session
        if (webApp.getConfiguration().isPreventHotlinking() &&
            session.getAttribute(HOTLINKING_ALLOWED) == null &&
            !pagePath.isContainedIn(webApp.getAdminPath())) {
          session.setAttribute(HOTLINKING_ALLOWED, HOTLINKING_ALLOWED);
        }
      } 
      
      if (webApp.getConfiguration().isPreventHotlinking() &&
          webApp.getFileTypes().isPreventHotlinking(pagePath) &&
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
                    webApp.getAdminPath() + "/hotlinking.jsp?path=" + pagePath);
                return;
              }
            }
          } catch (MalformedURLException ex) {}
        }
      }

      SiteMap siteMap = webApp.getSiteMap();
      PageInfo pageInfo = siteMap.getPageInfo(pagePath);

      if (pageInfo != null) { // this page is contained in the site map
        webApp.updateSiteMap(false); // better here than nowhere :)
        String pageCharset = pageInfo.getCharset();

        if (pageCharset != null) {
          httpRes.setHeader("Content-Type", "text/html; charset=" + pageCharset);
        }

        if (isGuest) {
          pageInfo.addHit();
        }
		
        // If it is a static page, try to get it from the cache
        if (isGuest && Utils.isNullOrEmpty(httpReq.getQueryString()) &&
            webApp.getFileTypes().isLike(pagePath, "html")) {
          int cacheType = webApp.getConfiguration().getCacheType();

          // Let's see if the browser supports GZIP
          String ae = httpReq.getHeader("Accept-Encoding");
          boolean gzip = ae != null && ae.toLowerCase().indexOf("gzip") > -1;

          File cacheFile = webApp.getRepositoryFile(pagePath, CACHE_FILE_NAME);
          InputStream in = null;

          if (cacheType == IN_MEMORY_CACHE) {
            byte[] pageBytes = siteMap.getCached(pageInfo.getPath());

            // a cached page too small is suspicious
            if (pageBytes != null && pageBytes.length > 256) {
              in = new ByteArrayInputStream(pageBytes);
            }
          } else if (cacheType == ON_DISK_CACHE) {
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
          
          if (cacheType != NO_CACHE) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStream os = new GZIPOutputStream(baos);
            CacheResponseWrapper wrapper = new CacheResponseWrapper(httpRes, os,
                pageCharset == null ? 
                webApp.getConfiguration().getPreferredCharset() : pageCharset);
            chain.doFilter(httpReq, wrapper);
            wrapper.finishResponse();
            os.flush();
            
            // If WebUtils.setBlockCache has not been called while creating the
            // page, it can be cached
            if (!WebUtils.isCacheBlocked(httpReq)) {
              if (cacheType == IN_MEMORY_CACHE) {
                siteMap.cache(pageInfo.getPath(), baos.toByteArray());
              } else if (cacheType == ON_DISK_CACHE) {
                cacheFile.getParentFile().mkdirs();
                Utils.writeFully(cacheFile, baos.toByteArray());
              }
            }
            
            return;
          }
        } // cache control finished
      } else { // not a page in the site map
        // Let's try to apply the right charset to TinyMCE lang files
        if (pagePath.isContainedIn(webApp.getAdminPath()) && 
            pagePath.getLastElement().endsWith(".js")) {
          if (userInfo != null && userInfo.canDo(UserInfo.CAN_EDIT_PAGES)) {
            Locale locale = Utils.getLocale(userInfo.getPreferredLocaleCode());
            ResourceBundle bundle =
                ResourceBundle.getBundle("com/cromoteca/meshcms/Locales", locale);
            String s = bundle.getString("TinyMCELangCode");

            if (!Utils.isNullOrEmpty(s) && pagePath.getLastElement().equals(s + ".js")) {
              s = bundle.getString("TinyMCELangCharset");

              if (!Utils.isNullOrEmpty(s)) {
                httpRes.setHeader("Content-Type", "text/html; charset=" + s);
              }
            }
          }
        }
      }
    }

    chain.doFilter(httpReq, httpRes);
  }
}
