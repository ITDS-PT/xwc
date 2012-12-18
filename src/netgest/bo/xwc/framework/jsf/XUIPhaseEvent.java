package netgest.bo.xwc.framework.jsf;

import javax.faces.component.UIComponent;
import javax.faces.event.PhaseEvent;

public class XUIPhaseEvent {
	
	PhaseEvent event;
	UIComponent viewRoot;
	
	public XUIPhaseEvent( UIComponent root, PhaseEvent event ) {
		this.event = event; 
		this.viewRoot = root;
	}
	
	public UIComponent getSrcComponent() {
		return viewRoot;
	}

}
