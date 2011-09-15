package netgest.bo.xwc.components.classic.mainRegions;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;

import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * 
 * 
 * 
 */
public class CollapsiblePanel extends AbstractPanel{

	/**
	 * An icon for the panel title
	 */
	private XUIBaseProperty<String> icon = 
		new XUIBaseProperty<String>("icon", this, "" );
	
	/**
	 * 
	 * Sets the panel title's icon
	 * 
	 * @param iconVal Path to the icon
	 */
	public void setIcon(String iconVal){
		this.icon.setValue(iconVal);
	}
	
	/**
	 * 
	 * Retrieves the panel titl'es icon
	 * 
	 * @return A path to the icon
	 */
	public String getIcon(){
		return this.icon.getValue();
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
	
	public ExtConfig getListeners(){
		 ExtConfig listeners = new ExtConfig();
		 listeners.add( "'expand'" , "function(p){ ExtXeo.layoutMan.doLayout('"+XUIRequestContext.getCurrentContext().getViewRoot().getClientId()+"')}");
		 return listeners;
	}
	
	@Override
	public ExtConfig renderRegion() {
		
		
		ExtConfig configListeners = getListeners();
		
		ExtConfig panel = new ExtConfig("Ext.Panel");
		if (getIcon() != null && !"".equalsIgnoreCase(getIcon()))
			panel.addJSString("title", "<img style='padding-right:3px;vertical-align:bottom;' src='"+getIcon()+"' /> " + getTitle());
		else
			panel.addJSString("title",getTitle());
		panel.add("collapsible", true);
		panel.add("collapsed", false);
		
		//panel.add("width", 200);
		panel.add("border", false);
		panel.add("frame", false);
		panel.addJSString("id", "p_"+getClientId());
		panel.addJSString("contentEl",getClientId());
		
		if( configListeners != null ) {
			panel.add( "listeners", configListeners );
		}
		
		return panel;
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
			CollapsiblePanel oPanel = (CollapsiblePanel) component;
			XUIResponseWriter w = getResponseWriter();
			
			w.startElement(DIV, oPanel);
				w.writeAttribute(ID, oPanel.getClientId(), null);
			
			if (!oPanel.isRenderedOnClient()){
				ExtConfig config = oPanel.renderRegion();
				StringBuilder b = config.renderExtConfig();
				getRequestContext().getScriptContext().
					add(XUIScriptContext.POSITION_FOOTER, 
							"dynamicPanel" + oPanel.getClientId(), b);
			}
		}
		
		
	}

	
}
