package netgest.bo.xwc.components.connectors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import netgest.bo.data.DataManager;
import netgest.bo.data.DataSet;
import netgest.bo.data.DriverUtils;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.boApplication;
import netgest.bo.utils.XEOQLModifier;

public class XEOObjectListGroupConnector implements DataGroupConnector {

	private XEOObjectListConnector 	rootList;
	private String 					groupAttribute;
	private Object[]				parentValues;
	private DataSet					dataSet;
	
	private String					preparedSql;
	private ArrayList<Object>		preparedSqlArgs;

	private int	page;
	private int pageSize;

	public XEOObjectListGroupConnector( 
			XEOObjectListConnector rootList, 
			String[] parentGroups, 
			Object[] parentValues, 
			String groupAttribute,
			int page,
			int pageSize
		)
	{
		this.groupAttribute 	= groupAttribute;
		this.rootList 			= rootList;
		this.parentValues		= parentValues;
		this.page = page;
		this.pageSize = pageSize;
	}

	public boolean isDateValue( String objectName ) {
		boolean isDate;
		
		isDate = false;
		boDefHandler def = boDefHandler.getBoDefinition( objectName );
		if( def != null ) {
			boDefAttribute defAtt = def.getAttributeRef( this.groupAttribute );
			if( defAtt != null ) {  
				if( 
						defAtt.getValueType() == boDefAttribute.VALUE_DATE
						||
						defAtt.getValueType() == boDefAttribute.VALUE_DATETIME
				) {
					isDate = true;
				}
			}
		}
		return isDate;
	}
	
	@SuppressWarnings("unchecked")
	public DataListConnector getDetails() {
		
		String 		userQuery 		= this.rootList.oObjectList.getUserQuery();
		Object[] 	userQueryArgs 	= this.rootList.oObjectList.getUserQueryArgs();
		String 		orderBy			= this.rootList.oObjectList.getOrderBy();
		String 		boql			= this.rootList.oObjectList.getBOQL();
		Object[] 	boqlArgs		= this.rootList.oObjectList.getBOQLArgs();
		
		List<Object> qlArgsList = new ArrayList<Object>(0);
		if( boqlArgs != null ) {
			qlArgsList.addAll( Arrays.asList( boqlArgs ) );
		}
		QLParser qp = new QLParser();
		qp.toSql(  boql, getEboContext() );
		
		DriverUtils dutl = getEboContext().getDataBaseDriver().getDriverUtils();
		
		boDefHandler  defObj = qp.getObjectDef();
		
		XEOQLModifier q = new XEOQLModifier( boql, qlArgsList );
		
		boolean isDate;
		boolean isNull = false;
		String nativeQlTag1 = "[";
		String nativeQlTag2 = "]";
		String groupFieldName = this.groupAttribute;
		if( boql.startsWith( "{" ) ) {
			nativeQlTag1 = "";
			nativeQlTag2 = "";
                        if( defObj.getAttributeRef( this.groupAttribute ) != null )
			groupFieldName = defObj.getAttributeRef( this.groupAttribute ).getDbName();
                        else
                            groupFieldName = this.groupAttribute;
		}
		
		
		isDate = isDateValue( defObj.getName() );

		String groupWhere;
		if( this.parentValues[0] == null || String.valueOf( this.parentValues[0] ).length() == 0 ) {
			groupWhere = groupFieldName + " IS NULL";
			isNull = true;
		}
		else {
			if( isDate ) {
				if( this.parentValues[0] instanceof String ) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
					try {
						Date parsedDate = sdf.parse( (String)this.parentValues[0] );
						this.parentValues[0] = new java.sql.Timestamp( parsedDate.getTime() );
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
			
			if( isDate ) {
				groupWhere = nativeQlTag1 + dutl.fnTruncateDate( groupFieldName ) + nativeQlTag2 + "= " + nativeQlTag1 + dutl.fnTruncateDate( "?" ) + nativeQlTag2;
			} else {
				groupWhere = groupFieldName + "= ?";
			}
		}
		String wherePart = q.getWherePart();
		if( userQuery != null && userQuery.length() > 0 ) {
			if( wherePart != null && wherePart.length() > 0 ) {
				wherePart = "(" + wherePart + ") AND (" + userQuery + ") AND ";
			} else {
				wherePart = "(" + userQuery + ") AND ";
			}
		}
		else if( wherePart != null && wherePart.length() > 0 ) {
				wherePart = "(" + wherePart + ") AND ";
		}
		
		wherePart += groupWhere;
		
		if( userQueryArgs != null ) {
			q.getWherePartParameters().addAll( Arrays.asList( userQueryArgs ) );
		}
		if( !isNull ) {
			q.getWherePartParameters().add( this.parentValues[0] );
		}
		
		q.setWherePart( wherePart );
		
		if( orderBy != null && orderBy.length() > 0 ) {
			q.setOrderByPart( orderBy );
			q.getOrderByPartParameters().clear();
		}
		
		ArrayList<Object>	modifiedBoqlParams = new ArrayList<Object>();
		
		String newboql 		= q.toBOQL( modifiedBoqlParams );
		 
		boObjectList list = boObjectList.list(getEboContext(), newboql, 
				modifiedBoqlParams.toArray(), 
				page, 
				this.pageSize, 
				true
			);
		
		return new XEOObjectListConnector( list );
	}
	
	public DataSet getDataSet() {
		return this.dataSet;
	}
	
	public XEOObjectListConnector getRootList() {
		return this.rootList;
	}
	
	public DataFieldMetaData getAttributeMetaData(String attributeName) {
		return rootList.getAttributeMetaData( attributeName );
	}

	public int getPage() {
		return this.page;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public int getRecordCount() {
		prepareQuery();
		DataSet countDataSet = DataManager.executeNativeQuery( 
				getEboContext(), 
				"DATA", 
				"select count(*) from (" + this.preparedSql + ") COUNT_SELECT", 
				1,
				1,
				this.preparedSqlArgs 
			);		
		return countDataSet.rows(1).getInt( 1 );
	}

	public int getRowCount() {
		return this.dataSet.getRowCount();
	}

	public DataListIterator iterator() {
		refresh();
		return new XEOObjectListGroupIterator( this );
	}

	@SuppressWarnings("unchecked")
	public void prepareQuery() {
		String 		userQuery 		= this.rootList.oObjectList.getUserQuery();
		Object[] 	userQueryArgs 	= this.rootList.oObjectList.getUserQueryArgs();
		String 		boql			= this.rootList.oObjectList.getBOQL();
		Object[] 	boqlArgs		= this.rootList.oObjectList.getBOQLArgs();
		String 		orderBy			= this.rootList.oObjectList.getOrderBy();
		
		List<Object> qlArgsList = new ArrayList<Object>(0);
		if( boqlArgs != null ) {
			qlArgsList.addAll( Arrays.asList( boqlArgs ) );
		}
		
		XEOQLModifier q = new XEOQLModifier( boql, qlArgsList );
		String fields = q.getFieldsPart();
		if( fields.length() > 0 ) {
			fields = ""; 
		}

		// Handle native SQL in BOQL
		QLParser qp = new QLParser();
		qp.toSql(  boql, getEboContext() );

		DriverUtils dutl = getEboContext().getDataBaseDriver().getDriverUtils();
		
		boolean isObject = false;
		String nativeQlTag1 = "[";
		String nativeQlTag2 = "]";
		String groupFieldName = this.groupAttribute;
		
		boDefHandler def = qp.getObjectDef();
		if( def != null ) {
			boDefAttribute defAtt = def.getAttributeRef( this.groupAttribute );
			if( defAtt != null ) {
				isObject = true;
				if( boql.startsWith( "{" ) ) {
					nativeQlTag1 = "";
					nativeQlTag2 = "";
					groupFieldName = defAtt.getDbName();
				}
			}
		}
		
		
		boolean isDate;
		isDate = isObject && isDateValue( qp.getObjectName() );
		if ( !isDate ) {
			fields += groupFieldName + ", "+nativeQlTag1 +"count(*) as count" + nativeQlTag2;
		} else {
			fields += nativeQlTag1 + dutl.fnTruncateDate( groupFieldName ) + " as " + groupFieldName + nativeQlTag2 + ", " + nativeQlTag1 + "count(*) as count" + nativeQlTag2;
		}
		
		q.setFieldsPart( fields );
		
		if( !isDate )
			q.setGroupByPart( groupFieldName );
		else
			q.setGroupByPart( nativeQlTag1 + dutl.fnTruncateDate( groupFieldName ) + nativeQlTag2 );
		
		
		q.setFieldsPart( fields );
		
		String wherePart = q.getWherePart();
		if( userQuery != null && userQuery.length() > 0 ) {
			if( wherePart != null && wherePart.length() > 0 ) {
				wherePart += " AND (" + userQuery + ")";
			} else {
				wherePart = userQuery;
			}
			if( userQueryArgs != null ) {
				q.getWherePartParameters().addAll( Arrays.asList( userQueryArgs ) );
			}
		}
		q.setWherePart( wherePart );
		if( orderBy != null && orderBy.length() > 0 ) {
			String orderField = orderBy;
			String uOrderBy  = orderBy.toUpperCase();
			if( uOrderBy.endsWith( " ASC" ) ) {
				orderField = orderBy.substring( 0, orderBy.length() - 4 );
			} else if ( uOrderBy.endsWith( " DESC" ) ) {
				orderField = orderBy.substring( 0, orderBy.length() - 5 );
			}
			if( orderField.equals( groupFieldName ) ) {
				q.setOrderByPart( orderBy );
				q.getOrderByPartParameters().clear();
			}
			else {
				q.setOrderByPart( "" );
				q.getOrderByPartParameters().clear();
			}
		}
		else {
			q.setOrderByPart( "" );
			q.getOrderByPartParameters().clear();
		}
		
		qp = new QLParser();
		ArrayList<Object>	modifiedBoqlParams = new ArrayList<Object>();
		
		String newboql 		= q.toBOQL( modifiedBoqlParams );
		String newsql       = qp.toSql( newboql, getEboContext() );
		
		this.preparedSql  = newsql;
		this.preparedSqlArgs = modifiedBoqlParams;
		
	}
	
	public void refresh() {
		prepareQuery();
		this.dataSet = DataManager.executeNativeQuery( 
				getEboContext(), 
				"DATA", 
				this.preparedSql, 
				getPage(),
				getPageSize(),
				this.preparedSqlArgs 
			);
	}
	
	public void setPage(int pageNo) {
		this.page = pageNo;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	private EboContext getEboContext() {
		return boApplication.currentContext().getEboContext();
	}
}
