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
  - quality = "low" | "high" (the default depends on the site configuration)
  - order = "name" (default) | "date" (same as date_fwd) | "date_fwd" | "date_rev" | "random"
  - lightbox = "true" | "false" (default) to use the lightbox script to show big images
--%>

<%!
  private void readCaptionFile(File captionFile, Map captionMap, Map linkMap) {
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader
          (new FileInputStream(captionFile), Utils.SYSTEM_CHARSET));
      StringBuffer caption = new StringBuffer();
      String file = null;
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();

        // Is it a blank line?
        if (line.length() == 0) {
          // Save any existing caption block.
          if (file != null && caption.length() > 0) {
            captionMap.put(file, caption.toString());
          }
          file = null;
          caption.setLength(0);

        // Otherwise it's a normal line
        } else {
          // Is it the first line of a caption block?
          if (file == null) {
            file = line;
          } else if (line.startsWith("link:")) {
            linkMap.put(file, line.substring(5).trim());
          } else {
            if (caption.length() > 0) {
              caption.append(' ');
            }
            caption.append(line);
          }
        }
      }
      if (file != null && caption.length() > 0) {
        captionMap.put(file, caption.toString());
      }
      reader.close();
    } catch (IOException e) {
    }
  }
%>

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
    String sort = md.getAdvancedParam("order", "name");
    if("random".equalsIgnoreCase(sort)) {
      Collections.shuffle(Arrays.asList(files));
    } else {
      Comparator cmp;
      if ("date".equalsIgnoreCase(sort) || "date_fwd".equalsIgnoreCase(sort)) {
        cmp = new FileDateComparator(true);
      } else if ("date_rev".equals(sort)) {
        cmp = new FileDateComparator(false);
      } else {
        cmp = new FileNameComparator();
      }
      Arrays.sort(files, cmp);
    }
    int col = 0;
    int cols = Utils.parseInt(md.getAdvancedParam("columns", null), Math.min(3, files.length));
    boolean captions = Utils.isTrue(md.getAdvancedParam("captions", "true"));
    Map captionMap = null;
    Map linkMap = null;
    if (captions) {
      File captionFile = webSite.getFile(
       md.getModuleArgumentDirectoryPath(webSite, true).add("gallery.captions"));
      if (captionFile.exists()) {
        captionMap = new HashMap();
        linkMap = new HashMap();
        WebUtils.updateLastModifiedTime(request, captionFile);
        readCaptionFile(captionFile, captionMap, linkMap);
      }
    }
    
    boolean lightbox = Utils.isTrue(md.getAdvancedParam("lightbox", "false"));
    
    if (lightbox) { %>
      <script type="text/javascript" src="<%= cp + '/' + md.getModulePath() %>/js/mootools.v1.00.js"></script>
      <script type="text/javascript" src="<%= cp + '/' + md.getModulePath() %>/js/slimbox.v1.3.js"></script>
      <script type="text/javascript">
      window.addEvent('domready', function() {
          new Asset.css('<%= cp + '/' + md.getModulePath() %>/slimbox.css');
      });
      </script>
    <% } else { %>
      <script type="text/javascript" src="<%= cp + '/' + md.getModulePath() %>/closePopImage.js"></script>
    <% } %>

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
          String caption = null;
          String link = null;
          boolean onClick = !lightbox;

          if (captionMap != null) {
            caption = (String) captionMap.get(path.getLastElement());
          }

          if (caption == null) {
            caption = Utils.beautify(Utils.removeExtension(path), true);
          }
          
          if (linkMap != null) {
            link = (String) linkMap.get(path.getLastElement());
            onClick = false;
          }
          
          if (link == null) {
            link = cp + '/' + path;
          }
          %><td align="center" valign="top">
           <a href="<%= link %>" rel="lightbox[<%= moduleCode %>]"
          <% if (onClick) { %>
            onclick="return popImageExtra(this.href, '<%= Utils.replace(caption, '\'', "\\'") %>', true);"
          <% } %>
           ><img
            src="<%= cp + '/' + thumbPath %>" alt="<%= Utils.encodeHTML(caption) %>" /><% if (captions) {
              %><br /><%= caption %><% } %></a>
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
