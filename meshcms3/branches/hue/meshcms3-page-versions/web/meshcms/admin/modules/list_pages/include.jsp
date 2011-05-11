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
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%--
  Advanced parameters for this module:
  - css = (name of a css class)
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
  Path argPath = md.getModuleArgumentDirectoryPath(webSite, false);
  String style = md.getAdvancedParam("css", md.getStyle());
  
  if (argPath == null) {
    argPath = md.getPagePath();
  }

  List list = webSite.getSiteMap(userInfo).getPagesInDirectory(argPath, false);

  if (list != null && list.size() > 0) {
    PageInfo[] pages = (PageInfo[]) list.toArray(new PageInfo[list.size()]);
    %><ul>
     <li><%= Utils.generateList(webSite.getLinkList(pages, cp, null, style), "</li><li>") %></li>
    </ul><%
  }
%>
