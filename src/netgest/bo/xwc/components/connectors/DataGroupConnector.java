package netgest.bo.xwc.components.connectors;

public interface DataGroupConnector {
	
    public DataListIterator iterator();

    public int getRecordCount();
    
    public int getRowCount();
    
    public void refresh();
    
    public int 	getPage();
    
    public void setPage( int pageNo );
    
    public int 	getPageSize();
    
    public void setPageSize( int pageSize );
    
    public DataFieldMetaData getAttributeMetaData( String attributeName );
    
    public boolean hasMorePages();

}
