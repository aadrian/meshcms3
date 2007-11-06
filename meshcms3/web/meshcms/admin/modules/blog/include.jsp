<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2007 Luciano Vernaschi

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 You can contact the author at http://www.cromoteca.com
 and at info@cromoteca.com
--%>

<%@ page import="java.io.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="com.opensymphony.module.sitemesh.*" %>
<%@ page import="com.opensymphony.module.sitemesh.parser.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%--
  Advanced parameters for this module:
  - css = (name of a css class)
  - date = none (default) | normal | full
  - entries = number of entries per page (default 5)
  - maxchars = maximum length of the excerpt for each article (default 2000)
--%>

<%
  Locale locale = WebUtils.getPageLocale(pageContext);
  ResourceBundle pageBundle = ResourceBundle.getBundle
      ("org/meshcms/webui/Locales", locale);

  String moduleCode = request.getParameter("modulecode");
  ModuleDescriptor md = null;

  if (moduleCode != null) {
    md = (ModuleDescriptor) request.getAttribute(moduleCode);
  }

  if (md == null) {
    if (!response.isCommitted()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    return;
  }

  Path argPath = md.getModuleArgumentDirectoryPath(webSite, true);

  if (argPath != null) {
    SiteMap siteMap = webSite.getSiteMap();
    ArrayList pagesList = new ArrayList(siteMap.getPagesList(argPath));
    Iterator iter = pagesList.iterator();
    Path pagePathInMenu = siteMap.getPathInMenu(md.getPagePath());
    
    while (iter.hasNext()) {
      if (((PageInfo) iter.next()).getPath().equals(pagePathInMenu)) {
        iter.remove();
      }
    }
    
    Collections.sort(pagesList, new Comparator() {
      public int compare(Object o1, Object o2) {
        try {
          PageInfo p1 = (PageInfo) o1;
          PageInfo p2 = (PageInfo) o2;
          
          return p1.getLastModified() < p2.getLastModified() ? 1 :
            (p1.getLastModified() > p2.getLastModified() ? -1 : 0);
        } catch (ClassCastException ex) {
          return 0;
        }
      }
    });

    Path dirPath = webSite.getDirectory(pagePathInMenu);
    DateFormat df = md.getDateFormat(locale, "date");
    int maxChars = Utils.parseInt(md.getAdvancedParam("maxchars", ""), 2000);
    int entries = Utils.parseInt(md.getAdvancedParam("entries", ""), 5);
    int firstEntry = Utils.parseInt(request.getParameter("firstentry"), 0);
%>

<div<%= md.getFullCSSAttribute("css") %>>
<%
    for (int i = firstEntry; i < firstEntry + entries && i < pagesList.size(); i++) {
      PageInfo pi = (PageInfo) pagesList.get(i);
      WebUtils.updateLastModifiedTime(request, pi.getLastModified());
      HTMLPageParser fpp = new HTMLPageParser();

      Reader reader = new InputStreamReader
          (new FileInputStream(webSite.getFile(siteMap.getServedPath(pi.getPath()))),
          Utils.SYSTEM_CHARSET);
      HTMLPage pg = (HTMLPage) fpp.parse(Utils.readAllChars(reader));
      reader.close();
      String title = pg.getTitle();
      String link = pi.getPath().getRelativeTo(dirPath).toString();
      String body = WebUtils.createExcerpt(webSite, pg.getBody(), maxChars,
          request.getContextPath(), pi.getPath(), md.getPagePath());
%>
 <div class="includeitem">
  <h3 class="includetitle">
    <a href="<%= link %>"><%= Utils.isNullOrEmpty(title) ? "&nbsp;" : title %></a>
  </h3>
<%
          if (df != null) {
%>
  <h4 class="includedate">
    (<%= df.format(new Date(pi.getLastModified())) %>)
  </h4>
<%
          }
%>
  <div class="includetext">
    <%= body %>
  </div>
  <p class="includereadmore">
    <a href="<%= link %>"><%= pageBundle.getString("includeReadFull") %></a>
  </p>
<%
      String[] tags = pi.getKeywords();

      if (tags != null && tags.length > 0) {
%>
  <p class="includetags">
    <%= pageBundle.getString("includeTags") %>
    <strong><%= Utils.generateList(tags, "</strong>, <strong>") %></strong>
  </p>
<%
      }
%>
 </div>
<%
    }
    
    boolean newer = firstEntry > 0;
    boolean older = firstEntry + entries < pagesList.size();
    
    if (newer || older) {
      String baseURL = request.getContextPath() + md.getPagePath().getAsLink();
      
      %><p class="includenavigation"><%

      if (newer) {
        String qs = firstEntry - entries > 0 ? "?firstentry=" + (firstEntry - entries) : "";
        %> <a href="<%= baseURL + qs %>"><%= pageBundle.getString("includeNewer") %></a> <%
      }
      
      if (newer && older) {
        %> | <%
      }

      if (older) {
        %> <a href="<%= baseURL + "?firstentry=" + (firstEntry + entries) %>"><%= pageBundle.getString("includeOlder") %></a> <%
      }
      
      %></p><%
    }
%>
</div>

<%
  }
%>
