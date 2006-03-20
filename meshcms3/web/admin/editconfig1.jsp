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
<%@ page import="com.cromoteca.meshcms.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
  Locale locale = WebUtils.getPageLocale(pageContext);
  Configuration configuration = webApp.getConfiguration();
  int cacheType = configuration.getCacheType();
  String cp = request.getContextPath();
%>

<html>
<head>
<%= webApp.getAdminMetaThemeTag() %>
<title><fmt:message key="configTitle" /></title>

<script language="javascript" type="text/javascript">
 var contextPath = "<%= cp %>";
 var adminPath = "<%= webApp.getAdminPath() %>";
</script>
<script language="javascript" type="text/javascript" src="editor.js"></script>
</head>

<body>

<div align="right"><%= webApp.helpIcon(cp, Finals.HELP_ANCHOR_CONFIGURE, userInfo) %></div>

<form action="editconfig2.jsp" method="POST">
 <table class="meshcmseditor" cellspacing="0">
  <tr><th colspan="2"><fmt:message key="configFolders" /></th></tr>

  <tr>
   <td align="right"><fmt:message key="configThemes" /></td>
   <td width="260">
    <img src="images/clear_field.gif" onclick="javascript:editor_clr('themesPath');"
     align="middle" /><input type="text" id="themesPath" name="themesPath" size="30"
     value="<%= configuration.getThemesDir() %>" />
    <img src="tiny_mce/themes/advanced/images/browse.gif" alt="<fmt:message key="genericBrowse" />"
     onclick="javascript:editor_openFileManager('themesPath');" align="middle" /> 
   </td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="configModules" /></td>
   <td>
    <img src="images/clear_field.gif" onclick="javascript:editor_clr('moduleTemplatesPath');"
     align="middle" /><input type="text" id="moduleTemplatesPath" name="moduleTemplatesPath" size="30"
     value="<%= configuration.getModuleTemplatesDir() %>" />
    <img src="tiny_mce/themes/advanced/images/browse.gif" alt="<fmt:message key="genericBrowse" />"
     onclick="javascript:editor_openFileManager('moduleTemplatesPath');" align="middle" /> 
   </td>
  </tr>

  <tr><th colspan="2"><fmt:message key="configInterface" /></th></tr>

  <tr>
   <td align="right"><fmt:message key="configVisual" /></td>
   <td>
    <img src="images/clear_field.gif" onclick="javascript:editor_clr('visualTypes');"
     align="middle" /><input type="text" id="visualTypes" name="visualTypes" size="30"
     value="<%= configuration.getVisualTypes() %>" />
   </td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="configSysTheme" /></td>
   <td>
    <input type="checkbox" id="useAdminTheme" name="useAdminTheme"
     value="true"<%= configuration.isUseAdminTheme() ? " checked='true'" : "" %> />
   </td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="configHotlinking" /></td>
   <td>
    <input type="checkbox" id="preventHotlinking" name="preventHotlinking"
     value="true"<%= configuration.isPreventHotlinking() ? " checked='true'" : "" %> />
   </td>
  </tr>

  <tr><th colspan="2"><fmt:message key="configMailParams" /></th></tr>

  <tr>
   <td align="right"><fmt:message key="configMail" /></td>
   <td>
    <img src="images/clear_field.gif" onclick="javascript:editor_clr('mailServer');"
     align="middle" /><input type="text" id="mailServer" name="mailServer" size="30"
     value="<%= configuration.getMailServer() %>" />
   </td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="configSmtpUsername" /></td>
   <td>
    <img src="images/clear_field.gif" onclick="javascript:editor_clr('smtpUsername');"
     align="middle" /><input type="text" id="smtpUsername" name="smtpUsername" size="30"
     value="<%= configuration.getSmtpUsername() %>" />
   </td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="configSmtpPassword" /></td>
   <td>
    <img src="images/clear_field.gif" onclick="javascript:editor_clr('smtpPassword');"
     align="middle" /><input type="text" id="smtpPassword" name="smtpPassword" size="30"
     value="<%= configuration.getSmtpPassword() %>" />
   </td>
  </tr>
  
  <tr><th colspan="2"><fmt:message key="configSystem" /></th></tr>

  <tr>
   <td align="right"><fmt:message key="configCache" /></td>
   <td>
    <select name="cacheType">
     <option value="<%= Finals.NO_CACHE %>"
      <%= cacheType == Finals.NO_CACHE ? " selected='selected'" : "" %>><fmt:message key="configCacheNone" /></option>
     <option value="<%= Finals.IN_MEMORY_CACHE %>"
      <%= cacheType == Finals.IN_MEMORY_CACHE ? " selected='selected'" : "" %>><fmt:message key="configCacheMemory" /></option>
     <option value="<%= Finals.ON_DISK_CACHE %>"
      <%= cacheType == Finals.ON_DISK_CACHE ? " selected='selected'" : "" %>><fmt:message key="configCacheDisk" /></option>
    </select>
   </td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="configMap" /></td>
   <td>
    <img src="images/clear_field.gif" onclick="javascript:editor_clr('updateInterval');"
     align="middle" /><input type="text" id="updateInterval" name="updateInterval" size="30"
     value="<%= configuration.getUpdateInterval() %>" />
   </td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="configBackup" /></td>
   <td>
    <img src="images/clear_field.gif" onclick="javascript:editor_clr('backupLife');"
     align="middle" /><input type="text" id="backupLife" name="backupLife" size="30"
     value="<%= configuration.getBackupLife() %>" />
   </td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="configHits" /></td>
   <td>
    <img src="images/clear_field.gif" onclick="javascript:editor_clr('statsLength');"
     align="middle" /><input type="text" id="statsLength" name="statsLength" size="30"
     value="<%= configuration.getStatsLength() %>" />
   </td>
  </tr>

  <tr>
   <td align="right"><fmt:message key="configCharset" /></td>
   <td><select name="preferredCharset"><%
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
     } %>
   </select></td>
  </tr>

  <tr><th colspan="2"><input type="submit" value="<fmt:message key="genericSave" />" /></th></tr>
 </table>
</form>

</body>
</html>
