package netgest.bo.xwc.components.beans;

import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class Dialogs {

	public static ProgressBean createProgress( String actionExpression, String title, String text, String progressText ) {
		
		XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
		XUISessionContext sessionContext =  requestContext.getSessionContext();
		
		XUIViewRoot progressView = sessionContext.createChildView( "DialogProgress.xvw" );
		ProgressBean progressBean = (ProgressBean)progressView.getBean("viewBean");
		
		progressBean.setFinished( false );
		progressBean.setTitle( title );
		progressBean.setText( text );
		progressBean.setProgressText( progressText );
		
		progressBean.setTargetAction( actionExpression );
		
		requestContext.setViewRoot( progressView );
		progressView.processInitComponents();
		
		requestContext.renderResponse();
		
		return progressBean;
	}
	
}
