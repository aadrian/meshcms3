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

import com.cromoteca.meshcms.server.core.Context;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AvatarServlet extends HttpServlet {
	public static final int SIZE = 60;
	private static final Color[] colors = {
			Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE,
			Color.PINK, Color.RED, Color.YELLOW
		};
	private static final int STEP = 4;
	private static final int STEP_BITS = STEP * 4;
	private long startupTime;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		BufferedImage image = new BufferedImage(SIZE, SIZE,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		String pathInfo = request.getPathInfo().substring(1, 33);

		for (int i = 0; i < 32; i += STEP) {
			int n = Integer.parseInt(pathInfo.substring(i, i + STEP), 16);
			Color color = colors[getInt(n, 0, 3)];
			int w = getInt(n, 3, 3) + 8;
			int x = getInt(n, 6, 5) * (SIZE - w) / 32;
			int y = getInt(n, 11, 5) * (SIZE - w) / 32;
			graphics.setPaint(color);
			graphics.fillRect(x, y, w, w);
		}

		response.setContentType("image/png");
		ImageIO.write(image, "png", response.getOutputStream());
	}

	private static int getInt(int n, int pos, int len) {
		return (n << (32 - STEP_BITS + pos)) >>> (32 - len);
	}

	@Override
	protected long getLastModified(HttpServletRequest req) {
		if (startupTime == 0L) {
			startupTime = Context.getServer().getStartupTime();
		}

		return startupTime;
	}
}
