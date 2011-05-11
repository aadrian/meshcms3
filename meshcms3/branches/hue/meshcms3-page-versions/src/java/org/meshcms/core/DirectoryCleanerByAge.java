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
public class DirectoryCleanerByAge extends DirectoryCleaner {
  private long currentTime;
  private long maxLife;

	/**
   * Creates a repository cleaner for the given web application.
   */
	public DirectoryCleanerByAge(File directory, long maxLifeMillis) {
		super(directory);
    maxLife = maxLifeMillis;
	}

  protected boolean preProcess() {
    currentTime = System.currentTimeMillis();
    return true;
  }

  protected void processFile(File file, Path path) {
    String name = file.getName();

    // process files whose file name is in the form
    // something_lastmodified.extension
    // for example _bak_admin_20060128093159.html is a backup of a page
    int us = name.lastIndexOf('_');
    
    if (us >= 0) {
      try {
        if (currentTime - WebUtils.numericDateFormatter.parse(name.substring(us + 1, us + 15)).getTime() > maxLife) {
          file.delete();
        }
        
        return;
      } catch (Exception ex) {}
    }
    
    // other files are deleted when too old based on last modified date.
    if (currentTime - file.lastModified() > (long) (maxLife * (1.0 + Math.random() / 2.0))) {
      file.delete();
    }
  }
}
