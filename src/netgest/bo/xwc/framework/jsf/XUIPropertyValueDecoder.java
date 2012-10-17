package netgest.bo.xwc.framework.jsf;

public class XUIPropertyValueDecoder {
	
    boolean isBool 		= false;
    boolean isFloat 	= false;
    boolean isLong 		= false;
    
    double  valueDouble = 0;
    long	valueLong	= 0;
    boolean boolValue	= false;
    
    String value = null;
    
	
	public XUIPropertyValueDecoder(String value){
		this.value = value;
	}
	
	public Object getDecodedValue(){
		if( "true".equals( value ) || "false".equals( value ) ) {
	    	boolValue 	= Boolean.parseBoolean( value );
	    	isBool 		= true;
	    	return boolValue;
	    }
	    
	    if( !isBool ) {
	        try {
	        	valueLong = Long.parseLong( value );
	        	isLong     = true;
	        	return valueLong;
	        }
	        catch ( NumberFormatException e ) {
	        }
	        
	        if( !isLong ) {
	            try {
	            	valueDouble = Double.parseDouble( value );
	            	isFloat     = true;
	            	return valueDouble;
	            }
	            catch ( NumberFormatException e ) {
	            }
	        }
	    }
	    return value;
	}
	
	public boolean isLong(){
		return isLong;
	}
	
	public boolean isBoolean(){
		return isBool;
	}
	
	public boolean isFloat(){
		return isFloat;
	}
	

}
