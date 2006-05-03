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
import java.util.*;
import javax.servlet.jsp.*;
import com.cromoteca.meshcms.*;
import com.cromoteca.util.*;
import com.opensymphony.module.sitemesh.*;

/**
 * Writes the page body or the main part of the page editor.
 */
public class PageBody extends AbstractTag {
  public void writeTag() throws IOException {
    String body = getPage().getBody();
    
    // Let's prevent caching of pages with a "small body"
    if (body.length() < 128) {
      WebUtils.setBlockCache(request);
    }
    
    getOut().write(body);
  }
  
  public void writeEditTag() throws IOException {
    UserInfo userInfo = (UserInfo) pageContext.getAttribute("userInfo",
      PageContext.SESSION_SCOPE);
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = ResourceBundle.getBundle("com/cromoteca/meshcms/Locales", locale);

    Writer w = getOut();

    w.write("<table cellspacing='0' class='meshcmseditor'>\n");
    w.write("<tr><td align='right'>" + webApp.helpIcon(cp, HELP_ANCHOR_EDIT_PAGE, userInfo) + "</td></tr>\n");
    w.write("<tr><th>" + bundle.getString("editorPageTitle") + "</th></tr>\n");
    w.write("<tr><td><input type='text' name='pagetitle' value=\"" +
      Utils.noNull(getPage().getTitle()) + 
      "\" style='width: 100%;' /></td></tr>\n");
    
    w.write("<tr><th><img src=\"" + afp +
      "/images/tree_plus.gif\" id='togglehead' onclick='javascript:editor_toggleHeadEditor();' />\n");
    w.write(bundle.getString("editorPageHead") + "</th></tr>\n");
    w.write("<tr><td><textarea id='meshcmshead' name='meshcmshead' style='height: 5em; width: 100%; display: none;'>" +
      Utils.noNull(((HTMLPage) getPage()).getHead()) + "</textarea></td></tr>\n");

    w.write("<tr><th>" + bundle.getString("editorPageBody") + "</th></tr>\n");
    w.write("<tr><td><textarea id='meshcmsbody' name='meshcmsbody' style='height: 30em; width: 100%;'>");
    w.write(Utils.encodeHTML(getPage().getBody()));
    w.write("</textarea></td></tr>\n");
    w.write("<tr><td align='center'><input type='checkbox' checked='checked'\n");
    w.write(" onclick=\"javascript:tinyMCE.settings['relative_urls']=this.checked;\" />\n");
    w.write(" " + bundle.getString("editorRelative") + "</td></tr>\n");

    w.write(" <tr><th><input type='submit' value='" +
        bundle.getString("genericSave") + "' /></th></tr>\n");
    w.write("</table>");
  }
}
