package netgest.bo.xwc.components.classic.grid;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataGroupConnector;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataListIterator;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.SortTerms;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.connectors.SortTerms.SortTerm;
import netgest.bo.xwc.components.data.JavaScriptArrayProvider;
import netgest.bo.xwc.components.model.Column;

public class GridPanelJSonRenderer {
	
    public StringBuilder buildDataArray( GridPanel oGrid, DataListConnector oDataSource,Iterator<DataRecordConnector> dataIterator, int start, int limit ) {
    	return buildDataArray( oGrid, oDataSource,dataIterator, start, limit, -1 );
    }
    public StringBuilder buildDataArray( GridPanel oGrid, DataListConnector oDataSource,Iterator<DataRecordConnector> dataIterator, int start, int limit, long rowCount ) {
        JavaScriptArrayProvider oJsArrayProvider;
        
        Map<String,GridColumnRenderer> columnRenderer = new HashMap<String,GridColumnRenderer>();
        ArrayList<String> columns = new ArrayList<String>();
        
        String rowIdentifier = oGrid.getRowUniqueIdentifier();
    	columns.add( rowIdentifier );

        Column[] oAttributeColumns = oGrid.getColumns();
        for( Column gridCol : oAttributeColumns ) {
        	if( gridCol != null && (!gridCol.isHidden() && !rowIdentifier.equals( gridCol.getDataField() ) ) ) {
        		columns.add( gridCol.getDataField() );
	        	if( gridCol instanceof ColumnAttribute ) {
	        		GridColumnRenderer r = ( ( ColumnAttribute ) gridCol ).getRenderer();
	        		if( r != null ) {
	        			columnRenderer.put( gridCol.getDataField(), r );
	        		}
	        	}
        	}
        }
        
        String[] selRows = new String[] { oGrid.getActiveRowIdentifier() }; 
        
        oJsArrayProvider = new JavaScriptArrayProvider( 
        		dataIterator, 
        		(String[])columns.toArray( new String[ columns.size() ] ), 
        		start, 
        		limit 
        );
        
        StringBuilder s = new StringBuilder(200);
        s.append( '{' );
        s.append( oGrid.getId() ); 
        s.append( ":" );
        oJsArrayProvider.getJSONArray( s, oGrid, rowIdentifier, selRows, oGrid.getRowClass(), columnRenderer );

        s.append(",totalCount:").append( rowCount > -1?rowCount:oDataSource.getRecordCount() );

        s.append('}');
        
        return s;
        
    }
	
    public void getJsonData( ServletRequest oRequest, ServletResponse oResponse, 
    		GridPanelRequestParameters reqParam, GridPanel oGrid, DataListConnector oDataCon ) throws IOException {
    	
        Iterator<DataRecordConnector> dataIterator;
        oResponse.setContentType( "text/plain;charset=utf-8" );
        PrintWriter w = oResponse.getWriter();
        if( oDataCon != null ) {
        	if( reqParam.getGroupBy() != null ) {
        		StringBuilder groupByString = new StringBuilder();
        		boolean first = true;
        		for( String group : reqParam.getGroupBy() ) {
        			if( !first ) groupByString.append(',');
        			groupByString.append( group );
        			first = false;
        		}
        		oGrid.setGroupBy( groupByString.toString() );
        		w.print( getJsonDataApplyingLocalGrouping(oGrid, oDataCon, reqParam) );
        	}
        	else if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_PAGING) == DataListConnector.CAP_PAGING )  {
            	oGrid.setGroupBy( null );
        		oDataCon.setPageSize( reqParam.getLimit() ); 
                oDataCon.setPage( reqParam.getPage() );
                dataIterator = getDataListIterator(oGrid, oDataCon);
                StringBuilder oStrBldr = buildDataArray( oGrid, oDataCon, dataIterator, 0, reqParam.getLimit() );
                w.print( oStrBldr );
        	} else {
            	oGrid.setGroupBy( null );
                dataIterator = getDataListIterator(oGrid, oDataCon);
                
            	// Result Counter
                List<DataRecordConnector> counterList = new ArrayList<DataRecordConnector>();
                int counter = 0;
                while( dataIterator.hasNext() ) {
                	counterList.add( dataIterator.next() );
                	counter++;
                }
                dataIterator  = counterList.iterator();
                
            	StringBuilder oStrBldr = buildDataArray( oGrid, oDataCon, dataIterator, reqParam.getStart(), reqParam.getLimit(), counter );
                w.print( oStrBldr );
        	}
        }
        else {
            w.print(MessageLocalizer.getMessage("GRID_PANEL_SOURCE_IS_INVALID"));
        }
        
    }
    
    protected static Iterator<DataRecordConnector> getDataListIterator( GridPanel oGrid, DataListConnector oDataCon ) {

		oGrid.applySqlFields( oDataCon );

		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_FULLTEXTSEARCH) > 0 )
			oGrid.applyFullTextSearch( oDataCon );
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_FILTER) > 0 )
				oGrid.applyFilters( oDataCon );
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_SORT) > 0 )
			oGrid.applySort( oDataCon );
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_AGGREGABLE) > 0 )
			oGrid.applyAggregate( oDataCon );
		
		
        oDataCon.refresh();
        
        Iterator<DataRecordConnector> dataIterator;
        dataIterator = oDataCon.iterator();
        
        
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_FILTER) == 0 )
			dataIterator = oGrid.applyLocalFilter( dataIterator );
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_SORT) == 0 )
			dataIterator = oGrid.applyLocalSort( dataIterator );
        
        return dataIterator;
    }
    
	public StringBuilder getJsonDataApplyingLocalGrouping( GridPanel oGrid, DataListConnector oDataCon, GridPanelRequestParameters reqParam ) {

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
        
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_PAGING) > 0 ) {
    		oDataCon.setPage(1);
    		oDataCon.setPageSize( Integer.MAX_VALUE );
    		oGrid.applySqlFields(oDataCon);
    		oDataCon.refresh();
		}
		else {
			oDataCon.refresh();
		}
    	
		String[] 	groupBy 	 = reqParam.getGroupBy();
        
		Object[]	oParentValues = reqParam.getGroupParentValues(); 
		Object[]	parentValues = null;
		if( oParentValues != null ) {
			parentValues = new Object[ oParentValues.length ];
	        final SimpleDateFormat datetime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        final SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
	    	for( int i = 0;oParentValues != null && i < oParentValues.length; i++ ) {
	    		
	    		byte fieldType = DataFieldTypes.VALUE_CHAR;
	    		DataFieldMetaData fieldMeta = oDataCon.getAttributeMetaData( groupBy[i] );
	    		if( fieldMeta != null ) {
	    			fieldType = fieldMeta.getDataType();	    			
	    		}
	    		
	    		switch ( fieldType ) {
	    			case DataFieldTypes.VALUE_DATE:
	    				try {
	    					parentValues[i] = date.parse( (String)oParentValues[i] );
	    				} catch( ParseException e ) {}
	    				break;
	    			case DataFieldTypes.VALUE_DATETIME:
	    				try {
	    					parentValues[i] = datetime.parse( (String)oParentValues[i] );
	    				} catch( ParseException e ) {}
	    				break;
	    			case DataFieldTypes.VALUE_BOOLEAN:
	    				parentValues[i] = oParentValues[i];
	    				break;
	    			case DataFieldTypes.VALUE_BLOB:
	    			case DataFieldTypes.VALUE_CHAR:
	    				parentValues[i] = oParentValues[i];
	    				break;
	    			case DataFieldTypes.VALUE_NUMBER:
	    				if( ((String)oParentValues[i]).length() > 0 )
	    					parentValues[i] = new BigDecimal( oParentValues[i].toString() );
	    				break;
	    		}
	    	}
		}
    	if( reqParam.getGroupByLevel() >= reqParam.getGroupBy().length ) {
            Iterator<DataRecordConnector> dataIterator;
            dataIterator = oDataCon.iterator();
            dataIterator = oGrid.applyLocalFilter( dataIterator );
            if( reqParam.getGroupByLevel() > 0 ) {
            	FilterTerms groupFilterTerms = null;
            	for( int i = 0; i < reqParam.getGroupByLevel(); i++ ) {
                	FilterTerm term = new FilterTerm( groupBy[i], null, FilterTerms.OPERATOR_EQUAL, parentValues[i] );
                	if( groupFilterTerms == null ) {
                		groupFilterTerms = new FilterTerms( term );
                	}
                	else {
                		groupFilterTerms.addTerm( FilterTerms.JOIN_AND, term );
                	}
            	}
            	dataIterator = oGrid.applyLocalFilter(dataIterator, groupFilterTerms);
            	
            }
            
            
        	dataIterator = oGrid.applyLocalSort(dataIterator);
        	
        	// Result Counter
            List<DataRecordConnector> counterList = new ArrayList<DataRecordConnector>();
            int counter = 0;
            while( dataIterator.hasNext() ) {
            	counterList.add( dataIterator.next() );
            	counter++;
            }
            dataIterator  = counterList.iterator();
            
            //dataIterator = getDataListIterator(oGrid, oDataCon);
        	StringBuilder oStrBldr = buildDataArray( oGrid, oDataCon, dataIterator, reqParam.getStart(), reqParam.getLimit(), counter );
        	
        	return oStrBldr;
            
    		/*
    		DataListConnector groupDetails = 
        		((GroupableDataList)c).getGroupDetails(
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
            */
    	}
    	else {
            Iterator<DataRecordConnector> dataIterator;
            dataIterator = oDataCon.iterator();
            dataIterator = oGrid.applyLocalFilter( dataIterator );
            
            if( reqParam.getGroupByLevel() > 0 ) {
            	FilterTerms groupFilterTerms = null;
            	for( int i = 0; i < reqParam.getGroupByLevel(); i++ ) {
                	FilterTerm term = new FilterTerm( groupBy[i], null, FilterTerms.OPERATOR_EQUAL, parentValues[i] );
                	if( groupFilterTerms == null ) {
                		groupFilterTerms = new FilterTerms( term );
                	}
                	else {
                		groupFilterTerms.addTerm( FilterTerms.JOIN_AND, term );
                	}
            	}
            	dataIterator = oGrid.applyLocalFilter(dataIterator, groupFilterTerms);
            }
    		
    		String grouByLevelField = reqParam.getGroupBy()[ reqParam.getGroupByLevel() ];
    		
    		DataGroupConnector groupConnector = getGroups(reqParam, dataIterator );
    		
    		
    		/*
    		
        	DataGroupConnector groupConnector = ((GroupableDataList)c).getGroups(
        		reqParam.getGroupBy(),
        		reqParam.getParentValues(),
        		reqParam.getGroupBy()[ reqParam.getGroupByLevel() ],
        		reqParam.getPage(), 
        		reqParam.getPageSize() 
        	);
        	*/
    		
    		
    		Iterator<DataRecordConnector> dataListIterator = groupConnector.iterator();
    		
            SortTerms sortTerms = oGrid.getCurrentSortTerms();
            
            if( sortTerms != null ) {
            	
            	boolean needSort = false;
            	SortTerms groupSortTerms = new SortTerms();
            	Iterator<SortTerm> sortTermIt = sortTerms.iterator();
            	while( sortTermIt.hasNext() ) {
            		SortTerm term = sortTermIt.next();
            		String field = term.getField();
            		if( grouByLevelField.equals( field ) ) {
            			groupSortTerms.addSortTerm( term.getField() , term.getDirection() );
            			needSort = true;
            			break;
            		}
            	}
            	if( needSort ) {
            		dataListIterator = oGrid.applyLocalSort(  dataListIterator );
            	}
            }
    		
            StringBuilder s = new StringBuilder(200);
            s.append( '{' );
            s.append( oGrid.getId() ); 
            s.append( ":" );
            JavaScriptArrayProvider oJsArrayProvider = new JavaScriptArrayProvider( 
            		dataListIterator,
            		new String[] { grouByLevelField, grouByLevelField + "__value", grouByLevelField + "__count" },
            		reqParam.getStart(),
            		reqParam.getLimit()
            	);
            
            int cnt = groupConnector.getRecordCount();
            oJsArrayProvider.getJSONArray( s, oGrid, oGrid.getGroupBy(), null, null, columnRenderer );
            s.append(",totalCount:").append( cnt );
            s.append('}');
            return s;
        	//oResponse.getWriter().print( s );
        	
    		
    	}
		
	}
    
    
	public DataGroupConnector getGroups( GridPanelRequestParameters reqParam, Iterator<DataRecordConnector> iterator) {
		LinkedHashMap<Object, GroupMapData> groupMap = new LinkedHashMap<Object, GroupMapData>();
		String groupByField = reqParam.getGroupBy()[ reqParam.getGroupByLevel() ];
		while( iterator.hasNext() ) {
			DataRecordConnector dr = iterator.next();
			DataFieldConnector  df = dr.getAttribute( groupByField );
			Object value = df.getValue();
			if( value == null ) {
				value = "";
			}
			GroupMapData groupMapData = groupMap.get( value );
			if( groupMapData != null ) {
				groupMapData.count ++;
			}
			else {
				groupMapData = new GroupMapData();
				groupMapData.count = 1;
				groupMapData.fieldConnector = df;
				groupMapData.value = value;
				groupMapData.displayValue = df.getDisplayValue();
				if( groupMapData.displayValue == null || groupMapData.displayValue.length() == 0 ) {
					groupMapData.displayValue = String.valueOf( value );
				}
				
				groupMap.put(value, groupMapData);
			}
		}
		return new LocalDataGroupConnector( groupMap );
	}
    
	private class LocalDataGroupConnector implements DataGroupConnector {

		protected LinkedHashMap<Object, GroupMapData> groupMap;
		
		public LocalDataGroupConnector( LinkedHashMap<Object, GroupMapData> groupMap ) {
			this.groupMap = groupMap;
		}
		
		@Override
		public DataFieldMetaData getAttributeMetaData(String attributeName) {
			throw new RuntimeException( "Not implemented!" );
		}

		@Override
		public int getPage() {
			return 0;
		}

		@Override
		public int getPageSize() {
			return Integer.MAX_VALUE;
		}

		@Override
		public int getRecordCount() {
			return groupMap.size();
		}

		@Override
		public int getRowCount() {
			return groupMap.size();
		}

		@Override
		public DataListIterator iterator() {
			return new LocalGroupDataListIterator(this);
		}

		@Override
		public void refresh() {
			//
		}

		@Override
		public void setPage(int pageNo) {
			//
		}

		@Override
		public void setPageSize(int pageSize) {
			//
		}
	}
	
	private class LocalGroupDataListIterator implements DataListIterator {
		private int rowIndex = 0;
		private Iterator<Entry<Object,GroupMapData>> mapIterator;
		
		public LocalGroupDataListIterator( LocalDataGroupConnector groupConnector ) {
			mapIterator = groupConnector.groupMap.entrySet().iterator();
		}
		
		@Override
		public boolean skip(int nRows) {
			for( int i=0; i < nRows; i++ ) {
				if( mapIterator.hasNext() ) {
					rowIndex++;
					mapIterator.next();
				} else 
					return false;
				
			}
			return true;
		}

		@Override
		public boolean hasNext() {
			return this.mapIterator.hasNext();
		}

		@Override
		public DataRecordConnector next() {
			rowIndex++;
			return new LocalGroupDataRecordConnector( mapIterator.next(), rowIndex);
		}

		@Override
		public void remove() {
		}
	}
	
	private class LocalGroupDataRecordConnector implements DataRecordConnector {
		
		private int rowIndex;
		private Entry<Object, GroupMapData> record;
		
		private LocalGroupDataRecordConnector( Entry<Object, GroupMapData> record, int rowIndex ) {
			this.rowIndex = rowIndex;
			this.record = record;
			
		}

		@Override
		public DataFieldConnector getAttribute(String name) {
			if( name.endsWith("__count") )
				return new LocalGroupDataFieldConnector( 
						record.getValue(), netgest.bo.xwc.components.classic.grid.GridPanelJSonRenderer.LocalGroupDataFieldConnector.Type.COUNTER 
				);
			else if ( name.endsWith("__value") ) 
				return new LocalGroupDataFieldConnector( 
						record.getValue(), netgest.bo.xwc.components.classic.grid.GridPanelJSonRenderer.LocalGroupDataFieldConnector.Type.VALUE 
				);
		
			return new LocalGroupDataFieldConnector( 
					record.getValue(), netgest.bo.xwc.components.classic.grid.GridPanelJSonRenderer.LocalGroupDataFieldConnector.Type.DISPLAY_VALUE 
			);
		}

		@Override
		public int getRowIndex() {
			return rowIndex;
		}

		@Override
		public byte getSecurityPermissions() {
			return 0;
		}
	}
	
	private static class LocalGroupDataFieldConnector implements DataFieldConnector {
		
		public static enum Type {
			COUNTER,
			VALUE,
			DISPLAY_VALUE
		}
		
		public GroupMapData mapEntryData;
		public Type			type;
		
		public LocalGroupDataFieldConnector( GroupMapData mapEntryData, Type type ) {
			this.mapEntryData = mapEntryData;
			this.type = type;
		}
		
		@Override
		public DataListConnector getDataList() {
			return mapEntryData.fieldConnector.getDataList();
		}

		@Override
		public String[] getDependences() {
			return mapEntryData.fieldConnector.getDependences();
		}

		@Override
		public boolean getDisabled() {
			return mapEntryData.fieldConnector.getDisabled();
		}

		@Override
		public String getDisplayValue() {
			switch( type ) {
				case COUNTER:
					return String.valueOf( mapEntryData.count );
				case DISPLAY_VALUE:
					return String.valueOf( mapEntryData.displayValue );
				default: 
					return null;
			}
		}

		@Override
		public String getInvalidMessage() {
			return mapEntryData.fieldConnector.getInvalidMessage();
		}

		@Override
		public boolean getIsLovEditable() {
			return mapEntryData.fieldConnector.getIsLovEditable();
		}

		@Override
		public boolean getOnChangeSubmit() {
			return mapEntryData.fieldConnector.getOnChangeSubmit();
		}

		@Override
		public boolean getRecomended() {
			return mapEntryData.fieldConnector.getRecomended();
		}

		@Override
		public boolean getRequired() {
			return mapEntryData.fieldConnector.getRequired();
		}

		@Override
		public byte getSecurityPermissions() {
			return mapEntryData.fieldConnector.getSecurityPermissions();
		}

		@Override
		public Object getValue() {
			return mapEntryData.value;
		}

		@Override
		public boolean getVisible() {
			return mapEntryData.fieldConnector.getVisible();
		}

		@Override
		public void setValue(Object newValue) {
			mapEntryData.value = newValue;
		}

		@Override
		public boolean validate() {
			return mapEntryData.fieldConnector.validate();
		}

		@Override
		public byte getDataType() {
			switch( type ) {
				case COUNTER:
					return DataFieldTypes.VALUE_NUMBER;
				case DISPLAY_VALUE:
					return DataFieldTypes.VALUE_CHAR;
				default: 
					return DataFieldTypes.VALUE_CHAR;
					//return mapEntryData.fieldConnector.getDataType();
			}
		}

		@Override
		public int getDecimalPrecision() {
			return 0;
		}

		@Override
		public byte getInputRenderType() {
			switch( type ) {
				case COUNTER:
				case DISPLAY_VALUE:
					return DataFieldTypes.RENDER_DEFAULT;
				default: 
					return mapEntryData.fieldConnector.getInputRenderType();
			}
		}

		@Override
		public boolean getIsLov() {
			switch( type ) {
				case COUNTER:
				case DISPLAY_VALUE:
					return false;
				default: 
					return mapEntryData.fieldConnector.getIsLov();
			}
		}

		@Override
		public String getLabel() {
			return mapEntryData.fieldConnector.getLabel();
		}

		@Override
		public Map<Object, String> getLovMap() {
			return mapEntryData.fieldConnector.getLovMap();
		}

		@Override
		public int getMaxLength() {
			return mapEntryData.fieldConnector.getMaxLength();
		}

		@Override
		public int getMinDecimals() {
			return mapEntryData.fieldConnector.getMinDecimals();
		}

		@Override
		public boolean getNumberGrouping() {
			return mapEntryData.fieldConnector.getNumberGrouping();
		}

		@Override
		public double getNumberMaxValue() {
			return mapEntryData.fieldConnector.getNumberMaxValue();
		}

		@Override
		public double getNumberMinValue() {
			return mapEntryData.fieldConnector.getNumberMinValue();
		}
	}
	
	private class GroupMapData {
		private Object value;
		private String displayValue;
		private int	   count;
		private DataFieldConnector fieldConnector;
	}
	
}
