package netgest.bo.xwc.xeo.beans;

import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

/**
 * 
 * Bean to Support Showing differences bettwen an object and an unsaved  
 * version of the same object
 * 
 */
public class ShowDifferenceBean extends XEOBaseBean {

	private String differenceResult = "";
	
	public void setDifferences(String result){
		this.differenceResult = result;
	}
	
	public String getShowDifferences(){
		return differenceResult;
	}
	
	public void canCloseTab(){
		XUIRequestContext oRequestContext = getRequestContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.renderResponse();
	}
	
}
