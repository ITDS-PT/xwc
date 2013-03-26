package netgest.bo.xwc.components.connectors;

import netgest.bo.data.DataSet;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

public class XEOObjectListRowConnector extends XEOObjectConnector {

	int row;
	boObjectList oObjectList;
	public XEOObjectListRowConnector( long boui, boObjectList oObjectList ,int row ) {
		super( boui, row );
		this.row = row;
		this.oObjectList = oObjectList;
	}
	
	@Override
	public DataFieldConnector getAttribute(String name) {
		preloadObject();
		DataFieldConnector ret = null;
		ret = super.getAttribute(name);
		if (ret == null){
			if( this.oObjectList.getRslt() != null ) {
				DataSet dataSet = this.oObjectList.getRslt().getDataSet();
				int col = dataSet.findColumn( name );
				if( col > 0 ) {
					Object value = dataSet.rows( row ).getObject( col );
					ret = new XEOObjectConnector.GenericFieldConnector( 
							name, 
							value!=null?String.valueOf( value ):null, 
									DataFieldTypes.VALUE_CHAR 
							);
				}
			}
		}
		return ret;
	}
	
	protected void preloadObject() {
		// Force preload...
		try {
			this.oObjectList.getObject();
		} catch (boRuntimeException e) {
			
		}
	}
	
	
	
	
}
