package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * This component represents a HtmlEditor.
 * 
 * @author jcarreira
 *
 */
public class AttributeHtmlEditor extends AttributeBase {
	
	@Override
	public void initComponent() {
		XUIRequestContext.getCurrentContext()
	        .getStyleContext().addInclude(
	        		XUIScriptContext.POSITION_HEADER, 
	        		"ext-xeo-nohtmleditor", 
	        		"ext-xeo/css/ext-xeo-htmleditor.css" 
	        );
	}
	
	public AttributeHtmlEditor() {
		super.setHeight("auto");
	}

    public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            
            XUIResponseWriter w = getResponseWriter();
            AttributeHtmlEditor oHtmlComp = (AttributeHtmlEditor)oComp;
            
            if( !oComp.isRenderedOnClient() ) {
	            w.getStyleContext().addInclude(
	            		XUIScriptContext.POSITION_HEADER, 
	            		"ext-xeo-nohtmleditor", 
	            		"ext-xeo/css/ext-xeo-htmleditor.css" 
	            );
	
	            // Place holder for the component
	            if( oHtmlComp.isDisabled() || oHtmlComp.isReadOnly() ) {
	            	
	                String sActionUrl = getRequestContext().getActionUrl();
	                //'javax.faces.ViewState'
	                String sViewState = getRequestContext().getViewRoot().getViewState();
	                //xvw.servlet
	                String sServletId = oComp.getClientId();
	                
	                if( sActionUrl.indexOf('?') != -1 ) {
	                	sActionUrl += "&";
	                }
	                else {
	                	sActionUrl += "?";
	                }
	                sActionUrl += "javax.faces.ViewState=" + sViewState;
	                sActionUrl += "&xvw.servlet=" + sServletId;
	            	
		            w.startElement( HTMLTag.IFRAME, oComp );
		            w.writeAttribute( ID, oComp.getClientId(), null );
		            w.writeAttribute( HTMLAttr.CLASS, "x-form-text x-form-field" , null );
		            w.writeAttribute( HTMLAttr.STYLE, "width:99%;height:" + oHtmlComp.getHeight()+"px" , null );
		            w.writeAttribute( HTMLAttr.FRAMEBORDER, "0" , null );
		            w.writeAttribute( HTMLAttr.SCROLLING, "1" , null );
		            
		            w.writeAttribute( HTMLAttr.SRC , sActionUrl, null );
		            
		            w.endElement( HTMLTag.IFRAME );
	            } else {
		            w.startElement( DIV, oComp );
		            w.writeAttribute( ID, oComp.getClientId(), null );
	                w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
	                        oComp.getId(),
	                        renderExtJs( oComp )
	                    );
	                w.endElement( DIV ); 
	            }
	            if( "auto".equals( oHtmlComp.getHeight() ) ) {
	            	Layouts.registerComponent(  w, oComp, Layouts.LAYOUT_FIT_PARENT );
	            }
            }
        }

        public String renderExtJs( XUIComponentBase oComp ) {
            
            StringBuilder sOut = new StringBuilder( 200 );
            
            AttributeHtmlEditor oHtmlComp = (AttributeHtmlEditor)oComp;
            
            ExtConfig oHtmlCfg = new ExtConfig( "Ext.form.HtmlEditor" );
            oHtmlCfg.addJSString("id", oComp.getClientId() + "_editor" );
            oHtmlCfg.addJSString("name", oComp.getClientId() );
            oHtmlCfg.addJSString("renderTo", oComp.getClientId() );
            oHtmlCfg.addJSString("value", JavaScriptUtils.safeJavaScriptWrite(oHtmlComp.getDisplayValue(), '\'') );
//            oHtmlCfg.add( "width" ,  oHtmlComp.getWidth() );
//            oHtmlCfg.add( "height" ,  "0" );
            oHtmlCfg.add( "frame" ,  false );
//            oHtmlCfg.addJSString( "layout" ,  "fit" );
            oHtmlCfg.add( "enableColors" ,  true );
            oHtmlCfg.add( "enableAlignments" ,  true );
            oHtmlCfg.add( "enableLinks" ,  false );
            
            if( oHtmlComp.isDisabled() ) 
                oHtmlCfg.add( "disabled" ,  true );
            
            if( oHtmlComp.isVisible() )
                oHtmlCfg.add( "visible" ,  false );

            sOut.append( "Ext.onReady( function() {" );
            sOut.append( "try { \n");
            oHtmlCfg.renderExtConfig( sOut );
            sOut.append( "} catch(e){} \n");
            sOut.append("});\n");

            return sOut.toString();
        }

		@Override
		public void decode(XUIComponentBase component) {
			
			String sHtml = XUIRequestContext.getCurrentContext().getRequestParameterMap().get( component.getClientId() );
			((AttributeHtmlEditor)component).setSubmittedValue( sHtml );
			
		
		}

		public void service(ServletRequest request, ServletResponse response, XUIComponentBase oComp) throws IOException {
            AttributeHtmlEditor oHtmlComp = (AttributeHtmlEditor)oComp;
            String sDisplayValue = oHtmlComp.getDisplayValue();
            PrintWriter w = response.getWriter();
            final Pattern p = Pattern.compile( "<([A-Z][A-Z0-9]*)\\b[^>]*>(.*?)</\\1>", Pattern.CASE_INSENSITIVE );
            Matcher m = p.matcher( sDisplayValue );
            if ( m.find() ) {
            	// Content is HTML
            	response.setContentType("text/html");
                w.print( sDisplayValue );
            }
            else {
            	response.setContentType("text/html");
            	w.write("<pre>");
                w.print( sDisplayValue );
            	w.write("</pre>");
            }
		}
    }

}
