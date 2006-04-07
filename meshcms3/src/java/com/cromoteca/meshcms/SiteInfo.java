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
 * Contains data about site menu customization and theme mappings.
 */
public class SiteInfo implements Finals {
  /**
   * Prefix of the title codes.
   */
  public static final String TITLE = "title";

  /**
   * Prefix of the score codes.
   */
  public static final String SCORE = "score";

  /**
   * Prefix of the theme codes.
   */
  public static final String THEME = "theme";

  private Properties data;
  private WebApp webApp;

  /**
   * Creates an instance of this class for the given WebApp.
   */
  public SiteInfo(WebApp webApp) {
    this.webApp = webApp;
    

    if (!load()) {
      data = new Properties();
    }
  }

  /**
   * Loads configuration from the config file (if found).
   *
   * @return true if the configuration has been loaded, false otherwise
   */
  public boolean load() {
    File propsFile = webApp.getFile(PROPS_PATH);

    if (propsFile.exists()) {
      InputStream is = null;

      try {
        data = new Properties();
        is = new BufferedInputStream(new FileInputStream(propsFile));
        data.load(is);
        return true;
      } catch (IOException ex) {
        webApp.log("Can't load menu properties file", ex);
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (IOException ex) {
            webApp.log("Can't close menu properties file", ex);
          }
        }
      }
    }

    return false;
  }

  /**
   * Saves the configuration to file.
   *
   * @return true if the configuration has been saved, false otherwise
   */
  public boolean save() {
    File propsFile = webApp.getFile(PROPS_PATH);
    OutputStream os = null;

    try {
      os = new BufferedOutputStream(new FileOutputStream(propsFile));
      data.store(os, "Custom Menu Values");
      return true;
    } catch (IOException ex) {
      webApp.log("Can't save menu properties file", ex);
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException ex) {
          webApp.log("Can't close menu properties file", ex);
        }
      }
    }

    return false;
  }
  
  /**
   * Returns the theme to be applied to the given path.
   */
  public String getPageTheme(Path pagePath) {
    return Utils.noNull(data.getProperty(getThemeCode(pagePath)));
  }
  
  /**
   * Sets the theme to be applied to the given path. If the value is null or
   * empty, the theme is removed.
   */
  public void setPageTheme(Path pagePath, String theme) {
    if (Utils.isNullOrEmpty(theme)) {
      data.remove(getThemeCode(pagePath));
    } else {
      data.setProperty(getThemeCode(pagePath), theme);
    }
  }

  /**
   * Returns the menu title for a page. If the menu configuration does not
   * contain a value for this page, the page title itself is returned.
   */
  public String getPageTitle(PageInfo pageInfo) {
    String customTitle = getPageTitle(pageInfo.getPath());

    if (Utils.isNullOrEmpty(customTitle)) {
      customTitle = pageInfo.getTitle();
    }

    return customTitle;
  }

  /**
   * Returns the menu title for the given path (null if not available).
   */
  public String getPageTitle(Path pagePath) {
    return Utils.noNull(data.getProperty(getTitleCode(pagePath)));
  }

  /**
   * Sets the menu title for the given path. If the value is null or empty,
   * the title is removed.
   */
  public void setPageTitle(Path pagePath, String title) {
    if (Utils.isNullOrEmpty(title)) {
      data.remove(getTitleCode(pagePath));
    } else {
      data.setProperty(getTitleCode(pagePath), title);
    }
  }

  /**
   * Returns the page score for the given path (0 if not available).
   */
  public int getPageScore(Path pagePath) {
    return Utils.parseInt(data.getProperty(getScoreCode(pagePath)), 0);
  }

  /**
   * Returns the page score as a string for the given path. An empty string
   * is returned if the page score is 0.
   */
  public String getPageScoreAsString(Path pagePath) {
    int score = getPageScore(pagePath);
    return score == 0 ? "" : Integer.toString(score);
  }

  /**
   * Sets the page score for the given path.
   */
  public void setPageScore(Path pagePath, String score) {
    setPageScore(pagePath, Utils.parseInt(score, 0));
  }

  /**
   * Sets the page score for the given path. If the score is 0, it is removed
   * since 0 is the default.
   */
  public void setPageScore(Path pagePath, int score) {
    setPageScore(getScoreCode(pagePath), score);
  }

  private void setPageScore(String pageCode, int score) {
    if (score == 0) {
      data.remove(pageCode);
    } else {
      data.setProperty(pageCode, Integer.toString(score));
    }
  }

  /**
   * Returns the code for the score field of the given path. This code is
   * used in the HTML configuration form and in the config file.
   */
  public String getScoreCode(Path pagePath) {
    return SCORE + WebUtils.getMenuCode(pagePath);
  }

  /**
   * Returns the code for the title field of the given path. This code is
   * used in the HTML configuration form and in the config file.
   */
  public String getTitleCode(Path pagePath) {
    return TITLE + WebUtils.getMenuCode(pagePath);
  }

  /**
   * Returns the code for the theme field of the given path. This code is
   * used in the HTML configuration form and in the config file.
   */
  public String getThemeCode(Path pagePath) {
    return THEME + WebUtils.getMenuCode(pagePath);
  }

  /**
   * Sets a generic value. The <code>fieldName</code> parameter is analyzed to
   * guess what value is going to be set. This is used to save all form fields.
   *
   * @return true if the fieldName has been recognized as a valid one, false
   * otherwise
   */
  public boolean setValue(String fieldName, String value) {
    if (fieldName == null) {
      return false;
    }

    if (value != null) {
      value = value.trim();
    }

    if (fieldName.startsWith(TITLE) || fieldName.startsWith(THEME)) {
      if (Utils.isNullOrEmpty(value)) {
        data.remove(fieldName);
      } else {
        data.setProperty(fieldName, value);
      }

      return true;
    }

    if (fieldName.startsWith(SCORE)) {
      int n = Utils.parseInt(value, 0);

      if (n == 0) {
        data.remove(fieldName);
      } else {
        data.setProperty(fieldName, value);
      }

      return true;
    }

    return false;
  }

  /**
   * Returns the path of the theme to be applied to the given path. This depends
   * on the stored values and on the option to use the default theme for the
   * admin pages. This method returns null if no theme is found.
   */
  public Path getThemePath(Path pagePath) {
    Path themePath = findThemePath(pagePath);
    
    if (pagePath.isContainedIn(webApp.getAdminPath())) {
      if (webApp.getConfiguration().isUseAdminTheme() ||
          themePath == null ||
          !webApp.getFile(themePath.add(THEME_DECORATOR)).exists()) {
        themePath = webApp.getAdminPath().add(WebApp.ADMIN_THEME);
      }
    }
    
    return themePath;
  }
  
  private Path findThemePath(Path pagePath) {
    do {
      String theme = getPageTheme(pagePath);
      
      if (!Utils.isNullOrEmpty(theme)) {
        return new Path(webApp.getConfiguration().getThemesDir(), theme);
      }
      
      pagePath = pagePath.getParent();
    } while (!pagePath.isRelative());
    
    return null;
  }
  
  public Path getThemeRoot(Path pagePath) {
    do {
      String theme = getPageTheme(pagePath);
      
      if (!Utils.isNullOrEmpty(theme)) {
        return pagePath;
      }
      
      pagePath = pagePath.getParent();
    } while (!pagePath.isRelative());
    
    return null;
  }
}
