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
import java.text.*;
import java.util.*;
import org.meshcms.core.*;
import org.meshcms.util.*;
import com.opensymphony.module.sitemesh.*;

/**
 * Writes the page title. Please note that since this tag is used within the
 * &lt;head&gt; tag, the field to edit the page title are displayed by
 * {@link PageBody}
 */
public class PageTitle extends AbstractTag {
  private String defaultTitle = "&nbsp;";

  public void setDefault(String defaultTitle) {
    this.defaultTitle = Utils.noNull(defaultTitle);
  }

  public String getDefault() {
    return defaultTitle;
  }

  public String getTitle() {
    String title = null;
    Page page = getPage();

    if (page != null) {
      title = page.getTitle();
    }

    if (Utils.isNullOrEmpty(title)) {
      title = defaultTitle;
    }

    return title;
  }

  private String getTitlePrefix() {
  	StringBuilder prefix = new StringBuilder();
  	Configuration configuration = webSite.getConfiguration();
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);
  	if (userInfo != null && !userInfo.isGuest() && configuration.isEnableHiddenPaths() && isHidden) {
  		prefix.append( " "+ bundle.getString("genericHiddenPrefix") );
  	}
  	if (userInfo != null && !userInfo.isGuest() && configuration.isEnableSaveAsDraft() && pageOverride != null) {
  		prefix.append( " "+ bundle.getString("genericDraftPrefix") );
  	}
  	return prefix.toString().trim() +" ";
  }
  
  public void writeTag() throws IOException {
    getOut().write(getTitlePrefix()+getTitle());
  }

  public void writeEditTag() throws IOException {
    Locale locale = WebUtils.getPageLocale(pageContext);
    ResourceBundle bundle = ResourceBundle.getBundle("org/meshcms/webui/Locales", locale);
    MessageFormat formatter = new MessageFormat("", locale);

    Object[] args = { getTitle() };
    formatter.applyPattern(bundle.getString("editorTitle"));
    getOut().write(getTitlePrefix()+formatter.format(args));
  }
}