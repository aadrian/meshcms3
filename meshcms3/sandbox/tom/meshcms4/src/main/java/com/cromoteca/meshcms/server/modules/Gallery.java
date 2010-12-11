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
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.webui.ResizedThumbnail;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Gallery extends ServerModule {
	public static final String[] EXTENSIONS = { "jpg", "png", "gif" };
	private List<Image> images;
	private ResizedThumbnail thumbMaker;
	private String order;
	private String path;
	private boolean captions;
	private boolean useColorBox;
	private int thumbSize;

	public Gallery() {
		captions = true;
	}

	public void setThumbSize(int thumbSize) {
		this.thumbSize = thumbSize;
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

			for (File file : collectFiles(imagePath, EXTENSIONS, order)) {
				images.add(new Image(imagePath.add(file.getName())));
			}
		}

		return images;
	}

	private ResizedThumbnail getThumbMaker() {
		if (thumbMaker == null) {
			thumbMaker = new ResizedThumbnail();

			if (thumbSize <= 0) {
				thumbSize = ResizedThumbnail.DEFAULT_SIZE;
			}

			thumbMaker.setWidth(thumbSize);
			thumbMaker.setHeight(thumbSize);
			thumbMaker.setMode(ResizedThumbnail.MODE_SCALE);
			thumbMaker.setHighQuality(Context.getServer().getServerConfiguration()
						.isHighQualityThumbnails());
		}

		return thumbMaker;
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
			Path thumbPath = getThumbMaker().checkAndCreate(path);

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
