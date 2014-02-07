package netgest.bo.xwc.components.connectors;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import netgest.bo.data.DataSet;
import netgest.bo.data.DriverUtils;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectList.SqlField;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterJoin;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.connectors.SortTerms.SortTerm;
import netgest.bo.xwc.components.connectors.XEOObjectConnector.GenericFieldConnector;
import netgest.bo.xwc.components.connectors.decoder.XEOObjectAttributeDecoder;
import netgest.bo.xwc.components.connectors.helper.CardIDSearch;
import netgest.bo.xwc.components.connectors.helper.CardIDSearchQueryCreator;
import netgest.bo.xwc.components.localization.ConnectorsMessages;
import netgest.bo.xwc.xeo.components.utils.columnAttribute.LovColumnNameExtractor;
import netgest.utils.StringUtils;

public class XEOObjectListConnector implements GroupableDataList, AggregableDataList, List {

	boObjectList oObjectList;
	
	private static final Logger logger = Logger.getLogger( XEOObjectListConnector.class );
	
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
    
    public void setObjectList(boObjectList oObjectList) {
		this.oObjectList=oObjectList;
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
    	setFilterTerms( filterTerms, dutl );
    }
    
    public void setFilterTerms( FilterTerms filterTerms, DriverUtils dutl){
    	if ( filterTerms == null ) {
    		oObjectList.setUserQuery( null, null );
    		return;
    	}
    	StringBuilder query = new StringBuilder();
    	List<Object>  pars  = new ArrayList<Object>();
    	Iterator<FilterJoin> it = filterTerms.iterator();
    	
    	for( ;it.hasNext();  ){ 
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
			byte operator = t.getOperator();
			
			String parVal = "?";
			//Only enter here if we have a value and (IMPORTANT) operator is for comparison with a value (can't be IS NULL / NOT IS NULL)
			if( val != null && (FilterTerms.OPERATOR_CONTAINS != operator && FilterTerms.OPERATOR_NOT_CONTAINS != operator) ) {
				if (!t.isCardIdSearch()){
					if( val instanceof String && !t.isCardIdSearch() ) {
						val = ((String)val).replace( "'" , "''").toUpperCase();
						if( operator == FilterTerms.OPERATOR_NOT_LIKE  || operator == FilterTerms.OPERATOR_LIKE) {
							val = "%" + val + "%";
						}
						else if (operator == FilterTerms.OPERATOR_STARTS_WITH)
							val = val + "%";
						else if (operator == FilterTerms.OPERATOR_ENDS_WITH)
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
					CardIDSearch search = formatCardIdSearch( t.getDataField() , dutl, val ); 
					if (StringUtils.hasValue(search.getBoqlExpression())){
						query.append( sqlExpr );
						parVal = search.getBoqlExpression();
						for (Object param : search.getParameters() ){
							pars.add( "%"+param+"%" );
						}
					} else {
						operator = FilterTerms.OPERATOR_NONE;
					}
				}
			} else {
				if ( StringUtils.hasValue( sqlExpr ) ){
					query.append( sqlExpr );
				}
			}
			
			switch ( operator ) {
				case FilterTerms.OPERATOR_CONTAINS:
					query.append( " IS NOT NULL " );
					break;
				case FilterTerms.OPERATOR_EQUAL:
					query.append( " = " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_NOT_EQUAL:
					query.append( " != " ).append( parVal );
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
				case FilterTerms.OPERATOR_NOT_IN:
					query.append( " NOT IN " ).append( parVal );
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

	/**
	 * 
	 * Prepare a search query for a cardID search, at the moment it only
	 * 
	 * @param columnName
	 * @param utils
	 * @return
	 */
	protected CardIDSearch formatCardIdSearch(String columnName, DriverUtils utils, Object parameter)  {
		String objectName = "Unknown";
		try {
			boDefHandler parentDefinition = this.oObjectList.getBoDef(); 
			objectName = parentDefinition.getName();
			boDefAttribute attributeDef = new XEOObjectAttributeDecoder(parentDefinition).decode( columnName );
			if (attributeDef != null)
				return new CardIDSearchQueryCreator( utils , attributeDef ).formatCardIdSearch( parameter );
		} catch ( boRuntimeException e ) {
			logger.warn( "Could not parse CardID search for %s of object %s", e, objectName, columnName );
		}
		return CardIDSearch.NULL;
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
		DataFieldMetaData result = null;
		try {
			boDefHandler parentDefinition = this.oObjectList.getBoDef(); 
			boDefAttribute oAtt = parentDefinition.getAttributeRef( attributeName );
			
			if( oAtt != null ) {
				result = new XEOObjectAttributeMetaData( oAtt );
			} else if ( attributeIsLov( attributeName ) ) {
				result = findAttributeMetadataForLov( attributeName , result );
			} else {
				result = findAttributeMetadataInExternalObject( attributeName ,
						result , parentDefinition );
			}
			
			if( result == null && this.oObjectList.getRslt() != null ) {
				result = findAttributeMetadataInResultSet( attributeName ,
						result );
			}
			
			return result;

		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}

	private DataFieldMetaData findAttributeMetadataInResultSet(
			String attributeName, DataFieldMetaData result)
			throws boRuntimeException {
		DataSet dataSet = this.oObjectList.getRslt().getDataSet();
		int col = dataSet.findColumn( attributeName );
		if( col > 0 ) {
			result = new XEOObjectConnector.GenericFieldConnector( attributeName, null, DataFieldTypes.VALUE_CHAR );
		} else {
			if ("SYS_CARDID".equalsIgnoreCase( attributeName )){
				result = new GenericFieldConnector( this.oObjectList.getBoDef().getLabel(), "", DataFieldTypes.VALUE_CHAR );
			} else if ("SYS_ROWNUM".equalsIgnoreCase( attributeName )){
				result =  new GenericFieldConnector( ConnectorsMessages.ROW_NUM.toString(), "", DataFieldTypes.VALUE_CHAR );
			}
		}
		return result;
	}

	private DataFieldMetaData findAttributeMetadataInExternalObject(
			String attributeName, DataFieldMetaData result,
			boDefHandler parentDefinition) {
		boDefAttribute oAttDefinition = new XEOObjectAttributeDecoder( parentDefinition ).decode( attributeName );
		if (oAttDefinition != null)
			result = new XEOObjectAttributeMetaData(oAttDefinition);
		return result;
	}

	private DataFieldMetaData findAttributeMetadataForLov(String attributeName,
			DataFieldMetaData result) throws boRuntimeException {
		LovColumnNameExtractor extractor = new LovColumnNameExtractor( attributeName );
		String newAttributeName = extractor.extractName();
		boDefAttribute attributeDefinition = this.oObjectList.getBoDef().getAttributeRef( newAttributeName );
		if( attributeDefinition != null ) {
			result = new XEOObjectAttributeMetaData( attributeDefinition );
		}
		return result;
	}

	private boolean attributeIsLov(String attributeName) {
		return LovColumnNameExtractor.isXeoLovColumn( attributeName);
	}

	public void refresh() {
		if (oObjectList.getPage() == -1){
			oObjectList.lastPage();
		}
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

	@Override
	public boolean add(Object arg0) {	
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int arg0, Collection arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object obj) {
		Iterator<DataRecordConnector> it=this.iterator();
		while (it.hasNext())
		{
			if (it.next().equals(obj))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(int row) {
		int i=0;
		Iterator<DataRecordConnector> it=this.iterator();
		while (it.hasNext())
		{
			DataRecordConnector data=it.next();
			if (i==row)
				return data;
			i++; 
		}
		return null;
	}

	@Override
	public int indexOf(Object obj) {
		int i=0;
		Iterator<DataRecordConnector> it=this.iterator();
		while (it.hasNext())
		{
			if (it.next().equals(obj))
				return i;
			i++; 
		}
		return -1;
	}

	@Override
	public boolean isEmpty() {
		return (this.oObjectList.getRecordCount()==0);
	}

	@Override
	public int lastIndexOf(Object obj) {
		int i=0;
		int lastIndexOf=-1;
		Iterator<DataRecordConnector> it=this.iterator();
		while (it.hasNext())
		{
			if (it.next().equals(obj))
				lastIndexOf=i;
			i++; 
		}
		return lastIndexOf;
	}

	@Override
	public ListIterator listIterator() {
		ArrayList<DataRecordConnector> auxArr=new ArrayList<DataRecordConnector>();
		Iterator<DataRecordConnector> it=this.iterator();
		while (it.hasNext())
		{
			auxArr.add(it.next());
		}
		return auxArr.listIterator();
	}

	@Override
	public ListIterator listIterator(int row) {
		int i=0;
		ArrayList<DataRecordConnector> auxArr=new ArrayList<DataRecordConnector>();
		Iterator<DataRecordConnector> it=this.iterator();
		while (it.hasNext())
		{
			if (i>=row)
				auxArr.add(it.next());
			i++;
		}
		return auxArr.listIterator();
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object set(int arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return this.getRowCount();
	}

	@Override
	public List subList(int rowBegin, int rowEnd) {
		int i=0;
		ArrayList<DataRecordConnector> auxArr=new ArrayList<DataRecordConnector>();
		Iterator<DataRecordConnector> it=this.iterator();
		while (it.hasNext())
		{
			if (i>=rowBegin && i<rowEnd)
				auxArr.add(it.next());
			i++;
		}
		return auxArr;
	}

	@Override
	public Object[] toArray() {
		Object[] toRet=null;
		int recCount=this.getRowCount();
		int i = 0;
		if (recCount>0)
		{
			toRet=new Object[recCount];
			Iterator<DataRecordConnector> it=this.iterator();
			while (it.hasNext())
			{
				toRet[i]=it.next();
				i++;
			}
		}
		return toRet;
	}

	@Override
	public Object[] toArray(Object[] arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasMorePages() {
		return oObjectList.haveMorePages();
	}
}
