package netgest.bo.xwc.components.classic;

import java.io.IOException;
import java.util.List;

import javax.el.ValueExpression;

import netgest.bo.xwc.components.annotations.Localize;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtJsBaseRenderer;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;
/**
 * This component renders a label for a input component
 * @author jcarreira
 *
 */
public class AttributeLabel extends ViewerOutputSecurityBase {

	@Localize
    public XUIViewStateProperty<String> text 		= new XUIViewStateProperty<String>("text", this, " Label Text ");
    
    private XUIViewStateBindProperty<Boolean> 	visible        = new XUIViewStateBindProperty<Boolean>( "visible", this, Boolean.class );
    private XUIViewStateBindProperty<Boolean> 		modelRequired  = new XUIViewStateBindProperty<Boolean>( "modelRequired", this, Boolean.class );
    private XUIViewStateBindProperty<Boolean> 		recommended    = new XUIViewStateBindProperty<Boolean>( "recommended", this, Boolean.class );
    @Localize
    private XUIViewStateBindProperty<String> 		toolTip    = new XUIViewStateBindProperty<String>( "toolTip", this, String.class );
    
    /**
     * Bean property where to keep the value of the attribute
     */
    private XUIBaseProperty<String> beanProperty         		= new XUIBaseProperty<String>( "beanProperty", this, "currentData" );
    
    /**
     * Name of the attribute of a XEO Model to bind this attributeLabel
     */
    private XUIBaseProperty<String> objectAttribute         	= new XUIBaseProperty<String>( "objectAttribute", this );
    
    /**
     * A connector to keep/retrieve the value of the attribute
     */
    protected XUIBindProperty<DataFieldConnector> dataFieldConnector = new XUIBindProperty<DataFieldConnector>( "dataFieldConnector", this, DataFieldConnector.class );

    
    
    /**
     * Initialize the component
     */
    @Override
    public void initComponent() {
        super.initComponent();
        setAttributeProperties( );
    }
    
    /**
     * 
     * Returns the value of the property dataFieldConnector
     * Property: <code>dataFieldConnector</code>
     * @return
     * 		{@link DataFieldConnector}
     */
    public DataFieldConnector getDataFieldConnector() {
        return this.dataFieldConnector.getEvaluatedValue();
    }
    
    public void setDataFieldConnector(String dataFieldExpr){
    	this.dataFieldConnector.setExpressionText( dataFieldExpr );
    }
    
    
    public void setText( String sText ) {
        this.text.setValue( sText );
    }
    
	public String getText( ) {
        return this.text.getValue();
    }
	
    public void setObjectAttribute( String objectAttribute ) {
        this.objectAttribute.setValue( objectAttribute );
    }
    
	public String getObjectAttribute( ) {
        return this.objectAttribute.getValue();
    }
	

    public void setVisible( String visible) {
    	this.visible.setExpressionText(visible);
        //this.visible.setValue( createValueExpression( visible, Boolean.class ) );
    }
    
    public boolean getVisible(){
    	return visible.getEvaluatedValue();
    }

    public boolean isVisible() {
        if( visible.getValue() != null && visible.getValue().isLiteralText() ) {
            return Boolean.parseBoolean( visible.getValue().getExpressionString() );
        }
        else if ( visible.getValue() != null ) {
             return (Boolean)visible.getValue().getValue( getELContext() );
        }
        return true;
    }

	public boolean isModelRequired() {
		Boolean ret = modelRequired.getEvaluatedValue();
		if( ret != null ) 
			return ret;
		return false;
	}

	public void setModelRequired(String requiredExpression) {
		this.modelRequired.setExpressionText( requiredExpression );
	}

	public boolean isRecommended() {
		Boolean ret = recommended.getEvaluatedValue();
		if( ret != null ) 
			return ret;
		return false;
	}

	public void setRecommended(String recommendedExpression ) {
		this.recommended.setExpressionText( recommendedExpression );
	}
	
	public String getToolTip(){
		return toolTip.getEvaluatedValue();
	}
	
	public void setToolTip(String ttipExpr){
		this.toolTip.setExpressionText( ttipExpr );
	}
	
	/**
	 * Save the object property of this component 
	 */
    public void setBeanProperty(String beanProperty) {
        this.beanProperty.setValue( beanProperty ); 
    }
    
    /**
     * Get the bean property associated with the component
     * 
     * @return String in the format of {@link ValueExpression}
     */
    public String getBeanProperty() {
        return beanProperty.getValue();
    }
	
	protected void setAttributeProperties() {
		String beanProperty = getBeanProperty();
    	String sBeanExpression = "";
    	String sObjectAttribute = getObjectAttribute();
    	if (beanProperty.startsWith("viewBean.")) //Backward compatibility
    		sBeanExpression = "#{" + getBeanProperty() + "." + sObjectAttribute;
    	else
    		sBeanExpression = "#{" + getBeanId() + "." + getBeanProperty() + "." + sObjectAttribute;
        
    	if (sObjectAttribute != null){
	        this.dataFieldConnector.setExpressionText( sBeanExpression + "}" );
	        
	        this.objectAttribute.setValue( sObjectAttribute );
	
	        // Value
	        this.setValueExpression(
	            "value", createValueExpression( sBeanExpression +  ".value}", Object.class ) 
	        );
	
	        // Label
	        if (text.isDefaultValue() ){	        	
		        this.text.setValue( (String)
		                createValueExpression( sBeanExpression + ".label}", String.class ).getValue(getELContext())
		            );
	        }
	        if (visible.isDefaultValue()){
		        this.visible.setValue( 
		                createValueExpression( sBeanExpression + ".visible}", Boolean.class ) 
		            );
	        }
	        if (modelRequired.isDefaultValue()){
		        this.modelRequired.setValue( 
		                createValueExpression( sBeanExpression + ".required}", Boolean.class ) 
		            );
	        }
	        if (recommended.isDefaultValue()){
		        this.recommended.setValue( 
		                createValueExpression( sBeanExpression + ".recomended}", Boolean.class ) 
		            );
	        }
	        
	        this.setSecurityPermissions( sBeanExpression + ".securityPermissions}" );
	
	        this.dataFieldConnector.setValue( 
	                createValueExpression( sBeanExpression + "}", DataFieldConnector.class ) 
	            );
	        
	        
	        if (toolTip.isDefaultValue()){
	        	this.toolTip.setExpressionText(sBeanExpression + ".toolTip}" );
	        }
    	}
	}

	public static class XEOHTMLRenderer extends ExtJsBaseRenderer {

		@Override
		public StateChanged wasStateChanged( XUIComponentBase component, List<XUIBaseProperty<?>> changedProperties ) {
			return StateChanged.FOR_UPDATE;
		}
		
		
		@Override
		public String getExtComponentType( XUIComponentBase oComp ) {
			return "Ext.form.Label";
		};
		
		@Override
		public void encodeBeginPlaceHolder(XUIComponentBase oAtt) throws IOException {
			super.encodeBeginPlaceHolder(oAtt);
			XUIResponseWriter w = getResponseWriter();
            w.writeAttribute( "class" , "xwc-form-label", null);
		}
		
		@Override
		public void encodeEndPlaceHolder(XUIComponentBase oAtt)
				throws IOException {
			super.encodeEndPlaceHolder( oAtt );

			AttributeLabel label = (AttributeLabel) oAtt;
			addToolTip( oAtt , label );
		}


		protected void addToolTip(XUIComponentBase oAtt, AttributeLabel label) {
			String tooltip = label.getToolTip();
			if (StringUtils.hasValue( tooltip )){
				tooltip = JavaScriptUtils.safeJavaScriptWrite( tooltip );
				StringBuilder b = new StringBuilder();
				b.append("new Ext.ToolTip({ " +
			        "target: '"+oAtt.getClientId()+"', " +
			        "html: '"+ tooltip  +"'" +
			        "});"	);
				addScript( oAtt.getClientId() , b );
			}
		}

		private String getTextValue(AttributeLabel oAttrLabel) {
			String textValue=oAttrLabel.getText();
			if (!StringUtils.isEmpty(oAttrLabel.getObjectAttribute()) &&
					StringUtils.isEmpty(oAttrLabel.getText())) {				
			}
			return textValue;
		}
		
		protected void addScript(String id, StringBuilder b) {
			getRequestContext().getScriptContext().add( XUIScriptContext.POSITION_FOOTER , id + "_ttip" , b.toString() );
		}
		
		@Override
		public boolean reRenderField( XUIComponentBase comp ) {
	    	return true;
	    }
		
		@Override
		public ExtConfig getExtJsConfig(XUIComponentBase oComp) {
			
            AttributeLabel oAttrLabel = (AttributeLabel)oComp;

            ExtConfig config = super.getExtJsConfig(oComp);
            
            //It's not working... in extjs 2.2.1
//			config.addString( "forId" , ((Attribute)oComp.getParent()).getInputComponent().getClientId() );
            
            
			config.addString( "text" , JavaScriptUtils.writeValue( getTextValue(oAttrLabel)) );
			
            if( !oAttrLabel.isVisible() )
            	config.add("hidden",true);
            
            config.addString("cls", getComponentClass(oAttrLabel) );
            
			return config;
		}
		
		public StringBuilder getComponentClass( AttributeLabel oLabel ) {
            StringBuilder cls = new StringBuilder("xwc-form-label ");

            if( oLabel.isRecommended() )
            	cls.append( "xwc-form-recommended " );

            if( oLabel.isModelRequired() )
            	cls.append( "xwc-form-required " );
            
            if (StringUtils.hasValue( oLabel.getToolTip() ) )
            	cls.append( "xwc-tooltip " );
            
            return cls;
		}
		
		@Override
		public ScriptBuilder getEndComponentScript(XUIComponentBase oComp) {
			
			ScriptBuilder s = null;
			boolean addScript = false;	
			
			if( oComp.isRenderedOnClient() ) {
				
				AttributeLabel oAttrLabel = (AttributeLabel)oComp;
				s = new ScriptBuilder();
				s.startBlock();
				super.writeExtContextVar(s, oComp);
			
				if( oComp.getStateProperty("text").wasChanged() ){
					s.w( "c.setText('" ).writeValue( oAttrLabel.getText() ).l("');");
					addScript = true;
				}

				if( oComp.getStateProperty("visible").wasChanged() ){
					s.w( "c.setVisible(" ).writeValue( oAttrLabel.isVisible() ).l(");");
					addScript = true;
				}
					
				if( oComp.getStateProperty("recommended").wasChanged() || oComp.getStateProperty("modelRequired").wasChanged() ) {
					s.s( "c.removeClass('xwc-form-recommended')");
					s.s( "c.removeClass('xwc-form-required')");
					s.w( "c.addClass('").w( getComponentClass(oAttrLabel) ).s("')");
					addScript = true;
				}
				s.endBlock();
			}
			
			if (!addScript)
				s = null;
			
			return s;
			
		}
		
		
    }

}
