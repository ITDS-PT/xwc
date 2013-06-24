package netgest.bo.xwc.components.classic.grid;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLAttr.TYPE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.INPUT;
import static netgest.bo.xwc.components.HTMLTag.TABLE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Layouts;
import netgest.bo.xwc.components.classic.grid.GridPanelJSonRenderer.JsonFormat;
import netgest.bo.xwc.components.classic.grid.GridPanelJqueryRenderer.GridOptionsBuilder.ColumnModelBuilder;
import netgest.bo.xwc.components.classic.grid.GridPanelJqueryRenderer.GridOptionsBuilder.ColumnModelBuilder.EditOptions;
import netgest.bo.xwc.components.classic.grid.GridPanelJqueryRenderer.GridOptionsBuilder.GroupingViewBuilder;
import netgest.bo.xwc.components.classic.grid.jquery.GridPanelParametersDecoder;
import netgest.bo.xwc.components.classic.grid.jquery.JSFunction;
import netgest.bo.xwc.components.classic.renderers.jquery.JQueryBaseRenderer;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.GroupableDataList;
import netgest.bo.xwc.components.data.JavaScriptArrayProvider;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.template.util.CustomTemplateRenderer;
import netgest.bo.xwc.components.util.ComponentRenderUtils;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.components.ColumnAttribute;
import netgest.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridPanelJqueryRenderer extends JQueryBaseRenderer implements XUIRendererServlet {
	
	private static final int START_INDEX = 0;



	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		GridPanel grid = (GridPanel) component;
		XUIResponseWriter w = getResponseWriter();
		encodeBegin( grid, w );
		
		Layouts.registerComponent( w , grid , Layouts.LAYOUT_FIT_PARENT );
	}
	
	
	public void encodeBegin(GridPanel grid , XUIResponseWriter w) throws IOException {
		
		w.startElement( DIV );
			w.writeAttribute( ID, grid.getClientId() );
			w.writeAttribute( STYLE, "width:auto" );
		
		createGridMarkup( grid, w );
		
		createGridHiddenFields( grid, w );
		
		createGridNavBarMarkup( grid, w );
		
		createFullTextSearchMarkup(grid, w);
		
		
		String script = createGridPanelJavascript(grid);
		addScriptFooter( "gridPanelRender_" + grid.getClientId(), script );
		
		String gridNavBar = createGridNavBarScript(grid);
		addScriptFooter( "gridPanelRender_" + grid.getClientId() + "_nav", gridNavBar );
		
		String groupHeaders = addGroupHeaders(grid);
		if (StringUtils.hasValue( groupHeaders ))
			addScriptFooter( "gridPanelRender_" + grid.getClientId() + "_headers", groupHeaders );
		
		String addNavBarButtons = addGridNavBarButtons(grid);
		addScriptFooter( "gridPanelRender_" + grid.getClientId() + "_btns", addNavBarButtons );
		
	}
	
	private void createGridHiddenFields(GridPanel grid, XUIResponseWriter w) throws IOException {

		w.startElement( INPUT , grid);
        w.writeAttribute( TYPE, "hidden", null );
        w.writeAttribute( NAME, getGridId(grid) +"_srs", null );
        w.writeAttribute( ID, getGridId(grid) +"_srs", null );
        w.endElement( INPUT );

        //Active Row
        w.startElement( INPUT , grid);
        w.writeAttribute( TYPE, "hidden", null );
        w.writeAttribute( NAME, getGridId(grid) +"_act", null );
        w.writeAttribute( ID, getGridId(grid) +"_act", null );
        w.endElement( INPUT );
		
	}


	@Override
	public void encodeChildren( XUIComponentBase oComp ) throws IOException {
		
	}
	
	@Override
    public boolean getRendersChildren() {
        return true;
    }


	private void createFullTextSearchMarkup( GridPanel grid, XUIResponseWriter w ) throws IOException  {
		w.startElement( DIV );
			w.writeAttribute( ID, grid.getClientId() + "_fullTxtSearch" );
			w.writeAttribute( STYLE, "display:none" );
			w.startElement( INPUT );
				w.writeAttribute( NAME, grid.getClientId() + "_fullTxtSearchInput" );
				w.writeAttribute( ID, grid.getClientId() + "_fullTxtSearchInput" );
			w.endElement( INPUT );
		w.endElement( DIV );
		
	}


	private String addGroupHeaders( GridPanel grid ) {
			
		GroupHeaderBuilder header = new GroupHeaderBuilder(grid);
		if (header.hasHeaders()){
			header.useColSpanStyle( false );
			
			StringBuilder b = new StringBuilder(100);
				b.append("jQuery(XVW.get('");
				b.append(getGridId( grid ));
				b.append("')).jqGrid('setGroupHeaders',");
				b.append(header.serialize());
				b.append(");");
			return b.toString();
		}
		return "";
	}


	private String addGridNavBarButtons( GridPanel grid ) {
		
		StringBuilder b = new StringBuilder(200);
		
		createClearFiltersButton( grid, b );	
		
		activateFilterToolBar( grid, b );
		activateFrozenColumns(grid, b);
			
		createExportExcelButton(grid, b);
		createExportPdfButton( grid, b );
		createFullTextSearchButton(grid, b);
		
		createColumnChooserButton(grid, b);
		
		return b.toString();
	}


	private void createFullTextSearchButton( GridPanel grid, StringBuilder b ) {
		b.append("jQuery(XVW.get('");
		b.append(getGridId( grid ));
		b.append("')).jqGrid('navButtonAdd',");
		b.append("'#");
		b.append(getEscapedNavBarId( grid ));
		b.append("',");
		b.append("{caption:'',title:'Fulltext Search', buttonicon :'ui-icon-zoomin', ");
		b.append(" onClickButton: function () { ");
		
		String buttons = "{ 'Pesquisar' : function () { var val = $(XVW.get('" + grid.getId() + "_fullTxtSearchInput')).val(); " +
				"jQuery(XVW.get('"+getGridId( grid )+"')).jqGrid('setGridParam',{ postData: { fullTxt: val} }).trigger('reloadGrid'); $( this ).dialog( \"close\" ); " +
		"} ,";
		buttons += "'Limpar' : function() { jQuery(XVW.get('"+getGridId(grid)+"')).jqGrid('setGridParam',{ postData: { fullTxt: ''} }).trigger('reloadGrid'); $( this ).dialog( \"close\" ); }, ";
		buttons += "'Cancelar' : function() { $( this ).dialog( \"close\" );} }";
		
		JQueryWidget w = WidgetFactory.createWidget( JQuery.WINDOW );
		w.selectorById( grid.getClientId()+"_fullTxtSearch" )
			.createAndStartOptions()
				.addOption( "width", 300 )
				.addOption( "height", 180 )
				.addOption( "modal", true )
				.addNonLiteral( "buttons", buttons)
			.endOptions()
			;
		String result = w.build();
				b.append( result );
			
	b.append(" } }); ");
	}


	private void createColumnChooserButton( GridPanel grid, StringBuilder b ) {
		b.append("jQuery(XVW.get('");
		b.append(getGridId( grid ));
		b.append("')).jqGrid('navButtonAdd',");
		b.append("'#");
		b.append(getEscapedNavBarId( grid ));
		b.append("',");
		b.append("{caption:'Columns',title:'Reorder Columns',");
		b.append(" onClickButton: function () { ");
				b.append( "jQuery(XVW.get('"+getGridId( grid )+"')).jqGrid('columnChooser');");
			
	b.append(" } }); ");
	
	}


	private void activateFrozenColumns( GridPanel grid, StringBuilder b ) {
		b.append("jQuery(XVW.get('");
		b.append(getGridId( grid ));
		b.append("')).jqGrid('setFrozenColumns');");
		
	}


	private void createExportExcelButton( GridPanel grid, StringBuilder b ) {
		b.append("jQuery(XVW.get('");
		b.append(getGridId( grid ));
		b.append("')).jqGrid('navButtonAdd',");
			b.append("'#");
			b.append(getEscapedNavBarId( grid ));
			b.append("',");
			b.append("{caption:'Excel',title:'Export Excel', buttonicon :'ui-icon-document',");
			b.append(" onClickButton: ");
				b.append( new GridPanelUtilities( grid ).getExcelDownloadScript());
				
		b.append(" }); ");
		
	}
	
	private void createExportPdfButton( GridPanel grid, StringBuilder b ) {
		b.append("jQuery(XVW.get('");
		b.append(getGridId( grid ));
		b.append("')).jqGrid('navButtonAdd',");
		b.append("'#");
		b.append(getEscapedNavBarId( grid ));
		b.append("',");
		b.append("{caption:'PDF',title:'Export PDF', buttonicon :'ui-icon-document',");
		b.append(" onClickButton: ");
		b.append( new GridPanelUtilities( grid ).getPdfDownloadScript());
		b.append(" }); ");
		
	}


	private void activateFilterToolBar( GridPanel grid, StringBuilder b ) {
		b.append("jQuery(XVW.get('");
		b.append(getGridId( grid ));
		b.append("')).jqGrid('filterToolbar');");
	}


	private void createClearFiltersButton( GridPanel grid, StringBuilder b ) {
		b.append("jQuery(XVW.get('");
		b.append(getGridId( grid ));
		b.append("')).jqGrid('navButtonAdd',");
			b.append("'#");
			b.append(getEscapedNavBarId( grid ));
			b.append("',");
			b.append("{caption:'',title:'Clear Search', buttonicon :'ui-icon-circle-close',");
			b.append(" onClickButton:function(){ ");
			b.append(" jQuery(XVW.get('" + getGridId(grid) + "')).jqGrid('filterToolbar')[0].clearToolbar(); ");
			b.append(" } ");
			b.append(" }); ");
	}


	private String createGridNavBarScript( GridPanel grid ) {
		return "jQuery(XVW.get('" +getGridId(grid)+"')).jqGrid('navGrid','#"+ getEscapedNavBarId( grid ) 
				+ "',{edit:false,add:false,del:false},{},{},{},{multipleSearch:true, multipleGroup:true});";
	}


	private void createGridNavBarMarkup( GridPanel grid, XUIResponseWriter w ) throws IOException {
		w.startElement( DIV );
			w.writeAttribute( ID, getNavBarId( grid ));
		w.endElement( DIV );
	}


	protected String getNavBarId( GridPanel grid ) {
		//return grid.getClientId() + "_navBar" ;
		return grid.getClientId().replaceAll( ":" , "_" ) + "_navBar";
	}
	
	protected String getEscapedNavBarId( GridPanel grid ) {
		//return JQueryBuilder.escapeJquerySelector( grid.getClientId()  ) + "_navBar";
		return grid.getClientId().replaceAll( ":" , "_" ) + "_navBar";
	}


	private void createGridMarkup( GridPanel grid, XUIResponseWriter w ) throws IOException {
		w.startElement( TABLE );
			w.writeAttribute( ID, getGridId(grid) );
		w.endElement( TABLE );
	}
	
	private String getGridId(GridPanel grid){
		return grid.getClientId( ) + "_table";
	}
	
	private String createGridPanelJavascript( GridPanel grid ) {
		GridOptionsBuilder b = new GridOptionsBuilder();
		DataListConnector connector = grid.getDataSource();
		
		
		b.dataType( "json" )
			.dataUrl( ComponentRenderUtils.getCompleteServletURL( grid.getRequestContext(), grid.getClientId() ) )
			.title( grid.getTitle() )
			.height( 250 )
			//.width( 800 )
			.autoWidth( true )
			.shrinkToFit( true )
			.columnReordering( true )
			.onDoubleClick( grid )
			.onSelectRow()
			.rowNum( Integer.parseInt( grid.getPageSize() ) )
			.pageEvent( grid )
			//.loadError()
			.sortName( "BOUI" )
			.pagerId( grid );
		
		//buildGroupingConfiguration( grid, b );
		
		setRowSelectionMode( grid, b );
				
		buildColumnDefinition( grid, b, connector );
		buildJsonReader(grid, b);
		
		String options = b.build();
		
		StringBuilder gridDefinition = new StringBuilder(options.length() + 100);
		gridDefinition.append("jQuery(XVW.get('").append(getGridId(grid)).append("')).jqGrid(").append(options).append(");");
		
		return gridDefinition.toString();
	}


	private void buildGroupingConfiguration( GridPanel grid, GridOptionsBuilder b ) {
		if (grid.getEnableGroupBy()){
			b.grouping( true );
			List<String> groups = new LinkedList<String>();
			
			for (Column col : grid.getColumns()){
				if (col.isGroupable()){
					groups.add( col.getDataField() );
				}
			}
			if (groups.size() > 0){
				GroupingViewBuilder builder = b.groupingView();
				for (String current : groups){
					builder.addGroupField( current );
				}
				builder.end();
			}
		}
	}


	private void setRowSelectionMode( GridPanel grid, GridOptionsBuilder b ) {
		if (GridPanel.SELECTION_MULTI_ROW.equalsIgnoreCase( grid.getRowSelectionMode() ) )
			b.multiSelect( true );
	}


	/**
	 * 
	 * Creates the JSON Reader to read the JSON from the ExtJS format to the jqGrid format)
	 * 
	 * @param grid
	 * @param b
	 */
	private void buildJsonReader( GridPanel grid, GridOptionsBuilder b ) {
		
		b.jsonReader()
			//.root( getGridId(grid) )
			.root(grid.getId())
			.repeatItems( false )
			.id( "0" ) //Id is the first element in the list
			.cell( "" ) //Don't put lines inside an array named "cell"
			.totalRecords( JsonFormat.TOTAL_RECORDS )
			.end();
		
	}


	protected void buildColumnDefinition( GridPanel grid, GridOptionsBuilder b, DataListConnector connector ) {
		ColumnModelBuilder columnBuilder = b.columnDefinition();
		Column[] columns = grid.getColumns();
		String[] columnNames = new String[columns.length]; 
		int k = 0;		
		for (Column column : columns){
			columnNames[k] = column.getDataField();
			columnBuilder.newColumn()
				.name( column.getDataField() )
				.index( column.getDataField() )
				.sortable( column.isSortable() )
				.hidden(column.isHidden( ))
				.align( column.getAlign() );
			
				if (isFrozen(column))
					columnBuilder.frozen( true );
				
				DataFieldMetaData metadata = connector.getAttributeMetaData( column.getDataField() );
				if (metadata != null){
					if (isColumnSelect(metadata)){
						columnBuilder.searchType( "select" );
						createSelectOptions(metadata, columnBuilder);
					}
					
					setSearchDataType(metadata, columnBuilder);
				}
				if (StringUtils.hasValue( column.getWidth() ))
					columnBuilder.width( Integer.valueOf( column.getWidth() ) );
			k++;
		}
		
		//Append the rest of the column definition
		columnBuilder.endColumns().columns( columnNames );
	}


	private void setSearchDataType( DataFieldMetaData metadata, ColumnModelBuilder builder) {
		
		switch (metadata.getDataType()){
			case DataFieldTypes.VALUE_NUMBER : builder.searchDataType( "integer" ); break;
			case DataFieldTypes.VALUE_DATE : builder.searchDataType( "date" ); break;
			case DataFieldTypes.VALUE_DATETIME : builder.searchDataType( "date" ); break;
			default : builder.searchDataType( "text" );
		}
		
	}


	private boolean isFrozen( Column column ) {
		if (column instanceof ColumnAttribute){
			return ((ColumnAttribute) column).getIsFrozen();
		}
		return false;
	}


	private void createSelectOptions( DataFieldMetaData attributeMetaData, ColumnModelBuilder columnBuilder ) {
		EditOptions opts = columnBuilder.editOptions();
		opts.defaultValue( "" );
		if (DataFieldTypes.VALUE_BOOLEAN == attributeMetaData.getDataType()){
			opts.value(  "" , "" );
			opts.value(  Boolean.TRUE.toString() , "Sim");
			opts.value(  Boolean.FALSE.toString(), "Não" );
		} else if (attributeMetaData.getIsLov()){
			Map<Object,String> map = attributeMetaData.getLovMap();
			Iterator<Object> keys = map.keySet().iterator();
			opts.value( "" , "" );
			while (keys.hasNext()){
				Object key = keys.next();
				String value = map.get( key );
				if (StringUtils.hasValue( value ) && StringUtils.hasValue( key.toString() ))
					opts.value( key.toString(), value );
			}
		}
		opts.end();
	}


	private boolean isColumnSelect( DataFieldMetaData attributeMetaData ) {
		if (DataFieldTypes.VALUE_BOOLEAN == attributeMetaData.getDataType()){
			return true;
		} else if (attributeMetaData.getIsLov()){
			return true;
		}
		return false;
	}


	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException{
		GridPanel grid = (GridPanel) component;
		XUIResponseWriter w = getResponseWriter();
		encodeEnd(grid,w);
	}
	
	public void encodeEnd(GridPanel grid, XUIResponseWriter w ) throws IOException {
		w.endElement( DIV );
	}
	
	@Override
	public void decode(XUIComponentBase component){
		
		GridPanel grid = (GridPanel) component;
		Map<String,String> requestParameters = getFacesContext().getExternalContext().getRequestParameterMap();
		decode( grid , requestParameters );
	}
	
	public void decode(GridPanel grid, Map<String,String> requestParameters){
		String sGridSelectedIds = null;
		sGridSelectedIds = requestParameters.get( getGridId( grid ) +"_srs" );
        if( sGridSelectedIds != null && sGridSelectedIds.length() > 0 ){
        	grid.setSelectedRowsByIdentifier( sGridSelectedIds.split("\\|") );
        }
        
        sGridSelectedIds = requestParameters.get( getGridId( grid ) +"_act" );
        if( sGridSelectedIds != null && sGridSelectedIds.length() > 0 ){
        	grid.setActiveRowByIdentifier( sGridSelectedIds );
        }
		
	}

	@Override
	public void service( ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp )
			throws IOException {
		String sType = oRequest.getParameter("type");
		GridPanel grid = (GridPanel) oComp;
        if( "pdf".equalsIgnoreCase( sType ) ) {
        	GridPanelPDFRenderer pdfRender = new GridPanelPDFRenderer();
        	pdfRender.render( oRequest , oResponse, grid );
        }
        else if ( "excel".equalsIgnoreCase( sType ) ) {
        	GridPanelExcelRenderer excelRenderer = new GridPanelExcelRenderer();
        	excelRenderer.getExcel( oRequest, oResponse, grid );
        }
        else {
        	WebRequest request = new HttpServletRequestWrapper( oRequest );
        	GridPanelParametersDecoder decoder = 
    				new GridPanelParametersDecoder( request, getColumsNamesGrid( grid ) );
        	WebResponse response = new HttpServletResponseWrapper( oResponse );
        	if (decoder.isGrouped())
        		serviceGroupData( request, response, grid, decoder );
        	else
        		serviceData( request, response, grid , decoder);
        	}
        }


	private void serviceGroupData( WebRequest request, WebResponse oResponse, GridPanel grid, GridPanelParametersDecoder decoder ) throws IOException {
		oResponse.setContentType( "text/plain;charset=utf-8" );
		
		GridPanelRequestParameters reqParam = null;
		DataListConnector dataSource = grid.getDataSource();
		
		grid.applySqlFields(dataSource);
		grid.applyFilters( dataSource );
		grid.applySort( dataSource );
		grid.applyFullTextSearch( dataSource );
		grid.applyAggregate( dataSource );
		
		
		List<String> groupBy = decoder.getGroups(); 
		int pageSize = Integer.parseInt(  grid.getPageSize() );
		//if( reqParam.getGroupByLevel() >= reqParam.getGroupBy().length ) {

			String[] groups = (String[])groupBy.toArray(new String[groupBy.size()]);
			DataListConnector groupDetails = 
				((GroupableDataList)dataSource).getGroupDetails(
						groups,
						new Object[0],
						groupBy.get( 0  ),
						decoder.getPage(), 
						pageSize 
					);

			GridPanelJSonRenderer jsonRenderer = new GridPanelJSonRenderer();
		    StringBuilder oStrBldr = jsonRenderer.buildDataArray( 
		    		grid, 
		    		groupDetails, 
		    		groupDetails.iterator(), 
		    		0, 
		    		pageSize,
		    		-1,
		    		reqParam
		    	);
		    oResponse.getWriter().print( oStrBldr );
		    
	}
		


	public void serviceData( WebRequest oRequest, WebResponse oResponse, GridPanel grid, GridPanelParametersDecoder decoder )
			throws IOException {
		
		oResponse.setContentType( "text/plain;charset=utf-8" );
		
		
		
		DataListConnector oDataCon = grid.getDataSource();
		
		grid.setGroupBy( null );
		grid.setCurrentSortTerms( decoder.getSortTermsInternalGridFormat() );
		String format = decoder.getFilterTermsInternalGridFormat( grid , oDataCon );
		grid.setCurrentFilters( format );
		String advancedFilters = decoder.getAdvancedSearchTermsAsJson(oDataCon);
		grid.setAdvancedFilters( advancedFilters );
		
		if (decoder.wasFullTextSearchExecuted()){
			String fullTextSearch = decoder.getFullText();
			grid.setCurrentFullTextSearch( fullTextSearch );
		}
		
		int currentPage = decoder.getPage();
		int pageSize = Integer.parseInt( grid.getPageSize() );
		
		oDataCon.setPageSize( pageSize ); 
        oDataCon.setPage( currentPage );
        
        
        Iterator<DataRecordConnector> dataIterator = GridPanelJSonRenderer.getDataListIterator(grid, oDataCon);
        
		JavaScriptArrayProvider dataProvider = new JavaScriptArrayProvider( 
        		dataIterator, 
        		getColumsNamesGrid( grid ), 
        		START_INDEX, 
        		pageSize
        );
		
		StringBuilder b = new StringBuilder(800);
		dataProvider.getJSONArray(b,grid,grid.getRowUniqueIdentifier(),null);
		
		StringBuilder s = new StringBuilder(b.length() + 100);
		
		s.append( "{ \"" );
        s.append( grid.getId() ); 
        s.append( "\" :" );
        s.append( b.toString() );
        
        double recordCount = oDataCon.getRecordCount();
        double pageSizeDouble = oDataCon.getPageSize();
        double pagesDouble = recordCount / pageSizeDouble;
        int pages = (int) Math.ceil( pagesDouble );
        
        s.append(",\"totalCount\":\"").append( oDataCon.getRecordCount() );
        s.append("\", \"page\" : \""+oDataCon.getPage()+"\" , \"total\" : \""+ pages +"\" ");
        s.append('}');
		
		oResponse.getWriter().write( s.toString() );
	}
	
	private String[] getColumsNamesGrid(GridPanel grid){
		Column[] columns = grid.getColumns();
		String[] fields = new String[columns.length];
		for (int i = 0 ; i < columns.length ; i++){
			fields[i] = columns[i].getDataField();
		}
		return fields;
	}
	
	
	
	
	/**
	 * 
	 * Builder for the Options of the GridPanel (using the jqGrid plugin)
	 * 
	 * @author PedroRio
	 *
	 */
	protected class GridOptionsBuilder{
		
		protected JSONObject options = new JSONObject();
		
		public GridOptionsBuilder(){
			
		}
		
		public GridOptionsBuilder onDoubleClick(GridPanel grid) {
			
			XUICommand oRowDblClickComp = (XUICommand)grid.findComponent( grid.getId() + "_rowDblClick" );
			UIComponent root = grid.getParent();
			String viewId = null;
			while ( root != null){
				if (root instanceof XUIViewRoot){
					viewId = ((XUIViewRoot) root).getClientId( );
					break;
				}
				root = root.getParent();
			}
			
			
			addOption( "ondblClickRow" , new JSFunction("function(rowId, iRow, iCol, e){ "+
					/*XVWScripts.getCommandScript( 
                    		grid.getRowDblClickTarget( ) ,
                    		"edit_" ,
                    		oRowDblClickComp , 
                    		XVWScripts.WAIT_STATUS_MESSAGE) + "} "
                    		)*/
                    	XVWScripts.getOpenCommandTab( oRowDblClickComp , viewId ) + "}"	
			));
			return this;
		}
		
		public GridOptionsBuilder onSelectRow(){
			addOption("onSelectRow", new JSFunction("function(rowId,status,e){ XVW.grid.onSelectRow(rowId,status,e,this)}"));
			return this;
		}

		public GridOptionsBuilder pageEvent(GridPanel grid) {
			Map<String,Object> ctx = new HashMap<String, Object>();
			ctx.put( "this", grid );
			addOption( "onPaging", new JSFunction( CustomTemplateRenderer.processTemplateFile( "templates/components/grid/gridBackButton.ftl", ctx ) ) );
			return this;
		}

		private void addOption(String key, Object value){
			try{
				this.options.put( key, value );
				
			} catch (JSONException e){
				e.printStackTrace();
			}
		}
		
		private void addArray(String key, Object[] value){
			try{
				this.options.put( key, new JSONArray( value ) );
			} catch (JSONException e){
				e.printStackTrace();
			}
		}
		
		public GridOptionsBuilder columns(String... columns){
			addArray( "colNames", columns );
			return this;
		}
		
		public GridOptionsBuilder shrinkToFit(boolean shrinkToFit){
			addOption( "shrinkToFit", shrinkToFit );
			return this;
		}
		
		public GridOptionsBuilder grouping(boolean grouping){
			addOption( "grouping", grouping );
			return this;
		}
		
		public GridOptionsBuilder dataType(String type){
			addOption( "datatype", type );
			return this;
		}
		
		
		
		public GridOptionsBuilder height(int height){
			addOption( "height", height );
			return this;
		}
		
		public GridOptionsBuilder autoWidth(boolean autoWidth){
			addOption( "autowidth", autoWidth );
			return this;
		}
		
		public GridOptionsBuilder width(int width){
			addOption( "width", width );
			return this;
		}
		
		public GridOptionsBuilder rowNum(int rowNum){
			addOption( "rowNum", rowNum );
			return this;
		}
		
		public GridOptionsBuilder loadError(){
			addOption("loadError", "function(xhr,st,err) { " + 
    " alert(st + '; Response: ' + xhr.status + ' ' +xhr.statusText); " +
    " }");
			return this;
		}
		
		public GridOptionsBuilder sortName(String sortname){
			addOption( "sortname", sortname );
			return this;
		}
		
		public GridOptionsBuilder rowList(String rowList){
			addOption( "rowList", rowList );
			return this;
		}
		
		public GridOptionsBuilder pagerId(GridPanel grid){
			addOption( "pager", "#"+getEscapedNavBarId( grid ) );
			return this;
		}
		
		public GridOptionsBuilder multiSelect(boolean multiSelect){
			addOption( "multiselect", multiSelect );
			return this;
		}
		
		public GridOptionsBuilder dataUrl(String dataUrl){
			addOption( "url", dataUrl );
			return this;
		}
		
		public ColumnModelBuilder columnDefinition(){
			return new ColumnModelBuilder( this );
		}
		
		public JsonReaderBuilder jsonReader(){
			return new JsonReaderBuilder( this );
		}
		
		public GroupingViewBuilder groupingView(){
			return new GroupingViewBuilder( this );
		}
		
		
		public GridOptionsBuilder title(String title){
			addOption( "caption", title );
			return this;
		}
		
		public GridOptionsBuilder columnReordering(boolean sortable){
			addOption( "sortable", sortable );
			return this;
		}
		
		public String build(){
			return this.options.toStringWithFunctions();
		}
		
		public class GroupingViewBuilder{
			
			private JSONObject current;
			private GridOptionsBuilder parent;
			private JSONArray groupField;
			
			public GroupingViewBuilder(GridOptionsBuilder builder){
				this.parent = builder;
				current = new JSONObject();
				groupField = new JSONArray();
			}
			
			private void addOption(String name, Object value){
				try {
					current.put( name, value );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
			}
			
			private void addOptionArray(String value){
				this.groupField.put( value );
			}
			
			public GroupingViewBuilder addGroupField(String name){
				addOptionArray( name );
				return this;
			}
			
			
			public GridOptionsBuilder end(){
				try {
					current.put( "groupField", groupField );
					parent.options.put( "groupingView", current );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
				return parent;
			}
			
		}
		
		public class JsonReaderBuilder{
			
			private JSONObject current;
			private GridOptionsBuilder parent;
			
			public JsonReaderBuilder(GridOptionsBuilder builder){
				this.parent = builder;
				current = new JSONObject();
			}
			
			private void addOption(String key, Object value){
				try{
					this.current.put( key, value );
				} catch (JSONException e){
					e.printStackTrace();
				}
			}
			
			public JsonReaderBuilder root(String root){
				addOption( "root", root );
				return this;
			}
			
			public JsonReaderBuilder repeatItems(boolean repeat){
				addOption( "repeatitems", repeat );
				return this;
			}
			
			
			public JsonReaderBuilder id(String id){
				addOption( "id", id );
				return this;
			}
			
			public JsonReaderBuilder totalPages(String pages){
				addOption( "total", pages );
				return this;
			}
			
			public JsonReaderBuilder totalRecords(JsonFormat records){
				addOption( "records", records.toString() );
				return this;
			}
			
			public JsonReaderBuilder cell(String cell){
				addOption( "cell", cell );
				return this;
			}
			
			
			
			public GridOptionsBuilder end(){
				try {
					parent.options.put( "jsonReader", this.current );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
				return parent;
			}
			
		}
		
		public class ColumnModelBuilder {
			
			private GridOptionsBuilder parent;
			
			private JSONArray values = new JSONArray();
			
			private JSONObject current;
			
			private void addOption(String key, Object value){
				try{
					this.current.put( key, value );
				} catch (JSONException e){
					e.printStackTrace();
				}
			}
			
			public ColumnModelBuilder(GridOptionsBuilder builder){
				this.parent = builder;
			}
			
			public ColumnModelBuilder name(String name){
				addOption( "name", name );
				return this;
			}
			
			public ColumnModelBuilder index(String index){
				addOption( "index", index );
				return this;
			}
			
			public ColumnModelBuilder width(int width){
				addOption( "width", width );
				return this;
			}
			
			public ColumnModelBuilder align(String align){
				addOption( "align", align );
				return this;
			}
			
			public ColumnModelBuilder frozen(boolean frozen){
				addOption( "frozen", frozen );
				return this;
			}
			
			public ColumnModelBuilder searchType(String type){
				addOption( "stype" , type );
				return this;
			}
			
			public ColumnModelBuilder searchDataType(String dataType){
				addOption( "searchtype" , dataType );
				return this;
			}
			
			public ColumnModelBuilder editType(String type){
				addOption( "edittype" , type );
				return this;
			}
			
			public EditOptions editOptions(){
				return new EditOptions( this );
			}
			
			
			public class EditOptions {
				
				
				private StringBuilder builder = new StringBuilder();
				private boolean usedFirst = false;
				private JSONObject currentEditOptions = new JSONObject();
				
				private void addOption(String name, Object value){
					
						if (usedFirst)
							builder.append(";").append( name ).append(":").append(value);
						else{
							usedFirst = true;
							builder.append( name ).append(":").append(value);
						}
				}
				
				public ColumnModelBuilder parent;
				
				public EditOptions(ColumnModelBuilder builder){
					this.parent = builder;
				}
				
				public EditOptions value( String name, String value ){
					addOption( name, value );
					return this;
				}
				
				public EditOptions defaultValue( String value ){
					try {
						currentEditOptions.put( "defaultValue", value );
					} catch ( JSONException e ) {
						e.printStackTrace();
					}
					return this;
				}
				
				public ColumnModelBuilder end(){
					try {
						currentEditOptions.put( "value", builder.toString() );
						parent.current.put( "editoptions", currentEditOptions );
					} catch ( JSONException e ) {
						e.printStackTrace();
					}
					return parent;
				}
				
			}
			
			public ColumnModelBuilder hidden(boolean hidden){
				addOption( "hidden", hidden );
				return this;
			}
			
			public ColumnModelBuilder sortable(boolean sortable){
				addOption( "sortable", sortable );
				return this;
			}
			
			public ColumnModelBuilder newColumn(){
				putLastColumn();
				
				current = new JSONObject();
				return this;
			}
			
			
			public GridOptionsBuilder endColumns() {
				try {
					putLastColumn();
					parent.options.put( "colModel", this.values );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
				return parent;
			}

			private void putLastColumn() {
				if (current != null)
					values.put( current );
			}
		}
		
	}
}
