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

package org.meshcms.util;

import java.io.*;

/**
 * Deletes a directory even if not empty.
 */
public class DirectoryRemover extends DirectoryParser {
  private boolean result;
  
  public DirectoryRemover(File dir) {
    setInitialDir(dir);
    setRecursive(true);
    setProcessDirBeforeContent(false);
    setProcessStartDir(true);
    result = true;
  }

  protected boolean processDirectory(File file, Path path) {
    if (!file.delete()) {
      result = false;
    }
    
    return true; // processDirBeforeContent is false, so the return value is useless
  }
  
  protected void processFile(File file, Path path) {
    if (!file.delete()) {
      result = false;
    }
  }

  /**
   * This method can be called after processing to know whether the directory
   * has been fully deleted or not.
   */
  public boolean getResult() {
    return result;
  }
}
