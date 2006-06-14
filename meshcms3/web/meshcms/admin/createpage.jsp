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

<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_MANAGE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webSite.getConfiguration().getPreferredCharset());
  String cp = request.getContextPath();
%>

<html>
<head>
<title><fmt:message key="newpage" /></title>
<%
  boolean popup = Utils.isTrue(request.getParameter("popup"));

  if (popup) {
    out.write(webSite.getDummyMetaThemeTag());
    out.write("\n<link href='theme/main.css' type='text/css' rel='stylesheet' />");
  } else {
    out.write(webSite.getAdminMetaThemeTag());
  }
%>
</head>

<body>

<% String title = Utils.noNull(request.getParameter("title"));
Path path = new Path(request.getParameter("path"));

if (title.equals("")) { %>
  <p align="right"><%= webSite.helpIcon(cp, WebSite.HELP_ANCHOR_NEW_PAGE, userInfo) %></p>
  
  <form action='createpage.jsp' method='post' id='createpage' name='createpage'>
    <input type="hidden" name="popup" value="<%= popup %>" />
    <input type='hidden' name='path' value='<%= path %>' />

    <table align='center' border='0' cellspacing='0' cellpadding='2'>
      <tr><td>
        <fmt:message key="newpageTitle" />
        <input type='text' name='title' />
      </td></tr>
      <tr><td align='center'>
        <input type='checkbox' name='newdir' checked='checked' value='true' />
        <fmt:message key="newpageFolder" />
      </td></tr>
      <tr><th align='center'>
        <input type='submit' value='<fmt:message key="newpageCreate" />' />
        <input type='button' value='<fmt:message key="genericCancel" />'
         onclick='javascript:<%= popup ? "window.close" : "history.back" %>();' />
      </th></tr>
    </table>
  </form>
<% } else { 
  boolean newDir = Utils.isTrue(request.getParameter("newdir"));
  String fileName = WebUtils.fixFileName(title, false).toLowerCase();
  
  if (!newDir) {
    fileName += '.' + webSite.getConfiguration().getVisualExtensions()[0];
  }
  
  fileName = Utils.generateUniqueName(fileName, webSite.getFile(path));
  
  if (fileName == null) {
    %><fmt:message key="newpageError" /><br /><%
  } else {
    path = path.add(fileName);
    
    if (newDir) {
      path = path.add(webSite.getWelcomeFileNames()[0]);
    }
    
    String text = webSite.getHTMLTemplate
        (WebUtils.convertToHTMLEntities(request.getParameter("title")));

    if (webSite.saveToFile(userInfo, text, path, null)) {
      webSite.updateSiteMap(true);
      %><script type="text/javascript">
        var page = "<%= cp + '/' + path + '?' + HitFilter.ACTION_NAME + '=' + HitFilter.ACTION_EDIT %>";
        
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
