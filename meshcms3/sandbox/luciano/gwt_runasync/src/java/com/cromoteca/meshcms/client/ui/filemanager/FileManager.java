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
import com.cromoteca.meshcms.client.server.CMSDirectoryItem;
import com.cromoteca.meshcms.client.server.FileInfo;
import com.cromoteca.meshcms.client.server.SiteInfo;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.ui.controlpanel.FileManagerPanel;
import com.cromoteca.meshcms.client.ui.widgets.ButtonBar;
import com.cromoteca.meshcms.client.ui.widgets.Popup;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class FileManager extends DockLayoutPanel {
	public static final int SELECT_FILES = 0;
	public static final int SELECT_DIRS = 1;
	public static final int SELECT_BOTH = 2;
	private ButtonBar buttonBar;
	private DirectoryTree directoryTree;
	private FileClipboard clipboard;
	private FileList fileList;
	private MainMenuBar menuBar;
	private SelectionHandler selectionHandler;
	private SplitLayoutPanel split;
	private TabLayoutPanel tabs;

	public FileManager(FileList fileList) {
		this(fileList, null);
	}

	public FileManager(FileList fileList, SelectionHandler selectionHandler) {
		super(Unit.PX);
		fileList.setFileManager(this);
		this.fileList = fileList;
		this.selectionHandler = selectionHandler;
		buildLayout();
	}

	public void setFileList(FileList newFileList) {
		newFileList.setFileManager(this);
		newFileList.setFiles(fileList.getFiles());
		fileList = newFileList;

		int lastTabIndex = tabs.getWidgetCount() - 1;
		tabs.remove(lastTabIndex);
		addFileListTab();
		tabs.selectTab(lastTabIndex);
	}

	private void addFileListTab() {
		tabs.add(new ScrollPanel(fileList.asWidget()), getFileListTabCaption(), true);
	}

	protected String getFileListTabCaption() {
		return AbstractImagePrototype.create(fileList.getIcon()).getHTML() + ' '
		+ Strings.limitedLength(getCurrentPath().asAbsolute(), 48, "\u2026 ", false);
	}

	private void buildLayout() {
		clipboard = new FileClipboard();
		// menu bar
		menuBar = new MainMenuBar(this);
		addNorth(menuBar, 24);
		// split between dir tree and dir contents
		split = new SplitLayoutPanel();

		// dir tree
		directoryTree = new DirectoryTree(new Path(Window.Location.getParameter(
						"dir"))) {
					@Override
					public void onSelection(SelectionEvent<TreeItem> event) {
						while (tabs.getWidgetCount() > 1) {
							tabs.remove(0);
						}

						FileInfo fileInfo = ((DirectoryTreeItem) event.getSelectedItem())
									.getFileInfo();

						if (!isBrowser()
									&& fileInfo.getPath()
									.isContainedIn(MeshCMS.SITE_INFO.getCMSPath())) {
							CMSDirectoryItem cmsDirectoryItem = MeshCMS.SITE_INFO
										.getCMSDirectoryItem(fileInfo.getPath());

							if (cmsDirectoryItem != null) {
								FileManagerPanel fmp = cmsDirectoryItem.getFileManagerPanel();

								if (fmp != null) {
									fmp.setFileManager(FileManager.this);
									tabs.insert(new ScrollPanel(fmp.asWidget()),
										AbstractImagePrototype.create(cmsDirectoryItem.getIcon())
												.getHTML() + ' ' + fmp.getTabCaption(), true, 0);
								}
							}
						}

						loadFileList(fileInfo.getPath());

						// TODO: remove workaround for tab selection when fixed in GWT
						if (tabs.getWidgetCount() > 1) {
							tabs.selectTab(1);
						}

						tabs.selectTab(0);
					}
				};
		split.addWest(new ScrollPanel(getDirectoryTree()), 300);
		// dir contents
		tabs = new TabLayoutPanel(40, Unit.PX);
		split.add(tabs);
		addFileListTab();
		tabs.selectTab(0);

		if (selectionHandler != null) {
			buttonBar = new ButtonBar();
			addSouth(buttonBar, ButtonBar.HEIGHT);
			buttonBar.add(new Button(MeshCMS.CONSTANTS.genericSelect(),
					new ClickHandler() {
					public void onClick(ClickEvent event) {
						List<FileInfo> selectedFiles = fileList.getSelectedFiles();
						final List<Path> paths = new ArrayList<Path>();

						if (selectedFiles != null) {
							for (FileInfo fileInfo : selectedFiles) {
								boolean include;

								switch (selectionHandler.getSelectionMode()) {
									case SELECT_DIRS:
										include = fileInfo.isDirectory();

										break;

									case SELECT_FILES:
										include = !fileInfo.isDirectory();

										break;

									default:
										include = true;
								}

								if (include) {
									paths.add(fileInfo.getPath());
								}
							}
						}

						if (paths.size() == 0
										&& selectionHandler.getSelectionMode() != SELECT_FILES) {
							paths.add(getCurrentPath());
							setSelection(paths);
						} else if (paths.size() > 1) {
							Popup.getAlertBox(MeshCMS.CONSTANTS.msgSingleFile())
										.showDialog(null);
						} else {
							setSelection(paths);
						}
					}

					private void setSelection(List<Path> paths) {
						if (selectionHandler.handleSelection(paths.get(0))) {
							selectionHandler.close();
						}
					}
				}));
			buttonBar.add(new Button(MeshCMS.CONSTANTS.genericClose(),
					new ClickHandler() {
					public void onClick(ClickEvent event) {
						selectionHandler.close();
					}
				}));
		}

		add(split);
	}

	public boolean isBrowser() {
		return selectionHandler != null;
	}

	public void loadFileList(final Path path) {
		final SortedSet<Path> paths = new TreeSet<Path>();
		paths.add(path);
		new AuthorizableServerCall<Map<Path, SortedSet<FileInfo>>>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getDirContents(paths, 0, true, getAsyncCallback());
				}

				@Override
				public void onResult(Map<Path, SortedSet<FileInfo>> result) {
					List<FileInfo> list = new ArrayList<FileInfo>();
					list.addAll(result.get(path));
					fileList.setFiles(list);
					tabs.setTabHTML(tabs.getWidgetCount() - 1, getFileListTabCaption());
				}
			}.run();
	}

	public Path getCurrentPath() {
		FileInfo currentDir = directoryTree.getCurrentDir();

		return currentDir == null ? Path.ROOT : currentDir.getPath();
	}

	/**
	 * @return the directoryTree
	 */
	public DirectoryTree getDirectoryTree() {
		return directoryTree;
	}

	/**
	 * @return the fileList
	 */
	public FileList getFileList() {
		return fileList;
	}

	/**
	 * @return the clipboard
	 */
	public FileClipboard getClipboard() {
		return clipboard;
	}

	public void refreshView() {
		directoryTree.refresh();
	}

	public void refreshSiteInfo() {
		new AuthorizableServerCall<SiteInfo>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getSiteInfo(true, getAsyncCallback());
				}

				@Override
				public void onResult(SiteInfo result) {
					MeshCMS.storeSiteInfo(result);
					refreshView();
				}
			}.run();
	}

	public void refreshFullPage() {
		String href = Window.Location.getHref();

		if (href.indexOf("?dir=") < 0 && href.indexOf("&dir=") < 0) {
			href += (href.indexOf('?') < 0 ? '?' : '&') + "dir=" + getCurrentPath();
		} else {
			href = href.replaceAll("([\\?&]dir=)([^\\?&]*)", "$1" + getCurrentPath());
		}

		Window.Location.assign(href);
	}

	public static interface SelectionHandler {
		int getSelectionMode();

		boolean handleSelection(Path path);

		void close();
	}
}
