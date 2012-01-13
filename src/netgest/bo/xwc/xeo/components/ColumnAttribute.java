package netgest.bo.xwc.xeo.components;

import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.grid.CardIdLinkRenderer;
import netgest.bo.xwc.framework.XUIViewProperty;



public class ColumnAttribute extends netgest.bo.xwc.components.classic.ColumnAttribute {
	
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
				return new CardIdLinkRenderer();
			}
		}
		return null;
	}

}
