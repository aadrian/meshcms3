<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="meshcms-taglib" prefix="cms" %>

<cms:setlocale defaultValue="en" /> 

<html>
  <head>
    <title><cms:pagetitle /> [MeshCMS]</title>
    <cms:defaultcss />
    <cms:pagehead /> 
    <meta name="description" content="<cms:info id="description" />" />
    <meta name="keywords" content="<cms:info id="keywords" />" />
    <meta name="author" content="<cms:info id="author" />" />
    <cms:alibmenu part="head" orientation="horizontal" />
  </head>

  <body id="realbody">
    <cms:editor> 
      <div id="container"> 
        <div id="sitename"> 
          <h1><cms:pagetitle /></h1>
          <h2><cms:info id="name" /></h2>
        </div>

        <div id="navigation"><cms:alibmenu part="body" orientation="horizontal" currentPathStyle="selected" /></div>

        <div id="wrap"> 
          <div id="rightside">
            <%-- To make this search form work, ensure that the Host (domain)
                 field in the configuration has been set correctly --%>
            <form id="searchform" action="http://www.google.com/search" method="get">
              <cms:lang id="en"><h1>Search</h1></cms:lang>
              <cms:lang id="it"><h1>Cerca</h1></cms:lang>
              <p class="searchform">
                <input type="hidden" name="as_sitesearch" value="<cms:info id="host" />"/>
                <input type="text" id="google_search" name="as_q" class="searchbox" />
                <input type="submit" value="Go!" class="searchbutton" />
              </p>
            </form>

            <cms:lang id="en"><h1>Navigation</h1></cms:lang>
            <cms:lang id="it"><h1>Navigazione</h1></cms:lang>
            <cms:listmenu items="onpath,lastlevel,children" style="linklist" />

            <%-- Display a languages menu --%>
            <cms:langmenu separator="</li><li>" pre="<ul class='linklist'><li>"
            post="</li></ul>" flags="true" />

            <cms:module location="right" alt="" />
	  
            <cms:lang id="en"><h1>User Menu</h1></cms:lang>
            <cms:lang id="it"><h1>Menu utente</h1></cms:lang>
            <p><cms:adminmenu separator="<br />" /></p>
          </div>

          <div id="contentalt" >
            <cms:module location="top" alt="" />
            <cms:pagebody /> 
            <cms:module location="bottom" alt="" />
            <cms:mailform /> 
            <p style="text-align: right;">
              <cms:lang id="en">
                <cms:lastmodified pre="Last modified: " />
              </cms:lang>
              <cms:lang id="it">
                <cms:lastmodified pre="Ultima modifica: " />
              </cms:lang>
            </p>
          </div>
	
          <div class="clearingdiv">&nbsp;</div>
        </div>
      </div>

      <div id="footer">&copy; <a href="<cms:info id="authorurl" />"><cms:info id="author" /></a>
      | Design by <a href="http://andreasviklund.com">Andreas Viklund</a></div>
    </cms:editor>
  </body>
</html>
