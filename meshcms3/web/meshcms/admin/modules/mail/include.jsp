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
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webSite" scope="request" type="com.cromoteca.meshcms.WebSite" />
<jsp:useBean id="fields" scope="session" class="java.util.ArrayList" />

<%
  WebUtils.setBlockCache(request);
  fields.clear();
  File[] files = WebUtils.getModuleFiles(webSite, request, false);
  
  if (files == null || files.length != 1) {
    FormField tempField = new FormField();
    String recipient = request.getParameter("recipient");
    
    if (Utils.isNullOrEmpty(recipient)) {
      recipient = request.getParameter("argument");
    }
    
    tempField.setParameter("recipient:" + recipient);
    fields.add(tempField);
    
    ResourceBundle pageBundle = ResourceBundle.getBundle
        ("com/cromoteca/meshcms/Locales", WebUtils.getPageLocale(pageContext));
        
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
    String mp = request.getParameter("modulepath");
    pageContext.include(mp + "/form.jsp?modulepath=" + mp);
  }
%>
