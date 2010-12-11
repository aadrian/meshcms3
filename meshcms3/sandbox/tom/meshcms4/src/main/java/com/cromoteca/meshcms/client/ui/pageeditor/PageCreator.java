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
package com.cromoteca.meshcms.client.ui.pageeditor;

import com.cromoteca.meshcms.client.core.AbstractAsyncCallback;
import com.cromoteca.meshcms.client.core.AuthorizableServerCall;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.ui.fields.Field;
import com.cromoteca.meshcms.client.ui.fields.FieldConnector;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.fields.TextField;
import com.cromoteca.meshcms.client.ui.widgets.ButtonBar;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class PageCreator extends DockLayoutPanel {
	private FieldList fields;
	private String newPageAddress;
	private String pageTitle;
	private boolean editBody;

	public PageCreator(boolean editBody) {
		super(Unit.PX);
		this.editBody = editBody;
		fields = new FieldList();
		buildLayout();
	}

	private void buildLayout() {
		ButtonBar buttonBar = new ButtonBar();
		Button createButton = new Button(MeshCMS.CONSTANTS.genericCreate(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						fields.storeAll();
						new AuthorizableServerCall<Path>() {
								@Override
								public void callServer() {
									MeshCMS.SERVER.createPage(MeshCMS.CURRENT_DIR,
										newPageAddress, pageTitle, getAsyncCallback());
								}

								@Override
								public void onResult(Path result) {
									MeshCMS.changeCurrentPath(result);
									MeshCMS.changeCurrentDir(result);

									Panel parent = (Panel) getParent();
									removeFromParent();
									parent.add(new PageEditor(editBody));
								}
							}.run();
					}
				});
		buttonBar.add(createButton);
		fields.addActionButton(createButton);
		buttonBar.add(new Button(MeshCMS.CONSTANTS.genericClose(),
				new ClickHandler() {
				public void onClick(ClickEvent event) {
					MeshCMS.closeWindow(false, null);
				}
			}));
		addSouth(buttonBar, EditorButtonBar.HEIGHT);

		TabLayoutPanel tabPanel = new TabLayoutPanel(40, Unit.PX);
		add(tabPanel);

		FlowPanel panel = new FlowPanel();
		tabPanel.add(new ScrollPanel(panel), MeshCMS.CONSTANTS.editorCreate());

		final Field titleField = new TextField(false,
				MeshCMS.CONSTANTS.editorPageTitle()).bigger();
		panel.add(titleField);
		titleField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					pageTitle = value;
				}

				public String retrieveValue() {
					return pageTitle;
				}
			});
		fields.add(titleField);

		final String urlPrefix = MeshCMS.CURRENT_DIR.asLink() + '/';
		final Label urlLengthLabel = new Label();
		final Field urlField = new TextField(true, MeshCMS.CONSTANTS.editorAddress()) {
					@Override
					public boolean isInvalid() {
						urlLengthLabel.setText(MeshCMS.MESSAGES.editorAddressLength(
								Integer.toString(getValue().length() + urlPrefix.length())));

						return super.isInvalid();
					}
				}.bigger();
		urlField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					newPageAddress = value;
				}

				public String retrieveValue() {
					return newPageAddress;
				}
			});
		panel.add(urlField);
		fields.add(urlField);
		urlField.insert(new InlineLabel(urlPrefix),
			urlField.getWidgetIndex(urlField.getFieldWidget()));
		urlField.add(new Button(MeshCMS.CONSTANTS.editorURLFromTitle(),
				new ClickHandler() {
				public void onClick(ClickEvent event) {
					MeshCMS.SERVER.createFileName(titleField.getValue(),
							new AbstractAsyncCallback<String>() {
							@Override
							public void onResult(String result) {
								urlField.setValue(result);
								fields.verifyAll();
							}
						});
				}
			}));
		urlField.add(urlLengthLabel);
		fields.verifyAll();
	}
}
