package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class MainLayout extends XUIComponentBase {

	XUIBindProperty<String> applicationType = new XUIBindProperty<String>("applicationType", this, "TREE_BASED", String.class );
	
	public String getApplicationType() {
		return applicationType.getEvaluatedValue();
	}

	public void setApplicationType(String applicationType) {
		this.applicationType.setExpressionText( applicationType );
	}

	@Override
	public void processInitComponents() {
	}

	public static class XEOHTMLRenderer extends XUIRenderer {
        public ExtConfig oExtToolBar;

        @Override
        public void encodeEnd(XUIComponentBase oComp ) throws IOException {
        	
        	MainLayout oMainLayout = (MainLayout)oComp;
        	
            /*
            <div id='topdownMenu' style="height:'50px'"></div>
            <div id='workArea'></div>
            */
            XUIResponseWriter w = getResponseWriter();
            w.startElement( DIV, oMainLayout );
            w.writeAttribute( ID, "topdownMenu", null );
            w.endElement( DIV );
            w.startElement( DIV, oMainLayout );
            w.writeAttribute( ID, "workArea", null );
            w.endElement( DIV );
            
            w.getScriptContext().addIncludeAfter("ext-xeo",XUIScriptContext.POSITION_HEADER, "xeo-layouts", "ext-xeo/xeo-layouts.js" );
            
            if( this.oExtToolBar != null ) {
	            this.oExtToolBar.setVarName( "mainToolBar" );
	            
	            w.getScriptContext().add(
	                    XUIScriptContext.POSITION_HEADER,
	                    oMainLayout.getId() + "_tb",
	                    this.oExtToolBar.renderExtConfig()
	                );
            }
        }
        
        
        @Override
        public void encodeChildren(XUIComponentBase oComp) throws IOException {
            Iterator<UIComponent> oChildIterator;
            UIComponent           oChildComp;
            oChildIterator = oComp.getChildren().iterator();
            while( oChildIterator.hasNext() ) {
                oChildComp = oChildIterator.next();

                if( oChildComp instanceof ToolBar ) {
                    if (!oChildComp.isRendered()) {
                        return;
                    }
                    String rendererType;
                    rendererType = oChildComp.getRendererType();
                    if (rendererType != null) {
                        Renderer renderer = getRenderer( oChildComp, XUIRequestContext.getCurrentContext().getFacesContext() );
                        if (renderer != null) {
                            this.oExtToolBar = ((ExtJsRenderer)renderer).extEncodeAll( (XUIComponentBase)oChildComp );
                        }
                    }
                }
            }
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

        @Override
        public boolean getRendersChildren() {
            return true;
        }
    }
}
