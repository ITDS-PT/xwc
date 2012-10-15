package netgest.bo.xwc.components.classic.grid.jquery;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import netgest.bo.xwc.components.classic.grid.GridPanelJSonFiltersBuilder;
import netgest.bo.xwc.components.classic.grid.GridPanelJSonFiltersBuilder.ValueType;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchRow.JOIN_OPERATOR;
import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchRow.VALUE_OPERATOR;
import netgest.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Decodes the terms sent for search
 * 
 * @author PedroRio
 *
 */
public class JqGridAdvancedSearchTermsDecoder {

	private enum GroupOperator{
		AND,
		OR,
		NONE;
		
		
		
		public static GroupOperator fromString(String operator){
			for (GroupOperator op : values()){
				if (op.name().equalsIgnoreCase( operator ))
					return op;
			}
			return NONE;
		}
		
		
	}
	
	
	
	private SearchGroup searchGroup = null;
	
	
	public JqGridAdvancedSearchTermsDecoder(JSONObject object, Map<String,DataFieldMetaData> metadata){
		this.searchGroup = new SearchGroup( object, metadata );
	}
	
	public JqGridAdvancedSearchTermsDecoder(String parameters, Map<String,DataFieldMetaData> metadata){
		try {
			if (StringUtils.hasValue( parameters ) )
				this.searchGroup = new SearchGroup( new JSONObject(parameters), metadata );
		} catch ( JSONException e ) {
			throw new IllegalArgumentException( String.format( "Could not parse %s as a JSON Object", parameters) );
		}
	}
	
	public String getTermsForGridPanel(){
		if (searchGroup != null)
			return searchGroup.toGridPanelJSonFormat();
		return null;
	}
	
	
	private enum SearchOperator{
		EQUAL("eq","="),
		NOT_EQUAL("ne", "<>"),
		LESS("lt","<"),
		LESS_OR_EQUAL("le","<="),
		GREATER("gt",">"),
		GREATER_OR_EQUAL("ge",">="),
		IS_NULL("nu"," IS NULL "),
		NOT_NULL("nn"," IS NOT NULL "),
		IS_IN("in", " IN "),
		IS_NOT_IN("ni", " NOT IN "),
		NONE("","");
		;
		
		private String name;
		
		private String realOperator;
		
		private SearchOperator(String name, String operator){
			this.name = name;
			this.realOperator = operator;
		}
		
		public static SearchOperator fromString(String operator){
			for (SearchOperator op : values()){
				if (op.getName().equalsIgnoreCase( operator ))
					return op;
			}
			throw new IllegalArgumentException( "Could not convert " + operator );
		}
		
		public String getOperator(){
			return this.realOperator;
		}
		
		public String getName(){
			return name;
		}
	}
	
	
	/**
	 * 
	 * Represents a Search Group  
	 *
	 */
	private class SearchGroup{
		
		private JSONObject originalFilters;
		
		private GroupOperator operator = GroupOperator.NONE;
		
		private Map<String,DataFieldMetaData> metadata;
		
		private List<SearchRule> rules = new LinkedList<JqGridAdvancedSearchTermsDecoder.SearchRule>();
		
		private List<SearchGroup> subGroups = new LinkedList<JqGridAdvancedSearchTermsDecoder.SearchGroup>();
		
		private List<SearchRule> getRuleList(){
			return rules;
		}
		
		private List<SearchGroup> getSubGroupList(){
			return subGroups;
		}
		
		private String getOption(String key){
			try {
				return originalFilters.getString( key );
			} catch ( JSONException e ) {
				return null;
			}
		}
		
		private JSONArray getRules(){
			try {
				return originalFilters.getJSONArray( "rules" );
			} catch ( JSONException e ) {
				return new JSONArray();
			}
		}
		
		private JSONArray getSubGroups(){
			try {
				return originalFilters.getJSONArray( "groups" );
			} catch ( JSONException e ) {
				return new JSONArray();
			}
		}
		
		public SearchGroup(JSONObject filters, Map<String,DataFieldMetaData> metadata){
			this.originalFilters = filters;
			this.operator = GroupOperator.fromString( getOption( "groupOp" ) );
			this.metadata = metadata;
			processRules(getRules());
			processSubGroups(getSubGroups());
		}
		
		private void processSubGroups( JSONArray subGroups2 ) {
			
			for (int k = 0 ; k < subGroups2.length() ; k++){
				try {
					JSONObject current = subGroups2.getJSONObject( k );
					SearchGroup newSubGroup = new SearchGroup( current, metadata );
					subGroups.add( newSubGroup );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
				
			}
			
		}

		private void processRules( JSONArray rules2 ) {
			
			for (int k = 0 ; k < rules2.length() ; k++){
				try {
					JSONObject current = rules2.getJSONObject( k );
					String field = current.getString( "field" );
					String operator = current.getString( "op" );
					String data = current.getString( "data" );
					SearchRule rule = new SearchRule( field, data, operator );
					rules.add( rule );
				} catch ( JSONException e ) {
					e.printStackTrace();
				}
				
			}
			
		}

		public String toGridPanelJSonFormat(){
			
			GridPanelJSonFiltersBuilder filterBuilder = new GridPanelJSonFiltersBuilder();
			
			boolean ruleWasProcessed = false;
			openBracket( filterBuilder ); 
			for (Iterator<SearchRule> it = rules.iterator(); it.hasNext() ; ){
				
				SearchRule group = it.next();
				
				filterBuilder.addFilter( group.getField()  , 
						getTypeFromName( group.getField(), metadata ), 
						 JOIN_OPERATOR.NONE , 
						decodeValueOperator(group.getOperator()), 
						group.getValue() );
				
				ruleWasProcessed = true;
				
				if (it.hasNext()){
					addJoin( filterBuilder );
				}
				
			}
			
			addJoinBeforeSubGroups( filterBuilder, ruleWasProcessed );
			
			for (Iterator<SearchGroup> it = this.subGroups.iterator() ; it.hasNext() ; ){
				
				openBracket( filterBuilder );
				renderSubGroup(it.next(),filterBuilder);
				closeBracket( filterBuilder );
				if (it.hasNext()){
					addJoin( filterBuilder );
				}
			}
			
			closeBracket( filterBuilder ); 
			return filterBuilder.serializeJSON();
			
			
		}

		private void addJoinBeforeSubGroups( GridPanelJSonFiltersBuilder filterBuilder, boolean ruleWasProcessed ) {
			if (ruleWasProcessed){
				if (this.subGroups.size() > 0)
					addJoin( filterBuilder );
			}
		}
		
		private void renderSubGroup(SearchGroup groupToRender, GridPanelJSonFiltersBuilder filterBuilder){
			
			Iterator<SearchRule> it = groupToRender.getRuleList().iterator();
			openBracket( filterBuilder ); 
			while (it.hasNext()){
				
				SearchRule group = it.next();
				
				filterBuilder.addFilter( group.getField()  , 
						getTypeFromName( group.getField(), metadata ), 
						JOIN_OPERATOR.NONE.getLabel(), 
						decodeValueOperator(group.getOperator()), 
						group.getValue() );
				
				for (Iterator<SearchGroup> it2 = groupToRender.getSubGroupList().iterator() ; it2.hasNext() ; ){
					openBracket( filterBuilder );
					renderSubGroup(it2.next(),filterBuilder);
					closeBracket( filterBuilder ); 
				}
				
				if (it.hasNext()){
					addJoin( filterBuilder );
				}
				
			}
			closeBracket( filterBuilder ); 
			
		}

		private void addJoin( GridPanelJSonFiltersBuilder filterBuilder ) {
			switch (operator){
				case AND :	joinWithAND( filterBuilder ); break; 
				case OR  :  joinWithOR( filterBuilder ); break;
				default  : break;
			}
		}

		private void joinWithOR( GridPanelJSonFiltersBuilder filterBuilder ) {
			filterBuilder.addFilter( "", ValueType.NONE, JOIN_OPERATOR.OR, "", "" );
		}

		private void joinWithAND( GridPanelJSonFiltersBuilder filterBuilder ) {
			filterBuilder.addFilter( "", ValueType.NONE, JOIN_OPERATOR.AND, "", "" );
		}

		private void openBracket( GridPanelJSonFiltersBuilder filterBuilder ) {
			filterBuilder.addFilter( "", ValueType.NONE, JOIN_OPERATOR.OPEN_BRACKET, "", "" );
		}

		private void closeBracket( GridPanelJSonFiltersBuilder filterBuilder ) {
			filterBuilder.addFilter( "", ValueType.NONE, JOIN_OPERATOR.CLOSE_BRACKET, "", "" );
		}
		
		private String decodeValueOperator( SearchOperator operator2 ) {
			switch (operator2){
				case EQUAL : return String.valueOf( VALUE_OPERATOR.EQUAL.getCode() );
				case NOT_EQUAL : return String.valueOf( VALUE_OPERATOR.DIFFERENT.getCode() );
				case GREATER: return String.valueOf( VALUE_OPERATOR.BIGGER.getCode() );
				case GREATER_OR_EQUAL: return String.valueOf( VALUE_OPERATOR.BIGGER_OR_EQUAL.getCode() );
				case LESS: return String.valueOf( VALUE_OPERATOR.LESS.getCode() );
				case LESS_OR_EQUAL: return  String.valueOf( VALUE_OPERATOR.LESS_OR_EQUAL.getCode() );
				case IS_IN : return  String.valueOf( VALUE_OPERATOR.IN.getCode() );
				case IS_NOT_IN : return  String.valueOf( VALUE_OPERATOR.NOT_CONTAINS.getCode() );
				case IS_NULL : return  String.valueOf( VALUE_OPERATOR.NOT_CONTAINS_DATA.getCode() );
				case NOT_NULL : return  String.valueOf( VALUE_OPERATOR.CONTAINS_DATA.getCode() );
				default : return String.valueOf( VALUE_OPERATOR.EQUAL.getCode() );
			}
		}

		private ValueType getTypeFromName(String name, Map<String,DataFieldMetaData> metadataMap){
			
				DataFieldMetaData metadata = metadataMap.get( name );
				switch(metadata.getDataType()){
					case DataFieldTypes.VALUE_NUMBER : return ValueType.NUMERIC;
					case DataFieldTypes.VALUE_BOOLEAN : return ValueType.BOOLEAN;
					case DataFieldTypes.VALUE_DATE : return ValueType.DATE;
					case DataFieldTypes.VALUE_DATETIME : return ValueType.DATE;
					case DataFieldTypes.VALUE_CHAR : return ValueType.STRING;
					default : return ValueType.STRING;
				}
				
		}
	}
	
	
	
	
	/**
	 * Represents a search rule
	 *
	 */
	private class SearchRule{
		
		private String field;
		
		private String value;
		
		private SearchOperator operator;
		
		public SearchRule(String field, String value, String operator){
			this.field = field;
			this.value = value;
			this.operator = SearchOperator.fromString( operator );
		}

		public String getField() {
			return field;
		}

		public String getValue() {
			return value;
		}

		public SearchOperator getOperator() {
			return operator;
		}
		
		public String toString(){
			return new StringBuilder(40)
				.append(getField())
				.append(" ")
				.append( getOperator().getOperator() )
				.append("'")
				.append( getValue() )
				.append("'")
				.toString(); 
		}
		
		
		
	}
	

}
