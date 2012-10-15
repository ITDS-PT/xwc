package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class FlatTreePanel extends XUIComponentBase {

	/**
	 * To create a tree panel with dynamic content.
	 *	This property should return a {@link Menu} instance holding
	 * the structure of the represent
	 */
	private XUIBindProperty<Menu> root = 
		new XUIBindProperty<Menu>( "root", this,  Menu.class );

	public Menu getRoot() {
		return root.getEvaluatedValue();
	}

	public void setRoot( String rootExpr ) {
		this.root.setExpressionText( rootExpr );
	}
	
	private XUIViewProperty<String> width = 
		new XUIViewProperty<String>( "root", this, "240px" );

	public String getWidth() {
		return width.getValue();
	}

	public void setWidth( String width ) {
		this.width.setValue( width );
	}
	
	@Override
	public void initComponent(){
		super.initComponent();
		initializeTemplate( "templates/components/flatTreePanel.ftl" );
	}
	
}
