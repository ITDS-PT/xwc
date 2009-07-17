package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class AttributeText extends AttributeBase {

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
            AttributeText       oAttrText;
            String              sJsValue;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            oAttrText = (AttributeText)oComp; 
            
            if( !oAttrText.getEffectivePermission(SecurityPermissions.READ) ) {
            	return "";            	
            }
            
            sJsValue = (String)oAttrText.getValue(); 
            
            ExtConfig textConfig = new ExtConfig( "Ext.form.TextField" );
            textConfig.addJSString( "renderTo" , oComp.getClientId() );
            textConfig.addJSString( "width" , oAttrText.getWidth() );
            textConfig.add( "enableKeyEvents" , true );
            
        	ExtConfig textListeners = textConfig.addChild("listeners");
            textListeners.add( "keydown" , 
        			"function(fld,e) { " +
        			"	var fldVal = new String(fld.getValue());" +
        			"	if( fldVal.length >= fld.maxLength && event.keyCode >= 32 ){" +
        			"			event.returnValue=false; " +
        			"	}" +
        			"}"
            );

            if( oForm.haveDependents( oAttrText.getObjectAttribute() ) || oAttrText.isOnChangeSubmit()  ) {
            	textListeners.add( "change" , "function(fld,newValue,oldValue){fld.setValue(newValue);" 
                + XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}" );
            	
            }
            textConfig.add( "maxLength", oAttrText.getMaxLength());
            
            if( !oAttrText.isVisible() ) {
                textConfig.add( "hidden", true );
            }
            if( oAttrText.isDisabled() || !oAttrText.getEffectivePermission(SecurityPermissions.WRITE) ) {
                textConfig.add( "disabled", true );
            }
            textConfig.addJSString( "name",  oComp.getClientId() );
            
            // Write value            
            textConfig.addJSString( "value",  sJsValue );
            
            StringBuilder sOut = new StringBuilder( 150 );
            textConfig.renderExtConfig( sOut );
            
            return sOut.toString();
        }


        @Override
        public void decode(XUIComponentBase component) {

            AttributeText oAttrComp;
            
            oAttrComp = (AttributeText)component;
            
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
            oAttrComp.setSubmittedValue( value );
            
            super.decode(component);

        }
    }
}
