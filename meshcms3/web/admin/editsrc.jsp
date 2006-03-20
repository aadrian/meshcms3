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
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  String cp = request.getContextPath();
  Path pagePath = new Path(Utils.decodeURL(request.getParameter("path")));

  if (!userInfo.canWrite(webApp, pagePath)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
  File file = webApp.getFile(pagePath);
  PageInfo pageInfo = webApp.getSiteMap().getPageInfo(pagePath);
  String charset = null;
  
  if (pageInfo != null) {
    charset = pageInfo.getCharset();
  }
  
  String full = Utils.readFully(file, charset);
  session.setAttribute("MeshCMSNowEditing", pagePath);
%>

<html>
<head>
<%= webApp.getAdminMetaThemeTag() %>
<title><fmt:message key="srcTitle" /></title>
</head>

<body>

<form action="savepage.jsp" method="POST" id="srceditor" name="srceditor">
 <input type='hidden' name='pagepath' value="<%= pagePath %>" />
<table align="center" border="0" cellspacing="10" cellpadding="2" width="100%">
<tr>
 <th>
  <fmt:message key="srcEditing" />
  <a href="<%= cp + '/' + pagePath %>"><%= pagePath.getLastElement() %></a>
 </th>
</tr>

<tr>
 <td align="center"><textarea rows="25" cols="80"
  id="fullsrc" name="fullsrc"><%= Utils.encodeHTML(full) %></textarea></td>
</tr>

<tr>
 <th><input type="submit" value="<fmt:message key="genericSave" />"></th>
</tr>
</table>
</form>

</body>
</html>
