<%@ taglib uri="meshcms-taglib" prefix="cms" %>

<html>
<head>
<script language="javascript" type="text/javascript" src="<cms:themepath />/leftframe.js"></script>
<title><cms:pagetitle /></title>
  <cms:defaultcss />
  <cms:pagehead />
<script language="javascript">
<!--
	function setH() {
		var s = document.getElementById ('stick');
		var y;
		if (self.innerHeight) {// all except Explorer
			y = self.innerHeight;
		} else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
			y = document.documentElement.clientHeight;
		} else if (document.body) { // other Explorers
			y = document.body.clientHeight;
		}
		s.style.height = (y-100) + 'px';
	}

//-->
</script>
</head>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"><cms:editor>
<div class="top">
<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0" class="topbody">
 <tr>
  <td>MeshCMS</td>
  <td align="right" valign="bottom">&nbsp;</td>
 </tr>
</table>
</div>
<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
 <tr>
  <td class="left" valign="top">		<table cellspacing="0" cellpadding="0" align="center" class="navOpened" width="100%" id="sw_n0">
			  			<tr>
    			<th valign="top"  onClick="opentree('sw_n0')"><table border="0" cellspacing="0" cellpadding="0" width="100%" class="navTitle" onMouseOver="mover(this)" onMouseOut="mout(this)">
	<tr>
		<td class="titleLeft"><img src="<cms:themepath />/1x1.gif" border="0" alt="" valign="top" width="14" heigth="1"/></td>
		<td width="100%" class="titleText">Site Menu</td>
		<td class="titleHandle"><img src="<cms:themepath />/1x1.gif" border="0" alt="" valign="top" width="20" heigth="1"/></td>
		<td class="titleRight"><img src="<cms:themepath />/1x1.gif" border="0" alt="" valign="top" width="3" heigth="1"/></td>
	</tr>
</table>
</th>
    		</tr>
			    		<tr>
    			<td class="modulecontent"><div class="modulecontent">
					<cms:simplemenu bullet="&diams;" expand="true" space="16" />
				</div></td>
			</tr>
      	</table>
		<br />
		
		
		
		
		
			<table cellspacing="0" cellpadding="0" align="center" class="navOpened" width="100%" id="sw_n1">
			  			<tr>
    			<th valign="top"  onClick="opentree('sw_n1')"><table border="0" cellspacing="0" cellpadding="0" width="100%" class="navTitle" onMouseOver="mover(this)" onMouseOut="mout(this)">
	<tr>
		<td class="titleLeft"><img src="<cms:themepath />/1x1.gif" border="0" alt="" valign="top" width="14" heigth="1"/></td>
		<td width="100%" class="titleText">Admin Menu</td>
		<td class="titleHandle"><img src="<cms:themepath />/1x1.gif" border="0" alt="" valign="top" width="20" heigth="1"/></td>
		<td class="titleRight"><img src="<cms:themepath />/1x1.gif" border="0" alt="" valign="top" width="3" heigth="1"/></td>
	</tr>
</table>
</th>
    		</tr>
			    		<tr>
    			<td class="modulecontent"><div class="modulecontent">
    				<ul><li><cms:adminmenu separator="</li><li>" /></li></ul>
    			</div></td>
			</tr>
      	</table>
		<br />
                    <cms:ifmodule location="left">
			<table cellspacing="0" cellpadding="0" align="center" class="navOpened" width="100%" id="sw_n2">
			    		<tr>
    			<td class="modulecontent"><div class="modulecontent">
            <cms:module location="left" date="normal" />
    			</div></td>
			</tr>
      	</table>
		<br />
                    </cms:ifmodule>
	  </td>

 <script language="javascript">
 <!--
	var y;
	if (self.innerHeight) {// all except Explorer
		y = self.innerHeight;
	} else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
		y = document.documentElement.clientHeight;
	} else if (document.body) { // other Explorers
		y = document.body.clientHeight;
	}
	document.write ('<td id="stick" style="height: '+(y-100)+'px; width: 1px; background-color: #cbcbd5" valign="top"><img src="<cms:themepath />/1x1.gif" width="1" height="1" border="0" alt=""/></td>');

 //-->
 </script>
  <td class="main" valign="top">

  <table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
   <tr>
		<td  height="27" class="util">
			<div align="left">
			  <table width="100%"  border="0" cellpadding="0" cellspacing="0" class="pathbar">
                <tr>
                  <td align="left" class="pathbar">
				<span class="pathway"><cms:breadcrumbs separator="\\" pre="C:\\" mode="links" /></span></td>
                </tr>
              </table>
	  </div></td>
   </tr>
   <tr>
    <td valign="top">


<table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
 <tr>
  <td valign="top" class="maincontent">
         
<table width="100%" cellspacing="0" cellpadding="0" border="0">
<tr>
<td valign="top" colspan="2">
<div class="content-header"><table cellpadding="0" cellspacing="0" border="0" width="100%" class="contentpaneopentitle">
  <tr><td class="moduleheaderleft"><img src="<cms:themepath />/1x1.gif" width="3" height="19" border="0" alt=""/></td><td class="contentheading" width="100%">
			<cms:pagetitle />
	</td>
    		      </tr>

</table></div>
<table cellpadding="0" cellspacing="1" border="0" width="100%" class="contentpaneopen">
  <tr>
    <td width="70%" align="left" valign="top">
      	</td>
    <td valign="top" align="right">
	    </td>
  </tr>

  <tr>
    <td valign="top" colspan="2">
    	<cms:pagebody />
	</td>
  </tr>
      </table>
<br />
</td>
</tr>
<tr>
<td width="50%" valign="top"><cms:module location="bottom" date="normal" /></td>
</tr>
<tr>
<td width="50%" valign="top"><cms:mailform /></td>
</tr>
</table>     </td>
  <td width="160" valign="top" class="right">
      		<table cellspacing="0" cellpadding="0" align="center" class="moduletable" width="100%">
			  			<tr>
    			<th valign="top" ><table width="100%" cellspacing="0" cellpadding="0" border="0">
	<tr>
		<td class="moduleheaderleft"><img src="<cms:themepath />/1x1.gif" width="3" height="19" border="0" alt=""/></td>
		<td class="moduleheadertext">Related links:</td>
	</tr>
</table></th>
    		</tr>
			    		<tr>
    			<td class="modulecontent"><div class="modulecontent">
    				<cms:links separator="<br />" current="false" />
				</div></td>
			</tr>
      	</table>
		<br />
     </td>
 </tr>
</table>

    </td>
   </tr>
  </table>
   
  </td>
 </tr>
</table>

</cms:editor></body>
</html>

