package netgest.bo.xwc.components.connectors;

import java.util.Iterator;


public class XEOObjectListGroupIterator implements DataListIterator, Iterator<DataRecordConnector> {
	
	private int currentPos = 0;
	private XEOObjectListGroupConnector parent;

	public XEOObjectListGroupIterator( XEOObjectListGroupConnector parent ) {
		this.parent = parent;
	}
	
	public boolean skip(int rows) {
		currentPos += rows;
		if( currentPos > parent.getDataSet().getRowCount() ) {
			currentPos = parent.getDataSet().getRowCount();
			return false;
		}
		return true;
	}

	public boolean hasNext() {
		return currentPos < parent.getDataSet().getRowCount();
	}

	public DataRecordConnector next() {
		// TODO Auto-generated method stub
		currentPos++;
		return new XEOObjectListGroupDataRecord( currentPos, this.parent );
		
	}

	public void remove() {
		// TODO Auto-generated method stub

	}

}
