package netgest.bo.xwc.components.classic;

import java.math.BigDecimal;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.components.XUIComponentBase;
/**
 * This component reders a only number's input
 * @author jcarreira
 *
 */
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
                    getRequestContext().addMessage( getClientId(), 
                    		new XUIMessage(
                                    XUIMessage.TYPE_MESSAGE,
                                    XUIMessage.SEVERITY_ERROR,
                                    getLabel(),
                                    ComponentMessages.VALUE_ERROR_ON_FORMAT.toString( oSubmitedValue )
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

    public static class XEOHTMLRenderer extends ExtJsFieldRendeder {

		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
			// TODO Auto-generated method stub
			return "Ext.form.NumberField";
		}
    	
		@Override
		public ExtConfig getExtJsFieldConfig( AttributeBase oAttr ) {
			ExtConfig config;
			
			config = super.getExtJsFieldConfig( oAttr );
			
			config.add( "enableKeyEvents" , true );
			config.add("decimalPrecision", oAttr.getDecimalPrecision());
			
			return config;
		}
		
		@Override 
		public ExtConfig getExtJsFieldListeners( AttributeBase oAttr ) {
			ExtConfig listeners;
			
			listeners = super.getExtJsFieldListeners( oAttr );
			ScriptBuilder s = new ScriptBuilder();
			s.l( "function(fld,e) {" )
			.s( "	var fldVal = new String(fld.getValue())" )
			.s( "	fldVal = fldVal.replace(/\\./g,'')" )
			.l( "	if( fldVal.length >= fld.maxLength ) {" )
			.l( "		if( '8,9,13,16,17,18,19,20,27,33,34,35,36,37,38,39,40,45,46'.indexOf(''+event.keyCode) == -1 && '01234556789'.indexOf(String.fromCharCode(event.keyCode))>-1 ) {")
			.l("			event.returnValue=false; ")
			.l("		}")
			.l("	}")
			.l("}");
            listeners.add( "'keydown'" , s );
            return listeners;
			
		}

        @Override
        public void decode(XUIComponentBase component) {

            AttributeNumber oAttrComp;
            
            oAttrComp = (AttributeNumber)component;
            if( !oAttrComp.isDisabled() && !oAttrComp.isReadOnly() && oAttrComp.isVisible() ) {
	            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
	            value = value.replace(',', '.');
	            oAttrComp.setSubmittedValue( value );
            }
            super.decode(component);

        }

    }

}
