package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.extjs.ExtJsBaseRenderer;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.properties.XUIComponent;

public class AdvancedPanel extends XUIComponentBase {
	
	XUIViewProperty<String> layout =
		new XUIViewProperty<String>( "layout", this, "" );
	
	XUIViewProperty<String> region =
		new XUIViewProperty<String>( "region", this, "" );
	
	XUIViewProperty<String> margins =
		new XUIViewProperty<String>( "margins", this, "" );

	XUIViewProperty<String> hideMode =
		new XUIViewProperty<String>( "hideMode", this, "" );

	XUIViewProperty<String> height =
		new XUIViewProperty<String>( "height", this, "" );

	XUIViewProperty<String> width =
		new XUIViewProperty<String>( "width", this, "" );
	
	/**
	 * @return the layout
	 */
	public String getLayout() {
		return layout.getValue();
	}

	/**
	 * @param layout the layout to set
	 */
	public void setLayout(String layout) {
		this.layout.setValue( layout );
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region.getValue();
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region.setValue( region );
	}

	/**
	 * @return the margins
	 */
	public String getMargins() {
		return margins.getValue();
	}

	/**
	 * @param margins the margins to set
	 */
	public void setMargins(String margins) {
		this.margins.setValue( margins );
	}

	/**
	 * @return the hideMode
	 */
	public String getHideMode() {
		return hideMode.getValue();
	}

	/**
	 * @param hideMode the hideMode to set
	 */
	public void setHideMode(String hideMode) {
		this.hideMode.setValue( hideMode );
	}
	
	
	
	/**
	 * @return the height
	 */
	public String getHeight() {
		return height.getValue();
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height) {
		this.height.setValue( height );
	}

	/**
	 * @return the width
	 */
	public String getWidth() {
		return width.getValue();
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(String width) {
		this.width.setValue( width );
	}

	public static class XEOHTMLRenderer extends XUIRenderer implements ExtJsRenderer {

		/* (non-Javadoc)
		 * @see netgest.bo.xwc.framework.XUIRenderer#encodeEnd(netgest.bo.xwc.framework.components.XUIComponentBase)
		 */
		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
			AdvancedPanel panel = (AdvancedPanel)component;

			
			ExtConfig cPanel = getExtJsConfig(panel);
			cPanel.addJSString( "renderTo" , panel.getClientId() );

			XUIResponseWriter rw = getResponseWriter();
			rw.startElement("div", component );
			rw.writeAttribute("id", component.getClientId(), "id" );
			rw.endElement("div" );
			
			rw.getScriptContext()
				.add( XUIScriptContext.POSITION_FOOTER, component.getClientId(), cPanel.renderExtConfig() );
			
			
		}

		/* (non-Javadoc)
		 * @see javax.faces.render.Renderer#getRendersChildren()
		 */
		@Override
		public boolean getRendersChildren() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public void encodeChildren(XUIComponentBase component)
				throws IOException {
			// TODO Auto-generated method stub
			return;
		}
		

		@Override
		public ExtConfig getExtJsConfig(XUIComponentBase oComp) {
			AdvancedPanel panel = (AdvancedPanel)oComp;
			ExtConfig cPanel = new ExtConfig( "Ext.Panel" );
			cPanel.addJSString( "id" , "ext-" + oComp.getClientId() );
			cPanel.addJSString( "layout" , panel.getLayout() );
			cPanel.addJSString( "region" , panel.getRegion() );
			cPanel.addJSString( "margins" , panel.getMargins() );
			cPanel.addJSString( "hideMode" , panel.getHideMode() );
			cPanel.addJSString( "title" , "Hello World!" );
			cPanel.addJSString( "html" , "<h1>Hello World!</h1>" );
			
			ExtConfigArray itens = cPanel.addChildArray( "items" );
			
			for( UIComponent child : oComp.getChildren() ) {
                Renderer renderer = getRenderer( child, XUIRequestContext.getCurrentContext().getFacesContext() );
                if( renderer instanceof ExtJsRenderer ) {
                	itens.add( ((ExtJsRenderer)renderer).getExtJsConfig((XUIComponentBase)child ) );
                }
			}
			
			return cPanel;
		}
		
	    protected Renderer getRenderer(UIComponent oComp, FacesContext context) {

	        String rendererType = oComp.getRendererType();
	        Renderer result = null;
	        if (rendererType != null) {
	            result = context.getRenderKit().getRenderer(oComp.getFamily(),
	                                                        rendererType);
	        }            
	        return result;
	    }
		
	}
	
	
	
	
}
