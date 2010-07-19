package netgest.bo.xwc.components.classic;

import java.io.IOException;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * Cell of a row
 * 
 * He can have children of any type
 * 
 * @author jcarreira
 *
 */
public class Cell extends XUIComponentBase
{
	
	private XUIBaseProperty<Integer> colSpan = new XUIBaseProperty<Integer>("colSpan", this, 0 );
	
	protected void setColSpan( int colSpan ) {
		this.colSpan.setValue( colSpan );
	}
	
	protected int getColSpan() {
		return this.colSpan.getValue();
	}
	
	@Override
	public void restoreState(Object oState) {
		// TODO Auto-generated method stub
		super.restoreState(oState);
	}
	
	@Override
	public boolean wasStateChanged() {
		// TODO Auto-generated method stub
		return super.wasStateChanged();
	}

    public static final class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            super.encodeBegin(component);
            Cell cell = ((Cell)component);
            XUIResponseWriter w = getResponseWriter();
            w.startElement("td",component);
            w.writeAttribute("id", component.getClientId(), null );
            w.writeAttribute("colspan", cell.getColSpan(), null );
        }
        
        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            XUIResponseWriter w = getResponseWriter();
            w.endElement("td");
            super.encodeEnd(component);
        }

    }

}
