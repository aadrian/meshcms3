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

import com.cromoteca.util.*;

/**
 * Some constant values.
 */
public interface Finals {
  /**
   * The length of a hour in milliseconds.
   */
  public static final long LENGTH_OF_HOUR = 60 * 60 * 1000;

  /**
   * The length of a day in milliseconds.
   */
  public static final long LENGTH_OF_DAY = 24 * LENGTH_OF_HOUR;

  /**
   * A prefix to be used for every backup file.
   */
  public static final String BACKUP_PREFIX = "_bak_";

  /**
   * A prefix to be used for every temporary file created in the repository.
   */
  public static final String TEMP_PREFIX = "_tmp_";

  /**
   * A prefix to be used for every thumbnail.
   */
  public static final String THUMB_PREFIX = "_thumb_";

  /**
   * Name of a cache file in the repository.
   */
  public static final String CACHE_FILE_NAME = "_cache.gz";

  /**
   * Path of the directory that contains user profiles.
   */
  public static final Path USERS_PATH = new Path("WEB-INF/meshcms/users");
  
  /**
   * Path of the file that contains site preferences.
   */
  public static final Path CONFIG_PATH =
      new Path("WEB-INF/meshcms/configuration.xml");

  /**
   * Path for the menu configuration file.
   */
  public static final Path PROPS_PATH =
      new Path("WEB-INF/meshcms/siteprops.xml");

  /**
   * Path for the menu configuration file.
   */
  public static final Path FILETYPES_PATH =
      new Path("WEB-INF/meshcms/filetypes.txt");

  /**
   * Path of the repository (contains backups and thumbnails)
   */
  public static final Path REPOSITORY_PATH =
      new Path("WEB-INF/meshcms/repository");

  /**
   * Path of the folder where a copy of all message sent through mail forms is
   * stored.
   */
  public static final Path MESSAGES_PATH =
      new Path("WEB-INF/meshcms/messages");
  
  public static final String MODULE_INCLUDE_FILE = "include.jsp";

  /**
   * The name of the properties for the modules to include in the page.
   */
  public static final String MODULES_PARAM = "meshcmsmodules";
  
  /**
   * The name of the properties for the mail recipient of the page.
   */
  public static final String EMAIL_PARAM = "meshcmsmail";

  public static final String MODULES_SUBDIRECTORY = "modules";

  public static final String GENERATED_FILES_SUBDIRECTORY = "generated";
  
  public static final String THEMES_SUBDIRECTORY = "themes";
  
  public static final String ADMIN_SUBDIRECTORY = "admin";

  /**
   * Name of the page context attribute that stores the descriptions of the
   * modules contained in a page.
   *
   * @see com.cromoteca.meshcms.taglib.Module
   * @see ModuleDescriptor
   */
  public static final String PAGE_MODULES = "page_modules";

  /**
   * Name of the file name that contains the version id of MeshCMS. This file
   * is used to find the admin folder.
   */
  public static final String ID_FILE = "meshcms_id.txt";

  /**
   * Name of the default admin theme folder.
   */
  public static final String ADMIN_THEME = "theme";

  /**
   * Name of the request parameter that is used to specify some actions.
   * Currently only {@link #ACTION_EDIT} is used as value. This parameter is
   * read by custom JSP tags.
   */
  public static final String ACTION_NAME = "meshcmsaction";

  /**
   * Value of {@link #ACTION_NAME} used to indicate that the current page
   * must be edited.
   */
  public static final String ACTION_EDIT = "edit";
  
  /**
   * Name of the main file of a theme.
   */
  public static final String THEME_DECORATOR = "main.jsp";

  /**
   * Name of the stylesheet of a theme.
   */
  public static final String THEME_CSS = "main.css";

  /**
   * Name of the CSS that contains styles for elements of the MeshCMS interface
   * (mail forms, editor and so on).
   */
  public static final String MESHCMS_CSS = "meshcms.css";

  /**
   * Name of the request attribute that contains the name of the current theme
   * file.
   *
   * @see RequestDecoratorMapper
   */
  public static final String THEME_FILE_ATTRIBUTE = "meshcmstheme";

  /**
   * Name of the request attribute that contains the name of the current theme
   * folder.
   */
  public static final String THEME_PATH_ATTRIBUTE = "meshcmsthemepath";
  
  public static final String LOCALE_ATTRIBUTE = "meshcmslocale";
  
  public static final String LAST_MODIFIED_ATTRIBUTE = "meshcmslastmodified";

  /**
   * Generic string used to indicate an empty value.
   */
  public static final String EMPTY = "(none)";

  /**
   * Name of the HTTP header that indicates a request coming from MeshCMS
   * itself. This header is set when MeshCMS requests a page to cache it.
   */
  public static final String CACHE_HEADER = "MeshCMS-Cache";
  
  /**
   * Name of the session attribute that allows hotlinking within the session
   * itself.
   */
  public static final String HOTLINKING_ALLOWED = "HotlinkingAllowed";

  /**
   * Default color for thumbnail borders.
   */
  public static final java.awt.Color DEFAULT_BORDER_COLOR =
      new java.awt.Color(216, 206, 203);

  /**
   * Value used to disable page caching.
   */
  public static final int NO_CACHE = 0;

  /**
   * Value used to cache pages in memory.
   */
  public static final int IN_MEMORY_CACHE = 1;

  /**
   * Value used to cache pages on disk.
   */
  public static final int ON_DISK_CACHE = 2;
  
  public static final String BLOCK_CACHE_ATTR = "NoCachePlease";
  
  public static final String HELP_ANCHOR_CONFIGURE = "configure";
  public static final String HELP_ANCHOR_CONTROL_PANEL = "control_panel";
  public static final String HELP_ANCHOR_EDIT_PAGE = "edit_page";
  public static final String HELP_ANCHOR_EDIT_PROFILE = "edit_profile";
  public static final String HELP_ANCHOR_FILE_MANAGER = "file_manager";
  public static final String HELP_ANCHOR_MANAGE_PAGES = "manage_pages";
  public static final String HELP_ANCHOR_NEW_PAGE = "create_new_page";
  public static final String HELP_ANCHOR_NEW_USER = "new_user";
  public static final String HELP_ANCHOR_UNZIP = "unzip";
  public static final String HELP_ANCHOR_UPLOAD = "upload";
}
