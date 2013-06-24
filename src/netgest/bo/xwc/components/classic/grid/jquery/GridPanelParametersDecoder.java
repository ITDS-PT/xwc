package netgest.bo.xwc.components.classic.grid.jquery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.grid.Direction;
import netgest.bo.xwc.components.classic.grid.SortTermsDecoder;
import netgest.bo.xwc.components.classic.grid.WebParameter;
import netgest.bo.xwc.components.classic.grid.WebRequest;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.SortTerms;
import netgest.bo.xwc.components.connectors.helper.MetadataConverter;
import netgest.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Decodes the parameters sent by the GridPanel when using the jqGrid
 * plugin
 * 
 * @author PedroRio
 *
 */
public class GridPanelParametersDecoder {
	
	/**
	 * @author PedroRio
	 *
	 */
	public enum Parameter implements WebParameter {
		 WAS_SEARCH_EXECUTED("_search")
		,ORDER("sord")
		,ROWS("rows")
		,PAGE("page")
		,SEARCH_FILTERS("filters")
		,CURRENT_COLUMN("sidx")
		,FULL_TEXT_SEARCH("fullTxt"); //Custom parameter, nor part of jqGrid
		 
		private String name;
		
		private Parameter(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		public String toString(){
			return name;
		}
		
	}
	
	private WebRequest request;
	private String[] columns;
	
	private int page = -1;
	private String currentColumn = "";
	
	public GridPanelParametersDecoder(WebRequest request, String[] columns){
		this.request = request;
		this.columns = columns;
		processGroups();
	}
	
	public boolean wasSearchExecuted(){
		return Boolean.parseBoolean( request.getParameter( Parameter.WAS_SEARCH_EXECUTED ) );
	}
	
	public boolean wasColumnSearched(String columnName){
		return request.getParameter( columnName ) != null;
	}
	
	public String getSearchTerm(String columnName){
		return request.getParameter( columnName );
	}
	
	public Direction getOrder(){
		return Direction.fromString( request.getParameter( Parameter.ORDER ) );
	}
	
	public int getRows(){
		try{
			return Integer.parseInt( request.getParameter( Parameter.ROWS ) );
		} catch (NumberFormatException e){
			e.printStackTrace();
			return page;
		}
	}
	
	public List<String> getGroups(){
		return groups;
	}
	
	private List<String> groups;
	private boolean isGrouped = false;
	
	/**
	 * The groups are sent in the sidx column in the following way:
	 * sdix = COLUMN_GROUPED1 ASC, COLUMN_GROUPEDN DESC, CURRENT_COLUMN
	 */
	private void processGroups(){
		String requestColumn = request.getParameter( Parameter.CURRENT_COLUMN );
		if (StringUtils.hasValue( requestColumn )){
			String[] values = requestColumn.split( "," );
			if (onlyOneColumnInRequest( values )){
				groups = Collections.emptyList();
				currentColumn = values[0];
			} else {
				isGrouped = true;
				groups = new LinkedList<String>();
				for (String column : values){
					String[] currentRequestGroups = column.trim().split( " " );
					if (isValueWithColumnAndIndex( currentRequestGroups )){
						groups.add( currentRequestGroups[0].trim() );
					} else {
						currentColumn = currentRequestGroups[0].trim();
					}
				}
			}
			
		} else {
			groups = Collections.emptyList();
		}

	}
	
	public boolean wasFullTextSearchExecuted(){
		return StringUtils.hasValue( request.getParameter( Parameter.FULL_TEXT_SEARCH ) );
	}
	
	public String getFullText(){
		return request.getParameter( Parameter.FULL_TEXT_SEARCH );
	}
	

	private boolean isValueWithColumnAndIndex( String[] currentRequestGroups ) {
		return currentRequestGroups.length == 2;
	}

	private boolean onlyOneColumnInRequest( String[] values ) {
		return values.length == 1;
	}
	
	public boolean isGrouped(){
		return isGrouped;
	}
	
	public void setGrouped(boolean grouped){
		
	}
	
	public String getCurrentColumn(){
		return currentColumn;
	}
	
	public int getPage(){
		try{
			return Integer.parseInt( request.getParameter( Parameter.PAGE ) );
		} catch (NumberFormatException e){
			e.printStackTrace();
			return page;
		}
		
	}
	
	
	
	public String getSortTermsInternalGridFormat(){
		
		SortTermsDecoder decoder = new SortTermsDecoder();
		decoder.addTerm( getCurrentColumn(), getOrder() );
		return decoder.toGridPanelInternalFormat();
	}
	
	public SortTerms getSortTerms(){
		
		SortTerms terms = new SortTerms();
		Direction order = getOrder();
		
		String columnName = getCurrentColumn();
		if (StringUtils.isEmpty( columnName ))
			return null;
		
		switch (order){
			case ASCENDING :
				terms.addSortTerm( getCurrentColumn(), SortTerms.SORT_ASC );
				break;
			case DESCENDING : 
				terms.addSortTerm( getCurrentColumn(), SortTerms.SORT_DESC );
				break;
			case NONE :
				return null;
			
			default : return null;
		}
		
		return terms;
		
	}
	
	

	public String getFilterTermsInternalGridFormat(GridPanel grid, DataListConnector oDataCon) {
	
		Map<String,String> parameters = new HashMap<String, String>();
		for (String columnName : columns){
			if (StringUtils.hasValue( request.getParameter( columnName ) )){
				parameters.put( columnName, request.getParameter( columnName ) );
			}
		}
		
		JSONObject o = new JSONObject();
		
		Set<Entry<String,String>> entries = parameters.entrySet();
		Iterator<Entry<String,String>> it = entries.iterator();
		
		while (it.hasNext()){
			Entry<String,String> current = it.next();
			JSONObject o1 = new JSONObject();
			DataFieldMetaData fieldMeta = oDataCon.getAttributeMetaData( current.getKey() );
			try{
				String dataType = findAttributeType(fieldMeta);
				o1.put( "active", true );
				o1.put( "type", dataType );
				o1.put( "value", createValueRepresentation( fieldMeta.getDataType(), dataType, current.getValue() ) );
				
				o.put( current.getKey(), o1 );
			} catch (JSONException e ){
				e.printStackTrace();
			}
			
		}
		
		return o.toString();	
	}
	
	protected Object createValueRepresentation(byte type, String dataType, String value){
		
		if ("object".equalsIgnoreCase( dataType )){
			JSONArray array = new JSONArray();
			array.put( value );
			return array;
		}
		
		if (isNumberOrDate( type )){
			JSONArray array = new JSONArray();
			JSONObject o = new JSONObject();
			try {
				if (isNumber(type))
					o.put( "value", Long.valueOf( value ).longValue() );
				else
					o.put( "value", value );
				o.put( "comparison", "eq" );
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
			array.put( o );
			return array;
		} else
			return value;
	}

	

	private boolean isNumber( byte type ) {
		return DataFieldTypes.VALUE_NUMBER == type;
		
	}

	private boolean isNumberOrDate( byte type ) {
		return DataFieldTypes.VALUE_NUMBER == type || DataFieldTypes.VALUE_DATE == type || DataFieldTypes.VALUE_DATETIME == type;
	}

	protected String findAttributeType(DataFieldMetaData meta) {
		return new MetadataConverter( meta ).getDataTypeAsString();
	}

	private String advancedSearchTerms = null;
	
	private String processAdvancedSearchTerms(Map<String, DataFieldMetaData> metadata){
		
		String parameter = request.getParameter( Parameter.SEARCH_FILTERS );
		return new JqGridAdvancedSearchTermsDecoder( parameter, metadata ).getTermsForGridPanel();
	}
	
	public String getAdvancedSearchTermsAsJson(DataListConnector connector){
		if (advancedTermsNotProcessed()){
			Map<String,DataFieldMetaData> metadata = new HashMap<String, DataFieldMetaData>();
			for (String column : columns){
				DataFieldMetaData metadataAttribute = connector.getAttributeMetaData( column );
				if (metadataAttribute != null)
					metadata.put( column, metadataAttribute );
			}
			advancedSearchTerms = processAdvancedSearchTerms(metadata);
		}
		return advancedSearchTerms;
	}

	private boolean advancedTermsNotProcessed() {
		return advancedSearchTerms == null;
	}
	
}
