package netgest.bo.xwc.components.classic.grid;

import java.io.IOException;
import java.sql.Timestamp;
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

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridPanelRenderer extends XUIRenderer implements XUIRendererServlet {
    
	GridPanelExtJsRenderer extRenderer;
	
    @Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
        super.encodeBegin(component);
        extRenderer = new GridPanelExtJsRenderer();
    	extRenderer.encodeBegin( component );
    }

    @Override
    public void encodeEnd(XUIComponentBase oComp) throws IOException {
    	extRenderer.encodeEnd( oComp );
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
        
        sGridSelectedIds = oComp.getRequestContext().getRequestParameterMap().get( oComp.getClientId() +"_srs" );
        if( sGridSelectedIds != null && sGridSelectedIds.length() > 0 )
        {
            oGridComp.setSelectedRowsByIdentifier( sGridSelectedIds.split("\\|") );
        }
        
        sGridSelectedIds = oComp.getRequestContext().getRequestParameterMap().get( oComp.getClientId() +"_act" );
        if( sGridSelectedIds != null && sGridSelectedIds.length() > 0 )
        {
            oGridComp.setActiveRowByIdentifier( sGridSelectedIds );
        }
    }

    @Override
    public void encodeChildren(XUIComponentBase oComp) throws IOException {
    	this.extRenderer.encodeChildren( oComp );
    }
    
	public ExtConfig getExtJsConfig(XUIComponentBase comp) throws IOException {
		if( this.extRenderer == null ) 
			this.extRenderer = new GridPanelExtJsRenderer();
		
		return this.extRenderer.extEncodeAll( comp );
	}
    
    
	public void service(ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp ) throws IOException
    {
        
		
		GridPanel oGrid;
        GridPanelRequestParameters reqParam;

        oGrid = (GridPanel)oComp;
        
        
        
        reqParam = decodeServiceParmeters( oGrid, (HttpServletRequest)oRequest );
        
        if( "true".equals( oRequest.getParameter("updateConfig") ) )
        	return;
        
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
        	DataListConnector dataSource = oGrid.getDataSource();
	        if ( reqParam.getGroupBy() == null || (dataSource.dataListCapabilities() & DataListConnector.CAP_GROUPING) == 0 ) {
	        	GridPanelJSonRenderer jsonRenderer = new GridPanelJSonRenderer();
	        	jsonRenderer.getJsonData(oRequest, oResponse, reqParam, oGrid, dataSource);
	        }
	        else if ( reqParam.getGroupBy() != null  ) {
	        	oResponse.setContentType( "text/plain;charset=utf-8" );
	        	
	        	oGrid.applySqlFields(dataSource);
	        	oGrid.applyFilters( dataSource );
	        	oGrid.applySort( dataSource );
	        	oGrid.applyFullTextSearch( dataSource );
	        	oGrid.applyAggregate( dataSource );
	        	
	        	
	            Map<String,GridColumnRenderer> columnRenderer = new HashMap<String,GridColumnRenderer>();
	            Column[] oAttributeColumns = oGrid.getColumns();
	            for( Column gridCol : oAttributeColumns ) {
	                if( gridCol != null && gridCol instanceof ColumnAttribute ) {
	                    GridColumnRenderer r = ((ColumnAttribute)gridCol).getRenderer();
	                    if( r != null ) {
	                    	columnRenderer.put( gridCol.getDataField(), r );
	                    }
	                }
	            }
	    		String[] groupBy = reqParam.getGroupBy(); 
	        	
	        	if( reqParam.getGroupByLevel() >= reqParam.getGroupBy().length ) {
	
	        		DataListConnector groupDetails = 
	            		((GroupableDataList)dataSource).getGroupDetails(
	            				groupBy,
	            				reqParam.getParentValues(),
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
	                		reqParam.getPageSize() 
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
	                	                
	                int cnt = groupConnector.getRecordCount();
	                oJsArrayProvider.getJSONArray( s, oGrid, oGrid.getGroupBy(), null, null, columnRenderer );
	                s.append(",totalCount:").append( cnt );
	                s.append('}');
	            	oResponse.getWriter().print( s );
	        	}
	        }
        }
    }
	
	public GridPanelRequestParameters decodeServiceParmeters( GridPanel oGridPanel, HttpServletRequest oRequest ) {
		
		String selectedRows 	= oRequest.getParameter( "selectedRows" );
        String activeRow 		= oRequest.getParameter( "activeRow" );
        
        String[] groupBy 		= oRequest.getParameterValues( "groupBy" );
        String aggregateField 		= oRequest.getParameter( "aggregateField" );
        
        // Fix empty group by array
        if( groupBy != null && groupBy.length == 1 && groupBy[0] != null && groupBy[0].trim().length() == 0 )
        	groupBy = null;
        
        if( groupBy != null ) {
        	for( int i=0; i < groupBy.length; i++ ) {
        		groupBy[i] = groupBy[i].replaceAll("__", ".").trim();
        	}
        }
        
        String 	 groupByLevelS 	= oRequest.getParameter( "groupByLevel" );
        String[] sParentValues	= oRequest.getParameterValues( "groupByParentValues" );
        
        
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
	        	byte dataType = dataSource.getAttributeMetaData(groupByColumn).getDataType();
	        	switch (dataType){
	        		case DataFieldTypes.VALUE_CHAR: serviceParameterValues.add(sParent); break;
	        		case DataFieldTypes.VALUE_NUMBER: serviceParameterValues.add(Long.parseLong(sParent)); break;
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
	        		case DataFieldTypes.VALUE_BOOLEAN: serviceParameterValues.add(Boolean.parseBoolean(sParent)); break;
	        		//Default: Add as a String
	        		default : serviceParameterValues.add(sParent); break;
	        	}
	        }
        }
        
        int groupByLevel = -1;
        
        if( groupByLevelS != null ) {
        	groupByLevel = Integer.parseInt( groupByLevelS );
        }
        Object[] parentValues = null;
        
        /*if( sParentValues != null ) {
        	parentValues = Arrays.asList(sParentValues).toArray( new Object[ sParentValues.length ] );
        }*/
        if (sParentValues != null){
        	parentValues = new Object[serviceParameterValues.size()];
        	int k = 0;
        	for (Iterator<Object> it = serviceParameterValues.iterator(); it.hasNext(); ){
        		Object currentParameter = it.next();
        		parentValues[k++] = currentParameter;
        	}
        }
        
        
        String 	columnsConfig  = oRequest.getParameter( "columnsConfig" );
        String 	expandedGroups 	= oRequest.getParameter( "expandedGroups" );
        if( expandedGroups != null ) {
    		oGridPanel.setCurrentExpandedGroups( expandedGroups );
        }
        else {
        	oGridPanel.setCurrentExpandedGroups( null );
        }
        
        //oGridPanel.setGroupBy( groupBy );
        oGridPanel.setActiveRowByIdentifier( activeRow );
        if( selectedRows != null && selectedRows.length() > 0 ) {
        	oGridPanel.setSelectedRowsByIdentifier( selectedRows.split("\\|"));
        }
        
        // Parameters at requestLevel
        String sStart = oRequest.getParameter("start");
        String sLimit = oRequest.getParameter("limit");
        
        int start = Integer.parseInt( sStart!=null&&sStart.length()>0?sStart : "0" );
        int limit = Integer.parseInt( sLimit!=null&&sLimit.length()>0?sLimit : oGridPanel.getPageSize() );
        
        int page = 1;
        if( start > 0 && start % limit == 0 )
            page = (start / limit) + 1;
        else
        	page = (start / limit) + 1;
		
		GridPanelRequestParameters reqParam;
		reqParam = new GridPanelRequestParameters();
		
		// Sumary Parameter
		reqParam.setAggregateParameter(aggregateField);
		
        oGridPanel.setCurrentAggregateField( aggregateField );
				
        if (oGridPanel != null && 
        		oGridPanel.getCurrAggregateFieldOpSet() != null &&
        		oGridPanel.getCurrAggregateFieldCheckSet() != null &&
        		oGridPanel.getCurrAggregateFieldSet() != null) {
			String desc = oGridPanel.getCurrAggregateFieldDescSet() != null ? oGridPanel.getCurrAggregateFieldDescSet() : oGridPanel.getCurrAggregateFieldSet();
			
			boolean check = oGridPanel.getCurrAggregateFieldCheckSet().equalsIgnoreCase("T");
			String op = oGridPanel.getCurrAggregateFieldOpSet();
			if(op.equalsIgnoreCase("SUM"))
			{
				oGridPanel.aggregateSum(check, oGridPanel.getCurrAggregateFieldSet(), desc);
			}
			else if(op.equalsIgnoreCase("MIN"))
			{
				oGridPanel.aggregateMin(check, oGridPanel.getCurrAggregateFieldSet(), desc);
			}
			else if(op.equalsIgnoreCase("MAX"))
			{
				oGridPanel.aggregateMax(check, oGridPanel.getCurrAggregateFieldSet(), desc);
			}
			else if(op.equalsIgnoreCase("AVG"))
			{
				oGridPanel.aggregateAvg(check, oGridPanel.getCurrAggregateFieldSet(), desc);
			}
		}
		
		// Group by parameters
		reqParam.setGroupBy( groupBy );
		
		
		oGridPanel.setGroupBy( StringUtils.join(groupBy, ',') );
		 
		reqParam.setGroupByLevel( groupByLevel );
		reqParam.setParentValues( parentValues );
		
		reqParam.setPage( page );
		reqParam.setPageSize( limit );
		reqParam.setStart( start );
		reqParam.setLimit( limit );
        
        String sFullText    = oRequest.getParameter("fullText");
        oGridPanel.setCurrentFullTextSearch( sFullText );
        
        
        // Sort Terms
        String rSort 		= oRequest.getParameter("sort");
        if( rSort != null && rSort.length() > 0 ) {
        	String sortString = "";
        	try {
				JSONArray json = new JSONArray( rSort );
				for( int i=0; i < json.length(); i++ ) {
					JSONObject j = json.getJSONObject(i);
					if( j.has("field") ) {
						if( i > 0 ) 
							sortString += ",";
						
						sortString += j.getString("field").replaceAll("__", ".") + "|" + (j.has("direction")?j.getString("direction"):"");
					}
				}
				oGridPanel.setCurrentSortTerms( sortString );
			} catch (JSONException e) {
				// Igonre Sort JSON errors
			}
        }
        else {
        	oGridPanel.setCurrentSortTerms(null);
        }
        
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
					String colDataField = colConfig.optString( "dataField" );
					
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
        String sFilters = oRequest.getParameter("filters");
		String sServerFilters = oGridPanel.getCurrentFilters();
        if( sFilters != null ) {
	        try {
				// Fix something in json filters.
	        	// BUG... when setting a value to a objecto, client doesn't send that! so, it must be
	        	// merged with server side.
	        	
//	        	System.out.println( "REQ:" + sFilters );
	    		
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
	
	
	
	public static String getExportTitle( GridPanel oGrid ) {
    	XUIViewRoot oViewRoot = XUIRequestContext.getCurrentContext().getViewRoot();
    	Object viewBean = oViewRoot.getBean("viewBean");
    	String sTitle;
    	if( viewBean instanceof XEOEditBean  ) {
    		sTitle = ((XEOEditBean)viewBean).getTitle();
    	} else if( viewBean instanceof XEOBaseList ) {
    		sTitle = ((XEOBaseList)viewBean).getTitle();
    	}
    	else {
    		sTitle = "Listagem";
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
