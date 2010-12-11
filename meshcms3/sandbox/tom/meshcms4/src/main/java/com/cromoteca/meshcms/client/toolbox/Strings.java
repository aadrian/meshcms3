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
package com.cromoteca.meshcms.client.toolbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Useful methods and costants about Strings.
 */
public class Strings {
	protected Strings() {}

	public static String limitedLength(String s, int len, String hellipsis,
		boolean atEnd) {
		String s1;

		if (Strings.isNullOrEmpty(s)) {
			s1 = "";
		} else if (s.length() <= len) {
			s1 = s;
		} else if (len <= hellipsis.length()) {
			s1 = "";
		} else if (atEnd) {
			s1 = s.substring(0, len - hellipsis.length()) + hellipsis;
		} else {
			s1 = hellipsis + s.substring(s.length() - len + hellipsis.length());
		}

		return s1;
	}

	public static String[] split(String s, String regex, boolean trim) {
		if (s == null) {
			return null;
		}

		List<String> list = new ArrayList<String>();
		String[] result = s.split(regex);

		if (result != null) {
			for (String str : result) {
				if (trim) {
					str = str.trim();
				}

				if (str.length() > 0) {
					list.add(str);
				}
			}
		}

		return list.toArray(new String[list.size()]);
	}

	public static String generateList(String[] list, String sep) {
		return list == null ? null : generateList(Arrays.asList(list), sep);
	}

	/**
	 * Creates a <code>String</code> containing the string representations of the
	 * objects in the array, separated by <code>sep</code>. It can be seen as
	 * somewhat opposite of <code>java.util.StringTokenizer</code>.
	 */
	public static <T> String generateList(List<T> list, String sep) {
		if (list == null) {
			return null;
		}

		if (list.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(list.get(0));

		for (int i = 1; i < list.size(); i++) {
			sb.append(sep).append(list.get(i));
		}

		return sb.toString();
	}

	/**
	 * Creates a <code>String</code> containing the string representations of the
	 * objects in the array, separated by <code>sep</code>. It can be seen as
	 * somewhat opposite of <code>java.util.StringTokenizer</code>.
	 */
	public static <T> String generateList(List<T> list, String sep,
		Function<String, T> func) {
		if (list == null) {
			return null;
		}

		if (list.size() == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(func.execute(list.get(0)));

		for (int i = 1; i < list.size(); i++) {
			sb.append(sep).append(func.execute(list.get(i)));
		}

		return sb.toString();
	}

	/**
	 * Checks if an object is null or if its string representation is empty.
	 *
	 * @param s the String to be checked
	 *
	 * @return <code>true</code> if the String is null or empty, <code>false</code>
	 *         otherwise.
	 */
	public static boolean isNullOrEmpty(CharSequence s) {
		return s == null || s.length() == 0;
	}

	public static boolean isNullOrWhitespace(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static String noNull(String s) {
		return s == null ? "" : s;
	}

	/**
	 * Removes the extension from a file name. The dot is removed too.
	 *
	 * @param fileName the file name
	 *
	 * @return the name without extension
	 */
	public static String removeExtension(String fileName) {
		if (fileName == null) {
			return null;
		}

		int dot = fileName.lastIndexOf('.');

		return dot == -1 ? fileName : fileName.substring(0, dot);
	}

	/**
	 * Returns the extension of the given file name, with or without the dot.
	 *
	 * @param fileName   the name of the File to be processed
	 * @param includeDot if true, the dot is returned with the extension
	 *
	 * @return the extension
	 */
	public static String getExtension(String fileName, boolean includeDot) {
		if (fileName == null) {
			return null;
		}

		int dot = fileName.lastIndexOf('.');

		return dot == -1 ? ""
		: fileName.substring(includeDot ? dot : dot + 1).toLowerCase();
	}

	public static String getExtension(Path path, boolean includeDot) {
		return getExtension(path.getLastElement(), includeDot);
	}

	public static String asConstantName(String s) {
		return s.toUpperCase().replace(' ', '_');
	}

	/**
	 * Converts the underscores to spaces and, if requested, applies the title case
	 * to a string.
	 *
	 * @param s         the String to be beautified
	 * @param titleCase flag if to title case. See {@link Character#toTitleCase(char)}
	 *
	 * @return the converted String.
	 */
	public static String beautify(CharSequence s, boolean titleCase) {
		StringBuilder sb = new StringBuilder(s.length());
		boolean nextUpper = true;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == '_' || c == '-') {
				c = ' ';
			}

			if (c == ' ') {
				nextUpper = true;
			} else {
				if (titleCase && nextUpper) {
					c = Character.toUpperCase(c);
					nextUpper = false;
				}
			}

			sb.append(c);
		}

		return sb.toString();
	}

	public static boolean isInvalidEmail(String value) {
		int at = value.indexOf('@');
		int dot = value.lastIndexOf('.');

		return at <= 0 || dot <= 0 || dot < at || dot == at + 1
				|| dot > value.length() - 3;
	}

	/**
	 * Searches a string in an array of strings.
	 *
	 * @param array      the array of strings
	 * @param s          the string to be searched
	 * @param ignoreCase used to ask for a case insensitive search
	 *
	 * @return the index of the first occurrence, or -1 if not found
	 */
	public static int searchString(String[] array, String s, boolean ignoreCase) {
		return searchString(Arrays.asList(array), s, ignoreCase);
	}

	public static int searchString(List<String> strings, String s,
		boolean ignoreCase) {
		if (strings == null || strings.size() == 0 || s == null) {
			return -1;
		}

		if (ignoreCase) {
			for (int i = 0; i < strings.size(); i++) {
				if (s.equalsIgnoreCase(strings.get(i))) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < strings.size(); i++) {
				if (s.equals(strings.get(i))) {
					return i;
				}
			}
		}

		return -1;
	}

	public static boolean equal(String s1, String s2, boolean ignoreCase) {
		if (s1 == null) {
			return s2 == null;
		} else {
			return ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
		}
	}

	public static String[] commonPart(String[] sa1, String[] sa2, boolean fromEnd) {
		int len1 = sa1.length;
		int len2 = sa2.length;
		int cnt = Math.min(len1, len2);

		if (fromEnd) {
			for (int i = 1; i <= cnt; i++) {
				if (!sa1[len1 - i].equals(sa2[len2 - i])) {
					cnt = i - 1;
				}
			}
		} else {
			for (int i = 0; i < cnt; i++) {
				if (!sa1[i].equals(sa2[i])) {
					cnt = i;
				}
			}
		}

		String[] result = new String[cnt];
		System.arraycopy(sa1, fromEnd ? len1 - cnt : 0, result, 0, cnt);

		return result;
	}

	/**
	 * Parses the string argument as an integer, but without returning exception.
	 * If that would be the case, the default value provided is returned instead.
	 *
	 * @param s   the string to be converted to <code>int<
	 * @param def default value in case the string is not parasble.
	 *
	 * @return a <code>int</code> representation of a String, or a default value if
	 *         the string can't be parsed.
	 */
	public static int parseInt(String s, int def) {
		try {
			def = Integer.parseInt(s);
		} catch (Exception ex) {}

		return def;
	}
}
