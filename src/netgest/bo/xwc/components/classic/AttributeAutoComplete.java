package netgest.bo.xwc.components.classic;

import java.math.BigDecimal;

import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.jsf.XUIValueChangeEvent;

public class AttributeAutoComplete extends AttributeNumberLookup {
	
	public enum SearchType{
		WORD,
		CHARACTER
	}
	
	/**
	 * Whether or not to use character based search (each character triggers a search on the server)
	 */
	private XUIBaseProperty<String> searchType = 
		new XUIBaseProperty<String>( "searchType", this, SearchType.WORD.toString() );
	
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
	
	public XUICommand getLookupCommand(){
		return super.getLookupCommand();
	}
	
	public XUICommand getOpenCommand(){
		return super.getOpenCommand();
	}
	
	/**
	 * Whether or not to use character based search (each character triggers a search on the server)
	 */
	private XUIBindProperty<String> template = 
		new XUIBindProperty<String>( "template", this, String.class );
	
	public String getTemplate() {
		return template.getEvaluatedValue();
	}
	
	public void setTemplate( String templateExpr ) {
		this.template.setExpressionText( templateExpr );
	}
	
	/**
	 * The method to retrieve the list of values from (defaults to getLookupResults(String filter)
	 */
	private XUIBindProperty<String> lookupMethod = new XUIBindProperty<String>( "lookupMethod", this, String.class, "autoCompleteSearchResult" );

	public String getLookupMethod() {
		return lookupMethod.getEvaluatedValue();
	}

	public void setLookupMethod( String lookupMethod ) {
		this.lookupMethod.setExpressionText( lookupMethod );
	}
	
	
	private XUIBaseProperty<String> objectName = 
		new XUIBaseProperty<String>( "objectName", this );
	
	public String getObjectName() {
		return objectName.getValue();
	}

	public void setObjectName( String objectName ) {
		this.objectName.setValue( objectName );
	}

	private XUIBaseProperty<String> attributeName = 
		new XUIBaseProperty<String>( "attributeName", this );

	public String getAttributeName() {
		return attributeName.getValue();
	}

	public void setAttributeName( String attributeName ) {
		this.attributeName.setValue( attributeName );
	}
	
	
	/**
	 * The maximum number of items that can be added to the attribute
	 */
	private XUIBindProperty<Integer> maxItems = 
		new XUIBindProperty<Integer>( "maxItems", this, Integer.class );
	
	public Integer getMaxItems(){
		return this.maxItems.getEvaluatedValue();
	}
	
	public void setMaxItems(String maxItemsExpr){
		this.maxItems.setExpressionText( maxItemsExpr );
	}
	
	/**
	 * Message to show when the user selected the input to start typing
	 */
	private XUIBindProperty<String> typeMessage = 
		new XUIBindProperty<String>( "typeMessage", this, String.class );
	
	public String getTypeMessage(){
		return this.typeMessage.getEvaluatedValue();
	}
	
	public void setTypeMessage(String typeHelpExpr){
		this.typeMessage.setExpressionText( typeHelpExpr );
	}
	
	/**
     * 
     * Returns whether or not the component is enabled
     * 
     * @return True if the component is visible and enabled, false otherwise (disabled, readOnly, not write permissions)
     */
    public boolean isUsable(){
    	/*boolean disabled = isDisabled();
		boolean read = isReadOnly();
		boolean writePermissions = getEffectivePermission(SecurityPermissions.WRITE);
		boolean visible = isVisible();
		if (disabled || read || !writePermissions || !visible){
			return false;
		}*/
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
                	//FIXME: Esta validação não faz nada de momento, foi só para 
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