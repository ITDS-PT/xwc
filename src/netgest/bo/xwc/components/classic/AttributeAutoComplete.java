package netgest.bo.xwc.components.classic;

import netgest.bo.def.boDefAttribute;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.jsf.XUIValueChangeEvent;

import java.math.BigDecimal;

import javax.faces.context.FacesContext;

public class AttributeAutoComplete extends AttributeNumberLookup {
	
	public enum SearchType{
		WORD,
		CHARACTER
	}
	
	/**
	 * Search type - word or character based
	 * default is character based
	 */
	@Values({"WORD","CHARACTER"})
	private XUIBaseProperty<String> searchType = 
		new XUIBaseProperty<String>( "searchType", this, SearchType.CHARACTER.toString() );
	
	public SearchType getSearchType() {
		String value = this.searchType.getValue();
    	if( value != null ) {
    		return SearchType.valueOf( value );
    	}
    	return null;
	}
	
	
	public void setSearchType( String newValueExpr ) {
		searchType.setValue( newValueExpr );
	}
	
	
	
	@Override
	public void setLookupResults(String queryExpr) {
		lookupResults.setExpressionText( queryExpr , new Class<?>[]{String.class, AttributeBase.class} );
	}
	
	public XUICommand getLookupCommand(){
		return super.getLookupCommand();
	}
	
	public XUICommand getOpenCommand(){
		return super.getOpenCommand();
	}
	
	@Override
	public void initSpecificSettings() {
		if (maxItems.isDefaultValue()){
			if ( isXEOEnabled() ){
					XEOObjectAttributeConnector connector = (XEOObjectAttributeConnector) getDataFieldConnector( );
					boDefAttribute attributeMetadata = connector.getBoDefAttribute( );
					if (boDefAttribute.ATTRIBUTE_OBJECT.equals( attributeMetadata.getAtributeDeclaredType( ) ) ){
						maxItems.setValue( 1 );
					}
					else if ( boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equals( attributeMetadata.getAtributeDeclaredType( ) ) ){
						if (attributeMetadata.getMaxOccurs() < Integer.MAX_VALUE){
							maxItems.setValue( attributeMetadata.getMaxOccurs() );
						} else {
							maxItems.setValue( Integer.MAX_VALUE );
						}
					}
			} else {
				maxItems.setValue(10);
			}
		}
		
	}
	
	protected void includeHeaderScript(String id, String scriptPath){
		getRequestContext().getScriptContext().addInclude( XUIScriptContext.POSITION_FOOTER,
				id, 
				scriptPath );
	}
	
	protected void includeHeaderCss(String id, String cssPath){
		getRequestContext().getStyleContext().addInclude( 
				XUIScriptContext.POSITION_FOOTER,
				id, 
				cssPath );
	}
	
	/**
	 * The maximum number of items that can be added to the attribute
	 */
	private XUIBindProperty<Integer> maxItems = 
		new XUIBindProperty<Integer>( "maxItems", this, Integer.class, "1" );
	
	public Integer getMaxItems(){
		return this.maxItems.getEvaluatedValue();
	}
	
	public void setMaxItems(String maxItemsExpr){
		this.maxItems.setExpressionText( maxItemsExpr );
	}
	
	/**
	 * Message to show when the user selected the input to start typing
	 */
	 XUIBindProperty<String> typeMessage = 
		new XUIBindProperty<String>( "typeMessage", this, String.class );
	
	public String getTypeMessage(){
		return this.typeMessage.getEvaluatedValue();
	}
	
	public void setTypeMessage(String typeHelpExpr){
		this.typeMessage.setExpressionText( typeHelpExpr );
	}
	
	/**
	 * Minimal number of characters required for searching
	 * Default value is 3 chars
	 */
	 XUIBaseProperty<Integer> minSearchChars = 
		new XUIBaseProperty<Integer>( "minSearchChars", this, Integer.valueOf(3) );
	
	public Integer getMinSearchChars(){
		//The component uses < instead of <= to decide the number of characters
		return minSearchChars.getValue( ) - 1;
	}
	
	public void setMinSearchChars(int value){
		minSearchChars.setValue(value);
	}
	
	public void setMinSearchChars(String value){
		minSearchChars.setValue(Integer.valueOf( value ) );
	}
	
	/**
	 * CSS class to apply to element with the "Search Here" message
	 * default class is "xwc-initial-text"
	 */
	 XUIBaseProperty< String > initialTextClass = new XUIBaseProperty< String >( "initialTextClass" ,
			this, "" );

	public String getInitialTextClass() {
		return initialTextClass.getValue( );
	}

	public void setInitialTextClass(String value) {
		initialTextClass.setValue( value );
	}
	
	/**
	 * CSS class to apply to elements in the search result
	 * default class is "xwc-lookup-element"
	 */
	XUIBaseProperty< String > resultTextClass = new XUIBaseProperty< String >( "resultTextClass" ,
			this, "" );

	public String getResultTextClass() {
		return resultTextClass.getValue( );
	}

	public void setResultTextClass(String value) {
		resultTextClass.setValue( value );
	}
	
	/**
	 * Class applied to selected elements 
	 * Default is "xwc-selected-element"
	 */
	XUIBaseProperty<String> selectedElementClass = 
			new XUIBaseProperty<String>( "selectedElementClass" , this, "" );

	public String getSelectedElementClass() {
		return selectedElementClass.getValue( );
	}

	public void setSelectedElementClass(String value) {
		selectedElementClass.setValue( value );
	}
	
	/**
	 * Delay between ajax requests (bigger delay, lower server time request) - in miliseconds
	 * Default value is 200 ms
	 */
	XUIBaseProperty< Integer > searchDelay = new XUIBaseProperty< Integer >( "searchDelay" ,
			this, 200 );

	public Integer getSearchDelay() {
		return searchDelay.getValue( );
	}

	public void setSearchDelay(Integer value) {
		searchDelay.setValue( value );
	}
	
	public void setSearchDelay(String value) {
		searchDelay.setValue(Integer.valueOf( value) );
	}
	
	/**
	 * Whether or not to allow wildcard searches
	 */
	XUIBindProperty< Boolean > allowWildCardSearch = new XUIBindProperty< Boolean >(
			"allowWildCardSearch" , this , Boolean.class, "true" );

	public Boolean getAllowWildCardSearch() {
		return allowWildCardSearch.getEvaluatedValue();
	}

	public void setAllowWildCardSearch(String newValExpr) {
		allowWildCardSearch.setExpressionText( newValExpr );
	}
	
	@Override
	public void initComponent() {
		super.initComponent( );
		setAttributeProperties();
		if (lookupResults.isDefaultValue( ) ){
			lookupResults.setExpressionText( "#{" + getBeanId( ) + ".getAutoCompleteSearchResult}", new Class<?>[]{String.class, AttributeBase.class} );
		}
		
		if (!isRenderedOnClient()){
			includeHeaderCss( "autoComplete_css", "ext-xeo/autocomplete/style.css" );
			includeHeaderScript( "autoComplete_js","ext-xeo/autocomplete/jquery.fcbkcomplete.js" );
		}
	}
	
	/**
     * 
     * Returns whether or not the component is enabled
     * 
     * @return True if the component is visible and enabled, false otherwise (disabled, readOnly, not write permissions)
     */
    public boolean isUsable(){
    	boolean disabled = isDisabled();
		boolean read = isReadOnly();
		boolean writePermissions = getEffectivePermission(SecurityPermissions.WRITE);
		boolean visible = isVisible();
		if (disabled || read || !writePermissions || !visible){
			return false;
		}
		return true;
    }
    
    @Override
    public boolean isReadOnly() {
    	return false;
    }
	
	/**
	 * Whether or not the component is connector to an XEO Object
	 * 
	 * @return
	 */
	public boolean isXEOEnabled(){
		return getDataFieldConnector() instanceof XEOObjectAttributeConnector;
	}

	@Override
	public void validate( FacesContext context ) {
        Object      oSubmitedValue = getSubmittedValue();
        String      sSubmitedValue = null;
        BigDecimal  oSubmitedBigDecimal;
        
        Object oldValue = getValue();
        
        if( oSubmitedValue != null )
        {
            sSubmitedValue = (String) oSubmitedValue;     
            if( sSubmitedValue.length() > 0 )
            {
                try {
                	//Esta validacao nao faz nada de momento, foi so para 
                	//que não estoirasse com os bouis separados por virgula quando é mais do que um valor
                    //oSubmitedBigDecimal = new BigDecimal( String.valueOf( sSubmitedValue ) );
                    setValue( oSubmitedValue );
                    //Since we're overring  the validate, we need to send 
                    //activate the value change listeners
                    if (!compareValue(oldValue, oSubmitedValue))
                    	queueEvent(new XUIValueChangeEvent(this, oldValue, oSubmitedValue));
                }
                catch( NumberFormatException ex ) {
                    getRequestContext().addMessage( getClientId(), 
                    		new XUIMessage(
                                XUIMessage.TYPE_MESSAGE,
                                XUIMessage.SEVERITY_ERROR,
                                getLabel(),
                                ComponentMessages.VALUE_ERROR_ON_FORMAT.toString( oSubmitedValue )
                           )
			        );
                    setValid( false );
                }
            }
            else {
                setValue( null );
            }
        }
    }
	
	public void setDataFieldConnector(String connectorExpr){
		this.dataFieldConnector.setExpressionText( connectorExpr );
	}
	

}
