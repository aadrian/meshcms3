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

<div id="${bean.id}" class="mesh-field <#if bean.invalid>mesh-invalid</#if> <#if bean.required>mesh-required</#if>">
  <input type="checkbox" id="${bean.id}_field" name="${bean.name}" value="${bean.value}"<c:if test="bean.checked"> checked="checked"</c:if> />
  <label for="${bean.id}_field">${bean.label}</label>
</div>
