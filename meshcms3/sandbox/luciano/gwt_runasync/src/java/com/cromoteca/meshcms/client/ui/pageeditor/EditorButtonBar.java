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
import com.cromoteca.meshcms.client.toolbox.Path;
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
	private Path path;
	private Set<ZoneEditorPanel> zoneEditorPanels;
	private String newPageAddress;
	private boolean newPage;

	public EditorButtonBar(Path path, Page page, boolean newPage, FieldList fields) {
		this.path = path;
		this.page = page;
		zoneEditorPanels = new HashSet<ZoneEditorPanel>();
		this.newPage = newPage;
		buildLayout(fields);
	}

	private void buildLayout(FieldList fields) {
		Button saveButton = new Button(MeshCMS.CONSTANTS.genericSave(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						for (ZoneEditorPanel zoneEditorPanel : zoneEditorPanels) {
							zoneEditorPanel.storeAll();
						}

						if (newPage) {
							new AuthorizableServerCall<Path>() {
									@Override
									public void callServer() {
										MeshCMS.SERVER.getNewPagePath(path.add(newPageAddress),
											getAsyncCallback());
									}

									@Override
									public void onResult(Path result) {
										if (result != null) {
											path = result;
											save(false);
										}
									}
								}.run();
						} else {
							Popup.getYesNoCancelBox(MeshCMS.CONSTANTS.editorPublish()).showDialog(new Task<Integer>() {
									public void execute(final Integer param) {
										save(param > 0);
									}
								});
						}
					}
				});
		add(saveButton);
		fields.setActionButton(saveButton);

		if (page.isDraft()) {
			add(new Button(MeshCMS.CONSTANTS.editorDeleteDraft(),
					new ClickHandler() {
					public void onClick(ClickEvent event) {
						Popup.getOkCancelBox(MeshCMS.CONSTANTS.editorConfirmDeleteDraft()).showDialog(new Task<Integer>() {
								public void execute(Integer param) {
									new AuthorizableServerCall<Null>() {
											@Override
											public void callServer() {
												MeshCMS.SERVER.deleteDraft(path, getAsyncCallback());
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
					MeshCMS.closeWindow(false);
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
					MeshCMS.SERVER.savePage(path, page, asDraft, getAsyncCallback());
				}

				@Override
				public void onResult(Null result) {
					closeWindow();
				}
			}.run();
	}

	private void closeWindow() {
		if (newPage) {
			MeshCMS.closeWindow(path.getParent());
		} else {
			MeshCMS.closeWindow(true);
		}
	}

	public void setNewPageAddress(String newPageAddress) {
		this.newPageAddress = newPageAddress;
	}
}
