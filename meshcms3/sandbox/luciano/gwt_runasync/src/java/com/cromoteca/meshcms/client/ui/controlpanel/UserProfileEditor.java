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
package com.cromoteca.meshcms.client.ui.controlpanel;

import com.cromoteca.meshcms.client.core.AuthorizableServerCall;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.UserProfile;
import com.cromoteca.meshcms.client.toolbox.Null;
import com.cromoteca.meshcms.client.toolbox.Pair;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.ui.fields.BooleanField;
import com.cromoteca.meshcms.client.ui.fields.BooleanField.BooleanConnector;
import com.cromoteca.meshcms.client.ui.fields.Field;
import com.cromoteca.meshcms.client.ui.fields.FieldConnector;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.fields.PasswordField;
import com.cromoteca.meshcms.client.ui.fields.PathField;
import com.cromoteca.meshcms.client.ui.fields.SortedSelectionField;
import com.cromoteca.meshcms.client.ui.fields.TextField;
import com.cromoteca.meshcms.client.ui.filemanager.FileManager;
import com.cromoteca.meshcms.client.ui.widgets.ButtonBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class UserProfileEditor extends FlowPanel implements FileManagerPanel {
	private FileManager fileManager;
	private boolean newUser;

	public UserProfileEditor(boolean newUser) {
		this.newUser = newUser;
		buildLayout();
	}

	private void buildLayout() {
		if (newUser) {
			new AuthorizableServerCall<Pair<String, HashMap<String, String>>>() {
					@Override
					public void callServer() {
						MeshCMS.SERVER.getAvailableLocalesInUserLocale(getAsyncCallback());
					}

					@Override
					public void onResult(Pair<String, HashMap<String, String>> result) {
						UserProfile userProfile = new UserProfile();
						userProfile.setLocale(result.getFirstObject());
						buildLayout(userProfile, result.getSecondObject());
					}
				}.run();
		} else {
			new AuthorizableServerCall<Pair<UserProfile, HashMap<String, String>>>() {
					@Override
					public void callServer() {
						MeshCMS.SERVER.getCurrentUserProfileAndLocales(getAsyncCallback());
					}

					@Override
					public void onResult(
						Pair<UserProfile, HashMap<String, String>> result) {
						buildLayout(result.getFirstObject(), result.getSecondObject());
					}
				}.run();
		}
	}

	private void buildLayout(final UserProfile user,
		final Map<String, String> locales) {
		clear();

		final FieldList fields = new FieldList();
		CaptionPanel userDataFieldset = new CaptionPanel(newUser
				? MeshCMS.CONSTANTS.userNew() : MeshCMS.CONSTANTS.userEdit());
		add(userDataFieldset);

		FlowPanel userDataPanel = new FlowPanel();
		userDataFieldset.add(userDataPanel);

		Field usernameField = new TextField(newUser,
				MeshCMS.CONSTANTS.loginUsername());
		userDataPanel.add(usernameField);
		usernameField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					user.setUsername(value);
				}

				public String retrieveValue() {
					return user.getUsername();
				}
			});
		fields.add(usernameField);
		usernameField.setEnabled(newUser);

		Field nameField = new TextField(false, MeshCMS.CONSTANTS.user_name()).bigger();
		userDataPanel.add(nameField);
		nameField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					user.setName(value);
				}

				public String retrieveValue() {
					return user.getName();
				}
			});
		fields.add(nameField);

		Field emailField = new TextField(false, MeshCMS.CONSTANTS.userMail()) {
					@Override
					public boolean isInvalid() {
						String value = getValue();

						if (Strings.isNullOrEmpty(value)) {
							return false;
						}

						return Strings.isInvalidEmail(value);
					}
				}.bigger();
		userDataPanel.add(emailField);
		emailField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					user.setEmail(value);
				}

				public String retrieveValue() {
					return user.getEmail();
				}
			});
		fields.add(emailField);

		Field langField = new SortedSelectionField(MeshCMS.CONSTANTS.userLanguage()) {
				@Override
				public void populateOptions(SortedMap<String, String> itemValueMap) {
					for (String langCode : locales.keySet()) {
						itemValueMap.put(locales.get(langCode), langCode);
					}
				}
			};

		userDataPanel.add(langField);
		langField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					user.setLocale(value);
				}

				public String retrieveValue() {
					return user.getLocale();
				}
			});
		fields.add(langField);

		CaptionPanel passwordFieldset = new CaptionPanel(newUser
				? MeshCMS.CONSTANTS.userInitPwd() : MeshCMS.CONSTANTS.userChangePwd());
		add(passwordFieldset);

		FlowPanel passwordPanel = new FlowPanel();
		passwordFieldset.add(passwordPanel);

		final Field newPasswordField = new PasswordField(newUser,
				MeshCMS.CONSTANTS.loginPassword());
		passwordPanel.add(newPasswordField);
		newPasswordField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					user.setUnencryptedPassword(value);
				}

				public String retrieveValue() {
					return null;
				}
			});
		fields.add(newPasswordField);

		Field confirmPasswordField = new PasswordField(false,
				MeshCMS.CONSTANTS.userConfirmPwd()) {
				@Override
				public boolean isInvalid() {
					return !getValue().equals(newPasswordField.getValue());
				}
			};

		passwordPanel.add(confirmPasswordField);
		confirmPasswordField.useVoidConnector();
		fields.add(confirmPasswordField);

		CaptionPanel permissionsFieldset = new CaptionPanel(MeshCMS.CONSTANTS
						.userPermissions());
		add(permissionsFieldset);

		FlowPanel permissionsPanel = new FlowPanel();
		permissionsFieldset.add(permissionsPanel);

		Field editorField = new BooleanField(MeshCMS.CONSTANTS.userEditor());
		permissionsPanel.add(editorField);
		editorField.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					user.setEditor(value);
				}

				@Override
				public boolean loadValue() {
					return user.isEditor();
				}
			});
		fields.add(editorField);
		editorField.setEnabled(newUser);

		Field adminField = new BooleanField(MeshCMS.CONSTANTS.userAdmin());
		permissionsPanel.add(adminField);
		adminField.setConnector(new BooleanConnector() {
				@Override
				public void storeValue(boolean value) {
					user.setAdmin(value);
				}

				@Override
				public boolean loadValue() {
					return user.isAdmin();
				}
			});
		fields.add(adminField);
		adminField.setEnabled(newUser);

		Field homeField = new PathField(false, MeshCMS.CONSTANTS.userHome(), null);
		permissionsPanel.add(homeField);
		homeField.setConnector(new FieldConnector() {
				public void storeValue(String value) {
					user.setHome(new Path(value));
				}

				public String retrieveValue() {
					Path home = user.getHome();

					return home == null ? null : home.asLink();
				}
			});
		fields.add(homeField);
		homeField.setEnabled(newUser);

		ButtonBar buttonBar = new ButtonBar();
		add(buttonBar);

		Button saveButton = new Button(newUser ? MeshCMS.CONSTANTS.genericSave()
				: MeshCMS.CONSTANTS.genericUpdate(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (fields.verifyAll()) {
							fields.storeAll();
							new AuthorizableServerCall<Null>() {
									@Override
									public void callServer() {
										if (newUser) {
											MeshCMS.SERVER.saveNewUser(user, getAsyncCallback());
										} else {
											MeshCMS.SERVER.saveUserProfile(user, getAsyncCallback());
										}
									}

									@Override
									public void onResult(Null result) {
										if (newUser
													|| MeshCMS.isQueryStringLocale(user.getLocale())) {
											fileManager.refreshView();
										} else {
											Window.Location.reload();
										}
									}
								}.run();
						}
					}
				});
		buttonBar.add(saveButton);
		fields.setActionButton(saveButton);
		fields.verifyAll();
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public Widget asWidget() {
		return this;
	}

	public String getTabCaption() {
		return newUser ? MeshCMS.CONSTANTS.userNew() : MeshCMS.CONSTANTS.userEdit();
	}
}
