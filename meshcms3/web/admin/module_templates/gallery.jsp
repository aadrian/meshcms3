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
<%@ page import="java.util.*" %>
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />

<%
  String cp = request.getContextPath();
  String style = request.getParameter("style");

  if (Utils.isNullOrEmpty(style)) {
    style = "";
  } else {
    style = " class=\"" + style + "\"";
  }
  
  File[] files = WebUtils.getModuleFiles(webApp, request, true);

  if (files != null) {
    Arrays.sort(files, new FileNameComparator());
    int col = 0;
    int cols = Utils.parseInt(request.getParameter("columns"), 3);
%>

<table<%= style %> width="300" align="center" border="0" cellspacing="20" cellpadding="0">
<%
    GalleryThumbnail thumbMaker = new GalleryThumbnail();
    String thumbName = thumbMaker.getSuggestedFileName();
    
    for (int i = 0; i < files.length; i++) {
      if (!files[i].isDirectory() &&
          webApp.getFileTypes().isLike(files[i], "jpg")) {
        Path path = webApp.getPath(files[i]);
        Path thumbPath = thumbMaker.checkAndCreate(webApp, path, thumbName);
        
        if (thumbPath != null) {
          if (col == 0) {
            %><tr><%
          }

          %><td align="center" valign="top">
           <a href="<%= cp + '/' + path %>" target="meshcms_image"><img
            src="<%= cp + '/' + thumbPath %>" /><br /><%= Utils.beautify(Utils.removeExtension(path), true) %></a>
          </td><%
          
          out.flush();

          if (col == cols - 1) {
            %></tr><%
          }

          col = (col + 1) % cols;
        }
      }
    }

    if (col > 0) {
      for (int i = col; i < cols; i++) {
        %><td>&nbsp;</td><%
      }

      %></tr><%
    }
%>
</table>
<%
  }
%>
