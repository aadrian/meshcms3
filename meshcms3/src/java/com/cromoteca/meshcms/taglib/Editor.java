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
import com.cromoteca.meshcms.*;
import com.opensymphony.module.sitemesh.parser.*;

/**
 * This tag must include all others in a theme file. This is required to enclose
 * all other tags in an HTML form while editing the page. Usually this tag is
 * the first child of <code>&lt;body&gt;</code>:
 *
 * <pre> &lt;body&gt;&lt;cms:editor&gt;
 *     <em>... (html and other MeshCMS tags) ...</em>
 * &lt;/cms:editor&gt;&lt;/body&gt;</pre>
 *
 * <em>Important note:</em> since a form can't be enclosed in another form, you
 * must surround any other form in your theme with a &lt;cms:ifnotediting&gt;
 * tag:
 *
 * <pre> &lt;body&gt;&lt;cms:editor&gt;
 *     <em>... (html and other MeshCMS tags) ...</em>
 *         &lt;cms:ifnotediting&gt;&lt;form action='youraction'&gt;
 *             <em>... your form ...</em>
 *         &lt;/form&gt;&lt;/cms:ifnotediting&gt;
 *     <em>... (html and other MeshCMS tags) ...</em>
 * &lt;/cms:editor&gt;&lt;/body&gt;</pre>
 *
 * This way your form won't be displayed while editing.
 */
public class Editor extends AbstractTag {
  public int doEndTag() {
    if (isEdit) {
      try {
        getOut().write("</form>");
      } catch (IOException ex) {
        pageContext.getServletContext().log("Can't write", ex);
      }
    }
    
    return EVAL_PAGE;
  }

  public void writeTag() throws IOException {
    // nothing to do here
  }
  
  public void writeEditTag() throws IOException {
    Writer w = getOut();
    w.write("<form id='editor' name='editor' action=\"" + afp +
      "/savepage.jsp\" method='POST' " +
      "onsubmit='javascript:fixPageTitle();'>\n");

    FastPage fastPage = (FastPage) getPage();
    String[] keys = fastPage.getPropertyKeys();

    for (int i = 0; i < keys.length; i++) {
      if (!keys[i].equals(PageReconstructor.EMAIL_PARAM) &&
          !keys[i].equals(PageReconstructor.MODULES_PARAM) &&
          !keys[i].equals("title")) {
        w.write("<input type='hidden' name='" + keys[i] + "' value=\"" + 
            fastPage.getProperty(keys[i]) + "\" />\n");
      }
    }

    w.write("<input type='hidden' name='pagepath' value=\"" + 
      pagePath + "\" />");
  }

  public int getStartTagReturnValue() {
    return EVAL_BODY_INCLUDE;
  }
}
