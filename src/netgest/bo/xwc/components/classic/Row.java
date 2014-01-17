package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CLASS;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.VALIGN;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.annotations.Visible;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * The xvw:row element creates a row (hence the name) inside a table (xvw:rows), 
 * this component does not have any properties and 
 * serves only to create a row that can be filled using xvw:cell components.
 * 
 * @author Joao Carreira
 *
 */
public class Row extends XUIComponentBase
{
	
	/**
	 * Whether or not the row should be visible.
	 * Defaults to true
	 */
	@Visible
	private XUIStateBindProperty<Boolean> visible = 
			new XUIStateBindProperty<Boolean>( "visible", this, "true", Boolean.class );
	
	private XUIViewProperty<String> css = new XUIViewProperty<String>("css", this);
	
	public void setCss(String css){
		this.css.setValue(css);
	}
	
	public String getCss(){
		return css.getValue();
	}
	
	/**
	 * Set the row visibility
	 * 
	 * @param visibleExpr
	 */
	public void setVisible( String visibleExpr ) {
		this.visible.setExpressionText( visibleExpr );
	}
	
	/**
	 * Whether or not the row is visible
	 * 
	 * @return True if the row is visible and false otherwise
	 */
	public Boolean isVisible() {
		return visible.getEvaluatedValue();
	}
	
    public static final class XEOHTMLRenderer extends XUIRenderer {
    	
    	@Override
    	public void encodeComponentChanges( XUIComponentBase component, List<XUIBaseProperty<?>> propertiesWithChangedState ) throws IOException {
    		Row row = (Row) component;
    		String rowId = row.getClientId();
    		component.setDestroyOnClient( false );
    		if (row.isVisible()){
    			showRow( rowId );
    		}
    		else
    			hideRow( rowId );
    	}
    	
    	
    	@Override
    	public StateChanged wasStateChanged(XUIComponentBase component, List<XUIBaseProperty<?>> updateProperties) {
    		updateProperties.add( component.getStateProperty( "visible" ) );
    		return super.wasStateChanged( component, updateProperties );
    	}
    	
    	@Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
        	
        	String rowId = component.getClientId();
        	XUIResponseWriter w = getResponseWriter();
        	
            w.startElement(TR );
	            w.writeAttribute(ID, rowId );
	            w.writeAttribute(VALIGN,"top");
	            w.writeAttribute( CLASS, "xwc-rows-row");
        	
	        Row row = (Row) component;
	        if (!row.isVisible())
	        	hideRow( row.getClientId() );
            
        }
        
        protected void showRow(String id){
        	getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_HEADER,
        			"rowVisible" + id, "Ext.fly('"+id+"').show();" );
        }
        
        protected void hideRow(String id){
        	getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_FOOTER,
        			"rowHide" + id, "Ext.fly('"+id+"').dom.style.display = 'none';" );
        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
        		XUIResponseWriter w = getResponseWriter();
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
