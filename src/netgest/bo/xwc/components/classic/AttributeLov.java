package netgest.bo.xwc.components.classic;
import java.util.Map;
import java.util.Map.Entry;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.components.XUIComponentBase;
/*
 * This component renders a combobox base on a Map
 */
public class AttributeLov extends AttributeBase {

    @Override
	public boolean isRenderedOnClient() {
		return false;
	}

	public static class XEOHTMLRenderer extends ExtJsFieldRendeder {

    	@Override
    	public String getExtComponentType( XUIComponentBase oComp ) {
    		return "Ext.form.ComboBox";
    	}
    	
    	@Override
        public ExtConfig getExtJsFieldConfig( AttributeBase oComp ) {
            AttributeLov        oAtt;

            oAtt = (AttributeLov)oComp;
            
            ExtConfig oInpConfig = super.getExtJsFieldConfig( oAtt );
            oInpConfig.add("typeAhead", "true");
            oInpConfig.addString("triggerAction", "all");
            oInpConfig.addString("forceSelection", "true" );
            if( oAtt.isReadOnly() ) {
            	oInpConfig.add("triggerAction", "''" ); 
            }
            oInpConfig.add("store", getLovStore(oAtt) );
            oInpConfig.addString("mode", "local" );
            oInpConfig.addString("displayField", "d" );
            oInpConfig.addString("valueField", "i" );
            oInpConfig.addString("hiddenName", oAtt.getClientId() + "_h" );
            
            // Override super properties..
            oInpConfig.addString( "value" , JavaScriptUtils.writeValue( oComp.getValue()) );
            oInpConfig.add( "maxLength" , 255 );
            
            super.addValidator( oInpConfig );
            
            return oInpConfig;
        }

    	
    	public ScriptBuilder getLovStore( AttributeLov lov ) {
    		ScriptBuilder valuesStore = new ScriptBuilder();
    		valuesStore.w( "new Ext.data.SimpleStore(");
    		
    		ExtConfig valuesConfig = new ExtConfig();
    		ExtConfigArray fields = valuesConfig.addChildArray( "fields");
    		fields.addString( "i" );
    		fields.addString( "d" );
    		valuesConfig.add( "data", getLovStoreData( lov.getLovMap() ) );
    		valuesStore.w( valuesConfig.renderExtConfig() );
    		valuesStore.w( ")" );
    		
    		return valuesStore;
    	}	
    	
    	@Override
    	public ScriptBuilder getEndComponentScript(AttributeBase oComp) {
    		
    		
    		ScriptBuilder s = new ScriptBuilder(); 
    		
    		Map<Object,String> map = oComp.getLovMap();
    		
    		String jsValue = JavaScriptUtils.writeValue( oComp.getValue() );

    		if( jsValue.length() == 0 && map.size() > 0 ) {
    			jsValue = JavaScriptUtils.writeValue( map.keySet().iterator().next() );
    		}
    		if( oComp.isRenderedOnClient() ) {
        		s.startBlock();
        		writeExtContextVar(s, oComp);
	    		s.l( "c.getStore().loadData(" );
	    		s.l( getLovStoreData( oComp.getLovMap() ) );
	    		s.l( ");" );
	    		s.w( "c.setValue('" ).writeValue( jsValue ).l("');");
	    		s.endBlock();
    		}
    		s.w(super.getEndComponentScript( oComp, true, false ) );
    		return s;
    	}
    	
    	public ScriptBuilder getLovStoreData( Map<Object,String> lovMap ) {
    		ScriptBuilder s = new ScriptBuilder();
    		boolean first = true;
    		s.w("[");
    		for( Entry<Object,String> entry : lovMap.entrySet() ) {
    			if( !first ) {
    				s.w(",");
    			}
    			String sdValue = entry.getValue();
    			if( sdValue == null || sdValue.length() == 0 ) {
    				sdValue = "";
    			}
    			
    			s.w("['").writeValue( entry.getKey() ).w("','").writeValue( sdValue ).w("']");
    			first = false;
    		}
    		s.w("]");
    		
    		return s;
    	}
    	
    	
    	@Override
    	public ExtConfig getExtJsFieldListeners(AttributeBase oAtt) {
    		ExtConfig listeners = null;
    		
            String              sFormId;
            Form                oForm;
            
            sFormId = oAtt.getNamingContainerId();
            oForm   = (Form)oAtt.findComponent( sFormId );
            if( oForm.haveDependents( oAtt.getObjectAttribute() ) || oAtt.isOnChangeSubmit()  ) {
            	listeners = new ExtConfig();
            	listeners.add( "'select'" , 
            			"function(fld,newValue,oldValue){\n" +
            			 XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oAtt.getParent(), 0 ) + "}"
            	);
            }
            
            return listeners;
    	}

        @Override
        public void decode(XUIComponentBase component) {

            AttributeLov oAttrComp;
            
            oAttrComp = (AttributeLov)component;
            
            String value = getFacesContext().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() + "_h" );
            oAttrComp.setSubmittedValue( value );
            
            super.decode(component);

        }
    }


}
