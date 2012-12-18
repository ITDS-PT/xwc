/**
 * Closes a Window
 */

XVW.closeWindowJquery = function (sComponentId, sViewId){
	
	var finalId = "#" + sComponentId + "";
	var finalViewId = "#" + sViewId + "";
	
	//Destroy any ExtJS Based Components inside the Window
	var domElement = jQuery( finalId ).get(0);
	ExtXeo.destroyComponents( domElement , true );
	
	//Destroy the Window and Remove the elements from the DOM
	jQuery( finalId ).dialog('destroy').parent().remove();
	
	//Destroy the Div with the ViewID that gets separated from the rest
	//when creating the window
	jQuery( finalViewId ).remove();
	
}


XVW.Wait = function( iWaitMode ) {
	//$.blockUI.defaults.css = {}; //Should be placed somewhere else
	//$.blockUI({ message: '<div id="xwc-ajax-loading"><span class="xwc-ajax-loading-image" /><p class="xwc-ajax-loading-message"> ' + ExtXeo.Messages.SENDING_DATA + '</p></div>' });
}


XVW.NoWait = function() { 
	//$.unblockUI();
}
