/*
 * MeshCMS | open source web content management system
 * more info at http://www.cromoteca.com/meshcms
 *
 * developed by Luciano Vernaschi
 * released under the GNU General Public License (GPL)
 * visit http://www.gnu.org/licenses/gpl.html for details on GPL
 */

/**
 * Functions in this file are used in the MeshCMS page editor of the. Some
 * functions are also used in the Configuration editor.
 */

  /**
   * Full path of the admin folder (context path included)
   */
  var adminFullPath = contextPath + '/' + adminPath;

  /**
   * Used to store a reference to the TinyMCE window used by the
   * file_browser_callback
   */
  var mcewin = null;

  /**
   * Called by TinyMCE to display the file manager to choose links and images.
   */
  function editor_fileBrowserCallBack(field_name, url, type, win) {
    if (win) {
      mcewin = win; // store for future use in editor_setFile
    } else {
      mcewin = null;
    }

    var popup = window.open(
      adminFullPath + '/filemanager/index.jsp?field='+field_name+'&type='+type,
      'filemanager',
      'width=630,height=420,menubar=no,status=yes,toolbar=no,resizable=yes'
    );

    popup.focus();
  }

  /**
   * Opens the file manager
   *
   * field_name: the id of the field that will store the return value
   *
   * see: editor_setFile below and fm_return in admin/filemanager.js
   */
  function editor_openFileManager(field_name) {
    var url = contextPath + '/' + adminPath + '/filemanager/index.jsp';

    if (field_name) {
      url += '?field=' + field_name;
      mcewin = null; // file manager not opened from within TinyMCE, so clear this
    }

    var popup = window.open(
      url,
      'filemanager',
      'width=680,height=420,menubar=no,status=yes,toolbar=no,resizable=yes'
    );

    popup.focus();
  }

  /**
   * Stores the return value of the file manager into the desired field
   */
  function editor_setFile(field_name, filePath) {
    // if mcewin contains a valid value, use it
    if (mcewin && mcewin.document && mcewin.document.getElementById(field_name)) {
      mcewin.document.getElementById(field_name).value = contextPath + filePath;
    } else { // field_name is in the document
      document.getElementById(field_name).value = filePath;
    }
  }

  /**
   * Shows or hides the textarea to edit the headers of the page.
   */
  function editor_toggleHeadEditor() {
    var he = document.getElementById('meshcmshead');

    if (he.style.display == 'none') {
      he.style.display = '';
      document.getElementById('togglehead').src = adminFullPath + '/images/tree_minus.gif';
    } else {
      he.style.display = 'none';
      document.getElementById('togglehead').src = adminFullPath + '/images/tree_plus.gif';
    }
  }

  /**
   * Clears a field
   */
  function editor_clr(fid) {
    document.getElementById(fid).value = "";
    document.getElementById(fid).focus();
  }
