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
package com.cromoteca.meshcms.server.toolbox;

import java.util.Locale;

public class Locales {
	/**
	 * Returns the java.util.Locale object for a given locale name (e.g. en_US).
	 *
	 * @param localeName the locale name to be searched.
	 *
	 * @return the found {@link java.util.Locale} object for the given locale name,
	 *         or null if not found.
	 */
	public static Locale getLocale(String localeName) {
		if (!Strings.isNullOrEmpty(localeName)) {
			Locale[] locales = Locale.getAvailableLocales();

			for (Locale locale : locales) {
				if (localeName.equals(locale.toString())) {
					return locale;
				}
			}
		}

		return null;
	}
}
