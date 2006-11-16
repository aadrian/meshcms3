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
import javax.servlet.jsp.JspException;
import org.meshcms.core.*;
import org.meshcms.util.*;

public class SwitchLang extends AbstractTag {
  private String separator = " ";

  public void setSeparator(String separator) {
    if (separator != null) {
      this.separator = separator;
    }
  }

  public String getSeparator() {
    return separator;
  }

  public void writeTag() throws IOException, JspException {
    SiteMap siteMap = webSite.getSiteMap();

    if (pagePath.isRoot() || siteMap.getPageInfo(pagePath) == null) {
      return;
    }
    
    List langList = siteMap.getLangList();
    Iterator iter = langList.iterator();
    boolean putSeparator = false;
    Writer w = getOut();
    
    while (iter.hasNext()) {
      if (putSeparator) {
        w.write(separator);
      }
      
      putSeparator = true;
      SiteMap.CodeLocalePair lang = (SiteMap.CodeLocalePair) iter.next();
      String langCode = lang.getCode();
      Locale locale = lang.getLocale();
      String localeName = WebUtils.encodeHTML
          (Utils.toTitleCase(locale.getDisplayName(locale)));
      
      if (langCode.equals(pagePath.getElementAt(0))) {
        w.write(localeName);
      } else {
        Path path = siteMap.getServedPath(pagePath.replace(0, langCode));

        if (!webSite.getFile(path).isFile()) {
          if (isEdit) {
            path = new Path(langCode); // temporary
          } else {
            path = new Path(langCode);
          }
        }

        w.write("<a href=\"" + cp + path.getAsLink() + "\">" + localeName + "</a>");
      }
    }
  }
}
