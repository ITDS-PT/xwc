/**
 * Functions associated to the LovChooser component
 * */


/**
 * 
 * Change an element from the choices tree to the selections tree
 * 
 * */
XVW.changeToTargetTree = function( button ){
	
	var idSource = button.sourceTree;
	var idDestiny = button.destinyTree;
	var idInput = button.idInput;
	
	var destiny = Ext.getCmp(idDestiny);
	
	var values = Ext.getCmp(idSource).getChecked();
	for (var i = 0; i < values.length; i++){
		var removed = values[i].remove();
		destiny.getRootNode().appendChild(removed.attributes);
	}
	
	var result = "";
	var choices = destiny.getRootNode().childNodes;
	var append = "";
	for (var k = 0 ; k < choices.length; k++){
		result += append;
		result += choices[k].id;
		append = ",";
	} 
	
	Ext.get(idInput).set({'value' : result});
	
	destiny.doLayout();
	
};

/**
 * 
 * Change an element from the selections tree to the choices tree
 * 
 * */
XVW.changeToSourceTree = function( button ){
	
	var idSource = button.sourceTree;
	var idDestiny = button.destinyTree;
	var idInput = button.idInput;
	
	var destiny = Ext.getCmp(idSource);
	
	var values = Ext.getCmp(idDestiny).getChecked();
	for (var i = 0; i < values.length; i++){
		var removed = values[i].remove();
		destiny.getRootNode().appendChild(removed.attributes);
	}
	
	var result = "";
	var choices = Ext.getCmp(idDestiny).getRootNode().childNodes;
	var append = "";
	for (var k = 0 ; k < choices.length; k++){
		result += append;
		result += choices[k].id;
		append = ",";
	} 
	
	Ext.get(idInput).set({'value' : result});
	
	destiny.doLayout();
	
};


XVW.checkNode = function ( idDestiny, idInput){
	
	var result = "";
	var choices = Ext.getCmp(idDestiny).getChecked();
	var append = "";
	for (var k = 0 ; k < choices.length; k++){
		result += append;
		result += choices[k].id;
		append = ",";
	} 
	
	Ext.get(idInput).set({'value' : result});
};
