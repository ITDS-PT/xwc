package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.Title;

public class ViewerTitle extends Title {
	@Override
	public String getRendererType() {
		return "title";
	}

	@Override
	public void initComponent() {
		super.setValueExpression( "#{" + getBeanId() + ".title}" );
	}
	

}
