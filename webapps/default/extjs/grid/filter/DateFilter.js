/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.grid.filter.DateFilter = Ext.extend(Ext.grid.filter.Filter, {
    /**
     * @cfg {Date} dateFormat
     * The date format applied to the menu's {@link Ext.menu.DateMenu}
     */
	dateFormat: 'd/m/Y',
    /**
     * @cfg {Object} pickerOpts
     * The config object that will be passed to the menu's {@link Ext.menu.DateMenu} during
     * initialization (sets minDate, maxDate and format to the same configs specified on the filter)
     */
	pickerOpts: {},
    /**
     * @cfg {String} beforeText
     * The text displayed for the "Before" menu item
     */
    beforeText: 'Antes de',
    /**
     * @cfg {String} afterText
     * The text displayed for the "After" menu item
     */
    afterText: 'Depois de',
    /**
     * @cfg {String} onText
     * The text displayed for the "On" menu item
     */
    onText: 'Em',
    /**
     * @cfg {Date} minDate
     * The minimum date allowed in the menu's {@link Ext.menu.DateMenu}
     */
    /**
     * @cfg {Date} maxDate
     * The maximum date allowed in the menu's {@link Ext.menu.DateMenu}
     */
    lookupCommand : Ext.emptyFn
	
	, init: function() {
		var opts = Ext.apply(this.pickerOpts, {
			minDate: this.minDate, 
			maxDate: this.maxDate, 
			format:  this.dateFormat
		});
		var dates = this.dates = {
			'before': new Ext.menu.CheckItem({text: this.beforeText, menu: new Ext.menu.DateMenu(opts)}),
			'after':  new Ext.menu.CheckItem({text: this.afterText, menu: new Ext.menu.DateMenu(opts)}),
			'on':     new Ext.menu.CheckItem({text: this.onText, menu: new Ext.menu.DateMenu(opts)})
		};
				
		var between = new Ext.menu.Item({
			  text: 'Entre datas'
					, handler : this.lookupCommand
		});
		
		this.menu.add(dates.before, dates.after, between, "-", dates.on);
		
		for(var key in dates) {
			var date = dates[key];
			date.menu.on('select', this.onSelect.createDelegate(this, [date]), this);
  
      date.on('checkchange', function(){
        this.setActive(this.isActivatable());
			}, this);
		};
	},
  
	onSelect: function(date, menuItem, value, picker) {
	    date.setChecked(true);
	    var dates = this.dates;
	    
	    if(date == dates.on) {
	      dates.before.setChecked(false, true);
	      dates.after.setChecked(false, true);
	    } else {
	      dates.on.setChecked(false, true);
	      
	      if(date == dates.after && dates.before.menu.picker.value < value) {
	        dates.before.setChecked(false, true);
	      } else if (date == dates.before && dates.after.menu.picker.value > value) {
	        dates.after.setChecked(false, true);
	      }
	    }
	    this.clearData();
	    this.fireEvent("update", this);
  },
  
	getFieldValue: function(field) {
		return this.dates[field].menu.picker.getValue();
	},
	
	getPicker: function(field) {
		return this.dates[field].menu.picker;
	},
	
	isActivatable: function() {
		return this.dates.on.checked || this.dates.after.checked || this.dates.before.checked || this.containsData != null;
	},
	
	setValue: function(value) {
		
		if (value['lt'] ) value['before'] = value['lt'];
		if (value['gt'] ) value['after'] = value['gt'];
		if (value['eq'] ) value['on'] = value['eq'];
		
		for(var key in this.dates) {
			if(value[key]) {
				this.dates[key].menu.picker.setValue(value[key]);
				this.dates[key].setChecked(true);
			} else {
				this.dates[key].setChecked(false);
			}
		}
		this.clearData();
	}
	
	, clearValue : function () {
		for(var key in this.dates){
			this.dates[key].setChecked(false);
		}
	}
	
	
	, getValue: function() {
		var result = {};
		for(var key in this.dates) {
			if(this.dates[key].checked) {
				result[key] = this.dates[key].menu.picker.getValue();
      }
    }	
		return result;
	},
	deSerialize: function( data ) {
		this.setActive( data.active );
		if( data.value )
			for( var i=0; i < data.value.length; i ++ ) {
				var d = data.value[i];
				var dt = Date.parseDate( d.value, 'd/m/Y');
				if( "eq" == d.comparison )
					this.setValue( { on : dt } );
				if( "lt" == d.comparison )
					this.setValue( { before : dt } );
				if( "gt" == d.comparison )
					this.setValue( { after : dt } );
			}
	},
	serialize: function() {
		var args = [];
		if(this.dates.before.checked || this.dates.after.checked || this.containsData != null) {
			var values =  [];
			
			if( this.dates.before.checked )
				values[ values.length ] = { comparison: 'lt', value: this.getFieldValue('before').format(this.dateFormat) };
			
			if( this.dates.after.checked )
				values[ values.length ] = { comparison: 'gt', value: this.getFieldValue('after').format(this.dateFormat) };
			
			args = {active:this.active, type: 'date', value: values};
		}
		if(this.dates.on.checked) {
			args = {active:this.active, type: 'date', value: [ {comparison: 'eq', value: this.getFieldValue('on').format(this.dateFormat)}]};
		}
		
		if (args.length == 0){
			args = {active:this.active, type: 'date', containsData : this.containsData};
		} else {
			args.containsData = this.containsData;
		}
		this.fireEvent('serialize', args, this);
		return args;
	},
	
	validateRecord: function(record) {
		var val = record.get(this.dataIndex).clearTime(true).getTime();
		
		if(this.dates.on.checked && val != this.getFieldValue('on').clearTime(true).getTime()) {
			return false;
    }
		if(this.dates.before.checked && val >= this.getFieldValue('before').clearTime(true).getTime()) {
			return false;
    }
		if(this.dates.after.checked && val <= this.getFieldValue('after').clearTime(true).getTime()) {
			return false;
    }
		return true;
	}
	
	
	, setBeforeValue : function(newDate){
		this.dates.before.menu.picker.setValue(new Date(newDate));
	}
	
	, setBeforeCheck : function(checked){
		this.dates.before.setChecked(checked);
	}
	
	, setAfterValue : function(newDate){
		this.dates.after.menu.picker.setValue(new Date(newDate));
	}
	
	, setAfterCheck : function(checked){
		this.dates.after.setChecked(checked);
	}
});