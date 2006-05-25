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

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%--
  Advanced parameters for this module: none
--%>

<%
  ModuleDescriptor md = (ModuleDescriptor)
      request.getAttribute(request.getParameter("modulecode"));
  String cp = request.getContextPath();
  Path argPath = md.getModuleArgumentDirectoryPath(webSite, true);

  if (argPath != null) {
    SiteInfo siteInfo = webSite.getSiteInfo();
    int lastLevel = argPath.getElementCount() - 1;
    List pagesList = webSite.getSiteMap().getPagesList(argPath);
    
    if (pagesList != null) {
      Iterator iter = pagesList.iterator();

      while (iter.hasNext()) {
        PageInfo pageInfo = (PageInfo) iter.next();
        int level = pageInfo.getLevel();

        for (int i = lastLevel; i < level; i++) {
          %><ul><%
        }

        for (int i = level; i < lastLevel; i++) {
          %></ul><%
        }
  %>
    <li><a href="<%= cp + webSite.getLink(pageInfo) %>"><%=
      siteInfo.getPageTitle(pageInfo) %></a></li>
  <%
        lastLevel = level;
      }

      for (int i = argPath.getElementCount() - 1; i < lastLevel; i++) {
        %></ul><%
      }
    }
  }
%>
