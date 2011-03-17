package netgest.bo.xwc.components.connectors;

import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

public class XEOBridgeRowIterator implements DataListIterator {
    
    XEOBridgeListConnector oDataListConnector;
    boBridgeIterator       oBridgeIterator;
    
    public boolean skip( int nRows ) {
        return oBridgeIterator.absolute( oBridgeIterator.getRow() + nRows );
    }

    public XEOBridgeRowIterator( XEOBridgeListConnector oDataListConnector ) {
        this.oDataListConnector = oDataListConnector;
        this.oBridgeIterator = oDataListConnector.getBridge().iterator();
    }

    public boolean hasNext() {
        return !oBridgeIterator.isLast();
    }

    public DataRecordConnector next() {
        oBridgeIterator.next();
        try {
            return new XEOBridgeRecordConnector(oBridgeIterator.currentRow());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        throw new RuntimeException(ExceptionMessage.NOT_IMPLEMENTED_REMOVE_ON.toString()+" netgest.bo.xwc.components.connectors.XEOBridgeRowIterator");
    }
}
