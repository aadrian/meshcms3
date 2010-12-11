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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;

public class FieldList extends ArrayList<Field> {
	private Button actionButton;

	@Override
	public boolean add(Field field) {
		super.add(field);
		field.enableVerify(this);

		return true;
	}

	public boolean verifyAll() {
		boolean ok = true;

		for (Field field : this) {
			if (field.checkInvalid()) {
				ok = false;
			}
		}

		if (actionButton != null) {
			actionButton.setEnabled(ok);
		}

		return ok;
	}

	public void storeAll() {
		for (Field field : this) {
			field.storeValue();
		}
	}

	public Button getActionButton() {
		return actionButton;
	}

	public void setActionButton(Button actionButton) {
		this.actionButton = actionButton;
	}

	public void removeAll(HasWidgets container) {
		for (Widget widget : container) {
			if (widget instanceof Field) {
				remove(widget);
			}

			if (widget instanceof HasWidgets) {
				removeAll((HasWidgets) widget);
			}
		}
	}
}
