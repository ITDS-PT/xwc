package netgest.bo.xwc.xeo.beans;

import netgest.bo.xwc.components.classic.MessageBox;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

public class Dialogs {

	/**
	 * <p>Creates the progress Message Box.<p>
	 * <p>Example of usage in a viewer Bean<p>
	 * <pre>
	 * 	import import netgest.bo.xwc.xeo.beans.Dialogs;
	 * 	import netgest.bo.xwc.xeo.beans.ProgressBean;
	 * 	...
	 * 	private ProgressBean myActionProgressBean;
	 * 
	 *  // Method called from a toolbar or other component
	 * 	public void createMyActionProgress() {
	 * 		myActionProgressBean = Dialogs.createProgress( 
	 * 			"#{viewBean.executeMyAction}" , 
	 * 			"Updating", 
	 * 			"Sending data to the server", 
	 * 			"Preparing...."
	 *		);
	 * 	}
	 * 	
	 * 	// Method to be executed after the progress MessageBox 
	 * 	public void executeMyAction() {
	 * 		for (int i = 0; i < 10; i++) {
	 * 			Thread.sleep( 2000 );
	 *			p.setProgressPercent( (i+1)*10 );
	 *			p.setProgressText( ((i+1)*10) + " %" ); 
	 *		}
	 * 	}
	 * 
	 * </pre>
	 * 
	 * @param actionExpression the action expression to be executed with progress
	 * @param title the title of the progress box
	 * @param text the text
	 * @param progressText the text to be displayed in the progress bar 
	 * 
	 * @return the progress bean
	 */
	public static ProgressBean createProgress( String actionExpression, String title, String text, String progressText ) {
		
		XUIRequestContext requestContext = XUIRequestContext.getCurrentContext();
		XUISessionContext sessionContext =  requestContext.getSessionContext();
		
		XUIViewRoot progressView = sessionContext.createView( "DialogProgress.xvw" );
		ProgressBean progressBean = (ProgressBean)progressView.getBean("viewBean");
		
		progressBean.setFinished( false );
		progressBean.setTitle( title );
		progressBean.setText( text );
		progressBean.setProgressText( progressText );
		progressBean.setTargetAction( actionExpression );
		requestContext.setViewRoot( progressView );
		
		requestContext.renderResponse();
		
		return progressBean;
	}
	
	/**
	 * Show a MessageBox that was declared on a viewer.
	 * 
	 * @param viewRoot the viewer root of the MessageBox
	 * @param dialogId the Message Box id composed with the form id 
	 */
	public static void showDialog( XUIViewRoot viewRoot, String dialogId ) {
		MessageBox mb = (MessageBox)viewRoot.findComponent(dialogId);
		if( mb != null ) {
			mb.show();
		}
		else {
			throw new RuntimeException(ExceptionMessage.DIALOG_NOT_FOUND_IN_THE_VIEWER.toString());
		}
	}
	
}
