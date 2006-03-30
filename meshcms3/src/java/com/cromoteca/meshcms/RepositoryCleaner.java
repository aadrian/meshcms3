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
 * Cleans the MeshCMS repository by deleting unneeded and old files.
 *
 * <ul>
 *  <li>backup files and temporary files are deleted if too old
 *      (based on backup date)</li>
 *  <li>non backup/temp files are deleted if too old (based on last modification
 *      date)</li>
 * </ul>
 */
public class RepositoryCleaner extends DirectoryParser implements Finals {
  private WebSite webSite;
  private long currentTime;

  /**
   * Creates a repository cleaner for the given web application.
   */
  public RepositoryCleaner(WebSite webSite) {
    this.webSite = webSite;
    setInitialDir(webSite.getFile(REPOSITORY_PATH));
    setRecursive(true);
    // the folder is processed after contents so we can delete empty folders
    setProcessDirBeforeContent(false);
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

    if (name.startsWith(BACKUP_PREFIX) || name.startsWith(TEMP_PREFIX)) {
      // process backup files and temp files whose file name is in the form
      // _prefix_otherinfo_lastmodified.extension
      // for example _bak_admin_20060123133144.html is a backup of a page
      // use 0 as default so files non properly named are deleted
      long bakDate = 0L;
      
      try {
        int dot = name.lastIndexOf('.');
        int us = name.substring(0, dot).lastIndexOf('_');
        bakDate = WebUtils.numericDateFormatter.parse
            (name.substring(us + 1, dot)).getTime();
      } catch (Exception ex) {}

      if (currentTime - bakDate >
          webSite.getConfiguration().getBackupLifeMillis()) { // file too old
        file.delete();
      }
    } else if (currentTime - file.lastModified() >
        webSite.getConfiguration().getBackupLifeMillis()) {
      // other files are deleted when too old.
      file.delete();
    }
  }
}
