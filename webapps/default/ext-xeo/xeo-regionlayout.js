/**
*
* Fetches the dinamically moved divs by the ExtJS framework into the form
* component so that submitted values can be fetched
*
*/
XVW.appendPanels = function appendPanelsToForm(){

	//Retrieve forms and regions 
	var form = Ext.get('formMain');
	var top = Ext.get('north-panel');
	var bottom = Ext.get('south-panel');
	var left = Ext.get('west-panel');
	var right = Ext.get('east-panel');
	
	//For each of the existing regions append them to the form
	//so that we can receive values
	if (top)
		top.appendTo(form);
	if (bottom)	
		bottom.appendTo(form);
	if (left)	
		left.appendTo(form);
	if (right)	
		right.appendTo(form);
}
