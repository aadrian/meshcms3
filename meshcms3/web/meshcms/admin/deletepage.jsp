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
%>

<html>
<head>
 <%= webSite.getDummyMetaThemeTag() %>
 <title><fmt:message key="deletepage" /></title>
 <link href="theme/main.css" type="text/css" rel="stylesheet" />
</head>

<body>

<p>&nbsp;</p>

<p align='center'>
<% Path path = webSite.getSiteMap().getServedPath(new Path(request.getParameter("path")));
boolean deleted = webSite.delete(userInfo, path);

if (deleted) {
  webSite.delete(userInfo, path.getParent());
  webSite.updateSiteMap(true);
  %><script type='text/javascript'>window.opener.location.reload(true);window.close();</script><%
} else {
  %><fmt:message key="deletepageError" /><%
} %>
</p>

<p align='center'><a href="javascript:window.close();"><fmt:message key="genericClose" /></a></p>

</body>
</html>
