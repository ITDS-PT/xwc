package netgest.bo.xwc.components.classic.extjs;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.List;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;

import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIELContextWrapper;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;

public abstract class ExtJsBaseRenderer extends XUIRenderer implements ExtJsRenderer {

	public abstract String getExtComponentType( XUIComponentBase oComp );
	
	public String getExtComponentId( XUIComponentBase oComp ) {
		return "ext-" + oComp.getClientId();
	}
	
	@Override
	public void encodeComponentChanges( XUIComponentBase oComp, List<XUIBaseProperty<?>> propertiesWithChangedState )
			throws IOException {
		
		// Write Scripts
    	encodeComponentScript( oComp );
		
	}
	
	
	 @Override
	    public StateChanged wasStateChanged(XUIComponentBase component, List<XUIBaseProperty<?>> changedProperties) {
		 
		 	XUIBaseProperty<?> readOnly = component.getStateProperty( "readOnly" );
		 	if (readOnly != null){
		 			if (readOnly.wasChanged())
		 				return StateChanged.FOR_RENDER; 
		 	}
		 
		 	AttributeBase base = ( AttributeBase ) component;
	        if( super.wasStateChanged(component, changedProperties) == StateChanged.NONE ) {
	        	Object value;
	        	ValueExpression ve = component.getValueExpression("value");
	        	if (ve != null) {
	        	    try {
	        	    	XUIELContextWrapper context = new XUIELContextWrapper( getFacesContext().getELContext() , component );
	        			value = (ve.getValue(context));
	        		}
	    		    catch (ELException e) {
	        			throw new FacesException(e);
	    		    }
	        	}
	        	else {
	        		value = base.getValue();
	        	}
	        	
	            if (!XUIStateProperty.compareValues( base.getRenderedValue(), value )) {
	                return StateChanged.FOR_UPDATE;
	            }
	        }
	        else {
	            return StateChanged.FOR_UPDATE;
	        }
	        return StateChanged.NONE;
	    }
	    
	
    public void encodeEnd(XUIComponentBase oComp) throws IOException {
    	
    	// Write the field
		oComp.setDestroyOnClient( true );
    	// Create a place holder for the field!
    	encodePlaceHolder( oComp );

    	if( oComp instanceof SecurableComponent ) {
    		if (!((SecurableComponent)oComp).getEffectivePermission(SecurityPermissions.READ) ) {
    			return;
    		}
    	}
    	// If the user cannot read the field... only render of the place holder
    	encodeExtJs( oComp );
    	
    	
    	// Write Scripts
    	encodeComponentScript( oComp );
    	
    }
    
    public boolean reRenderField( XUIComponentBase comp ) {
    	return false;
    }
    
	public void encodeExtJs( XUIComponentBase oAtt ) {
		ExtConfig 		config;
		config = getExtJsConfig( oAtt );
    	getResponseWriter().getScriptContext().add(
    			XUIScriptContext.POSITION_FOOTER, 
    			oAtt.getClientId() + "_ext", 
    			config.renderExtConfig()
    	);
	}
	
    @Override
    public ExtConfig getExtJsConfig(XUIComponentBase oComp) {
    	
    	ExtConfig config = new ExtConfig( getExtComponentType( oComp ) );
    	
    	config.addJSString( "id" , getExtComponentId( oComp ) );
    	config.addJSString( "renderTo" , oComp.getClientId() );
    	
    	return config;
    }
    
	public void encodeBeginPlaceHolder( XUIComponentBase oAtt ) throws IOException {
		XUIResponseWriter w = getResponseWriter();
        w.startElement( DIV, oAtt );
        w.writeAttribute( ID, oAtt.getClientId(), null );
	}
	
	public void encodeEndPlaceHolder(  XUIComponentBase oAtt ) throws IOException {
		XUIResponseWriter w = getResponseWriter();
        w.endElement( DIV );
	}
	
	public void encodePlaceHolder( XUIComponentBase oAtt ) throws IOException {
		encodeBeginPlaceHolder( oAtt );
		encodeEndPlaceHolder( oAtt );
	}
	
    public void encodeComponentScript( XUIComponentBase oComp ) {
    	encodeBeginComponentScript(oComp);
    	encodeEndComponentScript(oComp);
    }
    
    public void encodeBeginComponentScript( XUIComponentBase oComp ) {
    	ScriptBuilder scriptBuilder = getBeginComponentScript(oComp);
    	if( scriptBuilder != null ) {
	    	getResponseWriter().getScriptContext().add(
	    			XUIScriptContext.POSITION_HEADER, 
	    			oComp.getClientId() + "_scrph", 
	    			scriptBuilder
	    	);
    	}
    }

    public void encodeEndComponentScript( XUIComponentBase oComp ) {
    	ScriptBuilder scriptBuilder = getEndComponentScript(oComp);
    	if( scriptBuilder != null ) {
	    	getResponseWriter().getScriptContext().add(
	    			XUIScriptContext.POSITION_FOOTER, 
	    			oComp.getClientId() + "_scrpf", 
	    			scriptBuilder
	    	);
    	}
    }
	
    public ScriptBuilder getBeginComponentScript( XUIComponentBase oComp ) {
    	return null;
    }
    
    public ScriptBuilder getEndComponentScript( XUIComponentBase oComp ) {
    	return null;
    }
    
    public void writeExtContextVar( ScriptBuilder s, XUIComponentBase oAttr ) {
    	s.w( "var c = " ).w( "Ext.getCmp('" ).w( getExtComponentId( oAttr ) ).w( "')" ).endStatement();
    }
}
