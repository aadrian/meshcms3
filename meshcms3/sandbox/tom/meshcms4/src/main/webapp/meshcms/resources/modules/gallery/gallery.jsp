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
  <ul class="mesh-gallery" id="${bean.id}">
    <c:forEach items="${bean.images}" var="image">
      <li>
        <a href="${image.link}" rel="${bean.id}_gallery"<c:if test="${bean.printCaptions}"> title="${image.name}"</c:if>>
          <img src="${image.thumbnail}" alt="${image.name}" />
          <c:if test="${bean.printCaptions}">
            <span class="mesh-gallery-caption">${image.name}</span>
          </c:if>
        </a>
      </li>
    </c:forEach>
  </ul>

  <div class="mesh-clear"></div>

  <script type="text/javascript">
    if (window.jQuery) {
      var fnCenter = function($thumb, $parent) {
        $thumb.css('padding-top', ($parent.height() - $thumb.height()) / 2 + 'px');
      }

      jQuery(function() {
        jQuery('#${bean.id} img').each(function() {
          var $thumb = jQuery(this);
          var $parent = $thumb.parent();

          if ($thumb.position().top == $parent.position().top) {
            $parent.css('display', 'block');

            $thumb.load(function() {
              fnCenter($thumb, $parent);
            });

            fnCenter($thumb, $parent);
          }
        });
      });

    <c:if test="${bean.useColorBox}">
        if (jQuery.fn.colorbox) {
          jQuery(function() {
            jQuery("a[rel='${bean.id}_gallery']").colorbox({maxWidth:"85%", maxHeight:"85%", current:"{current}/{total}"});
          });
        }
    </c:if>
      }
  </script>
</c:if>
