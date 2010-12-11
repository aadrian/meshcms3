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

import com.cromoteca.meshcms.client.core.AuthorizableServerCall;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.toolbox.Task;
import com.cromoteca.meshcms.client.ui.widgets.CodeEditor;
import com.cromoteca.meshcms.client.ui.widgets.RichTextEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.List;

public class RichTextField extends MultilineField {
	private Button editMenuButton;
	private HTML preview;
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

		//add(widget);
		preview = new HTML();
		preview.setStylePrimaryName("mesh-html-preview");
		preview.setTitle(MeshCMS.CONSTANTS.editorClickToEdit());
		add(preview);

		final List<String> editors = new ArrayList<String>();

		if (MeshCMS.SITE_INFO.isTinyMCE()) {
			editors.add("tiny_mce");
		}

		if (MeshCMS.SITE_INFO.isCKEditor()) {
			editors.add("ckeditor");
		}

		editMenuButton = new Button(MeshCMS.CONSTANTS.fmEditVisually());

		final MenuBar editorsMenu = new MenuBar(true);
		final PopupPanel popup = new PopupPanel(true);
		popup.setStylePrimaryName("mesh-popup-menu");
		popup.add(editorsMenu);

		final List<Command> commands = new ArrayList<Command>();

		for (final String editorName : editors) {
			Command cmd = new Command() {
					public void execute() {
						popup.hide();
						launchEditor(editorName);
					}
				};

			commands.add(cmd);
			editorsMenu.addItem(MeshCMS.getDynamicTranslation("editor_", editorName),
				cmd);
		}

		if (!editors.isEmpty()) {
			editorsMenu.addSeparator();
		}

		Command cmd = new Command() {
				public void execute() {
					popup.hide();
					new CodeEditor(getValue(),
						new Task<String>() {
							public void execute(String param) {
								setValue(param);
							}
						}).showEditor();
				}
			};

		commands.add(cmd);
		editorsMenu.addItem(MeshCMS.CONSTANTS.asSourceCode(), cmd);
		preview.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					commands.get(0).execute();
					event.preventDefault();
				}
			});
		editMenuButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Widget source = (Widget) event.getSource();
					int left = source.getAbsoluteLeft();
					int top = source.getAbsoluteTop() + source.getOffsetHeight();
					popup.setPopupPosition(left, top);
					popup.show();
				}
			});

		SimplePanel buttonPanel = new SimplePanel();
		add(buttonPanel);
		buttonPanel.add(editMenuButton);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		editMenuButton.setEnabled(enabled);
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

	@Override
	public void setValue(final String value) {
		super.setValue(value);
		new AuthorizableServerCall<String>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getHTMLPreview(value, MeshCMS.CURRENT_DIR,
						getAsyncCallback());
				}

				@Override
				public void onResult(String result) {
					if (Strings.isNullOrWhitespace(result)) {
						result = "<p><em>" + MeshCMS.CONSTANTS.editorNoText() + "</em></p>";
					}

					preview.setHTML(result);
				}
			}.run();
	}
}
