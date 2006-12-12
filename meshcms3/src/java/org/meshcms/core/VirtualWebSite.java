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

import java.io.File;
import javax.servlet.*;
import javax.servlet.http.*;
import org.meshcms.util.*;

public class VirtualWebSite extends WebSite {
  private MainWebSite mainWebSite;
  
  protected static WebSite create(ServletContext sc,
      String[] welcomeFiles, File rootFile, Path rootPath, Path cmsPath) {
    throw new UnsupportedOperationException
        ("You should use create(MainWebSite, Path, Path) instead");
  }

  protected static VirtualWebSite create(MainWebSite mainWebSite, Path rootPath,
      Path cmsPath) {
    VirtualWebSite virtualWebSite = new VirtualWebSite();
    virtualWebSite.init(mainWebSite, rootPath, cmsPath);
    return virtualWebSite;
  }
  
  protected void init(MainWebSite mainWebSite, Path rootPath, Path cmsPath) {
    this.mainWebSite = mainWebSite;
    init(mainWebSite.getServletContext(), mainWebSite.getWelcomeFileNames(),
        mainWebSite.getFile(rootPath), rootPath, cmsPath);
  }

  public WebSite getWebSite(ServletRequest request) {
    throw new UnsupportedOperationException("This is a virtual website");
  }

  public boolean isVirtual() {
    return true;
  }
  
  public HttpServletRequest wrapRequest(ServletRequest request) {
    return new MultiSiteRequestWrapper((HttpServletRequest) request, this);
  }

  public String getTypeDescription() {
    return "virtual web site (" + rootPath.getLastElement() + ')';
  }
  
  public Path getRequestedPath(HttpServletRequest request) {
    return ((MultiSiteRequestWrapper) request).getRequestedPath();
  }
  
  public Path getServedPath(HttpServletRequest request) {
    return ((MultiSiteRequestWrapper) request).getServedPath();
  }

  public Path getServedPath(Path requestedPath) {
    // a null adminPath is handled correctly
    return requestedPath.isContainedIn(adminPath) ?
        mainWebSite.getAdminPath().add(requestedPath.getRelativeTo(adminPath)) :
        rootPath.add(requestedPath);
  }
  
  public File getFile(Path path) {
    return mainWebSite.getFile(getServedPath(path));
  }

  public MainWebSite getMainWebSite() {
    return mainWebSite;
  }

  /* public String getLink(Path path) {
    return siteMap.getServedPath(path).getAsLink();
  } */

  public void updateSiteMap(boolean force) {
    if (cmsPath != null) {
      super.updateSiteMap(force);
    }
  }
}
