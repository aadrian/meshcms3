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

<%
  if (!userInfo.canDo(org.meshcms.core.UserInfo.CAN_BROWSE_FILES)) {
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
<script language="javascript" type="text/javascript">
 var contextPath = "<%= request.getContextPath() %>";
 var adminPath = "<%= webSite.getAdminPath() %>";
</script>
<script language="javascript" type="text/javascript" src="scripts/editor.js"></script>
</head>

<body>

<%
  MultiSiteManager msm = null;
  
  if (webSite instanceof MainWebSite) {
    msm = ((MainWebSite) webSite).getMultiSiteManager();
  }
%>
<form action="editsites2.jsp" method="POST">
  
<% if (msm != null) { %>
  <fieldset>
    <legend><fmt:message key="sitesGeneral" /></legend>

    <div>
      <input type="checkbox" id="useDirsAsDomains" name="useDirsAsDomains"
       value="true"<%= msm.isUseDirsAsDomains() ? " checked='checked'" : "" %> />
      <label for="useDirsAsDomains"><fmt:message key="sitesDirsAsDomains" /></label>
    </div>

    <div>
      <input type="checkbox" id="manageTripleWs" name="manageTripleWs"
       value="true"<%= msm.isManageTripleWs() ? " checked='checked'" : "" %> />
      <label for="manageTripleWs"><fmt:message key="sitesManageTripleWs" /></label>
    </div>

    <hr />

    <div>
      <label for="mainWebSiteDomains"><fmt:message key="sitesMainWebSiteDomains" /></label><br />
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('mainWebSiteDomains');" alt=""
       style="vertical-align:middle;" /><input type="text" id="mainWebSiteDomains" name="mainWebSiteDomains" size="60"
       value="<%= Utils.noNull(msm.getMainWebSiteDomains()) %>" />
    </div>
  </fieldset>
<% } else { %>
  <input type="hidden" name="useDirsAsDomains" value="true" />
  <input type="hidden" name="manageTripleWs" value="true" />
<% } %>

  <fieldset>
    <legend><fmt:message key="sitesList" /></legend>

    <table class="meshcmseditor" cellspacing="0">
      <tr>
        <th><fmt:message key="sitesHeaderName" /></th>
        <th><fmt:message key="sitesHeaderAliases" /></th>
        <th><fmt:message key="sitesHeaderCMS" /></th>
      </tr>
<%
  if (msm != null) {
    String[] dirs = webSite.getFile(webSite.getVirtualSitesPath()).list();
    
    for (int i = 0; i < dirs.length; i++) {
%>
      <tr>
        <td><label for="aliases_<%= dirs[i] %>"><%= dirs[i] %></label></td>
        <td>
          <img src="images/clear_field.gif" onclick="javascript:editor_clr('aliases_<%= dirs[i] %>');" alt=""
           style="vertical-align:middle;" /><input type="text" id="aliases_<%= dirs[i] %>"
           name="aliases_<%= dirs[i] %>" size="40"
           value="<%= Utils.noNull(msm.getDomains(dirs[i])) %>" />
        </td>
        <td align="center">
        <% if (((MainWebSite) webSite).getVirtualSite(dirs[i]).getCMSPath() != null) { %>
          <img src="filemanager/images/button_yes.gif" alt=""
           style='vertical-align:middle;' title="<fmt:message key="genericYes" />" />
        <% } else { %>
          &nbsp;
        <% } %>
        </td>
      </tr>
<%
    }
  }
%>
      <tr>
        <td colspan="3" align="center"><fmt:message key="sitesNew" /></td>
      </tr>
      <tr>
        <td>
          <img src="images/clear_field.gif" onclick="javascript:editor_clr('newsite_dirname');" alt=""
           style="vertical-align:middle;" /><input type="text" id="newsite_dirname"
           name="newsite_dirname" size="15" />
        </td>
        <td>
          <img src="images/clear_field.gif" onclick="javascript:editor_clr('newsite_aliases');" alt=""
           style="vertical-align:middle;" /><input type="text" id="newsite_aliases"
           name="newsite_aliases" size="40" />
        </td>
        <td align="center">
          <input type="checkbox" id="newsite_cms" name="newsite_cms" value="true"
           checked="checked" />
        </td>
      </tr>

    </table>
  </fieldset>

  <div align="center">
    <input type="submit" value="<fmt:message key="genericUpdate" />" />
  </div>
</form>

</body>
</html>
