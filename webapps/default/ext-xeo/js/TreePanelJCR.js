ExtXeo.TreeJCR = function() {};

ExtXeo.TreeJCR.nodeSelection = function( node, event, inputId ) {
	
	var oInput = document.getElementById( inputId );
    if( oInput != null ) 
    {
        oInput.value = node.id;
    }
    else {
        alert('Cannot store selected node!');
    }
    
};

ExtXeo.TreeJCR.treeContextHandler = function( node ) {
	node.select();
	if (node.leaf)
		contextMenu.show(node.ui.getAnchor());
	else
		contextMenuFolder.show(node.ui.getAnchor());
	
	
};
