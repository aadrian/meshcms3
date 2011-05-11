/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2005 Luciano Vernaschi
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

/**
 * Cleans the directory used to export static files. This is done by removing
 * all files that are not available in the dynamic version of the site. Empty
 * directories are also removed. This class is used by {@link StaticExporter}.
 */
public class StaticExportCleaner extends DirectoryParser {
  File contextRoot;
  Writer writer;
  private Path protectedPath;

  /**
   * Creates an instance for the given context root
   */
  public StaticExportCleaner(File contextRoot) {
    super();
    this.contextRoot = contextRoot;
    setRecursive(true);
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

  protected boolean preProcess() {
    return contextRoot != null && contextRoot.exists();
  }

  protected boolean preProcessDirectory(File file, Path path) {
    return !path.equals(protectedPath);
  }

  protected void postProcessDirectory(File file, Path path) {
    if (file.delete()) {
      write("empty " + path + " directory deleted");
    }
  }

  protected void processFile(File file, Path path) {
    if (!path.getFile(contextRoot).exists() ||
        file.getName().equals(WebSite.CMS_ID_FILE) ||
        file.getName().equals(WebSite.ADMIN_ID_FILE)) {
      if (file.delete()) {
        write(path + " file deleted");
      }
    }
  }

  void write(String message) {
    if (writer != null) {
      try {
        writer.write(message);
        writer.write('\n');
        writer.flush();
      } catch (IOException ex) {}
    }
  }

  public Path getProtectedPath() {
    return protectedPath;
  }

  public void setProtectedPath(Path protectedPath) {
    this.protectedPath = protectedPath;
  }
}
