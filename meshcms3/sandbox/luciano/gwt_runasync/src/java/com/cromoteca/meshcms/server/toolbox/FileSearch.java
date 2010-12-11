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

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.storage.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileSearch extends DirectoryParser {
	private List<Path> results;
	private Pattern regex;

	public FileSearch(File dir, String regex) {
		this.regex = Pattern.compile(regex);
		setInitialDir(dir);
		setRecursive(true);
		setSorted(true);
		results = new ArrayList<Path>();
	}

	public Path[] getResults() {
		return results.toArray(new Path[results.size()]);
	}

	@Override
	protected void processFile(File file, Path path) {
		if (regex.matcher(file.getName()).matches()) {
			results.add(path);
		}
	}
}
