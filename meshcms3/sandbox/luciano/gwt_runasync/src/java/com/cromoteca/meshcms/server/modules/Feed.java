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
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.toolbox.Strings;

public class Feed extends ServerModule {
	private String path;
	private String title;
	private boolean showPageImages;
	private int numberOfEntries;

	public int getNumberOfEntries() {
		return numberOfEntries;
	}

	public void setNumberOfEntries(int numberOfEntries) {
		this.numberOfEntries = numberOfEntries;
	}

	@Override
	public String getPath() {
		return getAbsoluteDirPath(path).asAbsolute();
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isShowPageImages() {
		return showPageImages;
	}

	public void setShowPageImages(boolean showPageImages) {
		this.showPageImages = showPageImages;
	}

	public String getRssLink() {
		return getRelativeLink(new Path("/meshcms/resources/feed.jsp"));
	}

	public String getTitle() {
		return Strings.noNull(title);
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
