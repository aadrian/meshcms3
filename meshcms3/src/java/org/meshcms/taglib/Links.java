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
import java.util.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Writes a list of links related to the current page.
 */
public final class Links extends AbstractTag {
  private String separator = " ";
  private String style;
  private String target;
  private String current;
  private String pre;
  private String post;
  private String path;
  private String welcome = "true";
  
  public void setSeparator(String separator) {
    if (separator != null) {
      this.separator = separator;
    }
  }
  
  public void setStyle(String style) {
    this.style = style;
  }
  
  public void setTarget(String target) {
    this.target = target;
  }
  
  public void setPre(String pre) {
    this.pre = pre;
  }
  
  public void setPost(String post) {
    this.post = post;
  }
  
  public void writeTag() throws IOException {
    Path rootPath = (path == null) ? pagePath : new Path(path);
    List list = webSite.getSiteMap().getPagesInDirectory(rootPath,
        Utils.isTrue(welcome));
    
    /*if (Utils.isTrue(current)) {
      if (list == null) {
        list = new ArrayList();
      }
     
      list.add(0, webSite.getSiteMap().getPageInfo(pagePath));
    }*/
    
    if (list != null) {
      if (!Utils.isTrue(current)) {
        PageInfo pageInfo = webSite.getSiteMap().getPageInfo(pagePath);
        
        for (int i = 0; i < list.size(); i++) {
          PageInfo pi = (PageInfo) list.get(i);
          
          if (pi.equals(pageInfo)) {
            list.remove(i--);
          }
        }
      }
      
      PageInfo[] pages = (PageInfo[]) list.toArray(new PageInfo[list.size()]);
      String[] outs = webSite.getLinkList(pages, pageDirPath, target, style);
      Writer w = getOut();
      
      if (outs != null && outs.length > 0) {
        if (pre != null) {
          w.write(pre);
        }
        
        w.write(Utils.generateList(outs, separator));
        
        if (post != null) {
          w.write(post);
        }
      }
    }
  }
  
  public String getSeparator() {
    return separator;
  }
  
  public String getStyle() {
    return style;
  }
  
  public String getTarget() {
    return target;
  }
  
  public String getCurrent() {
    return current;
  }
  
  public void setCurrent(String current) {
    this.current = current;
  }
  
  public String getPre() {
    return pre;
  }
  
  public String getPost() {
    return post;
  }
  
  public String getPath() {
    return path;
  }
  
  public void setPath(String path) {
    this.path = path;
  }

  public String getWelcome() {
    return welcome;
  }

  public void setWelcome(String welcome) {
    this.welcome = welcome;
  }
}
