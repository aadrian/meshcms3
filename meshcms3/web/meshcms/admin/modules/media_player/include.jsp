<%--
 Copyright 2004-2008 Luciano Vernaschi
 
 This file is part of MeshCMS.
 
 MeshCMS is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 MeshCMS is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@ page import="java.io.*" %>
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

  File[] files = md.getModuleFiles(webSite, true);

  if (files != null && files.length > 0) {
    Arrays.sort(files, new FileNameComparator());
    Path pagePath = webSite.getRequestedPath(request);
    Path modulePath = webSite.getLink(md.getModulePath(), pagePath);
%>

<script type="text/javascript" src="<%= webSite.getLink(webSite.getAdminScriptsPath().add("jquery/jquery-1.2.6.pack.js"), pagePath) %>"></script>
<script type="text/javascript" src="<%= modulePath.add("flashembed-0.31.pack.js") %>"></script>
<script type="text/javascript" src="<%= modulePath.add("flow.playlist.js") %>"></script>
<script type="text/javascript">
  $(function() {
    $("#playlist").playlist("<%= modulePath.add("FlowPlayerLight.swf") %>", {
      initialScale: 'orig'
    }, {
      player: '#player',
      loop: false
    });
  });
</script>

<div class="meshcmsMediaPlayer" id="player">
  <img src="<%= modulePath.add("blank_player.png") %>" alt="MeshCMS Media Player"/>
</div>

<div id="playlist" class="meshcmsPlayList">
<%
    ResizedThumbnail thumbMaker = new ResizedThumbnail();
    thumbMaker.setMode(ResizedThumbnail.MODE_CROP);
    thumbMaker.setWidth(48);
    thumbMaker.setHeight(48);
    thumbMaker.setHighQuality(webSite.getConfiguration().isHighQualityThumbnails());
    String thumbName = thumbMaker.getSuggestedFileName();

    for (int i = 0; i < files.length; i++) {
      Path icon = null;
      Path filePath = webSite.getPath(files[i]);
      Path link = webSite.getPath(files[i]);
      String ext = Utils.getExtension(files[i], false).toLowerCase();
      
      if (ext.equals("jpg")) {
        icon = thumbMaker.checkAndCreate(webSite, filePath, thumbName);
        link = webSite.getLink(link, pagePath);
      } else if (ext.equals("mp3")) {
        icon = md.getModulePath().add("tango-audio.png");
        link = webSite.getLink(link, pagePath);
      } else if (ext.equals("flv")) {
        icon = md.getModulePath().add("tango-video.png");
        // unfortunately, videos must be relative to the SWF
        link = webSite.getLink(link, md.getModulePath());
      }
      
      if (icon != null) {
        WebUtils.updateLastModifiedTime(request, files[i]);
        String name = Utils.beautify(files[i].getName(), true);
%>
  <a href="<%= link %>" title="<%= name %>">
    <img src="<%= webSite.getLink(icon, pagePath) %>" alt="<%= name %>" width="48" height="48"/>
    <%= name %>
  </a>
<%
      }
    }
%>
</div>
<%
  }
%>