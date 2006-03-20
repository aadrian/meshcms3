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
<%@ page import="javax.mail.*" %>
<%@ page import="javax.mail.internet.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="fields" scope="session" class="java.util.ArrayList" />

<%  
  response.setHeader("Content-Type", "text/html; charset=" + webApp.getConfiguration().getPreferredCharset());
%>

<%@ taglib prefix="fmt" uri="standard-fmt-rt" %>
<fmt:setBundle basename="com.cromoteca.meshcms.Locales" scope="page" />

<html>
<head>
<%= webApp.getAdminMetaThemeTag() %>
<title><fmt:message key="sendTitle" /></title>
</head>

<body>

<%
  List errMsgs = new ArrayList();
  Iterator iterator = fields.iterator();
  String sender = null;
  String senderName = null;
  String subject = null;
  String recipient = null;
  StringBuffer textMsg = new StringBuffer();

  // textMsg.append("Message sent on:\n" + new Date() + "\n\n\n");

  ResourceBundle bundle = WebUtils.getPageResourceBundle(pageContext);
  Locale locale = WebUtils.getPageLocale(pageContext);
  MessageFormat formatter = new MessageFormat("", locale);

  while (iterator.hasNext()) {
    FormField field = (FormField) iterator.next();
    String code = field.getCode();

    if (!Utils.isNullOrEmpty(code)) {
      String newValue = request.getParameter(code);

      if (newValue != null) {
        field.setValue(newValue);
      }
    }

    if (!field.checkValue()) {
      Object[] args = { field.getName() };
      formatter.applyPattern(bundle.getString("sendCheck"));
      errMsgs.add(formatter.format(args));
    }

    if (field.isSender()) {
      sender = field.getValue();
    } else if (field.isRecipient()) {
      recipient = field.getValue();
    } else if (field.isSenderName()) {
      senderName = field.getValue();
    } else if (field.isSubject()) {
      subject = field.getValue();
    } else if (field.isMessageBody()) {
      textMsg.append(field.getValue() + "\n\n");
    } else if (!Utils.isNullOrEmpty(field.getValue())) {
      textMsg.append(field.getName() + ":\n" + field.getValue() + "\n\n\n");
    }
  }
  
  String textMsgString = textMsg.toString();

  if (!Utils.checkAddress(recipient)) {
    errMsgs.add(bundle.getString("sendNoRecipient"));
  }

  if (!Utils.checkAddress(sender)) {
    errMsgs.add(bundle.getString("sendNoSender"));
  }

  if (errMsgs.size() == 0) {
    try {
      InternetAddress senderAddress = new InternetAddress(sender);
      
      if (!Utils.isNullOrEmpty(senderName)) {
        senderAddress.setPersonal(senderName);
      }
      
      InternetAddress recipientAddress = new InternetAddress(recipient);
      
      Properties props = new Properties();
      props.put("mail.smtp.host", webApp.getConfiguration().getMailServer());
      final String smtpUsername = webApp.getConfiguration().getSmtpUsername();
      final String smtpPassword = webApp.getConfiguration().getSmtpPassword();

      if (!Utils.isNullOrWhitespace(smtpUsername)) {
        props.put("mail.smtp.auth", "true");
      }

      Session mailSession = Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(smtpUsername, smtpPassword);
        }
      });      
      
      // Copy for the recipient
      MimeMessage outMsg = new MimeMessage(mailSession);
      outMsg.setFrom(senderAddress);
      outMsg.addRecipient(Message.RecipientType.TO, recipientAddress);
      outMsg.setSubject(Utils.isNullOrEmpty(subject) ?
        "Message from " + request.getServerName() : subject);
      outMsg.setHeader("Content-Transfer-Encoding", "8bit");
      outMsg.setText(textMsgString);

      OutputStream os = null;
      
      try {
        File msgDir = webApp.getFile(Finals.MESSAGES_PATH);
        msgDir.mkdirs();
        os = new FileOutputStream(new File(msgDir, "msg" +
            WebUtils.numericDateFormatter.format(new Date()) + ".txt"));
        outMsg.writeTo(os);
      } catch (Exception ex) {
        webApp.log("Can't save mail message to disk", ex);
      } finally {
        if (os != null) {
          try {
            os.close();
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }

      Transport.send(outMsg);

/* disabled to avoid spam  (thanks to Matthijs Dekker for having pointed this out)
      // Copy for the sender
      outMsg = new MimeMessage(mailSession);
      outMsg.setFrom(senderAddress);
      outMsg.addRecipient(Message.RecipientType.TO, senderAddress);
      outMsg.setSubject(Utils.isNullOrEmpty(subject) ?
        "You sent this message to " + request.getServerName() :
        "[Your copy of] " + subject);
      outMsg.setHeader("Content-Transfer-Encoding", "8bit");
      outMsg.setText(textMsgString);
      Transport.send(outMsg);
*/
      fields.clear(); // message has been sent, so clear all fields
    } catch (Exception ex) {
      webApp.log("send failed", ex);
      errMsgs.add(bundle.getString("sendFailed"));
    }
  }

  if (errMsgs.size() == 0) {
%>
    <fmt:message key="sendOk" />
<%
  } else {
%>
    <fmt:message key="sendError" />
    <ul>
<%
    for (int i = 0; i < errMsgs.size(); i++) {
%>
      <li><%= errMsgs.get(i) %></li>
<%
    }
%>
    </ul>
<%
    pageContext.include("/" + webApp.getAdminPath() + "/mailform.jsp");
  }
%>

</body>
</html>
