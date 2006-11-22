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

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="org.meshcms.webui.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />
<jsp:useBean id="fileClipboard" scope="session" class="org.meshcms.webui.FileClipboard" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }
%>

<%
  ResourceBundle bundle = WebUtils.getPageResourceBundle(pageContext);

  String cp = request.getContextPath();
  String field = Utils.noNull(request.getParameter("field"));

  String thumbsParam = request.getParameter("thumbnails");
  // "type" is "image" when the file manages is called from within TinyMCE to
  // select an image
  boolean thumbnails = (thumbsParam == null) ?
    "image".equals(request.getParameter("type")) : Utils.isTrue(thumbsParam);
  thumbsParam = "&thumbnails=" + thumbnails;
  
  String folderParam = request.getParameter("folder");
  Path folderPath = (folderParam == null) ? userInfo.getHomePath() :
    new Path(folderParam);
%>

<html>
<head>
  <%= webSite.getDummyMetaThemeTag() %>
  <title><fmt:message key="homeFile" /></title>
  <link href="../theme/main.css" type="text/css" rel="stylesheet" />
  <link href="../scripts/xmenu/xmenu.css" type="text/css" rel="stylesheet" />
  <link href="../scripts/xmenu/xmenu.windows.css" type="text/css" rel="stylesheet" />
  <link href="../scripts/xtree/xtree.css" type="text/css" rel="stylesheet">
  <link href="../scripts/jscalendar/calendar-win2k-1.css" type="text/css" rel="stylesheet">

  <style type="text/css">
    body { margin: 0px; overflow: hidden; }
    .full { width: 100%; height: 100%; }
    .menuicon { vertical-align: middle; margin-right: 12px; }
  </style>
  
  <script type="text/javascript">
    function msgConfirmDelete(path) {
      return "<fmt:message key="msgConfirmDelete"><fmt:param value="\" + path + \"" /></fmt:message>";
    }
    
    function msgSelectFolder() {
      return "<fmt:message key="msgSelectFolder" />";
    }

    function msgSingleFile() {
      return "<fmt:message key="msgSingleFile" />";
    }
    
    function msgNewName() {
      return "<fmt:message key="msgNewName" />";
    }
    
    function msgCopyName() {
      return "<fmt:message key="msgCopyName" />";
    }
    
    function msgNewFile() {
      return "<fmt:message key="msgNewFile" />";
    }
    
    function msgNewFolder() {
      return "<fmt:message key="msgNewFolder" />";
    }
    
    function msgSuggestedFolderName() {
      return "<fmt:message key="msgSuggestedFolderName" />";
    }
  </script>
  <script type="text/javascript" src="../scripts/xmenu/cssexpr.js"></script>
  <script type="text/javascript" src="../scripts/xmenu/xmenu.js"></script>
  <script type="text/javascript" src="../scripts/xtree/xtree.js"></script>
  <script type="text/javascript" src="../scripts/filemanager.js"></script>
  <script type="text/javascript" src="../scripts/jscalendar/calendar.js"></script>
  <script type="text/javascript" src="../scripts/jscalendar/lang/<fmt:message key="DHTMLCalendarLangCode" />.js" charset="<fmt:message key="DHTMLCalendarLangCharset" />"></script>
  <script type="text/javascript" src="../scripts/jscalendar/calendar-setup.js"></script>
</head>

<body style="margin: 0px;" onload="fm_xTreeExpandTo(folder<%= WebUtils.getMenuCode(folderPath) %>)">
 <table cellspacing="0" class="full">
  <tr>
   <td colspan="2">
    <script type="text/javascript">
      webfxMenuImagePath = "../scripts/xmenu/images/";
      webfxMenuUseHover = true;
      WebFXMenu.prototype.borderLeft = 2;
      WebFXMenu.prototype.borderRight = 2;
      WebFXMenu.prototype.borderTop = 2;
      WebFXMenu.prototype.borderBottom = 2;
      WebFXMenu.prototype.paddingLeft = 1;
      WebFXMenu.prototype.paddingRight = 1;
      WebFXMenu.prototype.paddingTop = 1;
      WebFXMenu.prototype.paddingBottom	= 1;
      WebFXMenu.prototype.shadowLeft = 0;
      WebFXMenu.prototype.shadowRight = 0;
      WebFXMenu.prototype.shadowTop = 0;
      WebFXMenu.prototype.shadowBottom = 0;
      
      var fileMenu = new WebFXMenu;
      fileMenu.width = 200;
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_open.gif\' class=\'menuicon\'><fmt:message key="fmViewFile" />', 'javascript:fm_viewFile()'));
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_wysiwyg.gif\' class=\'menuicon\'><fmt:message key="fmEditVisually" />', 'javascript:fm_editPage()'));
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_edit.gif\' class=\'menuicon\'><fmt:message key="fmEditSrc" />', 'javascript:fm_editFile()'));
      fileMenu.add(new WebFXMenuSeparator());
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_newdir.gif\' class=\'menuicon\'><fmt:message key="fmNewFolder" />', 'javascript:fm_createDir()'));
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_new.gif\' class=\'menuicon\'><fmt:message key="fmNewFile" />', 'javascript:fm_createFile()'));
      fileMenu.add(new WebFXMenuSeparator());
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_delete.gif\' class=\'menuicon\'><fmt:message key="fmDelete" />', 'javascript:fm_deleteFiles()'));
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_rename.gif\' class=\'menuicon\'><fmt:message key="fmRename" />', 'javascript:fm_renameFile()'));
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_fixname.gif\' class=\'menuicon\'><fmt:message key="fmFixFileNames" />', 'javascript:fm_fixFileNames()'));
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_touch.gif\' class=\'menuicon\'><fmt:message key="fmTouch" />', 'javascript:fm_touchFiles()'));
      fileMenu.add(new WebFXMenuItem('<img src=\'images/button_changedate.gif\' class=\'menuicon\'><fmt:message key="fmChangeDate" />', null));
    
      var editMenu = new WebFXMenu;
      editMenu.width = 200;
      editMenu.add(new WebFXMenuItem('<img src=\'images/button_cut.gif\' class=\'menuicon\'><fmt:message key="fmCut" />', 'javascript:fm_clipboardCut()'));
      editMenu.add(new WebFXMenuItem('<img src=\'images/button_copy.gif\' class=\'menuicon\'><fmt:message key="fmCopy" />', 'javascript:fm_clipboardCopy()'));
      editMenu.add(new WebFXMenuItem('<img src=\'images/button_paste.gif\' class=\'menuicon\'><fmt:message key="fmPaste" />', 'javascript:fm_clipboardPaste()'));
      editMenu.add(new WebFXMenuItem('<img src=\'images/button_duplicate.gif\' class=\'menuicon\'><fmt:message key="fmDuplicate" />', 'javascript:fm_duplicateFile()'));
      editMenu.add(new WebFXMenuSeparator());
      editMenu.add(new WebFXMenuItem('<img src=\'images/button_selall.gif\' class=\'menuicon\'><fmt:message key="fmSelAll" />', 'javascript:fm_selectAll(true)'));
      editMenu.add(new WebFXMenuItem('<img src=\'images/button_selnone.gif\' class=\'menuicon\'><fmt:message key="fmSelNone" />', 'javascript:fm_selectAll(false)'));
      editMenu.add(new WebFXMenuItem('<img src=\'images/button_toggle.gif\' class=\'menuicon\'><fmt:message key="fmSelInv" />', 'javascript:fm_toggleAll()'));

      var viewMenu = new WebFXMenu;
      viewMenu.width = 200;
      viewMenu.add(new WebFXMenuItem('<img src=\'images/button_details.gif\' class=\'menuicon\'><fmt:message key="fmDetails" />', 'javascript:fm_viewThumbnails(false)'));
      viewMenu.add(new WebFXMenuItem('<img src=\'images/button_thumbs.gif\' class=\'menuicon\'><fmt:message key="fmThumbs" />', 'javascript:fm_viewThumbnails(true)'));
      viewMenu.add(new WebFXMenuSeparator());
      viewMenu.add(new WebFXMenuItem('<img src=\'images/button_refresh.gif\' class=\'menuicon\'><fmt:message key="fmRefresh" />', 'javascript:fm_dummy()'));
      
      var toolsMenu = new WebFXMenu;
      toolsMenu.width = 200;
      toolsMenu.add(new WebFXMenuItem('<img src=\'images/button_upload.gif\' class=\'menuicon\'><fmt:message key="fmUpload" />', 'javascript:fm_uploadFile()'));
      toolsMenu.add(new WebFXMenuItem('<img src=\'images/button_download.gif\' class=\'menuicon\'><fmt:message key="fmDownload" />', 'javascript:fm_downloadFile(\'<%= cp %>\')'));
      toolsMenu.add(new WebFXMenuItem('<img src=\'images/button_downloadzip.gif\' class=\'menuicon\'><fmt:message key="fmDownloadZip" />', 'javascript:fm_downloadZip(\'<%= cp %>\')'));
      toolsMenu.add(new WebFXMenuSeparator());
      toolsMenu.add(new WebFXMenuItem('<img src=\'images/button_unzip.gif\' class=\'menuicon\'><fmt:message key="fmUnzip" />', 'javascript:fm_unzipFile()'));

      var themesMenu = new WebFXMenu;
      themesMenu.width = 200;
      themesMenu.add(new WebFXMenuItem('<img src=\'../theme/tx1x1.gif\' width=\'16\' height=\'16\' class=\'menuicon\'><fmt:message key="fmInherit" />', 'javascript:fm_changeTheme()'));
      themesMenu.add(new WebFXMenuItem('<img src=\'../theme/tx1x1.gif\' width=\'16\' height=\'16\' class=\'menuicon\'><fmt:message key="fmNoTheme" />', 'javascript:fm_changeTheme(\'<%= PageAssembler.EMPTY %>\')'));
      themesMenu.add(new WebFXMenuSeparator());
      
      <% String[] themes = webSite.getSiteMap().getThemeNames();

      for (int i = 0; i < themes.length; i++) { %>
        themesMenu.add(new WebFXMenuItem('<img src=\'../theme/tx1x1.gif\' width=\'16\' height=\'16\' class=\'menuicon\'><%=
            Utils.escapeSingleQuotes(Utils.beautify(themes[i], true)) %>', 'javascript:fm_changeTheme(\'<%= Utils.escapeSingleQuotes(themes[i]) %>\')'));
      <% } %>
      
      
      var toolBar = new WebFXMenuBar;
      toolBar.add(new WebFXMenuButton('<fmt:message key="fmFile" />', null, null, fileMenu));
      toolBar.add(new WebFXMenuButton('<fmt:message key="fmEdit" />', null, null, editMenu));
      toolBar.add(new WebFXMenuButton('<fmt:message key="fmView" />', null, null, viewMenu));
      toolBar.add(new WebFXMenuButton('<fmt:message key="fmTools" />', null, null, toolsMenu));
      toolBar.add(new WebFXMenuButton('<fmt:message key="fmThemes" />', null, null, themesMenu));
      toolBar.add(new WebFXMenuButton('<fmt:message key="fmClose" />', 'javascript:fm_closeFileManager()', null, null));
      document.write(toolBar);
    </script>  
   </td>
  </tr>

  <tr class="full">
   <td width="240" valign="top">
    <div style="width: 240px; height: 100%; overflow:auto;">
      <script type="text/javascript">
        webFXTreeConfig['usePersistence'] = false;
        
        webFXTreeConfig['rootIcon'] = '../scripts/xtree/images/foldericon.png';
        webFXTreeConfig['openRootIcon'] = '../scripts/xtree/images/openfoldericon.png';
        webFXTreeConfig['folderIcon'] = '../scripts/xtree/images/foldericon.png';
        webFXTreeConfig['openFolderIcon'] = '../scripts/xtree/images/openfoldericon.png';
        webFXTreeConfig['fileIcon'] = '../scripts/xtree/images/file.png';
        webFXTreeConfig['iIcon'] = '../scripts/xtree/images/I.png';
        webFXTreeConfig['lIcon'] = '../scripts/xtree/images/L.png';
        webFXTreeConfig['lMinusIcon'] = '../scripts/xtree/images/Lminus.png';
        webFXTreeConfig['lPlusIcon'] = '../scripts/xtree/images/Lplus.png';
        webFXTreeConfig['tIcon'] = '../scripts/xtree/images/T.png';
        webFXTreeConfig['tMinusIcon'] = '../scripts/xtree/images/Tminus.png';
        webFXTreeConfig['tPlusIcon'] = '../scripts/xtree/images/Tplus.png';
        webFXTreeConfig['blankIcon'] = '../scripts/xtree/images/blank.png';
        <% new FolderXTree(webSite, userInfo, pageContext.getOut(),
            thumbsParam, bundle.getString("fmSiteRoot")).process(); %>
        document.write(folder0);
      </script>
    </div>
   </td>
   
   <td width="100%"><iframe src="showlist.jsp?folder=<%= folderPath %><%= thumbsParam %>"
    class="full" id="listframe" name="listframe"></iframe></td>
  </tr>
  
  <tr>
    <td colspan="2" bgcolor="#D4D0C8"
     style="border: 1px inset #D4D0C8; padding: 1px 6px 1px 6px;">
<%
  int n = fileClipboard.countFiles();

  if (n == 0) {
    out.write(bundle.getString("fmClipEmpty"));
  } else {
    Locale locale = WebUtils.getPageLocale(pageContext);
    MessageFormat formatter = new MessageFormat("", locale);
    double[] fileLimits = { 1, 2 };
    String [] fileStrings = { bundle.getString("fmClipOneFile"),
        bundle.getString("fmClipManyFiles") };
    ChoiceFormat choiceFormat = new ChoiceFormat(fileLimits, fileStrings);
    String pattern = bundle.getString("fmClipFull");
    Format[] formats = { choiceFormat, null, NumberFormat.getInstance() };
    formatter.applyPattern(pattern);
    formatter.setFormats(formats);
    Object[] args = { new Integer(n),
        fileClipboard.getDirPath().getAsLink(), new Integer(n) };
    out.write(formatter.format(args));
  }
  
  if (!field.equals("")) {
%>
     <div align="right"><input type="button" value="<fmt:message key="genericSelect" />"
       onclick="javascript:fm_return('<%= field %>');" />
      <input type="button" value="<fmt:message key="genericCancel" />"
       onclick="javascript:window.close();" /></div>
<%
  }
%>
    </td>
  </tr>
  
 <%--
 <tr>
  <td colspan="2">
   <form name="fmfm" method="post" action="process.jsp">
    <input type="text" name="f_action" id="f_action" />
    <input type="text" name="f_dir" id="f_dir" value="<%= folderPath %>" />
    <input type="text" name="f_files" id="f_files" />
    <input type="text" name="s_thumbs" id="s_thumbs" value="<%= thumbnails %>" />
    <input type="text" name="s_field" id="s_field" value="<%= field %>" />
   </form>
  </td>
 </tr>
 --%>
 </table> 

 <form name="fmfm" method="post" action="process.jsp">
  <input type="hidden" name="f_action" id="f_action" />
  <input type="hidden" name="f_dir" id="f_dir" value="<%= folderPath %>" />
  <input type="hidden" name="f_files" id="f_files" />
  <input type="hidden" name="s_thumbs" id="s_thumbs" value="<%= thumbnails %>" />
  <input type="hidden" name="s_field" id="s_field" value="<%= field %>" />
 </form>

 <script type="text/javascript">
  Calendar.setup(
    {
      inputField : "f_action",
      ifFormat : "cdate%Y%m%d%H%M%S",
      button : "webfx-menu-object-14",
      onClose : fm_closeCalendar,
      showsTime : true,
      singleClick : false,
      align : "bR"
    }
  );
 </script>
</body>
</html>
