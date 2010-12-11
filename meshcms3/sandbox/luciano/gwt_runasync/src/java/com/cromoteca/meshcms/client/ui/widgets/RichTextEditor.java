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
package com.cromoteca.meshcms.client.ui.widgets;

import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Task;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTML;

public class RichTextEditor extends Popup {
	private static int counter;
	Task<String> callback;
	private HTML html;
	private Object editorJSObject;
	private Path dirPath;

	public RichTextEditor(String text, Path dirPath, Task<String> callback) {
		super(600, 400);
		this.dirPath = dirPath;
		this.callback = callback;
		setText(MeshCMS.CONSTANTS.visualEditorTitle());
		html = new HTML(text);
		html.getElement().setId("meshcms_rich_text_editor_" + counter++);
		setWidget(html);
		addButton(MeshCMS.CONSTANTS.genericOk(), true);
		addButton(MeshCMS.CONSTANTS.genericCancel(), true);
	}

	public void showEditor() {
		showDialog(new Task<Integer>() {
				public void execute(Integer button) {
					if (button == 0) {
						String html = saveEditor(editorJSObject);
						callback.execute(html);
					}

					editorJSObject = null;
					hide();
				}
			});

		Path userCSS = MeshCMS.SITE_INFO.getUserCSS();
		editorJSObject = loadEditor(html.getElement(), MeshCMS.CONTEXT_PATH,
				dirPath.asLink(), userCSS == null ? null : userCSS.asAbsolute());
	}

	private static native Object loadEditor(JavaScriptObject element,
		String contextPath, String dirPath, String userCSS) /*-{
	return $wnd.MESHCMS_EDITOR.load(element, contextPath, dirPath, userCSS);
	}-*/;

	private native String saveEditor(Object editorJSObject) /*-{
	return $wnd.MESHCMS_EDITOR.save(editorJSObject);
	}-*/;
}
