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
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class Panel extends ViewerSecurityBase {
    
    private XUIStateProperty<String> height = new XUIStateProperty<String>( "height", this );
    private XUIStateProperty<String> width = new XUIStateProperty<String>( "height", this );
	private XUIStateBindProperty<String> title = new XUIStateBindProperty<String>( "title", this, String.class );
    
	private XUIStateProperty<String> layout = new XUIStateProperty<String>( "layout", this, "" );

    public String getTitle() {
		return title.getEvaluatedValue();
	}

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

	public String getLayout() {
		return layout.getValue();
	}

	public void setLayout(String width) {
		this.layout.setValue( width );
	}
	
	@Override
	public boolean isRendered() {
		if ( !getEffectivePermission(SecurityPermissions.READ) ) {
			return false;
		}
		return super.isRendered();
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
        	w.writeAttribute( ID, component.getId(), null );
        	//w.writeAttribute( CLASS, "x-panel-bwrap", null );
        	
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
