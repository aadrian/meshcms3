/*
 * Copyright 2004-2008 Luciano Vernaschi
 *
 * This file is part of MeshCMS.
 *
 * MeshCMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MeshCMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.meshcms.taglib;

import java.io.IOException;
import org.meshcms.core.WebUtils;

/**
 * Writes the path of the theme folder. Often used to access files included in
 * that folder.
 */
public final class ThemePath extends AbstractTag {
  public void writeTag() throws IOException {
    getOut().write(WebUtils.getFullThemeFolder(request));
  }
}
