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

import java.io.*;
import java.text.*;
import java.util.*;
import org.meshcms.taglib.*;
import org.meshcms.util.*;

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
  
  private Path pagePath;
  private Path modulePath;
  private String dateFormat;
  private String style;
  
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
      advancedParams = new Properties();

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
   * String is equal to {@link PageAssembler#EMPTY}.
   */
  public void setArgument(String s) {
    argument = PageAssembler.EMPTY.equals(s) ? null : s;
  }
  
  public Properties getAdvancedParams() {
    return advancedParams;
  }
  
  public String getAdvancedParam(String paramName, String defaultValue) {
    return advancedParams.getProperty(paramName, defaultValue);
  }

  public void setAdvancedParams(Properties advancedParams) {
    this.advancedParams = advancedParams;
  }

  public Path getPagePath() {
    return pagePath;
  }

  public void setPagePath(Path pagePath) {
    this.pagePath = pagePath;
  }

  public Path getModulePath() {
    return modulePath;
  }

  public void setModulePath(Path modulePath) {
    this.modulePath = modulePath;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  /**
   * Returns the files to be passed to the module.
   *
   * @param allowCurrentDir if true and the argument parameter is null, the
   * files included in the same folder of the page are returned
   */
  public File[] getModuleFiles(WebSite webSite, boolean allowCurrentDir) {
    Path argPath = getModuleArgumentPath(false);
    
    if (argPath == null && allowCurrentDir) {
      argPath = getModuleArgumentDirectoryPath(webSite, true);
    }

    if (argPath != null && !webSite.isSystem(argPath)) {
      File moduleFile = webSite.getFile(argPath);
      File[] files = null;

      if (moduleFile.isDirectory()) {
        files = moduleFile.listFiles();
      } else if (moduleFile.exists()) {
        files = new File[1];
        files[0] = moduleFile;
      }

      return files;
    }
    
    return null;
  }
  
  public Path getModuleArgumentPath(boolean allowCurrentPath) {
    Path argPath = null;
    
    if (argument != null) {
      argPath = new Path(argument);
    } else if (allowCurrentPath) {
      argPath = pagePath;
    }
    
    return argPath;
  }
  
  /**
   * Returns the folder path to be used as argument for the module.
   *
   * @param allowCurrentPath if true and the argument parameter is null, the
   * path of the page is returned
   */
  public Path getModuleArgumentDirectoryPath(WebSite webSite,
      boolean allowCurrentPath) {
    Path argPath = getModuleArgumentPath(allowCurrentPath);
    return (argPath == null || webSite.isSystem(argPath)) ? null :
        webSite.getDirectory(argPath);
  }
  
  public String getFullCSSAttribute(String paramName) {
    String css = getAdvancedParam(paramName, style);
    return Utils.isNullOrEmpty(css) ? "" : " class=\"" + css + "\"";
  }

  /**
   * Returns format to be used to display the date (can be null).
   */
  public DateFormat getDateFormat(Locale locale, String paramName) {
    String paramValue = getAdvancedParam(paramName, dateFormat);
    DateFormat df = null;

    if (Module.DATE_NORMAL.equals(paramValue)) {
      df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    } else if (Module.DATE_FULL.equals(paramValue)) {
      df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,
          locale);
    }
    
    return df;
  }
}
