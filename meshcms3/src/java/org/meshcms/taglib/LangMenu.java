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
import java.util.*;
import javax.servlet.jsp.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

public class LangMenu extends AbstractTag {
  private static ResourceBundle flagsBundle;
  
  private String separator = " ";
  private String pre;
  private String post;
  private String flags;
  private String names = "true";
  
  public void setSeparator(String separator) {
    if (separator != null) {
      this.separator = separator;
    }
  }
  
  public String getSeparator() {
    return separator;
  }
  
  public void writeTag() throws IOException, JspException {
    SiteMap siteMap = webSite.getSiteMap();
    
    boolean translatable =
        !(pagePath.isRoot() || siteMap.getPageInfo(pagePath) == null);
    
    List langList = siteMap.getLangList();
    
    if (langList.size() > 1) {
      Iterator iter = langList.iterator();
      boolean putSeparator = false;
      Writer w = getOut();
      
      if (pre != null) {
        w.write(pre);
      }
      
      Path baseIconPath = Utils.isTrue(flags) ? webSite.getAdminPath().add("flags") : null;
      
      while (iter.hasNext()) {
        if (putSeparator) {
          w.write(separator);
        }
        
        putSeparator = true;
        SiteMap.CodeLocalePair lang = (SiteMap.CodeLocalePair) iter.next();
        String langCode = lang.getCode();
        String localeName = Utils.encodeHTML(lang.getName());
        String link = null;
        String msg = null;
        
        if (translatable && !langCode.equalsIgnoreCase(pagePath.getElementAt(0))) {
          Path path = siteMap.getServedPath(pagePath.replace(0, langCode));
          
          if (!webSite.getFile(path).isFile()) {
            if (userInfo != null && userInfo.canWrite(webSite, path)) {
              PageInfo ppi = siteMap.getParentPageInfo(pagePath);
              
              if (ppi != null && ppi.getLevel() > 0) {
                Path pPath = ppi.getPath().replace(0, langCode);
                
                if (siteMap.getPageInfo(pPath) != null) {
                  if (msg == null) {
                    ResourceBundle bundle =
                        ResourceBundle.getBundle("org/meshcms/webui/Locales",
                        WebUtils.getPageLocale(pageContext));
                    msg = Utils.replace(bundle.getString("confirmTranslation"),
                        '\'', "\\'");
                  }
                  link = "javascript:if (confirm('" + msg +"')) location.href='" +
                      afp + "/createpage.jsp?popup=false&newdir=false&fullpath=" +
                      path + "';";
                }
              }
            }
            
            if (link == null) {
              path = new Path(langCode);
            }
          }
          
          if (link == null) {
            link = cp + webSite.getLink(path);
          }
        }
        
        if (link != null) {
          w.write("<a href=\"" + link + "\">");
        }
        
        if (Utils.isTrue(flags)) {
          if (flagsBundle == null) {
            flagsBundle = ResourceBundle.getBundle("org/meshcms/webui/Flags");
          }
          
          try {
            Path iconPath = baseIconPath.add(flagsBundle.getString(langCode) + ".png");
            w.write("<img src='" + cp + webSite.getLink(iconPath) +
                "' alt='" + localeName + "' title='" + localeName + "'/>");
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          
          if (Utils.isTrue(names)) {
            w.write(" ");
          }
        }
        
        if (Utils.isTrue(names)) {
          w.write(localeName);
        }
        
        if (link != null) {
          w.write("</a>");
        }
      }
      
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
  
  public String isFlags() {
    return flags;
  }
  
  public void setFlags(String flags) {
    this.flags = flags;
  }

  public String getNames() {
    return names;
  }

  public void setNames(String names) {
    this.names = names;
  }
}
