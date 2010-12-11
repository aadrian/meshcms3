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
import com.cromoteca.meshcms.server.storage.File;

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
					|| thumbnailFile.getLastModified() < imageFile.getLastModified()) {
			thumbnailFile.getParent().create(true);

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
}
