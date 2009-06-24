package netgest.bo.xwc.components.connectors;

import java.util.Map;

public interface DataFieldMetaData {
	
    public byte  getDataType();
    public byte  getInputRenderType();
    public int   getMaxLength();
    
    public int   	getDecimalPrecision();
    public int   	getMinDecimals();

    public double   getNumberMaxValue();
    public double   getNumberMinValue();
    public boolean  getNumberGrouping();
    
    public boolean  getIsLov();
    public String   getLabel();

    public byte		getSecurityPermissions();
    
    public Map<Object,String> getLovMap();
	
}
