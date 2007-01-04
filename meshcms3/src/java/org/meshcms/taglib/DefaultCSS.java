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

package org.meshcms.taglib;

import java.io.*;
import org.meshcms.core.*;

/**
 * Writes the appropriate CSS tags.
 */
public final class DefaultCSS extends AbstractTag {
  public void writeTag() throws IOException {
    Writer w = getOut();
    w.write("<link type=\"text/css\" rel=\"stylesheet\" href=\"" +
        WebUtils.getFullMeshCSS(webSite, request) + "\" />");
    w.write("<link type=\"text/css\" rel=\"stylesheet\" href=\"" +
        WebUtils.getFullThemeCSS(request) + "\" />\n");
  }
}
