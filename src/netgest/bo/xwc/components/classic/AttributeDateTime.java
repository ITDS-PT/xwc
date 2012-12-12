package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * This components automatic creates to Child's, on to represent the Date and another the Time
 * 
 * He take control of the decodes, to handle a unique java.sql.Timestamp with date and hour.
 * 
 * He cannot have children
 * 
 * @author jcarreira
 *
 */
public class AttributeDateTime extends AttributeBase {

    private static final SimpleDateFormat oDateTimeFormat   = new SimpleDateFormat( "dd/MM/yyyy HH:mm" );

    @Override
    public void initComponent() {
    	
    	super.initComponent();
    	
    	AttributeDate attDate = new AttributeDate();
    	propagateProperties( attDate );
    	attDate.setId( getId() + "_d" );
    	getChildren().add( attDate );
    	
    	AttributeTime attTime = new AttributeTime();
    	propagateProperties( attTime );
    	attTime.setId( getId() + "_t" );
    	getChildren().add( attTime );
    	
    }
    
    @SuppressWarnings("unchecked")
	private void propagateProperties( AttributeBase attDate ) {
    	Set<Entry<String, XUIBaseProperty<?>>> properties = getStateProperties();
    	for( Entry<String,XUIBaseProperty<?>> entry : properties ) {
    		XUIBaseProperty prop = attDate.getStateProperty( entry.getKey() );
    		if( prop != null ) {
    			prop.setValue( entry.getValue().getValue() );
    		}
    	}
    	
    	ValueExpression val = getValueExpression( "value" );
    	if( val != null ) {
    		attDate.setValueExpression("value", val );
    	}
    	
    }
    
    @Override
    public void processDecodes( FacesContext context ) {
    	decode();
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
	            	getRequestContext().addMessage( getClientId(), 
	            			new XUIMessage(
	                                XUIMessage.TYPE_ALERT,
	                                XUIMessage.SEVERITY_ERROR,
	                                getLabel(),
	                                ComponentMessages.VALUE_ERROR_ON_FORMAT.toString( oSubmitedValue )
                           )
                    );
	                setValid( false );
	            }
            }
        }
        
        
    }

    @Override
    public StateChanged wasStateChanged2() {
    	return StateChanged.NONE;
    }
    
    @Override
    public String getWidth() {
        return "200";
    }


    public static class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
        	
        	XUIComponentBase childComp;
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
            childComp = oComp.findComponent( AttributeDate.class ); 
            childComp.setRenderedOnClient( false );
            childComp.encodeAll();
            w.endElement( TD );
            
            w.startElement( TD, oComp );
            childComp = oComp.findComponent( AttributeTime.class ); 
            childComp.setRenderedOnClient( false );
            childComp.encodeAll();
            w.endElement( TD );
            
            w.endElement( TR );
            w.endElement( TABLE );
                
        }
        
        
        @Override
        public void encodeChildren(XUIComponentBase component) {
        	//
        }
        
        @Override
        public boolean getRendersChildren() {
        	return true;
        }
        
        /**
         * 
         * Decodes the values submitted for the DateTime component
         * 
         * @param component The component
         * @param reqMap The submitted values
         */
        public void decode(AttributeDateTime component, Map<String,String> reqMap){
        	
        	String sDate;
            String sTime;
            

            if( !component.isDisabled() && !component.isReadOnly() && component.isVisible() ) {
	            // To avoid multiple inputs to the same value...
	            if( component.getSubmittedValue() == null ) {
	                
	                
	                String baseId =  component.getClientId();
	                String dateClientId = baseId + "_d"; //date input
	                String timeClientId = baseId + "_t"; //time input
	                
	            	// No extjs quando o componente attribute e inicializado 
		            // em disabled o name da input no form fica com ext-
		            // Em principio serao um bug do extjs.
	            	if( !reqMap.containsKey( dateClientId ) ) {
	            		dateClientId = "ext-" + baseId + "_d";
	            	}
	            	
	            	if (!reqMap.containsKey(timeClientId)) {
						timeClientId = "ext-" + baseId + "_t";
					}

	                
	                if( reqMap.containsKey( dateClientId ) ) {
		                sDate = reqMap.get( dateClientId );                
		                sTime = reqMap.get( timeClientId );                
		                
		                if( sTime != null ) {
		                    sDate += " " + sTime;
		                }
		                component.setSubmittedValue( sDate );
	                }
	            }
            }
        }

        @Override
        public void decode(XUIComponentBase component) {
        	
        	AttributeDateTime oAttrComp;
            oAttrComp = (AttributeDateTime)component;
            Map<String,String> reqMap = getFacesContext().getExternalContext().getRequestParameterMap();
            
            decode( oAttrComp, reqMap );
            
        }
            
    }


}
