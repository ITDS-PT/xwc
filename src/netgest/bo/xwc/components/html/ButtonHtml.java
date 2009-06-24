package netgest.bo.xwc.components.html;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.ActionButton;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class ButtonHtml extends XUICommand {
	
	private XUIBaseProperty<String>  style 		= new XUIBaseProperty<String>("style",this);
	private XUIBaseProperty<String>  className 	= new XUIBaseProperty<String>("className",this);
    private XUIStateProperty<String> action 	= new XUIStateProperty<String>( "action", this );
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
			// TODO Auto-generated method stub
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
