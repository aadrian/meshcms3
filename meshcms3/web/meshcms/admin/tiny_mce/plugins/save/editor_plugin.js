/* Import plugin specific language pack */
 tinyMCE.importPluginLanguagePack('save','en,zh_cn,cs,fa,fr_ca,fr,de,pl,pt_br,nl');function TinyMCE_save_getControlHTML(control_name){switch(control_name){case "save":return '<img id="{$editor_id}_save" src="{$pluginurl}/images/save.gif" title="{$lang_save_desc}" width="20" height="20" class="mceButtonNormal" onmouseover="tinyMCE.switchClass(this,\'mceButtonOver\');" onmouseout="tinyMCE.switchClass(this,\'mceButtonNormal\');" onmousedown="tinyMCE.switchClass(this,\'mceButtonDown\');" onclick="tinyMCE.execInstanceCommand(\'{$editor_id}\',\'mceSave\');" />';}return "";}function TinyMCE_save_execCommand(editor_id,element,command,user_interface,value){switch(command){case "mceSave":var formObj=tinyMCE.selectedInstance.formElement.form;if(formObj){tinyMCE.triggerSave();for(var i=0;i<formObj.elements.length;i++){var elementId=formObj.elements[i].name?formObj.elements[i].name:formObj.elements[i].id;if(elementId.indexOf('mce_editor_')==0)formObj.elements[i].disabled=true;}tinyMCE.selectedInstance.formElement.form.submit();}else alert("Error: No form element found.");return true;}return false;}