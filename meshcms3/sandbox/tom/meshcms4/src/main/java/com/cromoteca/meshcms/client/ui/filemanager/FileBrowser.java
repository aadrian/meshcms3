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
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.ui.filemanager.FileManager.SelectionHandler;
import com.google.gwt.user.client.Window;

public class FileBrowser implements SelectionHandler {
	private int selectionMode;

	private FileBrowser(int selectionMode) {
		this.selectionMode = selectionMode;
		MeshCMS.setSelectedRichTextEditor(Window.Location.getParameter("editor"));
	}

	public void handleSelection(final Path path) {
		if (path != null) {
			new AuthorizableServerCall<String>() {
					@Override
					public void callServer() {
						MeshCMS.SERVER.getLink(path, MeshCMS.CURRENT_DIR, getAsyncCallback());
					}

					@Override
					public void onResult(String result) {
						setResult(result);
						close();
					}
				}.run();
		}
	}

	public native void close() /*-{
	$wnd.MESHCMS_EDITOR.closeBrowser();
	}-*/;

	private native void setResult(String path) /*-{
	$wnd.MESHCMS_EDITOR.browserResult(path);
	}-*/;

	public int getSelectionMode() {
		return selectionMode;
	}

	public static FileManager getFileBrowser() {
		FileManager fileManager = new FileManager(new DetailedList(),
				new FileBrowser(FileManager.SELECT_BOTH));

		return fileManager;
	}

	public static FileManager getImageBrowser() {
		FileManager fileManager = new FileManager(new ImageList(),
				new FileBrowser(FileManager.SELECT_FILES));

		return fileManager;
	}
}
