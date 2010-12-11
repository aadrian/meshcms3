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
package com.cromoteca.meshcms.server.core;

import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.toolbox.DirectoryParser;
import com.cromoteca.meshcms.server.toolbox.IO;
import java.io.File;

/**
 * Deletes old files from a directory.
 */
public class DirectoryCleaner extends DirectoryParser {
	private long currentTime;
	private long maxLifeMillis;

	/**
	 * Creates a directory cleaner for the given web application to remove empty
	 * directories.
	 */
	public DirectoryCleaner(File directory) {
		this(directory, 0L);
	}

	/**
	 * Creates a directory cleaner for the given web application to remove old
	 * files empty directories.
	 *
	 * @param maxLifeMillis maximum file age in milliseconds
	 */
	public DirectoryCleaner(File directory, long maxLifeMillis) {
		setInitialDir(directory);
		this.maxLifeMillis = maxLifeMillis;
		setRecursive(true);
	}

	@Override
	protected void postProcessDirectory(File file, Path path) {
		// try to delete the folder: this will succeed if the folder is empty
		// so we can get rid of useless folders.
		file.delete();
	}

	@Override
	protected boolean preProcess() {
		currentTime = System.currentTimeMillis();

		return true;
	}

	@Override
	protected void processFile(File file, Path path) {
		if (maxLifeMillis > 0L) {
			String name = file.getName();

			// process files whose file name is in the form
			// something_lastmodified.extension
			// for example _bak_admin_20060128093159.html is a backup of a page
			int us = name.lastIndexOf('_');

			if (us >= 0) {
				try {
					if (currentTime
								- WebUtils.numericDateFormatter.parse(name.substring(us + 1,
										us + 15)).getTime() > maxLifeMillis) {
						IO.forceDelete(file);
					}

					return;
				} catch (Exception ex) {}
			}

			// other files are deleted when too old based on last modified date.
			if (currentTime - file.lastModified() > (long) (maxLifeMillis * (1.0
						+ Math.random() / 2.0))) {
				IO.forceDelete(file);
			}
		}
	}
}
