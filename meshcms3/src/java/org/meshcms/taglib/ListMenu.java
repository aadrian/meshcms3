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
import java.util.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Creates a navigation menu, using a unnumbered list.
 */
public final class ListMenu extends AbstractTag {
  public static final String ITEMS_ALL = "all";
  public static final String ITEMS_ON_PATH = "onpath";
  public static final String ITEMS_FIRST_LEVEL = "firstlevel";
  public static final String ITEMS_LAST_LEVEL = "lastlevel";
  public static final String ITEMS_CHILDREN = "children";
  public static final String ITEMS_INTERMEDIATE_LEVELS = "intermediatelevels";
  public static final String TEXT = "text";
  public static final String LINK = "link";

  private String indentation = "  ";
  private String indentBuffer = "";

  private String path;
  private String items;
  private String style;
  private String current;
  private String currentStyle;
  private String currentPathStyle;
  private String allowHiding;

  boolean itemsAll;
  boolean itemsOnPath;
  boolean itemsFirstLevel;
  boolean itemsLastLevel;
  boolean itemsChildren;
  boolean itemsIntermediateLevels;

  public void writeTag() throws IOException {
    if (items == null) {
      itemsAll = itemsIntermediateLevels = false;
      itemsOnPath = itemsFirstLevel = itemsLastLevel = itemsChildren = true;
    } else {
      StringTokenizer st = new StringTokenizer(items.toLowerCase(), ",;: ");

      while (st.hasMoreTokens()) {
        String token = st.nextToken();

        if (token.equals(ITEMS_ALL)) {
          itemsAll = true;
        } else if (token.equals(ITEMS_CHILDREN)) {
          itemsChildren = true;
        } else if (token.equals(ITEMS_FIRST_LEVEL)) {
          itemsFirstLevel = true;
        } else if (token.equals(ITEMS_INTERMEDIATE_LEVELS)) {
          itemsIntermediateLevels = true;
        } else if (token.equals(ITEMS_LAST_LEVEL)) {
          itemsLastLevel = true;
        } else if (token.equals(ITEMS_ON_PATH)) {
          itemsOnPath = true;
        }
      }
    }

    boolean linkCurrent = current != null && current.equalsIgnoreCase(LINK);

    SiteMap siteMap = webSite.getSiteMap();
    SiteInfo siteInfo = webSite.getSiteInfo();
    Path rootPath = (path == null) ? siteInfo.getThemeRoot(pagePath) : new Path(path);
    Path pathInMenu = webSite.getSiteMap().getPathInMenu(pagePath);
    int baseLevel = rootPath.getElementCount() + 1;
    Writer outWriter = getOut();
    int lastLevel = rootPath.getElementCount();
    SiteMapIterator iter = new SiteMapIterator(webSite, rootPath);
    iter.setSkipHiddenSubPages(Utils.isTrue(allowHiding));
    boolean styleNotApplied = !Utils.isNullOrEmpty(style);
    int showLastLevel = -1;

    while (iter.hasNext()) {
      PageInfo current = (PageInfo) iter.next();
      Path currentPath = current.getPath();
      int level = Math.max(baseLevel, current.getLevel());

      if ( current.getLevel() <= showLastLevel )
      	showLastLevel = -1;

      if ( siteInfo.getHideSubmenu(currentPath) && showLastLevel == -1 )
      	showLastLevel = current.getLevel();

      boolean add = false;

      if (itemsAll) {
        add = true;
      } else {
        Path parentPath = currentPath.getParent();

        if (parentPath.isRelative() || pathInMenu.isContainedIn(parentPath)) {
          if (itemsOnPath && pathInMenu.isContainedIn(currentPath)) {
            add = true;
          } else if (level <= baseLevel) {
            add = itemsFirstLevel;
          } else if (currentPath.getElementCount() == pathInMenu.getElementCount()) {
            add = itemsLastLevel;
          } else if (currentPath.getElementCount() > pathInMenu.getElementCount()) {
            add = itemsChildren;
          } else {
            add = itemsIntermediateLevels;
          }
        }
      }

      if (add) {
      // Close off any pending lower levels
      for (int i = lastLevel - 1; i >= level; i--) {
              writeIndented(outWriter, "</li>", i + 1);
              writeIndented(outWriter, "</ul>", i);
      }

      // If we're a level deeper - then create new sub ul+li
      for (int i = lastLevel; i < level; i++) {
        if (styleNotApplied) {
          writeIndented(outWriter, "<ul class=\"" + style + "\">", i);
          styleNotApplied = false;
        } else {
          writeIndented(outWriter, "<ul>", i);
        }
        writeIndented(outWriter, "<li>", i + 1);
      }

      // If we do not have an open li at the right level - then create one
      if (level <= lastLevel) {
          writeIndented(outWriter, "</li>", level);
          writeIndented(outWriter, "<li>", level);
      }

      if ( current.getLevel() > 0 && pathInMenu.isContainedIn(currentPath)
              && ! current.getPath().equals(pathInMenu)
              && ! Utils.isNullOrEmpty(currentPathStyle)) {
          outWriter.write("<a href=\"" + cp + webSite.getLink(current) +
                  "\" class='" + currentPathStyle + "'>" +
                  siteInfo.getPageTitle(current) + "</a>");
      } else if (current.getPath().equals(pathInMenu)) {
        if (isEdit || linkCurrent) {
          if (! Utils.isNullOrEmpty(currentStyle) || ! Utils.isNullOrEmpty(currentPathStyle)) {
              outWriter.write("<a href=\"" + cp + webSite.getLink(current) +
                      "\" class='" + (currentPathStyle+" "+currentStyle).trim() + "'>" +
                      siteInfo.getPageTitle(current) + "</a>");
          } else {
              outWriter.write("<a href=\"" + cp + webSite.getLink(current) + "\">" +
                      siteInfo.getPageTitle(current) + "</a>");
          }
        } else {
          if (Utils.isNullOrEmpty(currentStyle)) {
            outWriter.write(siteInfo.getPageTitle(current));
          } else {
            outWriter.write("<span class='" + currentStyle + "'>" +
              siteInfo.getPageTitle(current) + "</span>");
          }
        }
      } else {
        outWriter.write("<a href=\"" + cp + webSite.getLink(current) + "\">" +
          siteInfo.getPageTitle(current) + "</a>");
      }

      lastLevel = level;
      }
    }

    for (int i = lastLevel - 1; i >= rootPath.getElementCount(); i--) {
      writeIndented(outWriter, "</li>", i + 1);
      writeIndented(outWriter, "</ul>", i);
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

  public String getItems() {
    return items;
  }

  public void setItems(String items) {
    this.items = items;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getCurrent() {
    return current;
  }

  public void setCurrent(String current) {
    this.current = current;
  }

  public String getCurrentStyle() {
    return currentStyle;
  }

  public void setCurrentStyle(String currentStyle) {
    this.currentStyle = currentStyle;
  }

  public String getCurrentPathStyle() {
    return currentPathStyle;
  }

  public void setCurrentPathStyle(String currentPathStyle) {
    this.currentPathStyle = currentPathStyle;
  }
  public String getAllowHiding() {
    return allowHiding;
  }

  public void setAllowHiding(String allowHiding) {
    this.allowHiding = allowHiding;
  }
}
