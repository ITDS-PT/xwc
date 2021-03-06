package netgest.bo.xwc.components.classic.grid;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.runtime.boConvertUtils;
import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Tab;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.grid.Aggregate.AggregateAction;
import netgest.bo.xwc.components.classic.grid.WebRequest.GridParameter;
import netgest.bo.xwc.components.classic.grid.metadata.GridPanelGroupJSONRendererMetadata;
import netgest.bo.xwc.components.classic.grid.utils.DataFieldDecoder;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataGroupConnector;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.GroupableDataList;
import netgest.bo.xwc.components.data.JavaScriptArrayProvider;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOBaseList;
import netgest.bo.xwc.xeo.beans.XEOEditBean;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridPanelRenderer extends XUIRenderer implements XUIRendererServlet {
    
	ThreadLocal<GridPanelExtJsRenderer> extRenderer = new ThreadLocal<GridPanelExtJsRenderer>() {
		@Override
		protected GridPanelExtJsRenderer initialValue() {
			return new GridPanelExtJsRenderer();
		}
	};
	
    @Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
        super.encodeBegin(component);
        //extRenderer = new GridPanelExtJsRenderer();
    	extRenderer.get().encodeBegin( component );
    }

    @Override
    public void encodeEnd(XUIComponentBase oComp) throws IOException {
    	extRenderer.get().encodeEnd( oComp );
    	super.encodeEnd( oComp );
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    @Override
    public void decode(XUIComponentBase oComp) {
        GridPanel oGridComp;
        String    sGridSelectedIds;
        
        oGridComp = (GridPanel)oComp;
        
        Map< String , String > parameters = oComp.getRequestContext().getRequestParameterMap();
        
        sGridSelectedIds = parameters.get( oComp.getClientId() +"_srs" );
        if( sGridSelectedIds != null && sGridSelectedIds.length() > 0 ){
            oGridComp.setSelectedRowsByIdentifier( sGridSelectedIds.split("\\|") );
        }
        
        sGridSelectedIds = parameters.get( oComp.getClientId() +"_act" );
        if( sGridSelectedIds != null && sGridSelectedIds.length() > 0 ){
            oGridComp.setActiveRowByIdentifier( sGridSelectedIds );
        }
        
        String pagesAllSelected = parameters.get( oComp.getClientId() +"_pages" );
        if (StringUtils.hasValue( pagesAllSelected )) {
        	String[] valuesRaw = pagesAllSelected.trim().split(",");
        	int[] values = new int[valuesRaw.length];
        	int count = 0;
        	final int invalidPage = -1;
        	for (String pageNumCandidate : valuesRaw){
        		try {
					values[count] = Integer.parseInt(pageNumCandidate);
				} catch (Exception e) {
					values[count] = invalidPage ;
				}
        	}
        	oGridComp.setSelectedPages(values);
        } 
    }

    @Override
    public void encodeChildren(XUIComponentBase oComp) throws IOException {
    	this.extRenderer.get().encodeChildren( oComp );
    }
    
	public ExtConfig getExtJsConfig(XUIComponentBase comp) throws IOException {
		return this.extRenderer.get().extEncodeAll( comp );
	}
    
	//Bug with /*DUMMY_AGGREGATE*/ 
	//remove from array to prevent being used on a query
	//this is a workaround
	//The use of the virtual field /*DUMMY_AGGREGATE*/ should be reevaluated 
	private Object[] removeDummyAggregateFromArray(Object[] array) {
		List<Object> newArray= new ArrayList<Object>();
		for (Object currItem:array) {
			if (currItem instanceof String) {
				if (((String)currItem).indexOf("/*DUMMY_AGGREGATE*/")==-1) {
					newArray.add(currItem);
				}
			}
			else {
				newArray.add(String.valueOf(currItem));
			}
		}
		return newArray.toArray(new String[newArray.size()]);
	}
    
	public void service(ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp ) throws IOException
    {
		GridPanel oGrid;
        GridPanelRequestParameters reqParam;

        oGrid = (GridPanel)oComp;

        String sType = oRequest.getParameter("type");
                
        
        if( "pdf".equalsIgnoreCase( sType ) ) {
        	GridPanelPDFRenderer pdfRender = new GridPanelPDFRenderer();
        	pdfRender.render( oRequest , oResponse, oGrid );
        }
        else if ( "excel".equalsIgnoreCase( sType ) ) {
        	GridPanelExcelRenderer excelRenderer = new GridPanelExcelRenderer();
        	excelRenderer.getExcel( oRequest, oResponse, oGrid );
        }
        else {
        	//Decode only for data render, PDF and Excel submit don't have the parameters (removing sorting, etc...)  
        	reqParam = decodeServiceParmeters( oGrid, (HttpServletRequest)oRequest );
        	if( "true".equals( oRequest.getParameter("updateConfig") ) )
            	return;
        	DataListConnector dataSource = oGrid.getDataSource();
	        if ( reqParam.getGroupBy() == null || (dataSource.dataListCapabilities() & DataListConnector.CAP_GROUPING) == 0 ) {
	        	GridPanelJSonRenderer jsonRenderer = new GridPanelJSonRenderer();
	        	jsonRenderer.getJsonData(oRequest, oResponse, reqParam, oGrid, dataSource);
	        }
	        else if ( reqParam.getGroupBy() != null  ) {
	        	oResponse.setContentType( "text/plain;charset=utf-8" );
	        	
	        	if (reqParam.isGroupToolBarVisible() != null)
	        		oGrid.setShowGroupToolBar(reqParam.isGroupToolBarVisible());
	        	oGrid.applySqlFields(dataSource);
	        	oGrid.applyFilters( dataSource );
	        	oGrid.applySort( dataSource );
	        	oGrid.applyFullTextSearch( dataSource );
	        	oGrid.applyAggregate( dataSource );
	        	
	        	
	            Map<String,GridColumnRenderer> columnRenderer = new HashMap<String,GridColumnRenderer>();
	            Column[] oAttributeColumns = oGrid.getColumns();
	            for( Column gridCol : oAttributeColumns ) {
	                if( gridCol != null && gridCol instanceof ColumnAttribute ) {
	                    GridColumnRenderer r = ( ( ColumnAttribute ) gridCol ).getRenderer();
	                    if( r != null ) {
	                    	columnRenderer.put( gridCol.getDataField(), r );
	                    }
	                }
	            }	    		
	        	
	        	if( reqParam.getGroupByLevel() >= reqParam.getGroupBy().length ) {
	        		String[] groupBy = reqParam.getGroupBy(); 
	        		Object[] pValues=reqParam.getParentValues();
	        		
	        		//Bug with /*DUMMY_AGGREGATE*/ 
	        		//remove from array to prevent being used on a query
	        		//this is a workaround
	        		//The use of the virtual field /*DUMMY_AGGREGATE*/ should be reevaluated 
	        		if (groupBy.length>1 && !StringUtils.isEmpty(oGrid.getAggregateFieldsString())) {
	 	        		groupBy=(String[])removeDummyAggregateFromArray(groupBy);
		        		pValues=removeDummyAggregateFromArray(reqParam.getParentValues());
	        		}
	        		DataListConnector groupDetails = 
	            		((GroupableDataList)dataSource).getGroupDetails(
	            				groupBy,
	            				pValues,
	            				groupBy[groupBy.length-1],
	            				reqParam.getPage(), 
	            				reqParam.getPageSize()
	            			);
	
	        		GridPanelJSonRenderer jsonRenderer = new GridPanelJSonRenderer();
	                StringBuilder oStrBldr = jsonRenderer.buildDataArray( 
	                		oGrid, 
	                		groupDetails, 
	                		groupDetails.iterator(), 
	                		0, 
	                		reqParam.getPageSize(),
	                		-1,
	                		reqParam
	                	);
	                oResponse.getWriter().print( oStrBldr );
	                
	        	}
	        	else {
	        		String grouByLevelField = reqParam.getGroupBy()[ reqParam.getGroupByLevel() ];
	            	DataGroupConnector groupConnector = ((GroupableDataList)dataSource).getGroups(
	            		reqParam.getGroupBy(),
	            		reqParam.getParentValues(),
	            		reqParam.getGroupBy()[ reqParam.getGroupByLevel() ],
	            		reqParam.getPage(), 
	            		reqParam.getPageSize() 
	            	);
	                StringBuilder s = new StringBuilder(200);
	                s.append( '{' );
	                s.append( oGrid.getId() ); 
	                s.append( ":" );
	                
	                JavaScriptArrayProvider oJsArrayProvider = null;
	                if((dataSource.dataListCapabilities() & DataListConnector.CAP_AGGREGABLE) > 0)
	                {
	                	oJsArrayProvider = new JavaScriptArrayProvider( 
		                		groupConnector.iterator(),
		                		new String[] { grouByLevelField, grouByLevelField + "__value", grouByLevelField + "__count", grouByLevelField + "__aggregate" },
		                		0,
		                		50
		                	);
	                }
	                else
	                {
	                	oJsArrayProvider = new JavaScriptArrayProvider( 
		                		groupConnector.iterator(),
		                		new String[] { grouByLevelField, grouByLevelField + "__value", grouByLevelField + "__count" },
		                		0,
		                		50
		                	);
	                }
	                
	                
	                oJsArrayProvider.getJSONArray( s, oGrid, oGrid.getGroupBy(), null, null, columnRenderer );
	                GridPanelGroupJSONRendererMetadata metadata = new GridPanelGroupJSONRendererMetadata( oGrid , groupConnector , reqParam );
	                s.append(",");
	                	s.append(metadata.outputJSON());
	                s.append( "}" );
	                
	            	oResponse.getWriter().print( s );
	        	}
	        }
        }
    }
	
	
	public GridPanelRequestParameters decodeServiceParameters( GridPanel oGridPanel, WebRequest oRequest ) {
		String selectedRows 	= oRequest.getParameter( GridParameter.SELECTED_ROWS );
        String activeRow 		= oRequest.getParameter( GridParameter.ACTIVE_ROW );
        
        String[] groupBy 		= oRequest.getParameterValues( GridParameter.GROUP_BY );
        String aggregateField 		= oRequest.getParameter( GridParameter.AGGREGATE );
        
        String groupToolBarVisible = oRequest.getParameter( GridParameter.TOOLBAR_VISIBLE );
        
        GridPanelRequestParameters reqParam = new GridPanelRequestParameters();
        
        String pagesAllSelected = oRequest.getParameter( GridParameter.PAGES_ALL_SELECTED );
        if (StringUtils.hasValue( pagesAllSelected )) {
        	String[] valuesRaw = pagesAllSelected.trim().split(",");
        	int[] values = new int[valuesRaw.length];
        	int count = 0;
        	final int invalidPage = -1;
        	for (String pageNumCandidate : valuesRaw){
        		try {
					values[count] = Integer.parseInt(pageNumCandidate);
				} catch (Exception e) {
					values[count] = invalidPage ;
				}
        	}
        	oGridPanel.setSelectedPages(values);
        } 
        
        if (groupToolBarVisible != null){
        	Boolean groupToolBarVisibleValue = Boolean.parseBoolean( groupToolBarVisible );
        	oGridPanel.setShowGroupToolBar(groupToolBarVisibleValue);
        }
        
        // Fix empty group by array
        if( groupBy != null && groupBy.length == 1 && groupBy[0] != null && groupBy[0].trim().length() == 0 )
        	groupBy = null;
        
        if( groupBy != null ) {
        	for( int i=0; i < groupBy.length; i++ ) {
        		groupBy[i] = DataFieldDecoder.convertForBOQL( groupBy[i] );
        	}
        }
        
        String 	 groupByLevelS 	= oRequest.getParameter( GridParameter.GROUP_BY_LEVEL );
        String[] sParentValues	= oRequest.getParameterValues( GridParameter.GROUP_BY_PARENT_VALUES );
        
        
        //Decode parameters received as String into their native data types
        //
        //This is required because of PostGre implementation as it does not play well
        //with PreparedStatements with bind parameters in situations where the value
        //of the parameter is a string but the data type of the column is another thing (date, number)
        //It must receive the correct data type.
        //
        //Other databases (Oracle, MySQl, SQLServer) can 
        //figure the type automatically (or so it seems)
        //and don't have any problems
        
        List<Object> serviceParameterValues = new LinkedList<Object>();
        DataListConnector dataSource = oGridPanel.getDataSource(); 
        if (sParentValues != null){
	        for (int groupParameter = 0; groupParameter < sParentValues.length; groupParameter++){
	        	String sParent = sParentValues[groupParameter];
	        	String groupByColumn = groupBy[groupParameter];
	        	byte dataType = DataFieldTypes.VALUE_UNKNOWN;
	        	if (dataSource.getAttributeMetaData(groupByColumn)!=null)
	        		dataType = dataSource.getAttributeMetaData(groupByColumn).getDataType();	        
	        	switch (dataType){
	        		case DataFieldTypes.VALUE_CHAR: serviceParameterValues.add(sParent); break;
	        		case DataFieldTypes.VALUE_NUMBER:
	        			Column c = oGridPanel.getColumn( groupByColumn );
	        			if (c.useValueOnLov())
	        				serviceParameterValues.add(StringUtils.hasValue( sParent ) ? new BigDecimal(sParent) : null);
	        			else{ //Special situation for Lov columns that are numeric columns
	        				serviceParameterValues.add(sParent);
	        			}
	        			break;
	        		case DataFieldTypes.VALUE_CURRENCY:
	        				serviceParameterValues.add(StringUtils.hasValue( sParent ) ? new BigDecimal(sParent) : null);
	        			break;
	        		case DataFieldTypes.VALUE_DATE:
	        			Date result = boConvertUtils.convertToDate(sParent, null);
	        			if (result != null){
	        				Timestamp ts = new Timestamp(result.getTime());
	        				serviceParameterValues.add(ts);
	        			}
	        			else
	        				serviceParameterValues.add(sParent);
	        			break;
	        		case DataFieldTypes.VALUE_DATETIME: 
	        			Date resultTime = boConvertUtils.convertToDate(sParent, null);
	        			if (resultTime != null){
	        				Timestamp ts = new Timestamp(resultTime.getTime());
	        				serviceParameterValues.add(ts);
	        			}
	        			else
	        				serviceParameterValues.add(sParent);
	        			break;
	        		case DataFieldTypes.VALUE_BOOLEAN:
	        			BigDecimal booleanResult = boConvertUtils.convertToBigDecimal(sParent, null);
	        			if (booleanResult != null){
	        				serviceParameterValues.add(booleanResult);
	        			} else {
	        				serviceParameterValues.add(sParent);
	        			}
	        			break;
	        		//Default: Add as a String
	        		default : serviceParameterValues.add(sParent); break;
	        	}
	        }
        }
        
        String dataSourceChanged = oRequest.getParameter( GridParameter.DATASOURCE_CHANGE );
        if (StringUtils.hasValue( dataSourceChanged ) && Boolean.parseBoolean( dataSourceChanged )){
        	reqParam.dataSourceChanged();
        }
        
        int groupByLevel = -1;
        
        if( groupByLevelS != null ) {
        	groupByLevel = Integer.parseInt( groupByLevelS );
        }
        Object[] parentValues = null;
        
        if (sParentValues != null){
        	parentValues = new Object[serviceParameterValues.size()];
        	int k = 0;
        	for (Iterator<Object> it = serviceParameterValues.iterator(); it.hasNext(); ){
        		Object currentParameter = it.next();
        		parentValues[k++] = currentParameter;
        	}
        }
        
        
        String 	columnsConfig  = oRequest.getParameter( GridParameter.COLUMNS_CONFIG );
        String 	expandedGroups 	= oRequest.getParameter( GridParameter.EXPANDED_GROUPS );
        if( expandedGroups != null ) {
    		oGridPanel.setCurrentExpandedGroups( expandedGroups );
        }
        else {
        	oGridPanel.setCurrentExpandedGroups( null );
        }
        
        oGridPanel.setActiveRowByIdentifier( activeRow );
        if( selectedRows != null && selectedRows.length() > 0 ) {
        	oGridPanel.setSelectedRowsByIdentifier( selectedRows.split("\\|"));
        }
        
        // Group by parameters
        reqParam.setGroupBy( groupBy );
        //String previousGroupBy = oGridPanel.getGroupBy();
        String newGroupBy = org.apache.commons.lang.StringUtils.join(groupBy, ',');
        //if (!org.apache.commons.lang.StringUtils.equals( previousGroupBy , newGroupBy ))
        //	reqParam.dataSourceChanged();
        oGridPanel.setGroupBy( newGroupBy );
        
        //FultexSearch Parameters
        String sFullText    = oRequest.getParameter(GridParameter.FULL_TEXT);
        String previousFullText = oGridPanel.getCurrentFullTextSearch();
        if (!org.apache.commons.lang.StringUtils.equals( previousFullText , sFullText ))
        	reqParam.dataSourceChanged();
        oGridPanel.setCurrentFullTextSearch( sFullText );
        
        // Parameters at requestLevel
        String sStart = oRequest.getParameter( GridParameter.START );
        String sLimit = oRequest.getParameter( GridParameter.LIMIT );
        
        
        int start = Integer.parseInt( sStart!=null&&sStart.length()>0?sStart : "0" );
        if (reqParam.isDataSourceChanged()){
        	start = 0; //Prevent wrong pages from being present to the user
        }
        
        int limit = Integer.parseInt( sLimit!=null&&sLimit.length()>0?sLimit : oGridPanel.getPageSize() );
        
        int page = 1;
        if( start > 0 && start % limit == 0 )
            page = (start / limit) + 1;
        else
        	page = (start / limit) + 1;
		
		
		
		if (groupToolBarVisible != null)
			reqParam.setGroupToolBarVisible(Boolean.parseBoolean(groupToolBarVisible));
		
		// Summary Parameter
		reqParam.setAggregateParameter(aggregateField);
		
        oGridPanel.setCurrentAggregateField( aggregateField );
				
        if ( 
        		oGridPanel.getCurrAggregateFieldOpSet() != null &&
        		oGridPanel.getCurrAggregateFieldCheckSet() != null &&
        		oGridPanel.getCurrAggregateFieldSet() != null) {
			
        	AggregateAction whatAction = AggregateAction.fromString( oGridPanel.getCurrAggregateFieldCheckSet() );
			if ( whatAction == AggregateAction.ADD_AGGREGATE )
				oGridPanel.updateAggregateFieldsBean();
			else if ( whatAction == AggregateAction.REMOVE_AGGREGATE ){
				oGridPanel.removeAggregateFieldsBean();
			}
        	
			
		}
		
		
		 
		reqParam.setGroupByLevel( groupByLevel );
		reqParam.setParentValues( parentValues );
		
		if (start >= 0 )
			reqParam.setPage( page );
		else
			reqParam.setPage( -1 );
		reqParam.setPageSize( limit );
		reqParam.setStart( start );
		reqParam.setLimit( limit );
        
        
        
        
        // Sort Terms
        SortTermsDecoder sortDecoder = new SortTermsDecoder( oRequest.getParameter(GridParameter.SORT) );
        if (sortDecoder.areTermsValid()){
        	oGridPanel.setCurrentSortTerms( sortDecoder.toGridPanelInternalFormat() );
        } else
        	oGridPanel.setCurrentSortTerms( null );
        
        // Column State
        if( columnsConfig != null ) {
        	try {
        		String sConfig = oGridPanel.getCurrentColumnsConfig();
        		JSONArray savedColsConfig;
        		if( sConfig != null && sConfig.length() > 0 ) {
        			savedColsConfig = new JSONArray( sConfig );
        		} else {
        			savedColsConfig = new JSONArray();
        		}
        		
        		JSONArray colsConfig;
        		if( columnsConfig == null || columnsConfig.length() > 0 ) {
        			colsConfig = new JSONArray( columnsConfig );
        		} else {
        			colsConfig = new JSONArray();
        		}
        		
				for( int i=0; i < colsConfig.length(); i++ ) {
					JSONObject colConfig = colsConfig.getJSONObject(i);
					boolean found = false;
					String colDataField = DataFieldDecoder.convertForBOQL( colConfig.optString( "dataField" ) );
					
					JSONObject savedCfg = null;
					
					for(int j=0;j<savedColsConfig.length();j++) {
						savedCfg = savedColsConfig.getJSONObject(j);
						if( colDataField.equals( savedCfg.opt( "dataField" ) ) ) {
							found = true;
							break;
						}
					}
					if( !found ) {
						savedCfg = new JSONObject();
						savedCfg.put("dataField", colDataField );
						savedColsConfig.put( savedCfg );
					}
					if( colConfig.has("width") )
						savedCfg.put("width", colConfig.opt("width") );
					if( colConfig.has("hidden") ) {
						savedCfg.put("hidden", colConfig.opt("hidden") );
						Column col = oGridPanel.getColumn( colDataField );
						if( col != null ) {
							boolean visible = savedCfg.optBoolean("hidden"); 
							col.setHidden( Boolean.toString( visible ) );
						}
					}
					if( colConfig.has("position") )
						savedCfg.put("position", colConfig.opt("position") );
					
				}
				oGridPanel.setCurrentColumnsConfig( savedColsConfig.toString() );
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        
        
        
        // Filter terms
        String sFilters = oRequest.getParameter(GridParameter.FILTERS);
		String sServerFilters = oGridPanel.getCurrentFilters();
        if( sFilters != null ) {
	        try {
				// Fix something in json filters.
	        	// BUG... when setting a value to a objecto, client doesn't send that! so, it must be
	        	// merged with server side.
	    		
	        	JSONObject jFilters;
	    		JSONObject serverFilters;
	    		
	    		jFilters = new JSONObject( "{}" );
	    		
				if( sFilters != null ) {
					jFilters = new JSONObject( sFilters );
				}
				
				if( sServerFilters != null ) {
					serverFilters = new JSONObject( sServerFilters );
				}
				else {
					serverFilters  = new JSONObject( "{}" );
				}
				
	    		String[] names = JSONObject.getNames( jFilters );
	    		if( names != null ) {
	        		for( String name : names ) {
	        			JSONObject serverFilter = serverFilters.optJSONObject( name );
	        			JSONObject clientFilter = jFilters.getJSONObject(name);
        				if( serverFilter != null ) {
        					serverFilter.put( "active", clientFilter.getBoolean("active") );
        					// Filter's in objects can't be readed from the client
        					// because it clears all other searchs
        					if( clientFilter.opt("value") != null ) {
        						serverFilter.put( "value", clientFilter.get("value") );
        					}
        					if (clientFilter.opt( "containsData" ) != null){
        						serverFilter.put( "containsData", clientFilter.get("containsData") );
        					}
        					
        					if (clientFilter.opt( "cardIdSearch" ) != null){
        						serverFilter.put( "cardIdSearch", clientFilter.get("cardIdSearch") );
        					}
        					
        					jFilters.put( name, serverFilter );
	        			}
	        		}
	    		}
	    		oGridPanel.setCurrentFilters( jFilters.toString() );
			} catch (JSONException e) {
				// Do nothing.. ignore filters...
				e.printStackTrace();
			}
        }
        if( oGridPanel.getAutoSaveGridState()) {
        	oGridPanel.saveUserState();
        }
		return reqParam;
		
	}
	
	public GridPanelRequestParameters decodeServiceParmeters( GridPanel oGridPanel, HttpServletRequest oRequest ) {
		return decodeServiceParameters( oGridPanel, new HttpServletRequestWrapper( oRequest ) );
	}
	
	
	public static String getExportTitle( GridPanel oGrid ) {
    	XUIViewRoot oViewRoot = XUIRequestContext.getCurrentContext().getViewRoot();
    	Object viewBean = oViewRoot.getBean( oGrid.getBeanId() );
    	String sTitle;
    	if( viewBean instanceof XEOEditBean  ) {
    		sTitle = ((XEOEditBean)viewBean).getTitle();
    	} else if( viewBean instanceof XEOBaseList ) {
    		sTitle = ((XEOBaseList)viewBean).getTitle();
    	}
    	else {
    		sTitle = BeansMessages.LIST_OF.toString();
    	}
    	Tab t = (Tab)oGrid.findParentComponent( Tab.class );
    	if( t != null ) {
    		sTitle += " - " + t.getLabel();
    	}
    	sTitle = sTitle.trim();
    	sTitle = sTitle.replaceAll( "<[a-zA-Z\\/][^>]*>", "");
		sTitle = HTMLEntityDecoder.htmlEntityToChar( sTitle );  
    	return sTitle;
	}

}
