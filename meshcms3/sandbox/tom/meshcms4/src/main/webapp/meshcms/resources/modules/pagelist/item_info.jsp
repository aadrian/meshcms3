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

<c:if test="${not empty bean.date}">
  <img src="${bean.modulePath}/mesh-calendar-select.png" alt="" />
  ${bean.dateMsg}
  <fmt:formatDate value="${bean.date}" type="date"/>
</c:if>

<c:if test="${bean.hasTags and not empty bean.date}">|</c:if>

<c:if test="${bean.hasTags}">
  <img src="${bean.modulePath}/tag.png" alt="" />
  ${bean.tagsMsg}
  <c:forEach items="${bean.tags}" var="tag" varStatus="status">
    <a href="${tag.link}">${tag.value}</a><c:if test="${not status.last}">,</c:if>
  </c:forEach>
</c:if>
