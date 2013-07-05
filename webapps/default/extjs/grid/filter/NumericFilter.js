/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.grid.filter.NumericFilter = Ext.extend(Ext.grid.filter.Filter, {
	init: function() {
		this.menu = new Ext.menu.RangeMenu();
		
		this.menu.on("update", function() { this.clearData(); this.fireUpdate(); }, this);
	},
	
	fireUpdate: function() {
		this.setActive(this.isActivatable());
		this.fireEvent("update", this);
	},
	
	isActivatable: function() {
		var value = this.menu.getValue();
		return value.eq !== undefined || value.gt !== undefined || value.lt !== undefined || this.containsData !== null;
	},
	
	setValue: function(value) {
		this.menu.setValue(value);
	},
	
	getValue: function() {
		return this.menu.getValue();
	}
	
	, clearValue : function () {
		var fields = this.menu.fields;
		if (fields){
			for (var k in fields){
				fields[k].setValue("");
			}
		}
	}
	
	, serialize: function() {
		var args = {};
		var argvals = [];
		
		var values = this.menu.getValue();
		
		for(var key in values) {
			argvals.push({comparison: key, value: values[key]});
		}
		
		args = {active:this.active, type: 'numeric', value: argvals, containsData : this.containsData };
		
		this.fireEvent('serialize', args, this);
		return args;
	},
	
	validateRecord: function(record) {
		var val = record.get(this.dataIndex),
			values = this.menu.getValue();
			
		if(values.eq != undefined && val != values.eq) {
			return false;
		}
		if(values.lt != undefined && val >= values.lt) {
			return false;
		}
		if(values.gt != undefined && val <= values.gt) {
			return false;
		}
		return true;
	}
});