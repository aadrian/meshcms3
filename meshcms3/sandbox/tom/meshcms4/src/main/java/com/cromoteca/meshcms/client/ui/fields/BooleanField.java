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

import com.cromoteca.meshcms.client.toolbox.Strings;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class BooleanField extends Field<CheckBox> {
	public BooleanField(String label) {
		this(false, label);
	}

	public BooleanField(boolean required, String label) {
		super(new CheckBox(Strings.noNull(label), true), required, null);
	}

	@Override
	protected void init(CheckBox widget, String label) {
		add(widget);
	}

	@Override
	public String getValue() {
		return getFieldWidget().getValue().toString();
	}

	@Override
	public void setValue(String value) {
		getFieldWidget().setValue(Boolean.parseBoolean(value));
	}

	@Override
	public void enableVerify(final FieldList propertyList) {
		getFieldWidget().addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (propertyList != null) {
						propertyList.verifyAll();
					}
				}
			});
	}

	@Override
	public boolean isInvalid() {
		return isRequired() && !getFieldWidget().getValue();
	}

	public abstract static class BooleanConnector implements FieldConnector {
		public void storeValue(String value) {
			storeValue(Boolean.parseBoolean(value));
		}

		public String retrieveValue() {
			return Boolean.toString(loadValue());
		}

		public abstract void storeValue(boolean value);

		public abstract boolean loadValue();
	}
}
