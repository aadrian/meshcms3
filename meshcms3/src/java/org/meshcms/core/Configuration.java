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
 * Manages the configuration parameters of a website.
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

  /**
   * Contains the extensions of files that are visually editable by default.
   */
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
  private boolean alwaysDenyDirectoryListings;
  private boolean hideExceptions;
  private boolean editorModulesCollapsed;
  private boolean highQualityThumbnails;
  private boolean exportCheckDates;
  private int backupLife;
  private int statsLength;
  private int updateInterval;
  private int cacheType;
  private String mailServer;
  private String smtpUsername;
  private String smtpPassword;
  private String siteName;
  private String siteHost;
  private String siteDescription;
  private String siteKeywords;
  private String siteAuthor;
  private String siteAuthorURL;
  private String exportBaseURL;
  private String exportDir;
  private String exportCommand;
  private String[] visualExtensions;
  
  private Configuration() {
    setUseAdminTheme(false);
    setPreventHotlinking(false);
    setAlwaysRedirectWelcomes(true);
    setAlwaysDenyDirectoryListings(true);
    setHideExceptions(true);
    setEditorModulesCollapsed(false);
    setHighQualityThumbnails(false);
    setExportCheckDates(true);
    
    setBackupLife(90);
    setStatsLength(3);
    setUpdateInterval(4);
    setCacheType(ON_DISK_CACHE);
    
    setMailServer("localhost");
    setSmtpUsername("");
    setSmtpPassword("");
    setSiteAuthor("Luciano Vernaschi");
    setSiteAuthorURL("http://www.cromoteca.com/");
    setSiteDescription("MeshCMS is a Content Management System designed to be" +
        " easy to use. It doesn't require a database and offers easy" +
        " deployment, management of multiple sites, wysiwyg editing, file" +
        " manager, themes, modules and a custom tag library.");
    setSiteHost("www.meshcms.org");
    setSiteKeywords("meshcms, cms, java, jsp, servlet, content management system");
    setSiteName("MeshCMS - Open Source Content Management System");
    setExportBaseURL("");
    setExportDir("");
    setExportCommand("");

    setVisualExtensions(DEFAULT_VISUAL_EXTENSIONS);
  }

  /**
   * @return true if the default MeshCMS theme is always used for the pages
   * of the control panel.
   */
  public boolean isUseAdminTheme() {
    return useAdminTheme;
  }

  /**
   * Sets if the default MeshCMS theme is always used for the pages
   * of the control panel.
   */
  public void setUseAdminTheme(boolean useAdminTheme) {
    this.useAdminTheme = useAdminTheme;
  }

  /**
   * @return true if the option to prevent hotlinking is enabled.
   */
  public boolean isPreventHotlinking() {
    return preventHotlinking;
  }

  /**
   * Enables or disables hotlinking prevention.
   */
  public void setPreventHotlinking(boolean preventHotlinking) {
    this.preventHotlinking = preventHotlinking;
  }

  /**
   * @return the minimum time before deleting a backup file,
   * measured in days.
   */
  public int getBackupLife() {
    return backupLife;
  }

  /**
   * Sets the minimum time before deleting a backup file,
   * measured in days.
   */
  public void setBackupLife(int backupLife) {
    this.backupLife = Math.max(backupLife, 0);
  }

  /**
   * @return the length of stats (hit counts) measured in days.
   */
  public int getStatsLength() {
    return statsLength;
  }

  /**
   * Sets the length of stats (hit counts) measured in days. Please note that
   * this value is fixed when the web application is initialized, so if the
   * value is changed, the new value won't be used until the next restart of the
   * web application.
   */
  public void setStatsLength(int statsLength) {
    this.statsLength = Math.max(statsLength, 1);
  }

  /**
   * @return the minimum interval between two updates of the site map,
   * measured in hours.
   */
  public int getUpdateInterval() {
    return updateInterval;
  }

  /**
   * Sets the minimum interval between two updates of the site map,
   * measured in hours.
   */
  public void setUpdateInterval(int updateInterval) {
    this.updateInterval = Math.max(updateInterval, 1);
  }

  /**
   * @return the type of cache to be used for pages.
   *
   * @see #setCacheType
   */
  public int getCacheType() {
    return cacheType;
  }

  /**
   * Sets the type of cache to be used for pages. Possible values are
   * {@link #NO_CACHE}, {@link #IN_MEMORY_CACHE} and {@link #ON_DISK_CACHE}.
   */
  public void setCacheType(int cacheType) {
    this.cacheType = cacheType;
  }

  /**
   * @return the name of the mail server (SMTP).
   */
  public String getMailServer() {
    return mailServer;
  }

  /**
   * Sets the name of the mail server (SMTP).
   */
  public void setMailServer(String mailServer) {
    this.mailServer = mailServer;
  }

  /**
   * @return the SMTP username.
   */
  public String getSmtpUsername() {
    return smtpUsername;
  }

  /**
   * Sets the SMTP username.
   */
  public void setSmtpUsername(String smtpUsername) {
    this.smtpUsername = smtpUsername;
  }

  /**
   * @return the SMTP password.
   */
  public String getSmtpPassword() {
    return smtpPassword;
  }

  /**
   * Sets the SMTP password.
   */
  public void setSmtpPassword(String smtpPassword) {
    this.smtpPassword = smtpPassword;
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
   * @return the minimum interval between two updates of the site map,
   * measured in milliseconds.
   */
  public long getUpdateIntervalMillis() {
    return getUpdateInterval() * LENGTH_OF_HOUR;
  }

  /**
   * @return the minimum time before deleting a backup file,
   * measured in milliseconds.
   */
  public long getBackupLifeMillis() {
    return getBackupLife() * LENGTH_OF_DAY;
  }

  /**
   * @return the extensions that denote file types that can be edited
   * using the wysiwyg editor.
   */
  public String[] getVisualExtensions() {
    return visualExtensions;
  }

  /**
   * Sets the extensions that denote file types that can be edited
   * using the wysiwyg editor.
   */
  public void setVisualExtensions(String[] visualExtensions) {
    this.visualExtensions = visualExtensions;
  }

  /**
   * @return the state of the automatic redirection to welcome files.
   */
  public boolean isAlwaysRedirectWelcomes() {
    return alwaysRedirectWelcomes;
  }

  /**
   * Enables or disables automatic redirection to welcome files.
   */
  public void setAlwaysRedirectWelcomes(boolean alwaysRedirectWelcomes) {
    this.alwaysRedirectWelcomes = alwaysRedirectWelcomes;
  }

  /**
   * @return the state of directory list blocking.
   */
  public boolean isAlwaysDenyDirectoryListings() {
    return alwaysDenyDirectoryListings;
  }

  /**
   * Enables or disables blocking of directory listings.
   */
  public void setAlwaysDenyDirectoryListings(boolean alwaysDenyDirectoryListings) {
    this.alwaysDenyDirectoryListings = alwaysDenyDirectoryListings;
  }

  /**
   * @return the main host name of this website.
   */
  public String getSiteHost() {
    return siteHost;
  }

  /**
   * Sets the main host name of this website.
   */
  public void setSiteHost(String siteHost) {
    this.siteHost = siteHost;
  }

  /**
   * @return the website description.
   */
  public String getSiteDescription() {
    return siteDescription;
  }

  /**
   * Sets the website description.
   */
  public void setSiteDescription(String siteDescription) {
    this.siteDescription = siteDescription;
  }

  /**
   * @return the keywords related to the website.
   */
  public String getSiteKeywords() {
    return siteKeywords;
  }

  /**
   * Sets the keywords related to the website.
   */
  public void setSiteKeywords(String siteKeywords) {
    this.siteKeywords = siteKeywords;
  }

  /**
   * @return the author name.
   */
  public String getSiteAuthor() {
    return siteAuthor;
  }

  /**
   * Sets the author name.
   */
  public void setSiteAuthor(String siteAuthor) {
    this.siteAuthor = siteAuthor;
  }

  /**
   * @return the site name.
   */
  public String getSiteName() {
    return siteName;
  }

  /**
   * Sets the site name.
   */
  public void setSiteName(String siteName) {
    this.siteName = siteName;
  }

  /**
   * @return the author's URL.
   */
  public String getSiteAuthorURL() {
    return siteAuthorURL;
  }

  /**
   * Sets the author's URL. Can be a website URL or a mailto. It is expected
   * to be a full URL.
   */
  public void setSiteAuthorURL(String siteAuthorURL) {
    this.siteAuthorURL = siteAuthorURL;
  }

  /**
   * @return the state of exception hiding.
   */
  public boolean isHideExceptions() {
    return hideExceptions;
  }

  /**
   * Enables or disables hiding of Java exceptions. If enabled, exception will
   * be catched and not rethrown.
   */
  public void setHideExceptions(boolean hideExceptions) {
    this.hideExceptions = hideExceptions;
  }

  /**
   * @return the state of whether modules are collapsed in the editor. 
   */
  public boolean isEditorModulesCollapsed() {
    return editorModulesCollapsed;
  }

  /**
   * Sets whether modules are collapsed in the editor or not.
   */
  public void setEditorModulesCollapsed(boolean editorModulesCollapsed) {
    this.editorModulesCollapsed = editorModulesCollapsed;
  }

  public boolean isHighQualityThumbnails() {
    return highQualityThumbnails;
  }

  public void setHighQualityThumbnails(boolean highQualityThumbnails) {
    this.highQualityThumbnails = highQualityThumbnails;
  }

  public boolean isExportCheckDates() {
    return exportCheckDates;
  }

  public void setExportCheckDates(boolean exportCheckDates) {
    this.exportCheckDates = exportCheckDates;
  }

  public String getExportBaseURL() {
    return exportBaseURL;
  }

  public void setExportBaseURL(String exportBaseURL) {
    this.exportBaseURL = exportBaseURL;
  }

  public String getExportDir() {
    return exportDir;
  }

  public void setExportDir(String exportDir) {
    this.exportDir = exportDir;
  }

  public String getExportCommand() {
    return exportCommand;
  }

  public void setExportCommand(String exportCommand) {
    this.exportCommand = exportCommand;
  }
}
