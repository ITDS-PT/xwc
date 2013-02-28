package netgest.bo.xwc.xeo.beans;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.component.UIComponentBase;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import netgest.bo.transaction.XTransaction;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;

/**
 * The Class ProgressBean.
 */
public class ProgressBean {

	/** The finished. */
	private boolean finished;
	
	/** The width. */
	private int width;
	
	/** The update interval. */
	private int updateInterval = 5;

	/** The progress percent. */
	private int progressPercent;

	/** The title. */
	private String title;
	
	/** The progress text. */
	private String progressText;
	
	/** The text. */
	private String text;
	
	/** The target action. */
	private String targetAction;
	
	
	/**
	 * Gets the EL Expression of target action to be executed within a progress bar.
	 * 
	 * @return the target action
	 */
	public String getTargetAction() {
		return targetAction;
	}
	
	/**
	 * Sets the EL Expression with the target action to be executed within a progress bar.
	 * 
	 * @param targetAction the new target action
	 */
	public void setTargetAction(String targetAction) {
		this.targetAction = targetAction;
	}
	
	
	/**
	 * Gets the progress percent.
	 * 
	 * @return the progress percent
	 */
	public int getProgressPercent() {
		return progressPercent;
	}
	
	/**
	 * Sets the progress percent.
	 * 
	 * @param progressPercent the new progress percent
	 */
	public void setProgressPercent(int progressPercent) {
		this.progressPercent = progressPercent;
	}

	/**
	 * Gets the update interval in seconds.
	 * 
	 * @return the update interval in seconds
	 */
	public int getUpdateInterval() {
		return updateInterval;
	}
	
	/**
	 * Sets the update interval in seconds.
	 * 
	 * @param updateInterval the new update interval in seconds
	 */
	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}
	
	
	/**
	 * Checks if is finished.
	 * 
	 * @return true, if is finished
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Sets the finished state, if true the progress dialog is closed.
	 * 
	 * @param finished the new finished
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Sets the width.
	 * 
	 * @param width the new width
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	
	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title.
	 * 
	 * @param title the new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Gets the progress text.
	 * 
	 * @return the progress text
	 */
	public String getProgressText() {
		return progressText;
	}
	
	/**
	 * Sets the progress text.
	 * 
	 * @param progressText the new progress text
	 */
	public void setProgressText(String progressText) {
		this.progressText = progressText;
	}
	
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Sets the text.
	 * 
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Execute action.
	 */
	public void executeAction() {
		
		try {
			XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
	
			XUIViewRoot parentView = requestContext.getViewRoot().getParentView();
			
			parentView.getTransactionId();
			
			requestContext.getTransactionManager().release();
			
			XTransaction oTransaction = requestContext.getTransactionManager().getTransaction( parentView.getTransactionId() );
			oTransaction.activate();
			
			ELContext context = parentView.getELContext();
			MethodExpression m = requestContext.getEvent().getComponent().createMethodBinding( this.targetAction );
			requestContext.setViewRoot( parentView );
			
			m.invoke( context, null );
		}
		finally {
			finished = true;
		}
	}
	
}
