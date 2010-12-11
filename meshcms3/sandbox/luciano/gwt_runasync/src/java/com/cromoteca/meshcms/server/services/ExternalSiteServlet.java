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
import com.cromoteca.meshcms.server.core.Server;
import com.cromoteca.meshcms.server.core.WebSite;
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExternalSiteServlet extends HttpServlet {
	public static final Path SERVLET_PATH = new Path("meshcms/external");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		process(request, response);
	}

	private void process(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		File file = getFile(request);

		if (file != null) {
			String ext = Strings.getExtension(file.getName(), false);
			Server server = Context.getServer();

			if ("jsp".equalsIgnoreCase(ext) || "ftl".equalsIgnoreCase(ext)) {
				Path tmpPath = new Path("externalScriptFiles");
				WebSite mainWebSite = server.getMainWebSite();
				tmpPath = mainWebSite.getRepositoryPath(tmpPath);

				URL url = Context.getURL();
				String tmpFileName = Strings.getMD5(url.getHost() + url.getPath())
					+ '.' + ext;
				File tmpFile = mainWebSite.getFile(tmpPath).getDescendant(tmpFileName);

				if (!(tmpFile.exists()
							&& tmpFile.getLastModified() == file.getLastModified()
							&& tmpFile.getLength() == file.getLength())) {
					IO.copyFile(file, tmpFile, true, true);
				}

				request.getRequestDispatcher(tmpPath.add(tmpFileName).asAbsolute())
						.forward(request, response);

				return;
			}

			if (server.isPage(file.getName())) {
				response.setContentType("text/html");
			} else {
				response.setContentType(getServletContext().getMimeType(file.getName()));
			}

			response.setStatus(HttpServletResponse.SC_OK);

			InputStream fis = file.getInputStream();
			IO.copyStream(fis, response.getOutputStream(), false);
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	protected long getLastModified(HttpServletRequest request) {
		File file = getFile(request);

		return file == null ? System.currentTimeMillis() : file.getLastModified();
	}

	/**
	 * Get the file to serve (non-existent files are returned as null)
	 */
	private static File getFile(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();

		if (pathInfo.startsWith("/")) {
			pathInfo = pathInfo.substring(1);
		}

		int slash = pathInfo.indexOf('/');

		if (slash >= 0) {
			String domain = pathInfo.substring(0, slash);
			WebSite webSite = Context.getServer().getMultiSiteManager()
						.getWebSite(domain);

			if (webSite != null) {
				Path pagePath = new Path(pathInfo.substring(slash));
				File file = webSite.getFile(pagePath);

				if (file.exists()) {
					return file;
				}
			}
		}

		return null;
	}
}
