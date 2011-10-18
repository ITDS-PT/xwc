
Ext.ns('ExtXeo','ExtXeo.Toolbar');

ExtXeo.Toolbar.Separator = function( config ){
    ExtXeo.Toolbar.Separator.superclass.constructor.call(this, config );
};
Ext.extend(ExtXeo.Toolbar.Separator, Ext.Component, {
    enable:Ext.emptyFn,
    disable:Ext.emptyFn,
    focus:Ext.emptyFn,
	setDisabled:Ext.emptyFn,
    hidden: false,
    onRender: function(container, position) {
    	this.el = document.createElement("span");
    	this.el.className = "ytb-sep";
    	Ext.menu.Item.superclass.onRender.call(this, container, position);
    	if( this.hidden )
    		this.el.dom.style.display = 'none';
	},
    hide: function() {
		if( !this.hidden ) {
			this.el.dom.style.display = 'none';
			this.hidden = true;
		}
	},
	show: function() {
		if( !this.hidden ) {
			this.el.dom.style.display = '';
			this.hidden = false;
		}
	}
});


Ext.ns('ExtXeo','ExtXeo.form');

ExtXeo.form.NumberField = Ext.extend(Ext.form.TextField,  {
    
    fieldClass: "x-form-field x-form-num-field",
    allowDecimals : true,
    group : false,
    decimalSeparator : ",",
    groupSeparator : ".",
    decimalPrecision : 2,
    minDecimalPrecision : 2,
    allowNegative : true,
    minValue : Number.NEGATIVE_INFINITY,
    maxValue : Number.MAX_VALUE,
    minText : "The minimum value for this field is {0}",
    maxText : "The maximum value for this field is {0}",
    nanText : "{0} is not a valid number",
    baseChars : "0123456789",
    
    // private
    initEvents : function(){
        Ext.form.NumberField.superclass.initEvents.call(this);
        var allowed = this.baseChars+'';
        if(this.allowDecimals){
            allowed += this.decimalSeparator;
        }
        
        allowed += this.groupSeparator;
        
        if(this.allowNegative){
            allowed += "-";
        }
        this.stripCharsRe = new RegExp('[^'+allowed+']', 'gi');
        var keyPress = function(e){
            var k = e.getKey();
            if(!Ext.isIE && (e.isSpecialKey() || k == e.BACKSPACE || k == e.DELETE)){
                return;
            }
            var c = e.getCharCode();
            if(allowed.indexOf(String.fromCharCode(c)) === -1){
                e.stopEvent();
            }
        };
        this.el.on("keypress", keyPress, this);
        this.el.on("focus", this.focusHandler, this);
    },
    focusHandler : function() {
    	var v = this.getValue();
    	this.setRawValue( String(v).replace( ".", this.decimalSeparator ) );
    },
    // private
    validateValue : function(value){
        value = String(value).replace( new RegExp("\\" + this.groupSeparator,"g"), "");
        if(!Ext.form.NumberField.superclass.validateValue.call(this, value)){
            return false;
        }
        if(value.length < 1){ // if it's blank and textfield didn't flag it then it's valid
             return true;
        }
        
        var v2 = String(value).replace(new RegExp("\\" + this.groupSeparator,"g"), "");
        v2 = parseFloat(String(v2).replace(this.decimalSeparator, "."));
        if(isNaN(v2)){
            this.markInvalid(String.format(this.nanText, value));
            return false;
        }
        var num = this.parseValue(value);
        if(num < this.minValue){
            this.markInvalid(String.format(this.minText, this.minValue));
            return false;
        }
        if(num > this.maxValue){
            this.markInvalid(String.format(this.maxText, this.maxValue));
            return false;
        }
        return true;
    },

    getValue : function(){
    	return this.fixPrecision(this.parseValue(Ext.form.NumberField.superclass.getValue.call(this)));
    },

    setValue : function(v){
    	v = String(v).replace(new RegExp("\\" + this.groupSeparator,"g"), ".");
    	v = String(v).replace(this.decimalSeparator, ".");
    	v = typeof v == 'number' ? v : parseFloat(v);
        v = isNaN(v) ? '' : String(v).replace(".", this.decimalSeparator);
        Ext.form.NumberField.superclass.setValue.call(this, v);
        this.setRawValue( this.formatNumber( v ) );
    },

    // private
    parseValue : function(value) {
        value = String(value).replace(new RegExp("\\" + this.groupSeparator,"g"), "");
        value = parseFloat(String(value).replace(this.decimalSeparator, "."));
        return isNaN(value) ? '' : value;
    },
    
    formatNumber : function(nStr) {
		nStr += '';
		if( nStr.length > 0 ) {
			var x = nStr.split(',');
			var x1 = x[0];
			var x2 = x.length > 1 ? ',' + x[1] : '';
			
			var y = (x2.length == 0)?0:x2.length-1;
			
			while ( y < this.minDecimalPrecision ) {
				if( x2.length == 0 ) 
					x2 = this.decimalSeparator;
				x2 += '0';
				y = x2.length-1;
			}
	
			if( this.group ) {
				var rgx = /(\d+)(\d{3})/;
				while (rgx.test(x1)) {
					x1 = x1.replace(rgx, '$1' + '.' + '$2');
				}
	    	}
			return x1 + x2;
		}
		return nStr;
    },

    // private
    fixPrecision : function(value){
        var nan = isNaN(value);
        if(!this.allowDecimals || this.decimalPrecision == -1 || nan || !value){
           return nan ? '' : value;
        }
        return parseFloat(parseFloat(value).toFixed(this.decimalPrecision));
    },

    beforeBlur : function(){
        var v = this.parseValue(this.getRawValue());
        if(v || v === 0){
            this.setValue(this.fixPrecision(v));
        }
    }
});


ExtXeo.MessageBox = function(){
    var dlg, opt, mask, waitTimer;
    var bodyEl, msgEl, textboxEl, textareaEl, progressBar, pp, iconEl, spacerEl;
    var buttons, activeTextEl, bwidth, iconCls = '';
    var dlgs={};

    // private
    var handleButton = function(button){
        if(dlg.isVisible()){
            dlg.hide();
            Ext.callback(opt.fn, opt.scope||window, [button, activeTextEl.dom.value, opt], 1);
        }
    };

    // private
    var handleHide = function(){
        if(opt && opt.cls){
            dlg.el.removeClass(opt.cls);
        }
        progressBar.reset();
    };

    // private
    var handleEsc = function(d, k, e){
        if(opt && opt.closable !== false){
            dlg.hide();
        }
        if(e){
            e.stopEvent();
        }
    };

    // private
    var updateButtons = function(b){
        var width = 0;
        if(!b){
//            buttons["ok"].hide();
//            buttons["cancel"].hide();
//            buttons["yes"].hide();
//            buttons["no"].hide();
            return width;
        }
        dlg.footer.dom.style.display = '';
        for(var k in buttons){
            if(typeof buttons[k] != "function"){
                if(b[k]){
                    buttons[k].show();
                    //buttons[k].setText(typeof b[k] == "string" ? b[k] : this.buttonText[k]);
                    width += buttons[k].el.getWidth()+15;
                }else{
                    buttons[k].hide();
                }
            }
        }
        return width;
    };

    return {
        
        getDialog : function( id, titleText, btns, defaultBtn, wndProps ){
           if(!dlgs[ id ]){
                dlg = new Ext.Window({
                    autoCreate : true,
                    title:titleText,
                    resizable:false,
                    id: id+"_msgBox",
                    constrain:true,
                    constrainHeader:true,
                    minimizable : false,
                    maximizable : false,
                    stateful: false,
                    modal: true,
                    shim:true,
                    buttonAlign:"center",
                    width:400,
                    height:100,
                    minHeight: 80,
                    plain:true,
                    footer:true,
                    closable:true,
                    close : function(){
                        if(opt && opt.buttons && opt.buttons.no && !opt.buttons.cancel){
                            handleButton("no");
                        }else{
                            handleButton("cancel");
                        }
                    }
                });
                
                dlgs[ id ] = dlg;                
                
                buttons = {};
                var bt = this.buttonText;
                //TODO: refactor this block into a buttons config to pass into the Window constructor
                for(var b in btns ) {
                	buttons[b] = dlg.addButton(bt[b], handleButton.createCallback(b));
                	buttons[b].hideMode = 'offsets';
                }
                /*
                buttons["ok"] = dlg.addButton(bt["ok"], handleButton.createCallback("ok"));
                buttons["yes"] = dlg.addButton(bt["yes"], handleButton.createCallback("yes"));
                buttons["no"] = dlg.addButton(bt["no"], handleButton.createCallback("no"));
                buttons["cancel"] = dlg.addButton(bt["cancel"], handleButton.createCallback("cancel"));
                buttons["ok"].hideMode = buttons["yes"].hideMode = buttons["no"].hideMode = buttons["cancel"].hideMode = 'offsets';
                */
                
                dlg.render(document.body);
                dlg.getEl().addClass('x-window-dlg');
                mask = dlg.mask;
                bodyEl = dlg.body.createChild({
                    html:'<div class="ext-mb-icon"></div><div class="ext-mb-content"><span class="ext-mb-text"></span><br /><div class="ext-mb-fix-cursor"><input type="text" class="ext-mb-input" /><textarea class="ext-mb-textarea"></textarea></div></div>'
                });
                iconEl = Ext.get(bodyEl.dom.firstChild);
                var contentEl = bodyEl.dom.childNodes[1];
                msgEl = Ext.get(contentEl.firstChild);
                textboxEl = Ext.get(contentEl.childNodes[2].firstChild);
                textboxEl.enableDisplayMode();
//                textboxEl.addKeyListener([10,13], function(){
//                    if(dlg.isVisible() && opt && opt.buttons){
//                        if(opt.buttons[defaultBtn]){
//                            handleButton(defaultBtn);
//                        }
//                    }
//                });
                textareaEl = Ext.get(contentEl.childNodes[2].childNodes[1]);
                textareaEl.enableDisplayMode();
                progressBar = new Ext.ProgressBar({
                    renderTo:bodyEl
                });
               bodyEl.createChild({cls:'x-clear'});
            }
           	else {
           		dlg = dlgs[id];
           	}
            return dlg;
        },

        
        move: function(x,y){
        	dlg.setPagePosition(x,y);
        },

        
        
        updateText : function(text){
            if(!dlg.isVisible() && !opt.width){
                dlg.setSize(this.maxWidth, 100); // resize first so content is never clipped from previous shows
            }
            msgEl.update(text || '&#160;');

            var iw = iconCls != '' ? (iconEl.getWidth() + iconEl.getMargins('lr')) : 0;
            var mw = msgEl.getWidth() + msgEl.getMargins('lr');
            var fw = dlg.getFrameWidth('lr');
            var bw = dlg.body.getFrameWidth('lr');
            if (Ext.isIE && iw > 0){
                //3 pixels get subtracted in the icon CSS for an IE margin issue,
                //so we have to add it back here for the overall width to be consistent
                iw += 3;
            }
            var w = Math.max(Math.min(opt.width || iw+mw+fw+bw, this.maxWidth),
                        Math.max(opt.minWidth || this.minWidth, bwidth || 0));

            if(opt.prompt === true){
                activeTextEl.setWidth(w-iw-fw-bw);
            }
            if(opt.progress === true || opt.wait === true){
                progressBar.setSize(w-iw-fw-bw);
            }
            if(Ext.isIE && w == bwidth){
                w += 4; //Add offset when the content width is smaller than the buttons.    
            }
            dlg.setSize(w, 'auto').center();
            return this;
        },

        
        updateProgress : function(value, progressText, msg){
            progressBar.updateProgress(value, progressText);
            if(msg){
                this.updateText(msg);
            }
            return this;
        },

        
        isVisible : function(){
            return dlg && dlg.isVisible();
        },

        
        hide : function(){
            var proxy = dlg.activeGhost;
            if(this.isVisible() || proxy) {
                dlg.hide();
                handleHide();
                if (proxy) {
                    proxy.hide();
                } 
            }
            return this;
        },

        
        show : function(options){
            if(this.isVisible()){
                this.hide();
            }
            opt = options;
            this.buttonText = opt.buttonText;
            
            var wndProps = { top:0, left:0 };
            if( options.top )
            	wndProps.top = options.top;
            if( options.left )
            	wndProps.left = options.left;
            
            var d = this.getDialog( opt.id , opt.title || "&#160;", opt.buttons, null, wndProps );
            d.setTitle(opt.title || "&#160;");
            var allowClose = (opt.closable !== false && opt.progress !== true && opt.wait !== true);
            d.tools.close.setDisplayed(allowClose);
            activeTextEl = textboxEl;
            opt.prompt = opt.prompt || (opt.multiline ? true : false);
            if(opt.prompt){
                if(opt.multiline){
                    textboxEl.hide();
                    textareaEl.show();
                    textareaEl.setHeight(typeof opt.multiline == "number" ?
                        opt.multiline : this.defaultTextHeight);
                    activeTextEl = textareaEl;
                }else{
                    textboxEl.show();
                    textareaEl.hide();
                }
            }else{
                textboxEl.hide();
                textareaEl.hide();
            }
            activeTextEl.dom.value = opt.value || "";
            
            if(opt.prompt){
                d.focusEl = activeTextEl;
            }else{
                var bs = opt.buttons;
                if( opt.defaultButton ) {
                    var db = null;
                    db = buttons[ opt.defaultButton ];
                    if (db){
                        d.focusEl = db;
                    }
                }
            }
            
            //Show Buttons if hidden
            //Fixes the issues of displaying the same 
            //messagebox inside a window multiple times
            //after closing the window and opening it again
            for (btnCurr in d.buttons){
            	if (btnCurr.hidden){
            		btnCurr.show();
            	}
            }
            
            if(opt.iconCls){
              d.setIconClass(opt.iconCls);
            }
            this.setIcon(opt.icon);
            bwidth = updateButtons(opt.buttons);
            progressBar.setVisible(opt.progress === true || opt.wait === true);
            this.updateProgress(0, opt.progressText);
            this.updateText(opt.msg);
            if(opt.cls){
                d.el.addClass(opt.cls);
            }
            d.proxyDrag = opt.proxyDrag === true;
            d.modal = opt.modal !== false;
            d.mask = opt.modal !== false ? mask : false;
            if(!d.isVisible()){
                // force it to the end of the z-index stack so it gets a cursor in FF
                document.body.appendChild(dlg.el.dom);
                d.setAnimateTarget(opt.animEl);
                d.show(opt.animEl);
            }
            
            var dp = d.getPosition();
        	d.setPagePosition( 
        		wndProps.left!=0?wndProps.left:dp[0],
        		wndProps.top!=0?wndProps.top:dp[1]
        	);

            //workaround for window internally enabling keymap in afterShow
            d.on('show', function(){
                if(allowClose === true){
                    d.keyMap.enable();
                }else{
                    d.keyMap.disable();
                }
            }, this, {single:true});

            if(opt.wait === true){
                progressBar.wait(opt.waitConfig);
            }
            return this;
        },

        
        setIcon : function(icon){
            if(icon && icon != ''){
                iconEl.removeClass('x-hidden');
                iconEl.replaceClass(iconCls, icon);
                iconCls = icon;
            }else{
                iconEl.replaceClass(iconCls, 'x-hidden');
                iconCls = '';
            }
            return this;
        },

        
        progress : function(title, msg, progressText){
            this.show({
                title : title,
                msg : msg,
                buttons: false,
                progress:true,
                closable:false,
                minWidth: this.minProgressWidth,
                progressText: progressText
            });
            return this;
        },

        
        wait : function(msg, title, config){
            this.show({
                title : title,
                msg : msg,
                buttons: false,
                closable:false,
                wait:true,
                modal:true,
                minWidth: this.minProgressWidth,
                waitConfig: config
            });
            return this;
        },

        
        alert : function(title, msg, fn, scope){
            this.show({
                title : title,
                msg : msg,
                buttons: this.OK,
                fn: fn,
                scope : scope
            });
            return this;
        },

        
        confirm : function(title, msg, fn, scope){
            this.show({
                title : title,
                msg : msg,
                buttons: this.YESNO,
                fn: fn,
                scope : scope,
                icon: this.QUESTION
            });
            return this;
        },

        
        prompt : function(title, msg, fn, scope, multiline, value){
            this.show({
                title : title,
                msg : msg,
                buttons: this.OKCANCEL,
                fn: fn,
                minWidth:250,
                scope : scope,
                prompt:true,
                multiline: multiline,
                value: value
            });
            return this;
        },

        
        OK : {ok:true},
        
        CANCEL : {cancel:true},
        
        OKCANCEL : {ok:true, cancel:true},
        
        YESNO : {yes:true, no:true},
        
        YESNOCANCEL : {yes:true, no:true, cancel:true},
        
        INFO : 'ext-mb-info',
        
        WARNING : 'ext-mb-warning',
        
        QUESTION : 'ext-mb-question',
        
        ERROR : 'ext-mb-error',

        
        defaultTextHeight : 75,
        
        maxWidth : 600,
        
        minWidth : 100,
        
        minProgressWidth : 250,
        
        buttonText : {
            ok : "OK",
            cancel : "Cancel",
            yes : "Yes",
            no : "No"
        }
    };
}();

