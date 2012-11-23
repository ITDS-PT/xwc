package netgest.bo.xwc.xeo.workplaces.admin.connectors;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectConnector.GenericFieldConnector;

public class GenericDataRecordConnector implements DataRecordConnector, Map<String,Object> {
	Map<String, Object> atts;
	Map<String,GenericDataFieldMetaData> cols=null;
	
	public GenericDataRecordConnector(Map<String, Object> atts) {
		this.atts = atts;
	}
	
	public GenericDataRecordConnector(Map<String, Object> atts,
			Map<String,GenericDataFieldMetaData> cols) {
		this.atts = atts;
		this.cols = cols;
	}

	@Override
	public DataFieldConnector getAttribute(String colKey) {
		byte dataType=DataFieldTypes.VALUE_CHAR;
		if (this.cols!=null)
		{	
			GenericDataFieldMetaData colmdata=this.cols.get(colKey);
			if (colmdata!=null)
				dataType=colmdata.getDataType();
		}
		return new GenericFieldConnector(colKey,(String) this.atts.get(colKey),dataType );
	}

	@Override
	public int getRowIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getSecurityPermissions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> keySet() {
		return Collections.emptySet();
	}

	@Override
	public Object put(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object remove(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return null;
	}
}
