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
package com.cromoteca.meshcms.client.ui.filemanager;

import com.cromoteca.meshcms.client.toolbox.Path;
import java.util.ArrayList;
import java.util.List;

public class FileClipboard {
	private List<Path> contents;
	private Path dirPath;
	private boolean cut;

	public FileClipboard() {
		contents = new ArrayList<Path>();
	}

	public void addPath(Path path) {
		if (!contents.contains(path)) {
			contents.add(path);
		}
	}

	/**
	 * @return the dirPath
	 */
	public Path getDirPath() {
		return dirPath;
	}

	/**
	 * @param dirPath the dirPath to set
	 */
	public void setDirPath(Path dirPath) {
		this.dirPath = dirPath;
	}

	/**
	 * @return the filePaths
	 */
	public List<Path> getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(List<Path> contents) {
		this.contents = contents;
	}

	/**
	 * @return the cut
	 */
	public boolean isCut() {
		return cut;
	}

	/**
	 * @param cut the cut to set
	 */
	public void setCut(boolean cut) {
		this.cut = cut;
	}

	public void clear() {
		contents.clear();
	}
}
