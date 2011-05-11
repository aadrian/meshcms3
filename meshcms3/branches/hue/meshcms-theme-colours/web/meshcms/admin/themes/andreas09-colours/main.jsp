<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="meshcms-taglib" prefix="cms" %>
<cms:setlocale value="en" /> 

<html>
<head>
<title><cms:pagetitle /> [MeshCMS]</title>
<cms:defaultcss />
<cms:pagehead /> 
<cms:editor> 
<cms:module location="color" alt="" />
</head>

<body id="realbody">
<div id="container"> 
  <div id="sitename"> 
    <h1><cms:pagetitle /></h1>
    <h2>MeshCMS - Open Source Content Management System</h2>
  </div>

  <div id="mainmenu"><cms:listmenu items="firstlevel" current="link" currentStyle="current" /></div>

  <div id="wrap"> 
    <div id="rightside">
      <h1>Navigation</h1>
      <cms:listmenu items="onpath,lastlevel,children" style="linklist" />

      <cms:module location="right" alt="" />
	  
      <cms:ifuser> 
      <h1>User Menu</h1>
      <p><cms:adminmenu separator="<br />" /></p>
      </cms:ifuser> 

      <cms:ifnotediting>
      <h1>Search</h1>
      <p class="searchform"> 
        <input type="text" alt="Search" class="searchbox" />
        <input type="submit" value="Go!" class="searchbutton" />
      </p>
      </cms:ifnotediting>
    </div>
    <div id="contentalt" >
      <cms:module location="top" alt="" />
      <cms:pagebody /> 
      <cms:module location="bottom" alt="" />
      <cms:mailform /> 
      <p style="text-align: right;"><cms:lastmodified pre="Last modified: " /></p>
    </div>
	
    <div class="clearingdiv">&nbsp;</div>
  </div>
</div>

<div id="footer">&copy; 2004-2006 <a href="http://www.cromoteca.com/">Luciano 
  Vernaschi</a> | Design by <a href="http://andreasviklund.com">Andreas Viklund</a></div>
</cms:editor>
</body>
</html>
