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
  if (!userInfo.canDo(UserInfo.CAN_MANAGE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webSite.getConfiguration().getPreferredCharset());
%>

<html>
<head>
  <%= webSite.getDummyMetaThemeTag() %>
  <title><fmt:message key="fmUploadTitle" /></title>
  <link type="text/css" rel="stylesheet" href="../theme/main.css">

  <script type="text/javascript">
    function showWaiting() {
      document.getElementById("subBtn").disabled = true;
      document.getElementById("warningMsg").style.visibility = "hidden";
      document.getElementById("waitMsg").style.visibility = "visible";
      document.getElementById("waitImg").style.visibility = "visible";
    }
  </script>
</head>

<body>
  <p align="right"><%= webSite.helpIcon(request.getContextPath(),
      Finals.HELP_ANCHOR_UPLOAD, userInfo) %></p>

  <form name="upform" action="upload2.jsp" method="POST"
   enctype="multipart/form-data" onsubmit="javascript:showWaiting();">
  <input type="hidden" name="dir" /> 
    <table align="center" border="0" cellspacing="10" cellpadding="2">
      <tr>
        <td><fmt:message key="fmUploadHint" /></td>
      </tr>

      <tr>
        <td><input type="file" name="upfile" size="25"></td>
      </tr>

      <tr>
        <th>
          <input type="submit" value="<fmt:message key="fmUploadButton" />" id="subBtn">
          <input type="button" value="<fmt:message key="genericCancel" />" onclick="javascript:history.back();" />
        </th>
      </tr>

      <tr>
        <td align="center">
          <div id="warningMsg"><fmt:message key="fmUploadWarn" /></div>
          <div id="waitMsg" style="visibility:hidden;"><fmt:message key="fmUploadWait" /></div>
          <div id="waitImg" style="visibility:hidden;">
            <img src="images/waiting.gif" vspace="24" alt="<fmt:message key="fmUploadImgAlt" />" />
          </div>
        </td>
      </tr>
    </table>
  </form>
  <script type="text/javascript">
    document.forms['upform'].dir.value = window.parent.document.forms['fmfm'].f_dir.value;
  </script>
</body>
</html>
