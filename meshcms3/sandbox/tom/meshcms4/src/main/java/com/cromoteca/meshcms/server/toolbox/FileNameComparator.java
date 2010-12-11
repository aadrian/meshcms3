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

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Sorts files by name (directories before files).
 *
 * @author Luciano Vernaschi
 */
public final class FileNameComparator implements Comparator<File>,
	Serializable {
	private boolean caseSensitive = true;

	public int compare(File f1, File f2) {
		if (f1.isDirectory() && f2.isFile()) {
			return -1;
		} else if (f1.isFile() && f2.isDirectory()) {
			return 1;
		} else if (caseSensitive) {
			return f1.getName().compareTo(f2.getName());
		} else {
			return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
		}
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
}
