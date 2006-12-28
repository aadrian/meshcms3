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
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Creates a navigation menu based on the TigraMenu script
 * created by <a href="http://www.softcomplex.com/">SoftComplex</a>.
 */
public final class TigraMenu extends AbstractTag {
  public static final String PART_HEAD = "head";
  public static final String PART_BODY = "body";

  private String part;
  private String path;
  private String placeholder;
  private boolean allowHiding = false;

  public void writeTag() throws IOException {
    Path rootPath = (path == null) ?
        webSite.getSiteInfo().getThemeRoot(pagePath) : new Path(path);
    String script = WebUtils.getFullThemeFolder(request);
    Writer outWriter = getOut();

    if (part.equals(PART_BODY)) {
      outWriter.write("<!-- Tigra Menu 2.0 -->\n");
      outWriter.write("<script language=\"JavaScript\">\n");

      if (Utils.isTrue(placeholder)) {
        outWriter.write("document.write('<img src=\"" + afp + '/' + 
                        WebSite.ADMIN_THEME + "/tx1x1.gif\" width=\"'+MENU_POS[0]" +
                        "['width']+'\" height=\"'+(MENU_ITEMS.length*" +
                        "MENU_POS[0]['top'])+'\">');\n");
      }

      outWriter.write("new menu(MENU_ITEMS, MENU_POS);\n");
      outWriter.write("</script>\n");
      outWriter.write("<!-- End Tigra Menu -->\n");
    } else if (part.equals(PART_HEAD)) {
      outWriter.write("<!-- Tigra Menu 2.0 -->\n");
      outWriter.write("<link rel=\"stylesheet\" href=\"" + script +
                      "/menu.css\">\n");
      outWriter.write("<script language=\"JavaScript\" src=\"" + script +
                      "/menu.js\"></script>\n");
      outWriter.write("<script language=\"JavaScript\">\n");
      outWriter.write(webSite.getSiteMap().getTigraItems(cp, rootPath, false, allowHiding));
      outWriter.write("\n</script>\n");
      outWriter.write("<script language=\"JavaScript\" src=\"" + script +
                      "/menu_tpl.js\"></script>\n");
      outWriter.write("<!-- End Tigra Menu -->\n");
    }
  }

  public String getPart() {
    return part;
  }

  public void setPart(String part) {
    this.part = part;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }
  
  public boolean getAllowHiding() {
    return allowHiding;
  }

  public void setAllowHiding(boolean allowHiding) {
    this.allowHiding = allowHiding;
  }
}
