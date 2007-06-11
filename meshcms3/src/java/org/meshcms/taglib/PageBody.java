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
import java.util.regex.*;
import javax.servlet.jsp.*;
import org.meshcms.core.*;
import org.meshcms.util.*;
import org.meshcms.webui.*;
import com.opensymphony.module.sitemesh.*;

/**
 * Writes the page body or the main part of the page editor.
 */
public class PageBody extends AbstractTag {
  public static final int MINIMUM_PAGE_SIZE = 32;
  public void writeTag() throws IOException {
    String body = getPage().getBody();
    
    // Let's prevent caching of pages with a "small body"
    if (body.length() < MINIMUM_PAGE_SIZE) {
      WebUtils.setBlockCache(request);
    }
    
    if (webSite.getConfiguration().isReplaceThumbnails()) {
      body = replaceThumbnails(body);
    }
    
    getOut().write(body);
  }
  
  public void writeEditTag() throws IOException {
    UserInfo userInfo = (UserInfo) pageContext.getAttribute("userInfo",
        PageContext.SESSION_SCOPE);
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);
    
    Writer w = getOut();
    
    w.write("<div align='right'>" + Help.icon(webSite, cp, Help.EDIT_PAGE, userInfo) + "</div>\n");
    
    w.write("<fieldset class='meshcmseditor'>\n");
    w.write("<legend>" + bundle.getString("editorMainSection") + "</legend>\n");
    w.write("<div class='meshcmsfieldlabel'><label for='pagetitle'>" +
        bundle.getString("editorPageTitle") + "</label></div>\n");
    w.write("<div class='meshcmsfield'><input type='text' id='pagetitle' name='pagetitle' value=\"" +
        Utils.noNull(getPage().getTitle()) +
        "\" style='width: 80%;' /></div>\n");
    
    w.write("<div class='meshcmsfieldlabel'><img alt=\"\" src=\"" + afp +
        "/filemanager/images/bullet_toggle_plus.png\" id='togglehead' onclick=\"javascript:editor_toggleHideShow('meshcmshead','togglehead');\" />\n");
    w.write("<label for='meshcmshead'>" + bundle.getString("editorPageHead") + "</label></div>\n");
    w.write("<div class='meshcmsfield'><textarea id='meshcmshead' name='meshcmshead' rows='5' cols='80' style='height: 5em; width: 100%; display: none;'>" +
        Utils.noNull(((HTMLPage) getPage()).getHead()) + "</textarea></div>\n");
    
    w.write("<div class='meshcmsfieldlabel'><label for='meshcmsbody'>" +
        bundle.getString("editorPageBody") + "</label></div>\n");
    w.write("<div class='meshcmsfield'><textarea id='meshcmsbody' name='meshcmsbody' rows='25' cols='80' style='height: 30em; width: 100%;'>");
    w.write(Utils.encodeHTML(getPage().getBody(), true));
    w.write("</textarea></div>\n");
    w.write("<div class='meshcmsfield'><input type='checkbox' checked='checked' id='relch' name='relch' value='true' \n");
    w.write(" onclick=\"javascript:tinyMCE.settings['relative_urls']=this.checked;\" />\n");
    w.write(" <label for='relch'>" + bundle.getString("editorRelative") + "</label></div>\n");
    
    w.write("<div class='meshcmsbuttons'><input type='submit' value='" +
        bundle.getString("genericSave") + "' /></div>\n");
    w.write("</fieldset>");
  }
  
  private String replaceThumbnails(String body) {
    ResizedThumbnail thumbMaker = new ResizedThumbnail();
    thumbMaker.setHighQuality(webSite.getConfiguration().isHighQualityThumbnails());
    thumbMaker.setMode(ResizedThumbnail.MODE_STRETCH);
    Pattern whPattern = Pattern.compile
        ("width\\s*:\\s*(\\d+)\\s*px|width\\s*=[\"'](\\d+)[\"']|height\\s*:\\s*(\\d+)\\s*px|height\\s*=[\"'](\\d+)[\"']");
    Pattern srcPattern = Pattern.compile("src\\s*=\\s*([\"'])(.*?)\\1");
    Pattern imgPattern = Pattern.compile("<img[^>]*>");
    Matcher imgMatcher = imgPattern.matcher(body);
    StringBuffer sb = null;
    
    while (imgMatcher.find()) {
      if (sb == null) {
        sb = new StringBuffer(body.length());
      }
      
      String imgTag = imgMatcher.group();
      Matcher whMatcher = whPattern.matcher(imgTag);
      int styleWidth = 0;
      int attrWidth = 0;
      int styleHeight = 0;
      int attrHeight = 0;
      
      while (whMatcher.find()) {
        styleWidth = Utils.parseInt(whMatcher.group(1), styleWidth);
        attrWidth = Utils.parseInt(whMatcher.group(2), attrWidth);
        styleHeight = Utils.parseInt(whMatcher.group(3), styleHeight);
        attrHeight = Utils.parseInt(whMatcher.group(4), attrHeight);
      }
      
      int w = (styleWidth < 1) ? attrWidth : styleWidth;
      int h = (styleHeight < 1) ? attrHeight : styleHeight;
      
      if (w > 0 || h > 0) {
        Matcher srcMatcher = srcPattern.matcher(imgTag);
        
        if (srcMatcher.find()) {
          String imgURL = srcMatcher.group(2).trim();
          
          if (imgURL.indexOf("://") < 0) {
            Path imgPath = null;
            
            if (imgURL.startsWith("/")) {
              if (cp.length() > 0 && imgURL.startsWith(cp)) {
                imgURL = imgURL.substring(cp.length());
                imgPath = new Path(imgURL);
              }
            } else {
              imgPath = new Path(webSite.getDirectory(pagePath), imgURL);
            }
            
            if (imgPath != null) {
              thumbMaker.setWidth(w);
              thumbMaker.setHeight(h);
              String thumbName = thumbMaker.getSuggestedFileName();
              Path thumbPath = thumbMaker.checkAndCreate(webSite, imgPath, thumbName);
              
              if (thumbPath != null) {
                String newImgTag = srcMatcher.replaceAll("src=\"" + cp + '/' + thumbPath + "\"");
                imgMatcher.appendReplacement(sb, newImgTag);
              }
            }
          }
        }
      }
    }
    
    if (sb != null) {
      imgMatcher.appendTail(sb);
      body = sb.toString();
    }
    
    return body;
  }
}
