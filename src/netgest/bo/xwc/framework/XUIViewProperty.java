package netgest.bo.xwc.framework;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIViewProperty<V> extends XUIBaseProperty<V> {

    public XUIViewProperty(String sPropertyName, XUIComponentBase oComponent) {
		super(sPropertyName, oComponent);
	}

    public XUIViewProperty(String sPropertyName, XUIComponentBase oComponent, V oValue ) {
		super(sPropertyName, oComponent, oValue );
	}

}
