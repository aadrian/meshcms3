/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2008 Luciano Vernaschi
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

package org.meshcms.util;

import java.util.*;

/**
 * Convenience class to use java.util.Properties to store values that are not
 * strings. All additional getters require a default value, but you can safely
 * use null for non-primitive types.
 */
public class CustomProperties extends Properties {
  public int getProperty(String key, int defaultValue) {
    try {
      defaultValue = Integer.parseInt(getProperty(key));
    } catch (Exception ex) {
      //
    }
    
    return defaultValue;
  }
  
  public void setProperty(String key, int value) {
    setProperty(key, Integer.toString(value));
  }

  public boolean getProperty(String key, boolean defaultValue) {
    try {
      defaultValue = Utils.isTrue(key, true);
    } catch (Exception ex) {
      //
    }
    
    return defaultValue;
  }
  
  public void setProperty(String key, boolean value) {
    setProperty(key, Boolean.toString(value));
  }

  public String[] getProperty(String key, String[] defaultValue, char separator) {
    String value = getProperty(key);
    
    if (value != null) {
      defaultValue = Utils.tokenize(value, Character.toString(separator));
    }
    
    return defaultValue;
  }
  
  public void setProperty(String key, String[] value, char separator) {
    setProperty(key, Utils.generateList(value, Character.toString(separator)));
  }
}
