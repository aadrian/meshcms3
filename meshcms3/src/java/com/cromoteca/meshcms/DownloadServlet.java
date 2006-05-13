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

package com.cromoteca.meshcms;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.cromoteca.util.*;

/**
 * A simple servlet to allow to download a file regardless of its type.
 */
public final class DownloadServlet extends HttpServlet {
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }
  
  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // Do not restrict usage to registered users.
    /* UserInfo userInfo = (UserInfo)
        request.getSession(true).getAttribute("userInfo");

    if (userInfo == null || userInfo.isGuest()) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN,
          "You don't have enough privileges");
      return;
    } */
    
    WebSite webSite = (WebSite) getServletContext().getAttribute("webSite");
    Path path = new Path(request.getPathInfo());

    if (webSite.isSystem(path, true)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN,
          "You are not allowed to download this file");
      return;
    }
    
    File file = webSite.getFile(path);
    
    if (!file.exists()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND,
          "File not found on server");
      return;
    }

    String fileName = request.getParameter("filename");

    if (Utils.isNullOrEmpty(fileName)) {
      fileName = path.getLastElement();
    }

    try {
      InputStream is = new FileInputStream(file);
      response.setContentType("application/x-download");
      response.setHeader("Content-Disposition", "attachment; filename=\"" + 
          fileName + "\"");
      response.setHeader("Content-Length", Long.toString(file.length()));
      Utils.copyStream(is, response.getOutputStream(), false);
    } catch (IOException ex) {
      if (!response.isCommitted()) {
        response.resetBuffer();
        response.sendError(HttpServletResponse.SC_NOT_FOUND,
            "File not found on server");
      }
    }
  }
}
