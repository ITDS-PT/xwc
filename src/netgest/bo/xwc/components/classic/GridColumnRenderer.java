package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
/**
 * This interface is used to add a custim renderer to a column
 * 
 * When a GridPanel is rendering a column, e invokes the method render(...,...,...) and receive the
 * text ou html regenerated and put in the GridColumn
 * 
 * Exempla of usage:
 * <code>
 * 		<xvw:columnAttribute dataField='myFields' renderer='#{viewBean.myRenderer}'></columnAttribute>
 * 
 * 		Bean Code:
 * 		public GridColumnRenderer getMyRenderer() {	
 * 			return new GridColumnRenderer() {
 *				public String render( GridPanel grid, DataRecordConnector record, DataFieldConnector field ) {
 *					String oValue = field.getValue();
 *					return "<i>" + oValue + "</i>";
 *				} 
 * 			}
 * 		}
 * 	
 * </code>
 * 
 * @author jcarreira
 *
 */
public interface GridColumnRenderer {

	public String render( GridPanel grid, DataRecordConnector record, DataFieldConnector field );

}
