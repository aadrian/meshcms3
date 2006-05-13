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
<jsp:useBean id="webSite" scope="request" type="com.cromoteca.meshcms.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  ResourceBundle bundle = WebUtils.getPageResourceBundle(pageContext);
  String edituser = Utils.noNull(request.getParameter("username"));
  String username = userInfo.getUsername();

  List errMsgs = new ArrayList();
  boolean error = false;
  boolean newUser = false;
  UserInfo edit = null;

  if (username.equals("")) {
    error = true;
  } else if (username.equals(edituser)) {
    edit = userInfo;
  } else if (edituser.equals("")) {
    error = true;
  } else if (userInfo.canDo(UserInfo.CAN_ADD_USERS)) {
    newUser = true;
    edit = new UserInfo();
    edit.setUsername(edituser);

    if (userInfo.exists(webSite, edituser)) {
      errMsgs.add(bundle.getString("userExists"));
    }
  } else {
    error = true;
  }

  if (error) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webSite.getConfiguration().getPreferredCharset());

  try {
    edit.setPermissions(Integer.parseInt(request.getParameter("permissions")));
  } catch (Exception ex) {
    errMsgs.add(bundle.getString("userWrongPerm"));
  }

  String p1 = request.getParameter("password1").trim();
  String p2 = request.getParameter("password2").trim();

  if (!p1.equals("") || !p2.equals("")) {
    if (p1.equals(p2)) {
      edit.setPassword(p1);
    } else {
      errMsgs.add("Passwords don't match");
    }
  }

  String email = request.getParameter("email");

  if (!Utils.isNullOrEmpty(email)) {
    email = email.trim();

    if (Utils.checkAddress(email)) {
      edit.setEmail(email);
    } else {
      errMsgs.add(bundle.getString("userWrongMail"));
    }
  }

  if (newUser) {
    Path hp = new Path(request.getParameter("homepath"));
    File hf = webSite.getFile(hp);
    hf.mkdirs();

    if (hf.isDirectory()) {
      edit.setHomePath(hp);
    } else {
      errMsgs.add(bundle.getString("userWrongHome"));
    }
  }
  
  edit.setPreferredLocaleCode(request.getParameter("language"));

  Enumeration ps = request.getParameterNames();

  while(ps.hasMoreElements()) {
    String n = ps.nextElement().toString();
    edit.setDetail(n, request.getParameter(n));
  }

  if (errMsgs.size() == 0) {
    if (!edit.save(webSite)) {
      errMsgs.add(bundle.getString("userFileError"));
    }
  }
%>

<html>
<head>
<%= webSite.getAdminMetaThemeTag() %>
  <title><fmt:message key="<%= newUser ? "userNew" : "userEdit" %>" /></title>
</head>

<body>

<%
  if (errMsgs.size() == 0) {
%>
    <fmt:message key="userOk" >
      <fmt:param value="<%= edituser %>" />
    </fmt:message>
    <script type="text/javascript">location.replace('index.jsp');</script>
<%
  } else {
%>
    Some errors have occurred:
    <ul>
<%
    for (int i = 0; i < errMsgs.size(); i++) {
%>
      <li><%= errMsgs.get(i) %></li>
<%
    }
%>
    </ul>
    <a href="javascript:history.back();">Try again</a>
<%
  }
%>
</body>
</html>
