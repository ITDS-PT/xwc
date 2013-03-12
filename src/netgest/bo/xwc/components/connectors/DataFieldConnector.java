package netgest.bo.xwc.components.connectors;

public interface DataFieldConnector extends DataFieldMetaData {
    
    public Object getValue();

    public String getDisplayValue();

    public boolean getDisabled();

    public boolean getVisible();

    public boolean getRequired();
    
    public byte 	getSecurityPermissions();
    
    public boolean 	getRecomended();
    
    public boolean 	validate(); 
    
    public boolean  isValid();

    public String 	getInvalidMessage(); 
    
    public String[] getDependences();
    
    public boolean 	getOnChangeSubmit();

    public void 	setValue( Object newValue );
    
    public boolean  getIsLovEditable();
    
    public DataListConnector getDataList();
    
}
