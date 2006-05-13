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
import com.cromoteca.meshcms.*;
import com.cromoteca.util.*;
import com.opensymphony.module.sitemesh.*;

/**
 * Writes the page head. Also adds some init variables when editing. Please note
 * that since this tag is used within the &lt;head&gt; tag, the field to edit
 * the page head are displayed by {@link PageBody}
 */
public class PageHead extends AbstractTag {
  public void writeTag() throws IOException {
    ((HTMLPage) getPage()).writeHead(getOut());
  }

  public void writeEditTag() throws IOException {
    writeTag();

    Path linkListPath = new Path(ap, "tinymce_linklist.jsp");
    linkListPath = linkListPath.getRelativeTo(webSite.getDirectory(pagePath));
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = ResourceBundle.getBundle("com/cromoteca/meshcms/Locales", locale);
    String langCode = bundle.getString("TinyMCELangCode");
      
    if (Utils.isNullOrEmpty(langCode)) {
      langCode = locale.getLanguage();
    }

    Writer w = getOut();
    w.write("\n<script language='javascript' type='text/javascript' src='" +
      afp + "/tiny_mce/tiny_mce.js'></script>\n");
    w.write("<script language='javascript' type='text/javascript'>\n");
    w.write(" var contextPath = \"" + cp + "\";\n");
    w.write(" var adminPath = \"" + webSite.getAdminPath() + "\";\n");
    w.write(" var languageCode = \"" + langCode + "\";\n");
    w.write(" var linkListPath = \"" + linkListPath + "\";\n");
    w.write(" var cssPath = \"" + WebUtils.getFullThemeCSS(request) + "\";\n");
    w.write("</script>\n");
    w.write("<script language='javascript' type='text/javascript' src='" +
      afp + "/editor.js'></script>\n");
    
    Path themePath = (Path) request.getAttribute(THEME_PATH_ATTRIBUTE);
    w.write("<script language='javascript' type='text/javascript' src='" +
      (webSite.getFile(themePath.add("tinymce_init.js")).exists() ?
      WebUtils.getFullThemeFolder(request) : afp) + "/tinymce_init.js'></script>");
  }
}
