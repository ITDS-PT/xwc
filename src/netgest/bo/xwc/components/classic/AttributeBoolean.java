package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
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
	
	private boolean getUseBooleanValues() {
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
		public ExtConfig getExtJsFieldConfig(AttributeBase oComp) {

			AttributeBoolean    oAttrBoolean;
            String              sJsValue;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );
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
            	oCheckConfig.add( "handler", "function(fld,newValue,oldValue){fld.setValue(newValue);" 
                        + XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}"
	            );
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
            
            String value = getFacesContext().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
            if( "on".equals( value ) ) {
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
            
            super.decode(component);

            
        }
    }


}
