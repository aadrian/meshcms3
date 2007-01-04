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

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

public class XHTMLBuilder {
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

  Document xmlDocument;
  Element headElement;
  Element titleElement;
  Element bodyElement;
  Fragment headFragment;
  Fragment bodyFragment;

  public XHTMLBuilder() {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      DOMImplementation impl = builder.getDOMImplementation();
      xmlDocument = impl.createDocument(null, "html", null);
      Node htmlNode = xmlDocument.getFirstChild();
      headElement = xmlDocument.createElement("head");
      titleElement = xmlDocument.createElement("title");
      bodyElement = xmlDocument.createElement("body");
      titleElement.appendChild(xmlDocument.createTextNode("Generated XHTML Code"));
      headElement.appendChild(titleElement);
      htmlNode.appendChild(headElement);
      htmlNode.appendChild(bodyElement);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    headFragment = new Fragment(headElement);
    bodyFragment = new Fragment(bodyElement);
  }

  public Fragment getHead() {
    return headFragment;
  }

  public Fragment getBody() {
    return bodyFragment;
  }

  public Element getHeadElement() {
    return headElement;
  }

  public Element getTitleElement() {
    return titleElement;
  }

  public Element getBodyElement() {
    return bodyElement;
  }

  public void setTitle(String title) {
    if (title == null) {
      throw new IllegalArgumentException("Provided title is null");
    }

    Node node;

    while((node = titleElement.getLastChild()) != null) {
      titleElement.removeChild(node);
    }

    titleElement.appendChild(xmlDocument.createTextNode(title));
  }

  public void normalize() {
    xmlDocument.normalize();
  }

  public void writeFullDocument(Writer out, String charset) {
    write(xmlDocument, out, charset);
  }

  public void writeBodyContent(Writer out, String charset) {
    Node n = bodyElement.getFirstChild();

    while (n != null) {
      write(n, out, charset);
      n = n.getNextSibling();
    }
  }

  private void write(Node node, Writer out, String charset) {
    try {
      DOMSource domSource = new DOMSource(node);
      StreamResult streamResult = new StreamResult(out);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer serializer = tf.newTransformer();
      serializer.setOutputProperty(OutputKeys.METHOD, "xml");
      serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");

      if (charset != null) {
        serializer.setOutputProperty(OutputKeys.ENCODING, charset);
      }

      serializer.transform(domSource, streamResult);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public class Fragment {
    Element mainElement;
    Stack tagStack;

    public Fragment(Element mainElement) {
      this.mainElement = mainElement;
      tagStack = new Stack();
    }

    public Element getCurrentTag() {
      return tagStack.empty() ? mainElement : (Element) tagStack.peek();
    }

    public Fragment openTag(String tagName) {
      Element tag = xmlDocument.createElement(tagName);
      getCurrentTag().appendChild(tag);
      tagStack.push(tag);
      return this;
    }

    public Fragment setAttribute(String name, String value) {
      getCurrentTag().setAttribute(name, value);
      return this;
    }

    public Fragment addText(String textData) {
      getCurrentTag().appendChild(xmlDocument.createTextNode(textData));
      return this;
    }

    public Fragment addCDATA(String textData) {
      getCurrentTag().appendChild(xmlDocument.createCDATASection(textData));
      return this;
    }

    public Element closeTag() {
      if (tagStack.empty()) {
        throw new IllegalStateException("No tag to close");
      }

      return (Element) tagStack.pop();
    }

    public Element closeTag(String tagName) {
      if (tagStack.empty()) {
        throw new IllegalStateException("No tag to close");
      }

      Element current = (Element) tagStack.peek();
      String currentTagName = current.getTagName();

      if (!currentTagName.equals(tagName)) {
        throw new IllegalStateException("Current tag is " + currentTagName +
            ", not " + tagName);
      }

      return (Element) tagStack.pop();
    }
  }

  public static void main(String[] args) {
    XHTMLBuilder doc = new XHTMLBuilder();
    Fragment f = doc.getBody();

    f.openTag("form"); {
      f.setAttribute("action", "");
      f.setAttribute("method", "post");
      f.openTag("fieldset"); {
        f.openTag("legend").addText("Mail Form").closeTag();
        f.openTag("div"); {
          f.setAttribute("class", "field");
          f.openTag("label"); {
            f.setAttribute("for", "msgmail");
            f.addText("Your Mail:");
          } f.closeTag();
          f.openTag("input"); {
            f.setAttribute("type", "text");
            f.setAttribute("id", "msgmail");
            f.setAttribute("name", "msgmail");
          } f.closeTag();
        } f.closeTag();
        f.openTag("div"); {
          f.setAttribute("class", "field");
          f.openTag("label"); {
            f.setAttribute("for", "msgtext");
            f.addText("Message Body:");
          } f.closeTag();
          f.openTag("textarea"); {
            f.setAttribute("id", "msgtext");
            f.setAttribute("name", "msgtext");
            f.addText("A line of text\n");
            f.addText("Other text");
          } f.closeTag();
        } f.closeTag();
        f.openTag("div").setAttribute("class", "buttons"); {
          f.openTag("input").setAttribute("value", "Send").closeTag();
        } f.closeTag();
      } f.closeTag("fieldset"); // specify tag name just to check if correct
    } f.closeTag();

    doc.writeBodyContent(new PrintWriter(System.out), "UTF-8");
  }
}
