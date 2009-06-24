package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class Attribute extends AttributeBase
{

    private AttributeLabel      oLabel = null;
    private AttributeBase       oInput = null;

    private XUIStateProperty<String> renderLabel = new XUIStateProperty<String>( "renderLabel", this, "1" );
    private XUIStateBindProperty<String> inputType   = new XUIStateBindProperty<String>( "inputType", this,"", String.class );


    public Attribute() {
    }
    
    public void initComponent() {
        // per component inicializations.
        createChildComponents();
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
	
	            if( getValueExpression("disabled") != null )
	                this.oLabel.setDisabled( getValueExpression("disabled").getExpressionString() );
	            
	            if( getValueExpression("visible") != null )
	                this.oLabel.setVisible( getValueExpression("visible").getExpressionString() );
	
	            if( getValueExpression("modelRequired") != null ) {
	                this.oLabel.setModelRequired( getValueExpression("modelRequired").getExpressionString() );
	            }
	            
	            if( getValueExpression("recommended") != null )
	                this.oLabel.setRecommended( getValueExpression("recommended").getExpressionString() );
	            
	            //this.oLabel.setObjectAttribute( getObjectAttribute() );
	            //propagateProperties( oLabel );
	            
	            if( getValueExpression( "securityPermissions" )!=null )
	            	oLabel.setSecurityPermissions( getValueExpression("securityPermissions").getExpressionString() );

	            if( getValueExpression( "viewerSecurityPermissions" )!=null )
	            	oLabel.setViewerSecurityPermissions( getValueExpression("viewerSecurityPermissions").getExpressionString() );

	            oLabel.setInstanceId( getInstanceId() );
	            
	            this.getChildren().add( this.oLabel );
	            
	        }
	        
	        if( oInput == null ) {
	            XUIRequestContext oRequestContext = getRequestContext();
	            
	            this.oInput = (AttributeBase)oRequestContext.getApplicationContext().getViewerBuilder()
	                                    .createComponent( oRequestContext, sComponentType );
	            
	            //this.oInput = new AttributeText(  );
	            this.oInput.setId( getId() + "_i" );
	            this.oInput.setValueExpression( "value", getValueExpression("value") );
	            
	            if( getObjectAttribute() != null ) {
	            	this.oInput.setObjectAttribute( getObjectAttribute() );
	            }
	            
	            propagateProperties( this.oInput );
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
/*
                case DataFieldTypes.VALUE_DURATION:
                    sRet = "attributeDuration";
                    break;
                case DataFieldTypes.VALUE_IFILELINK:
                    sRet = "attributeIFileLink";
                    break;
                case DataFieldTypes.VALUE_NUMBER_LOOKUP:
                    sRet = "attributeNumberLookup";
                    break;
                case DataFieldTypes.VALUE_BLOB:
*/                
            //
            setInputType( sRet );
        }
        return sRet;
        
    }
    
    private void propagateProperties( AttributeBase oAttr ) {

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

        if( getValueExpression("enableCardIdLink") != null )
            oAttr.setEnableCardIdLink( getValueExpression("enableCardIdLink").getExpressionString() );
        
        if( getValueExpression("lookupViewer") != null )
            oAttr.setLookupViewer( getValueExpression("lookupViewer").getExpressionString() );
        
        if( getValueExpression( "securityPermissions" ) != null )
            oAttr.setSecurityPermissions( getValueExpression("securityPermissions").getExpressionString() );

        if( getValueExpression( "viewerSecurityPermissions" ) != null )
            oAttr.setViewerSecurityPermissions( getValueExpression("viewerSecurityPermissions").getExpressionString() );
        
        oAttr.setInstanceId( getInstanceId() );
        
        if( getValueExpression("onChangeSubmit") != null )
            oAttr.setOnChangeSubmit( getValueExpression("onChangeSubmit").getExpressionString() );
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
        super.restoreState(oState);
        
        if( this.getChildCount() > 0 ) {
            this.oLabel = (AttributeLabel)getChild( 0 );
            this.oInput = (AttributeBase)getChild( 1 );
        }
        
    }

    @Override
    public Object getValue() {
        return super.getValue();
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
    }

    @Override
    public boolean wasStateChanged() {
        return false;
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

        return renderLabel.getValue();

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
            String labelPos = "left";
            
            Rows r = (Rows)oAttr.findParentComponent( Rows.class );

            if( r!=null )
            	labelPos = r.getLabelPosition();
            
            w.startElement( TABLE, oComp );
            w.writeAttribute( CELLPADDING, "0", null );
            w.writeAttribute( CELLSPACING, "0", null );
            w.writeAttribute( HTMLAttr.STYLE, "table-layout:fixed;width:100%", null ); 

            if( !"Top".equalsIgnoreCase( labelPos ) ) 
            {
	            w.startElement("COLGROUP", oComp);
	            w.startElement(COL, oComp );
	            w.writeAttribute( HTMLAttr.WIDTH, "100px", null );
	            w.endElement("COL");
	            w.startElement(COL, oComp );
	            w.endElement("COL");
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
