package netgest.bo.xwc.xeo.components;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.AttributeLabel;
import netgest.bo.xwc.components.classic.AttributeNumberLookup;
import netgest.bo.xwc.components.classic.Rows;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class SplitedLookup extends XUIComponentBase {
	
	private XUIBindProperty<boObject> targetObject 	=
		new XUIBindProperty<boObject>("targetObject", this, boObject.class, "#{viewBean.XEOObject}" );
		
	private XUIBaseProperty<String> targetLookupAttribute 	=
			new XUIBaseProperty<String>("targetLookupAttribute", this);
	
	private XUIBaseProperty<String> objectAttribute		= 	
			new XUIBaseProperty<String>("objectAttribute", this);
	
	private XUIStateBindProperty<String> label 			=
			new XUIStateBindProperty<String>("label", this , "", String.class);
	
	private XUIBaseProperty<Boolean> renderLabel 		=
			new XUIBaseProperty<Boolean>("renderLabel", this, true );

	private XUIBaseProperty<String>  lookupWidth		=
			new XUIBaseProperty<String>("renderWidth", this, "60%" );
		
	private XUIBaseProperty<String>  inputWidth		=
		new XUIBaseProperty<String>("inputWidth", this, "30%" );
	
	private XUIBaseProperty<String>  inputType		=
		new XUIBaseProperty<String>("inputType", this, "attributeText" );
	
	private XUIBaseProperty<Integer>  inputMaxLength	=
		new XUIBaseProperty<Integer>("inputType", this, 30 );
	
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

	public String getInputWidth() {
		return this.inputWidth.getValue();
	}
	public void setFieldWidth( String fieldWidth ) {
		this.inputWidth.setValue( fieldWidth );
	}
	
	public String getInputType() {
		return this.inputType.getValue();
	}
	public void setInputType( String inputType ) {
		this.inputType.setValue( inputType );
	}

	public int getInputMaxLength() {
		return this.inputMaxLength.getValue();
	}
	public void setInputMaxLength( int inputMaxLength ) {
		this.inputMaxLength.setValue( inputMaxLength );
	}
	
	public void setTargetObject( String beanExpr ) {
		this.targetObject.setExpressionText( beanExpr );
	}
	
	public boObject getTargetObject() {
		return this.targetObject.getEvaluatedValue();
	}
	
	public void setRenderLabel( boolean renderLabel ) {
		this.renderLabel.setValue( renderLabel );
	}
	public boolean getRenderLabel() {
		return this.renderLabel.getValue();
	}
	
	public void setLabel( String labelExpr ) {
		this.label.setExpressionText( labelExpr );
	}
	public String getLabel() {
		return this.label.getEvaluatedValue();
	}
	
	public void setObjectAttribute( String objectAttributeExpr ) {
		this.objectAttribute.setValue( objectAttributeExpr );
	}
	public String getObjectAttritute() {
		return this.objectAttribute.getValue();
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
		
		super.initComponent();
		
		boDefAttribute locAtt = getTargetObject().getAttribute( getObjectAttritute() ).getDefAttribute();
		boDefAttribute remAtt = 
			locAtt.getReferencedObjectDef().getAttributeRef( getTargetLookupAttribute() );

		if( getRenderLabel() ) {
			AttributeLabel label = new AttributeLabel();
			label.setId( getId() + "_lbl" );
			if( this.label.isDefaultValue() )
				label.setText( locAtt.getLabel() );
			else
				label.setText( getLabel() );
			
			getChildren().add( label );
		}

		String inputTypeName = getInputType();
		AttributeBase input = (AttributeBase)getRequestContext().getApplicationContext().getViewerBuilder()
        					.createComponent( getRequestContext(), inputTypeName );
        input.setId( getId() + "_fld" );

        if( this.inputMaxLength.isDefaultValue() )
        	input.setMaxLength( getInputMaxLength() );
        else
        	input.setMaxLength( remAtt.getLen() );
        	
        input.setOnChangeSubmit( true );
        
        input.addValueChangeListener( new AttributeChangeListener() );
        getChildren().add( input );
        
		AttributeNumberLookup numberLookup = new AttributeNumberLookup();
		numberLookup.setId( getId() + "_lk" );
		numberLookup.setObjectAttribute( getObjectAttritute() );
		numberLookup.setOnChangeSubmit( "true" );
		getChildren().add( numberLookup );
		
	}
	
	@Override
	public void preRender() {
		super.preRender();
		
		String targetAtt = getTargetLookupAttribute();
		try {
			AttributeBase att = getInputComponent();
			boObject obj = getTargetObject();
			boObject childObj = obj.getAttribute( getObjectAttritute() ).getObject();
			if( childObj != null ) {
				Object value = childObj.getAttribute( targetAtt ).getValueObject();
				att.setValue( String.valueOf( value ) );
				att.setDisplayValue( String.valueOf( value )  );
				att.clearInvalid();
			}
			else {
				if( att.getInvalidText() == null ) {
					att.setValue( null );
					att.setDisplayValue( null );
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
		            if( oAttr.getRenderLabel() )
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
	
	            if( oAttr.getRenderLabel() )
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
	            w.writeAttribute( HTMLAttr.WIDTH, oAttr.getInputWidth(), null );
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
        
        
	}
	
	public static class AttributeChangeListener implements ValueChangeListener {
		@Override
		public void processValueChange(ValueChangeEvent arg0) throws AbortProcessingException {
			try {
				SplitedLookup lookup = (SplitedLookup)((AttributeBase)arg0.getComponent()).findParentComponent( SplitedLookup.class );
				AttributeNumberLookup att = (AttributeNumberLookup)lookup.findComponent( AttributeNumberLookup.class );
				
				Object inputValue = lookup.getInputComponent().getValue(); 
				
				boObject objAtt = lookup.getTargetObject();
				XEOBaseBean b = (XEOBaseBean)XUIRequestContext.getCurrentContext().getViewRoot().getBean( "viewBean" );
				Object value = b.validateLookupValue(  
						objAtt.getAttribute( lookup.getObjectAttritute() ), 
						new String[] { lookup.getTargetLookupAttribute() }, 
						new Object[] { inputValue } 
				);
				
				if( value == null && (inputValue instanceof String && ((String)inputValue).length() > 0 ) ) {
					lookup.getInputComponent().setInvalidText( "O valor introduzido é inválido!" );
				}
				else {
					lookup.getInputComponent().clearInvalid();
				}
				att.setValue( value );
				att.updateModel();
				System.out.println( lookup.getObjectAttritute() );
			} catch (boRuntimeException e) {
				throw new RuntimeException( e );
			}
		}
	}
	
}
