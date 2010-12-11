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

<ul<c:if test="${not empty bean.style}"> class="${bean.style}"</c:if>>
  <c:forEach items="${bean.items}" var="item">
    <li>
      <a<c:if test="${item.current}"> class="active"</c:if> href="${item.link}" title="${item.name}">
        <c:if test="${bean.style ne 'mesh-menu'}">
          <span class="l"></span><span class="r"></span><span class="t">
          </c:if>
          <c:if test="${not empty item.flag}">
            <img src="${item.flag}" alt="${item.name}" />
          </c:if>
          <c:if test="${bean.names}">
            ${item.name}
          </c:if>
          <c:if test="${bean.style ne 'mesh-menu'}">
          </span>
        </c:if>
      </a>
    </li>
  </c:forEach>
</ul>
