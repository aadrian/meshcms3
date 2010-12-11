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
import com.cromoteca.meshcms.client.toolbox.Task;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Shows a source code editor within a dialog box.
 */
public class CodeEditor extends Popup {
	private Task<String> callback;
	private TextArea textArea;

	public CodeEditor(String text, Task<String> callback) {
		super(800, 600);
		this.callback = callback;
		setText(MeshCMS.CONSTANTS.srcTitle());
		textArea = new TextArea();
		textArea.setValue(text);
		setWidget(textArea);
		addButton(MeshCMS.CONSTANTS.genericOk(), true);
		addButton(MeshCMS.CONSTANTS.genericCancel(), true);
	}

	public void showEditor() {
		showDialog(new Task<Integer>() {
				public void execute(Integer button) {
					if (button == 0) {
						callback.execute(textArea.getValue());
					}

					hide();
				}
			});
		textArea.setFocus(true);
	}
}
