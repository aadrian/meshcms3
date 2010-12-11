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

public class IntegerField extends TextField {
	public static final int ALL_NUMBERS = 0;
	public static final int POSITIVE_OR_ZERO = 1;
	public static final int POSITIVE_ONLY = 2;
	private int mode;

	public IntegerField(boolean required, String label, int mode) {
		super(required, label);
		this.mode = mode;
		smaller();
		smaller();
	}

	@Override
	public boolean isInvalid() {
		String value = getValue();
		boolean invalid;

		if (Strings.isNullOrEmpty(value)) {
			invalid = isRequired();
		} else {
			try {
				int n = Integer.parseInt(value);

				switch (mode) {
					case POSITIVE_OR_ZERO:
						invalid = n < 0;

						break;

					case POSITIVE_ONLY:
						invalid = n <= 0;

						break;

					default:
						invalid = false;
				}
			} catch (NumberFormatException ex) {
				invalid = true;
			}
		}

		return invalid;
	}

	public abstract static class IntegerConnector implements FieldConnector {
		public void storeValue(String value) {
			storeValue(Integer.parseInt(value));
		}

		public String retrieveValue() {
			return Integer.toString(loadValue());
		}

		public abstract void storeValue(int value);

		public abstract int loadValue();
	}
}
