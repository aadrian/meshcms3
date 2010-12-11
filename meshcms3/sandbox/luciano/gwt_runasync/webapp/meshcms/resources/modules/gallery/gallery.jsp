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

<c:if test="${bean.hasImages}">
  <ul class="mesh-gallery">
    <c:forEach items="${bean.images}" var="image">
      <li>
        <a href="${image.link}" rel="${bean.id}_gallery"<c:if test="${bean.printCaptions}"> title="${image.name}"</c:if>>
          <img src="${image.thumbnail}" alt="${image.name}" width="96" height="96" />
          <c:if test="${bean.printCaptions}">
            <span class="mesh-gallery-caption">${image.name}</span>
          </c:if>
        </a>
      </li>
    </c:forEach>
  </ul>

  <div class="mesh-clear"></div>

  <c:if test="${bean.useColorBox}">
    <script type="text/javascript">
      if (window.jQuery && jQuery.fn.colorbox) {
        jQuery(function() {
          jQuery("a[rel='${bean.id}_gallery']").colorbox({maxWidth:"85%", maxHeight:"85%", current:"{current}/{total}"});
        });
      }
    </script>
  </c:if>
</c:if>
