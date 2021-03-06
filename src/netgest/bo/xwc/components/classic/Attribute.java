package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TD;
import static netgest.bo.xwc.components.HTMLTag.TR;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIMethodBindingValueChangeListener;

import netgest.utils.StringUtils;

import java.io.IOException;

import javax.faces.component.UIComponent;

/**
 * This components renders a label and the input component for a {@link DataFieldConnector}
 * 
 * The inputType is automatic calculated based on the renderType returned from the DataFieldConnector
 * but can be overwritten for any valid AttributeBase input field
 * 
 * Example:
 * <code>
 * 		<xvw:attribute objectAttribute='att1' inputType='attributePassword'/>
 * 		<!-- In this case the attribute is forced to render as a password -->
 * </code>
 * 
 * @author jcarreira
 *
 */
public class Attribute extends AttributeBase
{

//    private AttributeLabel      oLabel = null;
//    private AttributeBase       oInput = null;

    /**
     * Whether or not the label of this attribute should be rendered
     */
    private XUIViewStateProperty<String> renderLabel = new XUIViewStateProperty<String>( "renderLabel", this, "1" );
    /**
     * The input type for the field
     */
    @Values({"attributeText","attributeBoolean","attributeNumber","attributeDate","attributeTime","attributeDateTime","attributeHtmlEditor","attributeLov","bridgeLookup","attributeAutoComplete"})
    private XUIViewStateBindProperty<String> inputType   = new XUIViewStateBindProperty<String>( "inputType", this,"", String.class );


    public Attribute() {}
    
	@Override
	public void initComponent() {
        super.initComponent();
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
        
        if (StringUtils.hasValue( sComponentType )){
        	createLabelComponent( );
        	createInputComponent( sComponentType );
        }
	            
	    
    }

	private void createLabelComponent() {
		if( "1".equals( getRenderLabel() ) ) {
			
			AttributeLabel oLabel = null;
			UIComponent labelFacet = getFacet( "label" );
			if (labelFacet != null){
				oLabel = (AttributeLabel) labelFacet.getChildren( ).get( 0 );
			} else {
				oLabel = new AttributeLabel();
				oLabel.setText( getLabel() );
			}
			
		    oLabel.setId( getId() +  "_l" );

		    propagateLabelProperties( oLabel );
		    
		    this.getChildren().add( oLabel );
		    
		}
	}

	private void createInputComponent( String sComponentType) {
		UIComponent inputFacet = getFacet( "input" );
		AttributeBase oInput = null;
		XUIRequestContext oRequestContext = getRequestContext();
		if (inputFacet != null){
			oInput = (AttributeBase) inputFacet.getChildren( ).get( 0 );
		} else {
		    oInput = (AttributeBase)oRequestContext.getApplicationContext().getViewerBuilder()
                            .createComponent( oRequestContext, sComponentType );
		}
		
		oInput.setId( getId() + "_i" );
		
		propagateInputProperties( oInput );
		
		this.getChildren().add( oInput );
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
                        case DataFieldTypes.RENDER_HTMLEDITOR:
                        	sRet = "attributeHtmlEditor";
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
                        	if (!getEnableFullTextSearch())
                        		sRet = "attributeNumberLookup";
                        	else
                        		sRet = "attributeSearchLookup";
                            break;
                    }
                    break;
                case DataFieldTypes.VALUE_BOOLEAN:
                    sRet = "attributeBoolean";
                    break;
                case DataFieldTypes.VALUE_CLOB:
                	switch( getInputRenderType() ) {
	                	case DataFieldTypes.RENDER_HTMLEDITOR : sRet = "attributeHtmlEditor"; break;
	                	default : sRet = "attributeTextArea"; break;
                	}
                    break;
                case DataFieldTypes.VALUE_DATE:
                    sRet = "attributeDate";
                    break;
                case DataFieldTypes.VALUE_DATETIME:
                    sRet = "attributeDateTime";
                    break;
                case DataFieldTypes.VALUE_BRIDGE:
                	sRet = "bridgeLookup";
                	break;
                case DataFieldTypes.VALUE_CURRENCY:
                	sRet = "attributeNumber"; 
                	break;	
            }

            setInputType( sRet );
            
        }
        return sRet;
        
    }
    
    private boolean isPropertyDefaultValue(AttributeLabel label, String propertyName) {
    	XUIBaseProperty< ? > prop = label.getStateProperty( propertyName );
    	if (prop != null) {
    		return prop.isDefaultValue();
    	}
    	return true;
    	
    }
    
    protected void propagateLabelProperties( AttributeLabel label ) {
        if( getValueExpression("visible") != null  && isPropertyDefaultValue( label , "visible" ))
        	label.setVisible( getValueExpression("visible").getExpressionString() );

        if( getValueExpression("modelRequired") != null  && isPropertyDefaultValue( label , "modelRequired" )) {
            label.setModelRequired( getValueExpression("modelRequired").getExpressionString() );
        }
        
        if( getValueExpression("recommended") != null && isPropertyDefaultValue( label , "recommended" ) )
        	label.setRecommended( getValueExpression("recommended").getExpressionString() );
        
        if( getValueExpression( "securityPermissions" )!=null )
        	label.setSecurityPermissions( getValueExpression("securityPermissions").getExpressionString() );

        if( getValueExpression( "viewerSecurityPermissions" )!=null )
        	label.setViewerSecurityPermissions( getValueExpression("viewerSecurityPermissions").getExpressionString() );

        if (getValueExpression( "toolTip" ) != null  && isPropertyDefaultValue( label , "toolTip" ))
        	label.setToolTip( getValueExpression( "toolTip" ).getExpressionString() );
        
        label.setInstanceId( getInstanceId() );
    }
    
    protected void propagateInputProperties( AttributeBase oAttr ) {

    	oAttr.setValueExpression( "value", getValueExpression("value") );
    	
    	if (getBeanId() != null){
        	oAttr.setBeanId( getBeanId() );
        }
    	
        if( getBeanProperty() != null )
            oAttr.setBeanProperty( getBeanProperty() );
    	
        if( getObjectAttribute() != null ) {
        	oAttr.setObjectAttribute( getObjectAttribute() );
        }
    	
        // Set attribute properties
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
        
        if( getValueExpression("label") != null )
            oAttr.setLabel( getValueExpression("label").getExpressionString() );

        if( getValueExpression("lovMap") != null )
            oAttr.setLovMap( getValueExpression("lovMap").getExpressionString() );

        if( getValueExpression("validation") != null )
            oAttr.setValidation( getValueExpression("validation").getExpressionString() );

        if( getValueExpression("enableCardIdLink") != null )
            oAttr.setEnableCardIdLink( getValueExpression("enableCardIdLink").getExpressionString() );
        
        if( getValueExpression("showFilenameEdit") != null )
            oAttr.setShowFilenameEdit( getValueExpression("showFilenameEdit").getExpressionString() );
        
        if( getValueExpression("enableFullTextSearch") != null )
            oAttr.setEnableFullTextSearch( getValueExpression("enableFullTextSearch").getExpressionString() );
        
        if( getValueExpression("lookupViewer") != null )
            oAttr.setLookupViewer( getValueExpression("lookupViewer").getExpressionString() );
        
        if( getValueExpression( "securityPermissions" ) != null )
            oAttr.setSecurityPermissions( getValueExpression("securityPermissions").getExpressionString() );
        
        if( getValueExpression( "invalidText" ) != null )
            oAttr.setInvalidText( getValueExpression("invalidText").getExpressionString() );

        if( getValueExpression( "viewerSecurityPermissions" ) != null )
            oAttr.setViewerSecurityPermissions( getValueExpression("viewerSecurityPermissions").getExpressionString() );
        
        if( getValueExpression( "minDecimalPrecision" ) != null )
            oAttr.setMinDecimalPrecision( getValueExpression("minDecimalPrecision").getExpressionString() );

        if( getValueExpression( "decimalPrecision" ) != null )
            oAttr.setDecimalPrecision( getValueExpression("decimalPrecision").getExpressionString() );
        
        if( getValueExpression( "groupNumber" ) != null )
            oAttr.setGroupNumber( getValueExpression("groupNumber").getExpressionString() );

        if( getValueExpression( "maxValue" ) != null )
            oAttr.setMaxValue( getValueExpression("maxValue").getExpressionString() );
        
        if( getValueExpression( "minValue" ) != null )
            oAttr.setMinValue( getValueExpression("minValue").getExpressionString() );
        
        if( getValueExpression( "showFavorites" ) != null )
            oAttr.setShowFavorites( getValueExpression("showFavorites").getExpressionString() );
        
        if( getValueExpression( "listFavorites" ) != null )
            oAttr.setListFavorites( getValueExpression("listFavorites").getExpressionString() );
        
        oAttr.setInstanceId( getInstanceId() );
        
        if( getValueExpression("onChangeSubmit") != null )
            oAttr.setOnChangeSubmit( getValueExpression("onChangeSubmit").getExpressionString() );

        Boolean lockScreen = this.getOnChangeSubmitLockScreen();
        if( lockScreen )
            oAttr.setOnChangeSubmitLockScreen( Boolean.TRUE );

        if( getValueExpression("displayValue") != null )
            oAttr.setDisplayValue( getValueExpression("displayValue").getExpressionString() );
        
        if( getValueExpression("dataType") != null )
            oAttr.setDataType( getValueExpression("dataType").getExpressionString() );
        

		if( getValueExpression("dataFieldConnector") != null ){
			oAttr.setDataFieldConnector( getValueExpression("dataFieldConnector").getExpressionString() );
		}
        

        if (StringUtils.hasValue( lookupResults.getExpressionString( ) ) )
            oAttr.setLookupResults( lookupResults.getExpressionString( ) );
        
        XUIMethodBindingValueChangeListener[] valueChangeListener  = 
        	(XUIMethodBindingValueChangeListener[]) getFacesListeners( XUIMethodBindingValueChangeListener.class );
        for (int i = 0; i < valueChangeListener.length; i++) {
			oAttr.addValueChangeListener( valueChangeListener[i] );
		}
        
        oAttr.initSpecificSettings();
        
    }
     

    public AttributeLabel getLabelComponent() {
        return (AttributeLabel)findComponent( getId() + "_l" );
    }

    public XUIComponentBase getInputComponent() {
        return (XUIComponentBase)findComponent( getId() + "_i" );
    }

    @Override
    public void decode( ) {
        // This object cannot decode... is a representation of label and attrbiute
        // Overwrite avoid decodes
    }

    @Override
    public void restoreState(Object oState) {
    	/*        	
        if( this.getChildCount() > 0 ) {
        	UIComponent comp = getChild( 0 );
        	if( comp instanceof AttributeLabel ) {
        		this.oLabel = (AttributeLabel)comp;
        		this.oInput = (AttributeBase)getChild( 1 );
        	}
        	else {
        		this.oInput = (AttributeBase)getChild( 0 );
        	}
        }*/
        super.restoreState(oState);
    }

    @Override
    public StateChanged wasStateChanged2() {
        return StateChanged.NONE;
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
    @Override
    public Object saveState() {
    	return super.saveState();
    }

    public static  class XEOHTMLRenderer extends XUIRenderer {


        @Override
        public void encodeEnd(XUIComponentBase oComp) throws IOException {

            Attribute oAttr = (Attribute)oComp;
            
            XUIResponseWriter w = getResponseWriter();
            String labelPos 	= "left";
            int	   labelWidth   = 100;
            
            Rows r = (Rows)oAttr.findParentComponent( Rows.class );

            if( r!=null ) {
            	labelPos 	= r.getLabelPosition();
            	labelWidth	= r.getLabelWidth();
            }
            
            w.startElement( TABLE, oComp );
            w.writeAttribute( HTMLAttr.ID, oComp.getClientId(), null ); 
            w.writeAttribute( CELLPADDING, "0", null );
            w.writeAttribute( CELLSPACING, "0", null );
            w.writeAttribute( HTMLAttr.STYLE, "table-layout:fixed;width:100%", null ); 

            if( !"Top".equalsIgnoreCase( labelPos ) ) 
            {
	            w.startElement("COLGROUP", oComp);
	            if( "1".equals( oAttr.getRenderLabel() ) )
	            {
		            w.startElement(COL, oComp );
		            w.writeAttribute( HTMLAttr.WIDTH, labelWidth + "px", null );
		            w.writeAttribute(HTMLAttr.STYLE, "padding-right:3px", null);
		            w.writeAttribute(HTMLAttr.ALIGN, labelPos, null); 
		            w.endElement("COL");
	            }

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
            w.writeAttribute(HTMLAttr.STYLE, "width:100%");
            
            if( "1".equals( oAttr.getRenderLabel() ) )
            {
            	w.startElement( TD, oComp );
            	w.writeAttribute( HTMLAttr.HEIGHT , "20px", null );
            	w.writeAttribute(HTMLAttr.ALIGN, labelPos, null);
            	w.writeAttribute(HTMLAttr.STYLE, "padding-right:3px", null);
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
                	w.writeText( "- Invalid [" + oAttr.getObjectAttribute() + "] -", null );
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
            
            XUIComponentBase label = oAttrComp.getLabelComponent();
            XUIComponentBase input = oAttrComp.getInputComponent();
            
            if( label != null )
            	label.decode( );
    
            if( input != null )
                input.decode( );
            
            
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
