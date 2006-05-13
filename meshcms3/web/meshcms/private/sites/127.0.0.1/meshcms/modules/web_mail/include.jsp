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
<%@ page import="java.util.*" %>
<%@ page import="javax.activation.*" %>
<%@ page import="javax.mail.*" %>
<%@ page import="javax.mail.internet.*" %>
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webSite" scope="request" type="com.cromoteca.meshcms.WebSite" />
<jsp:useBean id="userInfo" scope="session" class="com.cromoteca.meshcms.UserInfo" />

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<%!
  String[] allowedServers = {"mail.arscolor.com", "cromoteca.com", "in.virgilio.it"};
  String defaultCharset;
  Path tempPath = null;
  
  String actionLink(String actionName, String caption) {
    return "<a href=\"javascript:document.forms['mcwm'].mcwmaction.value='" +
        actionName + "';document.forms['mcwm'].submit();\" class='mcwmbutton'>" + caption + "</a>";
  }
  
  void displayText(JspWriter out, InputStream in, String charset) throws Exception {
    if (Utils.isNullOrEmpty(charset)) {
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
  String cp = request.getContextPath();
  defaultCharset = webSite.getConfiguration().getPreferredCharset();
  
  Folder mcwmFolder = (Folder) session.getAttribute("mcwmFolder");
  Session mcwmSession = (Session) session.getAttribute("mcwmSession");
  
  if (mcwmFolder == null || mcwmSession == null) {
    String username = request.getParameter("username");
    
    if (!Utils.isNullOrEmpty(username) &&
        Utils.searchString(allowedServers, request.getParameter("host"), false) >= 0) {
      URLName urlName = new URLName("pop3", request.getParameter("host"), 110,
          "INBOX", username, request.getParameter("password"));
      
      try {
        if (mcwmSession == null) {
          mcwmSession = Session.getInstance(new Properties());
          session.setAttribute("mcwmSession", mcwmSession);
        }

        mcwmFolder = mcwmSession.getFolder(urlName);
        session.setAttribute("mcwmFolder", mcwmFolder);
        
        if (!mcwmFolder.isOpen()) {
          mcwmFolder.open(Folder.READ_WRITE);
        }
      } catch (Exception ex) {
        %>[ERROR]<%
      }
    }
  }
    
  if (mcwmFolder == null || mcwmSession == null) {
    %><table align="center" border="0" cellspacing="1" cellpadding="4">
     <tr>
      <td><fmt:message key="loginUsername" /></td>
      <td><input type="text" name="username" size="12" /></td>
     </tr>

     <tr>
      <td><fmt:message key="loginPassword" /></td>
      <td><input type="password" name="password" size="12" /></td>
     </tr>

    <% if (allowedServers.length == 1) { %>
     <input type="hidden" name="host" value="<%= allowedServers[0] %>" />
    <% } else { %>
     <tr>
      <td><fmt:message key="loginServer" /></td>
      <td><select name="host">
       <% for (int i = 0; i < allowedServers.length; i++) { %>
        <option value="<%= allowedServers[i] %>"><%= allowedServers[i] %></option>
       <% } %>
      </select></td>
     </tr>
    <% } %>
     
     <tr>
      <th colspan="2">
       <input type="submit" value="<fmt:message key="loginSubmit" />" />
      </th>
     </tr>
    </table><%
  } else {
    String action = request.getParameter("mcwmaction");
    
    if (Utils.isNullOrEmpty(action)) {
      action = "inbox";
    }

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
         <td title="<%= ((InternetAddress) messages[i].getFrom()[0]).getAddress() %>"><%= ((InternetAddress) messages[i].getFrom()[0]).toString() %></td>
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
    } else if (action.startsWith("display")) {
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
          tempPath = webSite.getCMSPath().add(Finals.GENERATED_FILES_SUBDIRECTORY,
              webSite.getRequestedPath(request));
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
         <td align="center"><%= Utils.noNull(mtpl.get("name"), "&nbsp;") %></td>
         <td align="center"><a href="<%= request.getContextPath() + 
           tempPath.getAsLink() + '/' + tempFileName %>" target="_blank">View</a></td>
         <td align="center"><a href="<%=
           request.getContextPath() %>/servlet/com.cromoteca.meshcms.DownloadServlet<%=
           tempPath.getAsLink() + '/' + tempFileName %>?filename=<%=
           fileName + fileExt %>">Download</a></td>
        </tr><%
      }
      
      %></table><%
      }
      %><div class="mcwmbuttons">
       <%= actionLink("replyto" + messageNumber, "Reply") %>
       <%= actionLink("replyall" + messageNumber, "Reply All") %>
       <%= actionLink("logout", "Logout") %>
       <%= actionLink("inbox", "Inbox") %>
      </div><%
    } else if (action.startsWith("downatt") || action.startsWith("viewatt")) {
      int comma = action.indexOf(',');
      int messageNumber = Integer.parseInt(action.substring(7, comma));
      int attachmentNumber = Integer.parseInt(action.substring(comma + 1));
    } else if (action.equals("logout")) {
      if (mcwmFolder.isOpen()) {
        mcwmFolder.close(true);
      }
      
      mcwmFolder = null;
      mcwmSession = null;

      session.removeAttribute("mcwmFolder");
      // session.removeAttribute("mcwmSession");
      session.removeAttribute("mcwmAttachments");
      %>[LOGOUT_OK]

      <div class="mcwmbuttons">
       <%= actionLink("login", "Login") %>
      </div><%
    } else if (action.equals("write") || action.startsWith("reply")) {
      Message message;
      
      if (action.startsWith("replyto")) {
        message = mcwmFolder.getMessage(Integer.parseInt(action.substring(7))).reply(false);
      } else if (action.startsWith("replyall")) {
        message = mcwmFolder.getMessage(Integer.parseInt(action.substring(8))).reply(true);
      } else {
        message = new MimeMessage(mcwmSession);
      }
      
      session.setAttribute("mcwmWriting", message);
      
      %><form>
        <div><label for="msgfrom">From:</label></div>
        <div><input type="text" name="msgfrom" id="msgfrom"
         value="" style="width: 100%;" /></div>

        <div><label for="msgto">To:</label></div>
        <div><input type="text" name="msgto" id="msgto" style="width: 100%;"
         value="<%= Utils.noNull(Utils.generateList(message.getRecipients(Message.RecipientType.TO), ", ")) %>" /></div>

        <div><label for="msgcc">Cc:</label></div>
        <div><input type="text" name="msgcc" id="msgcc" style="width: 100%;"
         value="<%= Utils.noNull(Utils.generateList(message.getRecipients(Message.RecipientType.CC), ", ")) %>" /></div>

        <div><label for="msgsubject">Subject:</label></div>
        <div><input type="text" name="msgsubject" id="msgsubject" style="width: 100%;"
         value="<%= Utils.noNull(message.getSubject()) %>" /></div>

        <div><label for="msgtext">Text:</label></div>
        <div><textarea name="msgtext" id="msgtext"
         style="width: 100%; height: 20em;"></textarea></div>
      </form><%
    }
  }
%>
</form>
