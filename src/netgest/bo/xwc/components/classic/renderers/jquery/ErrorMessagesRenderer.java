package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.P;
import static netgest.bo.xwc.components.HTMLTag.SPAN;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.application.FacesMessage;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.ErrorMessages;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * Renderer for the Error Messages using Jquery UI
 * 
 * @author PedroRio
 *
 */
public class ErrorMessagesRenderer extends JQueryBaseRenderer {

	@Override
    public void encodeEnd(XUIComponentBase component) throws IOException {
        XUIResponseWriter       w;
        ErrorMessages			oErrorMessagesComp;
        
        oErrorMessagesComp = (ErrorMessages) component;
        
        w = getResponseWriter();
        
        w.startElement( DIV , component );
        	w.writeAttribute( ID, component.getClientId(), null );
        
        if( !oErrorMessagesComp.haveMessagesToRender() ){
            w.writeAttribute( HTMLAttr.STYLE, "display:none", null );
        }
        
        renderFacesMessages( component, w );
        

        Iterator<XUIMessage>  oItXuiMessages;
        XUIMessage            oXuiMessage;
        
        w = getResponseWriter();
        oItXuiMessages = getContext().getMessages();
        
        while( oItXuiMessages.hasNext() ) {
            oXuiMessage = oItXuiMessages.next();
            
    		int iconType = 0;
    		iconType = selectIcon( oXuiMessage );
            
            switch( oXuiMessage.getType() ) {
            	case XUIMessage.TYPE_MESSAGE: 
            		renderMessage( w, oXuiMessage );
                    break;
                case XUIMessage.TYPE_POPUP_MESSAGE:
                	renderPopUpMessage( component, w, oErrorMessagesComp, oXuiMessage );
            		break;
            	default:
            		renderFallback( component, w, oXuiMessage, iconType );
            }
        }
        w.endElement( DIV );
        

    }

	private int selectIcon( XUIMessage oXuiMessage ) {
		int iconType;
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
			default : 
				iconType = XVWScripts.ALERT_ICON_WARNING;
				break;
		}
		return iconType;
	}

	/**
	 * 
	 * When everything else fails, try the old method for showing messages
	 * 
	 * @param component 
	 * @param w
	 * @param oXuiMessage
	 * @param iconType
	 */
	private void renderFallback( XUIComponentBase component, XUIResponseWriter w, XUIMessage oXuiMessage, int iconType ) {
		w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER,
		        component.getClientId(),
		        XVWScripts.getAlertDialog( oXuiMessage.getTitle(), oXuiMessage.getMessage() , iconType )
		    );
	}

	private void renderPopUpMessage( XUIComponentBase component, XUIResponseWriter w, ErrorMessages oErrorMessagesComp,
			XUIMessage oXuiMessage ) throws IOException {
		w.startElement( DIV );
			w.writeAttribute( CLASS, "xwc-error-message-text" );
			w.writeAttribute( ID, oErrorMessagesComp.getClientId() + "_a" );
			
			w.startElement( P );
				w.writeAttribute( CLASS, "xwc-error-message-content" );
				w.write( oXuiMessage.getMessage() );
			w.endElement( P );	
			
			w.startElement( P );
				w.writeAttribute( CLASS, "xwc-error-message-detail" );
				w.write( oXuiMessage.getDetail() );
			w.endElement( P );	
		w.endElement( DIV );
		
		String messageId = oErrorMessagesComp.getClientId() + "_a";
		
		JQueryWidget dialog = WidgetFactory.createWidget( JQuery.DIALOG );
		dialog.componentSelectorById( messageId )
			.createAndStartOptions()
			.addOption( "title", oXuiMessage.getTitle() )
			.addOption( "modal", true )
			.addNonLiteral( "buttons", getOkButtonScript() )
			.addNonLiteral( "beforeClose", getCloseScript( getIdForJquerySelector( messageId ) ) )
			.endOptions();
		dialog.command( "parent( )").addClass( "xwc-error-message" );
		
		
		addScriptFooter( component.getClientId(), dialog.build() );
	}

	private String getOkButtonScript() {
		return 
		"{ "+ ComponentMessages.DIALOG_OK_BTN.toString() + ": function() { " + 
		"	$( this ).dialog( \"close\" ) " +
		"} }";
	
	}

	private void renderMessage( XUIResponseWriter w, XUIMessage oXuiMessage ) throws IOException {
		w.startElement( DIV );
		w.writeAttribute( CLASS, "ui-widget" );
		
			w.startElement( DIV );
			if (XUIMessage.SEVERITY_ERROR == oXuiMessage.getSeverity() )
				w.writeAttribute( CLASS, "ui-state-error ui-corner-all" );
			else
				w.writeAttribute( CLASS, "ui-state-highlight ui-corner-all" );
			
			renderMessageContent( w, oXuiMessage );
			w.endElement( DIV );
		w.endElement( DIV );
	}

	private void renderMessageContent( XUIResponseWriter w, XUIMessage oXuiMessage ) throws IOException {
		w.startElement( P );
			w.writeAttribute( STYLE, "display:inline" );
		
			renderIcon( w, oXuiMessage );
			
		if( oXuiMessage.getMessage() != null || oXuiMessage.getDetail() != null ){
		    if( oXuiMessage.getMessage() != null ){
		        w.writeText( oXuiMessage.getMessage(), null );
		        w.writeText( "-", null );
		    }
		    if( oXuiMessage.getDetail() != null )
		        w.writeText( oXuiMessage.getDetail(), null );
		}
		else {
		    w.writeText( oXuiMessage.getMessage().toString(), null  );
		}
		w.endElement( P );
	}

	private void renderIcon( XUIResponseWriter w, XUIMessage oXuiMessage ) throws IOException {
		w.startElement( SPAN );
		String iconCls = "";
		switch (oXuiMessage.getSeverity()){
		case XUIMessage.SEVERITY_ERROR :
			iconCls = "ui-icon-alert";
			break;
		case XUIMessage.SEVERITY_INFO :
			iconCls = "ui-icon-info";
			break;
		case XUIMessage.SEVERITY_CRITICAL :
			iconCls = "ui-icon-alert";
			break;
		case XUIMessage.SEVERITY_WARNING :
			iconCls = "ui-icon-notice";
			break;
		default : 
			iconCls = "ui-icon-alert";
			break;
		}
		
		w.writeAttribute( CLASS, "ui-icon " + iconCls );
		
		w.endElement( SPAN );
	}

	private String getCloseScript( String messageId ) {
			StringBuilder b = new StringBuilder();
			b.append("function(event, ui) {");
			b.append(" $(this).dialog('destroy').remove(); $('#"+messageId+"').remove()");
			b.append("}");
			return b.toString();
	}

	private void renderFacesMessages( XUIComponentBase component, XUIResponseWriter w ) throws IOException {
		Iterator<FacesMessage> oItMessages;
		FacesMessage oFacesMessage;
		w.startElement( HTMLTag.INPUT, component );
        w.writeAttribute( HTMLAttr.TYPE, "hidden", null);
        w.writeAttribute( HTMLAttr.NAME, "__isChanged", null);
        w.writeAttribute( HTMLAttr.VALUE,
        		component.getChild(0).getClientId( getFacesContext() )
        		, null);
        w.endElement( HTMLTag.INPUT );
        w = getResponseWriter();
        oItMessages = getFacesContext().getMessages();
        
        // Render Faces Messages for backward compatibility
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
	}

    @Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
        super.encodeBegin(component);
    }
	
}
