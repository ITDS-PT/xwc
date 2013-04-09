package netgest.bo.xwc.framework.jsf;

import javax.faces.component.UIComponent;
import javax.faces.event.ValueChangeEvent;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIValueChangeEvent extends ValueChangeEvent {

	public XUIValueChangeEvent(UIComponent component, Object oldValue, Object newValue) {
		super(component, oldValue, newValue);
		// TODO Auto-generated constructor stub
	}
	
	public XUIComponentBase getComponent() {
		// TODO Auto-generated method stub
		return (XUIComponentBase)super.getComponent();
	}
	
}
