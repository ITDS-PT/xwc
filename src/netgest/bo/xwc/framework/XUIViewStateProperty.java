package netgest.bo.xwc.framework;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIViewStateProperty<V> extends XUIStateProperty<V>  {

	public XUIViewStateProperty( String sPropertyName, XUIComponentBase oComponent) {
		super(sPropertyName, oComponent);
	}

	public XUIViewStateProperty(String sPropertyName, XUIComponentBase oComponent, V oValue) {
		super(sPropertyName, oComponent, oValue);
	} 
	
}
