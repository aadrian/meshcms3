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
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Writes a module or the needed fields to edit it.
 */
public final class Module extends AbstractTag {
  public static final String DATE_NONE = "none";
  public static final String DATE_NORMAL = "normal";
  public static final String DATE_FULL = "full";

  private String name;
  private String date;
  private String style;
  private String location = "";
  private String alt = "&nbsp;";
  private String parameters;

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
      if (parameters != null) {
        md.parseParameters(parameters);
      }

      Path modulePath = webSite.getModulePath(md.getTemplate());

      if (modulePath != null) {
        md.setPagePath(pagePath);
        md.setModulePath(modulePath);
        md.setDateFormat(date);
        md.setStyle(style);

        String moduleCode = "meshcmsmodule_" + location;
        request.setAttribute(moduleCode, md);

        try {
          pageContext.include("/" + webSite.getServedPath(modulePath) + "/" +
              SiteMap.MODULE_INCLUDE_FILE + "?modulecode=" + moduleCode);
        } catch (ServletException ex) {
          WebUtils.setBlockCache(request);
          webSite.log("Exception while including module " + modulePath, ex);
        }
      }
    } else {
      getOut().write(alt);
    }
  }

  public void writeEditTag() throws IOException, JspException {
    String uniqueHash = new Integer(new Object().hashCode()).toString();
    String tagIdPrefix = "meshcmsmodule_"+ location +"_"+ uniqueHash +"_";
    String idCont = tagIdPrefix +"cont";
    String idElem = tagIdPrefix +"elem";
    String idIcon = tagIdPrefix +"icon";
    boolean isEditorModulesCollapsed = webSite.getConfiguration().isEditorModulesCollapsed();

    String template = null;
    String argPath = null;
    String advParms = null;

    ModuleDescriptor md = getModuleDescriptor(location, name);
    
    if (md != null) {
      template = md.getTemplate();
      argPath  = md.getArgument();
      advParms = Utils.listProperties(md.getAdvancedParams(), ", ");
    }

    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);
    MessageFormat formatter = new MessageFormat("", locale);
    
    Writer w = getOut();

    Object[] args = {
      location,
      template != null ? Utils.beautify(template,true) : bundle.getString("editorNoTemplate"),
      Utils.noNull(argPath),
      Utils.noNull(advParms)
    };

    if (isEditorModulesCollapsed) {
      w.write("<div id=\"" + idCont + "\" class='meshcmsfieldlabel' " +
          " style=\"cursor:pointer;position:relative;\" onclick=\"javascript:editor_moduleShow('" +
          idCont + "','" + idElem + "','" + idIcon + "');\">" +
          "<img alt=\"\" src=\"" + afp + "/images/tree_plus.gif\" id=\"" + idIcon + "\" />\n");
      formatter.applyPattern(bundle.getString("editorModuleLocExt"));
      w.write("<label for=\"" + idElem + "\">" + formatter.format(args) + "</label>");
      w.write("</div>");
    }

    w.write("<fieldset "+ (isEditorModulesCollapsed ? "style=\"display:none;\"" : "") +
    	" id=\""+ idElem +"\" class='meshcmseditor' >\n");
    formatter.applyPattern(bundle.getString("editorModuleLoc"));
    w.write(" <legend>" + formatter.format(args) + "</legend>\n");

    if (name != null) {
      w.write(bundle.getString("editorFixedModule"));

      if (argPath != null) {
        w.write("<img alt=\"\" src='" + afp + "/images/small_browse.gif' title='" +
          bundle.getString("editorBrowseModule") +
          "' onclick=\"javascript:window.open('" +
          afp + "/filemanager/index.jsp?folder=" +
          Utils.escapeSingleQuotes(argPath) +
          "', '_blank').focus();\" style='vertical-align:middle;' />\n");
      }
    } else {
      w.write(" <div class='meshcmsfieldlabel'><label for='" +
        ModuleDescriptor.TEMPLATE_ID + location + "'>" +
        bundle.getString("editorModuleTemplate") + "</label></div>\n");
      w.write(" <div class='meshcmsfield'>\n  <select name='" +
        ModuleDescriptor.TEMPLATE_ID + location + "' id='" +
        ModuleDescriptor.TEMPLATE_ID + location + "'>\n");
      w.write("   <option value='" + PageAssembler.EMPTY + "'>" +
        bundle.getString("editorNoTemplate") + "</option>\n");

      String[] mtNames = webSite.getSiteMap().getModuleNames();

      for (int i = 0; i < mtNames.length; i++) {
        w.write("   <option value='" + mtNames[i] + "'");

        if (md != null && mtNames[i].equals(template)) {
          w.write(" selected='selected'");
        }

        w.write(">" + Utils.beautify(Utils.removeExtension(mtNames[i]), true) + "</option>\n");
      }

      w.write("  </select>\n </div>\n");
      w.write(" <div class='meshcmsfieldlabel'><label for='" +
        ModuleDescriptor.ARGUMENT_ID + location + "'>" +
        bundle.getString("editorModuleArgument") + "</label></div>\n");
      w.write(" <div class='meshcmsfield'><img alt=\"\" src='" + afp +
        "/images/clear_field.gif' onclick=\"javascript:editor_clr('" +
        ModuleDescriptor.ARGUMENT_ID + location + "');\" style='vertical-align:middle;' /><input type='text' id='" +
        ModuleDescriptor.ARGUMENT_ID + location + "' name='" +
        ModuleDescriptor.ARGUMENT_ID + location + "' value=\"" +
        (md == null || argPath == null ? "" : argPath) +
        "\" style='width: 80%;' /><img alt=\"\" src='" + afp +
        "/images/small_browse.gif' title='" + bundle.getString("genericBrowse") +
        "' onclick=\"javascript:editor_openFileManager('" +
        ModuleDescriptor.ARGUMENT_ID + location + "');\" style='vertical-align:middle;' /></div>\n");

      w.write(" <div class='meshcmsfieldlabel'><label for='" +
        ModuleDescriptor.PARAMETERS_ID + location + "'>" +
        bundle.getString("editorModuleParameters") + "</label></div>\n");
      w.write(" <div class='meshcmsfield'><img alt=\"\" src='" + afp +
        "/images/clear_field.gif' onclick=\"javascript:editor_clr('" +
        ModuleDescriptor.PARAMETERS_ID + location + "');\" style='vertical-align:middle;' /><input type='text' id='" +
        ModuleDescriptor.PARAMETERS_ID + location + "' name='" +
        ModuleDescriptor.PARAMETERS_ID + location + "' value=\"" +
        (md == null || md.getAdvancedParams() == null ? "" :
        Utils.listProperties(md.getAdvancedParams(), ", ")) +
        "\" style='width: 80%;' /></div>\n");
    }

    w.write("</fieldset>");
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

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }
}
