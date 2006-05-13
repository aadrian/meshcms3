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
<jsp:useBean id="webSite" scope="request" type="com.cromoteca.meshcms.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  String cp = request.getContextPath();
  response.setHeader("Content-Type", "text/html; charset=" + webSite.getConfiguration().getPreferredCharset());
%>

<html>
<head>
<%= webSite.getAdminMetaThemeTag() %>
<title><fmt:message key="homeTitle" /></title>
</head>

<body>

<div align="right"><%= webSite.helpIcon(cp, Finals.HELP_ANCHOR_CONTROL_PANEL, userInfo) %></div>

<table width="512" align="center" border="0" cellspacing="3" cellpadding="0">
 <tr>
  <th colspan="4"><fmt:message key="homeSite" /></th>
 </tr>

 <tr valign="top">
  <td width="128" align="center">
   <a href="<%= cp %>/"><img src="images/button_sitehome.gif" vspace="12" /><br /><fmt:message key="homePage" /></a>
  </td>

  <td width="128" align="center">
   <% if (userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) { %>
    <a href="filemanager/"><img src="images/button_filemanager.gif" vspace="12" /><br /><fmt:message key="homeFile" /></a>
   <% } else { %>
    &nbsp;
   <% } %>
  </td>

  <td width="128" align="center">
   <% if (userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) { %>
    <a href="<%= cp + '/' + webSite.getAdminPath() %>/refresh.jsp"><img src="images/button_refresh.gif" vspace="12" /><br /><fmt:message key="homeRefresh" /></a>
   <% } else { %>
    &nbsp;
   <% } %>
  </td>

  <td width="128" align="center">
   <% if (userInfo.canDo(UserInfo.CAN_EDIT_PAGES)) { %>
    <a href="<%= cp + '/' + webSite.getAdminPath() %>/editmap1.jsp"><img src="images/button_editmenu.gif" vspace="12" /><br /><fmt:message key="homePages" /></a>
   <% } else { %>
    &nbsp;
   <% } %>
  </td>
 </tr>

 <tr>
  <td colspan="4">&nbsp;</td>
 </tr>

 <tr>
  <th colspan="4"><fmt:message key="homeSystem" /></th>
 </tr>

 <tr valign="top">
  <td align="center">
   <% if (userInfo.isGuest()) { %>
    <a href="<%= cp + '/' + webSite.getAdminPath() %>/login.jsp"><img src="images/button_login.gif" vspace="12" /><br /><fmt:message key="homeLogin" /></a>
   <% } else { %>
    <a href="<%= cp + '/' + webSite.getAdminPath() %>/logout.jsp"><img src="images/button_logout.gif" vspace="12" /><br /><fmt:message key="homeLogout" /></a>
   <% } %>
  </td>

  <td align="center">
   <% if (!userInfo.isGuest()) { %>
    <a href="<%= cp + '/' + webSite.getAdminPath() %>/edituser1.jsp?username=<%= userInfo.getUsername() %>"><img src="images/button_profile.gif" vspace="12" /><br /><fmt:message key="homeProfile" /></a>
   <% } else { %>
    &nbsp;
   <% } %>
  </td>

  <td align="center">
   <% if (userInfo.canDo(UserInfo.CAN_ADD_USERS)) { %>
    <a href="<%= cp + '/' + webSite.getAdminPath() %>/edituser1.jsp"><img src="images/button_adduser.gif" vspace="12" /><br /><fmt:message key="homeUser" /></a>
   <% } else { %>
    &nbsp;
   <% } %>
  </td>

  <td align="center">
   <% if (userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS)) { %>
    <a href="<%= cp + '/' + webSite.getAdminPath() %>/editconfig1.jsp"><img src="images/button_configure.gif" vspace="12" /><br /><fmt:message key="homeConfigure" /></a>
   <% } else { %>
    &nbsp;
   <% } %>
  </td>
 </tr>
</table>

</body>
</html>
