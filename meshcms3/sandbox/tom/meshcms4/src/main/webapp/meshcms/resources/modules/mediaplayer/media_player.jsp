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

<c:if test="${bean.hasItems}">
  <div class="${bean.style}">
    <script type="text/javascript" src="${bean.meshPath}/modules/mediaplayer/flowplayer-3.1.4.min.js"></script>
    <script type="text/javascript" src="${bean.meshPath}/modules/mediaplayer/flowplayer.playlist-3.0.7.min.js"></script>

    <a id="${bean.id}_player" class="mesh-media-player-swf">
      MeshCMS Media Player
    </a>

    <div id="${bean.id}_playlist" class="mesh-media-player-playlist">
      <c:forEach items="${bean.items}" var="item">
        <a href='${item.link}'>
          <img src="${item.icon}" alt="${item.fileType}" />
          ${item.name}
        </a>
      </c:forEach>
    </div>
  </div>

  <script type="text/javascript">
    if (window.jQuery) {
      jQuery(function() {
        flowplayer(
        "${bean.id}_player", {
          src: "${bean.meshPath}/modules/mediaplayer/flowplayer-3.1.5.swf",
          wmode: "opaque"
        }, {
          play: {
            opacity: 0
          },
          clip: {
            scaling: "fit"
          }
        }
      ).playlist("#${bean.id}_playlist");
      });
    }
  </script>
</c:if>
