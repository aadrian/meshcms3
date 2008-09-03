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
<%@ page import="org.meshcms.extra.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="org.meshcms.webui.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<html>
<head>
<%= webSite.getAdminMetaThemeTag() %>
<title><fmt:message key="homeSync" /></title>
</head>

<body>

<%
  if (!userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have enough privileges");
    return;
  }

  MainWebSite mws = null;
  MultiSiteManager msm = null;

  if (webSite instanceof MainWebSite) {
    mws = ((MainWebSite) webSite);
  } else if (webSite instanceof VirtualWebSite) {
    mws = ((VirtualWebSite) webSite).getMainWebSite();
  }
  
  SiteSynchronizer ss = null;
  
  if (mws != null) {
    msm = mws.getMultiSiteManager();
    
    WebSite targetSite = msm.getWebSite(request.getParameter("targetSite"));
    UserInfo targetUser = new UserInfo();
    
    if (targetUser.load(targetSite, request.getParameter("targetUsername"),
        request.getParameter("targetPassword"))) {
      if (webSite.getCMSPath().equals(targetSite.getCMSPath())) {
        ss = new SiteSynchronizer(webSite, targetSite, targetUser);
        ss.setCopySiteInfo(Utils.isTrue(request.getParameter("copySiteInfo")));
        ss.setCopyConfig(Utils.isTrue(request.getParameter("copyConfig")));
        ss.setUpdateFileTime(Utils.isTrue(request.getParameter("updateFileTime")));
        ss.setWriter(out);
      } else {
%>
<p><fmt:message key="syncCMSPathError" /></p>
<%
      }
    } else {
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have enough privileges to access target site");
      return;
    }
  }
%>


<pre>
<%
  if (ss != null) {
    ss.process();
  }
%>
</pre>

<p><fmt:message key="syncDone" /></p>

</body>
</html>
