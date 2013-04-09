package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XUIExtJsComponent extends XUIComponentBase {
	
	public XUIBaseProperty<ExtConfig> 
		extConfig = new XUIBaseProperty<ExtConfig>("extConfig", this );
	
	public void setExtConfig( ExtConfig extJsConfig ) {
		this.extConfig.setValue( extJsConfig);
	}
	
	public ExtConfig getExtConfig( ) {
		return this.extConfig.getValue();
	}
	
}
