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
<%@ page import="java.nio.charset.*" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.activation.*" %>
<%@ page import="javax.mail.*" %>
<%@ page import="javax.mail.internet.*" %>
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="org.meshcms.core.UserInfo" />

<%!
  static final String DEFAULT_SERVERS = "localhost:pop.gmail.com";
  String defaultCharset;
  Path tempPath = null;

  String actionLink(String actionName, String caption) {
    return "<a href=\"javascript:document.forms['mcwm'].mcwmaction.value='" +
        actionName + "';document.forms['mcwm'].submit();\" class='mcwmbutton'>" + caption + "</a>";
  }

  void displayText(JspWriter out, InputStream in, String charset) throws Exception {
    if (Utils.isNullOrEmpty(charset) || !Charset.isSupported(charset)) {
      charset = defaultCharset;
    }
    
    BufferedReader br = new BufferedReader(new InputStreamReader(in, charset));
    String line;
    out.write("<div class='mcwmtext'>");

    while ((line = br.readLine()) != null) {
      int n = 0;
      boolean other = true;
      boolean indentOnSpaces = false;

      for (int i = 0; other && i < line.length(); i++) {
        switch (line.charAt(i)) {
          case '>':
            n++;
            indentOnSpaces = false;
            break;
          case ' ':
          case '\t':
            if (indentOnSpaces) {
              n++;
            }
            break;
          default:
            other = false;
        }
      }

      line = Utils.encodeHTML(line);

      out.write("<div style='padding-left: " + n + "em;'>" + line + "&nbsp;</div>\n");
    }

    out.write("</div>");
  }

  ArrayList displayMessagePart(Part part, JspWriter out, ArrayList attachments)
      throws Exception {
    MimeType mimeType = new MimeType(part.getContentType());

    if (mimeType.getBaseType().equals("text/plain")) {
      displayText(out, part.getInputStream(), mimeType.getParameter("charset"));
    } else if (mimeType.getBaseType().startsWith("multipart")) {
      Multipart multipart = (Multipart) part.getContent();

      for (int i = 0; i < multipart.getCount(); i++) {
        attachments = displayMessagePart(multipart.getBodyPart(i), out, attachments);
      }
    } else {
      attachments.add(part);
    }

    return attachments;
  }
  
  public void appendText(Part part, StringBuffer sb) throws Exception {
    MimeType mimeType = new MimeType(part.getContentType());

    if (mimeType.getBaseType().equals("text/plain")) {
      sb.append(part.getContent());
    } else if (mimeType.getBaseType().startsWith("multipart")) {
      Multipart multipart = (Multipart) part.getContent();

      for (int i = 0; i < multipart.getCount(); i++) {
        appendText(multipart.getBodyPart(i), sb);
      }
    }
  }
%>
<style type="text/css">
  .mcwmtext {
    border: 1px solid black;
    padding: 3em;
    font-family: "Lucida Console", monospace;
    font-size: 0.8em;
  }

  .mcwmheader {
    background-color: silver;
    border-top: 1px solid black;
    padding-left: 2em;
  }

  .mcwmattachment {
    background-color: silver;
    border-bottom: 1px solid black;
    padding-left: 2em;
  }

  .mcwmtable th {
    border-top: 1px solid gray;
    border-bottom: 1px solid gray;
  }

  .mcwmtable td {
    border-bottom: 1px solid gray;
  }

  .mcwmbuttons {
    text-align: center;
    margin: 6px;
  }

  .mcwmbutton {
    border: 1px solid;
    padding: 3px 15px;
    background-color: silver;
  }

  .mcwmbutton:hover {
    text-decoration: none;
    border: 2px solid;
  }
</style>

<form name="mcwm" id="mcwm" method="POST">
 <input type="hidden" name="mcwmaction" value="" />

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
  
  defaultCharset = WebSite.SYSTEM_CHARSET;
  String[] allowedServers = Utils.tokenize(md.getAdvancedParam("servers", DEFAULT_SERVERS), ": ");
  Folder mcwmFolder = (Folder) session.getAttribute("mcwmFolder");
  Session mcwmSession = (Session) session.getAttribute("mcwmSession");

  if (mcwmFolder == null || mcwmSession == null) {
    String username = request.getParameter("username");

    if (!Utils.isNullOrEmpty(username) && (allowedServers.length == 0 ||
        Utils.searchString(allowedServers, request.getParameter("host"), false) >= 0)) {
      URLName urlName = new URLName("pop3", request.getParameter("host"), 110,
          "INBOX", username, request.getParameter("password"));

      try {
        if (mcwmSession == null) {
          mcwmSession = WebUtils.getMailSession(webSite);
          session.setAttribute("mcwmSession", mcwmSession);
        }

        mcwmFolder = mcwmSession.getFolder(urlName);
        session.setAttribute("mcwmFolder", mcwmFolder);

        if (!mcwmFolder.isOpen()) {
          mcwmFolder.open(Folder.READ_WRITE);
        }
      } catch (Exception ex) {
        %>Error: can't open mailbox<%
      }
    }
  }

  if (mcwmFolder == null || mcwmSession == null) {
    %><table align="center" border="0" cellspacing="1" cellpadding="4">
     <tr>
      <td align="right">Username:</td>
      <td><input type="text" name="username" size="20" /></td>
     </tr>

     <tr>
      <td align="right">Password:</td>
      <td><input type="password" name="password" size="20" /></td>
     </tr>

    <% if (allowedServers.length == 1) { %>
     <input type="hidden" name="host" value="<%= allowedServers[0] %>" />
    <% } else { %>
     <tr>
      <td align="right">POP3 Server</td>
      <% if (allowedServers.length == 0) { %>
        <td><input type="text" name="host" size="20" /></td>
      <% } else { %>
        <td><select name="host">
         <% for (int i = 0; i < allowedServers.length; i++) { %>
          <option value="<%= allowedServers[i] %>"><%= allowedServers[i] %></option>
         <% } %>
        </select></td>
      <% } %>
     </tr>
    <% } %>

     <tr>
      <th colspan="2" align="center">
       <input type="submit" value="Open Mailbox" class="mcwmbutton" />
      </th>
     </tr>
    </table><%
  } else {
    String action = request.getParameter("mcwmaction");

    if (Utils.isNullOrEmpty(action)) {
      action = "inbox";
    }

    // list of messages
    if (action.startsWith("inbox")) {
      Message[] messages = mcwmFolder.getMessages();

      if (action.equals("inbox_delete")) {
        for (int i = 0; i < messages.length; i++) {
          String field = "msg" + i;
          String value = request.getParameter(field);

          if (!Utils.isNullOrEmpty(value)) {
            if (messages[i].isSet(Flags.Flag.DELETED)) {
              messages[i].setFlag(Flags.Flag.DELETED, false);
            } else {
              messages[i].setFlag(Flags.Flag.DELETED, true);
            }
          }
        }
      }
      %><table class="mcwmtable" border="0" cellspacing="1" cellpadding="1">
       <tr>
        <th>&nbsp;</th>
        <th>From:</th>
        <th>Subject:</th>
        <th>Date:</th>
        <th>Size:</th>
       </tr><%

      for (int i = 0; i < messages.length; i++) {
        %><tr>
         <td align="center"><input type="checkbox" id="msg<%= i %>" name="msg<%= i %>" value="msg<%= i %>" /></td>
         <td title="<%= ((InternetAddress) messages[i].getFrom()[0]).getAddress() %>"><%= messages[i].getFrom()[0].toString() %></td>
         <% if (messages[i].isSet(Flags.Flag.DELETED)) { %>
         <td><span style="text-decoration: line-through;"><%= messages[i].getSubject() %></span></td>
         <% } else { %>
         <td><a href="javascript:document.forms['mcwm'].mcwmaction.value='display<%= messages[i].getMessageNumber() %>';document.forms['mcwm'].submit();"><%= messages[i].getSubject() %></a></td>
         <% } %>
         <td><%= messages[i].getSentDate() %></td>
         <td><%= messages[i].getSize() %></td>
        </tr><%
      }

      %></table>

      <div class="mcwmbuttons">
       <%= actionLink("inbox_delete", "(Un)delete selected") %>
       <%= actionLink("write", "Write new") %>
       <%= actionLink("logout", "Logout") %>
      </div><%
    } 
    
    // show a message
    if (action.startsWith("display")) {
      int messageNumber = Integer.parseInt(action.substring(7));
      Message message = mcwmFolder.getMessage(messageNumber);

      out.write("<div class='mcwmheader'>From: " + ((InternetAddress) message.getFrom()[0]).getAddress() + "</div>\n");
      out.write("<div class='mcwmheader'>Subject: " + message.getSubject() + "</div>\n");

      ArrayList attList = new ArrayList();
      attList = displayMessagePart(message, out, attList);
      Part[] attachments = (Part[]) attList.toArray(new Part[attList.size()]);
      session.setAttribute("mcwmAttachments", attachments);

      if (attachments.length > 0) {
      %><table class="mcwmtable" border="0" cellspacing="1" cellpadding="1">
       <tr>
        <th>Attachment:</th>
        <th>Name:</th>
        <th>View:</th>
        <th>Download:</th>
       </tr><%

      for (int i = 0; i < attachments.length; i++) {
        MimeType mimeType = new MimeType(attachments[i].getContentType());
        MimeTypeParameterList mtpl = mimeType.getParameters();
        String disposition = attachments[i].getDisposition();
        boolean isAttachment = disposition != null &&
            disposition.equalsIgnoreCase("attachment");

        if (tempPath == null) {
          tempPath = webSite.getGeneratedFilesPath().add(webSite.getRequestedPath(request));
        }

        File currentDir = webSite.getFile(tempPath);
        currentDir.mkdirs();
        String fileName = attachments[i].getFileName();
        String fileExt = null;

        if (fileName == null) {
          fileName = mimeType.getPrimaryType();
        } else {
          fileExt = Utils.getExtension(fileName, true);
        }

        if (fileExt == null) {
          fileExt = '.' + mimeType.getSubType();
        } else {
          fileName = Utils.removeExtension(fileName);
        }

        String tempFileName = Utils.generateUniqueName(fileName + fileExt, currentDir);
        File attFile = new File(currentDir, tempFileName);
        Utils.copyStream(attachments[i].getInputStream(),
            new FileOutputStream(attFile), true);

        %><tr>
         <td align="center"><%= isAttachment ? "+" : "&nbsp;" %></td>
         <td align="center"><%= Utils.noNull(mtpl.get("name"), mimeType.getBaseType()) %></td>
         <td align="center"><a href="<%= request.getContextPath() +
           tempPath.getAsLink() + '/' + tempFileName %>" target="_blank">View</a></td>
         <td align="center"><a href="<%=
           request.getContextPath() %>/servlet/org.meshcms.core.DownloadServlet<%=
           tempPath.getAsLink() + '/' + tempFileName %>?filename=<%=
           fileName + fileExt %>">Download</a></td>
        </tr><%
      }

      %></table><%
      }
      %><div class="mcwmbuttons">
       <%= actionLink("reply-to" + messageNumber, "Reply") %>
       <%= actionLink("replyall" + messageNumber, "Reply All") %>
       <%= actionLink("logout", "Logout") %>
       <%= actionLink("inbox", "Inbox") %>
      </div><%
    } 
    
    // logout
    if (action.equals("logout")) {
      if (mcwmFolder.isOpen()) {
        mcwmFolder.close(true);
      }

      mcwmFolder = null;
      mcwmSession = null;

      session.removeAttribute("mcwmFolder");
      // session.removeAttribute("mcwmSession");
      session.removeAttribute("mcwmAttachments");
      %>
      Logout successful

      <div class="mcwmbuttons">
       <%= actionLink("login", "Login") %>
      </div><%
    } 
    
    // send a message
    if (action.equals("send")) {
      Message message = (Message) session.getAttribute("mcwmWriting");
      boolean sent = false;
      
      if (message != null) {
        boolean okToSend = true;
        
        try {
          InternetAddress[] addr = InternetAddress.parse(request.getParameter("msgfrom"), false);
          
          if (addr == null || addr.length == 0) {
            %>Error: the sender is missing<%
            okToSend = false;
          } else {
            message.setFrom(InternetAddress.parse(request.getParameter("msgfrom"), false)[0]);
          }
           
          addr = InternetAddress.parse(request.getParameter("msgto"), false);
            
          if (addr == null || addr.length == 0) {
            %>Error: the recipient is missing<%
            okToSend = false;
          } else {
            message.setRecipients(Message.RecipientType.TO, addr);
          }
          
          message.setRecipients(Message.RecipientType.CC,
              InternetAddress.parse(request.getParameter("msgcc"), false));
          message.setSubject(request.getParameter("msgsubject"));
          message.setText(request.getParameter("msgtext"));

          if (okToSend) {
            Transport.send(message);
            sent = true;
            session.removeAttribute("mcwmWriting");
            %>
              Message sent successfully
              <%= actionLink("logout", "Logout") %>
              <%= actionLink("inbox", "Inbox") %>
            <%
          }
        } catch (Exception ex) {
          %>Error: couldn't send message<%
          webSite.log("Can't send mail message", ex);
          ex.printStackTrace();
        }
      }
      
      if (!sent) {
        action = "edit";
      }
    }
    
    // compose a message
    if (action.equals("write") || action.startsWith("reply") || action.equals("edit")) {
      Message message;
      
      Message original = action.startsWith("reply") ?
        mcwmFolder.getMessage(Integer.parseInt(action.substring(8))) : null;

      if (action.startsWith("reply-to")) {
        message = original.reply(false);
      } else if (action.startsWith("replyall")) {
        message = original.reply(true);
      } else if (action.equals("edit")) {
        message = (Message) session.getAttribute("mcwmWriting");
      } else {
        message = new MimeMessage(mcwmSession);
      }
      
      if (original != null) {
        StringBuffer sb = new StringBuffer();
        appendText(original, sb);
        message.setText(sb.toString());
      }

      session.setAttribute("mcwmWriting", message);
      String msgContent = "";
      
      try {
        msgContent = Utils.noNull(message.getContent().toString());
      } catch (IOException ex) {
        // no content
      }

      %><form>
        <div><label for="msgfrom">From:</label></div>
        <div><input type="text" name="msgfrom" id="msgfrom"
         value="" style="width: 100%;" /></div>

        <div><label for="msgto">To:</label></div>
        <div><input type="text" name="msgto" id="msgto" style="width: 100%;"
         value="<%= Utils.encodeHTML(Utils.generateList(message.getRecipients(Message.RecipientType.TO), ", ")) %>" /></div>

        <div><label for="msgcc">Cc:</label></div>
        <div><input type="text" name="msgcc" id="msgcc" style="width: 100%;"
         value="<%= Utils.encodeHTML(Utils.generateList(message.getRecipients(Message.RecipientType.CC), ", ")) %>" /></div>

        <div><label for="msgsubject">Subject:</label></div>
        <div><input type="text" name="msgsubject" id="msgsubject" style="width: 100%;"
         value="<%= Utils.encodeHTML(message.getSubject()) %>" /></div>

        <div><label for="msgtext">Text:</label></div>
        <div><textarea name="msgtext" id="msgtext"
         style="width: 100%; height: 20em;"><%= Utils.encodeHTML(msgContent) %></textarea></div>
        <div class="mcwmbuttons">
         <%= actionLink("send", "Send Message") %>
         <%= actionLink("logout", "Logout") %>
         <%= actionLink("inbox", "Inbox") %>
        </div>
      </form><%
    } 
  }
%>
</form>
