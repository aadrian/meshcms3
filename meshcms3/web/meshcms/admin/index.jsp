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

<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="org.meshcms.webui.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<%
  String cp = request.getContextPath();
%>

<html>
<head>
<%= webSite.getAdminMetaThemeTag() %>
<title><fmt:message key="homeTitle" /></title>
</head>

<body>

<div align="right"><%= Help.icon(webSite, cp, Help.CONTROL_PANEL, userInfo) %></div>

<%
  if (userInfo.isGlobal()) {
%>
  <p style="text-align: center;"><fmt:message key="homeGlobal" /></p>
<%
  }
%>

<fieldset>
  <legend><fmt:message key="homeSite" /></legend>
  
  <a href="<%= cp + webSite.getLink(Path.ROOT) %>" class="meshcmspanelicon"><img src="images/button_sitehome.gif" alt="" />
  <fmt:message key="homePage" /></a>

   <% if (userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) { %>
  <a href="refresh.jsp" class="meshcmspanelicon"><img src="images/button_refresh.gif" alt="" />
  <fmt:message key="homeRefresh" /></a>
   <% } %>

   <% if (userInfo.canDo(UserInfo.CAN_EDIT_PAGES)) { %>
  <a href="editmap1.jsp" class="meshcmspanelicon"><img src="images/button_editmenu.gif" alt="" />
  <fmt:message key="homePages" /></a>
   <% } %>

   <% if (userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS)) { %>
  <a href="editconfig1.jsp" class="meshcmspanelicon"><img src="images/button_configure.gif" alt="" />
  <fmt:message key="homeConfigure" /></a>
   <% } %>
</fieldset>

<fieldset>
  <legend><fmt:message key="homeUsers" /></legend>
  
   <% if (userInfo.isGuest()) { %>
  <a href="login.jsp" class="meshcmspanelicon"><img src="images/button_login.gif" alt="" />
  <fmt:message key="homeLogin" /></a>
   <% } else { %>
  <a href="logout.jsp" class="meshcmspanelicon"><img src="images/button_logout.gif" alt="" />
  <fmt:message key="homeLogout" /></a>
   <% } %>
  </td>

   <% if (!userInfo.isGuest()) { %>
  <a href="edituser1.jsp?username=<%= userInfo.getUsername() %>" class="meshcmspanelicon"><img src="images/button_profile.gif" alt="" />
  <fmt:message key="homeProfile" /></a>
   <% } %>

   <% if (userInfo.canDo(UserInfo.CAN_ADD_USERS)) { %>
  <a href="edituser1.jsp" class="meshcmspanelicon"><img src="images/button_adduser.gif" alt="" />
  <fmt:message key="homeUser" /></a>
   <% } %>
</fieldset>

 <% if (!userInfo.isGuest()) { %>
<fieldset>
  <legend><fmt:message key="homeSystem" /></legend>
  
   <% if (userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) { %>
  <a href="filemanager/index.jsp" class="meshcmspanelicon"><img src="images/button_filemanager.gif" alt="" />
  <fmt:message key="homeFile" /></a>
   <% } %>
  </td>

   <% if (userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) { %>
  <a href="staticexport1.jsp" class="meshcmspanelicon"><img src="images/button_export.gif" alt="" />
  <fmt:message key="homeExport" /></a>
   <% } %>

   <% if (!webSite.isVirtual() && userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS)) { %>
  <a href="editsites1.jsp" class="meshcmspanelicon"><img src="images/button_sites.gif" alt="" />
  <fmt:message key="homeSites" /></a>
   <% } %>
</fieldset>
 <% } %>

</body>
</html>
