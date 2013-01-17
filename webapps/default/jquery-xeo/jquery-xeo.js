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
	$.blockUI.defaults.css = {}; //Should be placed somewhere else
	$.blockUI({ message: '<div id="xwc-ajax-loading"><span class="xwc-ajax-loading-image" /><p class="xwc-ajax-loading-message"> ' + ExtXeo.Messages.SENDING_DATA + '</p></div>' });
}


XVW.NoWait = function() { 
	$.unblockUI();
}

/**
 * 
 * Functions to deal with Jquery Grid row select 
 *  
 * */

XVW.grid = function(){}

XVW.grid.utils = function(){}

XVW.grid.utils.getInputValue = function ( inputId ){
	var elem = $(XVW.get(inputId));
	var selected = elem.val();
	if (selected == "")
		selected = [];
	else
		selected = selected.split(",");
	return selected;
}

XVW.grid.utils.setInputValue = function ( inputId, value ){
	var elem = $(XVW.get(inputId));
	elem.val(value);	
}

XVW.grid.utils.hasElement = function( inputId, id ){
	return XVW.grid.utils.elementIndex( inputId, id ) > -1;
}

XVW.grid.utils.elementIndex = function( inputId, id ){
	var values = XVW.grid.utils.getInputValue( inputId );
	for (var i = 0 ; i < values.length; i++){
		if (values[i] == id)
			return i;
	}	
	return -1;
}

XVW.grid.utils.removeElement = function(inputId, id){
	var values = XVW.grid.utils.getInputValue(inputId);
	var exists = XVW.grid.utils.hasElement(inputId,id);
	var index = XVW.grid.utils.elementIndex( inputId , id ); 
	if (exists && index > -1){
		values.splice(index,1);
		XVW.grid.utils.setInputValue( inputId , values );
	}
}	

XVW.grid.utils.addElement = function(inputId, id){
	var values = XVW.grid.utils.getInputValue(inputId);
	var exists = XVW.grid.utils.hasElement(inputId,id); 
	if (!exists){
		values.push(id);
		XVW.grid.utils.setInputValue( inputId , values );
	}
}

XVW.grid.onSelectRow = function( rowId , status, e , grid){
	
	var inputId = grid.id + "_srs";
		
	if (status == '1'){
		XVW.grid.utils.addElement( inputId, rowId );
	} else {
		XVW.grid.utils.removeElement( inputId, rowId );
	}
	
	//Funciona para a active Row
	if (status == '1')
		$(XVW.get(grid.id + "_act")).val(rowId);
	
}

XVW.grid.resize = function (elem){
	var newElem = jQuery(elem.id + "_table"); 
	if (newElem){
		if (newElem.jqGrid){
			var width = newElem[0].offsetWidth;
			console.log(width);
			newElem.jqGrid().setGridWidth(width);
		}
	}
	
}

ExtXeo.layoutMan.registerManager('fit-parent',XVW.grid.resize);


