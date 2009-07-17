package netgest.bo.xwc.components.classic;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLAttr.NAME;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.OPTION;
import static netgest.bo.xwc.components.HTMLTag.SELECT;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
public class AttributeLov extends AttributeBase {

    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            AttributeLov        oAttrLov;

            Map<Object,String>	oLovMap;
            Object              oCurrentValue;

            XUIResponseWriter w = getResponseWriter();
            oAttrLov = (AttributeLov)oComp; 

            oLovMap = oAttrLov.getLovMap();
            
            oCurrentValue    = oAttrLov.getValue();
            
            if( oCurrentValue != null ) {
                oCurrentValue = String.valueOf( oCurrentValue );
            }
            
            // Place holder for the component
            w.startElement( DIV, oComp );
            w.writeAttribute( ID, oComp.getClientId(), null );
            w.startElement( SELECT, oComp );
            w.writeAttribute( ID, oComp.getClientId() + "_s", null );
            w.writeAttribute( NAME, oComp.getClientId() + "_s", null );
            
            Set<Object> oValues = oLovMap.keySet();
            for ( Object oValue : oValues ) {
                
                w.startElement( OPTION, oComp );
                w.writeAttribute( "value", String.valueOf( oValue ), null );
                
                if( oCurrentValue != null && oCurrentValue.equals( String.valueOf( oValue ) ) ) {
                    w.writeAttribute( "selected", "1", null );
                }
                
                String oValueDesc = oLovMap.get( oValue );
                if( oValueDesc != null && oValueDesc.length() > 0 )
                	w.writeText( oValueDesc, null );
                
                w.endElement( OPTION );
                
            }
            w.endElement( SELECT ); 

            w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
                oComp.getId(),
                renderExtJs( oComp )
            );

        }

        public String renderExtJs( XUIComponentBase oComp ) {
            AttributeLov        oAttrLov;
            String              sJsValue;
            StringBuilder    	sOut;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            sOut = new StringBuilder( 100 );

            oAttrLov = (AttributeLov)oComp; 

            if ( !oAttrLov.getEffectivePermission(SecurityPermissions.READ) ) {
            	// Without permissions do not render the field
            	return "";
            }
            
            sJsValue = String.valueOf( oAttrLov.getValue() ); 
            
            ExtConfig oInpConfig = new ExtConfig("Ext.form.ComboBox");
            
            oInpConfig.addJSString("renderTo", oComp.getClientId());
            oInpConfig.addJSString("id", oComp.getClientId());
            oInpConfig.addJSString("typeAhead", "true");
            oInpConfig.addJSString("triggerAction", "all");
            oInpConfig.addJSString("transform", oComp.getClientId() + "_s" );
            oInpConfig.addJSString("forceSelection", "true" );
            oInpConfig.add("width", oAttrLov.getWidth() );

            if( oAttrLov.isReadOnly() ) {
            	oInpConfig.add("readOnly", true );
            	oInpConfig.add("triggerAction", "''" ); 
            }
            
            if( !oAttrLov.isVisible() )
            	oInpConfig.add("hidden", true );

            if( oAttrLov.isDisabled() || !oAttrLov.getEffectivePermission(SecurityPermissions.WRITE) )
            	oInpConfig.add("disabled", true );
            
            oInpConfig.addJSString("name", oAttrLov.getClientId() );
            
            if( sJsValue != null )
            	oInpConfig.addJSString("value", sJsValue );

            if( oForm.haveDependents( oAttrLov.getObjectAttribute() ) || oAttrLov.isOnChangeSubmit()  ) {
            	ExtConfig listeners = oInpConfig.addChild("listeners");
            	listeners.add( "'select'" , 
            			"function(fld,newValue,oldValue){\n" +
            			 XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}"
            	);
            }
            
            oInpConfig.renderExtConfig( sOut );

            return sOut.toString();
        }


        @Override
        public void decode(XUIComponentBase component) {

            AttributeLov oAttrComp;
            
            oAttrComp = (AttributeLov)component;
            
            String value = getFacesContext().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() + "_s" );
            oAttrComp.setSubmittedValue( value );
            
            super.decode(component);

        }
    }


}
