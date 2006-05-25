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

import java.util.*;
import org.meshcms.util.*;

/**
 * Contains data about file types and extension.
 */
public final class FileTypes {
  public static final SortedMap EXT_MAP = new TreeMap();
  
  /**
   * Default icon for unknown file types.
   */
  public static final String DEFAULT_ICON = "icon_file.gif";

  /**
   * Default icon for folders.
   */
  public static final String DIR_ICON = "icon_folder.gif";

  public static final TypeInfo UNKNOWN;
  public static final TypeInfo DIRECTORY;

  public static final int HTML_ID = 1;
  public static final int SERVERSIDE_ID = 2;

  static {
    UNKNOWN = new TypeInfo();
    UNKNOWN.id = -1;
    UNKNOWN.description = "Unknown";
    UNKNOWN.compressible = false;
    UNKNOWN.iconFile = DEFAULT_ICON;
    UNKNOWN.preventHotlinking = false;

    DIRECTORY = new TypeInfo();
    DIRECTORY.id = 0;
    DIRECTORY.description = "Folder";
    DIRECTORY.compressible = false;
    DIRECTORY.iconFile = DIR_ICON;
    DIRECTORY.preventHotlinking = false;

    TypeInfo info = new TypeInfo();
    info.id = HTML_ID;
    info.description = "Static Page";
    info.compressible = true;
    info.preventHotlinking = false;
    info.iconFile = "icon_html.gif";
    EXT_MAP.put("htm", info);
    EXT_MAP.put("html", info);
    EXT_MAP.put("xhtml", info);

    info = new TypeInfo();
    info.id = SERVERSIDE_ID;
    info.description = "Server-Side Page";
    info.compressible = true;
    info.preventHotlinking = false;
    info.iconFile = "icon_html.gif";
    EXT_MAP.put("asp", info);
    EXT_MAP.put("cgi", info);
    EXT_MAP.put("jsp", info);
    EXT_MAP.put("php", info);
    EXT_MAP.put("pl", info);
    EXT_MAP.put("shtml", info);

    info = new TypeInfo();
    info.id = 3;
    info.description = "Web Image";
    info.compressible = false;
    info.preventHotlinking = true;
    info.iconFile = "icon_image.gif";
    EXT_MAP.put("gif", info);
    EXT_MAP.put("jpeg", info);
    EXT_MAP.put("jpg", info);
    EXT_MAP.put("mng", info);
    EXT_MAP.put("png", info);

    info = new TypeInfo();
    info.id = 4;
    info.description = "Java File";
    info.compressible = true;
    info.preventHotlinking = false;
    info.iconFile = "icon_java.gif";
    EXT_MAP.put("class", info);
    EXT_MAP.put("java", info);

    info = new TypeInfo();
    info.id = 5;
    info.description = "Java Archive";
    info.compressible = false;
    info.preventHotlinking = false;
    info.iconFile = "icon_java.gif";
    EXT_MAP.put("ear", info);
    EXT_MAP.put("jar", info);
    EXT_MAP.put("war", info);

    info = new TypeInfo();
    info.id = 6;
    info.description = "XML File";
    info.compressible = true;
    info.preventHotlinking = false;
    info.iconFile = "icon_xml.gif";
    EXT_MAP.put("dtd", info);
    EXT_MAP.put("tld", info);
    EXT_MAP.put("xml", info);
    EXT_MAP.put("xsl", info);

    info = new TypeInfo();
    info.id = 7;
    info.description = "Audio File";
    info.compressible = false;
    info.preventHotlinking = true;
    info.iconFile = "icon_audio.gif";
    EXT_MAP.put("au", info);
    EXT_MAP.put("mp3", info);
    EXT_MAP.put("ogg", info);
    EXT_MAP.put("wav", info);
    EXT_MAP.put("wma", info);

    info = new TypeInfo();
    info.id = 8;
    info.description = "Video File";
    info.compressible = false;
    info.preventHotlinking = true;
    info.iconFile = "icon_video.gif";
    EXT_MAP.put("avi", info);
    EXT_MAP.put("mov", info);
    EXT_MAP.put("mpeg", info);
    EXT_MAP.put("mpg", info);
    EXT_MAP.put("wmv", info);

    info = new TypeInfo();
    info.id = 9;
    info.description = "Archive";
    info.compressible = false;
    info.preventHotlinking = true;
    info.iconFile = "icon_archive.gif";
    EXT_MAP.put("7z", info);
    EXT_MAP.put("bz2", info);
    EXT_MAP.put("gz", info);
    EXT_MAP.put("rar", info);
    EXT_MAP.put("rpm", info);
    EXT_MAP.put("tar", info);
    EXT_MAP.put("tgz", info);
    EXT_MAP.put("z", info);
    EXT_MAP.put("zip", info);

    info = new TypeInfo();
    info.id = 10;
    info.description = "Plain Text File";
    info.compressible = true;
    info.preventHotlinking = false;
    info.iconFile = "icon_text.gif";
    EXT_MAP.put("log", info);
    EXT_MAP.put("txt", info);

    info = new TypeInfo();
    info.id = 11;
    info.description = "Style Sheet File";
    info.compressible = true;
    info.preventHotlinking = false;
    info.iconFile = "icon_style.gif";
    EXT_MAP.put("css", info);

    info = new TypeInfo();
    info.id = 12;
    info.description = "Script File";
    info.compressible = true;
    info.preventHotlinking = false;
    info.iconFile = "icon_script.gif";
    EXT_MAP.put("js", info);

    info = new TypeInfo();
    info.id = 13;
    info.description = "Executable File";
    info.compressible = false;
    info.preventHotlinking = true;
    info.iconFile = "icon_program.gif";
    EXT_MAP.put("bin", info);
    EXT_MAP.put("exe", info);

    info = new TypeInfo();
    info.id = 14;
    info.description = "Word Document";
    info.compressible = true;
    info.preventHotlinking = true;
    info.iconFile = "icon_word.gif";
    EXT_MAP.put("doc", info);
    EXT_MAP.put("rtf", info);

    info = new TypeInfo();
    info.id = 15;
    info.description = "Excel Document";
    info.compressible = true;
    info.preventHotlinking = true;
    info.iconFile = "icon_excel.gif";
    EXT_MAP.put("xls", info);

    info = new TypeInfo();
    info.id = 16;
    info.description = "PowerPoint Document";
    info.compressible = true;
    info.preventHotlinking = true;
    info.iconFile = "icon_powerpoint.gif";
    EXT_MAP.put("pps", info);
    EXT_MAP.put("ppt", info);

    info = new TypeInfo();
    info.id = 17;
    info.description = "PDF Document";
    info.compressible = false;
    info.preventHotlinking = true;
    info.iconFile = "icon_pdf.gif";
    EXT_MAP.put("pdf", info);

    info = new TypeInfo();
    info.id = 18;
    info.description = "Image";
    info.compressible = false;
    info.preventHotlinking = true;
    info.iconFile = "icon_image.gif";
    EXT_MAP.put("bmp", info);
    EXT_MAP.put("psd", info);
    EXT_MAP.put("tga", info);
    EXT_MAP.put("tif", info);
    EXT_MAP.put("tiff", info);

    info = new TypeInfo();
    info.id = 19;
    info.description = "Icon";
    info.compressible = true;
    info.preventHotlinking = false;
    info.iconFile = "icon_image.gif";
    EXT_MAP.put("ico", info);

    info = new TypeInfo();
    info.id = 20;
    info.description = "Flash File";
    info.compressible = false;
    info.preventHotlinking = true;
    info.iconFile = "icon_flash.gif";
    EXT_MAP.put("swf", info);

    info = new TypeInfo();
    info.id = 21;
    info.description = "Isaac File";
    info.compressible = false;
    info.preventHotlinking = true;
    info.iconFile = "icon_isaac.gif";
    EXT_MAP.put("isc", info);
  }
  
  static TypeInfo getInfo(String extension) {
    TypeInfo info = (TypeInfo) EXT_MAP.get(extension);
    return (info == null) ? UNKNOWN : info;
  }

  /**
   * Returns true if the type of the file is the same as the given extension.
   * For example, isLike(new Path("images/button.gif"), "jpg") returns true
   * since gif and jpg are both images.
   *
   * @param file an Object of type String java.io.File or
   * org.meshcms.util.Path (other object types are converted to Strings)
   */
  public static boolean isLike(Object file, String extension) {
    return getInfo(Utils.getExtension(file, false)).id == getInfo(extension).id;
  }
  
  /**
   * Returns the description of the type of the file.
   *
   * @param file an Object of type String java.io.File or
   * org.meshcms.util.Path (other object types are converted to Strings)
   */
  public static String getDescription(Object file) {
    return getInfo(Utils.getExtension(file, false)).description;
  }
  
  /**
   * Returns the name of the icon file for the type of the given file.
   *
   * @param file an Object of type String java.io.File or
   * org.meshcms.util.Path (other object types are converted to Strings)
   */
  public static String getIconFile(Object file) {
    return getInfo(Utils.getExtension(file, false)).iconFile;
  }
  
  /**
   * Returns true if the file is supposed to be compressible. For example, text
   * files are compressible, while ZIP files are not.
   *
   * @param file an Object of type String java.io.File or
   * org.meshcms.util.Path (other object types are converted to Strings)
   */
  public static boolean isCompressible(Object file) {
    return getInfo(Utils.getExtension(file, false)).compressible;
  }
  
  /**
   * Returns true if the file should be referred from a page to be accessed.
   *
   * @param file an Object of type String java.io.File or
   * org.meshcms.util.Path (other object types are converted to Strings)
   */
  public static boolean isPreventHotlinking(Object file) {
    return getInfo(Utils.getExtension(file, false)).preventHotlinking;
  }
  
  /**
   * Returns true if the file is a page (static or server-side).
   *
   * @param file an Object of type String java.io.File or
   * org.meshcms.util.Path (other object types are converted to Strings)
   */
  public static boolean isPage(Object file) {
    int id = getInfo(Utils.getExtension(file, false)).id;
    return id == HTML_ID || id == SERVERSIDE_ID;
  }
  
  static class TypeInfo {
    int id;
    String description;
    String iconFile;
    boolean compressible;
    boolean preventHotlinking;
  }
}