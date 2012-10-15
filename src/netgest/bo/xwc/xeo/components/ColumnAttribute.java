package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.xeo.components.utils.CardIdLinkRenderer;



public class ColumnAttribute extends netgest.bo.xwc.components.classic.ColumnAttribute {
	
	private XUICommand cardIdCommand;

	public XUICommand getCardIdCommand(){
		return cardIdCommand;
	}
	
	@Override
	public void initComponent(){
		if (findComponent(getClientId() +"_cardIdLink") == null){
			XUICommand cmd = new XUICommand();
    		cmd.setActionExpression( createMethodBinding( "#{" + getBeanId() + ".openCardIdLink}") );
    		cmd.setId(getId() + "_cardIdLink");
    		this.cardIdCommand = cmd;
    		getChildren().add( cmd );
		}
	}
	
	/**
     * Whether to enable cardid links on this column or not
     */
    private XUIViewProperty<Boolean> enableCardIdLink = 
    	new XUIViewProperty<Boolean>( "enableCardIdLink", this, false );
    
    
    public boolean getEnableCardIdLink() {
		return this.enableCardIdLink.getValue().booleanValue();
	}
	
	public void setEnableCardIdLink(String isCardLinkActive){
		this.enableCardIdLink.setValue(Boolean.valueOf(isCardLinkActive));
	}
    
	/**
	 *  Whether the column is Frozen or not
	 */
	private XUIViewProperty<Boolean> frozen = 
			new XUIViewProperty<Boolean>( "frozen", this, false );
	
	public boolean getIsFrozen() {
		return this.frozen.getValue().booleanValue();
	}
	
	public void setFrozen(String isFrozen){
		this.frozen.setValue(Boolean.valueOf(isFrozen));
	}
	
	@Override
	public String getRendererType() {
		return "columnAttribute";
	}
	
	public void setObjectAttribute( String objectAttribute ) {
		super.setDataField( objectAttribute );
	}
	
	public String getObjectAttribute() {
		return super.getDataField();
	}
	
	public GridColumnRenderer getRenderer() {
		GridColumnRenderer renderer = super.getRenderer();
		if (renderer == null){
			if (getEnableCardIdLink()){
				return new CardIdLinkRenderer(this);
			}
		}
		return renderer;
		
	}

}
