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
 * Manages the configuration parameters of MeshCMS.
 */
public class Configuration implements Finals, Serializable {
  private Properties internalData;
  
  static final String ADMIN_THEME_PROPERTY = "UseAdminTheme";
  static final String HOTLINKING_PROPERTY = "PreventHotlinking";
  static final String BACKUP_LIFE_PROPERTY = "BackupLife";
  static final String STATS_LENGTH_PROPERTY = "StatsLength";
  static final String UPDATE_INTERVAL_PROPERTY = "UpdateInterval";
  static final String CACHE_TYPE_PROPERTY = "CacheType";
  static final String MAIL_SERVER_PROPERTY = "MailServer";
  static final String SMTP_USERNAME_PROPERTY = "SmtpUsername";
  static final String SMTP_PASSWORD_PROPERTY = "SmtpPassword";
  static final String VISUAL_TYPES_PROPERTY = "VisualTypes";
  static final String THEMES_PROPERTY = "ThemesFolder";
  static final String MODULE_TEMPLATES_PROPERTY = "ModuleTemplatesFolder";
  static final String THUMBNAILS_PROPERTY = "ThumbnailsFolder";
  static final String USE_ENTITIES_PROPERTY = "UseEntities";
  static final String PREFERRED_CHARSET = "PreferredCharset";

  public Configuration() {
    internalData = new Properties();
  }
  
  /**
   * Returns the minimum time before deleting a backup file,
   * measured in days.
   */
  public int getBackupLife() {
    return getProperty(BACKUP_LIFE_PROPERTY, 30);
  }
  
  /**
   * Returns the minimum time before deleting a backup file,
   * measured in milliseconds.
   */
  public long getBackupLifeMillis() {
    return getBackupLife() * LENGTH_OF_DAY;
  }

  /**
   * Sets the minimum time before deleting a backup file,
   * measured in days.
   */
  public void setBackupLife(int backupLife) {
    setProperty(BACKUP_LIFE_PROPERTY, backupLife);
  }

  /**
   * Returns the path that contains custom module templates.
   */
  public String getModuleTemplatesDir() {
    return getProperty(MODULE_TEMPLATES_PROPERTY, '/' + MODULE_TEMPLATES_DIR);
  }

  /**
   * Sets the path that contains custom module templates.
   */
  public void setModuleTemplatesDir(String moduleTemplatesDir) {
    if (moduleTemplatesDir != null) {
      setProperty(MODULE_TEMPLATES_PROPERTY,
          Utils.addAtBeginning(moduleTemplatesDir, "/"));
    }
  }

  /**
   * Returns the path that contains image thumbnails.
   */
  public String getThumbnailsDir() {
    return getProperty(THUMBNAILS_PROPERTY, '/' + THUMBNAILS_DIR);
  }

  /**
   * Sets the path that contains image thumbnails.
   */
  public void setThumbnailsDir(String thumbnailsDir) {
    if (thumbnailsDir != null) {
      setProperty(THUMBNAILS_PROPERTY, Utils.addAtBeginning(thumbnailsDir, "/"));
    }
  }

  /**
   * Returns the preferred charset.
   */
  public String getPreferredCharset() {
    return getProperty(PREFERRED_CHARSET, "UTF-8");
  }

  /**
   * Sets the preferred charset, that will be used as often as possible.
   */
  public void setPreferredCharset(String preferredCharset) {
    setProperty(PREFERRED_CHARSET, WebUtils.getCharsetCanonicalName(preferredCharset));
  }
  
  /**
   * Returns the name of the mail server (SMTP).
   */
  public String getMailServer() {
    return getProperty(MAIL_SERVER_PROPERTY, "localhost");
  }

  /**
   * Sets the name of the mail server (SMTP).
   */
  public void setMailServer(String mailServer) {
    setProperty(MAIL_SERVER_PROPERTY, mailServer);
  }

  /**
   * Returns the SMTP username.
   */
  public String getSmtpUsername() {
    return getProperty(SMTP_USERNAME_PROPERTY, "");
  }

  /**
   * Sets the SMTP username.
   */
  public void setSmtpUsername(String smtpUsername) {
    setProperty(SMTP_USERNAME_PROPERTY, smtpUsername);
  }

  /**
   * Returns the SMTP password.
   */
  public String getSmtpPassword() {
    return getProperty(SMTP_PASSWORD_PROPERTY, "");
  }

  /**
   * Sets the SMTP password.
   */
  public void setSmtpPassword(String smtpPassword) {
    setProperty(SMTP_PASSWORD_PROPERTY, smtpPassword);
  }

  /**
   * Returns the length of stats (hit counts) measured in days.
   */
  public int getStatsLength() {
    return getProperty(STATS_LENGTH_PROPERTY, 3);
  }

  /**
   * Sets the length of stats (hit counts) measured in days. Please note that
   * this value is fixed when the web application is initialized, so if the
   * value is changed, the new value won't be used until the next restart of the
   * web application.
   */
  public void setStatsLength(int statsLength) {
    setProperty(STATS_LENGTH_PROPERTY, statsLength);
  }

  /**
   * Returns the minimum interval between two updates of the site map,
   * measured in hours.
   */
  public int getUpdateInterval() {
    return getProperty(UPDATE_INTERVAL_PROPERTY, 12);
  }
  
  /**
   * Returns the minimum interval between two updates of the site map,
   * measured in milliseconds.
   */
  public long getUpdateIntervalMillis() {
    return getUpdateInterval() * LENGTH_OF_HOUR;
  }

  /**
   * Sets the minimum interval between two updates of the site map,
   * measured in hours.
   */
  public void setUpdateInterval(int updateInterval) {
    setProperty(UPDATE_INTERVAL_PROPERTY, updateInterval);
  }

  /**
   * Returns the extensions that denote file types that can be edited
   * using the wysiwyg editor (e.g. &quot;html,htm&quot;).
   */
  public String getVisualTypes() {
    return getProperty(VISUAL_TYPES_PROPERTY, "html,htm");
  }

  /**
   * Sets the extensions that denote file types that can be edited
   * using the wysiwyg editor (e.g. &quot;html,htm&quot;).
   */
  public void setVisualTypes(String visualTypes) {
    setProperty(VISUAL_TYPES_PROPERTY, visualTypes);
  }

  /**
   * Returns an array of extensions that denote file types that can be edited
   * using the wysiwyg editor.
   */
  public String[] getVisualExtensions() {
    return Utils.tokenize(getVisualTypes(), " .,:;");
  }

  /**
   * Returns the path of the directory that contains the themes.
   */
  public String getThemesDir() {
    return getProperty(THEMES_PROPERTY, "/themes");
  }

  /**
   * Sets the path of the directory that contains the themes.
   */
  public void setThemesDir(String themesDir) {
    if (themesDir != null) {
      setProperty(THEMES_PROPERTY, Utils.addAtBeginning(themesDir, "/"));
    }
  }

  /**
   * Returns true if the default MeshCMS theme is always used for the pages
   * of the control panel.
   */
  public boolean isUseAdminTheme() {
    return getProperty(ADMIN_THEME_PROPERTY, true);
  }

  /**
   * Sets if the default MeshCMS theme is always used for the pages
   * of the control panel.
   */
  public void setUseAdminTheme(boolean useAdminTheme) {
    setProperty(ADMIN_THEME_PROPERTY, useAdminTheme);
  }

  /**
   * Loads the configuration from file or creates a new configuration with
   * default values if the file doesn't exist.
   */
  public boolean load(WebApp webApp) {
    File configFile = webApp.getFile(CONFIG_PATH);
    
    if (configFile.exists()) {
      InputStream is = null;
      
      try {
        is = new BufferedInputStream(new FileInputStream(configFile));
        internalData.load(is);
        return true;
      } catch (IOException ex) {
        ex.printStackTrace();
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    }
    
    return false;
  }
  
  /**
   * Saves the current configuration to file.
   */
  public boolean store(WebApp webApp) {
    OutputStream os = null;
    
    try {
      os = new BufferedOutputStream
          (new FileOutputStream(webApp.getFile(CONFIG_PATH)));
      internalData.store(os, "Configuration for MeshCMS");
      return true;
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    }
    
    return false;
  }

  /**
   * Returns the type of cache to be used for pages.
   *
   * @see #setCacheType
   */
  public int getCacheType() {
    return getProperty(CACHE_TYPE_PROPERTY, NO_CACHE);
  }

  /**
   * Sets the type of cache to be used for pages. Possible values are defined in
   * {@link Finals} and are {@link Finals#NO_CACHE},
   * {@link Finals#IN_MEMORY_CACHE} and {@link Finals#ON_DISK_CACHE}.
   */
  public void setCacheType(int cacheType) {
    setProperty(CACHE_TYPE_PROPERTY, cacheType);
  }

  /**
   * Returns true if the option to prevent hotlinking is enabled.
   */
  public boolean isPreventHotlinking() {
    return getProperty(HOTLINKING_PROPERTY, false);
  }

  /**
   * Enables or disables hotlinking prevention.
   */
  public void setPreventHotlinking(boolean preventHotlinking) {
    setProperty(HOTLINKING_PROPERTY, preventHotlinking);
  }
  
  /**
   * Returns true if the option to use HTML entities in the menu titles is enabled.
   */
  public boolean isUseEntities() {
    return getProperty(USE_ENTITIES_PROPERTY, true);
  }

  /**
   * Enables or disables conversion to HTML for page titles in the site map.
   */
  public void setUseEntities(boolean useEntities) {
    setProperty(USE_ENTITIES_PROPERTY, useEntities);
  }
  
  private boolean getProperty(String name, boolean defaultValue) {
    String value = internalData.getProperty(name);
    return (value == null) ? defaultValue : Utils.isTrue(value);
  }
  
  private void setProperty(String name, boolean value) {
    internalData.setProperty(name, value ? "yes" : "no");
  }
  
  private int getProperty(String name, int defaultValue) {
    return Utils.parseInt(internalData.getProperty(name), defaultValue);
  }
  
  private void setProperty(String name, int value) {
    internalData.setProperty(name, Integer.toString(value));
  }
  
  private String getProperty(String name, String defaultValue) {
    return internalData.getProperty(name, defaultValue);
  }
  
  private void setProperty(String name, String value) {
    internalData.setProperty(name, Utils.noNull(value).trim());
  }
}
