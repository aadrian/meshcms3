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

<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%
  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
%>

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<html>
<head>
<%= webApp.getAdminMetaThemeTag() %>
<title><fmt:message key="loginTitle" /></title>
</head>

<body>

<%
  String username = Utils.noNull(request.getParameter("username"));
  String password = Utils.noNull(request.getParameter("password"));
  boolean loaded = false;
  
  if (!username.equals("") && !password.equals("")) {
    loaded = userInfo.load(webApp, username, password);
    
    if (!loaded) {
      %><ul><li><fmt:message key="loginError" /></li></ul><%
    }
  }
  
  if (loaded) {
%>
    <fmt:message key="loginOk" />
    <script type="text/javascript">location.replace('index.jsp');</script>
<%
  } else {
    if (!request.isRequestedSessionIdFromCookie()) {
%>
      <p><fmt:message key="loginCookies" /></p>
<%
    }
%>
    <form name="loginform" action="login.jsp" method="POST">
     <table align="center" border="0" cellspacing="1" cellpadding="4">
      <tr>
       <td><fmt:message key="loginUsername" /></td>
       <td><input type="text" name="username" size="12" value="<%= username %>" /></td>
      </tr>

      <tr>
       <td><fmt:message key="loginPassword" /></td>
       <td><input type="password" name="password" size="12" /></td>
      </tr>

      <tr>
       <th colspan="2">
        <input type="submit" value="<fmt:message key="loginSubmit" />" />
       </th>
      </tr>
     </table>
    </form>
<%
  }
%>

</body>
</html>
