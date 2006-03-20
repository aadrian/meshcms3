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
  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
  String fullSrc = request.getParameter("fullsrc");
  Path filePath = null;
  String title = null;
  String charset = null;

  if (Utils.isNullOrEmpty(fullSrc)) {
    PageReconstructor pr = new PageReconstructor();
    pr.setCharset(webApp.getConfiguration().getPreferredCharset());
    Enumeration names = request.getParameterNames();

    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      String value = request.getParameter(name);

      if (name.equals("pagepath")) {
        filePath = new Path(value);
      } else {
        if (name.equals("pagetitle")) {
          // value = Utils.encodeHTML(value);
          title = value;
        }

        pr.addProperty(name, value);
      }
    }

    fullSrc = pr.getPage();
    charset = pr.getCharset();
  } else {
    filePath = new Path(request.getParameter("pagepath"));
    PageInfo pageInfo = webApp.getSiteMap().getPageInfo(filePath);
    
    if (pageInfo != null) {
      charset = pageInfo.getCharset();
    }
  }

  if (!userInfo.canWrite(webApp, filePath)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }
%>

<html>
<head>
<%= webApp.getAdminMetaThemeTag() %>
<title><fmt:message key="saveTitle" /></title>
</head>

<body>

<%
  if (webApp.saveToFile(userInfo, fullSrc, filePath, charset)) {
    webApp.updateSiteMap(true);
%>
  <p><% if (Utils.isNullOrEmpty(title)) { %>
      <fmt:message key="saveOkNoTitle" />
  <% } else { %>    
      <fmt:message key="saveOk"><fmt:param value="<%= title %>" /></fmt:message>
  <% } %></p>

  <p><fmt:message key="saveContinue"><fmt:param value="<%= request.getHeader("referer") %>" /></fmt:message></p>
<%
    if (!webApp.isSystem(filePath) && webApp.getFileTypes().isPage(filePath)) {
%>
  <p><fmt:message key="saveView"><fmt:param value="<%= WebUtils.getPathInContext(request, filePath) %>" /></fmt:message></p>
<%
    }
  } else {
%>
  <p><fmt:message key="saveError" /></p>
<%
  }
%>
</body>
</html>
