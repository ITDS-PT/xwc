package netgest.bo.xwc.components.assync;

import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Component that provides asynchronous loading of its content 
 *
 */
public class AsyncRegion extends XUIComponentBase {
	
	/**
	 * The delay for the initial load request (in miliseconds)
	 * Defaults to 0 which means request immediately
	 */
	XUIBindProperty< Integer > delay = new XUIBindProperty< Integer >(
			"delay" , this , Integer.class, "0" );

	public Integer getDelay() {
		return delay.getEvaluatedValue();
	}

	public void setDelay(String newValExpr) {
		delay.setExpressionText( newValExpr );
	}
	
	/**
	 * Message to display while loading the content
	 */
	XUIBindProperty< String > waitMessage = new XUIBindProperty< String >(
			"waitMessage" , this , String.class, ComponentMessages.ASSYNCH_REGION_WAIT_MESSAGE.toString() );

	public String getWaitMessage() {
		return waitMessage.getEvaluatedValue();
	}

	public void setWaitMessage(String newValExpr) {
		waitMessage.setExpressionText( newValExpr );
	}
	
	/**
	 * Path to an icon to display before the message when loading the content for the first time
	 */
	XUIBindProperty< String > pathIcon = new XUIBindProperty< String >(
			"PathIcon" , this , String.class, "jquery-xeo/images/loading.gif" );

	public String getPathIcon() {
		return pathIcon.getEvaluatedValue();
	}

	public void setPathIcon(String newValExpr) {
		pathIcon.setExpressionText( newValExpr );
	}
	
	/**
	 * Custom html to show when loading the content the first time
	 */
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
	
	/**
	 * Time between refresh updates are requested (in seconds)
	 * Defaults to 0 (which means never refresh)
	 */
	XUIBindProperty< Integer > refreshInterval = new XUIBindProperty< Integer >(
			"refreshInterval" , this , Integer.class, "0" );

	public Integer getRefreshInterval() {
		return refreshInterval.getEvaluatedValue();
	}

	public void setRefreshInterval(String newValExpr) {
		refreshInterval.setExpressionText( newValExpr );
	}

}
