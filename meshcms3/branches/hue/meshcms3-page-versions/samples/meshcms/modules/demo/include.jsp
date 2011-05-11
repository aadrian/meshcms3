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
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>

<%-- get the website instance --%>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%--
  Advanced parameters for this module:
  - css = (name of a css class)
  - date = none (default) | normal | full
  - message = (an optional message, just to show how to use advanced parameters)
--%>

<%
  /* get the module descriptor (contains all the info we need about this module */
  String moduleCode = request.getParameter("modulecode");
  ModuleDescriptor md = null;

  if (moduleCode != null) {
    md = (ModuleDescriptor) request.getAttribute(moduleCode);
  }
  
  /* if md is null, this module has not been called correctly */
  if (md == null) {
    /* send an error if possible (maybe the module has been called directly) */
    if (!response.isCommitted()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
    return;
  }
  
  /* get the files to be processed. Note that the argument is not required to
     be a path (it can be any string), so getModuleFiles can return null */
  File[] files = md.getModuleFiles(webSite, false);

  /* display the list of files into a div that uses the style if it is provided */
  out.println("<p" + md.getFullCSSAttribute("css") + ">");
  
  if (files != null && files.length > 0) {
    Arrays.sort(files);
    DateFormat df = md.getDateFormat(WebUtils.getPageLocale(pageContext), "date");
      out.println("  <div>List of files:</div>");

    for (int i = 0; i < files.length; i++) {
      /* update the last modified time for the page */
      WebUtils.updateLastModifiedTime(request, files[i]);
      out.print("  <div>" + files[i].getName());
      
      if (df != null) {
        out.print(" (" + df.format(new Date(files[i].lastModified())) + ")");
      }
      
      out.println("</div>");
    }
  } else {
    out.println("  <div><em>no files in &quot;" + md.getArgument() + "&quot;</em></div>");
  }
  
  out.println("</p>");
  
  /* if a message has been provided, show it */
  String msg = md.getAdvancedParam("message", null);
  
  if (msg != null) {
    out.println("  <p>Message: &quot;" + msg + "&quot;</p>");
  }
%>
