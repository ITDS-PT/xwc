package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class GroupHeader extends XUIComponentBase {

	private XUIBaseProperty<String> label = new XUIBaseProperty<String>( "label", this );
	
	public void setLabel(String label){
		this.label.setValue( label );
	}
	
	public String getLabel(){
		return label.getValue();
	}

}
