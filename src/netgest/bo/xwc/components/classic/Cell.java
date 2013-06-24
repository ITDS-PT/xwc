package netgest.bo.xwc.components.classic;

import java.io.IOException;

import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * The Cell component creates a column within a row of a table. 
 * The component does not have properties by itself 
 * but allows to define a structure for a table.
 * 
 * Can have children of any type
 * 
 * @author Jo�o Carreira
 *
 */
public class Cell extends XUIComponentBase
{
	
	private XUIViewProperty<Integer> colSpan = new XUIViewProperty<Integer>("colSpan", this, 0 );
	
	
	public void setColSpan( int colSpan ) {
		this.colSpan.setValue( colSpan );
	}
	
	protected int getColSpan() {
		return this.colSpan.getValue();
	}
	
	@Override
	public void restoreState(Object oState) {
		super.restoreState(oState);
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
