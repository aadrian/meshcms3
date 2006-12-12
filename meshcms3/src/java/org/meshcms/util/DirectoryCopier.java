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

public class DirectoryCopier extends DirectoryParser {
  private File newDir;
  private boolean overwriteDir;
  private boolean overwriteFiles;
  private boolean setLastModified;
  private boolean result;
  
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

  protected boolean preProcess() {
    return overwriteDir || !newDir.exists();
  }

  protected boolean preProcessDirectory(File file, Path path) {
    File dir = path.getFile(newDir);
    dir.mkdirs();
    
    if (!dir.isDirectory()) {
      return result = false;
    }
    
    return true;
  }
  
  protected void processFile(File file, Path path) {
    try {
      Utils.copyFile(file, path.getFile(newDir), overwriteFiles,
          setLastModified);
    } catch (IOException ex) {
      result = false;
      ex.printStackTrace();
    }
  }

  public boolean getResult() {
    return result;
  }
}
