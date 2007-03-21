<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2007 Luciano Vernaschi

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
  commentsDir.mkdirs();

  if (!commentsDir.isDirectory()) {
    throw new IllegalStateException(Utils.getFilePath(commentsDir) +
        " is not a directory");
  }

  Locale locale = WebUtils.getPageLocale(pageContext);
  ResourceBundle pageBundle = ResourceBundle.getBundle
      ("org/meshcms/webui/Locales", locale);

  if (request.getMethod().equalsIgnoreCase("post") &&
      moduleCode.equals(request.getParameter("post_modulecode"))) {
    WebUtils.setBlockCache(request);
    WebUtils.removeFromCache(webSite, null, md.getPagePath());
    String delId = request.getParameter("delId");

    if (!Utils.isNullOrEmpty(delId) &&
        userInfo.canWrite(webSite, md.getPagePath())) {
      File delFile = new File(commentsDir, delId);

      if (delFile.exists()) {
        webSite.delete(userInfo, webSite.getPath(delFile), false);
      }
    }

    String name = request.getParameter("name");
    String text = request.getParameter("text");
    int sum = Utils.parseInt(request.getParameter("sum"), -1);
    int n1 = Utils.parseInt(request.getParameter("n1"), 0) /
        (Utils.SYSTEM_CHARSET.hashCode() >>> 8);
    int n2 = Utils.parseInt(request.getParameter("n2"), 0) /
        (WebSite.VERSION_ID.hashCode() >>> 8);

    if (!(Utils.isNullOrEmpty(name) || Utils.isNullOrEmpty(text)) &&
        sum == n1 + n2) {
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
            " by " + name + " (" + request.getRemoteAddr() + "):\n\n" + text);
        Transport.send(outMsg);
      }
    }
  }

  // numbers to verify submitted post
  int n1 = Utils.getRandomInt(30) + 1;
  int n2 = Utils.getRandomInt(30) + 1;

  String langCode = pageBundle.getString("TinyMCELangCode");

  if (Utils.isNullOrEmpty(langCode)) {
    langCode = locale.getLanguage();
  }
%>

<script type='text/javascript'
 src='<%= request.getContextPath() + '/' + webSite.getAdminScriptsPath() %>/tiny_mce/tiny_mce.js'></script>
<script type="text/javascript">
// <![CDATA[
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

    if (isNaN(f.sum.value) || f.sum.value != <%= n1 + n2 %>) {
      alert("<%= pageBundle.getString("commentsWrongSum") %>");
      f.sum.focus();
      return;
    }

    f.submit();
  }

  tinyMCE.init({
    mode : "exact",
    theme : "simple",
    elements : "mcc_text",
    language : "<%= langCode %>"
  });
// ]]>
</script>

<form name="mcc_<%= md.getLocation() %>" method="post" action="">
<input type="hidden" name="post_modulecode" value="<%= moduleCode %>" />
<input type="hidden" name="delId" value="" />
<div class="<%= md.getAdvancedParam("form_css", "mailform") %>">

<%
  String fieldStyle = md.getAdvancedParam("field_css", "formfields");
  File[] files = commentsDir.listFiles();

  if (files != null && files.length > 0) {
    Arrays.sort(files, new ReverseComparator(new FileDateComparator()));
    DateFormat df = md.getDateFormat(locale, "date");

    for (int i = 0; i < files.length; i++) {
      if (FileTypes.isPage(files[i].getName())) {
        WebUtils.updateLastModifiedTime(request, files[i]);
        HTMLPageParser fpp = new HTMLPageParser();

        try {
          Reader reader = new InputStreamReader(new FileInputStream(files[i]),
              Utils.SYSTEM_CHARSET);
          HTMLPage pg = (HTMLPage) fpp.parse(Utils.readAllChars(reader));
          String title = pg.getTitle();
%>
 <div class="includeitem">
  <div class="includetitle">
    <%= Utils.isNullOrEmpty(title) ? "&nbsp;" : title %>
    <% if (userInfo.canWrite(webSite, md.getPagePath())) { %>
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
%>

<%
  if (!org.meshcms.extra.StaticExporter.isExportRequest(request)) {
%>
 <div class="includeitem">
  <div class="includetext">
    <div><label for="mcc_name"><%= pageBundle.getString("commentsName") %></label></div>
    <div><input type="text" name="name" id="mcc_name" class="<%= fieldStyle %>" /></div>
  </div>
  <div class="includetext">
    <div><label for="mcc_text"><%= pageBundle.getString("commentsText") %></label></div>
    <div><textarea name="text" id="mcc_text" class="<%= fieldStyle %>"
      rows="12" cols="80" style="height: 12em;"></textarea></div>
  </div>
  <div class="includetext">
    <div>
      <label for="mcc_sum"><%= n1 %> + <%= n2 %> =</label>
      <input type="text" name="sum" id="mcc_sum" class="<%= fieldStyle %>" style="width: 3em;" />
      <input type="hidden" name="n1" value="<%= n1 * (Utils.SYSTEM_CHARSET.hashCode() >>> 8) %>" />
      <input type="hidden" name="n2" value="<%= n2 * (WebSite.VERSION_ID.hashCode() >>> 8) %>" />
    </div>
    <div style="margin-top: 1em;">
      <input type="button" value="<%= pageBundle.getString("commentsSubmit") %>" onclick="javascript:submitComment();" />
    </div>
  </div>
 </div>
<%
  }
%>

</div>
</form>
