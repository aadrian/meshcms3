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

var MESHCMS_CKEDITOR = MESHCMS_CKEDITOR || function() {
  return {
    load : function(element, contextPath, dirPath, userCSS) {
      return CKEDITOR.replace(element, {
        filebrowserBrowseUrl : 'host.jsp?mode=file_browser&editor=ckeditor&dir=' + dirPath,
        filebrowserImageBrowseUrl : 'host.jsp?mode=image_browser&editor=ckeditor&dir=' + dirPath,
        baseHref : location.protocol + "//" + location.host + contextPath + dirPath + "/"
      });
    },

    save : function(editor) {
      return editor.getData();
    },

    closeBrowser : function() {
      window.close();
    },

    browserResult : function(path) {
      var qs = decodeURIComponent(location.search.substring(1));
      qs = qs.split('&');
      var funcNum;

      for (var i = 0; i < qs.length; i++) {
        var param = qs[i].split('=');

        if (param[0] == 'CKEditorFuncNum') {
          funcNum = param[1];
        }
      }

      opener.CKEDITOR.tools.callFunction(funcNum, path);
    }
  }
}();
