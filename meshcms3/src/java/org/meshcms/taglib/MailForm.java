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

package org.meshcms.taglib;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;
import org.meshcms.core.*;
import org.meshcms.util.*;

/**
 * Creates a mail form if a recipient has been specified for the page being
 * viewed. If the editor is active, this tag writes the form field needed to
 * specify the recipient address.
 */
public final class MailForm extends AbstractTag {
  public void writeTag() throws IOException, JspException {
    String email = getPage().getProperty(PageAssembler.EMAIL_PARAM);

    if (Utils.checkAddress(email)) {
      try {
        Path mailModulePath = webSite.getAdminModulesPath().add("mail");
        String location = "meshcmsmailformtag";
        ModuleDescriptor md = new ModuleDescriptor();
        md.setLocation(location);
        md.setArgument(email);
        md.setModulePath(mailModulePath);
        md.setPagePath(pagePath);
        String moduleCode = "meshcmsmodule_" + location;
        request.setAttribute(moduleCode, md);
        pageContext.include("/" + webSite.getServedPath(mailModulePath) + "/" +
            SiteMap.MODULE_INCLUDE_FILE + "?modulecode=" + moduleCode);
      } catch (ServletException ex) {
        throw new JspException(ex);
      }
    } else {
      getOut().write("&nbsp;");
    }
  }

  public void writeEditTag() throws IOException, JspException {
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);

    String email = getPage().getProperty(PageAssembler.EMAIL_PARAM);

    Writer w = getOut();
    w.write("<div class='meshcmseditor'>\n");
    w.write("<div class='meshcmstitle'>" + bundle.getString("editorMailTitle") + "</div>\n");
    w.write("<div class='meshcmsfieldname'>" + bundle.getString("editorMail") + "</div>\n");
    w.write("<div class='meshcmsfield'><img src='" + afp +
      "/images/clear_field.gif' onclick=\"javascript:editor_clr('" +
      PageAssembler.EMAIL_PARAM + "');\" style='vertical-align:middle;' /><input type='text' id='" +
      PageAssembler.EMAIL_PARAM + "' name='" +
      PageAssembler.EMAIL_PARAM + "' value=\"" +
      Utils.noNull(email) + "\" style='width: 12em;' /></div>\n");
    w.write("</div>");
  }
}
