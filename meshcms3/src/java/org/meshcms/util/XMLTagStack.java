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
 * along with this program; if not, writeBodyContent to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * You can contact the author at http://www.cromoteca.com
 * and at info@cromoteca.com
 */

package org.meshcms.util;

import java.util.*;
import javax.xml.transform.*;

public abstract class XMLTagStack {
  protected Stack tagStack;
  
  public XMLTagStack() {
    tagStack = new Stack();
  }
  
  public abstract XMLTagStack addCDATA(String textData);
  
  public abstract XMLTagStack addText(String textData);
  
  public abstract String getCurrentTagName();
  
  public abstract XMLTagStack startTag(String tagName);
  
  public abstract XMLTagStack addAttribute(String name, String value);
  
  protected abstract void performEndTag();
  
  public XMLTagStack endTag() {
    return endTag(null);
  }
  
  public XMLTagStack endTag(String tagName) {
    if (tagStack.empty()) {
      throw new IllegalStateException("No tag to close");
    }
    
    if (tagName != null) {
      String currentTagName = getCurrentTagName();
      
      if (!currentTagName.equals(tagName)) {
        throw new IllegalStateException("Current tag is " + currentTagName +
            ", not " + tagName);
      }
    }
    
    performEndTag();
    return this;
  }
  
  public static void configureTransformer(Transformer t, String charset,
      boolean xhtml) {
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    
    if (xhtml) {
      t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
          "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
    }
    
    if (charset != null) {
      t.setOutputProperty(OutputKeys.ENCODING, charset);
    }
  }
}
