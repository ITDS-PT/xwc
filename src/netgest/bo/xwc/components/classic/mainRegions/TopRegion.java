package netgest.bo.xwc.components.classic.mainRegions;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * Represents a top region in a main viewer
 * 
 */
public class TopRegion extends ExtremeRegion implements ExtJSRegionRenderer {

		
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
		}
		
		@Override
		public void encodeBegin( XUIComponentBase component ) throws IOException
	    {
			TopRegion oNorthRegion = (TopRegion) component;
			XUIResponseWriter w = getResponseWriter();
			
//			w.startElement(DIV, oNorthRegion);
//				w.writeAttribute(ID, "northRegionRender", null);
//			w.endElement(DIV);
//			
			w.startElement(DIV, oNorthRegion);
				w.writeAttribute(ID, "northRegion", null);
		}
		
		
	}

	@Override
	public ExtConfig renderRegion() {
		
		ExtConfig topRegion = new ExtConfig();
		
		ExtConfig listeners = getListeners();
		
		topRegion.addJSString("region", "north");
		topRegion.addJSString("id", "north-panel");
		String title = getTitle();
		if (title != null && !" ".equalsIgnoreCase(title) && title.length() > 0)
			topRegion.addJSString("title", getTitle());
		topRegion.add("split",getSplit());
		
		topRegion.add("border",false);
		topRegion.add("frame",false);
		topRegion.add("allowDomMove",getAllowDomMove()); 
		topRegion.add("height",getHeight());
		topRegion.add("minSize",getMinHeight());
		topRegion.add("maxSize",getMaxHeight());
		topRegion.add("collapsible",getCollapsible());
		topRegion.add("collapsed",getCollapsed());
		topRegion.add("hideBorders",true);
		topRegion.addJSString("margins","0 0 0 0");
		
		if (listeners != null){
			topRegion.add("listeners", listeners);
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
			topRegion.addJSString("contentEl","northRegion");
		else
			topRegion.add("items", itemsArray);
		
		return topRegion;
	}
}
