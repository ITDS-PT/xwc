Ext.ns('ExtXeo');
Ext.ns('ExtXeo.Messages');

ExtXeo.Messages = {
	PROCESSING 		: 'Processing your request...',
	SENDING_DATA 	: 'Sending data, wait a moment...',
	WELCOME			: 'Welcome',
	TREE_TITLE		: 'Options',
	XEODM_ACTIVE	: "XEODM Enabled",
	XEODM_INACTIVE	: "XEODM Disabled",
	LOGOUT_BTN		: 'Logout',
	USER_PROPS			: 'User settings'
};

if(ExtXeo.PagingToolbar){
	  Ext.apply(ExtXeo.PagingToolbar.prototype, {
	    beforePageText : "Page",
	    afterPageText  : "of {0} ({1})",
	    firstText      : "First Page",
	    prevText       : "Previous Page",
	    nextText       : "Next Page",
	    lastText       : "Last Page",
	    refreshText    : "Refresh",
	    displayMsg     : "Showing {0} - {1} of {2}",
	    emptyMsg       : 'No results to show'
	  });
	}

if( ExtXeo.grid.GroupingView) {
	  Ext.apply(ExtXeo.grid.GroupingView.prototype , {
	    emptyGroupText : '(None)',
	    groupByText    : 'Group by this field',
	    showGroupsText : 'Show in groups',
	    loadingMsg	   : 'Loading...'  	  
	 });
}

if( Ext.grid.GridFilters) {
	  Ext.apply(Ext.grid.GridFilters.prototype , {
		 filtersText: 'Filters'	  
	 });
}

if( Ext.grid.filter.ObjectFilter) {
	  Ext.apply(Ext.grid.filter.ObjectFilter.prototype , {
		  yesText: 'Select values'	,
		  noText: 'No'
	 });
}

if( Ext.grid.filter.DateFilter) {
	  Ext.apply(Ext.grid.filter.DateFilter.prototype , {
		  beforeText: 'Before',
		  afterText: 'After',
		  onText: 'In'
	 });
}

if( Ext.grid.filter.BooleanFilter) {
	  Ext.apply(Ext.grid.filter.BooleanFilter.prototype , {
		  yesText: 'Yes'	,
		  noText: 'No'
	 });
}

if( Ext.grid.filter.ListFilter) {
	  Ext.apply(Ext.grid.filter.ListFilter.prototype , {
		  loadingText: 'Reading Data...'
	 });
}

if( ExtXeo.form.NumberField) {
	  Ext.apply(ExtXeo.form.NumberField.prototype , {
		    minText : "The minimum value for this field is {0}",
		    maxText : "The maximum value for this field is {0}",
		    nanText : "{0} is not a valid number"
	 });
}