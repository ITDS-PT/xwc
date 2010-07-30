package netgest.bo.xwc.components.classic.renderers;

import java.io.IOException;
import java.io.InputStream;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.classic.Layouts;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUITheme;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.utils.IOUtils;

public class XMLViewRootRenderer extends XUIRenderer {

	@Deprecated
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        XUIResponseWriter w = getResponseWriter();

        XUIViewRoot viewRoot = (XUIViewRoot)component;
        
        //if( renderHead() ) {

            // Add Scripts and Style
            XUIResponseWriter headerW = getResponseWriter().getHeaderWriter();

            // Write Header
            
        	headerW.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE foo-bar [\n");
        	
        	InputStream is1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("xhtml-lat1.ent");
        	headerW.write( new String(IOUtils.copyByte( is1 )) );
        	headerW.write("\n");
        	is1.close();
        	
        	InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("xhtml-special.ent");
        	headerW.write( new String(IOUtils.copyByte( is2 )) );
        	headerW.write("\n");
        	is2.close();

        	InputStream is3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("xhtml-symbol.ent");
        	headerW.write( new String(IOUtils.copyByte( is3 )) );
        	headerW.write("\n]>\n");
        	is3.close();
        	
            headerW.startElement("html", component);

            XUITheme t = getTheme();
            if( t != null ) {
            	headerW.writeAttribute( "style", getTheme().getHtmlStyle(), "style" );
            }
            
            headerW.writeText('\n');
            headerW.startElement("head", component );
            	headerW.startElement("base", component);
	            	HttpServletRequest req = (HttpServletRequest) getRequestContext()
					.getRequest();
	            	String link = (req.isSecure() ? "https" : "http")
					+ "://"
					+ req.getServerName()
					+ (req.getServerPort() == 80 ? "" : ":"
							+ req.getServerPort())
					+ getRequestContext().getResourceUrl("");
					headerW.writeAttribute("href", link, "href");
            	headerW.endElement("base");
            
            // Write Body
            w.startElement("body", component );
            if( t != null && t.getBodyStyle() != null ) {
                w.writeAttribute( "style", getTheme().getBodyStyle() + ";height:100%;width:100%", "style" );
            }
            headerW.writeText('\n');
        	w.startElement( "div", component );
            w.writeAttribute( "id", ((XUIViewRoot)component).getClientId(), "id" );
            
            // N�o sei se � necess�rio, foi criado a necessidade atrav�s 
            if( viewRoot.findComponent( Window.class ) != null ) {
            	w.writeAttribute( HTMLAttr.CLASS, "x-panel", "" );
            }
        	w.writeAttribute("style", "width:100%;height:100%", null);
        	
        	if( t != null ) {
                t.addScripts( w.getScriptContext() );
                t.addStyle( w.getStyleContext() );
        	}
        	
    }

    @Deprecated
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        XUIRequestContext oRequestContext;

        
        oRequestContext = XUIRequestContext.getCurrentContext();
        XUIResponseWriter w = getResponseWriter();

        if( getRequestContext().isPortletRequest() ) {
        	String sWidth = (String)((HttpServletRequest)getRequestContext().getRequest()).getAttribute("xvw.width");
        	if( sWidth != null ) {
        		w.endElement( "div" );
        	}
        } else {
        	w.endElement( "div" );
        }
        
    	Layouts.doLayout(w);
        XUIResponseWriter footerW = getResponseWriter().getFooterWriter();
        XUIResponseWriter headerW = getResponseWriter().getHeaderWriter();

        //if( renderScripts() ) {

            w.getScriptContext().render( headerW, w, footerW );
            w.getStyleContext().render( headerW, w, footerW );
            oRequestContext.getScriptContext().render( headerW, w, footerW );
        //}

        //if( renderHead() ) {
            // Write footer Elements
        	
        	XUITheme t = getTheme();
        	if( t != null ) {
                if( t.getHtmlStyle() != null ) {
                    w.writeAttribute( "style", getTheme().getHtmlStyle(), "style" );
                }
        	}
        	
        	// Write Head Elements
            headerW.writeText('\n');
            headerW.endElement("head");
            headerW.writeText('\n');
                
            // End tag body
            w.writeText('\n');
            w.endElement("body");
            
            // End Tag HTML
            footerW.writeText('\n');
            footerW.endElement("html");
       
        //}
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
}
