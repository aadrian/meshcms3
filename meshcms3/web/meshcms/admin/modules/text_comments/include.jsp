<%--
 Copyright 2004-2008 Luciano Vernaschi
 
 This file is part of MeshCMS.
 
 MeshCMS is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 MeshCMS is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@ page import="java.io.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.mail.*" %>
<%@ page import="javax.mail.internet.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="com.opensymphony.module.sitemesh.*" %>
<%@ page import="com.opensymphony.module.sitemesh.parser.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%--
  Advanced parameters for this module:
  - date = none (default) | normal | full
  - notify = (e-mail address to send notifications of new comments)
  - form_css = (name of a css class for full form)
  - field_css = (name of a css class for input fields)
  - max_age = (max number of days after which comments are not shown)
  - moderated = true (default) | false (logged users can always publish directly)
  - parse = true | false (default) if true, find hyperlinks in text
--%>

<%
  String moduleCode = request.getParameter("modulecode");
  ModuleDescriptor md = null;

  if (moduleCode != null) {
    md = (ModuleDescriptor) request.getAttribute(moduleCode);
  }

  if (md == null) {
    if (!response.isCommitted()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    return;
  }

  Path commentsPath = md.getModuleArgumentPath(false);

  if (commentsPath == null) {
    commentsPath = md.getModuleDataPath(webSite).add(md.getPagePath(), md.getLocation());
  }

  File commentsDir = webSite.getFile(commentsPath);

  /* if (!commentsDir.isDirectory()) {
    throw new IllegalStateException(Utils.getFilePath(commentsDir) +
        " is not a directory");
  } */

  boolean moderated = Utils.isTrue(md.getAdvancedParam("moderated", "true"));
  boolean parse = Utils.isTrue(md.getAdvancedParam("parse", "false"));
  
  if (!userInfo.isGuest()) {
    moderated = false;
  }
  
  Locale locale = WebUtils.getPageLocale(pageContext);
  ResourceBundle pageBundle = ResourceBundle.getBundle
      ("org/meshcms/webui/Locales", locale);
  
  String name = request.getParameter("mcc_name");
  String text = request.getParameter("mcc_text");

  String delId = request.getParameter("delId");

  if (!Utils.isNullOrEmpty(delId)) {
    File delFile = new File(commentsDir, delId);

    if (delFile.exists()) {
      if (userInfo.canWrite(webSite, md.getPagePath())) {
        webSite.delete(userInfo, webSite.getPath(delFile), false);
      } else {
        delFile.delete();
      }
    }
  }

  String showId = request.getParameter("showId");

  if (!Utils.isNullOrEmpty(showId)) {
    File hiddenFile = new File(commentsDir, showId);

    if (hiddenFile.exists()) {
      File visibleFile = new File(commentsDir, showId.replaceAll("mch", "mcc"));
      hiddenFile.renameTo(visibleFile);
    }
  }

  String hideId = request.getParameter("hideId");

  if (!Utils.isNullOrEmpty(hideId)) {
    File visibleFile = new File(commentsDir, hideId);

    if (visibleFile.exists()) {
      File hiddenFile = new File(commentsDir, hideId.replaceAll("mcc", "mch"));
      visibleFile.renameTo(hiddenFile);
    }
  }

  if (request.getMethod().equalsIgnoreCase("post") &&
      moduleCode.equals(request.getParameter("post_modulecode"))) {
    WebUtils.setBlockCache(request);
    WebUtils.removeFromCache(webSite, null, md.getPagePath());

    if (!(Utils.isNullOrEmpty(name) || Utils.isNullOrEmpty(text))) {
      if (name.length() > 20) {
        name = name.substring(0, 20);
      }
      
      commentsDir.mkdirs();
      File commentFile = new File(commentsDir, (moderated ? "mch_" : "mcc_") +
          WebUtils.numericDateFormatter.format(new Date()) + ".txt");
      Utils.writeFully(commentFile, name + "\n\n" + text);

      String email = md.getAdvancedParam("notify", null);

      if (Utils.checkAddress(email)) {
        InternetAddress address = new InternetAddress(email);
        Session mailSession = WebUtils.getMailSession(webSite);
        MimeMessage outMsg = new MimeMessage(mailSession);
        outMsg.setFrom(address);
        outMsg.addRecipient(Message.RecipientType.TO, address);
        outMsg.setSubject("Comment added on " + request.getServerName());
        outMsg.setHeader("Content-Transfer-Encoding", "8bit");
        outMsg.setHeader("X-MeshCMS-Log", "Sent from " + request.getRemoteAddr() +
            " at " + new Date() + " using page /" + md.getPagePath());
        
        String url = WebUtils.getContextHomeURL(request).append
            (md.getPagePath().getAsLink()).toString();
        StringBuffer sb = new StringBuffer();
        sb.append("A comment has been added to ");
        sb.append(url);
        sb.append(" by ");
        sb.append(name);
        sb.append(" (");
        sb.append(request.getRemoteAddr());
        sb.append("):\n\n");
        sb.append(text);
        sb.append("\n\nDelete: ");
        sb.append(url);
        sb.append("?delId=");
        sb.append(commentFile.getName());
        
        if (moderated) {
          sb.append("\nShow: ");
          sb.append(url);
          sb.append("?showId=");
          sb.append(commentFile.getName());
        } else {
          sb.append("\nHide: ");
          sb.append(url);
          sb.append("?hideId=");
          sb.append(commentFile.getName());
        }
        
        outMsg.setText(sb.toString());
        Transport.send(outMsg);
      }
      
      name = text = "";
    }
  }
%>

<script type="text/javascript">
// <![CDATA[
  function deleteComment(id) {
    if (confirm("<%= pageBundle.getString("commentsConfirmDel") %>")) {
      var f = document.forms["mcc_<%= md.getLocation() %>"];
      f.delId.value = id;
      f.submit();
    }
  }

  function hideComment(id) {
    var f = document.forms["mcc_<%= md.getLocation() %>"];
    f.hideId.value = id;
    f.submit();
  }

  function showComment(id) {
    var f = document.forms["mcc_<%= md.getLocation() %>"];
    f.showId.value = id;
    f.submit();
  }

  function submitComment() {
    var f = document.forms["mcc_<%= md.getLocation() %>"];

    if (f.mcc_name.value == "") {
      alert("<%= pageBundle.getString("commentsNoName") %>");
      f.mcc_name.focus();
      return false;
    }

    if (f.mcc_text.value == "") {
      alert("<%= pageBundle.getString("commentsNoText") %>");
      f.mcc_text.focus();
      return false;
    }

    return true;
  }
// ]]>
</script>

<form name="mcc_<%= md.getLocation() %>" method="post" action="">
<input type="hidden" name="post_modulecode" value="<%= moduleCode %>" />
<input type="hidden" name="delId" value="" />
<input type="hidden" name="showId" value="" />
<input type="hidden" name="hideId" value="" />
<div class="<%= md.getAdvancedParam("form_css", "mailform") %>">

<%
  String fieldStyle = md.getAdvancedParam("field_css", "formfields");
  
  int maxAge = Utils.parseInt(md.getAdvancedParam("max_age", ""), 0);
  long start = (maxAge > 0) ? System.currentTimeMillis() - maxAge *
      Configuration.LENGTH_OF_DAY : 0L;
  
  File[] files = null;
  
  if (commentsDir.exists() && commentsDir.isDirectory()) {
    files = commentsDir.listFiles();
  }

  if (files != null && files.length > 0) {
    Arrays.sort(files, new ReverseComparator(new FileDateComparator()));
    DateFormat df = md.getDateFormat(locale, "date");

    for (int i = 0; i < files.length; i++) {
      if (FileTypes.isLike(files[i].getName(), "txt") && files[i].lastModified() > start) {
        WebUtils.updateLastModifiedTime(request, files[i]);
        
        try {
          String body = Utils.encodeHTML(Utils.readFully(files[i]));
          String title = "<em>anonymous</em>";
          int nn = body.indexOf("\n\n");
          
          if (nn < 0) {
            nn = body.indexOf("\r\n\r\n");
          }

          if (nn >= 0) {
              title = body.substring(0, nn);
              body = body.substring(nn + 2);
          }
          
          if (parse) {
            body = WebUtils.findLinks(body);
            body = WebUtils.findEmails(body);
          }
          
          body = body.replaceAll("\n", "<br />");
          boolean hidden = false;
          
          if (files[i].getName().startsWith("mch")) {
            hidden = true;
            
            if (userInfo.canWrite(webSite, md.getPagePath())) {
              body = "<h4>" + pageBundle.getString("commentsAuthorize") +
                  "</h4>\n" + body;
            } else {
              body = "<p><em>" + pageBundle.getString("commentsNotAuthorized") +
                  "</em></p>";
            }
          }
  %>
 <div class="includeitem">
  <div class="includetitle">
    <%= Utils.isNullOrEmpty(title) ? "&nbsp;" : title %>
    <% if (userInfo.canWrite(webSite, md.getPagePath())) { %>
      (
      <a href="javascript:deleteComment('<%= files[i].getName() %>');"><%= pageBundle.getString("commentsDelete") %></a>
      |
      <a href="javascript:<%= hidden ? "show" : "hide" %>Comment('<%= files[i].getName() %>');"><%= pageBundle.getString(hidden ? "commentsShow" : "commentsHide") %></a>
      )
    <% } %>
  </div>
<%
          if (df != null) {
%>
  <div class="includedate">
    (<%= df.format(new Date(files[i].lastModified())) %>)
  </div>
<%
          }
%>
  <div class="includetext">
    <%= body %>
  </div>
 </div>
<%
        } catch (Exception ex) {}
      }
    }
  }
%>

<%
  if (!org.meshcms.extra.StaticExporter.isExportRequest(request)) {
%>
 <div class="includeitem">
  <div class="includetext">
    <div><label for="mcc_name"><%= pageBundle.getString("commentsName") %></label></div>
    <div><input type="text" name="mcc_name" id="mcc_name" class="<%= fieldStyle %>"
     maxlength="20" value="<%= Utils.encodeHTML(name) %>" /></div>
  </div>
  <div class="includetext">
    <div><label for="mcc_text"><%= pageBundle.getString("commentsText") %></label></div>
    <div><textarea name="mcc_text" id="mcc_text" class="<%= fieldStyle %>"
      rows="12" cols="80" style="height: 12em;"><%= Utils.encodeHTML(text) %></textarea></div>
  </div>
  <div class="includetext">
    <div style="margin-top: 1em; clear: both;">
      <input type="submit" value="<%= pageBundle.getString("commentsSubmit") %>" onclick="return submitComment();" />
    </div>
  </div>
 </div>
<%
  }
%>

</div>
</form>
