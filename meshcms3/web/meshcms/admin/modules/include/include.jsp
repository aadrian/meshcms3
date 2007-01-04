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
--%>

<%
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

  File[] files = md.getModuleFiles(webSite, false);

  if (files != null && files.length > 0) {
    Arrays.sort(files, new FileDateComparator());
    DateFormat df = md.getDateFormat(WebUtils.getPageLocale(pageContext), "date");
%>

<div<%= md.getFullCSSAttribute("css") %>>
<%
    for (int i = 0; i < files.length; i++) {
      if (FileTypes.isPage(files[i].getName())) {
        WebUtils.updateLastModifiedTime(request, files[i]);
        HTMLPageParser fpp = new HTMLPageParser();

        try {
          Reader reader = new BufferedReader(new FileReader(files[i]));
          HTMLPage pg = (HTMLPage) fpp.parse(Utils.readAllChars(reader));
          String title = pg.getTitle();
%>
 <div class="includeitem">
  <div class="includetitle">
    <%= Utils.isNullOrEmpty(title) ? "&nbsp;" : title %>
  </div>
<%
          if (df != null) {
%>
  <div class="includedate">
    (<%= df.format(new Date(files[i].lastModified())) %>)
  </div>
<%
          }
%>
  <div class="includetext">
    <%= pg.getBody() %>
  </div>
 </div>
<%
          reader.close();
        } catch (Exception ex) {}
      }
    }
%>
</div>

<%
  }
%>
