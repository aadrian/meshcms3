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

import com.cromoteca.meshcms.client.core.AuthorizableServerCall;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.MenuPolicy;
import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.client.server.PageConfiguration;
import com.cromoteca.meshcms.client.server.Zone;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.ui.fields.DateField;
import com.cromoteca.meshcms.client.ui.fields.DateField.DateConnector;
import com.cromoteca.meshcms.client.ui.fields.Field;
import com.cromoteca.meshcms.client.ui.fields.FieldConnector;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.fields.MultilineField;
import com.cromoteca.meshcms.client.ui.fields.RichTextField;
import com.cromoteca.meshcms.client.ui.fields.SelectionField;
import com.cromoteca.meshcms.client.ui.fields.TextField;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import java.util.Date;

public class PageEditor extends DockLayoutPanel {
	private EditorButtonBar buttonBar;
	private FieldList fields;
	private Page page;
	private boolean editBodyZone;

	public PageEditor(boolean editBodyZone) {
		super(Unit.PX);
		this.editBodyZone = editBodyZone;

		new AuthorizableServerCall<Page>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.getPage(MeshCMS.CURRENT_PATH, true, getAsyncCallback());
				}

				@Override
				public void onResult(Page result) {
					page = result;
					buildLayout();
				}
			}.run();
	}

	private void buildLayout() {
		fields = new FieldList();

		FlowPanel headPanel = new FlowPanel();
		Zone head = page.getPageConfiguration().getZone(Zone.HEAD_ZONE);

		if (head == null) {
			head = new Zone(Zone.HEAD_ZONE);
			page.getPageConfiguration().addZone(head);
		}

		ZoneEditorPanel headZonePanel = new ZoneEditorPanel(fields, head,
				MeshCMS.CONSTANTS.editorHeadModules());
		headPanel.add(headZonePanel);

		Field headField = new MultilineField(false, MeshCMS.CONSTANTS.editorHead())
					.bigger(true, true);
		headPanel.add(headField);
		headField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					page.setHead(value);
				}

				public String retrieveValue() {
					return page.getHead();
				}
			});
		fields.add(headField);
		buttonBar = new EditorButtonBar(page, fields);
		buttonBar.add(headZonePanel);

		TabLayoutPanel tabPanel = new TabLayoutPanel(40, Unit.PX);
		tabPanel.add(new ScrollPanel(new BasicPageEditor()),
			MeshCMS.CONSTANTS.editorBasic());
		tabPanel.add(new ScrollPanel(new AdvancedPageEditor()),
			MeshCMS.CONSTANTS.editorAdvanced());
		tabPanel.add(new ScrollPanel(headPanel), MeshCMS.CONSTANTS.editorPageHead());
		tabPanel.selectTab(0);
		addSouth(buttonBar, EditorButtonBar.HEIGHT);
		add(tabPanel);
		fields.verifyAll();
	}

	private class AdvancedPageEditor extends FlowPanel {
		public AdvancedPageEditor() {
			buildLayout();
		}

		private void buildLayout() {
			MultilineField descField = new MultilineField(false,
					MeshCMS.CONSTANTS.editorDescription()).bigger(true, false)
						.smaller(false, true);
			add(descField);
			descField.setConnector(new FieldConnector() {
					public void storeValue(String value) {
						page.setDescription(value);
					}

					public String retrieveValue() {
						return page.getDescription();
					}
				});
			fields.add(descField);

			Field keysField = new TextField(false, MeshCMS.CONSTANTS.editorKeywords())
						.bigger();
			add(keysField);
			keysField.setConnector(new FieldConnector() {
					public void storeValue(String value) {
						page.getKeywords().clear();

						String[] keys = value.split("[,;:]");

						for (String key : keys) {
							page.addKeyword(key.trim());
						}
					}

					public String retrieveValue() {
						return Strings.generateList(page.getKeywords(), ", ");
					}
				});
			fields.add(keysField);

			final PageConfiguration pageConfiguration = page.getPageConfiguration();

			Field menuPolicyField = new SelectionField(MeshCMS.CONSTANTS
							.editorMenuPolicy()) {
					@Override
					public void populateOptions(ListBox listBox) {
						listBox.addItem(MeshCMS.getDynamicTranslation("menupolicy_",
								MenuPolicy.INSERT.name()), MenuPolicy.INSERT.name());
						listBox.addItem(MeshCMS.getDynamicTranslation("menupolicy_",
								MenuPolicy.INSERT_NO_SUBPAGES.name()),
							MenuPolicy.INSERT_NO_SUBPAGES.name());
						listBox.addItem(MeshCMS.getDynamicTranslation("menupolicy_",
								MenuPolicy.SKIP.name()), MenuPolicy.SKIP.name());
					}
				};

			add(menuPolicyField);
			menuPolicyField.setConnector(new FieldConnector() {
					public void storeValue(String value) {
						pageConfiguration.setMenuPolicy(MenuPolicy.valueOf(value));
					}

					public String retrieveValue() {
						return pageConfiguration.getMenuPolicy().name();
					}
				});
			fields.add(menuPolicyField);

			Field shortTitleField = new TextField(false,
					MeshCMS.CONSTANTS.editorMenuTitle()).bigger();
			add(shortTitleField);
			shortTitleField.setConnector(new FieldConnector() {
					public void storeValue(String value) {
						pageConfiguration.setShortTitle(value);
					}

					public String retrieveValue() {
						return pageConfiguration.getShortTitle();
					}
				});
			fields.add(shortTitleField);

			final DateField dateField = new DateField(false,
					MeshCMS.MESSAGES.editorCreationDate(DateField.currentDateTime()));
			add(dateField);
			dateField.setConnector(new DateConnector() {
					@Override
					public void storeValue(Date value) {
						page.getPageConfiguration().setCreationDate(value);
					}

					@Override
					public Date loadValue() {
						return page.getPageConfiguration().getCreationDate();
					}
				});
			fields.add(dateField);

			Image fdImage = new Image(MeshCMS.ICONS_BUNDLE.calendarInsert());
			fdImage.setTitle(MeshCMS.CONSTANTS.editorRetrieveFileDate());
			fdImage.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						new AuthorizableServerCall<Date>() {
								@Override
								public void callServer() {
									MeshCMS.SERVER.getFileDate(MeshCMS.CURRENT_PATH,
										getAsyncCallback());
								}

								@Override
								public void onResult(Date result) {
									dateField.setValue(DateField.DATE_TIME_FORMAT.format(result));
								}
							}.run();
					}
				});
			dateField.add(fdImage);
		}
	}

	private class BasicPageEditor extends FlowPanel {
		public BasicPageEditor() {
			buildLayout();
		}

		private void buildLayout() {
			final Field titleField = new TextField(false,
					MeshCMS.CONSTANTS.editorPageTitle()).bigger();
			add(titleField);
			titleField.setConnector(new FieldConnector() {
					public void storeValue(String value) {
						page.setTitle(value);
					}

					public String retrieveValue() {
						return page.getTitle();
					}
				});
			fields.add(titleField);

			Field bodyField = new RichTextField(false,
					MeshCMS.CONSTANTS.editorPageBody(), MeshCMS.CURRENT_DIR).bigger(true,
					true);
			add(bodyField);
			bodyField.setConnector(new FieldConnector() {
					public void storeValue(String value) {
						page.setBody(value);
					}

					public String retrieveValue() {
						return page.getBody();
					}
				});
			fields.add(bodyField);

			if (editBodyZone) {
				Zone body = page.getPageConfiguration().getZone(Zone.BODY_ZONE);

				if (body == null) {
					body = new Zone(Zone.BODY_ZONE);
					page.getPageConfiguration().addZone(body);
				}

				ZoneEditorPanel bodyEditorPanel = new ZoneEditorPanel(fields, body,
						MeshCMS.CONSTANTS.editorBodyModules());
				buttonBar.add(bodyEditorPanel);
				add(bodyEditorPanel);
			}
		}
	}
}
