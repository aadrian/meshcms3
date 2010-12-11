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
import com.cromoteca.meshcms.client.server.FileInfo;
import com.cromoteca.meshcms.client.toolbox.Null;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.toolbox.Task;
import com.cromoteca.meshcms.client.ui.widgets.Popup;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import java.util.List;

public class ToolsMenu extends MenuBar {
	public static final String NO_UPLOAD_MESSAGE = "KO";
	private FileManager fileManager;

	public ToolsMenu(FileManager fileManager) {
		super(true);
		this.fileManager = fileManager;
		buildLayout();
	}

	private void buildLayout() {
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.meshUploadFile())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmUpload(), true,
			new Command() {
				private Popup inputBox;

				public void execute() {
					final FormPanel form = new FormPanel();
					// TODO: remove static path /meshcms
					form.setAction(MeshCMS.CONTEXT_PATH + "/meshcms/upload/");
					form.setEncoding(FormPanel.ENCODING_MULTIPART);
					form.setMethod(FormPanel.METHOD_POST);

					FlowPanel panel = new FlowPanel();
					form.setWidget(panel);

					Hidden dir = new Hidden();
					dir.setName("dir");
					dir.setValue(fileManager.getCurrentPath().toString());
					panel.add(dir);
					inputBox = Popup.getInputBox(MeshCMS.CONSTANTS.fmUploadHint(), form);
					inputBox.setHideOnAction(false);
					inputBox.showDialog(new Task<Integer>() {
							public void execute(Integer buttonIndex) {
								form.submit();
							}
						});

					FileUpload upload = new FileUpload();
					upload.setName("upfile");
					form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
							public void onSubmitComplete(SubmitCompleteEvent event) {
								inputBox.hide();

								String message = event.getResults();

								if (NO_UPLOAD_MESSAGE.equals(message)) {
									Popup.getAlertBox(MeshCMS.CONSTANTS.fmUploadFailed())
											.showDialog(null);
								} else {
									fileManager.refreshView();
								}
							}
						});
					panel.add(upload);
				}
			});
		addItem(AbstractImagePrototype.create(
				MeshCMS.ICONS_BUNDLE.meshDownloadFile()).getHTML() + ' '
			+ MeshCMS.CONSTANTS.fmDownload(), true,
			new Command() {
				public void execute() {
					final FileInfo selectedFile = fileManager.getFileList()
								.getSelectedFile();

					if (selectedFile == null) {
						Popup.getAlertBox(MeshCMS.CONSTANTS.msgSingleFile()).showDialog(null);
					} else {
						// TODO: remove static path /meshcms
						Location.assign(MeshCMS.CONTEXT_PATH + "/meshcms/download/"
							+ selectedFile.getPath());
					}
				}
			});
		addItem(AbstractImagePrototype.create(
				MeshCMS.ICONS_BUNDLE.meshDownloadZip()).getHTML() + ' '
			+ MeshCMS.CONSTANTS.fmDownloadZip(), true,
			new Command() {
				public void execute() {
					final FileInfo selectedFile = fileManager.getFileList()
								.getSelectedFile();

					if (selectedFile == null) {
						Popup.getAlertBox(MeshCMS.CONSTANTS.msgSingleFile()).showDialog(null);
					} else {
						// TODO: remove static path /meshcms
						Location.assign(MeshCMS.CONTEXT_PATH + "/meshcms/zipdownload/"
							+ selectedFile.getPath());
					}
				}
			});
		addSeparator();
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.meshUnzip())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmUnzip(), true,
			new Command() {
				public void execute() {
					final FileInfo selectedFile = fileManager.getFileList()
								.getSelectedFile();

					if (selectedFile == null) {
						Popup.getAlertBox(MeshCMS.CONSTANTS.msgSingleFile()).showDialog(null);
					} else {
						new AuthorizableServerCall<List<FileInfo>>() {
								@Override
								public void callServer() {
									MeshCMS.SERVER.getZipContents(selectedFile.getPath(),
										getAsyncCallback());
								}

								@Override
								public void onResult(List<FileInfo> result) {
									FlowPanel zipReport = new FlowPanel();
									FlexTable zipContents = new FlexTable();

									for (FileInfo fileInfo : result) {
										int n = zipContents.getRowCount();
										zipContents.setWidget(n, 0,
											new InlineLabel(fileInfo.getName()));
										zipContents.setWidget(n, 1,
											new InlineLabel(fileInfo.getPath().getParent().toString()));
									}

									ScrollPanel zipReportScroll = new ScrollPanel(zipContents);
									zipReportScroll.setWidth("400px");
									zipReportScroll.setHeight("250px");

									FlowPanel newDirPanel = new FlowPanel();
									final CheckBox createNewDir = new CheckBox(MeshCMS.CONSTANTS
													.fmUnzipNewDir());
									final TextBox newDirName = new TextBox();
									newDirName.setValue(Strings.removeExtension(
											selectedFile.getName()));

									if (zipContents.getRowCount() > 1) {
										createNewDir.setValue(true);
									} else {
										newDirName.setEnabled(false);
									}

									createNewDir.addClickHandler(new ClickHandler() {
											public void onClick(ClickEvent event) {
												newDirName.setEnabled(createNewDir.getValue());
											}
										});
									newDirPanel.add(createNewDir);
									newDirPanel.add(newDirName);
									zipReport.add(zipReportScroll);
									zipReport.add(newDirPanel);
									zipReport.add(new Label(MeshCMS.CONSTANTS.fmUnzipWarn()));

									Popup inputBox = Popup.getInputBox(MeshCMS.MESSAGES
													.fmUnzipList(selectedFile.getName()), zipReport);
									inputBox.showDialog(new Task<Integer>() {
											public void execute(Integer buttonIndex) {
												new AuthorizableServerCall<Null>() {
														@Override
														public void callServer() {
															MeshCMS.SERVER.unzipFile(selectedFile.getPath(),
																createNewDir.getValue() ? newDirName.getValue()
																: null, getAsyncCallback());
														}

														@Override
														public void onResult(Null result) {
															fileManager.refreshView();
														}
													}.run();
											}
										});
								}
							}.run();
					}
				}
			});
	}
}
