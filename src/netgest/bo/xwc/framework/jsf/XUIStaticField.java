package netgest.bo.xwc.framework.jsf;

public class XUIStaticField {

    private int iValue;
    private String sName;
    
    public XUIStaticField( int iValue, String sName ) {
        this.iValue = iValue;
        this.sName = sName;
    }

    public int getValue() {
        return iValue;
    }

    public String getName() {
        return sName;
    }

}
