package netgest.bo.xwc.components.html;

import java.io.File;
import java.io.IOException;
import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLAttr.WIDTH;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TR;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.HTMLFileBrowse;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIInput;
import netgest.bo.xwc.framework.http.XUIMultiPartRequestWrapper;

import netgest.io.FSiFile;
import netgest.io.iFile;
/**
 *@author Pedro Campos
 */
public class FileBrowserHtml extends XUIInput{

    /* FIXME Consolidar */
    XUIStateBindProperty<String> label = new XUIStateBindProperty<String>("label", this, String.class );


    private iFile    submitedFile = null;
    
    
    public iFile getSubmitedFile() {
            return submitedFile;
    }

    public void setSubmitedFile(iFile submitedFile) {
            this.submitedFile = submitedFile;
            
    }


    public String getLabel() {
            return label.getEvaluatedValue();
    }


    public void setLabel(String label) {
            this.label.setExpressionText( label );
    }


    public String getFamily() {
    return getRendererType();
    }

    public static class XEOHTMLRenderer extends XUIRenderer {

                @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            XUIResponseWriter w = getResponseWriter();
            FileBrowserHtml oFBComp = (FileBrowserHtml)oComp;

            w.startElement( TABLE, oComp );
            w.writeAttribute( CELLPADDING, "0", null );
            w.writeAttribute( CELLSPACING, "0", null );
            w.writeAttribute( WIDTH, "100%", null );
            
            w.startElement( HTMLTag.TD, oComp );
            w.writeAttribute( WIDTH, "150px", null );
            w.writeText( oFBComp.getLabel(), null );
            w.endElement(HTMLTag.TD);

            w.startElement( HTMLTag.TD, oComp );
            
            w.startElement( HTMLTag.INPUT , oComp );
            w.writeAttribute( WIDTH, "100%", null );
            w.writeAttribute( HTMLAttr.TYPE, "file", null );
            w.writeAttribute( HTMLAttr.NAME, oComp.getClientId(), null );
            w.writeAttribute( HTMLAttr.ID, oComp.getClientId(), null );
            w.endElement(HTMLTag.INPUT);
            
            w.endElement(HTMLTag.TD);
            w.startElement( TR, oComp );
            w.endElement( TR );
            w.endElement( TABLE );
                
                }

                @Override
                public void decode(XUIComponentBase component) {
                        FileBrowserHtml    oFileBrowse;
                        XUIRequestContext oRequestContext;
                        
                        oRequestContext = XUIRequestContext.getCurrentContext();
                        oFileBrowse = (FileBrowserHtml)component;
                        
                        XUIMultiPartRequestWrapper mPartRequest = 
                                (XUIMultiPartRequestWrapper)oRequestContext.getRequest();
                        
                        File oFile = mPartRequest.getFile( component.getClientId() );
                        
                        if( oFile != null ) {
                                oFileBrowse.setSubmittedValue( oFile.getName() );
                                oFileBrowse.setSubmitedFile( new FSiFile(null, oFile.getAbsolutePath(),null) );
                                oFile.deleteOnExit();
                        }
                
                }
        }
    
}
