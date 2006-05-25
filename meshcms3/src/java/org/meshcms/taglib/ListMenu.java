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
public final class ListMenu extends AbstractTag {
  public static final String ITEMS_ALL = "all";
  public static final String ITEMS_ON_PATH = "onpath";
  public static final String ITEMS_FIRST_LEVEL = "firstlevel";
  public static final String ITEMS_LAST_LEVEL = "lastlevel";
  public static final String ITEMS_CHILDREN = "children";
  public static final String ITEMS_INTERMEDIATE_LEVELS = "intermediatelevels";

  private String indentation = "  ";
  private String indentBuffer = "";

  private String path;
  private String items;
  private String style;
  private String current;

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

    boolean linkCurrent = current != null && current.equalsIgnoreCase("link");

    SiteMap siteMap = webSite.getSiteMap();
    SiteInfo siteInfo = webSite.getSiteInfo();
    Path rootPath = (path == null) ? siteInfo.getThemeRoot(pagePath) : new Path(path);
    Path pathInMenu = webSite.getSiteMap().getPathInMenu(pagePath);
    int baseLevel = rootPath.getElementCount() + 1;
    Writer outWriter = getOut();
    int lastLevel = rootPath.getElementCount();
    Iterator iter = siteMap.getPagesList(rootPath).iterator();
    boolean liUsed = false;

    while (iter.hasNext()) {
      PageInfo current = (PageInfo) iter.next();
      Path currentPath = current.getPath();
      int level = Math.max(baseLevel, current.getLevel());

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
        for (int i = lastLevel; i < level; i++) {
          writeIndented(outWriter, "<ul>", i);
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

        if (!isEdit && !linkCurrent && current.getPath().equals(pathInMenu)) {
          outWriter.write(siteInfo.getPageTitle(current));
        } else {
          outWriter.write("<a href=\"" + cp + webSite.getLink(current) +"\">" +
            siteInfo.getPageTitle(current) + "</a>");
        }

        liUsed = true;
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
}