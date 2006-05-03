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
import java.util.*;
import com.cromoteca.util.*;

/**
 * Contains data about file types and extension.
 */
public final class FileTypes implements Finals {
  /**
   * Default icon for unknown file types.
   */
  public static final String DEFAULT_ICON = "icon_file.gif";

  /**
   * Default icon for folders.
   */
  public static final String DIR_ICON = "icon_folder.gif";

  static final String COLUMN_EXTENSIONS = "EXTENSIONS";
  static final String COLUMN_DESCRIPTION = "DESCRIPTION";
  static final String COLUMN_ICONFILE = "ICONFILE";
  static final String COLUMN_COMPRESSIBLE = "COMPRESSIBLE";
  static final String COLUMN_PREVENT_HOTLINKING = "PREVENT_HOTLINKING";

  TypeInfo unknown;
  TypeInfo directory;
  SortedMap extMap;
  int htmlId;
  int serverSideId;

  /**
   * Creates a new instance for the given WebApp.
   */
  public FileTypes(WebApp webApp) {
    unknown = new TypeInfo();
    unknown.id = -1;
    unknown.description = "Unknown";
    unknown.compressible = false;
    unknown.iconFile = DEFAULT_ICON;
    unknown.preventHotlinking = false;

    directory = new TypeInfo();
    directory.id = -1;
    directory.description = "Folder";
    directory.compressible = false;
    directory.iconFile = DIR_ICON;
    directory.preventHotlinking = false;
    
    try {
      extMap = new TreeMap();
      Table table = new Table(webApp.getFile(FILETYPES_PATH));
      TableCursor cursor = table.getCursor();
      
      while (cursor.next()) {
        TypeInfo info = new TypeInfo();
        info.id = cursor.getValue();
        info.description = table.getValueAt(cursor, COLUMN_DESCRIPTION);
        info.compressible =
            Utils.isTrue(table.getValueAt(cursor, COLUMN_COMPRESSIBLE));
        info.preventHotlinking =
            Utils.isTrue(table.getValueAt(cursor, COLUMN_PREVENT_HOTLINKING));
        info.iconFile = table.getValueAt(cursor, COLUMN_ICONFILE);
        StringTokenizer st = new StringTokenizer
            (table.getValueAt(cursor, COLUMN_EXTENSIONS), ",");
        
        while (st.hasMoreTokens()) {
          extMap.put(st.nextToken(), info);
        }
        
        htmlId = getInfo("html").id;
        serverSideId = getInfo("jsp").id;
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  TypeInfo getInfo(String extension) {
    TypeInfo info = (TypeInfo) extMap.get(extension);
    return (info == null) ? unknown : info;
  }

  /**
   * Returns true if the type of the file is the same as the given extension.
   * For example, isLike(new Path("images/button.gif"), "jpg") returns true
   * since gif and jpg are both images.
   *
   * @param file an Object of type String java.io.File or
   * com.cromoteca.util.Path (other object types are converted to Strings)
   */
  public boolean isLike(Object file, String extension) {
    return getInfo(Utils.getExtension(file, false)).id == getInfo(extension).id;
  }
  
  /**
   * Returns the description of the type of the file.
   *
   * @param file an Object of type String java.io.File or
   * com.cromoteca.util.Path (other object types are converted to Strings)
   */
  public String getDescription(Object file) {
    return getInfo(Utils.getExtension(file, false)).description;
  }
  
  /**
   * Returns the name of the icon file for the type of the given file.
   *
   * @param file an Object of type String java.io.File or
   * com.cromoteca.util.Path (other object types are converted to Strings)
   */
  public String getIconFile(Object file) {
    return getInfo(Utils.getExtension(file, false)).iconFile;
  }
  
  /**
   * Returns true if the file is supposed to be compressible. For example, text
   * files are compressible, while ZIP files are not.
   *
   * @param file an Object of type String java.io.File or
   * com.cromoteca.util.Path (other object types are converted to Strings)
   */
  public boolean isCompressible(Object file) {
    return getInfo(Utils.getExtension(file, false)).compressible;
  }
  
  /**
   * Returns true if the file should be referred from a page to be accessed.
   *
   * @param file an Object of type String java.io.File or
   * com.cromoteca.util.Path (other object types are converted to Strings)
   */
  public boolean isPreventHotlinking(Object file) {
    return getInfo(Utils.getExtension(file, false)).preventHotlinking;
  }
  
  /**
   * Returns true if the file is a page (static or server-side).
   *
   * @param file an Object of type String java.io.File or
   * com.cromoteca.util.Path (other object types are converted to Strings)
   */
  public boolean isPage(Object file) {
    int id = getInfo(Utils.getExtension(file, false)).id;
    return id == htmlId || id == serverSideId;
  }
  
  class TypeInfo {
    int id;
    String description;
    String iconFile;
    boolean compressible;
    boolean preventHotlinking;
  }
}
