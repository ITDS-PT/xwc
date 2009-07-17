package netgest.bo.xwc.components.model;

import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class RequestAction extends XUIComponentBase {

	private XUIBaseProperty<String> requestParameter = new XUIBaseProperty<String>( "requestParameter", this );

	public String getRequestParameter() {
		return requestParameter.getValue();
	}
	
	public void setRequestParameter( String requestParameter ) {
		this.requestParameter.setValue( requestParameter ); 
	}

	@Override
	public void initComponent() {
		// TODO Auto-generated method stub
		String parName = getRequestParameter();
		if( parName != null ) {
			Map<String,String> requestMap = XUIRequestContext.getCurrentContext().getRequestParameterMap();
			if( requestMap.containsKey( parName ) ) {
				
				String kValue = requestMap.get( parName );
				
				if( kValue != null ) {
				
					Iterator<UIComponent> it = getChildren().iterator();
					
					for (; it.hasNext(); ) {
						Object type = it.next();
						if( type instanceof RequestActionValue ) {
							RequestActionValue oActionValue = (RequestActionValue)type;
							if ( kValue.equalsIgnoreCase( (String)oActionValue.getValue() ) ) {
								ActionEvent e = new ActionEvent( oActionValue );
								oActionValue.queueEvent( e );
								break;
							}
						}
					}
				}
			}
		}
		
		getRequestContext().getViewRoot().processApplication( getFacesContext() );
		
	}
	
}
