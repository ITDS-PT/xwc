package netgest.bo.xwc.framework;

import java.io.IOException;
import java.util.Enumeration;

import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XUIStyleContext extends XUIScriptContext {

	public void render( XUIResponseWriter hW, XUIResponseWriter bW, XUIResponseWriter fW ) throws IOException {
        Enumeration<Fragment> oFragmentsEnum;
        Fragment oFragment;
        
        boolean bInLineTagOpen = false;
        
        oFragmentsEnum  = oFragmentsVector.elements();
        while( oFragmentsEnum.hasMoreElements() ) {
            oFragment = oFragmentsEnum.nextElement();
            if( oFragment.getPosition() == POSITION_HEADER ) 
            {
                if( oFragment.getType() == TYPE_INCLUDE ) {
                    if( bInLineTagOpen ) {
                        hW.write('\n');
                        hW.endElement("link");   
                        bInLineTagOpen = false;
                    }
                }
                else if ( oFragment.getType() == TYPE_TEXT ) {
                    /*if( !bInLineTagOpen ) {
                        hW.write('\n');
                        hW.startElement("style", null );
                        hW.writeAttribute("type", "text/css", null);
                        bInLineTagOpen = true;
                    }*/
                }
                renderFragment( hW, oFragment );
            }
        }
        if( bInLineTagOpen ) {
            hW.write('\n');
            hW.endElement("style");    
        }
        
        bInLineTagOpen = false;
        oFragmentsEnum  = oFragmentsVector.elements();
        while( oFragmentsEnum.hasMoreElements() ) {
            oFragment = oFragmentsEnum.nextElement();
            if( oFragment.getPosition() == POSITION_FOOTER ) 
            {
                if( oFragment.getType() == TYPE_INCLUDE ) {
                    if( bInLineTagOpen ) {
                        fW.endElement("link");   
                        bInLineTagOpen = false;
                    }
                }
                else if ( oFragment.getType() == TYPE_TEXT ) {
                    if( !bInLineTagOpen ) {
                        fW.startElement("style", null );
                        bInLineTagOpen = true;
                    }
                }
                renderFragment( fW, oFragment );
            }
        }
        if( bInLineTagOpen ) {
            hW.write('\n');
            fW.endElement("style");    
        }
        hW.write('\n');

    }
	
	
    public void renderFragment( XUIResponseWriter w, Fragment oFragment ) throws IOException {

        // <script type="text/javascript" src="/en/us/shared/core/2/js/js.ashx?s=Csp;shared"></script>
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();

    	
    	if( !oFragment.isRendered() ) {

            //<link rel="stylesheet" type="text/css" href="/en/us/shared/core/2/css/css.ashx?sc=/en/us/site.config&m=cspMscomHomePageBase" />
                                                       
            w.write('\n');
            if( oFragment.getType().getValue() == TYPE_INCLUDE.getValue()  ) {
                w.startElement("link", null);
                w.writeAttribute("rel","stylesheet", null );
                w.writeAttribute("type","text/css", null );
                w.writeAttribute("media","screen", null );
                w.writeAttribute("href", oRequestContext.getResourceUrl( oFragment.getContent().toString() ), null );
                w.endElement("link");
            }
            else if ( oFragment.getType().getValue() == TYPE_TEXT.getValue() ) {
                w.startElement("style", null);
                w.writeAttribute("type","text/css", null );
                w.write('\n');
                w.writeText( oFragment.getContent(), null );
                w.write('\n');
                w.endElement( "style" );
            }
            else {
                throw new IllegalStateException(ExceptionMessage.STYLE_RENDER_TYPE_IS_INVALID.toString());
            }
            oFragment.markRenderered();
        }
    }
    
    @Override
    protected void renderFragmentForAjaxDom( Element Element, Fragment oFragment ) {
        Element cssElement;
        Document oXmlDoc;

        oXmlDoc = Element.getOwnerDocument();

        cssElement = oXmlDoc.createElement("link");
        cssElement.setAttribute( "id", oFragment.getScriptId() );
        cssElement.setAttribute( "rel", "stylesheet" );
        cssElement.setAttribute( "type", "text/css" );
        
        if( oFragment.getType() == TYPE_TEXT )
        	cssElement.appendChild( oXmlDoc.createCDATASection( String.valueOf( oFragment.getContent() ) ) );
        else
        	cssElement.setAttribute("src", String.valueOf( oFragment.getContent() ) );

        Element.appendChild( cssElement );
    }

}
