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
import java.io.File;
import java.io.IOException;

public class DirectoryCopier extends DirectoryParser {
	private File newDir;
	private boolean overwriteDir;
	private boolean overwriteFiles;
	private boolean result;
	private boolean setLastModified;

	public DirectoryCopier(File dir, File newDir, boolean overwriteDir,
		boolean overwriteFiles, boolean setLastModified) {
		setInitialDir(dir);
		this.newDir = newDir;
		this.overwriteDir = overwriteDir;
		this.overwriteFiles = overwriteFiles;
		this.setLastModified = setLastModified;
		setRecursive(true);
		setProcessStartDir(true);
		result = true;
	}

	public boolean getResult() {
		return result;
	}

	@Override
	protected boolean preProcess() {
		return overwriteDir || !newDir.exists();
	}

	@Override
	protected boolean preProcessDirectory(File file, Path path) {
		File dir = IO.getFileFromPath(newDir, path);
		dir.mkdirs();

		if (dir.isFile()) {
			return result = false;
		}

		return true;
	}

	@Override
	protected void processFile(File file, Path path) {
		try {
			IO.copyFile(file, IO.getFileFromPath(newDir, path), overwriteFiles,
				setLastModified);
		} catch (IOException ex) {
			result = false;
			ex.printStackTrace();
		}
	}
}
