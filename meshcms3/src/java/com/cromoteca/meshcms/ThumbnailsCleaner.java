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
 * Cleans old and unneeded thumbnails.
 */
public class ThumbnailsCleaner extends DirectoryParser implements Finals {
  private WebApp webApp;
  private long currentTime;
  private Configuration configuration;

  /**
   * Creates a thumbnail cleaner for the given web application.
   */
  public ThumbnailsCleaner(WebApp webApp) {
    this.webApp = webApp;
    configuration = webApp.getConfiguration();
    setInitialDir(webApp.getFile(new Path(configuration.getThumbnailsDir())));
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
    Path parentPath = path.getParent();

    // searching for a valid file corresponding to part of the path
    while (!webApp.getFile(parentPath).isFile()) {
      parentPath = parentPath.getParent();

      if (parentPath.isRoot()) {
        // no image file found for this thumbnail
        file.delete();
        return;
      }
    }

    // delete old thumbnails anyway
    if (currentTime - file.lastModified() >
        configuration.getBackupLifeMillis()) {
      file.delete();
    }
  }
}
