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
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.ui.filemanager.FileManager.SelectionHandler;
import com.cromoteca.meshcms.client.ui.widgets.Popup;

public abstract class FileManagerDialogBox extends Popup
	implements SelectionHandler {
	private FileManager fileManager;

	public FileManagerDialogBox() {
		super(800, 800);
		setText(MeshCMS.CONSTANTS.homeFile());
		buildLayout();
	}

	private void buildLayout() throws UnsupportedOperationException {
		fileManager = new FileManager(new DetailedList(), this);
		setWidget(fileManager);
	}

	public abstract void handleSelection(Path path);

	public void close() {
		hide();
	}
}
