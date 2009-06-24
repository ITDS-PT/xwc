package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import com.sun.faces.io.FastStringWriter;

public class AttributeOutput extends AttributeBase {
	
    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            
            XUIResponseWriter w = getResponseWriter();

            // Place holder for the component
            w.startElement( DIV, oComp );
            w.writeAttribute( ID, oComp.getClientId(), null );
            w.writeAttribute( HTMLAttr.CLASS, "", null );
            w.writeAttribute( HTMLAttr.STYLE, "width:100%", null );
            w.endElement( DIV );
            
            w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
                oComp.getId(),
                renderExtJs( (AttributeOutput)oComp )
            );
            
        }

        public String renderExtJs( AttributeOutput oAttrLabel ) {
            
            FastStringWriter sOut = new FastStringWriter( 500 );
            
//            sOut.write( "var " + oAttrLabel.getId() + " = " +
            sOut.write(		"new Ext.form.Label({"); sOut.write("\n");
            sOut.write( "renderTo: '" ); sOut.write( oAttrLabel.getClientId() ); sOut.write("',");

            if ( !oAttrLabel.getEffectivePermission(SecurityPermissions.READ) ) {
            	// Without permissions do not render the field
            	return "";
            }            
            
            if( !oAttrLabel.isVisible() )
                sOut.write( "hidden: true,"); 
            if( oAttrLabel.isDisabled() )
                sOut.write( "disabled: true,");

            sOut.write( "text: '"); 
            sOut.write( String.valueOf( oAttrLabel.getValue() ) ); 
            sOut.write("'");
            sOut.write("});");
            return sOut.toString();
        }

    }

}
