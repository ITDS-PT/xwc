/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.grid.filter.ObjectFilter = Ext.extend(Ext.grid.filter.Filter, {
    /**
	 * The delay between request for new data on cardId filtering
	 * */
	updateBuffer: 400
    /**
     * @cfg {Boolean} defaultValue
     * The default value of this filter (defaults to false)
     */
    , defaultValue: false,
    /**
     * @cfg {String} yesText
     * The text displayed for the "Yes" checkbox
     */
    yesText: 'Seleccionar valores',
    /**
     * @cfg {String} noText
     * The text displayed for the "No" checkbox
     */
    noText: 'Não',

    icon: 'extjs/grid/img/find.png'
    
	, init: function(){
	    var gId = Ext.id();
			this.options = [
				new Ext.menu.Item({text: this.yesText, group: gId })
	    ];
		this.menu.add(this.options[0]);
		for(var i=0; i<this.options.length; i++) {
			this.options[i].on('click', this.fireClick, this);
			this.options[i].on('checkchange', this.fireUpdate, this);
		}
		
		this.cardIdSearch = new Ext.menu.EditableItem({icon: this.icon});
		this.cardIdSearch.on('keyup', this.onKeyUp, this);
		
		this.updateTask = new Ext.util.DelayedTask(this.fireUpdate, this);
		
		this.menu.add(this.cardIdSearch);
	}
	
	, onKeyUp : function (event ){
		if(event.getKey() == event.ENTER){
			this.menu.hide(true);
			return;
		}
		this.clearData();
		this.setActive(true);
		this.updateTask.delay(this.updateBuffer);
		
	}
	
	, isActivatable: function() {
		return true || this.containsData != null;
	}
	
	, clearValue : function(){
		this.cardIdSearch.setValue("");
	}
	
	,fireClick: function() {
		this.clearData();
		this.clearValue();
		this.parentFilters.uploadConfig();
		this.lookupCommand();
	},
	fireUpdate: function() {		
		this.fireEvent("update", this);			
		this.setActive(true);
	},
	
	setValue: function(value) {
		this.value = value;
		//this.options[value ? 0 : 1].setChecked(true);
	},
	
	getValue: function() {
		return this.value;
		//return this.options[0].checked;
	},
	
	serialize: function() {
		var cardId = false;
		if (this.cardIdSearch.getValue() != null){
			if (this.cardIdSearch.getValue().length > 0){
				cardId = true;
			}
		}
		
		var theValue = this.getValue();
		if (cardId){
			theValue = this.cardIdSearch.getValue();
		}
		
		var args = {active:this.active, type: 'object', value: theValue, containsData : this.containsData, cardIdSearch :  cardId};
		this.fireEvent('serialize', args, this);
		return args;
	},
	
	validateRecord: function(record) {
		return record.get(this.dataIndex) == this.getValue();
	}
});
