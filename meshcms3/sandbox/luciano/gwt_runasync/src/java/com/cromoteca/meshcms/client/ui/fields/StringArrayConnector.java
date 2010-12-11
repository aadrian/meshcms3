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

public abstract class StringArrayConnector implements FieldConnector {
	public static final String DEFAULT_SPACER = " ";
	public static final String DEFAULT_SPACERS_REGEX = "[;:, \\n]";
	private String spacer;
	private String spacersRegex;

	public StringArrayConnector() {
		spacer = DEFAULT_SPACER;
		spacersRegex = DEFAULT_SPACERS_REGEX;
	}

	public StringArrayConnector(String spacer, String spacersRegex) {
		this.spacer = spacer;
		this.spacersRegex = spacersRegex;
	}

	public void storeValue(String value) {
		storeValue(Strings.split(value, spacersRegex, true));
	}

	public String retrieveValue() {
		return Strings.generateList(loadValue(), spacer);
	}

	public abstract void storeValue(String[] value);

	public abstract String[] loadValue();
}
