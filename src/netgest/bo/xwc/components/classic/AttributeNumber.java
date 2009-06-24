package netgest.bo.xwc.components.classic;

import com.sun.faces.io.FastStringWriter;

import java.io.IOException;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.text.ParseException;

import java.util.Date;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIInput;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.*;

public class AttributeNumber extends AttributeBase {

    @Override
    public void validate( FacesContext context ) {
        Object      oSubmitedValue = getSubmittedValue();
        String      sSubmitedValue = null;
        BigDecimal  oSubmitedBigDecimal;
        
        if( oSubmitedValue != null )
        {
            sSubmitedValue = (String)oSubmitedValue;
            if( sSubmitedValue.length() > 0 )
            {
                try {
                    oSubmitedBigDecimal = new BigDecimal( String.valueOf( oSubmitedValue ) );
                    setValue( oSubmitedBigDecimal );
                }
                catch( NumberFormatException ex ) {
                    getRequestContext().addMessage( getClientId(), new XUIMessage(
                                                                        XUIMessage.TYPE_MESSAGE,
                                                                        XUIMessage.SEVERITY_ERROR,
                                                                        getLabel(),
                                                                        oSubmitedValue + " não está no formato correcto "
                                                                   )
                                                    );
                    setValid( false );
                }
            }
            else {
                setValue( null );
            }
        }
    }

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

            // Mark field as valid/invalid
            if( !((XUIInput)oComp).isValid() ) {
            	w.getScriptContext().add(
            			XUIScriptContext.POSITION_FOOTER, 
            			oComp.getClientId()+"_invalid", 
            			"Ext.ComponentMgr.get('" + oComp.getClientId() + "_c" + "').markInvalid();"
            		);
            }
            
        }

        public String renderExtJs( XUIComponentBase oComp ) {
            AttributeNumber     oAttr;
            String              sJsValue;
            StringBuilder    	sOut;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            sOut = new StringBuilder( 100 );

            oAttr = (AttributeNumber)oComp; 
            sJsValue = null;

            if ( !oAttr.getEffectivePermission(SecurityPermissions.READ) ) {
            	// Without permissions do not render the field
            	return "";
            }
            
            try {
				sJsValue = String.valueOf( oAttr.getValue() );
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

            ExtConfig oInpConfig = new ExtConfig("Ext.form.NumberField");
            oInpConfig.add( "enableKeyEvents" , true );
            oInpConfig.addJSString("renderTo", oComp.getClientId());
            oInpConfig.addJSString("id", oComp.getClientId() + "_c" );
            oInpConfig.add("decimalPrecision", oAttr.getDecimalPrecision());
            oInpConfig.addJSString("width", oAttr.getWidth() );

            oInpConfig.add("maxLength", oAttr.getMaxLength() );

            if( !oAttr.isVisible() )
            	oInpConfig.add("hidden", true );

            if( oAttr.isDisabled() || !oAttr.getEffectivePermission(SecurityPermissions.WRITE) )
            	oInpConfig.add("disabled", true );
            
            oInpConfig.addJSString("name", oAttr.getClientId() );
            
            if( sJsValue != null )
            	oInpConfig.addJSString("value", sJsValue );
 
        	ExtConfig listeners = oInpConfig.addChild("listeners");
            if( oForm.haveDependents( oAttr.getObjectAttribute() ) || oAttr.isOnChangeSubmit() ) {
            	listeners.add( "'change'" , 
            			"function(fld,newValue,oldValue){fld.setValue(newValue);" +
            			 XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}"
            	);
            }
            listeners.add( "'keydown'" , 
        			"function(fld,e) { " +
        			"	var fldVal = new String(fld.getValue());" +
        			"	if(fldVal.length > 5 ) debugger;" +
        			"	fldVal = fldVal.replace(/\\./g,'');" +
        			"	if( fldVal.length >= fld.maxLength ){" +
        			"		if( '01234556789'.indexOf(String.fromCharCode(event.keyCode))>-1 ) {" +
        			"			event.returnValue=false; " +
        			"		}" +
        			"	}" +
        			"}"
            );
            oInpConfig.renderExtConfig( sOut );
            return sOut.toString();
        }

        @Override
        public void decode(XUIComponentBase component) {

            AttributeNumber oAttrComp;
            
            oAttrComp = (AttributeNumber)component;
            
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
            oAttrComp.setSubmittedValue( value );
            
            super.decode(component);

        }
    }

}
