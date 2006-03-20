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
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<%@ page import="com.opensymphony.module.sitemesh.parser.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />

<%
  String style = request.getParameter("style");

  if (Utils.isNullOrEmpty(style)) {
    style = "";
  } else {
    style = " class=\"" + style + "\"";
  }
  
  File[] files = WebUtils.getModuleFiles(webApp, request, false);

  if (files != null && files.length > 0) {
    Arrays.sort(files, new FileDateComparator());
    DateFormat df = WebUtils.getModuleDateFormat(pageContext);
%>

<table<%= style %> align="center" border="0" cellspacing="2" cellpadding="0">
<%
    for (int i = 0; i < files.length; i++) {
      if (webApp.getFileTypes().isPage(files[i])) {
        FastPageParser fpp = new FastPageParser();

        try {
          Reader reader = new BufferedReader(new FileReader(files[i]));
          FastPage pg = (FastPage) fpp.parse(reader);
          String title = pg.getTitle();
%>
  <tr>
    <th><%= Utils.isNullOrEmpty(title) ? "&nbsp;" : title %></th>
  </tr>
<%
          if (df != null) {
%>
  <tr>
    <td align="right">(<%= df.format(new Date(files[i].lastModified())) %>)</td>
  </tr>
<%
          }
%>
  <tr>
    <td><%= pg.getBody() %></td>
  </tr>
<%
          reader.close();
        } catch (Exception ex) {}
      }
    }
%>
</table>

<%
  }
%>
