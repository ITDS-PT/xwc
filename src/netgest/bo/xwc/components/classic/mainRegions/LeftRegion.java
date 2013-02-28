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
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * 
 */
public class LeftRegion extends RightRegion implements ExtJSRegionRenderer {

		
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
		
		ExtConfig listeners = getListeners();
		
		ExtConfig leftRegion = new ExtConfig();
		leftRegion.addJSString("region", "west");
		leftRegion.addJSString("id", "west-panel");
		leftRegion.addJSString("title",getTitle());
		leftRegion.add("split",getSplit());
		leftRegion.add("width",getWidth());
		leftRegion.add("minSize",175);
		leftRegion.add("maxSize",400);
		leftRegion.add("collapsible",getCollapsible());
		leftRegion.add("collapsed",getCollapsed());
		leftRegion.add("animated",true);
		leftRegion.add("frame",false);
		leftRegion.add("autoScroll",true);
		leftRegion.addJSString("margins","0 0 3 3");
		leftRegion.addJSString("layout",getLayout());
		leftRegion.add("allowDomMove",getAllowDomMove());
		if (listeners != null){
			leftRegion.add("listeners", listeners);
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
			leftRegion.addJSString("contentEl","westRegion");
		else
			leftRegion.add("items", itemsArray);
		
		
		return leftRegion;
	}
	
	
	public static class XEOHTMLRenderer extends XUIRenderer {
		
		@Override
		public void encodeEnd(XUIComponentBase oComp) throws IOException {
			
			XUIResponseWriter w = getResponseWriter();
			w.endElement(DIV);
			
			//Render the layout at the end
			String result = " Ext.onReady(function() { Ext.getCmp('west-panel').doLayout(); });";
			getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER,
					oComp.getId(), result);
			
		}
		
		@Override
		public void encodeBegin( XUIComponentBase component ) throws IOException
	    {
			LeftRegion oWestRegion = (LeftRegion) component;
			
			XUIResponseWriter w = getResponseWriter();
			
//			w.startElement(DIV, oWestRegion);
//				w.writeAttribute(ID, "westRegionRender", null);
//			w.endElement(DIV);
			
			w.startElement(DIV, oWestRegion);
				w.writeAttribute(ID, "westRegion", null);
				
				
		}
		
		
	}

	
}
