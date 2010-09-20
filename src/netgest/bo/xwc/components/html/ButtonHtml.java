package netgest.bo.xwc.components.html;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * This components represents a XUICommand in the form of an HTML button
 * 
 * @author João Carreira
 *
 */
public class ButtonHtml extends XUICommand {
	
	/**
	 * HTML "style" property, to have CSS properties declared directly in the element
	 */
	private XUIBaseProperty<String>  style 		= new XUIBaseProperty<String>("style",this);
	/**
	 * The name of a CSS class to apply to the button
	 */
	private XUIBaseProperty<String>  className 	= new XUIBaseProperty<String>("className",this);
    /**
     * The action to execute server-side
     */
    private XUIStateProperty<String> action 	= new XUIStateProperty<String>( "action", this );
    /**
     * The label of the button
     */
    private XUIStateProperty<String> label 		= new XUIStateProperty<String>( "label", this, "#Button#" );

    public void setAction( String sExpr ) {
        this.action.setValue( sExpr );
        setActionExpression( createMethodBinding( sExpr ) );
    }
    
    public String getActionString() {
        return action.getValue();
    }
    
    public String getStyle() {
		return style.getValue();
	}

	public void setStyle(String style) {
		this.style.setValue( style );
	}

	public String getClassName() {
		return className.getValue();
	}

	public void setClassName(String className) {
		this.className.setValue( className );
	}

    public void setLabel(String label) {
        this.label.setValue( label );
    }

    public String getLabel() {
        return label.getValue();
    }

	public static class XEOHTMLRenderer extends XUIRenderer {

		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
			ButtonHtml inputComp = (ButtonHtml)component;
			
			XUIResponseWriter w = getResponseWriter();
			
			w.startElement( HTMLTag.INPUT , inputComp);
			w.writeAttribute( HTMLAttr.NAME , inputComp.getClientId(), null);
			w.writeAttribute( HTMLAttr.STYLE , inputComp.getStyle(), null);
			w.writeAttribute( HTMLAttr.CLASS , inputComp.getClassName(), null);
			w.writeAttribute( HTMLAttr.VALUE , inputComp.getLabel(), null);
			w.writeAttribute( HTMLAttr.TYPE , "submit", null);;
			w.endElement(HTMLTag.INPUT);
			
		}
	}
	
}
