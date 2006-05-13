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

package com.cromoteca.meshcms.taglib;

import java.io.*;

/**
 * Writes some user data (username, e-mail or user details).
 *
 * @see com.cromoteca.meshcms.UserInfo
 */
public final class User extends AbstractTag {
  public void writeTag() throws IOException {
    String result = null;
    
    if (id != null) {
      id = id.toLowerCase();
      
      if (id.equals("username")) {
        result = userInfo.getUsername();
      } else if (id.equals("email") || id.equals("e-mail")) {
        result = userInfo.getEmail();
      } else {
        result = userInfo.getValue(id);
      }
    }
    
    if (result == null) {
      result = userInfo.getDisplayName();
    }
    
    getOut().write(result);
  }
}
