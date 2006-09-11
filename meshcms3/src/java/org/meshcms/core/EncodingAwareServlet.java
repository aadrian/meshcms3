/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2006 Luciano Vernaschi
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

package org.meshcms.core;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.glaforge.i18n.io.*;
import org.meshcms.util.*;

public class EncodingAwareServlet extends HttpServlet {
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    WebSite webSite = (WebSite) request.getAttribute("webSite");
    HttpServletRequest httpReq = webSite.wrapRequest(request);
    Path pagePath = webSite.getRequestedPath(httpReq);
    String servedPath = webSite.getServedPath(httpReq).getAsLink();
    PageInfo pageInfo = webSite.getSiteMap().getPageInfo(pagePath);
    File servedFile = new File(getServletContext().getRealPath(servedPath));
    
    if (!servedFile.exists()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    String charset = (pageInfo == null) ? null : pageInfo.getCharset();
    Reader reader;

    if (charset == null) {
      SmartEncodingInputStream seis = new SmartEncodingInputStream(
          new FileInputStream(servedFile),
          SmartEncodingInputStream.BUFFER_LENGTH_4KB,
          Charset.forName(webSite.getConfiguration().getPreferredCharset())
      );
      reader = seis.getReader();
      charset = seis.getEncoding().toString();
    } else {
      reader = new InputStreamReader(new FileInputStream(servedFile), charset);
    }
    
    response.setContentType("text/html");
    response.setCharacterEncoding(charset);
    Utils.copyReaderToWriter(reader, response.getWriter(), false);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }
  
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }
  
  public String getServletInfo() {
    return "Servlet that reads a plain HTML file using charset information when available";
  }
}
