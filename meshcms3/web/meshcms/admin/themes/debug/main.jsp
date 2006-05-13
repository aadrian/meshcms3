<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="meshcms-taglib" prefix="cms" %>

<html>
<head>
<style type="text/css">
	.boxdiv {
		border: 1px dotted navy;
		padding: 20px;
	}
</style>
<title><cms:pagetitle /></title>
<cms:defaultcss />
<cms:pagehead />
</head>

<body id="realbody">
  <cms:editor>
    <div class="boxdiv">
      <p>Path:</p>
      <cms:breadcrumbs mode="links" separator=" &raquo; " />
    </div>

    <div class="boxdiv">
      <p>Title:</p>
      <cms:pagetitle />
    </div>

    <div class="boxdiv">
      <p>Body:</p>
      <cms:pagebody />
    </div>

    <cms:ifmodule location="module1">
      <p>Module:</p>
      <div class="boxdiv">
        <cms:module location="module1" date="normal" />
      </div>
    </cms:ifmodule>

    <cms:ifmailform>
      <p>Mail Form:</p>
      <div class="boxdiv">
        <cms:mailform />
      </div>
    </cms:ifmailform>

    <div class="boxdiv">
      <p>Menu:</p>
      <cms:simplemenu expand="true" />
    </div>

    <div class="boxdiv">
      <p>Admin:</p>
      <cms:adminmenu separator="<br/>" />
    </div>

    <div class="boxdiv">
      <p>Site Menu:</p>
      <cms:listmenu />
    </div>
  </cms:editor>
</body>
</html>
