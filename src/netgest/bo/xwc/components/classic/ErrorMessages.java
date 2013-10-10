package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.components.FormEdit;

/**
 * This component render's alert boxes and messages in client browser, 
 * and controls the save state of of viewer.~
 * 
 * Also invokes some special bean properties to handle the alert box, data was not saved!
 * 
 * Declaration in the Viewer as Follows (Note, the {@link FormEdit} component already includes the
 * {@link ErrorMessages} component by default:
 * 
 * <code>
 * 	<xvw:errorMessages/>
 * </code>
 * 
 * In a Bean Method:
 * <code>XUIRequestContext.getCurrentContext().addMessage(STRING_CLIENT_ID,
 *
 *               new XUIMessage(INT_MESSAGE_TYPE, INT_MESSAGE_SEVERITY, 
 *
 *     STRING_MESSAGE_TITLE, STRING_MESSAGE_BODY));
 *     </code>
 *
 *  INT_MESSAGE_TYPE = 
 *  	{@link XUIMessage#TYPE_ALERT} or
 *   	{@link XUIMessage#TYPE_MESSAGE} or 
 *   	{@link XUIMessage#TYPE_POPUP_MESSAGE}
 *   
 *  INT_MESSAGE_SEVERITY = 
 *  	{@link XUIMessage#SEVERITY_CRITICAL} or  
 * 		{@link XUIMessage#SEVERITY_ERROR} 
 * 		{@link XUIMessage#SEVERITY_INFO} 
 * 		{@link XUIMessage#SEVERITY_WARNING} 
 * 
 * @author jcarreira
 *
 */
public class ErrorMessages extends XUIComponentBase {
    
    /**
     * Property to save if last request where messages writen in the request before this.
     * This is to trigger a render of the component to clear messages. Needed for Ajax.
     */
    private XUIStateProperty<Boolean> lastRequestWasChanged = 
    	new XUIStateProperty<Boolean>( "lastRequestWasChanged", this, false );
    
    public ErrorMessages() {
    }

    @Override
	public void preRender() {
    	if( getChildCount() == 0 ) {
    		XUICommand cmd = new XUICommand();
    		cmd.setActionExpression( createMethodBinding( "#{" + getBeanId() + ".canCloseTab}" ) );
    		getChildren().add( cmd );
    	}
	}

    @Override
    public StateChanged wasStateChanged2() {
        boolean bRet;
        bRet = getRequestContext().getMessages().hasNext();
        bRet = bRet || getFacesContext().getMessages().hasNext(); 
        bRet = bRet || lastRequestWasChanged.getValue();
        if (bRet)
        	return StateChanged.FOR_RENDER;
        return StateChanged.NONE;
    }
    
    public boolean haveMessagesToRender()
    {
        Iterator<XUIMessage>  oItXuiMessages;
        XUIMessage            oXuiMessage;

        oItXuiMessages = getRequestContext().getMessages();
        
        while( oItXuiMessages.hasNext() ) {
            oXuiMessage = oItXuiMessages.next();
            if( oXuiMessage.getType() == XUIMessage.TYPE_MESSAGE ) {
            	return true;
            }
        }
        
        for ( Iterator<FacesMessage> it = getRequestContext().getFacesContext().getMessages(); it.hasNext() ; ){
        	return true;
        }
        
        
        return false;
        
    }
    

    @Override
    public Object saveState() {

        if( getRequestContext().getMessages().hasNext() || getRequestContext().getMessages().hasNext() ) {
            lastRequestWasChanged.setValue( true );
        }
        else {
            lastRequestWasChanged.setValue( false );
        }
        return super.saveState();

    }


    public static final class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            XUIResponseWriter       w;
            Iterator<FacesMessage>  oItMessages;
            FacesMessage            oFacesMessage;
            ErrorMessages			oErrorMessagesComp;
            
            oErrorMessagesComp = (ErrorMessages)component;
            
            w = getResponseWriter();
            w.startElement( DIV , component );
            w.writeAttribute( ID, component.getClientId() );
            if( !oErrorMessagesComp.haveMessagesToRender() )
            {
                w.writeAttribute( HTMLAttr.STYLE, "display:none" );
            }
            w.startElement( HTMLTag.INPUT, component );
            w.writeAttribute( HTMLAttr.TYPE, "hidden");
            w.writeAttribute( HTMLAttr.NAME, "__isChanged");
            w.writeAttribute( HTMLAttr.VALUE,
            		component.getChild(0).getClientId( getFacesContext() ));
            w.endElement( HTMLTag.INPUT );
            w = getResponseWriter();
            oItMessages = getFacesContext().getMessages();
            
            // Render Faces Messages for backword compatibility
            while( oItMessages.hasNext() ) {
                oFacesMessage = oItMessages.next();
                w.startElement( DIV, component );
                w.writeAttribute( HTMLAttr.STYLE, "color:red" );

                if( oFacesMessage.getSummary() != null || oFacesMessage.getDetail() != null )
                {
                    if( oFacesMessage.getSummary() != null )
                    {
                        w.write( oFacesMessage.getSummary() );
                        w.write( "-" );
                    }
                    if( oFacesMessage.getDetail() != null )
                        w.write( oFacesMessage.getDetail() );
                }
                else {
                    w.write( oFacesMessage.getSeverity().toString()  );
                }
                
                w.endElement( DIV );
                
            }
            

            Iterator<XUIMessage>  oItXuiMessages;
            XUIMessage            oXuiMessage;
            
            w = getResponseWriter();
            oItXuiMessages = getContext().getMessages();
            
            while( oItXuiMessages.hasNext() ) {
                oXuiMessage = oItXuiMessages.next();
                
        		int iconType = 0;
        		switch( oXuiMessage.getSeverity() ) {
        			case XUIMessage.SEVERITY_CRITICAL:
        				iconType = XVWScripts.ALERT_ICON_ERROR;
        				break;
        			case XUIMessage.SEVERITY_ERROR:
        				iconType = XVWScripts.ALERT_ICON_ERROR;
        				break;
        			case XUIMessage.SEVERITY_INFO:
        				iconType = XVWScripts.ALERT_ICON_INFO;
        				break;
        			case XUIMessage.SEVERITY_WARNING:
        				iconType = XVWScripts.ALERT_ICON_WARNING;
        				break;
        		}
                
                switch( oXuiMessage.getType() ) {
                	case XUIMessage.TYPE_MESSAGE: 
	                    w.startElement( DIV , component );
	                    w.writeAttribute( HTMLAttr.STYLE, "color:red" );
	                    if( oXuiMessage.getMessage() != null || oXuiMessage.getDetail() != null )
	                    {
	                        if( oXuiMessage.getMessage() != null )
	                        {
	                            w.write( oXuiMessage.getMessage() );
	                            w.write( "-" );
	                        }
	                        if( oXuiMessage.getDetail() != null )
	                            w.write( oXuiMessage.getDetail() );
	                    }
	                    else {
	                        w.write( oXuiMessage.getMessage().toString() );
	                    }
	                    w.endElement( DIV );
	                    break;
                	case XUIMessage.TYPE_POPUP_MESSAGE:
                		w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, 
                				"message_success", 
                				"var xapp = window.App;\n" +
                				"if(window.parent.App) xapp=window.parent.App;\n" +
                				"xapp.setAlert('" + 
                					JavaScriptUtils.safeJavaScriptWrite( oXuiMessage.getTitle(), '\'') + 
                					"','" + 
                					JavaScriptUtils.safeJavaScriptWrite( oXuiMessage.getMessage(), '\'') + 
                					"')"
                		);
                		break;
                	default:
	                    w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER,
	                            component.getClientId(),
	                            XVWScripts.getAlertDialog( oXuiMessage.getTitle(), oXuiMessage.getMessage() , iconType )
	                        );
                }
            }
            w.endElement( DIV );
            
            // Render popup Messages
            

        }

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            super.encodeBegin(component);
        }
    }


}
