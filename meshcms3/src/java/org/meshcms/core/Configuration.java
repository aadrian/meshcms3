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

import java.io.*;

/**
 * Manages the configuration parameters of MeshCMS.
 */
public class Configuration implements Serializable {
  /**
   * The length of a hour in milliseconds.
   */
  public static final long LENGTH_OF_HOUR = 60 * 60 * 1000;

  /**
   * The length of a day in milliseconds.
   */
  public static final long LENGTH_OF_DAY = 24 * LENGTH_OF_HOUR;

  public static final String[] DEFAULT_VISUAL_EXTENSIONS = {"html", "htm"};
  
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

  private boolean useAdminTheme;
  private boolean preventHotlinking;
  private boolean alwaysRedirectWelcomes;
  private int backupLife;
  private int statsLength;
  private int updateInterval;
  private int cacheType;
  private String mailServer;
  private String smtpUsername;
  private String smtpPassword;
  private String preferredCharset;
  private String[] visualExtensions;
  
  private Configuration() {
    setUseAdminTheme(true);
    setPreventHotlinking(false);
    setAlwaysRedirectWelcomes(true);
    
    setBackupLife(90);
    setStatsLength(3);
    setUpdateInterval(4);
    setCacheType(NO_CACHE);
    
    setMailServer("localhost");
    setSmtpUsername("");
    setSmtpPassword("");
    setPreferredCharset("UTF-8");
    
    setVisualExtensions(DEFAULT_VISUAL_EXTENSIONS);
  }

  public boolean isUseAdminTheme() {
    return useAdminTheme;
  }

  public void setUseAdminTheme(boolean useAdminTheme) {
    this.useAdminTheme = useAdminTheme;
  }

  public boolean isPreventHotlinking() {
    return preventHotlinking;
  }

  public void setPreventHotlinking(boolean preventHotlinking) {
    this.preventHotlinking = preventHotlinking;
  }

  public int getBackupLife() {
    return backupLife;
  }

  public void setBackupLife(int backupLife) {
    this.backupLife = backupLife;
  }

  public int getStatsLength() {
    return statsLength;
  }

  public void setStatsLength(int statsLength) {
    this.statsLength = statsLength;
  }

  public int getUpdateInterval() {
    return updateInterval;
  }

  public void setUpdateInterval(int updateInterval) {
    this.updateInterval = updateInterval;
  }

  public int getCacheType() {
    return cacheType;
  }

  public void setCacheType(int cacheType) {
    this.cacheType = cacheType;
  }

  public String getMailServer() {
    return mailServer;
  }

  public void setMailServer(String mailServer) {
    this.mailServer = mailServer;
  }

  public String getSmtpUsername() {
    return smtpUsername;
  }

  public void setSmtpUsername(String smtpUsername) {
    this.smtpUsername = smtpUsername;
  }

  public String getSmtpPassword() {
    return smtpPassword;
  }

  public void setSmtpPassword(String smtpPassword) {
    this.smtpPassword = smtpPassword;
  }

  public String getPreferredCharset() {
    return preferredCharset;
  }

  public void setPreferredCharset(String preferredCharset) {
    this.preferredCharset = preferredCharset;
  }

  /**
   * Loads the configuration from file or creates a new configuration with
   * default values if the file doesn't exist.
   */
  public static Configuration load(WebSite webSite) {
    Configuration c = null;
    
    try {
      c = (Configuration) webSite.loadFromXML(webSite.getConfigFilePath());
    } catch (Exception ex) {}
    
    if (c == null) {
      c = new Configuration();
      c.setPreferredCharset(WebSite.SYSTEM_CHARSET);
    }
    
    return c;
  }
  
  /**
   * Saves the current configuration to file.
   */
  public boolean store(WebSite webSite) {
    return webSite.storeToXML(this, webSite.getConfigFilePath());
  }

  /**
   * Returns the minimum interval between two updates of the site map,
   * measured in milliseconds.
   */
  public long getUpdateIntervalMillis() {
    return getUpdateInterval() * LENGTH_OF_HOUR;
  }

  /**
   * Returns the minimum time before deleting a backup file,
   * measured in milliseconds.
   */
  public long getBackupLifeMillis() {
    return getBackupLife() * LENGTH_OF_DAY;
  }

  public String[] getVisualExtensions() {
    return visualExtensions;
  }

  public void setVisualExtensions(String[] visualExtensions) {
    this.visualExtensions = visualExtensions;
  }

  public boolean isAlwaysRedirectWelcomes() {
    return alwaysRedirectWelcomes;
  }

  public void setAlwaysRedirectWelcomes(boolean alwaysRedirectWelcomes) {
    this.alwaysRedirectWelcomes = alwaysRedirectWelcomes;
  }
}
