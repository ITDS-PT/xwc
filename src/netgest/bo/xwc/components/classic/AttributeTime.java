package netgest.bo.xwc.components.classic;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsFieldRendeder;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * This component renders a combo with day hour's. It can be bind to a {@link DataFieldConnector} and
 * the value is java.sql.Time. Ignores the day and years of the Timestamp 
 * @author jcarreira
 *
 */
public class AttributeTime extends AttributeBase {
    private static final SimpleDateFormat oTimeFormat       = new SimpleDateFormat( "HH:mm" );

	public static class XEOHTMLRenderer extends ExtJsFieldRendeder {

		@Override
		public String getExtComponentType(XUIComponentBase oComp) {
			return "Ext.form.TimeField";
		}
		
		@Override
		public ExtConfig getExtJsFieldConfig(AttributeBase oAttr) {
	        ExtConfig oInpTimeConfig = super.getExtJsFieldConfig( oAttr );
	        oInpTimeConfig.addJSString("format", "H:i");
	        oInpTimeConfig.add("width", 95 );
            oInpTimeConfig.addJSString("value", formatValue( oAttr ) );
            if( oAttr.isReadOnly() ) {
            	oInpTimeConfig.add("readOnly", true );
            	oInpTimeConfig.add("hideTrigger", true );
            	oInpTimeConfig.add("disabled", true );
            }
	        return oInpTimeConfig;
		}
		
		@Override
		public ScriptBuilder getEndComponentScript(AttributeBase oComp) {
			
			ScriptBuilder sb = new ScriptBuilder();
			sb.startBlock();
			
			super.writeExtContextVar(sb, oComp);
			
			if( oComp.isRenderedOnClient() ) {
				String s = formatValue( oComp );
				sb.w( "c.setValue('" ).writeValue( s ).s("')");
	            
	        	if( oComp.getStateProperty("readOnly").wasChanged() ) { 
	        		sb.w("c.setDisabled(").w( oComp.isReadOnly() ).w(")").endStatement();
	        		sb.w("c.trigger.setDisplayed(").w( !oComp.isReadOnly() ).w(")").endStatement();
	        	}
			}
			
			sb.endBlock();
			sb.w(super.getEndComponentScript( oComp, true, false ));
			return sb;
		}

			String	jsValue;
		private String formatValue( AttributeBase attBase ) {
			Object 	oValue;
            oValue = attBase.getValue();
            if( oValue != null ) {
	            if( oValue instanceof Timestamp ) {
	            	jsValue = JavaScriptUtils.writeValue( oTimeFormat.format( oValue ) );
	            }
	            else {
	            	jsValue = JavaScriptUtils.writeValue( oValue );
	            }
	        }
            else {
            	jsValue = "";
            }
            return jsValue;
		}
		
		@Override
		public void decode(XUIComponentBase component) {
			AttributeTime oAttrComp;
			
			super.decode(component);
			
            oAttrComp = (AttributeTime)component;
            
            if( !oAttrComp.isDisabled() && !oAttrComp.isReadOnly() && oAttrComp.isVisible() ) {
	            // To avoid multiple inputs to the same value...
	            if( oAttrComp.getSubmittedValue() == null ) {
	                Map<String,String> reqMap = getFacesContext().getExternalContext().getRequestParameterMap();
	                if( reqMap.containsKey( oAttrComp.getClientId() ) ) {
		                oAttrComp.setSubmittedValue( reqMap.get( oAttrComp.getClientId() ) );
	                }
	            }
            }
		}
	}
	
}
