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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${bean.hasItems}">
  <dl class="mesh-downloads">
    <c:forEach items="${bean.items}" var="item">
      <dt>
        <a href="${item.link}">
          <img src="${item.icon}" alt="${item.fileType}" />
          ${item.name}
        </a>
      </dt>
      <dd>
        <span class="mesh-download-date">
          <fmt:formatDate value="${item.date}" type="date"/>
        </span>
        <span class="mesh-download-size">
          ${item.size}
        </span>
      </dd>
    </c:forEach>
  </dl>
  <div class="mesh-clear"></div>
</c:if>
