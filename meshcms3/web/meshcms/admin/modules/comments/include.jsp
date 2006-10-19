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
<%@ page import="javax.mail.*" %>
<%@ page import="javax.mail.internet.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="com.opensymphony.module.sitemesh.parser.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%--
  Advanced parameters for this module:
  - css = (name of a css class)
  - date = none (default) | normal | full
  - width = (width of fields, defaults to 98%)
  - notify = (e-mail address to send notifications of new comments)
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
    commentsPath = new Path("_meshcms_comments", md.getPagePath(),
        md.getLocation());
  }
  
  File commentsDir = webSite.getFile(commentsPath);
  commentsDir.mkdirs();
  
  if (!commentsDir.isDirectory()) {
    return;
  }

  Locale locale = WebUtils.getPageLocale(pageContext);
  ResourceBundle pageBundle = ResourceBundle.getBundle
      ("org/meshcms/webui/Locales", locale);

  if (request.getMethod().equalsIgnoreCase("post") &&
      moduleCode.equals(request.getParameter("post_modulecode"))) {
    WebUtils.setBlockCache(request);
    WebUtils.removeFromCache(webSite, null, md.getPagePath());
    String delId = request.getParameter("delId");
    
    if (!Utils.isNullOrEmpty(delId)) {
      File delFile = new File(commentsDir, delId);
      
      if (delFile.exists()) {
        delFile.delete();
      }
    }
    
    String name = request.getParameter("name");
    String text = request.getParameter("text");
    
    if (!(Utils.isNullOrEmpty(name) || Utils.isNullOrEmpty(text))) {
      PageAssembler pa = new PageAssembler();
      pa.addProperty("pagetitle", Utils.encodeHTML(name));
      pa.addProperty("meshcmsbody", text);
      File commentFile = new File(commentsDir, "mcc_" +
          WebUtils.numericDateFormatter.format(new Date()) + ".html");
      Utils.writeFully(commentFile, pa.getPage());
      
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
        outMsg.setText("A comment has been added to " +
            WebUtils.getContextHomeURL(request) + md.getPagePath().getAsLink() +
            " by " + name + ":\n\n" + text);
        Transport.send(outMsg);
      }
    }
  }

  String langCode = pageBundle.getString("TinyMCELangCode");

  if (Utils.isNullOrEmpty(langCode)) {
    langCode = locale.getLanguage();
  }
%>

<script language='javascript' type='text/javascript'
 src='<%= request.getContextPath() + '/' + webSite.getAdminScriptsPath() %>/tiny_mce/tiny_mce.js'></script>
<script type="text/javascript">
  function deleteComment(id) {
    if (confirm("<%= pageBundle.getString("commentsConfirmDel") %>")) {
      var f = document.forms["mcc_<%= md.getLocation() %>"];
      f.delId.value = id;
      f.submit();
    }
  }
  
  function submitComment() {
    var f = document.forms["mcc_<%= md.getLocation() %>"];
    
    if (f.name.value == "") {
      alert("<%= pageBundle.getString("commentsNoName") %>");
      f.name.focus();
      return;
    }
    
    tinyMCE.triggerSave();

    if (f.text.value == "") {
      alert("<%= pageBundle.getString("commentsNoText") %>");
      f.text.focus();
      return;
    }
    
    f.submit();
  }
  
  tinyMCE.init({
    mode : "specific_textareas",
    theme : "simple",
    editor_selector : "mceEditor",
    language : "<%= langCode %>"
  });
</script>

<form name="mcc_<%= md.getLocation() %>" method="post">
<input type="hidden" name="post_modulecode" value="<%= moduleCode %>" />
<input type="hidden" name="delId" value="" />
<div<%= md.getFullCSSAttribute("css") %>>

<%
  File[] files = commentsDir.listFiles();

  if (files != null && files.length > 0) {
    Arrays.sort(files, new ReverseComparator(new FileDateComparator()));
    DateFormat df = md.getDateFormat(locale, "date");

    for (int i = 0; i < files.length; i++) {
      if (FileTypes.isPage(files[i].getName())) {
        WebUtils.updateLastModifiedTime(request, files[i]);
        FastPageParser fpp = new FastPageParser();

        try {
          Reader reader = new BufferedReader(new FileReader(files[i]));
          FastPage pg = (FastPage) fpp.parse(reader);
          String title = pg.getTitle();
%>
 <div class="includeitem">
  <div class="includetitle">
    <%= Utils.isNullOrEmpty(title) ? "&nbsp;" : title %>
    <% if (!userInfo.isGuest()) { %>
      (<a href="javascript:deleteComment('<%= files[i].getName() %>');"><%= pageBundle.getString("commentsDelete") %></a>)
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
    <%= pg.getBody() %>
  </div>
 </div>
<%
          reader.close();
        } catch (Exception ex) {}
      }
    }
  }
  
  String width = md.getAdvancedParam("width", "98%");
%>

 <div class="includeitem">
  <div class="includetext">
    <div><label for="mcc_name"><%= pageBundle.getString("commentsName") %></label></div>
    <div><input type="text" name="name" id="mcc_name" style="width: <%= width %>;" /></div>
  </div>
  <div class="includetext">
    <div><label for="mcc_text"><%= pageBundle.getString("commentsText") %></label></div>
    <div><textarea name="text" id="mcc_text" class="mceEditor" style="width: <%= width %>; height: 12em;"></textarea></div>
    <div style="margin-top: 1em;">
      <input type="button" value="<%= pageBundle.getString("commentsSubmit") %>" onclick="javascript:submitComment();" />
    </div>
  </div>
 </div>

</div>
</form>
