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
  String data = mp + "/xspf_player.swf?playlist_url=" +
      URLEncoder.encode(mp + "/playlist.jsp?path=" + md.getModuleArgumentDirectoryPath(webSite, true) +
      "&modulepath=" + md.getModulePath(), webSite.getConfiguration().getPreferredCharset()) +
      "&autoload=false&info_button_text=download";
%>

<%--
<div align="center">
  <object type="application/x-shockwave-flash" width="400" height="170" data="<%= data %>">
    <param name="movie" value="<%= data %>" />
  </object>
</div>
--%>

<script type="text/javascript" src="<%= request.getContextPath() %>/<%= webSite.getAdminPath() %>/scripts/swfobject/swfobject.js"></script>

<div id="flashcontent" align="center">
  This module requires a Flash Player.
</div>

<script type="text/javascript">
   var so = new SWFObject("<%= data %>", "xspfplayer", "400", "170", "7", "#FFFFFF");
   so.addParam("movie", "<%= data %>");
   so.write("flashcontent");
</script>
