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

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.cromoteca.meshcms.client.core.AuthorizableServerCall;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.FileInfo;
import com.cromoteca.meshcms.client.server.FileInfo.PageInfo;
import com.cromoteca.meshcms.client.server.Theme;
import com.cromoteca.meshcms.client.toolbox.Null;
import com.cromoteca.meshcms.client.ui.fields.Field;
import com.cromoteca.meshcms.client.ui.fields.FieldConnector;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.fields.SelectionField;
import com.cromoteca.meshcms.client.ui.widgets.ButtonBar;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PageList extends AbsolutePanel implements FileList {
	private Collection<Theme> themes;
	private FieldList themeEditors;
	private FileManager fileManager;
	private FlowPanel pagesPanel;
	private FlowPanel welcomePanel;
	private List<FileInfo> files;
	private List<Page> pageList;
	private PickupDragController dragController;

	public PageList() {
		buildLayout();
	}

	private void buildLayout() {
		pageList = new ArrayList<Page>();
		setWidth("95%");
		themes = MeshCMS.SITE_INFO.getThemes().values();
		themeEditors = new FieldList();

		FlowPanel mainPanel = new FlowPanel();
		add(mainPanel);
		mainPanel.setStylePrimaryName("mesh-page-list");

		ButtonBar buttonBar = new ButtonBar();
		mainPanel.add(buttonBar);
		buttonBar.add(new Button("^SAVEPAGEORDER^",
				new ClickHandler() {
				public void onClick(ClickEvent event) {
					themeEditors.storeAll();

					int score = 0;
					final List<FileInfo> files = new ArrayList<FileInfo>();

					for (int i = 0; i < welcomePanel.getWidgetCount(); i++) {
						Widget w = welcomePanel.getWidget(i);

						if (w instanceof Page) {
							Page page = (Page) w;
							files.add(page.getFileInfo());
						}
					}

					for (int i = pagesPanel.getWidgetCount() - 1; i >= 0; i--) {
						Widget w = pagesPanel.getWidget(i);

						if (w instanceof Page) {
							Page page = (Page) w;
							FileInfo fileInfo = page.getFileInfo();
							fileInfo.getPageInfo().setScore(score);
							files.add(fileInfo);

							if (page.getSeparator().isActive()) {
								score += 10;
							}
						}
					}

					new AuthorizableServerCall<Null>() {
							@Override
							public void callServer() {
								MeshCMS.SERVER.updatePersistentPageData(files,
										getAsyncCallback());
							}

							@Override
							public void onResult(Null result) {
								fileManager.refreshSiteInfo();
							}
						}.run();
				}
			}));
		buttonBar.add(new Button("^SPLITALL^",
				new ClickHandler() {
				public void onClick(ClickEvent event) {
					toggleSeparators(true);
				}
			}));
		buttonBar.add(new Button("^JOINALL^",
				new ClickHandler() {
				public void onClick(ClickEvent event) {
					toggleSeparators(false);
				}
			}));

		DisclosurePanel dp1 = new DisclosurePanel(MeshCMS.CONSTANTS.pageListWelcome());
		dp1.setOpen(true);
		dp1.setAnimationEnabled(true);
		dp1.add(welcomePanel = new FlowPanel());
		mainPanel.add(dp1);

		DisclosurePanel dp2 = new DisclosurePanel(MeshCMS.CONSTANTS
						.pageListOtherPages());
		dp2.setOpen(true);
		dp2.setAnimationEnabled(true);
		dp2.add(pagesPanel = new FlowPanel());
		pagesPanel.setStylePrimaryName("mesh-page-list-pages");
		mainPanel.add(dp2);
		dragController = new PickupDragController(this, false);

		FlowPanelDropController dropController = new FlowPanelDropController(pagesPanel) {
				@Override
				public void onDrop(DragContext context) {
					super.onDrop(context);

					if (context.draggable instanceof Page) {
						Page dragged = (Page) context.draggable;
						Page previous = null;

						for (int i = 0; i < pagesPanel.getWidgetCount(); i++) {
							Widget w = pagesPanel.getWidget(i);

							if (w instanceof Page) {
								Page page = (Page) w;

								if (dragged.equals(previous) || dragged.equals(page)) {
									page.getSeparator().setActive(true);
								}

								previous = page;
							}
						}

						hideFirstSeparator();
					}
				}
			};

		dragController.registerDropController(dropController);
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	private void hideFirstSeparator() {
		boolean visible = false;

		for (int i = 0; i < pagesPanel.getWidgetCount(); i++) {
			Widget w = pagesPanel.getWidget(i);

			if (w instanceof Page) {
				((Page) w).getSeparator().setVisible(visible);
				visible = true;
			}
		}
	}

	private void toggleSeparators(boolean enable) {
		for (int i = 0; i < pagesPanel.getWidgetCount(); i++) {
			Widget w = pagesPanel.getWidget(i);

			if (w instanceof Page) {
				((Page) w).getSeparator().setActive(enable);
			}
		}
	}

	public FileInfo getSelectedFile() {
		List<FileInfo> selectedFiles = getSelectedFiles();

		return selectedFiles.size() == 1 ? selectedFiles.get(0) : null;
	}

	public List<FileInfo> getSelectedFiles() {
		List<FileInfo> selectedFiles = new ArrayList<FileInfo>();

		for (Page page : pageList) {
			if (page.getStylePrimaryName().equals(STYLE_SELECTED)) {
				selectedFiles.add(page.getFileInfo());
			}
		}

		return selectedFiles;
	}

	public void invertSelection() {
		for (Page child : pageList) {
			if (child.getStylePrimaryName().equals(STYLE_UNSELECTED)) {
				child.setStylePrimaryName(STYLE_SELECTED);
			} else {
				child.setStylePrimaryName(STYLE_UNSELECTED);
			}
		}
	}

	public void selectAll() {
		for (Page child : pageList) {
			child.setStylePrimaryName(STYLE_SELECTED);
		}
	}

	public void selectNone() {
		for (Page child : pageList) {
			child.setStylePrimaryName(STYLE_UNSELECTED);
		}
	}

	public void setFiles(List<FileInfo> files) {
		this.files = files;
		themeEditors.clear();
		welcomePanel.clear();
		pagesPanel.clear();

		List<FileInfo> pages = new ArrayList<FileInfo>();

		for (FileInfo fileInfo : files) {
			PageInfo pageInfo = fileInfo.getPageInfo();

			if (pageInfo != null) {
				if (pageInfo.isWelcome()) {
					welcomePanel.add(new Page(fileInfo));
				} else {
					pages.add(fileInfo);
				}
			}
		}

		Collections.sort(pages,
			new Comparator<FileInfo>() {
				public int compare(FileInfo fi1, FileInfo fi2) {
					return fi2.getPageInfo().getScore() - fi1.getPageInfo().getScore();
				}
			});

		Integer score = null;

		for (FileInfo fileInfo : pages) {
			Page page = new Page(fileInfo);
			pagesPanel.add(page);

			if (score != null) {
				page.getSeparator().setActive(score != fileInfo.getPageInfo().getScore());
			}

			score = fileInfo.getPageInfo().getScore();
		}

		hideFirstSeparator();
	}

	public List<FileInfo> getFiles() {
		return files;
	}

	public Widget asWidget() {
		return this;
	}

	public ImageResource getIcon() {
		return MeshCMS.ICONS_BUNDLE.blogsStack();
	}

	public class Page extends FocusPanel implements ClickHandler {
		private FileInfo fileInfo;
		private Label titleLabel;
		private Separator separator;

		public Page(final FileInfo fileInfo) {
			this.fileInfo = fileInfo;
			setStylePrimaryName(STYLE_UNSELECTED);

			FlowPanel panel = new FlowPanel();
			add(panel);

			if (!fileInfo.getPageInfo().isWelcome()) {
				panel.add(separator = new Separator());

				PushButton dragButton = new PushButton(new Image(
							MeshCMS.ICONS_BUNDLE.arrowMove()));
				dragButton.setStylePrimaryName("mesh-page-list-drag");
				dragButton.setTitle(MeshCMS.CONSTANTS.dragReorderHandlerTooltip());
				panel.add(dragButton);
				dragController.makeDraggable(this, dragButton);
			}

			panel.add(titleLabel = new Label(fileInfo.getPageInfo().getTitle()));
			titleLabel.setTitle(MeshCMS.CONSTANTS.pageListTitleClick());

			Field theme = new SelectionField(MeshCMS.CONSTANTS.pageListTheme()) {
					@Override
					public void populateOptions(ListBox listBox) {
						listBox.addItem("", Theme.INHERITED_THEME.getName());
						listBox.addItem(MeshCMS.CONSTANTS.fmNoTheme(),
							Theme.NO_THEME.getName());

						for (Theme theme : themes) {
							String itemText = MeshCMS.getDynamicTranslation("themes_",
									theme.getName());
							listBox.addItem(itemText, theme.getName());
						}
					}
				};

			theme.setConnector(new FieldConnector() {
					public void storeValue(String value) {
						fileInfo.getPageInfo().setTheme(value);
					}

					public String retrieveValue() {
						return fileInfo.getPageInfo().getTheme();
					}
				});
			themeEditors.add(theme);
			panel.add(theme);
			addClickHandler(this);
			pageList.add(this);
		}

		public FileInfo getFileInfo() {
			return fileInfo;
		}

		public Separator getSeparator() {
			return separator;
		}

		public void onClick(ClickEvent event) {
			EventTarget target = event.getNativeEvent().getEventTarget();

			if (separator != null && target.equals(separator.getElement())) {
				separator.toggle();
			} else if (target.equals(titleLabel.getElement())) {
				if (getStylePrimaryName().equals(STYLE_UNSELECTED)) {
					setStylePrimaryName(STYLE_SELECTED);
				} else {
					setStylePrimaryName(STYLE_UNSELECTED);
				}
			}
		}
	}

	public static class Separator extends FlowPanel {
		private boolean active;

		public Separator() {
			setActive(false);
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
			setStylePrimaryName(active ? "mesh-separator-active"
				: "mesh-separator-inactive");
		}

		private void toggle() {
			setActive(!active);
		}
	}
}
