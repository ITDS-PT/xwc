//ExtsJS 2.2 (XEO Version) does not have this property, we needed to create itExt.isObject = function(v){ return v && typeof v == "object"; }//ExtsJS 2.2 (Xeo Version) has a bug with insertAtCursor in Google ChromeExt.override(Ext.form.HtmlEditor,{	onEditorEvent:function(){if(Ext.isIE){this.currentRange = this.getDoc().selection.createRange();}this.updateToolbar();},    insertAtCursor:function(text){        if(!this.activated){return;}        if(Ext.isIE){            this.win.focus();            if(this.copyRange){                this.copyRange.select();                if(this.selectionType == this.NODE_TYPE.CONTROL){ this.copyRange.collapse(); }                if(this.selectionType == this.NODE_TYPE.TEXT){ this.copyRange.expand("word"); }				this.copyRange.pasteHTML(text);				this.copyRange.collapse(false);                this.syncValue();                this.deferFocus();            } else {                var r = this.currentRange;				if(r){					r.collapse(true);					r.pasteHTML(text);					this.syncValue();					this.deferFocus();				}            }        }else{this.win.focus();this.execCmd('InsertHTML', text);this.deferFocus();}    },	setValue:function(a){		//Need to remove the "\" that showed up while editing source code		a = a.split("\\\\n").join("");a = a.split("\\n").join("");a = a.split("\\'").join("'");		Ext.form.HtmlEditor.superclass.setValue.call(this,a);this.pushValue();	},	getSelectedText: function(clip){        var doc = this.getDoc(), selDocFrag;        var txt = '', hasHTML = false, selNodes = [], ret, html = '';        if (this.win.getSelection || doc.getSelection) {            // FF, Chrome, Safari            var sel = this.win.getSelection();            if (!sel) {                sel = doc.getSelection();            }            if (clip) {                selDocFrag = sel.getRangeAt(0).extractContents();            } else {                selDocFrag = this.win.getSelection().getRangeAt(0).cloneContents();            }            Ext.each(selDocFrag.childNodes, function(n){                if (n.nodeType !== 3) {                    hasHTML = true;                }            });            if (hasHTML) {                var div = document.createElement('div');                div.appendChild(selDocFrag);                html = div.innerHTML;                txt = this.win.getSelection() + '';            } else {                html = txt = selDocFrag.textContent;            }            ret = {                textContent: txt,                hasHTML: hasHTML,                html: html            };        } else if (doc.selection) {            // IE            this.win.focus();            txt = doc.selection.createRange();            if (txt.text !== txt.htmlText) {                hasHTML = true;            }            ret = {                textContent: txt.text,                hasHTML: hasHTML,                html: txt.htmlText            };        } else {            return {                textContent: ''            };        }                return ret;    }});Ext.ns('Ext.ux.form.HtmlEditor');//XEO specific buttons//YOUTUBEExt.ux.form.HtmlEditor.YOUTUBE = Ext.extend(Ext.util.Observable, {	init: function(cmp){this.cmp = cmp;this.cmp.on('render', this.onRender, this);},	onRender: function(){		var toolbar = this.cmp.getToolbar();		toolbar.addButton([{			iconCls: 'xeo-youtube',			handler: function(){				if(!this.youtubeWindow){					this.youtubeWindow=new Ext.Window({						title:"<img src='ext-xeo/htmlAdvanced/imgs/youtube.png'>&nbsp;Insert YouTube video ID",closeAction:"hide",width:300,height:200,layout:'fit',						items:[						{							itemId:"insert-youtube",xtype:"form",border:false,plain:true,bodyStyle:"padding: 10px;",labelWidth:60,labelAlign:"right",							items:[								{xtype:"label",	html:"Enter the ID of the YouTube video you want to display.<br/>&nbsp;"},								{xtype:"textfield",fieldLabel:"ID",name:"youtubeid",width:150,itemCls:'xwc-form-label xwc-form-required'},								{xtype:"textfield",fieldLabel:"Width",name:"youtubewidth",width:50,maskRe:/[0-9]|%/,regex:/^[1-9][0-9%]{1,3}/,itemCls:'xwc-form-label xwc-form-required'},								{xtype:"textfield",fieldLabel:"Height",name:"youtubeheight",width:50,maskRe:/[0-9]|%/,regex:/^[1-9][0-9%]{1,3}/,itemCls:'xwc-form-label xwc-form-required'}							]						}],						buttons:[						{							text:"Insert",							handler:function(){								var c=this.youtubeWindow.getComponent("insert-youtube").getForm();								if(c.isValid()){this.doInsertYouTube()}else{c.findField("ID").getEl().frame()}							},							scope:this						},						{							text:"Cancel",							handler:function(){								this.youtubeWindow.hide()							},							scope:this						}]					})				}else{this.youtubeWindow.getEl().frame()}				this.youtubeWindow.show()			},			scope: this,tooltip: 'Insert YouTube',overflowText: 'Insert YouTube'		}]);	},	doInsertYouTube:function(){		var b=this.youtubeWindow.getComponent("insert-youtube").getForm();		if(b.isValid()){			var youId =b.findField("youtubeid").getValue();			var youWidth =b.findField("youtubewidth").getValue();			var youHeight =b.findField("youtubeheight").getValue();						if(youId && youWidth && youHeight){				this.cmp.insertAtCursor('<object data="http:\/\/www.youtube.com\/v\/'+youId+'&amp;rel=1" height="'+ youHeight				+'" type="application\/x-shockwave-flash" width="'+ youWidth+'"><param name="wmode" value="transparent" \/><param name="src" value="http:\/\/www.youtube.com\/v\/'+ youId				+'&amp;rel=1" \/><\/object><br \/><br \/>');				b.reset();				this.youtubeWindow.hide()			}		}	}});//GOOGLE MAPSExt.ux.form.HtmlEditor.GMAPS = Ext.extend(Ext.util.Observable, {	init: function(cmp){this.cmp = cmp;this.cmp.on('render', this.onRender, this);},	onRender: function(){		var toolbar = this.cmp.getToolbar();		toolbar.addButton([{			iconCls: 'xeo-gmaps',			handler: function(){				if(!this.gMapsWindow){					this.gMapsWindow=new Ext.Window({						title:"<img src='ext-xeo/htmlAdvanced/imgs/map.jpg'>&nbsp;Insert Google Map",closeAction:"hide",width: 470,height: 250,layout: 'fit',						items:[						{							itemId:"insert-gMap",xtype:"form",border:false,plain:true,bodyStyle:"padding: 10px;",labelWidth:220,labelAlign:"right",							items:[								{xtype:"label",html:"<b>Enter the map details to display:</b><br/>&nbsp;"},								{xtype:"textfield",fieldLabel:"Latitude (between -85 and 85)",name:"mapLatitude",width:100,								itemCls: 'xwc-form-label xwc-form-required',regex:/^(-8[0-5]|-[1-7][0-9]|-[1-9]|[0-9]|[1-7][0-9]|8[0-5])/},								{xtype:"textfield",fieldLabel:"Longitude (between -180 and 180)",name:"mapLongitude",width:100,								itemCls: 'xwc-form-label xwc-form-required',regex:/^(-180|-1[0-7][0-9]|-[1-9][0-9]|-[1-9]|180|1[0-7][0-9]|[1-9][0-9]|[0-9])/},								{xtype:"textfield",fieldLabel:"Text to display in given point",name:"mapDesc",width:200,								itemCls:'xwc-form-label xwc-form-required'},								{xtype:"textfield",fieldLabel:"Map width",name:"mapWidth",width:100},								{xtype:"textfield",fieldLabel:"Map height",name:"mapHeight",width:100}							]						}],						buttons:[							{text:"Insert",							 handler:function(){								var c=this.gMapsWindow.getComponent("insert-gMap").getForm();								if(c.isValid()){this.doInsertMap()}							},							scope:this						},						{text:"Cancel",						 handler:function(){this.gMapsWindow.hide()	},							scope:this						}]					})				}else{ this.gMapsWindow.getEl().frame()	}				this.gMapsWindow.show()			},			scope: this,tooltip: 'Insert Google Map',overflowText: 'Insert Google Map'		}]);	},	doInsertMap:function(){		var b=this.gMapsWindow.getComponent("insert-gMap").getForm();		if(b.isValid()){			var mapLat =b.findField("mapLatitude").getValue();var mapLong =b.findField("mapLongitude").getValue();var mapDesc =b.findField("mapDesc").getValue();			var mapWidth =b.findField("mapWidth").getValue();var mapHeight =b.findField("mapHeight").getValue();			if (!mapWidth){mapWidth="100%";}						if (!mapHeight){mapHeight="100%";}						var mapdivId = new Date().getMilliseconds();			var scripts = "<script type='text\/javascript' src='http:\/\/maps.google.com\/maps?file=api&amp;v=2&amp;sensor=true&amp;key=ABQIAAAAAaVFxs6kNq7gWY59qf5XMxSec6s_uUscdbTyPSy8oWl8zYzqFRRanjFebOU60thMmEQQDEPx3A3y5Q'><\/script><script type='text\/javascript' src='"+ getBaseUrl() +"ext-xeo\/htmlAdvanced\/js\/maps.js'><\/script>";			var existingContent = this.cmp.getValue();			var html2Insert = "<div id='"+ mapdivId +"' style='width: "+ mapWidth +"; height: "+ mapHeight +"; background: #dddddd;'>MAP: (Lat. "+ mapLat +", Long. "+ mapLong +" - "+ mapDesc +")</div>";			if ( existingContent.indexOf( "<!--GOOGLE_MAPS-->" ) < 0 ){//se ainda n�o foi inclu�do								html2Insert += '<!--GOOGLE_MAPS-->'+scripts;			}					var script2Include = "<br><SCRIPT type='text\/javascript'>initializeMap('"+ mapdivId +"', '"+ mapLat +"', '"+ mapLong +"', '"+ mapDesc +"');</SCRIPT><br>";						if(mapLat && mapLong && mapDesc){				this.cmp.insertAtCursor(html2Insert);				this.cmp.setValue( this.cmp.getValue()+script2Include );//tem de incluir o arranque to mapa no fim				b.reset();				this.gMapsWindow.hide()			}		}	}});//FLASHExt.ux.form.HtmlEditor.FLASH = Ext.extend(Ext.util.Observable, {								init: function(cmp){ this.cmp = cmp;this.cmp.on('render', this.onRender, this);},	onRender: function(){		var toolbar=this.cmp.getToolbar();		toolbar.addButton([{			iconCls:'xeo-flash',			handler:function(){			var u=getBaseUrl();			u+="X3OCM_util_lookupB.xvw?pluginToLoad=x3ocm_flash&objsToList=XEOCM_Flash&cmpId="+this.cmp.id+"&winId=flashWin&obj=XEOCM_Flash";						var winFlash=new Ext.Window({				id:'flashWin',layout:'fit',width:550,height:400,title:"<img width='16' height='16' src='ext-xeo/htmlAdvanced/imgs/flash.png'>&nbsp;Flash",				closeAction:'destroy',plain:true,html:'<iframe name="fraFlash" src="'+u+'" scrolling=no frameBorder="0" width="100%" height="100%"></iframe>'			});			winFlash.show(this);			XVW.lookupWindow=winFlash;			},			scope:this,tooltip:'Insert Flash',overflowText:'Insert Flash'		}]);	}});//XEOCM_ImageExt.ux.form.HtmlEditor.XEO_IMAGE = Ext.extend(Ext.util.Observable,{	init:function(cmp){this.cmp=cmp;this.cmp.on('render',this.onRender,this);},	onRender:function(){		var toolbar=this.cmp.getToolbar();		toolbar.addButton([{			iconCls:'xeo-image',			handler:function(){				var u=getBaseUrl();								u+="X3OCM_util_lookupB.xvw?pluginToLoad=x3ocm_img&objsToList="+this.cmp.imageObjs+"&cmpId="+this.cmp.id+"&winId=xeoCmImageWin&obj=XEOCM_Image";				var winXEOImage = new Ext.Window({					id:'xeoCmImageWin',layout:'fit',width:550,height:400,					title:"<img width='16' height='16' src='ext-xeo/htmlAdvanced/imgs/xeocm_img.gif'>&nbsp;XEOCM Image",					closeAction:'destroy',plain: true,html:'<iframe name="fraXeoCmImage" src="'+u+'" scrolling=no frameBorder="0" width="100%" height="100%"></iframe>'				});				winXEOImage.show(this);				XVW.lookupWindow=winXEOImage;			},			scope:this,			tooltip:'Insert Xeo Image',			overflowText:'Insert Xeo Image'		}]);	}});//XEOCM_ContentsExt.ux.form.HtmlEditor.XEO_CONTENTS = Ext.extend(Ext.util.Observable,{	init: function(cmp){this.cmp = cmp;this.cmp.on('render', this.onRender, this);},	onRender: function(){				var toolbar = this.cmp.getToolbar();		toolbar.addButton([{			iconCls: 'xeo-contents',			handler: function(){				var u=getBaseUrl();								u+= "X3OCM_util_lookupB.xvw?pluginToLoad=x3ocm_lnk&objsToList="+this.cmp.contentObjs+"&cmpId="+this.cmp.id+"&winId=xeoContsWin&obj=XEOCM_Contents";							var winXEOLnk = new Ext.Window({					id: 'xeoCmLnkWin',layout:'fit',width:550,height:400,					title: "<span class='x-window-header-text'><span><img width='16' height='16' src='ext-xeo/htmlAdvanced/imgs/xeocm_lnk.gif'>XEOCM Link</span></span>",					closeAction:'destroy',plain:true,html:'<iframe name="fraXeoCmLnk" src="'+u+'" frameBorder="0" scrolling=no width="100%" height="100%"></iframe>'				});				winXEOLnk.show(this);				XVW.lookupWindow = winXEOLnk;			},			scope: this,tooltip: 'Insert Xeo Link',overflowText: 'Insert Xeo Link'		}]);	}});//other pluginsExt.ux.form.HtmlEditor.MidasCommand=Ext.extend(Ext.util.Observable,{init:function(a){this.cmp=a;this.btns=[];this.cmp.on("render",this.onRender,this);this.cmp.on("initialize",this.onInit,this,{delay:100,single:true})},onInit:function(){Ext.EventManager.on(this.cmp.getDoc(),{mousedown:this.onEditorEvent,dblclick:this.onEditorEvent,click:this.onEditorEvent,keyup:this.onEditorEvent,buffer:100,scope:this})},onRender:function(){var c,a=this.cmp.getToolbar(),b;Ext.each(this.midasBtns,function(d){if(Ext.isObject(d)){c={iconCls:"x-edit-"+d.cmd,handler:function(){this.cmp.relayCmd(d.cmd)},scope:this,tooltip:d.tooltip||{title:d.title},overflowText:d.overflowText||d.title}}else{c=new Ext.Toolbar.Separator()}b=a.addButton(c);if(d.enableOnSelection){b.disable()}this.btns.push(b)},this)},onEditorEvent:function(){var a=this.cmp.getDoc();Ext.each(this.btns,function(c,d){if(this.midasBtns[d].enableOnSelection||this.midasBtns[d].disableOnSelection){if(a.getSelection){if((this.midasBtns[d].enableOnSelection&&a.getSelection()!=="")||(this.midasBtns[d].disableOnSelection&&a.getSelection()==="")){c.enable()}else{c.disable()}}else{if(a.selection){if((this.midasBtns[d].enableOnSelection&&a.selection.createRange().text!=="")||(this.midasBtns[d].disableOnSelection&&a.selection.createRange().text==="")){c.enable()}else{c.disable()}}}}if(this.midasBtns[d].monitorCmdState){c.toggle(a.queryCommandState(this.midasBtns[d].cmd))}},this)}});Ext.ux.form.HtmlEditor.Divider=Ext.extend(Ext.util.Observable,{init:function(a){this.cmp=a;this.cmp.on("render",this.onRender,this)},onRender:function(){this.cmp.getToolbar().addButton([new Ext.Toolbar.Separator()])}});Ext.ux.form.HtmlEditor.IndentOutdent=Ext.extend(Ext.ux.form.HtmlEditor.MidasCommand,{midasBtns:["|",{cmd:"indent",tooltip:{title:"Indent Text"},overflowText:"Indent Text"},{cmd:"outdent",tooltip:{title:"Outdent Text"},overflowText:"Outdent Text"}]});Ext.ux.form.HtmlEditor.SubSuperScript=Ext.extend(Ext.ux.form.HtmlEditor.MidasCommand,{midasBtns:["|",{enableOnSelection:true,cmd:"subscript",tooltip:{title:"Subscript"},overflowText:"Subscript"},{enableOnSelection:true,cmd:"superscript",tooltip:{title:"Superscript"},overflowText:"Superscript"}]});Ext.ux.form.HtmlEditor.SpecialCharacters=Ext.extend(Ext.util.Observable,{specialChars:[],charRange:[160,256],	init:function(a){this.cmp=a;this.cmp.on("render",this.onRender,this)},onRender:function(){			var b=this.cmp;			var a=this.cmp.getToolbar().addButton({				iconCls:"x-edit-char",				handler:function(){					if(this.specialChars.length==0){						Ext.each(this.specialChars,function(e,d){							this.specialChars[d]=["&#"+e+";"]},this)						for(i=this.charRange[0];i<this.charRange[1];i++){							this.specialChars.push(["&#"+i+";"])						}					}					var c=new Ext.data.SimpleStore({						fields:["char"],						data:this.specialChars					});					this.charWindow=new Ext.Window({						title:"<img src='ext-xeo/htmlAdvanced/imgs/edit-char.png'>&nbsp;Insert Special Character",						width:436,						autoHeight:true,						layout:"fit",						items:[{xtype:"label",html:"To insert a special character just double click the one you want&nbsp;"},							{							xtype:"dataview",							store:c,							ref:"../charView",							autoHeight:true,							multiSelect:true,							tpl:new Ext.XTemplate('<tpl for="."><div class="char-item">{char}</div></tpl><div class="x-clear"></div>'),							overClass:"char-over",							itemSelector:"div.char-item",							listeners:{								dblclick:function(f,d,h,g){									this.insertChar(f.getStore().getAt(d).get("char"));									this.charWindow.close()},scope:this							}						}],						buttons:[							{								text:"Cancel",								handler:function(){									this.charWindow.close()},scope:this							}]					});					this.charWindow.show()				},				scope:this,tooltip:{title:"Insert Special Character"},overflowText:"Special Characters"			})},			insertChar:function(a){if(a){this.cmp.insertAtCursor(a);}}	});Ext.ux.form.HtmlEditor.Table=Ext.extend(Ext.util.Observable,{cmd:"table",tableBorderOptions:[["none","None"],["solid 1px #000","Solid Thin"],["solid 2px #000","Solid Thick"],["1px dashed #000","Dashed"],["1px dotted #000","Dotted"]],init:function(a){this.cmp=a;this.cmp.on("render",this.onRender,this)},onRender:function(){var b=this.cmp;var a=this.cmp.getToolbar().addButton({iconCls:"x-edit-table",handler:function(){if(!this.tableWindow){this.tableWindow=new Ext.Window({title:"<img src='ext-xeo/htmlAdvanced/imgs/edit-table.png'>&nbsp;Insert Table",width:200,height:180,closeAction:"hide",items:[{itemId:"insert-table",xtype:"form",border:false,plain:true,bodyStyle:"padding: 10px;",labelWidth:60,labelAlign:"right",items:[{xtype:"numberfield",allowBlank:false,allowDecimals:false,fieldLabel:"Rows",itemCls: 'xwc-form-label xwc-form-required',name:"row",width:60},{xtype:"numberfield",itemCls: 'xwc-form-label xwc-form-required',allowBlank:false,allowDecimals:false,fieldLabel:"Columns",name:"col",width:60},{xtype:"combo",fieldLabel:"Border",name:"border",forceSelection:true,mode:"local",store:new Ext.data.SimpleStore({autoDestroy:true,fields:["spec","val"],data:this.tableBorderOptions}),triggerAction:"all",value:"none",displayField:"val",valueField:"spec",width:90}]}],buttons:[{text:"Insert",handler:function(){var g=this.tableWindow.getComponent("insert-table").getForm();if(g.isValid()){var e=g.findField("border").getValue();var c=[g.findField("row").getValue(),g.findField("col").getValue()];if(c.length==2&&c[0]>0&&c[0]<10&&c[1]>0&&c[1]<10){var f="<table>";for(var h=0;h<c[0];h++){f+="<tr>";for(var d=0;d<c[1];d++){f+="<td width='20%' style='border: "+e+";'>"+h+"-"+d+"</td>"}f+="</tr>"}f+="</table>";this.cmp.insertAtCursor(f); g.reset();}this.tableWindow.hide()}else{if(!g.findField("row").isValid()){g.findField("row").getEl().frame()}else{if(!g.findField("col").isValid()){g.findField("col").getEl().frame()}}}},scope:this},{text:"Cancel",handler:function(){this.tableWindow.hide()},scope:this}]})}else{this.tableWindow.getEl().frame()}this.tableWindow.show();},scope:this,tooltip:{title:"Insert Table"},overflowText:"Table"})}});Ext.ux.form.HtmlEditor.HR=Ext.extend(Ext.util.Observable,{cmd:"hr",	init:function(a){this.cmp=a;this.cmp.on("render",this.onRender,this);},	onRender:function(){var b=this.cmp;		var a=this.cmp.getToolbar().addButton(		{			iconCls:"x-edit-hr",			handler:function(){				if(!this.hrWindow){					this.hrWindow=new Ext.Window(					{						title:"<img src='ext-xeo/htmlAdvanced/imgs/edit-rule.png'>&nbsp;Insert Rule",						closeAction:"hide",						width:233,						height:170,						items:[						{	itemId:"insert-hr",xtype:"form",border:false,plain:true,bodyStyle:"padding: 10px;",labelWidth:60,labelAlign:"right",							items:[								{									xtype:"label",									html:"Enter the width of the Rule in percentage<br/> followed by the % sign at the end, or to<br/> set a fixed width ommit the % symbol.<br/>&nbsp;"								},								{									xtype:"textfield",maskRe:/[0-9]|%/,regex:/^[1-9][0-9%]{1,3}/,fieldLabel:"Width",itemCls: 'xwc-form-label xwc-form-required',									name:"hrwidth",width:60,									listeners:									{										specialkey:function(c,d){											if((d.getKey()==d.ENTER||d.getKey()==d.RETURN)&&c.isValid()){this.doInsertHR();}											else{c.getEl().frame();}										},										scope:this									}								}]						}],						buttons:[						{							text:"Insert",							handler:function(){								var c=this.hrWindow.getComponent("insert-hr").getForm();								if(c.isValid()){									this.doInsertHR();								}else{									c.findField("hrwidth").getEl().frame();								}							},							scope:this						},						{							text:"Cancel",							handler:function(){								this.hrWindow.hide();							},							scope:this						}]					});				}else{this.hrWindow.getEl().frame();	}				this.hrWindow.show();			},			scope:this,tooltip:{title:"Insert Horizontal Rule"},overflowText:"Horizontal Rule"		});	},	doInsertHR:function(){		var b=this.hrWindow.getComponent("insert-hr").getForm();		if(b.isValid()){			var a=b.findField("hrwidth").getValue();							if(a){this.insertHR(a);}			else{this.insertHR("100%");}			b.reset();			this.hrWindow.hide();		}	},	insertHR:function(a){this.cmp.insertAtCursor('<hr width="'+a+'">');}});	//SMILEY	Ext.ux.form.HtmlEditor.Smileys = Ext.extend(Ext.util.Observable, {    langTitle:'<img src="ext-xeo/htmlAdvanced/imgs/smile.png">&nbsp;Insert Smileys',    langInsert:'Insert',langCancel:'Cancel',imageUrl:getBaseUrl()+'ext-xeo/htmlAdvanced/imgs/emoticons/',    init: function(cmp){this.cmp = cmp;this.cmp.on('render', this.onRender, this);},    onRender: function(){        var cmp = this.cmp;var btn = this.cmp.getToolbar().addButton({            iconCls: 'x-edit-smiley',            handler: function(){                var smileyStore = new Ext.data.SimpleStore({                    fields: ['url','altname'],                    data: [	    				['smile.gif','Smile'],['cool.gif','Cool'],['cry.gif','Cry'],['laughing.gif','Laughing'],['embarassed.gif','Embarassed'],['tongue.gif','Tongue'],						['foot-in-mouth.gif','Foot in mouth'],['frown.gif','Frown'],['innocent.gif','Innocent'],['kiss.gif','Kiss'],['money-mouth.gif','Money mouth'],						['sealed.gif','Sealed'],['surprised.gif','Surprised'],['undecided.gif','Undecided'],['wink.gif','Wink'],['yell.gif','Yell']											]                });				this.smileyView = new Ext.DataView({                        store: smileyStore,ref: 'smileyView',autoHeight: true,multiSelect: true,                        tpl: new Ext.XTemplate('<tpl for="."><div class="smiley-item"><img src="'+this.imageUrl+'{url}" title="{altname}"></div></tpl><div class="x-clear"></div>'),                        overClass: 'smiley-over',itemSelector: 'div.smiley-item',						listeners: {                            dblclick: function(t, i, n, e){                                this.insertSmiley(t.getStore().getAt(i).get('url'), t.getStore().getAt(i).get('altname'));                                this.smileyWindow.close();                            },                            scope: this                        }				});                this.smileyWindow = new Ext.Window({                    title: this.langTitle,width: 250,autoHeight: true,layout: 'fit',items: [{xtype:"label",html:"To insert an emoticon just double click the one you want&nbsp;"}, this.smileyView],                    buttons: [{                        text: this.langCancel,                        handler: function(){                            this.smileyWindow.close();                        },                        scope: this                    }]                });                this.smileyWindow.show();            },            scope: this,tooltip:{title: 'Insert Smiley'},overflowText: this.langTitle        });    },        insertSmiley: function(url,altname){if (url) {this.cmp.insertAtCursor('<img src="'+this.imageUrl+url+'" alt="'+altname+'">');}}});//IMAGEExt.ux.form.HtmlEditor.Image = Ext.extend(Ext.util.Observable, {	urlSizeVars: ['width','height'],    init: function(cmp){this.cmp=cmp;this.cmp.on('render',this.onRender,this);this.cmp.on('initialize',this.onInit,this,{delay:100, single: true});},    onEditorMouseUp : function(e){		if ( Ext.get ){			try{				Ext.get(e.getTarget()).select('img').each(function(el){					if(el!=null){						if (el.getAttribute){							var w = el.getAttribute('width'), h = el.getAttribute('height'), src = el.getAttribute('src')+' ';							src = src.replace(new RegExp(this.urlSizeVars[0]+'=[0-9]{1,5}([&| ])'), this.urlSizeVars[0]+'='+w+'$1');							src = src.replace(new RegExp(this.urlSizeVars[1]+'=[0-9]{1,5}([&| ])'), this.urlSizeVars[1]+'='+h+'$1');							el.set({src:src.replace(/\s+$/,"")});						}					}				}, this);			}catch(err){}		}    },    onInit: function(){Ext.EventManager.on(this.cmp.getDoc(),{'mouseup': this.onEditorMouseUp,buffer: 100,scope: this});},    onRender: function() {        var btn = this.cmp.getToolbar().addButton({            iconCls: 'x-edit-image',            scope: this,            tooltip: 'Insert Image',			handler: function(){				this.imgWindow=new Ext.Window({					title:"<img src='ext-xeo/htmlAdvanced/imgs/edit-image.png'>&nbsp;Insert Image",closeAction:"hide",width: 400,height: 250,layout: 'fit',					items:[					{						itemId:"insert-image",xtype:"form",border:false,plain:true,	bodyStyle:"padding: 10px;",labelWidth:120,labelAlign:"right",						items:[							{xtype:"label",html:"Enter the settings of the image that you want to display.<br/>&nbsp;"},							{xtype:"textfield",fieldLabel:"Image Url<br>(with&nbsp;image&nbsp;name)",name:"imgUrl",width:150,itemCls:'xwc-form-label xwc-form-required'},							{xtype:"textfield",fieldLabel:"Alt/Title text",name:"imgAlt",itemCls:'xwc-form-label xwc-form-required',width:150},							{xtype:"textfield",fieldLabel:"Width",name:"imgWidth",width:50,itemCls: 'xwc-form-label xwc-form-required',maskRe:/[0-9]|%/,regex:/^[1-9][0-9%]{1,3}/},							{xtype:"textfield",fieldLabel:"Height",name:"imgHeight",itemCls: 'xwc-form-label xwc-form-required',width:50,maskRe:/[0-9]|%/,regex:/^[1-9][0-9%]{1,3}/}						]					}],					buttons:[						{							text:"Insert",							handler:function(){								var c=this.imgWindow.getComponent("insert-image").getForm();								if(c.isValid()){									var imgUrl =c.findField("imgUrl").getValue();var imgAlt =c.findField("imgAlt").getValue();									var imgWidth =c.findField("imgWidth").getValue();var imgHeight =c.findField("imgHeight").getValue();																		if(imgUrl && imgAlt && imgWidth && imgHeight){										this.cmp.insertAtCursor('<img src="'+imgUrl+'" height="'+ imgHeight +'" width="'+ imgWidth +'" alt="'+ imgAlt +'" title="'+ imgAlt +'"><br>');										c.reset();										this.imgWindow.hide();									}								}							},							scope:this						},						{							text:"Cancel",							handler:function(){								this.imgWindow.hide();							},							scope:this						}]				});				this.imgWindow.show();			},overflowText: 'Insert Image'        });    }});//ANCHOR	Ext.ux.form.HtmlEditor.Anchor = Ext.extend(Ext.util.Observable, {      init: function(cmp){this.cmp=cmp;this.cmp.on('render', this.onRender, this);},        onRender: function() {        var btn = this.cmp.getToolbar().addButton({            iconCls: 'x-edit-anchor',scope:this,tooltip:'Insert Achor',			handler: function(){				if(!this.anchorWindow){					this.anchorWindow=new Ext.Window({											title:"<img src='ext-xeo/htmlAdvanced/imgs/edit-anchor.png'>&nbsp;Insert Anchor",closeAction:"hide",width:450,height:200,layout:'fit',						items:[							{								itemId:"insert-anchor",								xtype:"form",								border:false,								plain:true,								bodyStyle:"padding: 10px;",								labelWidth:95,								labelAlign:"right",								items:[									{										xtype:"label",										html:"Enter the anchor settings:<br/>&nbsp;"									},									{										xtype:"textfield",										fieldLabel:"Achor Name",										name:"anchorName",										itemCls: 'xwc-form-label xwc-form-required',										width:250																			},									{										xtype:"textfield",										fieldLabel:"Achor Text",										name:"anchorTxt",										width:250																			}								]							}],							buttons:[							{								text:"Insert",								handler:function(){									var c=this.anchorWindow.getComponent("insert-anchor").getForm();									if(c.isValid()){										var anchorName =c.findField("anchorName").getValue();										var anchorTxt =c.findField("anchorTxt").getValue();										if(anchorName!=null && anchorName!=''){											this.cmp.insertAtCursor('<br><a name="'+ anchorName	+'">'+ anchorTxt +'</a></br>');											c.reset();											this.anchorWindow.hide();										}									}								},								scope:this							},							{								text:"Cancel",								handler:function(){									this.anchorWindow.hide();								},								scope:this							}]					});				}				this.anchorWindow.show();			},overflowText: 'Insert Anchor'        });    }    });//Link	Ext.ux.form.HtmlEditor.Link = Ext.extend(Ext.util.Observable, {        init: function(cmp){		this.cmp = cmp;		this.cmp.on('render', this.onRender, this);			},        onRender: function() {        var btn = this.cmp.getToolbar().addButton({            iconCls:'x-edit-link',scope:this,tooltip:'Insert Web Link',			handler: function(){				var originalText='';								var doc = this.cmp.getDoc();						var as = doc.getElementsByTagName('a');				var arr=new Array();				for(i=0;i<as.length;i++){					if(as[i].name){							var aux = {aName:as[i].name};						arr.push([aux.aName]);					}				}						// create the data store				this.anchorType = new Ext.data.SimpleStore({fields: [{name: 'aName'}]});								this.anchorType.loadData(arr);								//if(!this.linkWindow){									/*if (Ext.isIE){									this.cmp.win.focus();						if(this.cmp.copyRange){            								originalText = this.cmp.getDoc().selection.createRange().text;										} else {							var r = this.cmp.currentRange;														if(r){											originalText=r.text;													}						}					}else{						originalText= this.cmp.getDoc().getSelection();																	}					if (originalText==null||originalText.legth==0){						originalText="Text to display in link";					}*/									this.linkWindow=new Ext.Window({						title:"<img src='ext-xeo/htmlAdvanced/imgs/edit-link.png'>&nbsp;Insert Web Link",closeAction:"hide",width:450,height:210,layout:'fit',						items:[						{							itemId:"insert-link",xtype:"form",border:false,plain:true,bodyStyle:"padding: 10px;",labelWidth:95,labelAlign:"right",							items:[								{xtype:"label",html:"Enter the link settings:<br/>&nbsp;"},								{xtype:"textfield",fieldLabel:"Link Web Address",name:"linkUrl",width:250,itemCls: 'xwc-form-label xwc-form-required',value:'http://'},																{xtype:"combo",fieldLabel:"Achor Name",name:"anchorName",width:250,displayField:'aName',mode:'local',store:this.anchorType}															]						}],												buttons:[							{								text:"Insert",								handler:function(){									var c=this.linkWindow.getComponent("insert-link").getForm();									if(c.isValid()){										var lnkUrl =c.findField("linkUrl").getValue();										var anchorName =c.findField("anchorName").getValue();																				if ( anchorName ){lnkUrl = lnkUrl +"#"+ anchorName;}																				sel = this.cmp.getSelectedText(true);										var originalText=sel.html;																				if ( originalText==null || originalText=='' ){											if (Ext.isIE){												var r = this.cmp.currentRange;												if(r){													originalText=r.htmlText;												}															}										}																				if ( originalText!=null && originalText!='' ){											//this.cmp.relayCmd("createlink",lnkUrl);												var html = "<a href='"+ lnkUrl +"'>"+ originalText +"</a><br>";																						if (Ext.isIE){															this.cmp.win.focus();															var r = this.cmp.currentRange;												if(r){													r.pasteHTML(html);												}											}else{												this.cmp.insertAtCursor(html);											}										}																			this.linkWindow.hide();										this.linkWindow.destroy();									}								},								scope:this							},							{								text:"Cancel",								handler:function(){									this.linkWindow.hide();									this.linkWindow.destroy();								},								scope:this							}]					});				//}				this.linkWindow.show();			},overflowText: 'Insert Web Link'        });    }});//UTILSfunction handleXeoRetPopupVal(retVal, compId, winId, objName, icon){	var html = '';var comp = Ext.ComponentMgr.get( compId );	if ( objName=='XEOCM_Flash' ){			var fWidth = "100px";var fHeight = "100px";			html='<object standby="Flash Obj. [boui: '+ retVal +']" id="'+ retVal +'" codebase="http:\/\/fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0" '+		'classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" width="'+ fWidth +'" align="middle" '+		'height="'+ fHeight +'" menu="true" loop="true" play="true" xeoobj="true" style="border:dotted 1px black;">'+		'<param name="allowScriptAccess" value="sameDomain" /><param name="movie" value="'+ getBaseUrl() +'xst_loadimg.jsp?id='+ retVal +'" />'+		'<param name="quality" value="high" /><param name="bgcolor" value="#ffffff" />'+		'<embed src="'+ getBaseUrl() +'xst_loadimg.jsp?id='+retVal+'" quality="high" bgcolor="#ffffff" width="'+ fWidth +'" height="'+fHeight+'" name="banner 1" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http:\/\/www.macromedia.com/go/getflashplayer" />'+		'</object>';		comp.insertAtCursor(html);	}else if ( objName == 'XEOCM_Image' ){		html ='<img alt="XEO Image [boui: '+ retVal +']" src="'+getBaseUrl()+'attachfileu.jsp?look_parentBoui='+ retVal +'&att_display=n&att_download=y" /><br><br>';		comp.insertAtCursor(html);	}else if ( objName == 'XEOCM_Contents' ){		//html = '<a href="http://#URL:'+retVal+'#"><img style="CURSOR: hand" class="lui" title="Conteudo" border="0" hspace="3" align="absMiddle" src="'+getBaseUrl()+'resources/XEOCM_Contents/ico16.gif" width="16" height="16" alt="" /><span title="Conte�do [boui: '+retVal+']">Conte&uacute;do [boui: '+retVal+']</span></a>';		//TODO rever a situa��o em cima		//html = '<a href="javascript:void(0);" onclick="window.location=\'http://#URL:'+retVal+'#\'" ><img style="CURSOR: hand" class="lui" title="Conteudo" border="0" hspace="3" align="absMiddle" src="'+getBaseUrl()+'resources/XEOCM_Contents/ico16.gif" width="16" height="16" alt="" /><span title="Conte�do [boui: '+retVal+']">Conte&uacute;do [boui: '+retVal+']</span></a>';				sel = comp.getSelectedText(true);		var originalText=sel.html;				if ( originalText==null || originalText=='' ){			if (Ext.isIE){				var r = comp.currentRange;				if(r){					originalText=r.htmlText;				}							}			if ( originalText==null || originalText=='' ){				originalText='<img style="CURSOR: hand" class="lui" title="Conteudo" border="0" hspace="3" align="absMiddle" src="'+getBaseUrl()+'resources/'+icon+'/ico16.gif" width="16" height="16" alt="" /><span title="Conte�do [boui: '+retVal+']">XEO Link [boui: '+retVal+']</span>';			}		}				html = '<a href="javascript:void(0);" onclick="window.location='+ String.fromCharCode(39) +'http://#URL:'+retVal+'#'+ String.fromCharCode(39) +'" >'+originalText+'</a>';				if (Ext.isIE){						comp.win.focus();						var r = comp.currentRange;			if(r){//originalText=r.htmlText;							r.pasteHTML(html);			}					}else{//originalText= comp.getDoc().getSelection();									comp.insertAtCursor(html);		}	}		if ( XVW.lookupWindow ){XVW.lookupWindow.hide();XVW.lookupWindow.destroy();}}function getBaseUrl(){	var arr=document.getElementsByName("xvw.ajax.resourceUrl");	if (arr.length==1){return arr[0].value;}	else{var loc=parent.location;var u="/"+loc.pathname.split("/")[1]+"/";return u;}}