<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2007 Luciano Vernaschi

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
<%@ page import="org.meshcms.util.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="org.meshcms.webui.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_MANAGE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }
%>

<html>
<head>
<%= webSite.getDummyMetaThemeTag() %>
<title><fmt:message key="fmUploadTitle" /></title>
<link href="filemanager.css" type="text/css" rel="stylesheet" />
</head>

<body>

  <p align="center"><fmt:message key="fmUploadImgAlt" /></p>

<%
  out.flush();

  Path path = null;
  boolean ok = false;
  boolean fixName = false;

  try {
    FileItem upItem = null;
    ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
    List items = upload.parseRequest(request);
    Iterator iter = items.iterator();

    while (iter.hasNext()) {
      FileItem item = (FileItem) iter.next();

      if (item.getFieldName().equals("dir")) {
        path = new Path(item.getString());
      } else if (item.getFieldName().equals("fixname")) {
        fixName = Utils.isTrue(item.getString());
      } else if (item.getFieldName().equals("upfile") && item.getSize() > 0L) {
        upItem = item;
      }
    }

    if (upItem != null && path != null) {
      String fileName = new Path(upItem.getName()).getLastElement();

      if (fixName) {
        fileName = Utils.generateUniqueName
            (WebUtils.fixFileName(fileName, true), webSite.getFile(path));
      }

      ok = webSite.saveToFile(userInfo, upItem, path.add(fileName));
    }
  } catch (Exception ex) {
    webSite.log("Can't upload in directory " + path, ex);
  }

  if (ok) {
%>
  <p align="center"><fmt:message key="fmUploadDone" /></p>

  <script type="text/javascript">
  // <![CDATA[
    window.parent.fm_dummy();
  // ]]>
  </script>
<%
  } else {
%>
  <p align="center"><fmt:message key="fmUploadFailed" /></p>
<%
  }
%>

</body>
</html>
