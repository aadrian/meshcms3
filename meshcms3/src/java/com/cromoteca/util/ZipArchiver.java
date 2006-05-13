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

package com.cromoteca.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class ZipArchiver extends DirectoryParser {
  private ZipOutputStream zout;
  private byte[] buf;
  
  public ZipArchiver(File contents, OutputStream out) {
    zout = new ZipOutputStream(out);
    setInitialDir(contents);
    setRecursive(true);
    buf = new byte[Utils.BUFFER_SIZE];
  }

  protected boolean processDirectory(File file, Path path) {
    try {
      ZipEntry ze = new ZipEntry(path + "/");
      ze.setTime(file.lastModified());
      zout.putNextEntry(ze);
      zout.closeEntry();
    } catch (IOException ex) {
      ex.printStackTrace();
      return false;
    }
    
    return true;
  }
  
  protected void processFile(File file, Path path) {
    try {
      ZipEntry ze = new ZipEntry(path.isRoot() ? file.getName() : path.toString());
      ze.setTime(file.lastModified());
      ze.setSize(file.length());
      zout.putNextEntry(ze);
      FileInputStream fis = new FileInputStream(file);
      int len;

      while((len = fis.read(buf)) != -1) {
        zout.write(buf, 0, len);
      }

      zout.closeEntry();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  protected void postProcess() {
    try {
      zout.finish();
      zout.flush();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
}
