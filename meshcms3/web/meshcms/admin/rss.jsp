<% out.print("<?xml version='1.0' encoding='" +
             org.meshcms.util.Utils.SYSTEM_CHARSET + "'?>"); %>

<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2008 Luciano Vernaschi

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 You can contact the author at http://www.cromoteca.com
 and at info@cromoteca.com
--%>

<%@ page contentType="application/rss+xml" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="com.opensymphony.module.sitemesh.*" %>
<%@ page import="com.opensymphony.module.sitemesh.parser.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

<%
  SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
  Path root = new Path(request.getParameter("root"));
  List pagesList = new ArrayList(webSite.getSiteMap().getPagesList(root));
  
  Collections.sort(pagesList, new Comparator() {
    public int compare(Object o1, Object o2) {
      PageInfo p1 = (PageInfo) o1;
      PageInfo p2 = (PageInfo) o2;
      return new Long(p2.getLastModified()).compareTo(new Long(p1.getLastModified()));
    }
  });
  
  int max = Utils.parseInt(request.getParameter("max"), pagesList.size());
  
  Iterator iter = pagesList.iterator();
  SiteMap siteMap = webSite.getSiteMap();
  String homeURL = WebUtils.getContextHomeURL(request).toString();
  response.setContentType("text/xml; charset=" + Utils.SYSTEM_CHARSET);
%>
<rss version="2.0">
    <channel>
        <title><%= webSite.getConfiguration().getSiteName() %></title>
        <description><%= Utils.noNull(webSite.getConfiguration().getSiteDescription()) %></description>
        <link><%= homeURL %></link>
        <generator><%= WebSite.APP_NAME + ' ' + WebSite.VERSION_ID %></generator>
<%
  for (int i = 0; i < max && iter.hasNext(); i++) {
      PageInfo pi = (PageInfo) iter.next();
      HTMLPageParser fpp = new HTMLPageParser();

      Reader reader = new InputStreamReader
          (new FileInputStream(webSite.getFile(siteMap.getServedPath(pi.getPath()))),
          Utils.SYSTEM_CHARSET);
      HTMLPage pg = (HTMLPage) fpp.parse(Utils.readAllChars(reader));
      reader.close();
%>
<item>
    <title><%= pi.getTitle() %></title>
    <link><%= homeURL + webSite.getAbsoluteLink(pi) %></link>
    <pubDate><%= dateFormat.format(new Date(pi.getLastModified())) %></pubDate>
    <description><%= Utils.encodeHTML(Utils.limitedLength(Utils.stripHTMLTags(pg.getBody()).replaceAll("[\\n\\r\\t ]+", " "), 800), true) %></description>
</item>
<%
  }
%>
    </channel>
</rss>
