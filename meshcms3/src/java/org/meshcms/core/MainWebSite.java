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
import java.util.*;
import javax.servlet.*;
import org.meshcms.util.*;

public class MainWebSite extends WebSite {
  private SortedMap virtualSitesMap;
  private MultiSiteManager multiSiteManager;

  protected static WebSite create(ServletContext sc,
      String[] welcomeFiles, File rootFile, Path rootPath, Path cmsPath) {
    MainWebSite mainWebSite = new MainWebSite();
    mainWebSite.init(sc, welcomeFiles, rootFile, rootPath, cmsPath);
    return mainWebSite;
  }
  
  protected void init(ServletContext sc, String[] welcomeFiles, File rootFile,
      Path rootPath, Path cmsPath) {
    super.init(sc, welcomeFiles, rootFile, rootPath, cmsPath);
    
    if (virtualSitesMap == null) {
      virtualSitesMap = new TreeMap();
    }
    
    if (multiSiteManager == null) {
      multiSiteManager = MultiSiteManager.load(this);
    }
    
    multiSiteManager.initDomainsMap();
  }

  public WebSite getWebSite(ServletRequest request) {
    return multiSiteManager.getWebSite(request.getServerName());
  }

  public String getName() {
    return "main web site";
  }
  
  public VirtualWebSite getVirtualSite(String dirName) {
    VirtualWebSite vws = (VirtualWebSite) virtualSitesMap.get(dirName);
    
    if (vws == null) {
      Path sitePath = virtualSitesPath.add(dirName);
      File rootFile = getFile(sitePath);
      Path cmsPath = new CMSDirectoryFinder(rootFile).getCMSPath();
      vws = VirtualWebSite.create(this, sitePath, cmsPath);
      virtualSitesMap.put(dirName, vws);
    }
    
    return vws;
  }

  public MultiSiteManager getMultiSiteManager() {
    return multiSiteManager;
  }

  public void updateSiteMap(boolean force) {
    super.updateSiteMap(force);
    multiSiteManager.initDomainsMap();
  }
  
  public String getHost(String dirName) {
    WebSite site = multiSiteManager.getWebSite(dirName);
    
    if (site != null) {
      String host = site.getConfiguration().getSiteHost();
      
      if (!Utils.isNullOrEmpty(host) &&
          multiSiteManager.getWebSite(host).equals(site)) {
        return host;
      }
      
      if (multiSiteManager.isUseDirsAsDomains()) {
        return dirName;
      }
      
      StringTokenizer st =
          new StringTokenizer(multiSiteManager.getDomains(dirName), ";:, \t");
      
      if (st.hasMoreTokens()) {
        return st.nextToken();
      }
    }
    
    return null;
  }
}
