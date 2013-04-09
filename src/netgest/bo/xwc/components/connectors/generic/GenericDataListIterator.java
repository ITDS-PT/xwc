package netgest.bo.xwc.components.connectors.generic;

import java.util.Iterator;
import java.util.Map;

import netgest.bo.xwc.components.connectors.DataListIterator;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

public class GenericDataListIterator implements DataListIterator {
	Iterator<Map<String, Object>> iterator;
	private Map<String,GenericDataFieldMetaData> cols=null;
	
	public GenericDataListIterator(GenericDataListConnector genericDataListConnector) {
		this.iterator = genericDataListConnector.getRows().iterator();
		this.cols = genericDataListConnector.cols;
	}

	@Override
	public boolean skip(int nRows) {
		// TODO Auto-generated method stub
		throw new RuntimeException(ExceptionMessage.NOT_IMPLEMENTED.toString());
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public DataRecordConnector next() {
		try {
			return new GenericDataRecordConnector(iterator.next(),cols);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new RuntimeException(ExceptionMessage.NOT_IMPLEMENTED.toString());
	}

}
