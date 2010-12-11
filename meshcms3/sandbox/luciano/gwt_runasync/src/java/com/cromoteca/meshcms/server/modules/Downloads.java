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
import com.cromoteca.meshcms.client.ui.fields.StringArrayConnector;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.core.WebUtils;
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.toolbox.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Downloads extends ServerModule {
	private List<Download> items;
	private Path directory;
	private String order;
	private String path;
	private String[] extensions;
	private boolean beautify;
	private boolean force;

	public void setPath(String path) {
		this.path = path;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setBeautify(boolean beautify) {
		this.beautify = beautify;
	}

	public void setExtensions(String exts) {
		extensions = Strings.split(exts,
				StringArrayConnector.DEFAULT_SPACERS_REGEX, true);

		if (extensions != null) {
			Arrays.sort(extensions);
		}
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public List<Download> getItems() {
		return collect();
	}

	public boolean isHasItems() {
		return collect().size() > 0;
	}

	private List<Download> collect() {
		if (items == null) {
			items = new ArrayList<Download>();
			directory = getAbsoluteDirPath(path);

			List<File> files = collectFiles(directory, extensions, order);

			for (File file : files) {
				items.add(new Download(file));
			}
		}

		return items;
	}

	public class Download {
		File file;
		Path path;
		String fileName;
		String name;

		public Download(File file) {
			this.file = file;
			fileName = file.getName();
			path = directory.add(fileName);
			name = beautify
				? Strings.beautify(Strings.removeExtension(fileName), true) : fileName;
		}

		public String getLink() {
			// TODO: remove static path meshcms
			Path down = force ? new Path("meshcms/download").add(path) : path;

			return getRelativeLink(down);
		}

		public String getName() {
			return name;
		}

		public Date getDate() {
			return new Date(file.getLastModified());
		}

		public String getIcon() {
			// TODO: remove static path meshcms
			Path iconPath = new Path("/meshcms/resources/lib/icons",
					FileTypes.getIcon(fileName));

			return getRelativeLink(iconPath);
		}

		public String getFileType() {
			return FileTypes.getDescription(fileName);
		}

		public String getSize() {
			return WebUtils.formatFileLength(file.getLength());
		}

		public long getLastModified() {
			return file.getLastModified();
		}
	}
}
