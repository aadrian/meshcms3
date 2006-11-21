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
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%--
  Advanced parameters for this module:
  - captions = "true" (default) | false
  - columns = n (default 3)
  - css = (name of a css class)
  - quality = "low" | "high" (default)
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
  File[] files = md.getModuleFiles(webSite, true);

  if (files != null) {
    Arrays.sort(files, new FileNameComparator());
    int col = 0;
    int cols = Utils.parseInt(md.getAdvancedParam("columns", null), Math.min(3, files.length));
    boolean captions = Utils.isTrue(md.getAdvancedParam("captions", "true"));
%>

<table<%= md.getFullCSSAttribute("css") %> width="100" align="center" border="0" cellspacing="20" cellpadding="0">
<%
    GalleryThumbnail thumbMaker = new GalleryThumbnail();
    thumbMaker.setHighQuality(!"low".equals(md.getAdvancedParam("quality",
        webSite.getConfiguration().isHighQualityThumbnails() ? "high" : "low")));
    String thumbName = thumbMaker.getSuggestedFileName();
    
    for (int i = 0; i < files.length; i++) {
      if (!files[i].isDirectory() && FileTypes.isLike(files[i].getName(), "jpg")) {
        Path path = webSite.getPath(files[i]);
        Path thumbPath = thumbMaker.checkAndCreate(webSite, path, thumbName);
        
        if (thumbPath != null) {
          WebUtils.updateLastModifiedTime(request, files[i]);
          
          if (col == 0) {
            %><tr><%
          }

          %><td align="center" valign="top">
           <a href="<%= cp + '/' + path %>" target="meshcms_image"><img
            src="<%= cp + '/' + thumbPath %>" alt=""/><% if (captions) { 
              %><br /><%= Utils.beautify(Utils.removeExtension(path), true) %><%
            } %></a>
          </td><%
          
          out.flush();

          if (col == cols - 1) {
            %></tr><%
            out.flush();
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
