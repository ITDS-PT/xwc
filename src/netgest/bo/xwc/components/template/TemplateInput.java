package netgest.bo.xwc.components.template;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.framework.components.XUIInput;

public class TemplateInput extends XUIInput {
	
	@Override
	public void decode() {
		HttpServletRequest request = (HttpServletRequest) getRequestContext( ).getRequest( ); 
		String value =  request.getParameter( getClientId( ) );
		setSubmittedValue( value );
		
	}
	
	

	
}
