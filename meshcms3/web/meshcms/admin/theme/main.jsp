<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

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

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>MeshCMS: <decorator:title default="" /></title>
    <link rel="stylesheet" type="text/css" href="<%= themePath %>/main.css" />
    <link rel="stylesheet" type="text/css" href="<%= themePath %>/meshcms.css" />
    <decorator:head />
  </head>

  <body <decorator:getProperty property="body.onload" writeEntireProperty="true" /> id="cmsbody">
    <div id="header">
    </div>

    <div id="pagecontentcolumn">
      <div id="pagecontent">
        <div id="mainarea">
          <div class="pagebody">
            <h3>
              <a href="<%= request.getContextPath() + '/' + webSite.getAdminPath() + "/index.jsp" %>">MeshCMS</a>:
              <decorator:title default="" />
            </h3>
            <decorator:body />
          </div>
        </div>
      </div>
    </div>

    <div id="additionalcolumn">
      <div class="cmssidemodule">
        <% if (userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS)) {
          Runtime runtime = Runtime.getRuntime();
        %>
          <h4>System information</h4>
          <div class="sysinfo">Used Memory: <%= (runtime.totalMemory() - runtime.freeMemory()) * 100 / runtime.maxMemory() %>%</div>
          <div class="sysinfo">System charset: <%= WebSite.SYSTEM_CHARSET %></div>
        <% } %>
      </div>
    </div>

    <div id="footer">
      Powered by <a href="http://www.cromoteca.com/meshcms/" target="blank">MeshCMS</a>
      <%= WebSite.VERSION_ID %> | Copyright &copy; 2004-2007
      <a href="http://www.cromoteca.com/" target="blank">Luciano Vernaschi</a>
    </div>
  </body>
</html>
