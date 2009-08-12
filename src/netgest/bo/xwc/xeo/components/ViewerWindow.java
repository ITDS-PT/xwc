package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.Window;

public class ViewerWindow extends Window {
	@Override
	public String getRendererType() {
		return "window";
	}
	
	@Override
	public void initComponent() {
		super.initComponent();
		super.setUseExtJsRenderer( false );
	}
	
}
