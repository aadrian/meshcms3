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
<%@ page import="java.util.*" %>
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webSite" scope="request" type="com.cromoteca.meshcms.WebSite" />

<%
  File[] files = WebUtils.getModuleFiles(webSite, request, false);

  if (files != null && files.length > 0) {
    Arrays.sort(files, new FileNameComparator());

    for (int i = 0; i < files.length; i++) {
      if (files[i].isFile()) {
        WebUtils.updateLastModifiedTime(request, files[i]);
        out.write("<pre>");
        out.write(Utils.encodeHTML(Utils.readFully(files[i])));
        out.write("</pre>");
      }
    }
  }
%>
