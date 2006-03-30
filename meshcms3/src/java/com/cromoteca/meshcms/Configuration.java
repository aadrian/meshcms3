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
import com.cromoteca.util.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.*;

/**
 * Manages the configuration parameters of MeshCMS.
 */
public class Configuration implements Finals, Serializable {
  private Path cmsPath;
  
  private boolean useAdminTheme;
  private boolean preventHotlinking;
  private boolean useEntities;
  
  private int backupLife;
  private int statsLength;
  private int updateInterval;
  private int cacheType;
  
  private String mailServer;
  private String mailUsername;
  private String mailPassword;
  private String preferredCharset;

  private String[] visualTypes;
  
  public static final Path defaultCmsPath = new Path("/meshcms");
  public static final String[] defaultVisualTypes = {"html", "htm"};
  
  public Configuration() {
    setCmsPath(defaultCmsPath);
    
    setUseAdminTheme(true);
    setPreventHotlinking(false);
    setUseEntities(false);
    
    setBackupLife(30);
    setStatsLength(3);
    setUpdateInterval(4);
    setCacheType(NO_CACHE);
    
    setMailServer("localhost");
    setMailUsername("");
    setMailPassword("");
    setPreferredCharset("UTF-8");
    
    setVisualTypes(defaultVisualTypes);
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

  public boolean isUseEntities() {
    return useEntities;
  }

  public void setUseEntities(boolean useEntities) {
    this.useEntities = useEntities;
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

  public String getMailUsername() {
    return mailUsername;
  }

  public void setMailUsername(String mailUsername) {
    this.mailUsername = mailUsername;
  }

  public String getMailPassword() {
    return mailPassword;
  }

  public void setMailPassword(String mailPassword) {
    this.mailPassword = mailPassword;
  }

  public String getPreferredCharset() {
    return preferredCharset;
  }

  public void setPreferredCharset(String preferredCharset) {
    this.preferredCharset = preferredCharset;
  }

  public String[] getVisualTypes() {
    return visualTypes;
  }

  public void setVisualTypes(String[] visualTypes) {
    this.visualTypes = visualTypes;
  }

  /**
   * Loads the configuration from file or creates a new configuration with
   * default values if the file doesn't exist.
   */
  public static Configuration load(WebSite webSite) {
    File configFile = webSite.getFile(CONFIG_PATH);
    
    if (configFile.exists()) {
      InputStream is = null;
      
      try {
        is = new BufferedInputStream(new FileInputStream(configFile));
        XStream xStream = new XStream(new DomDriver());
        XStreamPathConverter pConv = new XStreamPathConverter();
        pConv.setPrependSlash(true);
        xStream.registerConverter(pConv);
        return (Configuration) xStream.fromXML(is);
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

    Configuration configuration = new Configuration();
    configuration.setPreferredCharset(webSite.getWebApp().systemCharset);
    return configuration;
  }
  
  /**
   * Saves the current configuration to file.
   */
  public boolean store(WebSite webSite) {
    OutputStream os = null;
    
    try {
      os = new BufferedOutputStream
          (new FileOutputStream(webSite.getFile(CONFIG_PATH)));
      XStream xStream = new XStream(new DomDriver());
      XStreamPathConverter pConv = new XStreamPathConverter();
      pConv.setPrependSlash(true);
      xStream.registerConverter(pConv);
      xStream.toXML(this, os);
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

  public Path getCmsPath() {
    return cmsPath;
  }

  public void setCmsPath(Path cmsPath) {
    this.cmsPath = cmsPath;
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
}
