package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import com.sun.faces.io.FastStringWriter;

public class AttributeBoolean extends AttributeBase {

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
                oComp.getClass().getName() + ":" + oComp.getId(),
                renderExtJs( oComp )
            );

        }

        public String renderExtJs( XUIComponentBase oComp ) {
            AttributeBoolean       oAttrBoolean;
            String              sJsValue;
            FastStringWriter    sOut;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );
            oAttrBoolean = (AttributeBoolean)oComp; 

            // Check permissions for field 
            if ( !oAttrBoolean.getEffectivePermission(SecurityPermissions.READ) ) {
            	// Without permissions do not render the field
            	return "";
            }
            
            sOut = new FastStringWriter( 100 );
            
            sJsValue = (String)oAttrBoolean.getValue(); 
            
            ExtConfig oCheckConfig = new ExtConfig( "Ext.form.Checkbox" );
            //sOut.write( "Ext.onReady( function() { " ); sOut.write("\n");
            //sOut.write( "var " + oComp.getId() + " = new Ext.form.Checkbox({"); sOut.write("\n");
            oCheckConfig.addJSString("renderTo", oComp.getClientId() );
            //sOut.write( "renderTo: '" ); sOut.write( oComp.getClientId() ); sOut.write("',");
            oCheckConfig.add("width","15");
            oCheckConfig.addJSString("id", oComp.getClientId() + "_c" );

            //sOut.write( "width: 15,");

            if( oForm.haveDependents( oAttrBoolean.getObjectAttribute() ) || oAttrBoolean.isOnChangeSubmit() )
            {
            	oCheckConfig.add( "handler", "function(fld,newValue,oldValue){fld.setValue(newValue);" 
                        + XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}"
	            );
            }
            
            if( !oAttrBoolean.isVisible()  )
                oCheckConfig.add("hidden", true);
                //sOut.write( "hidden: true,");
                 
            if( oAttrBoolean.isDisabled() || ( !oAttrBoolean.getEffectivePermission(SecurityPermissions.WRITE) ) )
                oCheckConfig.add("disabled", true);
                //sOut.write( "disabled: true,");
    
            oCheckConfig.addJSString("name",  oComp.getClientId());
            //sOut.write( "name: '"); sOut.write( oComp.getClientId() ); sOut.write("',");
            
            // Write value            
            if( sJsValue != null )  {
                if ( "1".equals( sJsValue ) ) {
                    oCheckConfig.add( "checked", true );
                    //sOut.write( "checked: true");
                }
                else {
                    oCheckConfig.add( "checked", false );
                    //sOut.write( "checked: false");
                }
            }
            else {
                oCheckConfig.add( "checked", false );
                //sOut.write( "checked: false");
            }

            //oCheckConfig.setVarName( oAttrBoolean.getId() );
            oCheckConfig.renderExtConfig( sOut.getBuffer() );
            sOut.getBuffer().append(';');
            return sOut.toString();
        }


        @Override
        public void decode(XUIComponentBase component) {

            AttributeBoolean oAttrComp;
             
            oAttrComp = (AttributeBoolean)component;
            
            String value = getFacesContext().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
            if( "on".equals( value ) ) {
                oAttrComp.setSubmittedValue( "1" );    
            }
            else if ( value != null ) {
                oAttrComp.setSubmittedValue( "0" );    
            }
            super.decode(component);

        }
    }


}
