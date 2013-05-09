package netgest.bo.xwc.components.assync;

import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class AssyncRegion extends XUIComponentBase {
	
	/**
	 * The delay for the request (in mili-seconds)
	 */
	XUIBindProperty< Integer > delay = new XUIBindProperty< Integer >(
			"delay" , this , Integer.class, "0" );

	public Integer getDelay() {
		return delay.getEvaluatedValue();
	}

	public void setDelay(String newValExpr) {
		delay.setExpressionText( newValExpr );
	}
	
	XUIBindProperty< String > waitMessage = new XUIBindProperty< String >(
			"waitMessage" , this , String.class, ComponentMessages.ASSYNCH_REGION_WAIT_MESSAGE.toString() );

	public String getWaitMessage() {
		return waitMessage.getEvaluatedValue();
	}

	public void setWaitMessage(String newValExpr) {
		waitMessage.setExpressionText( newValExpr );
	}
	
	XUIBindProperty< String > pathIcon = new XUIBindProperty< String >(
			"PathIcon" , this , String.class, "jquery-xeo/images/loading.gif" );

	public String getPathIcon() {
		return pathIcon.getEvaluatedValue();
	}

	public void setPathIcon(String newValExpr) {
		pathIcon.setExpressionText( newValExpr );
	}
	
	XUIBindProperty< String > loadingHtml = new XUIBindProperty< String >(
			"loadingHtml" , this , String.class );

	public String getLoadingHtml() {
		return loadingHtml.getEvaluatedValue();
	}

	public void setLoadingHtml(String newValExpr) {
		loadingHtml.setExpressionText( newValExpr );
	}
	
	public boolean usesCustomLoadingHtml(){
		return !loadingHtml.isDefaultValue();
	}

}
