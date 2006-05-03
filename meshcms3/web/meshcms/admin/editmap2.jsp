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
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_EDIT_PAGES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
  SiteInfo siteInfo = webApp.getSiteInfo();
  Enumeration enumeration = request.getParameterNames();

  while (enumeration.hasMoreElements()) {
    String pName = (String) enumeration.nextElement();
    siteInfo.setValue(pName, request.getParameter(pName));
  }
  
  webApp.getConfiguration().setUseEntities(Utils.isTrue(request.getParameter("useEntities")));
  webApp.getConfiguration().store(webApp);
  
  webApp.updateSiteMap(true);
%>

<html>
<head>
<%= webApp.getAdminMetaThemeTag() %>
<title><fmt:message key="mapTitle" /></title>
</head>

<body>

<%
  if (siteInfo.store()) {
%>
    <fmt:message key="mapOk" />
    <script type="text/javascript">location.replace('index.jsp');</script>
<%
  } else {
%>
    <fmt:message key="mapError" />
    <a href="editmap1.jsp"><fmt:message key="genericRetry" /></a>.
<%
  }
%>
</body>
</html>
