package netgest.bo.xwc.components.classic;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

/**
 * This attribute represents a Date Field
 * 
 * It works with the value in the format of java.sql.Timestamp
 * 
 * @author jcarreira
 *
 */
public class AttributeDate extends AttributeBase {

    protected static final SimpleDateFormat oFormatDate = new SimpleDateFormat( "dd/MM/yyyy" );


    @Override
    public void initComponent() {

        super.initComponent();
        
        // Overwrite datatype;
        this.setValueExpression(
            "value", createValueExpression( getValueExpression( "value" ).getExpressionString(), Timestamp.class ) 
        );

    }
    
    @Override
	public void validate( FacesContext context ) {
        String      sSubmitedValue = null;
        Object oSubmitedValue = getSubmittedValue();
        Date   oSubmitedDate;
        
        if( oSubmitedValue != null )
        {
            sSubmitedValue = (String)oSubmitedValue;     
            if( sSubmitedValue.length() > 0 )
            {
                try {
                    oSubmitedDate = oFormatDate.parse( String.valueOf( oSubmitedValue ) );
                    setValue( new Timestamp( oSubmitedDate.getTime() ) );
    
                }
                catch( ParseException ex ) {
                    getRequestContext().addMessage( 
                    		getClientId(), new XUIMessage(
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

    @Override
    public boolean wasStateChanged() {
        return super.wasStateChanged();
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
        	  	sValue = oFormatDate.format( sJsValue );
          	}
    		ExtConfig oInpDateConfig = super.getExtJsFieldConfig( oAttr );
            oInpDateConfig.addJSString("format", "d/m/Y");
            
            if( oAttrDate.isDisabled() || !oAttrDate.getEffectivePermission(SecurityPermissions.WRITE) ) {
            	oInpDateConfig.addJSString("name", "" );
            } 
            oInpDateConfig.addJSString("value", sValue );

            return oInpDateConfig;
    	}
    	
    	@Override
    	public ScriptBuilder getEndComponentScript(AttributeBase oComp) {
    		// TODO Auto-generated method stub
    		ScriptBuilder s = super.getEndComponentScript(oComp, false, false );
    		
            Object sJsValue = (Timestamp)oComp.getValue(); 
            String sValue = "";
            if( sJsValue != null )  {
        	  	sValue = oFormatDate.format( sJsValue );
          	}
    		s.w( "c.setValue('" ).writeValue( sValue ).l( "')" );
    		
    		s.endBlock();
    		
    		return s;
    		
    	}
    	
        @Override
        public void decode(XUIComponentBase component) {

            AttributeDate oAttrComp;
            oAttrComp = (AttributeDate)component;
            
            if( !oAttrComp.isDisabled() && !oAttrComp.isReadOnly() && oAttrComp.isVisible() ) {
	            Map<String,String> reqMap = getFacesContext().getExternalContext().getRequestParameterMap();
	            if( oAttrComp.getSubmittedValue() == null && reqMap.containsKey( oAttrComp.getClientId() ) ) {
	                String value = reqMap.get( oAttrComp.getClientId() );
	                oAttrComp.setSubmittedValue( value );
	            } 
            }
        }
    }

}
