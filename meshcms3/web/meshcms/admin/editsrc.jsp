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
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<%
  String cp = request.getContextPath();
  Path pagePath = new Path(Utils.decodeURL(request.getParameter("path")));

  if (!userInfo.canWrite(webSite, pagePath)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webSite.getConfiguration().getPreferredCharset());
  File file = webSite.getFile(pagePath);
  PageInfo pageInfo = webSite.getSiteMap().getPageInfo(pagePath);
  String charset = null;
  
  if (pageInfo != null) {
    charset = pageInfo.getCharset();
  }
  
  String full = Utils.readFully(file, charset);
  session.setAttribute("MeshCMSNowEditing", pagePath);
%>

<html>
<head>
<%= webSite.getAdminMetaThemeTag() %>
<title><fmt:message key="srcTitle" /></title>
</head>

<body>

<form action="savepage.jsp" method="post" id="srceditor" name="srceditor">
  <input type="hidden" name="pagepath" value="<%= pagePath %>" />

  <div class="meshcmseditor">
    <div class="meshcmstitle">
      <fmt:message key="srcEditing" />
      <a href="<%= cp + '/' + pagePath %>"><%= pagePath.getLastElement() %></a>
    </div>

    <div class="meshcmsfield">
      <textarea style="width: 100%; height: 25em;" id="fullsrc" name="fullsrc"><%= Utils.encodeHTML(full) %></textarea>
    </div>

    <div class="meshcmsfield" align="center">
      <input type="submit" value="<fmt:message key="genericSave" />">
    </div>
  </div>
</form>

</body>
</html>
