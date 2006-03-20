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

package com.cromoteca.meshcms.taglib;

import java.io.*;
import java.util.*;
import com.cromoteca.meshcms.*;
import com.cromoteca.util.*;

/**
 * Creates a simple navigation menu.
 */
public final class SimpleMenu extends AbstractTag {
  private String path;
  private String space = "8";
  private String bullet = "&bull;";
  private String style;
  private String expand;

  private int baseLevel;

  public void setPath(String path) {
    this.path = path;
  }

  public void setBullet(String bullet) {
    if (bullet != null) {
      this.bullet = bullet;
    }
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public void writeTag() throws IOException {
    SiteMap siteMap = webApp.getSiteMap();
    Path rootPath = new Path(path);
    Path pathInMenu = webApp.getSiteMap().getPathInMenu(pagePath);
    baseLevel = rootPath.getElementCount() + 1;
    int spc = Utils.parseInt(space, 8);
    Iterator iter = siteMap.getPagesList(rootPath).iterator();
    
    Writer outWriter = getOut();

    while (iter.hasNext()) {
      PageInfo current = (PageInfo) iter.next();
      Path currentPath = current.getPath();
      Path parentPath = currentPath.getParent();

      if (parentPath.isRelative() || pathInMenu.isContainedIn(parentPath)) {
        if (Utils.isTrue(expand) ||
            pathInMenu.isContainedIn(currentPath) ||
            currentPath.getElementCount() == baseLevel ||
            currentPath.getElementCount() >= pathInMenu.getElementCount()) {
          outWriter.write("<div style=\"padding-left: " +
            (spc * Math.max(current.getLevel() - baseLevel, 0)) + "px;\">");

          if (style != null) {
            outWriter.write("<div class=\"" + style + "\">");
          }

          outWriter.write(bullet + "&nbsp;");

          if (!isEdit && current.getPath().equals(pathInMenu)) {
            outWriter.write(webApp.getSiteInfo().getPageTitle(current));
          } else {
            outWriter.write("<a href=\"" + cp + current.getLink() +
              "\">" + webApp.getSiteInfo().getPageTitle(current) + "</a>");
          }

          if (style != null) {
            outWriter.write("</div>");
          }

          outWriter.write("</div>\n");
        }
      }
    }
  }

  public String getPath() {
    return path;
  }

  public String getSpace() {
    return space;
  }

  public void setSpace(String space) {
    this.space = space;
  }

  public String getBullet() {
    return bullet;
  }

  public String getStyle() {
    return style;
  }

  public String getExpand() {
    return expand;
  }

  public void setExpand(String expand) {
    this.expand = expand;
  }
}
