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
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%@ taglib uri="sitemesh-decorator" prefix="decorator" %>

<!--
     MeshCMS | open source web content management system
       more info at http://www.cromoteca.com/meshcms

       developed by Luciano Vernaschi
       released under the GNU General Public License (GPL)
       visit http://www.gnu.org/licenses/gpl.html for details on GPL
//-->

<%
  String themePath = request.getContextPath() + "/" + webSite.getAdminThemePath();
%>

<html>
<head>
<title>MeshCMS - <decorator:title default="" /></title>
<link rel="stylesheet" type="text/css" href="<%= themePath %>/main.css" />
<link rel="stylesheet" type="text/css" href="<%= themePath %>/meshcms.css" />
<decorator:head />
</head>

<body>

<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0" bgcolor="white">
 <tr>
  <td background="<%= themePath %>/dbg.gif" bgcolor="#061F32"
   align="right" height="78">
    <a href="http://www.cromoteca.com/meshcms/" target="blank"><img
     src="<%= themePath %>/logo.gif" alt="MeshCMS" border="0"></a>
  </td>
 </tr>

 <tr>
  <td bgcolor="#CCDC56" align="center">
   <a href="<%= request.getContextPath() + '/' + webSite.getAdminPath() + "/index.jsp" %>">MeshCMS</a>:
   <decorator:title default="" />
  </td>
 </tr>

 <tr>
  <td align="center" valign="top" height="100%" background="<%= themePath %>/l2bg.gif">
   <table border="0" cellspacing="10" cellpadding="0" width="97%">
    <tr>
     <td align="center">
       <table border="0"><tr><td><decorator:body /></td></tr></table>
     </td>
    </tr>
   </table>
  </td>
 </tr>

 <tr>
  <td bgcolor="#CCDC56" align="center">
<%
  if (userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS)) {
    Runtime runtime = Runtime.getRuntime();
%>
    Used Memory: <%= (runtime.totalMemory() - runtime.freeMemory()) *
                      100 / runtime.maxMemory() %>% |
    System charset: <%= webSite.SYSTEM_CHARSET %> |
    Preferred charset: <%= webSite.getConfiguration().getPreferredCharset() %>
    
    <br />
<%
  }
%>
    Powered by <a href="http://www.meshcms.org/" target="blank">MeshCMS</a>
    <%= webSite.VERSION_ID %> | Copyright &copy; 2004-2006
    <a href="http://www.cromoteca.com/" target="blank">Luciano Vernaschi</a>
  </td>
 </tr>
</table>

</body>
</html>
