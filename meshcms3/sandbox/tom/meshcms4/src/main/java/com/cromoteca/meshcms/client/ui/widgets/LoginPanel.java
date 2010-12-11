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
package com.cromoteca.meshcms.client.ui.widgets;

import com.cromoteca.meshcms.client.core.AbstractAsyncCallback;
import com.cromoteca.meshcms.client.core.MeshCMS;
import com.cromoteca.meshcms.client.server.UserProfile;
import com.cromoteca.meshcms.client.toolbox.Strings;
import com.cromoteca.meshcms.client.toolbox.Task;
import com.cromoteca.meshcms.client.ui.fields.FieldList;
import com.cromoteca.meshcms.client.ui.fields.PasswordField;
import com.cromoteca.meshcms.client.ui.fields.TextField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public class LoginPanel extends FlowPanel {
	private FieldList fields;
	private PasswordField passwordField;
	private TextField usernameField;

	public LoginPanel() {
		buildLayout();
	}

	private void buildLayout() {
		fields = new FieldList();
		usernameField = new TextField(true, MeshCMS.CONSTANTS.loginUsername())
					.bigger();
		add(usernameField);
		usernameField.useVoidConnector();
		fields.add(usernameField);
		passwordField = new PasswordField(true, MeshCMS.CONSTANTS.loginPassword())
					.bigger();
		add(passwordField);
		passwordField.useVoidConnector();
		fields.add(passwordField);
		fields.verifyAll();
	}

	public void showDialog(final LoginHandler loginHandler) {
		final Popup loginDialog = Popup.getInputBox(MeshCMS.CONSTANTS.loginCookies(),
				this);
		fields.addActionButton(loginDialog.getButton(0));
		fields.verifyAll();
		loginDialog.setText(MeshCMS.CONSTANTS.loginTitle());
		loginDialog.setHideOnAction(false);
		loginDialog.showDialog(new Task<Integer>() {
				public void execute(Integer param) {
					String username = usernameField.getValue();
					String password = passwordField.getValue();

					if (!(Strings.isNullOrEmpty(username)
								&& Strings.isNullOrEmpty(password))) {
						MeshCMS.SERVER.login(username, password,
							new AbstractAsyncCallback<UserProfile>() {
								@Override
								public void onResult(UserProfile user) {
									if (user != null) {
										loginDialog.hide();

										if (MeshCMS.isQueryStringLocale(user.getLocale())) {
											if (loginHandler != null) {
												loginHandler.onLogin();
											}
										} else {
											Window.Location.reload();
										}
									} else {
										passwordField.setValue("");
										passwordField.setFocus(true);
									}
								}
							});
					}
				}
			});
		usernameField.setFocus(true);
	}

	public static interface LoginHandler {
		void onLogin();
	}
}
