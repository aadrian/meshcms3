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
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Writes the context path as returned by
 * <code>HttpServletRequest.getContextPath()</code>.
 */
public final class SetLocale extends AbstractTag {
  /**
   * @deprecated no longer used
   */
  public static final String REDIRECT_ATTRIBUTE = "meshcms-redirect";

  private String value;
  private String defaultValue;
  private String redirectRoot;

  public void writeTag() throws IOException {
    /* if (Utils.isTrue(redirectRoot) &&
        webSite.getSiteMap().getPathInMenu(pagePath).isRoot()) {
      if (setRedirectToLanguage(request,
          (HttpServletResponse) pageContext.getResponse())) {
        return;
      }
    } */

    Locale locale = null;

    if (value != null) {
      locale = Utils.getLocale(value);
    } else if (!pagePath.isRoot()) {
      for (int i = pagePath.getElementCount() - 1; locale == null && i >= 0; i--) {
        locale = Utils.getLocale(pagePath.getElementAt(i));
      }
    }

    if (locale == null) {
      locale = Utils.getLocale(defaultValue);
    }

    if (locale != null) {
      pageContext.setAttribute(HitFilter.LOCALE_ATTRIBUTE, locale,
          PageContext.REQUEST_SCOPE);
    }
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  /**
   * @deprecated parameter no longer used
   */
  public String getRedirectRoot() {
    return redirectRoot;
  }

  /**
   * @deprecated parameter no longer used
   */
  public void setRedirectRoot(String redirectRoot) {
    this.redirectRoot = redirectRoot;
  }

  /**
   * @deprecated moved to HitFilter
   */
  public static boolean setRedirectToLanguage(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    if (response.isCommitted()) {
      return false;
    }

    WebSite webSite = (WebSite) request.getAttribute(HitFilter.WEBSITE_ATTRIBUTE);
    List available = webSite.getSiteMap().getLangList();
    String[] accepted = WebUtils.getAcceptedLanguages(request);
    SiteMap.CodeLocalePair chosen = null;

    if (available != null && available.size() > 0) {
      for (int i = 0; chosen == null && i < accepted.length; i++) {
        Iterator iter = available.iterator();

        while (chosen == null && iter.hasNext()) {
          SiteMap.CodeLocalePair clp = (SiteMap.CodeLocalePair) iter.next();

          if (clp.getCode().equalsIgnoreCase(accepted[i])) {
            chosen = clp;
          }
        }
      }

      if (chosen == null) {
        chosen = (SiteMap.CodeLocalePair) available.get(0);
      }

      WebUtils.setBlockCache(request);
      request.setAttribute(REDIRECT_ATTRIBUTE, request.getContextPath() + '/' +
          chosen.getCode() + '/');
      return true;
    }

    return false;
  }
}
