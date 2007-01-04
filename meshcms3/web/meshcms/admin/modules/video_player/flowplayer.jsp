<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2007 Luciano Vernaschi

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

<%@ page contentType="text/javascript" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

{
  playList: [
<%
  String cp = request.getContextPath();
  Path path = new Path(request.getParameter("path"));
  String[] files = webSite.getFile(path).list();
  String base = cp + path.getAsLink() + '/';
  boolean comma = false;

  if (files != null) {
    Arrays.sort(files);

    for (int i = 0; files != null && i < files.length; i++) {
      if (Utils.getExtension(files[i], false).equalsIgnoreCase("flv")) {
%>
    <%= comma ? "," : "" %>
    { name: '<%= Utils.beautify(Utils.removeExtension(files[i]), true).replaceAll("'", "\\'") %>', url: '<%= base + files[i].replaceAll("'", "\\'") %>' }
<%
        comma = true;
      }
    }
  }
%>
  ],

  baseURL: '',
  autoPlay: false,
  autoBuffering: false,
  bufferLength: 5,
  loop: false,
  videoHeight: 240,
  splashImageFile: '<%= cp %><%= new Path(request.getParameter("modulepath")).getAsLink() %>/clicktoplay.jpg',
  scaleSplash: false
}
