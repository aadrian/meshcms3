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

package com.cromoteca.meshcms;

import java.io.*;
import com.cromoteca.util.*;

/**
 * Deletes old files from a directory.
 */
public class DirectoryCleaner extends DirectoryParser implements Finals {
  private long currentTime;
  private long maxLife;

  /**
   * Creates a repository cleaner for the given web application.
   */
  public DirectoryCleaner(File directory, long maxLifeMillis) {
    setInitialDir(directory);
    maxLife = maxLifeMillis;
    setRecursive(true);
    // the folder is processed after contents so we can delete empty folders
    setProcessDirBeforeContent(false);
    setDaemon(true);
    setName("DirectoryCleaner for " + Utils.getFilePath(directory));
  }

  protected boolean preProcess() {
    currentTime = System.currentTimeMillis();
    return true;
  }

  protected boolean processDirectory(File file, Path path) {
    // try to delete the folder: this will succeed if the folder is empty
    // so we can get rid of useless folders.
    file.delete();
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
    if (currentTime - file.lastModified() > maxLife) {
      file.delete();
    }
  }
}
