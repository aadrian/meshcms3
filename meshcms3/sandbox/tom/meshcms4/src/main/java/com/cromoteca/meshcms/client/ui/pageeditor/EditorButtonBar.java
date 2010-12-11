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
import com.cromoteca.meshcms.client.server.Page;
import com.cromoteca.meshcms.client.toolbox.Null;
import com.cromoteca.meshcms.client.toolbox.Task;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.widgets.ButtonBar;
import com.cromoteca.meshcms.client.ui.widgets.Popup;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import java.util.HashSet;
import java.util.Set;

public class EditorButtonBar extends ButtonBar {
	private Page page;
	private Set<ZoneEditorPanel> zoneEditorPanels;

	public EditorButtonBar(Page page, FieldList fields) {
		this.page = page;
		zoneEditorPanels = new HashSet<ZoneEditorPanel>();
		buildLayout(fields);
	}

	private void buildLayout(FieldList fields) {
		Button publishButton = new Button(MeshCMS.CONSTANTS.editorPublish(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						for (ZoneEditorPanel zoneEditorPanel : zoneEditorPanels) {
							zoneEditorPanel.storeAll();
						}

						save(false);
					}
				});
		add(publishButton);
		fields.addActionButton(publishButton);

		Button draftButton = new Button(MeshCMS.CONSTANTS.editorDraft(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						for (ZoneEditorPanel zoneEditorPanel : zoneEditorPanels) {
							zoneEditorPanel.storeAll();
						}

						save(true);
					}
				});
		add(draftButton);
		fields.addActionButton(draftButton);

		if (page.isDraft()) {
			add(new Button(MeshCMS.CONSTANTS.editorDeleteDraft(),
					new ClickHandler() {
					public void onClick(ClickEvent event) {
						Popup.getOkCancelBox(MeshCMS.CONSTANTS.editorConfirmDeleteDraft()).showDialog(new Task<Integer>() {
								public void execute(Integer param) {
									new AuthorizableServerCall<Null>() {
											@Override
											public void callServer() {
												MeshCMS.SERVER.deleteDraft(MeshCMS.CURRENT_PATH,
														getAsyncCallback());
											}

											@Override
											public void onResult(Null result) {
												closeWindow();
											}
										}.run();
								}
							});
					}
				}));
		}

		add(new Button(MeshCMS.CONSTANTS.genericClose(),
				new ClickHandler() {
				public void onClick(ClickEvent event) {
					MeshCMS.closeWindow(false, null);
				}
			}));
	}

	public void add(ZoneEditorPanel zoneEditorPanel) {
		zoneEditorPanels.add(zoneEditorPanel);
	}

	private void save(final boolean asDraft) {
		new AuthorizableServerCall<Null>() {
				@Override
				public void callServer() {
					MeshCMS.SERVER.savePage(MeshCMS.CURRENT_PATH, page, asDraft,
						getAsyncCallback());
				}

				@Override
				public void onResult(Null result) {
					closeWindow();
				}
			}.run();
	}

	private void closeWindow() {
		MeshCMS.closeWindow(false, MeshCMS.CURRENT_PATH);
	}
}
