package netgest.bo.xwc.components.beans;

import javax.el.MethodExpression;

import netgest.bo.transaction.XTransaction;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class ProgressBean extends XEOBase {

	private boolean finished;
	
	private int width;
	private int updateInterval = 5;

	private int progressPercent;

	private String title;
	private String progressText;
	private String text;
	
	private String targetAction;
	
	
	public String getTargetAction() {
		return targetAction;
	}
	public void setTargetAction(String targetAction) {
		this.targetAction = targetAction;
	}
	public int getProgressPercent() {
		return progressPercent;
	}
	public void setProgressPercent(int progressPercent) {
		this.progressPercent = progressPercent;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}
	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}
	
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getProgressText() {
		return progressText;
	}
	public void setProgressText(String progressText) {
		this.progressText = progressText;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public void executeAction() {
		
		try {
			XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
	
			XUIViewRoot parentView = getParentView();
			
			parentView.getTransactionId();
			
			requestContext.getTransactionManager().release();
			
			XTransaction oTransaction = requestContext.getTransactionManager().getTransaction( parentView.getTransactionId() );
			oTransaction.activate();
			
			MethodExpression m = requestContext.getEvent().getComponent().createMethodBinding( this.targetAction );
			requestContext.setViewRoot( parentView );
			m.invoke( requestContext.getELContext(), null );
		}
		finally {
			finished = true;
		}
	}
	
}
