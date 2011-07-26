package netgest.bo.xwc.components.classic.grid;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.TYPE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.INPUT;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import netgest.bo.xwc.components.classic.ActionButton;
import netgest.bo.xwc.components.classic.GridNavBar;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Layouts;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.classic.theme.ExtJsTheme;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridPanelExtJsRenderer extends XUIRenderer  {

    private ExtConfigArray oExtButtons;
    private ExtConfigArray oExtToolbar;
    
    @Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
        this.oExtButtons = null;
        this.oExtToolbar = null;
        super.encodeBegin(component);
    }
    
    public ExtConfig extEncodeAll( XUIComponentBase oComp ) throws IOException {

        this.oExtButtons = null;
        this.oExtToolbar = null;
        
        encodeChildren( oComp );
        encodeGridHiddenInputs( oComp );
        return renderExtComponent( getResponseWriter(), oComp );

    }


    @Override
	public void encodeChildren( XUIComponentBase oComp ) throws IOException {
        Iterator<UIComponent> oChildIterator;
        UIComponent           oChildComp;
        oChildIterator = oComp.getChildren().iterator();
        while( oChildIterator.hasNext() ) {
            oChildComp = oChildIterator.next();

            if( oChildComp instanceof ActionButton ) {

            if (!oChildComp.isRendered()) {
                    return;
            }            

                String rendererType;
                rendererType = oChildComp.getRendererType();
                if (rendererType != null) {
                    Renderer renderer = getRenderer( oChildComp, XUIRequestContext.getCurrentContext().getFacesContext() );
                    if (renderer != null) {
                        if( this.oExtButtons == null ) {
                            oExtButtons = new ExtConfigArray();
                        }
                        oExtButtons.addChild(
                            ((ExtJsRenderer)renderer).getExtJsConfig( (XUIComponentBase)oChildComp )
                        );
                    }
                }
            }
            if( oChildComp instanceof ToolBar ) {
                if (!oChildComp.isRendered()) {
                    return;
                }
                
                if( ((ToolBar) oChildComp).isRenderedOnClient() ) {
                	ScriptBuilder sb = ToolBar.XEOHTMLRenderer.updateMenuItems( (ToolBar) oChildComp );
                	ToolBar.XEOHTMLRenderer.generateToolBarUpdateScript(sb, (ToolBar) oChildComp );
                    if( sb.length() > 0 ) {
                    	getResponseWriter().getScriptContext().add( 
                    			XUIScriptContext.POSITION_FOOTER, 
                    			((ToolBar)oChildComp).getClientId(), 
                    			sb.toString()
                    	);
                    }
                }
                else {
	                String rendererType;
	                rendererType = oChildComp.getRendererType();
	                if (rendererType != null) {
	                    Renderer renderer = getRenderer( oChildComp, XUIRequestContext.getCurrentContext().getFacesContext() );
	                    if (renderer != null) {
	                        if( this.oExtToolbar == null ) {
	                            oExtToolbar = new ExtConfigArray();
	                        }
	                        oExtToolbar.addChild(
	                            ((ExtJsRenderer)renderer).getExtJsConfig( (XUIComponentBase)oChildComp )
	                        );
	                        
	                    }
	                }
	                ((ToolBar)oChildComp).setRenderedOnClient( true );
                }
            }
        }
    }
    
    @Override
    public void encodeEnd(XUIComponentBase oComp) throws IOException {
        ExtConfig oGridConfig;
        if( oComp.isRendered() ) {
        
            XUIResponseWriter w = getResponseWriter();
        
            GridPanel oGrid = (GridPanel)oComp;
            // Add the scripts...
            addGridScripts( w );

            // Place holder for the component
        
            if( oComp.isRenderedOnClient() ) {
            	if( oGrid.getEnableColumnFilter() ) {
            		oGrid.getStateProperty("currentFilters").wasChanged();
            		ScriptBuilder scriptBuilder = new ScriptBuilder();
            		scriptBuilder.startBlock();
            		scriptBuilder.w( "var g=Ext.getCmp('").w( oGrid.getClientId() ).w( "');" );
            		scriptBuilder.w( "if( g && g.plugins ) {");
            		scriptBuilder.w( "g.plugins.updateFilters( ").w( oGrid.getCurrentFilters()).w( " );" );
            		scriptBuilder.w( "}" );
            		scriptBuilder.endBlock();
                	w.getScriptContext().add( XUIScriptContext.POSITION_HEADER , oGrid.getClientId() + "_filters" , scriptBuilder.toString() );
            	}

                if( oGrid.getAutoReloadData() || oGrid.isMarkedToReloadData() ) {
            		triggerLoadData( w, oGrid );
            	}

            }
            else {
	            oGridConfig = renderExtComponent( getResponseWriter(), oComp );

	            oGridConfig.setVarName( oGrid.getId() );
	
	            w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
	                oComp.getClass().getName() + ":" + oComp.getId(),
	                "Ext.onReady( function(){" + oGridConfig.renderExtConfig(  ) + ";" +
	                oGrid.getId()+".render('" + JavaScriptUtils.safeJavaScriptWrite( oGrid.getClientId(), '\'') + "');" + 		                
	                "});"
	            );

	            w.startElement( DIV, oComp );
	            w.writeAttribute( ID, oComp.getClientId(), null );
	            encodeGridHiddenInputs( oComp );
	            w.endElement( DIV ); 
	            
	            if( "fit-parent".equalsIgnoreCase( oGrid.getLayout() ) ) {
	            	Layouts.registerComponent( w, oComp, Layouts.LAYOUT_FIT_PARENT);
	            }
            }
        }
    }

    public void encodeGridHiddenInputs( XUIComponentBase oComp ) throws IOException {
        XUIResponseWriter w;
        
        w = getResponseWriter();
        
        w.startElement( INPUT , oComp);
        w.writeAttribute( TYPE, "hidden", null );
        w.writeAttribute( NAME, oComp.getClientId() +"_srs", null );
        w.writeAttribute( ID, oComp.getClientId() +"_srs", null );
        w.endElement( INPUT );

        w.startElement( INPUT , oComp);
        w.writeAttribute( TYPE, "hidden", null );
        w.writeAttribute( NAME, oComp.getClientId() +"_act", null );
        w.writeAttribute( ID, oComp.getClientId() +"_act", null );
        w.endElement( INPUT );
        
    }

	public ExtConfig renderExtComponent( XUIResponseWriter w, XUIComponentBase oComp ) {
        
        GridPanel           oGrid;
        
        ExtConfig           oStoreConfig;
        ExtConfig           oGridConfig;
        ExtConfig           oSelModelConfig;
        
        oGrid = (GridPanel)oComp;
        
        // Build DataStore Config
        oStoreConfig = buildDataStore( oGrid );
        oStoreConfig.setPublic( true );
        oStoreConfig.setVarName( oComp.getId() + "_store" );
        w.getScriptContext().add(
                XUIScriptContext.POSITION_HEADER,
                oComp.getId() + "_store",
                oStoreConfig.renderExtConfig()
            );

        // Build Selection Model
        oSelModelConfig = buildColumnSelectModel( oGrid );
        oSelModelConfig.setVarName( oGrid.getId() + "_sm" );
        w.getScriptContext().add(
                XUIScriptContext.POSITION_HEADER,
                oComp.getId() + "_sm",
                oSelModelConfig.renderExtConfig()
            );
        
        
        // Build column filters
        if( oGrid.getEnableColumnFilter() ) {
            ExtConfig oFiltersConfig = buildFiltersConfig( oGrid );
            oFiltersConfig.setVarName( oComp.getId() + "_filters" );
            w.getScriptContext().add(
                    XUIScriptContext.POSITION_HEADER,
                    oComp.getId() + "_filters",
                    oFiltersConfig.renderExtConfig()
                );
        }
        
        // Build navigation bottom toolbar
        ExtConfig oNavBarConfig = buildNavBar( oGrid );
        oNavBarConfig.setVarName( oGrid.getId() + "_nbar" );
        w.getScriptContext().add(
                XUIScriptContext.POSITION_HEADER,
                oComp.getId() + "_nbar",
                oNavBarConfig.renderExtConfig()
            );

        // BuildGrid
        oGridConfig = buildGrid( oGrid );
        
        triggerLoadData( w, oGrid );
        
        return oGridConfig;
    }
    
    public void triggerLoadData( XUIResponseWriter w, GridPanel oGrid ) {
        // Trigger load grid data
        ExtConfig oLoad = new ExtConfig();
        ExtConfig oLoadParams = oLoad.addChild("params");
        
        oLoadParams.add( "'javax.faces.ViewState'" ,"'" + XUIRequestContext.getCurrentContext().getViewRoot().getViewState() + "'" );
        oLoadParams.addJSString( "'xvw.servlet'" , oGrid.getClientId() );

        oLoadParams.add( "start" , 0 );
        oLoadParams.add( "limit" , oGrid.getPageSize() );
        
        ExtConfig oLoadParams1 = oLoadParams.addChild( "params" );
        if( oGrid.getGroupBy() != null ) {
        	oLoadParams1.addJSString( "groupBy" , oGrid.getGroupBy() );
        }

        StringBuilder sb = new StringBuilder();
        if( GridPanel.SELECTION_CELL.equals( oGrid.getRowSelectionMode() ) ) {
        	DataRecordConnector d = oGrid.getActiveRow();
        	if( d != null ) {
		        oLoadParams.add("callback", 
		        		"function(){" +
			    			oGrid.getId() + "_sm.suspendEvents(false);" +
			    			oGrid.getId() + "_sm.selectRow(" + (d.getRowIndex()-1) + ");" + 
			    			oGrid.getId() + "_sm.resumeEvents();"
		    			+ "}"
		        );
        	}
        }
        else {
	    	int[] selPos = oGrid.getSelectedRowsPos();
	    	sb.append( "[");
	    	for( int sel : selPos ) {
	    		if( sb.length() > 1 )
	    			sb.append(',');
	    		sb.append( sel - 1 );
	    	}
	    	sb.append( "]");
	    	if( sb.length() > 2 ) {
		        oLoadParams.add("callback", 
		        		"function(){" +
			    			oGrid.getId() + "_sm.suspendEvents(false);" +
			    			oGrid.getId() + "_sm.selectRows(" + sb + ");" + 
			    			oGrid.getId() + "_sm.resumeEvents();"
		    			+ "}"
		        );
	    	}
        }
        
    	w.getScriptContext().add(
                XUIScriptContext.POSITION_HEADER,
                oGrid.getId() + "_loadData",
                oGrid.getId() + "_store.load(" + oLoadParams.renderExtConfig().toString() + ");"
            );
    }
    
    public ExtConfig buildNavBar( GridPanel oGrid ) {

        ExtConfig oPagingConfig = new ExtConfig();

        oPagingConfig.setComponentType( "ExtXeo.PagingToolbar" );
        oPagingConfig.add( "store", oGrid.getId() + "_store" );
        oPagingConfig.add( "pageSize", oGrid.getPageSize() );
        oPagingConfig.add( "displayInfo", true );
        oPagingConfig.addJSString("displayMsg", "" );
        oPagingConfig.addJSString("emptyMsg", "" );
        
        GridNavBar oNavBarComp = (GridNavBar)oGrid.findComponent( GridNavBar.class );
        
        ExtConfigArray oPagingItems = oPagingConfig.addChildArray("items");
        if( oNavBarComp == null || oNavBarComp.getShowExportToExcel() ) {
        oPagingItems.add( "'-'" );
        {
            String sActionUrl = getRequestContext().getAjaxURL();
            ExtConfig oChild = oPagingItems.addChild();
            oChild.addJSString("text", "Excel");
            String sPar = "javax.faces.ViewState=" + XUIRequestContext.getCurrentContext().getViewRoot().getViewState();
            if( sActionUrl.indexOf("?") == -1 ) {
            	sActionUrl += "?" + sPar;
            }
            else {
            	sActionUrl += "&" + sPar;
            }
            sPar = "xvw.servlet=" + oGrid.getClientId();
        	sActionUrl += "&" + sPar;
            sPar = "type=excel";
        	sActionUrl += "&" + sPar;
            StringBuilder sb = new StringBuilder(100);
            sb.append( "function(){" );
            sb.append(     		"		XVW.downloadFile('" + sActionUrl + "');");
            sb.append( "}" );
            oChild.add( "handler", sb.toString() );
        }
        }
        if( oNavBarComp == null || oNavBarComp.getShowExportToPDF() ) {
        oPagingItems.add( "'-'" );
        {
            String sActionUrl = getRequestContext().getAjaxURL();
            ExtConfig oChild = oPagingItems.addChild();
            oChild.addJSString("text", "PDF");
            
            String sPar = "javax.faces.ViewState=" + XUIRequestContext.getCurrentContext().getViewRoot().getViewState();

            if( sActionUrl.indexOf("?") == -1 ) {
            	sActionUrl += "?" + sPar;
            }
            else {
            	sActionUrl += "&" + sPar;
            }
            
            sPar = "xvw.servlet=" + oGrid.getClientId();
        	sActionUrl += "&" + sPar;
            sPar = "type=pdf";
        	sActionUrl += "&" + sPar;
            StringBuilder sb = new StringBuilder(100);
            sb.append( "function(){" );
            sb.append( "window.open('" );
            sb.append( sActionUrl );
            sb.append( "');" );
            sb.append( "}" );
            oChild.add( "handler", sb.toString() );
        }
        }
        
        if( oNavBarComp != null ) {
        	for(UIComponent child : oNavBarComp.getChildren() ) {
                ExtConfig oChild = oPagingItems.addChild();
                Menu oMenu = (Menu)child;
            	ToolBar.XEOHTMLRenderer.configExtMenu( this, oNavBarComp, oMenu, oChild );
        	}
        }
        
        if( oNavBarComp == null || oNavBarComp.getShowFullTextSearch() ) {
	        if( (oGrid.getDataSource().dataListCapabilities() & DataListConnector.CAP_FULLTEXTSEARCH) != 0 ) {
	            oPagingItems.addChild("Ext.form.Label" ).addJSString( "text", ComponentMessages.GRID_FREE_SEARCH.toString() );
	            ExtConfig oSearchField = oPagingItems.addChild("Ext.form.TwinTriggerField" );
	            oSearchField.add("hideTrigger1", false);
	            oSearchField.add("hideTrigger1", false);
	            oSearchField.addJSString("trigger1Class", "x-form-clear-trigger");
	            oSearchField.addJSString("trigger2Class", "x-form-search-trigger");
	            oSearchField.add("onTrigger2Click", "function(){ " +
	            			oGrid.getId() + "_store.baseParams['fullText'] = this.getValue();\n" +
	            			oGrid.getId() + "_store.load();\n" +
	            		"}"
	            );
	            oSearchField.add("onTrigger1Click", "function(){ " +
	        			"this.setValue('');\n" +
	        			oGrid.getId() + "_store.baseParams['fullText'] = this.getValue();\n" +
	        			oGrid.getId() + "_store.load();\n" +
	        		"}"
	            );
	        }
        }
        return oPagingConfig;

    
    }

    public ExtConfig buildDataStore( GridPanel oGrid ) {
        ExtConfig oDataStoreConfig;
        ExtConfig oFieldConfig;
        ExtConfigArray  oFieldsConfig;
        XUIRequestContext oRequestContext;
        
        oRequestContext = XUIRequestContext.getCurrentContext();

        String[] oGridColumns;
        oGridColumns = oGrid.getDataColumns();

        String actionURL = XUIRequestContext.getCurrentContext().getAjaxURL();
        
        if( oGrid.getEnableGroupBy() ) {
        	oDataStoreConfig = new ExtConfig("ExtXeo.data.GroupingStore");
        }
        else {
            oDataStoreConfig = new ExtConfig("Ext.data.GroupingStore");
        }
        
        oFieldsConfig = oDataStoreConfig.addChildArray( "fields" );
        for (int i = 0; i < oGridColumns.length; i++) {
            oFieldConfig = oFieldsConfig.addChild();
            oFieldConfig.addJSString( "name", oGridColumns[i].replaceAll("\\.", "__") );
        }
        
        
        oDataStoreConfig.add("reader","new Ext.data.JsonReader({remoteSort:true, url:'"+actionURL+"',root:'" + oGrid.getId() + "',totalProperty:'totalCount'}," 
        		+ oFieldsConfig.renderExtConfig() + ")");
        
        ExtConfig proxy = oDataStoreConfig.addChild("proxy");
        proxy.setComponentType("Ext.data.HttpProxy");
        proxy.addJSString( "url", actionURL );
        
        
        
        oDataStoreConfig.add( "remoteGroup", true );
        String sGroupBy = oGrid.getGroupBy();
        if( sGroupBy != null ) {
        	oDataStoreConfig.addJSString( "groupField", sGroupBy );
        }
        
        oDataStoreConfig.add( "remoteSort", Boolean.TRUE );
                    
        ExtConfig oBaseParamsConfig = new ExtConfig();
        oBaseParamsConfig.add( "'javax.faces.ViewState'" ,"'" + oRequestContext.getViewRoot().getViewState() + "'" );
        oBaseParamsConfig.addJSString( "'xvw.servlet'" , oGrid.getClientId() );
        
        oDataStoreConfig.add( "baseParams" ,  oBaseParamsConfig );

        ExtConfig oExtListeners = oDataStoreConfig.addChild( "listeners" );
        ExtConfig oSelChange = oExtListeners.addChild( "'beforeload'");
        oSelChange.add( "fn", "" +
        		"function() { " +
        		"	var bp={}; " +
        		"	if(!this.baseParams['xvw.servlet'])" +
        		" 		this.baseParams = " + oBaseParamsConfig.renderExtConfig() + ";\n" +
        		"	" +
        		"	var x;" +
        		"	x=document.getElementById('" + oGrid.getClientId() + "_srs');\n" +
        		"	if( x ) this.baseParams.selectedRows = x.value;\n" +
        		"	x=document.getElementById('" + oGrid.getClientId() + "_act');\n" +
        		"	if( x ) this.baseParams.activeRow = x.value;\n" +
        		"	x=Ext.ComponentMgr.get('" + oGrid.getClientId() + "');\n" +
        		"	if( x ) this.baseParams.visibleColumns = x.getVisibleColumns();\n" +
        		"}" );

        ExtConfig oSelLoad = oExtListeners.addChild( "'load'");
        oSelLoad.add( "fn", "function() { \n" +
        		"var ogrid = '" + oGrid.getId() + "_sm';\n" + 
        		//"window.setTimeout(ogrid+'.selectFirst();alert(1);',1000);\n" + 
        		"}");
        
        
        ExtConfig oSelException = oExtListeners.addChild( "'loadexception'");
        oSelException.add( "fn", "function() { alert('" + ComponentMessages.GRID_ERROR_LOADING_DATA.toString() + "\\n ' + " +
        		"arguments[2].responseText" + 
        		"   );}" );

        
        return oDataStoreConfig;
    }


    public ExtConfig buildColumnSelectModel( GridPanel oGrid ) {
        String sRowSelectionMode;
        ExtConfig oSelModelConfig = new ExtConfig( "Ext.grid.CheckboxSelectionModel" );

        sRowSelectionMode = oGrid.getRowSelectionMode();

        oSelModelConfig.add( "singleSelect", !GridPanel.SELECTION_MULTI_ROW.equals( sRowSelectionMode ) );

        ExtConfig oExtListeners = oSelModelConfig.addChild( "listeners" );
        ExtConfig oSelChange = oExtListeners.addChild( "'selectionchange'");
        
        
        String rowSelChangeCode = "";
        XUICommand oSelChangeComp = (XUICommand)oGrid.findComponent( oGrid.getId() + "_selChange" );
        if( oSelChangeComp != null )
        {
        	rowSelChangeCode += XVWScripts.getCommandScript( 
                		"self",
                		oSelChangeComp, 
                		XVWScripts.WAIT_STATUS_MESSAGE 
                	);
        }
        
        oSelChange.add( "fn",
                            "function(oSelModel){ " +
                                "ExtXeo.grid.rowSelectionHndlr(" +
                                    "oSelModel," +
                                    "'" + oGrid.getClientId() +"_srs'," +
                                    "'" + oGrid.getRowUniqueIdentifier() + "'" +
                                "); " +
                                rowSelChangeCode +
                            "}" 
                        );
        
        return oSelModelConfig;
    }

    public ExtConfig buildGrid( GridPanel oGrid ) {
        Column[] oGridColumns;
        ExtConfig oGridConfig;
        ExtConfig oColConfig;
        
        oGridConfig = new ExtConfig( "ExtXeo.grid.GridPanel" );

        oGridConfig.add( "store", oGrid.getId() + "_store" );
        
        DataListConnector dataList = oGrid.getDataSource();
        
        ExtConfigArray oColsConfig = oGridConfig.addChildArray( "columns" );
            
        
        String sRowSelectionMode;
        
        sRowSelectionMode = oGrid.getRowSelectionMode();
        oGridColumns = oGrid.getColumns();
        if(  GridPanel.SELECTION_ROW.equals( sRowSelectionMode ) || GridPanel.SELECTION_MULTI_ROW.equals( sRowSelectionMode ) ) {
            oColsConfig.add( oGrid.getId() + "_sm" );
        }

        boolean validExpandColumn = false;
        boolean gridIsSortable    = oGrid.getEnableColumnSort();

        String  autoExpadColumn = null;
        autoExpadColumn = oGrid.getAutoExpandColumn();
        if( !(oGrid.getAutoExpandColumn() != null && oGrid.getAutoExpandColumn().length() > 0 )) {
        	autoExpadColumn = null;
        }
        for (int i = 0; i < oGridColumns.length; i++) {
            oColConfig = oColsConfig.addChild();
            oColConfig.addJSString( "id", oGridColumns[ i ].getDataField() );
            oColConfig.add( "width", oGridColumns[ i ].getWidth() );
            
            String label = GridPanel.getColumnLabel( dataList, oGridColumns[ i ] );
            
            oColConfig.addJSString( "header", label );
            oColConfig.addJSString( "dataIndex", oGridColumns[ i ].getDataField().replaceAll("\\.", "__") );
            oColConfig.add( "resizable" , oGridColumns[ i ].isResizable() );
            oColConfig.add( "hideable" , oGridColumns[ i ].isHideable() );
            oColConfig.add( "hidden" , oGridColumns[ i ].isHidden() );
            oColConfig.add( "groupable", oGridColumns[ i ].isGroupable() );
            
            if( gridIsSortable )
            	oColConfig.add( "sortable", oGridColumns[ i ].isSortable() );
            else
            	oColConfig.add( "sortable", false );
            
            if(  oGridColumns[i].getDataField().equals( autoExpadColumn )  ) {
            	validExpandColumn = true;
            }
        }
        
        if( autoExpadColumn != null && validExpandColumn ) {
            oGridConfig.addJSString( "autoExpandColumn", oGrid.getAutoExpandColumn() );
        }
        
        oGridConfig.addJSString( "id", oGrid.getClientId() );
        oGridConfig.add( "stripeRows", true );
        oGridConfig.add( "height", oGrid.getHeight() );
        oGridConfig.add( "autoHeight", oGrid.getAutoHeight() );
        oGridConfig.add( "minHeight", oGrid.getMinHeight());
        
        if( oGrid.getTitle() != null )
        	oGridConfig.addJSString( "title", oGrid.getTitle() );
        
        oGridConfig.add( "frame", false );
        oGridConfig.add( "loadMask", "(Ext.isIE?false:new Ext.LoadMask(Ext.get('" + oGrid.getClientId() + "'), {msg:'" + ComponentMessages.GRID_REFRESHING_DATA.toString() + "'}))" );
        oGridConfig.add( "maskDisabled", "(Ext.isIE?false:true)" );

        oGridConfig.addJSString( "region", oGrid.getRegion() );
        
        if( !oGrid.getEnableColumnHide() )
            oGridConfig.add( "enableColumnHide", false );
        if( !oGrid.getEnableColumnMove() )
            oGridConfig.add( "enableColumnMove", false );
        if( !oGrid.getEnableColumnResize() )
            oGridConfig.add( "enableColumnResize", false );
        if( !oGrid.getEnableHeaderMenu() )
            oGridConfig.add( "enableHdMenu", false );

        /*
        new Ext.grid.GroupingView({
            forceFit:oGrid.getForceColumnsFitWidth(),
            groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
        })
        */
        
        if( oGrid.getEnableGroupBy() ) {
            ExtConfig oView =  oGridConfig.addChild( "view" );
        	oView.setComponentType( "ExtXeo.grid.GroupingView" );
            oView.addJSString("groupTextTpl", "{text}" );
            oView.add("startCollapsed", true );
            oView.add("forceFit", oGrid.getForceColumnsFitWidth() );
            oView.add("getRowClass", "function(record, index){ return record.json['__rc']; }" );
        }
        else {
            ExtConfig oView =  oGridConfig.addChild( "view" );
            oView.setComponentType( "ExtXeo.grid.GridView" );
            oView.add("forceFit", oGrid.getForceColumnsFitWidth() );
            oView.add("showPreview", true );
            oView.add("getRowClass", "function(record, index){ return record.json['__rc']; }" );
        }
        //oGridConfig.addJSString("layout", "fit");
        
        if( this.oExtButtons != null ) {
            oGridConfig.add( "buttons", this.oExtButtons );    
        }

        if( this.oExtToolbar != null ) {
            oGridConfig.add( "tbar", this.oExtToolbar );    
        }
        
        ExtConfig oGridListeners = oGridConfig.addChild( "listeners" );

        String rowClickCode = "";
        XUICommand oRowClickComp = (XUICommand)oGrid.findComponent( oGrid.getId() + "_rowClick" );
        if( oRowClickComp != null )
        {
        		rowClickCode += XVWScripts.getCommandScript( 
                		oGrid.getRowClickTarget(),
                		oRowClickComp, 
                		oGrid.getServerActionWaitMode().ordinal()
                	);
        }
        
        oGridListeners.addChild("'rowclick'")
            .add(
                "fn",
                "function( oGrid, rowIndex, oEvent ) {" +
                "   ExtXeo.grid.rowClickHndlr( oGrid, rowIndex, oEvent, '" + oGrid.getClientId() +"_act" + "', '" + oGrid.getRowUniqueIdentifier() + "' );" +
                rowClickCode +
                "}"
            );

        oGridListeners.addChild("'destroy'")
        .add(
            "fn",
            "function( oGrid ) {" +
            "	" + oGrid.getId() + "_store = null;\n" +
            "	" + oGrid.getId() + "_sm = null;\n" +
            "	" + oGrid.getId() + "_nbar.destroy();\n" +
            "	" + oGrid.getId() + "_nbar = null;\n" +
            "	" + oGrid.getId() + "_filters = null;\n" +
            "}"
        );
        
        XUICommand oRowDblClickComp = (XUICommand)oGrid.findComponent( oGrid.getId() + "_rowDblClick" );
        if( oRowDblClickComp != null )
        {
            String targetName = 
            	"edit_'+arguments[0].getStore().getAt( arguments[1] ).get(\"" + 
            	oGrid.getRowUniqueIdentifier() + "\")+'";
        	
            oGridListeners.addChild("'rowdblclick'")
            .add(
                    "fn","function(){" + 
                    XVWScripts.getCommandScript( 
                    		oGrid.getRowDblClickTarget(),
                    		targetName,
                    		oRowDblClickComp, 
                    		oGrid.getServerActionWaitMode().ordinal()
                    	) 
                    +"}"
            );
        } 

        if(  GridPanel.SELECTION_ROW.equals( sRowSelectionMode ) || GridPanel.SELECTION_MULTI_ROW.equals( sRowSelectionMode ) ) {
            oGridConfig.add( "sm", oGrid.getId() + "_sm" );
        }
        
        oGridConfig.add( "bbar", oGrid.getId() + "_nbar" );
        
        if( oGrid.getEnableColumnFilter() ) {
        	oGridConfig.add( "plugins", oGrid.getId() + "_filters" );
        }
        
        return oGridConfig;
    }
    
	
    public ExtConfig buildFiltersConfig( GridPanel oGrid ) {
        
        ExtConfig 		oFilterConfig 	= new ExtConfig( "Ext.grid.GridFilters" );
        oFilterConfig.addJSString("id", oGrid.getClientId() + "_filters" );
        
        
        ExtConfigArray 	oFiltersArray 	= oFilterConfig.addChildArray("filters");
        
        
        ExtConfig 		oExtFiltersChild;

    	String v = oGrid.getCurrentFilters();
    	
    	if( v == null || v.trim().length() == 0 ) {
    		v = "{}";
    	}
    	
    	JSONObject j = null;
    	try {
			j = new JSONObject( v );
        
            Column[] cols = oGrid.getColumns();
            for( Column col : cols ) {

            	JSONObject colFilter = j.optJSONObject( col.getDataField() );

            	if( colFilter == null ) {
            		colFilter = new JSONObject();
            		j.put( col.getDataField(), colFilter );
            		colFilter.put( "value" , (Object)null );
            		colFilter.put( "active" , false);
            	}
            	
            	DataFieldMetaData metaData = null;
            	
            	
            	if( oGrid.getDataSource() == null ) {
            		throw new RuntimeException( ComponentMessages.GRID_DATASOURCE_IS_NULL.toString() );
            	}
				metaData = oGrid.getDataSource().getAttributeMetaData( col.getDataField() );
				
				String dataField = col.getDataField();

				oExtFiltersChild = oFiltersArray.addChild();
            	oExtFiltersChild.addJSString( "dataIndex", dataField.replaceAll("\\.", "__") );
            	oExtFiltersChild.add( "active" , colFilter.getBoolean("active"));
            	oExtFiltersChild.add( "searchable", col.isSearchable() );
        		
            	JSONArray filters;
            	String	  filterValue;

        		if( metaData != null ) {
	            	
	            	if( metaData.getIsLov() ) {
	            		Map<Object,String> lovMap = metaData.getLovMap();
	            		if( lovMap.size() < 30 ) {
	                    	
	            			oExtFiltersChild.addJSString( "type", "list" );
	            			colFilter.put("type", "list");
	            			
	                    	ExtConfigArray valuesArray = oExtFiltersChild.addChildArray("options");
	                    	StringBuilder values = new StringBuilder();
	                    	boolean first = true;
	                    	for( Object key : lovMap.keySet() ) {
	                    		String desc = lovMap.get( key );
	                    		if( !first )
	                    			values.append(',');
	                    		
	                    		if( !(desc.length() == 0  && first) ) {
	                    			ExtConfig c = valuesArray.addChild();
	                    			c.addJSString("id", String.valueOf( key ));
	                    			c.addJSString("text", desc);
		                    		first = false;
	                    		}
	                    	}
	                    	
	            			filters = colFilter.optJSONArray("filters");
	            			if( filters != null && filters.length() > 0 ) {
		            			String value = filters.getJSONObject(0).getJSONArray("value").toString();
		            			oExtFiltersChild.add("value", value );
	            			}
	                    	
	                    	
	            		}
	            	}
	            	else if( metaData.getInputRenderType() == DataFieldTypes.RENDER_OBJECT_LOOKUP ) {
	                	oExtFiltersChild.addJSString( "type", "object" );
            			colFilter.put("type", "object");
	                	oExtFiltersChild.addJSString( "lookupInputName", oGrid.getFilterLookupInput().getClientId() );
	                	oExtFiltersChild.add( "lookupCommand", 	            		
	                			"function(){ " +
	                				XVWScripts.getAjaxCommandScript( oGrid.getFilterLookupCommand(),col.getDataField(),XVWScripts.WAIT_DIALOG ) +
	                			"}"
	                	);
	            	}
	            	else {
	            		
	            		
		            	switch( metaData.getDataType() ) {
		            		case DataFieldTypes.VALUE_CHAR:
		            		case DataFieldTypes.VALUE_BLOB:
		            		case DataFieldTypes.VALUE_CLOB:
		                    	oExtFiltersChild.addJSString( "type", "string" );
		            			colFilter.put("type", "string");
		            			filterValue = colFilter.optString("value");
		            			if( filterValue != null ) {
		                			oExtFiltersChild.addString("value", filterValue );
		            			}
		                    	break;
		            		case DataFieldTypes.VALUE_BOOLEAN:
		                    	oExtFiltersChild.addJSString( "type", "boolean" );
		            			colFilter.put("type", "boolean");
		            			filters = colFilter.optJSONArray("filters");
		            			if( filters != null && filters.length() > 0 ) {
			            			filterValue = filters.getJSONObject(0).getString("value");
			            			oExtFiltersChild.add("value", filterValue );
		            			}
		                    	break;
		            		case DataFieldTypes.VALUE_DATETIME:
		            		case DataFieldTypes.VALUE_DATE:
		                    	oExtFiltersChild.addJSString( "type", "date" );
		            			colFilter.put("type", "date");
		            			filters = colFilter.optJSONArray("filters");
		            			if( filters != null && filters.length() > 0 ) {
		            				ExtConfig values = oExtFiltersChild.addChild("value");
		            				for( int i=0; i < filters.length(); i++ ) {
		            					JSONObject fltObj = filters.getJSONObject( i );
		            					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		            					Calendar c = Calendar.getInstance();
		            					try {
											c.setTime( sdf.parse( fltObj.optString("value") ) );
											values.add( 
													fltObj.optString("comparison"),
													"new Date(" + c.getTimeInMillis() + ")" 
												);
										} catch (ParseException e) {
										}
		            				}
		            			}
		                    	break;
		            		case DataFieldTypes.VALUE_NUMBER:
		                    	oExtFiltersChild.addJSString( "type", "numeric" );
		            			colFilter.put("type", "numeric");
		            			filters = colFilter.optJSONArray("filters");
		            			if( filters != null && filters.length() > 0 ) {
		            				ExtConfig values = oExtFiltersChild.addChild("value");
		            				for( int i=0; i < filters.length(); i++ ) {
		            					JSONObject fltObj = filters.getJSONObject( i );
		            					values.addJSString( fltObj.optString("comparison"), fltObj.optString("value") );
		            				}
		            			}
		                    	break;
		            	}
	            	}
            	}
            	else {
                	oExtFiltersChild.addJSString( "type", "string" );
        			colFilter.put("type", "string");
        			filterValue = colFilter.optString("value");
        			if( filterValue != null ) {
            			oExtFiltersChild.addString("value", filterValue );
        			}
            	}
            }
    	}
    	catch( JSONException e ) {
    		throw new RuntimeException( e );
    	}
    	oGrid.setCurrentFilters( j.toString() );
    	return oFilterConfig;
    }
    
    protected Renderer getRenderer(UIComponent oComp, FacesContext context) {

        String rendererType = oComp.getRendererType();
        Renderer result = null;
        if (rendererType != null) {
            result = context.getRenderKit().getRenderer(oComp.getFamily(),
                                                        rendererType);
        }            
        return result;
    }
    
    public void addGridScripts( XUIResponseWriter w ) {
    	XUIScriptContext scriptContext = w.getScriptContext();
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter", ExtJsTheme.composeUrl( "extjs/grid/GridFilters.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-boolean", ExtJsTheme.composeUrl( "extjs/grid/filter/BooleanFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-date", ExtJsTheme.composeUrl( "extjs/grid/filter/DateFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-filter", ExtJsTheme.composeUrl( "extjs/grid/filter/Filter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-list", ExtJsTheme.composeUrl( "extjs/grid/filter/ListFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-numeric", ExtJsTheme.composeUrl( "extjs/grid/filter/NumericFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-string", ExtJsTheme.composeUrl( "extjs/grid/filter/StringFilter.js" ) );
    }

    
	
}
