package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIMethodBindingValueChangeListener;

/**
 * This components renders a label and the input component for a {@link DataFieldConnector}
 * 
 * The inputType is automatic calculated based on the renderType returned from the DataFieldConnector
 * but can be overwrited for any valid AttributeBase input field
 * 
 * Example:
 * <code>
 * 		<xvw:attribute objectAttribute='att1' inputType='attributePassword'/>
 * 		<!-- In this case the attribute is forced to render as a password -->
 * 
 * 		<!-- To overwrite propreties from the {@link DataFieldConnector} the
 * 			objectAttribute property must be first attribute in the xml
 * 	 	-->
 * 		<-- Wrong Way -->
 * 		<xvw:attribute objectAttribute='att1' disabled='true'/>
 * 		<-- Write Way -->
 * 		<xvw:attribute disabled='true' objectAttribute='att1'/>
 * 
 * </code>
 * 
 * Can't have children
 * 
 * 
 * @author jcarreira
 *
 */
public class Attribute extends AttributeBase
{

    private AttributeLabel      oLabel = null;
    private AttributeBase       oInput = null;

    private XUIStateProperty<String> renderLabel = new XUIStateProperty<String>( "renderLabel", this, "1" );
    private XUIStateBindProperty<String> inputType   = new XUIStateBindProperty<String>( "inputType", this,"", String.class );


    public Attribute() {}
    
	@Override
	public void initComponent() {
        // per component inicializations.
        createChildComponents();
    }

    @Override
	public String getViewerSecurityLabel() {
    	String ret = getViewerSecurityComponentType().toString();
    	if( getObjectAttribute() != null && !"".equals( getObjectAttribute() ) ) {
	    	try {
	    		ret = getViewerSecurityComponentType().toString() + " " + getObjectAttribute();
	    	}
	    	catch (Throwable e){
	    		
	    	}
    	}
		return ret; 
	}
    
    public void createChildComponents() {
        
        String sComponentType;
        if( isLov() ) {
            sComponentType = "attributeLov";
        }
        else {
            sComponentType = getInputComponentType();    
        }

        if( sComponentType != null ) {
	        if( oLabel == null ) {
	
	            this.oLabel = new AttributeLabel();
	            this.oLabel.setId( getId() +  "_l" );
	            this.oLabel.setText( getLabel() );
	
	            propagateLabelProperties( oLabel );
	            
	            this.getChildren().add( this.oLabel );
	            
	        }
	        
	        if( oInput == null ) {
	            XUIRequestContext oRequestContext = getRequestContext();
	            
	            this.oInput = (AttributeBase)oRequestContext.getApplicationContext().getViewerBuilder()
	                                    .createComponent( oRequestContext, sComponentType );
	            
	            //this.oInput = new AttributeText(  );
	            this.oInput.setId( getId() + "_i" );
	            
	            propagateInputProperties( this.oInput );
	            
	            this.getChildren().add( this.oInput );
	            
	            
	        }
        }
    }
    
    
    private String getInputComponentType(  ) {
        String sRet;
        byte bDataType;
        
        sRet = getInputType();
        
        if( sRet == null || sRet.length()==0 ) {
            sRet = null;
            try {
            	bDataType = getDataType();
            } catch ( Exception e ) {
            	throw new RuntimeException(e);
            }
            switch ( bDataType )
            {
                case DataFieldTypes.VALUE_CHAR:
                    switch( getInputRenderType() ) {
                        case DataFieldTypes.RENDER_DEFAULT:
                            sRet = "attributeText";
                            break;
                        case DataFieldTypes.RENDER_LOV:
                            sRet = "attributeLov";
                            break;
                        case DataFieldTypes.RENDER_TEXTAREA:
                            sRet = "attributeTextArea";
                            break;
                        case DataFieldTypes.RENDER_IFILE_LINK:
                            sRet = "attributeFile";
                            break;
                    }
                    break;
                case DataFieldTypes.VALUE_NUMBER:
                    switch( getInputRenderType() ) {
                        case DataFieldTypes.RENDER_DEFAULT:
                            sRet = "attributeNumber";
                            break;
                        case DataFieldTypes.RENDER_LOV:
                            sRet = "attributeLov";
                            break;
                        case DataFieldTypes.RENDER_OBJECT_LOOKUP:
                            sRet = "attributeNumberLookup";
                            break;
                    }
                    break;
                case DataFieldTypes.VALUE_BOOLEAN:
                    sRet = "attributeBoolean";
                    break;
                case DataFieldTypes.VALUE_CLOB:
                    sRet = "attributeTextArea";
                    break;
                case DataFieldTypes.VALUE_DATE:
                    sRet = "attributeDate";
                    break;
                case DataFieldTypes.VALUE_DATETIME:
                    sRet = "attributeDateTime";
                    break;
            }

            setInputType( sRet );
            
        }
        return sRet;
        
    }
    
    
    protected void propagateLabelProperties( AttributeLabel label ) {
        if( getValueExpression("visible") != null )
        	label.setVisible( getValueExpression("visible").getExpressionString() );

        if( getValueExpression("modelRequired") != null ) {
            label.setModelRequired( getValueExpression("modelRequired").getExpressionString() );
        }
        
        if( getValueExpression("recommended") != null )
        	label.setRecommended( getValueExpression("recommended").getExpressionString() );
        
        if( getValueExpression( "securityPermissions" )!=null )
        	label.setSecurityPermissions( getValueExpression("securityPermissions").getExpressionString() );

        if( getValueExpression( "viewerSecurityPermissions" )!=null )
        	label.setViewerSecurityPermissions( getValueExpression("viewerSecurityPermissions").getExpressionString() );

        label.setInstanceId( getInstanceId() );
    }
    
    protected void propagateInputProperties( AttributeBase oAttr ) {

    	oAttr.setValueExpression( "value", getValueExpression("value") );
        if( getObjectAttribute() != null ) {
        	oAttr.setObjectAttribute( getObjectAttribute() );
        }
    	
        // Set attribute properties
        if( getBeanProperty() != null )
            oAttr.setBeanProperty( getBeanProperty() );

        if( getValueExpression("maxLength") != null )
            oAttr.setMaxLength( getValueExpression("maxLength").getExpressionString() );

        if( getValueExpression("disabled") != null )
            oAttr.setDisabled( getValueExpression("disabled").getExpressionString() );

        if( getValueExpression("readOnly") != null )
            oAttr.setReadOnly( getValueExpression("readOnly").getExpressionString() );
        
        if( getValueExpression("visible") != null )
            oAttr.setVisible( getValueExpression("visible").getExpressionString() );

        if( getValueExpression("width") != null )
            oAttr.setWidth( getValueExpression("width").getExpressionString() );

        if( getValueExpression("height") != null )
            oAttr.setHeight( getValueExpression("height").getExpressionString() );

        if( getValueExpression("lovMap") != null )
            oAttr.setLovMap( getValueExpression("lovMap").getExpressionString() );

        if( getValueExpression("validation") != null )
            oAttr.setValidation( getValueExpression("validation").getExpressionString() );

        if( getValueExpression("enableCardIdLink") != null )
            oAttr.setEnableCardIdLink( getValueExpression("enableCardIdLink").getExpressionString() );
        
        if( getValueExpression("lookupViewer") != null )
            oAttr.setLookupViewer( getValueExpression("lookupViewer").getExpressionString() );
        
        if( getValueExpression( "securityPermissions" ) != null )
            oAttr.setSecurityPermissions( getValueExpression("securityPermissions").getExpressionString() );

        if( getValueExpression( "viewerSecurityPermissions" ) != null )
            oAttr.setViewerSecurityPermissions( getValueExpression("viewerSecurityPermissions").getExpressionString() );
        
        if( getValueExpression( "minDecimalPrecision" ) != null )
            oAttr.setMinDecimalPrecision( getValueExpression("minDecimalPrecision").getExpressionString() );

        if( getValueExpression( "decimalPrecision" ) != null )
            oAttr.setDecimalPrecision( getValueExpression("decimalPrecision").getExpressionString() );
        
        if( getValueExpression( "groupNumber" ) != null )
            oAttr.setGroupNumber( getValueExpression("groupNumber").getExpressionString() );

        oAttr.setInstanceId( getInstanceId() );
        
        if( getValueExpression("onChangeSubmit") != null )
            oAttr.setOnChangeSubmit( getValueExpression("onChangeSubmit").getExpressionString() );

        if( getValueExpression("displayValue") != null )
            oAttr.setDisplayValue( getValueExpression("displayValue").getExpressionString() );
        
        XUIMethodBindingValueChangeListener[] valueChangeListener  = 
        	(XUIMethodBindingValueChangeListener[]) getFacesListeners( XUIMethodBindingValueChangeListener.class );
        for (int i = 0; i < valueChangeListener.length; i++) {
			oAttr.addValueChangeListener( valueChangeListener[i] );
		}
        
    }
     

    public void setLabelComponent(AttributeLabel oLabel) {
        this.oLabel = oLabel;
    }

    public AttributeLabel getLabelComponent() {
        return oLabel;
    }

    public void setInputComponent(AttributeText oInput) {
        this.oInput = oInput;
    }

    public XUIComponentBase getInputComponent() {
        return oInput;
    }

    @Override
    public void decode( ) {
        // This object cannot decode... is a representation of label and attrbiute
        // Overwrite avoid decodes
    }

    @Override
    public void restoreState(Object oState) {
        if( this.getChildCount() > 0 ) {
            this.oLabel = (AttributeLabel)getChild( 0 );
            this.oInput = (AttributeBase)getChild( 1 );
        }
        super.restoreState(oState);
    }

    @Override
    public boolean wasStateChanged() {
        return false;
    }

    @Override
    public void validateModel() {
    	return;
    }

    public void setRenderLabel( boolean renderLabel) {
        this.renderLabel.setValue( renderLabel?"1":"0" );
    }
    
    public void setRenderLabel(String renderLabel) {

        this.renderLabel.setValue( renderLabel );

    }

    public String getRenderLabel(  ) {

        return renderLabel.getValue();

    }

    public void setRenderInput(String renderLabel) {

        this.renderLabel.setValue( renderLabel );

    }

    public String getRenderInput(  ) {

        return "1";

    }

    @Override
	public void updateModel() {
    	// Do nothing... this is a virtual component....
    	
	}

	public void setInputType(String sInputType) {
        this.inputType.setExpressionText( sInputType );
    }

    public String getInputType() {
        return inputType.getEvaluatedValue();
    }

    public static final class XEOHTMLRenderer extends XUIRenderer {


        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {

            Attribute oAttr = (Attribute)oComp;
            
            XUIResponseWriter w = getResponseWriter();
            String labelPos 	= "left";
            int	   labelWidth   = 100;
            
            if( !"1".equals( oAttr.getRenderLabel() ) )
            {
            	System.out.print(false);
            }
            	
            
            Rows r = (Rows)oAttr.findParentComponent( Rows.class );

            if( r!=null ) {
            	labelPos 	= r.getLabelPosition();
            	labelWidth	= r.getLabelWidth();
            }
            
            w.startElement( TABLE, oComp );
            w.writeAttribute( CELLPADDING, "0", null );
            w.writeAttribute( CELLSPACING, "0", null );
            w.writeAttribute( HTMLAttr.STYLE, "table-layout:fixed;width:100%", null ); 

            if( !"Top".equalsIgnoreCase( labelPos ) ) 
            {
	            w.startElement("COLGROUP", oComp);
	            w.startElement(COL, oComp );
	            w.writeAttribute( HTMLAttr.WIDTH, labelWidth + "px", null );
	            w.endElement("COL");

	            if( "1".equals( oAttr.getRenderLabel() ) )
	            {
		            w.startElement(COL, oComp );
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

            if( "1".equals( oAttr.getRenderLabel() ) )
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
            
            if( "1".equals( oAttr.getRenderInput() ) )
            {
                // Write Control
                w.startElement( TD, oComp );
                
                XUIComponentBase inpComp = oAttr.getInputComponent();
                
                if( inpComp != null ) {
                	inpComp.encodeAll();
                } else {
                	w.writeText( "« Invalid [" + oAttr.getObjectAttribute() + "] »", null );
                }
                w.endElement( TD );
            }
            w.endElement( TR );
            w.endElement( TABLE );

        }

        @Override
        public void decode( XUIComponentBase oComp ) {
            
            Attribute oAttrComp;
            
            oAttrComp = (Attribute)oComp;
            
            String value = getContext().getRequestParameterMap().get( oComp.getClientId() );
            ((Attribute)oComp).setSubmittedValue( value );
            
            if( oAttrComp.oLabel != null )
                oAttrComp.oLabel.decode( );
    
            if( oAttrComp.oInput != null )
                oAttrComp.oInput.decode( );
            
            
        }

        @Override
        public boolean getRendersChildren() {
            return true;
        }

        @Override
        public void encodeChildren(XUIComponentBase component) throws IOException {


        }
    }
}
