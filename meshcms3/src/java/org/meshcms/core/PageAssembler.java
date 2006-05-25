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

package org.meshcms.core;

import java.util.*;
import org.meshcms.util.*;

/**
 * Rebuilds a page from its components.
 */
public final class PageAssembler {
  /**
   * Generic string used to indicate an empty value.
   */
  public static final String EMPTY = "(none)";

  /**
   * The name of the properties for the modules to include in the page.
   */
  public static final String MODULES_PARAM = "meshcmsmodules";

  /**
   * The name of the properties for the mail recipient of the page.
   */
  public static final String EMAIL_PARAM = "meshcmsmail";

  private String title;
  private String head;
  private String body;
  private String email;
  // private StringBuffer htmlTag = new StringBuffer();
  private StringBuffer bodyTag = new StringBuffer();
  private Properties mod_templates, mod_args, mod_params;
  private boolean contentType;
  private String charset;

  public PageAssembler() {
    mod_templates = new Properties();
    mod_args = new Properties();
    mod_params = new Properties();
    contentType = false;
  }

  /**
   * Adds a property to the page.
   */
  public void addProperty(String name, String value) {
    value = Utils.noNull(value).trim();

    if (name.equals("pagetitle")) {
      title = value;
    } else if (name.equals("meshcmshead")) {
      head = value;
    } else if (name.equals("meshcmsbody")) {
      body = value;
    } else if (name.equals(EMAIL_PARAM)) {
      email = value;
    } else if (name.startsWith(ModuleDescriptor.TEMPLATE_ID)) {
      if (!value.equals(EMPTY)) { // EMPTY is the value when "no module" is selected
        // name is something like sel_location: we need to set location->value
        // value is the name of the module template
        mod_templates.setProperty(name.substring(ModuleDescriptor.TEMPLATE_ID.length()), value);
      }
    } else if (name.startsWith(ModuleDescriptor.ARGUMENT_ID)) {
      if (!Utils.isNullOrEmpty(value)) {
        // name is something like arg_location: we need to set location->value
        // value is the name of the module argument (i.e. the selected file/folder)
        mod_args.setProperty(name.substring(ModuleDescriptor.ARGUMENT_ID.length()), value);
      }
    } else if (name.startsWith(ModuleDescriptor.PARAMETERS_ID)) {
        mod_params.setProperty(name.substring(ModuleDescriptor.PARAMETERS_ID.length()), value);
    } else if (name.startsWith("body.")) {
      bodyTag.append(' ').append(name.substring(5)).append("=\"").append(value).append('\"');
    } else if (name.startsWith("meta.")) {
      if (name.toLowerCase().indexOf("content-type") != -1) {
        charset = WebUtils.parseCharset(value);

        if (charset != null) {
          contentType = true;
        }
      }
    /*
      } else if (name.startsWith("page.")) {
        //
      } else if (name.startsWith("mce_editor")) {
        //
      } else {
        htmlTag.append(' ').append(name).append("=\"").append(value).append('\"');
    */
    }
  }

  /**
   * Returns the complete page.
   */
  public String getPage() {
    StringBuffer sb = new StringBuffer("<html");
    // sb.append(htmlTag);

    Enumeration locations = mod_templates.keys();
    List modules = new ArrayList();

    // parse all modules stored before by calls to addProperty
    while (locations.hasMoreElements()) {
      String loc = locations.nextElement().toString();
      String template = mod_templates.getProperty(loc);

      if (!Utils.isNullOrEmpty(template)) {
        String argument = mod_args.getProperty(loc);

        if (Utils.isNullOrEmpty(argument)) {
          argument = EMPTY;
        }

        String params = mod_params.getProperty(loc);
        modules.add(
            ModuleDescriptor.LOCATION_ID + '=' + loc + ',' +
            ModuleDescriptor.TEMPLATE_ID + '=' + template + ',' +
            ModuleDescriptor.ARGUMENT_ID + '=' + argument +
            (Utils.isNullOrEmpty(params) ? "" : ',' + params)
        );
      }
    }

    if (modules.size() > 0) { // we have modules, so create the html attribute
      sb.append(' ').append(MODULES_PARAM).append("=\"").append
        (Utils.generateList(modules, ";")).append('"');
    }

    if (Utils.checkAddress(email)) { // we have an e-mail address
      sb.append(' ').append(EMAIL_PARAM).append("=\"").append(email).append('"');
    }

    sb.append(">\n<head>\n<title>");
    sb.append(Utils.noNull(title));
    sb.append("</title>\n");

    if (!contentType && charset != null) {
      sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html;");
      sb.append(" charset=").append(charset).append("\" />");
    }

    sb.append(Utils.noNull(head));
    sb.append("\n</head>\n<body");
    sb.append(bodyTag);
    sb.append(">\n");
    sb.append(Utils.noNull(body));
    sb.append("\n</body>\n</html>\n");

    return sb.toString();
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public String getCharset() {
    return charset;
  }
}