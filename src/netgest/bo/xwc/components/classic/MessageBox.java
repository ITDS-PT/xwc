/*
 * 
 */
package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import org.json.JSONArray;
import org.json.JSONException;

import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIInput;

/**
 * <p>Component to generate different styles of message boxes.</p>
 * 
 
 * <p>Example of usages</p>
 * 
 * <p>Declaration in a viewer using predefined buttons</p><pre>
 *         	&lt;xvw:messageBox 
 *       			id='myMessageBox' 
 *       			messageBoxType='WARNING'
 *       			buttons='YESNO'
 *       			actions='["#{viewBean.myYesBeanMethod}","#{viewBean.myNoBeanMethod}"]'
 *       			title='Message Box Title' 
 *       			message='My Message Box text' 
 *       	/>
 * </pre>
 * 
 * <p>Declaration in a viewer using custom buttons</p><pre>
 *         	
 *         &lt;xvw:messageBox 
 *       			id='myMessageBoxId' 
 *       			messageBoxType='WARNING'
 *       			title='Message Box Title' 
 *       			defaultConfirmButton='btn1'
 *       			defaultCancelButton='btn3'
 *       			message='My Message Box text' 
 *       	>
 *       		&lt;xvw:menu text='Button 1' id='btn1' serverAction='#{viewBean.btn1Action}' />
 *       		&lt;xvw:menu text='Button 2' id='btn2' serverAction='#{viewBean.btn2Action}' />
 *       		&lt;xvw:menu text='Button 3' id='btn3' serverAction='#{viewBean.btn3Action}' />
 *       	&lt;/xvw:messageBox>
 * 
 * </pre>
 * 
 *<p>Showing the Dialog Box in a bean method</p>
 * <pre>
 * 		import netgest.bo.xwc.xeo.beans.Dialogs;
 * 		...
 * 		...
 * 		public void someMehtod() {
 * 			Dialogs.showDialog( getViewRoot(), "formId:myMessageBoxId" );	
 * 		}
 * </pre>
 *  
 *  
 */
public class MessageBox extends XUIInput {
	
	/**
	 * The Enum MessageBoxType.
	 */
	public enum MessageBoxType {
		
		/** The ERROR style Message Box. */
		ERROR,
		
		/** The INFO style Message Box. */
		INFO,
		
		/** The QUESTION style Message Box.*/
		QUESTION,
		
		/** The WARNING style Message Box.*/
		WARNING
	}

	/**
	 * The Enum MessageBoxButtons.
	 */
	public enum MessageBoxButtons {
		
		/** The YES template button. */
		YES,
		
		/** The OK template button. */
		OK,
		
		/** The OKCANCEL template buttons. */
		OKCANCEL,
		
		/** The YESNO template buttons. */
		YESNO,
		
		/** The YESNOCANCEL template buttons. */
		YESNOCANCEL,
	}
	
	/** The OK. */
	private final Menu OK = new Menu();
	
	/** The CANCEL. */
	private final Menu CANCEL = new Menu();

	/** The YES. */
	private final Menu YES = new Menu();
	
	/** The NO. */
	private final Menu NO = new Menu();

	/** The OKCANCEL. */
	private final List<Menu> OKCANCEL = new ArrayList<Menu>(2);
	
	/** The YESNO. */
	private final List<Menu> YESNO = new ArrayList<Menu>(2);
	
	/** The YESNOCANCEL. */
	private final List<Menu> YESNOCANCEL = new ArrayList<Menu>(3);
	
	
	/**
	 * The title of the message box
	 */
	private XUIBindProperty<String> title 
		= new XUIBindProperty<String>("title", this, "Title!", String.class );

	
	/**
	 * The content of the message box
	 */
	private XUIBindProperty<String> message 
		= new XUIBindProperty<String>("message", this, "Message!", String.class );

//	/** The prompt multi line. */
//	private XUIBaseProperty<Boolean> promptMultiLine 
//		= new XUIBaseProperty<Boolean>("promptMultiLine", this, false );
	
	
	/**
	 * The minimum width of the {@link MessageBox}
	 */
	private XUIBaseProperty<Integer> minWidth 
		= new XUIBaseProperty<Integer>("minWidth", this, 100 );
	
	/**
	 * The maximum width of the {@link MessageBox}
	 */
	private XUIBaseProperty<Integer> maxWidth 
		= new XUIBaseProperty<Integer>("maxWidth", this, 600 );
	
	/**
	 * The top position of the {@link MessageBox}
	 */
	private XUIBindProperty<Integer> top 
		= new XUIBindProperty<Integer>("top", this, 0, Integer.class );

	/**
	 * The left position of the {@link MessageBox}
	 */
	private XUIBindProperty<Integer> left 
		= new XUIBindProperty<Integer>("left", this, 0, Integer.class  );

	
	/**
	 * The set of buttons to display in the {@link MessageBox}, must be one from
	 * {@link MessageBox.MessageBoxButtons}
	 */
	@Values({"YES","OK","OKCANCEL","YESNO","YESNOCANCEL"})
	private XUIBaseProperty<String> buttons 
		= new XUIBaseProperty<String>("buttons", this, MessageBoxButtons.OK.name() );

	
	/**
	 * 
	 * The set of actions binded to the buttons. If the component has two buttons, it must provide
	 * two actions (meaning, the number of actions must match the number of components)
	 * 
	 * To fill this property a JSON Array must be specified, featuring the same number
	 * of positions as the number of buttons for the components
	 * 
	 * Eg. If the button used is OKCANCEL a two position array should be provided, the first
	 * position with the action for the OK button and the other with the action for the "CANCEL"
	 * button, as seen bellow
	 * 
	 * actions="['#{viewBean.myOkAction}','#{viewBean.myCancelAction}']"
	 * 
	 */
	private XUIBaseProperty<String> actions 
		= new XUIBaseProperty<String>("actions", this );

	
	/**
	 * The icon to be shown in the MessageBox, the types of icon 
	 * are declared in the {@link MessageBox.MessageBoxType} enumeration
	 */
	@Values({"ERROR","INFO","QUESTION","WARNING"})
	private XUIBaseProperty<MessageBoxType> messageBoxType 
		= new XUIBaseProperty<MessageBoxType>("messageBoxType", this, MessageBoxType.INFO );
	
	
	/**
	 * The identifier of the button that'll be invoked when the user presses
	 * the ENTER key
	 */
	private XUIBaseProperty<String>	defaultConfirmButton 
		= new XUIBaseProperty<String>( "defaultConfirmButton", this );
	
	
	/**
	 * The identifier of the button that'll be invoked when the user presses the
	 * ESC key
	 */
	private XUIBaseProperty<String>	defaultCancelButton 
		= new XUIBaseProperty<String>( "defaultCancelButton", this );
	
	/** The show message box. */
	private boolean showMessageBox = false;
	
	/**
	 * Gets the title of the Message Box.
	 * 
	 * @return the title of the dialog
	 */
	public String getTitle() {
		return title.getEvaluatedValue();
	}

	/**
	 * Sets the title of the Message Box.
	 * 
	 * @param title the new title of the Message Box
	 */
	public void setTitle(String title) {
		this.title.setExpressionText( title );
	}
	
	/**
	 * Gets the title of the Message Box.
	 * 
	 * @return the top position of the dialog
	 */
	public int getTop() {
		return top.getEvaluatedValue();
	}

	/**
	 * Sets the top position of the Message Box.
	 * 
	 * @param title the new postion of the Message Box
	 */
	public void setTop(String top) {
		this.top.setExpressionText( top );
	}
	
	/**
	 * Gets the left position of the Message Box.
	 * 
	 * @return the postion of the dialog
	 */
	public int getLeft() {
		return left.getEvaluatedValue();
	}

	/**
	 * Sets the left position of the Message Box.
	 * 
	 * @param left the new postion of the Message Box
	 */
	public void setLeft(String left) {
		this.left.setExpressionText( left );
	}

	
	
	/**
	 * Gets the message to display in the Message Box.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message.getEvaluatedValue();
	}

	/**
	 * Sets the message to display in the Message Box.
	 * 
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message.setExpressionText( message );
	}

//	/**
//	 * Gets the if the prompt allows multi lines.
//	 * 
//	 * @return true - if the prompt is multi line.
//	 */
//	public boolean getPromptMultiLine() {
//		return promptMultiLine.getValue();
//	}
//
//	/**
//	 * Sets the prompt to allow multi line line input (TextArea).
//	 * 
//	 * @param promptMultiLine true to allow multiple lines in the prompt text
//	 */
//	public void setPromptMultiLine(boolean promptMultiLine) {
//		this.promptMultiLine.setValue( promptMultiLine );
//	}

	/**
	 * Gets the min width of the Message Box in pixels.
	 * 
	 * @return the min width of the Message Box in pixels
	 */
	public int getMinWidth() {
		return minWidth.getValue();
	}

	/**
	 * Sets the minimum width of the Message Box.
	 * 
	 * @param minWidth the new minimum width
	 */
	public void setMinWidth( int minWidth ) {
		this.minWidth.setValue( minWidth );
	}

	/**
	 * Gets the max widtho of the Message Box.
	 * 
	 * @return the max width
	 */
	public int getMaxWidth() {
		return maxWidth.getValue();
	}

	/**
	 * Sets the max width.
	 * 
	 * @param maxWidth the new max width
	 */
	public void setMaxWidth( int maxWidth ) {
		this.maxWidth.setValue(  maxWidth );
	}
	
	/**
	 * Sets the message box icon type.
	 * 
	 * @param type the new message box type
	 */
	public void setMessageBoxType( MessageBoxType type ) {
		this.messageBoxType.setValue( type );
	}

	/**
	 * Sets the message box icon based on the next values.
	 * 
	 * The acceptable values are:
	 * 
	 * ERROR - For error Message Box boxes
	 * INFO - For info Message Box boxes
	 * QUESTION - For question Message Box boxes
	 * WARNING - For warning Message Box boxes
	 * 
	 * @param type the new message box type
	 */
	public void setMessageBoxType( String type ) {
		this.messageBoxType.setValue( MessageBoxType.valueOf( type ) );
	}
	
	/**
	 * Gets the message box icon type.
	 * 
	 * @return the message box type
	 */
	public MessageBoxType getMessageBoxType() {
		return this.messageBoxType.getValue();
	}
	
	
	/**
	 * Sets the predefined buttons.
	 * 
	 * The acceptable values are:
	 * 
	 * YES - Add a Yes button to the Message Box
	 * OK - Add a OK button in the Message Box
	 * YESNO - Add a YES and a NO buttons to the Message Box
	 * YESNOCANCEL - Add a Yes, No and Cancel buttons to the Message Box 
	 * OKCANCEL - Add a OK and Cancel buttons to the Message Box
	 * 
	 * @param btns the new buttons
	 */
	public void setButtons( String btns ) {
		setButtons( MessageBoxButtons.valueOf( btns ) );
	}
	
	/**
	 * Sets the buttons.
	 * 
	 * @param buttons the new buttons
	 */
	public void setButtons( MessageBoxButtons buttons ) {
		this.buttons.setValue( buttons.name() );
	}
	
	/**
	 * Gets the buttons.
	 * 
	 * @return the buttons
	 */
	public MessageBoxButtons getButtons() {
		return MessageBoxButtons.valueOf( this.buttons.getValue() );
	}
	
	/**
	 * <p>Sets the actions.</p>
	 * 
	 * <p>String in the notation JavaScript Array with Method expression's for the pre-configured buttons.<br> 
	 * The sequence of the expressions must be the same as the name of predefined buttons.</p>
	 * 
	 * For example:<br>
	 * In a MessageBox with the buttons OKCANCEL, the string must be like this.<pre>
	 * ['#{viewBean.myOkAction}','#{viewBean.myCancelAction}']
	 * </pre>
	 * 
	 * @param actions the new actions
	 */
	public void setActions( String actions ) {
		this.actions.setValue( actions );
	}

	/**
	 * Gets the actions.
	 * 
	 * @return the actions
	 */
	public String getActions() {
		return this.actions.getValue();
	}
	
	/**
	 * Show the Message Box Box in the end of the request.
	 */
	public void show() {
		this.showMessageBox = true;
	}
	
	/**
	 * Gets the if the message box is to be showed on the client of the request was finished.
	 * 
	 * @return the show message box
	 */
	public boolean getShowMessageBox() {
		return this.showMessageBox;
	}
	
	/**
	 * Gets the default confirm button.
	 * 
	 * @return the default confirm button
	 */
	public String getDefaultConfirmButton() {
		return defaultConfirmButton.getValue();
	}

	/**
	 * Sets the default confirm button.
	 * 
	 * This accept's the id of the child component that should be select when the Message Box is showed.
	 * 
	 * @param expressionText the new default confirm button
	 */
	public void setDefaultConfirmButton(String expressionText ) {
		this.defaultConfirmButton.setValue( expressionText );
	}

	/**
	 * Gets the default cancel button.
	 * 
	 * @return the default cancel button
	 */
	public String getDefaultCancelButton() {
		return defaultCancelButton.getValue();
	}

	/**
	 * Sets the default cancel button.
	 * 
	 * This accept's the id of the child component that should executed when the user closes the dialog
	 * or when the presses the Escape Key
	 * 
	 * @param expressionText the new default cancel button
	 */
	public void setDefaultCancelButton( String expressionText ) {
		this.defaultCancelButton.setValue( expressionText );
	}

	/* (non-Javadoc)
	 * @see netgest.bo.xwc.framework.components.XUIComponentBase#wasStateChanged()
	 */
	@Override
	public boolean wasStateChanged() {
		// TODO Auto-generated method stub
		return getShowMessageBox();
	}

	/* (non-Javadoc)
	 * @see netgest.bo.xwc.framework.components.XUIComponentBase#initComponent()
	 */
	@Override
	public void initComponent() {
		// TODO Auto-generated method stub
		super.initComponent();
		
		OK.setText( ComponentMessages.DIALOG_OK_BTN.toString() );
		CANCEL.setText( ComponentMessages.DIALOG_CANCEL_BTN.toString() );
		
		YES.setText( ComponentMessages.DIALOG_YES_BTN.toString() );
		NO.setText( ComponentMessages.DIALOG_NO_BTN.toString() );

		OKCANCEL.add( OK );
		OKCANCEL.add( CANCEL );

		YESNO.add( YES );
		YESNO.add( NO );

		YESNOCANCEL.add( YES );
		YESNOCANCEL.add( NO );
		YESNOCANCEL.add( CANCEL );
		
		JSONArray jarr = null;
		String buttonActions = getActions();
		try {
			if( buttonActions != null && buttonActions.length() > 0 ) {
					jarr = new JSONArray( buttonActions );
			}
			if( getChildren().isEmpty() ) {
				switch( getButtons() ) {
					case OK:
						getChildren().add( OK );
						if( jarr != null && jarr.length() > 0 ) {
							String serverAction = (String)jarr.get( 0 );
							if( serverAction != null && serverAction.trim().length() > 0 ) {
								OK.setServerAction( serverAction );
							}
						}
						break;
					case YES:
						getChildren().add( YES );
						if( jarr != null && jarr.length() > 0 ) {
							String serverAction = (String)jarr.get( 0 );
							if( serverAction != null && serverAction.trim().length() > 0 ) {
								YES.setServerAction( serverAction );
							}
						}
						break;
					case OKCANCEL:
						getChildren().addAll( OKCANCEL );
						if( jarr != null ) {
							if( jarr.length() > 0 ) { 
								String serverAction = (String)jarr.get( 0 );
								if( serverAction != null && serverAction.trim().length() > 0 ) {
									OK.setServerAction( (String)jarr.get( 0 ) );
								}
							}
							if( jarr.length() > 1 ) { 
								String serverAction = (String)jarr.get( 1 );
								if( serverAction != null && serverAction.trim().length() > 0 ) {
									CANCEL.setServerAction( serverAction );
								}
							}
						}
						break;
					case YESNO:
						getChildren().addAll( YESNO );
						if( jarr != null ) {
							if( jarr.length() > 0 ) {
								String serverAction = (String)jarr.get( 0 );
								if( serverAction != null && serverAction.trim().length() > 0 ) {
									YES.setServerAction( serverAction );
								}
							}
							if( jarr.length() > 1 ) { 
								String serverAction = (String)jarr.get( 1 );
								if( serverAction != null && serverAction.trim().length() > 0 ) {
									NO.setServerAction( serverAction );
								}
							}
						}
						break;
					case YESNOCANCEL:
						getChildren().addAll( YESNOCANCEL );
						if( jarr != null ) {
							if( jarr.length() > 0 ) { 
								String serverAction = (String)jarr.get( 0 );
								if( serverAction != null && serverAction.trim().length() > 0 ) {
									YES.setServerAction( serverAction );
								}
							} 
							
							if( jarr.length() > 1 ) { 
								String serverAction = (String)jarr.get( 1 );
								if( serverAction != null && serverAction.trim().length() > 0 ) {
									NO.setServerAction( serverAction );
								}
							}
							
							if( jarr.length() > 2 ) { 
								String serverAction = (String)jarr.get( 2 );
								if( serverAction != null && serverAction.trim().length() > 0 ) {
									CANCEL.setServerAction( serverAction );
								}
							}
						}
						break;
				}
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The Class XEOHTMLRenderer.
	 */
	public static class XEOHTMLRenderer extends XUIRenderer {

		/* (non-Javadoc)
		 * @see netgest.bo.xwc.framework.XUIRenderer#encodeEnd(netgest.bo.xwc.framework.components.XUIComponentBase)
		 */
		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
			
			
			MessageBox 	box = (MessageBox)component;
			
			if( box.getShowMessageBox() ) {
			
				ExtConfig 	mbConfig = new ExtConfig();
				
				mbConfig.addJSString( "title" , box.getTitle() );
				mbConfig.addJSString( "msg" , box.getMessage() );
				mbConfig.addJSString( "id" , box.getClientId() );
				
				ExtConfig btnConfig = new ExtConfig();
				ExtConfig btnTextConfig = new ExtConfig();
				ScriptBuilder sb = new ScriptBuilder();
				sb.l( "function(o) { " );
				
				String cancelBtn = box.getDefaultCancelButton();
				if( cancelBtn != null ) {
					Menu c = (Menu)component.findComponent( cancelBtn );
					if( c != null ) {
						sb.l( "if (o == 'cancel') {" );
						sb.statement( 
								XVWScripts.getAjaxCommandScript( 
										c, 
										(String)c.getValue(), 
										XVWScripts.WAIT_STATUS_MESSAGE  ) 
								);
						sb.endBlock();
					}
				}
				
				for( UIComponent comp : component.getChildren() ) {
					if( comp instanceof Menu ) {
						Menu m = (Menu)comp;
						btnConfig.add( m.getId(), true );
						btnTextConfig.addJSString( m.getId(), m.getText() );
						sb.l( "if (o == '"+m.getId()+"') {" );
						sb.statement( 
								XVWScripts.getCommandScript( 
										m.getTarget(), 
										m, 
										(String)m.getValue(), 
										XVWScripts.WAIT_STATUS_MESSAGE  ) 
								);
						sb.endBlock();
					}
				}
				sb.endBlock();
				
				mbConfig.add( "buttonText" , btnTextConfig.renderExtConfig() );
				mbConfig.add( "buttons" , btnConfig.renderExtConfig() );
				mbConfig.add( "fn" , sb.toString() );
				
				if( box.getTop() > 0 )
					mbConfig.add( "top" , box.getTop() );
				if( box.getLeft() > 0 )
					mbConfig.add( "left" , box.getLeft() );
				
				mbConfig.addJSString( "defaultButton" , box.getDefaultConfirmButton() );
				
				MessageBoxType type = box.getMessageBoxType();
				if( type != null ) {
					mbConfig.add( "icon","Ext.MessageBox."+type.toString() );  
				}
				getResponseWriter().getScriptContext().add(
						XUIScriptContext.POSITION_FOOTER,
						box.getClientId(),
						"ExtXeo.MessageBox.show(" +
							mbConfig.renderExtConfig() +
						");"
				);
				
			}
			
		}
	}
	
}
