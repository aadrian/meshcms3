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
import java.util.List;

public class FieldList extends ArrayList<Field> {
	private List<Button> actionButtons = new ArrayList<Button>();

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

		for (Button button : actionButtons) {
			button.setEnabled(ok);
		}

		return ok;
	}

	public void storeAll() {
		for (Field field : this) {
			field.storeValue();
		}
	}

	public Button getFirstActionButton() {
		return actionButtons.size() == 1 ? actionButtons.get(0) : null;
	}

	public void addActionButton(Button button) {
		actionButtons.add(button);
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
