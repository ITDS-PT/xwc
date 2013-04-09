package netgest.bo.xwc.components.classic.grid;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchRow;
import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchRow.JOIN_OPERATOR;
import netgest.bo.xwc.xeo.advancedSearch.AdvancedSearchRow.VALUE_OPERATOR;
import netgest.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility to create the GridPanel Filters (which have a JSON representing)
 *
 */
public class GridPanelJSonFiltersBuilder {
	
		
	public enum ValueType{
		STRING("string"),
		NUMERIC("numeric"),
		DATE("date"),
		BOOLEAN("boolean"),
		OBJECT("object"),
		LIST("list"),
		NONE(""),;
		
		private String label;
		
		public String getLabel(){
			return label;
		}
		
		private ValueType(String label){
			this.label = label;
		}
		
		public static ValueType fromLabel(String label){
			
			if (label == null)
				return NONE;
			
			for (ValueType curr : values()){
				if (curr.getLabel().equalsIgnoreCase( label ))
					return curr;
			}
			
			return NONE;
		}
	}
	
	public JSONArray filters;

	public GridPanelJSonFiltersBuilder() {
		this.filters = new JSONArray();
	}
	
	private List<Filter> filtersExisting = new LinkedList<Filter>();
	
	public GridPanelJSonFiltersBuilder(String filterJson){
		try{
			if (StringUtils.hasValue( filterJson )){
				JSONArray filters = new JSONArray( filterJson );
				for (int i = 0 ; i < filters.length() ; i++){
					JSONObject currentObj = filters.getJSONObject( i );
					ValueType type = ValueType.fromLabel( currentObj.getString( "type" ) );
					Filter newFilter = new Filter( 
							currentObj.getString( "name" ), 
							getCorrectValue( currentObj, type ), 
							currentObj.getString( "joinOperator" ), 
							currentObj.getString( "valueOperator" ) ,
							type );
					filtersExisting.add( newFilter );
				}
			}
		} catch (JSONException e ){
			
		}
	}

	private Object getCorrectValue( JSONObject currentObj, ValueType type ) throws JSONException {
		String value = currentObj.getString( "value" );
		if (StringUtils.isEmpty( value ))
			return null;
		switch (type){
			case STRING : return value;
			case NUMERIC :
					return new BigDecimal(value);
			case DATE : 
				return new Timestamp( Long.valueOf( value ) );
			case BOOLEAN : 
				return value;
			case OBJECT : 
				return value;
			case LIST : 
				return value;
		default :
			break;
		}
		return value;
	}
	
	
	public void addFilter(String name, ValueType type, JOIN_OPERATOR joinOperator, String valueOperator, Object value){
		addFilter(name, type, String.valueOf( joinOperator.getCode() ), valueOperator, value);
	}
	
	public void addFilter(String name, ValueType type, String joinOperator, String valueOperator, Object value){
		try{
			JSONObject newFilter = new JSONObject();
			addFilterName( name, newFilter );
			newFilter.put( "active", Boolean.TRUE );
			newFilter.put( "type", type.getLabel() );
			
			addJoinOperator( joinOperator, newFilter );
			addValueOperator( valueOperator, newFilter );
			addValue( value, newFilter );
			
			this.filters.put( newFilter );
			
		} catch (JSONException e){
			e.printStackTrace();
		}
	}
	
	
	public void addFilter(String name, ValueType type, JOIN_OPERATOR joinOperator, VALUE_OPERATOR valueOperator, Object value){
		try{
			JSONObject newFilter = new JSONObject();
			addFilterName( name, newFilter );
			newFilter.put( "active", Boolean.TRUE );
			newFilter.put( "type", type.getLabel() );
			
			addJoinOperator( joinOperator, newFilter );
			addValueOperator( valueOperator, newFilter );
			addValue( value, newFilter );
			
			this.filters.put( newFilter );
			
		} catch (JSONException e){
			e.printStackTrace();
		}
	}

	private void addFilterName( String name, JSONObject newFilter ) throws JSONException {
		if (StringUtils.hasValue( name ))
			newFilter.put( "name", name );
		else
			newFilter.put( "name", "" );
	}

	private void addValue( Object value, JSONObject newFilter ) throws JSONException {
		if (value != null){
			if (value instanceof Timestamp)
				newFilter.put( "value", ((Timestamp)value).getTime() );
			else
				newFilter.put( "value", value );
		}
		else
			newFilter.put( "value", "" );
	}

	private void addValueOperator( String valueOperator, JSONObject newFilter ) throws JSONException {
		if (StringUtils.hasValue( valueOperator ))
			newFilter.put( "valueOperator", String.valueOf(AdvancedSearchRow.VALUE_OPERATOR.fromCode( valueOperator ).getCode() ) );
		else
			newFilter.put( "valueOperator", "" );
	}
	
	private void addValueOperator( VALUE_OPERATOR valueOperator, JSONObject newFilter ) throws JSONException {
		newFilter.put( "valueOperator", String.valueOf( valueOperator.getCode() ) );
		
	}

	private void addJoinOperator( String joinOperator, JSONObject newFilter ) throws JSONException {
		if (StringUtils.hasValue( joinOperator ))
			newFilter.put( "joinOperator", String.valueOf(AdvancedSearchRow.JOIN_OPERATOR.fromCode( joinOperator ).getCode()) );
		else
			newFilter.put( "joinOperator" , "" );
	}
	
	private void addJoinOperator( JOIN_OPERATOR joinOperator, JSONObject newFilter ) throws JSONException {
		newFilter.put( "joinOperator", String.valueOf(joinOperator.getCode() ) );
	}
	
	/**
	 * 
	 * Serializes the filters to JSON
	 * 
	 * @return A JSON string with the filters
	 */
	public String serializeJSON(){
		return filters.toString();
	}
	
	public String toString(){
		return filters.toString();
	}
	
	public Iterator<Filter> getFilters(){
		return filtersExisting.iterator();
	}
	
	public class Filter{
		private String name;
		private Object value;
		private String joinOperator;
		private String valueOperator;
		private ValueType type;
		public Filter( String name, Object value, String joinOperator, String valueOperator, ValueType type ) {
			this.name = name;
			this.value = value;
			this.joinOperator = joinOperator;
			this.valueOperator = valueOperator;
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public Object getValue() {
			return value;
		}
		public String getJoinOperator() {
			return joinOperator;
		}
		public String getValueOperator() {
			return valueOperator;
		}
		
		public ValueType getType(){
			return type;
		}
		
		
	}
}