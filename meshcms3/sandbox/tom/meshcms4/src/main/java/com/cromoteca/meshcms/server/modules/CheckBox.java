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

import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.webview.Context;

public class CheckBox extends FormField {
	private boolean checked;

	@Override
	protected void onPost(Form form) {
		String fieldName = getName();
		String value = Context.getRequest().getParameter(fieldName);
		setValue(value);
		checked = value != null;

		if (!checked) {
			form.getFields().remove(this);
		}

		setInvalid(isRequired() && !checked);

		if (isInvalid()) {
			form.setValid(false);
		}
	}

	@Override
	public String getValue() {
		String value = super.getValue();

		return Strings.isNullOrEmpty(value) ? Boolean.TRUE.toString() : value;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
