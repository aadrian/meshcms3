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
package com.cromoteca.meshcms.client.ui.widgets;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.TextBox;

public class WideTextBox extends TextBox implements FocusHandler {
	private AutoSelectionType autoSelectionType = AutoSelectionType.NONE;

	public WideTextBox() {
		setVisibleLength(30);
		addFocusHandler(this);
	}

	public WideTextBox(Element element) {
		super(element);
		addFocusHandler(this);
	}

	public AutoSelectionType getAutoSelectionType() {
		return autoSelectionType;
	}

	public void setAutoSelectionType(AutoSelectionType autoSelectionType) {
		this.autoSelectionType = autoSelectionType;
	}

	public void onFocus(FocusEvent event) {
		switch (autoSelectionType) {
			case FILENAME:

				String text = getValue();
				int dot = text.lastIndexOf('.');

				if (dot > 1
							&& text.length() > dot + 1
							&& Character.isLetterOrDigit(text.charAt(dot + 1))) {
					setSelectionRange(0, dot);
				} else {
					selectAll();
				}

				break;

			case FULL:
				selectAll();

				break;
		}
	}
	public static enum AutoSelectionType {
		FILENAME,
		FULL,
		NONE;
	}
}
