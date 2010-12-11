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

import com.google.gwt.user.client.ui.ListBox;

public abstract class SelectionField extends Field<ListBox> {
	public SelectionField(String label) {
		super(new ListBox(), false, label);
	}

	@Override
	public String getValue() {
		ListBox listBox = getFieldWidget();
		int selectedIndex = listBox.getSelectedIndex();

		return selectedIndex < 0 ? null : listBox.getValue(selectedIndex);
	}

	@Override
	public void setValue(String value) {
		ListBox listBox = getFieldWidget();
		listBox.clear();
		populateOptions(listBox);

		int n = -1;

		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (listBox.getValue(i).equals(value)) {
				n = i;
			}
		}

		if (n != -1) {
			listBox.setSelectedIndex(n);
		}
	}

	public abstract void populateOptions(ListBox listBox);
}
