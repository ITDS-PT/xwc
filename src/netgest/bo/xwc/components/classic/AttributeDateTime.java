package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.DIV;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class AttributeDateTime extends AttributeDate {

    private static final SimpleDateFormat oTimeFormat       = new SimpleDateFormat( "HH:mm" );
    private static final SimpleDateFormat oDateTimeFormat   = new SimpleDateFormat( "dd/MM/yyyy HH:mm" );
    private static final SimpleDateFormat oDateFormat   = new SimpleDateFormat( "dd/MM/yyyy" );

    @Override
    public void initComponent() {
        
        super.initComponent();

    }
    
    
    

    @Override
    public void validate(FacesContext context) {
        Object oSubmitedValue = getSubmittedValue();
        Date   oSubmitedDate;
        String sSubmitedValue;
        if( oSubmitedValue instanceof String )  
        {
            try {
                sSubmitedValue = ((String)oSubmitedValue).trim();
                if(  sSubmitedValue.length() > 0 )
                {
                    oSubmitedDate = oDateTimeFormat.parse( String.valueOf( oSubmitedValue ) );
                    setValue( new Timestamp( oSubmitedDate.getTime() ) );
                    setValid(true);
                }
                else {
                    setValue( null );
                }
            }
            catch( ParseException ex ) {
            	try {
	                final SimpleDateFormat oDateFormat   = new SimpleDateFormat( "dd/MM/yyyy" );
	                oSubmitedDate = oDateFormat.parse( String.valueOf( oSubmitedValue ) );
	                setValue( new Timestamp( oSubmitedDate.getTime() ) );
	                setValid(true);
            	}
	            catch( ParseException e ) {
	            	getRequestContext().addMessage( getClientId(), new XUIMessage(
	                                                                    XUIMessage.TYPE_MESSAGE,
	                                                                    XUIMessage.SEVERITY_ERROR,
	                                                                    getLabel(),
	                                                                    oSubmitedValue + " não está no formato correcto "
	                                                               )
	                                                );
	                setValid( false );
	            }
            }
        }
    }

    @Override
    public String getWidth() {
        return "200";
    }


    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
            XUIResponseWriter w = getResponseWriter();

            w.startElement( TABLE, oComp );
            w.writeAttribute( HTMLAttr.ID , oComp.getClientId() , null );
            w.writeAttribute( CELLPADDING, "0", null );
            w.writeAttribute( CELLSPACING, "0", null );
            w.writeAttribute( HTMLAttr.STYLE, "table-layout:fixed;width:100%", null ); 
            w.startElement("COLGROUP", oComp);
            w.startElement(COL, oComp );
            w.writeAttribute( HTMLAttr.WIDTH, "65%", null );
            w.endElement("COL");
            w.startElement(COL, oComp );
            w.writeAttribute( HTMLAttr.WIDTH, "35%", null );
            w.endElement("COL");
            w.endElement("COLGROUP");
            w.startElement( TR, oComp );

            
        	w.startElement( TD, oComp );
            w.writeAttribute( "id" , oComp.getClientId() + "_d" , null );
            w.endElement( TD );
            w.startElement( TD, oComp );
            w.writeAttribute( "id" , oComp.getClientId() + "_t" , null );
            w.endElement( TD );

            w.endElement( TR );
            w.endElement( TABLE );
                
            w.getScriptContext().addInclude( XUIScriptContext.POSITION_HEADER, "xwc-components.js", "/xwc/ext-xeo/js/xwc-components.js");
            
            w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, 
                    oComp.getId(),
                    renderExtJs( oComp )
                );
            
            

//            w.startElement( TABLE, oComp );
//            w.writeAttribute( HTMLAttr.STYLE, "width:100%", null );
//            w.writeAttribute( HTMLAttr.CELLPADDING, "0", null );
//            w.writeAttribute( HTMLAttr.CELLSPACING, "0", null );
//            w.writeAttribute( ID, oComp.getClientId() + "_ctnt", null );
//            w.startElement( TR, oComp );
//
//            w.startElement( TD, oComp );
//            
//            // Saves the id to super render with other id
//            // Isto é para que este componente controlo o place holder do elemento            
//            //super.encodeEnd( oComp );
//            
//            w.endElement( TD );
//
//            w.startElement( TD, oComp ); 
//            w.writeAttribute( HTMLAttr.STYLE, "width:100%", null );
//			*/
//            // Place holder for the component
//            w.startElement( DIV, oComp );
//            w.writeAttribute( ID, oComp.getClientId(), null );
//            //w.writeAttribute( HTMLAttr.STYLE, "width:120px", null );
//            w.endElement( DIV ); 

            
            
            /*
            w.endElement( TD );

            w.endElement( TR );
            w.endElement( TABLE );
			*/

        }

        private String renderExtJs( XUIComponentBase oComp ) {
            AttributeDate       oAttrText;
            Timestamp           oJsValue;
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

            oJsValue = (Timestamp)oAttrText.getValue(); 
            
            String sValue = "";
            
            ExtConfig oInpDateConfig = new ExtConfig("Ext.form.DateField");
            oInpDateConfig.addJSString("renderTo", oComp.getClientId() + "_d" );
            oInpDateConfig.addJSString("format", "d/m/Y");
            oInpDateConfig.add("width", 95 );
            if( !oAttrText.isVisible() )
            	oInpDateConfig.add("hidden", true );

            if( oAttrText.isDisabled() || !oAttrText.getEffectivePermission(SecurityPermissions.WRITE) ) {
            	oInpDateConfig.add("disabled", true );
            }
            else {
            	oInpDateConfig.addJSString("name", oAttrText.getClientId() + "_d" );
            }

            if( oJsValue != null )  {
                sValue = oDateFormat.format( oJsValue );
                oInpDateConfig.addJSString("value", sValue );
            }
            

            if( oForm.haveDependents( oAttrText.getObjectAttribute() ) || oAttrText.isOnChangeSubmit()  ) {
            	ExtConfig listeners = oInpDateConfig.addChild("listeners");
            	listeners.add( "'change'" , 
            			"function(fld,newValue,oldValue){fld.setValue(newValue);" +
            			 XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}"
            	);
            }

            ExtConfig oInpTimeConfig = new ExtConfig("Ext.form.TimeField");
            oInpTimeConfig.addJSString("renderTo", oComp.getClientId() + "_t" );
            oInpTimeConfig.addJSString("format", "H:i");
            oInpTimeConfig.add("width", 95 );
            if( !oAttrText.isVisible() )
            	oInpTimeConfig.add("hidden", true );

            if( oAttrText.isDisabled() || !oAttrText.getEffectivePermission(SecurityPermissions.WRITE) ) {
            	oInpTimeConfig.add("disabled", true );
            }
            else {
            	oInpTimeConfig.addJSString("name", oAttrText.getClientId() + "_t" );
            } 

            if( oJsValue != null )  {
                sValue = oTimeFormat.format( oJsValue );
                oInpTimeConfig.addJSString("value", sValue );
            }
            

            if( oForm.haveDependents( oAttrText.getObjectAttribute() ) || oAttrText.isOnChangeSubmit()  ) {
            	ExtConfig listeners = oInpTimeConfig.addChild("listeners");
            	listeners.add( "'change'" , 
            			"function(fld,newValue,oldValue){fld.setValue(newValue);" +
            			 XVWScripts.getAjaxUpdateValuesScript( (XUIComponentBase)oComp.getParent(), 0 ) + "}"
            	);
            }

            oInpDateConfig.renderExtConfig( sOut );
            sOut.append(";\n");
            oInpTimeConfig.renderExtConfig( sOut );
            
//            
//            sOut.write( "Ext.onReady( function() { " ); sOut.write("\n");
//            sOut.write( "var " + oComp.getId() + " = new Ext.form.TimeField({"); sOut.write("\n");
//            sOut.write( "renderTo: '" ); sOut.write( oComp.getClientId() ); sOut.write("_t',");
//            sOut.write( "format: 'H:i',");
//            sOut.write( "width: 95,");
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
//            sOut.write( "name: '"); sOut.write( oComp.getClientId() ); sOut.write("_t',");
//            
//            // Write value            
//            sOut.write( "value: '"); 
//            if( oJsValue != null )  {
//                String sValue = oTimeFormat.format( oJsValue );
//                sOut.write( sValue ); 
//            }
//                
//            sOut.write("'");
//                
//            sOut.write("});");
//            sOut.write("});");
//            
            return sOut.toString();
        }


        @Override
        public void decode(XUIComponentBase component) {

            String sDate;
            String sTime;
                
            AttributeDate oAttrComp;
            oAttrComp = (AttributeDate)component;
            
            // To avoid multiple inputs to the same value...
            if( oAttrComp.getSubmittedValue() == null ) {
                Map<String,String> reqMap = getFacesContext().getExternalContext().getRequestParameterMap();
                if( reqMap.containsKey( oAttrComp.getClientId() + "_d" ) ) {
	                sDate = reqMap.get( oAttrComp.getClientId() + "_d" );                
	                sTime = reqMap.get( oAttrComp.getClientId() + "_t" );                
	                
	                if( sTime != null ) {
	                    sDate += " " + sTime;
	                }
	                oAttrComp.setSubmittedValue( sDate );
                }
            }
        }
    }


}
