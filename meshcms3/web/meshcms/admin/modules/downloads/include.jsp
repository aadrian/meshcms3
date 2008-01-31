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
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%--
  Advanced parameters for this module:
  - css = (name of a css class)
  - date = none (default) | normal | full
  - force = true (default) | false
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

  String cp = request.getContextPath();
  File[] files = md.getModuleFiles(webSite, false);

  if (files != null && files.length > 0) {
    boolean force = Utils.isTrue(md.getAdvancedParam("force", "true"));
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle =
        ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);
    Arrays.sort(files, new FileNameComparator());
    DateFormat df = md.getDateFormat(locale, "date");
%>

<table<%= md.getFullCSSAttribute("css") %> align="center" border="0" cellspacing="10" cellpadding="0">
<%
    for (int i = 0; i < files.length; i++) {
      if (!files[i].isDirectory()) {
        WebUtils.updateLastModifiedTime(request, files[i]);
        String link = force ?
            cp + "/servlet/org.meshcms.core.DownloadServlet/" + webSite.getPath(files[i]) :
            cp + '/' + webSite.getPath(files[i]);
%>
 <tr valign="top">
  <td><img src="<%= cp + '/' + webSite.getAdminPath() %>/filemanager/images/<%= FileTypes.getIconFile(files[i].getName()) %>" border="0" alt="<%= FileTypes.getDescription(files[i].getName()) %>" /></td>
  <td><a href="<%= link %>"><%= files[i].getName() %></a></td>
  <td align="right"><%= WebUtils.formatFileLength(files[i].length(), locale, bundle) %></td>
  <% if (df != null) { %><td><%= df.format(new Date(files[i].lastModified())) %></td><% } %>
 </tr>
<%
      }
    }
%>
</table>

<%
  }
%>
