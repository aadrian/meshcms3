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

<c:forEach items="${bean.items}" var="item">
  <c:if test="${item.stepsDown gt 0}">
    <c:forEach begin="1" end="${item.stepsDown}" var="i" varStatus="status">
      <ul<c:if test="${item.level eq 0}"> class="${bean.style}"</c:if>>
        <c:if test="${not status.last}">
          <li>
          </c:if>
        </c:forEach>
      </c:if>

      <c:if test="${item.stepsUp gt 0}">
        <c:forEach begin="1" end="${item.stepsUp}" var="i">
        </li></ul>
      </c:forEach>
    </c:if>

  <c:if test="${item.li}">
  </li>
</c:if>

<li<c:if test="${not empty item.style}"> class="${item.style}"</c:if>>
  <c:choose>
    <c:when test="${item.current and not item.linkCurrent}">
      ${item.title}
    </c:when>
    <c:otherwise>
      <a<c:if test="${not empty item.style}"> class="${item.style}"</c:if> href="${item.link}">
        <c:choose>
          <c:when test="${bean.style ne 'mesh-menu' and item.level le 1}">
            <span class="l"></span><span class="r"></span><span class="t">${item.title}</span>
          </c:when>
          <c:otherwise>
            ${item.title}
          </c:otherwise>
        </c:choose>
      </a>
    </c:otherwise>
  </c:choose>
</c:forEach>

<c:if test="${bean.lastSteps gt 0}">
  <c:forEach begin="1" end="${bean.lastSteps}" var="i">
  </li></ul>
</c:forEach>
</c:if>
