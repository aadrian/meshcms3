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
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

public abstract class SortedSelectionField extends SelectionField {
	public SortedSelectionField(String label) {
		super(label);
	}

	@Override
	public void populateOptions(ListBox listBox) {
		SortedMap<String, String> itemValueMap = new TreeMap<String, String>(new Comparator<String>() {
					public int compare(String o1, String o2) {
						return o1.compareToIgnoreCase(o2);
					}
				});
		populateOptions(itemValueMap);

		for (String item : itemValueMap.keySet()) {
			listBox.addItem(item, itemValueMap.get(item));
		}
	}

	public abstract void populateOptions(SortedMap<String, String> itemValueMap);
}
