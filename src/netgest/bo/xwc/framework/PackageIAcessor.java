package netgest.bo.xwc.framework;

import java.io.Writer;
import java.util.List;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.jsf.XUIWriteBehindStateWriter;

public class PackageIAcessor {

    public static final void setResponseContextIsPostBack( XUIRequestContext oResponseContext, boolean bValue ) {
        oResponseContext.setPostBack( bValue );
    }
    
    public static final void initApplicationContext( XUIApplicationContext oApplicationContext ) {
        oApplicationContext.initApplication();
    }
    
    public static final XUIRequestContext createRequestContext( XUIApplicationContext oApplicationContext ){
        XUIRequestContext oRequestContext = new XUIRequestContext( oApplicationContext );
        XUIRequestContext.oCurrentContext.set( oRequestContext );
        return oRequestContext;
    }

    public static final void setHeaderAndFooterToWriter( 
        XUIResponseWriter oWriter,  
        Writer oHeader,
        Writer oFooter
        ) {
            
        oWriter.setHeaderAndFooterWriters( oHeader, oFooter );

    }

    public static final void setScriptContextToWriter( 
        XUIResponseWriter oWriter,  
        XUIScriptContext oScriptContext
        ) {
        
        oWriter.setScriptContext( oScriptContext );

    }
    
    
    public static void recursiveProcessStateChanged(UIComponent oComponent, List<XUIComponentBase> oRenderList) {
    	XUIRenderer.recursiveProcessStateChanged(oComponent, oRenderList);
    }
    


}
