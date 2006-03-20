/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2005 Luciano Vernaschi
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
 *
 * This tag has been created by Matthijs Dekker.
 */

package com.cromoteca.meshcms.taglib;

import java.io.*;
import java.util.*;
import com.cromoteca.meshcms.*;
import com.cromoteca.util.*;

/**
 * Creates a navigation menu, using a unnumbered list.
 */
public final class ListMenu extends AbstractTag {
  private int baseLevel;
  private String path;
  private String expand;
  private String style;

  public void setPath(String path) {
    this.path = path;
  }

  public void writeTag() throws IOException {
    Writer outWriter = getOut();
    SiteMap siteMap = webApp.getSiteMap();
    Path rootPath = new Path(path);
    baseLevel = rootPath.getElementCount() + 1;
    List items = siteMap.getPagesList(rootPath);
    StringBuffer sb = new StringBuffer();
    printLevel(items, 0, -1, sb);
    outWriter.write(sb.toString());
  }

  /**
   * Print the current menu level. This method uses recursion to print the sub
   * levels also.
   * 
   * @param items
   * @param curItem
   * @param currentLevel
   * @param sbReturn
   * @return
   * @throws IOException
   */
  private int printLevel(List items, int curItem, int currentLevel,
      StringBuffer sbReturn) throws IOException {
    StringBuffer sb = new StringBuffer();
    int liCount = 0;

    while (curItem < items.size()) {
      PageInfo current = (PageInfo) items.get(curItem);

      if (current.getLevel() > currentLevel) {
        // submenu
        StringBuffer sbRes = new StringBuffer();
        curItem = printLevel(items, curItem, current.getLevel(), sbRes);
        // Only process if submenu has content
        if (sbRes.length() > 0) {
          // encapsulate in ul element
          if (currentLevel == -1 && !Utils.isNullOrEmpty(style)) {
            sb.append("<ul class=\"");
            sb.append(style);
            sb.append("\">\n");
          } else {
            sb.append("<ul>\n");
          }
          sb.append(sbRes.toString());
          addIdentation(sb, currentLevel + 1);
          sb.append("</ul>");
        }
      } else if (current.getLevel() < currentLevel) {
        // Nothing more for this level
        break;
      } else {
        // Normal list item
        if (liCount > 0) {
          // Close the previous list item
          sb.append("</li>\n");
        }
        // Get the list items
        String li = liItem(current);
        if (li.length() > 0) {
          liCount++;
          sb.append(li);
        }
        curItem++;
      }
    }
    // Done with this level, finish everything
    if (sb.length() > 0) {
      if (currentLevel > -1) {
        // Close the list item containing the UL submenu
        sb.append("</li>\n");
      }
      sbReturn.append(sb.toString());
    }
    return curItem;
  }

  /**
   * Create a list item
   * 
   * @param current
   * @return
   */
  private String liItem(PageInfo current) {
    Path pathInMenu = webApp.getSiteMap().getPathInMenu(pagePath);
    StringBuffer sb = new StringBuffer();
    Path currentPath = current.getPath();
    Path parentPath = currentPath.getParent();

    if (parentPath.isRelative() || pathInMenu.isContainedIn(parentPath)
        || Utils.isTrue(expand)) {
      if (pathInMenu.isContainedIn(currentPath)
          || currentPath.getElementCount() == baseLevel
          || currentPath.getElementCount() >= pathInMenu
              .getElementCount() || Utils.isTrue(expand)) {
        addIdentation(sb, current.getLevel() + 1);
        sb.append("<li>");
        if ((!isEdit && current.getPath().equals(pathInMenu))
            && !Utils.isTrue(expand)) {
          // Just the title, no anchor
          sb.append("<span>");
          sb.append(webApp.getSiteInfo().getPageTitle(current));
          sb.append("</span>");
        } else {
          sb.append("<a href=\"");
          sb.append(cp);
          sb.append(current.getLink());
          sb.append("\" ");
          sb.append("title=\"");
          sb.append(webApp.getSiteInfo().getPageTitle(current));
          sb.append("\" >");
          sb.append(webApp.getSiteInfo().getPageTitle(current));
          sb.append("</a>");
        }
      }
    }
    return sb.toString();
  }

  /**
   * Add 'tab' characters to the StringBuffer, to ident menu levels (looks
   * nicer in HTML source and has no impact on the rendering)
   * 
   * @param sb
   * @param level
   */
  private void addIdentation(StringBuffer sb, int level) {
    for (int x = 0; x < level; x++) {
      sb.append("\t");
    }
  }

  public String getPath() {
    return path;
  }

  public String getExpand() {
    return expand;
  }

  public void setExpand(String expand) {
    this.expand = expand;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }
}
