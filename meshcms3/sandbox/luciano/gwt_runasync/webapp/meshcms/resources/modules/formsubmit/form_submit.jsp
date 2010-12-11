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

<%-- the markup of this script completes the one in form.jsp --%>

<div>
  <button type="submit" id="${bean.id}_submit">${bean.buttonLabel}</button>
</div>

<c:if test="${bean.form.clientScript}">
  <script type="text/javascript">
    if (window.jQuery) {
      jQuery(function() {
        jQuery("#${bean.id}_submit").click(function() {
          var ok = true;

    <c:forEach items="${bean.form.fields}" var="field">
      <c:if test="${field.required}">
              if (jQuery("#${field.id}_field").val()) {
                jQuery("#${field.id}").removeClass("mesh-invalid");
              } else {
                ok = false;
                jQuery("#${field.id}").addClass("mesh-invalid");
              }
      </c:if>
    </c:forEach>

            return ok;
          });
        });
      }
  </script>
</c:if>
</form>

<c:if test="${bean.showMessage}">
  <div id="${bean.form.id}_result" class="${bean.result.style}">
    ${bean.result.message}
  </div>

  <script type="text/javascript">
    if (window.jQuery) {
      jQuery(function() {
    <c:if test="${not bean.result.error}">jQuery("#${bean.form.id}_form").hide();</c:if>
          window.scrollTo(0, jQuery("#${bean.form.id}_result").offset().top - jQuery(window).height() / 2);
        });
      }
  </script>
</c:if>
