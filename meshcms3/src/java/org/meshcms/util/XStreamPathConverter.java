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

package org.meshcms.util;

import com.thoughtworks.xstream.converters.basic.*;

/**
 * Allows to save instances of {@link Path} using XStream.
 */
public class XStreamPathConverter extends AbstractSingleValueConverter {
  private boolean prependSlash;

  public Object fromString(String string) {
    return new Path(string);
  }

  public boolean canConvert(Class aClass) {
    return aClass.equals(Path.class);
  }

  public String toString(Object obj) {
    return prependSlash ? ((Path) obj).getAsLink() : obj.toString();
  }

  /**
   * Returns the current type of string (with or without prepended slash).
   */
  public boolean isPrependSlash() {
    return prependSlash;
  }

  /**
   * Defines the type of string that will be used to save (with or without
   * prepended slash).
   *
   * @param prependSlash if to prepend or not the slah
   */
  public void setPrependSlash(boolean prependSlash) {
    this.prependSlash = prependSlash;
  }
}
