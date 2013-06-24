package netgest.bo.xwc.components.connectors;

import java.util.Iterator;

public interface DataListIterator extends Iterator<DataRecordConnector> {

    public boolean skip( int nRows );
    
}
