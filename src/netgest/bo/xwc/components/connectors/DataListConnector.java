package netgest.bo.xwc.components.connectors;

import java.util.Iterator;


public interface DataListConnector {
	
	public static final int CAP_FULLTEXTSEARCH = 1;
	public static final int CAP_PAGING = 2;
	public static final int CAP_SORT = 4;
	public static final int CAP_FILTER = 8;
	public static final int CAP_GROUPING = 16;
	
    public DataListIterator iterator();
    
    public int getRecordCount();
    
    public int getRowCount();
    
    public DataRecordConnector findByUniqueIdentifier( String sUniqueIdentifier );
    
    public void setSearchText( String sSearchText );
    
    public void setSortTerms( SortTerms sortTerms );
    
    public void setFilterTerms( FilterTerms sortTerms );
    
    public void setSearchTerms( String[] columnName, Object[] sColumnValue );
    
    public void refresh();
    
    public int 	getPage();
    
    public void setPage( int pageNo );
    
    public int 	getPageSize();
    
    public void setPageSize( int pageSize );
    
    public int 	dataListCapabilities();
    
    public DataFieldMetaData getAttributeMetaData( String attributeName );
    
    public void setGroupBy( String[] attributes );
    
    public DataGroupConnector getGroups( int deep, String[] parentGroups, Object[] parentValues, int page, int pageSize );

    public DataListConnector getGroupDetails( int deep, String[] parentGroups, Object[] parentValues, int page, int pageSize );
    
}
