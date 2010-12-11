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
import com.cromoteca.meshcms.client.toolbox.Function;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.toolbox.Task;
import com.cromoteca.meshcms.client.ui.fields.DateField;
import com.cromoteca.meshcms.client.ui.fields.DateField.DateConnector;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.widgets.CodeEditor;
import com.cromoteca.meshcms.client.ui.widgets.Popup;
import com.cromoteca.meshcms.client.ui.widgets.WideTextBox;
import com.cromoteca.meshcms.client.ui.widgets.WideTextBox.AutoSelectionType;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.MenuBar;
import java.util.Date;
import java.util.List;

public class FileMenu extends MenuBar {
	private FileManager fileManager;

	public FileMenu(final FileManager fileManager) {
		super(true);
		this.fileManager = fileManager;
		addItem(AbstractImagePrototype.create(
				MeshCMS.ICONS_BUNDLE.documentSearchResult()).getHTML() + ' '
			+ MeshCMS.CONSTANTS.fmViewFile(), true, new ViewMenu());
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.documentPencil())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmEditSrc(), true,
			new Command() {
				public void execute() {
					final FileInfo selectedFile = fileManager.getFileList()
								.getSelectedFile();

					if (selectedFile == null) {
						Popup.getAlertBox(MeshCMS.CONSTANTS.msgSingleFile()).showDialog(null);
					} else if (!selectedFile.isDirectory()) {
						new AuthorizableServerCall<String>() {
								@Override
								public void callServer() {
									MeshCMS.SERVER.getFile(selectedFile.getPath(),
										getAsyncCallback());
								}

								@Override
								public void onResult(String result) {
									new CodeEditor(result,
										new Task<String>() {
											public void execute(final String param) {
												new AuthorizableServerCall<Boolean>() {
														@Override
														public void callServer() {
															MeshCMS.SERVER.saveFile(selectedFile.getPath(),
																param, getAsyncCallback());
														}

														@Override
														public void onResult(Boolean result) {
															if (result) {
																fileManager.refreshView();
															}
														}
													}.run();
											}
										}).showEditor();
								}
							}.run();
					}
				}
			});
		addSeparator();
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.folderPlus())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmNewFolder(), true,
			new Command() {
				public void execute() {
					final WideTextBox textBox = new WideTextBox();
					textBox.setAutoSelectionType(AutoSelectionType.FULL);
					textBox.setValue(MeshCMS.CONSTANTS.msgSuggestedFolderName());

					final Popup inputBox = Popup.getInputBox(MeshCMS.CONSTANTS
									.msgNewFolder(), textBox);
					inputBox.showDialog(new Task<Integer>() {
							public void execute(Integer buttonIndex) {
								final String value = textBox.getValue();
								new AuthorizableServerCall<Boolean>() {
										@Override
										public void callServer() {
											MeshCMS.SERVER.createDirectory(fileManager.getCurrentPath(),
												value, getAsyncCallback());
										}

										@Override
										public void onResult(Boolean result) {
											if (result) {
												fileManager.refreshView();
											}
										}
									}.run();
							}
						});
					textBox.setFocus(true);
				}
			});
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.documentPlus())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmNewFile(), true,
			new Command() {
				public void execute() {
					final WideTextBox textBox = new WideTextBox();
					textBox.setAutoSelectionType(AutoSelectionType.FULL);
					textBox.setValue("index.html");

					final Popup inputBox = Popup.getInputBox(MeshCMS.CONSTANTS.msgNewFile(),
							textBox);
					inputBox.showDialog(new Task<Integer>() {
							public void execute(Integer buttonIndex) {
								final String value = textBox.getValue();
								new AuthorizableServerCall<Boolean>() {
										@Override
										public void callServer() {
											MeshCMS.SERVER.createFile(fileManager.getCurrentPath(),
												value, getAsyncCallback());
										}

										@Override
										public void onResult(Boolean result) {
											if (result) {
												fileManager.refreshView();
											}
										}
									}.run();
							}
						});
					textBox.setFocus(true);
				}
			});
		addSeparator();
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.documentMinus())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmDelete(), true,
			new Command() {
				public void execute() {
					final List<FileInfo> selectedFiles = fileManager.getFileList()
								.getSelectedFiles();

					if (selectedFiles.isEmpty()) {
						Popup.getAlertBox(MeshCMS.CONSTANTS.msgNoSelection())
								.showDialog(null);
					} else {
						final Popup yesNoBox = Popup.getYesNoBox(MeshCMS.MESSAGES
										.msgConfirmDelete(Strings.generateList(selectedFiles, ", ",
												new Function<String, FileInfo>() {
											public String execute(FileInfo param) {
												return param.getName();
											}
										})));

						yesNoBox.showDialog(new Task<Integer>() {
								public void execute(Integer buttonIndex) {
									if (buttonIndex == 0) {
										final List<Path> paths = FileInfo.getPaths(selectedFiles);
										new AuthorizableServerCall<Boolean>() {
												@Override
												public void callServer() {
													MeshCMS.SERVER.deleteFiles(paths, getAsyncCallback());
												}

												@Override
												public void onResult(Boolean result) {
													if (result) {
														fileManager.refreshView();
													}
												}
											}.run();
									}
								}
							});
					}
				}
			});
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.documentRename())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmRename(), true,
			new Command() {
				public void execute() {
					final FileInfo selectedFile = fileManager.getFileList()
								.getSelectedFile();

					if (selectedFile == null) {
						Popup.getAlertBox(MeshCMS.CONSTANTS.msgSingleFile()).showDialog(null);
					} else {
						final WideTextBox textBox = new WideTextBox();
						textBox.setAutoSelectionType(AutoSelectionType.FILENAME);
						textBox.setValue(selectedFile.getName());

						final Popup inputBox = Popup.getInputBox(MeshCMS.CONSTANTS
										.msgNewName(), textBox);
						inputBox.showDialog(new Task<Integer>() {
								public void execute(Integer buttonIndex) {
									final String value = textBox.getValue();

									if (!(selectedFile.getName().equals(value))) {
										new AuthorizableServerCall<Boolean>() {
												@Override
												public void callServer() {
													MeshCMS.SERVER.renameFile(selectedFile.getPath(),
														value, getAsyncCallback());
												}

												@Override
												public void onResult(Boolean result) {
													if (result) {
														fileManager.refreshView();
													}
												}
											}.run();
									}
								}
							});
						textBox.setFocus(true);
					}
				}
			});
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.calendarDay())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmChangeDate(), true,
			new Command() {
				public void execute() {
					final List<FileInfo> selectedFiles = fileManager.getFileList()
								.getSelectedFiles();

					if (selectedFiles.isEmpty()) {
						Popup.getAlertBox(MeshCMS.CONSTANTS.msgNoSelection())
								.showDialog(null);
					} else {
						final FieldList fields = new FieldList();
						final DateField dateField = new DateField(true);
						dateField.setConnector(new DateConnector() {
								@Override
								public void storeValue(final Date value) {
									final List<Path> paths = FileInfo.getPaths(selectedFiles);
									new AuthorizableServerCall<Boolean>() {
											@Override
											public void callServer() {
												MeshCMS.SERVER.changeDates(paths, value,
													getAsyncCallback());
											}

											@Override
											public void onResult(Boolean result) {
												if (result) {
													fileManager.refreshView();
												}
											}
										}.run();
								}

								@Override
								public Date loadValue() {
									return selectedFiles.size() == 1
									? selectedFiles.get(0).getLastModified() : new Date();
								}
							});
						fields.add(dateField);

						final Popup inputBox = Popup.getInputBox(MeshCMS.CONSTANTS
										.msgChangeDate(), dateField);
						fields.addActionButton(inputBox.getButton(0));
						fields.verifyAll();
						inputBox.showDialog(new Task<Integer>() {
								public void execute(Integer buttonIndex) {
									fields.storeAll();
								}
							});
					}
				}
			});
	}

	private class ViewMenu extends MenuBar {
		public ViewMenu() {
			super(true);
			addItem(AbstractImagePrototype.create(
					MeshCMS.ICONS_BUNDLE.applicationBlue()).getHTML() + ' '
				+ MeshCMS.CONSTANTS.fmThisWindow(), true,
				new Command() {
					public void execute() {
						final FileInfo selectedFile = fileManager.getFileList()
									.getSelectedFile();

						if (selectedFile == null) {
							Popup.getAlertBox(MeshCMS.CONSTANTS.msgSingleFile())
									.showDialog(null);
						} else {
							Window.Location.assign(MeshCMS.CONTEXT_PATH
								+ selectedFile.getPath().asLink());
						}
					}
				});
			addItem(AbstractImagePrototype.create(
					MeshCMS.ICONS_BUNDLE.applicationsBlue()).getHTML() + ' '
				+ MeshCMS.CONSTANTS.fmNewWindow(), true,
				new Command() {
					public void execute() {
						final FileInfo selectedFile = fileManager.getFileList()
									.getSelectedFile();

						if (selectedFile == null) {
							Popup.getAlertBox(MeshCMS.CONSTANTS.msgSingleFile())
									.showDialog(null);
						} else {
							Window.open(MeshCMS.CONTEXT_PATH
								+ selectedFile.getPath().asLink(), "_blank", "");
						}
					}
				});
		}
	}
}
