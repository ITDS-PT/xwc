package netgest.bo.xwc.components.classic.mainRegions;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * Represents a base region with common properties for other regions
 * to reuse
 *
 */
public abstract class BaseRegion extends XUIComponentBase implements ExtJSRegionRenderer {

	
	/**
	 * The title of the panel
	 */
	private XUIBindProperty<String> title = 
		new XUIBindProperty<String>("title", this, String.class, " " );

	/**
	 * Whether the panel is split (has an expandable border)
	 */
	private XUIBindProperty<Boolean> split = 
		new XUIBindProperty<Boolean>("split", this, Boolean.class, "true" );
	
	/**
	 * Whether the panel starts collapsed (defaults to false to show at the beginning)
	 */
	private XUIBindProperty<Boolean> collapsed = 
		new XUIBindProperty<Boolean>("collapsed", this, Boolean.class, "false" );
	
	/**
	 * Whether the panel can be collapsed (defaults to true)
	 */
	private XUIBindProperty<Boolean> collapsible = 
		new XUIBindProperty<Boolean>("collapsible", this, Boolean.class, "true" );
	
	/**
	 * Allow dom Move
	 */
	private XUIBindProperty<Boolean> allowDomMove = 
		new XUIBindProperty<Boolean>("allowDomMove", this, Boolean.class, "false" );

	
	/**
	 * 
	 * Retrieves the title of the region
	 * Note, for the panel to be colapsible a title must be set
	 * 
	 * @return A string with the title
	 */
	public String getTitle() {
		return title.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the title of the region
	 * 
	 * @param titleExpr
	 */
	public void setTitle(String titleExpr) {
		this.title.setExpressionText(titleExpr);
	}

	/**
	 * 
	 * Returns whether or not the region is split
	 * (has a border that the user can use to expand the
	 * size of the region)
	 * 
	 * @return True if the region is split and false otherwise
	 */
	public Boolean getSplit() {
		return split.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets whether the region is split
	 * 
	 * @param splitExpr
	 */
	public void setSplit(String splitExpr) {
		this.split.setExpressionText(splitExpr);
	}

	/**
	 * 
	 * Retrieves whether the region is collapsed
	 * from the beggining or not
	 * 
	 * @return True if the region is collapsed and false otherwise
	 */
	public Boolean getCollapsed() {
		return collapsed.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the collapsed setting
	 * 
	 * @param collapsed
	 */
	public void setCollapsed(String collapsed) {
		this.collapsed.setExpressionText(collapsed);
	}

	/**
	 * 
	 * Retrieves whether the region is collapsible or not
	 * 
	 * @return True if the region is collapsible and false otherwise
	 */
	public Boolean getCollapsible() {
		return collapsible.getEvaluatedValue();
	}
	
	public ExtConfig getListeners(){
		ExtConfig listeners = new ExtConfig();
		if (getSplit())
			listeners.add( "'resize'" , "function(c,aW,aH,rW,rH){ ExtXeo.layoutMan.doLayout('"+XUIRequestContext.getCurrentContext().getViewRoot().getClientId()+"')}");
		else
			return null;
		return listeners;
	}

	/**
	 * 
	 * Sets the collapsible setting
	 * 
	 * @param collapsible
	 */
	public void setCollapsible(String collapsible) {
		this.collapsible.setExpressionText(collapsible);
	}
	
	public Boolean getAllowDomMove(){
		return allowDomMove.getEvaluatedValue();
	}
	
	public void setAllowDomMove(String domMoveExpr){
		allowDomMove.setExpressionText(domMoveExpr);
	}
	
	
}
