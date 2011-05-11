<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2007 Luciano Vernaschi
 Login module for MeshCMS
 Copyright (C) 2008 Simon Kornél

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
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />
<%
  String moduleCode = request.getParameter("modulecode");
  ModuleDescriptor md = null;

  if (moduleCode != null) {
    md = (ModuleDescriptor) request.getAttribute(moduleCode);
  }

  if (md == null) {
    if (!response.isCommitted()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    return;
  }
  
  Path pagePath = webSite.getRequestedPath(request);
  Locale locale = WebUtils.getPageLocale(pageContext);
  ResourceBundle pageBundle = ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);
  String cssForm = md.getAdvancedParam( "cssForm", null );
  String cssBox = md.getAdvancedParam( "cssBox", null );
  String cssButton = md.getAdvancedParam( "cssButton", null );
  
  if( cssButton != null ) cssButton = "class=\"" + cssButton + "\"";
  else cssButton = "class=\"loginbutton\"";
  if( cssForm != null ) cssForm = "class=\"" + cssForm + "\"";
  else cssForm = "class=\"loginform\"";
  if( cssBox != null ) cssBox = "class=\"" + cssBox + "\"";
  else cssBox = "class=\"loginbox\"";
%>
<%
	boolean reloadPage = false;
	boolean forumLogin = false;
	boolean forumLogout = false;

	String aUsername ="";
	String aPassword ="";

	String action = Utils.noNull(request.getParameter("_loginAction"));
	String userName = userInfo.getUsername();
	if (!action.equals("")) {
		if (userName.equals("")) {
			  aUsername = Utils.encodeHTML(request.getParameter("_loginUsername"));
			  aPassword = Utils.encodeHTML(request.getParameter("_loginPassword"));
			  boolean loaded = false;
			  if (!aUsername.equals("") || !aPassword.equals("")) {
			    loaded = userInfo.load(webSite, aUsername, aPassword);
			    if (!loaded) {
					%><div class="meshcmsfieldlabel"><%= pageBundle.getString("loginError") %></div><%
			    } else {
			    	reloadPage = true;
			    	forumLogin = true;
			    }
			  }
		} else {
			userInfo.loadGuest();
			forumLogout = true;
		}
	}

	userName = userInfo.getUsername();
	if (userName!=null && (!userName.equals(""))) {
%>

<script type="text/javascript" src="<%= request.getContextPath() + '/' + md.getModulePath() %>/js/mootools-1.2.4-core.js"></script>
<script>
	function doLogout(){
		document.cookie = "JforumSSO==<%=userInfo.getUsername()%>; expires=" + new Date().toGMTString();
		document.cookie = "email=<%=userInfo.getEmail()%>; expires=" + new Date().toGMTString();
		//Cookie.write("JforumSSO", "", {path: '/'} );
		//Cookie.dispose( "JforumSSO" );
		document.getElementById('logoutform').submit();
	}
</script>

  <form name="logoutform" id="logoutform" action="<%= pagePath.getLastElement().toString() %>" method="post" <%=cssForm%>>
   <input type="hidden" name="_loginAction" value="logout" />
   <div class="meshcmsfieldlabel">
    <label><%= pageBundle.getString("loginUsername") %></label> <%= userName %>
   </div>
   <ul class="menublock">
   	<li><a href="javascript:doLogout()"><%= pageBundle.getString("homeLogout") %></a></li>
   </ul>
  </form>
    
<%
	} else {
		userName = Utils.noNull(request.getParameter("_username"));
%>
  <form name="loginform" action="<%= pagePath.getLastElement().toString() %>" method="post" <%=cssForm %>>
   <input type="hidden" name="_loginAction" value="login" />
   <div class="meshcmsfieldlabel">
    <label for="_loginUsername"><%= pageBundle.getString("loginUsername") %></label>
   </div>

   <div class="meshcmsfield">
    <input type="text" id="_loginUsername" name="_loginUsername" value="<%= userName %>" <%=cssBox%>/>
   </div>

   <div class="meshcmsfieldlabel">
    <label for="_loginPassword"><%= pageBundle.getString("loginPassword") %></label>
   </div>

   <div class="meshcmsfield">
    <input type="password" id="_loginPassword" name="_loginPassword"  <%=cssBox%>/>
   </div>
 
   <div class="meshcmsfield">
   	<br></br>
    <input type="submit" value="<%= pageBundle.getString("loginSubmit") %>" <%=cssButton%>/>
   </div>
  </form>
<%
	}
%>

<%
if( forumLogin ){
%>
<script type="text/javascript" src="<%= request.getContextPath() + '/' + md.getModulePath() %>/js/mootools-1.2.4-core.js"></script>
<script>
var date = new Date();
var millis = date.getTime() + (24 * 60 * 60 * 1000);
date.setTime(millis);

document.cookie = "JforumSSO=<%=userInfo.getUsername()%>; expires=" + date.toGMTString() + "; path=/jforum";
document.cookie = "email=<%=userInfo.getEmail()%>; expires=" + date.toGMTString() + "; path=/jforum";

location.href = "http://<%=request.getServerName()%>/intern/forum2.html";
</script>
<%
}
%>

<% if (forumLogout ) { %>
	<script type="text/javascript">
	location.href = "http://<%=request.getServerName()%>";
	</script>
<% } %>


<% if (reloadPage ) {%>
	<script type="text/javascript">
	location.href = location.href;
	</script>
<% } %>
