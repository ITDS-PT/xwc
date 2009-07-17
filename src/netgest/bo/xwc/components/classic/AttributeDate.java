package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class AttributeDate extends AttributeBase {

    protected static final SimpleDateFormat oFormatDate = new SimpleDateFormat( "dd/MM/yyyy" );


    @Override
    public void initComponent() {

        super.initComponent();
        
        // Overwrite datatype;
        this.setValueExpression(
            "value", createValueExpression( getValueExpression( "value" ).getExpressionString(), Timestamp.class ) 
        );

    }
    
    public void validate( FacesContext context ) {
        String      sSubmitedValue = null;
        Object oSubmitedValue = getSubmittedValue();
        Date   oSubmitedDate;
        
        if( oSubmitedValue != null )
        {
            sSubmitedValue = (String)oSubmitedValue;     
            if( sSubmitedValue.length() > 0 )
            {
                try {
                    oSubmitedDate = oFormatDate.parse( String.valueOf( oSubmitedValue ) );
                    setValue( new Timestamp( oSubmitedDate.getTime() ) );
    
                }
                catch( ParseException ex ) {
                    getRequestContext().addMessage( getClientId(), new XUIMessage(
                                                                        XUIMessage.TYPE_MESSAGE,
                                                                        XUIMessage.SEVERITY_ERROR,
                                                                        getLabel(),
                                                                        ComponentMessages.VALUE_ERROR_ON_FORMAT.toString( oSubmitedValue )
                                                                   )
                                                    );
                    setValid( false );
                }
            }
            else {
                setValue( null );
            }
        }
    }

    @Override
    public boolean wasStateChanged() {
        return super.wasStateChanged();
    }

    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            
            XUIResponseWriter w = getResponseWriter();
            
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

        private String renderExtJs( XUIComponentBase oComp ) {
            AttributeDate       oAttrText;
            Timestamp           sJsValue;
            StringBuilder    	sOut;
            String              sFormId;
            Form                oForm;
            
            sFormId = oComp.getNamingContainerId();
            oForm   = (Form)oComp.findComponent( sFormId );

            sOut = new StringBuilder( 100 );

            oAttrText = (AttributeDate)oComp; 
            
            if ( !oAttrText.getEffectivePermission(SecurityPermissions.READ) ) {
            	// Without permissions do not render the field
            	return "";
            }

            sJsValue = (Timestamp)oAttrText.getValue(); 
            String sValue = "";
            
            if( sJsValue != null )  {
        	  	sValue = oFormatDate.format( sJsValue );
          	}
            
            ExtConfig oInpDateConfig = new ExtConfig("Ext.form.DateField");
            oInpDateConfig.addJSString("renderTo", oComp.getClientId());
            oInpDateConfig.addJSString("id", oComp.getClientId() + "_c" );
            oInpDateConfig.addJSString("format", "d/m/Y");
            
            oInpDateConfig.add("width", oAttrText.getWidth() );
//            oInpDateConfig.add("maxLength", oAttrText.getMaxLength() );

            if( !oAttrText.isVisible() )
            	oInpDateConfig.add("hidden", true );

            if( oAttrText.isDisabled() || !oAttrText.getEffectivePermission(SecurityPermissions.WRITE) ) {
            	oInpDateConfig.add("disabled", true );
            	oInpDateConfig.addJSString("name", "" );
            } 
            else {
            	oInpDateConfig.addJSString("name", oAttrText.getClientId() );
            }
            oInpDateConfig.addJSString("value", sValue );

            if( oForm.haveDependents( oAttrText.getObjectAttribute() ) || oAttrText.isOnChangeSubmit() ) {
            	ExtConfig listeners = oInpDateConfig.addChild("listeners");
            	listeners.add( "'change'" , 
            			"function(fld,newValue,oldValue){fld.setValue(newValue);" +
            			 XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}"
            	);
            }
            
            oInpDateConfig.renderExtConfig( sOut );
            
            
//            sOut.write( "Ext.onReady( function() { " ); sOut.write("\n");
//            sOut.write( "var " + oComp.getId() + " = new ({"); sOut.write("\n");
//            sOut.write( "renderTo: '" ); sOut.write( oComp.getClientId() ); sOut.write("',");
//            sOut.write( "format: 'd/m/Y',");
//            sOut.write( "width: "+oAttrText.getWidth()+",");
//
//            if( oForm.haveDependents( oAttrText.getObjectAttribute() ) )
//                sOut.write( "listeners : { change: function(fld,newValue,oldValue){fld.setValue(newValue);" 
//                            + XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "} },"
//                        );
//            /*
//            sOut.write( "maxLength: '"+oAttrText.getMaxLength()+"',");
//            if( !oAttrText.isVisible() )
//                sOut.write( "hidden: true,");
//            */
//            
//            if( oAttrText.isDisabled() )
//                sOut.write( "disabled: true,");
//    
//            sOut.write( "name: '"); sOut.write( oComp.getClientId() ); sOut.write("',");
//            
//            // Write value            
//            sOut.write( "value: '"); 
//            if( sJsValue != null )  {
//                String sValue = oFormatDate.format( sJsValue );
//                sOut.write( sValue ); 
//            }
//                
//            sOut.write("'");
//                
//            sOut.write("});");
//            sOut.write("});");
            
            return sOut.toString();
        }


        @Override
        public void decode(XUIComponentBase component) {

            AttributeDate oAttrComp;
            oAttrComp = (AttributeDate)component;
            Map<String,String> reqMap = getFacesContext().getExternalContext().getRequestParameterMap();
            if( oAttrComp.getSubmittedValue() == null && reqMap.containsKey( oAttrComp.getClientId() ) ) {
                String value = reqMap.get( oAttrComp.getClientId() );
                oAttrComp.setSubmittedValue( value );
            } 
        }
    }

}
