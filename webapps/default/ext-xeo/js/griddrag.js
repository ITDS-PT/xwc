/**
 * Grid grouping through drag and drop implementation 
 */

ExtXeo.GridGroupDragDrop = Ext.extend(Ext.util.Observable, {
	
	 grid : null
	,gridView : null
	,groupSeparatorsCache : null
	,indexOfCurrentDivider : -1
	,indexGroupDragForReoder : -1
	,idOfCurrentDivider : null
	,idDivForDrop : null
	
	, constructor: function( opts ) {
		ExtXeo.GridGroupDragDrop.superclass.constructor.apply(this, arguments);
		this.grid = opts.grid;
		this.gridView = opts.gridView;
		this.groupSeparatorsCache = [];
		this.initDropTarget();
		this.idDivForDrop = opts.grid.id + "_dragDropGroup";
		
		//Create First Divider
		var firstDivider = this.createDivider();
		this.addDropSupportToDivider(firstDivider);
		
		Ext.get(this.idDivForDrop).child('div').child('div').appendChild(firstDivider);
		
		this.rebuildGroups();
		
	}
	
	,initDropTarget : function(){
		
		var gridId = this.grid.id;
		var gridGroupDropTargetId = gridId + "_dropTarget"; 
    	var divIdentifier = this.grid.id + "_dragDropGroup";
    	
    	new Ext.dd.DDTarget( Ext.get(gridId) , gridGroupDropTargetId ); //Id por GridPanel
    	
    	var columnDropTargetForGroup = new Ext.dd.DropTarget(divIdentifier,  {
    		
	    	ddGroup : "gridHeader" + this.grid.getGridEl().id //Same as the GridHeaderDrag/DropZone
	    	
	    	, isGroupDragDrop : true
	    	
	    	,notifyOver: function ( source, evtObj, data ){ 
	    		
	    		var grid = source.grid;
	    		var gridDragPlugin = grid.getGroupDragDropPlugin(); 
	    		
	    		var elements = gridDragPlugin.getCacheOfGroupSeparators();
	    		if ( elements.length == 0 ){
	    			elements = Ext.get(divIdentifier).query('.group_divider');
	    			if ( elements != null && elements != undefined )
	    				gridDragPlugin.setGroupSeparators( elements );
	    			else
	    				gridDragPlugin.setGroupSeparators( [] );
	    		}
	    	
	    		var found = false;
	    		Ext.each(elements, function(element, index){
	    			var newElem = Ext.get(element);
	    			if (evtObj.within(element,false) && !found){
	    				gridDragPlugin.highlightDivider(newElem);
	    				gridDragPlugin.indexOfCurrentDivider = gridDragPlugin.calculateIndexOfDivider(newElem);
	    				gridDragPlugin.idOfCurrentDivider = element.id;
	    				found = true;
	    			} else {
	    				gridDragPlugin.unHighlightDivider(newElem);
	    			}
	    			
	    		});
	    		
	    		var columnIdentifier = gridDragPlugin.findColumnIdentifier( source.view, data.ddel );
	        	if (grid.canGroupColumn(columnIdentifier)){
	        		return "x-dd-drop-ok";
	        	} else
	        		return "x-dd-drop-nodrop";
	    		
	    	}
    	
	    	, notifyDrop: function ( source, evtObj, data ){
	    		var grid = source.grid;
	    		var gridDragPlugin = grid.getGroupDragDropPlugin();
	    		var columnIdentifier = gridDragPlugin.findColumnIdentifier( source.view, data.ddel );
	    		
	    		if (grid.canGroupColumn(columnIdentifier)){
	        		gridDragPlugin.groupDroppedOnTarget( source, evtObj, data );
		    		return true; 
	        	} 
	        	
	        	//Hide the divider
	        	var id = gridDragPlugin.idOfCurrentDivider;
	        	if (id != null){
	        		var divider = Ext.get(id);
	        		gridDragPlugin.unHighlightDivider(divider);
	        	}
	        	
	        	gridDragPlugin.reset();
	        	return false;
	    		
	    	}
	    	
	    });
    }
	
	, highlightDivider : function ( element ) {
		element.addClass('group_dividir_hover');
	}
	, calculateIndexOfDivider : function ( elementToFind ){
		var dividers = elementToFind.parent().query('.group_divider');
		var indexToReturn = -1;
		//Tentei com Ext.each e por algum motivo a coisa nunca
		//entrava, tive de mudar para o for :|
		for (k = 0; k < dividers.length; k++){
			var elem = Ext.get(dividers[k]);
			if ( elem.id == elementToFind.id ){
				indexToReturn = k;
				break;
			}
		}
		
		return indexToReturn;
	}
	
	, unHighlightDivider : function ( element ){
		element.removeClass('group_dividir_hover');
	}
	
	//Invoked when a user drags a column from the GridPanel and drops
	//it on the correct spot, Creates a group button on the drop zone
	,groupDroppedOnTarget : function ( source, e, data ){
    	
		var gridIdentifier = this.grid.id;
    	var columnIdentifier = this.findColumnIdentifier( source.view, data.ddel );
    	
    	if (this.grid.canGroupColumn(columnIdentifier)){
			this.groupDataStoreByColumnAtIndex(columnIdentifier, this.indexOfCurrentDivider);
	    	this.addGroupAtIndex( columnIdentifier, this.indexOfCurrentDivider );
	    	this.hideGroupedColumn( columnIdentifier );
    	}
		this.unHighlightAllDividers();
		this.reset();
		
		return true;
    	
	}
	
	/**
	 * 
	 * Creates a group at the current position
	 * 
	 * */
	, createGroup : function ( columnIdentifier ){
		
		this.groupDataStoreByColumnAtIndex( columnIdentifier, this.indexOfCurrentDivider );
    	this.addGroupAtIndex( columnIdentifier, this.indexOfCurrentDivider );
    	if (this.grid.isGroupToolBarVisible())
    		this.hideGroupedColumn( columnIdentifier );
    	
    	this.unHighlightAllDividers();
		this.reset();
		
	}
	
	
	/**
	 * Create a group for a given column (in a given index)
	 * may need to re-order groups
	 * 
	 * @param groupId the Group identifier
	 * @param index The index to create the group
	 * */
   , addGroupAtIndex : function(groupId, index){
   	   
	   groupButton = this.createGroupButton( groupId );
	   
	   newDivider = this.createDivider(); 
	   this.addDropSupportToDivider(newDivider);
	   
	   var dividerToAppend = this.getDividerToInsertGroup();
	   if (index == 0){
		   newDivider.insertBefore(dividerToAppend);
		   groupButton.insertBefore(dividerToAppend);
	   } else {
		   newDivider.insertAfter(dividerToAppend);
		   groupButton.insertAfter(dividerToAppend);
	   }
	   
	   //Reset groups
	   this.setGroupSeparators([]);
	   
	   this.addDragDropSupportToGroupButton( groupButton.child('div') );
   }
   
   , groupDataStoreByColumnAtIndex : function (columnId, index) {
	   
	   
	   var groups = this.getCurrentGroups();
	   
	   var groupTotal = groups.length;
	   var beforeGroups = [];
	   var afterGroups = [];
	   
	   if (this.isToAddGroupAtEnd( groups, index ) ){
		   this.grid.groupByColumn( columnId );
	   } else { 
		   for ( i = 0 ; i < groupTotal ; i++ ){
			   if ( i < index )
				   beforeGroups.push( groups[ i ] );
			   else if ( i >= index )
				   afterGroups.push( groups[ i ] );
		   }
		   
		   this.grid.clearGroups();
		   
		   for ( k = 0 ; k < beforeGroups.length ; k++ ){
			   this.grid.groupByColumn( beforeGroups[ k ] );
		   }
		   
		   this.grid.groupByColumn( columnId );
		   
		   for ( m = 0 ; m < afterGroups.length ; m++){
			   this.grid.groupByColumn( afterGroups[ m ] );
		   }
		   
		   
	   }
	   
	   this.grid.reload();
		
   }
   
   , isToAddGroupAtEnd : function (elements , index ){
	   if ( index == -1 )
		   return true;
	   
	   if ( index >= 0 && ( index < ( elements.length ) ) )
		   return false;
	   
	   return true;
   }
   
   , createDivider : function (){
	   var element = Ext.get( document.createElement( 'div' ) );
	   element.addClass( 'group_divider' );
	   element.addClass( 'placeHolder' );
	   element.id = Ext.id();
	   return element;
   }
   
   , addDropSupportToDivider : function ( divider ){
	
	   new Ext.dd.DropTarget(divider,  {
   		
		     ddGroup : this.grid.id + "_reorderGroup" 
	    
		    , grid : this.grid
		   
		    , notifyOut : function( source, evtObj, data ) {
		    	
		    	var gridPlugin = this.grid.getGroupDragDropPlugin();
		    	gridPlugin.unHighlightAllDividers();
		    	
		    }
		    
	    	, notifyOver : function ( source, evtObj, data ){
	    		
	    		//Refactor to somewhere else, here is not appropriate for the the DropTarget
	    		//to know the identifier for the dropGroup
	    		
	    		
	    		var gridDragPlugin = this.grid.getGroupDragDropPlugin();
	    		
	    		var divIdentifier = gridDragPlugin.idDivForDrop;
	    		
	    		var elements = gridDragPlugin.getCacheOfGroupSeparators();
	    		if ( elements.length == 0 ){
	    			elements = Ext.get(divIdentifier).query('.group_divider');
	    			if ( elements != null && elements != undefined )
	    				gridDragPlugin.setGroupSeparators( elements );
	    			else
	    				gridDragPlugin.setGroupSeparators( [] );
	    		}
	    	
	    		var found = false;
	    		Ext.each(elements, function(element, index){
	    			var newElem = Ext.get(element);
	    			if (evtObj.within(element,false) && !found){
	    				gridDragPlugin.highlightDivider(newElem);
	    				gridDragPlugin.indexOfCurrentDivider = gridDragPlugin.calculateIndexOfDivider(newElem);
	    				gridDragPlugin.idOfCurrentDivider = element.id;
	    				found = true;
	    			} else {
	    				gridDragPlugin.unHighlightDivider(newElem);
	    			}
	    			
	    		});
	    		
	    		
	    		return "x-dd-drop-ok";
	    	}
	      
	   });
	   
   }
   
   , reset : function (){
	   this.idOfCurrentDivider = null;
	   this.indexOfCurrentDivider = -1;
	   this.indexGroupDragForReoder = -1;
   }
   
   , getDividerToInsertGroup : function (){
	   
	   if (	   this.idOfCurrentDivider != null && 
			   this.idOfCurrentDivider != undefined 
			   && this.idOfCurrentDivider != "")
	   {
		   return Ext.get(this.idOfCurrentDivider);
	   }
	   
	   elem =  Ext.get(this.idDivForDrop).query('.group_divider');
	   return Ext.get(elem[elem.length-1]);
	   
   }
   
   ,hideGroupedColumn : function (columnIdentifier) {
		var idx = this.grid.colModel.findColumnIndex(columnIdentifier);
		if (idx >= 0)
			this.grid.colModel.setHidden(idx,true);
		else
			console.log("Could not find column " + columnIdentifier);
	}
	
	, findColumnIdentifier : function (gridView, column){
		var cell = gridView.findCellIndex(column);
		return gridView.cm.getColumnId(cell);
	}
	
	/**
	 * 
	 * Create an HTML element representing the grouped column
	 * with the ability to remove the group and drag it
	 * */
	,createGroupButton : function (columnIdentifier) {
		
		
		myEl = new Ext.Element(document.createElement('div'));
		
		var name = this.grid.getColumnLabel( columnIdentifier );
		
		var nameElement = new Ext.Element(document.createElement('div'));
		nameElement.addClass('x-grid3-hd-inner x-grid3-header-1 sort-desc');
		nameElement.update(name + ' ');
		nameElement.set({	
					  style : 'display:inline'
				});
		
		var imgDirection = new Ext.Element(document.createElement('img'));
		imgDirection.addClass('no_sort');
		imgDirection.addClass('x-grid3-sort-icon');
		
		imgDirection.set({	
			  src : 'extjs/resources/images/default/s.gif'
		});
		
    	if (this.grid.isColumnSorted( columnIdentifier )){
    		var sort = this.grid.getColumnSort( columnIdentifier );
    		if (sort == 'ASC')
    			imgDirection.addClass('sort_asc');
    		else if (sort == 'DESC')
    			imgDirection.addClass('sort_desc');
    	}
    	
    	var buttonToRemoveGroup = this.createRemoveGroupButton(columnIdentifier);
    	
    	myEl.appendChild(imgDirection.dom);
    	myEl.appendChild(nameElement.dom);
    	myEl.appendChild(buttonToRemoveGroup.dom);
		
		myEl.set(
		{	
			  data : columnIdentifier
			, direction : ''
			
		});
		
		myEl.addClass('x-grid3-header');
		if ( this.grid.isColumnSortable( columnIdentifier ) ){
				nameElement.on('click', ExtXeo.sortGroupedColumn, 
					this,
					{
						gridId: this.grid.id
					}
			);
		} else  {
			myEl.addClass('xwc-disabled-group'); 
		}
		
		var parentColumn = new Ext.Element( document.createElement( 'div' ) );
		parentColumn.appendChild( myEl.dom );
		parentColumn.addClass( 'placeHolder' );
		
		return parentColumn;
    }
	
	
	, unHighlightAllDividers : function (){
		var elems = Ext.get(this.idDivForDrop).query('.group_divider');
		Ext.each(elems, function(element,index){
			Ext.get(element).removeClass('group_dividir_hover');
		});
	} 
	
	,addDragDropSupportToGroupButton : function (btn){
		
		this.addReorderGroupsDragSupportToButton(btn);
		
	}
	
	, addReorderGroupsDragSupportToButton : function (groupButton) {
		
		var ddReorder = new Ext.dd.DragSource(groupButton, {
			  grid : this.grid
			  , ddGroup : this.grid.id + "_reorderGroup"
			  , isTarget : false
			  
			  
		});
		
		var overridesReorder = {

			onBeforeDrag : function ( data, evtObj ) {
				
				var gridPlugin = this.grid.getGroupDragDropPlugin();
				
				var groups = gridPlugin.getCurrentGroups();
				
				var columnName = ExtXeo.getAttribute(this.getEl(),'data');
				var index = this.findIndexOfGroup(columnName,groups);
				
				gridPlugin.indexGroupDragForReoder = index;
				
				return true;
			}
				
				
			 , onDragDrop : function ( evtObj, targetElId ){
				var target = Ext.get(targetElId);
				
				//NOTE; Have to call this, PERIOD. Or the Drag ceases to function
				//The visual effect is kinda ugly
				this.getProxy().repair(target.getXY(), this.afterRepair, this);
				this.getProxy().hide(true);
				
				this.applyReOrderGroups(target);
				
				var gridPlugin = this.grid.getGroupDragDropPlugin();
				gridPlugin.unHighlightAllDividers();
				
			}
			
			//NOTE; If I don't call the repair function like this, I cannot override the function PERIOD
			//Haven't really understood why
			, onInvalidDrop : function ( evtObj ) {
				this.getProxy().repair(this.getRepairXY(evtObj, this.dragData), this.afterRepair, this);
			}
			
			
			, applyReOrderGroups : function ( dividerToAppendGroup ) {
				
				var gridDragPlugin = this.grid.getGroupDragDropPlugin();
				var finalIndex = gridDragPlugin.indexOfCurrentDivider;
				var initialIndex = gridDragPlugin.indexGroupDragForReoder;
				
				//Reorder GridGroups (Javascript)
				var groups = gridDragPlugin.getCurrentGroups();
				
				if ( this.validReOrder( initialIndex, finalIndex ) ){
				
					var newGroups = this.calculateNewGroups(groups, initialIndex, finalIndex);
					
					this.reCreateGroups( newGroups );
					this.reOrderHtmlElements(
							gridDragPlugin.idDivForDrop, 
							dividerToAppendGroup,
							initialIndex);
				
				}
				
			}
			
			, validReOrder : function (initialIndex, finalIndex){
				return ( Math.abs( initialIndex - finalIndex ) > 1 )  || ( initialIndex > finalIndex );
			}
			
			, findIndexOfGroup: function ( groupName , groups ){
				for (k = 0; k < groups.length; k++){
					if (groups[k] === groupName)
						return k;
				}
				return -1;
			}
			
			, calculateNewGroups : function ( groups, initialIndex, finalIndex ) {
				
				var groupToMove = groups[ initialIndex ];
				
				var newGroups = [];
				for (i = 0 ; i < groups.length; i++){
					if ( i == finalIndex ){
						newGroups.push( groupToMove );
						newGroups.push( groups[ i ] );
					}
					else {
						//Add the remaining groups
						if ( i != initialIndex )
							newGroups.push( groups[ i ] );
					}
				}
				
				if (finalIndex >= groups.length){
					newGroups.push( groupToMove );
				}
				
				return newGroups;
			}
			
			/**
			 * 
			 * Re-Orders HTML elements (group buttons and dividers) to respect
			 * the new group order.
			 * 
			 * */
			,reOrderHtmlElements : function (idDivForDrop, dividerToAppendGroup, initialIndex){
				
				var elem = Ext.get(idDivForDrop).child('div');
				
				//Reason for (initialIndex + 1 * 2)
				/*
				 * Imagine the following scenario:
				 * 
				 *  | Group1 | Group 2 | Group 3 | 
				 * 
				 * | -> Divider
				 * Group1 -> A group by a column
				 * 
				 * Initial index is 0-based and is the index for the groups (Group1, Group2, etc..)
				 * The ( initialIndex ) * 2 is because we are selecting elements from the list of | and Groups
				 * which means that the html element of the group, in order to be found must have the index
				 * multiplied by two, the plus one is because of the array being 0 based  
				 * */
				
				//var groupToReorder = elem.select('.DDrowDrop div:nth-child('+((initialIndex+1)*2)+')');
				//var dividirToReOrder = elem.select('.DDrowDrop div:nth-child('+(((initialIndex+1)*2)+1)+')');
				
				var elems = Ext.get(elem.query('.DDrowDrop > div'));
				groupIndex = (initialIndex*2)+1;
				var groupToReorder = Ext.get(elems.elements[groupIndex]);
				var dividirToReOrder = Ext.get(elems.elements[groupIndex+1]);
				
				dividirToReOrder.insertAfter(dividerToAppendGroup);
				groupToReorder.insertAfter(dividerToAppendGroup);
			}
			
			, reCreateGroups : function ( newGroups ){
				this.grid.clearGroups();
				for ( k = 0 ; k < newGroups.length ; k++ ){
					this.grid.groupByColumn( newGroups[k] );
				}
				this.grid.reload();
			}
		
		};
		
		Ext.apply(ddReorder,overridesReorder);
	}
	
	, ungroup : function (groupId){
		this.removeGroup(groupId);
		this.showColumnAfterGroupRemove(groupId);
		this.reset();
	}
	
	, removeGroup : function (groupId){
		this.grid.removeGroupByColumn(groupId);
	}
	, showColumnAfterGroupRemove : function (groupId){
		var idx = this.grid.colModel.findColumnIndex(groupId);
		grid.colModel.setHidden(idx,false);
	}
	
	/**
	 * Creates the remove button to be added to the element representing
	 * the grouped column so that the group can be removed
	 * 
	 * */
    ,createRemoveGroupButton : function ( columnId ) {
    	var cleanButton = new Ext.Element( document.createElement( 'div' ) );
		cleanButton.set( { style : "display:inline; color:#800000; background-color:transparent; cursor:hand" } );
		cleanButton.update( " " );
		cleanButton.on( 'click', ExtXeo.removeColumn, null, {gridId: this.grid.id, colId : columnId } );
		
		var imgRemove = new Ext.Element( document.createElement( 'img' ) );
		imgRemove.set({ src : "icons/delete.png" , style : 'vertical-align:middle'});
		
		cleanButton.appendChild(imgRemove.dom);
		
		return cleanButton;
    }
    
    //Retrieve the currently selected groups
    ,getCurrentGroups : function(){
    	return this.grid.getGroups();
    }
    
    //Retrieve a cache of the elements that are group separators
    ,getCacheOfGroupSeparators : function (){
    	if (this.groupSeparatorsCache === null){
    		this.groupSeparatorsCache == [];
    	}
    	return this.groupSeparatorsCache;
    }
    , setGroupSeparators : function (elements){
    	//Should be array
    	this.groupSeparatorsCache = elements;
    }
    
    , rebuildGroups : function () {
    	
    	var groups = this.getCurrentGroups();
    	if ( groups.length > 0 ){
    		var count = Ext.get( this.idDivForDrop )
    			.child( 'div' ).child( 'div' ).query( 'div' ).length;
    		if ( count <= 1 ){
    			for (k = 0; k < groups.length ; k++){
    				var group = groups[ k ];
    				this.addGroupAtIndex( group, 0);
    				this.hideGroupedColumn( group );
    			}
    		}
    	}
    	
    	
    }
    
	
});

/**
 * 
 * Remove grouping for a given column
 * 
 * */
ExtXeo.removeColumn = function(ev, target, options){
	
	var grid = Ext.getCmp(options.gridId);
	var columnId = options.colId;
	
	grid.removeGroupByColumn(columnId);
	ExtXeo.destroyGroupElements(target);
	
	grid.getGroupDragDropPlugin().reset();
	
	grid.showColumn(columnId);
	
    
};

/**
 * 
 * Sort an existing grouped column
 * 
 * */
ExtXeo.sortGroupedColumn = function (ev, target, options ){
	
	
	var targetElem = Ext.get(target).parent();
	
	var columnId = ExtXeo.getAttribute(targetElem,'data');
	var direction = ExtXeo.getAttribute(targetElem,'direction');
	
	var gridId = options.gridId;
	var gridCmp = Ext.getCmp(gridId);
	
	if (direction == ''){
		gridCmp.sortColumn( columnId, 'ASC' );
		targetElem.set({direction : 'ASC'});
		targetElem.child('.no_sort').addClass('sort_asc');
	} else if (direction == 'ASC'){
		gridCmp.sortColumn( columnId, 'DESC' );
		targetElem.set({direction : 'DESC'});
		targetElem.child('.no_sort').replaceClass('sort_asc','sort_desc');
	} else if (direction == 'DESC'){
		gridCmp.sortColumn( columnId, '' );
		targetElem.set({direction : ''});
		targetElem.child('.no_sort').removeClass('sort_desc');
	} else {
		gridCmp.sortColumn( columnId, 'ASC' );
		targetElem.set({direction : 'ASC'});
		targetElem.child('.no_sort').removeClass('sort_desc');
		targetElem.child('.no_sort').addClass('sort_asc');
	}
	
	
}

/**
 * Destroys the button and the divider of a group
 * when it's being removed
 * */
ExtXeo.destroyGroupElements = function ( target ){
	var element = Ext.get(target).parent('div.placeHolder');
	var divider = element.next('.group_divider');
	element.remove();
	divider.remove();
};

/**
 * 
 * Retrieves a given attribute value from an element
 * */
ExtXeo.getAttribute = function (element, attribute){
	return Ext.get(element).getAttributeNS( "", attribute );
};