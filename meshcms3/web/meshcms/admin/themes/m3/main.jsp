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

  <body><cms:editor>
    <div id="header"></div>

    <div id="container">
      <div class="column" id="center">
        <cms:breadcrumbs mode="links" separator=" &raquo; " pre="You are viewing: " />
        <cms:module location="top" alt="" />
        <cms:pagebody />
        <cms:module location="bottom" alt="" />
      </div>

      <div class="column" id="left">
        <cms:listmenu />
        <cms:module location="left" alt="" />
        <cms:adminmenu />
      </div>

      <div class="column" id="right">
        <cms:module location="right" alt="" />
      </div>
    </div>

    <div id="footer-wrapper">
      <div id="footer">
        <cms:lastmodified pre="Last modified: " />
        | Powered by <a href="http://www.meshcms.org/">MeshCMS</a>
      </div>
    </div>
  </cms:editor></body>
</html>
