package netgest.bo.xwc.components.classic;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class AttributePassword extends AttributeBase {

    public static class XEOHTMLRenderer extends AttributeText.XEOHTMLRenderer {

        @Override
        public void decode(XUIComponentBase component) {
            AttributePassword oAttrComp;
            oAttrComp = (AttributePassword)component;
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
            oAttrComp.setSubmittedValue( value );
            super.decode(component);

        }

		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
			return "Ext.form.TextField";
		}
		
		@Override
		public ExtConfig getExtJsFieldConfig( AttributeBase oComp ) {
			AttributeBase oAtt;
			oAtt = oComp;
			
			ExtConfig config = super.getExtJsFieldConfig( oAtt );
			config.addString( "inputType","password" );
			config.add( "enableKeyEvents" , true );
			return config;
		}
		
		
		@Override 
		public ExtConfig getExtJsFieldListeners( AttributeBase oAttr ) {
			ExtConfig listeners;
			
			listeners = super.getExtJsFieldListeners( oAttr );
			ScriptBuilder s = new ScriptBuilder();
			s.l( "function(fld,event) {" )
			.s( "	var fldVal = new String(fld.getValue())" )
			.l( "		if(  ' '.indexOf(String.fromCharCode(event.keyCode))>-1 ) {")
			.l("			event.stopEvent(); ")
			.l("		}")
			.l("	if( fldVal.length >= fld.maxLength ) {" +
        			"		if(event.keyCode >= 32 && '33,34,35,36,37,38,39,40,45,46'.indexOf(''+event.keyCode) == -1 ) {" +
        			"			event.stopEvent();" +
        			"		}" +
        			"	}") 
			.l("}");
            listeners.add( "'keydown'" , s );
            return listeners;
			
		}
    }

}
