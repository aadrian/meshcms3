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
package com.cromoteca.meshcms.server.modules;

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.webui.GalleryThumbnail;
import java.util.ArrayList;
import java.util.List;

public class Gallery extends ServerModule {
	public static final String[] EXTENSIONS = { "jpg", "png", "gif" };
	private GalleryThumbnail thumbMaker;
	private List<Image> images;
	private String order;
	private String path;
	private boolean captions;
	private boolean useColorBox;

	public Gallery() {
		thumbMaker = new GalleryThumbnail();
		thumbMaker.setHighQuality(Context.getServer().getServerConfiguration()
					.isHighQualityThumbnails());
		captions = true;
	}

	public boolean isUseColorBox() {
		return useColorBox;
	}

	public void setUseColorBox(boolean useColorBox) {
		this.useColorBox = useColorBox;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setCaptions(boolean captions) {
		this.captions = captions;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setQuality(String quality) {
		if ("high".equals(quality)) {
			thumbMaker.setHighQuality(true);
		} else if ("low".equals(quality)) {
			thumbMaker.setHighQuality(false);
		}
	}

	public List<Image> getImages() {
		return collectImages();
	}

	public boolean isHasImages() {
		return collectImages().size() > 0;
	}

	public boolean isPrintCaptions() {
		return captions;
	}

	private List<Image> collectImages() {
		if (images == null) {
			images = new ArrayList<Image>();

			Path imagePath = getAbsoluteDirPath(path);
			List<File> files = collectFiles(imagePath, EXTENSIONS, order);

			for (File file : files) {
				images.add(new Image(imagePath.add(file.getName())));
			}
		}

		return images;
	}

	public class Image {
		Path path;
		String name;

		public Image(Path path) {
			this.path = path;
			name = Strings.beautify(Strings.removeExtension(path.getLastElement()),
					true);
		}

		public String getLink() {
			return getRelativeLink(path);
		}

		public String getThumbnail() {
			Path thumbPath = thumbMaker.checkAndCreate(path);

			if (thumbPath == null) {
				thumbPath = path;
			}

			return getRelativeLink(thumbPath);
		}

		public String getName() {
			return name;
		}
	}
}
