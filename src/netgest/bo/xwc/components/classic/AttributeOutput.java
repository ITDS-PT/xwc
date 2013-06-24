package netgest.bo.xwc.components.classic;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsBaseRenderer;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
/**
 * This component outputs data from a {@link DataFieldConnector}
 * 
 * @author jcarreira
 *
 */
public class AttributeOutput extends AttributeBase {
	
    public static class XEOHTMLRenderer extends ExtJsBaseRenderer {

    	
    	@Override
    	public String getExtComponentType( XUIComponentBase oComp ) {
    		return "Ext.form.Label";
    	}
    	
    	@Override
    	public void encodeBeginPlaceHolder(XUIComponentBase oAtt) throws IOException {
            
    		super.encodeBeginPlaceHolder(oAtt);
    		
    		XUIResponseWriter w = getResponseWriter();
            w.writeAttribute( HTMLAttr.CLASS, "", null );
            w.writeAttribute( HTMLAttr.STYLE, "width:100%", null );
            
    	}
    	
    	@Override
    	public ExtConfig getExtJsConfig(XUIComponentBase oComp) {
    		
    		ExtConfig config = super.getExtJsConfig(oComp);
    		
    		AttributeOutput oAtt = (AttributeOutput)oComp;

    		if( !oAtt.isVisible() )
                config.add( "hidden", true ); 
            if( oAtt.isDisabled() )
                config.add( "disabled", true ); 
            
            config.addString( "text", JavaScriptUtils.writeValue( oAtt.getValue() ) ); 
    		return config;
    	}
    	
		@Override
		public ScriptBuilder getEndComponentScript(XUIComponentBase oComp) {
			ScriptBuilder s = null;
			if( oComp.isRenderedOnClient() ) {
				AttributeOutput oAttr = (AttributeOutput)oComp;
	
				s = new ScriptBuilder();
				s.startBlock();
				super.writeExtContextVar(s, oComp);
			
				s.w( "c.setText('" ).writeValue( oAttr.getValue() ).l("');");

				if( oComp.getStateProperty("visible").wasChanged() )
					s.w( "c.setVisible('" ).writeValue( oAttr.isVisible() ).l("');");

				if( oComp.getStateProperty("disabled").wasChanged() )
					s.w( "c.setDisabled('" ).writeValue( oAttr.isDisabled() ).l("');");
					
				s.endBlock();
			}
			return s;
		}
    }
}
