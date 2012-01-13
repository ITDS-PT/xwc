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
    		cmd.setActionExpression( createMethodBinding( "#{viewBean.openCardIdLink}") );
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
		GridColumnRenderer rendererer = super.getRenderer();
		if (rendererer == null){
			if (getEnableCardIdLink()){
				return new CardIdLinkRenderer(this);
			}
		}
		return null;
	}

}
