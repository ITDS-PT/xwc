package netgest.bo.xwc.components.connectors;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import netgest.bo.data.DriverUtils;
import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterJoin;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.connectors.SortTerms.SortTerm;

public class XEOObjectListConnector implements DataListConnector {

	public String[] groupedAttributes;
	
	boObjectList oObjectList;
    
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
        return (int)this.oObjectList.getRecordCount();
    }

    public int getRowCount() {
        return this.oObjectList.getRowCount();
    }

    public void setSortTerms(SortTerms sortTerms) {
    	StringBuilder sb = new StringBuilder();
    	Iterator<SortTerm> it = sortTerms.iterator();

    	while( it.hasNext() ) {

    		if( sb.length() > 0 )
    			sb.append(';');
    		
    		SortTerm st = it.next();
    		String field = st.getField();
    		
    		// native
    		if( oObjectList.getBOQL().startsWith( "{" ) ) { 
    			try {
					field = oObjectList.getBoDef().getAttributeRef( field ).getDbName();
				} catch (boRuntimeException e) {
					// TODO Auto-generated catch block
					throw new RuntimeException( e );
				}
    		}
    		String dir   = st.getDirection()==SortTerms.SORT_DESC?"DESC":"";
    		sb.append( field ).append( ' ' ).append( dir );
    	}
    	
    	this.oObjectList.setQueryOrderBy( sb.toString() );
	}
    
    public void setFilterTerms( FilterTerms filterTerms ) {
    	
    	EboContext eboCtx = boApplication.currentContext().getEboContext();
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
					default:
						query.append( " AND " );
    			}
    		}
			FilterTerm t =  j.getTerm();
			
			Object val = t.getValue();
			
			String parVal = "?";
			if( val != null ) {
				if( val instanceof String ) {
					val = ((String)val).replace( "'" , "''").toUpperCase();
					if( t.getOperator() == FilterTerms.OPERATOR_CONTAINS || t.getOperator() == FilterTerms.OPERATOR_NOT_CONTAINS ) {
						val = "%" + val + "%";
					}
					parVal = "?";
					query.append( "UPPER(" + t.getDataField() + ")" );
					pars.add( val );
				} else if ( val instanceof Boolean ) {
					val = ((Boolean)val).booleanValue()?"1":"0";
					query.append( t.getDataField() );
					parVal = "?";
					pars.add( val );
				} else if ( val instanceof java.util.Date ) {
					val = new Timestamp( ((Date)val).getTime() );
					query.append( dutl.fnTruncateDate( t.getDataField() ) );
					parVal = dutl.fnTruncateDate( "?" );
					pars.add( val );
				} else if ( val instanceof Object[] ) {
					
					StringBuilder parValues = new StringBuilder("(");
					query.append( t.getDataField() );
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
					query.append( t.getDataField() );
					parVal = "?";
					pars.add( val );
				}
			} else {
				query.append( t.getDataField() );
				parVal = "?";
				pars.add( val );
			}
			
			switch ( t.getOperator() ) {
				case FilterTerms.OPERATOR_CONTAINS:
					query.append( " LIKE " ).append( parVal );
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
					query.append( " NOT LIKE " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_NOT_LIKE:
					query.append( " NOT LIKE " ).append( parVal );
					break;
				case FilterTerms.OPERATOR_IN:
					query.append( " IN " ).append( parVal );
					break;
			}
    	}
    	if( query.length() > 0 )
    		oObjectList.setUserQuery( query.length()>0?query.toString():null, pars.toArray() );
    	else
    		oObjectList.setUserQuery( null, null );
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void setPageSize(int pageSize) {

		try {
			Field pageField = this.oObjectList.getClass().getDeclaredField( "p_pagesize" );
			pageField.setAccessible( true );
			pageField.setInt( this.oObjectList , pageSize);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
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
			}
			
			/*
			if( this.oObjectList.getRslt() != null ) {
				DataSet dataSet = this.oObjectList.getRslt().getDataSet();
				int col = dataSet.findColumn( attributeName );
				if( col > 0 ) {
					return new XEOObjectConnector.GenericFieldConnector( attributeName, null, DataFieldTypes.VALUE_CHAR );
				}
			}
			*/
			
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
	
	public DataListConnector getGroupDetails(int level, String[] parentGroups,
			Object[] parentValues, int page, int pageSize) {
		// TODO Auto-generated method stub
		return new XEOObjectListGroupConnector( 
				this, 
				parentGroups, 
				parentValues, 
				this.groupedAttributes[level-1], 
				page, 
				pageSize 
			).getDetails();
	}

	public DataGroupConnector getGroups(int level, String[] parentGroups,
			Object[] parentValues, int page, int pageSize) {
		
		return new XEOObjectListGroupConnector( this, null, null, this.groupedAttributes[level-1], page, pageSize );
	}

	public void setGroupBy(String[] attributes) {
		this.groupedAttributes = attributes;
	}

	public int dataListCapabilities() {
		return 
			DataListConnector.CAP_FULLTEXTSEARCH + 
			DataListConnector.CAP_PAGING + 
			DataListConnector.CAP_SORT + 
			DataListConnector.CAP_FILTER +
			DataListConnector.CAP_GROUPING;
	}

	@Override
	public int indexOf(String sUniqueIdentifier) {
		int ret = -1;
		
		int lastRow = this.oObjectList.getRow();
		if( this.oObjectList.haveBoui( Long.valueOf( sUniqueIdentifier ) ) ) {
			ret = this.oObjectList.getRow();
		}
		this.oObjectList.moveTo( lastRow );
		return ret;
	}


}
