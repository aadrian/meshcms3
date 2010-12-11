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

import com.cromoteca.meshcms.client.server.FileInfo;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.List;

public interface FileList {
	public static final String STYLE_SELECTED = "mesh-selected";
	public static final String STYLE_UNSELECTED = "mesh-unselected";

	FileInfo getSelectedFile();

	List<FileInfo> getSelectedFiles();

	void invertSelection();

	void selectAll();

	void selectNone();

	void setFiles(List<FileInfo> files);

	List<FileInfo> getFiles();

	Widget asWidget();

	ImageResource getIcon();

	FileManager getFileManager();

	void setFileManager(FileManager fileManager);
}
