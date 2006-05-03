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
<%@ page import="java.util.zip.*" %>
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
%>

<html>
<head>
 <%= webApp.getDummyMetaThemeTag() %>
 <title><fmt:message key="fmUnzipTitle" /></title>
 <link href="../theme/main.css" type="text/css" rel="stylesheet" />

 <style type="text/css">
  td { white-space: nowrap; }
  body { margin: 0px; overflow: scroll; }
 </style>
</head>

<body>

<%
  Path zipPath = new Path(request.getParameter("zip"));
%>


<table width="100%" border="0" cellspacing="0" cellpadding="3">
 <tr>
  <th align="left"><fmt:message key="fmUnzipList">
    <fmt:param value="<%= zipPath.getLastElement() %>" />
  </fmt:message></th>
  <th align="right"><%= webApp.helpIcon(request.getContextPath(),
      Finals.HELP_ANCHOR_UNZIP, userInfo) %></th>
 </tr>

<%
  File zipFile = webApp.getFile(zipPath);
  InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
  ZipInputStream zin = new ZipInputStream(in);
  ZipEntry e;
  int count = 0;

  while ((e = zin.getNextEntry()) != null) {
    count++;
    String name = e.getName();
    String icon;

    if (e.isDirectory()) {
      icon = FileTypes.DIR_ICON;
    } else {
      icon = webApp.getFileTypes().getIconFile(name);
    }
%>
 <tr>
  <td colspan="2"><img class="icon" src="images/<%= icon %>"> <%= name %></td>
 </tr>
<%
  }

  zin.close();
  
  if (count == 0) {
%>
 <tr>
  <td colspan="2"><fmt:message key="fmUnzipNoFiles" /></td>
 </tr>
<%
  }
%>

</table>

<form name="where" action="unzip2.jsp">
<table width="100%" border="0" cellspacing="0" cellpadding="2">
 <tr>
  <th align="left">
   <input type="checkbox" name="createdir" value="true" style="border: none;"
    onclick="javascript:document.forms['where'].dirname.disabled=!this.checked;" />
   <fmt:message key="fmUnzipNewDir" />
   <input type="text" name="dirname" disabled="true"
    value="<%= Utils.removeExtension(zipPath.getLastElement()) %>" />
   <input type="hidden" name="zippath" value="<%= zipPath %>" />
   <input type="button" value="<fmt:message key="fmUnzipButton" />" <%= count == 0 ? "disabled=\"true\"" : ""%>
    onclick="javascript:window.parent.fm_doUnzip(document.forms['where']);" />
   <input type="button" value="<fmt:message key="genericCancel" />" onclick="javascript:history.back();" />
  </th>
 </tr>
</table>
</form>

<p><fmt:message key="fmUnzipWarn" /></p>

</body>
</html>
