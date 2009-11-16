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
        value = String(value).replace(this.decimalSeparator, ".");
        if(isNaN(value)){
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
    parseValue : function(value){
        value = String(value).replace(new RegExp("\\" + this.groupSeparator,"g"), "");
        value = parseFloat(String(value).replace(this.decimalSeparator, "."));
        return isNaN(value) ? '' : value;
    },
    
    formatNumber : function(nStr) {
		nStr += '';
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



ExtXeo.form.DateTimeField2 = Ext.extend( Ext.form.Field,{
    initComponent : function(){
    	Ext.form.TextField.superclass.initComponent.call(this);
		this.dateField = new Ext.form.DateField( );
		this.timeField = new Ext.form.TimeField( );
	},
    onRender : function(ct, position){
		Ext.form.TextField.superclass.onRender.call(ct, position);
	},
	onResize : function(w, h){
		this.dateField.onResize( this, w/2-10, h );
		this.timeField.onResize( this, w/2-10, h );
	},
    getResizeEl : function(){
        return this.wrap;
    },

    getPositionEl : function(){
        return this.wrap;
    },
    onDestroy : function(){
        this.dateField.onDestroy();
        this.timeField.onDestroy();
        if(this.dateField){
            this.dateField.removeAllListeners();
            this.dateField.remove();
        }
        if(this.timeField){
            this.timeField.removeAllListeners();
            this.timeField.remove();
        }
        if(this.wrap){
            this.wrap.remove();
        }
    }
}); 



ExtXeo.form.__DateTimeField2 = Ext.extend(Ext.form.DateField,  {
	defaultAutoCreate : {tag: "input", type: "text", size: "16", autocomplete: "off"},
    hideTrigger:false,
    
    autoSize: Ext.emptyFn,
        monitorTab : true,
        deferHeight : true,
        mimicing : false,

    onResize : function(w, h){
		ExtXeo.form.DateTimeField2.superclass.onResize.call(this, w/2-10, h);
		/*
        if(typeof w == 'number'){
            //this.el.setWidth(this.adjustWidth('input', w - this.trigger.getWidth()));
        	this.el.setWidth(this.adjustWidth('input', w/2-10 ));
        	this.trigger.setWidth(this.adjustWidth('input', w/2-10 ));
        }
        */
        this.trigger.setWidth(w/2-10);
    },

    adjustSize : Ext.BoxComponent.prototype.adjustSize,

    getResizeEl : function(){
        return this.wrap;
    },

    getPositionEl : function(){
        return this.wrap;
    },

    alignErrorIcon : function(){
        if(this.wrap){
            this.errorIcon.alignTo(this.wrap, 'tl-tr', [2, 0]);
        }
    },

    onRender : function(ct, position){
    	ExtXeo.form.DateTimeField2.superclass.onRender.call(this, ct, position);
        this.wrap = this.el.wrap({cls: "x-form-field-wrap"});
        this.trigger = new Ext.form.TimeField();
        this.trigger.onRender( ct, position );
        /*
        this.wrap = this.el.wrap({cls: "x-form-field-wrap"});
        this.trigger = this.wrap.createChild(this.triggerConfig ||
                {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass});
        if(this.hideTrigger){
            this.trigger.setDisplayed(false);
        }
        this.initTrigger();
        if(!this.width){
            this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
        }
        */
    },

    afterRender : function(){
    	ExtXeo.form.DateTimeField2.superclass.afterRender.call(this);
        var y;
        if(Ext.isIE && this.el.getY() != (y = this.trigger.getY())){
            this.el.position();
            this.el.setY(y);
        }
    },

    initTrigger : function(){
        this.trigger.on("click", this.onTriggerClick, this, {preventDefault:true});
        this.trigger.addClassOnOver('x-form-trigger-over');
        this.trigger.addClassOnClick('x-form-trigger-click');
    },

    onDestroy : function(){
        if(this.trigger){
            this.trigger.removeAllListeners();
            this.trigger.remove();
        }
        if(this.wrap){
            this.wrap.remove();
        }
        ExtXeo.form.DateTimeField2.superclass.onDestroy.call(this);
    },

    onFocus : function(){
    	ExtXeo.form.DateTimeField2.superclass.onFocus.call(this);
        if(!this.mimicing){
            this.wrap.addClass('x-trigger-wrap-focus');
            this.mimicing = true;
            Ext.get(Ext.isIE ? document.body : document).on("mousedown", this.mimicBlur, this, {delay: 10});
            if(this.monitorTab){
                this.el.on("keydown", this.checkTab, this);
            }
        }
    },
    checkTab : function(e){
        if(e.getKey() == e.TAB){
            this.triggerBlur();
        }
    },
    onBlur : function(){
            },
        mimicBlur : function(e){
        if(!this.wrap.contains(e.target) && this.validateBlur(e)){
            this.triggerBlur();
        }
    },

    triggerBlur : function(){
        this.mimicing = false;
        Ext.get(Ext.isIE ? document.body : document).un("mousedown", this.mimicBlur, this);
        if(this.monitorTab){
            this.el.un("keydown", this.checkTab, this);
        }
        this.beforeBlur();
        this.wrap.removeClass('x-trigger-wrap-focus');
        ExtXeo.form.DateTimeField2.superclass.onBlur.call(this);
    },

    beforeBlur : Ext.emptyFn, 

            validateBlur : function(e){
        return true;
    },

    onDisable : function(){
    	ExtXeo.form.DateTimeField2.superclass.onDisable.call(this);
        if(this.wrap){
            this.wrap.addClass(this.disabledClass);
            this.el.removeClass(this.disabledClass);
        }
    },

    onEnable : function(){
    	ExtXeo.form.DateTimeField2.superclass.onEnable.call(this);
        if(this.wrap){
            this.wrap.removeClass(this.disabledClass);
        }
    },

    onShow : function(){
        if(this.wrap){
            this.wrap.dom.style.display = '';
            this.wrap.dom.style.visibility = 'visible';
        }
    },

    onHide : function(){
        this.wrap.dom.style.display = 'none';
    },
    onTriggerClick : Ext.emptyFn
    
});


ExtXeo.form.DateTimeField = Ext.extend( Ext.BoxComponent, {
	timeComp: null,
	dateComp: null,
	initComponent: function() {
		ExtXeo.form.DateTimeField.superclass.initComponent.call( this );
	},
	onDestroy: function() {
		this.dateComp.destroy();
		this.timeComp.destroy();
		ExtXeo.form.DateTimeField.superclass.onDestroy.call( this );
	},	
	onRender: function( ct, position ) {
		
		var rEl = Ext.get( this.initialConfig.renderTo );
		var htmId = this.getId();
		this.el = rEl;
		
		
		//ExtXeo.form.DateTimeField.superclass.onRender.call(this, ct, position);

		this.oDivDate = Ext.DomHelper.append( rEl, {tag:'span', id: htmId+"_d" } );
		this.oDivDate.style.display='inline';
		this.oDivDate.className='x-form-field-wrap';
		
		this.oDivTime = Ext.DomHelper.append( rEl, {tag:'span', id: htmId+"_t" } );
		this.oDivTime.style.display='inline';
		this.oDivTime.className='x-form-field-wrap';

		//Ext.DomHelper.append( rEl, {tag:'span', id: this.getId() + "_t" } );
		
		var id = this.getId();
		
		var dateConfig = [];
		dateConfig.renderTo = htmId+"_d";
		dateConfig.width = '120px';
		dateConfig.name  = this.getName() + "_d";
		dateConfig.listeners = this.initialConfig.listeners;
		dateConfig.format = 'd/m/Y'
		dateConfig.value = this.initialConfig.dateValue;
		this.timeComp 		= new Ext.form.DateField( dateConfig );
		this.timeComp.wrap.dom.style.display='inline';
		
		var timeConfig = [];
		timeConfig.renderTo = htmId+"_t";
		timeConfig.format = 'H:i'
		timeConfig.width = '120px';
		timeConfig.name  = this.getName() + "_t";
		timeConfig.listeners = this.initialConfig.listeners;
		timeConfig.value = this.initialConfig.timeValue;
		this.dateComp 		= new Ext.form.TimeField( timeConfig );
		
		this.dateComp.wrap.dom.style.display='inline';
		this.initialConfig.name = id; 
		this.initialConfig.id = id; 
		
		/*
		this.timeCompEl = Ext.DomHelper.insertAfter( this.el, {tag:'div', id: this.getId()+"_t" } );
		this.initialConfig.renderEl = this.getId()+"_t";
		this.timeComp = new Ext.form.TimeField( this );
		*/
	},
	getResizeEl: function() { return this },
	getName: function() { return this.initialConfig.name },
	setHeight: function( h ) {
		ExtXeo.form.DateTimeField.superclass.setHeight.call(this,h); 
	},
	setWidth: function( w ) {
		this.dateComp.setWidth( (w / 2) );
		this.timeComp.setWidth( (w / 2) );
	}
}
);
