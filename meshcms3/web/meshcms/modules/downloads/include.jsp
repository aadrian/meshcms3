<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2006 Luciano Vernaschi

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
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />

<%
  String cp = request.getContextPath();
  String style = request.getParameter("style");

  if (Utils.isNullOrEmpty(style)) {
    style = "";
  } else {
    style = " class=\"" + style + "\"";
  }
  
  File[] files = WebUtils.getModuleFiles(webApp, request, false);

  if (files != null && files.length > 0) {
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle =
        ResourceBundle.getBundle("com/cromoteca/meshcms/Locales", locale);
    Arrays.sort(files, new FileNameComparator());
    DateFormat df = WebUtils.getModuleDateFormat(pageContext);
%>

<table<%= style %> align="center" border="0" cellspacing="10" cellpadding="0">
<%
    for (int i = 0; i < files.length; i++) {
      if (!files[i].isDirectory()) {
        WebUtils.updateLastModifiedTime(request, files[i]);
%>
 <tr valign="top">
  <td><img src="<%= cp + '/' + webApp.getAdminPath() %>/filemanager/images/<%= webApp.getFileTypes().getIconFile(files[i]) %>" border="0"></td>
  <td><a href="<%= cp + '/' + webApp.getPath(files[i]) %>"><%= files[i].getName() %></a></td>
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
