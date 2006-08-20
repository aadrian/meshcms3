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
--%><%@ page contentType="text/xml" import="java.io.*,java.util.*,java.text.*,org.meshcms.core.*,org.meshcms.util.*" %><jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" /><?xml version="1.0" encoding="<%= webSite.getConfiguration().getPreferredCharset() %>" ?>

<%
  Path path = new Path(request.getParameter("path"));
  Path mp = new Path(request.getParameter("modulepath"));
  String cp = request.getContextPath();
  String defaultImage = WebUtils.getContextHomeURL(request).append('/').append(mp).append("/defaultimage.jpg").toString();
%>

<playlist version="1" xmlns = "http://xspf.org/ns/0/">
  <title>Playlist loaded</title>
  
  <trackList>

<%
  File dir = webSite.getFile(path);
  String[] files = dir.list();
  
  for (int i = 0; files != null && i < files.length; i++) {
    if (Utils.getExtension(files[i], false).equalsIgnoreCase("mp3")) {
      String base = WebUtils.getContextHomeURL(request).append('/').append(path).append('/').toString();
      String imgName = Utils.removeExtension(files[i]) + ".jpg";
      
      if (new File(dir, imgName).exists()) {
        imgName = base + imgName;
      } else {
        imgName = defaultImage;
      }
%>
    <track>
      <location><%= WebUtils.getContextHomeURL(request).append('/').append(path).append('/') %><%= files[i] %></location>
      <info><%= WebUtils.getContextHomeURL(request).append('/').append(path).append('/') %><%= files[i] %></info>
      <title><%= Utils.beautify(Utils.removeExtension(files[i]), true) %></title>
      <image><%= imgName %></image>
    </track>
<%
    }
  }
%>
  </trackList>

</playlist>