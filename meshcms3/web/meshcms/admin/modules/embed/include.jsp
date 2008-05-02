<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2008 Luciano Vernaschi

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

<%--
  Advanced parameters for this module:
  - delay = true | false (default)
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
  
  String arg = md.getArgument();
  Path argPath = new Path(arg);
  
  if (webSite.getFile(argPath).isFile()) {
    arg = Utils.readFully(webSite.getFile(argPath));
  }
  
  arg = Utils.decodeHTML(arg);
  String cp = request.getContextPath();
  boolean delay = Utils.isTrue(md.getAdvancedParam("delay", "false"));
  
  if (delay) {
%>
<script type="text/javascript">
  if (!window.jQuery) {
    document.write("<scr" + "ipt type='text/javascript' src='<%= cp + '/' + webSite.getAdminScriptsPath() %>/jquery/jquery.pack.js'></scr" + "ipt>");
  }
</script>
  
<script type="text/javascript">
  $(function() {
    var tm = window.setTimeout(function() {
      $("#embed_<%= moduleCode %>").empty().append("<%= arg.replaceAll("\"", "\\\"") %>");
    }, 1000);
  });
</script>

<div id="embed_<%= moduleCode %>">
  <img src="<%= cp + '/' + md.getModulePath() %>/ajax-loader.gif"
   alt="Loading..." />
</div>
<%
  } else {
%>
<div><%= arg %></div>
<%
  }
%>
