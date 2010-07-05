package netgest.bo.xwc.components.explorer;

public interface XUIPropertiesStore {
	
	public String getProperty( String propertyName );

	public String setProperty( String propertyName, String propertyValue );
	
	public boolean save();

}
