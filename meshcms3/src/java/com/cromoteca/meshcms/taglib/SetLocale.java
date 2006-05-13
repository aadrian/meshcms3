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
import javax.servlet.jsp.*;
import com.cromoteca.util.*;

/**
 * Writes the context path as returned by
 * <code>HttpServletRequest.getContextPath()</code>.
 */
public final class SetLocale extends AbstractTag {
  private String value;
  
  public void writeTag() throws IOException {
    Locale locale = null;

    if (value != null) {
      locale = Utils.getLocale(value);
    } else if (!pagePath.isRoot()) {
      for (int i = pagePath.getElementCount() - 1; locale == null && i >= 0; i--) {
        locale = Utils.getLocale(pagePath.getElementAt(i));
      }
    }
    
    if (locale != null) {
      pageContext.setAttribute(LOCALE_ATTRIBUTE, locale, PageContext.SESSION_SCOPE);
    }
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
