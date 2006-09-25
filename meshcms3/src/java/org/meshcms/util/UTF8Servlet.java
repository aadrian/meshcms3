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

package org.meshcms.util;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class UTF8Servlet extends HttpServlet {
  public static final String CHARSET = "UTF-8";
  public static final String EXTENSION = ".utf8";
  
  public static boolean matchExtension(String fileName) {
    return EXTENSION.equals(Utils.getExtension(fileName, true));
  }
  
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    ServletContext context = getServletContext();
    File servedFile = new File(context.getRealPath(request.getServletPath()));
    
    if (!servedFile.exists()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    Reader reader = new BufferedReader
        (new InputStreamReader(new FileInputStream(servedFile), CHARSET));
    String mimeType = context.getMimeType(Utils.removeExtension(servedFile));
    
    if (mimeType == null) {
      mimeType = "text/html";
    }
    
    response.setContentType(mimeType + "; charset=" + CHARSET);
    Utils.copyReaderToWriter(reader, response.getWriter(), true);
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
    return "Serves files using UTF-8 as charset";
  }
}
