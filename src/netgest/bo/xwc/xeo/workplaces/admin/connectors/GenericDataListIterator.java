package netgest.bo.xwc.xeo.workplaces.admin.connectors;

import java.util.Iterator;
import java.util.Map;

import netgest.bo.xwc.components.connectors.DataListIterator;
import netgest.bo.xwc.components.connectors.DataRecordConnector;

public class GenericDataListIterator implements DataListIterator {
	Iterator<Map<String, Object>> iterator;

	public GenericDataListIterator(GenericDataListConnector genericDataListConnector) {
		this.iterator = genericDataListConnector.getRows().iterator();
	}

	@Override
	public boolean skip(int nRows) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public DataRecordConnector next() {
		try {
			return new GenericDataRecordConnector(iterator.next());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented");
	}

}
