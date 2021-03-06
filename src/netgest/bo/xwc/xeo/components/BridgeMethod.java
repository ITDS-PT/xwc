package netgest.bo.xwc.xeo.components;

import javax.faces.component.UIComponent;

import netgest.bo.runtime.bridgeHandler;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.XEOBridgeListConnector;

public class BridgeMethod extends ViewerMethod {

	public boolean isDisabled() {
		
		UIComponent parent =  this.getParent();
		if (parent instanceof BridgeToolBar){
			//Only makes sense for the top most menu
			DataListConnector connector = ((Bridge)((BridgeToolBar) this.getParent()).getParent()).getDataSource();
			if (connector instanceof XEOBridgeListConnector){
				XEOBridgeListConnector bridgeConnector = (XEOBridgeListConnector) connector;
				bridgeHandler handler = bridgeConnector.getBridge();
				return handler.hasMaxElements() || super.isDisabled();
			}
		}
		return false;
	}
	
	public StateChanged wasStateChanged2(){
		//Had to do this to force it to re-render
		return StateChanged.FOR_RENDER;
	}
	
}
