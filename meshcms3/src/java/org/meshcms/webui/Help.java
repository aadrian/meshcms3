/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2007 Luciano Vernaschi
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

package org.meshcms.webui;

import java.util.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 *
 */
public class Help {
  public static final String CONFIGURE = "configure";
  public static final String CONTROL_PANEL = "control_panel";
  public static final String EDIT_PAGE = "edit_page";
  public static final String EDIT_PROFILE = "edit_profile";
  public static final String FILE_MANAGER = "file_manager";
  public static final String PAGE_MANAGER = "page_manager";
  public static final String NEW_PAGE = "create_new_page";
  public static final String NEW_USER = "new_user";
  public static final String SITE_MANAGER = "site_manager";
  public static final String STATIC_EXPORT = "static_export";
  public static final String UNZIP = "unzip";
  public static final String UPLOAD = "upload";
  public static final String MODULES = "modules";

  private static Properties args;

  static {
    args = new Properties();
    args.setProperty(EDIT_PAGE, "ch04s02.html");
    args.setProperty(NEW_PAGE, "ch04s03.html");
    args.setProperty(CONTROL_PANEL, "ch05.html");
    args.setProperty(PAGE_MANAGER, "ch05s01.html");
    args.setProperty(CONFIGURE, "ch05s02.html");
    args.setProperty(EDIT_PROFILE, "ch05s03.html");
    args.setProperty(NEW_USER, "ch05s04.html");
    args.setProperty(FILE_MANAGER, "ch05s05.html");
    args.setProperty(UPLOAD, "ch05s05.html#upload");
    args.setProperty(UNZIP, "ch05s05.html#unzip");
    args.setProperty(STATIC_EXPORT, "ch05s06.html");
    args.setProperty(SITE_MANAGER, "ch05s07.html");
    args.setProperty(MODULES, "ch06s01.html");
  }

  /**
   * Creates the HTML used to display the help icon in the admin pages.
   */
  public static String icon(WebSite webSite, String contextPath,
      String argument, UserInfo userInfo) {
    return icon(webSite, contextPath, argument, userInfo, null, false);
  }

  public static String icon(WebSite webSite, String contextPath,
      String argument, UserInfo userInfo, String anchor, boolean grayIcon) {
    String lang = getHelpLang(webSite, userInfo);

    return "<img src='" + contextPath + '/' + webSite.getAdminPath() +
        "/images/" + (grayIcon ? "small_help_gray.gif" : "small_help.gif") +
        "' title='Help: " + argument +
        "' alt='Help Icon' onclick=\"javascript:window.open('" +
        contextPath + '/' + webSite.getAdminPath() + "/help/" + lang +
        '/' + args.getProperty(argument, "index.html") +
        (Utils.isNullOrEmpty(anchor) ? "" : "#" + anchor) +
        "', 'meshcmshelp', 'width=740,height=560,menubar=no,status=yes,toolbar=no,resizable=yes,scrollbars=yes').focus();\" />";
  }

  public static String getHelpLang(WebSite webSite, UserInfo userInfo) {
    String lang = "en";

    if (userInfo != null) {
      String otherLang = userInfo.getPreferredLocaleCode();

      if (webSite.getFile(webSite.getAdminPath().add("help", otherLang)).exists()) {
        lang = otherLang;
      }
    }

    return lang;
  }
}
