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

import com.cromoteca.util.*;

/**
 * Stores the description of a module when read from the page.
 */
public class ModuleDescriptor implements Finals {
  private String location;
  private String template;
  private Path argumentPath;
  
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
    String[] values = Utils.tokenize(data, ":");

    if (values != null) {
      switch (values.length) {
        case 1: // path only
          setLocation("");
          setTemplate("include.jsp");
          setArgumentPath(values[0]);
          break;
        case 2: // template and path
          setLocation("");
          setTemplate(values[0]);
          setArgumentPath(values[1]);
          break;
        case 3: // location, template and path
          setLocation(values[0]);
          setTemplate(values[1]);
          setArgumentPath(values[2]);
      }
    }
  }
  
  /**
   * Checks if this module descriptor has been initialized correctly. This is
   * true if both location adn template are not null.
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
    if (!template.endsWith(".jsp")) {
      template += ".jsp";
    }
    
    this.template = template;
  }

  /**
   * Returns the name of the module argument.
   *
   * @see #setArgumentPath(Path)
   */
  public Path getArgumentPath() {
    return argumentPath;
  }

  /**
   * Sets the name of the module argument. The module argument is the (optional)
   * path of the file/folder the template is applied to. If the module argument
   * is empty, some templates use the folder that contains the page, while
   * others do nothing.
   */
  public void setArgumentPath(Path path) {
    this.argumentPath = path;
  }
  
  /**
   * Sets the name of the module argument by parsing the given String. The
   * argument is set to null if that String is equal to
   * {@link Finals#EMPTY}, otherwise a new <code>Path</code> is built from
   * the String.
   */
  public void setArgumentPath(String s) {
    argumentPath = EMPTY.equals(s) ? null : new Path(s);
  }
}
