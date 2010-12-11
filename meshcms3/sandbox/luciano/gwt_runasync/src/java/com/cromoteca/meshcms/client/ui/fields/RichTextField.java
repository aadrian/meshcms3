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
import com.cromoteca.meshcms.client.toolbox.Task;
import com.cromoteca.meshcms.client.ui.widgets.CodeEditor;
import com.cromoteca.meshcms.client.ui.widgets.RichTextEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;

public class RichTextField extends MultilineField {
	private Button browseButton;
	private Path dirPath;

	public RichTextField(boolean required, String label, Path dirPath) {
		super(required, label);
		getFieldWidget().setVisibleLines(3);
		this.dirPath = dirPath;
	}

	@Override
	protected void init(TextArea widget, String label) {
		if (label != null) {
			add(new Label(label, true));
		}

		add(widget);

		final List<String> editors = new ArrayList<String>();

		if (MeshCMS.SITE_INFO.isTinyMCE()) {
			editors.add("tiny_mce");
		}

		if (MeshCMS.SITE_INFO.isCKEditor()) {
			editors.add("ckeditor");
		}

		browseButton = new Button(MeshCMS.CONSTANTS.fmEditVisually());

		if (editors.size() > 0) {
			if (editors.size() == 1) {
				browseButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							launchEditor(editors.get(0));
						}
					});
			} else {
				MenuBar editorsMenu = new MenuBar(true);
				final PopupPanel popup = new PopupPanel(true);
				popup.setStylePrimaryName("mesh-popup-menu");
				popup.add(editorsMenu);

				for (final String editorName : editors) {
					editorsMenu.addItem(MeshCMS.getDynamicTranslation("editor_",
							editorName),
						new Command() {
							public void execute() {
								launchEditor(editorName);
								popup.hide();
							}
						});
				}

				if (!editors.isEmpty()) {
					editorsMenu.addSeparator();
					editorsMenu.addItem(MeshCMS.CONSTANTS.asSourceCode(),
						new Command() {
							public void execute() {
								new CodeEditor(getValue(), "en", "html",
									new Task<String>() {
										public void execute(String param) {
											setValue(param);
										}
									}).showEditor();
							}
						});
				}

				browseButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							Widget source = (Widget) event.getSource();
							int left = source.getAbsoluteLeft();
							int top = source.getAbsoluteTop() + source.getOffsetHeight();
							popup.setPopupPosition(left, top);
							popup.show();
						}
					});
			}

			SimplePanel buttonPanel = new SimplePanel();
			add(buttonPanel);
			buttonPanel.add(browseButton);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		browseButton.setEnabled(enabled);
	}

	private void launchEditor(String editorName) {
		MeshCMS.setSelectedRichTextEditor(editorName);
		new RichTextEditor(getValue(), dirPath,
			new Task<String>() {
				public void execute(String param) {
					setValue(param);
				}
			}).showEditor();
	}
}
