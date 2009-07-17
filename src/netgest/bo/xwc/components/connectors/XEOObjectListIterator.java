package netgest.bo.xwc.components.connectors;

import netgest.bo.runtime.boObjectList;

public class XEOObjectListIterator implements DataListIterator {
    
    private boObjectList oBoObjectList;
    private int currentRow = 0;
    
    public XEOObjectListIterator( boObjectList oBoObjectList ) {
        this.oBoObjectList = oBoObjectList;
    }

    public boolean hasNext() {
        return currentRow < oBoObjectList.getRowCount();
    }
    
    public boolean skip( int nRows ) {
        currentRow += nRows;
        if( currentRow > 0 && currentRow < oBoObjectList.getRowCount() ) {
            return true; 
        }
        else {
            if(  nRows > 0 ) currentRow = oBoObjectList.getRowCount() + 1;
            else currentRow = 0;
            return false;
        }
        
    }

    public XEOObjectConnector next() {
        int iListPos;
        long oCurrentObjectBoui = Long.MIN_VALUE;
        currentRow++;
        synchronized( oBoObjectList )
        {
            iListPos = oBoObjectList.getRow();
            if( oBoObjectList.moveTo(currentRow)) {
                oCurrentObjectBoui = oBoObjectList.getCurrentBoui();
            }
            oBoObjectList.moveTo( iListPos );
        }
        if( oCurrentObjectBoui != Long.MIN_VALUE )
        {
			return new XEOObjectListRowConnector( oCurrentObjectBoui, oBoObjectList, currentRow );
        }
        throw new RuntimeException("There's no object at current iterator position");
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
