/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2006 Luciano Vernaschi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * You can contact the author at http://www.cromoteca.com
 * and at info@cromoteca.com
 */

package org.meshcms.core;

import java.io.*;

import org.meshcms.util.*;

/**
 * Deletes old files from a directory.
 */
public class DirectoryCleanerByRegex extends DirectoryCleaner {
  private String regex;

	/**
   * Creates a repository cleaner for the given web application.
   */
	public DirectoryCleanerByRegex(File directory, String regexFileMask) {
		super(directory);
    this.regex = regexFileMask;
	}

  protected void processFile(File file, Path path) {
    String name = file.getName();

    // process files that match the regex
    try {
    	if (name.matches(this.regex)) {
   			file.delete();
    	}
    } catch (Exception ex) {}
  }
}