<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="meshcms-taglib" prefix="cms" %>
<cms:setlocale value="en" />

<!-- Taken from http://alistapart.com/articles/holygrail -->
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <title><cms:pagetitle /> [MeshCMS]</title>
    <cms:defaultcss />
    <cms:pagehead />
  </head>

  <body>
    <cms:editor>
      <div id="header"></div>

      <div id="container">
        <div class="column" id="center">
          <div class="breadcrumbs">
            <cms:breadcrumbs mode="links" separator=" &raquo; " pre="You are viewing: " />
          </div>
          
          <div class="maincontent">
            <cms:module location="top" alt="" />
            <cms:pagebody />
            <cms:module location="bottom" alt="" />
          </div>
        </div>

        <div class="column" id="left">
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

        <div class="column" id="right">
          <cms:ifmodule location="right">
            <div class="box">
              <div class="boxcontent">
                <cms:module location="right" alt="" />
              </div>
            </div>
          </cms:ifmodule>

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
        </div>
      </div>

      <div id="footer-wrapper">
        <div id="footer">
          <cms:lastmodified pre="Last modified: " post=" |" />
          Powered by <a href="http://www.meshcms.org/">MeshCMS</a>
        </div>
      </div>
    </cms:editor>
  </body>
</html>
