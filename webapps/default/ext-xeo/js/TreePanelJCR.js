ExtXeo.TreeJCR = function() {}

ExtXeo.TreeJCR.nodeSelection = function( node, event, inputId ) {
	
	var oInput = document.getElementById( inputId );
    if( oInput != null ) 
    {
        oInput.value = node.id;
    }
    else {
        alert('Cannot store selected node!');
    }
    
}


ExtXeo.TreeJCR.treeContextHandler = function( node ) {
	node.select();
	if (node.leaf)
		contextMenu.show(node.ui.getAnchor());
	else
		contextMenuFolder.show(node.ui.getAnchor());
	
	
}

/*ExtXeo.TreeJCR.removeElementsFromList = function( inputId ) {
	
	var elSel = document.getElementById( inputId );
	var i;
	var selected = false;
    for (i = elSel.length - 1; i>=0; i--) {
      if (elSel.options[i].selected) {
        elSel.remove(i);
        selected = true;
      }
    }
    
    if (!selected)
	{
    	Ext.MessageBox.show({
            title: 'Error',
            msg: 'You must select an element to remove',
            buttons: Ext.MessageBox.OK,
            icon: Ext.MessageBox.ERROR
        });

	}
    
}*/