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
import com.cromoteca.meshcms.client.ui.filemanager.ToolsMenu;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.WebSite;
import com.cromoteca.meshcms.server.toolbox.IO;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

public class Upload extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		response.getWriter().write("Servlet per l'upload");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		Path path = null;
		String fileName = null;
		WebSite webSite = Context.getRequestContext().getWebSite();

		try {
			ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream inputStream = item.openStream();

				if (item.isFormField()) {
					if ("dir".equals(name)) {
						path = new Path(Streams.asString(inputStream));
					}
				} else {
					if (path != null && "upfile".equals(name)) {
						String[] dirs = item.getName().split("[\\\\/]");

						if (dirs.length > 0) {
							fileName = IO.generateUniqueName(dirs[dirs.length - 1],
									webSite.getFile(path));

							if (!webSite.saveToFile(inputStream, path.add(fileName))) {
								fileName = null;
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			Context.log("Can't upload in directory " + path, ex);
		}

		response.setContentType("text/html");

		if (fileName == null) {
			response.getWriter().write(ToolsMenu.NO_UPLOAD_MESSAGE);
		}
	}
}
