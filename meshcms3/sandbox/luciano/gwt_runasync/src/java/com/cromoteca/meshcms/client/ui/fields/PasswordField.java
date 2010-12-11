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
package com.cromoteca.meshcms.client.ui.fields;

import com.google.gwt.user.client.ui.PasswordTextBox;

public class PasswordField extends Field<PasswordTextBox> {
	public PasswordField(boolean required, String label) {
		super(new PasswordTextBox(), required, label);
		getFieldWidget().setVisibleLength(30);
		setEnterToAction(true);
	}

	@Override
	public String getValue() {
		return getFieldWidget().getValue();
	}

	@Override
	public void setValue(String value) {
		getFieldWidget().setValue(value);
	}

	public PasswordField smaller() {
		getFieldWidget()
				.setVisibleLength(getFieldWidget().getVisibleLength() * 2 / 3);

		return this;
	}

	public PasswordField bigger() {
		getFieldWidget()
				.setVisibleLength(getFieldWidget().getVisibleLength() * 3 / 2);

		return this;
	}
}
