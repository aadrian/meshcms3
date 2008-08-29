<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2008 Luciano Vernaschi

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
<%@ page import="org.meshcms.webui.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%
  if (!userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have enough privileges");
    return;
  }

  MainWebSite mainWebSite = null;
  MultiSiteManager siteManager = null;

  if (webSite instanceof MainWebSite) {
    mainWebSite = (MainWebSite) webSite;
  } else if (webSite instanceof VirtualWebSite) {
    mainWebSite = ((VirtualWebSite) webSite).getMainWebSite();
  }
  
  if (mainWebSite != null) {
    siteManager = mainWebSite.getMultiSiteManager();
  }
%>

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<html>
<head>
<%= webSite.getAdminMetaThemeTag() %>
<title><fmt:message key="syncTitle" /></title>
</head>

<body>

<div align="right"><%= Help.icon(webSite, request.getContextPath(),
    Help.SITE_SYNC, userInfo) %></div>

<form action="syncsite2.jsp" method="post">
  <fieldset class="meshcmseditor">
    <legend><fmt:message key="syncChooseSite" /></legend>
    
    <div class="meshcmsfieldlabel">
      <label for="targetSite"><fmt:message key="syncTargetSite" /></label>
    </div>
    
    <div class="meshcmsfield">
      <select name="targetSite">
<%
  if (mainWebSite != null) {
    String[] dirs = mainWebSite.getFile(mainWebSite.getVirtualSitesPath()).list();
    Arrays.sort(dirs);

    for (int i = 0; i < dirs.length; i++) {
      WebSite ws = siteManager.getWebSite(dirs[i]);
      
      if (ws != webSite && ws.getCMSPath() != null) {
%>
        <option value="<%= dirs[i]%>"><%= dirs[i]%></option>
<%
      }
    }
  }
  
  String userName = "";
  
  if (userInfo.isGlobal() ||
      (webSite == mainWebSite && userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS))) {
    userName = userInfo.getUsername();
  }
%>
      </select>
    </div>
  </fieldset>

  <fieldset class="meshcmseditor">
    <legend><fmt:message key="syncCredentials" /></legend>

    <div class="meshcmsfieldlabel">
      <label for="targetUsername">
        <fmt:message key="loginUsername" />
      </label>
    </div>

    <div class="meshcmsfield">
      <input type="text" id="targetUsername" name="targetUsername" value="<%= userName %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="targetPassword">
        <fmt:message key="loginPassword" />
      </label>
    </div>

    <div class="meshcmsfield">
      <input type="password" id="targetPassword" name="targetPassword" />
    </div>
  </fieldset>

  <div class="meshcmsbuttons">
    <input type="submit" value="<fmt:message key="syncDo" />" />
  </div>
</form>

</body>
</html>
