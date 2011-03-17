package netgest.bo.xwc.components.connectors;

import netgest.bo.runtime.boObjectList;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

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
        throw new RuntimeException(ExceptionMessage.THERES_NO_OBJECT_AT_CURRENT_ITERATOR_POSITION.toString());
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
