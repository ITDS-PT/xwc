package netgest.bo.xwc.components.classic.grid;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.TYPE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.INPUT;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
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
import netgest.bo.xwc.components.classic.theme.ExtJsTheme;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.SortTerms;
import netgest.bo.xwc.components.connectors.SortTerms.SortTerm;
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

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridPanelExtJsRenderer extends XUIRenderer  {

    private ExtConfigArray oExtButtons;
    private ExtConfigArray oExtToolbar;
    
    /**
     * Represents the Javascript empty array string length : '[]'
     */
    private static final int EMPTY_ARRAY_STRING_SIZE = 2;
    
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
        ExtConfig ret = renderExtComponent( getResponseWriter(), oComp, true );
        //encodeEnd(oComp);
        return ret;
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
            }
        }
    }
    
    private String getCodeClearSelections(GridPanel oGrid ){
    	ScriptBuilder scriptBuilder = new ScriptBuilder();
		scriptBuilder.startBlock();
		scriptBuilder.w( "Ext.getCmp('").w( oGrid.getClientId() ).w( "').reset();" );
		scriptBuilder.endBlock();
    	return scriptBuilder
    	.toString();
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
            	
        		if (oGrid.getClearSelections()){
                	w.getScriptContext().add( XUIScriptContext.POSITION_HEADER , 
                			oGrid.getClientId() + "_clearSelections" , getCodeClearSelections(oGrid) );
                	oGrid.maintainSelections();
                }
                
                

            }
            else {
	            oGridConfig = renderExtComponent( getResponseWriter(), oComp, false );

	            oGridConfig.setVarName( oGrid.getId() );
	
	            w.startElement( DIV, oComp );
	            w.writeAttribute( ID, oComp.getClientId(), null );
	            encodeGridHiddenInputs( oComp );
	            w.endElement( DIV );
	            
	            triggerLoadData(w, oGrid);
	            
	            String loadMaskVar = oGrid.getId()+".loadMask";	            
	            w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
	                oComp.getClass().getName() + ":" + oComp.getId(),
	                "/*Ext.onReady( function()*/{" + oGridConfig.renderExtConfig(  ) + ";" +
	                oGrid.getId()+".render('" + JavaScriptUtils.safeJavaScriptWrite( oGrid.getClientId(), '\'') + "');\n" +
	                "if( " + loadMaskVar +") { " + loadMaskVar + ".xwc_wtout = window.setTimeout('Ext.getCmp(\"" + oGrid.getClientId() +"\").loadMask.onBeforeLoad();',1000);}\n" + 		                
	                "}/*);*/"
	            );
	            
	            
	            if( "fit-parent".equalsIgnoreCase( oGrid.getLayout() ) ) {
	            	Layouts.registerComponent( w, oComp, Layouts.LAYOUT_FIT_PARENT);
	            }
            }
        }
    }

    public void encodeGridHiddenInputs( XUIComponentBase oComp ) throws IOException {
        XUIResponseWriter w;
        
        w = getResponseWriter();
        
        //Selected Rows
        w.startElement( INPUT , oComp);
        w.writeAttribute( TYPE, "hidden", null );
        w.writeAttribute( NAME, oComp.getClientId() +"_srs", null );
        w.writeAttribute( ID, oComp.getClientId() +"_srs", null );
        w.endElement( INPUT );

        //Active Row
        w.startElement( INPUT , oComp);
        w.writeAttribute( TYPE, "hidden", null );
        w.writeAttribute( NAME, oComp.getClientId() +"_act", null );
        w.writeAttribute( ID, oComp.getClientId() +"_act", null );
        w.endElement( INPUT );
        
    }

	public ExtConfig renderExtComponent( XUIResponseWriter w, XUIComponentBase oComp, boolean triggerLoadData ) {
        
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
        oSelModelConfig.setVarName( oGrid.getId() + "_selm" );
        w.getScriptContext().add(
                XUIScriptContext.POSITION_HEADER,
                oComp.getId() + "_selm",
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
        
        if( triggerLoadData )
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
        	String[] groupByElements = oGrid.getGroupBy().split(",");
        	ExtConfigArray c = oLoadParams1.addChildArray( "groupBy" );
        	for( String g : groupByElements ) {
        		c.addString( g.replaceAll("\\.", "__") );
        	}
        	oLoadParams1.add( "groupByLevel", 0 );
        	//oLoadParams1.addJSString( "groupField" , oGrid.getGroupBy() );
        }

        StringBuilder sb = new StringBuilder();
        
        //Clears selections by demand or when the Grid is grouped, because groups start collapsed
        boolean clearSelections = oGrid.getClearSelections() || 
        			(netgest.utils.StringUtils.hasValue( oGrid.getGroupBy() ) && oGrid.getEnableGroupBy() );
        
        if( GridPanel.SELECTION_CELL.equals( oGrid.getRowSelectionMode() ) ) {
        	DataRecordConnector d = oGrid.getActiveRow();
        	if( d != null  && !clearSelections) {
		        oLoadParams.add("callback", 
		        		"function(){" +
			    			oGrid.getId() + "_selm.suspendEvents(false);" +
			    			oGrid.getId() + "_selm.clearSelections();" +
			    			oGrid.getId() + "_selm.selectRow(" + (d.getRowIndex()-1) + ");" + 
			    			oGrid.getId() + "_selm.resumeEvents();"
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
	    	
	    	//Prevent row selection if selections are to be cleared
	    	//by the GridPanel.reset() function 
	    	if (clearSelections)
	    		sb = new StringBuilder("");
	    	
	    	if( sb.length() > EMPTY_ARRAY_STRING_SIZE) {
	    		//Select rows on datatore load event callback
		        oLoadParams.add("callback", 
		        		"function(){" +
			    			oGrid.getId() + "_selm.suspendEvents(false);" +
			    			oGrid.getId() + "_selm.clearSelections();" +
			    			oGrid.getId() + "_selm.selectRows(" + sb + ");" + 
			    			oGrid.getId() + "_selm.resumeEvents();"
		    			+ "}"
		        );
	    	}
        }
        
        w.getScriptContext().add(
                XUIScriptContext.POSITION_FOOTER,
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
        if( oNavBarComp == null || oNavBarComp.getShowExportToPDF() ) 
        {
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
	            oChild.add( "handler", "function() {XVW.downloadFile('" + sActionUrl + "')}" );
	        }
        }
        oPagingItems.add( "'-'" );
        if( oNavBarComp != null ) {
        	for(UIComponent child : oNavBarComp.getChildren() ) {
                ExtConfig oChild = oPagingItems.addChild();
                Menu oMenu = (Menu)child;
            	ToolBar.XEOHTMLRenderer.configExtMenu( this, oNavBarComp, oMenu, oChild );
        	}
        }
        
        if( oNavBarComp == null || oNavBarComp.getShowFullTextSearch() ) {
	        if( (oGrid.getDataSource().dataListCapabilities() & DataListConnector.CAP_FULLTEXTSEARCH) != 0 ) {
	            oPagingItems.addChild("Ext.form.ToolBarLabel" ).addJSString( "text", ComponentMessages.GRID_FREE_SEARCH.toString() );
	            ExtConfig oSearchField = oPagingItems.addChild("Ext.form.TwinTriggerField" );
	            
	            oSearchField.add("hideTrigger1", false);
	            
	            String searchCode =	
	            		"var value = this.getValue(); " +
	            		" if (value && value.length == 1 && value == '%'){" + 
	            		" value = ''; this.setValue(''); " +
	            		" } " + 
	            		oGrid.getId() + "_store.baseParams['fullText'] = value;\n" +
    					oGrid.getId() + "_store.load();\n";
 
	            
	            ExtConfig extCfg = oSearchField.addChild("listeners");
	            extCfg.add("specialkey", 
	            		"function(comp,e) {if(e.getKey()==e.RETURN){" + searchCode + "}}"
	            );
	            
	            oSearchField.add("hideTrigger1", false);
	            oSearchField.add("hideTrigger1", false);
	            oSearchField.addJSString("trigger1Class", "x-form-clear-trigger");
	            oSearchField.addJSString("trigger2Class", "x-form-search-trigger");
	            oSearchField.add("onTrigger2Click", "function(){ " + searchCode + "}"
	            );
	            oSearchField.add("onTrigger1Click", "function(){ " +
	        			"this.setValue('');\n" + 
	        			 searchCode + 
	        			"}"
	            );
	        }
        }
        
        //Always have the counter if we have multi-selected
        if (oGrid.isMultiSelection()){
        	oPagingItems.add( "' '" );
        	oPagingItems.add( "' '" );
        	ExtConfig selections = oPagingItems.addChild("Ext.form.ToolBarLabel" );
            selections.addJSString( "text", "0 " );
            selections.addJSString( "id", oGrid.getClientId() + "_selections" );
        }
        
        if (oGrid.getMaxSelections() > 0){
        	oPagingItems.add( "' '" );
            ExtConfig labelMaxSelection = oPagingItems.addChild("Ext.form.ToolBarLabel" );
            labelMaxSelection.addJSString( "text", " / "  + oGrid.getMaxSelections() +  " selec." );
        } else if (oGrid.isMultiSelection()){
        	oPagingItems.add( "' '" );
        	ExtConfig labelNumSelected = oPagingItems.addChild("Ext.form.ToolBarLabel" );
        	labelNumSelected.addJSString( "text", " selec." );
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
        
//        if( oGrid.getEnableGroupBy() ) {
//        	oDataStoreConfig = new ExtConfig("ExtXeo.data.GroupingStore");
//        }
//        else {
        oDataStoreConfig = new ExtConfig("ExtXeo.data.GroupingStore");
//        }
        
        oFieldsConfig = oDataStoreConfig.addChildArray( "fields" );
        for (int i = 0; i < oGridColumns.length; i++) {
            oFieldConfig = oFieldsConfig.addChild();
            oFieldConfig.addJSString( "name", oGridColumns[i].replaceAll("\\.", "__") );
        }
        
        
        oDataStoreConfig.addJSString( "url", actionURL );
        oDataStoreConfig.add("reader","new Ext.data.JsonReader({remoteSort:true, url:'"+actionURL+"',root:'" + oGrid.getId() + "',totalProperty:'totalCount'}," 
        		+ oFieldsConfig.renderExtConfig() + ")");
        
        ExtConfig proxy = oDataStoreConfig.addChild("proxy");
        proxy.setComponentType("Ext.data.HttpProxy");
        proxy.addJSString( "url", actionURL );
        
        
        
        oDataStoreConfig.add( "remoteGroup", true );
        String sGroupBy = oGrid.getGroupBy();
        boolean hasGroupBy = false;
        if( sGroupBy != null ) {
        	String[] groupByElements = oGrid.getGroupBy().split(",");
        	ExtConfigArray c = oDataStoreConfig.addChildArray( "groupField" );
        	for( String g : groupByElements ) {
        		if( g.trim().length() > 0 ) {
        			c.addString( g.replaceAll("\\.", "__") );
        			hasGroupBy = true;
        		}
        	}
        }
        oDataStoreConfig.add( "remoteSort", Boolean.TRUE );
        
		if( hasGroupBy ) {
	        String expandedGroups = oGrid.getCurrentExpandedGroups();
	        if (!StringUtils.isEmpty(expandedGroups))
	        	oDataStoreConfig.add( "expandedGroups", expandedGroups );
		} else {
			oGrid.setCurrentExpandedGroups(null);
		}
        
        SortTerms sortTerms = oGrid.getCurrentSortTerms();
        if( sortTerms != null ) {
        	ExtConfig c = oDataStoreConfig.addChild( "sortInfo" );
        	ExtConfigArray fields = c.addChildArray( "field" );
        	ExtConfigArray fieldsDir = c.addChildArray( "direction" );
        	
        	Iterator<SortTerm> itSort = sortTerms.iterator();
        	while( itSort.hasNext() ) {
        		 SortTerm sortTerm = itSort.next();
        		 fields.addString( sortTerm.getField() );
        		 fieldsDir.addString( sortTerm.getDirection()==1?"ASC" : "DESC" );
        	}
        }
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
        		"	if( x ) this.baseParams.selectedRows = x.value; \n" +
        		"	x=document.getElementById('" + oGrid.getClientId() + "_act');\n" +
        		"	if( x ) this.baseParams.activeRow = x.value;\n" +
        		"}" );

        /*
        ExtConfig oSelLoad = oExtListeners.addChild( "'load'");
        oSelLoad.add( "fn", "function() { \n" +
        		"var ogrid = '" + oGrid.getId() + "_sm';\n" + 
        		//"window.setTimeout(ogrid+'.selectFirst();alert(1);',1000);\n" + 
        		"}");
        */
        
        ExtConfig oSelException = oExtListeners.addChild( "'loadexception'");
        oSelException.add( "fn", "function() { debugger; alert('" + ComponentMessages.GRID_ERROR_LOADING_DATA.toString() + "\\n ' + " +
        		"arguments[2].responseText" + 
        		"   );}" );

        
        ExtConfig onLoad = oExtListeners.addChild( "'load'");
        onLoad.add( "fn", 
                "function() {" +
                "	var c = Ext.getCmp(\"" + oGrid.getClientId() +"\");\n" +
                "	if( c && c.loadMask ) { " +
                "		if( c.loadMask.xwc_wtout ) {window.clearTimeout(c.loadMask.xwc_wtout);}\n" + 		                
                "		c.loadMask.onLoad();\n" +
        		"	} " +
        		"}" );
        
/*        
        onLoad.add( "fn", 
        "function() {" +
        "var c = Ext.getCmp(\"" + oGrid.getClientId() +"\");\n" +
        "if( c && c.loadMask && c.loadMask.xwc_wtout ) { window.clearTimeout(c.loadMask.xwc_wtout); c.loadMask.onLoad(); }\n" + 		                
//        "if( " + loadMaskVar +") {" +loadMaskVar +".onLoad();}\n" + 		                
		" }" );
        */
        return oDataStoreConfig;
    }

    public ExtConfig buildColumnSelectModel( GridPanel oGrid ) {
        String sRowSelectionMode;

        ExtConfig oSelModelConfig;
        
        sRowSelectionMode = oGrid.getRowSelectionMode();
        if (GridPanel.SELECTION_ROW.equals( sRowSelectionMode ) || GridPanel.SELECTION_MULTI_ROW.equals( sRowSelectionMode )) { 
        	oSelModelConfig = new ExtConfig( "Ext.grid.CheckboxSelectionModel" );
        }
        else {
        	oSelModelConfig = new ExtConfig( "Ext.grid.RowSelectionModel" );
        }
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
                            "function(oSelModel){" +
                                "ExtXeo.grid.rowSelectionHndlr(" +
                                    "oSelModel," +
                                    "'" + oGrid.getClientId() +"_srs'," +
                                    "'" + oGrid.getRowUniqueIdentifier() + "'" +
                                ");\n" +
                                "ExtXeo.grid.activeRowHndlr(" + 
                                    "oSelModel," +
	                                "'" + oGrid.getClientId() +"_act'," +
	                                "'" + oGrid.getRowUniqueIdentifier() + "'" +
	                            ");\n" +
                                rowSelChangeCode +
                            "}" 
                        );
        
        if ( oGrid.getMaxSelections() > 0 || 
        		oGrid.isMultiSelection() ){
        	//We only need these handlers to prevent selection of items that 
        	//cardinality restriction
	        ExtConfig oBeforeRowSelect = oExtListeners.addChild( "'beforerowselect'");
	        oBeforeRowSelect.add( "fn",
	                "function( selModel, rowIndex, keepExisting, record){" +
	                    "return ExtXeo.grid.beforeRowSelectionHndlr(selModel);}\n" );
        }
        return oSelModelConfig;
    }
    
    public ExtConfig buildGrid( GridPanel oGrid ) {
        final Column[] oGridColumns;
        ExtConfig oGridConfig;
        ExtConfig oColConfig;
        
        oGridConfig = new ExtConfig( "ExtXeo.grid.GridPanel" );

        oGridConfig.add( "store", oGrid.getId() + "_store" );
        oGridConfig.add( "maxSelections", oGrid.getMaxSelections() );
        oGridConfig.add( "toolBarVisible", oGrid.getShowGroupToolBar() );
        oGridConfig.add( "multiPagePreserve", oGrid.getEnableSelectionAcrossPages() );
        if (oGrid.isMultiSelection())
        	oGridConfig.add( "multiSelection", true );
        
        
        DataListConnector dataList = oGrid.getDataSource();
        
        ExtConfig oColumnModel = oGridConfig.addChild("colModel");
        oColumnModel.setComponentType( "Ext.grid.ColumnModel" );
        
        ExtConfigArray oColsConfig = oColumnModel.addChildArray( "columns" );
        oGridColumns = oGrid.getColumns();
        
        boolean validExpandColumn = false;
        boolean gridIsSortable    	= oGrid.getEnableColumnSort();
        boolean gridAllowColumnMove 	= oGrid.getEnableColumnMove();
        boolean gridAllowColumnResize 	= oGrid.getEnableColumnResize();
        boolean gridAllowColumnHide		= oGrid.getEnableColumnHide();
        
        boolean needsSort = false;
        String sColumnsConfig = oGrid.getCurrentColumnsConfig();
        final Map<String,JSONObject> map = new HashMap<String, JSONObject>();
        if( sColumnsConfig != null && sColumnsConfig.length() > 0 ) {
        	try {
		        JSONArray columnsConfig = new JSONArray(sColumnsConfig);
		        for( int i=0;i<columnsConfig.length(); i++ ) {
					JSONObject colCfg = columnsConfig.getJSONObject(i);
					if( gridAllowColumnMove && !needsSort && colCfg.has( "position" ) ) {
						needsSort = true;
					}
					map.put( colCfg.optString("dataField"), colCfg );
		        }
			} catch (JSONException e) {
				e.printStackTrace();
			}
	        
	        if( needsSort ) {
	        	Arrays.sort( oGridColumns, new Comparator<Column>() {
	        		@Override
	        		public int compare(Column left, Column right) {
	        			Integer posLeft;
	        			Integer posRight;
	        			JSONObject j;
	        			
	        			j = map.get( left.getDataField() );
	        			if( j != null && j.has("position") )
	        				posLeft = j.optInt( "position" );
	        			else
	        				posLeft = getOriginalIndex( left );

	        			j = map.get( right.getDataField() );
	        			if( j != null && j.has("position") )
	        				posRight = j.optInt( "position" );
	        			else
	        				posRight = getOriginalIndex( left );
	        			
	        			return posLeft.compareTo( posRight );
	        			
	        		}
	        		private int getOriginalIndex( Column c ) {
	        			for (int i = 0; i < oGridColumns.length; i++) {
							if (oGridColumns[i]==c)
								return i+1;
						}
	        			return oGridColumns.length+1;
	        		}
				});
	        }
	        
        }
        
        if( oGrid.isMultiSelection() ){  
        	oColsConfig.add( oGrid.getId() + "_selm" );
        }

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
            if (oGridColumns[ i ].wrapText()){
            	oColConfig.add( "renderer" , "ExtXeo.grid.nowrap" );
            }

			JSONObject colCfg = map.get( oGridColumns[ i ].getDataField() );
			if( colCfg != null ) {
				if( gridAllowColumnResize && oGridColumns[i].isResizable() && colCfg.has("width") )
					oColConfig.add("width", colCfg.optInt("width"));
				if( gridAllowColumnHide && oGridColumns[i].isHideable() && colCfg.has("hidden") ) {
					boolean hidden = colCfg.optBoolean("hidden");
					oGridColumns[i].setHidden( Boolean.toString( hidden ) );
				}
			}
            
            oColConfig.add( "hidden" , oGridColumns[ i ].isHidden() );
            oColConfig.add( "groupable", oGridColumns[ i ].isGroupable() );
            
            
        	// BEGIN ML: 19-09-2011 - Number Type Columns/And aggregate Enable
			DataFieldMetaData metaData = null;

			String dataField = oGridColumns[i].getDataField();
			
			metaData = oGrid.getDataSource().getAttributeMetaData( dataField );

			if (metaData != null) {
				if (metaData.getDataType() == DataFieldTypes.VALUE_NUMBER
						&& !metaData.getIsLov()
						&& !(metaData.getInputRenderType() == DataFieldTypes.RENDER_OBJECT_LOOKUP)
						&& oGridColumns[i].isEnableAggregate()
						&& ((oGrid.getDataSource().dataListCapabilities() & DataListConnector.CAP_AGGREGABLE) > 0)) {
					oColConfig.add("aggregate", true);
				} else {
					oColConfig.add("aggregate", false);
				}
			}
			// END ML: 19-09-2011 - Number Type Columns
            
            
            if (!"".equalsIgnoreCase(oGridColumns[ i ].getAlign()))
            	oColConfig.addJSString( "align" , oGridColumns[ i ].getAlign() );
            oColConfig.add( "stateful", false );
            oColConfig.addJSString( "tooltip", oGridColumns[ i ].getLabel() );
            
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
        //oGridConfig.add( "loadMask", "(Ext.isIE?false:new Ext.LoadMask(Ext.get('" + oGrid.getClientId() + "'), {msg:'" + ComponentMessages.GRID_REFRESHING_DATA.toString() + "'}))" );
        //oGridConfig.add( "maskDisabled", "(Ext.isIE?false:true)" );
        oGridConfig.add( "maskDisabled", false );
        oGridConfig.add( "loadMask", "new Ext.LoadMask(Ext.get('" + oGrid.getClientId() + "')!=null?Ext.get('" + oGrid.getClientId() + "'):document.body, {msg:'" + ComponentMessages.GRID_REFRESHING_DATA.toString() + "'})" );        

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
        
        ExtConfig oView;
        if( oGrid.getEnableGroupBy() ) {
            oView =  oGridConfig.addChild( "view" );
        	oView.setComponentType( "ExtXeo.grid.GroupingView" );
            oView.addJSString("groupTextTpl", "{text}" );
            oView.add("startCollapsed", true );
            oView.add("forceFit", oGrid.getForceColumnsFitWidth() );
            oView.add("getRowClass", "function(record, index){ return record.json['__rc']; }" );
        }
        else {
            oView =  oGridConfig.addChild( "view" );
            oView.setComponentType( "ExtXeo.grid.GridView" );
            oView.add("forceFit", oGrid.getForceColumnsFitWidth() );
            oView.add("showPreview", true );
            oView.add("getRowClass", "function(record, index){ return record.json['__rc']; }" );
        }
        
        oView.add("enableAggregate", oGrid.getEnableAggregate() );
        
        /** ML - 10-10-2011 **/
        oGrid.setAggregateData(oGrid.getAggregateFieldsString());
    	StringBuffer result = new StringBuffer();
    	result.append("{\"SVALS\":[");
        if(oGrid.getAggregateData() != null &&  !"".equalsIgnoreCase(oGrid.getAggregateData()))
        {        	
        	boolean first = true;
    			
			String[] aggregateSplit = oGrid.getAggregateData().split(";");
			for(int i = 0; i < aggregateSplit.length; i++)
			{
				String[] aggregateSplitNext = aggregateSplit[i].split("=");
				
				String key = aggregateSplitNext[0];
				
				String tempdetail = aggregateSplitNext[1];
				tempdetail = tempdetail.replace("[", "");
				tempdetail = tempdetail.replace("]", "");
				tempdetail = tempdetail.replaceAll(" ","");
				
				String[] aggregateDetailSplit = tempdetail.split(",");
				
				for(int j = 0; j < aggregateDetailSplit.length; j++)
				{
					if(!first)
					{
						result.append(",");
					}
					result.append("{\"VALUEAGG\":\"" + aggregateDetailSplit[j] + ":" + key + "\"}");
					first = false;
				}
			}    	
        }        
    	result.append("]}");   
        oView.add("aggregateState", result.toString());
        /** END ML - 10-10-2011 **/
        
        oView.add("onSelColumns", "function() { " + XVWScripts.getCommandScript("self", oGrid.getSelectColumnsCommand(), XVWScripts.WAIT_DIALOG ) + " }" );
        oView.add("onResetDefaults", "function() { " + XVWScripts.getCommandScript("self", oGrid.getResetDefaultsCommand(), XVWScripts.WAIT_DIALOG ) + " }" );
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
        if( oRowClickComp != null && oGrid.getOnRowClick() != null)
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
  //              "   ExtXeo.grid.rowClickHndlr( oGrid, rowIndex, oEvent, '" + oGrid.getClientId() +"_act" + "', '" + oGrid.getRowUniqueIdentifier() + "' );" +
                rowClickCode +
                "}"
            );

        oGridListeners.addChild("'destroy'")
        .add(
            "fn",
            "function( oGrid ) {" +
            "	" + oGrid.getId() + "_store = null;\n" +
            "	" + oGrid.getId() + "_selm = null;\n" +
            "	" + oGrid.getId() + "_sm = null;\n" +
            "	" + oGrid.getId() + "_nbar.destroy();\n" +
            "	" + oGrid.getId() + "_nbar = null;\n" +
            "	" + oGrid.getId() + "_filters = null;\n" +
            " if (oGrid.getTopToolbar()) " + 
            "	oGrid.getTopToolbar().destroy();\n" +
            
            "}"
        );
        
        oGridListeners.add("statesave", "function(comp, state) {var id = comp.getStateId(); if(id) Ext.state.Manager.clear(id); }" );
        oGridListeners.add("beforestaterestore", "function() {return false;}" );
        oGridListeners.add("beforestatesave", "function() {return false;}" );
        oGridListeners.add("columnmove", "function(idx, n) { this.onColumnConfigChange('moved',idx, n ) }" );
        oGridListeners.add("columnresize", "function(idx, n) { this.onColumnConfigChange('width',idx,n ) }" );
        if (oGrid.getEnableGroupBy())
        	oGridListeners.add("beforedestroy", "function(grid) {return ExtXeo.grid.destroyGroupDDSupport(grid);}" );
        
        XUICommand oRowDblClickComp = (XUICommand)oGrid.findComponent( oGrid.getId() + "_rowDblClick" );
        if( oRowDblClickComp != null && oGrid.getOnRowDoubleClick() != null )
        {
            String targetName = 
            	"edit_'+arguments[0].getStore().getAt( arguments[1] ).get(\"" + 
            	oGrid.getRowUniqueIdentifier() + "\")+'";
        	
            String dblTarget = oGrid.getRowDblClickTarget();
            
            oGridListeners.addChild("'rowdblclick'")
            .add(
                    "fn","function(){" + 
                    XVWScripts.getCommandScript( 
                    		dblTarget,
                    		targetName,
                    		oRowDblClickComp, 
                    		"self".equals( dblTarget )?oGrid.getServerActionWaitMode().ordinal():XVWScripts.WAIT_STATUS_MESSAGE
                    	) 
                    +"}"
            );
        } 

        oGridConfig.add( "sm", oGrid.getId() + "_selm" );
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
            DataListConnector connector = oGrid.getDataSource();
            for( Column col : cols ) {

            	JSONObject colFilter = j.optJSONObject( col.getDataField() );

            	if( colFilter == null ) {
            		colFilter = new JSONObject();
            		j.put( col.getDataField(), colFilter );
            		colFilter.put( "value" , (Object)null );
            		colFilter.put( "active" , false);
            	}
            	
            	DataFieldMetaData metaData = null;
            	
            	if( connector == null ) {
            		throw new RuntimeException( ComponentMessages.GRID_DATASOURCE_IS_NULL.toString() );
            	}
				metaData = connector.getAttributeMetaData( col.getDataField() );
				
				String dataField = col.getDataField();

				oExtFiltersChild = oFiltersArray.addChild();
            	oExtFiltersChild.addJSString( "dataIndex", dataField.replaceAll( "\\.", "__" ) );
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
	                    			if (col.useValueOnLov())
	                    				c.addJSString("id", String.valueOf( key ));
	                    			else
	                    				c.addJSString("id", desc );
	                    			c.addJSString("text", desc);
		                    		first = false;
	                    		}
	                    	}
	                    	
	            			filters = colFilter.optJSONArray("filters");
	            			if( filters != null && filters.length() > 0 ) {
		            			String value = filters.getJSONObject(0).getJSONArray("value").toString();
		            			oExtFiltersChild.add("value", value );
	            			} else {
	            				filters = colFilter.optJSONArray("value");
	            				if( filters != null && filters.length() > 0 ) {
	            					String value = filters.getString( 0 );
			            			oExtFiltersChild.addJSString( "value", value );
	            				}
	            			}
	                    	
	            			
	            		} else if (metaData.getDataType() == DataFieldTypes.VALUE_NUMBER){
	            			oExtFiltersChild.addJSString( "type", "object" );
	            			colFilter.put("type", "object");
		                	oExtFiltersChild.addJSString( "lookupInputName", oGrid.getFilterLookupInput().getClientId() );
		                	oExtFiltersChild.add( "lookupCommand", 	            		
		                			"function(){ " +
		                				XVWScripts.getAjaxCommandScript( oGrid.getFilterLookupCommand(),col.getDataField(),XVWScripts.WAIT_DIALOG ) +
		                			"}"
		                	);	
	            		} else { //Really big lovs
	            			oExtFiltersChild.addJSString( "type", "string" );
	            			colFilter.put("type", "string");
	            			
	            			ExtConfigArray valuesArray = oExtFiltersChild.addChildArray("options");
	            			
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
		            			Object booleanFilter = colFilter.opt( "value" );
		            			if( booleanFilter != null ) {
			            			oExtFiltersChild.add("value", booleanFilter );
		            			}
		                    	break;
		            		case DataFieldTypes.VALUE_DATETIME:
		            		case DataFieldTypes.VALUE_DATE:
		                    	oExtFiltersChild.addJSString( "type", "date" );
		            			colFilter.put("type", "date");
		            			filters = colFilter.optJSONArray("value");
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
		            			filters = colFilter.optJSONArray("value");
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
        
        if (oComp instanceof ToolBar)
        	return new ToolBar.XEOHTMLRenderer();
        
        return result;
        //Coloquei isto aqui porque senão ia tentar renderizar
        //a toolbar do Grid com a ToolBar Jquery e o GridPanel n está feito para isso.
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
