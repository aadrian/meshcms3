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
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Writes some site and system data.
 */
public final class Info extends AbstractTag {
  public void writeTag() throws IOException {
    Configuration c = webSite.getConfiguration();
    String result = null;

    if (id != null) {
      id = id.toLowerCase();

      if (id.equals("host") || id.equals("domain")) {
        result = c.getSiteHost();
      } else if (id.equals("description")) {
        result = c.getSiteDescription();
      } else if (id.equals("keywords")) {
        result = c.getSiteKeywords();
      } else if (id.equals("author")) {
        result = c.getSiteAuthor();
      } else if (id.equals("authorurl")) {
        result = c.getSiteAuthorURL();
      } else if (id.equals("meshcms")) {
        result = WebSite.APP_NAME + " " + WebSite.VERSION_ID;
      } else if (id.equals("charset")) {
        result = Utils.SYSTEM_CHARSET;
      }
    }

    if (result == null) {
      result = Utils.encodeHTML(Utils.noNull(c.getSiteName()));
    }

    getOut().write(result);
  }
}
