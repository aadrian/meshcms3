<%--
 Copyright 2004-2010 Luciano Vernaschi

 This file is part of MeshCMS.

 MeshCMS is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 MeshCMS is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@ page isELIgnored="false" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />

    <title>${bean.title}</title>

    <c:if test="${bean.tinyMCE}">
      <c:choose>
        <c:when test="${bean.browserPopup}">
          <script type="text/javascript" src="${bean.editorDir}/tiny_mce/tiny_mce_popup.js"></script>
        </c:when>
        <c:otherwise>
          <script type="text/javascript" src="${bean.editorDir}/tiny_mce/tiny_mce.js"></script>
        </c:otherwise>
      </c:choose>
      <%-- <script type="text/javascript">
        MESHCMS_TINY_MCE_POPUP = "${bean.editorDir}/tiny_mce/tiny_mce_popup.js";
      </script> --%>
      <script type="text/javascript" src="tiny_mce_integration.js"></script>
    </c:if>

    <c:if test="${bean.ckEditor}">
      <script type="text/javascript" src="${bean.editorDir}/ckeditor/ckeditor.js"></script>
      <script type="text/javascript" src="ckeditor_integration.js"></script>
    </c:if>

    <c:if test="${bean.editArea}">
      <script type="text/javascript" src="${bean.editorDir}/edit_area/edit_area_full.js"></script>
      <script type="text/javascript" src="edit_area_integration.js"></script>
    </c:if>

    <%-- TODO: remove static path /meshcms --%>
    <script type="text/javascript" src="../meshcms.nocache.js"></script>
  </head>

  <body>
  </body>
</html>
