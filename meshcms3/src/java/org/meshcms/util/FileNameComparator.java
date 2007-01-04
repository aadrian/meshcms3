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

import java.io.*;
import java.util.*;

/**
 * Sorts files by name (directories before files).
 *
 * @author Luciano Vernaschi
 */
public final class FileNameComparator implements Comparator, Serializable {
  private boolean caseSensitive = true;

  public int compare(Object o1, Object o2) {
    try {
      File f1 = (File) o1;
      File f2 = (File) o2;

      if (f1.isDirectory() && !f2.isDirectory()) {
        return -1;
      } else if (!f1.isDirectory() && f2.isDirectory()) {
        return 1;
      } else if (caseSensitive) {
        return f1.getName().compareTo(f2.getName());
      } else {
        return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
      }
    } catch (ClassCastException ex) {}

    return 0;
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }

  public void setCaseSensitive(boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }
}
