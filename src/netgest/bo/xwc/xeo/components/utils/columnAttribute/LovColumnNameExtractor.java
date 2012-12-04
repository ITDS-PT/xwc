package netgest.bo.xwc.xeo.components.utils.columnAttribute;

public class LovColumnNameExtractor {

	private static final String LOV_ID_PREFIX = "xeoLov";
	
	private String dataField = "";
	
	public LovColumnNameExtractor(String name){
		assert name != null : "Name cannot be null";
		this.dataField = name;
	}
	
	public String extractName(){
		if ( dataField.startsWith( LOV_ID_PREFIX ) )
			dataField = dataField.substring( LOV_ID_PREFIX.length(), dataField.length() );
		return dataField;
	}
	
	public static boolean isXeoLovColumn(String dataField){
		return dataField.startsWith( LOV_ID_PREFIX );
	}
	
	public String prefixColumnName(){
		return LOV_ID_PREFIX + dataField;
	}

}
