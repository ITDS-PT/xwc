package netgest.bo.xwc.framework;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIViewBindProperty<V> extends XUIBindProperty<V> {

	
	public XUIViewBindProperty(String sPropertyName, XUIComponentBase oComponent, V oDefaultValue, Class cValueType) {
		super(sPropertyName, oComponent, oDefaultValue, cValueType);
	}

	public XUIViewBindProperty(String sPropertyName, XUIComponentBase oComponent, Class cValueType, String sExpressionString) {
		super(sPropertyName, oComponent, cValueType, sExpressionString);
	}

	public XUIViewBindProperty(String sPropertyName, XUIComponentBase oComponent, Class cValueType) {
		super(sPropertyName, oComponent, cValueType);
	}
	
}
