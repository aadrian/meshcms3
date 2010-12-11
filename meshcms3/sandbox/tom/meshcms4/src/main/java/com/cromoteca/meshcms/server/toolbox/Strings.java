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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Strings extends com.cromoteca.meshcms.client.toolbox.Strings {
	public static String getMD5(String s) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());

			String result = new BigInteger(1, m.digest()).toString(16);

			while (result.length() < 32) {
				result = '0' + result;
			}

			return result;
		} catch (NoSuchAlgorithmException ex) {
			return null;
		}
	}

	public static String escapeRegexReplacement(String s) {
		return s.replaceAll("[\\\\\\$]", "\\\\$0");
	}

	/**
	 * Strips the HTML tags from a string.
	 *
	 * @param s the HTML String to be processed
	 *
	 * @return the stripped String.
	 */
	public static String stripHTMLTags(String s) {
		return s != null ? s.replaceAll("</?\\S+?[\\s\\S+]*?>", " ") : null;
	}

	public static String toTitleCase(String s) {
		char[] chars = s.trim().toLowerCase().toCharArray();
		boolean found = false;

		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i])) {
				found = false;
			}
		}

		return String.valueOf(chars);
	}
}
