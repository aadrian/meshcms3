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
import javax.servlet.*;
import javax.servlet.jsp.*;
import com.cromoteca.meshcms.*;
import com.cromoteca.util.*;

/**
 * Creates a mail form if a recipient has been specified for the page being
 * viewed. If the editor is active, this tag writes the form field needed to
 * specify the recipient address.
 */
public final class MailForm extends AbstractTag {
  public void writeTag() throws IOException, JspException {
    String email = getPage().getProperty(EMAIL_PARAM);

    if (Utils.checkAddress(email)) {
      try {
        pageContext.include(ap + "/" + MODULE_TEMPLATES_DIR +
            "/mail.jsp?recipient=" + email);
      } catch (ServletException ex) {
        throw new JspException(ex);
      }
    } else {
      getOut().write("&nbsp;");
    }
  }
  
  public void writeEditTag() throws IOException, JspException {
    UserInfo userInfo = (UserInfo) pageContext.getAttribute("userInfo",
      PageContext.SESSION_SCOPE);
    Locale locale = Utils.getLocale(userInfo == null ? null :
      userInfo.getPreferredLocaleCode(), request.getLocale());
    ResourceBundle bundle = ResourceBundle.getBundle("com/cromoteca/meshcms/Locales", locale);

    String email = getPage().getProperty(EMAIL_PARAM);

    Writer w = getOut();
    w.write("<table cellspacing='0' class='meshcmseditor'>\n");
    w.write("<tr><th>" + bundle.getString("editorMailTitle") + "</th></tr>\n");
    w.write("<tr><td>" + bundle.getString("editorMail") + " <img src='" + afp +
      "/images/clear_field.gif' onclick=\"javascript:editor_clr('" +
      EMAIL_PARAM + "');\" align='middle' /><input type='text' id='" +
      EMAIL_PARAM + "' name='" + EMAIL_PARAM + "' value=\"" +
      Utils.noNull(email) + "\" style='width: 12em;' /></td></tr>\n");
    w.write("</table>");
  }
}
