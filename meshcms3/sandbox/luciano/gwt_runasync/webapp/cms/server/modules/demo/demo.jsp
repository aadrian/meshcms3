<%@ page isELIgnored="false" session="false" %>
<jsp:useBean id="bean" scope="request" type="com.cromoteca.meshcms.server.core.ServerModule" />

<div id='<%= bean.getId() %>'>
  <dl>
    <dt>
      Text:
    </dt>
    <dd>
      <%= bean.getParameter("sample_text") %>
    </dd>

    <dt>
      Long Text:
    </dt>
    <dd>
      <%= bean.getParameter("sample_big_text") %>
    </dd>

    <dt>
      Rich Text:
    </dt>
    <dd>
      <%= bean.getParameter("sample_rich_text") %>
    </dd>

    <dt>
      Integer:
    </dt>
    <dd>
      <%= bean.getParameter("sample_integer") %>
    </dd>

    <dt>
      Boolean:
    </dt>
    <dd>
      <%= bean.getParameter("sample_boolean") %>
    </dd>

    <dt>
      Selection:
    </dt>
    <dd>
      <%= bean.getParameter("sample_selection") %>
    </dd>

    <dt>
      Path:
    </dt>
    <dd>
      <a href='<%= bean.getParameter("sample_path") %>'><%= bean.getParameter("sample_path") %></a>
    </dd>
  </dl>
</div>
