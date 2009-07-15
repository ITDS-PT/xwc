package netgest.bo.xwc.components.classic;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.el.ValueExpression;

import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;

public class AttributeBase extends ViewerInputSecurityBase {
    
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private boolean hasDependents = false;
    private XUIBaseProperty<String> beanProperty         		= new XUIBaseProperty<String>( "beanProperty", this, "viewBean.currentData" );
    private XUIBaseProperty<String> objectAttribute         	= new XUIBaseProperty<String>( "objectAttribute", this );
    
    private XUIBindProperty<DataFieldConnector> dataFieldConnector = new XUIBindProperty<DataFieldConnector>( "dataFieldConnector", this, DataFieldConnector.class );

    private XUIStateBindProperty<Boolean> isLov = 
    	new XUIStateBindProperty<Boolean>( "isLov", this, Boolean.class );
    
    private XUIStateBindProperty<Boolean> isLovEditable   = 
    	new XUIStateBindProperty<Boolean>( "isLovEditable", this, Boolean.class );

    private XUIStateBindProperty<Boolean> validation	= 
    	new XUIStateBindProperty<Boolean>( "valid", this, Boolean.class );
    
    private XUIStateBindProperty<Boolean> onChangeSubmit = 
    	new XUIStateBindProperty<Boolean>( "onChangeSubmit", this, Boolean.class );
    
    private XUIStateBindProperty<String[]> dependences = 
    	new XUIStateBindProperty<String[]>( "dependences", this, Boolean.class );
    	
    private XUIStateBindProperty<Byte> 	dataType = 
    	new XUIStateBindProperty<Byte>( "dataType", this, Byte.class );
    
    private XUIStateBindProperty<Byte> 	inputRenderType	= 
    	new XUIStateBindProperty<Byte>( "inputRenderType", this, Byte.class );

    private XUIStateBindProperty<String> width = 
    	new XUIStateBindProperty<String>( "width", this, String.class );

    private XUIStateBindProperty<String> height = 
    	new XUIStateBindProperty<String>( "height", this, "100",String.class );

    private XUIStateBindProperty<Integer> maxLength = 
    	new XUIStateBindProperty<Integer>( "maxLength", this, Integer.class );
    
    private XUIStateBindProperty<Integer> decimalPrecision  = 
    	new XUIStateBindProperty<Integer>( "decimalPrecision", this, Integer.class );

    private XUIBaseProperty<Object> renderedValue     = 
    	new XUIBaseProperty<Object>( "renderedValue", this, Object.class );

    private XUIStateBindProperty<Boolean> disabled       	= 
    	new XUIStateBindProperty<Boolean>( "disabled", this, Boolean.class );
    
    private XUIStateBindProperty<Boolean> readOnly       		= 
    	new XUIStateBindProperty<Boolean>( "readOnly", this, Boolean.class );
    
    private XUIStateBindProperty<Boolean> visible        	= 
    	new XUIStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );
    
    private XUIStateBindProperty<Boolean> modelRequired  	= 
    	new XUIStateBindProperty<Boolean>( "modelRequired", this, Boolean.class );
    
    private XUIStateBindProperty<Boolean> recommended     	= 
    	new XUIStateBindProperty<Boolean>( "recommended", this, Boolean.class );
    
    private XUIStateBindProperty<String> label          	= 
    	new XUIStateBindProperty<String>( "label", this, String.class );

    private XUIStateBindProperty<Boolean> enableCardIdLink  = 
    	new XUIStateBindProperty<Boolean>( "enableCardIdLink", this, "false",Boolean.class );

    private XUIStateBindProperty<String> displayValue = 
    	new XUIStateBindProperty<String>( "displayValue", this, String.class );
    
    private XUIBindProperty<String> lookupViewer = 
    	new XUIBindProperty<String>( "lookupViewer", this, String.class );
    
    private XUIBindProperty<Map<Object,String>> lovMap = 
    	new XUIBindProperty<Map<Object,String>>( "lovMap", this, Map.class );
    
    @Override
    public void initComponent() {
        super.initComponent();
        //  Perform init per component
    }

    /**
     * Define o atributo de um objecto XEO ao qual o componente fica associado. 
     * 
     * @property objectAttribute
     * @propertyType baseProperty
     *
     * @param  value
     *         Array that is the source of characters
     *
     */
    public void setObjectAttribute(String sObjectAttribute) {
    	
        String sBeanExpression = "#{" + getBeanProperty() + "." + sObjectAttribute;
        
        this.objectAttribute.setValue( sObjectAttribute );

        // Value
        this.setValueExpression(
            "value", createValueExpression( sBeanExpression +  ".value}", Object.class ) 
        );

        // Config
        this.dataType.setValue( 
                createValueExpression( sBeanExpression + ".dataType}", Byte.class ) 
            );
        this.inputRenderType.setValue( 
                createValueExpression( sBeanExpression + ".inputRenderType}", Byte.class ) 
            );
        
        this.validation.setValue( 
                createValueExpression( sBeanExpression + ".valid}", Boolean.class ) 
            );

        this.onChangeSubmit.setValue( 
                createValueExpression( sBeanExpression + ".onChangeSubmit}", Boolean.class ) 
            );

        this.maxLength.setValue(  
                createValueExpression( sBeanExpression + ".maxLength}", Integer.class ) 
            );

        this.decimalPrecision.setValue( 
                createValueExpression( sBeanExpression + ".decimalPrecision}", Integer.class ) 
            );
        
        // Label

        this.label.setValue( 
                createValueExpression( sBeanExpression + ".label}", String.class ) 
            );

        // States 

        this.disabled.setValue( 
                createValueExpression( sBeanExpression + ".disabled}", Boolean.class ) 
            );
        this.visible.setValue( 
                createValueExpression( sBeanExpression + ".visible}", Boolean.class ) 
            );
        this.modelRequired.setValue( 
                createValueExpression( sBeanExpression + ".required}", Boolean.class ) 
            );
        this.recommended.setValue( 
                createValueExpression( sBeanExpression + ".recomended}", Boolean.class ) 
            );

        this.setSecurityPermissions( sBeanExpression + ".securityPermissions}" );
        
        // Dependeces
        this.dependences.setValue( 
                createValueExpression( sBeanExpression + ".dependences}", String[].class ) 
            );
        
        // Lovs 
        this.isLov.setValue( 
                createValueExpression( sBeanExpression + ".isLov}", Boolean.class ) 
            );
        this.isLovEditable.setValue( 
                createValueExpression( sBeanExpression + ".isLovEditable}", Boolean.class ) 
            );

        this.lovMap.setExpressionText( sBeanExpression + ".lovMap}" );

        this.dataFieldConnector.setValue( 
                createValueExpression( sBeanExpression + "}", DataFieldConnector.class ) 
            );
        
        this.displayValue.setExpressionText(sBeanExpression + ".displayValue}" );
        
    }
    
    public DataFieldConnector getDataFieldConnector() {
        return this.dataFieldConnector.getEvaluatedValue();
    }

    public String getObjectAttribute() {
        return this.objectAttribute.getValue();
    }

    public void setVisible( String visible) {
        this.visible.setExpressionText( visible );
    }

    public boolean isVisible() {
        return this.visible.getEvaluatedValue();
    }
    
    public boolean isReadOnly() {
    	return this.readOnly.getEvaluatedValue();
    }

    public void setReadOnly( String readOnly ) {
    	this.readOnly.setExpressionText( readOnly );
    }
    
    public String getDisplayValue() {
		return displayValue.getEvaluatedValue();
	}

	public void setDisplayValue( String cardIdExpression ) {
		this.displayValue.setExpressionText( cardIdExpression );
	}

    
    public void setDataType(String dataType) {
        this.dataType.setExpressionText( dataType );
    }

    @Override
	public void validateModel() {
//    	boolean isValid = false;
//    	if( validation.getValue() != null && validation.getValue().isLiteralText() ) {
//    		isValid = Boolean.parseBoolean( validation.getValue().getExpressionString() );
//        }
//        else if ( validation.getValue() != null ) {
//        	isValid = (Boolean)validation.getValue().getValue( getELContext() );
//        }
//    	if( !isValid ) {
//    		setValid( isValid );
//    	}
    }

	public byte getDataType() {
		return this.dataType.getEvaluatedValue();
	}

    public boolean isModelRequired() {
    	return this.modelRequired.getEvaluatedValue();
    }

    public void setRequired(String recomended) {
    	this.modelRequired.setExpressionText( recomended );
    }

    public void setRecomended(String recomended) {
    	this.recommended.setExpressionText( recomended );
    }

    public boolean isRecomended() {
        return this.recommended.getEvaluatedValue();
    }

    public void setValidation(String validation) {
        this.validation.setExpressionText( validation );
    }

    public void setOnChangeSubmit(String onChangeSubmit) {
        this.onChangeSubmit.setExpressionText( onChangeSubmit  );
    }

    public boolean isOnChangeSubmit() {
        return this.onChangeSubmit.getEvaluatedValue();
    }

    public void setDisabled(String sDisable) {
        this.disabled.setExpressionText( sDisable );
    }

    public boolean isDisabled() {
        return this.disabled.getEvaluatedValue();
    }

    public void setLabel(String label) {
        this.label.setExpressionText( label );
    }

    public String getLabel() {
        return this.label.getEvaluatedValue();
    }

    public void setEnableCardIdLink(String sExpressionText) {
        this.enableCardIdLink.setExpressionText( sExpressionText );
    }

    public boolean getEnableCardIdLink() {
        return this.enableCardIdLink.getEvaluatedValue();
    }
    
    public void setMaxLength(String maxLength) {
        this.maxLength.setExpressionText( maxLength );
    }
    
    public int getMaxLength() {
        return this.maxLength.getEvaluatedValue();
    }

    public void setDecimalPrecision(String decimalPrecision) {
        this.dataType.setExpressionText( decimalPrecision );
    }

    public int getDecimalPrecision() {
        return this.decimalPrecision.getEvaluatedValue();
    }

    public void setDependences(String sDependences ) {
        this.dependences.setExpressionText( sDependences );
    }

    public String[] getDependences() {
        if( !dependences.isNull() ) {
	        if( dependences.isLiteralText() ) {
	            return dependences.getExpressionString().split(";");
	        }
	        else {
	             return (String[])dependences.getValue().getValue( getELContext() );
	        }
        }
        return EMPTY_STRING_ARRAY;
    }

    /**
     * Define se o component é rendarizado em formato de combobox. 
     * 
     * @property isLov
     * @propertyType stateBindProperty
     * @propertyValue boolean
     *
     * @param  sIsLov
     *         Expresão EL que deve retornar true ou false.
     *
     */
    public void setIsLov( String sIsLov ) {
        this.isLov.setExpressionText( sIsLov );
    }

    public boolean isLov() {
        return this.isLov.getEvaluatedValue();
    }

    public void setLookupViewer( String sLookupViewerExpr ) {
        this.lookupViewer.setExpressionText( sLookupViewerExpr );
    }

    public String getLookupViewer() {
        return this.lookupViewer.getEvaluatedValue();
    }
    
    public void setIsLovEditable( String sIsLovEditable ) {
        this.isLovEditable.setExpressionText( sIsLovEditable );
    }

    public boolean isLovEditable() {
        return this.isLovEditable.getEvaluatedValue();
    }

    public void setLovMap( String sLovValues ) {
        this.lovMap.setExpressionText( sLovValues );
    }

    public Map<Object, String> getLovMap() {
    	if( this.lovMap.getValue() != null && this.lovMap.getValue().isLiteralText() ) {
        	Map<Object, String> oRetLovMap = new LinkedHashMap<Object, String>();
            String[] values = this.lovMap.getValue().getExpressionString().split(";");
            for( String lovValue : values  ) {
            	oRetLovMap.put( lovValue , lovValue);
            }
        }
        else if( this.lovMap.getValue() != null ) {
             return this.lovMap.getEvaluatedValue();
        }
    	final Map<Object, String> oRetLovMap = new LinkedHashMap<Object, String>();
    	return oRetLovMap;
    }

    public void setHasDependents(boolean hasDependents) {
        this.hasDependents = hasDependents;
    }

    public boolean getHasDependents() {
        return hasDependents;
    }

    public void setWidth( String sWidth ) {
        this.width.setExpressionText( sWidth );
    }

    public void setHeight( String sHeight ) {
        this.height.setExpressionText( sHeight );
    }

    public String getHeight() {
        return this.height.getEvaluatedValue();
    }
    
    public String getWidth() {
    	if ( !this.width.isNull() ) {
    		return this.width.getEvaluatedValue();
    	}
        return "150";
    }

    public void setInputRenderType(String inputRenderType) {
        this.inputRenderType.setExpressionText( inputRenderType );
    }

    public byte getInputRenderType() {
    	return this.inputRenderType.getEvaluatedValue();
    }

    @Override
    public boolean wasStateChanged() {
        if( !super.wasStateChanged() ) {
            if (!XUIStateProperty.compareValues( this.renderedValue.getValue(), getValue() )) {
                return true;
            }
        }
        else {
            return true;
        }
        return false;
    }

    public String getValueExpression() {
		ValueExpression oExpr = getValueExpression( "value" );
		if( oExpr != null ) {
			return oExpr.getExpressionString();
		}
		return null;
	}

	public void setValueExpression(String valueExpression) {
		ValueExpression oVExpr = createValueExpression(valueExpression, Object.class);
		this.setValueExpression( "value" , oVExpr );
		if( this.displayValue.getExpressionString() == null ) {
			this.displayValue.setValue( oVExpr );
		}
	}
    
    @Override
    public Object saveState() {
        this.renderedValue.setValue( getValue() );
        return super.saveState();
    }

    public void setBeanProperty(String beanProperty) {
        this.beanProperty.setValue( beanProperty ); 
        
        if( getObjectAttribute() != null )
            setObjectAttribute( getObjectAttribute() );
        
    }

    public String getBeanProperty() {
        return beanProperty.getValue();
    }

	@Override
	public void updateModel() {
		
		// Compare values, update model only if the value was changed.
		Object oRenderedValue = this.renderedValue.getValue();
        Object oCurrentValue  = this.getLocalValue();
		 
		// Se estivermos a comparar strings troca null por ""
		// Quando os valores null vao para o cliente, ao serem submetidos vï¿½m sempre
		// como string vazia. Iria submeter + vezes que as necessï¿½rias ao modelo o valor
		if( oCurrentValue == null && oRenderedValue instanceof String ) {
		        oCurrentValue = "";
		}

		if( oRenderedValue == null && oCurrentValue instanceof String ) {
			oRenderedValue = "";
		}
		
		// Se os valores forem diferentes, submete a alteracao ao modelo.
		if( !XUIStateBindProperty.compareValues( oRenderedValue, oCurrentValue ) ) {
			super.updateModel();
		}
        else {
            // Important! - Clear Local value
            setValue(null);
            setLocalValueSet(false);
        }
	}
}
