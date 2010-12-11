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
import com.cromoteca.meshcms.client.server.FileInfo;
import com.cromoteca.meshcms.client.server.FileTypes;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;

public class ImageList extends FocusPanel implements FileList {
/**
   * Width and height of the image in the thumbnail.
   */
	public static final int THUMB_SIZE = 108;
	private FileManager fileManager;
	private FlowPanel flowPanel;
	private List<FileInfo> files;

	public ImageList() {
		setWidth("95%");
		add(flowPanel = new FlowPanel());
		flowPanel.setStylePrimaryName("mesh-filemanager-thumbnails");
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public FileInfo getSelectedFile() {
		List<FileInfo> selectedFiles = getSelectedFiles();

		return selectedFiles.size() == 1 ? selectedFiles.get(0) : null;
	}

	public List<FileInfo> getSelectedFiles() {
		List<FileInfo> selectedFiles = new ArrayList<FileInfo>();

		for (int i = 0; i < flowPanel.getWidgetCount(); i++) {
			Widget child = flowPanel.getWidget(i);

			if (child instanceof Thumbnail) {
				if (child.getStylePrimaryName().equals(STYLE_SELECTED)) {
					selectedFiles.add(((Thumbnail) child).getFileInfo());
				}
			}
		}

		return selectedFiles;
	}

	public void invertSelection() {
		for (int i = 0; i < flowPanel.getWidgetCount(); i++) {
			Widget child = flowPanel.getWidget(i);

			if (child instanceof Thumbnail) {
				if (child.getStylePrimaryName().equals(STYLE_UNSELECTED)) {
					child.setStylePrimaryName(STYLE_SELECTED);
				} else {
					child.setStylePrimaryName(STYLE_UNSELECTED);
				}
			}
		}
	}

	public void selectAll() {
		for (int i = 0; i < flowPanel.getWidgetCount(); i++) {
			Widget child = flowPanel.getWidget(i);

			if (child instanceof Thumbnail) {
				child.setStylePrimaryName(STYLE_SELECTED);
			}
		}
	}

	public void selectNone() {
		for (int i = 0; i < flowPanel.getWidgetCount(); i++) {
			Widget child = flowPanel.getWidget(i);

			if (child instanceof Thumbnail) {
				child.setStylePrimaryName(STYLE_UNSELECTED);
			}
		}
	}

	public void setFiles(List<FileInfo> files) {
		this.files = files;
		flowPanel.clear();

		for (FileInfo fileInfo : files) {
			if (!fileInfo.isDirectory() && FileTypes.isWebImage(fileInfo.getName())) {
				flowPanel.add(new Thumbnail(fileInfo));
			}
		}
	}

	public ImageList asWidget() {
		return this;
	}

	public List<FileInfo> getFiles() {
		return files;
	}

	public ImageResource getIcon() {
		return MeshCMS.ICONS_BUNDLE.imagesStack();
	}

	private class Thumbnail extends FocusPanel implements ClickHandler {
		private FileInfo fileInfo;

		public Thumbnail(FileInfo fileInfo) {
			this.fileInfo = fileInfo;
			setStylePrimaryName(STYLE_UNSELECTED);

			FlowPanel panel = new FlowPanel();
			add(panel);

			FlowPanel imageFrame = new FlowPanel();
			imageFrame.setStylePrimaryName("mesh-filemanager-thumbnail");
			panel.add(imageFrame);

			// TODO: remove static path /meshcms
			Image image = new Image(MeshCMS.CONTEXT_PATH + "/meshcms/thumbnails/"
					+ THUMB_SIZE + '_' + THUMB_SIZE + "_scale/" + fileInfo.getPath());
			imageFrame.add(image);

			Label label = new Label(fileInfo.getName());
			label.setTitle(fileInfo.getName() + '\n' + fileInfo.getLength());
			panel.add(label);
			addClickHandler(this);
		}

		public void onClick(ClickEvent event) {
			if (getStylePrimaryName().equals(STYLE_UNSELECTED)) {
				setStylePrimaryName(STYLE_SELECTED);
			} else {
				setStylePrimaryName(STYLE_UNSELECTED);
			}
		}

		public FileInfo getFileInfo() {
			return fileInfo;
		}
	}
}
