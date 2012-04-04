
package netgest.bo.xwc.components.classic.mainRegions;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
import netgest.bo.xwc.components.classic.mainRegions.BottomRegion;
import netgest.bo.xwc.components.classic.mainRegions.LeftRegion;
import netgest.bo.xwc.components.classic.mainRegions.RightRegion;
import netgest.bo.xwc.components.classic.mainRegions.TopRegion;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;

/**
 * The component that creates the application Workspace
 * 
 * @author jcarreira
 * 
 */
public class RegionLayout extends XUIComponentBase {

	XUIBindProperty<String> applicationType = new XUIBindProperty<String>(
			"applicationType", this, "TREE_BASED", String.class);

	public String getApplicationType() {
		return applicationType.getEvaluatedValue();
	}

	public void setApplicationType(String applicationType) {
		this.applicationType.setExpressionText(applicationType);
	}

	public void initComponent() {
		XUICommand showUserPropsCmd = (XUICommand) this
				.findComponent("showUserPropsCmd");

		if (showUserPropsCmd == null) {
			showUserPropsCmd = new XUICommand();
			showUserPropsCmd.setId("showUserPropsCmd");
			showUserPropsCmd
					.setActionExpression(createMethodBinding("#{" + getBeanId() + ".showUserProperties}"));
			this.getChildren().add(showUserPropsCmd);
		}
		//Add the script
		getRequestContext().getScriptContext().addInclude(XUIScriptContext.POSITION_HEADER, "regionLayout", "ext-xeo/xeo-regionlayout.js");
	}

	
	
	
	public ExtConfigArray buildAllRegions(){
		
		ExtConfigArray allRegions = new ExtConfigArray();
		List<UIComponent> children = this.getChildren();
		for(UIComponent child: children){
			if (child instanceof TopRegion)
				allRegions.addChild(((TopRegion)child).renderRegion());
			else if (child instanceof LeftRegion)
				allRegions.addChild(((LeftRegion)child).renderRegion());
			else if (child instanceof RightRegion)
				allRegions.addChild(((RightRegion)child).renderRegion());
			else if (child instanceof BottomRegion)
				allRegions.addChild(((BottomRegion)child).renderRegion());
		}
		allRegions.add("this.tabPanel");
		return allRegions;
		
	}
	
	
	
	
	protected Renderer getRenderer(UIComponent oComp, FacesContext context) {

		String rendererType = oComp.getRendererType();
		Renderer result = null;
		if (rendererType != null) {
			result = context.getRenderKit().getRenderer(oComp.getFamily(),
					rendererType);
		}
		return result;
	}
		
	/**
	 * 
	 * Builds the javascript to create the the tab panel 
	 * 
	 * @return
	 */
	public ExtConfig buildTabPanel(){
		
		TabPanel panel = (TabPanel) this.findComponent(TabPanel.class);
		return panel.renderRegion();
		
	}

	public static class XEOHTMLRenderer extends XUIRenderer {
		public ExtConfig oExtToolBar;

		@Override
		public void encodeEnd(XUIComponentBase oComp) throws IOException {
			
			XUIResponseWriter w = getResponseWriter();
			
			RegionLayout oMainLayout = (RegionLayout) oComp;

			/*
			 * <div id='topdownMenu' style="height:'50px'"></div> <div
			 * id='workArea'></div>
			 */
			
			w.startElement(DIV, oMainLayout);
			w.writeAttribute(ID, "topdownMenu", null);
			w.endElement(DIV);
			w.startElement(DIV, oMainLayout);
			w.writeAttribute(ID, "workArea", null);
			w.endElement(DIV);
			
			w.startElement(DIV, oMainLayout);
			w.writeAttribute(ID, "tabPanelRender", null);
			w.endElement(DIV);

			StringBuilder layoutBuilder = new StringBuilder();
			
			layoutBuilder.append("var xeoUserDisplayName = null;");
			layoutBuilder.append("XEOLayout = function(){    this.desktop = new XEOLayout.ViewPort(); };");
			layoutBuilder.append("XEOLayout.closeWindow = function() {	ExtXeo.destroyComponents( document.body );};");
			
			layoutBuilder.append("XEOLayout.ViewPort = function(){");
			layoutBuilder.append(" this.toolBar = null;");
			layoutBuilder.append("this.tabPanel = ");
			
			layoutBuilder.append(oMainLayout.buildTabPanel().renderExtConfig());
			layoutBuilder.append(";");
			
			
			
			layoutBuilder.append("var layoutItems = [];");
			layoutBuilder.append("layoutItems = ");
			ExtConfigArray test = oMainLayout.buildAllRegions();
			layoutBuilder.append(test.renderExtConfig());
			layoutBuilder.append(";");
			

			//layoutBuilder.append("var myItems = [{layout: 'border' , el:'formMain', items: layoutItems}];");
				
			//Build the view port with the created layout items
			layoutBuilder.append("XEOLayout.ViewPort.superclass.constructor.call( this,{");
			layoutBuilder.append("layout:'border', id:'viewPort', frame:false, allowDomMove:true, border:false, " +
					"items: layoutItems"); 
			layoutBuilder.append("} );"); //End ViewPort Constructor
			
			layoutBuilder.append("};"); //Close XEOLayout.ViewPort function
			
//			//Important
			layoutBuilder.append("Ext.extend(XEOLayout.ViewPort, Ext.Viewport, {    openTab : function( sFormName, sCommandName ) " +
					" {  XVW.openCommandTab( 'Viewer1'+(new Date()-100), sFormName, sCommandName );    }, " +
					" closeTab : function( tab )   { " + 
					" var tabs = this.findById('app-tabpanel'); " + 
					" tabs.remove( tab );   } } );"	);
			
			w.getScriptContext().add(XUIScriptContext.POSITION_HEADER,
					"xeo-dynamic-workspace", layoutBuilder);
			
			StringBuilder TreeScript = new StringBuilder();
			TreeScript.append("var XApp = null;");
			TreeScript.append("XEOLayoutInit = function() {");
			TreeScript.append("if( window.xeodmstate ) { }");
			TreeScript.append("else { xeodmstate = false; }");

			TreeScript.append("XApp = new XEOLayout();");
			TreeScript.append("XApp.desktop.syncSize();");
			TreeScript.append("window.onunload = XEOLayout.closeWindow;");
			TreeScript
					.append("window.setInterval(\"XVW.keepAlive( document.getElementsByTagName('form')[0] );\" ,7*60000);");
			TreeScript.append("}; Ext.onReady( function(){ XEOLayoutInit();  XVW.appendPanels(); } ) ;");
						
			w.getScriptContext().add(XUIScriptContext.POSITION_HEADER,
					"xeo-dynamic-menu", TreeScript);

			if (this.oExtToolBar != null) {
				this.oExtToolBar.setVarName("mainToolBar");

				w.getScriptContext().add(XUIScriptContext.POSITION_HEADER,
						oMainLayout.getId() + "_tb",
						this.oExtToolBar.renderExtConfig());
			}
		}

		@Override
		public void encodeChildren(XUIComponentBase oComp) throws IOException {
			Iterator<UIComponent> oChildIterator;
			UIComponent oChildComp;
			oChildIterator = oComp.getChildren().iterator();
			while (oChildIterator.hasNext()) {
				oChildComp = oChildIterator.next();

				if (oChildComp instanceof ToolBar) {
					if (!oChildComp.isRendered()) {
						return;
					}
					String rendererType;
					rendererType = oChildComp.getRendererType();
					if (rendererType != null) {
						Renderer renderer = getRenderer(oChildComp,
								XUIRequestContext.getCurrentContext()
										.getFacesContext());
						if (renderer != null) {
							this.oExtToolBar = ((ExtJsRenderer) renderer)
									.getExtJsConfig((XUIComponentBase) oChildComp);
						}
					}
				}
				else{
					oChildComp.encodeAll(getFacesContext());
					
				}
			}
		}

		protected Renderer getRenderer(UIComponent oComp, FacesContext context) {

			String rendererType = oComp.getRendererType();
			Renderer result = null;
			if (rendererType != null) {
				result = context.getRenderKit().getRenderer(oComp.getFamily(),
						rendererType);
			}
			return result;
		}

		@Override
		public boolean getRendersChildren() {
			return true;
		}
	}
}


