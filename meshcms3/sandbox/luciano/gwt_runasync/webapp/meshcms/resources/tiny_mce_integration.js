/*
 * Copyright 2004-2010 Luciano Vernaschi
 *
 * This file is part of MeshCMS.
 *
 * MeshCMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MeshCMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MeshCMS. If not, see <http://www.gnu.org/licenses/>.
 */

var MESHCMS_TINY_MCE = MESHCMS_TINY_MCE || function() {
  return {
    cp : null,

    path : null,

    load : function(element, contextPath, dirPath, userCSS) {
      cp = contextPath;
      path = dirPath;

      tinyMCE.init({
        theme : 'advanced',
        mode : 'exact',
        elements : element.id,
        //language : $$language,
        plugins : 'fullscreen',
        dialog_type : 'modal',
        use_native_selects : true,
        file_browser_callback : 'MESHCMS_TINY_MCE.openBrowser',
        theme_advanced_buttons3_add : "fullscreen",
        document_base_url : /*location.protocol + '//' + location.host +*/ contextPath + dirPath + '/',
        content_css : userCSS,
        theme_advanced_toolbar_location : 'top',
        theme_advanced_toolbar_align : 'left',
        theme_advanced_statusbar_location : "bottom"
      });

      return element.id;
    },

    save : function(editorName) {
      var tinyEd = tinyMCE.get(editorName);
      var html = tinyEd.getContent();
      tinyMCE.remove(tinyEd);
      return html;
    },

    closeBrowser : function() {
      tinyMCEPopup.close();
    },

    browserResult : function(path) {
      var win = tinyMCEPopup.getWindowArg('window');
      win.document.getElementById(tinyMCEPopup.getWindowArg('input')).value = path;

      if (typeof(win.ImageDialog) != 'undefined') {
        if (win.ImageDialog.getImageData)
          win.ImageDialog.getImageData();

        if (win.ImageDialog.showPreviewImage)
          win.ImageDialog.showPreviewImage(URL);
      }

      tinyMCEPopup.close();
    },

    openBrowser : function(field_name, url, type, win) {
      // TODO: remove static path /meshcms
      var browserURL = cp + '/meshcms/resources/host.jsp?mode=';

      switch (type){
        case 'image':
          browserURL += 'image_browser';
          break;
        default:
          browserURL += 'file_browser';
      }

      browserURL += '&editor=tiny_mce&dir=' + path;

      tinyMCE.activeEditor.windowManager.open({
        file : browserURL,
        title : 'MeshCMS File Browser',
        width : 640,
        height : 480,
        resizable : true,
        inline : false,
        close_previous : false,
        popup_css : false,
        translate_i18n : false
      }, {
        window : win,
        input : field_name
      });

      return false;
    }
  }
}();
