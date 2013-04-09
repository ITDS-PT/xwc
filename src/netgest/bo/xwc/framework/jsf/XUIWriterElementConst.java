package netgest.bo.xwc.framework.jsf;

public class XUIWriterElementConst {
    
    protected String  sTagName; 
    protected boolean bCanHaveChilds;

    public XUIWriterElementConst( String sTagName ) {
        this.sTagName = sTagName;
    }

    public XUIWriterElementConst( String sTagName, boolean canHaveChilds ) {
        this.sTagName = sTagName;
        this.bCanHaveChilds = canHaveChilds;
    }
    
    public String getValue() {
        return sTagName;
    }
    
    public boolean canHaveChilds() {
        return bCanHaveChilds;
    }

}
