package netgest.bo.xwc.components.viewers.beans;

import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class BuilderLogBean {

	
	public String getText()
	{
		XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
		
		XUIViewRoot		  viewRoot		 = requestContext.getViewRoot();
		BuilderDevelopmentBean bean=(BuilderDevelopmentBean)viewRoot.getParentView().getBean("viewBean");
		
		return bean.getLogText();	
	}
	
	public void canCloseTab() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		Window xWnd = (Window)viewRoot.findComponent(Window.class);
		if( xWnd != null ) {
			if( xWnd.getOnClose() != null ) {
				xWnd.getOnClose().invoke( oRequestContext.getELContext(), null);
            }
		}
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.getViewRoot().setTransient( true );
		oRequestContext.renderResponse();
	}
}
