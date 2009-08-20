package netgest.bo.xwc.xeo.components;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.Attribute;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.AttributeLabel;
import netgest.bo.xwc.components.classic.AttributeNumberLookup;
import netgest.bo.xwc.components.classic.Rows;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.beans.XEOEditBean;

public class SplitedLookup extends Attribute {
	
	private XUIBindProperty<boObject> targetObject 	=
		new XUIBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );
		
	private XUIBaseProperty<String> targetLookupAttribute 	=
			new XUIBaseProperty<String>("targetLookupAttribute", this);
	
	private XUIBaseProperty<String>  lookupWidth		=
			new XUIBaseProperty<String>("lookupWidth", this, "60%" );
		
	private XUIBaseProperty<String>  keyInputWidth		=
		new XUIBaseProperty<String>("keyInputWidth", this, "30%" );

	private XUIBaseProperty<String>  keyInputType		=
		new XUIBaseProperty<String>("keyInputType", this, "attributeText" );
	
	public String getLookupWidth() {
		return this.lookupWidth.getValue();
	}
	
	public void setLookupWidth( String lookupWidth ) {
		this.lookupWidth.setValue( lookupWidth );
	}
	
	@Override
	public String getRendererType() {
		return super.getRendererType();
	}

	public String getKeyInputWidth() {
		return this.keyInputWidth.getValue();
	}
	public void setKeyInputWidth( String fieldWidth ) {
		this.keyInputWidth.setValue( fieldWidth );
	}

	public void setKeyInputType( String keyInputType ) {
		this.keyInputType.setValue( keyInputType );
	}

	public String getKeyInputType() {
		return this.keyInputType.getValue();
	}
	
	public void setTargetObject( String beanExpr ) {
		this.targetObject.setExpressionText( beanExpr );
	}
	
	public boObject getTargetObject() {
		return this.targetObject.getEvaluatedValue();
	}
	
	public void setTargetLookupAttribute( String attribute ) {
		this.targetLookupAttribute.setValue( attribute );
	}
	
	public String getTargetLookupAttribute() {
		return this.targetLookupAttribute.getValue();
	}
	
	public AttributeLabel getLabelComponent() {
		return (AttributeLabel)findComponent( getId() + "_lbl" );
	}

	public AttributeBase getInputComponent() {
		return (AttributeBase)findComponent( getId() + "_fld" );
	}
	
	public AttributeBase getLookupComponent() {
		return (AttributeBase)findComponent( getId() + "_lk" );
	}
	
	@Override
	public boolean wasStateChanged() {
		return super.wasStateChanged();
	}
	
	@Override
	public void initComponent() {

		boObject targetObject = getTargetObject();
		String 	 attName	  =  getObjectAttribute();
		
		boDefAttribute locAtt = targetObject.getAttribute( attName ).getDefAttribute();
		boDefAttribute remAtt = 
			locAtt.getReferencedObjectDef().getAttributeRef( getTargetLookupAttribute() );

		if( "1".equals( getRenderLabel() ) ) {
			AttributeLabel label = new AttributeLabel();
			label.setId( getId() + "_lbl" );
			if( getStateProperty("label").isDefaultValue() )
				label.setText( locAtt.getLabel() );
			else
				label.setText( getLabel() );
			
			propagateLabelProperties(label);
			
			getChildren().add( label );
		}

		String inputTypeName = getKeyInputType();
		AttributeBase input = (AttributeBase)getRequestContext().getApplicationContext().getViewerBuilder()
        					.createComponent( getRequestContext(), inputTypeName );
        input.setId( getId() + "_fld" );

        if( getStateProperty("maxLength").isDefaultValue() )
        	input.setMaxLength( getMaxLength() );
        else
        	input.setMaxLength( remAtt.getLen() );
        	
        input.setOnChangeSubmit( "true" );
        
        input.addValueChangeListener( new ChangeListener() );
        
        getChildren().add( input );
        
		AttributeNumberLookup numberLookup = new AttributeNumberLookup();
		numberLookup.setId( getId() + "_lk" );
		
		propagateInputProperties( numberLookup );
		
		numberLookup.setOnChangeSubmit( "true" );
		numberLookup.addValueChangeListener( new AttributeChangeListener() );
		getChildren().add( numberLookup );
	}
	
	@Override
	public void validate(FacesContext context) {
		super.validate(context);
	}
	
	@Override
	public void validateModel() {
		super.validateModel();
		if( isModelValid() ) {
			String s = getInputComponent().getInvalidText();
			if( s != null && s.length() > 0 ) {
				XUIRequestContext c = XUIRequestContext.getCurrentContext();
				c.addMessage( getInputComponent().getClientId() + "_msg" ,
						new XUIMessage( XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, "", getLabel() + " - " + s )
				);
				setModelValid( false );
			}
		}
	}
	
	@Override
	public void preRender() {
		super.preRender();
		
		String targetAtt = getTargetLookupAttribute();
		try {
			AttributeBase att = getInputComponent();
			boObject obj = getTargetObject();
			boObject childObj = obj.getAttribute( getObjectAttribute() ).getObject();
			if( childObj != null ) {
				Object value = childObj.getAttribute( targetAtt ).getValueObject();
				att.setValue( String.valueOf( value )  );
				att.setDisplayValue( String.valueOf( value )  );
				att.clearInvalid();
			}
			else {
				if( att.getInvalidText() == null ) {
					att.setValue( null );
					att.setDisplayValue( null );
				}
				else {
					if( att.getValue() != null )
						att.setDisplayValue( String.valueOf( att.getValue() ) );
				}
			}
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer {
        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {
        	
            SplitedLookup oAttr = (SplitedLookup)oComp;
            if( !oAttr.isRenderedOnClient() ) {
	            XUIResponseWriter w = getResponseWriter();
	            String labelPos 	= "left";
	            int	   labelWidth   = 100;
	            
	            Rows r = (Rows)oAttr.findParentComponent( Rows.class );
	
	            if( r!=null ) {
	            	labelPos 	= r.getLabelPosition();
	            	labelWidth	= r.getLabelWidth();
	            }
	            
	            w.startElement( TABLE, oComp );
	            w.writeAttribute( CELLPADDING, "0", null );
	            w.writeAttribute( CELLSPACING, "0", null );
	            w.writeAttribute( HTMLAttr.ID, oComp.getClientId(), null );
	            
	            w.writeAttribute( HTMLAttr.STYLE, "table-layout:fixed;width:100%", null ); 
	
	            if( !"Top".equalsIgnoreCase( labelPos ) ) 
	            {
		            w.startElement("COLGROUP", oComp);
		            if( "1".equals( oAttr.getRenderLabel() ) )
		            {
			            w.startElement(COL, oComp );
			            w.writeAttribute( HTMLAttr.WIDTH, labelWidth + "px", null );
			            w.endElement("COL");
		            }
		            w.endElement("COLGROUP");
	            } else {
		            w.startElement("COLGROUP", oComp);
		            w.startElement(COL, oComp );
		            w.writeAttribute( HTMLAttr.WIDTH, "100%", null );
		            w.endElement("COL");
		            w.endElement("COLGROUP");
	            }
	            
	            w.startElement( TR, oComp );
	
	            if( "1".equals(oAttr.getRenderLabel()) )
	            {
	            	w.startElement( TD, oComp );
	                if( oAttr.getLabelComponent() != null ) {
	                    oAttr.getLabelComponent().encodeAll();    
	                }
	                w.endElement( TD );
	            }
	
	            if( "Top".equalsIgnoreCase( labelPos ) ) 
	            {
	                w.endElement( TR );
	                w.startElement( TR, oComp );
	            }
	            
	            // Write Input
	            w.startElement( TD, oComp );
	
	            w.startElement( TABLE, oComp );
	            w.writeAttribute( CELLPADDING, "0", null );
	            w.writeAttribute( CELLSPACING, "1", null );
	            w.writeAttribute( HTMLAttr.STYLE, "table-layout:fixed;width:100%", null ); 
	            w.startElement("COLGROUP", oComp);
	            
	            w.startElement(COL, oComp );
	            w.writeAttribute( HTMLAttr.WIDTH, oAttr.getKeyInputWidth(), null );
	            w.endElement("COL");
	
	            w.startElement(COL, oComp );
	            w.writeAttribute( HTMLAttr.WIDTH, oAttr.getLookupWidth(), null );
	            w.endElement("COL");
	            w.endElement( HTMLTag.COLGROUP );
	            w.startElement( TR, oComp );
	            w.startElement( TD, oComp );
	            
	            XUIComponentBase inpComp = oAttr.getInputComponent();
	            if( inpComp != null ) {
	            	inpComp.encodeAll();
	            } 
	            w.endElement( TD );
	
	            // Write Lookup
	            w.startElement( TD, oComp );
	            XUIComponentBase lookupComp = oAttr.getLookupComponent();
	            if( lookupComp != null ) {
	            	lookupComp.encodeAll();
	            }
	            w.endElement( TD );
	            w.endElement( TR );
	            w.endElement( TABLE );
	            
	            w.endElement( TD );
	            w.endElement( TR );
	            w.endElement( TABLE );
            }
            else {
            	oAttr.setDestroyOnClient( false );
            }
        }
        
        @Override
        public boolean getRendersChildren() {
        	return true;
        }
        
        @Override
        public void encodeChildren(XUIComponentBase component) throws IOException {
        }
        
        
        @Override
        public void decode(XUIComponentBase component) {
        	
        	SplitedLookup s = (SplitedLookup)component;
        	
        	XUIRequestContext r = XUIRequestContext.getCurrentContext();
        	if( r.getRequestParameterMap().containsKey( component.getClientId() + "_fld" ) ) {
        		s.getInputComponent().setDisplayValue(  r.getRequestParameterMap( ).get( component.getClientId() + "_fld" ) );
        	}
        }
        
	}
	
	public static class ChangeListener implements ValueChangeListener {
		@Override
		public void processValueChange(ValueChangeEvent arg0) throws AbortProcessingException {
			try {
				
				SplitedLookup lookup = 
					(SplitedLookup)((AttributeBase)arg0.getComponent())
						.findParentComponent( SplitedLookup.class );

				Object inputValue = lookup.getInputComponent().getValue(); 
				
				boObject objAtt = lookup.getTargetObject();
				XEOEditBean b = (XEOEditBean)XUIRequestContext.getCurrentContext().getViewRoot().getBean( "viewBean" );
				Object value = b.validateLookupValue(  
						objAtt.getAttribute( lookup.getObjectAttribute() ), 
						new String[] { lookup.getTargetLookupAttribute() }, 
						new Object[] { inputValue } 
				);
				
				if( value == null && (inputValue instanceof String && ((String)inputValue).length() > 0 ) ) {
					lookup.getInputComponent().setInvalidText( "O valor introduzido é inválido!" );
					lookup.getLookupComponent().setValue(null);
				}
				else {
					lookup.getInputComponent().clearInvalid();
					AttributeBase lk = lookup.getLookupComponent();
					lk.setValue( value );
					lk.updateModel();
				}
			} catch (boRuntimeException e) {
				throw new RuntimeException( e );
			}
		}
	}
	
	
	public static class AttributeChangeListener implements ValueChangeListener {
		@Override
		public void processValueChange(ValueChangeEvent arg0) throws AbortProcessingException {
			SplitedLookup lookup = 
				(SplitedLookup)((AttributeBase)arg0.getComponent())
					.findParentComponent( SplitedLookup.class );
				
			String targetAtt = lookup.getTargetLookupAttribute();
			try {
				AttributeBase att = lookup.getInputComponent();
				boObject obj = lookup.getTargetObject();
				boObject childObj = obj.getAttribute( lookup.getObjectAttribute() ).getObject();
				if( childObj != null ) {
					Object value = childObj.getAttribute( targetAtt ).getValueObject();
					att.setDisplayValue( String.valueOf( value )  );
					att.clearInvalid();
				}
				else {
					if( att.getInvalidText() == null ) {
						att.setDisplayValue( null );
					}
				}
			} catch (boRuntimeException e) {
				throw new RuntimeException( e );
			}
		}
	}
	
}
