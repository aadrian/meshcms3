<%@ taglib uri="meshcms-taglib" prefix="cms" %>

<html>

<head>
  <title><cms:pagetitle /></title>
  <cms:defaultcss />
  <cms:pagehead />
</head>

<body style="margin: 0px;"><cms:editor>

<div align="center">
	<table border="0" width="780" cellspacing="0" cellpadding="0" id="table1" background="<cms:themepath />/bkgr.png">
		<tr>
			<td>
			<img border="0" src="<cms:themepath />/header.png" width="780" height="150"></td>
		</tr>
		<tr>
			<td background="<cms:themepath />/menubar.png" height="30">
				<img hspace="4" src="<cms:themepath />/arrow.png" border="0" alt="arrow" />
				<cms:breadcrumbs mode="links" separator=" &middot; " />
			</td>
		</tr>
		<tr>
			<td>
			<table border="0" width="100%" cellspacing="0" cellpadding="0" height="400" id="table2">
				<tr>
					<td valign="top" width="170">
					<table border="0" width="100%" id="table3" cellspacing="5" cellpadding="5">
						<tr>
							<td>
								<cms:simplemenu expand="true" />
							</td>
						</tr>
						<tr>
							<td>
								<br />
								<cms:module location="left" date="normal" />
							</td>
						</tr>
						<tr>
							<td>
								<cms:adminmenu separator="<br />" />
							</td>
						</tr>
					</table>
					</td>
					<td valign="top" background="<cms:themepath />/pixel.png" width="1">
					<img border="0" src="<cms:themepath />/pixel.png" width="1" height="1"></td>
					<td valign="top">&nbsp;<div align="center">
				<table border="0" width="95%" cellspacing="0" cellpadding="15" id="table5">
					<tr>
						<td>
							<cms:pagebody />
						</td>
					</tr>
					<tr>
						<td>
							<cms:module location="bottom" date="normal" />
						</td>
					</tr>
					<tr>
						<td>
							<cms:mailform />
						</td>
					</tr>
				</table>
					</div>
					&nbsp;</td>
					
<td valign="top" background="<cms:themepath />/pixel.png" width="1">
					<img border="0" src="<cms:themepath />/pixel.png" width="1" height="1"></td>
					<td valign="top" width="170">
					<table border="0" width="100%" cellspacing="5" cellpadding="5" id="table4">
						<tr>
							<td>
								<br />
								<cms:module location="right" date="normal" />
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td background="<cms:themepath />/menubar.png" height="30" align="center">
			<a target="_blank" href="http://www.pixelbunyip.com">
			<font color="#CCCCCC"><b><span style="text-decoration: none">Designed 
			by PixelBunyiP</span></b></font></a></td>
		</tr>
		<tr>
			<td align="center">&nbsp;<cms:lastmodified pre="Last modified: " />&nbsp;</td>
		</tr>
	</table>
</div>

</cms:editor></body>

</html>