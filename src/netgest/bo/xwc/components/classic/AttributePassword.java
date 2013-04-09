package netgest.bo.xwc.components.classic;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class AttributePassword extends AttributeBase {

    public static class XEOHTMLRenderer extends AttributeText.XEOHTMLRenderer {

        @Override
        public void decode(XUIComponentBase component) {
            AttributePassword oAttrComp;
            oAttrComp = (AttributePassword)component;
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
            oAttrComp.setSubmittedValue( value );
            super.decode(component);

        }

		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
			return "Ext.form.TextField";
		}
		
		@Override
		public ExtConfig getExtJsFieldConfig( AttributeBase oComp ) {
			AttributeBase oAtt;
			oAtt = oComp;
			
			ExtConfig config = super.getExtJsFieldConfig( oAtt );
			config.addString( "inputType","password" );
			
			return config;
		}
		
    }

}
