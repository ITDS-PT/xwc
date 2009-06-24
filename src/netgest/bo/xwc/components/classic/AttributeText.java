package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class AttributeText extends AttributeBase {

    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            AttributeText       oAttrText;

            oAttrText = (AttributeText)oComp; 
            
            XUIResponseWriter w = getResponseWriter();
            /*
            w.startElement( INPUT, oComp );
            w.writeAttribute( HTMLAttr.STYLE, "width:" + oAttrText.getWidth() + "px", null );
            w.writeAttribute( CLASS, "x-form-text x-form-field", null);
            w.writeAttribute( ID, oComp.getClientId(), null );
            w.writeAttribute( NAME, oComp.getClientId(), null );
            w.writeAttribute( VALUE, oAttrText.getValue(), "value" );
            w.endElement( INPUT );
            */


            // Place holder for the component
            w.startElement( DIV, oComp );
            w.writeAttribute( ID, oComp.getClientId(), null );
            //w.writeAttribute( HTMLAttr.STYLE, "width:120px", null );
            w.endElement( DIV ); 

            w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
                oComp.getId(),
                renderExtJs( oComp )
            );

        }

        public String renderExtJs( XUIComponentBase oComp ) {
            AttributeText       oAttrText;
            String              sJsValue;
//            FastStringWriter    sOut;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

//            sOut = new FastStringWriter( 250 );

            oAttrText = (AttributeText)oComp; 
            
            if( !oAttrText.getEffectivePermission(SecurityPermissions.READ) ) {
            	return "";            	
            }
            
            sJsValue = (String)oAttrText.getValue(); 
            
            
//            if( !isAjax() )
//                sOut.write( "Ext.onReady( function() { " ); sOut.write("\n");
                                                            
            //sOut.write( "var " + oComp.getId() + " =" +
            
            ExtConfig textConfig = new ExtConfig( "Ext.form.TextField" );
            textConfig.addJSString( "renderTo" , oComp.getClientId() );
            textConfig.addJSString( "width" , oAttrText.getWidth() );
            textConfig.add( "enableKeyEvents" , true );
            
                
            //sOut.write( 		" new Ext.form.TextField({"); sOut.write("\n");
            //sOut.write( "renderTo: '" ); sOut.write( oComp.getClientId() ); sOut.write("',");
            //sOut.write( "width: "+oAttrText.getWidth()+",");
            
            
            
        	ExtConfig textListeners = textConfig.addChild("listeners");
            textListeners.add( "keydown" , 
        			"function(fld,e) { " +
        			"	var fldVal = new String(fld.getValue());" +
        			"	if( fldVal.length >= fld.maxLength && event.keyCode >= 32 ){" +
        			"			event.returnValue=false; " +
        			"	}" +
        			"}"
            );

            if( oForm.haveDependents( oAttrText.getObjectAttribute() ) || oAttrText.isOnChangeSubmit()  ) {
            	textListeners.add( "change" , "function(fld,newValue,oldValue){fld.setValue(newValue);" 
                + XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}" );
            	
//                sOut.write( "listeners : { change: function(fld,newValue,oldValue){fld.setValue(newValue);" 
//                            + XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "} },"
//                        );
            }
            textConfig.add( "maxLength", oAttrText.getMaxLength());
            
            //sOut.write( "maxLength: '"+oAttrText.getMaxLength()+"',");
            if( !oAttrText.isVisible() ) {
                textConfig.add( "hidden", true );
                //sOut.write( "hidden: true,");
            }
            if( oAttrText.isDisabled() || !oAttrText.getEffectivePermission(SecurityPermissions.WRITE) ) {
                textConfig.add( "disabled", true );
//                sOut.write( "disabled: true,");
            }
//            sOut.write( "name: '"); sOut.write( oComp.getClientId() ); sOut.write("',");
            textConfig.addJSString( "name",  oComp.getClientId() );
            
            // Write value            
//            sOut.write( "value: '"); 
            textConfig.addJSString( "value",  sJsValue );
//            sOut.write("'");
                
//            sOut.write("});");
    
//            if( !isAjax() )
//                sOut.write("});");
            
            StringBuilder sOut = new StringBuilder( 150 );
            textConfig.renderExtConfig( sOut );
            
            return sOut.toString();
        }


        @Override
        public void decode(XUIComponentBase component) {

            AttributeText oAttrComp;
            
            oAttrComp = (AttributeText)component;
            
            String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get( oAttrComp.getClientId() );
            oAttrComp.setSubmittedValue( value );
            
            super.decode(component);

        }
    }
}
