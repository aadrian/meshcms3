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

<div id="${bean.id}" class="mesh-field <c:if test="${bean.invalid}">mesh-invalid</c:if> <c:if test="${bean.required}">mesh-required</c:if>">
  <label for="${bean.id}_field">${bean.label}</label>
  <textarea id="${bean.id}_field" name="${bean.name}"
            <c:if test="${bean.rows gt 0}">rows="${bean.rows}"</c:if>
            <c:if test="${bean.columns gt 0}">cols="${bean.columns}"</c:if>
            >${bean.value}</textarea>
</div>
