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
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_EDIT_PAGES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
  int padding = 13;
%>

<html>
<head>
<%= webApp.getAdminMetaThemeTag() %>
<title><fmt:message key="mapTitle" /></title>
<script language='javascript' type='text/javascript' src='tiny_mce/tiny_mce_src.js'></script>
<script language="javascript" type="text/javascript">
  tinyMCE.init({entity_encoding:'numeric',debug:false});
  
  function fixHTMLEntities() {
    if (document.forms["sitemapform"].useEntities.checked) {
      var elms = document.forms["sitemapform"].elements;

      for (var i = 0; i < elms.length; i++) {
        if (elms[i].type == "text") {
          elms[i].value = tinyMCE.convertStringToXML(elms[i].value);
        }
      }
    }
  }

  /**
   * Closed folder
   */
  var imgClosedFolder = "filemanager/images/icon_folder.gif";

  /**
   * Open folder
   */
  var imgOpenFolder = "filemanager/images/icon_folderopen.gif";
  
  /**
   * Clears a field
   */
  function editMap_clr(fid) {
    document.getElementById(fid).value = "";
  }

  /**
   * Toggles the visibility of a row in the table
   *
   * code: the numeric code of the row
   * pad: the amount of padding, used to find children elements
   */
  function editMap_toggle(code, pad) {
    var img = document.getElementById("img" + code);

    if (img) { // code is correct
      if (img.src.indexOf(imgClosedFolder) != -1) { // folder is currently closed
        editMap_openChildren(code, pad, false);
        img.src = imgOpenFolder;
      } else { // folder is currently opened
        editMap_closeChildren(code, pad);
        img.src = imgClosedFolder;
      }
    }
  }

  /**
   * Opens all children of the selected folder
   *
   * code: the numeric code of the row
   * pad: the amount of padding, used to find children elements
   * openAll: if true, open all descentants, else open children only
   */
  function editMap_openChildren(code, pad, openAll) {
    var tr = document.getElementById("tr" + code); // the row
    var pad0 = parseInt(tr.firstChild.style.paddingLeft); // padding in the 1st <td>
    tr.style.display = ""; // make this element visible
    var img0 = document.getElementById("img0"); // image of the root folder

    if (img0) {
      img0.src = imgOpenFolder; // root folder always open
    }

    while (true) {
      tr = tr.nextSibling; // recurse table rows

      if (!tr || !tr.id) { // no more rows?
        return;
      }

      var pad1 = parseInt(tr.firstChild.style.paddingLeft); // padding in the 1st <td>

      if (pad1 <= pad0) { // same or higer level
        return;
      }

      if (openAll) { // all descendants must be made visible
        tr.style.display = "";
        var img = document.getElementById("img" + tr.id.substring(2));

        if (img) {
          img.src = imgOpenFolder;
        }
      } else if (pad1 == pad0 + pad) { // open only if direct child
        tr.style.display = "";
      }
    }
  }

  /**
   * Closes all children of the selected folder. Same procedure as editMap_openChildren
   *
   * code: the numeric code of the row
   * pad: the amount of padding, used to find children elements
   */
  function editMap_closeChildren(code, pad) {
    var tr = document.getElementById("tr" + code);
    var pad0 = parseInt(tr.firstChild.style.paddingLeft);

    while (true) {
      tr = tr.nextSibling;

      if (!tr || !tr.id) {
        return;
      }

      var pad1 = parseInt(tr.firstChild.style.paddingLeft);

      if (pad1 <= pad0) {
        return;
      }

      tr.style.display = "none";
      var img = document.getElementById("img" + tr.id.substring(2));

      if (img) {
        img.src = imgClosedFolder;
      }
    }
  }

  /**
   * Opens a popup to create a new page
   */
  function editMap_createPage(path) {
    editMap_openSmallPopup("createpage.jsp?path=" + path);
  }

  /**
   * Opens a popup to delete a page
   */
  function editMap_deletePage(path) {
    if (confirm("<fmt:message key="msgConfirmDelete"><fmt:param value="/\" + path + \"" /></fmt:message>")) {
      editMap_openSmallPopup("deletepage.jsp?path=" + path);
    }
  }

  /**
   * Opens a small popup
   */
  function editMap_openSmallPopup(url) {
    popup = window.open(url, "smallpopup",
      "width=340,height=180,menubar=no,status=no,toolbar=no,resizable=yes");
    popup.focus();
  }
</script>

<style type="text/css">
  table.meshcmseditor td {
    white-space: nowrap;
  }
</style>
</head>

<body>

<%
  String cp = request.getContextPath();
  int cacheType = webApp.getConfiguration().getCacheType();
  SiteInfo siteInfo = webApp.getSiteInfo();
  SiteMap siteMap = webApp.getSiteMap();
  String[] themes = siteMap.getThemeNames();
  List pagesList = siteMap.getPagesList();
  int pagesCount = pagesList.size();
%>

<p style="padding-left: 5px; padding-right: 5px;">
 <div align="right"><%= webApp.helpIcon(cp, Finals.HELP_ANCHOR_MANAGE_PAGES, userInfo) %></div>
 <fmt:message key="mapTotal" /> <%= pagesCount %>
</p>

<%
  Path userHome = userInfo.getHomePath();
  String[] welcomes = WebUtils.getWelcomeFiles(application);
  boolean userHomePage = false;
  
  for (int i = 0; i < welcomes.length; i++) {
    if (webApp.getFile(userHome.add(welcomes[i])).exists()) {
      userHomePage = true;
    }
  }
  
  if (!userHomePage) {
    webApp.getFile(userHome).mkdirs();
%>
  <p align="center">
    <a href="createpage.jsp?path=<%= userHome %>&title=<%= Utils.removeExtension(welcomes[0]) %>&newdir=false"><fmt:message key="mapCreateUserHome" /></a>
  </p>
<%
  }
%>

<form action="editmap2.jsp" method="POST" name="sitemapform" onsubmit="javascript:fixHTMLEntities()">
  <table class="meshcmseditor" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <th><img src="filemanager/images/icon_folderopen.gif" align="left"
           onclick="javascript:editMap_openChildren(0, <%= padding %>, true);"
           title="<fmt:message key="mapClickExpandAll" />" /><fmt:message key="mapPageTitle" /></th>
      <th><fmt:message key="mapHits" /></th>
      <th><fmt:message key="mapCache" /></th>
      <th><fmt:message key="mapMenu" /></th>
      <th><fmt:message key="mapTheme" /></th>
      <th><fmt:message key="mapScore" /></th>
      <th colspan="2"><fmt:message key="mapActions" /></th>
    </tr>
<%
  Iterator iter = pagesList.iterator();
  
  while (iter.hasNext()) {
    PageInfo pageInfo = (PageInfo) iter.next();
    Path pagePath = pageInfo.getPath();
    int code = WebUtils.getMenuCode(pagePath);
    boolean hasChildren = siteMap.hasChildrenPages(pagePath);
                            // IMPORTANT: no spaces between <tr> and <td> below!
    %><tr id="tr<%= code %>"><td style="padding-left: <%= (pagePath.getElementCount() + 1) * padding %>px;">
        <% if (hasChildren) { %>
          <img src="filemanager/images/icon_folderopen.gif" id="img<%= code %>"
           title="<fmt:message key="mapClickExpand" />"
           onclick="javascript:editMap_toggle(<%= code %>, <%= padding %>);" />
        <% } else { %>
          <img src="filemanager/images/icon_file.gif"
           title="<fmt:message key="mapNoClick" />" />
        <% } %>
          <a href="<%= cp + pageInfo.getLink() %>"
           title="<fmt:message key="mapOpen">
             <fmt:param value="<%= pageInfo.getLink() %>" />
           </fmt:message>"><%= pageInfo.getTitle() %></a>
        </td>

        <td align="right"><%= pageInfo.getTotalHits() %></td>
        
        <% boolean cached = false;
        
        if (cacheType == Finals.IN_MEMORY_CACHE) {
          cached = siteMap.isCached(pagePath);
        } else if (cacheType == Finals.ON_DISK_CACHE) {
          File cacheFile = webApp.getRepositoryFile
              (siteMap.getServedPath(pagePath), Finals.CACHE_FILE_NAME);
          cached = cacheFile.exists() &&
              cacheFile.lastModified() > siteMap.getLastModified();
        }
        
        if (cached) { %>
          <td align="center"><img src="filemanager/images/button_yes.gif"
           align="absmiddle" title="<fmt:message key="mapInCache" />" /></td>
        <% } else { %>
          <td>&nbsp;</td>
        <% }
    
        if (userInfo.canWrite(webApp, pagePath)) {
          String theme = siteInfo.getPageTheme(pagePath);
          String tCode = siteInfo.getTitleCode(pagePath);
          String dCode = siteInfo.getThemeCode(pagePath);
          String sCode = siteInfo.getScoreCode(pagePath);
          %><td><img src="images/clear_field.gif" onclick="javascript:editMap_clr('<%= tCode %>');"
             align="middle" /><input type="text" name="<%= tCode %>"
             id="<%= tCode %>" size="24"
             value="<%= siteInfo.getPageTitle(pagePath) %>" /></td>

            <td><select name="<%= dCode %>">
             <option value="">&nbsp;</option>
             <option <%= Finals.EMPTY.equals(theme) ? "selected='selected'" : "" %>
              value="<%= Finals.EMPTY %>"><fmt:message key="mapNoTheme" /></option>
         
          <% for (int j = 0; j < themes.length; j++) { %>
             <option <%= themes[j].equals(theme) ? "selected='selected'" : "" %>
              value="<%= themes[j] %>"><%= Utils.beautify(themes[j], true) %></option>
          <% } %>
            </select></td>

            <td><img src="images/clear_field.gif" onclick="javascript:editMap_clr('<%= sCode %>');"
             align="middle" /><input type="text" name="<%= sCode %>"
             id="<%= sCode %>" size="6" value="<%= siteInfo.getPageScoreAsString(pagePath) %>" /></td>

          <% if (webApp.isDirectory(pagePath)) { %>
            <td align="center"><img src="filemanager/images/button_newchild.gif"
             onclick="javascript:editMap_createPage('<%= Utils.escapeSingleQuotes(pagePath) %>');" align="middle"
             title="<fmt:message key="mapNewChild" />" /></td>
          <% } else { %>
            <td>&nbsp;</td>
          <% }
          
          if (!hasChildren) { %>          
            <td align="center"><img src="filemanager/images/button_delete.gif"
             onclick="javascript:editMap_deletePage('<%= Utils.escapeSingleQuotes(pagePath) %>');" align="middle"
             title="<fmt:message key="mapDelete" />" /></td>
          <% } else { %>
            <td>&nbsp;</td>
          <% }
        } else { %>
          <td>&nbsp;<%= siteInfo.getPageTitle(pagePath) %></td>
          <td>&nbsp;<%= Utils.beautify(siteInfo.getPageTheme(pagePath), true) %></td>
          <td>&nbsp;<%= siteInfo.getPageScoreAsString(pagePath) %></td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
        <% } %>
    </tr><% } %> <%-- IMPORTANT: no spaces after <tr>! --%>
    <tr>
      <td align="center" colspan="8">
        <input type="checkbox" name="useEntities"
         value="true"<%= webApp.getConfiguration().isUseEntities() ? " checked='true'" : "" %> />
        <fmt:message key="mapUseEntities" />
      </td>
    </tr>
    <tr>
      <th align="center" colspan="8">
        <input type="submit" value="<fmt:message key="genericSave" />" />
      </th>
    </tr>
  </table>
</form>

<% if (pagesCount > 20) { %>
<script language="javascript" type="text/javascript">
  editMap_toggle(0, <%= padding %>);
  editMap_toggle(0, <%= padding %>);
</script>
<% } %>

</body>
</html>
