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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.MenuBar;

public class ViewMenu extends MenuBar {
	public ViewMenu(final FileManager fileManager) {
		super(true);
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.documentsStack())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmDetails(), true,
			new Command() {
				public void execute() {
					fileManager.setFileList(new DetailedList());
				}
			});
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.imagesStack())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmThumbs(), true,
			new Command() {
				public void execute() {
					fileManager.setFileList(new ImageList());
				}
			});
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.blogsStack())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmPages(), true,
			new Command() {
				public void execute() {
					fileManager.setFileList(new PageList());
				}
			});
		addSeparator();
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.arrowCircle225())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmRefresh(), true,
			new Command() {
				public void execute() {
					fileManager.refreshView();
				}
			});
		addItem(AbstractImagePrototype.create(
				MeshCMS.ICONS_BUNDLE.arrowContinue000Top()).getHTML() + ' '
			+ MeshCMS.CONSTANTS.homeRefresh(), true,
			new Command() {
				public void execute() {
					fileManager.refreshSiteInfo();
				}
			});
		addItem(AbstractImagePrototype.create(
				MeshCMS.ICONS_BUNDLE.arrowCircleDouble135()).getHTML() + ' '
			+ MeshCMS.CONSTANTS.fmReload(), true,
			new Command() {
				public void execute() {
					fileManager.refreshFullPage();
				}
			});
	}
}
