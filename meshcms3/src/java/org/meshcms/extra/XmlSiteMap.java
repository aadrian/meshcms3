/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2008 Luciano Vernaschi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * You can contact the author at http://www.cromoteca.com
 * and at info@cromoteca.com
 */
package org.meshcms.extra;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.meshcms.core.HitFilter;
import org.meshcms.core.PageInfo;
import org.meshcms.core.WebSite;
import org.meshcms.core.WebUtils;
import org.meshcms.util.Utils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlSiteMap extends HttpServlet {
  public static SimpleDateFormat ISO_8601_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd");
  public static float PRIORITY_WEIGHT = 9.0F;
  public static NumberFormat DECIMAL_FORMAT =
      NumberFormat.getNumberInstance(Locale.ENGLISH);

  static {
    DECIMAL_FORMAT.setMinimumFractionDigits(1);
    DECIMAL_FORMAT.setMaximumFractionDigits(2);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      response.setContentType("text/xml; charset=" + Utils.SYSTEM_CHARSET);
      String baseURL =
          WebUtils.getContextHomeURL(request).append('/').toString();

      DocumentBuilderFactory factory =
          DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      DOMImplementation impl = builder.getDOMImplementation();
      Document doc = impl.createDocument(null, null, null);

      Element urlset =
          doc.createElementNS("http://www.sitemaps.org/schemas/sitemap/0.9",
          "urlset");
      //urlset.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
      doc.appendChild(urlset);

      WebSite webSite =
          (WebSite) request.getAttribute(HitFilter.WEBSITE_ATTRIBUTE);

      if (webSite != null) {
        List pagesList = webSite.getSiteMap().getPagesList();

        for (Iterator iter = pagesList.iterator(); iter.hasNext();) {
          PageInfo pageInfo = (PageInfo) iter.next();

          Element url = doc.createElement("url");
          urlset.appendChild(url);

          Element loc = doc.createElement("loc");
          url.appendChild(loc);
          loc.appendChild(doc.createTextNode(baseURL + pageInfo.getPath()));

          Element lastmod = doc.createElement("lastmod");
          url.appendChild(lastmod);
          lastmod.appendChild(doc.createTextNode(ISO_8601_FORMAT.format(new Date(pageInfo.getLastModified()))));

          Element priority = doc.createElement("priority");
          url.appendChild(priority);
          float value = PRIORITY_WEIGHT / (PRIORITY_WEIGHT +
              pageInfo.getPath().getElementCount());
          priority.appendChild(doc.createTextNode(DECIMAL_FORMAT.format(value)));
        }
      }

      DOMSource domSource = new DOMSource(doc);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.ENCODING, Utils.SYSTEM_CHARSET);
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      StreamResult sr = new StreamResult(response.getOutputStream());
      transformer.transform(domSource, sr);
    } catch (ParserConfigurationException ex) {
      throw new ServletException(ex);
    } catch (TransformerException ex) {
      throw new ServletException(ex);
    }
  }
}
