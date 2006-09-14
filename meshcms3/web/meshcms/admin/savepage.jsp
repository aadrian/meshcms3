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
  response.setHeader("Content-Type", "text/html; charset=" + webSite.getConfiguration().getPreferredCharset());
  String fullSrc = request.getParameter("fullsrc");
  Path filePath = null;
  String title = null;
  String charset = null;

  if (Utils.isNullOrEmpty(fullSrc)) {
    PageAssembler pa = new PageAssembler();
    pa.setCharset(webSite.getConfiguration().getPreferredCharset());
    Enumeration names = request.getParameterNames();

    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      String value = request.getParameter(name);

      if (name.equals("pagepath")) {
        filePath = new Path(value);
      } else {
        if (name.equals("pagetitle")) {
          value = WebUtils.convertToHTMLEntities(value, true);
          title = value;
        }

        pa.addProperty(name, value);
      }
    }

    fullSrc = pa.getPage();
    charset = pa.getCharset();
  } else {
    filePath = new Path(request.getParameter("pagepath"));
    PageInfo pageInfo = webSite.getSiteMap().getPageInfo(filePath);
    
    if (pageInfo != null) {
      charset = pageInfo.getCharset();
    }
  }

  if (!userInfo.canWrite(webSite, filePath)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }
%>

<html>
<head>
<%= webSite.getAdminMetaThemeTag() %>
<title><fmt:message key="saveTitle" /></title>
</head>

<body>

<%
  if (webSite.saveToFile(userInfo, fullSrc, filePath, charset)) {
    webSite.updateSiteMap(true);
%>
  <p><% if (Utils.isNullOrEmpty(title)) { %>
      <fmt:message key="saveOkNoTitle" />
  <% } else { %>    
      <fmt:message key="saveOk"><fmt:param value="<%= title %>" /></fmt:message>
  <% } %></p>

  <p><fmt:message key="saveContinue"><fmt:param value="<%= request.getHeader("referer") %>" /></fmt:message></p>
<%
    if (!webSite.isSystem(filePath) && FileTypes.isPage(filePath)) {
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
