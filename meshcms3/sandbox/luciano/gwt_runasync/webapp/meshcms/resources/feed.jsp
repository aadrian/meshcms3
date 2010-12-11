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

<%@ page contentType="text/xml" isELIgnored="false" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<rss version="2.0">
  <channel>
    <title>${bean.title}</title>
    <link>${bean.link}</link>
    <generator>${bean.generator}</generator>

    <c:forEach items="${bean.entries}" var="page">
      <item>
        <title>${page.title}</title>
        <link>${page.rssLink}</link>
        <pubDate>${page.rssDate}</pubDate>
        <c:choose>
          <c:when test="${page.image}">
            <description><![CDATA[<p><img src="${page.rssImageURL}" alt="" /></p> ${page.text}]]></description>
              </c:when>
              <c:otherwise>
            <description><![CDATA[${page.text}]]></description>
          </c:otherwise>
        </c:choose>
      </item>
    </c:forEach>
  </channel>
</rss>
