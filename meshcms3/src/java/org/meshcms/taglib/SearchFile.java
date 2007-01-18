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

package org.meshcms.taglib;

import java.io.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Writes the path of the theme folder. Often used to access files included in
 * that folder.
 */
public final class SearchFile extends AbstractTag {
  private String name;
  private String defaultName;
  private String include;

  public void writeTag() throws IOException, JspException {
    if (!Utils.isNullOrEmpty(name)) {
      Path found = null;
      Path currentPath = webSite.getDirectory(pagePath);

      while (found == null && !currentPath.isRelative()) {
        Path p = currentPath.add(name);
        currentPath = currentPath.getParent();

        if (webSite.getFile(p).exists()) {
          found = p;
        }
      }

      if (found == null && defaultName != null) {
        Path themePath = (Path) request.getAttribute(HitFilter.THEME_PATH_ATTRIBUTE);

        if (themePath != null) {
          themePath = themePath.add(defaultName);

          if (webSite.getFile(themePath).exists()) {
            found = themePath;
          }
        }
      }

      if (found != null) {
        if (Utils.isTrue(include)) {
          File f = webSite.getFile(found);
          
          try {
            pageContext.include("/" + webSite.getServedPath(webSite.getPath(f)));
            WebUtils.updateLastModifiedTime(request, f);
          } catch (ServletException ex) {
            throw new JspException(ex);
          }
        } else {
          getOut().write(cp + '/' + found);
        }
      }
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDefaultName() {
    return defaultName;
  }

  public void setDefaultName(String defaultName) {
    this.defaultName = defaultName;
  }

  public String getInclude() {
    return include;
  }

  public void setInclude(String include) {
    this.include = include;
  }
}
