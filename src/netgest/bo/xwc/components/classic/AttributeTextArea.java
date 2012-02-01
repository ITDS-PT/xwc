package netgest.bo.xwc.components.classic;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;

/**
 * This attribute renderes a textarea from a {@link DataFieldConnector}
 * @author jcarreira
 *
 */
public class AttributeTextArea extends AttributeBase {

    public static class XEOHTMLRenderer extends ExtJsFieldRendeder {

		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
			return "Ext.form.TextArea";
		}

    	@Override
    	public ExtConfig getExtJsFieldConfig(AttributeBase oAttr) {
    		
    		ExtConfig config = super.getExtJsFieldConfig( oAttr );
    		
    		String height = oAttr.getHeight();
    		
    		if( StringUtils.isEmpty( height ) ) {
    			height = "100";
    		}
            
    		if( !"auto".equals( height ) ) {
            	config.add( "height" , oAttr.getHeight() );
    		}
    		
    		return config;
    		
    	}
    	
        @Override
        public void decode(XUIComponentBase component) {

            AttributeBase oAttrComp;
            
            oAttrComp = (AttributeBase)component;
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
            oAttrComp.setSubmittedValue( value );
            
            super.decode(component);

        }
    }

}
