<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="meshcms-taglib" prefix="mesh" %>
<mesh:setlocale defaultValue="en" redirectRoot="true" />

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title><mesh:pagetitle /> [<mesh:info id="name" />]</title>
    <mesh:defaultcss />
    <mesh:pagehead />
    <meta name="description" content="<mesh:info id="description" />" />
    <meta name="keywords" content="<mesh:info id="keywords" />" />
    <meta name="author" content="<mesh:info id="author" /> (<mesh:info id="authorurl" />)" />
    <meta http-equiv="Content-Type" content="text/html; charset=<%= org.meshcms.core.WebSite.SYSTEM_CHARSET %>" />
  </head>

  <body id="cmsbody"
   style="background: url(<mesh:searchfile name="topbg.jpg" defaultName="topbg.jpg" />) repeat-x top center;">
    <mesh:editor>
      <div id="header">
        <h1 id="cmspagetitle">
          <mesh:pagetitle />
        </h1>

        <h2 id="cmsdescription">
          <mesh:info id="name" />
        </h2>
      </div>

        <div id="pagecontentcolumn">
          <div id="pagecontent">
            <div id="mainarea">
              <div class="cmsmodule">
                <h3><mesh:moduletitle location="top" /></h3>
                <mesh:module location="top" alt="" />
              </div>

              <div class="pagebody">
                <mesh:pagebody />
              </div>

              <div class="cmsmodule">
                <h3><mesh:moduletitle location="bottom" /></h3>
                <mesh:module location="bottom" alt="" />
              </div>
            </div>
          </div>
        </div>

        <div id="additionalcolumn">
          <div class="secondarycolumn">
            <div class="cmsmenu">
              <ul>
                <li><mesh:adminmenu separator="</li><li>" /></li>
              </ul>
            </div>

            <mesh:ifmodule location="right">
              <div class="cmssidemodule">
                <h3><mesh:moduletitle location="right" /></h3>
                <mesh:module location="right" alt="" />
              </div>
            </mesh:ifmodule>
          </div>
        </div>

        <div id="navigationcolumn">
          <div class="secondarycolumn">
            <div class="cmsmenu">
              <mesh:listmenu currentStyle="currentpage" />
            </div>

            <mesh:ifmodule location="left">
              <div class="cmssidemodule">
                <h3><mesh:moduletitle location="left" /></h3>
                <mesh:module location="left" alt="" />
              </div>
            </mesh:ifmodule>
          </div>
        </div>

      <div id="footer">
        <mesh:lastmodified pre="Last modified: " post=" |" />
        &copy; <a href="<mesh:info id="authorurl" />"><mesh:info id="author" /></a> |
        Powered by <a href="http://www.cromoteca.com/meshcms/">MeshCMS <%= org.meshcms.core.WebSite.VERSION_ID %></a>
      </div>
    </mesh:editor>
  </body>
</html>

