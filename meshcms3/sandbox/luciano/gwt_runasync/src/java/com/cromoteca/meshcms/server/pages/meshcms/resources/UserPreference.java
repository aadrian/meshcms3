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
package com.cromoteca.meshcms.server.pages.meshcms.resources;

import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.SessionUser;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.webview.ServerPage;
import java.io.IOException;
import java.util.Map;

public class UserPreference implements ServerPage {
	private String name;
	private String value;

	public Object getBean() {
		return this;
	}

	public String process() {
		if (!Strings.isNullOrEmpty(name)) {
			SessionUser sessionUser = Context.getUser();

			if (sessionUser != null) {
				Map<String, String> preferences = sessionUser.getUser().getPreferences();

				if (value == null) {
					value = preferences.get(name);
				} else {
					if (value.length() == 0) {
						preferences.remove(name);
					} else {
						preferences.put(name, value);
					}

					try {
						sessionUser.store();
					} catch (IOException ex) {
						Context.log(ex);
					}
				}
			}
		}

		return null;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
