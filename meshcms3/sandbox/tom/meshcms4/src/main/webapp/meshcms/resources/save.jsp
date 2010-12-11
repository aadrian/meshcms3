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
${mesh.doctype}

<html${mesh.htmlAttributes}>
  <head${mesh.headAttributes}>
    <meta http-equiv="content-type" content="text/html; charset=${mesh.encoding}" />
    <title>${mesh.title}</title>
    <c:if test="${not empty mesh.description}">
      <meta name="description" content="${mesh.description}" />
    </c:if>
    <c:if test="${not empty mesh.keywords}">
      <meta name="keywords" content="${mesh.keywords}" />
    </c:if>
    ${mesh.head}
    <script type="application/javascript">
      // <![CDATA[
      var meshcmsPageInfo = ${mesh.pageInfo};
      // ]]>
    </script>
  </head>

  <body${mesh.bodyAttributes}>
    ${mesh.body}
  </body>
</html>
