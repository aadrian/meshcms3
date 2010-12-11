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

import com.cromoteca.meshcms.server.toolbox.Email;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.webview.Context;

public class TextField extends FormField {
	private boolean sender;

	public void setSender(boolean sender) {
		this.sender = sender;

		if (sender && !isRequired()) {
			setRequired(true);
		}
	}

	@Override
	public void setRequired(boolean required) {
		super.setRequired(required || sender);
	}

	public boolean isInvalid(String value) {
		boolean invalid = isRequired() && Strings.isNullOrEmpty(value);
		invalid = invalid || (sender && !Email.isValidEmailAddress(value));

		return invalid;
	}

	@Override
	protected void onPost(Form form) {
		String value = Context.getRequest().getParameter(getName());
		setValue(value);

		if (sender) {
			form.setSender(value);
		}

		setInvalid(isInvalid(value));

		if (isInvalid()) {
			form.setValid(false);
		}
	}
}
