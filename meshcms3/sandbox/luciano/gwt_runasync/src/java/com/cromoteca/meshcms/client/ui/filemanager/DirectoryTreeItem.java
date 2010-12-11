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
package com.cromoteca.meshcms.client.ui.filemanager;

import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.CMSDirectoryItem;
import com.cromoteca.meshcms.client.server.FileInfo;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class DirectoryTreeItem extends TreeItem {
	public DirectoryTreeItem(FileInfo fileInfo) {
		super();
		setHTML(getLabel(fileInfo));
		setUserObject(fileInfo);
		addItem("");
	}

	public FileInfo getFileInfo() {
		return (FileInfo) getUserObject();
	}

	public List<DirectoryTreeItem> find(Path path) {
		List<DirectoryTreeItem> result = null;

		for (int i = 0; i < getChildCount(); i++) {
			TreeItem ti = getChild(i);

			if (ti instanceof DirectoryTreeItem) {
				DirectoryTreeItem dti = (DirectoryTreeItem) ti;

				if (path.isContainedIn(dti.getFileInfo().getPath())) {
					result = dti.find(path);
				}
			}
		}

		if (path.isContainedIn(getFileInfo().getPath())) {
			if (result == null) {
				result = new ArrayList<DirectoryTreeItem>();
			}

			result.add(this);
		}

		return result;
	}

	public void refresh(Map<Path, SortedSet<FileInfo>> data, boolean recursive) {
		Path path = getFileInfo().getPath();
		SortedSet<FileInfo> dirData = data.get(path);
		Map<Path, DirectoryTreeItem> subItemMap = new HashMap<Path, DirectoryTreeItem>();

		if (isNew()) {
			removeItems();
		}

		if (dirData != null) {
			for (int i = 0; i < getChildCount(); i++) {
				TreeItem child = getChild(i);

				if (child instanceof DirectoryTreeItem) {
					DirectoryTreeItem item = (DirectoryTreeItem) child;
					subItemMap.put(item.getFileInfo().getPath(), item);
				}
			}

			for (FileInfo newInfo : dirData) {
				DirectoryTreeItem item = subItemMap.get(newInfo.getPath());

				if (item == null) {
					item = new DirectoryTreeItem(newInfo);
					addItem(item);
				} else {
					subItemMap.remove(newInfo.getPath());
				}

				if (recursive) {
					item.refresh(data, recursive);
				}
			}

			for (DirectoryTreeItem item : subItemMap.values()) {
				removeItem(item);
			}
		}
	}

	public boolean isNew() {
		return getChildCount() == 1 && getChild(0).getText().length() == 0;
	}

	public static String getLabel(FileInfo fileInfo) {
		CMSDirectoryItem cmsDirectoryItem = MeshCMS.SITE_INFO.getCMSDirectoryItem(fileInfo);

		return AbstractImagePrototype.create(cmsDirectoryItem.getIcon()).getHTML()
		+ ' ' + cmsDirectoryItem.getLabel();
	}
}
