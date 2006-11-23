/******
 *
 *	EditArea v0.6.1
 * 	Developped by Christophe Dolivet
 *	Released under LGPL license
 *
******/

	function EditAreaLoader(){
		date= new Date();
		this.start_time=date.getTime();
		this.win= "loading";	// window loading state
		this.error= false;	// to know if load is interrrupt
		this.baseURL="";
		//this.suffix="";
		this.template="";
		this.lang= new Object();	// array of loaded speech language
		this.load_syntax= new Object();	// array of loaded syntax language for highlight mode
		this.syntax= new Object();	// array of initilized syntax language for highlight mode
		this.loadedFiles= new Array();
		this.waiting_loading= new Object(); 	// files that must be loaded in order to allow the script to really start
		this.min_area_size= {"x": 400, "y": 100};
		// scripts that must be loaded in the iframe
		this.scripts_to_load= new Array("elements_functions", "resize_area", "reg_syntax");
		this.sub_scripts_to_load= new Array("edit_area", "manage_area" ,"edit_area_functions", "keyboard", "search_replace", "highlight", "regexp");
		
		this.resize= new Array(); // contain resizing datas
		this.hidden= new Array();	// store datas of the hidden textareas
		
		this.default_settings= {
			//id: "src"	// id of the textarea to transform
			debug: false
			,smooth_selection: true
			,font_size: "10"		// not for IE
			,font_family: "monospace"	// don't change
			,start_highlight: false	// if start with highlight			
			,toolbar: "search, go_to_line, |, undo, redo, |, select_font,|, change_smooth_selection, highlight, reset_highlight, |, help"
			,begin_toolbar: ""		//  "new_document, save, load, |"
			,end_toolbar: ""		// or end_toolbar
			,load_callback: ""		// function name
			,save_callback: ""		// function name
			,change_callback: ""	// function name
			,allow_resize: "both"	// possible values: "no", "both", "x", "y"
			,allow_toggle: true		// true or false
			,language: "en"
			,syntax: ""
			,display: "onload" 		// onload or later
			,max_undo: 20
			,browsers: "known"	// all or known
			,plugins: "" // comma separated plugin list
			,gecko_spellcheck: false	// enable/disable by default the gecko_spellcheck
		};
		
		this.advanced_buttons = [
				// id, button img, command (it will try to find the translation of "id")
				['new_document', 'newdocument.gif', 'new_document'],
				['search', 'search.gif', 'show_search'],
				['go_to_line', 'go_to_line.gif', 'go_to_line'],
				['undo', 'undo.gif', 'undo'],
				['redo', 'redo.gif', 'redo'],
				['change_smooth_selection', 'smooth_selection.gif', 'change_smooth_selection_mode'],
				['reset_highlight', 'reset_highlight.gif', 'resync_highlight'],
				['highlight', 'highlight.gif','change_highlight'],
				['help', 'help.gif', 'show_help'],
				['save', 'save.gif', 'save'],
				['load', 'load.gif', 'load']
			];
		
		// navigator identification
		ua= navigator.userAgent;
		this.nav= new Object(); 
		this.nav['isIE'] = (navigator.appName == "Microsoft Internet Explorer");
		if(this.nav['isIE']){
			this.nav['isIE'] = ua.replace(/^.*?MSIE ([0-9\.]*).*$/, "$1");
			if(this.nav['isIE']<6)
				this.has_error(); 
			/*this.IEvers= this.nav['isIE'].substr(0,1);
			if(this.IEvers<6)
				this.has_error();
			else if(this.IEvers==6)
				this.nav['isIE']6=true;
			else if(this.IEvers==7)
				this.nav['isIE']7= true;*/
		}
		if(this.nav['isNS'] = ua.indexOf('Netscape/') != -1){	// work only on netscape > 8 with render mode IE
			this.nav['isNS']= ua.substr(ua.indexOf('Netscape/')+9);
			if(this.nav['isNS']<8 || !this.nav['isIE'])
				this.has_error();			
		}
		
		if(this.nav['isOpera'] = (ua.indexOf('Opera') != -1)){	
			this.nav['isOpera']= ua.replace(/^.*?Opera.*?([0-9\.]+).*$/i, "$1");
			if(this.nav['isOpera']<9)
				this.has_error();
			this.nav['isIE']=false;			
		}
		
		this.nav['isGecko'] = (ua.indexOf('Gecko') != -1);
		
		if(this.nav['isFirefox'] =(ua.indexOf('Firefox') != -1))
			this.nav['isFirefox'] = ua.replace(/^.*?Firefox.*?([0-9\.]+).*$/i, "$1");
		
		this.nav['isSafari'] = (ua.indexOf('Safari') != -1);
		if(this.nav['isSafari'])
			this.has_error();
			
		if(this.nav['isIE']>=6 || this.nav['isOpera']>=9 || this.nav['isFirefox'])
			this.nav['isValidBrowser']=true;
		else
			this.nav['isValidBrowser']=false;
	
		this.set_base_url();		
		
		for(var i=0; i<this.scripts_to_load.length; i++){
			setTimeout("editAreaLoader.load_script('"+this.baseURL + this.scripts_to_load[i]+ ".js');", 1);	// let the time to Object editAreaLoader to be created before loading additionnal scripts
			this.waiting_loading[this.scripts_to_load[i]+ ".js"]= false;
		}				
		this.addEvent(window, "load", EditAreaLoader.prototype.window_loaded);
	};
	
	EditAreaLoader.prototype.has_error= function(){
		this.error= true;
		// set to empty all EditAreaLoader functions
		for(var i in EditAreaLoader.prototype){
			EditAreaLoader.prototype[i]=function(){};		
		}
	};
	
	EditAreaLoader.prototype.window_loaded= function(){
		editAreaLoader.win="loaded";
		
		// add events on forms
		if (document.forms) {
			for (var i=0; i<document.forms.length; i++) {
				var form = document.forms[i];
				form.edit_area_replaced_submit=null;
				try {
					
					form.edit_area_replaced_submit = form.onsubmit;
					form.onsubmit="";
				} catch (e) {// Do nothing
				}
				editAreaLoader.addEvent(form, "submit", EditAreaLoader.prototype.submit);
				editAreaLoader.addEvent(form, "reset", EditAreaLoader.prototype.reset);
			}
		}
	};
		
	EditAreaLoader.prototype.init= function(settings){
	
		if(!settings["id"])
			this.has_error();
		
		if(this.error)
			return;
		// if an instance of the editor already exists for this textarea => delete the previous one
		if(editAreas[settings["id"]])
			editAreaLoader.delete_instance(settings["id"]);
			
		// init settings
		for(var i in this.default_settings){
			if(typeof(settings[i])=="undefined")
				settings[i]=this.default_settings[i]
		}
		
		if(settings["browsers"]=="known" && this.nav['isValidBrowser']==false){
			return;
		}
		
		if(settings["begin_toolbar"].length>0)
			settings["toolbar"]= settings["begin_toolbar"] +","+ settings["toolbar"];
		if(settings["end_toolbar"].length>0)
			settings["toolbar"]= settings["toolbar"] +","+ settings["end_toolbar"];
		settings["tab_toolbar"]= settings["toolbar"].replace(/ /g,"").split(",");
		
		settings["plugins"]= settings["plugins"].replace(/ /g,"").split(",");
		for(var i=0; i<settings["plugins"].length; i++){
			if(settings["plugins"][i].length==0)
				settings["plugins"].splice(i,1);
		}
	//	alert(settings["plugins"].length+": "+ settings["plugins"].join(","));
	/*	var tmp="";
		for(var i in settings){
			tmp+=i+" : "+settings[i]+";\n";
		}
		alert(tmp);*/
		this.get_template();
		this.load_script(this.baseURL + "langs/"+ settings["language"] + ".js");
		
		if(settings["syntax"].length>0){
			settings["syntax"]=settings["syntax"].toLowerCase();
			this.load_script(this.baseURL + "reg_syntax/"+ settings["syntax"] + ".js");
		}
		//alert(this.template);
		
		editAreas[settings["id"]]= {"settings": settings};
		editAreas[settings["id"]]["displayed"]=false;
		editAreas[settings["id"]]["hidden"]=false;
		
		//if(settings["display"]=="onload")
		editAreaLoader.start(settings["id"]);

	};
	
	// delete an instance of an EditArea
	EditAreaLoader.prototype.delete_instance= function(id){
		editAreaLoader.toggle_off(id);
		
		// remove toggle infos and debug textarea
		var span= document.getElementById("EditAreaArroundInfos_"+id);
		if(span){
			span.parentNode.removeChild(span);
		}
		
		// remove the iframe
		var iframe= document.getElementById("frame_"+id);
		if(iframe){
			iframe.parentNode.removeChild(iframe);
			//delete iframe;
			try {
				delete window.frames["frame_"+id];
			} catch (e) {// Do nothing
			}
		}	
		delete editAreas[id];
	};

	
	EditAreaLoader.prototype.start= function(id){
		// check that the window is loaded
		if(this.win!="loaded"){
			setTimeout("editAreaLoader.start('"+id+"');", 50);
			return;
		}
		
		// check that all needed scripts are loaded
		for(var i in editAreaLoader.waiting_loading){
			if(editAreaLoader.waiting_loading[i]!="loaded"){
				setTimeout("editAreaLoader.start('"+id+"');", 50);
				return;
			}
		}
		
		// wait until language and syntax files are loaded
		if(!editAreaLoader.lang[editAreas[id]["settings"]["language"]] || (editAreas[id]["settings"]["syntax"].length>0 && !editAreaLoader.load_syntax[editAreas[id]["settings"]["syntax"]]) ){
			setTimeout("editAreaLoader.start('"+id+"');", 50);
			return;
		}
		// init the regexp for syntax highlight
		if(editAreas[id]["settings"]["syntax"].length>0)
			editAreaLoader.init_syntax_regexp();
		
			
		// display toggle option and debug area
		if(!document.getElementById("EditAreaArroundInfos_"+id) && (editAreas[id]["settings"]["debug"] || editAreas[id]["settings"]["allow_toggle"]))
		{
			var span= document.createElement("span");
			span.id= "EditAreaArroundInfos_"+id;
			var html="";
			if(editAreas[id]["settings"]["allow_toggle"]){
				checked=(editAreas[id]["settings"]["display"]=="onload")?"checked":"";
				html+="<div id='edit_area_toggle_"+i+"'>";
				html+="<input id='edit_area_toggle_checkbox_"+ id +"' class='toggle_"+ id +"' type='checkbox' onclick='editAreaLoader.toggle(\""+ id +"\");' accesskey='e' "+checked+" />";
				html+="<label for='edit_area_toggle_checkbox_"+ id +"'>{$toggle}</label></div>";	
			}
			if(editAreas[id]["settings"]["debug"])
				html+="<textarea id='edit_area_debug_"+ id +"' style='z-index: 20; width: 100%; height: 120px;overflow: auto; border: solid black 1px;'></textarea><br />";				
			html= editAreaLoader.translate(html, editAreas[id]["settings"]["language"]);				
			span.innerHTML= html;				
			var father= document.getElementById(id).parentNode;
			var next= document.getElementById(id).nextSibling;
			if(next==null)
				father.appendChild(span);
			else
				father.insertBefore(span, next);
		}
		
		if(editAreas[id]["settings"]["display"]=="later"){
			editAreas[id]["settings"]["display"]="onload";
			return
		}
		
	/*	if(editAreas[id]["settings"]["display"]=="onload")
			editAreaLoader.start(id);*/

		
		// get toolbar content
		//alert("start: "+id);
		var html_toolbar_content="";
		area=editAreas[id];
		//this.current_language=area["settings"]["language"];
		
	/*	// wait until language and syntax files are loaded
		if(!this.lang[area["settings"]["language"]] || (area["settings"]["syntax"] && !this.load_syntax[area["settings"]["syntax"]]) ){
			setTimeout("editAreaLoader.start('"+id+"');", 100);
			return;
		}*/
		
		for(var i=0; i<area["settings"]["tab_toolbar"].length; i++){
		//	alert(this.tab_toolbar[i]+"\n"+ this.get_control_html(this.tab_toolbar[i]));
			html_toolbar_content+= this.get_control_html(area["settings"]["tab_toolbar"][i], area["settings"]["language"]);
		}
		
		// create javascript import rules for the iframe if the javascript has not been already loaded by the compressor
		if(!this.iframe_script){
			this.iframe_script="";
			for(var i=0; i<this.sub_scripts_to_load.length; i++)
				this.iframe_script+='<script language="javascript" type="text/javascript" src="'+ this.baseURL + this.sub_scripts_to_load[i] +'.js"></script>';
		}
		
		// add plugins scripts if not already loaded by the compressor (but need to load language in all the case)
		for(var i=0; i<area["settings"]["plugins"].length; i++){
			//if(typeof(area["settings"]["plugins"][i])=="function") continue;
			if(!editAreaLoader.all_plugins_loaded)
				this.iframe_script+='<script language="javascript" type="text/javascript" src="'+ this.baseURL + 'plugins/' + area["settings"]["plugins"][i] + '/' + area["settings"]["plugins"][i] +'.js"></script>';
			this.iframe_script+='<script language="javascript" type="text/javascript" src="'+ this.baseURL + 'plugins/' + area["settings"]["plugins"][i] + '/langs/' + area["settings"]["language"] +'.js"></script>';
		}
	
		
		// create css link for the iframe if the whole css text has not been already loaded by the compressor
		if(!this.iframe_css){
			this.iframe_css="<link href='"+ this.baseURL +"edit_area.css' rel='stylesheet' type='text/css' />";
		}
		
		
		// create template
		var template= this.template.replace(/\[__BASEURL__\]/g, this.baseURL);
		template= template.replace("[__TOOLBAR__]",html_toolbar_content);
			
		
		// fill template with good language sentences
		template= this.translate(template, area["settings"]["language"], "template");
		
		// add css_code
		template= template.replace("[__CSSRULES__]", this.iframe_css);				
		// add js_code
		template= template.replace("[__JSCODE__]", this.iframe_script);
		//template=template.replace(/\{\$([^\}]+)\}/gm, this.traduc_template);
		
		//editAreas[area["settings"]["id"]]["template"]= template;
		
		area.textarea=document.getElementById(area["settings"]["id"]);
		editAreas[area["settings"]["id"]]["textarea"]=area.textarea;
		
		// insert template in the document after the textarea
		var father= area.textarea.parentNode;
		var content= document.createElement("iframe");
		content.name= "frame_"+area["settings"]["id"];
		content.id= "frame_"+area["settings"]["id"];
		content.style.borderWidth= "0px";
		setAttribute(content, "frameBorder", "0"); // IE
		content.style.overflow="hidden";
		content.style.display="none";
		
		var next= area.textarea.nextSibling;

		if(next==null)
			father.appendChild(content);
		else
			father.insertBefore(content, next) ;
			
		var frame=window.frames["frame_"+area["settings"]["id"]];		
		
		frame.document.open();
		frame.editAreas=editAreas;
		frame.area_id= area["settings"]["id"];	
		frame.document.area_id= area["settings"]["id"];	
		frame.document.write(template);
		frame.document.close();

	//	frame.editAreaLoader=this;
		//editAreas[area["settings"]["id"]]["displayed"]=true;
		
	};
	
	EditAreaLoader.prototype.toggle= function(id, toggle_to){
		/*if(this.nav['isIE'])
			this.getIESelection();
		var pos_start= this.textarea.selectionStart;
		var pos_end= this.textarea.selectionEnd;
		*/
		if((editAreas[id]["displayed"]==true  && toggle_to!="on") || toggle_to=="off"){
			this.toggle_off(id);
		}else if((editAreas[id]["displayed"]==false  && toggle_to!="off") || toggle_to=="on"){
			this.toggle_on(id);
		}
		/*
		this.textarea= document.getElementById(this.id);
		this.textarea.focus();
		this.textarea.selectionStart = pos_start;
		this.textarea.selectionEnd = pos_end;
		if(this.nav['isIE'])
			this.setIESelection();*/
		return false;
	};
	
	EditAreaLoader.prototype.toggle_off= function(id){
		if(window.frames["frame_"+id])
		{
			
			var frame=window.frames["frame_"+id];
			// set wrap to off to keep same display mode (some browser get problem with this, so it need more complex operation
			editAreas[id]["textarea"].wrap = "off";	// for IE
			setAttribute(editAreas[id]["textarea"], "wrap", "off");	// for Firefox	
			var parNod = editAreas[id]["textarea"].parentNode;
			var nxtSib = editAreas[id]["textarea"].nextSibling;
			parNod.removeChild(editAreas[id]["textarea"]); 
			parNod.insertBefore(editAreas[id]["textarea"], nxtSib);
			
			// restore values
			editAreas[id]["textarea"].value= frame.editArea.textarea.value;
			var selStart= frame.editArea.last_selection["selectionStart"];
			var selEnd= frame.editArea.last_selection["selectionEnd"];
			var scrollTop= frame.document.getElementById("result").scrollTop;
			var scrollLeft= frame.document.getElementById("result").scrollLeft;
			frame.editArea.execCommand("toggle_off");
/*
			var selStart= window.frames["frame_"+id].editArea.textarea.selectionStart;
			var selEnd= window.frames["frame_"+id].editArea.textarea.selectionEnd;
			alert(selStart);*/
			document.getElementById("frame_"+id).style.display="none";
			
			editAreas[id]["textarea"].style.display="inline";
			editAreas[id]["textarea"].focus();			
			editAreas[id]["textarea"].selectionStart= selStart;
			editAreas[id]["textarea"].selectionEnd= selEnd;
			if(this.nav['isIE'])
				set_IE_selection(editAreas[id]["textarea"]);
			editAreas[id]["textarea"].scrollTop= scrollTop;
			editAreas[id]["textarea"].scrollLeft= scrollLeft;
			
			editAreas[id]["displayed"]=false;
			/*// init to good size
			var edit_area= document.getElementById("edit_area");
		
			previous_area.style.width= edit_area.offsetWidth+"px";
			previous_area.style.height= edit_area.offsetHeight+"px";
			previous_area.value= this.textarea.value;*/
		}
	};	
	
	EditAreaLoader.prototype.toggle_on= function(id){
		/*if(!editAreas[area["settings"]["id"]]["started"])
			editAreaLoader.start(id);*/
			
		if(window.frames["frame_"+id])
		{
			var frame=window.frames["frame_"+id];
			area= window.frames["frame_"+id].editArea;
			area.textarea.value= editAreas[id]["textarea"].value;
			
			// store display values;
			var selStart= 0;
			var selEnd= 0;
			var scrollTop= 0;
			var scrollLeft= 0;
			if(this.nav['isIE'])
				get_IE_selection(editAreas[id]["textarea"]);
			if(editAreas[id]["textarea"].use_last==true)
			{
				var selStart= editAreas[id]["textarea"].last_selectionStart;
				var selEnd= editAreas[id]["textarea"].last_selectionEnd;
				var scrollTop= editAreas[id]["textarea"].last_scrollTop;
				var scrollLeft= editAreas[id]["textarea"].last_scrollLeft;
				editAreas[id]["textarea"].use_last=false;
			}
			else
			{
				try{
					var selStart= editAreas[id]["textarea"].selectionStart;
					var selEnd= editAreas[id]["textarea"].selectionEnd;
					var scrollTop= editAreas[id]["textarea"].scrollTop;
					var scrollLeft= editAreas[id]["textarea"].scrollLeft;
					//alert(scrollTop);
				}catch(ex){}
			}
			
			// set to good size
			this.set_editarea_size_from_textarea(id, document.getElementById("frame_"+id));
			editAreas[id]["textarea"].style.display="none";			
			document.getElementById("frame_"+id).style.display="inline";
			area.execCommand("focus"); // without this focus opera doesn't manage well the iframe body height
			area.execCommand("update_size");
			area.execCommand("focus");	// without this focus after toggling off and on, firefox doesn't put the cursor in editarea
			
			area.execCommand("toggle_on");
			
			// restore display values
			window.frames["frame_"+id].document.getElementById("result").scrollTop= scrollTop;
			window.frames["frame_"+id].document.getElementById("result").scrollLeft= scrollLeft;	
			area.textarea.selectionStart= selStart;
			area.textarea.selectionEnd= selEnd;			
			if(this.nav['isIE'])
				set_IE_selection(area.textarea, document.getElementById("frame_"+id));

			/*date= new Date();
			end_time=date.getTime();		
			alert("load time: "+ (end_time-this.start_time));*/
			
			
			editAreas[id]["displayed"]=true;
		}
		else
		{
			if(this.nav['isIE'])
				get_IE_selection(document.getElementById(id));			
			document.getElementById(id).last_selectionStart= document.getElementById(id).selectionStart;
			document.getElementById(id).last_selectionEnd= document.getElementById(id).selectionEnd;
			document.getElementById(id).last_scrollTop= document.getElementById(id).scrollTop;
			document.getElementById(id).last_scrollLeft= document.getElementById(id).scrollLeft;
			document.getElementById(id).use_last=true;
			editAreaLoader.start(id);
		}
	};	
	
	EditAreaLoader.prototype.set_editarea_size_from_textarea= function(id, frame){	
		var width= document.getElementById(id).offsetWidth;
		var height= document.getElementById(id).offsetHeight;
		//alert("h: "+height+" w: "+width);
		frame.style.width= width+"px";
		frame.style.height= height+"px";
	};
		
	EditAreaLoader.prototype.set_base_url= function(){
		//this.baseURL="";
		if (!this.baseURL) {
			var elements = document.getElementsByTagName('script');
	
			for (var i=0; i<elements.length; i++) {
				if (elements[i].src && elements[i].src.match(/edit_area_[^\\\/]*$/i) ) {
					var src = elements[i].src;
					src = src.substring(0, src.lastIndexOf('/'));
					this.baseURL = src;
					this.file_name= elements[i].src.substr(elements[i].src.lastIndexOf("/")+1);
					break;
				}
			}
		}
		
		var documentBasePath = document.location.href;
		if (documentBasePath.indexOf('?') != -1)
			documentBasePath = documentBasePath.substring(0, documentBasePath.indexOf('?'));
		var documentURL = documentBasePath;
		documentBasePath = documentBasePath.substring(0, documentBasePath.lastIndexOf('/'));
	
		// If not HTTP absolute
		if (this.baseURL.indexOf('://') == -1 && this.baseURL.charAt(0) != '/') {
			// If site absolute
			this.baseURL = documentBasePath + "/" + this.baseURL;
		}
		this.baseURL+="/";	
	};
	
	EditAreaLoader.prototype.get_button_html= function(id, img, exec, baseURL) {
		if(!baseURL)
			baseURL= this.baseURL;
		var cmd = 'editArea.execCommand(\'' + exec + '\')';
		html= '<a href="javascript:' + cmd + '" onclick="' + cmd + ';return false;" onmousedown="return false;" target="_self">';
		html+= '<img id="' + id + '" src="'+ baseURL +'images/' + img + '" title="{$' + id + '}" width="20" height="20" class="editAreaButtonNormal" onmouseover="editArea.switchClass(this,\'editAreaButtonOver\');" onmouseout="editArea.restoreClass(this);" onmousedown="editArea.restoreAndSwitchClass(this,\'editAreaButtonDown\');" /></a>';
		return html;
	};

	EditAreaLoader.prototype.get_control_html= function(button_name, lang) {		
		
		for (var i=0; i<this.advanced_buttons.length; i++)
		{
			var but = this.advanced_buttons[i];			
			if (but[0] == button_name)
			{
				return this.get_button_html(but[0], but[1], but[2]);
			}	
		}		
				
		switch (button_name){
			case "*":
			case "return":
				return "<br />";
			case "|":
		  	case "separator":
				return '<img src="'+ this.baseURL +'images/spacer.gif" width="1" height="15" class="editAreaSeparatorLine">';
			case "select_font":
				html= "<select id='area_font_size' onchange='javascript:editArea.execCommand(\"change_font_size\")'>"
					+"			<option value='-1'>{$font_size}</option>"
					+"			<option value='8'>8 pt</option>"
					+"			<option value='9'>9 pt</option>"
					+"			<option value='10'>10 pt</option>"
					+"			<option value='11'>11 pt</option>"
					+"			<option value='12'>12 pt</option>"
					+"			<option value='14'>14 pt</option>"
					+"		</select>";
				return html;
		}
		
		return "<span id='tmp_tool_"+button_name+"'>["+button_name+"]</span>";		
	};
	
	
	EditAreaLoader.prototype.get_template= function() {
		if(this.template=="")
		{
			var xhr_object = null; 
			if(window.XMLHttpRequest) // Firefox 
				xhr_object = new XMLHttpRequest(); 
			else if(window.ActiveXObject) // Internet Explorer 
				xhr_object = new ActiveXObject("Microsoft.XMLHTTP"); 
			else { // XMLHttpRequest not supported
				alert("XMLHTTPRequest not supported. EditArea not loaded"); 
				return; 
			} 
			 
			xhr_object.open("GET", this.baseURL+"template.html", false); 
			xhr_object.send(null); 
			if(xhr_object.readyState == 4) 
				this.template=xhr_object.responseText;
			else
				this.has_error();
		}
	};
	
	// translate text
	EditAreaLoader.prototype.translate= function(text, lang, mode) {
		
		if(mode=="word")
			text=editAreaLoader.get_word_translation(text, lang);
		else if(mode="template"){
			editAreaLoader.current_language= lang;
			text=text.replace(/\{\$([^\}]+)\}/gm, editAreaLoader.translate_template);
		}
		return text;
	};
	
	EditAreaLoader.prototype.translate_template= function(){
		return editAreaLoader.get_word_translation(EditAreaLoader.prototype.translate_template.arguments[1], editAreaLoader.current_language);
	};
	
	EditAreaLoader.prototype.get_word_translation= function(val, lang){
		for(var i in editAreaLoader.lang[lang]){
			if(i == val)
				return editAreaLoader.lang[lang][i];
		}
		return "_"+val;
	};
	
	EditAreaLoader.prototype.load_script= function(url){
		
		if (this.loadedFiles[url])
			return;	
	//	alert("laod: "+url);
	//alert(url);
		try{
			script= document.createElement("script");
			script.type= "text/javascript";
			script.src= url;
			head= document.getElementsByTagName("head");
			head[0].appendChild(script);
		}catch(e){
			document.write('<sc'+'ript language="javascript" type="text/javascript" src="' + url + '"></sc'+'ript>');
		}
		//var filename= url.replace(/^.*?\/?([a-z\.\_\-]+)$/i, "$1");
		this.loadedFiles[url] = true;
	};
	
	EditAreaLoader.prototype.addEvent = function(obj, name, handler) {
		if (this.nav['isIE']) {
			obj.attachEvent("on" + name, handler);
		} else{
			obj.addEventListener(name, handler, false);
		}
	};


	// reset all the editareas in the form that have been reseted
	EditAreaLoader.prototype.reset= function(e){
		
		var formObj = editAreaLoader.nav['isIE'] ? window.event.srcElement : e.target;
		for(var i in editAreas){			
			if(window.frames["frame_"+i] && isChildOf(document.getElementById(i), formObj) && editAreas[i]["displayed"]==true){
			
				var exec= 'window.frames["frame_'+ i +'"].editArea.textarea.value= document.getElementById("'+ i +'").value;';
				exec+= 'window.frames["frame_'+ i +'"].editArea.execCommand("focus");';
				exec+= 'window.frames["frame_'+ i +'"].editArea.check_line_selection();';
				exec+= 'window.frames["frame_'+ i +'"].editArea.execCommand("reset");';
				window.setTimeout(exec, 10);
			}
		}		
		return;
	};
	
	
	// prepare all the textarea replaced by an editarea to be submited
	EditAreaLoader.prototype.submit= function(e){		
		var formObj = editAreaLoader.nav['isIE'] ? window.event.srcElement : e.target;
		for(var i in editAreas){
			//alert(formObj);
			/*for(j in window.frames)
				alert("frame: "+j)*/;
				
			if(isChildOf(document.getElementById(i), formObj) && window.frames["frame_"+i] && editAreas[i]["displayed"]==true ){
				//alert(window.frames["frame_"+ i].editArea.textarea.value);
				window.frames["frame_"+ i].editArea.execCommand("submit");
				document.getElementById(i).value= window.frames["frame_"+ i].editArea.textarea.value;		
				//alert(document.getElementById(i).value);
			}
		}				
		if(typeof(formObj.edit_area_replaced_submit) == "function"){
			res= formObj.edit_area_replaced_submit();
			if(res==false){
				if(editAreaLoader.nav['isIE'])
					return false;
				else
					e.preventDefault();
			}
		}
		return ;
	};
	
	// allow to get the value of the editarea
	EditAreaLoader.prototype.getValue = function(id){
        if(window.frames["frame_"+id] && editAreas[id]["displayed"]==true){
            return window.frames["frame_"+ id].editArea.textarea.value;       
        }else if(elem=document.getElementById(id)){
        	return elem.value;
        }
        return false;
    };
    
    // allow to set the value of the editarea
    EditAreaLoader.prototype.setValue = function(id, new_val){
        if(window.frames["frame_"+id] && editAreas[id]["displayed"]==true){
            window.frames["frame_"+ id].editArea.textarea.value= new_val;     
			window.frames["frame_"+ id].editArea.execCommand("focus"); 
			window.frames["frame_"+ id].editArea.check_line_selection(false);  
			window.frames["frame_"+ id].editArea.execCommand("onchange");
        }else if(elem=document.getElementById(id)){
        	elem.value= new_val;
        }
    };
    
    // allow to get infos on the selection: array(start, end)
    EditAreaLoader.prototype.getSelectionRange = function(id){
    	var sel= {"start": 0, "end": 0};
        if(window.frames["frame_"+id] && editAreas[id]["displayed"]==true){
        	var editArea= window.frames["frame_"+ id].editArea;
           
		/*	if(this.nav['isIE']){
				editArea.getIESelection();
				
			}
			sel["start"]=editArea.textarea.selectionStart;
			sel["end"]=editArea.textarea.selectionEnd;
		*/
			sel["start"]=editArea.textarea.selectionStart;
			sel["end"]=editArea.textarea.selectionEnd;
		
        }else if(elem=document.getElementById(id)){
        	sel= getSelectionRange(elem);
        }
        return sel;
    };
    
    // allow to set the selection with the given start and end positions
    EditAreaLoader.prototype.setSelectionRange = function(id, new_start, new_end){
        if(window.frames["frame_"+id] && editAreas[id]["displayed"]==true){
            window.frames["frame_"+ id].editArea.area_select(new_start, new_end-new_start);  
			// make an auto-scroll to the selection
			if(!this.nav['isIE']){
				window.frames["frame_"+ id].editArea.check_line_selection(false); 
				window.frames["frame_"+ id].editArea.scroll_to_view();
			}   
        }else if(elem=document.getElementById(id)){
        	setSelectionRange(elem, new_start, new_end);
        }
    };
    
    EditAreaLoader.prototype.getSelectedText = function(id){
    	var sel= this.getSelectionRange(id);
        return this.getValue(id).substring(sel["start"], sel["end"]);
    };
	
	EditAreaLoader.prototype.setSelectedText = function(id, new_val){
		new_val= new_val.replace(/\r/g, ""); 
		var sel= this.getSelectionRange(id);
		var text= this.getValue(id);
		if(window.frames["frame_"+id] && editAreas[id]["displayed"]==true){
			var scrollTop= window.frames["frame_"+ id].document.getElementById("result").scrollTop;
			var scrollLeft= window.frames["frame_"+ id].document.getElementById("result").scrollLeft;
		}else{
			var scrollTop= document.getElementById(id).scrollTop;
			var scrollLeft= document.getElementById(id).scrollLeft;
		}
		
		text= text.substring(0, sel["start"])+ new_val +text.substring(sel["end"]);
		this.setValue(id, text);
		var new_sel_end= sel["start"]+ new_val.length;
		this.setSelectionRange(id, sel["start"], new_sel_end);
		
		
		// fix \r problem for selection length count on IE & Opera
		if(new_val != this.getSelectedText(id).replace(/\r/g, "")){
			this.setSelectionRange(id, sel["start"], new_sel_end+ new_val.split("\n").length -1);
		}
		// restore scrolling position
		if(window.frames["frame_"+id] && editAreas[id]["displayed"]==true){
			window.frames["frame_"+ id].document.getElementById("result").scrollTop= scrollTop;
			window.frames["frame_"+ id].document.getElementById("result").scrollLeft= scrollLeft;
			window.frames["frame_"+ id].editArea.execCommand("onchange");
		}else{
			document.getElementById(id).scrollTop= scrollTop;
			document.getElementById(id).scrollLeft= scrollLeft;
		}
    };
    
    EditAreaLoader.prototype.insertTags = function(id, open_tag, close_tag){
    	var old_sel= this.getSelectionRange(id);
    	text= open_tag + this.getSelectedText(id) + close_tag; 
		editAreaLoader.setSelectedText(id, text);
    	var new_sel= this.getSelectionRange(id);
    	if(old_sel["end"] > old_sel["start"])	// if text was selected, cursor at the end
    		this.setSelectionRange(id, new_sel["end"], new_sel["end"]);
    	else // cursor in the middle
    		this.setSelectionRange(id, old_sel["start"]+open_tag.length, old_sel["start"]+open_tag.length);
    };
    
    // hide both EditArea and normal textarea
	EditAreaLoader.prototype.hide= function(id){
		if(document.getElementById(id) && !this.hidden[id])
		{
			this.hidden[id]= new Array();
			this.hidden[id]["selectionRange"]= this.getSelectionRange(id);
			if(document.getElementById(id).style.display!="none")
			{
				this.hidden[id]["scrollTop"]= document.getElementById(id).scrollTop;
				this.hidden[id]["scrollLeft"]= document.getElementById(id).scrollLeft;
			}
					
			if(window.frames["frame_"+id])
			{
				this.hidden[id]["toggle"]= editAreas[id]["displayed"];
				
				if(window.frames["frame_"+id] && editAreas[id]["displayed"]==true){
					var scrollTop= window.frames["frame_"+ id].document.getElementById("result").scrollTop;
					var scrollLeft= window.frames["frame_"+ id].document.getElementById("result").scrollLeft;
				}else{
					var scrollTop= document.getElementById(id).scrollTop;
					var scrollLeft= document.getElementById(id).scrollLeft;
				}
				this.hidden[id]["scrollTop"]= scrollTop;
				this.hidden[id]["scrollLeft"]= scrollLeft;
				
				
				if(editAreas[id]["displayed"]==true)
					editAreaLoader.toggle_off(id);
				
					
			}
			// hide toggle button and debug box
			var span= document.getElementById("EditAreaArroundInfos_"+id);
			if(span){
				span.style.display='none';
			}
			
			// hide textarea
			document.getElementById(id).style.display= "none";
		}
	};
	
	// restore hidden EditArea and normal textarea
	EditAreaLoader.prototype.show= function(id){
		if(document.getElementById(id) && this.hidden[id])
		{
			document.getElementById(id).style.display= "inline";
			document.getElementById(id).scrollTop= this.hidden[id]["scrollTop"];
			document.getElementById(id).scrollLeft= this.hidden[id]["scrollLeft"];
			var span= document.getElementById("EditAreaArroundInfos_"+id);
			if(span){
				span.style.display='inline';
			}
			
			if(window.frames["frame_"+id])
			{
								
				// restore toggle button and debug box
			
				
				// restore textarea
				document.getElementById(id).style.display= "inline";
				
				// restore EditArea
				if(this.hidden[id]["toggle"]==true)
					editAreaLoader.toggle_on(id);
				
				scrollTop= this.hidden[id]["scrollTop"];
				scrollLeft= this.hidden[id]["scrollLeft"];
				
				if(window.frames["frame_"+id] && editAreas[id]["displayed"]==true){
					window.frames["frame_"+ id].document.getElementById("result").scrollTop= scrollTop;
					window.frames["frame_"+ id].document.getElementById("result").scrollLeft= scrollLeft;
				}else{
					document.getElementById(id).scrollTop= scrollTop;
					document.getElementById(id).scrollLeft= scrollLeft;
				}
			
			}
			// restore selection
			sel= this.hidden[id]["selectionRange"];
			this.setSelectionRange(id, sel["start"], sel["end"]);
			delete this.hidden[id];	
		}
	};

	// allow to access to editarea functions and datas (for advanced users only)
	EditAreaLoader.prototype.execCommand = function(id, cmd){
        if(window.frames["frame_"+id]){
            return eval('window.frames["frame_'+ id +'"].editArea.'+ cmd +';');       
        }
        return false;
    };

	
	var editAreaLoader= new EditAreaLoader();
	var editAreas= new Object();
