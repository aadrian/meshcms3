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
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%--
  Advanced parameters for this module: none
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

  String mp = request.getContextPath() + '/' + md.getModulePath();
  String fName = mp + "/flowplayer.jsp?path=" + md.getModuleArgumentDirectoryPath(webSite, true) + "&modulepath=" + md.getModulePath();
%>

<div align="center">
  <object type="application/x-shockwave-flash" data="<%= mp %>/flowplayer.swf" width="420" height="380" id="FlowPlayer">
    <param name="allowScriptAccess" value="sameDomain" />
    <param name="movie" value="<%= mp %>/flowplayer.swf" />
    <param name="quality" value="high" />
    <param name="scale" value="noScale" />
    <param name="wmode" value="transparent" />
    <param name="flashvars" value="configFileName=<%= URLEncoder.encode(fName, webSite.getConfiguration().getPreferredCharset()) %>" />
  </object>
</div>
