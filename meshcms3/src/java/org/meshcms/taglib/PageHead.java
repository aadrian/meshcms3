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
import java.util.regex.*;
import javax.servlet.jsp.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Writes the page head. Also adds some init variables when editing. Please note
 * that since this tag is used within the &lt;head&gt; tag, the field to edit
 * the page head are displayed by {@link PageBody}
 */
public class PageHead extends AbstractTag {
  public static final Pattern META_REGEX = Pattern.compile("(?s)(?i)<meta\\s+" +
      "(?:name\\s*=\\s*([\"'])meshcms:module\\1\\s+content\\s*=\\s*([\"'])" +
      "(.+?)\\2|content\\s*=\\s*([\"'])(.+?)\\4\\s+name\\s*=\\s*([\"'])" +
      "meshcms:module\\6)[^>]*>\\n*");
  
  private String dropStyles;
  private String dropScripts;

  public void writeTag() throws IOException {
    Writer w = getOut();
    w.write(getHeadContent());
    Locale locale = (Locale) pageContext.getAttribute(HitFilter.LOCALE_ATTRIBUTE,
        PageContext.REQUEST_SCOPE);

    if (locale != null) {
      w.write("\n<meta http-equiv=\"Content-Language\" content=\"" + locale + "\" />");
    }

    if (!(userInfo == null || userInfo.isGuest()) &&
        webSite.getConfiguration().isRedirectRoot() &&
        webSite.getSiteMap().getPathInMenu(pagePath).isRoot() &&
        HitFilter.getPreferredLanguage(request) != null) {
      Locale pl = WebUtils.getPageLocale(pageContext);
      ResourceBundle bundle =
          ResourceBundle.getBundle("org/meshcms/webui/Locales", pl);
      w.write("\n<script type='text/javascript'>alert(\"" +
          bundle.getString("pageRedirectionAlert") + "\");</script>");
    }
  }

  public void writeEditTag() throws IOException {
    Path linkListPath = new Path(ap, "tinymce_linklist.jsp");
    linkListPath = linkListPath.getRelativeTo(webSite.getDirectory(pagePath));
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);
    String langCode = bundle.getString("TinyMCELangCode");

    if (Utils.isNullOrEmpty(langCode)) {
      langCode = locale.getLanguage();
    }

    Writer w = getOut();
    w.write(getHeadContent());
    w.write("\n<script type='text/javascript' src='" +
      cp + '/' + (webSite.getFile(webSite.getCMSPath().add("tiny_mce")).exists() ?
      webSite.getCMSPath() : webSite.getAdminScriptsPath()) +
      "/tiny_mce/tiny_mce.js'></script>\n");
    w.write("<script type='text/javascript'>\n");
    w.write("// <![CDATA[\n");
    w.write(" var contextPath = \"" + cp + "\";\n");
    w.write(" var adminPath = \"" + webSite.getAdminPath() + "\";\n");
    w.write(" var languageCode = \"" + langCode + "\";\n");
    w.write(" var linkListPath = \"" + linkListPath + "\";\n");
    w.write(" var cssPath = \"" + WebUtils.getFullThemeCSS(request) + "\";\n");
    w.write("// ]]>\n");
    w.write("</script>\n");
    w.write("<script type='text/javascript' src='" +
      cp + '/' + webSite.getAdminScriptsPath() + "/jquery/jquery-latest.pack.js'></script>\n");
    w.write("<script type='text/javascript' src='" +
      cp + '/' + webSite.getAdminScriptsPath() + "/editor.js'></script>\n");

    w.write("<script type='text/javascript' src='" + cp + '/' +
      (webSite.getFile(webSite.getCMSPath().add("tinymce_init.js")).exists() ?
      webSite.getCMSPath() : webSite.getAdminScriptsPath()) +
      "/tinymce_init.js'></script>");
  }

  private String getHeadContent() {
    String head = getHead();
    head = META_REGEX.matcher(head).replaceAll("");

    if (Utils.isTrue(dropStyles)) {
      head = head.replaceAll("(?i)(?s)<style[^>]*>.*?</style[^>]*>", "");
    }

    if (Utils.isTrue(dropScripts)) {
      head = head.replaceAll("(?i)(?s)<script[^>]*>.*?</script[^>]*>", "");
    }

    return head;
  }

  public String getDropStyles() {
    return dropStyles;
  }

  public void setDropStyles(String dropStyles) {
    this.dropStyles = dropStyles;
  }

  public String getDropScripts() {
    return dropScripts;
  }

  public void setDropScripts(String dropScripts) {
    this.dropScripts = dropScripts;
  }
}
