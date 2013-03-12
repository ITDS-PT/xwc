package netgest.bo.xwc.components.connectors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import netgest.bo.data.DataManager;
import netgest.bo.data.DataSet;
import netgest.bo.data.DriverUtils;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectList.SqlField;
import netgest.bo.system.boApplication;
import netgest.bo.utils.XEOQLModifier;
import netgest.bo.xwc.components.connectors.OrderByTerms.OrderByDir;
import netgest.bo.xwc.components.connectors.OrderByTerms.OrderByTerm;

public class XEOObjectListGroupConnector implements DataGroupConnector {

	private XEOObjectListConnector 	rootList;
	private String 					groupAttribute;
	
	private Object[]				parentValues;
	private String[]				parentGroups;
	
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
		this.parentGroups 		= parentGroups;
		this.page = page;
		this.pageSize = pageSize;
	}

	public boolean isDateValue( String objectName ) {
		return isDateValue(  this.groupAttribute, objectName );
	}
	
	public static boolean isDateValue( String attName, String objectName ) {
		boolean isDate;
		
		isDate = false;
		boDefHandler def = boDefHandler.getBoDefinition( objectName );
		if( def != null ) {
			boDefAttribute defAtt = def.getAttributeRef( attName );
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
		
		String 		orderBy			= this.rootList.oObjectList.getOrderBy();
		String 		boql			= this.rootList.oObjectList.getBOQL();
		Object[] 	boqlArgs		= this.rootList.oObjectList.getBOQLArgs();
		
		List<Object> qlArgsList = new ArrayList<Object>(0);
		if( boqlArgs != null ) {
			qlArgsList.addAll( Arrays.asList( boqlArgs ) );
		}
		QLParser qp = new QLParser();
		qp.toSql(  boql, getEboContext() );
		
		XEOQLModifier q = new XEOQLModifier( boql, qlArgsList );
		
		List<SqlField> sqlFieldsList = getRootList().getSqlFields();
		
		/** ML 07-10-2011 **/
	//	if(!(this.parentValues.length == 1 && this.parentGroups[0].equalsIgnoreCase("/*DUMMY_AGGREGATE*/")))
		{
			addParentWhere( boql, qp, q );
		}
		/** END ML 07-10-2011 **/
		
		if( orderBy != null && orderBy.length() > 0 ) {
			
			StringBuilder newOrderBy = new StringBuilder();
			
			OrderByTerms terms = new OrderByTerms( orderBy.trim() );
			List<String> parentGroups = Arrays.asList( this.parentGroups );
			for(OrderByTerm term : terms.sortTerms() ) {
				if( parentGroups.indexOf( term.getName() ) == -1 ) {
					if( newOrderBy.length() > 0 ) {
						newOrderBy.append(", ");
					}
					SqlField sqlOrderField = findSqlField(sqlFieldsList, term.getName());
					if( sqlOrderField != null ) {
						newOrderBy.append( "[\"" + term.getName() + '"' + (term.getOrderByDir()==OrderByDir.SORT_DESC?" DESC":"") + "]" );
					}
					else {
						newOrderBy.append( term.getExpression() + (term.getOrderByDir()==OrderByDir.SORT_DESC?" DESC":"") );					
					}
				}
			}
			
			// If details of the group doesn't have order, apply the same order of the group's
			if( newOrderBy.length() == 0 ) {
				for( int i=0; i<this.parentGroups.length; i++ ) {
					OrderByTerm term = terms.getSortTerm( this.parentGroups[ i ] ); 
					if( term != null ) {
						if( newOrderBy.length() > 0 ) {
							newOrderBy.append(", ");
						}
						SqlField sqlOrderField = findSqlField(sqlFieldsList, term.getName());
						if( sqlOrderField != null ) {
							newOrderBy.append( "[\"" + term.getName() + '"' + (term.getOrderByDir()==OrderByDir.SORT_DESC?" DESC":"") + "]" );
						}
						else {
							newOrderBy.append( term.getExpression() + (term.getOrderByDir()==OrderByDir.SORT_DESC?" DESC":"") );					
						}
					}
				}
			}
			q.setOrderByPart( newOrderBy.toString() );
			/*
			boolean isSqlField = true;
			boolean desc = false;
			orderBy = orderBy.trim();
			String orderField = orderBy.trim();
			String uOrderBy  = orderBy.toUpperCase();
			if( uOrderBy.endsWith( " ASC" ) ) {
				orderField = orderBy.substring( 0, orderBy.length() - 4 );
				desc = false;
			} else if ( uOrderBy.endsWith( " DESC" ) ) {
				orderField = orderBy.substring( 0, orderBy.length() - 5 );
				desc = true;
			}
			
			orderField = orderField.replaceAll("\"", "");
			
			if( sqlFieldsList != null ) {
	        	for( SqlField field : sqlFieldsList ) {
					if( orderField.equals( field.getSqlAlias() ) ) {
						q.setOrderByPart( "[\"" + orderField + '"' + (desc?" DESC":"") + "]" );
						isSqlField = true;
					}
	        	}
			}
        	if( !isSqlField ) {
				q.setOrderByPart( orderBy );
        	}
			q.getOrderByPartParameters().clear();
			*/
		}
		else {
			q.setOrderByPart( "" );
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

		if( sqlFieldsList != null ) {
			list.setSqlFields( sqlFieldsList.toArray( new SqlField[ sqlFieldsList.size() ] ) );
		}
		
		return new XEOObjectListConnector( list );
	}
	
	private static final SqlField findSqlField( SqlField[] fields, String fieldAlias ) {
		
		return findSqlField( (List<SqlField>)Arrays.asList( fields ), fieldAlias );
		
	}
	private static final SqlField findSqlField( List<SqlField> fields, String fieldAlias ) {
		if( fields != null ) {
	    	for( SqlField field : fields ) {
				if( fieldAlias.equals( field.getSqlAlias() ) ) {
					return field;
				}
	    	}
		}
    	return null;
	}
	
	private void addParentWhere( String boql, QLParser qp, XEOQLModifier q ) {
		
		
		DriverUtils dutl = getEboContext().getDataBaseDriver().getDriverUtils();
		
		List<SqlField> 	sqlFieldsList 	= getRootList().getSqlFields();
		String 			fullText		= boObjectList.arrangeFulltext(getEboContext(), this.rootList.oObjectList.getFullTextSearch());
		String 			userQuery 		= this.rootList.oObjectList.getUserQuery();
		Object[] 		userQueryArgs 	= this.rootList.oObjectList.getUserQueryArgs();
		boDefHandler  	defObj 			= qp.getObjectDef();
		
		String nativeQlTag1 = "[";
		String nativeQlTag2 = "]";
		String groupWhere   = "";
		
		if( this.parentValues != null && !(this.parentValues.length == 1 && this.parentGroups[0].equalsIgnoreCase("/*DUMMY_AGGREGATE*/"))) {
			for( int i=0; i < this.parentValues.length; i++ ) {
				String groupExpression = this.parentGroups[i];
				if( boql.startsWith( "{" ) ) {
					nativeQlTag1 = "";
					nativeQlTag2 = "";
		            if( defObj.getAttributeRef( this.parentGroups[i] ) != null ) {
		            	groupExpression = defObj.getAttributeRef( this.parentGroups[i] ).getDbName();
		            } else {
		            	groupExpression = this.parentGroups[i];
		            }
				}
				
		        if( sqlFieldsList != null ) {
		        	for( SqlField field : sqlFieldsList ) {
		        		 if( this.parentGroups[i].equals( field.getSqlAlias() ) ) {
		        			 groupExpression = "[(" + field.getSqlExpression() + ")]";
		        		 }
		        	}
		        }
				
				
				if( i > 0 ) {
					groupWhere += " AND ";
				}
				if( this.parentValues[i] == null || String.valueOf( this.parentValues[i] ).length() == 0 ) {
					groupWhere += groupExpression + " IS NULL";
				}
				else {
					boolean isDate = isDateValue( this.parentGroups[i],defObj.getName() );
					if( isDate ) {
						if( this.parentValues[i] instanceof String ) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
							try {
								Date parsedDate = sdf.parse( (String)this.parentValues[i] );
								this.parentValues[i] = new java.sql.Timestamp( parsedDate.getTime() );
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					if( isDate ) {
						
						String mTable = qp.getObjectDef().getBoMasterTable();
						if( qp.isObjectExtended() ) {
							mTable = qp.getObjectDef().getBoExtendedTable();
						}
						mTable = mTable + "." + this.parentGroups[i];
						groupWhere += nativeQlTag1 + dutl.fnTruncateDate( mTable ) + nativeQlTag2 + "= " + nativeQlTag1 + dutl.fnTruncateDate( "?" ) + nativeQlTag2;
					} else {
						groupWhere += groupExpression + "= ?";
					}
				}
			}
		}
		
		String wherePart = q.getWherePart();
		if( userQuery != null && userQuery.length() > 0 ) {
			if( wherePart != null && wherePart.length() > 0 ) {
				wherePart = "(" + wherePart + ") AND (" + userQuery + ") ";
			} else {
				wherePart = "(" + userQuery + ") ";
			}
		}
		else if( wherePart != null && wherePart.length() > 0 ) {
				wherePart = "(" + wherePart + ") ";
		}
		if( userQueryArgs != null ) {
			q.getWherePartParameters().addAll( Arrays.asList( userQueryArgs ) );
		}
		
		if( groupWhere.length() > 0 ) {
			if( wherePart.length() > 0 )
				wherePart += " AND ";
			wherePart += groupWhere;
		}
		
		if( this.parentValues != null && !(this.parentValues.length == 1 && this.parentGroups[0].equalsIgnoreCase("/*DUMMY_AGGREGATE*/"))) {
			for( Object value : this.parentValues  ) {
				boolean isNull = false;
				if( value == null || String.valueOf( value ).length() == 0 ) {
					isNull = true;
				}
				if( !isNull ) {
					q.getWherePartParameters().add( value );
				}
			}
		}
		
		if( fullText != null && fullText.length() > 0 ) {
			if( wherePart.length() > 0 )
				wherePart += " AND ";

			wherePart += " CONTAINS ?";
			q.getWherePartParameters().add( fullText );
		}
		
		q.setWherePart(wherePart);
		
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
		if( hasMoreResults() ) {
			prepareQuery();
			DataSet countDataSet = DataManager.executeNativeQuery( 
					getEboContext(), 
					"DATA", 
					"select count(*) from (" + this.preparedSql + ") count", 
					1,
					1,
					this.preparedSqlArgs 
				);		
			return countDataSet.rows(1).getInt( 1 );
		}
		else {
			int i = ((this.getPage()-1) * this.getPageSize());
			i = i + this.dataSet.getRowCount(); 
	    	return i;
	    	
		}
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
		//String 		userQuery 		= this.rootList.oObjectList.getUserQuery();
		//Object[] 	userQueryArgs 	= this.rootList.oObjectList.getUserQueryArgs();
		String 		boql			= this.rootList.oObjectList.getBOQL();
		Object[] 	boqlArgs		= this.rootList.oObjectList.getBOQLArgs();
		String 		orderBy			= this.rootList.oObjectList.getOrderBy();
		//String 		fulltext		= boObjectList.arrangeFulltext(getEboContext(), this.rootList.oObjectList.getFullTextSearch());
		
		boolean createSubSelect = false;
		
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
		
		String boqlField = this.groupAttribute;
		String boqlGroupBy = this.groupAttribute;
		
		// ML - 06-10-2011
		if(this.groupAttribute != null && this.groupAttribute.equalsIgnoreCase("/*DUMMY_AGGREGATE*/"))
		{
			boqlField = nativeQlTag1 + "'ALL' as DUMMY_VALUE "  + nativeQlTag2;
			boqlGroupBy = null ;
		}
		// END ML - 06-10-2011
		
//		boolean needFieldAlias = false;
//		String[] relatedAtt = this.groupAttribute.split("__");
//		if( relatedAtt.length > 1 ) {
//			needFieldAlias = true;
//			boqlField = "";
//			boqlGroupBy = "";
//			boolean first = true;
//			for( String att : relatedAtt ) {
//				if( !first ) { 
//					boqlField += ".";
//					boqlGroupBy += ".";
//				}
//				boqlField += att;
//				boqlGroupBy += att;
//				first = false;
//			}
//		}
		String groupByExpression = boqlField;
		String groupFieldExpression = boqlField;
		
		List<SqlField> sqlFields = this.rootList.getSqlFields();
		
		boDefHandler def = qp.getObjectDef();
		if( def != null ) {
			boDefAttribute defAtt = def.getAttributeRef( this.groupAttribute );
			if( defAtt != null ) {
				isObject = true;
            	if( !qp.isObjectExtended() ) {
            		groupFieldExpression = def.getBoMasterTable() + "." + this.groupAttribute;
            		groupByExpression = groupFieldExpression;
            	}
            	else {
            		groupFieldExpression = def.getBoExtendedTable() + "." + this.groupAttribute;
            		groupByExpression = groupFieldExpression;
            	}
				
				if( boql.startsWith( "{" ) ) {
					nativeQlTag1 = "";
					nativeQlTag2 = "";
					groupByExpression = defAtt.getDbName();
					boqlGroupBy = defAtt.getDbName();
					boqlField = defAtt.getDbName();
				}
			}
			else if ( sqlFields != null && sqlFields.size() > 0 ) {
				for( SqlField field : sqlFields ) {
					if( this.groupAttribute.equals( field.getSqlAlias() ) ) {
						String fieldSqlExpr = field.getSqlExpression();
						if( fieldSqlExpr.toUpperCase().indexOf("SELECT ") > -1 ) {
							createSubSelect = true;
							boqlGroupBy = groupByExpression = field.getSqlAlias();
						}
						else {
							boqlGroupBy = groupByExpression = "[(" + field.getSqlExpression() + ")]";
						}
						boqlField = groupFieldExpression = "[(" + field.getSqlExpression() + ")] " + field.getSqlAlias();
						break;
					}
				}
			}
		}
		boolean isDate;
		isDate = isObject && isDateValue( qp.getObjectName() );
		if ( !isDate ) {
			fields += boqlField;
//			if ( needFieldAlias )
//				fields += " as " + this.groupAttribute +" ";
		} else {
			fields += nativeQlTag1 + dutl.fnTruncateDate( groupFieldExpression ) + " as " + this.groupAttribute + nativeQlTag2;
		}
		
		if (!createSubSelect) {
			fields += ", "+nativeQlTag1 +"count(*) as count" + nativeQlTag2;

			// ML 22-09-2011 - Summary Fields
			if (parentGroups != null
					&& parentGroups.length > 0
					&& parentGroups[parentGroups.length - 1]
							.equalsIgnoreCase(this.groupAttribute)
					&& getRootList() != null
					&& getRootList().getAggregateFields() != null 
					&& !getRootList().getAggregateFields().isEmpty()) {
				fields += ", " + nativeQlTag1;

				// obtain an Iterator for Collection
				Iterator itr = getRootList().getAggregateFields().keySet().iterator();
				
				// iterate through HashMap values iterator
				boolean first = true;
				String agregateExp="";
				while (itr.hasNext()) {
					String currKey = (String) itr.next();
					ArrayList<String> temp = getRootList().getAggregateFields().get(currKey);
					
					String aggregateFieldId = currKey.substring(0, currKey.indexOf(":"));
					String aggregateFieldDesc = currKey.substring(currKey.indexOf(":")+1);

					String sum = "''''", avg = "''''", min = "''''", max = "''''";

					for (int k = 0; k < temp.size(); k++) {
						if (temp.get(k) != null && temp.get(k).equalsIgnoreCase("SUM")) {
							sum = getEboContext().getDataBaseDriver().getDriverUtils().getSumForAggregate(aggregateFieldId);
						} else if (temp.get(k) != null
								&& temp.get(k).equalsIgnoreCase("AVG")) {
							avg = getEboContext().getDataBaseDriver().getDriverUtils().getAvgForAggregate(aggregateFieldId);
						} else if (temp.get(k) != null
								&& temp.get(k).equalsIgnoreCase("MIN")) {
							min = getEboContext().getDataBaseDriver().getDriverUtils().getMinForAggregate(aggregateFieldId);
						} else if (temp.get(k) != null
								&& temp.get(k).equalsIgnoreCase("MAX")) {
							max = getEboContext().getDataBaseDriver().getDriverUtils().getMaxForAggregate(aggregateFieldId);
						}
					}

					if (!first) {
						agregateExp += getEboContext().getDataBaseDriver().getDriverUtils().getAggregateConcatenation();
					} else {
						first = false;
					}
					agregateExp+=getEboContext().getDataBaseDriver().getDriverUtils().getAggregateExpression(aggregateFieldId, aggregateFieldDesc, sum, avg, min, max);
				}
				agregateExp=getEboContext().getDataBaseDriver().getDriverUtils().getConcatFunction(agregateExp);
				fields += agregateExp+" as aggregate" + nativeQlTag2;
			} else {
				fields += ", " + nativeQlTag1 + "'none' as aggregate"
						+ nativeQlTag2;
			}
			// END ML 22-09-2011 - Summary Fields
		}
		
		q.setFieldsPart( fields );
		
		if( !createSubSelect && /* TODO: */ boqlGroupBy != null ) {
			if( !isDate )
				q.setGroupByPart( boqlGroupBy );
			else
				q.setGroupByPart( nativeQlTag1 + dutl.fnTruncateDate( groupByExpression ) + nativeQlTag2 );
		}
		
		
		String outerSelectOrderBy = null;
		if( orderBy != null && orderBy.trim().length() > 0 ) {
			OrderByTerms orderByTerms = new OrderByTerms( orderBy.trim() );
			OrderByTerm term = orderByTerms.getSortTerm( this.groupAttribute );
			if( term != null ) {
				outerSelectOrderBy = term.getExpression() + (term.getOrderByDir()==OrderByDir.SORT_DESC?" DESC":"");
				if( boql.startsWith( "{" ) ) {
					q.setOrderByPart( term.getExpression() + (term.getOrderByDir()==OrderByDir.SORT_DESC?" DESC":"") );
				}
				else {
					if ( createSubSelect )
						q.setOrderByPart( "" );
					else
						q.setOrderByPart( "[" + term.getExpression() + (term.getOrderByDir()==OrderByDir.SORT_DESC?" DESC]":"]") );
				}
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
		addParentWhere( boql, qp, q );
		
		qp = new QLParser();
		ArrayList<Object>	modifiedBoqlParams = new ArrayList<Object>();
		
		
		String newboql 		= q.toBOQL( modifiedBoqlParams );
		String newsql       = qp.toSql( newboql, getEboContext() );
		
		if (createSubSelect) {			
			// ML - Summary Fields
			String dummyGroupBy = this.groupAttribute;
			if(this.groupAttribute != null && this.groupAttribute.equalsIgnoreCase("/*DUMMY_AGGREGATE*/"))
			{
				dummyGroupBy = "'ALL' as DUMMY_VALUE ";
			}
			if (parentGroups != null
					&& parentGroups.length > 0
					&& parentGroups[parentGroups.length - 1]
							.equalsIgnoreCase(this.groupAttribute)
					&& getRootList() != null
					&& getRootList().getAggregateFields() != null 
					&& !getRootList().getAggregateFields().isEmpty()) {
				String fieldsOnSubSelect = ", ";

				// obtain an Iterator for Collection
				Iterator itr = getRootList().getAggregateFields().keySet().iterator();

				// iterate through HashMap values iterator
				boolean first = true;
				String agregateExp="";
				while (itr.hasNext()) {
					String currKey = (String) itr.next();
					ArrayList<String> temp = getRootList().getAggregateFields().get(currKey);
					
					String aggregateFieldId = currKey.substring(0, currKey.indexOf(":"));
					String aggregateFieldDesc = currKey.substring(currKey.indexOf(":")+1);

					String sum = "''''", avg = "''''", min = "''''", max = "''''";

					for (int k = 0; k < temp.size(); k++) {
						if (temp.get(k) != null && temp.get(k).equalsIgnoreCase("SUM")) {
							sum = getEboContext().getDataBaseDriver().getDriverUtils().getSumForAggregate(aggregateFieldId);
						} else if (temp.get(k) != null
								&& temp.get(k).equalsIgnoreCase("AVG")) {
							avg = getEboContext().getDataBaseDriver().getDriverUtils().getAvgForAggregate(aggregateFieldId);
						} else if (temp.get(k) != null
								&& temp.get(k).equalsIgnoreCase("MIN")) {
							min = getEboContext().getDataBaseDriver().getDriverUtils().getMinForAggregate(aggregateFieldId);
						} else if (temp.get(k) != null
								&& temp.get(k).equalsIgnoreCase("MAX")) {
							max = getEboContext().getDataBaseDriver().getDriverUtils().getMaxForAggregate(aggregateFieldId);
						}
					}

					if (!first) {
						agregateExp += getEboContext().getDataBaseDriver().getDriverUtils().getAggregateConcatenation();
					} else {
						first = false;
					}
					agregateExp+=getEboContext().getDataBaseDriver().
						getDriverUtils().getAggregateExpression(aggregateFieldId, aggregateFieldDesc, sum, avg, min, max);					
				}
				agregateExp=getEboContext().getDataBaseDriver().getDriverUtils().getConcatFunction(agregateExp);
				fieldsOnSubSelect += agregateExp+ " as aggregate";	
						
				newsql = "SELECT \"GROUP\".\"" + dummyGroupBy + "\", count(*) as count " + fieldsOnSubSelect + " FROM (" + newsql + ") \"GROUP\"" +
				 " GROUP BY \"" + dummyGroupBy +"\""
				+ (outerSelectOrderBy!=null?" ORDER BY " + outerSelectOrderBy:"");
				;
				
			} else {
				newsql = "SELECT \"GROUP\".\"" + dummyGroupBy + "\", count(*) as count,'none' as aggregate FROM (" + newsql + ") \"GROUP\"" +
				 " GROUP BY \"" + dummyGroupBy +"\"" 
			+  (outerSelectOrderBy!=null?" ORDER BY " + outerSelectOrderBy:"");
				 ;
			}
			// END ML - Summary Fields
			/*
			newsql = "SELECT \"GROUP\".\"" + this.groupAttribute + "\", count(*) as count,'none' as aggregate FROM (" + newsql + ") \"GROUP\"" +
					 " GROUP BY \"" + this.groupAttribute +"\"" + 
					 (outerSelectOrderBy!=null?" ORDER BY " + outerSelectOrderBy:"");*/
		}
		this.preparedSqlArgs = modifiedBoqlParams;
		this.preparedSql  = newsql;
	}
	
	private int getLastPageRecordCount(){
		prepareQuery();
		DataSet countDataSet = DataManager.executeNativeQuery( 
				getEboContext(), 
				"DATA", 
				"select count(*) from (" + this.preparedSql + ") count", 
				1,
				1,
				this.preparedSqlArgs 
			);		
		return countDataSet.rows(1).getInt( 1 );
	}
	
	public void refresh() {
		int pageNum = getPage();
		prepareQuery();
		if (getPage() == -1){
			pageNum = calculateLastPageNumber();
			setPage( pageNum );
		} 
		this.dataSet = DataManager.executeNativeQuery( 
				getEboContext(), 
				"DATA", 
				this.preparedSql, 
				pageNum,
				getPageSize(),
				this.preparedSqlArgs 
			);
		
	}

	protected int calculateLastPageNumber() {
		return (int) Math.ceil( (double) getLastPageRecordCount() / getPageSize());
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

	private boolean hasMoreResults() {
		String hm = this.dataSet.getParameter("HaveMoreData");
		if (hm != null && hm.equals("true")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean hasMorePages() {
		return hasMoreResults();
	}
}
