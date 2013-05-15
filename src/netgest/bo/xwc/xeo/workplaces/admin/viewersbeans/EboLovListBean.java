package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import org.apache.commons.lang.StringUtils;

import netgest.bo.lovmanager.LovManager;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.framework.messages.XUIMessageSender;
import netgest.bo.xwc.xeo.beans.XEOBaseList;
import netgest.bo.xwc.xeo.localization.BeansMessages;

/**
 * Supports the List of Ebo_Lov instances
 *
 */
public class EboLovListBean extends XEOBaseList {
	
	/**
	 * Reloads (removes from cache) a given lov
	 */
	public void reloadLov() throws boRuntimeException {
		
		GridPanel p = (GridPanel) getViewRoot().findComponent( GridPanel.class );
		String lovName = getSelectedLov(p);
		if (StringUtils.isEmpty( lovName )){
			XUIMessageSender.alertCritical( BeansMessages.EBO_LOV_LIST_CHOOSE_LOV.toString() );
		} else {
			LovManager.removeLovObject( lovName );
			LovManager.getLovObject( getEboContext() , lovName );
			XUIMessageSender.alertInfo( BeansMessages.EBO_LOV_LIST_RELOADED.toString( lovName ) );
		}
		
		
	}

	/**
	 * 
	 * Get the name of the lov from the selected 
	 * 
	 * @param p The grid panel
	 * 
	 * @return The name of the lov selected in the gridpanel
	 */
	private String getSelectedLov(GridPanel p) {
		DataRecordConnector connector = p.getActiveRow();
		if (connector != null){
			String result = connector.getAttribute( "name" ).getValue().toString();
			return result;
		} else {
			DataRecordConnector[] lines = p.getSelectedRows();
			if (lines != null && lines.length > 0){
				String result = lines[0].getAttribute( "name" ).getValue().toString();
				return result;
			}
		}
		return null;
	}
	
	
}
