package netgest.bo.xwc.components.classic.extjs;

import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.Form;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public abstract class ExtJsFieldRendeder extends ExtJsBaseRenderer implements ExtJsRenderer {
	
	public String getExtComponentId( XUIComponentBase oComp ) {
		return "ext-" + oComp.getClientId();
	}
	
    @Override
    public ExtConfig getExtJsConfig(XUIComponentBase oComp) {
    	
    	AttributeBase oAtt = (AttributeBase)oComp;
    	
    	ExtConfig cfg 			= getExtJsFieldConfig( oAtt );
    	ExtConfig listeners 	= getExtJsFieldListeners( oAtt );
    	if( listeners != null ) {
    		cfg.add( "listeners" , listeners );
    	}
    	
    	return cfg;
    	
    }
    
	public void encodeJsField( AttributeBase oAtt ) {
		ExtConfig 		config;
		ExtConfig		configListeners;
		
		config = getExtJsFieldConfig( oAtt );
		
		configListeners = getExtJsFieldListeners( oAtt );
		
		if( configListeners != null ) {
			config.add( "listeners", configListeners );
		}
		
    	getResponseWriter().getScriptContext().add(
    			XUIScriptContext.POSITION_FOOTER, 
    			oAtt.getClientId() + "_ext", 
    			config.renderExtConfig()
    	);
	}
	
	
    public ExtConfig getExtJsFieldConfig(AttributeBase oAttr) {
        ExtConfig			extConfig;
        
    	extConfig = super.getExtJsConfig(oAttr);
    	
        extConfig.addJSString( "name",  oAttr.getClientId() );
        extConfig.addJSString( "value", oAttr.getDisplayValue() );

        extConfig.add( "maxLength", oAttr.getMaxLength());

        if( !oAttr.isVisible() ) {
        	extConfig.add( "hidden", true );
        }
        if( oAttr.isDisabled() || !oAttr.getEffectivePermission(SecurityPermissions.WRITE) ) {
        	extConfig.add( "disabled", true );
        }
        if( oAttr.isReadOnly() ) {
        	extConfig.add( "readOnly", true );
        }
        
    	return extConfig;
    }
	
    public ExtConfig getExtJsFieldListeners( AttributeBase oAtt ) {
    	String				sFormId;
        Form                oForm;
        
        sFormId = oAtt.getNamingContainerId();
        oForm   = (Form)oAtt.findComponent( sFormId );

        ExtConfig listeners = new ExtConfig();
        if( oForm.haveDependents( oAtt.getObjectAttribute() ) || oAtt.isOnChangeSubmit()  ) {
        	listeners.add( "change" , "function(fld,newValue,oldValue){fld.setValue(newValue);" 
            + XVWScripts.getAjaxUpdateValuesScript( oAtt, 0 ) + "}" );
        }
        return listeners; 
    }
    
    @Override
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

    @Override
    public void encodeEndComponentScript( XUIComponentBase oComp ) {
    	ScriptBuilder scriptBuilder = getEndComponentScript( (AttributeBase)oComp);
    	if( scriptBuilder != null ) {
	    	getResponseWriter().getScriptContext().add(
	    			XUIScriptContext.POSITION_FOOTER, 
	    			oComp.getClientId() + "_scrpf", 
	    			scriptBuilder
	    	);
    	}
    }
	
    @Override
    public ScriptBuilder getBeginComponentScript( XUIComponentBase oComp ) {
    	return getBeginComponentScript( (AttributeBase)oComp, true );
    }
    
    public ScriptBuilder getBeginComponentScript( AttributeBase oComp,  boolean closeBlock ) {
    	return null;
    }
    
    public ScriptBuilder getEndComponentScript( AttributeBase oComp ) {
    	return getEndComponentScript( oComp, true, true );
    }

    public ScriptBuilder getEndComponentScript( AttributeBase oComp,  boolean closeBlock, boolean renderValue ) {
		ScriptBuilder s = new ScriptBuilder();
    	s.startBlock();
    	
    	writeExtContextVar(s, oComp);
    	
    	if( oComp.isRenderedOnClient() ) {

    		if( renderValue && oComp.getStateProperty("displayValue").wasChanged() )
        		s.w("c.setRawValue('").writeValue( oComp.getDisplayValue() ).w("')").endStatement();

        	if( oComp.getStateProperty("visible").wasChanged() )
        		s.w("c.setVisible('").writeValue( oComp.isVisible() ).w("')").endStatement();
        		
        	if( oComp.getStateProperty("disabled").wasChanged() )
        		s.w("c.setDisabled('").writeValue( oComp.isDisabled() ).w("')").endStatement();
    	}
    	
        String invalidText = oComp.getInvalidText();
        if ( invalidText != null && invalidText.length() > 0 ) {
        	s.w("c.markInvalid('" ).writeValue( invalidText ).w("')").endStatement();
        }
        if( closeBlock ) {
        	s.endBlock();
        }
        
        return s;
    }
    
    public void writeExtContextVar( ScriptBuilder s, AttributeBase oAttr ) {
    	s.w( "var c = " ).w( "Ext.ComponentMgr.get('" ).w( getExtComponentId( oAttr ) ).w( "')" ).endStatement();
    }
    

}
