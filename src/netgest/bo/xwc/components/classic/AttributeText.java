package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import javax.faces.context.FacesContext;

/**
 * This component outputs text from a {@link DataFieldConnector}
 * 
 * @author Joao Carreira
 *
 */
public class AttributeText extends AttributeBase {
	
	
	@Override
	public void validateModel() {
		super.validateModel();
	}
	
	@Override
	public void validate(FacesContext context) {
		Object      oSubmitedValue = getSubmittedValue();
        String      sSubmitedValue = null;
        
        if( oSubmitedValue != null )
        {
            sSubmitedValue = (String)oSubmitedValue;
            if( sSubmitedValue.length() > 0 )
            {
                    if( sSubmitedValue.length() > getMaxLength() ) {
                    	setValid( false );
                    	setInvalidText( ComponentMessages.VALUE_ERROR_MAX_LENGTH_LABEL.toString(getLabel(), getMaxLength() ) );
                        getRequestContext().addMessage( getClientId(), 
                        		new XUIMessage(
                                        XUIMessage.TYPE_ALERT,
                                        XUIMessage.SEVERITY_ERROR,
                                        getLabel(),
                                        ComponentMessages.VALUE_ERROR_MAX_LENGTH_LABEL.toString(getLabel(), getMaxLength() )
                                )
                        );
                    }
                    else {
                    	super.validate( context );
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
    		return "Ext.form.TextField";
    	}
    	
        @Override
        public ExtConfig getExtJsFieldConfig(AttributeBase oAttr) {
        	
            ExtConfig textConfig = super.getExtJsFieldConfig( oAttr );
        	textConfig.add( "enableKeyEvents" , true );
        	
        	return textConfig;
        	
        };

        @Override
        public ExtConfig getExtJsFieldListeners(AttributeBase oAtt) {
        	ExtConfig listeners = super.getExtJsFieldListeners(oAtt);
        	listeners.add( "keydown" , 
        			"function(fld,e) { " +
        			"	var fldVal = new String(fld.getValue());" +
        			"	if( fldVal.length >= fld.maxLength ) {" +
        			"		if(event.keyCode >= 32 && '33,34,35,36,37,38,39,40,45,46'.indexOf(''+event.keyCode) == -1 ) {" +
        			"			event.returnValue=false;" +
        			"		}" +
        			"	}" +
        			"}"
            );
        	return listeners;
        }
        
        @Override
        public void decode(XUIComponentBase component) {

        	AttributeBase oAttrComp;
            
            oAttrComp = (AttributeBase)component;
            
            if( !oAttrComp.isDisabled() && !oAttrComp.isReadOnly() && oAttrComp.isVisible() ) {
            	String clientId = oAttrComp.getClientId();
	            String value = FacesContext.getCurrentInstance()
	            .getExternalContext()
	            .getRequestParameterMap().get( clientId );
	            if( value != null ) {
	            	oAttrComp.setSubmittedValue( value );
	            }
            }
            super.decode(component);

        }
    }
}
