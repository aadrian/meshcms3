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

package org.meshcms.taglib;

import java.io.*;
import org.meshcms.core.*;

/**
 * This tag print the title of the specified module location, if that title
 * has been set when editing the page.
 */
public class ModuleTitle extends AbstractTag {
  private String location = "";
  private String pre;
  private String post;
  
  public void setLocation(String location) {
    this.location = location;
  }
  
  public String getLocation() {
    return location;
  }
  
  public void writeTag() throws IOException {
    String title = null;
    ModuleDescriptor md = getModuleDescriptor(location, null);
    
    if (md != null) {
      title = md.getTitle();
    }
    
    if (title != null) {
      Writer w = getOut();
      
      if (pre != null) {
        w.write(pre);
      }
      
      w.write(title);
      
      if (post != null) {
        w.write(post);
      }
    }
  }
  
  public String getPre() {
    return pre;
  }
  
  public void setPre(String pre) {
    this.pre = pre;
  }
  
  public String getPost() {
    return post;
  }
  
  public void setPost(String post) {
    this.post = post;
  }
}
