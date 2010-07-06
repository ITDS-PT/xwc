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

/**
 * This component render's alert boxes and messages in client brwoser, and controls the save state of of viewer.~
 * 
 * He also invokes some special bean properties to handle the alert box, data was not saved!
 * 
 * @author jcarreira
 *
 */
public class ErrorMessages extends XUIComponentBase {
    
    // Property to save if last request where messages writen in the request before this.
    // This is to trigger a render of the component to clear messages. Needed for Ajax.
    private XUIStateProperty<Boolean> lastRequestWasChanged = 
    	new XUIStateProperty<Boolean>( "lastRequestWasChanged", this, false );
    
    public ErrorMessages() {
    }

    @Override
	public void preRender() {
    	if( getChildCount() == 0 ) {
    		XUICommand cmd = new XUICommand();
    		cmd.setActionExpression( createMethodBinding( "#{viewBean.canCloseTab}") );
    		getChildren().add( cmd );
    	}
	}

    @Override
    public boolean wasStateChanged() {
        boolean bRet;
        bRet = getRequestContext().getMessages().hasNext();
        bRet = bRet || getFacesContext().getMessages().hasNext(); 
        bRet = bRet || lastRequestWasChanged.getValue();
        return bRet;
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
            w.writeAttribute( ID, component.getClientId(), null );
            if( !oErrorMessagesComp.haveMessagesToRender() )
            {
                w.writeAttribute( HTMLAttr.STYLE, "display:none", null );
            }
            w.startElement( HTMLTag.INPUT, component );
            w.writeAttribute( HTMLAttr.TYPE, "hidden", null);
            w.writeAttribute( HTMLAttr.NAME, "__isChanged", null);
            w.writeAttribute( HTMLAttr.VALUE,
            		component.getChild(0).getClientId( getFacesContext() )
            		, null);
            w.endElement( HTMLTag.INPUT );
            w = getResponseWriter();
            oItMessages = getFacesContext().getMessages();
            
            // Render Faces Messages for backword compatibility
            while( oItMessages.hasNext() ) {
                oFacesMessage = oItMessages.next();
                w.startElement( DIV, component );
                w.writeAttribute( HTMLAttr.STYLE, "color:red", null );

                if( oFacesMessage.getSummary() != null || oFacesMessage.getDetail() != null )
                {
                    if( oFacesMessage.getSummary() != null )
                    {
                        w.writeText( oFacesMessage.getSummary(), null );
                        w.writeText( "-", null );
                    }
                    if( oFacesMessage.getDetail() != null )
                        w.writeText( oFacesMessage.getDetail(), null );
                }
                else {
                    w.writeText( oFacesMessage.getSeverity().toString(), null  );
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
	                    w.writeAttribute( HTMLAttr.STYLE, "color:red", null );
	                    if( oXuiMessage.getMessage() != null || oXuiMessage.getDetail() != null )
	                    {
	                        if( oXuiMessage.getMessage() != null )
	                        {
	                            w.writeText( oXuiMessage.getMessage(), null );
	                            w.writeText( "-", null );
	                        }
	                        if( oXuiMessage.getDetail() != null )
	                            w.writeText( oXuiMessage.getDetail(), null );
	                    }
	                    else {
	                        w.writeText( oXuiMessage.getMessage().toString(), null  );
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
