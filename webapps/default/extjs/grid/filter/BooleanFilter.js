/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.grid.filter.BooleanFilter = Ext.extend(Ext.grid.filter.Filter, {
    /**
     * @cfg {Boolean} defaultValue
     * The default value of this filter (defaults to false)
     */
    defaultValue: false,
    /**
     * @cfg {String} yesText
     * The text displayed for the "Yes" checkbox
     */
    yesText: 'Sim',
    /**
     * @cfg {String} noText
     * The text displayed for the "No" checkbox
     */
    noText: 'N&atilde;o',

	init: function(){
	    var gId = Ext.id();
			this.options = [
				new Ext.menu.CheckItem({text: this.yesText, group: gId, checked: this.defaultValue === true}),
				new Ext.menu.CheckItem({text: this.noText, group: gId, checked: this.defaultValue === false})
	    ];
		
		this.menu.add(this.options[0], this.options[1]);
		for(var i=0; i<this.options.length; i++) {
			this.options[i].on('click', function (e) {
					this.clearData(); 
					this.fireUpdate();  
					if (e.checked) 
						e.setChecked(false); 
					else 
						e.setChecked(true);
			}, this);
			this.options[i].on('checkchange', function (e) {
				this.fireUpdate(); 
			}, this);
		}
	},
	
	isActivatable: function() {
		return true;
	},
	
	fireUpdate: function() {		
		this.fireEvent("update", this);			
		this.setActive(true);
	},
	
	setValue: function(value) {
		this.options[value ? 0 : 1].setChecked(true);
	}
	
	, clearValue : function () {
		this.options[0].setChecked(false);
		this.options[1].setChecked(false);
	}
	
	, getValue: function() {
		return this.options[0].checked;
	},
	
	serialize: function() {
		var args = {active:this.active, type: 'boolean', value: this.getValue(), containsData : this.containsData};
		this.fireEvent('serialize', args, this);
		return args;
	},
	
	validateRecord: function(record) {
		return record.get(this.dataIndex) == this.getValue();
	}
});