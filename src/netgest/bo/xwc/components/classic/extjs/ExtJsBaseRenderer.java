package netgest.bo.xwc.components.classic.extjs;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public abstract class ExtJsBaseRenderer extends XUIRenderer implements ExtJsRenderer {

	public abstract String getExtComponentType( XUIComponentBase oComp );
	
	public String getExtComponentId( XUIComponentBase oComp ) {
		return "ext-" + oComp.getClientId();
	}
	
    public void encodeEnd(XUIComponentBase oComp) throws IOException {
    	
    	// Write the field
    	if( !oComp.isRenderedOnClient() || reRenderField( oComp ) ) {
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
    	}
    	else {
    		oComp.setDestroyOnClient( false );
    	}
    	
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
    	s.w( "var c = " ).w( "Ext.ComponentMgr.get('" ).w( getExtComponentId( oAttr ) ).w( "')" ).endStatement();
    }
}
