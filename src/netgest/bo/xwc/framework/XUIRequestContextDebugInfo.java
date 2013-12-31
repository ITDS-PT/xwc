package netgest.bo.xwc.framework;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;

class XUIRequestContextDebugInfo {
	
	String mainViewId = "";
	boolean ajaxRequest = false;
	long requestId;
	List<String> innerViews = new ArrayList<String>();
	List<String> customContext = new ArrayList<String>();
	String hostname = "";
	String beanContext = "";
	String eventContext = "";
	Date dateError = new Date(System.currentTimeMillis());
	
	void setMainViewId(String mainViewId) {
		if (mainViewId != null)
			this.mainViewId = mainViewId;
	}
	
	void setRequestId(long requestId){
		this.requestId = requestId;
	}

	void setAjaxRequest(boolean ajaxRequest) {
		this.ajaxRequest = ajaxRequest;
	}

	void setInnerViews(List<String> innerViews) {
		this.innerViews = innerViews;
	}

	void setHostname(String hostname) {
		if (hostname != null)
			this.hostname = hostname;
	}

	void setBeanContext(String beanContext) {
		if (beanContext != null)
			this.beanContext = beanContext;
	}

	void setDateError(Date dateError) {
		this.dateError = dateError;
	}
	
	void addCustomContext(String context){
		customContext.add(context);
	}
	
	boolean hasViewInfo(){
		return StringUtils.hasValue(mainViewId);
	}

	void buildDebugInfoForEvent(XUIComponentBase component){
		if (component == null){
			eventContext = "";
		}
		String result = component.getClass().getName() + ":" + component.getClientId();
		if (component instanceof XUICommand){
			XUICommand command = (XUICommand) component;
			if (command.getActionExpression() != null){
				String expression = command.getActionExpression().getExpressionString();
				result += ":expression " + expression;
			}
			Object value = command.getCommandArgument();
			if (value != null && value.toString().length() > 0){
				result += ":value" + value.toString();
			}
		}
		eventContext = result;
	}

	public String getMainViewId() {
		return mainViewId;
	}

	public boolean isAjaxRequest() {
		return ajaxRequest;
	}

	public List<String> getInnerViews() {
		return innerViews;
	}

	public String getHostname() {
		return hostname;
	}

	public String getBeanContext() {
		return beanContext;
	}

	public String getEventContext() {
		return eventContext;
	}

	public Date getDateError() {
		return dateError;
	}
	
	public long getRequestId(){
		return requestId;
	}

	public List<String> getCustomContext(){
		return customContext;
	}
	
}
