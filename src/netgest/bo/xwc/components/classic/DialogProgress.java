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
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.properties.XUIProperty;

/**
 * A dialog progress component
 * 
 * @author jcarreira
 *
 */
public class DialogProgress extends XUIComponentBase {

	@XUIProperty(label="Title")
	private XUIViewStateBindProperty<String> 	title 			= 
		new XUIViewStateBindProperty<String>( "title", this, ComponentMessages.DIALOG_PROGRESS.toString(), String.class );
	
	@XUIProperty(label="Finished")
	private XUIStateBindProperty<Boolean> 	finished 		= 
		new XUIStateBindProperty<Boolean>( "finished", this, "false",Boolean.class );

	@XUIProperty(label="Update Interval")
	private XUIViewStateBindProperty<Integer> 	updateInterval 	= 
		new XUIViewStateBindProperty<Integer>( "updateInterval", this, "5" , Integer.class );

	@XUIProperty(label="Progress Value")
	private XUIViewStateBindProperty<Integer> 	progress 		= 
		new XUIViewStateBindProperty<Integer>( "progress", this, "0" , Integer.class );

	@XUIProperty(label="Progress Text")
	private XUIViewStateBindProperty<String> 	progressText 	= 
		new XUIViewStateBindProperty<String>( "progressText", this, "" , String.class );

	@XUIProperty(label="Text")
	private XUIViewStateBindProperty<String> 	text 			= 
		new XUIViewStateBindProperty<String>( "localText", this, "" , String.class );
	
	protected XUIStateProperty<Boolean> 		wasRendered 	= 
		new XUIStateProperty<Boolean>( "text", this );

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
			actionCmd.setActionExpression( createMethodBinding("#{" + getBeanId() + ".executeAction}") );
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
				String s = dialog.getProgressText();
				if( s == null || s.length() == 0 ) s = " ";
				
				ExtConfig dialogConfig = new ExtConfig();
				dialogConfig.addJSString( "msg" , dialog.getText() );
				dialogConfig.addJSString( "progressText" , s );
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
    	                "window.setTimeout( \"XVW.syncView('" + dialog.findParentComponent(XUIForm.class).getClientId() + "');\", " + dialog.getUpdateInterval() * 1000 + " );"
    				);
			}
			w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER , dialog.getClientId(), sb );
			
		}
	}
}
