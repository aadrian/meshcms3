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

import com.cromoteca.meshcms.WebUtils;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Writes the date and time of last modification of the page.
 */
public final class LastModified extends AbstractTag {
  public static final String DATE_NORMAL = "normal";
  public static final String DATE_FULL = "full";
  public static final String MODE_STATIC = "static";
  public static final String MODE_ALL = "all";

  private String date;
  private String mode;
  private String pre;
  private String post;

  public void setDate(String date) {
    this.date = date;
  }

  public void setMode(String mode) {
    this.mode = mode;
  }

  public void setPre(String pre) {
    this.pre = pre;
  }

  public void setPost(String post) {
    this.post = post;
  }

  public void writeTag() throws IOException {
    Writer w = getOut();
    
    if ((mode != null && mode.equals(MODE_ALL)) ||
        webApp.getFileTypes().isLike(pagePath, "html")) {
      Locale locale = WebUtils.getPageLocale(pageContext);
      DateFormat df;

      if (date != null && date.equals(DATE_FULL)) {
        df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,
            locale);
      } else {
        df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
      }

      if (pre != null) {
       w.write(pre);
      }

      w.write(df.format(new Date(WebUtils.getLastModifiedTime(request))));

      if (post != null) {
        w.write(post);
      }
    } else {
      w.write("&nbsp;");
    }
  }

  public String getDate() {
    return date;
  }

  public String getMode() {
    return mode;
  }

  public String getPre() {
    return pre;
  }

  public String getPost() {
    return post;
  }
}
