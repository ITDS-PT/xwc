package netgest.bo.xwc.components.template.renderers;

import java.util.Map;

import netgest.bo.xwc.components.classic.AttributeLov;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class AttributeLovRenderer extends TemplateRenderer {

	@Override
	public void decode( XUIComponentBase component ) {
		super.decode( component );
		Map<String,String> parameters = getFacesContext().getExternalContext().getRequestParameterMap();
		decode( (AttributeLov) component, parameters );
	}
	
	public void decode (AttributeLov component, Map<String,String> parameters){
		if (parameters.containsKey( component.getClientId() )){
			String value = parameters.get( component.getClientId() );
            if (!component.isDisabled()){
            	component.setSubmittedValue( value );
            }
		}
		
	}
	
}
