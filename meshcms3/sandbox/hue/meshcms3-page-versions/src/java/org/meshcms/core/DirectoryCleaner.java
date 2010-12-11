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
public class DirectoryCleaner extends DirectoryParser {

  /**
   * Creates a repository cleaner for the given web application.
   */
  public DirectoryCleaner(File directory) {
    setInitialDir(directory);
    setRecursive(true);
    // the folder is processed after contents so we can delete empty folders
    setProcessDirBeforeContent(false);
    setDaemon(true);
    setName("DirectoryCleaner for \"" + Utils.getFilePath(directory) + '"');
    setPriority(Thread.MIN_PRIORITY);
  }

  protected boolean preProcess() {
    return true;
  }

  protected boolean processDirectory(File file, Path path) {
    // try to delete the folder: this will succeed if the folder is empty
    // so we can get rid of useless folders.
    file.delete();
    return true;
  }

  protected void processFile(File file, Path path) {
  	// Do nothing by default
  }
}
