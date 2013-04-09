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

/**
 * 
 * Before close tab
 * 
 * */
XEOLayout.onCloseTab = function( oTabCont, oComp ) {
	if( !oComp.forceClose ) {
		var changed = false;
		var x = oComp.el.dom.getElementsByTagName('iframe');
		for( var i=0;!changed && i < x.length; i++ ) {
			try {
				var y = x[i].contentWindow.document.getElementsByName("__isChanged");
				for( var k = 0;k < y.length; k++ ) {
					var cmd = y[i].value;
					if( !x[i].contentWindow.XVW.canCloseTab( cmd.split(':')[0], cmd.split(':')[1] ) ) {
 						oTabCont.activate( oComp );
						return false;
					}
					break;
				}
			}
			catch(e) {
				e=e;
			}
		}
    }
}