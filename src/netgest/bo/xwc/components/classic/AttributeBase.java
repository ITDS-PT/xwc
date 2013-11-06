package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.annotations.Localize;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMethodBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.jsf.XUIELResolver;
import netgest.bo.xwc.framework.jsf.XUIValueChangeEvent;
import netgest.bo.xwc.framework.jsf.XUIViewHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.el.MethodBinding;
/**
 * This component is not usable in the viewers,
 * is the base of all attribute Type Components
 * 
 * @author João Carreira
 *
 */
public class AttributeBase extends ViewerInputSecurityBase {
    
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private boolean hasDependents = false;
    /**
     * Bean property where to keep the value of the attribute
     */
    private XUIBaseProperty<String> beanProperty         		= new XUIBaseProperty<String>( "beanProperty", this, "currentData" );
    /**
     * Name of the attribute of a XEO Model to bind this attribute
     */
    private XUIBaseProperty<String> objectAttribute         	= new XUIBaseProperty<String>( "objectAttribute", this );
    
    /**
     * A connector to keep/retrieve the value of the attribute
     */
    protected XUIBindProperty<DataFieldConnector> dataFieldConnector = new XUIBindProperty<DataFieldConnector>( "dataFieldConnector", this, DataFieldConnector.class );
    
    
    /**
     * Whether or not the attribute should be rendered as a list of values
     */
    private XUIBindProperty<Boolean> isLov = 
    	new XUIBindProperty<Boolean>( "isLov", this, Boolean.class );
    
    /**
     * Whether or not the values (as lov) of the attribute
     * can be changed by the user
     */
    private XUIViewBindProperty<Boolean> isLovEditable   = 
    	new XUIViewBindProperty<Boolean>( "isLovEditable", this, Boolean.class );

    /**
     * Stores the value of the validation result
     */
    private XUIBindProperty<Boolean> isValidAttribute	= 
    	new XUIBindProperty<Boolean>( "isValidAttribute", this,Boolean.TRUE ,Boolean.class );
    
    /**
     * A method to perform validation on the value of this attribute
     */
    private XUIBindProperty<Boolean> validation	= 
    	new XUIBindProperty<Boolean>( "validation", this,Boolean.TRUE ,Boolean.class );
    
    /**
     * Triggers a form submit whenever the value of this attribute
     * is changed
     */
    private XUIViewBindProperty<Boolean> onChangeSubmit = 
    	new XUIViewBindProperty<Boolean>( "onChangeSubmit", this, Boolean.class );
    
    /**
     * Dependencies of the component (the names of other attributes)
     */
    private XUIViewBindProperty<String[]> dependences = 
    	new XUIViewBindProperty<String[]>( "dependences", this, String[].class );
    	
    /**
     * The data type of this attribute (can be String, Number, Boolean)
     */
    private XUIBindProperty<Byte> 	dataType = 
    	new XUIBindProperty<Byte>( "dataType", this, Byte.class );
    
    /**
     * The type of input used by this component (
     * can be attributeText,attributeNumber, etc...)
     */
    private XUIBindProperty<Byte> 	inputRenderType	= 
    	new XUIBindProperty<Byte>( "inputRenderType", this, Byte.class );

    /**
     * The width of the component
     */
    private XUIViewStateBindProperty<String> width = 
    	new XUIViewStateBindProperty<String>( "width", this, String.class );

    /**
     * The height of the component
     */
    private XUIViewStateBindProperty<String> height = 
    	new XUIViewStateBindProperty<String>( "height", this, String.class );

    /**
     * The maximum length of the component
     */
    private XUIViewBindProperty<Integer> maxLength = 
    	new XUIViewBindProperty<Integer>( "maxLength", this, Integer.class );

    /**
     * The maximum value of the component
     */
    private XUIBindProperty<Integer> maxValue = 
    	new XUIBindProperty<Integer>( "maxValue", this, Double.class );

    /**
     * The minimum value of the component
     */
    private XUIBindProperty<Integer> minValue = 
    	new XUIBindProperty<Integer>( "minValue", this, Double.class );
    
    /**
     * The decimal precision for the value of the component
     */
    private XUIViewBindProperty<Integer> decimalPrecision  = 
    	new XUIViewBindProperty<Integer>( "decimalPrecision", this, Integer.class );

    /**
     * The minimal decimal precision for the value of the component
     */
    private XUIViewBindProperty<Integer> minDecimalPrecision  = 
    	new XUIViewBindProperty<Integer>( "minDecimalPrecision", this, Integer.class );

    /**
     * If the value (only works with numeric values) of the component
     * should be groups (ex: 1000000 becomes 1.000.000)
     */
    private XUIViewBindProperty<Boolean> groupNumber  = 
    	new XUIViewBindProperty<Boolean>( "groupNumber", this, Boolean.class );

    /**
     * The rendered value of the component
     */
    protected XUIBaseProperty<Object> renderedValue     = 
    	new XUIBaseProperty<Object>( "renderedValue", this, Object.class );

    public Object getRenderedValue(){
    	return renderedValue.getValue();
    }
    
    /**
     * Whether or not the component is disabled
     */
    private XUIViewStateBindProperty<Boolean> disabled       	= 
    	new XUIViewStateBindProperty<Boolean>( "disabled", this, Boolean.class );
    
    /**
     * Whether or not the component is read-only (value cannot be changed)
     */
    private XUIViewStateBindProperty<Boolean> readOnly       		= 
    	new XUIViewStateBindProperty<Boolean>( "readOnly", this, Boolean.class );
    
    /**
     * Whether or not the component is visible
     */
    private XUIViewStateBindProperty<Boolean> visible        	= 
    	new XUIViewStateBindProperty<Boolean>( "visible", this, "true",Boolean.class );
    
    /**
     * Whether or not the value of the component is required 
     */
    private XUIViewStateBindProperty<Boolean> modelRequired  	= 
    	new XUIViewStateBindProperty<Boolean>( "modelRequired", this,"false", Boolean.class );
    
    /**
     * 
     * Whether or not the value of the component is recommended to be filled 
     * 
     * */
    private XUIViewStateBindProperty<Boolean> recommended     	= 
    	new XUIViewStateBindProperty<Boolean>( "recommended", this,"false", Boolean.class );
    
    /**
     * The label of this attribute
     */
    @Localize
    private XUIViewStateBindProperty<String> label          	= 
    	new XUIViewStateBindProperty<String>( "label", this, String.class );

    /**
     * Whether or not a link should created when displaying the value of the attribute
     * attributes whose value is a reference to another object can have a link that opens
     * a viewer with the respective object
     */
    private XUIViewStateBindProperty<Boolean> enableCardIdLink  = 
    	new XUIViewStateBindProperty<Boolean>( "enableCardIdLink", this, "false",Boolean.class );

    /**
     * The value to display in the attribute
     */
    private XUIViewStateBindProperty<String> displayValue = 
    	new XUIViewStateBindProperty<String>( "displayValue", this, String.class );
    
    /**
     * Name of the lookup viewer to use with this attribute
     */
    private XUIBindProperty<String> lookupViewer = 
    	new XUIBindProperty<String>( "lookupViewer", this, String.class );
    
    /**
     * The list of values for attribute (if it is a lov)
     */
    private XUIBindProperty<Map<Object,String>> lovMap = 
    	new XUIBindProperty<Map<Object,String>>( "lovMap", this, Map.class );

    /**
     * The text to show when the attribute's value is invalid
     */
    private XUIViewStateBindProperty<String> invalidText = 
    	new XUIViewStateBindProperty<String>("invalidText", this, String.class ); 
    
    
    /**
     * Whether or not to show the favorites (only applicable to NumberLookup and BridgeLookup)
     * 
     */
    private XUIViewStateBindProperty<Boolean> showFavorites = 
    	new XUIViewStateBindProperty<Boolean>( "showFavorites", this, "false", Boolean.class);
    
    /**
     * Retrieves the list of bouis to show for the favorites
     */
    private XUIViewStateBindProperty<List<Long>> listFavorites = 
    	new XUIViewStateBindProperty<List<Long>>( "listFavorites", this, List.class);
    
    
    /**
     * Name of the attribute of a XEO Model to bind this attribute
     */
    private XUIViewStateBindProperty<Boolean> enableFullTextSearch  = 
    	new XUIViewStateBindProperty<Boolean>( "enableFullTextSearch", this, "false",Boolean.class );
    
    /**
     * Associates to a method that returns the list of results for a lookup
     * Must return a JSONArray containing a set of JSONObjects each with two entries:
     * key and value. Key is the BOUI of the object retrieved and value is the value to present to the user 
     * ( can have HTML )
     */
    XUIMethodBindProperty lookupResults = 
        	new XUIMethodBindProperty( "lookupResults", this  );
    
    
    /**
     * Tooltip for this component
     */
    XUIBindProperty< String > toolTip = new XUIBindProperty< String >(
			"toolTip" , this , String.class );

	public String getToolTip() {
		return toolTip.getEvaluatedValue();
	}

	public void setToolTip(String newValExpr) {
		toolTip.setExpressionText( newValExpr );
	}
	
	 /**
     * Tooltip for this component
     */
    XUIBaseProperty< Boolean > onChangeSubmitLockScreen = new XUIBaseProperty< Boolean >(
			"onChangeSubmitLockScreen" , this , Boolean.FALSE );

	public Boolean getOnChangeSubmitLockScreen() {
		return onChangeSubmitLockScreen.getValue();
	}

	public void setOnChangeSubmitLockScreen(String newValue) {
		onChangeSubmitLockScreen.setValue(Boolean.parseBoolean(newValue ));
	}
	
	public void setOnChangeSubmitLockScreen(Boolean newValue) {
		onChangeSubmitLockScreen.setValue(newValue );
	}
    
    /**
     * Initialize the component
     */
    @Override
    public void initComponent() {
        super.initComponent();
        setAttributeProperties( );
    }
    
    @Override
    public void preRender() {
    	super.preRender();
    }

	/**
	 * Sets the rendered value to the current value, so that the {@link #updateModel()}
	 * method can work correctly when it need to compare the rendered and current value
	 */
	private void setRenderedValueForModelUpdate() {
		this.renderedValue.setValue( getValue() );
	}
	
    public void initSpecificSettings(){
    	
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
	
	        // Config
	        this.dataType.setValue( 
	                createValueExpression( sBeanExpression + ".dataType}", Byte.class ) 
	            );
	        
	        if (inputRenderType.isDefaultValue()){
		        this.inputRenderType.setValue( 
		                createValueExpression( sBeanExpression + ".inputRenderType}", Byte.class ) 
		            );
	        }
	        
	        if (validation.isDefaultValue()){
		        this.validation.setValue( 
		        		createValueExpression( sBeanExpression + ".validate}", Boolean.class ) 
		            );
	        }
	        
	        if (isValidAttribute.isDefaultValue()){
		        this.isValidAttribute.setValue( 
		        		createValueExpression( sBeanExpression + ".valid}", Boolean.class ) 
		            );
	        }
	        
	        if (onChangeSubmit.isDefaultValue()){
		        this.onChangeSubmit.setValue( 
		                createValueExpression( sBeanExpression + ".onChangeSubmit}", Boolean.class ) 
		            );
	        }
	
	        if (maxLength.isDefaultValue()){
		        this.maxLength.setValue(  
		                createValueExpression( sBeanExpression + ".maxLength}", Integer.class ) 
		            );
	        }
	        
	        if (maxValue.isDefaultValue()){
		        this.maxValue.setValue(  
		                createValueExpression( sBeanExpression + ".numberMaxValue}", Double.class ) 
		            );
	        }
	
	        if (minValue.isDefaultValue()){
		        this.minValue.setValue(  
		                createValueExpression( sBeanExpression + ".numberMinValue}", Double.class ) 
		            );
	        }
	        
	        if (decimalPrecision.isDefaultValue()){
		        this.decimalPrecision.setValue( 
		                createValueExpression( sBeanExpression + ".decimalPrecision}", Integer.class ) 
		            );
	        }
	        
	        if (minDecimalPrecision.isDefaultValue()){
		        this.minDecimalPrecision.setValue( 
		                createValueExpression( sBeanExpression + ".minDecimals}", Integer.class ) 
		            );
	        }
	
	        if (groupNumber.isDefaultValue()){
		        this.groupNumber.setValue( 
		                createValueExpression( sBeanExpression + ".numberGrouping}", Boolean.class ) 
		            );
	        }
	
	        // Label
	        if (label.isDefaultValue()){
		        this.label.setValue( 
		                createValueExpression( sBeanExpression + ".label}", String.class ) 
		            );
	        }
	
	        // States 
	        if (disabled.isDefaultValue()){
		        this.disabled.setValue( 
		                createValueExpression( sBeanExpression + ".disabled}", Boolean.class ) 
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
	        
	        // Dependeces
	        this.dependences.setValue( 
	                createValueExpression( sBeanExpression + ".dependences}", String[].class ) 
	            );
	        
	        // Lovs
	        if (isLov.isDefaultValue()){
		        this.isLov.setValue( 
		                createValueExpression( sBeanExpression + ".isLov}", Boolean.class ) 
		            );
	        }
	        
	        if (this.isLovEditable.isDefaultValue()){
		        this.isLovEditable.setValue( 
		                createValueExpression( sBeanExpression + ".isLovEditable}", Boolean.class ) 
		            );
	        }
	
	        if (this.lovMap.isDefaultValue()){
	        	this.lovMap.setExpressionText( sBeanExpression + ".lovMap}" );
	        }
	
	        this.dataFieldConnector.setValue( 
	                createValueExpression( sBeanExpression + "}", DataFieldConnector.class ) 
	            );
	        
	        if (displayValue.isDefaultValue()){
	        	this.displayValue.setExpressionText(sBeanExpression + ".displayValue}" );
	        }
	        
	        if (toolTip.isDefaultValue()){
	        	this.toolTip.setExpressionText(sBeanExpression + ".toolTip}" );
	        }
    	}
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
    	
    	objectAttribute.setValue(sObjectAttribute);
    	
    	
        
        
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
    
    /**
     * 
     * Returns the the value of the property valid
     * 
     * @return True if the component is valid and false otherwise
     */
    public boolean getIsValid(){
    	return isValidAttribute.getEvaluatedValue();
    }
    
    public void setIsValid(String validExpr){
    	this.isValidAttribute.setExpressionText(validExpr);
    }
    
    public void setIsValid(Boolean val){
    	this.isValidAttribute.setValue(val);
    }
    
    public String getLookupResults(){
    	return lookupResults.getExpressionString();
    }
    
    public void setLookupResults(String queryExpr){
    	lookupResults.setExpressionText(queryExpr);
    	lookupResults.setValue(createMethodBinding(queryExpr, String.class));
    }
    
    public String getLookupResults(String filter){
    	lookupResults.invoke(new Object[]{ filter, this });
		return (String) lookupResults.getReturnValue();
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
     * 
     * Sets the required status
     * 
     * @param modelRequiredExpr
     */
    public void setModelRequired(String modelRequiredExpr){
    	this.modelRequired.setExpressionText(modelRequiredExpr);
    }
    /**
     * Set recommended property of the component
     * @param recomended true/false or a {@link ValueExpression}
     */
    public void setRecomended(String recomended) {
    	this.recommended.setExpressionText( recomended );
    }
    
    /**
     * Set recommended property of the component
     * 
     * @param recomendedExpr
     */
    public void setRecommended(String recomendedExpr){
    	this.recommended.setExpressionText( recomendedExpr );
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
    					new Class[]  { XUIValueChangeEvent.class }
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
     * 
     * Sets the search property of the component (only applies in relations)
     * 
     * @param enableSearch
     */
    public void setEnableFullTextSearch(String enableSearchExpr){
    	this.enableFullTextSearch.setExpressionText(enableSearchExpr);
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
     * 
     * Retrieves whether or not the full text search property is enabled
     * 
     * @return True if the property is enabled and false otherwise
     */
    public boolean getEnableFullTextSearch(){
    	return this.enableFullTextSearch.getEvaluatedValue();
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
        Object x = this.maxValue.getEvaluatedValue();
        if( x == null ) {
        	return 0;
        }
        if( x != null && !(x instanceof Double) ) {
        	return Double.parseDouble( x.toString() );
        }
        return (Double)x;
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
        this.minValue.setExpressionText( String.valueOf( minValue ) );
    }
    
    /**
     * Return the current maxLength of the component 
     * @return double with the min value of the component
     */
    public double getMinValue() {
        Object x = this.minValue.getEvaluatedValue();
        if( x == null ) {
        	return 0;
        }
        if( !(x instanceof Double) ) {
        	return Double.parseDouble( x.toString() );
        }
        return (Double)x;
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
     *
     * 
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
    	
    	final Map<Object, String> oRetLovMap = new LinkedHashMap<Object, String>();
    	try{
    		XUIELResolver.setContext( this );
    		if( this.lovMap.getValue() != null && this.lovMap.isLiteral() ) {
	        	String[] values = this.lovMap.getExpressionString().split(";");
	            for( String lovValue : values  ) {
	            	oRetLovMap.put( lovValue , lovValue);
	            }
	        }
	        else if( this.lovMap.getValue() != null ) {
	             return this.lovMap.getEvaluatedValue();
	        }
	    	
    	} finally{
    		XUIELResolver.setContext( null );
    	}
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
     * Set the height of the component, only works with multi-line components
     *  like textArea and HtmlEditor
     *  
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
    	
    	checkAndUpdateRenderedValue();
    	
        return super.saveState();
    }

	/**
	 * Update the rendered value (if not saving in cache, because
	 * when saving in cache the value of the component is not yet ready)
	 */
	private void checkAndUpdateRenderedValue() {
		if (!XUIViewHandler.isSavingInCache())
    		setRenderedValueForModelUpdate();
	}

	/**
	 * Save the object property of this component 
	 */
    public void setBeanProperty(String beanProperty) {
        this.beanProperty.setValue( beanProperty ); 
        
        /*if( getObjectAttribute() != null )
            setObjectAttribute( getObjectAttribute() );*/
        
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
     * 
     * Whether to show the favorites or not (Lookups only)
     * 
     * @return True to show the lookups and false otherwise
     */
    public Boolean getShowFavorites(){
    	return this.showFavorites.getEvaluatedValue();
    }
    
    /**
     * 
     * Show or hide the favorites
     * 
     * @param favoritesExpr
     */
    public void setShowFavorites(String favoritesExpr){
    	this.showFavorites.setExpressionText(favoritesExpr);
    }
    
    /**
     * 
     * Retrieves a list of bouis to show as favorites
     * 
     * @return A list of bouis
     */
    public List<Long> getListFavorites(){
    	return this.listFavorites.getEvaluatedValue();
    }
    
    public void setListFavorites(String lstFavoritesExpr){
    	this.listFavorites.setExpressionText(lstFavoritesExpr);
    }

    /**
     * Update the XEO Model with the submited value (depends on the {@link #saveState()}
     * method correctly setting the renderedValue property with the last rendered value
     */
	@Override
	public void updateModel() {
		
		// Compare values, update model only if the value was changed.
		Object oRenderedValue = this.renderedValue.getValue();
        Object oCurrentValue  = this.getLocalValue();
		 
		// Se estivermos a comparar strings troca null por ""
		// Quando os valores null vao para o cliente, ao serem submetidos vem sempre
		// como string vazia. Iria submeter + vezes que as necessarias ao modelo o valor
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
	
	private void validateAttribute(){
		this.validation.getEvaluatedValue();
	}
	
	@Override
	public void validateModel() {
		setModelValid( true );
		validateAttribute();
		if( !getIsValid() ) {
			setModelValid( false );
			if( this.dataFieldConnector.getValue() != null) {
				String sMsg = this.dataFieldConnector.getEvaluatedValue().getInvalidMessage();
				if( sMsg != null && sMsg.length() > 0 ) {
					setInvalidText(sMsg);
				}
			} else {
				String invalidText = getInvalidText();
				if (invalidText != null && invalidText.length() > 0)
					setInvalidText(invalidText);
				else
					setInvalidText( "Valor inválido!" );
			}
		} 
	}
	
	@Override
	public String toString() {
		return getObjectAttribute() + " " + getId();
	}
	
}
