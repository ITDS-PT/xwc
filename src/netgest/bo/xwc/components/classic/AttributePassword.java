package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import com.sun.faces.io.FastStringWriter;

public class AttributePassword extends AttributeBase {

    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {

        	XUIResponseWriter w = getResponseWriter();

            // Place holder for the component
            w.startElement( DIV, oComp );
            w.writeAttribute( ID, oComp.getClientId(), null );
            //w.writeAttribute( HTMLAttr.STYLE, "width:120px", null );
            w.endElement( DIV ); 

            w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
                oComp.getId(),
                renderExtJs( oComp )
            );

        }

        public String renderExtJs( XUIComponentBase oComp ) {
            AttributePassword       oAttrText;
            String              sJsValue;
            FastStringWriter    sOut;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            sOut = new FastStringWriter( 250 );

            oAttrText = (AttributePassword)oComp; 
            
            if ( !oAttrText.getEffectivePermission(SecurityPermissions.READ) ) {
            	// Without permissions do not render the field
            	return "";
            }

            sJsValue = (String)oAttrText.getValue(); 
            
            
            if( !isAjax() )
                sOut.write( "Ext.onReady( function() { " ); sOut.write("\n");
                                                            
            sOut.write( "var " + oComp.getId() + " = new Ext.form.TextField({"); sOut.write("\n");
            sOut.write( "renderTo: '" ); sOut.write( oComp.getClientId() ); sOut.write("',");
            sOut.write( "width: "+oAttrText.getWidth()+",");
            sOut.write( "inputType: 'password',");

            if( oForm.haveDependents( oAttrText.getObjectAttribute() ) || oAttrText.isOnChangeSubmit()  )
                sOut.write( "listeners : { change: function(fld,newValue,oldValue){fld.setValue(newValue);" 
                            + XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "} },"
                        );
            
            sOut.write( "maxLength: '"+oAttrText.getMaxLength()+"',");
            if( !oAttrText.isVisible() )
                sOut.write( "hidden: true,");
                 
            if( oAttrText.isDisabled() || !oAttrText.getEffectivePermission(SecurityPermissions.WRITE) )
                sOut.write( "disabled: true,");
    
            sOut.write( "name: '"); sOut.write( oComp.getClientId() ); sOut.write("',");
            
            // Write value            
            sOut.write( "value: '"); 
            if( sJsValue != null )  
                sOut.write( sJsValue ); 
            sOut.write("'");
                
            sOut.write("});");
    
            if( !isAjax() )
                sOut.write("});");
            
            return sOut.toString();
        }


        @Override
        public void decode(XUIComponentBase component) {

            AttributePassword oAttrComp;
            
            oAttrComp = (AttributePassword)component;
            
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
            oAttrComp.setSubmittedValue( value );
            
            super.decode(component);

        }
    }

}
