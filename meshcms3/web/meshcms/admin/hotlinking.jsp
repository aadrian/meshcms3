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

<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%
  response.setHeader("Content-Type", "text/html; charset=" + webSite.getConfiguration().getPreferredCharset());
  Path path = new Path(request.getParameter("path"));
  String cp = request.getContextPath();
  boolean isImage = FileTypes.isLike(path, "jpg");
%>

<html>
<head>
  <title>Hotlinking</title>
</head>

<body style="font: 12px monospace;">

<p align="center" style="border: 1px solid #cccccc; padding: 25px;">
  <% if (isImage) {
    %><img src="<%= cp + '/' + path %>" alt=""><%
  } else {
    %><img src="<%= cp + '/' + webSite.getAdminPath() + "/filemanager/images/" +
        FileTypes.getIconFile(path) %>" alt="">
    Get <a href="<%= cp + '/' + path %>"><%= path.getLastElement() %></a><%
  } %>
</p>

<p align="center">
 <%= isImage ? "Image" : "File" %> from
 <a href="<%= cp + webSite.getLink(Path.ROOT) %>"><%= request.getServerName() %></a>
</p>

</body>
</html>
