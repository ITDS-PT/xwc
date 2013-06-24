package netgest.bo.xwc.components.connectors;


public interface DataRecordConnector 
{
	
    public DataFieldConnector getAttribute( String name );
    public int				  getRowIndex();
    public byte	getSecurityPermissions();
    
}
