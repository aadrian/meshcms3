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

package com.cromoteca.meshcms;

import java.util.*;
import com.cromoteca.util.*;

/**
 * Stores the description of a module when read from the page.
 */
public class ModuleDescriptor {
  public static final String LOCATION_ID = "m_loc";
  public static final String ARGUMENT_ID = "m_arg";
  public static final String TEMPLATE_ID = "m_tpl";
  public static final String PARAMETERS_ID = "m_apm";
  
  private String location;
  private String template;
  private String argument;
  private Properties advancedParams;
  
  /**
   * Creates a new empty instance.
   */
  public ModuleDescriptor() {
    //
  }
  
  /**
   * Creates a new instance and calls {@link #init}.
   */
  public ModuleDescriptor(String data) {
    init(data);
  }

  /**
   * Parses the given String to get location, template and argument.
   */
  public void init(String data) {
    if (data.indexOf(LOCATION_ID) < 0) { // old module definition
      String[] values = Utils.tokenize(data, ":");

      if (values != null) {
        switch (values.length) {
          case 1: // path only
            setLocation("");
            setTemplate("include.jsp");
            setArgument(values[0]);
            break;
          case 2: // template and path
            setLocation("");
            setTemplate(values[0]);
            setArgument(values[1]);
            break;
          case 3: // location, template and path
            setLocation(values[0]);
            setTemplate(values[1]);
            setArgument(values[2]);
        }
      }
    } else { // new module definition
      parseParameters(data);
    }
  }

  public void parseParameters(String data) {
    advancedParams = new Properties();
    StringTokenizer st = new StringTokenizer(data, ",");
    String value, param;

    while (st.hasMoreTokens()) {
      value = st.nextToken().trim();
      int eqIdx = value.indexOf('=');

      if (eqIdx != -1) {
        param = value.substring(0, eqIdx).trim();
        value = value.substring(eqIdx + 1).trim();

        if (param.equals(LOCATION_ID)) {
          setLocation(value);
        } else if (param.equals(TEMPLATE_ID)) {
          setTemplate(value);
        } else if (param.equals(ARGUMENT_ID)) {
          setArgument(value);
        } else {
          advancedParams.setProperty(param, value);
        }
      }
    }
  }

  /**
   * Checks if this module descriptor has been initialized correctly. This is
   * true if both location and template are not null.
   */
  public boolean isValid() {
    return location != null && template != null;
  }

  /**
   * Returns the name of the module location.
   *
   * @see #setLocation
   */
  public String getLocation() {
    return location;
  }

  /**
   * Sets the name of the module location. Each in a page needs a
   * location name which is unique within the page itself.
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Returns the name of the module template.
   */
  public String getTemplate() {
    return template;
  }

  /**
   * Sets the name of the module template.
   */
  public void setTemplate(String template) {
    this.template = template.endsWith(".jsp") ?
        // old modules were in the form module_name.jsp
        template.substring(0, template.length() - 4) : template;
  }

  /**
   * Returns the name of the module argument.
   *
   * @see #setArgument(String)
   */
  public String getArgument() {
    return argument;
  }

  /**
   * Sets the name of the module argument. The argument is set to null if that
   * String is equal to {@link Finals#EMPTY}.
   */
  public void setArgument(String s) {
    argument = Finals.EMPTY.equals(s) ? null : s;
  }
  
  public Properties getAdvancedParams() {
    return advancedParams;
  }

  public void setAdvancedParams(Properties advancedParams) {
    this.advancedParams = advancedParams;
  }
}
