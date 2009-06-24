package netgest.bo.xwc.framework;

import java.io.IOException;

import netgest.bo.xwc.framework.XUIScriptContext.Fragment;

public class XUIStyleContext extends XUIScriptContext {

    protected void renderFragment( XUIResponseWriter w, Fragment oFragment ) throws IOException {

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
                throw new IllegalStateException("Style Render Type is invalid");
            }
            oFragment.markRenderered();
        }
    }

}
