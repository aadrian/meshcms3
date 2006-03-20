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

<%@ page import="java.util.*" %>
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />

<%
  String cp = request.getContextPath();
  Path argPath = WebUtils.getModuleArgumentDirectoryPath(webApp, request, false);
  
  if (argPath == null) {
    argPath = new Path(request.getParameter("pagepath"));
  }

  List list = webApp.getSiteMap().getPagesInDirectory(argPath, false);

  if (list != null && list.size() > 0) {
    PageInfo[] pages = (PageInfo[]) list.toArray(new PageInfo[list.size()]);
    %><ul><li><%= Utils.generateList(webApp.getLinkList(pages, cp, null, null), "</li><li>") %></li></ul><%
  }
%>
