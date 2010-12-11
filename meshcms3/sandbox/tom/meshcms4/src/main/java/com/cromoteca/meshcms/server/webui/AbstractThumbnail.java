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
package com.cromoteca.meshcms.server.webui;

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.WebSite;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class AbstractThumbnail {
	private boolean highQuality;

	public AbstractThumbnail() {}

	/**
	 * Checks the current thumbnail, and creates it if not available or too old.
	 */
	public Path checkAndCreate(Path imagePath) {
		WebSite webSite = Context.getRequestContext().getWebSite();
		File imageFile = webSite.getFile(imagePath);

		if (!imageFile.exists() || imageFile.isDirectory()) {
			return null;
		}

		Path thumbnailPath = webSite.getRepositoryPath(imagePath)
					.add(getClass().getName(), getFileName());
		File thumbnailFile = webSite.getFile(thumbnailPath);

		if (!thumbnailFile.exists()
					|| thumbnailFile.lastModified() < imageFile.lastModified()) {
			thumbnailFile.getParentFile().mkdirs();

			if (!createThumbnail(imageFile, thumbnailFile)) {
				return null;
			}
		}

		return thumbnailPath;
	}

	public boolean isHighQuality() {
		return highQuality;
	}

	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
	}

	protected abstract boolean createThumbnail(File imageFile, File thumbnailFile);

	public abstract String getFileName();

	/**
	 * Resizes an image using a simple area average (suitable to create images that
	 * are much smaller than the original).
	 */
	public static BufferedImage averageResize(BufferedImage in, int width,
		int height) {
		checkImageSize(width, height);

		int imageWidth = in.getWidth();
		int imageHeight = in.getHeight();
		int[] pixels = in.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth);
		int[] outPixels = new int[width * height];
		int nw = Math.max(imageWidth / width + 1, 2);
		int nh = Math.max(imageHeight / height + 1, 2);
		int idx;
		int i2;
		int j2;
		int argb;
		int cnt;
		int[] v = new int[3];
		float kw = (float) imageWidth / (float) width;
		float kh = (float) imageHeight / (float) height;
		float nw2 = nw / 2.0F;
		float nh2 = nh / 2.0F;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				v[0] = v[1] = v[2] = cnt = 0;
				i2 = (int) ((i + 0.5F) * kh - nh2);
				j2 = (int) ((j + 0.5F) * kw - nw2);

				for (int k = 0; k < nh; k++) {
					for (int l = 0; l < nw; l++) {
						idx = j2 + l + (i2 + k) * imageWidth;

						if (idx > -1 && idx < pixels.length) {
							argb = pixels[idx];
							v[0] += argb >> 16 & 0xFF;
							v[1] += argb >> 8 & 0xFF;
							v[2] += argb & 0xFF;
							cnt++;
						}
					}
				}

				outPixels[j + i * width] = 0xFF000000 | v[0] / cnt << 16
							| v[1] / cnt << 8 | v[2] / cnt;
			}
		}

		BufferedImage out = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		out.setRGB(0, 0, width, height, outPixels, 0, width);

		return out;
	}

	private static void checkImageSize(int width, int height) {
		if (width < 1) {
			throw new IllegalArgumentException("Invalid width: " + width + " pixels");
		}

		if (height < 1) {
			throw new IllegalArgumentException("Invalid height: " + height
				+ " pixels");
		}
	}

	public static void drawResizedImage(Graphics g, BufferedImage image, int x,
		int y, int width, int height, boolean highQuality) {
		if (highQuality) {
			BufferedImage resized = resize(image, width, height);
			g.drawImage(resized, x, y, null);
			resized.flush();
		} else {
			g.drawImage(image, x, y, width, height, null);
		}
	}

	/**
	 * Resizes an image using a simple linear interpolation (suitable to resize to
	 * a size similar to the original one).
	 */
	public static BufferedImage linearResize(BufferedImage in, int width,
		int height) {
		checkImageSize(width, height);

		int imageWidth = in.getWidth();
		int imageHeight = in.getHeight();
		int[] pixels = in.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth);
		int[] outPixels = new int[width * height];
		int a;
		int r;
		int g;
		int b;
		int ox;
		int x0;
		int x1;
		int oy;
		int y0;
		int y1;
		int d = width * height;
		int p11;
		int p10;
		int p01;
		int p00;
		int idx;

		for (int py = 0; py < height; py++) {
			for (int px = 0; px < width; px++) {
				idx = py * width + px;
				ox = px * imageWidth;
				x0 = ox % width;
				x1 = width - x0;
				ox /= width;
				oy = py * imageHeight;
				y0 = oy % height;
				y1 = height - y0;
				oy /= height;

				if (ox >= imageWidth - 1 || oy >= imageHeight - 1) {
					outPixels[idx] = pixels[oy * imageWidth + ox];
				} else {
					p11 = pixels[(oy + 1) * imageWidth + ox + 1];
					p10 = pixels[oy * imageWidth + ox + 1];
					p01 = pixels[(oy + 1) * imageWidth + ox];
					p00 = pixels[oy * imageWidth + ox];
					a = (x0 * y0 * (p11 >> 24 & 0xFF) + x0 * y1 * (p10 >> 24 & 0xFF)
						+ x1 * y0 * (p01 >> 24 & 0xFF) + x1 * y1 * (p00 >> 24 & 0xFF)) / d;
					r = (x0 * y0 * (p11 >> 16 & 0xFF) + x0 * y1 * (p10 >> 16 & 0xFF)
						+ x1 * y0 * (p01 >> 16 & 0xFF) + x1 * y1 * (p00 >> 16 & 0xFF)) / d;
					g = (x0 * y0 * (p11 >> 8 & 0xFF) + x0 * y1 * (p10 >> 8 & 0xFF)
						+ x1 * y0 * (p01 >> 8 & 0xFF) + x1 * y1 * (p00 >> 8 & 0xFF)) / d;
					b = (x0 * y0 * (p11 & 0xFF) + x0 * y1 * (p10 & 0xFF)
						+ x1 * y0 * (p01 & 0xFF) + x1 * y1 * (p00 & 0xFF)) / d;
					outPixels[idx] = a << 24 | r << 16 | g << 8 | b;
				}
			}
		}

		BufferedImage out = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		out.setRGB(0, 0, width, height, outPixels, 0, width);

		return out;
	}

	/**
	 * Resizes an image. The method used is chosen according to the ratio between
	 * original and new size.
	 */
	public static BufferedImage resize(BufferedImage in, int width, int height) {
		checkImageSize(width, height);

		int imageWidth = in.getWidth();
		int imageHeight = in.getHeight();

		return imageWidth * imageHeight / (width * height) > 4
		? averageResize(in, width, height) : linearResize(in, width, height);
	}
}