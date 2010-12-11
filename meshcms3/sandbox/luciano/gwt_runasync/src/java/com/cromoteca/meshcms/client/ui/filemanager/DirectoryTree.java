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

import com.cromoteca.meshcms.client.core.AuthorizableServerCall;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.FileInfo;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class DirectoryTree extends Tree implements OpenHandler<TreeItem>,
	SelectionHandler<TreeItem> {
	private DirectoryTreeItem rootItem;

	public DirectoryTree(Path initialPath) {
		setAnimationEnabled(true);
		addOpenHandler(this);
		addSelectionHandler(this);
		clear();
		FileInfo.ROOT.setName(MeshCMS.CONSTANTS.fmSiteRoot());
		rootItem = new DirectoryTreeItem(FileInfo.ROOT);
		addItem(rootItem);
		setCurrentDir(initialPath, true);
	}

	public void onOpen(OpenEvent<TreeItem> event) {
		final DirectoryTreeItem node = (DirectoryTreeItem) event.getTarget();

		if (node.getState()) {
			final SortedSet<Path> paths = new TreeSet<Path>();
			paths.add(node.getFileInfo().getPath());
			new AuthorizableServerCall<Map<Path, SortedSet<FileInfo>>>() {
					@Override
					public void callServer() {
						MeshCMS.SERVER.getDirContents(paths, 1, false, getAsyncCallback());
					}

					@Override
					public void onResult(Map<Path, SortedSet<FileInfo>> result) {
						node.refresh(result, true);
					}
				}.run();
		}
	}

	public SortedSet<Path> getOpenNodes() {
		SortedSet<Path> paths = new TreeSet<Path>();
		addOpenNodes(paths, (DirectoryTreeItem) getItem(0));

		return paths;
	}

	private static void addOpenNodes(Set<Path> paths, DirectoryTreeItem node) {
		if (node.getState()) {
			paths.add(node.getFileInfo().getPath());

			for (int i = 0; i < node.getChildCount(); i++) {
				addOpenNodes(paths, (DirectoryTreeItem) node.getChild(i));
			}
		} else if (node.getChildCount() == 0) {
			paths.add(node.getFileInfo().getPath());
		}
	}

	public SortedSet<Path> getAllNodes() {
		SortedSet<Path> paths = new TreeSet<Path>();
		addNodes(paths, (DirectoryTreeItem) getItem(0));

		return paths;
	}

	private static void addNodes(Set<Path> paths, DirectoryTreeItem node) {
		paths.add(node.getFileInfo().getPath());

		for (int i = 0; i < node.getChildCount(); i++) {
			TreeItem child = node.getChild(i);

			if (child instanceof DirectoryTreeItem) {
				addNodes(paths, (DirectoryTreeItem) child);
			}
		}
	}

	public abstract void onSelection(SelectionEvent<TreeItem> event);

	public FileInfo getCurrentDir() {
		DirectoryTreeItem dti = (DirectoryTreeItem) getSelectedItem();

		return dti == null ? null : dti.getFileInfo();
	}

	public void setCurrentDir(final Path path, boolean askServer) {
		List<DirectoryTreeItem> breadcrumbs = rootItem.find(path);
		final DirectoryTreeItem dti = breadcrumbs.get(0);

		Path foundPath = dti.getFileInfo().getPath();

		if (askServer && (path.isRoot() || !foundPath.equals(path))) {
			final SortedSet<Path> paths = new TreeSet<Path>();
			Path parent = path;

			while (parent.isContainedIn(foundPath)) {
				paths.add(parent);
				parent = parent.getParent();
			}

			new AuthorizableServerCall<Map<Path, SortedSet<FileInfo>>>() {
					@Override
					public void callServer() {
						MeshCMS.SERVER.getDirContents(paths, 1, false, getAsyncCallback());
					}

					@Override
					public void onResult(Map<Path, SortedSet<FileInfo>> result) {
						dti.refresh(result, true);
						setCurrentDir(path, false);
					}
				}.run();
		} else {
			for (DirectoryTreeItem item : breadcrumbs) {
				item.setState(true, false);
			}

			setSelectedItem(dti, true);
		}
	}

	/**
	 * @return the rootItem
	 */
	public DirectoryTreeItem getRootItem() {
		return rootItem;
	}

	protected void refresh() {
		final SortedSet<Path> openNodes = getOpenNodes();
		new AuthorizableServerCall<Map<Path, SortedSet<FileInfo>>>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getDirContents(openNodes, 1, false, getAsyncCallback());
				}

				@Override
				public void onResult(Map<Path, SortedSet<FileInfo>> result) {
					Path currentPath = getCurrentDir().getPath();
					getRootItem().refresh(result, true);
					setCurrentDir(currentPath, false);
				}
			}.run();
	}
}
