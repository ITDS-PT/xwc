ExtXeo.grid = function() {}

ExtXeo.grid.GridPanel = Ext.extend(Ext.grid.GridPanel,
	{
		minHeight : 0,
		getMinHeight : function() {
			return this.minHeight;
		},
		setMinHeight : function( minHeight ) {
			this.minHeight = minHeight; 
		}
	}
);

ExtXeo.grid.rowSelectionHndlr = function( oSelModel, oGridInputSelId, rowIdentifier ) {
	var sSelRecs = "";
    var oInput = document.getElementById( oGridInputSelId );
    if( oInput != null ) {
        var oSelRecs = oSelModel.getSelections();
        for (var i = 0; i < oSelRecs.length; i++)  {
            if(i>0) sSelRecs += "|";
            sSelRecs += oSelRecs[i].get( rowIdentifier );
        }
        oInput.value = sSelRecs;
    }
    else {
        alert('Select rows input not found!!');
    }
}

ExtXeo.grid.rowClickHndlr = function( oGrid, rowIndex, oEvent, sGridInputId, sRowIdentifier ) {
    var sSelRecs = "";
    var oInput = document.getElementById( sGridInputId );
    if( oInput != null ) {
        var oSelRec = oGrid.getStore().getAt( rowIndex );
        sSelRecs = oSelRec.get( sRowIdentifier );
        oInput.value = sSelRecs;
    }
    else {
        // Possible... the input was not rendered by the server...
    } 

}

ExtXeo.PagingToolbar = Ext.extend(Ext.Toolbar, {
    pageSize:50,
    displayMsg : 'Mostrando {0} - {1} de {2}',
    emptyMsg : 'Sem dados para mostrar',
    beforePageText : "P&aacute;gina",
    afterPageText : "de {0} ({1})",
    firstText : "Primeira P&aacute;gina",
    prevText : "P&aacute;gina Aterior",
    nextText : "Pr&oacute;xima P&aacute;gina",
    lastText : "&Uacute;ltima p&aacute;gina",
    refreshText : "Actualizar",
    paramNames : {start: 'start', limit: 'limit'},
    initComponent : function(){
        this.addEvents('change', 'beforechange');
        ExtXeo.PagingToolbar.superclass.initComponent.call(this);
        this.cursor = 0;
        this.bind(this.store);
    },
    onRender : function(ct, position){
    	ExtXeo.PagingToolbar.superclass.onRender.call(this, ct, position);
        this.first = this.addButton({
            tooltip: this.firstText,
            iconCls: "x-tbar-page-first",
            disabled: true,
            handler: this.onClick.createDelegate(this, ["first"])
        });
        this.prev = this.addButton({
            tooltip: this.prevText,
            iconCls: "x-tbar-page-prev",
            disabled: true,
            handler: this.onClick.createDelegate(this, ["prev"])
        });
        this.addSeparator();
        this.add(this.beforePageText);
        this.field = Ext.get(this.addDom({
           tag: "input",
           type: "text",
           size: "3",
           value: "1",
           cls: "x-tbar-page-number"
        }).el);
        this.field.on("keydown", this.onPagingKeydown, this);
        this.field.on("focus", function(){this.dom.select();});
        this.afterTextEl = this.addText(String.format(this.afterPageText, 1));
        this.field.setHeight(18);
        this.addSeparator();
        this.next = this.addButton({
            tooltip: this.nextText,
            iconCls: "x-tbar-page-next",
            disabled: true,
            handler: this.onClick.createDelegate(this, ["next"])
        });
        this.last = this.addButton({
            tooltip: this.lastText,
            iconCls: "x-tbar-page-last",
            disabled: true,
            handler: this.onClick.createDelegate(this, ["last"])
        });
        this.addSeparator();
        this.loading = this.addButton({
            tooltip: this.refreshText,
            iconCls: "x-tbar-loading",
            handler: this.onClick.createDelegate(this, ["refresh"])
        });

        if(this.displayInfo){
            this.displayEl = Ext.fly(this.el.dom).createChild({cls:'x-paging-info'});
        }
        if(this.dsLoaded){
            this.onLoad.apply(this, this.dsLoaded);
        }
    },
    updateInfo : function(){
        if(this.displayEl){
            var count = this.store.getTotalCount();
            var msg = count == 0 ?
                this.emptyMsg :
                String.format(
                    this.displayMsg,
                    this.cursor+1, this.cursor+count, this.store.getTotalCount()
                );
            this.displayEl.update(msg);
        }
    },
    onLoad : function(store, r, o){
        if(!this.rendered){
            this.dsLoaded = [store, r, o];
            return;
        }
		this.cursor = o.params && o.params[this.paramNames.start] ? o.params[this.paramNames.start] : 0;
		var d = this.getPageData(), ap = d.activePage, ps = d.pages;
		this.afterTextEl.el.innerHTML = String.format(this.afterPageText, d.pages, d.total );
		
		this.field.dom.value = ap;
		
		this.first.setDisabled(ap == 1);
		this.prev.setDisabled(ap == 1);
		this.next.setDisabled(ap == ps);
		this.last.setDisabled(ap == ps);
		this.loading.enable();
		this.updateInfo();
		this.fireEvent('change', this, d);
    },
    getPageData : function(){
        var total = this.store.getTotalCount();
        return {
            total : total,
            activePage : Math.ceil((this.cursor+this.pageSize)/this.pageSize),
            pages :  total < this.pageSize ? 1 : Math.ceil(total/this.pageSize)
        };
    },

    onLoadError : function(){
        if(!this.rendered){
            return;
        }
        this.loading.enable();
    },

    readPage : function(d){
        var v = this.field.dom.value, pageNum;
        if (!v || isNaN(pageNum = parseInt(v, 10))) {
            this.field.dom.value = d.activePage;
            return false;
        }
        return pageNum;
    },

    onPagingKeydown : function(e){
        var k = e.getKey(), d = this.getPageData(), pageNum;
        if (k == e.RETURN) {
            e.stopEvent();
            pageNum = this.readPage(d);
            if(pageNum !== false){
                pageNum = Math.min(Math.max(1, pageNum), d.pages) - 1;
                this.doLoad(pageNum * this.pageSize);
            }
        }else if (k == e.HOME || k == e.END){
            e.stopEvent();
            pageNum = k == e.HOME ? 1 : d.pages;
            this.field.dom.value = pageNum;
        }else if (k == e.UP || k == e.PAGEUP || k == e.DOWN || k == e.PAGEDOWN){
            e.stopEvent();
            if(pageNum = this.readPage(d)){
                var increment = e.shiftKey ? 10 : 1;
                if(k == e.DOWN || k == e.PAGEDOWN){
                    increment *= -1;
                }
                pageNum += increment;
                if(pageNum >= 1 & pageNum <= d.pages){
                    this.field.dom.value = pageNum;
                }
            }
        }
    },
    beforeLoad : function(){
        if(this.rendered && this.loading){
            this.loading.disable();
        }
    },
    doLoad : function(start){
        var o = {}, pn = this.paramNames;
        o[pn.start] = start;
        o[pn.limit] = this.pageSize;
        if(this.fireEvent('beforechange', this, o) !== false){
            this.store.load({params:o});
        }
    },
    changePage: function(page){
        this.doLoad(((page-1) * this.pageSize).constrain(0, this.store.getTotalCount()));  
    },

        onClick : function(which){
        var store = this.store;
        switch(which){
            case "first":
                this.doLoad(0);
            break;
            case "prev":
                this.doLoad(Math.max(0, this.cursor-this.pageSize));
            break;
            case "next":
                this.doLoad(this.cursor+this.pageSize);
            break;
            case "last":
                var total = store.getTotalCount();
                var extra = total % this.pageSize;
                var lastStart = extra ? (total - extra) : total-this.pageSize;
                this.doLoad(lastStart);
            break;
            case "refresh":
                this.doLoad(this.cursor);
            break;
        }
    },
    unbind : function(store){
        store = Ext.StoreMgr.lookup(store);
        store.un("beforeload", this.beforeLoad, this);
        store.un("load", this.onLoad, this);
        store.un("loadexception", this.onLoadError, this);
        this.store = undefined;
    },
    bind : function(store){
        store = Ext.StoreMgr.lookup(store);
        store.on("beforeload", this.beforeLoad, this);
        store.on("load", this.onLoad, this);
        store.on("loadexception", this.onLoadError, this);
        this.store = store;
    }
});


Ext.ns('ExtXeo','ExtXeo.grid');

ExtXeo.grid.GridView = Ext.extend(Ext.grid.GridView, {
    processRows : function(startRow, skipStripe){
        if(!this.ds || this.ds.getCount() < 1){
            return;
        }
        ExtXeo.grid.GridView.superclass.processRows.call( this, startRow, skipStripe);
    }
});

ExtXeo.grid.GroupingView = Ext.extend(Ext.grid.GridView, {
    hideGroupedColumn:false,
    showGroupName:true,
    startCollapsed:false,
    enableGrouping:true,
    enableGroupingMenu:true,
    enableNoGroups:true,
    emptyGroupText : '(None)',
    ignoreAdd: false,
    _groupTextTpl : '{text} ({count})',
    gidSeed : 1000,
    loadingMsg : 'A Carregar...',
    initTemplates : function(){
        ExtXeo.grid.GroupingView.superclass.initTemplates.call(this);
        this.state = {};

        var sm = this.grid.getSelectionModel();
        sm.on(sm.selectRow ? 'beforerowselect' : 'beforecellselect',
                this.onBeforeRowSelect, this);

        if(!this.startGroup){
            this.startGroup = new Ext.XTemplate(
                '<div id="{groupId}" class="x-grid-group {cls}">',
                    '<div id="{groupId}-hd" class="x-grid-group-hd" style="{style}"><div>', this._groupTextTpl ,'</div>',
                    '<SPAN id="{groupId}-tb" class=""></span>',
                    '</div>',
                    '<div id="{groupId}-bd" class="x-grid-group-body">'
            );
        }
        this.startGroup.compile();
        this.endGroup = '</div></div>';
    },
    findGroup : function(el){
        return Ext.fly(el).up('.x-grid-group', this.mainBody.dom);
    },
    getGroups : function(){
        return this.hasRows() ? this.mainBody.dom.childNodes : [];
    },
    onAdd : function(){
        if(this.enableGrouping && !this.ignoreAdd){
            var ss = this.getScrollState();
            this.refresh();
            this.restoreScroll(ss);
        }else if(!this.enableGrouping){
            ExtXeo.grid.GroupingView.superclass.onAdd.apply(this, arguments);
        }
    },
    onRemove : function(ds, record, index, isUpdate){
        ExtXeo.grid.GroupingView.superclass.onRemove.apply(this, arguments);
        var g = document.getElementById(record._groupId);
        if(g && g.childNodes[1].childNodes.length < 1){
            Ext.removeNode(g);
        }
        this.applyEmptyText();
    },
    refreshRow : function(record){
        if(this.ds.getCount()==1){
            this.refresh();
        }else{
            this.isUpdating = true;
            ExtXeo.grid.GroupingView.superclass.refreshRow.apply(this, arguments);
            this.isUpdating = false;
        }
    },
    beforeMenuShow : function(){
        var field = this.getGroupField();
        var g = this.hmenu.items.get('groupBy');
        if(g){
            g.setDisabled(this.cm.config[this.hdCtxIndex].groupable === false);
        }
        var s = this.hmenu.items.get('showGroups');
        if(s){
           s.setDisabled(!field && this.cm.config[this.hdCtxIndex].groupable === false);
			s.setChecked(!!field, true);
        }
    },
    renderUI : function(){
        ExtXeo.grid.GroupingView.superclass.renderUI.call(this);
        this.mainBody.on('mousedown', this.interceptMouse, this);

        if(this.enableGroupingMenu && this.hmenu){
            if(this.enableNoGroups){
                this.hmenu.add('-',{
                    id:'showGroups',
                    text: this.showGroupsText,
                    checked: true,
                    checkHandler: this.onShowGroupsClick,
                    scope: this
                });
            }
            this.hmenu.on('beforeshow', this.beforeMenuShow, this);
        }
    },
    onGroupByClick : function(){
        this.grid.store.groupBy(this.cm.getDataIndex(this.hdCtxIndex));
        this.beforeMenuShow(); 
    },
    onShowGroupsClick : function(mi, checked){
        if(checked){
            this.onGroupByClick();
        }else{
            this.grid.store.clearGrouping();
        }
    },
    toggleGroup : function(group, expanded){
        this.grid.stopEditing(true);
        group = Ext.getDom(group);
        
        var gel = Ext.fly(group);
        expanded = expanded !== undefined ?
                expanded : gel.hasClass('x-grid-group-collapsed');

        this.state[gel.dom.id] = expanded;
        gel[expanded ? 'removeClass' : 'addClass']('x-grid-group-collapsed');
        
    	var i=0;
    	for(;i<this.groups.length;i++ ) {
    		if( this.groups[i].groupId == group.id ) {
    			break;
    		}
    	}
        if( expanded ) {
        	
	        var scope = this;
	        var grpStore = this.grid.store.loadGroup( 
	        		function(i,s) { 
	        			scope.groupLoaded( i, s) 
	        		}, 
	        		i,
	        		this.groups[i].gvalue,
	        		this.groups[i].tb?this.groups[i].tb.cursor:0,
	        		50 
	        		);
	        grpStore.grpIdx = i;
        	grpStore.addListener( "datachanged", this.onDataChanged, this );
        	if( this.groups[i].tb != null ) {
        		this.groups[i].tb.hide();
        	} else {
	            var tb = new ExtXeo.PagingToolbar( { renderTo:this.groups[i].groupId + '-tb', store: grpStore } );
	            this.groups[i].tb = tb;
	            tb.hide();
        	}
        	
        	/*
	        var buf = [];
	        buf[buf.length] = ExtXeo.grid.GroupingView.superclass.doRender.call(
	                this, this.cs, this.groups[i].rs, this.ds, this.groups[i].startRow, this.colCount, this.stripe);
	        
	        //group.firstChild.nextSibling.innerHTML = buf.join('');
	        */
            group.firstChild.nextSibling.innerHTML = "<span>" + this.loadingMsg + "</span>";
        }
        else {
        	if( this.groups[i].tb != null ) {
        		this.groups[i].tb.hide();
        	}
        }
    },
    onDataChanged : function( store ) {
    	this.groupLoaded( store.grpIdx, store )
    },
    groupLoaded : function( idx , store ) {
        var buf = [];
//        buf[buf.length] = ExtXeo.grid.GroupingView.superclass.doRender.call(
//                this, this.cs, store.getRange(0,50), store, 0, this.colCount, this.stripe);
      buf[buf.length] = this.doRender1(
    		  this.cs, store.getRange(0,50), store, 0, this.colCount, this.stripe);
        
        var domg = this.getGroups();
        var i=0;
    	for(;i<domg.length;i++ ) {
    		if( this.groups[idx].groupId == domg[i].id ) {
    			break;
    		}
    	}
        var g = Ext.getDom(domg[i]);
        
        var gel = Ext.fly(g);
        this.state[gel.dom.id] = true;
        gel['removeClass']('x-grid-group-collapsed');        
        
        if( g ) {
        	if( !this.groups[i].tb ) {
	            var tb = new ExtXeo.PagingToolbar( { renderTo:this.groups[i].groupId + '-tb', store: store } );
	            this.groups[i].tb  = tb;
        	}
        	else {
        		if( store.getTotalCount() > 50 ) {
        			this.groups[i].tb.show();
        		}
        		else {
        			this.groups[i].tb.hide();
        		}
        	}
        	
        	g.firstChild.nextSibling.innerHTML = buf.join('');
	        this.grid.view.processRows(0,true);
        }
    },
    toggleAllGroups : function(expanded){
        var groups = this.getGroups();
        for(var i = 0, len = groups.length; i < len; i++){
            this.toggleGroup(groups[i], expanded);
        }
    },
    expandAllGroups : function(){
        this.toggleAllGroups(true);
    },
    collapseAllGroups : function(){
        this.toggleAllGroups(false);
    },
    interceptMouse : function(e){
        var hd = e.getTarget('.x-grid-group-hd', this.mainBody);
        if(hd){
            e.stopEvent();
            this.toggleGroup(hd.parentNode);
        }
    },
    getGroup : function(v, r, groupRenderer, rowIndex, colIndex, ds){
        var g = groupRenderer ? groupRenderer(v, {}, r, rowIndex, colIndex, ds) : String(v);
        if(g === ''){
            g = this.cm.config[colIndex].emptyGroupText || this.emptyGroupText;
        }
        return g;
    },
    getGroupField : function(){
        return this.grid.store.getGroupState();
    },
    renderRows : function(){
        var groupField = this.getGroupField();
        var eg = !!groupField;
        
        if(this.hideGroupedColumn) {
            var colIndex = this.cm.findColumnIndex(groupField);
            if(!eg && this.lastGroupField !== undefined) {
                this.mainBody.update('');
                this.cm.setHidden(this.cm.findColumnIndex(this.lastGroupField), false);
                delete this.lastGroupField;
            }else if (eg && this.lastGroupField === undefined) {
                this.lastGroupField = groupField;
                this.cm.setHidden(colIndex, true);
            }else if (eg && this.lastGroupField !== undefined && groupField !== this.lastGroupField) {
                this.mainBody.update('');
                var oldIndex = this.cm.findColumnIndex(this.lastGroupField);
                this.cm.setHidden(oldIndex, false);
                this.lastGroupField = groupField;
                this.cm.setHidden(colIndex, true);
            }
        }
        return ExtXeo.grid.GroupingView.superclass.renderRows.apply(
                    this, arguments);
    },
    doRender : function(cs, rs, ds, startRow, colCount, stripe){
    	this.cs = cs;
    	this.rs = rs;
    	this.ds = ds;
    	this.startRow = startRow;
    	this.colCount = colCount;
    	this.stripe = stripe;
    	
        if(rs.length < 1){
            return '';
        }
        var groupField = this.getGroupField();
        var colIndex = this.cm.findColumnIndex(groupField);

        this.enableGrouping = !!groupField;

        if(!this.enableGrouping || this.isUpdating){
            return ExtXeo.grid.GroupingView.superclass.doRender.apply(
                    this, arguments);
        }
        var gstyle = 'width:'+this.getTotalWidth()+';';

        var gidPrefix = this.grid.getGridEl().id;
        var cfg = this.cm.config[colIndex];
        var groupRenderer = cfg.groupRenderer || cfg.renderer;
        var prefix = this.showGroupName ?
                     (cfg.groupName || cfg.header)+': ' : '';

        this.groups = [];
        var curGroup, i, len, gid;
        for(i = 0, len = rs.length; i < len; i++){
            var rowIndex = startRow + i;
            var r = rs[i],
                gdvalue = r.data[groupField],
                gvalue = r.json[groupField+"__value"],
                gcount = r.json[groupField+"__count"],
                g = this.getGroup(gvalue, r, groupRenderer, rowIndex, colIndex, ds);
            
            if(!curGroup || curGroup.group != g){
                gid = gidPrefix + '-gp-' + groupField + '-' + Ext.util.Format.htmlEncode(g);
				
				var isCollapsed  = typeof this.state[gid] !== 'undefined' ? !this.state[gid] : this.startCollapsed;
				var gcls = isCollapsed ? 'x-grid-group-collapsed' : '';	
                curGroup = {
                    group: g,
                    gvalue: gvalue,
                    displayValue: gdvalue,
                    count: gcount,
                    text: prefix + gdvalue,
                    groupId: gid,
                    startRow: rowIndex,
                    rs: [r],
                    cls: gcls,
                    style: gstyle,
                    collapsed: isCollapsed
                };
                this.groups.push(curGroup);
            }else{
                curGroup.rs.push(r);
            }
            r._groupId = gid;
        }

        var buf = [];
        for(i = 0, len = this.groups.length; i < len; i++){
            var g = this.groups[i];
            this.doGroupStart(buf, g, cs, ds, colCount);
            /*
            buf[buf.length] = ExtXeo.grid.GroupingView.superclass.doRender.call(
                    this, cs, g.rs, ds, g.startRow, colCount, stripe);
			*/
            if( !g.collapsed ) {
	            buf[buf.length] = "<span>A Carregar...</span>";
	            //this.grid.store.loadGroup( this, i,g.gvalue,1,50 );
	            var scope = this;
		        var grpStore = this.grid.store.loadGroup( 
		        		function(i,s) { scope.groupLoaded( i, s) }, 
		        		i,
		        		g.gvalue, 
		        		this.groups[i].tb?this.groups[i].tb.cursor:0, 
		        		50 
		        	);
		        grpStore.grpIdx = i;
	        	grpStore.addListener( "datachanged", this.onDataChanged, this );
            }
            this.doGroupEnd(buf, g, cs, ds, colCount);
        }
        return buf.join('');
    },
    getGroupId : function(value){
        var gidPrefix = this.grid.getGridEl().id;
        var groupField = this.getGroupField();
        var colIndex = this.cm.findColumnIndex(groupField);
        var cfg = this.cm.config[colIndex];
        var groupRenderer = cfg.groupRenderer || cfg.renderer;
        var gtext = this.getGroup(value, {data:{}}, groupRenderer, 0, colIndex, this.ds);
        return gidPrefix + '-gp-' + groupField + '-' + Ext.util.Format.htmlEncode(value);
    },
    doGroupStart : function(buf, g, cs, ds, colCount){
        buf[buf.length] = this.startGroup.apply(g);
    },
    doGroupEnd : function(buf, g, cs, ds, colCount){
        buf[buf.length] = this.endGroup;
    },
    getRows : function(){
        if(!this.enableGrouping){
            return ExtXeo.grid.GroupingView.superclass.getRows.call(this);
        }
        var r = [];
        var g, gs = this.getGroups();
        for(var i = 0, len = gs.length; i < len; i++){
            g = gs[i].childNodes[1].childNodes;
            for(var j = 0, jlen = g.length; j < jlen; j++){
                r[r.length] = g[j];
            }
        }
        return r;
    },
    updateGroupWidths : function(){
        if(!this.enableGrouping || !this.hasRows()){
            return;
        }
        var tw = Math.max(this.cm.getTotalWidth(), this.el.dom.offsetWidth-this.scrollOffset) +'px';
        var gs = this.getGroups();
        for(var i = 0, len = gs.length; i < len; i++){
            gs[i].firstChild.style.width = tw;
        }
    },
    onColumnWidthUpdated : function(col, w, tw){
        this.updateGroupWidths();
    },
    onAllColumnWidthsUpdated : function(ws, tw){
        this.updateGroupWidths();
    },
    onColumnHiddenUpdated : function(col, hidden, tw){
        this.updateGroupWidths();
    },
    onLayout : function(){
        this.updateGroupWidths();
    },
    onBeforeRowSelect : function(sm, rowIndex){
        if(!this.enableGrouping){
            return;
        }
        var row = this.getRow(rowIndex);
        if(row && !row.offsetParent){
            var g = this.findGroup(row);
            this.toggleGroup(g, true);
        }
    },
    processRows : function(startRow, skipStripe){
        if(this.ds.getCount() < 1){
            return;
        }
        var rows = this.getRows();
        if( rows.length == 0 ) {
        	return;
        }
        ExtXeo.grid.GroupingView.superclass.processRows.call( this, startRow, skipStripe);
        
    },
    onDataChange : function(){
        this.refresh();
        this.updateHeaderSortState();
        //this.syncFocusEl(0);
    },
    doRender1 : function(cs, rs, ds, startRow, colCount, stripe){
        var ts = this.templates, ct = ts.cell, rt = ts.row, last = colCount-1;
        var tstyle = 'width:'+this.getTotalWidth()+';';
        // buffers
        var buf = [], cb, c, p = {}, rp = {tstyle: tstyle}, r;
        for(var j = 0, len = rs.length; j < len; j++){
            r = rs[j]; cb = [];
            var rowIndex = (j+startRow);
            
            var alt = [];
            rp.cols = colCount;
            if(this.getRowClass){
                alt[2] = this.getRowClass(r, rowIndex, rp, ds);
            }
            else {
            	alt[2] = "";
            }
            
            for(var i = 0; i < colCount; i++){
                c = cs[i];
                p.id = c.id;
                p.css = [ alt[2], ( i == 0 ? 'x-grid3-cell-first ' : (i == last ? 'x-grid3-cell-last ' : '') )].join();;
                p.attr = p.cellAttr = "";
                p.value = c.renderer(r.data[c.name], p, r, rowIndex, i, ds);
                p.style = c.style;
                if(p.value == undefined || p.value === "") p.value = "&#160;";
                if(r.dirty && typeof r.modified[c.name] !== 'undefined'){
                    p.css += ' x-grid3-dirty-cell';
                }
                cb[cb.length] = ct.apply(p);
            }
            if(stripe && ((rowIndex+1) % 2 == 0)){
                alt[0] = "x-grid3-row-alt";
            }
            if(r.dirty){
                alt[1] = " x-grid3-dirty-row";
            }
            rp.alt = alt.join(" ");
            rp.cells = cb.join("");
            buf[buf.length] =  rt.apply(rp);
        }
        return buf.join("");
    },    
    groupByText: 'Agrupar por esta coluna',
    showGroupsText: 'Agrupar'
});
ExtXeo.grid.GroupingView.GROUP_ID = 1000;

Ext.ns('ExtXeo','ExtXeo.data'); 
ExtXeo.data.GroupingStore = Ext.extend(Ext.data.Store, {
	groupStores : [],
    remoteGroup : true,
    clearGrouping : function(){
        this.groupField = false;
        if(this.baseParams){
            delete this.baseParams.groupBy;
            
            if( this.lastOptions.params )
            	delete this.lastOptions.params.groupBy;
            
        }
        this.reload();
    },
    getAt : function( index ) {
    	var r;
    	if( this.groupField ) {
	    	for( var s=0;s<this	.groupStores.length;s++ ) {
	    		var gs = this.groupStores[s];
	    		if( gs && gs.getCount ) {
	    			if( index >= gs.getCount() ) {
	    				index -= gs.getCount(); 
	    			}
	    			else {
	    				r = gs.getAt( index );
	    				break;
	    			}
	    		}
	    	}
    	}
    	else {
    		r =  ExtXeo.data.GroupingStore.superclass.getAt.call( this, index );
    	}
    	return r;
    },
    indexOfId : function(id){
    	if( this.groupField ) {
	    	var r;
	    	var f = false;
	    	var r = 0;
	    	for( var s=0;s<this.groupStores.length;s++ ) {
	    		var gs = this.groupStores[s];
	    		if( gs && gs.getCount ) {
	    			var x = gs.data.indexOfKey(id);
	    			if( x == -1 ) {
	    				r += gs.getCount();
	    			}
	    			else {
	    				r += x;
	    				f = true;
	    				break;
	    			}
	    		}
	    	}
	    	return f?r:-1;
    	}
    	else {
    		return ExtXeo.data.GroupingStore.superclass.indexOfId.call( this, id );
    	}
    },
    getCount : function() {
    	/*
    	var c=0;
    	debugger;
    	for( var s=0;s<this.groupStores.length;s++ ) {
    		var gs = this.groupStores[s];
    		if( gs && gs.getCount ) {
    			c += gs.getCount(); 
    		}
    	}
    	if( c==0 ) {
    		// Se for a root devolve o numero total dos items da root... ainda não tem filhos
    		// Codigo a verificar 
    		c = ExtXeo.data.GroupingStore.superclass.getCount.call(this);
    	}
    	return c;
    	*/
    	return 9999;
    },
    groupBy : function(field, forceRegroup){
        if(this.groupField == field && !forceRegroup){
            return; 
        }
        this.groupField = field;
        if(this.remoteGroup){
            if(!this.baseParams){
                this.baseParams = {};
            }
            this.baseParams['groupBy'] = field;
        }
        this.reload();
    },
    applyGrouping : function(alwaysFireChange){
        if(this.groupField !== false){
            this.groupBy(this.groupField, true);
            return true;
        }else{
            if(alwaysFireChange === true){
                this.fireEvent('datachanged', this);
            }
            return false;
        }
    },
    load : function( options ) {
    	options = options || {};
    	options.params = options.params || {};
    	if( this.groupField && this.groupField != "" )
    		options.params.groupBy = this.groupField;	
    	
		var x = ExtXeo.data.GroupingStore.superclass.load.call(this,options);
		var c = this.getCount();
		for( var i=0;i<c;i++ ) {
			this.groupStores[i] = null;
		}
		return x;
    },
    getGroupState : function(){
        return this.groupOnSort && this.groupField !== false ?
               (this.sortInfo ? this.sortInfo.field : undefined) : this.groupField;
    },
    getRange : function(s,e){
		return ExtXeo.data.GroupingStore.superclass.getRange.call(this,s,e);
    },
    loadGroup : function( callback, idx,groupValue, start, limit ) {
    	this.callback = callback;
    	// To avoid multiple loads of the same data.
    	if( this.groupStores[ idx ] && this.groupStores[ idx ].loading ) {
    		return;
    	}
    	
    	var fitems = this.fields.items;
    	var cf = [];
    	for(var i=0;i<fitems.length; i++ ) {
    		var nf = fitems[i];
    		cf[i] = { name: nf.name, type: nf.type };
    	}

    	var ss = this.getSortState();
    	var p = { groupValue: groupValue };
    	if( ss && ss.field && ss.field.length > 0 ) {
    		p.sort = ss.field;
    		p.dir   = ss.direction;
    	}
    	if( this.groupField && this.groupField != "" )
    		p.groupBy = this.groupField;
    	
    	if( this.lastOptions && this.lastOptions.params.filters )
    		p.filters = this.lastOptions.params.filters;
    		
    	var c = Ext.apply({},{ groupValue: groupValue }, this.reader.meta );
    	
    	c.fields = cf;
    	var gs = new Ext.data.JsonStore( c );
    	gs.baseParams = Ext.apply({},p, this.baseParams);
    	gs.load( {
    		params : Ext.apply({},this.baseParams, {start:start,limit:limit} ),
    		callback : function() { this.groupLoaded(idx) },
    		scope : this
    	} );
    	gs.loading = true;
    	this.groupStores[ idx ] = gs;
    	return gs;
    },
    groupLoaded : function( idx ) {
    	var g = this.groupStores[ idx ];
    	// Groups arrays was reset... maybe because a refresh... cancel callback event
    	if( g != null ) {
	    	g.loading = false;
	    	this.callback( idx, g  );
    	}
    }
});

