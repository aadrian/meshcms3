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

import com.google.gwt.user.client.ui.TextArea;

public class MultilineField extends Field<TextArea> {
	public MultilineField(boolean required, String label) {
		super(new TextArea(), required, label);
		getFieldWidget().setCharacterWidth(40);
		getFieldWidget().setVisibleLines(6);
	}

	@Override
	public String getValue() {
		return getFieldWidget().getValue();
	}

	@Override
	public void setValue(String value) {
		getFieldWidget().setValue(value);
	}

	public MultilineField smaller(boolean narrower, boolean shorter) {
		TextArea textArea = getFieldWidget();

		if (narrower) {
			textArea.setCharacterWidth(textArea.getCharacterWidth() * 2 / 3);
		}

		if (shorter) {
			textArea.setVisibleLines(textArea.getVisibleLines() * 2 / 3);
		}

		return this;
	}

	public MultilineField bigger(boolean larger, boolean taller) {
		TextArea textArea = getFieldWidget();

		if (larger) {
			textArea.setCharacterWidth(textArea.getCharacterWidth() * 3 / 2);
		}

		if (taller) {
			textArea.setVisibleLines(textArea.getVisibleLines() * 3 / 2);
		}

		return this;
	}
}
