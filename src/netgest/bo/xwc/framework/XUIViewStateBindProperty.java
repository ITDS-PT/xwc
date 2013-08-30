package netgest.bo.xwc.framework;

import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIViewStateBindProperty<V> extends XUIStateBindProperty<V>  {

	public XUIViewStateBindProperty(String sPropertyName, XUIComponentBase oComponent, String sExpressionString, Class<?> cValueType) {
		super(sPropertyName, oComponent, sExpressionString, cValueType);
	}

	public XUIViewStateBindProperty(String sPropertyName, XUIComponentBase oComponent, Class<?> cValueType) {
		super(sPropertyName, oComponent, cValueType);
	}

}
