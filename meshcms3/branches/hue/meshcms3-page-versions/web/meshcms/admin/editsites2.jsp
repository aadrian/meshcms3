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

<%
  if (webSite.isVirtual() || !userInfo.canDo(org.meshcms.core.UserInfo.CAN_BROWSE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have enough privileges");
    return;
  }
  
  response.setHeader("Content-Type", "text/html; charset=" + webSite.getConfiguration().getPreferredCharset());
%>

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<html>
<head>
<%= webSite.getAdminMetaThemeTag() %>
<title><fmt:message key="sitesTitle" /></title>
</head>

<body>

<%
  String newSiteDir = Utils.noNull(request.getParameter("newsite_dirname")).trim();
  
  if (!newSiteDir.equals("")) {
    File siteDirFile = webSite.getFile(webSite.getVirtualSitesPath().add(newSiteDir));
    
    if (siteDirFile.exists()) {
      newSiteDir = "";
    } else {
      siteDirFile.mkdirs();

      if (Utils.isTrue(request.getParameter("newsite_cms"))) {
        File cmsDir = new File(siteDirFile, "meshcms");
        cmsDir.mkdir();
        Utils.copyFile(webSite.getFile(webSite.getAdminPath().add(WebSite.ADMIN_ID_FILE)),
            new File(cmsDir, WebSite.CMS_ID_FILE), false, false);
      }

      if (!(webSite instanceof MainWebSite)) {
        webSite = HitFilter.getRootSite(application, true);
      }
    }
  }
  
  if (webSite instanceof MainWebSite) {
    MainWebSite mainWebSite = (MainWebSite) webSite;
    MultiSiteManager msm = mainWebSite.getMultiSiteManager();
    
    msm.setUseDirsAsDomains(Utils.isTrue(request.getParameter("useDirsAsDomains")));
    msm.setManageTripleWs(Utils.isTrue(request.getParameter("manageTripleWs")));
    msm.setMainWebSiteDomains(request.getParameter("mainWebSiteDomains"));
    String[] dirs = mainWebSite.getFile(mainWebSite.getVirtualSitesPath()).list();

    for (int i = 0; i < dirs.length; i++) {
      msm.setDomains(dirs[i], request.getParameter("aliases_" + dirs[i]));
    }
    
    if (!newSiteDir.equals("")) {
      msm.setDomains(newSiteDir, request.getParameter("newsite_aliases"));
    }
    
    msm.initDomainsMap();
    
    if (msm.store(mainWebSite)) {
%>
    <fmt:message key="sitesSaveOk" />
    <script type="text/javascript">location.replace('index.jsp');</script>
<%
    } else {
%>
    <fmt:message key="sitesError" />
    <a href="editsites1.jsp"><fmt:message key="genericRetry" /></a>.
<%
    }
  }
%>

</body>
</html>
