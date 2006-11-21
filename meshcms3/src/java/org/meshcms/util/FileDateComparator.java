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

package org.meshcms.util;

import java.io.*;
import java.util.*;

/**
 * Sorts files by date (newest files first).
 *
 * @author Luciano Vernaschi
 */
public final class FileDateComparator implements Comparator {

  private final boolean forwards;

  public FileDateComparator() {
    forwards = true;
  }

  public FileDateComparator(boolean forwards) {
    this.forwards = forwards;
  }

  public int compare(Object o1, Object o2) {
    try {
      long f1 = ((File) o1).lastModified();
      long f2 = ((File) o2).lastModified();
      if (forwards) {
        if (f1 > f2) {
          return -1;
        } else if (f1 < f2) {
          return 1;
        }
      } else {
        if (f2 > f1) {
          return -1;
        } else if (f2 < f1) {
          return 1;
        }
      }
    } catch (ClassCastException ex) {}
    return 0;
  }
}
