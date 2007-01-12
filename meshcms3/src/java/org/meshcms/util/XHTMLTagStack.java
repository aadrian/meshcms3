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

public abstract class XHTMLTagStack {
  public static final String A = "a";
  public static final String ABBR = "abbr";
  public static final String ACRONYM = "acronym";
  public static final String ADDRESS = "address";
  public static final String AREA = "area";
  public static final String B = "b";
  public static final String BASE = "base";
  public static final String BDO = "bdo";
  public static final String BIG = "big";
  public static final String BLOCKQUOTE = "blockquote";
  public static final String BODY = "body";
  public static final String BR = "br";
  public static final String BUTTON = "button";
  public static final String CAPTION = "caption";
  public static final String CITE = "cite";
  public static final String CODE = "code";
  public static final String COL = "col";
  public static final String COLGROUP = "colgroup";
  public static final String DD = "dd";
  public static final String DEL = "del";
  public static final String DIV = "div";
  public static final String DFN = "dfn";
  public static final String DL = "dl";
  public static final String DT = "dt";
  public static final String EM = "em";
  public static final String FIELDSET = "fieldset";
  public static final String FORM = "form";
  public static final String FRAME = "frame";
  public static final String FRAMESET = "frameset";
  public static final String H1 = "h1";
  public static final String H2 = "h2";
  public static final String H3 = "h3";
  public static final String H4 = "h4";
  public static final String H5 = "h5";
  public static final String H6 = "h6";
  public static final String HEAD = "head";
  public static final String HR = "hr";
  public static final String HTML = "html";
  public static final String I = "i";
  public static final String IFRAME = "iframe";
  public static final String IMG = "img";
  public static final String INPUT = "input";
  public static final String INS = "ins";
  public static final String KBD = "kbd";
  public static final String LABEL = "label";
  public static final String LEGEND = "legend";
  public static final String LI = "li";
  public static final String LINK = "link";
  public static final String MAP = "map";
  public static final String META = "meta";
  public static final String NOFRAMES = "noframes";
  public static final String NOSCRIPT = "noscript";
  public static final String OBJECT = "object";
  public static final String OL = "ol";
  public static final String OPTGROUP = "optgroup";
  public static final String OPTION = "option";
  public static final String P = "p";
  public static final String PARAM = "param";
  public static final String PRE = "pre";
  public static final String Q = "q";
  public static final String SAMP = "samp";
  public static final String SCRIPT = "script";
  public static final String SELECT = "select";
  public static final String SMALL = "small";
  public static final String SPAN = "span";
  public static final String STRONG = "strong";
  public static final String STYLE = "style";
  public static final String SUB = "sub";
  public static final String SUP = "sup";
  public static final String TABLE = "table";
  public static final String TBODY = "tbody";
  public static final String TD = "td";
  public static final String TEXTAREA = "textarea";
  public static final String TFOOT = "tfoot";
  public static final String TH = "th";
  public static final String THEAD = "thead";
  public static final String TITLE = "title";
  public static final String TR = "tr";
  public static final String TT = "tt";
  public static final String UL = "ul";
  public static final String VAR = "var";
  
  public static final String ATTR_ACCESSKEY = "accesskey";
  public static final String ATTR_CLASS = "class";
  public static final String ATTR_DIR = "dir";
  public static final String ATTR_ID = "id";
  public static final String ATTR_LANG = "lang";
  public static final String ATTR_STYLE = "style";
  public static final String ATTR_TABINDEX = "tabindex";
  public static final String ATTR_TITLE = "title";
  
  protected Stack tagStack;
  
  public XHTMLTagStack() {
    tagStack = new Stack();
  }
  
  public abstract XHTMLTagStack addCDATA(String textData);
  
  public abstract XHTMLTagStack addText(String textData);
  
  public abstract String getCurrentTagName();
  
  public abstract XHTMLTagStack openTag(String tagName);
  
  public abstract XHTMLTagStack setAttribute(String name, String value);
  
  protected abstract void performCloseTag();
  
  public XHTMLTagStack closeTag() {
    return closeTag(null);
  }
  
  public XHTMLTagStack closeTag(String tagName) {
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
    
    performCloseTag();
    return this;
  }
  
  public static void configureTransformer(Transformer t, String charset) {
    t.setOutputProperty(OutputKeys.METHOD, "xml");
    t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    t.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
    t.setOutputProperty(OutputKeys.INDENT, "yes");
    
    if (charset != null) {
      t.setOutputProperty(OutputKeys.ENCODING, charset);
    }
  }
  
  public void test() {
    openTag(SCRIPT); {
      setAttribute("type", "text/javascript");
      addCDATA("alert('ciao');");
    } closeTag();
    openTag(FORM); {
      setAttribute("action", "");
      setAttribute("method", "post");
      openTag(FIELDSET); {
        openTag(LEGEND).addText("Mail Form").closeTag();
        openTag(DIV); {
          setAttribute(ATTR_CLASS, "field");
          openTag(LABEL); {
            setAttribute("for", "msgmail");
            addText("Your Mail:");
          } closeTag();
          openTag(INPUT); {
            setAttribute("type", "text");
            setAttribute(ATTR_ID, "msgmail");
            setAttribute("name", "msgmail");
          } closeTag();
        } closeTag();
        openTag(DIV); {
          setAttribute(ATTR_CLASS, "field");
          openTag(LABEL); {
            setAttribute("for", "msgtext");
            addText("Message Body:");
          } closeTag();
          openTag(TEXTAREA); {
            setAttribute(ATTR_ID, "msgtext");
            setAttribute("name", "msgtext");
            addText("A line of text\n");
            addText("Other text");
          } closeTag();
        } closeTag();
        openTag(DIV).setAttribute(ATTR_CLASS, "buttons"); {
          openTag(INPUT).setAttribute("value", "Send").closeTag();
        } closeTag();
      } closeTag(FIELDSET); // specify tag name just to check if correct
    } closeTag();
  }
}
