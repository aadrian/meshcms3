/*
 * Copyright 2004-2010 Luciano Vernaschi
 *
 * This file is part of MeshCMS.
 *
 * MeshCMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MeshCMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cromoteca.meshcms.server.services;

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.SessionUser;
import com.cromoteca.meshcms.server.core.WebSite;
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A simple servlet to allow to download a file regardless of its type.
 */
public final class DownloadServlet extends HttpServlet {
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 */
	static void processRequest(HttpServletRequest request,
		HttpServletResponse response) throws IOException {
		WebSite webSite = Context.getRequestContext().getWebSite();
		Path path = new Path(request.getPathInfo());

		if (webSite.isSystem(path)
					|| webSite.isHidden(path, true)
					|| path.isContainedIn(Context.getServer().getCMSPath())) {
			/* Only authenticated users can download system files */
			SessionUser user = Context.getUser();

			if (user == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);

				return;
			}
		}

		File file = webSite.getFile(path);

		if (!file.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
				"File not found on server");

			return;
		}

		String fileName = request.getParameter("filename");

		if (Strings.isNullOrEmpty(fileName)) {
			fileName = path.getLastElement();
		}

		try {
			InputStream is = file.getInputStream();
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition",
				"attachment; filename=\"" + fileName + "\"");
			response.setHeader("Content-Length", Long.toString(file.getLength()));
			IO.copyStream(is, response.getOutputStream(), false);
		} catch (IOException ex) {
			if (!response.isCommitted()) {
				response.resetBuffer();
				response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"File not found on server");
			}
		}
	}
}
