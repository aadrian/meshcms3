<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="meshcms-taglib" prefix="cms" %>
<cms:setlocale value="en" />

<html>
  <head>
    <title><cms:pagetitle /> [MeshCMS]</title>
    <cms:defaultcss />
    <cms:pagehead />
  </head>

  <!-- Layout from http://tjkdesign.com/articles/one_html_markup_many_css_layouts.asp -->
  <body>
    <cms:editor>
      <div id="header">
        <h1>
          <cms:pagetitle />
        </h1>
      </div>

      <div id="content">
        <div class="breadcrumbs">
          <cms:breadcrumbs mode="links" separator=" &raquo; " pre="You are viewing: " />
        </div>

        <div class="maincontent">
          <cms:module location="top" alt="" />
          <cms:pagebody />
          <cms:module location="bottom" alt="" />
          <cms:mailform />
        </div>
      </div>

      <div id="leftcolumn">
        <div class="box">
          <div class="boxtitle">
            Site Menu
          </div>

          <div class="boxcontent">
            <cms:simplemenu bullet="" space="16" />
          </div>
        </div>

        <cms:ifmodule location="left">
          <div class="box">
            <div class="boxcontent">
              <cms:module location="left" alt="" />
            </div>
          </div>
        </cms:ifmodule>
      </div>

      <div id="rightcolumn">
        <cms:ifuser>
          <div class="box">
            <div class="boxtitle">
              User Menu
            </div>

            <div class="boxcontent">
              &nbsp;<cms:adminmenu separator="<br />&nbsp;" />
            </div>
          </div>
        </cms:ifuser>

        <cms:ifmodule location="right">
          <div class="box">
            <div class="boxcontent">
              <cms:module location="right" alt="" />
            </div>
          </div>
        </cms:ifmodule>
      </div>

      <div id="footer">
        <cms:lastmodified pre="Last modified: " post=" |" />
        Powered by <a href="http://www.meshcms.org/">MeshCMS</a>
      </div>
    </cms:editor>
  </body>
</html>
