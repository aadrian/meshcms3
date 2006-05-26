MeshCMS Readme

------------------------------------------------------------------------------
This file contains some basic information about MeshCMS. For a more detailed description and guide, open the userguide/index.html file.
------------------------------------------------------------------------------
MeshCMS is a content management system written in Java and based on SiteMesh (see Credits below).
MeshCMS provides the ability to edit a page using a web browser, then updates all navigation components automatically. It doesn't use databases: the site is file-based, so one can choose to make changes using MeshCMS or using local tools, like Dreamweaver, Notepad and so on.
MeshCMS is released under the GNU General Public License. See license.txt for details.

----------------------
0. Contents
----------------------
 1. Installing MeshCMS
 2. Using MeshCMS
 3. Credits

----------------------
1. Installing MeshCMS
----------------------
Deploy meshcms.war to your application server. This gives you a fresh web application containing the basic elements, a home page and some sample themes.

----------------------
2. Using MeshCMS
----------------------
Open your new web application (usually http://localhost:8080/meshcms/). You can login using username "admin" and password "admin".

----------------------
3. Credits
----------------------
MeshCMS uses free software from other companies/developers:
- SiteMesh*
    http://www.opensymphony.com/sitemesh/
- TinyMCE*
    http://tinymce.moxiecode.com/
- XTree*, XMenu*
    http://webfx.eae.net/
- Jakarta Commons FileUpload*
    http://jakarta.apache.org/commons/fileupload/
- JavaMail*
    http://java.sun.com/products/javamail/
- jcrypt
    http://locutus.kingwoodcable.com/jfd/crypt.html
- Nuvola KDE theme (control panel icons)
    http://www.icon-king.com/
- Tigra Menu & Tigra Tree Menu**
    http://www.softcomplex.com/
- ALib Menu*
    http://sourceforge.net/projects/alib/
- XStream*
    http://xstream.codehaus.org/
- SVNAnt***
    http://subclipse.tigris.org
- Tag Library Documentation Generator***
    https://taglibrarydoc.dev.java.net/
- Checkstyle***
    http://checkstyle.sourceforge.net/
    
*: these components are included unmodified, so you can upgrade them manually if you want to
**: this component is not included in the MeshCMS release, but there is a custom tag that allows you to create a JavaScript menu easily with this script. Connect to its homepage to download it for use with MeshCMS
***: these are used only in the build and the developement process.