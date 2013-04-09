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
 * Component that represents a bottom region in a viewer
 * 
 */
public class BottomRegion extends ExtremeRegion implements ExtJSRegionRenderer {

	
		
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
			BottomRegion oSouthRegion = (BottomRegion) component;
			
			XUIResponseWriter w = getResponseWriter();
			
//			w.startElement(DIV, oSouthRegion);
//				w.writeAttribute(ID, "southRegionRender", null);
//			w.endElement(DIV);
			
			w.startElement(DIV, oSouthRegion);
				w.writeAttribute(ID, "southRegion", null);
		}
		
		
	}

	@Override
	public ExtConfig renderRegion() {
		
			ExtConfig listeners = getListeners();
		
			ExtConfig bottomRegion = new ExtConfig();
			bottomRegion.addJSString("region", "south");
			bottomRegion.addJSString("id", "south-panel");
			bottomRegion.addJSString("title", getTitle());
			bottomRegion.add("allowDomMove",getAllowDomMove());
			bottomRegion.add("split",getSplit());
			bottomRegion.add("collapsed",getCollapsed());
			bottomRegion.add("height",getHeight());
			bottomRegion.add("minSize",getMinHeight());
			bottomRegion.add("maxSize",getMaxHeight());
			bottomRegion.add("collapsible",getCollapsible());
			bottomRegion.add("animated",true);
			bottomRegion.add("border",true);
			
			if (listeners != null){
				bottomRegion.add("listeners", listeners);
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
				bottomRegion.addJSString("contentEl","southRegion");
			else
				bottomRegion.add("items", itemsArray);
		
		return bottomRegion;
		
	}
}
