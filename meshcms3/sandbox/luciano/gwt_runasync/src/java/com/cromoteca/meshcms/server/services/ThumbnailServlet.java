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
import com.cromoteca.meshcms.client.ui.filemanager.ImageList;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.webui.ResizedThumbnail;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThumbnailServlet extends HttpServlet {
	public static final Pattern SIZE_TYPE_PATTERN = Pattern.compile(
			"(\\d+)_(\\d+)_(scale|crop|padding|stretch)");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		ResizedThumbnail thumbnailMaker = new ResizedThumbnail();
		thumbnailMaker.setHighQuality(Context.getServer().getServerConfiguration()
					.isHighQualityThumbnails());

		Path imagePath = new Path(request.getPathInfo());
		String elm = imagePath.getElementAt(0);
		Matcher m = SIZE_TYPE_PATTERN.matcher(elm);

		if (m.matches()) {
			int w = Integer.parseInt(m.group(1));

			if (w > 0) {
				thumbnailMaker.setWidth(w);
			}

			int h = Integer.parseInt(m.group(2));

			if (h > 0) {
				thumbnailMaker.setHeight(h);
			}

			thumbnailMaker.setMode(m.group(3));
			imagePath = imagePath.getRelativeTo(new Path(elm));
		} else {
			thumbnailMaker.setWidth(ImageList.THUMB_SIZE);
			thumbnailMaker.setHeight(ImageList.THUMB_SIZE);
			thumbnailMaker.setMode(ResizedThumbnail.MODE_SCALE);
		}

		Path thumbPath = thumbnailMaker.checkAndCreate(imagePath);
		Path rootPath = Context.getRequestContext().getWebSite().getRootPath();
		request.getRequestDispatcher(rootPath.add(thumbPath).asLink())
				.forward(request, response);
	}
}
