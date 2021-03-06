package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
/**
 * This component renders a checkbox to represent a boolean value
 * 
 * It works with the value '1' to represent checked e '0' to represent unchecked
 * 
 * @author jcarreira
 *
 */
public class AttributeBoolean extends AttributeBase {
	
	XUIBaseProperty<Boolean> useBooleanValues = new XUIBaseProperty<Boolean>( "useBooleanValues", this, false ); 
	
	public boolean getUseBooleanValues() {
		return useBooleanValues.getValue();
	}

	@Override
	public void preRender() {
		
		super.preRender();

		Object value = getValue();
		if( value != null && value instanceof Boolean )
			useBooleanValues.setValue( true );
		else
			useBooleanValues.setValue( false );
		
	}

	public static class XEOHTMLRenderer extends ExtJsFieldRendeder {

		@Override
		public String getExtComponentType(XUIComponentBase oComp) {
			return "Ext.form.Checkbox";
		}
		
    	@Override
    	public ScriptBuilder getEndComponentScript(AttributeBase oComp) {
    		ScriptBuilder s = super.getEndComponentScript(oComp, false, false );
    		
    		//We need the suspend/resume events because if something changes the
    		//value of this component and we have the "onChangeSubmit" property
    		//that could create a loop where the value is changed, the "checked" event
    		//is triggered, which in turns triggers a submission which can change the value
    		//of this component again
            Object sJsValue = oComp.getValue();
            s.w( "c.suspendEvents();" );
            s.w( "c.setValue('" ).writeValue( sJsValue ).l( "')" );
            s.w( "c.resumeEvents();" );
            s.endBlock();
    		
    		return s;
    		
    	}
		
		
		@Override
		public ExtConfig getExtJsFieldConfig(AttributeBase oComp) {

			AttributeBoolean    oAttrBoolean;
            String              sJsValue;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findParent( Form.class );
            oAttrBoolean = (AttributeBoolean)oComp; 

            if( oAttrBoolean.getUseBooleanValues() )
            	sJsValue = ((Boolean)oAttrBoolean.getValue()).booleanValue()?"1":"0";
            else
            	sJsValue = (String)oAttrBoolean.getValue();
           
            
            ExtConfig oCheckConfig = super.getExtJsFieldConfig(oComp);
            oCheckConfig.add("width","15");

            
            // Write value            
            if( sJsValue != null )  {
                if ( "1".equals( sJsValue ) ) {
                    oCheckConfig.add( "checked", true );
                }
                else {
                    oCheckConfig.add( "checked", false );
                }
            }
            else {
                oCheckConfig.add( "checked", false );
            }
            if( oForm.haveDependents( oAttrBoolean.getObjectAttribute() ) || oAttrBoolean.isOnChangeSubmit() )
            {
            	//Script for the check event, sets the value and triggers a form submission
            	StringBuilder chkBoxHandler = new StringBuilder();
            	chkBoxHandler.append("function(fld, newValue){");
	            	chkBoxHandler.append("fld.setValue(newValue);");
	            		chkBoxHandler.append(XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ));
	            	chkBoxHandler.append("; ");
            	chkBoxHandler.append("}");
            	ExtConfig listeners = new ExtConfig();
            	listeners.add("check", chkBoxHandler.toString());
            	oCheckConfig.add("listeners", listeners);
            	
            }

            return oCheckConfig;
        }
		
		
		@Override
		public ExtConfig getExtJsFieldListeners(AttributeBase oAtt) {
			return null;
		}


        @Override
        public void decode(XUIComponentBase component) {

            AttributeBoolean oAttrComp;
            oAttrComp = (AttributeBoolean)component;
            
            if( !oAttrComp.isDisabled() && !oAttrComp.isReadOnly() && oAttrComp.isVisible() ) {
            
	            String value = getFacesContext().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
	            // Por vezes o ExtJs devolve yes.. nao percebi porque razao, deve ser bug do extjs
	            if( "on".equalsIgnoreCase( value ) ) {
	                if( oAttrComp.getUseBooleanValues() )
	                	oAttrComp.setSubmittedValue( true );
	                else
	                	oAttrComp.setSubmittedValue( "1" );
	                	
	            }
	            else if ( value != null ) {
	                if( oAttrComp.getUseBooleanValues() )
	                	oAttrComp.setSubmittedValue( false );
	                else
	                	oAttrComp.setSubmittedValue( "0" );
	            }
            }
            super.decode(component);

            
        }
    }
	
	@Override
	public String toString(){
		return this.getId();
	}


}
