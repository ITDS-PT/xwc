package netgest.bo.xwc.components.classic.renderers.jquery;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.STYLE;
import static netgest.bo.xwc.components.HTMLAttr.TITLE;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.P;
import static netgest.bo.xwc.components.HTMLTag.SPAN;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.MessageBox;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.JQueryWidget;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory;
import netgest.bo.xwc.components.classic.renderers.jquery.generators.WidgetFactory.JQuery;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class MessageBoxRenderer extends JQueryBaseRenderer {
	
	@Override
    public void encodeBegin(XUIComponentBase component) throws IOException {
		XUIResponseWriter w = getResponseWriter();
		MessageBox box = (MessageBox) component;
		
		
			if (!component.isRenderedOnClient()){
				w.startElement( DIV );
					w.writeAttribute( ID, box.getClientId() );
					w.writeAttribute( TITLE, box.getTitle() );
					w.writeAttribute( CLASS, "xwc-messagebox" );
					w.writeAttribute( STYLE, "display:none" );
					w.startElement( P );
					w.writeAttribute( CLASS, "xwc-messagebox-text" );
						w.startElement( SPAN );
							setIcon( box, w );
							w.writeAttribute( STYLE, "float:left; margin:0 7px 20px 0" ); 
						w.endElement( SPAN );
						w.write( box.getMessage() );
					w.endElement( P );
				w.endElement( DIV );	
			}
			
			if ( box.getShowMessageBox() ){
				if ( shouldUpdate( component ) ) 
					updateComponent( box );
			}
	}

	private void setIcon( MessageBox box, XUIResponseWriter w ) throws IOException {
		String iconCls = "ui-icon-alert";
		switch (box.getMessageBoxType() ){
			case ERROR :
				iconCls = "ui-icon-alert";
				break;
			case INFO :
				iconCls = "ui-icon-info";
				break;
			case QUESTION :
				iconCls = "ui-icon-help";
				break;
			case WARNING :
				iconCls = "ui-icon-notice";
				break;
			default :
				break;
			
		}
		w.writeAttribute( CLASS, "xwc-messagebox-icon ui-icon " + iconCls ); 
	}
	
	private void updateComponent( MessageBox component ) {
		JQueryWidget widget = WidgetFactory.createWidget( JQuery.DIALOG );
		widget.componentSelectorById( component.getClientId() );
		widget.createAndStartOptions();
		widget.addOption( "height", 140 );
		widget.addOption( "modal", true );
		widget.addOption( "resizable", true );
		
		String buttons = renderButtons( component );
		
		widget.addNonLiteral( "buttons", buttons );
		widget.endOptions();
		widget.command( "parent( )").addClass( "xwc-messagebox-window" );
		addScriptFooter( component.getClientId(), widget.build() );
	}

	private String renderButtons( MessageBox component ) {
		StringBuilder buttonOptions = new StringBuilder(100);
		buttonOptions.append("{");
		Iterator<UIComponent> it = component.getChildren().iterator();
		while (it.hasNext()){
			UIComponent comp = it.next();
			if( comp instanceof Menu ) {
				Menu m = (Menu)comp;
				buttonOptions.append( "'" );
				buttonOptions.append( m.getText() );
				buttonOptions.append("':");
				buttonOptions.append(" function() { ");
				
				buttonOptions.append( 
						XVWScripts.getCommandScript( 
								m.getTarget(), 
								m, 
								(String)m.getValue(), 
								XVWScripts.WAIT_DIALOG  ) 
						);
				
				buttonOptions.append(";");
				buttonOptions.append("$(this).dialog('destroy');");
				buttonOptions.append(" } ");
				if (it.hasNext()){
					buttonOptions.append(" , ");
				}
			}
		}
		buttonOptions.append("}");
		
		String buttons = buttonOptions.toString();
		return buttons;
	}

	@Override
    public void encodeEnd(XUIComponentBase component ) throws IOException {
    	
	}

}
