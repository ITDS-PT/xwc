package netgest.bo.xwc.components.connectors;

import netgest.bo.data.DataSet;
import netgest.bo.runtime.boObjectList;

public class XEOObjectListRowConnector extends XEOObjectConnector {

	int row;
	boObjectList oObjectList;
	public XEOObjectListRowConnector( long boui, boObjectList oObjectList ,int row ) {
		super( boui );
		this.row = row;
		this.oObjectList = oObjectList;
	}
	@Override
	public DataFieldConnector getAttribute(String name) {
		DataFieldConnector ret = super.getAttribute(name);
		if( ret == null ) {
			if( this.oObjectList.getRslt() != null ) {
				DataSet dataSet = this.oObjectList.getRslt().getDataSet();
				int col = dataSet.findColumn( name );
				if( col > 0 ) {
					ret = new XEOObjectConnector.GenericFieldConnector( 
							name, 
							String.valueOf( dataSet.rows( row ).getObject( col ) ), 
							DataFieldTypes.VALUE_CHAR 
					);
				}
			}
		}
		return ret;
	}
	
	
	
	
}
