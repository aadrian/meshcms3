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

package com.cromoteca.meshcms;

import java.io.*;
import com.cromoteca.util.*;

/**
 * Used internally to create the JavaScript code needed by xTree to display
 * the folder tree. xTree has been created by <a href="http://webfx.eae.net/">WebFX</a>.
 */
public class FolderXTree extends DirectoryParser implements Finals {
  WebApp webApp;
  UserInfo userInfo;
  Writer writer;
  String thumbsParam;
  String rootName;
  
  /**
   * Creates a new instance.
   *
   * @param writer used to write down the needed JavaScript code.
   */
  public FolderXTree(WebApp webApp, UserInfo userInfo, Writer writer,
      String thumbsParam, String rootName) {
    this.webApp = webApp;
    this.userInfo = userInfo;
    this.writer = writer;
    this.thumbsParam = thumbsParam;
    this.rootName = rootName;
    setRecursive(true);
    setSorted(true);
    setInitialDir(webApp.getContextRoot());
    setDaemon(true);
    setName("XTree builder");
  }

  protected boolean preProcess() {
    try {
      writer.write("var folder0 = new WebFXTree('" + rootName + "');\n");
      writer.write("folder0.setBehavior('explorer');\n");
      writer.write("folder0.action='showlist.jsp?folder=" + thumbsParam + "';\n");
      writer.write("folder0.target='listframe';\n");
      writer.write("folder0.icon='images/world.gif';\n");
      writer.write("folder0.openIcon='images/world.gif';\n");
    } catch (IOException ex) {
      return false;
    }
    
    return true;
  }

  protected void processFile(File file, Path path) {
    // nothing to do here
  }

  protected boolean processDirectory(File file, Path path) {
    if (path.equals(REPOSITORY_PATH)) {
      return false;
    }
    
    boolean include = true;

    try {
      String imgType = null;

      if (webApp.isSystem(path, true)) {
        imgType = "system";
        include = userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS);
      } else if (path.isContainedIn(webApp.getGeneratedFilesPath())) {
        imgType = "themes";
        include = userInfo.canDo(UserInfo.CAN_DO_ADMINTASKS);
      } else if (path.isContainedIn(webApp.getCMSPath())) {
        imgType = "themes";
      }

      if (include) {
        int code = WebUtils.getMenuCode(path);
          writer.write("\nvar folder" + code + " = new WebFXTreeItem(\"" +
              path.getLastElement() + "\");\n");
          writer.write("folder" + WebUtils.getMenuCode(path.getParent()) +
              ".add(folder" + code + ");\n");
          writer.write("folder" + code + ".action=\"showlist.jsp?folder=" +
              path + thumbsParam + "\";\n");
          writer.write("folder" + code + ".target='listframe';\n");
        if (imgType != null) {
          writer.write("folder" + code + ".icon='images/" + imgType + 
              ".gif';\n");
          writer.write("folder" + code + ".openIcon='images/" + imgType + 
              "open.gif';\n");
        }
      }
    } catch (IOException ex) {
      return false;
    }
    
    return include;
  }
}
