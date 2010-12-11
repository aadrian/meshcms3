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

<c:if test="${bean.user and (bean.body or not bean.noDraft)}">
  <div class="mesh-edit">
    <c:choose>
      <c:when test="${bean.body}">
        <c:choose>
          <c:when test="${bean.noDraft}">
            <a class="mesh-edit-button mesh-edit-toggle-draft" href="${bean.draftLink}">
              ${bean.viewDraftLabel}
            </a>
          </c:when>

          <c:otherwise>
            <c:if test="${bean.hasDraft}">
              <a class="mesh-edit-button mesh-edit-toggle-draft" href="${bean.noDraftLink}">
                ${bean.viewPublishedLabel}
              </a>
            </c:if>

            <c:if test="${bean.editable}">
              <a class="mesh-modal mesh-edit-button mesh-edit-edit" href="${bean.resPath}/host.jsp?mode=page_editor&path=${bean.path}&dir=${bean.dir}&editBodyZone=${bean.editBodyZone}">
                ${bean.editPageLabel}
              </a>

              <a class="mesh-modal mesh-edit-button mesh-edit-create" href="${bean.resPath}/host.jsp?mode=new_page&dir=${bean.dir}&editBodyZone=${bean.editBodyZone}">
                ${bean.createPageLabel}
              </a>
            </c:if>

            <a class="mesh-modal mesh-edit-button mesh-edit-filemanager" href="${bean.resPath}/host.jsp?mode=file_manager&dir=${bean.dir}">
              ${bean.fileManagerLabel}
            </a>

            <label id="mesh-use-colorbox-label" class="mesh-edit-button mesh-edit-colorbox" for="mesh-use-colorbox">
              ${bean.popupsLabel}
              <input type="checkbox" id="mesh-use-colorbox" />
            </label>

            <script type="text/javascript">
              if (window.jQuery) {
                jQuery(function() {
                  $("img[alt]").each(function() {
                    var $img = $(this);

                    if (!$img.attr("title")) {
                      $img.attr("title", $img.attr("alt"));
                    }
                  });

                  if (jQuery.fn.colorbox) {
                    jQuery.get(
                    "${bean.resPath}/user_preference.jsp",
                    {
                      name: "useColorBox"
                    },
                    function(data) {
                      var $cb = $("#mesh-use-colorbox");

                      if (data.indexOf("false") < 0) {
                        $cb.attr("checked", "checked")
                      } else {
                        $cb.removeAttr("checked");
                      }

                      $cb.click(function() {
                        jQuery.get(
                        "${bean.resPath}/user_preference.jsp",
                        {
                          name: "useColorBox",
                          value: $("#mesh-use-colorbox").attr("checked")
                        }
                      );
                      });
                    }
                  );

                    jQuery('a.mesh-modal').click(function() {
                      var use = $("#mesh-use-colorbox").is(':checked');

                      if (use) {
                        jQuery.fn.colorbox({
                          href: $(this).attr('href'),
                          width: "85%",
                          height: "90%",
                          //width: Math.min(jQuery(window).width() - 60, 800) + "px",
                          //height: Math.min(jQuery(window).height() - 60, 800) + "px",
                          iframe: true,
                          overlayClose: false,
                          opacity: 0.7,
                          open: true
                        });
                      }

                      return !use;
                    });
                  } else {
                    $("#mesh-use-colorbox-label").hide();
                  }
                });
              }
            </script>
          </c:otherwise>
        </c:choose>
      </c:when>

      <c:otherwise>
        <c:if test="${bean.editable and not bean.noDraft}">
          <a class="mesh-modal mesh-edit-button mesh-edit-edit" href="${bean.resPath}/host.jsp?mode=zone_editor&path=${bean.path}&dir=${bean.dir}&zone=${bean.zoneName}">
            ${bean.editZoneLabel}
          </a>
        </c:if>
      </c:otherwise>
    </c:choose>
    <div class="mesh-clear"></div>
  </div>
</c:if>
