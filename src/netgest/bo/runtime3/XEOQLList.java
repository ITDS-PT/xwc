package netgest.bo.runtime3;

import java.util.List;

import netgest.bo.data.DataSet;
import netgest.bo.def.boDefHandler;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.transaction.XTransaction;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.SortTerms;

public class XEOQLList implements XEOList {

	public void execute(String query) {
		execute( query, null, page, pageSize );
	}

	public void execute(String query, int page, int pageSize) {
		execute( query, null, page, pageSize );
	}
	public void execute(String query, List<Object> queryArguments) {
		execute( query, queryArguments, page, pageSize );
	}

	public void execute(String query, List<Object> queryArguments, int page, int pageSize) {
		this.boql 		= query;
//		this.boqlArgs 	= queryArguments;
		this.page = page;
		this.pageSize = pageSize;
		execute();
	}

	private String 			boql;
//	private List<Object>	boqlArgs;
	private int				page				= 1;	
	private int				pageSize 			= 50;
//	private FilterTerms		filterTerms;
//	private SortTerms		sortTerms;
//	private String			searchText;
	private boDefHandler	boDefHandler;
	
//	private int				preLoadCount 		= pageSize;
//	private boolean			preLoadCountDefault = true;
	
	private DataSet			dataSet;
	
	private XTransaction	transaction;
	
	public XEOQLList( XTransaction transaction ) {
		this.transaction = transaction;
	}
	
	public int findObject( long boui ) {
		return 0;
	}
	
	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public long getRecordCount() {
		return _executeRecordCount();
	}

	public int getRowCount() {
		return this.dataSet.getRowCount();
	}

	public void refresh() throws boRuntimeException {
		_execute();
	}

	public void setPage(int pageNo) {
		this.page = pageNo;
	}

//	public void setPageSize(int pageSize) {
//		this.pageSize = pageSize;
//		if( this.preLoadCountDefault )
//			this.preLoadCount = pageSize;
//	}
//
//	public void setFilterTerms( FilterTerms filterTerms ) {
//		this.filterTerms = filterTerms;
//	}
//
//	public void setSearchText(String searchText) {
//		this.searchText = searchText;
//	}
//
//	public void setSortTerms(SortTerms sortTerms) {
//		this.sortTerms = sortTerms;
//	}

	public void setQuery( String boql ) {
		this.boql = boql;
	}

//	public void setQuery( String boql, List<Object> boqlArguments ) {
//		this.boql = boql;
//		this.boqlArgs = boqlArguments;
//	}
	
	public boDefHandler	getBoDefinition() {
		return this.boDefHandler;
	}
	
	public void execute() {
		_execute();
	}
	
	private void _execute()  {
		QLParser qp = new QLParser();
		String sql = qp.toSql( this.boql, this.transaction.getEboContext() );
		
		sql = qp.getWhereClause( this.boql, this.transaction.getEboContext() );
		
		// Removes Where clause;
		sql = sql.substring( 5 );
		this.boDefHandler = qp.getObjectDef();
//		XEOManager x = this.transaction.getManager();
//		dataSet = x.list( 
//				this.transaction.getEboContext(), 
//				this.boDefHandler, 
//				sql, 
//				this.boqlArgs, 
//				page, 
//				pageSize
//		);
	}
	
	private long _executeRecordCount() {
		QLParser qp 	= new QLParser();
//		String 	 sql 	= "SELECT COUNT(*) FROM (" + qp.toSql( this.boql, this.transaction.getEboContext() ) + ")";
		
		this.boDefHandler = qp.getObjectDef();
//		XEOManager x = this.transaction.getManager();
//		DataSet countDataSet = x.list( 
//				this.transaction.getEboContext(), 
//				this.boDefHandler, 
//				sql, 
//				this.boqlArgs, 
//				page, 
//				pageSize
//			);
//		return countDataSet.rows( 1 ).getLong( 1 );
		return 0;
	}
	
	public boObject getObject( int rowPos ) throws boRuntimeException 
	{
		long boui = this.dataSet.rows( rowPos ).getLong( "BOUI" );
		return boObject.getBoManager().loadObject( getEboContext(), boui);
		
	}
	
	private EboContext getEboContext() {
		return boApplication.currentContext().getEboContext();
	}

	@Override
	public void setFilterTerms(FilterTerms filterTerms) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPageSize(int pageSize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setQuery(String boql, List<Object> boqlArguments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSearchText(String searchText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSortTerms(SortTerms sortTerms) {
		// TODO Auto-generated method stub
		
	}
	
//	private String composeFilterClause() {
//		return null;
//	}
//
//	private String composeOrderByClause() {
//		return null;
//	}

}
