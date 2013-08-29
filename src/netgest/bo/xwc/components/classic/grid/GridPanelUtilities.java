package netgest.bo.xwc.components.classic.grid;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.grid.utils.DataFieldDecoder;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterJoin;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridPanelUtilities {

	private GridPanel gridPanel;
	
	public GridPanelUtilities(GridPanel panel)
	{
		this.gridPanel = panel;
	}
	
	/**
	 * 
	 * Retrieves a column given its name. If the column does not exist retrusn a dummy column
	 * so that testing is possible without the context
	 * 
	 * @param name
	 * @return
	 */
	protected Column getColumn( String name ){
		if (gridPanel != null){
			name = DataFieldDecoder.convertForBOQL( name );
			return gridPanel.getColumn( name );
		}
		return new Column() {
			
			@Override
			public boolean wrapText() {
				return false;
			}
			
			@Override
			public void setHidden( String hiddenExpr ) {
			}
			
			@Override
			public boolean isSortable() {
				return false;
			}
			
			@Override
			public boolean isSearchable() {
				return false;
			}
			
			@Override
			public boolean isResizable() {
				return false;
			}
			
			@Override
			public boolean isHideable() {
				return false;
			}
			
			@Override
			public boolean isHidden() {
				return false;
			}
			
			@Override
			public boolean isGroupable() {
				return false;
			}
			
			@Override
			public boolean isEnableAggregate() {
				return false;
			}
			
			@Override
			public boolean isContentHtml() {
				return false;
			}
			
			@Override
			public String getWidth() {
				return null;
			}
			
			@Override
			public String getSqlExpression() {
				return null;
			}
			
			@Override
			public byte getSecurityPermissions() {
				return 0;
			}
			
			@Override
			public String getLookupViewer() {
				return null;
			}
			
			@Override
			public String getLabel() {
				return null;
			}
			
			@Override
			public String getDataField() {
				return null;
			}
			
			@Override
			public String getAlign() {
				return null;
			}
			
			@Override
			public String applyRenderTemplate( Object value ) {
				return null;
			}

			@Override
			public boolean useValueOnLov() {
				return true;
			}
		};
	}
		
	/**
	 * 
	 * Create GridPanel FilterTerms from a JSON representation (the terms for the advanced search)
	 * 
	 * @param jsonFilters The string json representation
	 * @return
	 */
	public FilterTerms createAdvancedFilterTerms(String jsonFilters){
		
		FilterTerms terms = null;
		if ( StringUtils.hasValue( jsonFilters ) ){
			
			try{
			
			JSONArray advancedFilter = new JSONArray( jsonFilters );
			for (int i = 0 ; i < advancedFilter.length() ; i++){
				JSONObject currRow = advancedFilter.getJSONObject( i );
				
				String name = currRow.getString("name");
				String submitedType = currRow.getString("type");
				boolean active = currRow.getBoolean("active");
				String joinOperator = currRow.getString( "joinOperator" );
				String valueOperator = currRow.getString( "valueOperator" );
				Column col = getColumn( name );
				String sqlExpression = null;
				if (col != null){
					sqlExpression = col.getSqlExpression();
				}
				
				byte joinOpParam = FilterTerms.JOIN_NONE;
				byte valueOpParam = FilterTerms.OPERATOR_NONE;
				if (StringUtils.hasValue( ( joinOperator ) ))
					joinOpParam = FilterTerms.getJoinOperatorFromCode( Integer.valueOf( joinOperator) ); 
				if (StringUtils.hasValue( ( valueOperator ) ))
					valueOpParam = FilterTerms.getValueOperatorFromCode( Integer.valueOf( valueOperator ) );
				

				if( active ) {
					boolean bAddCodition = true;

					Object value = null;
					Byte operator = valueOpParam;

					
					if ("object".equals(submitedType)) {
						List<String> valuesList = new ArrayList<String>();

						String currValue = currRow.getString("value");
						if (StringUtils.hasValue( currValue )){
							valuesList.add( currValue );
							value = valuesList.toArray();
						}
						
						valueOpParam = fixOperatorForObjectType( valueOpParam, submitedType );
						
						if (valuesList.size() == 0 && checkingForData( valueOpParam ) ) {
							bAddCodition = false;
						}

					} else if ("list".equals(submitedType)) {
						List<BigDecimal> valuesList = new ArrayList<BigDecimal>();
						String values = currRow.getString("value");
						if (StringUtils.hasValue( values )){	
							String[] valArray = values.split( "," );
							for ( String currValue : valArray ) {
								valuesList.add( new BigDecimal(currValue) );
							}
							value = valuesList.toArray();
							operator = fixOperatorForObjectType( valueOpParam, submitedType );
	
							if (valuesList.size() == 0 && checkingForData( valueOpParam )) {
								bAddCodition = false;
							}
						}

					} else if ("string".equals(submitedType)) {
//						if (StringUtils.hasValue( currRow.getString("value") ))
//							value = currRow.getString("value");
						String val = currRow.getString("value");
						if ( val != null && val.length() > 0 ) {
							value = val;
						}
					} else if ("date".equals(submitedType)) {
						if (StringUtils.hasValue( currRow.getString("value") )){
							value = currRow.getString("value");
							Long date = Long.valueOf( value.toString() );
							value = new Date(date);
							
							terms = addFilterTerm(terms, name, sqlExpression, operator, value, joinOpParam);
							bAddCodition = false;
						}
					} else if ( "boolean".equals( submitedType) ) {
						String valueToCheck = currRow.getString( "value" );
						if (StringUtils.hasValue( valueToCheck )){
							if ("1".equals( valueToCheck ))
								value = Boolean.TRUE;
							else if ("0".equals( valueToCheck ))
								value = Boolean.FALSE;
							else
								value = Boolean.valueOf( valueToCheck );
						}
					} else if ("numeric".equals( submitedType) ) {
						if (currRow.has( "value" ) && StringUtils.hasValue( currRow.getString( "value" ) )){
							value = new BigDecimal( currRow.getString( "value" ) );
						}
						terms = addFilterTerm( terms, name, sqlExpression , operator, value, joinOpParam );
						bAddCodition = false;
					} else {
						value = null;
					}

					if (bAddCodition) {
						terms = addFilterTerm(terms, name, sqlExpression , operator, value, joinOpParam);
					}
				}
			}
				
		}
		catch (JSONException e ){
			e.printStackTrace();
		}
		}
		return terms;
	}
	
	
	/**
	 * Checks whether the operator being used is to be matched against real data or just agaainst null/not null
	 * 
	 * @param valueOpParam
	 * @return
	 */
	private boolean checkingForData( byte valueOpParam ) {
		if (valueOpParam == FilterTerms.OPERATOR_CONTAINS || valueOpParam == FilterTerms.OPERATOR_NOT_CONTAINS)
			return false;
		else
			return true;
	}

	private byte fixOperatorForObjectType( byte valueOpParam, String submitedType ) {
		if (valueOpParam != FilterTerms.OPERATOR_CONTAINS && valueOpParam != FilterTerms.OPERATOR_NOT_CONTAINS){
			if (valueOpParam == FilterTerms.OPERATOR_NOT_EQUAL)
				return FilterTerms.OPERATOR_NOT_IN;
			return FilterTerms.OPERATOR_IN;
		}
		else
			return valueOpParam;
	}

	private FilterTerms addFilterTerm( FilterTerms terms, String name, String sqlExpression, byte operator, Object value, byte joinOperator ) {
		if (terms == null) {
			FilterTerm f = new FilterTerm( name, sqlExpression, operator, value);
			terms = new FilterTerms( f, joinOperator ); 
		} else {
			terms.addTerm( joinOperator , name, sqlExpression,
					operator, value);
		}
		return terms;
	}
	
	private FilterTerms addFilterTerm( FilterTerms terms, String name, String sqlExpression, byte operator, Object value, byte joinOperator, boolean cardIdSearch ) {
		if (terms == null) {
			FilterTerm f = new FilterTerm( name, sqlExpression, operator, value);
			if (cardIdSearch)
				f.enableCardIdSearch();
			terms = new FilterTerms( f, joinOperator ); 
		} else {
			terms.addTerm( joinOperator , name, sqlExpression,
					operator, value, cardIdSearch);
		}
		return terms;
	}

	/**
	 * 
	 * Create the filter terms for the simple filters
	 * 
	 * @param currentFilters
	 * @return
	 */
	public FilterTerms createSimpleFilterTerms( String currentFilters ) {
		FilterTerms terms = null;
		
		if (currentFilters == null)
			return terms;
		
		try {
			JSONObject jFilters = new JSONObject(currentFilters);
			String[] names = JSONObject.getNames(jFilters);
			if (names != null) {
	
				for (String nameCol : names) {
					if (getColumn( nameCol ) == null)
						continue;
					
					JSONObject jsonColDef = jFilters.getJSONObject( nameCol );
					String submitedType = jsonColDef.getString("type");
	
					boolean active = jsonColDef.getBoolean("active");
	
					if( active ) {
						boolean bAddCodition = true;
	
						Object value = null;
						Byte operator = null;
	
						if ("object".equals(submitedType)) {
							List<String> valuesList = new ArrayList<String>();
							
							JSONArray jArray = jsonColDef.optJSONArray("value");
							if (jArray != null) {
								for (int z = 0; z < jArray.length(); z++) {
									valuesList.add(jArray.getString(z));
								}
								value = valuesList.toArray();
								operator = FilterTerms.OPERATOR_IN;
							}
							if (valuesList.size() == 0 && !jsonColDef.optBoolean( "cardIdSearch" )) {
								bAddCodition = false;
								value = null;
							}
							
							if (jsonColDef.optBoolean( "cardIdSearch" )){
								value = jsonColDef.get( "value" );
								operator = FilterTerms.OPERATOR_IN;
							}
							
							operator = checkForContainsOperator( jsonColDef , operator );
							
							if (isOperatorForContainsFilter( operator ))
								terms = addFilterTerm(terms, nameCol, getColumn( nameCol ).getSqlExpression(),operator, value, FilterTerms.JOIN_AND);
	
						} else if ("list".equals(submitedType)) {
							List<String> valuesList = new ArrayList<String>();
							JSONArray jArray = jsonColDef
									.getJSONArray("value");
	
							for (int z = 0; z < jArray.length(); z++) {
								valuesList.add(jArray.getString(z));
							}
							value = valuesList.toArray();
							operator = FilterTerms.OPERATOR_IN;
	
							if (valuesList.size() == 0) {
								bAddCodition = false;
								value = null;
							}
							
							operator = checkForContainsOperator( jsonColDef , operator );
							if (isOperatorForContainsFilter( operator ))
								terms = addFilterTerm(terms, nameCol, getColumn( nameCol ).getSqlExpression(),operator, value, FilterTerms.JOIN_AND);
	
						} else if ("string".equals(submitedType)) {
//							if (StringUtils.hasValue( jsonColDef.getString("value") ))
//								value = jsonColDef.getString("value");
							
							String val = jsonColDef.getString("value") ;
							if ( val != null && val.length() > 0 ) {
								value = val;
							}
							
							operator = FilterTerms.OPERATOR_LIKE;
							operator = checkForContainsOperator( jsonColDef , operator );
						} else if ("date".equals(submitedType)) {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"dd/MM/yyyy");
							
							JSONArray jArray = jsonColDef.getJSONArray("value");
							for( int i=0; i<jArray.length(); i++ ) {
								
								JSONObject jsonColFilter = jArray.getJSONObject( i );
								try {
									value = sdf.parse( jsonColFilter.getString( "value" ) );
								} catch (ParseException e) {
									e.printStackTrace();
									value = null;
								}
								String comp = jsonColFilter
										.getString("comparison");
								if ("lt".equals(comp))
									operator = FilterTerms.OPERATOR_LESS_THAN;
								else if ("eq".equals(comp))
									operator = FilterTerms.OPERATOR_EQUAL;
								else
									operator = FilterTerms.OPERATOR_GREATER_THAN;
								
								operator = checkForContainsOperator( jsonColDef , operator );
								terms = addFilterTerm(terms, nameCol, getColumn( nameCol ).getSqlExpression(),operator, value, FilterTerms.JOIN_AND);
							}
							
							operator = checkForContainsOperator( jsonColDef , operator );
							if (isOperatorForContainsFilter( operator ))
								terms = addFilterTerm(terms, nameCol, getColumn( nameCol ).getSqlExpression(),operator, value, FilterTerms.JOIN_AND);
							
							bAddCodition = false;
						} else if ("boolean".equals(submitedType)) {
							if (StringUtils.hasValue( jsonColDef.getString("value") ))
								value = Boolean.valueOf(jsonColDef.getString("value"));
							operator = FilterTerms.OPERATOR_EQUAL;
						} else if ("numeric".equals(submitedType)) {
							JSONArray jArray = jsonColDef.getJSONArray("value");
							for( int i=0; i<jArray.length(); i++ ) {
								JSONObject jsonColFilter = jArray.getJSONObject( i );
								String submitedValue = jsonColFilter.getString( "value" );
								
								String comp = jsonColFilter.getString("comparison");
								value = new BigDecimal(submitedValue);
								if ("lt".equals(comp))
									operator = FilterTerms.OPERATOR_LESS_THAN;
								else if ("eq".equals(comp))
									operator = FilterTerms.OPERATOR_EQUAL;
								else
									operator = FilterTerms.OPERATOR_GREATER_THAN;
								
								terms = addFilterTerm(terms, nameCol, getColumn( nameCol ).getSqlExpression(), operator, value, FilterTerms.JOIN_AND);
							}
							operator = checkForContainsOperator( jsonColDef , operator );
							if (isOperatorForContainsFilter( operator ))
								terms = addFilterTerm(terms, nameCol, getColumn( nameCol ).getSqlExpression(), operator, value, FilterTerms.JOIN_AND);
							bAddCodition = false;
						} else {
							value = null;
						}
	
						if (bAddCodition) {
							String sqlExpression = getColumn( nameCol ).getSqlExpression();
							operator = checkForContainsOperator( jsonColDef , operator );
							boolean cardIdSearch = false;
							if (jsonColDef.has( "cardIdSearch" )){
								cardIdSearch = jsonColDef.getBoolean( "cardIdSearch" );
							}
							terms = addFilterTerm(terms, nameCol, sqlExpression ,operator, value, FilterTerms.JOIN_AND, cardIdSearch);
						}
					}
				}
			}
		} catch (JSONException e) {
			// Error reading filters....
			e.printStackTrace();
		}
		return terms;
	}

	protected boolean isOperatorForContainsFilter(Byte operator) {
		return operator != null && (FilterTerms.OPERATOR_CONTAINS == operator || FilterTerms.OPERATOR_NOT_CONTAINS == operator);
	}

	protected Byte checkForContainsOperator(JSONObject jsonColDef,
			Byte operator) throws JSONException {
		Object contains = jsonColDef.opt( "containsData" ); 
		if (contains != null){
			if (JSONObject.NULL != contains){
				if ("true".equalsIgnoreCase( contains.toString()) ){
					operator = FilterTerms.OPERATOR_CONTAINS;
				} else if ("false".equalsIgnoreCase( contains.toString() ) ){
					operator = FilterTerms.OPERATOR_NOT_CONTAINS;
				}
			}
		}
		return operator;
	}
	
	/**
	 * 
	 * Merges Simples filter terms with the advanced search filter terms 
	 * 
	 * @param simple The simple filter terms
	 * @param advanced The terms from the advanced search
	 * @return The merge of the two terms
	 */
	public FilterTerms mergeFilterTerms(FilterTerms simple, final FilterTerms advanced){ 
		
		if ( advanced != null && simple != null ){
			Iterator<FilterJoin> it = advanced.iterator();
			int k = 0;
			while (it.hasNext()){
				FilterJoin curr = it.next();
				byte joinType = getCorrectJoinType( k, curr.getJoinType() );
				k++;
				simple.addTerm( joinType, curr.getTerm() );
			}
			return simple;
		}
		
		if ( simple != null )
			return simple;
		else
			return advanced;
		
	}

	/**
	 * 
	 * When merging the filter, must be careful to add a join so that they both work
	 * at the same time
	 * 
	 * @param k The index of the advanced filter
	 * @param joinType The type of join for the filter
	 * 
	 * @return The correct join type if needed
	 */
	private byte getCorrectJoinType( int k, byte joinType ) {
		if (k == 0){
			if (joinType == FilterTerms.JOIN_NONE)
				return FilterTerms.JOIN_AND;
			else if (joinType == FilterTerms.JOIN_OPEN_BRACKET)
				return FilterTerms.JOIN_AND_OPEN_BRACKET;
		} 
		return joinType;
		
	}

	
	public String getExcelDownloadScript(){
		
		return getDownloadScript( "excel", gridPanel.getRequestContext() );
	}
	
	
	
	private String getDownloadScript(String type, XUIRequestContext context){
		String sActionUrl = context.getAjaxURL();
        
        String sPar = "javax.faces.ViewState=" + context.getViewRoot().getViewState();
        if( sActionUrl.indexOf("?") == -1 ) {
        	sActionUrl += "?" + sPar;
        }
        else {
        	sActionUrl += "&" + sPar;
        }
        sPar = "xvw.servlet=" + this.gridPanel.getClientId();
    	sActionUrl += "&" + sPar;
        sPar = "type=" + type;
    	sActionUrl += "&" + sPar;
        StringBuilder sb = new StringBuilder(100);
        sb.append( "function(){" );
        	sb.append(     		"		XVW.downloadFile('" + sActionUrl + "');");
        sb.append( "}" );
        return sb.toString() ;
	}
	
	public String getPdfDownloadScript(){
		return getDownloadScript( "pdf", gridPanel.getRequestContext() );
	}
	
}
