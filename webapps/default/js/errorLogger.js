
function showInformation(){
	var elements = Ext.getCmp('form:errorList').getSelectionModel().getSelections();
	if (elements.length > 0){
		var newContent = "";
		for (var i = 0 ; i < elements.length ; i++){
			newContent = elements[i].get("STACK_TRACE");
		}
		Ext.get('preview').update(newContent);
	}
}

Ext.onReady(function(){
	Ext.getCmp('form:errorList').getSelectionModel().addListener('rowselect',showInformation);
});


