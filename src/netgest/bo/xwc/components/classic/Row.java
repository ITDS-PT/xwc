package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.VALIGN;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * The xvw:row element creates a row (hence the name) inside a table (xvw:rows), 
 * this component does not have any properties and 
 * serves only to create a row that can be filled using xvw:cell components.
 * 
 * @author Jo�o Carreira
 *
 */
public class Row extends XUIComponentBase
{
	
	private XUIStateBindProperty<Boolean> visible = 
			new XUIStateBindProperty<Boolean>( "visible", this, "true", Boolean.class );
	
	public void setVisible( String visibleExpr ) {
		this.visible.setExpressionText( visibleExpr );
	}
	
	public Boolean isVisible() {
		return visible.getEvaluatedValue();
	}
	
	@Override
	public boolean isRenderForUpdate() {
		if (wasStateChanged() == StateChanged.FOR_UPDATE)
			return true;
		return super.isRenderForUpdate();
	}
	
	@Override
	public StateChanged wasStateChanged() {
		if (visible.wasChanged())
			return StateChanged.FOR_UPDATE;
		return super.wasStateChanged();
	}
	
	@Override
	public void resetRenderedOnClient() {
		super.resetRenderedOnClient();
	}
	
    public static final class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
        	
        	Row row = (Row) component;
        	String rowId = component.getClientId();
        	XUIResponseWriter w = getResponseWriter();
        	if (!component.isRenderedOnClient()){
	            w.startElement(TR );
		            w.writeAttribute(ID, rowId );
		            w.writeAttribute(VALIGN,"top");
		            w.writeAttribute( CLASS, "xwc-rows-row");
        	} else {
        		component.setDestroyOnClient( false );
        		if (row.isVisible()){
        			showRow( rowId );
        		}
        		else
        			hideRow( rowId );
        	}
	            
        	
            
        }
        
        protected void showRow(String id){
        	getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_HEADER,
        			"rowVisible" + id, "Ext.get('"+id+"').show();" );
        }
        
        protected void hideRow(String id){
        	getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_FOOTER,
        			"rowHide" + id, "Ext.get('"+id+"').dom.style.display = 'none';" );
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
        		XUIResponseWriter w = getResponseWriter();
        		if (!component.isRenderedOnClient())
        			w.endElement( TR );
        }

        @Override
        public boolean getRendersChildren() {
            return true;
        }

        @Override
        public void encodeChildren(XUIComponentBase component) throws IOException {
        	if (!component.isRenderedOnClient() || !component.isRenderForUpdate()){
	            if (component.getChildCount() > 0) {
	                Iterator<UIComponent> kids = component.getChildren().iterator();
	                while (kids.hasNext()) {
	                    UIComponent kid = kids.next();
	                    if( component.getChildCount() == 1 && kid instanceof Cell ) {
	                    	((Cell)kid).setColSpan( 2 );
	                    }
	                    kid.encodeAll(getFacesContext());
	                }
	            }
        	}
        }
    }


}
