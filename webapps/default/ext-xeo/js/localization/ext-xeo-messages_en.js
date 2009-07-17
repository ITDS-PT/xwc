Ext.ns('ExtXeo');
Ext.ns('ExtXeo.Messages');

ExtXeo.Messages = {
	PROCESSING 		: 'Processing your request...',
	SENDING_DATA 	: 'Sending data, wait a moment...',
	WELCOME			: 'Welcome',
	TREE_TITLE		: 'Options',
	XEODM_ACTIVE	: "XEODM Enabled",
	XEODM_INACTIVE	: "XEODM Disabled",
	LOGOUT_BTN		: 'Logout'
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

if(ExtXeo.grid && ExtXeo.grid.GroupingView) {
	  Ext.apply(ExtXeo.grid.GroupingView , {
	    emptyGroupText : '(None)',
	    groupByText    : 'Group by this field',
	    showGroupsText : 'Show in groups',
	    loadingMsg	   : 'Loading...'  	  
	 });
}

