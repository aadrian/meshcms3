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
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import com.cromoteca.meshcms.*;
import com.cromoteca.util.*;
import com.opensymphony.module.sitemesh.*;
import com.opensymphony.module.sitemesh.util.*;

/**
 * This class works as a base for all tags of the MeshCMS custom tags library.
 */
public abstract class AbstractTag extends TagSupport 
                                  implements RequestConstants, Finals {
  WebApp webApp;
  HttpServletRequest request;
  Path pagePath;
  UserInfo userInfo;
  String cp, ap, afp;
  boolean isEdit;

  /**
   * Initializes some variables, then executes <code>writeTag()</code> if the
   * page is being viewed or <code>writeEditTag()</code> if it is being edited.
   *
   * @return the value returned by {@link #getStartTagReturnValue}
   */
  public int doStartTag() throws JspException {
    webApp = (WebApp) pageContext.getAttribute("webApp",
        PageContext.APPLICATION_SCOPE);
    request = (HttpServletRequest) pageContext.getRequest();
    pagePath = new Path(request.getServletPath());
    userInfo = (UserInfo)
        pageContext.getAttribute("userInfo", PageContext.SESSION_SCOPE);
    cp = request.getContextPath();
    ap = "/" + webApp.getAdminPath();
    afp = cp + ap;
    isEdit = ACTION_EDIT.equals(request.getParameter(ACTION_NAME)) &&
             userInfo != null && userInfo.canWrite(webApp, pagePath);

    try {
      if (isEdit) {
        writeEditTag();
      } else {
        writeTag();
      }
    } catch (IOException ex) {
      throw new JspException(ex);
      // pageContext.getServletContext().log("Can't write", ex);
    }

    return getStartTagReturnValue();
  }
  
  /**
   * Defines the return value of <code>doStartTag()</code>. This method can be
   * overridden by subclasses to change that value. The default implementation
   * returns SKIP_BODY.
   *
   * @see #doStartTag
   */
  public int getStartTagReturnValue() {
    return SKIP_BODY;
  }

  /**
   * Writes the contents of the tag. Subclasses will use this method to write
   * to the page.
   */
  public abstract void writeTag() throws IOException, JspException;

  /**
   * Writes the contents of the tag when the page is being edited. The default
   * implementation calls <code>writeTag()</code>. Subclasses can override it
   * when they behave differently while editing.
   */
  public void writeEditTag() throws IOException, JspException {
    writeTag();
  }

  /**
   * Copied from com.opensymphony.module.sitemesh.taglib.AbstactTag for 
   * compatibility with SiteMesh
   */
  protected Page getPage() {
    Page p = (Page) pageContext.getAttribute(PAGE, PageContext.PAGE_SCOPE);

    if (p == null) {
      p = (Page) pageContext.getAttribute(PAGE, PageContext.REQUEST_SCOPE);
      
      if (p == null) {
        pageContext.removeAttribute(PAGE, PageContext.PAGE_SCOPE);
      } else {
        pageContext.setAttribute(PAGE, p, PageContext.PAGE_SCOPE);
      }
      
      pageContext.removeAttribute(PAGE, PageContext.REQUEST_SCOPE);
    }
    
    if (p == null) {
      // No page? Weird! Better to block the cache at least.
      WebUtils.setBlockCache(request);
    }
    
    return p;
  }

  /**
   * Copied from com.opensymphony.module.sitemesh.taglib.AbstactTag for 
   * compatibility with SiteMesh
   */
  protected Writer getOut() {
    return OutputConverter.getWriter(pageContext.getOut());
  }
  
  ModuleDescriptor getModuleDescriptor(String location, String name) {
    ModuleDescriptor md;
    
    if (name == null) {
      Map pageModules = (Map) pageContext.getAttribute(PAGE_MODULES);

      if (pageModules == null) {
        pageModules = new HashMap();
        String[] modules = Utils.tokenize
            (getPage().getProperty(PageReconstructor.MODULES_PARAM), ";");

        if (modules != null) {
          for (int i = 0; i < modules.length; i++) {
            ModuleDescriptor md0 = new ModuleDescriptor(modules[i]);

            if (md0.isValid()) {
              pageModules.put(md0.getLocation(), md0);
            }
          }
        }

        pageContext.setAttribute(PAGE_MODULES, pageModules, PageContext.PAGE_SCOPE);
      }

      md = (ModuleDescriptor) pageModules.get(location);
    } else {
      md = new ModuleDescriptor(name);
    }
    
    return md;
  }
}
