/*
 * Ext JS Library 2.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

Ext.ns("Ext.grid.filter");
Ext.grid.filter.Filter = function(config){
	Ext.apply(this, config);
		
	this.events = {
		/**
		 * @event activate
		 * Fires when a inactive filter becomes active
		 * @param {Ext.ux.grid.filter.Filter} this
		 */
		'activate': true,
		/**
		 * @event deactivate
		 * Fires when a active filter becomes inactive
		 * @param {Ext.ux.grid.filter.Filter} this
		 */
		'deactivate': true,
		/**
		 * @event update
		 * Fires when a filter configuration has changed
		 * @param {Ext.ux.grid.filter.Filter} this
		 */
		'update': true,
		/**
		 * @event serialize
		 * Fires after the serialization process. Use this to apply additional parameters to the serialized data.
		 * @param {Array/Object} data A map or collection of maps representing the current filter configuration.
		 * @param {Ext.ux.grid.filter.Filter} filter The filter being serialized.
		 **/
		'serialize': true
	};
	Ext.grid.filter.Filter.superclass.constructor.call(this);
	
	this.menu = new Ext.menu.Menu();
	this.init();
	this.createDataPresenceFilters(config);
	this.afterPresenceFilters();
	if(config && config.value) {
		this.setValue(config.value);
		this.setActive(config.active !== false, true);
		//this.setSearchable( config.searchable !== false, true )
		delete config.value;
	}
};
Ext.extend(Ext.grid.filter.Filter, Ext.util.Observable, {
	/**
	 * @cfg {Boolean} active
	 * Indicates the default status of the filter (defaults to false).
	 */

    /**
     * True the column can be searchable. Read-only.
     * @type Boolean
     * @property
     */
	searchable: false,
    /**
     * True if this filter is active. Read-only.
     * @type Boolean
     * @property
     */
	active: false,
	/**
	 * @cfg {String} dataIndex 
	 * The {@link Ext.data.Store} data index of the field this filter represents. The dataIndex does not actually
	 * have to exist in the store.
	 */
	dataIndex: null,
	/**
	 * The filter configuration menu that will be installed into the filter submenu of a column menu.
	 * @type Ext.menu.Menu
	 * @property
	 */
	menu: null
	
	/**
	 * The menus for the Contains Data / Not Contains Data Filtering
	 * @type Ext.menu.Menu []
	 * 
	 */
	,dataPresenceMenus : null
	
	/**
	 * Whether or not the the Contains Data / Not Contains Data are in place.
	 * True = Contains Data
	 * False = Not Contains Data
	 * Null = Filters not active
	 * @type boolean
	 * 
	 */
	, containsData : null
	/**
	 * Text to show in the Contains Data filter
	 * */
	, containsDataText : 'Cont&eacute;m dados'
		/**
		 * Text to show in the Not Contains Data filter
		 * */	
	, notContainsDataText : 'N&atilde;o cont&eacute;m dados'	
	
	/**
	 * Initialize the filter and install required menu items.
	 */
	, init: Ext.emptyFn,
	
	createDataPresenceFilters : function (config) {
		
		this.dataPresenceMenus = [
			new Ext.menu.CheckItem({text: this.containsDataText, checked: false }),
			new Ext.menu.CheckItem({text: this.notContainsDataText, checked: false})
		];
		    	
		this.menu.add(this.dataPresenceMenus[0]);
		this.menu.add(this.dataPresenceMenus[1]);
		
		if (config){
			if (config.containsData != undefined){
				if (config.containsData == true){
					this.dataPresenceMenus[0].setChecked(true);
					this.dataPresenceMenus[1].setChecked(false);
				} else if (config.containsData == false){
					this.dataPresenceMenus[1].setChecked(false);
					this.dataPresenceMenus[1].setChecked(true);
				}
			}
		}
		
		var checkForData = this.dataPresenceMenus[0];
		var checkForNull = this.dataPresenceMenus[1];
		
		checkForData.on('checkchange', function (item, checked){
			if (checked){
			  checkForNull.setChecked(false);
			  this.clearValue();
			  this.containsData = true; 	
			} else{
			  this.containsData = null;
			}
			this.setActive(this.isActivatable());
			this.fireUpdate();
		}, this);
		
		
		checkForNull.on('checkchange', function (item, checked){
			
			if (checked){
			  checkForData.setChecked(false);
			  this.clearValue();
			  this.containsData = false; 	
			} else{
			  this.containsData = null;
			}
			this.setActive(this.isActivatable());
			this.fireUpdate();
		}, this);
		
		for(var i=0; i<this.dataPresenceMenus.length; i++) {
			this.dataPresenceMenus[i].on('click', function(item, evt){ 
				this.fireUpdate(); 
			}, this);
		}
		
		
	} 

	, afterPresenceFilters : function (){
		
	}

	, clearValue : Ext.emptyFn

	, clearData : function (){
		if (this.dataPresenceMenus != null){
			for (var k = 0 ; k < this.dataPresenceMenus.length ; k++){
				//Set false and supress event
				this.dataPresenceMenus[k].setChecked(false,true);
			}
		}
		this.containsData = null;
	}
	
	, fireUpdate: function() {
		if (this.item)
		this.value = this.item.getValue();
		
		if(this.active || this.containsData !== null) {
			this.fireEvent("update", this);
    }
		
		if (this.item && this.value)
		this.setActive(this.value.length > 0);
	},
	
	/**
	 * Returns true if the filter has enough configuration information to be activated.
	 * @return {Boolean}
	 */
	isActivatable: function() {
		return true;
	},
	
	/**
	 * Sets the searchable flag of the filter.
	 * @param {Boolean} active        The new filter state.
	 * @param {Boolean} suppressEvent True to prevent events from being fired.
	 */
	setActive: function(active, searchable) {
		if(this.searchable != searchable) {
			this.searchable = searchable;
		}
	},
	/**
	 * Sets the status of the filter and fires that appropriate events.
	 * @param {Boolean} active        The new filter state.
	 * @param {Boolean} suppressEvent True to prevent events from being fired.
	 */
	setActive: function(active, suppressEvent) {
		if(this.active != active) {
			this.active = active;
			if(suppressEvent !== true) {
				this.fireEvent(active ? 'activate' : 'deactivate', this);
      }
		}
	},
	
	/**
	 * Get the value of the filter
	 * @return {Object} The 'serialized' form of this filter
	 */
	getValue: Ext.emptyFn,
	
	/**
	 * Set the value of the filter.
	 * @param {Object} data The value of the filter
	 */	
	setValue: Ext.emptyFn,
	
	/**
	 * deSerialize the filter data from transmission to the server.
	 * @return {Object/Array} An object or collection of objects containing key value pairs representing
	 * 	the current configuration of the filter.
	 */
	deSerialize: function( data ) {
		this.setActive( data.active );
		if( data.active )
			this.setValue( data.value );
	},

	/**
	 * Serialize the filter data for transmission to the server.
	 * @return {Object/Array} An object or collection of objects containing key value pairs representing
	 * 	the current configuration of the filter.
	 */
	serialize: Ext.emptyFn,
	
	/**
	 * Validates the provided Ext.data.Record against the filters configuration.
	 * @param {Ext.data.Record} record The record to validate
	 * @return {Boolean} True if the record is valid with in the bounds of the filter, false otherwise.
	 */
	 validateRecord: function(){return true;}
});