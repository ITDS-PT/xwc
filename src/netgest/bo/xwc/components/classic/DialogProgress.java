package netgest.bo.xwc.components.classic;

import java.io.IOException;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * A dialog progress component
 * @author jcarreira
 *
 */
public class DialogProgress extends XUIComponentBase {

	private XUIStateBindProperty<String> 	title 			= 
		new XUIStateBindProperty<String>( "title", this, ComponentMessages.DIALOG_PROGRESS.toString(), String.class );
	
	private XUIStateBindProperty<Boolean> 	finished 		= new XUIStateBindProperty<Boolean>( "finished", this, "false",Boolean.class );
	private XUIStateBindProperty<Integer> 	updateInterval 	= new XUIStateBindProperty<Integer>( "updateInterval", this, "5" , Integer.class );
	private XUIStateBindProperty<Integer> 	progress 		= new XUIStateBindProperty<Integer>( "progress", this, "0" , Integer.class );
	private XUIStateBindProperty<String> 	progressText 	= new XUIStateBindProperty<String>( "progressText", this, "" , String.class );
	private XUIStateBindProperty<String> 	text 			= new XUIStateBindProperty<String>( "localText", this, "" , String.class );

	protected XUIStateProperty<Boolean> 		wasRendered 	= new XUIStateProperty<Boolean>( "text", this );

	public String getTitle() {
		return title.getEvaluatedValue();
	}

	public void setTitle(String title) {
		this.title.setExpressionText( title );
	}

	public boolean getFinished() {
		return finished.getEvaluatedValue();
	}

	public void setFinished(String finished) {
		this.finished.setExpressionText( finished );
	}

	public Integer getUpdateInterval() {
		return updateInterval.getEvaluatedValue();
	}

	public void setUpdateInterval(String updateInterval) {
		this.updateInterval.setExpressionText( updateInterval );
	}

	public int getProgress() {
		return progress.getEvaluatedValue();
	}

	public void setProgress(String progress) {
		this.progress.setExpressionText( progress );
	}

	public String getProgressText() {
		return progressText.getEvaluatedValue();
	}

	public void setProgressText(String progressText) {
		this.progressText.setExpressionText( progressText );
	}

	public String getText() {
		return text.getEvaluatedValue();
	}

	public void setText(String text) {
		this.text.setExpressionText( text ); 
	}
	
	

	private XUICommand actionCmd;
	
	@Override
	public void preRender() {
		
		if( wasRendered.getValue() == null ) {
			wasRendered.setValue( false );
		}
		
		actionCmd = (XUICommand)findComponent( getId() + "_action" );
		if( actionCmd == null ) {
			actionCmd = new XUICommand();
			actionCmd.setActionExpression( createMethodBinding("#{viewBean.executeAction}") );
			actionCmd.setId( getId() + "_action" );
			this.getChildren().add( actionCmd );
		}
		
	}

	@Override
	public boolean wasStateChanged() {
		return true;
	}

	public static class XEOHTMLRenderer extends XUIRenderer {

		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
			
			DialogProgress dialog = (DialogProgress)component;
			
			XUIResponseWriter w = getResponseWriter();
			
			StringBuilder sb = new StringBuilder();
			
			String clientid = component.getClientId().replaceAll("\\.", "_");
			clientid = component.getClientId().replaceAll(":", "_");
			
			
			String svarName = "window." +  clientid;
			
			if( dialog.getFinished() ) {
				if( dialog.wasRendered.getValue() ) {
					sb.append( "if( "+svarName+".getDialog().title=='" + JavaScriptUtils.safeJavaScriptWrite(dialog.getTitle(), '\'') +"') window." ).append( clientid ).append( ".hide();" );
					sb.append( svarName ).append( "=null" );
				}
			} else if( !dialog.wasRendered.getValue() ) {
				ExtConfig dialogConfig = new ExtConfig();
				dialogConfig.addJSString( "msg" , dialog.getText() );
				dialogConfig.addJSString( "progressText" , dialog.getProgressText() );
				dialogConfig.addJSString( "title" , dialog.getTitle() );
				dialogConfig.add( "width" , 300 );
				dialogConfig.add( "progress" , true );
				dialogConfig.add( "closable" , false );
				
				sb.append( "window." ).append( clientid ).append( "=Ext.MessageBox.show(" );
				dialogConfig.renderExtConfig( sb );
				sb.append( ");" );
				dialog.wasRendered.setValue( true );
				
				w.getScriptContext().add(  
						XUIScriptContext.POSITION_FOOTER, 
						dialog.actionCmd.getId(), 
						XVWScripts.getAjaxCommandScript( dialog.actionCmd , XVWScripts.WAIT_STATUS_MESSAGE  )
					);
			}
			else {
				sb.append( "window." ).append( clientid ).append( ".updateProgress(" );
				sb.append( Float.valueOf( dialog.getProgress() ) / 100f ).append(',');
				sb.append('"').append( JavaScriptUtils.safeJavaScriptWrite(  dialog.getProgressText(), '"') ).append('"').append(',');
				sb.append('"').append( JavaScriptUtils.safeJavaScriptWrite(  dialog.getText(), '"') ).append('"');
				sb.append( ");" );
				
			}
			
			if( !dialog.getFinished() ) {
    			XUIRequestContext.getCurrentContext().getScriptContext().add(
    					XUIScriptContext.POSITION_FOOTER, 
    					dialog.getClientId() + "_syncView", 
    	                "window.setTimeout( \"XVW.syncView('" + dialog.getNamingContainerId() + "');\", " + dialog.getUpdateInterval() * 1000 + " );"
    				);
			}
			w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER , dialog.getClientId(), sb );
			
		}
	}
}
