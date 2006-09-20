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
<%@ page import="org.meshcms.core.*" %>
<%@ page import="org.meshcms.util.*" %>
<%@ page import="org.meshcms.webui.*" %>
<%@ page import="javax.mail.*" %>
<%@ page import="javax.mail.internet.*" %>
<jsp:useBean id="webSite" scope="request" type="org.meshcms.core.WebSite" />

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

  Path pagePath = webSite.getRequestedPath(request);
  String cp = request.getContextPath();

  Locale locale = WebUtils.getPageLocale(pageContext);
  ResourceBundle pageBundle = ResourceBundle.getBundle
      ("org/meshcms/webui/Locales", locale);
  MessageFormat formatter = new MessageFormat("", locale);

  boolean sent = false;
  List fields = null;
  Path cachePath = webSite.getRepositoryPath().add(pagePath);
  webSite.getFile(cachePath).mkdirs();
  cachePath = cachePath.add(moduleCode + ".xml");
  File cacheFile = webSite.getFile(cachePath);
  
  if (request.getMethod().equalsIgnoreCase("post") &&
      moduleCode.equals(request.getParameter("post_modulecode"))) {
    WebUtils.setBlockCache(request);
    List errMsgs = new ArrayList();
    fields = (List) webSite.loadFromXML(cachePath);
    Iterator iterator = fields.iterator();
    String sender = null;
    String senderName = null;
    String subject = null;
    String recipient = null;
    StringBuffer textMsg = new StringBuffer();

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
        formatter.applyPattern(pageBundle.getString("sendCheck"));
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
        textMsg.append(field.getValue()).append("\n\n");
      } else if (!Utils.isNullOrEmpty(field.getValue())) {
        textMsg.append(field.getName()).append(":\n").append(field.getValue()).append("\n\n\n");
      }
    }

    String textMsgString = textMsg.toString();

    if (!Utils.checkAddress(recipient)) {
      errMsgs.add(pageBundle.getString("sendNoRecipient"));
    }

    if (!Utils.checkAddress(sender)) {
      errMsgs.add(pageBundle.getString("sendNoSender"));
    }

    if (errMsgs.size() == 0) {
      try {
        InternetAddress senderAddress = new InternetAddress(sender);

        if (!Utils.isNullOrEmpty(senderName)) {
          senderAddress.setPersonal(senderName);
        }

        InternetAddress recipientAddress = new InternetAddress(recipient);
        Session mailSession = WebUtils.getMailSession(webSite);
        MimeMessage outMsg = new MimeMessage(mailSession);
        outMsg.setFrom(senderAddress);
        outMsg.addRecipient(Message.RecipientType.TO, recipientAddress);
        outMsg.setSubject(Utils.isNullOrEmpty(subject) ?
          "Message from " + request.getServerName() : subject);
        outMsg.setHeader("Content-Transfer-Encoding", "8bit");
        outMsg.setHeader("X-MeshCMS-Log", "Sent from " + request.getRemoteAddr() +
            " at " + new Date() + " using page /" + pagePath);
        outMsg.setText(textMsgString);
        Transport.send(outMsg);
        sent = true;
      } catch (Exception ex) {
        webSite.log("send failed", ex);
        errMsgs.add(pageBundle.getString("sendFailed"));
      }
    }

    out.println("<p>");
    
    if (errMsgs.size() == 0) {
      out.println(pageBundle.getString("sendOk"));
    } else {
      out.println(pageBundle.getString("sendError"));
      out.println("<ul>");

      for (int i = 0; i < errMsgs.size(); i++) {
        out.println("<li>" + errMsgs.get(i) + "</li>");
      }

      out.println("</ul>");
    }

    out.println("</p>");
  } else {
    fields = new ArrayList();
    File[] files = md.getModuleFiles(webSite, false);

    if (files == null || files.length != 1) {
      FormField tempField = new FormField();
      String recipient = md.getArgument();

      if (Utils.isNullOrEmpty(recipient)) {
        recipient = request.getParameter("argument");
      }

      tempField.setParameter("recipient:" + recipient);
      fields.add(tempField);

      tempField = new FormField();
      tempField.setParameter(pageBundle.getString("mailName"));
      tempField.setParameter("sendername");
      fields.add(tempField);

      tempField = new FormField();
      tempField.setParameter(pageBundle.getString("mailAddress"));
      tempField.setParameter("email");
      tempField.setParameter("sender");
      fields.add(tempField);

      tempField = new FormField();
      tempField.setParameter(pageBundle.getString("mailMessage"));
      tempField.setParameter("textarea");
      tempField.setParameter("messagebody");
      fields.add(tempField);

      tempField = new FormField();
      tempField.setParameter(pageBundle.getString("mailSend"));
      tempField.setParameter("submit");
      fields.add(tempField);
    } else {
      WebUtils.updateLastModifiedTime(request, files[0]);
      String[] lines = Utils.readAllLines(files[0]);
      FormField field = null;

      for (int i = 0; i < lines.length; i++) {
        String line = lines[i].trim();

        if (line.equals("")) {
          if (field != null) {
            fields.add(field);
            field = null;
          }
        } else {
          if (field == null) {
            field = new FormField();
          }

          field.setParameter(line);
        }
      }

      if (field != null) {
        fields.add(field);
      }
    }
  
    if (!webSite.storeToXML(fields, cachePath)) {
      WebUtils.setBlockCache(request);
    }
  }
  
  if (!sent) {
    Iterator iter = fields.iterator();
    boolean hasRecipient = false;

    while (iter.hasNext()) {
      FormField fld = (FormField) iter.next();

      if (fld.isRecipient() && Utils.checkAddress(fld.getValue())) {
        hasRecipient = true;
        break;
      }
    }

    if (hasRecipient) {
%>
    <form method="post">
    <input type="hidden" name="post_modulecode" value="<%= moduleCode %>" />
    <div class="formfields">
<%
      Iterator iterator = fields.iterator();
      FormField field;

      while (iterator.hasNext()) {
        field = (FormField) iterator.next();
        String code = field.getCode();

        if (!Utils.isNullOrEmpty(code)) {
          int type = field.getType();

          if (type == FormField.TEXT ||
              type == FormField.EMAIL ||
              type == FormField.NUMBER) {
            %><div class="<%= field.isRequired() ? "requiredfieldname" :
                              "fieldname" %>">
                <label for="mcmf_<%= field.getCode() %>"><%= field.getName() %></label>
              </div><%

            if (field.getRows() == 1) {
              %><input type="text" name="<%= field.getCode() %>"
                 id="mcmf_<%= field.getCode() %>"
                 value="<%= Utils.noNull(field.getValue()) %>"
                 style="width: 98%;" /><%
            } else {
              %><textarea name="<%= field.getCode() %>"
                 id="mcmf_<%= field.getCode() %>"
                 style="width: 98%; height: <%= field.getRows()
                 %>em;"><%= Utils.encodeHTML(field.getValue()) %></textarea><%
            }
          } else if (type == FormField.SELECT_OPTION) {
            %><div class="<%= field.isRequired() ? "requiredfieldname" :
                              "fieldname" %>">
                <label for="mcmf_<%= field.getCode() %>"><%= field.getName() %></label>
              </div>
              <select name="<%= field.getCode() %>" id="mcmf_<%= field.getCode() %>"><%
            String[] options = field.getOptions();

            for (int i = 0; i < options.length; i++) {
              %><option value="<%= options[i] %>"<%=
               Utils.compareStrings(options[i], field.getValue(), false) ?
               "selected=\"selected\"" : "" %>><%= options[i] %></option>
              <%
            }

            %></select><%
          }
        }
      }
      %></div>
      <div class="fieldbuttons"><%
      iterator = fields.iterator();

      while (iterator.hasNext()) {
        field = (FormField) iterator.next();
        int type = field.getType();

        if (type == FormField.SUBMIT) {
          %><input type="submit" value="<%= field.getName() %>"> <%
        } else if (type == FormField.RESET) {
          %><input type="reset" value="<%= field.getName() %>"> <%
        }
      }
      %></div>

    </form>
<%
    }
  }
%>
