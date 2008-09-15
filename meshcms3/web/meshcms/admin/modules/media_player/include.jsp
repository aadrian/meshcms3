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
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%--
  Advanced parameters for this module:
  - layout = vertical (default) | horizontal
  - order = "name" (default) | "date" (same as date_fwd) | "date_fwd" | "date_rev" | "random"
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
    List fileList = new ArrayList();
    
    for (int i = 0; i < files.length; i++) {
      String ext = Utils.getExtension(files[i], false).toLowerCase();
      
      if ("jpg".equals(ext)) {
        File testFLV = Utils.replaceExtension(files[i], "flv");
        File testMP3 = Utils.replaceExtension(files[i], "mp3");
        
        if (!(testFLV.exists() || testMP3.exists())) {
          fileList.add(files[i]);
        }
      } else if ("mp3".equals(ext) || "flv".equals(ext)) {
        fileList.add(files[i]);
      }
    }
    
    files = (File[]) fileList.toArray(new File[fileList.size()]);
    String sort = md.getAdvancedParam("order", "name");

    if("random".equalsIgnoreCase(sort)) {
      Collections.shuffle(Arrays.asList(files));
    } else {
      Comparator cmp;

     if ("date".equalsIgnoreCase(sort) || "date_fwd".equalsIgnoreCase(sort)) {
        cmp = new FileDateComparator(true);
      } else if ("date_rev".equalsIgnoreCase(sort)) {
        cmp = new FileDateComparator(false);
      } else {
        cmp = new FileNameComparator();
      }
      
      Arrays.sort(files, cmp);
    }
  }

  if (files.length > 0) {
    Path pagePath = webSite.getRequestedPath(request);
    Path modulePath = webSite.getLink(md.getModulePath(), pagePath);
    String layout = md.getAdvancedParam("layout", "vertical").toLowerCase();
    // boolean horizontal = "horizontal".equalsIgnoreCase(layout);
    String id = "player_" + moduleCode;
    String plId = "playlist_" + moduleCode;

    ResizedThumbnail thumbMaker = new ResizedThumbnail();
    thumbMaker.setMode(ResizedThumbnail.MODE_CROP);
    thumbMaker.setWidth(48);
    thumbMaker.setHeight(48);
    thumbMaker.setHighQuality(webSite.getConfiguration().isHighQualityThumbnails());
    String thumbName = thumbMaker.getSuggestedFileName();
    
    ResizedThumbnail imgMaker = new ResizedThumbnail();
    imgMaker.setWidth(400);
    imgMaker.setHeight(272);
    imgMaker.setHighQuality(webSite.getConfiguration().isHighQualityThumbnails());
    String imgName = imgMaker.getSuggestedFileName();
%>

  <script type="text/javascript" src="<%= webSite.getLink(webSite.getAdminScriptsPath().add("jquery/jquery-1.2.6.pack.js"), pagePath) %>"></script>
  <script type="text/javascript" src="<%= modulePath.add("flashembed-0.31.pack.js") %>"></script>

<% if (files.length == 1) { %>
  <script type="text/javascript">
    $(function() {
      $("#<%= id %>").flashembed({src:"<%= modulePath.add("FlowPlayerLight.swf") %>"}, {config: {
        videoFile: "<%= webSite.getLink(webSite.getPath(files[0]), md.getModulePath()) %>",
        autoPlay: false,
        initialScale: "orig",
        menuItems: [true, true, true, true, true, false, false]
      }});
    });
  </script>
<% } else { %>
  <script type="text/javascript" src="<%= modulePath.add("flow.playlist.js") %>"></script>
  <script type="text/javascript">
    $(function() {
      $("#<%= plId %>").playlist("<%= modulePath.add("FlowPlayerLight.swf") %>", {
        initialScale: "orig",
        menuItems: [true, true, true, true, true, false, false]
      }, {
        player: "#<%= id %>",
        loop: false
      });
    });
  </script>
<% } %>

<div class="meshcmsMediaPlayer_<%= layout %>">
  <div class="meshcmsMediaPlayerScreen" id="<%= id %>">
    <img src="<%= modulePath.add("blank_player.png") %>" alt="MeshCMS Media Player"/>
  </div>

<% if (files.length > 1) { %>
  <div id="<%= plId %>" class="meshcmsPlayList">
<%
    for (int i = 0; i < files.length; i++) {
      Path icon = null;
      Path filePath = webSite.getPath(files[i]);
      Path link = webSite.getPath(files[i]);
      String ext = Utils.getExtension(files[i], false).toLowerCase();
      
      if (ext.equals("jpg")) {
        icon = thumbMaker.checkAndCreate(webSite, filePath, thumbName);
        link = webSite.getLink(imgMaker.checkAndCreate(webSite, filePath, imgName), pagePath);
      } else if (ext.equals("mp3")) {
        Path iconPath = Utils.replaceExtension(filePath, "jpg");
        
        if (webSite.getFile(iconPath).exists()) {
          icon = thumbMaker.checkAndCreate(webSite, iconPath, thumbName);
        } else {
          icon = md.getModulePath().add("tango-audio.png");
        }

        link = webSite.getLink(link, pagePath);
      } else if (ext.equals("flv")) {
        Path iconPath = Utils.replaceExtension(filePath, "jpg");
        
        if (webSite.getFile(iconPath).exists()) {
          icon = thumbMaker.checkAndCreate(webSite, iconPath, thumbName);
        } else {
          icon = md.getModulePath().add("tango-video.png");
        }

        // unfortunately, videos must be relative to the SWF
        link = webSite.getLink(link, md.getModulePath());
      }
      
      if (icon != null) {
        WebUtils.updateLastModifiedTime(request, files[i]);
        String name = Utils.beautify(Utils.removeExtension(files[i].getName()), true);
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
}
%>
</div>
