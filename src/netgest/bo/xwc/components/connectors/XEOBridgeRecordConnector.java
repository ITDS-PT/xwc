package netgest.bo.xwc.components.connectors;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boBridgeRow;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.framework.XUIRequestContext;

public class XEOBridgeRecordConnector extends XEOObjectConnector {
    
    private boBridgeRow oBridgeRow;
    
    public XEOBridgeRecordConnector( boBridgeRow oBridgeRow ) throws boRuntimeException {
        super(oBridgeRow.getValueLong());
        this.oBridgeRow = oBridgeRow;
    }


    @Override
    protected AttributeHandler getXEOAttribute(String sAttributeName) {
        
        // Check if attribute exists in the bridge;
        AttributeHandler oAttrHandler;
        
        oAttrHandler = oBridgeRow.getAttribute( sAttributeName );

        if (oAttrHandler == null) {
            try {
            	if( oBridgeRow.getParent().getEboContext() == null ) {
            		boObject.getBoManager().loadObject( boApplication.currentContext().getEboContext(), oBridgeRow.getParent().getBoui() );
            	}
            	oAttrHandler = super.decodeAttribute( sAttributeName , oBridgeRow.getObject());
            } catch (boRuntimeException e) {
                throw new RuntimeException(e);
            }
        }
        
        return oAttrHandler;
        
    }
}
