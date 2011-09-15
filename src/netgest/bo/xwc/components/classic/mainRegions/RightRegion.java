package netgest.bo.xwc.components.classic.mainRegions;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * Represents a layout region that is represented as panel
 * in the east (right) side of a main viewer
 * 
 */
public class RightRegion extends BaseRegion  {

	/**
	 * The width of the panel
	 */
	private XUIBindProperty<String> width = 
		new XUIBindProperty<String>("width", this, String.class, "180" );
	
	/**
	 * The layout of the panel,
	 * "accordion" uses accordion style to separate panels
	 * "form" uses collapsible panels
	 * "fit" will try to fit the panels
	 */
	@Values({"accordion","form","fit"})
	private XUIBindProperty<String> layout = 
		new XUIBindProperty<String>("layout", this, String.class, "accordion" );
	
	
	
	

	/**
	 * 
	 * Retrieves the width of the region
	 * 
	 * @return The width of the region in pixels
	 */
	public String getWidth() {
		return width.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the width of the region (in pixels)
	 * 
	 * @param widthExpr
	 */
	public void setWidth(String widthExpr) {
		this.width.setExpressionText(widthExpr);
	}

	/**
	 * 
	 * Retrieves the layout type of the region
	 * 
	 * @return A string with the name of the layout
	 */
	public String getLayout() {
		return layout.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the layout of the region
	 * 
	 * @param layoutExpr
	 */
	public void setLayout(String layoutExpr) {
		this.layout.setExpressionText(layoutExpr);
	}

	public boolean wasStateChanged() {
		return super.wasStateChanged();
	};
		
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
	
	public static class XEOHTMLRenderer extends XUIRenderer {
		
		@Override
		public void encodeEnd(XUIComponentBase oComp) throws IOException {
			
			XUIResponseWriter w = getResponseWriter();
			w.endElement(DIV);
			
			//Render the layout at the end
			String result = " Ext.onReady(function() { Ext.getCmp('east-panel').doLayout(); });";
			getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER,
					oComp.getId(), result);
			
		}
		
		@Override
		public void encodeBegin( XUIComponentBase component ) throws IOException
	    {
			RightRegion oEastRegion = (RightRegion) component;
			
			XUIResponseWriter w = getResponseWriter();
		
			w.startElement(DIV, oEastRegion);
				w.writeAttribute(ID, "eastRegion", null);
		}
		
		
	}

	@Override
	public ExtConfig renderRegion() {
		ExtConfig rightRegion = new ExtConfig();
		rightRegion.addJSString("region", "east");
		rightRegion.addJSString("id", "east-panel");
		rightRegion.addJSString("title", getTitle());
		rightRegion.add("split",getSplit());
		rightRegion.add("collapsed",getCollapsed());
		rightRegion.add("width",getWidth());
		rightRegion.add("collapsible",getCollapsible());
		rightRegion.add("animated",true);
		rightRegion.add("autoScroll",true);
		rightRegion.add("allowDomMove", getAllowDomMove());
		rightRegion.addJSString("layout",getLayout());
		
		ExtConfig layoutConfig = rightRegion.addChild("layoutConfig");
		layoutConfig.add("titleCollapse", true);
		layoutConfig.add("animate", true);
		
		ExtConfig listeners = getListeners();
		if (listeners != null){
			rightRegion.add("listeners", listeners);
		}
		
		ExtConfigArray itemsArray = new ExtConfigArray();
		boolean hasItems = false;
		Iterator<UIComponent> it = getChildren().iterator();
		while (it.hasNext()){
			UIComponent curr = it.next();
			if (curr instanceof ExtJSRegionRenderer){
				ExtJSRegionRenderer p = (ExtJSRegionRenderer) curr;
				ExtConfig config = p.renderRegion();
				itemsArray.addChild(config);
				hasItems = true;
			}
		}
		if (!hasItems)
			rightRegion.addJSString("contentEl","eastRegion");
		else
			rightRegion.add("items", itemsArray);
		
		return rightRegion;
	}
}
