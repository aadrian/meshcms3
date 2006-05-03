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

<%@ page import="java.util.*" %>
<%@ page import="com.cromoteca.meshcms.*" %>
<%@ page import="com.cromoteca.util.*" %>
<jsp:useBean id="webApp" scope="application" type="com.cromoteca.meshcms.WebApp" />
<jsp:useBean id="fields" scope="session" class="java.util.ArrayList" />

<%
  String cp = request.getContextPath();
  String mp = request.getParameter("modulepath");
  String theme = new Path(request.getAttribute(Finals.THEME_PATH_ATTRIBUTE)).getLastElement();
  
  if (!Utils.isNullOrEmpty(theme)) {
    theme = '?' + Finals.THEME_FILE_ATTRIBUTE + '=' + theme;
  }
%>

<form action="<%= response.encodeURL(cp + mp + "/send.jsp" + theme) %>" method="POST">
<input type="hidden" name="modulepath" value="<%= mp %>" />
<table align="center" border="0" cellspacing="0"
 cellpadding="0" width="400"><tr><td witdh="100%">

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
                          "fieldname" %>"><%= field.getName() %></div><%

        if (field.getRows() == 1) {
          %><input type="text" name="<%= field.getCode() %>"
             value="<%= Utils.noNull(field.getValue()) %>"
             style="width:100%;" /><%
        } else {
          %><textarea name="<%= field.getCode() %>"
             style="width:100%; height:<%= field.getRows()
             %>em;"><%= Utils.encodeHTML(field.getValue()) %></textarea><%
        }
      } else if (type == FormField.SELECT_OPTION) {
        %><div class="fieldname"><%= field.getName() %></div><select
           name="<%= field.getCode() %>"><%
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

  iterator = fields.iterator();
  %><div class="fieldbuttons"><%

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

</td></tr></table>
</form>
