/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2008 Luciano Vernaschi
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
package org.meshcms.extra;

import java.io.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

public class SiteSynchronizer extends DirectoryParser {
  WebSite sourceSite;
  WebSite targetSite;
  UserInfo targetUser;
  Writer writer;

  public SiteSynchronizer(WebSite sourceSite, WebSite targetSite,
      UserInfo targetUser) {
    this.sourceSite = sourceSite;
    this.targetSite = targetSite;
    this.targetUser = targetUser;
    setInitialDir(sourceSite.getRootFile());
    setRecursive(true);
    setProcessStartDir(true);
  }

  /**
   * Sets the writer for logging (usually the writer of the web page).
   */
  public void setWriter(Writer writer) {
    this.writer = writer;
  }

  /**
   * Returns the writer (if any).
   */
  public Writer getWriter() {
    return writer;
  }

  private void write(String message) {
    if (writer != null) {
      try {
        writer.write(message);
        writer.write('\n');
        writer.flush();
      } catch (IOException ex) {
      }
    }
  }

  protected boolean preProcessDirectory(File file, Path path) {
    if (path.isContainedIn(sourceSite.getCMSPath()) || sourceSite.isSystem(path)) {
      return false;
    }
    
    if (path.isContainedIn(targetSite.getCMSPath())) {
      write(path + " folder NOT copied");
      return false;
    }

    File targetDir = targetSite.getFile(path);

    if (targetDir.isFile()) {
      if (targetSite.delete(targetUser, path, false)) {
        write(path + " file deleted");
      } else {
        write(path + " file NOT deleted");
      }
    }

    targetSite.createDir(path);
    File[] targetFiles = targetDir.listFiles();

    for (int i = 0; i < targetFiles.length; i++) {
      File srcFile = new File(file, targetFiles[i].getName());

      if (!((srcFile.isFile() && targetFiles[i].isFile()) ||
          (srcFile.isDirectory() && targetFiles[i].isDirectory()))) {
        Path filePath = path.add(targetFiles[i].getName());

        if (targetSite.delete(targetUser, filePath, true)) {
          write(filePath + " folder deleted");
        } else {
          write(filePath + " folder NOT deleted");
        }
      }
    }

    return true;
  }

  protected void processFile(File file, Path path) {
    File targetFile = targetSite.getFile(path);

    if (targetFile.isDirectory()) {
      if (targetSite.delete(targetUser, path, true)) {
        write(path + " folder deleted");
      } else {
        write(path + " folder NOT deleted");
      }
    }

    if (!targetFile.exists() || file.lastModified() > targetFile.lastModified() ||
        file.length() != targetFile.length()) {
      try {
        FileInputStream fis = new FileInputStream(file);
        targetSite.saveToFile(targetUser, fis, path);
        write(path + " file copied");
        fis.close();
      } catch (IOException ex) {
        write(path + " file NOT copied");
        targetSite.log(ex.getMessage(), ex);
      }
    }
  }

  protected void postProcess() {
    targetSite.updateSiteMap(true);
  }
}
