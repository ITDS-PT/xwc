package netgest.bo.xwc.components.connectors;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boBridgeRow;
import netgest.bo.runtime.boRuntimeException;

public class XEOBridgeRecordConnector extends XEOObjectConnector {
    
    private boBridgeRow oBridgeRow;
    
    public XEOBridgeRecordConnector( boBridgeRow oBridgeRow ) throws boRuntimeException {
        super(oBridgeRow.getValueLong(), oBridgeRow.getLine() );
        this.oBridgeRow = oBridgeRow;
    }

    @Override
    protected AttributeHandler getXEOAttribute(String sAttributeName) {
        // Check if attribute exists in the bridge;
        AttributeHandler oAttrHandler;
		try {
			oAttrHandler = super.decodeAttribute( sAttributeName, oBridgeRow.getObject() );
		} catch ( boRuntimeException e ) {
			throw new RuntimeException(e);
		}
        
        
        return oAttrHandler;
    }   
    
}
