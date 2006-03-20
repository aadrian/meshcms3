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

<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_MANAGE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
  String cp = request.getContextPath();
%>

<html>
<head>
 <title><fmt:message key="newpage" /></title>
<% if (!Utils.isNullOrEmpty(request.getParameter("applytheme"))) { %>
 <%= webApp.getAdminMetaThemeTag() %>
<% } else { %>
 <%= webApp.getDummyMetaThemeTag() %>
 <link href="theme/main.css" type="text/css" rel="stylesheet" />
<% } %>

<script language='javascript' type='text/javascript' src='tiny_mce/tiny_mce.js'></script>
<script language="javascript" type="text/javascript">
  tinyMCE.init({debug:false});
  
  function fixHTMLEntities() {
    var tc = new TinyMCE_Cleanup();
    tc.settings.entity_encoding = "numeric";
    document.forms["createpage"].encTitle.value =
      tc.xmlEncode(document.forms["createpage"].title.value);
  }
</script>
</head>

<body>

<% String title = Utils.noNull(request.getParameter("title"));
Path path = new Path(request.getParameter("path"));

if (title.equals("")) { %>
  <p align="right"><%= webApp.helpIcon(cp, Finals.HELP_ANCHOR_NEW_PAGE, userInfo) %></p>
  
  <form action='createpage.jsp' method='POST' id='createpage' name='createpage'
   onsubmit='javascript:fixHTMLEntities();'>
    <input type='hidden' name='path' value='<%= path %>' />
    <input type='hidden' name='encTitle' value='' />

    <table align='center' border='0' cellspacing='0' cellpadding='2'>
      <tr><td>
        <fmt:message key="newpageTitle" />
        <input type='text' name='title' />
      </td></tr>
      <tr><td align='center'>
        <input type='checkbox' name='newdir' checked='true' value='true' />
        <fmt:message key="newpageFolder" />
      </td></tr>
      <tr><th align='center'>
        <input type='submit' value='<fmt:message key="newpageCreate" />' />
        <input type='button' value='<fmt:message key="genericCancel" />' onclick='javascript:window.close();' />
      </th></tr>
    </table>
  </form>
<% } else { 
  boolean newDir = Utils.isTrue(request.getParameter("newdir"));
  String fileName = WebUtils.fixFileName(title).toLowerCase();
  
  if (!newDir) {
    fileName += '.' + webApp.getConfiguration().getVisualExtensions()[0];
  }
  
  fileName = Utils.generateUniqueName(fileName, webApp.getFile(path));
  
  if (fileName == null) {
    %><fmt:message key="newpageError" /><br /><%
  } else {
    path = path.add(fileName);
    
    if (newDir) {
      path = path.add(webApp.getWelcomeFileNames()[0]);
    }
    
    String text = webApp.getHTMLTemplate(request.getParameter("encTitle"));

    if (webApp.saveToFile(userInfo, text, path, null)) {
      webApp.updateSiteMap(true);
      %><script type="text/javascript">
        var page = "<%= cp + '/' + path + '?' + Finals.ACTION_NAME + '=' + Finals.ACTION_EDIT %>";
        
        if (window.name && window.name == "smallpopup") { // it's a popup
          window.opener.location.href = page;
          window.close();
        } else {
          window.location.href = page;
        }
      </script><%
    } else {
      %><fmt:message key="newpageError" /><%
    }
  }
%><p><a href="javascript:history.back();"><fmt:message key="genericBack" /></a></p><%
} %>

</body>
</html>
