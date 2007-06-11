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
import java.text.*;
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
    final String uniqueHash = Integer.toString(new Object().hashCode());
  	final String tagIdPrefix = "meshcmsmodule_mail_"+ uniqueHash +"_";
  	final String idCont = tagIdPrefix +"cont";
  	final String idElem = tagIdPrefix +"elem";
  	final String idIcon = tagIdPrefix +"icon";
  	final boolean isEditorModulesCollapsed = webSite.getConfiguration().isEditorModulesCollapsed();

    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);

    String email = getPage().getProperty(PageAssembler.EMAIL_PARAM);

    Writer w = getOut();

    if (isEditorModulesCollapsed) {
      MessageFormat formatter = new MessageFormat("", locale);
	    w.write("<div id=\""+ idCont +"\" class='meshcmsfieldlabel' " +
	    	" style=\"cursor:pointer;\" onclick=\"javascript:editor_moduleShow('"+ idCont +"','"+ idElem +"','"+ idIcon +"');\">" +
	    	"<img alt=\"\" src=\"" + afp + "/images/tree_plus.gif\" id=\""+ idIcon +"\" />\n");
	    Object[] args = { bundle.getString("editorMailTitle"), email != null ? bundle.getString("editorMailTitle") : bundle.getString("editorNoTemplate"),
	    		Utils.noNull(email), "" };
	    formatter.applyPattern(bundle.getString("editorModuleLocExt"));
	    w.write("<label for=\""+ idElem +"\">"+ formatter.format(args) +"</label>");
	    w.write("</div>");
    }

    w.write("<fieldset  "+ (isEditorModulesCollapsed ? "style=\"display:none;\"" : "") +
    		"class='meshcmseditor' id=\""+ idElem +"\">\n");
    w.write("<legend>" + bundle.getString("editorMailTitle") + "</legend>\n");
    w.write("<div class='meshcmsfieldlabel'>" + bundle.getString("editorMail") + "</div>\n");
    w.write("<div class='meshcmsfield'><input type='text' id='" +
      PageAssembler.EMAIL_PARAM + "' name='" +
      PageAssembler.EMAIL_PARAM + "' value=\"" +
      Utils.noNull(email) + "\" style='width: 80%;' /></div>\n");
    w.write("</fieldset>");
  }
}
