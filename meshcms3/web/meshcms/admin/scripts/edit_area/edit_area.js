/******
 *
 *	EditArea 
 * 	Developped by Christophe Dolivet
 *	Released under LGPL license
 *
******/

	function EditArea(){
		this.error= false;	// to know if load is interrrupt
		
		this.inlinePopup= new Array({popup_id: "area_search_replace", icon_id: "search"},
									{popup_id: "edit_area_help", icon_id: "help"});
		this.plugins= new Array();
	
		this.line_number=0;
		
		this.nav=parent.editAreaLoader.nav; 	// navigator identification
		
		this.last_selection=new Array();		
		this.last_text_to_highlight="";
		this.last_hightlighted_text= "";
		
		this.textareaFocused= false;
		this.previous= new Array();
		this.next= new Array();
		this.last_undo="";
		//this.loaded= false;
		this.assocBracket=new Array();
		this.revertAssocBracket= new Array();		
		// bracket selection init 
		this.assocBracket["("]=")";
		this.assocBracket["{"]="}";
		this.assocBracket["["]="]";		
		for(var index in this.assocBracket){
			this.revertAssocBracket[this.assocBracket[index]]=index;
		}
		/*this.textarea="";	
		
		this.state="declare";
		this.code = new Array(); // store highlight syntax for languagues*/
		// font datas
		this.lineHeight= 16;
		this.charWidth=8;
		/*this.default_font_family= "monospace";
		this.default_font_size= 10;*/
		this.tab_nb_char= 8;	//nb of white spaces corresponding to a tabulation
		if(this.nav['isOpera'])
			this.tab_nb_char= 6;
		this.is_tabbing= false;
		
		
		this.isResizing=false;	// resize var
		
		// init with settings and ID
		this.id= area_id;
		this.settings= editAreas[this.id]["settings"];
	};
	
	
	//called by the toggle_on
	EditArea.prototype.update_size= function(){
		var height= document.body.offsetHeight - this.get_all_toolbar_height() - 4;
		document.getElementById("result").style.height= height +"px";
		
		//width=document.getElementById("editor").offsetWidth - 2;
		//width=parent.document.getElementById("frame_"+this.id).offsetWidth - 2;
		var width=document.body.offsetWidth -2;
		document.getElementById("result").style.width= width+"px";
		
		//alert("result h: "+ height+" w: "+width+"\ntoolbar h: "+this.get_all_toolbar_height()+"\nbody_h: "+document.body.offsetHeight);		
	};

	EditArea.prototype.init= function(){
		//alert("ini"+document.body.offsetWidth );
		/*document.open();
		document.write("bouh");
		document.close();*/
		this.textarea= document.getElementById("textarea");
		
		
		// add plugins buttons in the toolbar
		spans= parent.getChildren(document.getElementById("toolbar_1"), "span", "", "", "all", -1);
		for(var i=0; i<spans.length; i++){
			id=spans[i].id.replace(/tmp_tool_(.*)/, "$1");
			if(id!= spans[i].id){
				var found= false;
				for(var j in this.plugins){
					if(found==false && typeof(this.plugins[j].init)=="function" ){
						html=this.plugins[j].get_control_html(id);
						if(html!=false){
							found=true;
							html= parent.editAreaLoader.translate(html, this.settings["language"], "template");
							var new_span= document.createElement("span");
							new_span.innerHTML= html;				
							var father= spans[i].parentNode;
							spans[i].parentNode.replaceChild(new_span, spans[i]);						
						}
					}
				}
			}
		}
		
		
		
		// init datas
		this.textarea.value=editAreas[this.id]["textarea"].value;
		if(this.settings["debug"])
			this.debug=parent.document.getElementById("edit_area_debug_"+this.id);
		
		// init size		
		//this.update_size();
		
		if(document.getElementById("redo") != null)
			this.switchClassSticky(document.getElementById("redo"), 'editAreaButtonDisabled', true);
		
		
		// insert css rules for highlight mode		
		if(typeof(parent.editAreaLoader.syntax[this.settings["syntax"]])!="undefined"){
			for(var i in parent.editAreaLoader.syntax){
				var styles=parent.editAreaLoader.syntax[i]["styles"];
				
				if(styles.length>0){
					newcss = document.createElement("style");
					newcss.type="text/css";
					newcss.media="all";
					document.getElementsByTagName("head")[0].appendChild(newcss);
					cssrules = styles.split("}");
					newcss = document.styleSheets[0];
					if(newcss.rules) { //IE
						for(i=cssrules.length-2;i>=0;i--) {
							newrule = cssrules[i].split("{");
							newcss.addRule(newrule[0],newrule[1])
						}
					}
					else if(newcss.cssRules) { //Firefox etc
						for(i=cssrules.length-1;i>=0;i--) {
							if(cssrules[i].indexOf("{")!=-1){
								newcss.insertRule(cssrules[i]+"}",0);
							}
						}
					}
					//document.getElementsByTagName("head")[0].appendChild(newcss);
				}
			}
		}
		// init key events
		if(this.nav['isOpera'])
			document.getElementById("editor").onkeypress= keyDown;
		else
			document.getElementById("editor").onkeydown= keyDown;
	/*	if(this.nav['isIE'] || this.nav['isFirefox'])
			this.textarea.onkeydown= keyDown;
		else if
			this.textarea.onkeypress= keyDown;*/
		for(var i in this.inlinePopup){
			if(this.nav['isIE'] || this.nav['isFirefox'])
				document.getElementById(this.inlinePopup[i]["popup_id"]).onkeydown= keyDown;
			else
				document.getElementById(this.inlinePopup[i]["popup_id"]).onkeypress= keyDown;
		}
		
		if(this.settings["allow_resize"]!="no")
		{
			document.getElementById("resize_area").style.visibility="visible";
			document.getElementById("resize_area").onmouseup= editArea.start_resize;
		}
		
		parent.editAreaLoader.toggle(this.id, "on");
		//this.textarea.focus();
		// line selection init
		this.change_smooth_selection_mode(editArea.smooth_selection);
		// highlight
		this.execCommand("change_highlight", this.settings["start_highlight"]);
		
		// get font size datas		
		this.set_font(editArea.settings["font_family"], editArea.settings["font_size"]);
		
		// set unselectable text
		children= parent.getChildren(document.body, "", "selec", "none", "all", -1);
		for(var i in children){
			if(this.nav['isIE'])
				children[i].unselectable = true; // IE
			else
				children[i].onmousedown= function(){return false};
		/*	children[i].style.MozUserSelect = "none"; // Moz
			children[i].style.KhtmlUserSelect = "none";  // Konqueror/Safari*/
		}
		
		if(this.nav['isGecko']){
			this.textarea.spellcheck= this.settings["gecko_spellcheck"];
		}
		
		if(this.nav['isOpera']){
			document.getElementById("editor").style.position= "absolute";
			document.getElementById("selection_field").style.marginTop= "-1pt";			
			document.getElementById("selection_field").style.paddingTop= "1pt";
			document.getElementById("cursor_pos").style.marginTop= "-1pt";
			document.getElementById("end_bracket").style.marginTop= "-1pt";
			document.getElementById("content_highlight").style.marginTop= "-1pt";
			/*document.getElementById("end_bracket").style.marginTop="1px";*/
		}
		if(this.nav['isIE']>=7){
			/*document.getElementById("editor").style.marginTop="-2px";
			document.getElementById("editor").style.marginLeft="-2px";*/
		}
		
		setTimeout("editArea.manage_size();", 10);		
		//start checkup routine
		this.check_undo();
		this.check_line_selection(true);
		this.scroll_to_view();
		
		
		for(var i in this.plugins){
			if(typeof(this.plugins[i].onload)=="function")
				this.plugins[i].onload();
		}
		/*date= new Date();
		alert(date.getTime()- parent.editAreaLoader.start_time);*/
	};
	
	EditArea.prototype.manage_size= function(){
		if(editAreas[this.id]["displayed"]==true)
		{
			var resized= false;
			area_width= this.textarea.scrollWidth;
			area_height= this.textarea.scrollHeight;
			if(this.nav['isOpera']){
				area_height= this.last_selection['nb_line']*this.lineHeight;
				//area_width-=45;
				area_width=10000; /* TODO: find a better way to fix the width problem */
				//elem= document.getElementById("container");
				//elem= this.textarea;
				//window.status="area over: area_width "+area_width+" scroll: "+elem.scrollWidth+" offset: "+elem.offsetWidth +" client: "+ elem.clientWidth+" style: "+elem.style.width;
				//window.status+=" area_height "+area_height+" scroll: "+elem.scrollHeight+" offset: "+elem.offsetHeight +" client: "+ elem.clientHeight;				
			}
			
			if(this.nav['isIE']==7)
				area_width-=45;
	
			if(this.nav['isGecko'] && this.smooth_selection && this.last_selection["nb_line"])
				area_height= this.last_selection["nb_line"]*this.lineHeight;
				
			if(this.last_selection["nb_line"] >= this.line_number)
			{
				var div_line_number="";
				for(i=this.line_number+1; i<this.last_selection["nb_line"]+100; i++)
				{
					div_line_number+=i+"<br />";
					this.line_number++;
				}
				var span= document.createElement("span");
				if(this.nav['isIE'])
					span.unselectable=true;
				span.innerHTML=div_line_number;					
				document.getElementById("line_number").appendChild(span);				
			}
			//alert(area_height);
			if(this.textarea.previous_scrollWidth!=area_width)
			{	// need width resizing
				if(this.nav['isOpera']){
					/*if(this.textarea.style.width.replace("px","")-0+50 < area_width)
						area_width+=50;*/
				}else{
					if(this.textarea.style.width.replace("px","") < area_width){
						area_width+=50;
					}
				}
				
				if(this.nav['isGecko'] || this.nav['isOpera'])
					document.getElementById("container").style.width= (area_width+45)+"px";
				else
					document.getElementById("container").style.width= area_width+"px";
				document.getElementById("textarea").style.width= area_width+"px";
				document.getElementById("content_highlight").style.width= area_width+"px";	
				this.textarea.previous_scrollWidth=area_width;
				resized=true;
			}		
			if(this.textarea.previous_scrollHeight!=area_height)	
			{	// need height resizing
				/*container_height=area_height;
				if(document.getElementById("container").style.height.replace("px", "")<=area_height)
					container_height+=100;*/
				document.getElementById("container").style.height= (area_height+2)+"px";
				document.getElementById("textarea").style.height= area_height+"px";
				document.getElementById("content_highlight").style.height= area_height+"px";	
				this.textarea.previous_scrollHeight=area_height;
				//alert(area_height);
				resized=true;
			}
			this.textarea.scrollTop="0px";
			this.textarea.scrollLeft="0px";
			if(resized==true){
				this.scroll_to_view();
			}
		}
		setTimeout("editArea.manage_size();", 100);
	};
	
	EditArea.prototype.addEvent = function(obj, name, handler) {
		if (this.nav['isIE']) {
			obj.attachEvent("on" + name, handler);
		} else{
			obj.addEventListener(name, handler, false);
		}
	};
	
	EditArea.prototype.execCommand= function(cmd, param){
			
		for(var i in this.plugins){
			if(typeof(this.plugins[i].execCommand)=="function"){
				if(!this.plugins[i].execCommand(cmd, param))
					return;
			}
		}
		switch(cmd){
			case "save":
				if(this.settings["save_callback"].length>0)
					eval("parent."+this.settings["save_callback"]+"(editArea.textarea.value);");
				break;
			case "load":
				if(this.settings["load_callback"].length>0)
					eval("parent."+this.settings["load_callback"]+"(editArea.textarea);");
				break;			
			case "re_sync":
				if(!this.do_highlight)
					break;
			default:
				//alert(cmd+"\n"+params);
				if(typeof(eval("editArea."+cmd))=="function")
					eval("editArea."+ cmd +"(param);");	
		}
	};
	
	EditArea.prototype.get_translation= function(word, mode){
		if(mode=="template")
			return parent.editAreaLoader.translate(word, this.settings["language"], mode);
		else
			return parent.editAreaLoader.get_word_translation(word, this.settings["language"]);
	};
	
	EditArea.prototype.add_plugin= function(plug_name, plug_obj){
		for(var i in this.settings["plugins"]){
			if(this.settings["plugins"][i]==plug_name){
				this.plugins[plug_name]=plug_obj;
				plug_obj.baseURL=parent.editAreaLoader.baseURL + "plugins/" + plug_name + "/";
				if( typeof(plug_obj.init)=="function" )
					plug_obj.init();
			}
		}
	};
	
	EditArea.prototype.load_css= function(url){
		try{
			link = document.createElement("link");
			link.type = "text/css";
			link.rel= "stylesheet";
			link.media="all";
			link.href = url;
			head = document.getElementsByTagName("head");
			head[0].appendChild(link);
		}catch(e){
			document.write("<link href='"+ url +"' rel='stylesheet' type='text/css' />");
		}
	};
	
	EditArea.prototype.load_script= function(url){
		try{
			script = document.createElement("script");
			script.type = "text/javascript";
			script.src  = url;
			head = document.getElementsByTagName("head");
			head[0].appendChild(script);
		}catch(e){
			document.write("<script type='text/javascript' src='" + url + "'><"+"/script>");
		}
	};
	
	// add plugin translation to language translation array
	EditArea.prototype.add_lang= function(language, values){
		if(!parent.editAreaLoader.lang[language])
			parent.editAreaLoader.lang[language]=new Array();
		for( var i in values)
			parent.editAreaLoader.lang[language][i]= values[i];
	};
	

	var editArea = new EditArea();	
	editArea.addEvent(window, "load", init);
	
	function init(){		
		setTimeout("editArea.init();  ", 10);
	};
	
