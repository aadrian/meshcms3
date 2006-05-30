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

<%@ page import="java.util.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<%
  String edituser = Utils.noNull(request.getParameter("username"));
  String username = userInfo.getUsername();

  boolean error = false;
  boolean newUser = false;
  UserInfo edit = null;

  if (username.equals("")) {
    error = true;
  } else if (username.equals(edituser)) {
    edit = userInfo;
  } else if (userInfo.canDo(UserInfo.CAN_ADD_USERS)) {
    if (userInfo.exists(webSite, edituser)) {
      error = true;
    } else {
      newUser = true;
      edit = new UserInfo();
      edit.setUsername(edituser);
      edit.setPreferredLocaleCode(userInfo.getPreferredLocaleCode());
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
  String imagesPath = request.getContextPath() + '/' + webSite.getAdminPath() + "/images";
%>

<html>
<head>
  <%= webSite.getAdminMetaThemeTag() %>
  <title><fmt:message key="<%= newUser ? "userNew" : "userEdit" %>" /></title>

  <script type="text/javascript">
    function toggleDetails() {
      var mini = document.getElementById('toggledetails');

      if (mini.src.indexOf("tree_plus") < 0) {
        mini.src = "<%= imagesPath %>/tree_plus.gif";

        for (var i = 0; i < <%= UserInfo.DETAILS.length %>; i++) {
          document.getElementById("userdetail" + i).style.display = "none";
        }
      } else {
        mini.src = "<%= imagesPath %>/tree_minus.gif";

        for (var i = 0; i < <%= UserInfo.DETAILS.length %>; i++) {
          document.getElementById("userdetail" + i).style.display = "";
        }
      }
    }
  </script>
</head>

<body>
<div align="right"><%= webSite.helpIcon(request.getContextPath(),
    newUser ? WebSite.HELP_ANCHOR_NEW_USER : WebSite.HELP_ANCHOR_EDIT_PROFILE, userInfo) %></div>

<form action="edituser2.jsp" method="post">
 <table class='meshcmseditor' cellspacing='0'>
  <tr>
   <th colspan="2"><fmt:message key="<%= newUser ? "userNew" : "userEdit" %>" />:</th>
  </tr>

  <tr>
   <td align="right"><fmt:message key="loginUsername" /></td>
    <% if (newUser) { %>
     <td><input type="text" name="username" size="30" /></td>
    <% } else { %>
     <td><%= edit.getUsername() %><input type="hidden" name="username"
       value="<%= edit.getUsername() %>" /></td>
    <% } %>
  </tr>

  <tr>
   <td align="right"><fmt:message key="userType" /></td>
    <% if (newUser) { %>
     <td><select name="permissions">
      <option value="<%= UserInfo.EDITOR %>"><fmt:message key="userEditor" /></option>
      <option value="<%= UserInfo.ADMIN %>"><fmt:message key="userAdmin" /></option>
      <option value="<%= UserInfo.MEMBER %>"><fmt:message key="userMember" /></option>
     </select></td>
    <% } else {
      int perm = edit.getPermissions();
      String type;
      ResourceBundle bundle = WebUtils.getPageResourceBundle(pageContext);

      if (perm == UserInfo.ADMIN) {
        type = bundle.getString("userAdmin");
      } else if (perm == UserInfo.MEMBER) {
        type = bundle.getString("userMember");
      } else if (perm == UserInfo.EDITOR) {
        type = bundle.getString("userEditor");
      } else if (perm == UserInfo.GUEST) {
        type = bundle.getString("userGuest");
      } else {
        type = bundle.getString("userCustom");
      }
      %><td><%= type %><input type="hidden" name="permissions" value="<%= perm %>" /></td>
    <% } %>
  </tr>

  <tr>
   <td align="right"><fmt:message key="userHome" /></td>
    <% String hPath = "/" + edit.getHomePath();

    if (newUser) { %>
     <td><input type="text" name="homepath" size="30" value="<%= hPath %>" /></td>
    <% } else { %>
     <td><%= hPath %><input type="hidden" name="homepath" value="<%= hPath %>" /></td>
    <% } %>
  </tr>

  <tr>
   <td align="right"><fmt:message key="userMail" /></td>
   <td><input type="text" name="email" size="30"
        value="<%= edit.getEmail() %>" /></td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="userLanguage" /></td>
   <td><select name="language"><% Locale[] locales = Locale.getAvailableLocales();
     Arrays.sort(locales, new LocaleComparator(Locale.ENGLISH));

     for (int i = 0; i < locales.length; i++) { %>
       <option value="<%= locales[i] %>"<%= locales[i].toString().equals(edit.getPreferredLocaleCode()) ?
         " selected='selected'" : "" %>><%= locales[i].getDisplayName(Locale.ENGLISH) %></option><%
     } %>
   </select></td>
  </tr>

  <tr>
   <th colspan="2">
    <% if (newUser) { %>
      <fmt:message key="userInitPwd" />
    <% } else { %>
      <fmt:message key="userChangePwd" />
    <% } %>
   </th>
  </tr>

  <tr>
   <td align="right"><fmt:message key="loginPassword" /></td>
   <td><input type="password" name="password1" size="30" /></td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="userConfirmPwd" /></td>
   <td><input type="password" name="password2" size="30" /></td>
  </tr>

  <tr>
   <th colspan="2">
    <img src="<%= imagesPath %>/tree_plus.gif" alt=""
     id='toggledetails' onclick='javascript:toggleDetails();' />
    <fmt:message key="userOpt" />
   </th>
  </tr>

<%
  for (int i = 0; i < UserInfo.DETAILS.length; i++) {
%>
  <tr id="userdetail<%= i %>" style="display: none;">
   <td align="right"><fmt:message key="<%= "user_" + UserInfo.DETAILS[i] %>" />:</td>
   <td><input type="text" name="<%= UserInfo.DETAILS[i] %>"
    value="<%= edit.getValue(UserInfo.DETAILS[i]) %>" size="30" /></td>
  </tr>
<%
  }
%>

  <tr>
   <th colspan="2">
    <input type="submit" value="<fmt:message key="genericSave" />" />
   </th>
  </tr>
 </table>
</form>

</body>
</html>
