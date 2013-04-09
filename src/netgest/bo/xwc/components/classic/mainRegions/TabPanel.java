package netgest.bo.xwc.components.classic.mainRegions;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * The Tab Panel where XEO Viewers are displayed (as tabs)
 * 
 */
public class TabPanel extends XUIComponentBase implements ExtJSRegionRenderer{

	/**
	 * The body style of the panel
	 */
	private XUIBindProperty<String> bodyStyle = 
		new XUIBindProperty<String>("bodyStyle", this, String.class, "" );
	
	/**
	 * The default width for a tab
	 */
	private XUIBindProperty<String> tabWidth = 
		new XUIBindProperty<String>("tabWidth", this, String.class, "170" );
	
	/**
	 * 
	 * Retrieves the Body Panel style
	 * 
	 * 
	 * @return A body panel style
	 */
	public String getBodyStyle() {
		return bodyStyle.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the body panel
	 * 
	 * @param bodyPanelExpr
	 */
	public void setBodyStyle(String bodyPanelExpr) {
		this.bodyStyle.setExpressionText(bodyPanelExpr);
	}
	
	
	/**
	 * 
	 * Retrieve the tab width (defaults to 170)
	 * 
	 * @return The width for tabs
	 */
	public String getTabWidth(){
		return this.tabWidth.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the tabs width
	 * 
	 * @param tabWidthExpr (A number or expression)
	 */
	public void setTabWidth(String tabWidthExpr){
		this.tabWidth.setExpressionText(tabWidthExpr);
	}
	
		
	@Override
	public void initComponent() {
		super.initComponent();
	}

	@Override
	public void preRender() {
	}
	
	@Override
	public Object saveState() {				
		return super.saveState();
	}
	
	@Override
	public ExtConfig renderRegion() {
		
		ExtConfig treePanel = new ExtConfig("Ext.TabPanel");
		treePanel.addJSString("region", "center");
		treePanel.addJSString("id", "app-tabpanel");
		treePanel.add("deferredRender", true);
		treePanel.add("activeTab", 0);
		treePanel.add("resizeTabs", true);
		treePanel.add("minTabWidth", 100);
		treePanel.add("tabWidth", getTabWidth());
		treePanel.add("enableTabScroll", true);
		treePanel.add("width", 600);
		treePanel.add("height", 250);
		treePanel.addJSString("margins", "0 3 3 0");
		treePanel.add("autodestroy", true);
		treePanel.add("allowDomMove", false);
		treePanel.addJSString("bodyStyle", getBodyStyle());
		ExtConfig defaults = treePanel.addChild("defaults");
		defaults.add("autoScroll", true);
		treePanel.add("items", null);
		treePanel.add("listeners", getListeners());
		
		return treePanel;
	}

	@Override
	public ExtConfig getListeners() {
		//TabPanel has
		ExtConfig listeners = new ExtConfig();
		listeners.add("beforeremove", "{ "+
    			" fn:function( oTabCont, oComp ) { "+
    			" return XEOLayout.onCloseTab( oTabCont, oComp ); "+
    			" } }");
		
		return listeners;
		
		
	}	
	
}
