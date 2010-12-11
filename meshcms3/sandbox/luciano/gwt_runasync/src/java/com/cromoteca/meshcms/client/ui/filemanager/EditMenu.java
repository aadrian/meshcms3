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
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Task;
import com.cromoteca.meshcms.client.ui.widgets.Popup;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.TextBox;
import java.util.List;

public class EditMenu extends MenuBar {
	private FileManager fileManager;

	public EditMenu(final FileManager fileManager) {
		super(true);
		this.fileManager = fileManager;
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.scissors())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmCut(), true,
			new FillClipboardCommand(true));
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.documents())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmCopy(), true,
			new FillClipboardCommand(false));
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.clipboardPaste())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmPaste(), true,
			new Command() {
				public void execute() {
					final FileClipboard clipboard = fileManager.getClipboard();
					final List<Path> contents = clipboard.getContents();

					if (contents != null && contents.size() > 0) {
						final Path currentPath = fileManager.getCurrentPath();
						new AuthorizableServerCall<Boolean>() {
								@Override
								public void callServer() {
									if (clipboard.isCut()) {
										MeshCMS.SERVER.moveFiles(contents, currentPath,
											getAsyncCallback());
									} else {
										MeshCMS.SERVER.copyFiles(contents, currentPath,
											getAsyncCallback());
									}
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
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.documentsText())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmDuplicate(), true,
			new Command() {
				public void execute() {
					final FileInfo selectedFile = fileManager.getFileList()
								.getSelectedFile();

					if (selectedFile == null) {
						Popup.getAlertBox(MeshCMS.CONSTANTS.msgSingleFile()).showDialog(null);
					} else {
						final TextBox textBox = new TextBox();
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
													MeshCMS.SERVER.duplicateFile(selectedFile.getPath(),
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
					}
				}
			});
		addSeparator();
		addItem(AbstractImagePrototype.create(
				MeshCMS.ICONS_BUNDLE.selectionSelect()).getHTML() + ' '
			+ MeshCMS.CONSTANTS.fmSelAll(), true,
			new Command() {
				public void execute() {
					fileManager.getFileList().selectAll();
				}
			});
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.selection())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmSelNone(), true,
			new Command() {
				public void execute() {
					fileManager.getFileList().selectNone();
				}
			});
		addItem(AbstractImagePrototype.create(MeshCMS.ICONS_BUNDLE.tableSelectRow())
					.getHTML() + ' ' + MeshCMS.CONSTANTS.fmSelInv(), true,
			new Command() {
				public void execute() {
					fileManager.getFileList().invertSelection();
				}
			});
	}

	private class FillClipboardCommand implements Command {
		private boolean cut;

		public FillClipboardCommand(boolean cut) {
			this.cut = cut;
		}

		public void execute() {
			final List<FileInfo> selectedFiles = fileManager.getFileList()
						.getSelectedFiles();

			if (selectedFiles.size() == 0) {
				Popup.getAlertBox(MeshCMS.CONSTANTS.msgNoSelection()).showDialog(null);
			} else {
				FileClipboard clipboard = fileManager.getClipboard();
				clipboard.setContents(FileInfo.getPaths(selectedFiles));
				clipboard.setCut(cut);
			}
		}
	}
}
