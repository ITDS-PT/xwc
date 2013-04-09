
//Component for the bridge Lookup
Ext.form.BridgeLookup = Ext.extend(Ext.form.Label,  {
	//Actually there was no need to extend the form.Label, in any case it was done
	//may be useful in the future
});

/**
 * 
 * Opens a tab with the Edit Viewer for the selected boui
 * 
 * */
XVW.openBridgeLookup = function(form,id,boui,window){
	var idClient = form+":"+id+"_toEdit";
	document.getElementById(''+idClient+'').value = boui;
	if (window)
		XVW.AjaxCommand(form,form+':'+id+"_op",form+':'+id+"_op",'1');
	else
		XVW.openCommandTab( 'frame_'+id+'_ed',''+form+'',id+'_ed','',null,true);

};

/**
 * 
 * Removes an element from the bridge displayed as a "lookup"
 * 
 * */
XVW.removeBridgeLookup = function(form,id,boui){
	var idClient = form+":"+id+"_toRemove";
	var cmdId = form+":"+id+"_rmBridge";
	document.getElementById(''+idClient+'').value = boui;
	XVW.AjaxCommand( form,cmdId,cmdId,1);
};
