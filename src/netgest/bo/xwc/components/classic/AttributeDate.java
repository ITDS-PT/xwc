package netgest.bo.xwc.components.classic;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.localization.JavaToJavascriptPatternConverter;
import netgest.bo.xwc.framework.localization.XUILocalization;

/**
 * This attribute represents a Date Field
 * 
 * It works with the value in the format of java.sql.Timestamp
 * 
 * @author jcarreira
 *
 */
public class AttributeDate extends AttributeBase {

    @Override
    public void initComponent() {

        super.initComponent();
        
        
        if( getValueExpression( "value" ) != null ) {
        // Overwrite datatype;
        this.setValueExpression(
            "value", createValueExpression( getValueExpression( "value" ).getExpressionString(), Timestamp.class ) 
        );
        }

    }
    
    public boolean shouldUseTimezone(){
    	if (getParent() instanceof AttributeDateTime){
    		return true;
    	} else return false;
    }
    
    @Override
	public void validate( FacesContext context ) {
        String      sSubmitedValue = null;
        Object oSubmitedValue = getSubmittedValue();
        Date   oSubmitedDate;
        AttributeDate dateComponent = (AttributeDate) this;
        if( oSubmitedValue != null )
        {
            sSubmitedValue = (String)oSubmitedValue;     
            if( sSubmitedValue.length() > 0 )
            {
                try {
                	if (!dateComponent.shouldUseTimezone())
                		oSubmitedDate = XUILocalization.parseDateDefaultTimezone( String.valueOf( oSubmitedValue ) );
                	else
                		oSubmitedDate = XUILocalization.parseDate( String.valueOf( oSubmitedValue ) );
                    setValue( new Timestamp( oSubmitedDate.getTime() ) );
    
                }
                catch( ParseException ex ) {
                    getRequestContext().addMessage( 
                    		getClientId(), new XUIMessage(
                                    XUIMessage.TYPE_ALERT,
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
    	public String getExtComponentType(XUIComponentBase oComp) {
    		return "Ext.form.DateField";
    	}
    	
    	@Override
    	public ExtConfig getExtJsFieldConfig(AttributeBase oAttr) {
    		
            AttributeDate       oAttrDate;
            Timestamp           sJsValue;
            
            
            oAttrDate = (AttributeDate)oAttr;
            
            sJsValue = (Timestamp)oAttr.getValue(); 
            String sValue = "";
            
            if( sJsValue != null )  {
            	if (oAttrDate.shouldUseTimezone())
            		sValue = XUILocalization.formatDate(sJsValue);
            	else
            		sValue = XUILocalization.formatDateDefaultTimeZone((sJsValue));
          	}
    		ExtConfig oInpDateConfig = super.getExtJsFieldConfig( oAttr );
    		String format = XUILocalization.getDateFormat();
    		String javascriptPattern = 
    				JavaToJavascriptPatternConverter.convertDatePatternToJavascript( format);
    		oInpDateConfig.addJSString("format", javascriptPattern );
            oInpDateConfig.addJSString("altFormats", 
            		JavaToJavascriptPatternConverter.getAlternativeDateFormats( javascriptPattern ));
            
            if( oAttrDate.isDisabled() || !oAttrDate.getEffectivePermission(SecurityPermissions.WRITE) ) {
            	oInpDateConfig.addJSString("name", "" );
            } 
            oInpDateConfig.addJSString("value", sValue );
            
            if( oAttrDate.isReadOnly() ) {
                oInpDateConfig.add("hideTrigger", true );
            }

            return oInpDateConfig;
    	}
    	
    	@Override
    	public ExtConfig getExtJsFieldListeners( AttributeBase oAtt ) {
        	ExtConfig listeners = super.getExtJsFieldListeners(oAtt);
        	listeners.add( "'blur'" , "function(fld){ExtXeo.DateField.ProcessDot(fld);}");
            return listeners; 
        }
    	
    	@Override
    	public ScriptBuilder getEndComponentScript(AttributeBase oComp) {
    		ScriptBuilder s = new ScriptBuilder();
    		s.startBlock();
    		AttributeDate dateCmp = (AttributeDate) oComp;
    		
    		super.writeExtContextVar(s, oComp);
    		
    		Timestamp sJsValue = (Timestamp)oComp.getValue(); 
            String sValue = "";
            if( sJsValue != null )  {
            	if (dateCmp.shouldUseTimezone())
            		sValue = XUILocalization.formatDate(sJsValue);
            	else
            		sValue = XUILocalization.formatDateDefaultTimeZone((sJsValue));
          	}
            
            s.w( "c.setValue('" ).writeValue( sValue ).l( "');" );
    		
    		s.endBlock();
    		
    		s.w( super.getEndComponentScript(oComp, true, false ) );
    		
    		return s;
    		
    	}
    	
        @Override
        public void decode(XUIComponentBase component) {

            AttributeDate oAttrComp;
            oAttrComp = (AttributeDate)component;
            
            if( !oAttrComp.isDisabled() && !oAttrComp.isReadOnly() && oAttrComp.isVisible() ) {
	            Map<String,String> reqMap = getFacesContext().getExternalContext().getRequestParameterMap();
            	
	            String clientId =  oAttrComp.getClientId();
            	// No extjs quando o comopent attribute e inicializado 
	            // em disabled o name da input no form fica com ext-
	            // Em principio ser� o um bug do extjs.
            	if( !reqMap.containsKey( oAttrComp.getClientId() ) ) {
            		clientId = "ext-" + clientId;
            	}
            	
	            if( oAttrComp.getSubmittedValue() == null && reqMap.containsKey( clientId ) ) {
	                String value = reqMap.get( clientId );
	                oAttrComp.setSubmittedValue( value );
	            } 
            }
        }
    }

}
