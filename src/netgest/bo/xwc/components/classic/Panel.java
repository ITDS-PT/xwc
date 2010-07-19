package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class Panel extends ViewerSecurityBase {
    
    private XUIViewStateProperty<String> height = new XUIViewStateProperty<String>( "height", this );
    private XUIViewStateProperty<String> width = new XUIViewStateProperty<String>( "width", this );
	private XUIViewStateBindProperty<String> title = new XUIViewStateBindProperty<String>( "title", this, String.class );
	private XUIViewProperty<String> layout = new XUIViewProperty<String>( "layout", this, "" );
	private XUIViewStateBindProperty<Boolean> visible = new XUIViewStateBindProperty<Boolean>( "visible", this, "true", Boolean.class );
	
	/**
	 * Returns the title of the panel 
	 * @return Title
	 */
    public String getTitle() {
		return title.getEvaluatedValue();
	}
    
    /**
     * Set the title of the panel
     * @param title String with the title
     */
	public void setTitle(String title) {
		this.title.setExpressionText( title );
	}
	
	public void setHeight(String height) {
        this.height.setValue( height );
    }
	
    public String getHeight() {
        return height.getValue();
    }
    
    public String getWidth() {
		return width.getValue();
	}

	public void setWidth(String width) {
		this.width.setValue( width );
	}
	
	public void setVisible( String visibleExpr ) {
		this.visible.setExpressionText( visibleExpr );
	}
	
	public boolean getVisible() {
		return this.visible.getEvaluatedValue();
	}
	
	
	/**
	 * Set the layout type to this panel
	 * @return the current layout model
	 */
	public String getLayout() {
		return layout.getValue();
	}
	
	/**
	 * return the current layout type of this Panel
	 * @param layoutType String with one of the next layouts ( fit-parent, fit-window, form )
	 */
	public void setLayout(String layoutType) {
		this.layout.setValue( layoutType );
	}
	
	@Override
	public boolean isRendered() {
		if ( !getEffectivePermission(SecurityPermissions.READ) ) {
			return false;
		}
		return super.isRendered();
	}
	
	@Override
	public boolean wasStateChanged() {
		// TODO Auto-generated method stub
		return super.wasStateChanged();
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            XUIResponseWriter w;
            Panel oPanel;
            String sHeight;
            String sWidth;
            
            w = getResponseWriter();
            
            oPanel = (Panel)component;
            
        	sHeight = oPanel.getHeight();
        	sWidth = oPanel.getWidth();
        	
        	w.startElement( DIV , component);
        	w.writeAttribute( ID, component.getClientId(), null );
        	
        	if( !oPanel.getVisible() )
        		w.writeAttribute( HTMLAttr.CLASS, "x-hidden", null );
        	
        	/*            
        <DIV class="x-panel-tl">
        	<DIV class="x-panel-tr">
        		<DIV class="x-panel-tc">
        			<DIV class="x-panel-header x-unselectable" id="ext-gen18" style="MozUserSelect: none; KhtmlUserSelect: none" unselectable="on">
        				<SPAN class="x-panel-header-text" id="ext-gen40">Simple Form</SPAN>
        			</DIV>
        		</DIV>
        	</DIV>
        </DIV>
        	 */
        	if( oPanel.getTitle() != null ) {
        		w.startElement(DIV, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel x-panel-tl", null);
        		w.startElement(DIV, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel-tr", null);
        		w.startElement(DIV, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel-tc", null);
        		w.startElement(DIV, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel-header", null);
        		
        		w.startElement( HTMLTag.SPAN, component);
        		w.writeAttribute( HTMLAttr.CLASS , "x-panel-header-text", null);
        		w.writeText( oPanel.getTitle(), null );
        		w.endElement( HTMLTag.SPAN );
        		
        		w.endElement(DIV);
        		w.endElement(DIV);
        		w.endElement(DIV);
        		w.endElement(DIV);
        	}
        	
        	
        	w.startElement( DIV , component);
//            w.writeAttribute( CLASS, "x-panel-ml", null );
        	
        	w.startElement( DIV, component);
//            w.writeAttribute( CLASS, "x-panel-mr", null );
        	if( sHeight != null || sWidth != null ) {
        		w.writeAttribute(HTMLAttr.STYLE, "height:100%", null );
        	}
        	
        	w.startElement(DIV, component);
//            w.writeAttribute( CLASS, "x-panel-mc", null );
        	
        	w.startElement(DIV, component);
//            w.writeAttribute(CLASS, "x-panel-body", null );

        }

        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            XUIResponseWriter w;
            w = getResponseWriter();
            
//            w.endElement(DIV);
//            
//            ExtConfig oPanelConfig = new ExtConfig("Ext.Panel");
//            oPanelConfig.add("frame", true );
//            
//            oPanelConfig.addJSString("baseCls", "x-form");
//            oPanelConfig.addJSString("layout","border");            
//            oPanelConfig.addJSString("contentEL", component.getClientId() );
//            
//            
//            w.getScriptContext().add( XUIScriptContext.POSITION_FOOTER , component.getClientId(), oPanelConfig.renderExtConfig() );
            
        	w.endElement( DIV );
        	w.endElement( DIV );
        	w.endElement( DIV );
        	w.endElement( DIV );
        	w.endElement( DIV );
            
        }


        @Override
        public boolean getRendersChildren() {
            return true;
        }
        
    }

    //
    // Methods from SecurableComponent
    //

	public COMPONENT_TYPE getViewerSecurityComponentType() {
		return SecurableComponent.COMPONENT_TYPE.AREA;
	}

	public String getViewerSecurityId() {
		return getTitle();
	}

	public String getViewerSecurityLabel() {
		String label = getViewerSecurityComponentType().toString();
		if ( getViewerSecurityId()!=null ) {
			label += " "+ getViewerSecurityId();
		}
		return label; 
	}

	public boolean isContainer() {
		return true;
	}

}
