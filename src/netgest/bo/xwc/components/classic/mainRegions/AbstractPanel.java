package netgest.bo.xwc.components.classic.mainRegions;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * Represents an abstract Panel component with its properties(without renderer)
 * 
 * @author
 *
 */
public abstract class AbstractPanel extends XUIComponentBase implements ExtJSRegionRenderer {

	/**
	 * The width of the panel (defaults to auto)
	 * 
	 */
	private XUIBindProperty<String> width = 
		new XUIBindProperty<String>("width", this, String.class, "200" );
	
	/**
	 * Whether to show the border of the panel or not
	 * 
	 */
	private XUIBindProperty<Boolean> border = 
		new XUIBindProperty<Boolean>("border", this, Boolean.class, "true" );
	
	/**
	 * The title of the panel
	 * 
	 */
	private XUIBindProperty<String> title = 
		new XUIBindProperty<String>("title", this, String.class, "" );
	
	
	/**
	 * Whether or not the panel is collapsible (defaults to true)
	 */
	private XUIBindProperty<Boolean> collapsible = 
		new XUIBindProperty<Boolean>("collapsible", this, Boolean.class, "false" );
	
	
	/**
	 * Whether or not the panel is collapsed (defaults to false so that the panel is expanded)
	 */
	private XUIBindProperty<Boolean> collapsed = 
		new XUIBindProperty<Boolean>("collapsed", this, Boolean.class, "false" );
	
	/**
	 * Whether to render the panel with custom round borders
	 */
	private XUIBindProperty<Boolean> frame = 
		new XUIBindProperty<Boolean>("frame", this, Boolean.class, "false" );
	
	
	public String getWidth(){
		return width.getEvaluatedValue();
	}
	
	public void setWidth(String widthExpr){
		width.setExpressionText(widthExpr);
	}
	
	public String getTitle(){
		return title.getEvaluatedValue();
	}
	
	public void setTitle(String titleExpr){
		title.setExpressionText(titleExpr);
	}
	
	
	public Boolean getBorder(){
		return border.getEvaluatedValue();
	}
	
	public void setBorder(String borderExpr){
		border.setExpressionText(borderExpr);
	}
	
	
	public Boolean getCollapsible(){
		return collapsible.getEvaluatedValue();
	}
	
	public void setCollapsible(String collapsibleExpr){
		collapsible.setExpressionText(collapsibleExpr);
	}
	
	public Boolean getCollapsed(){
		return collapsed.getEvaluatedValue();
	}
	
	public void setCollapse(String collapsedExpr){
		collapsed.setExpressionText(collapsedExpr);
	}
	
	
	public Boolean getFrame(){
		return frame.getEvaluatedValue();
	}
	
	public void setFrame(String frameExpr){
		frame.setExpressionText(frameExpr);
	}
	
	public ExtConfig renderRegion(){
		return null;
	}
	
	
}
