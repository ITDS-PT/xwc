package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;

public interface GridColumnRenderer {

	public String render( GridPanel grid, DataRecordConnector record, DataFieldConnector field );

}
