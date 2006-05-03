<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2006 Luciano Vernaschi

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 You can contact the author at http://www.cromoteca.com
 and at info@cromoteca.com
--%>

<%@ page import="java.io.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />
<jsp:useBean id="fileClipboard" scope="session" class="com.cromoteca.meshcms.FileClipboard" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setLocale value="<%= userInfo.getPreferredLocaleCode() %>" scope="request" />
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%
  if (!userInfo.canDo(UserInfo.CAN_BROWSE_FILES)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                       "You don't have enough privileges");
    return;
  }

  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
%>

<html>

<head>
 <%= webApp.getDummyMetaThemeTag() %>
 <title><fmt:message key="homeFile" /></title>
 <link href="../theme/main.css" type="text/css" rel="stylesheet" />

<%
  String cp = request.getContextPath();
  List errMsgs = new ArrayList();
  boolean needsUpdate = false;
  String action = request.getParameter("f_action");
  Path path = new Path(request.getParameter("f_dir"));
  String redirect = null;
  ResourceBundle bundle = WebUtils.getPageResourceBundle(pageContext);
  Locale locale = WebUtils.getPageLocale(pageContext);
  MessageFormat formatter = new MessageFormat("", locale);
  
  if (!Utils.isNullOrEmpty(action)) {
    String[] fileNames = Utils.tokenize(request.getParameter("f_files"), ",");

    if (action.equals("delete")) {
      for (int i = 0; i < fileNames.length; i++) {
        if (webApp.delete(userInfo, path.add(fileNames[i]))) {
          needsUpdate = true;
        } else {
          Object[] args = { fileNames[i] };
          formatter.applyPattern(bundle.getString("fmErrorNotDel"));
          errMsgs.add(formatter.format(args));
        }
      }
    } else if (action.equals("touch")) {
      for (int i = 0; i < fileNames.length; i++) {
        if (webApp.touch(userInfo, path.add(fileNames[i]))) {
          needsUpdate = true;
        } else {
          Object[] args = { fileNames[i] };
          formatter.applyPattern(bundle.getString("fmErrorNotTouched"));
          errMsgs.add(formatter.format(args));
        }
      }
    } else if (action.startsWith("theme")) {
      SiteInfo siteInfo = webApp.getSiteInfo();
      String themeName = (action.length() == 5) ? "" : action.substring(5);
      
      for (int i = 0; i < fileNames.length; i++) {
        Path filePath = path.add(fileNames[i]);
        
        if (webApp.getFileTypes().isPage(fileNames[i]) || 
            webApp.getFile(filePath).isDirectory()) {
          if (userInfo.canWrite(webApp, filePath)) {
            siteInfo.setValue(siteInfo.getThemeCode(filePath), themeName);
            needsUpdate = true;
          } else {
            Object[] args = { fileNames[i] };
            formatter.applyPattern(bundle.getString("fmErrorNoThemePerm"));
            errMsgs.add(formatter.format(args));
          }
        } else {
          Object[] args = { fileNames[i] };
          formatter.applyPattern(bundle.getString("fmErrorNoTheme"));
          errMsgs.add(formatter.format(args));
        }
      }
      
      if (needsUpdate) {
        if (!siteInfo.store()) {
          errMsgs.add(bundle.getString("fmErrorNotSaved"));
        }
      }
    } else if (action.startsWith("rename") && fileNames.length == 1) {
      String name = WebUtils.fixFileName(action.substring(6));

      if (webApp.move(userInfo, path.add(fileNames[0]), path.add(name))) {
        needsUpdate = true;
      } else {
        Object[] args = { fileNames[0], name };
        formatter.applyPattern(bundle.getString("fmErrorNotRenamed"));
        errMsgs.add(formatter.format(args));
      }
    } else if (action.startsWith("copy") && fileNames.length == 1) {
      String name = WebUtils.fixFileName(action.substring(4));

      if (webApp.copyFile(userInfo, path.add(fileNames[0]), name)) {
        needsUpdate = true;
      } else {
        Object[] args = { fileNames[0], name };
        formatter.applyPattern(bundle.getString("fmErrorNotDuplicated"));
        errMsgs.add(formatter.format(args));
      }
    } else if (action.startsWith("createfile")) {
      String name = WebUtils.fixFileName(action.substring(10));

      if (webApp.createFile(userInfo, path.add(name))) {
        needsUpdate = true;
      } else {
        Object[] args = { name };
        formatter.applyPattern(bundle.getString("fmErrorNoNewFile"));
        errMsgs.add(formatter.format(args));
      }
    } else if (action.startsWith("createdir")) {
      String name = WebUtils.fixFileName(action.substring(9));

      if (webApp.createDirectory(userInfo, path.add(name))) {
        needsUpdate = true;
      } else {
        Object[] args = { name };
        formatter.applyPattern(bundle.getString("fmErrorNoNewDir"));
        errMsgs.add(formatter.format(args));
      }
    } else if (action.startsWith("unzip")) {
      File zipFile = webApp.getFile(path.add(fileNames[0]));
      File unzipDir = webApp.getFile(path);
      
      if (action.length() > 5) {
        unzipDir = new File(unzipDir, WebUtils.fixFileName(action.substring(5)));
        unzipDir.mkdir();
      }

      try {
        Utils.unzip(zipFile, unzipDir);
      } catch (IOException ex) {
        errMsgs.add(bundle.getString("fmErrorGeneric"));
      }
      
      needsUpdate = true;
    } else if (action.equals("view")) {
      Path pp = new Path(path, fileNames[0]);

      if (webApp.getFile(pp).isDirectory()) {
        path = pp;
      } else {
        redirect = cp + "/" + pp;
        /* out.println("<script type='text/javascript'>fm_viewPage(\"" + cp + "/" +
                    pp + "\");</script>"); */
      }
    } else if (action.equals("edit")) {
      Path pp = new Path(path, fileNames[0]);

      if (!webApp.getFile(pp).isDirectory()) {
        redirect = cp + "/" + webApp.getAdminPath() + "/editsrc.jsp?path=" + pp;
        /* out.println("<script type=\"text/javascript\">fm_viewPage(\"" + cp + "/" +
                    webApp.getAdminPath() + "/editsrc.jsp?path=" +
                    pp + "\");</script>"); */
      }
    } else if (action.equals("wysiwyg")) {
      Path pp = new Path(path, fileNames[0]);

      if (!webApp.getFile(pp).isDirectory()) {
        if (webApp.isVisuallyEditable(pp)) {
          redirect = cp + "/" + pp + "?" + WebApp.ACTION_NAME + "=" +
              WebApp.ACTION_EDIT;
          /* out.println("<script type='text/javascript'>fm_viewPage(\"" + cp + "/" +
                      pp + "?" + WebApp.ACTION_NAME + "=" +
                      WebApp.ACTION_EDIT + "\");</script>"); */
        } else {
          Object[] args = { fileNames[0] };
          formatter.applyPattern(bundle.getString("fmErrorNoHTML"));
          errMsgs.add(formatter.format(args));
        }
      }
    } else if (action.equals("clipboardcut")) {
      fileClipboard.setContent(path, request.getParameter("f_files"), true);
    } else if (action.equals("clipboardcopy")) {
      fileClipboard.setContent(path, request.getParameter("f_files"), false);
    } else if (action.equals("clipboardpaste")) {
      Path[] paths = fileClipboard.getContent();

      if (paths != null) {
        if (fileClipboard.isCut()) {
          for (int i = 0; i < paths.length; i++) {
            if (webApp.move(userInfo, paths[i], path.add(paths[i].getLastElement()))) {
              needsUpdate = true;
            } else {
              Object[] args = { paths[i].getLastElement(), path.getAsLink() };
              formatter.applyPattern(bundle.getString("fmErrorNotMoved"));
              errMsgs.add(formatter.format(args));
            }
          }

          fileClipboard.clear();
        } else {
          for (int i = 0; i < paths.length; i++) {
            if (webApp.copyFile(userInfo, paths[i], path.add(paths[i].getLastElement()))) {
              needsUpdate = true;
            } else {
              Object[] args = { paths[i].getLastElement(), path.getAsLink() };
              formatter.applyPattern(bundle.getString("fmErrorNotCopied"));
              errMsgs.add(formatter.format(args));
            }
          }
        }
      }
    } else if (action.equals("refresh")) {
      needsUpdate = true;
    }
  }
  
  if (redirect == null) {
    redirect = "index.jsp?folder=" + path + "&thumbnails=" + 
    request.getParameter("s_thumbs") + "&field=" + Utils.noNull(request.getParameter("s_field"));
  }
  
  if (needsUpdate) {
    webApp.updateSiteMap(true);
  }
%>

 <script type="text/javascript">
<%
  if (errMsgs.size() != 0) {
%>
  window.alert("<fmt:message key="fmErrorTitle" />\n\n<%= Utils.generateList(errMsgs, "\\n") %>");
<%
  }
%>
  location.replace("<%= redirect %>");
</script>

<a href="<%= redirect %>"><fmt:message key="genericContinue" /></a>
