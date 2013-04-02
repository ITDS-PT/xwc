/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.grid.filter.StringFilter = Ext.extend(Ext.grid.filter.Filter, {
	updateBuffer: 500
	, icon: 'extjs/grid/img/find.png'
	, containsData : null
	, options : null

	, init: function() {
		var value = this.value = new Ext.menu.EditableItem({icon: this.icon});
		value.on('keyup', this.onKeyUp, this);
		this.menu.add(value);
		
		this.updateTask = new Ext.util.DelayedTask(this.fireUpdate, this);
	},
	
	onKeyUp: function(event) {
		if(event.getKey() == event.ENTER){
			this.menu.hide(true);
			return;
		}
		this.clearData();
		this.updateTask.delay(this.updateBuffer);
	}
	
	, clearValue : function () {
		this.value.setValue(null);
	}
	
	,isActivatable: function() {
		return this.value.getValue().length > 0 || this.containsData !== null;
	},
	
	fireUpdate: function() {		
		if(this.active || this.containsData !== null) {
			this.fireEvent("update", this);
    }
		this.setActive(this.isActivatable());
	},
	
	setValue: function(value) {
		this.value.setValue(value);
		this.fireEvent("update", this);
	},
	
	getValue: function() {
		return this.value.getValue();
	},

	deSerialize: function( data ) {
		this.setActive( data.active );
		this.setValue( data.value );
	},

	serialize: function() {
		var args = {active:this.active, type: 'string', value: this.getValue(), containsData : this.containsData};
		this.fireEvent('serialize', args, this);
		return args;
	},
	
	validateRecord: function(record) {
		var val = record.get(this.dataIndex);
		if(typeof val != "string") {
			return this.getValue().length == 0;
    }
		return val.toLowerCase().indexOf(this.getValue().toLowerCase()) > -1;
	}
});