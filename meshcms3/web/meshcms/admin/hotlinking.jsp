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

<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />

<%
  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
  Path path = new Path(request.getParameter("path"));
  String cp = request.getContextPath();
  boolean isImage = webApp.getFileTypes().isLike(path, "jpg");
%>

<html>
<head>
  <title>Hotlinking</title>
</head>

<body style="font: 12px monospace;">

<p align="center" style="border: 1px solid #cccccc; padding: 25px;">
  <% if (isImage) {
    %><img src="<%= cp + '/' + path %>"><%
  } else {
    %><img src="<%= cp + '/' + webApp.getAdminPath() + "/filemanager/images/" +
        webApp.getFileTypes().getIconFile(path) %>">
    Get <a href="<%= cp + '/' + path %>"><%= path.getLastElement() %></a><%
  } %>
</p>

<p align="center">
 <%= isImage ? "Image" : "File" %> from
 <a href="<%= cp %>/"><%= request.getServerName() %></a>
</p>

</body>
</html>
