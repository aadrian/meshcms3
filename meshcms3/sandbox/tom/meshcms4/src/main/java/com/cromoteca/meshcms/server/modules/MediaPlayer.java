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

import com.cromoteca.meshcms.client.server.FileTypes;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayer extends ServerModule {
	private List<PlaylistItem> items;
	private Path dirPath;
	private String order;
	private boolean showImages;
	private boolean showSongs;
	private boolean showVideos;
	private boolean wide;

	public void setShowImages(boolean showImages) {
		this.showImages = showImages;
	}

	public void setShowSongs(boolean showSongs) {
		this.showSongs = showSongs;
	}

	public void setShowVideos(boolean showVideos) {
		this.showVideos = showVideos;
	}

	public void setWide(boolean wide) {
		this.wide = wide;
	}

	public void setDirectory(String dir) {
		dirPath = getAbsoluteDirPath(dir);
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public List<PlaylistItem> getItems() {
		return collectItems();
	}

	public boolean isHasItems() {
		return collectItems().size() > 0;
	}

	public String getStyle() {
		return wide ? "mesh-media-player-wide" : "mesh-media-player-normal";
	}

	private List<PlaylistItem> collectItems() {
		if (items == null) {
			List<String> exts = new ArrayList<String>();

			if (showImages) {
				exts.add("jpg");
				exts.add("png");
				exts.add("gif");
			}

			if (showSongs) {
				exts.add("mp3");
			}

			if (showVideos) {
				exts.add("flv");
			}

			items = new ArrayList<PlaylistItem>();

			List<File> files = collectFiles(dirPath,
					exts.toArray(new String[exts.size()]), order);

			for (File file : files) {
				items.add(new PlaylistItem(dirPath.add(file.getName())));
			}
		}

		return items;
	}

	public class PlaylistItem {
		Path path;
		String name;

		public PlaylistItem(Path path) {
			this.path = path;
			name = Strings.beautify(Strings.removeExtension(path.getLastElement()),
					true);
		}

		public String getLink() {
			return getRelativeLink(path);
		}

		public String getName() {
			return name;
		}

		public String getIcon() {
			// TODO: remove static path meshcms
			Path iconPath = new Path("/meshcms/resources/lib/icons",
					FileTypes.getIcon(path.getLastElement()));

			return getRelativeLink(iconPath);
		}

		public String getFileType() {
			return FileTypes.getDescription(path.getLastElement());
		}
	}
}
