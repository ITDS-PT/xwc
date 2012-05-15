package netgest.bo.xwc.components.connectors;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import netgest.bo.data.DataSet;
import netgest.bo.data.DriverUtils;
import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectList.SqlField;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterJoin;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.connectors.SortTerms.SortTerm;
import netgest.utils.StringUtils;

public class XEOObjectListConnector implements GroupableDataList, AggregableDataList {

	boObjectList oObjectList;
	
	private HashMap<String, ArrayList<String>> aggregateFields;
	
	public void setAggregateFields(HashMap<String, ArrayList<String>> aggregateFields)
	{
		this.aggregateFields = aggregateFields;
	}
	
	public HashMap<String, ArrayList<String>> getAggregateFields()
	{
		if(this.aggregateFields == null)
		{
			this.aggregateFields = new HashMap<String, ArrayList<String>>();
		}		
		return this.aggregateFields;
	}
	
	public void addAggregateField(String fieldId, String fieldDesc,
			String aggregateType) {
		if (this.aggregateFields == null) {
			this.aggregateFields = new HashMap<String, ArrayList<String>>();
		}

		ArrayList<String> sList = this.aggregateFields.get(fieldId);
		if (sList != null) {
			if(!sList.contains(aggregateType))
			{
				sList.add(aggregateType);
			}
		}
		else
		{
			sList = new ArrayList<String>();
			sList.add(aggregateType);
			this.aggregateFields.put(fieldId, sList);
		}
	}

	public void removeAggregateField(String fieldId, String aggregateType) {
		if (this.aggregateFields != null) {
			ArrayList<String> sList = this.aggregateFields.get(fieldId);
			if (sList != null) {
				if(sList.contains(aggregateType))
				{
					sList.remove(aggregateType);
				}
			}
		}
	}
    
    public boObjectList getObjectList() {
		return oObjectList;
	}
    
	public XEOObjectListConnector( boObjectList oBoObjectList ) {
        this.oObjectList = oBoObjectList;
    }

    public DataListIterator iterator() {
        return new XEOObjectListIterator( this.oObjectList );
    }

    public int getRecordCount() {
    	if( this.oObjectList.haveMorePages() ) {
    		return (int)this.oObjectList.getRecordCount();	
    	}
    	return 
    		((this.oObjectList.getPage()-1) * this.oObjectList.getPageSize()) +  this.oObjectList.getRowCount();
    }

    public int getRowCount() {
        return this.oObjectList.getRowCount();
    }
    
    @Override
    public void setSqlFields(List<SqlField> sqlFields) {
    	if( sqlFields != null )
    		this.oObjectList.setSqlFields( sqlFields.toArray( new SqlField[ sqlFields.size() ] ) );
    	else
    		this.oObjectList.setSqlFields( null );
    }
    
    public List<SqlField> getSqlFields() {
    	SqlField[] fields = this.oObjectList.getSqlFields();
    	if( fields != null ) {
    		return Arrays.asList( fields );
    	}
    	return null; 
    }
    
    public void setSortTerms(SortTerms sortTerms) {
    	StringBuilder sb = new StringBuilder();
    	Iterator<SortTerm> it = sortTerms.iterator();

    	while( it.hasNext() ) {

    		if( sb.length() > 0 )
    			sb.append(',');
    		
    		SortTerm st = it.next();
    		String field = st.getField();
    		
    		// native
    		if( oObjectList.getBOQL().startsWith( "{" ) ) { 
    			try {
    				if( oObjectList.getBoDef().getAttributeRef( field ) != null ) {
    					field = oObjectList.getBoDef().getAttributeRef( field ).getDbName();
    				}
				} catch (boRuntimeException e) {
					throw new RuntimeException( e );
				}
    		}
    		boolean isSqlField = false;
    		
    		SqlField[] sqlFieldsList = this.oObjectList.getSqlFields();
    		if( sqlFieldsList != null ) {
	        	for( SqlField sqlField : sqlFieldsList ) {
					if( field.equals( sqlField.getSqlAlias() ) ) {
						isSqlField = true;
					}
	        	}
    		}
    		String dir   = st.getDirection()==SortTerms.SORT_DESC?"DESC":"";
    		if( isSqlField ) {
    			sb.append('"').append( field ).append('"').append( ' ' ).append( dir );	
    		}
    		else {
    			sb.append( field ).append( ' ' ).append( dir );	
    		}
    	}
    	
    	this.oObjectList.setQueryOrderBy( sb.toString() );
	}
    
    public void setFilterTerms( FilterTerms filterTerms ){
    	EboContext eboCtx = boApplication.currentContext().getEboContext();
    	setFilterTerms( filterTerms, eboCtx );
    }
    
    public void setFilterTerms( FilterTerms filterTerms, EboContext eboCtx ) {
    	
    	DriverUtils dutl = eboCtx.getDataBaseDriver().getDriverUtils();
    	
    	if ( filterTerms == null ) {
    		oObjectList.setUserQuery( null, null );
    		return;
    	}
    	StringBuilder query = new StringBuilder();
    	List<Object>  pars  = new ArrayList<Object>();
    	Iterator<FilterJoin> it = filterTerms.iterator();
    	
    	for( ;it.hasNext();  ) 
    	{ 
    		FilterJoin j = it.next();
    		
    		if( j.previous() != null ) {
    			switch( j.getJoinType() ) 
    			{
	    			case FilterTerms.JOIN_OR:
						query.append( " OR " );
						break;
	    			case FilterTerms.JOIN_AND_NOT:
						query.append( " AND NOT " );
						break;
	    			case FilterTerms.JOIN_AND:
						query.append( " AND " );
						break;
	    			case FilterTerms.JOIN_OPEN_BRACKET:
						query.append( " ( " );
						break;
	    			case FilterTerms.JOIN_CLOSE_BRACKET:
						query.append( " ) " );
						break;	
	    			case FilterTerms.JOIN_AND_OPEN_BRACKET:
						query.append( " AND ( " );
						break;	
	    			case FilterTerms.JOIN_CLOSE_BRACKET_AND:
						query.append( " ) AND " );
						break;	
	    			case FilterTerms.JOIN_CLOSE_BRACKET_AND_OPEN_BRACKET:
						query.append( " ) AND ( " );
						break;
	    			case FilterTerms.JOIN_OR_OPEN_BRACKET:
						query.append( " OR ( " );
						break;	
	    			case FilterTerms.JOIN_CLOSE_BRACKET_OR:
						query.append( " ) OR " );
						break;	
	    			case FilterTerms.JOIN_CLOSE_BRACKET_OR_OPEN_BRACKET:
						query.append( " ) OR ( " );
						break;	
	    			default:
						query.append( "" );
    			}
    		} else {
    			if (j.getJoinType() == FilterTerms.JOIN_OPEN_BRACKET)
    				query.append( " ( " );
    		}
    		
    		
    		
			FilterTerm t =  j.getTerm();
			
			String name = t.getDataField();
			String sqlExpr = t.getSqlExpression();
			
			if( sqlExpr != null ) {
				sqlExpr = "[" + sqlExpr + "]";
			}
			else {
				sqlExpr = name;
			}
			
			Object val 	= t.getValue();
			
			String parVal = "?";
			if( val != null ) {
				if( val instanceof String ) {
					val = ((String)val).replace( "'" , "''").toUpperCase();
					if( t.getOperator() == FilterTerms.OPERATOR_NOT_LIKE  || t.getOperator() == FilterTerms.OPERATOR_LIKE) {
						val = "%" + val + "%";
					}
					else if (t.getOperator() == FilterTerms.OPERATOR_STARTS_WITH)
						val = val + "%";
					else if (t.getOperator() == FilterTerms.OPERATOR_ENDS_WITH)
						val = "%" + val;
					parVal = "?";
					query.append( "UPPER((" + sqlExpr + "))" );
					pars.add( val );
				} else if ( val instanceof Boolean ) {
					val = ((Boolean)val).booleanValue()?"1":"0";
					query.append( sqlExpr );
					parVal = "?";
					pars.add( val );
				} else if ( val instanceof java.util.Date ) {
					val = new Timestamp( ((Date)val).getTime() );
					query.append( dutl.fnTruncateDate( sqlExpr ) );
					parVal = dutl.fnTruncateDate( "?" );
					pars.add( val );
				} else if ( val instanceof Object[] ) {
					
					StringBuilder parValues = new StringBuilder("(");
					query.append( sqlExpr );
					Object[] parArray = (Object[])val;
					
					for( Object parArrayVal : parArray ) {
						if( parValues.length() > 1 )
							parValues.append( ',' );
						parValues.append( '?' );
						
						pars.add( parArrayVal );
					}
					if( parValues.length() == 1 ) {
						parValues.append("NULL");
					}
					parValues.append(")");
					parVal = parValues.toString();
					
				} else {
					query.append( sqlExpr );
					parVal = "?";
					pars.add( val );
				}
			} else {
				if ( StringUtils.hasValue( sqlExpr ) ){
					query.append( sqlExpr );
					parVal = "?";
					pars.add( val );
				}
			}
			
			if (isCheckingForNullPresence( t ) )
				removeLastParameter( pars );
			
			switch ( t.getOperator() ) {
				case FilterTerms.OPERATOR_CONTAINS:
					query.append( " IS NOT NULL " );
					break;
				case FilterTerms.OPERATOR_EQUAL:
					query.append( " = " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_GREATER_OR_EQUAL_THAN:
					query.append( " >= " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_GREATER_THAN:
					query.append( " > " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_LESS_OR_EQUAL_THAN:
					query.append( " <= " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_LESS_THAN:
					query.append( " < " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_LIKE:
					query.append( " LIKE " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_NOT_CONTAINS:
					query.append( " IS NULL " );
					break;
				case FilterTerms.OPERATOR_NOT_LIKE:
					query.append( " NOT LIKE " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_IN:
					query.append( " IN " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_STARTS_WITH:
					query.append( " LIKE " ).append( parVal );
					break;	
				case FilterTerms.OPERATOR_ENDS_WITH:
					query.append( " LIKE " ).append( parVal );
					break;	
				case FilterTerms.OPERATOR_NONE:
					break;
				default: break;
			}
			
    	}
    	
    	if( query.length() > 0 )
    		oObjectList.setUserQuery( query.length() > 0 ? query.toString() : null , pars.toArray() );
    	else
    		oObjectList.setUserQuery( null, null );
    	
    }

	private void removeLastParameter( List<Object> pars ) {
		if (!pars.isEmpty())
			pars.remove( pars.size() - 1);
	}

	private boolean isCheckingForNullPresence( FilterTerm t ) {
		return t.getOperator() == FilterTerms.OPERATOR_CONTAINS || t.getOperator() == FilterTerms.OPERATOR_NOT_CONTAINS;
	}
    
    public DataRecordConnector findByUniqueIdentifier(String sUniqueIdentifier) {
		long boui;
		DataRecordConnector drc;
		drc = null;
		boui = Long.parseLong( sUniqueIdentifier );
		drc = new XEOObjectConnector( boui, indexOf(sUniqueIdentifier) );
		return drc;
	}

	public int getPage() {
		return this.oObjectList.getPage();
		
	}

	public int getPageSize() {
		return this.oObjectList.getPageSize();
	}

	public void setPage(int pageNo) {
		// protected int p_page=1;
	    // protected int p_pagesize=Integer.MAX_VALUE;

		try {
			Field pageField = this.oObjectList.getClass().getDeclaredField( "p_page" );
			pageField.setAccessible( true );
			pageField.setInt( this.oObjectList , pageNo);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}

	public void setPageSize(int pageSize) {

		try {
			Field pageField = this.oObjectList.getClass().getDeclaredField( "p_pagesize" );
			pageField.setAccessible( true );
			pageField.setInt( this.oObjectList , pageSize);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		
	}

	public DataFieldMetaData getAttributeMetaData( String attributeName ) {
		try {
			boDefAttribute oAtt = this.oObjectList.getBoDef().getAttributeRef( attributeName );
			/*
			if( oAtt == null ) {
				boDefHandler bridgeObjectDef = oAtt.getReferencedObjectDef();
				oAtt = bridgeObjectDef.getAttributeRef( attributeName );
			}
			*/
			if( oAtt != null ) {
				return new XEOObjectAttributeMetaData( oAtt );
			} else if ( attributeName.contains("__" )) {
				//In the column definition for the attribute boql's dot syntax is used, but internal transformations
				//use "__" instead of dot syntax, as such the comparison is done against the "__" string. 
				return new XEOObjectAttributeMetaData(XEOObjectConnector.getAttributeDefinitionFromName(attributeName, this.oObjectList.getBoDef()));
			}
			
			
			if( this.oObjectList.getRslt() != null ) {
				DataSet dataSet = this.oObjectList.getRslt().getDataSet();
				int col = dataSet.findColumn( attributeName );
				if( col > 0 ) {
					return new XEOObjectConnector.GenericFieldConnector( attributeName, null, DataFieldTypes.VALUE_CHAR );
				}
			}
			
			
			return null;

		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}

	public void refresh() {
		this.oObjectList.refreshData();
	}

	public void setSearchTerms(String[] columnName, Object[] columnValue) {
	}

	public void setSearchText(String searchText) {
		this.oObjectList.setFullTextSearch( boObjectList.arrangeFulltext( this.oObjectList.getEboContext(), searchText ) );
	}
	
	public DataListConnector getGroupDetails(
			String[] parentGroups,
			Object[] parentValues,
			String groupField,
			int page, 
			int pageSize
		) 
	{
		return new XEOObjectListGroupConnector( 
				this, 
				parentGroups, 
				parentValues, 
				groupField, 
				page, 
				pageSize 
			).getDetails();
	}

	public DataGroupConnector getGroups(
			String[] parentGroups,
			Object[] parentValues, 
			String groupField, 
			int page, 
			int pageSize
		) 
	{
		return new XEOObjectListGroupConnector( this, parentGroups, parentValues, groupField, page, pageSize );
	}

	public int dataListCapabilities() {
		return 
			DataListConnector.CAP_FULLTEXTSEARCH + 
			DataListConnector.CAP_PAGING + 
			DataListConnector.CAP_SORT + 
			DataListConnector.CAP_FILTER +
			DataListConnector.CAP_GROUPING +
			DataListConnector.CAP_SQLFIELDS +
			DataListConnector.CAP_AGGREGABLE;
	}

	@Override
	public int indexOf(String sUniqueIdentifier) {
		int ret = -1;
		//TODO: Bug no  boObjectList com multiplos colunas de order by.
		String orderBy = this.oObjectList.getOrderBy();
		try {
			this.oObjectList.setQueryOrderBy("");
			int lastRow = this.oObjectList.getRow();
			if( this.oObjectList.haveBoui( Long.valueOf( sUniqueIdentifier ) ) ) {
				ret = this.oObjectList.getRow();
			}
			this.oObjectList.moveTo( lastRow );
			return ret;
		}
		finally {
			this.oObjectList.setQueryOrderBy( orderBy );
		}
	}
}
