package netgest.bo.xwc.components.classic;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.event.FacesEvent;

import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMethodBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
/**
 * This component is not usable in the viewers, is the base of all attribute Type Components
 * 
 * @author jcarreira
 *
 */
public class AttributeBase extends ViewerInputSecurityBase {
    
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private boolean hasDependents = false;
    private XUIBaseProperty<String> beanProperty         		= new XUIBaseProperty<String>( "beanProperty", this, "viewBean.currentData" );
    private XUIBaseProperty<String> objectAttribute         	= new XUIBaseProperty<String>( "objectAttribute", this );
    
    private XUIBindProperty<DataFieldConnector> dataFieldConnector = new XUIBindProperty<DataFieldConnector>( "dataFieldConnector", this, DataFieldConnector.class );

    private XUIBindProperty<Boolean> isLov = 
    	new XUIBindProperty<Boolean>( "isLov", this, Boolean.class );
    
    private XUIBindProperty<Boolean> isLovEditable   = 
    	new XUIBindProperty<Boolean>( "isLovEditable", this, Boolean.class );

    private XUIMethodBindProperty validation	= 
    	new XUIMethodBindProperty( "validation", this );
    
    private XUIBindProperty<Boolean> onChangeSubmit = 
    	new XUIBindProperty<Boolean>( "onChangeSubmit", this, Boolean.class );
    
    private XUIBindProperty<String[]> dependences = 
    	new XUIBindProperty<String[]>( "dependences", this, String[].class );
    	
    private XUIBindProperty<Byte> 	dataType = 
    	new XUIBindProperty<Byte>( "dataType", this, Byte.class );
    
    private XUIBindProperty<Byte> 	inputRenderType	= 
    	new XUIBindProperty<Byte>( "inputRenderType", this, Byte.class );

    private XUIStateBindProperty<String> width = 
    	new XUIStateBindProperty<String>( "width", this, String.class );

    private XUIStateBindProperty<String> height = 
    	new XUIStateBindProperty<String>( "height", this, "100",String.class );

    private XUIBindProperty<Integer> maxLength = 
    	new XUIBindProperty<Integer>( "maxLength", this, Integer.class );

    private XUIBindProperty<Integer> maxValue = 
    	new XUIBindProperty<Integer>( "maxValue", this, Double.class );

    private XUIBindProperty<Integer> minValue = 
    	new XUIBindProperty<Integer>( "minValue", this, Double.class );
    
    private XUIBindProperty<Integer> decimalPrecision  = 
    	new XUIBindProperty<Integer>( "decimalPrecision", this, Integer.class );

    private XUIBindProperty<Integer> minDecimalPrecision  = 
    	new XUIBindProperty<Integer>( "minDecimalPrecision", this, Integer.class );

    private XUIBindProperty<Boolean> groupNumber  = 
    	new XUIBindProperty<Boolean>( "groupNumber", this, Boolean.class );

    protected XUIBaseProperty<Object> renderedValue     = 
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

    private XUIStateBindProperty<String> invalidText = 
    	new XUIStateBindProperty<String>("invalidText", this, String.class ); 
    
    /**
     * Initialize the component
     */
    @Override
    public void initComponent() {
        super.initComponent();
        //  Perform init per component
    }

    /**
     * Bind this component to a XEO Attribute using a {@linkplain DataFieldConnector}.<br>
     *
     * Property: <code>objectAttribute</code>
     *
     * @param  value
     *         The EL expression to resolve the {@link DataFieldConnector}
     *
     */
    public void setObjectAttribute(String sObjectAttribute) {
    	
        String sBeanExpression = "#{" + getBeanProperty() + "." + sObjectAttribute;
        
        this.dataFieldConnector.setExpressionText( "#{" + getBeanProperty() + "." + sObjectAttribute + "}" );
        
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
                createMethodBinding( sBeanExpression + ".validate}", Boolean.class ) 
            );

        this.onChangeSubmit.setValue( 
                createValueExpression( sBeanExpression + ".onChangeSubmit}", Boolean.class ) 
            );

        this.maxLength.setValue(  
                createValueExpression( sBeanExpression + ".maxLength}", Integer.class ) 
            );

        this.maxValue.setValue(  
                createValueExpression( sBeanExpression + ".numberMaxValue}", Integer.class ) 
            );

        this.minValue.setValue(  
                createValueExpression( sBeanExpression + ".numberMinValue}", Integer.class ) 
            );
        
        this.decimalPrecision.setValue( 
                createValueExpression( sBeanExpression + ".decimalPrecision}", Integer.class ) 
            );
        
        this.minDecimalPrecision.setValue( 
                createValueExpression( sBeanExpression + ".minDecimals}", Integer.class ) 
            );

        this.groupNumber.setValue( 
                createValueExpression( sBeanExpression + ".numberGrouping}", Boolean.class ) 
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
    
    /**
     * Returns the XEO Model attribute binding to this component
     * @return The XEO Model attribute name
     * 	
     */
    public String getObjectAttribute() {
        return this.objectAttribute.getValue();
    }
    
    /**
     * Defines the visibility of the component
     * @param visible true/false or a {@link ValueExpression}
     */
    public void setVisible( String visible) {
        this.visible.setExpressionText( visible );
    }
    
    /**
     * Returns the visibility state of the component
     * @return true/false depending on the current visibility state
     */
    public boolean isVisible() {
        return this.visible.getEvaluatedValue();
    }
    
    /**
     * Returns if the component is readOnly
     * @return true/false The component readOnly state
     */
    public boolean isReadOnly() {
    	return this.readOnly.getEvaluatedValue();
    }
    
    /**
     * Set the component readOnly state
     * @param readOnly true/false ou {@link ValueExpression}
     */
    public void setReadOnly( String readOnly ) {
    	this.readOnly.setExpressionText( readOnly );
    }
    
    /**
     * Set the display value for the component. Typical used when showing a cardId instead of the BOUI
     * @return	The current display value
     */
    public String getDisplayValue() {
		return displayValue.getEvaluatedValue();
	}
    
    /**
     * Set the display value
     * @param cardIdExpression The display value in the format of a {@linkplain ValueExpression}
     */
	public void setDisplayValue( String cardIdExpression ) {
		this.displayValue.setExpressionText( cardIdExpression );
	}

    /**
     * Set's the data type of the component.
     * @param dataType Literal or a value in the format of a {@linkplain ValueExpression}
     */
    public void setDataType(String dataType) {
        this.dataType.setExpressionText( dataType );
    }
    
    /**
     * Returns the dataType of the component
     * @return Byte representing the dataType associated to the component
     */
	public byte getDataType() {
		return this.dataType.getEvaluatedValue();
	}
	
	
	/**
	 * Returns if this value is required by the XEO Model
	 * @return true/false if it is required
	 */
    public boolean isModelRequired() {
    	return this.modelRequired.getEvaluatedValue();
    }
    
    /**
     * Set required property of the component
     * @param modelRequired true/false or a {@link ValueExpression}
     */
    public void setRequired(String modelRequired) {
    	this.modelRequired.setExpressionText( modelRequired );
    }

    /**
     * Set recommended property of the component
     * @param recomended true/false or a {@link ValueExpression}
     */
    public void setRecomended(String recomended) {
    	this.recommended.setExpressionText( recomended );
    }
    
    /**
     * Return the value of the recommended property.
     * @return	true/false 
     */
    public boolean isRecomended() {
        return this.recommended.getEvaluatedValue();
    }
    
    /**
     * Set a validation method for the component
     * @param validation as a {@linkplain MethodBinding}
     */
    public void setValidation(String validation) {
        this.validation.setExpressionText( validation );
    }
    
    /**
     * Set if the component must post the data to the server after a change in the value
     * @param onChangeSubmit true/false
     */
    public void setOnChangeSubmit( boolean onChangeSubmit) {
        this.onChangeSubmit.setExpressionText( String.valueOf( onChangeSubmit ) );
    }
    
    public void setValueChangeListener( String methodBindingExpr ) {
    	super.setValueChangeListener(
    			getFacesContext().getApplication().createMethodBinding( 
    					methodBindingExpr,
    					new Class[]  { FacesEvent.class }
    			)
    	);
    }
    
    /**
     * Set if the component must post the data to the server after a change in the value
     * @param onChangeSubmit true/false or {@linkplain ValueExpression}
     */
    public void setOnChangeSubmit(String onChangeSubmit) {
        this.onChangeSubmit.setExpressionText( onChangeSubmit  );
    }
    
    /**
     * Read the property isOnChangeSubmit of the component
     * @return true/false
     */
    public boolean isOnChangeSubmit() {
        return this.onChangeSubmit.getEvaluatedValue();
    }
    
    /**
     * Set's the component state to disabled
     * @param sDisable true/false or {@link ValueExpression}
     */
    public void setDisabled(String sDisable) {
        this.disabled.setExpressionText( sDisable );
    }

    /**
     * Returns the current disabled state of the component
     * @return true/false
     */
    public boolean isDisabled() {
        return this.disabled.getEvaluatedValue();
    }
    
    /**
     * Set's the label text associated to this component
     * @param label String or {@link ValueExpression}
     */
    public void setLabel(String label) {
        this.label.setExpressionText( label );
    }
    
    /**
     * Get's the current label of the component
     * @return	Returns the current label text of the component
     */
    public String getLabel() {
        return this.label.getEvaluatedValue();
    }

    /**
     * Set's if the component show the link in the cardId witch allow the navigation 
     * to the original XEO Model
     * 
     * @param sExpressionText true/false or a {@linkplain ValueExpression}
     */
    public void setEnableCardIdLink(String sExpressionText) {
        this.enableCardIdLink.setExpressionText( sExpressionText );
    }
    
    /**
     * Get's the current value of the property enableCardIdLink 
     * @return true / false
     */
    public boolean getEnableCardIdLink() {
        return this.enableCardIdLink.getEvaluatedValue();
    }
    
    /**
     * Set the max length of the attribute 
     * @param maxLength {@linkplain ValueExpression} with the max length of the component
     */
    public void setMaxLength(String maxLength) {
        this.maxLength.setExpressionText( maxLength );
    }

    /**
     * Set the max length of the attribute 
     * @param maxLength int with the max length of the component
     */
    public void setMaxLength( int maxLength) {
        this.maxLength.setExpressionText( String.valueOf( maxLength ) );
    }
    
    /**
     * Return the current maxLength of the component 
     * @return int with the max length of the component
     */
    public int getMaxLength() {
        return this.maxLength.getEvaluatedValue();
    }

    
    /**
     * Set the number max value of the attribute 
     * @param maxLength {@linkplain ValueExpression} with the max value of the component
     */
    public void setMaxValue( String maxValueExpr ) {
        this.maxValue.setExpressionText( maxValueExpr );
    }

    /**
     * Set the max length of the attribute 
     * @param maxValue double with the max value of the component
     */
    public void setMaxValue( double maxValue ) {
        this.maxValue.setExpressionText( String.valueOf( maxValue ) );
    }
    
    /**
     * Return the current maxLength of the component 
     * @return double with the max value of the component
     */
    public double getMaxValue() {
        return this.maxValue.getEvaluatedValue();
    }

    /**
     * Set the number min value of the attribute 
     * @param maxLength {@linkplain ValueExpression} with the min value of the component
     */
    public void setMinValue( String minValueExpr ) {
        this.minValue.setExpressionText( minValueExpr );
    }

    /**
     * Set the min length of the attribute 
     * @param maxValue double with the min value of the component
     */
    public void setMinValue( double minValue ) {
        this.minValue.setExpressionText( String.valueOf( maxValue ) );
    }
    
    /**
     * Return the current maxLength of the component 
     * @return double with the min value of the component
     */
    public double getMinValue() {
        return this.minValue.getEvaluatedValue();
    }
    
    /**
     * Get the current decimal precision of the component
     * @return int with the current decimal precision
     */
    public int getDecimalPrecision() {
        return this.decimalPrecision.getEvaluatedValue();
    }

    /**
     * Set's the decimal precision of the component, when is of the numeric type
     * @param decimalPrecision integer or {@linkplain ValueExpression}
     */
    public void setDecimalPrecision(String decimalPrecision) {
        this.decimalPrecision.setExpressionText( decimalPrecision );
    }

    /**
     * Get the current minimum decimal precision of the component, when is of the numeric type
     * @return int with the current minimum decimal precision
     */
    public int getMinDecimalPrecision() {
        return this.minDecimalPrecision.getEvaluatedValue();
    }
    
    /**
     * Set's the decimal minimum precision of the component, when is of the numeric type
     * @param decimalPrecision integer or {@linkplain ValueExpression}
     */
    public void setMinDecimalPrecision(String decimalPrecision) {
        this.minDecimalPrecision.setExpressionText( decimalPrecision );
    }

    /**
     * Get if the number should be grouped when is rendered, when is of the numeric type
     * @return boolean true - if should be grouped . false - do not group
     */
    public boolean getGroupNumber() {
        return this.groupNumber.getEvaluatedValue();
    }

    /**
     * Set's if the number should be grouped, when is of the numeric type
     * @param decimalPrecision boolean or {@linkplain ValueExpression} returning a boolean
     */
    public void setGroupNumber(String groupNumber ) {
        this.groupNumber.setExpressionText( groupNumber );
    }

    /**
     * Set the dependences of the component by the objectAttribute property. 
     * If this component have dependences when a dependency changes the values are posted to the server.
     * 
     * sDependences String[] return by a {@link ValueExpression} or a comma spared String containing the objectAttributes 
     */
    public void setDependences(String sDependences ) {
        this.dependences.setExpressionText( sDependences );
    }
    
    /**
     * Get the current dependencies of the component
     * @return String[] with objectAttribute values form which this component depends 
     */
    public String[] getDependences() {
        if( !dependences.isNull() ) {
	        if( dependences.isLiteralText() ) {
	        	if( dependences.getValue() != null ) {
	        		return String.valueOf(dependences.getValue()).split(";");
	        	}
	        }
	        else {
	            return dependences.getEvaluatedValue();
	        }
        }
        return EMPTY_STRING_ARRAY;
    }

    /**
     * Define if the component is rendered in the format of a inputLov component 
     * 
     * @param  sIsLov tru/false or a {@link ValueExpression}
     *         
     *
     */
    public void setIsLov( String sIsLov ) {
        this.isLov.setExpressionText( sIsLov );
    }
    
    /**
     * Get the value of the isLov property of the component
     * @return true/false 
     */
    public boolean isLov() {
        return this.isLov.getEvaluatedValue();
    }
    
    /**
     * Forces a lookup viewer for this XEO Model object attribute
     * @param sLookupViewerExpr Literal string with the lookup viewer name or a {@link ValueExpression} 
     */
    public void setLookupViewer( String sLookupViewerExpr ) {
        this.lookupViewer.setExpressionText( sLookupViewerExpr );
    }
    
    /**
     * Get the current forced lookup viewer name for this component.
     * @return String with the viewer name
     */
    public String getLookupViewer() {
        return this.lookupViewer.getEvaluatedValue();
    }
    
    /**
     * Set's if the values of the inputLov are editable
     * @param sIsLovEditable true/false or a {@link ValueExpression}
     */
    public void setIsLovEditable( String sIsLovEditable ) {
        this.isLovEditable.setExpressionText( sIsLovEditable );
    }
    /**
     * Get the current value of the property isLovEditable
     * @return true/false
     */
    public boolean isLovEditable() {
        return this.isLovEditable.getEvaluatedValue();
    }
    
    /**
     * Set's the lov values
     * @param sLovValues {@link ValueExpression} returning a Map<Object,String> with the lov values.
     */
    public void setLovMap( String sLovValues ) {
        this.lovMap.setExpressionText( sLovValues );
    }

    /**
     * Get the current lov Map associated with the component
     * @return  Map<Object,String> with the lov Map
     */
    public Map<Object, String> getLovMap() {
    	if( this.lovMap.getValue() != null && this.lovMap.isLiteral() ) {
        	Map<Object, String> oRetLovMap = new LinkedHashMap<Object, String>();
            String[] values = this.lovMap.getExpressionString().split(";");
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
    
    /**
     * Set the with of the component
     * @param sWidth Integer or a {@link ValueExpression}
     */
    public void setWidth( String sWidth ) {
        this.width.setExpressionText( sWidth );
    }

    /**
     * Set the height of the component, only works with multiline components like textArea and HtmlEditor
     * @param sWidth Integer or a {@link ValueExpression} 
     */
    public void setHeight( String sHeight ) {
        this.height.setExpressionText( sHeight );
    }
    
    /**
     * Get the current Height of the component
     * @return String with a integer value with the Height of the Component
     */
    public String getHeight() {
        return this.height.getEvaluatedValue();
    }
    
    /**
     * Get the current Height of the component
     * @return String with a integer value with the Width of the Component
     */
    public String getWidth() {
    	if ( !this.width.isNull() ) {
    		return this.width.getEvaluatedValue();
    	}
        return "150";
    }

    /**
     * Force the component to render the value with the specified component
     * @param inputRenderType A component name, like attributePassword or attributeText 
     */
    public void setInputRenderType(String inputRenderType) {
        this.inputRenderType.setExpressionText( inputRenderType );
    }

    /**
     * Get the forced component type to manage the value of the component 
     * @return The component name, like attributePassword or attributeText 
     */
    public byte getInputRenderType() {
    	return this.inputRenderType.getEvaluatedValue();
    }

    /**
     * Marks this component as invalid a set the invalid text 
     * @param sInvalidText The text to be displayed in a format of literal String or {@link ValueExpression} 
     */
    public void setInvalidText( String sInvalidText ) {
    	this.invalidText.setChanged( true );
    	this.invalidText.setExpressionText( sInvalidText );
    }
    
    /**
     * Get the current invalid message of the component
     * @return String with the invalid message of the Component
     */
    public String getInvalidText() {
    	return this.invalidText.getEvaluatedValue();
    }
    
    /**
     * Reset's the invalid message of the Object
     */
    public void clearInvalid() {
    	this.invalidText.setExpressionText( null );
    }
    
    
    /**
     * Check's if the component need's to be rerendered on the client side after a postback in Ajax
     */
    @Override
    public boolean wasStateChanged() {
        if( !super.wasStateChanged() ) {
        	
        	
        	//System.out.println("WASCHANGED:[" + getId() + "]" + this.renderedValue.getValue() + " [Comp] " + getValue() );
        	
        	Object value;
        	ValueExpression ve = getValueExpression("value");
        	if (ve != null) {
        	    try {
        			value = (ve.getValue(getFacesContext().getELContext()));
        		}
    		    catch (ELException e) {
        			throw new FacesException(e);
    		    }
        	}
        	else {
        		value = getValue();
        	}
        	
            if (!XUIStateProperty.compareValues( this.renderedValue.getValue(), value )) {
                return true;
            }
        }
        else {
            return true;
        }
        return false;
    }
    
    /**
     * Get the value {@link ValueExpression} associated with this component
     * @return {@link ValueExpression}
     */
    public String getValueExpression() {
		ValueExpression oExpr = getValueExpression( "value" );
		if( oExpr != null ) {
			return oExpr.getExpressionString();
		}
		return null;
	}

    /**
     * Set the value {@link ValueExpression} associated with this component
     * @param valueExpression {@link ValueExpression}
     */
	public void setValueExpression(String valueExpression) {
		ValueExpression oVExpr = createValueExpression(valueExpression, Object.class);
		this.setValueExpression( "value" , oVExpr );
		if( this.displayValue.getExpressionString() == null ) {
			this.displayValue.setValue( oVExpr );
		}
	}
    
	/**
	 * Save the object property of this component 
	 */
    @Override
    public Object saveState() {
        this.renderedValue.setValue( getValue() );
        return super.saveState();
    }

	/**
	 * Save the object property of this component 
	 */
    public void setBeanProperty(String beanProperty) {
        this.beanProperty.setValue( beanProperty ); 
        
        if( getObjectAttribute() != null )
            setObjectAttribute( getObjectAttribute() );
        
    }
    
    /**
     * Get the bean property associated with the component
     * 
     * @return String in the format of {@link ValueExpression}
     */
    public String getBeanProperty() {
        return beanProperty.getValue();
    }

    /**
     * Update the XEO Model with the submited value
     */
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
		if( !XUIStateProperty.compareValues( oRenderedValue, oCurrentValue ) ) {
			super.updateModel();
			this.renderedValue.setValue( oCurrentValue );
			
		}
        else {
            // Important! - Clear Local value
            setValue(null);
            setLocalValueSet(false);
        }
	}
	
	@Override
	public void validateModel() {
		setModelValid( true );
		this.validation.invoke();
		Boolean ret = (Boolean)this.validation.getReturnValue();
		if( ret != null ) {
			if( !ret ) {
				setModelValid( false );
				setInvalidText( "Valor inválido!" );
				if( this.dataFieldConnector.getValue() != null) {
					String sMsg = this.dataFieldConnector.getEvaluatedValue().getInvalidMessage();
					if( sMsg != null && sMsg.length() > 0 ) {
						setInvalidText(sMsg);
					}
				}
			}
			else {
				setInvalidText( null );
			}
		}
	}
}
