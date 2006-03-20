<%@ taglib uri="meshcms-taglib" prefix="cms" %>

<html>
<head>
<title><cms:pagetitle /></title>
<cms:defaultcss />
<cms:pagehead />
<script language="JavaScript" type="text/JavaScript">
<!--
function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}

function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}
//-->
</script>
</head>

<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"><cms:editor>
<table width="950" height="100%" border="0" align="center" cellpadding="0" cellspacing="0" id="Table_01">
  <tr>
    <td height="100%" rowspan="5" background="<cms:themepath />/zak_bly_3col_09.png">
    <img src="<cms:themepath />/zak_bly_3col_09.png" width="16" height="20" alt=""></td>
    <td height="122" colspan="2" background="<cms:themepath />/zak_bly_3col_02.jpg"><img src="<cms:themepath />/zak_bly_3col_02.jpg" width="598" height="122" alt=""></td>
    <td height="122" colspan="2" valign="bottom" background="<cms:themepath />/zak_bly_3col_03.jpg"><h1><cms:pagetitle /></h1></td>
    <td height="100%" rowspan="5" background="<cms:themepath />/zak_bly_3col_13.png">
      <img src="<cms:themepath />/zak_bly_3col_13.png" width="14" height="20" alt=""></td>
  </tr>
  <tr>
    <td height="71" colspan="4" align="right" valign="bottom" background="<cms:themepath />/zak_bly_3col_05.jpg"><table width="230" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="125" valign="bottom">&nbsp;</td>
        <td width="165" valign="bottom">
           <div align="center">
             <input class="searchbox" type="text" name="searchword" height="16" size="15" value="" />
             <input type="hidden" name="option" value="search" />
          </div>
        </td>
        <td width="10">&nbsp;</td>
      </tr>
    </table>&nbsp;</td>
  </tr>
  <tr>
    <td height="34" valign="top" background="<cms:themepath />/zak_bly_3col_06.gif">
      <img src="<cms:themepath />/zak_bly_3col_06.gif" width="231" height="34" alt=""></td>
    <td height="34" colspan="2" align="center" background="<cms:themepath />/zak_bly_3col_07.gif"><cms:breadcrumbs mode="links" style="pathway" separator=" &middot; " /></td>
    <td height="34" background="<cms:themepath />/zak_bly_3col_08.gif"><img src="<cms:themepath />/zak_bly_3col_08.gif" width="230" height="34" alt=""></td>
  </tr>
  <tr>
    <td height="100%" valign="top" background="<cms:themepath />/zak_bly_3col_10.gif"><table width="100%"  border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="19">&nbsp;</td>
        <td width="193" align="left" valign="top">
          <table cellpadding="0" cellspacing="0" class="moduletable">
            <tr><th valign="top">Main Menu</th></tr>
            <tr><td><cms:simplemenu bullet="" /></td></tr>
          </table>

          <cms:module location="left" style="moduletable" />

          <table cellpadding="0" cellspacing="0" class="moduletable">
            <tr><th valign="top">CMS Menu</th></tr>
            <tr><td><cms:adminmenu separator="<br />" /></td></tr>
          </table>
        </td>
        <td width="19">&nbsp;</td>
      </tr>
    </table>
       </td>
    <td height="100%" colspan="2" valign="top" background="<cms:themepath />/zak_bly_3col_11.gif"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="3%" rowspan="4">&nbsp;</td>
        <td width="46%" align="left" valign="top"><cms:module location="user1" style="moduletable" /></td>
        <td width="2%">&nbsp;</td>
        <td width="46%" align="left" valign="top"><cms:module location="user2" style="moduletable" /></td>
        <td width="3%" rowspan="4">&nbsp;</td>
      </tr>
      <tr>
        <td colspan="3">&nbsp;</td>
        </tr>
      <tr>
        <td colspan="3"><cms:pagebody /></td>
        </tr>
      <tr>
        <td colspan="3"><cms:module location="afterbody" style="moduletable" /></td>
        </tr>
    </table> </td>
    <td height="100%" valign="top" background="<cms:themepath />/zak_bly_3col_12.gif"><table width="100%"  border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="20">&nbsp;</td>
        <td width="191" align="left" valign="top"><cms:module location="right" style="moduletable" /></td>
        <td width="19">&nbsp;</td>
      </tr>
    </table> </td>
  </tr>
  <tr>
    <td height="68" background="<cms:themepath />/zak_bly_3col_16.png"><img src="<cms:themepath />/zak_bly_3col_16.png" width="231" height="68" alt=""></td>
    <td height="68" colspan="2" background="<cms:themepath />/zak_bly_3col_17.png">
      <img src="<cms:themepath />/zak_bly_3col_17.png" width="459" height="68" alt=""></td>
    <td height="68" background="<cms:themepath />/zak_bly_3col_18.png">
      <img src="<cms:themepath />/zak_bly_3col_18.png" width="230" height="68" alt=""></td>
  </tr>
  <tr>
    <td>
      <img src="<cms:themepath />/spacer.gif" width="16" height="1" alt=""></td>
    <td>
      <img src="<cms:themepath />/spacer.gif" width="231" height="1" alt=""></td>
    <td>
      <img src="<cms:themepath />/spacer.gif" width="367" height="1" alt=""></td>
    <td>
      <img src="<cms:themepath />/spacer.gif" width="92" height="1" alt=""></td>
    <td>
      <img src="<cms:themepath />/spacer.gif" width="230" height="1" alt=""></td>
    <td>
      <img src="<cms:themepath />/spacer.gif" width="14" height="1" alt=""></td>
  </tr>
</table>
</cms:editor></body>
</html>