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
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import com.cromoteca.meshcms.*;
import com.cromoteca.util.*;

/**
 * Writes a module or the needed fields to edit it.
 */
public final class Module extends AbstractTag {
  public static final String DATE_NONE = "none";
  public static final String DATE_NORMAL = "normal";
  public static final String DATE_FULL = "full";

  private SortedSet modSet;
  
  private String name;
  private String date;
  private String style;
  private String location = "";
  private String alt = "&nbsp;";

  public void setName(String name) {
    this.name = name;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setAlt(String alt) {
    this.alt = alt;
  }
  
  public void writeTag() throws IOException, JspException {
    if (date == null) {
      date = DATE_NONE;
    }
    
    ModuleDescriptor md = getModuleDescriptor(location, name);

    if (md != null) {
      Path moduleTemplatePath = webApp.getModuleTemplatePath(md.getTemplate());
      Path argumentPath = md.getArgumentPath();
      
      if (moduleTemplatePath != null) {
        try {
          pageContext.include("/" + moduleTemplatePath + "?pagepath=" + pagePath +
              (argumentPath == null ? "" : "&argument=" + argumentPath) +
              "&dateformat=" + date + (style == null ? "" : "&style=" + style));
        } catch (ServletException ex) {
          throw new JspException(ex);
        }
      }
    } else {
      getOut().write(alt);
    }
  }

  public void writeEditTag() throws IOException, JspException {
    UserInfo userInfo = (UserInfo) pageContext.getAttribute("userInfo",
      PageContext.SESSION_SCOPE);
    Locale locale = Utils.getLocale(userInfo == null ? null :
      userInfo.getPreferredLocaleCode(), request.getLocale());
    ResourceBundle bundle = ResourceBundle.getBundle("com/cromoteca/meshcms/Locales", locale);
    MessageFormat formatter = new MessageFormat("", locale);

    ModuleDescriptor md = getModuleDescriptor(location, name);
    Writer w = getOut();

    w.write("<table cellspacing='0' class='meshcmseditor'>\n");
    Object[] args = { location };
    formatter.applyPattern(bundle.getString("editorModuleLoc"));
    w.write("<tr><th>" + formatter.format(args) + "</th></tr>\n");
    w.write("<tr><td>\n");

    if (name != null) {
      w.write(bundle.getString("editorFixedModule"));
      
      Path argPath = md.getArgumentPath();
      
      if (argPath != null) {
        w.write("<img src='" + afp +
          "/tiny_mce/themes/advanced/images/browse.gif' title='" +
          bundle.getString("editorBrowseModule") +
          "' onclick=\"javascript:window.open('" +
          afp + "/filemanager/index.jsp?folder=" +
          Utils.escapeSingleQuotes(argPath) +
          "', '_blank').focus();\" align='middle' />\n");
      }
    } else {
      w.write(" <div style='display: inline; white-space: nowrap;'>\n  " +
        bundle.getString("editorModuleTemplate") + " <select name='" +
        MODULES_SELECT + location + "'>\n");
      w.write("  <option value='" + EMPTY + "'>" +
        bundle.getString("editorNoTemplate") + "</option>\n");

      String[] mtNames = webApp.getSiteMap().getModuleTemplateNames();

      for (int i = 0; i < mtNames.length; i++) {
        w.write(" <option value='" + mtNames[i] + "'");

        if (md != null && mtNames[i].equals(md.getTemplate())) {
          w.write(" selected='selected'");
        }

        w.write(">" + Utils.beautify(Utils.removeExtension(mtNames[i]), true) + "</option>\n");
      }

      w.write(" </select>\n </div>\n");
      w.write(" <div style='display: inline; white-space: nowrap;'>\n  " +
        bundle.getString("editorModuleArgument") + " <img src='" +
        afp + "/images/clear_field.gif' onclick=\"javascript:editor_clr('" +
        MODULES_ARG + location + "');\" align='middle' /><input type='text' id='" +
        MODULES_ARG + location + "' name='" + MODULES_ARG + location + "' value=\"" +
        (md == null || md.getArgumentPath() == null ? "" : "/" + md.getArgumentPath()) +
        "\" style='width: 12em;' /> <img src='" + afp +
        "/tiny_mce/themes/advanced/images/browse.gif' title='" +
        bundle.getString("genericBrowse") +
        "' onclick=\"javascript:editor_openFileManager('" +
        MODULES_ARG + location + "');\" align='middle' />\n </div>\n");
    }

    w.write("</td></tr>\n");
    w.write("</table>");
  }

  public String getName() {
    return name;
  }

  public String getDate() {
    return date;
  }

  public String getStyle() {
    return style;
  }

  public String getLocation() {
    return location;
  }

  public String getAlt() {
    return alt;
  }
}
