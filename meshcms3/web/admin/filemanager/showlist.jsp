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
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />
<jsp:useBean id="fileNameComparator" scope="session" class="com.cromoteca.util.FileNameComparator" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
%>

<%
  String cp = request.getContextPath();
  boolean showThumbs = Utils.isTrue(request.getParameter("thumbnails"));
  Path folderPath = new Path(request.getParameter("folder"));
  File folder = webApp.getFile(folderPath);
  FileManagerThumbnail thumbMaker = new FileManagerThumbnail();
  String thumbName = thumbMaker.getSuggestedFileName();

  if (folder.isDirectory()) {
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = WebUtils.getPageResourceBundle(pageContext);
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
    FileTypes ft = webApp.getFileTypes();
    File[] list = folder.listFiles();
    Arrays.sort(list, fileNameComparator);
    
    Path welcomePath = webApp.getSiteMap().findCurrentWelcome(folderPath);
    Path thumbnailsPath = new Path(webApp.getConfiguration().getThumbnailsDir());
    String welcome = (welcomePath == null) ? null : welcomePath.getLastElement();
%>

<html>

<head>
  <%= webApp.getDummyMetaThemeTag() %>
  <title>List Files</title>
  <script type="text/javascript">
    if (window.parent.document.forms['fmfm'].s_thumbs) {
      window.parent.document.forms['fmfm'].f_dir.value = "<%= folderPath %>";
      window.parent.document.forms['fmfm'].s_thumbs.value = '<%= showThumbs %>';
      // window.parent.fm_xTreeExpandTo(window.parent.folder<%= WebUtils.getMenuCode(folderPath) %>);
    }
    
    function toggleSelection(tName) {
      var files = document.getElementsByTagName('input');

      for (var i = 0; i < files.length; i++) {
        if (files[i].type == 'checkbox' && files[i].name == tName) {
          files[i].checked = files[i].checked ? "" : "checked";
        }
      }
    }
  </script>
  <link href="../theme/main.css" type="text/css" rel="stylesheet" />
  
  <style type="text/css">
    body {
      margin: 0px;
      overflow: scroll;
    }

    .miniblock {
      display: block;
      width: <%= FileManagerThumbnail.THUMB_WIDTH %>px;
      max-width: <%= FileManagerThumbnail.THUMB_WIDTH %>px;
      overflow: hidden;
      padding: 0px;
    }

    td {
      white-space: nowrap;
    }

    input {
      border: none!important;
    }
  </style>
</head>

<body>
<form name="filelist">
<% 
    if (!showThumbs) {
%>
<table cellspacing="0">
 <thead> 
  <tr>
   <th>&nbsp;</th>
   <th>&nbsp;</th>
   <th><fmt:message key="fmListName" /></th>
   <th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
   <th><fmt:message key="fmListSize" /></th>
   <th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
   <th><fmt:message key="fmListDate" /></th>
   <th width="100%" align="right">
     <% if (!userInfo.canWrite(webApp, folderPath)) { %>
       <img src="images/lock.gif" title="<fmt:message key="fmListLocked" />" />
     <% } %>
     <%= webApp.helpIcon(cp, Finals.HELP_ANCHOR_FILE_MANAGER, userInfo) %>
   </th>
  </tr>
 </thead>
 
 <tbody>
<%
    } else {
%>
<div align="right" style="background-color: #D4D0C8;"><%= webApp.helpIcon(cp, Finals.HELP_ANCHOR_FILE_MANAGER, userInfo) %></div>
<%
    }
  
    for (int i = 0; i < list.length; i++) {
      String name = list[i].getName();
      Path filePath = folderPath.add(name);
      
      if (userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS) || 
          !(webApp.isSystem(filePath) || filePath.isContainedIn(thumbnailsPath))) {
        String color = "";

        if (ft.isPage(name)) {
          color = name.equals(welcome) ? "ffff99" : "eaeaea";
          color = " bgcolor='" + color + "'";
        }

        if (showThumbs) {
          Path thumbPath = null;

          if (!list[i].isDirectory() &&
              webApp.getFileTypes().isLike(list[i], "jpg")) {
            thumbPath = thumbMaker.checkAndCreate(webApp, filePath, thumbName);
          }
%>
  <table title="<%= name %>" style="display: inline; width: <%= FileManagerThumbnail.THUMB_WIDTH %>px;">
   <tr>
    <td onclick="javascript:toggleSelection('<%= Utils.escapeSingleQuotes(name) %>');"><% if (thumbPath != null) {
      %><img src="<%= cp + '/' + thumbPath %>" /><% 
      } else { 
        %><div style="border: 1px solid #D8CECB; padding: 51px 45px;"><img
           src="images/<%= list[i].isDirectory() ? FileTypes.DIR_ICON : ft.getIconFile(name) %>" /></div><%
      } %></td>
   </tr>
 
   <tr<%= color %>>
    <td><div class="miniblock"><input type="checkbox" id="fileselector"
     name="<%= name %>" value="<%= name %>" />
     <%= name %></div></td>
   </tr>
  </table> 
<% 
        } else {
%>
  <tr<%= color %>>
   <td><img class="icon" src="images/<%= list[i].isDirectory() ? FileTypes.DIR_ICON :
     ft.getIconFile(name) %>" vspace="2" title="<%= ft.getDescription(name) %>"></td>
   <td><input type="checkbox" id="fileselector"
    name="<%= name %>" value="<%= name %>" /></td>  
   <td onclick="javascript:toggleSelection('<%= Utils.escapeSingleQuotes(name) %>');"><%= name %></td>  
   <td>&nbsp;</td>
   <td align="right"><%= list[i].isDirectory() ? "&nbsp;" : WebUtils.formatFileLength(list[i].length(), locale, bundle) %></td>
   <td>&nbsp;</td>
   <td><%= df.format(new Date(list[i].lastModified())) %></td>
   <td>&nbsp;</td>
  </tr> 
<%
        }
      }
    }

    if (!showThumbs) {
%>
 </tbody>
</table>
<%
    }
  }
%>
</form>
</body>
</html>
