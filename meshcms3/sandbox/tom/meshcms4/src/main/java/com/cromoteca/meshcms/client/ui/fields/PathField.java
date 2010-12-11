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
package com.cromoteca.meshcms.client.ui.fields;

import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.ui.filemanager.FileManager;
import com.cromoteca.meshcms.client.ui.filemanager.FileManagerDialogBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PathField extends TextField {
	private Button browseButton;
	private FieldList propertyList;
	private Path basePath;

	public PathField(boolean required, String label, Path basePath) {
		super(required, label);
		this.basePath = basePath;
	}

	@Override
	protected void init(TextBox widget, String label) {
		if (label != null) {
			add(new Label(label, true));
		}

		add(widget);
		add(browseButton = new Button(MeshCMS.CONSTANTS.genericBrowse(),
					new ClickHandler() {
					public void onClick(ClickEvent event) {
						new FileManagerDialogBox() {
								public void handleSelection(Path path) {
									if (path != null) {
										String text;

										if (basePath == null) {
											text = path.asLink();
										} else {
											text = path.getRelativeTo(basePath).toString();
										}

										setValue(text);
										close();
									}
								}

								public int getSelectionMode() {
									return FileManager.SELECT_BOTH;
								}
							}.center();
					}
				}));
	}

	@Override
	public void setValue(String value) {
		//getFieldWidget().setValue(value, true);
		super.setValue(value);

		if (propertyList != null) {
			propertyList.verifyAll();
		}
	}

	@Override
	public void enableVerify(FieldList propertyList) {
		super.enableVerify(propertyList);
		this.propertyList = propertyList;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		browseButton.setEnabled(enabled);
	}
}
