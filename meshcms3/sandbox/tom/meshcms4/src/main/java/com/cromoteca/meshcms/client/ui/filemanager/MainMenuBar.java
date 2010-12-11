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
import com.google.gwt.user.client.ui.MenuBar;

public class MainMenuBar extends MenuBar {
	public MainMenuBar(FileManager fileManager) {
		addItem(MeshCMS.CONSTANTS.fmFile(), new FileMenu(fileManager));
		addItem(MeshCMS.CONSTANTS.fmEdit(), new EditMenu(fileManager));
		addItem(MeshCMS.CONSTANTS.fmView(), new ViewMenu(fileManager));
		addItem(MeshCMS.CONSTANTS.fmTools(), new ToolsMenu(fileManager));
	}
}
