package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.connectors.DataRecordConnector;

/**
 * Interface to allow the customization of the render of column in a GridPanel
 * @author jcarreira
 *
 */
public interface GridRowRenderClass {

	public String getRowClass( GridPanel grid, DataRecordConnector record );
	
}
