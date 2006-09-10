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

<%@ page import="java.nio.charset.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="org.meshcms.webui.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webSite.getConfiguration().getPreferredCharset());
  Locale locale = WebUtils.getPageLocale(pageContext);
  Configuration configuration = webSite.getConfiguration();
  int cacheType = configuration.getCacheType();
  String cp = request.getContextPath();
%>

<html>
<head>
<%= webSite.getAdminMetaThemeTag() %>
<title><fmt:message key="configTitle" /></title>

<script language="javascript" type="text/javascript">
 var contextPath = "<%= cp %>";
 var adminPath = "<%= webSite.getAdminPath() %>";
</script>
<script language="javascript" type="text/javascript" src="scripts/editor.js"></script>
</head>

<body>

<div align="right"><%= Help.icon(webSite, cp, Help.CONFIGURE, userInfo) %></div>

<form action="editconfig2.jsp" method="post">
  <fieldset class="meshcmseditor">
    <legend><fmt:message key="configSiteInfo" /></legend>

    <div class="meshcmsfieldlabel">
      <label for="siteName"><fmt:message key="configSiteName" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('siteName');" alt=""
       style="vertical-align:middle;" /><input type="text" id="siteName" name="siteName"
       style="width: 90%;" value="<%= Utils.noNull(configuration.getSiteName()) %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="siteHost"><fmt:message key="configSiteHost" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('siteHost');" alt=""
       style="vertical-align:middle;" /><input type="text" id="siteHost" name="siteHost"
       style="width: 90%;" value="<%= Utils.noNull(configuration.getSiteHost()) %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="siteDescription"><fmt:message key="configSiteDescription" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('siteDescription');" alt=""
       style="vertical-align:middle;" /><input type="text" id="siteDescription" name="siteDescription"
       style="width: 90%;" value="<%= Utils.noNull(configuration.getSiteDescription()) %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="siteKeywords"><fmt:message key="configSiteKeywords" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('siteKeywords');" alt=""
       style="vertical-align:middle;" /><input type="text" id="siteKeywords" name="siteKeywords"
       style="width: 90%;" value="<%= Utils.noNull(configuration.getSiteKeywords()) %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="siteAuthor"><fmt:message key="configSiteAuthor" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('siteAuthor');" alt=""
       style="vertical-align:middle;" /><input type="text" id="siteAuthor" name="siteAuthor"
       style="width: 90%;" value="<%= Utils.noNull(configuration.getSiteAuthor()) %>" />
    </div>
    
    <div class="meshcmsfieldlabel">
      <label for="siteAuthorURL"><fmt:message key="configSiteAuthorURL" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('siteAuthorURL');" alt=""
       style="vertical-align:middle;" /><input type="text" id="siteAuthorURL" name="siteAuthorURL"
       style="width: 90%;" value="<%= Utils.noNull(configuration.getSiteAuthorURL()) %>" />
    </div>
  </fieldset>
  
  <fieldset class="meshcmseditor">
    <legend><fmt:message key="configInterface" /></legend>

    <div class="meshcmsfieldlabel">
      <label for="visualTypes"><fmt:message key="configVisual" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('visualTypes');" alt=""
       style="vertical-align:middle;" /><input type="text" id="visualTypes" name="visualTypes"
       style="width: 90%;" value="<%= Utils.noNull(Utils.generateList(configuration.getVisualExtensions(), ", ")) %>" />
    </div>

    <div class="meshcmscheckbox">
      <input type="checkbox" id="useAdminTheme" name="useAdminTheme"
       value="true"<%= configuration.isUseAdminTheme() ? " checked='checked'" : "" %> />
      <label for="useAdminTheme"><fmt:message key="configSysTheme" /></label>
    </div>

    <div class="meshcmscheckbox">
      <input type="checkbox" id="preventHotlinking" name="preventHotlinking"
       value="true"<%= configuration.isPreventHotlinking() ? " checked='checked'" : "" %> />
      <label for="preventHotlinking"><fmt:message key="configHotlinking" /></label>
    </div>
  </fieldset>
  
  <fieldset class="meshcmseditor">
    <legend><fmt:message key="configMailParams" /></legend>

    <div class="meshcmsfieldlabel">
      <label for="mailServer"><fmt:message key="configMail" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('mailServer');" alt=""
       style="vertical-align:middle;" /><input type="text" id="mailServer" name="mailServer"
       style="width: 90%;" value="<%= configuration.getMailServer() %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="smtpUsername"><fmt:message key="configSmtpUsername" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('smtpUsername');" alt=""
       style="vertical-align:middle;" /><input type="text" id="smtpUsername" name="smtpUsername"
       style="width: 90%;" value="<%= configuration.getSmtpUsername() %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="smtpPassword"><fmt:message key="configSmtpPassword" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('smtpPassword');" alt=""
       style="vertical-align:middle;" /><input type="text" id="smtpPassword" name="smtpPassword"
       style="width: 90%;" value="<%= configuration.getSmtpPassword() %>" />
    </div>
  </fieldset>
  
  <fieldset class="meshcmseditor">
    <legend><fmt:message key="configSystem" /></legend>

    <div class="meshcmsfieldlabel">
      <label for="cacheType"><fmt:message key="configCache" /></label>
    </div>
    
    <div class="meshcmsfield">
      <select name="cacheType" id="cacheType">
       <option value="<%= Configuration.NO_CACHE %>"
        <%= cacheType == Configuration.NO_CACHE ? " selected='selected'" : "" %>><fmt:message key="configCacheNone" /></option>
       <option value="<%= Configuration.IN_MEMORY_CACHE %>"
        <%= cacheType == Configuration.IN_MEMORY_CACHE ? " selected='selected'" : "" %>><fmt:message key="configCacheMemory" /></option>
       <option value="<%= Configuration.ON_DISK_CACHE %>"
        <%= cacheType == Configuration.ON_DISK_CACHE ? " selected='selected'" : "" %>><fmt:message key="configCacheDisk" /></option>
      </select>
    </div>

    <div class="meshcmsfieldlabel">
      <label for="updateInterval"><fmt:message key="configMap" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('updateInterval');" alt=""
       style="vertical-align:middle;" /><input type="text" id="updateInterval" name="updateInterval"
       style="width: 90%;" value="<%= configuration.getUpdateInterval() %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="backupLife"><fmt:message key="configBackup" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('backupLife');" alt=""
       style="vertical-align:middle;" /><input type="text" id="backupLife" name="backupLife"
       style="width: 90%;" value="<%= configuration.getBackupLife() %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="statsLength"><fmt:message key="configHits" /></label>
    </div>
    
    <div class="meshcmsfield">
      <img src="images/clear_field.gif" onclick="javascript:editor_clr('statsLength');" alt=""
       style="vertical-align:middle;" /><input type="text" id="statsLength" name="statsLength"
       style="width: 90%;" value="<%= configuration.getStatsLength() %>" />
    </div>

    <div class="meshcmsfieldlabel">
      <label for="preferredCharset"><fmt:message key="configCharset" /></label>
    </div>
    
    <div class="meshcmsfield">
      <select name="preferredCharset" id="preferredCharset"><%
       Charset currentCharset = Charset.forName(configuration.getPreferredCharset());
       SortedMap encMap = Charset.availableCharsets();
       Iterator iter = encMap.keySet().iterator();
       String encName;
       Charset enc;

       while (iter.hasNext()) { 
         encName = (String) iter.next();
         enc = (Charset) encMap.get(encName); %>
         <option value="<%= encName %>"<%= currentCharset.equals(enc) ?
           " selected='selected'" : "" %>><%= enc.displayName(locale) %></option><%
       } %></select>
    </div>

    <div class="meshcmscheckbox">
      <input type="checkbox" id="alwaysRedirectWelcomes" name="alwaysRedirectWelcomes"
       value="true"<%= configuration.isAlwaysRedirectWelcomes() ? " checked='checked'" : "" %> />
      <label for="alwaysRedirectWelcomes"><fmt:message key="configAlwaysRedirect" /></label>
    </div>

    <div class="meshcmscheckbox">
      <input type="checkbox" id="alwaysDenyDirectoryListings" name="alwaysDenyDirectoryListings"
       value="true"<%= configuration.isAlwaysDenyDirectoryListings() ? " checked='checked'" : "" %> />
      <label for="alwaysDenyDirectoryListings"><fmt:message key="configAlwaysDenyDirList" /></label>
    </div>

    <div class="meshcmscheckbox">
      <input type="checkbox" id="hideExceptions" name="hideExceptions"
       value="true"<%= configuration.isHideExceptions() ? " checked='checked'" : "" %> />
      <label for="hideExceptions"><fmt:message key="configHideExceptions" /></label>
    </div>

    <div class="meshcmscheckbox">
      <input type="checkbox" id="editorModulesCollapsed" name="editorModulesCollapsed"
       value="true"<%= configuration.isEditorModulesCollapsed() ? " checked='checked'" : "" %> />
      <label for="editorModulesCollapsed"><fmt:message key="configEditorModulesCollapsed" /></label>
    </div>
  </fieldset>

  <div class="meshcmsbuttons">
    <input type="submit" value="<fmt:message key="genericSave" />" />
  </div>
</form>

</body>
</html>
