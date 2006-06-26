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

package org.meshcms.taglib;

import java.io.*;
import java.util.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Creates a navigation menu, using a unnumbered list.
 */
public final class AlibMenu extends AbstractTag {
  public static final String PART_HEAD = "head";
  public static final String PART_BODY = "body";
  public static final String HORIZONTAL = "horizontal";
  public static final String VERTICAL = "vertical";
  public static final String TEXT = "text";
  public static final String LINK = "link";
  
  private String indentation = "  ";
  private String indentBuffer = "";

  private String orientation;
  private String part;
  private String path;
  private String current;
  private String currentPathStyle = "selected";

  public void writeTag() throws IOException {
    Writer outWriter = getOut();
    boolean horizontal = orientation != null && orientation.equals(HORIZONTAL);

    if (part != null && part.equals(PART_HEAD)) {
      String sp = request.getContextPath() +
          webSite.getAdminScriptsPath().getAsLink() + "/alib";
      String menuType = horizontal ? HORIZONTAL : VERTICAL;
      outWriter.write("<script src='" + sp +
          "/alib.common/script.js' type='text/javascript'></script>\n");
      outWriter.write("<script src='" + sp +
          "/menu." + menuType + "/script.js' type='text/javascript'></script>\n");
      outWriter.write("<link type='text/css' href='" +
          WebUtils.getFullThemeFolder(request) + "/alib.css' rel='stylesheet' />\n");
    } else {
      boolean linkCurrent = current != null && current.equalsIgnoreCase(LINK);

      SiteMap siteMap = webSite.getSiteMap();
      SiteInfo siteInfo = webSite.getSiteInfo();
      Path rootPath = (path == null) ? siteInfo.getThemeRoot(pagePath) : new Path(path);
      Path pathInMenu = webSite.getSiteMap().getPathInMenu(pagePath);
      int baseLevel = rootPath.getElementCount() + 1;
      int lastLevel = rootPath.getElementCount();
      Iterator iter = siteMap.getPagesList(rootPath).iterator();
      boolean liUsed = false;
      boolean firstUl = true;

      while (iter.hasNext()) {
        PageInfo current = (PageInfo) iter.next();
        Path currentPath = current.getPath();
        int level = Math.max(baseLevel, current.getLevel());

        for (int i = lastLevel; i < level; i++) {
          if (firstUl) {
            writeIndented(outWriter, "<ul class=\"" +
                (horizontal ? "hmenu" : "vmenu") + "\">", i);
            firstUl = false;
          } else {
            writeIndented(outWriter, "<ul>", i);
          }

          writeIndented(outWriter, "<li>", i + 1);
          liUsed = false;
        }

        for (int i = lastLevel - 1; i >= level; i--) {
          if (liUsed) {
            outWriter.write("</li>");
            liUsed = false;
          } else {
            writeIndented(outWriter, "</li>", i + 1);
          }

          writeIndented(outWriter, "</ul>", i);
        }

        if (liUsed) {
          outWriter.write("</li>");
          writeIndented(outWriter, "<li>", level);
        }

        for ( int i = lastLevel - 1; i >= level; i--) {
            writeIndented(outWriter, "</li>", i);
            writeIndented(outWriter, "<li>", i);
        }

        if ( ! Utils.isNullOrEmpty(currentPathStyle)
        		&& ( current.getLevel() > 0
        		       && pathInMenu.isContainedIn(currentPath) 
                     || current.getPath().equals(pathInMenu)
                   ) ) {
          outWriter.write("<a href=\"" + cp + webSite.getLink(current) +
            "\" class='" + currentPathStyle + "'>" +
            siteInfo.getPageTitle(current) + "</a>");
        } else {
          outWriter.write("<a href=\"" + cp + webSite.getLink(current) +"\">" +
            siteInfo.getPageTitle(current) + "</a>");
        }

        liUsed = true;
        lastLevel = level;
      }

      for (int i = lastLevel - 1; i >= rootPath.getElementCount(); i--) {
        writeIndented(outWriter, "</li>", i + 1);
        writeIndented(outWriter, "</ul>", i);
      }
    }
  }

  private void writeIndented(Writer w, String s, int level) throws IOException {
    while (indentBuffer.length() < indentation.length() * level) {
      indentBuffer += indentation;
    }

    w.write('\n');
    w.write(indentBuffer, 0, indentation.length() * level);
    w.write(s);
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getCurrent() {
    return current;
  }

  public void setCurrent(String current) {
    this.current = current;
  }

  public String getOrientation() {
    return orientation;
  }

  public void setOrientation(String orientation) {
    this.orientation = orientation;
  }

  public String getPart() {
    return part;
  }

  public void setPart(String part) {
    this.part = part;
  }

  public String getCurrentPathStyle() {
    return currentPathStyle;
  }

  public void setCurrentPathStyle(String currentPathStyle) {
    this.currentPathStyle = currentPathStyle;
  }
}
