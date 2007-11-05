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
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="com.opensymphony.module.sitemesh.*" %>
<%@ page import="com.opensymphony.module.sitemesh.parser.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%--
  Advanced parameters for this module:
  - css = (name of a css class)
  - date = none (default) | normal | full
  - maxchars = maximum length of the excerpt for each article (default 500)
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
  Path dirPath = webSite.getDirectory(md.getPagePath());
  File[] files = md.getModuleFiles(webSite, true);
  int maxChars = Utils.parseInt(md.getAdvancedParam("maxchars", ""), 500);

  if (files != null && files.length > 0) {
    Arrays.sort(files, new FileDateComparator());
    DateFormat df = md.getDateFormat(locale, "date");
%>

<div<%= md.getFullCSSAttribute("css") %>>
<%
    for (int i = 0; i < files.length; i++) {
      if (FileTypes.isPage(files[i].getName())) {
        // Skip itself.
        if (dirPath.add(files[i].getName()).equals(webSite.getSiteMap().getServedPath(md.getPagePath()))) {
          continue;
        }

        WebUtils.updateLastModifiedTime(request, files[i]);
        HTMLPageParser fpp = new HTMLPageParser();

        try {
          Reader reader = new InputStreamReader(new FileInputStream(files[i]),
              Utils.SYSTEM_CHARSET);
          HTMLPage pg = (HTMLPage) fpp.parse(Utils.readAllChars(reader));

          String title = pg.getTitle();
          String link = argPath.add(files[i].getName()).getRelativeTo(dirPath).toString();
%>
 <div class="includeitem">
  <h3 class="includetitle">
    <a href="<%= link %>"><%= Utils.isNullOrEmpty(title) ? "&nbsp;" : title %></a>
  </h3>
<%
          if (df != null) {
%>
  <h4 class="includedate">
    (<%= df.format(new Date(files[i].lastModified())) %>)
  </h4>
<%
          }
%>
  <%= WebUtils.createExcerpt(webSite, pg.getBody(), maxChars) %>
  <p class="includereadmore">
    <a href="<%= link %>"><%= pageBundle.getString("readMore") %></a>
  </p>
 </div>
<%
          reader.close();
        } catch (Exception ex) {ex.printStackTrace();}
      }
    }
%>
</div>

<%
  }
%>
