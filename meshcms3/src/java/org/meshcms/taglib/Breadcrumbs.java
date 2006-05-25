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
 * Inserts breadcrumbs for the current page.
 */
public final class Breadcrumbs extends AbstractTag {
  public static final String MODE_TITLES = "titles";
  public static final String MODE_LINKS = "links";
  public static final String DEFAULT_SEPARATOR = " ";

  private String separator = DEFAULT_SEPARATOR;
  private String mode;
  private String style;
  private String target;
  private String current = "true";
  private String pre;
  private String post;

  public void setSeparator(String separator) {
    this.separator = Utils.noNull(separator, DEFAULT_SEPARATOR);
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public void setPre(String pre) {
    this.pre = pre;
  }

  public void setPost(String post) {
    this.post = post;
  }

  public void writeTag() throws IOException {
    PageInfo[] breadcrumbs = webSite.getSiteMap().getBreadcrumbs(pagePath);
    String[] outs;

    if (mode != null && mode.equals(MODE_LINKS)) {
      outs = webSite.getLinkList(breadcrumbs, request.getContextPath(), target,
                                style);
    } else {
      outs = webSite.getTitles(breadcrumbs);
    }

    if (Utils.isTrue(current)) {
      int last = 0;

      if (outs == null) {
        outs = new String[1];
      } else {
        last = outs.length;
        String[] temp = new String[last + 1];
        System.arraycopy(outs, 0, temp, 0, last);
        outs = temp;
      }

      PageInfo pageInfo = webSite.getSiteMap().getPageInfo(pagePath);
      outs[last] = (pageInfo == null) ? getPage().getTitle() :
          webSite.getSiteInfo().getPageTitle(pageInfo);
    }
    
    Writer w = getOut();

    if (outs != null && outs.length > 0) {
      if (pre != null) {
        w.write(pre);
      }

      w.write(Utils.generateList(outs, separator));

      if (post != null) {
        w.write(post);
      }
    } else {
      w.write("&nbsp;");
    }
  }

  public String getSeparator() {
    return separator;
  }

  public String getMode() {
    return mode;
  }

  public String getStyle() {
    return style;
  }

  public String getTarget() {
    return target;
  }

  public String getCurrent() {
    return current;
  }

  public void setCurrent(String current) {
    this.current = current;
  }

  public String getPre() {
    return pre;
  }

  public String getPost() {
    return post;
  }
}
