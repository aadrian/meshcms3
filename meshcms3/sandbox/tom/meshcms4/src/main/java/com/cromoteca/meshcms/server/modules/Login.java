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
package com.cromoteca.meshcms.server.modules;

import com.cromoteca.meshcms.client.server.ZoneItem;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.core.SessionUser;
import com.cromoteca.meshcms.server.webview.Scope;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import javax.servlet.ServletException;

public class Login extends ServerModule {
	private Path redirect;

	public void setRedirect(String redirect) {
		this.redirect = new Path(redirect);
	}

	@Override
	public void run() throws IOException, ServletException {
		ResourceBundle constants = Context.getConstantsInUserLocale();
		SessionUser user = Context.getUser();

		Context.setScopedSingleton(FormSubmitHandler.class,
			new LoginSubmitter(user), Scope.REQUEST);

		List<ZoneItem> items = getZoneOutput().getZone().getItems();
		int idx = getPosition();

		ZoneItem formItem = new ZoneItem();
		formItem.setModuleName("form");
		formItem.getParameters().put("destination", "");
		formItem.getParameters().put("clientScript", "true");
		items.add(++idx, formItem);

		if (user == null) {
			ZoneItem usernameItem = new ZoneItem();
			usernameItem.setModuleName("textfield");
			usernameItem.getParameters()
					.put("label", constants.getString("loginUsername"));
			usernameItem.getParameters().put("name", "username");
			usernameItem.getParameters().put("required", "true");
			items.add(++idx, usernameItem);

			ZoneItem passwordItem = new ZoneItem();
			passwordItem.setModuleName("passwordfield");
			passwordItem.getParameters()
					.put("label", constants.getString("loginPassword"));
			passwordItem.getParameters().put("name", "password");
			items.add(++idx, passwordItem);
		} else {
			ZoneItem welcomeItem = new ZoneItem();
			welcomeItem.setModuleName("content");
			welcomeItem.getParameters()
					.put("text",
						"<p>"
						+ Context.getMessagesInUserLocale().getString("loginWelcome")
						.replace("{0}", user.getDisplayName()) + "</p>");
			items.add(++idx, welcomeItem);
		}

		ZoneItem submitItem = new ZoneItem();
		submitItem.setModuleName("formsubmit");
		submitItem.getParameters()
				.put("buttonLabel",
					constants.getString(user == null ? "loginSubmit" : "homeLogout"));
		items.add(++idx, submitItem);
	}

	public String getError() {
		return Context.getMessagesInUserLocale().getString("loginError");
	}

	public class LoginSubmitter implements FormSubmitHandler {
		private SessionUser user;

		public LoginSubmitter(SessionUser user) {
			this.user = user;
		}

		public FormSubmissionResult submit() {
			Form form = Form.get();

			if (user == null) {
				String username = null;
				String password = null;
				List<FormField> fields = form.getFields();

				for (FormField field : fields) {
					String name = field.getName();

					if (name.equals("username")) {
						username = field.getValue();
					} else if (name.equals("password")) {
						password = field.getValue();
					}
				}

				try {
					user = SessionUser.load(username, password);

					if (user == null) {
						return new FormSubmissionResult(form.getId(),
							Context.getConstants().getString("loginError"), true, null);
					} else {
						Context.setUser(user);

						return new FormSubmissionResult(form.getId(), null, false, redirect);
					}
				} catch (IOException ex) {
					Context.log(ex);

					return FormSubmissionResult.getDefaultError(form.getId());
				}
			} else {
				Context.removeUser();

				return new FormSubmissionResult(form.getId(), null, false, null);
			}
		}
	}
}
