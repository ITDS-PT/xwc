package netgest.bo.xwc.components.connectors.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import netgest.bo.data.DriverUtils;
import netgest.bo.data.mysql.MysqlUtils;
import netgest.bo.data.oracle.OracleUtils;
import netgest.bo.data.postgre.PostGreUtils;
import netgest.bo.data.sqlserver.SqlServerUtils;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList.SqlField;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.bo.utils.XEOQLModifier;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataListIterator;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterJoin;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.connectors.SortTerms;
import netgest.bo.xwc.components.connectors.SortTerms.SortTerm;
import netgest.bo.xwc.components.connectors.XEOObjectConnector.GenericFieldConnector;
import netgest.utils.StringUtils;

/**
 * @author acruz
 * Generic Connector that works in all databases. It can be used for example to display the results of a query in
 * a XWCGrid. 
 * The default usage is to create a new instance of this class with the desired query, this
 * uses the default connection to the database available to XEO.
 * If you want to use different databases you should extend this class and reimplement the method getConnection
 * a return a Connection to a database of you choice.
 * 
 * A method called truncateDate is used to do queries on Date fields. Right now it works with Oracle, MySQL,
 * SQLServer and Postgres, if you want to use it in any other database you should reimplement it to the database
 * of you choice. 
 */
public class SQLDataListConnector implements DataListConnector {

	private String sqlQuery = null;
	
	//Control variables for count and query
	private String sqlOriginalQuery=null;
	private String sqlQueryCount=null;
	
	private int pageSize = 30;
	private int page = 1;
	private EboContext ctx = null;
	
	private Map<String,SQLDataFieldMetaData> dataFieldsMeta = null;
	
	private Collection<SQLDataRecordConnector> rows = null;
	private List<Object> pars= null;
	private List<Object> parsCount= null;
	
	private static Logger LOGGER = Logger.getLogger( SQLDataListConnector.class );
	
	public SQLDataListConnector(String sqlQuery) {
		this.sqlQuery = sqlQuery;
		this.sqlOriginalQuery = sqlQuery;
		this.sqlQueryCount = sqlQuery;
	}
	
	@Override
	public DataListIterator iterator() {
		return new SQLDataListIterator(rows);
	}

	@Override
	public int getRecordCount() {
		int recCount=0;
		Connection cn = this.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			XEOQLModifier modifier=new XEOQLModifier(sqlQueryCount, new ArrayList());
			//String fieldsPart  = modifier.getFieldsPart();
			//modifier.setFieldsPart("count(*),"+fieldsPart);			
			sqlQueryCount=modifier.toBOQL(new ArrayList());
			sqlQueryCount = "SELECT count(*) from ("+sqlQueryCount+") dummy";
			ps = cn.prepareStatement(sqlQueryCount,
					ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			setParameters(ps,parsCount);
			rs = ps.executeQuery();
			if (rs.next())
				recCount =rs.getInt(1);
			
			
		} catch (SQLException e) {
			LOGGER.severe("Error refreshing Connector", e);
		}		
		finally {
			try {
				this.sqlQueryCount  = sqlOriginalQuery;
				this.parsCount = null;
				if (rs!=null)
					rs.close();
				if (ps!=null)
					ps.close();			
				if (cn!=null)
					cn.close();
			} catch (SQLException e) {
				LOGGER.severe("Something went wrong closing jdbc resources", e);
			}
			if (ctx!=null)
				ctx.close();
		}
		return recCount;
	}

	@Override
	public int getRowCount() {
		
		return rows.size();
	}

	@Override
	public DataRecordConnector findByUniqueIdentifier(String sUniqueIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(String sUniqueIdentifier) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setSearchText(String sSearchText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSortTerms(SortTerms sortTerms) {
		if (sortTerms!=null)
		{
			Iterator<SortTerm>isortTerms=sortTerms.iterator();
			String sortString="";
			while (isortTerms.hasNext()) {
				SortTerm sterm=isortTerms.next();
				sortString+=sterm.getField()+" "+
						((sterm.getDirection()==SortTerms.SORT_ASC)?"ASC":"DESC")+" ";
			}
			if (!sortString.equals("")) {
				XEOQLModifier modifier = new XEOQLModifier(sqlQuery, new ArrayList());
				modifier.setOrderByPart(sortString);
				sqlQuery=modifier.toBOQL(new ArrayList());
				modifier = new XEOQLModifier(sqlQueryCount, new ArrayList());
				modifier.setOrderByPart(sortString);
				sqlQueryCount=modifier.toBOQL(new ArrayList());
			}
		}
	}

	@Override
	public void setFilterTerms(FilterTerms filterTerms) {
		if (filterTerms==null)
			return;	
    	
    	StringBuilder query = new StringBuilder();
    	pars  = new ArrayList<Object>();
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
			name = getAttributeMetaData(name).getName();
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
					query.append( truncateDate(sqlExpr) );
					parVal = truncateDate( "?" );
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
			
			if (isCheckingForNullPresence( t ) ) {
				if (!pars.isEmpty())
					pars.remove( pars.size() - 1);
			}
			switch ( t.getOperator() ) {
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
    	{
    		XEOQLModifier modifier = new XEOQLModifier(sqlQuery, new ArrayList());
    		String wherePart = modifier.getWherePart();
    		if (StringUtils.isEmpty(wherePart))
    			modifier.setWherePart(query.toString());
    		else
    			modifier.setWherePart(wherePart+ " AND "+query);
    	
    		sqlQuery = modifier.toBOQL(new ArrayList());
    		
    		modifier = new XEOQLModifier(sqlQueryCount, new ArrayList());
    		wherePart = modifier.getWherePart();
    		if (StringUtils.isEmpty(wherePart))
    			modifier.setWherePart(query.toString());
    		else
    			modifier.setWherePart(wherePart+ " AND "+query);
    	
    		sqlQueryCount = modifier.toBOQL(new ArrayList());
    		
    		this.parsCount = pars;
    	}
		
	}
	
	/**
	 * @param toTrunc
	 * @return
	 * Reimplement this method if you want to use a database different than the ones supported by XEO:
	 * Oracle, MySQL, MSSQL and PostGres
	 */
	public String truncateDate(String toTrunc)
	{
		DriverUtils dutl = null;
		Connection cn=null;
		try
		{
			if (ctx!=null)
				dutl = ctx.getDataBaseDriver().getDriverUtils();
			else
			{
				//try to check database type
				cn=this.getConnection();
				String dbName="";
				try {
					dbName = cn.getMetaData().getDatabaseProductName().toUpperCase();
				} catch (SQLException e) {
		
				}
				if (dbName.indexOf("ORACLE")>-1)
					dutl = new OracleUtils(null);
				else if (dbName.indexOf("POSTGRE")>-1)
					dutl = new PostGreUtils(null);
				else if (dbName.indexOf("MYSQL")>-1)
					dutl = new MysqlUtils(null);
				else if (dbName.indexOf("SQL SERVER")>-1)
					dutl = new SqlServerUtils(null);
				else //Default to Oracle
					dutl = new OracleUtils(null);
	
			}
		}
		finally {
			if (cn!=null)
				try {
					cn.close();
				} catch (SQLException e) {
				}
		}
		return dutl.fnTruncateDate(toTrunc);
	}
	
	private boolean isCheckingForNullPresence( FilterTerm t ) {
		return t.getOperator() == FilterTerms.OPERATOR_CONTAINS || t.getOperator() == FilterTerms.OPERATOR_NOT_CONTAINS;
	}
	
	@Override
	public void setSearchTerms(String[] columnName, Object[] sColumnValue) {
		// TODO Auto-generated method stub
		
	}

	private void resetRowsAndCols()
	{
		this.dataFieldsMeta = null;
		this.rows = null;
	}
	
	@Override
	public void refresh() {
		resetRowsAndCols();
		Connection cn = this.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = cn.prepareStatement(sqlQuery,
					ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			setParameters(ps,pars);
			rs = ps.executeQuery();
			
			if (this.dataFieldsMeta == null)
				addDataFieldMetaData(rs.getMetaData());
			int rowNumber = 1;
			int firstRow = (page * pageSize - pageSize) +1;
			int lastRow = page * pageSize;
			while (rs.next()) {
				if (rowNumber>=firstRow && rowNumber<=lastRow)
					addRow(rs, rowNumber);
				
				rowNumber++;
			}
			
		} catch (SQLException e) {
			LOGGER.severe("Error refreshing Connector", e);
		}		
		finally {
			sqlQuery = sqlOriginalQuery;
			pars = null;
			try {
				if (rs!=null)
					rs.close();
				if (ps!=null)
					ps.close();			
				if (cn!=null)
					cn.close();
			} catch (SQLException e) {
				LOGGER.severe("Something went wrong closing jdbc resources", e);
			}
			if (ctx!=null)
				ctx.close();
		}
	}

	private void setParameters(PreparedStatement ps,List<Object> params)
	{
		int parN=1;
		if (params!=null && params.size()>0) {
			Iterator<Object>iPars=params.iterator();
			while (iPars.hasNext()) {
				try {
					if (ps!=null)
						ps.setObject(parN, iPars.next());
				} catch (SQLException e) {
					LOGGER.severe("Error setting parameter",e);
				}
				parN++;
			}
		}
	}
	
	private void addRow(ResultSet rs, int rowIndex) {
		if (rows==null)
			rows = new ArrayList<SQLDataRecordConnector>();
		
		Map<String,DataFieldConnector> row = new HashMap<String, DataFieldConnector>();
		
		Iterator<SQLDataFieldMetaData> iColumns = dataFieldsMeta.values().iterator();
		int colIndex=1;
		while (iColumns.hasNext()) {
			SQLDataFieldMetaData sqlmdata=iColumns.next();
			
			
			GenericFieldConnector field=null;
			try {
				field = new GenericFieldConnector(sqlmdata.getLabel().toLowerCase(), 
						rs.getString(colIndex), sqlmdata.getDataType());
			} catch (SQLException e) {
			}			
			
			row.put(sqlmdata.getLabel().toLowerCase(), field);
			
			colIndex++;			
		}
		SQLDataRecordConnector record = new SQLDataRecordConnector(row, 1);
		rows.add(record);
		
		
	}
	
	private void addDataFieldMetaData(ResultSetMetaData rsMetaData) {
		if (dataFieldsMeta==null)
			dataFieldsMeta = new LinkedHashMap<String, SQLDataFieldMetaData>();
		try {
			for (int i =1;i<=rsMetaData.getColumnCount();i++) {
				byte dataType=convertJDBCTypeToXEO(rsMetaData.getColumnType(i));
				
				String name=this.getColumnNameOrExpression(i);
				if (StringUtils.isEmpty(name))
				{
					name=rsMetaData.getColumnName(i);
					if (!StringUtils.isEmpty(rsMetaData.getTableName(i)))
							name=rsMetaData.getTableName(i)+"."+name;
				}
				
				String label = rsMetaData.getColumnLabel(i);
				if (StringUtils.isEmpty(label))
					label = rsMetaData.getColumnName(i);
				
				int maxLength = rsMetaData.getPrecision(i);
				int decimalPrecision = rsMetaData.getScale(i);
				
				SQLDataFieldMetaData sqlmetadata=new SQLDataFieldMetaData(label,name,
						dataType, maxLength, decimalPrecision);
				
				dataFieldsMeta.put(label.toLowerCase(), sqlmetadata);
			
			}
		}
		catch (SQLException e) {
			
		}
		
	}
	
	private String getColumnNameOrExpression(int index) {
		String toRet="";
		XEOQLModifier modifier= new XEOQLModifier(sqlQuery, new ArrayList());
		String fieldsPart=modifier.getFieldsPart();
		String [] fields = fieldsPart.split(",");
		for (int i=0;i<fields.length;i++) {
			if (index==(i+1))
			{			
				fields[i]=fields[i].trim();				
				if (fields[i].indexOf(" ")>-1)
					toRet=fields[i].substring(0,fields[i].indexOf(" "));
				else
					toRet=fields[i];
			  break;
			}
		}		
		return toRet;
	}
	
	private byte convertJDBCTypeToXEO(int jdbcType) {
		byte dataType=0;
		if (jdbcType == Types.VARCHAR || jdbcType == Types.CHAR 
				|| jdbcType == Types.LONGNVARCHAR || jdbcType == Types.LONGVARCHAR
				|| jdbcType == Types.NCHAR || jdbcType == Types.NVARCHAR)
			dataType = DataFieldTypes.VALUE_CHAR;
		else if (jdbcType == Types.CLOB || jdbcType == Types.NCLOB)
			dataType = DataFieldTypes.VALUE_CLOB;
		else if (jdbcType == Types.BIGINT || jdbcType == Types.DECIMAL
				|| jdbcType == Types.DOUBLE || jdbcType == Types.DOUBLE
				|| jdbcType == Types.FLOAT || jdbcType == Types.INTEGER
				|| jdbcType == Types.NUMERIC || jdbcType == Types.REAL
				|| jdbcType == Types.SMALLINT || jdbcType == Types.TINYINT)
			dataType = DataFieldTypes.VALUE_NUMBER;
		else if (jdbcType == Types.BOOLEAN || jdbcType == Types.BIT)
			dataType = DataFieldTypes.VALUE_BOOLEAN;
		else if (jdbcType == Types.BLOB || jdbcType == Types.BINARY ||
				jdbcType == Types.LONGVARBINARY || jdbcType == Types.VARBINARY)
			dataType = DataFieldTypes.VALUE_BLOB;
		else if (jdbcType == Types.DATE)
			dataType = DataFieldTypes.VALUE_DATE;
		else if (jdbcType == Types.TIME || jdbcType == Types.TIMESTAMP)
			dataType = DataFieldTypes.VALUE_DATETIME;
		
		return dataType;
	}
	
	@Override
	public int getPage() {
		return this.page;
	}

	@Override
	public void setPage(int pageNo) {
		this.page = pageNo;		
	}

	@Override
	public int getPageSize() {
		return this.pageSize;
	}

	@Override
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public int dataListCapabilities() {
		return DataListConnector.CAP_PAGING + DataListConnector.CAP_SORT
				+DataListConnector.CAP_FILTER;
	}

	@Override
	public SQLDataFieldMetaData getAttributeMetaData(String attributeName) {
		if (this.dataFieldsMeta==null)
			refresh();

		return this.dataFieldsMeta.get(attributeName.toLowerCase());
	}

	@Override
	public void setSqlFields(List<SqlField> sqlFields) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return 
	 * Reimplement this method if you want to use a different connection than the XEO default
	 */
	public Connection getConnection()
	{
		boSession session=null;
		Connection cn = null;
		try {
			session = boApplication.getDefaultApplication()
					.boLogin("SYSTEM", boLoginBean.getSystemKey());
		} catch (boLoginException e) {
			LOGGER.severe("Error getting XEO Session - user SYSTEM",e);
		}
		if (session != null)
		{
			ctx = session.createRequestContext(null, null, null);
			if (ctx!=null)
				cn=ctx.getConnectionData();
		}
		
		return cn;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public Collection<SQLDataRecordConnector> getRows() {
		return rows;
	}

	public void setRows(Collection<SQLDataRecordConnector> rows) {
		this.rows = rows;
	}
}
