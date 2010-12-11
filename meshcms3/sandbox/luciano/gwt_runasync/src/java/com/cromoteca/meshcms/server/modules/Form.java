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

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.toolbox.Email;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.webview.Scope;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class Form extends ServerModule {
	private List<FormField> fields;
	private String action;
	private String destination;
	private String sender;
	private boolean clientScript;
	private boolean post;
	private boolean valid;

	@Override
	public String process() {
		super.process();
		valid = true;

		HttpServletRequest request = Context.getRequest();
		post = "post".equalsIgnoreCase(request.getMethod());

		if (post) {
			String hiddenFieldName = getId() + "_code";
			post = hiddenFieldName.equals(request.getParameter(hiddenFieldName));
		}

		fields = new ArrayList<FormField>();
		Context.setScopedSingleton(this, Scope.REQUEST);

		return null;
	}

	public List<FormField> getFields() {
		return fields;
	}

	public static Form get() {
		return Context.getScopedSingleton(Form.class, Scope.REQUEST);
	}

	public boolean isPost() {
		return post;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isEmail() {
		return !Strings.isNullOrEmpty(destination)
				&& Email.isValidEmailAddress(destination);
	}

	public boolean isClientScript() {
		return clientScript;
	}

	public void setClientScript(boolean clientScript) {
		this.clientScript = clientScript;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;

		if (isEmail()) {
			action = "";
		} else {
			action = destination;

			if (!Strings.isNullOrEmpty(action) && action.charAt(0) == '/') {
				action = getRelativeLink(new Path(action));
			}
		}
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getAction() {
		return action;
	}

	public boolean isSelfAction() {
		return Strings.isNullOrEmpty(action);
	}
}
