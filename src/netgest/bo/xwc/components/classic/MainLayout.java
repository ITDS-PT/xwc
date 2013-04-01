package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.utils.IProfileUtils;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.extjs.ExtJsRenderer;
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
public class MainLayout extends XUIComponentBase {

	XUIBindProperty<String> applicationType = new XUIBindProperty<String>(
			"applicationType", this, "TREE_BASED", String.class);

	public String getApplicationType() {
		return applicationType.getEvaluatedValue();
	}

	public void setApplicationType(String applicationType) {
		this.applicationType.setExpressionText(applicationType);
	}
	
	XUIBindProperty< Boolean > renderPropertiesBtn = new XUIBindProperty< Boolean >(
			"renderPropertiesBtn" , this , Boolean.class, "true" );

	public Boolean getRenderPropertiesBtn() {
		return renderPropertiesBtn.getEvaluatedValue();
	}

	public void setRenderPropertiesBtn(String newValExpr) {
		renderPropertiesBtn.setExpressionText( newValExpr );
	}
	
	XUIBindProperty< Boolean > renderProfilesList = new XUIBindProperty< Boolean >(
			"renderProfilesList" , this , Boolean.class, "true" );

	public Boolean getRenderProfilesList() {
		return renderProfilesList.getEvaluatedValue();
	}

	public void setRenderProfilesList(String newValExpr) {
		renderProfilesList.setExpressionText( newValExpr );
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
	}

	public Map<Object, String> getProfileLovMap() {

		boSession currSession = boApplication.currentContext().getEboContext()
				.getBoSession();

		Map<Object, String> oProfilesMap = new LinkedHashMap<Object, String>();
		if (currSession != null) {
			String[] iProfiles;

			// SYSUSER should have access to all profiles configured on XEO
			// Instance
			if (currSession.getUser().getUserName().equals("SYSUSER"))
				iProfiles = IProfileUtils.getAllIProfiles(currSession);
			else
				iProfiles = IProfileUtils.getIProfiles(currSession);

			if (iProfiles != null) {
				for (String profileString : iProfiles) {
					String[] currProfile = profileString.split(";");
					oProfilesMap.put(currProfile[0], currProfile[1]);
				}
			}
		}
		return oProfilesMap;
	}

	/**
	 * Makes the toolbar in the left corner with logout,user properties and
	 * change profile buttons (TEST)
	 * 
	 * 
	 */
	public ExtConfig buildTree() {

		Map<Object, String> map = getProfileLovMap();

		ExtConfig oTreeConfig = new ExtConfig("Ext.tree.TreePanel");
		oTreeConfig.addJSString("id", "formMain:tree");
		oTreeConfig.add("border", "false");
		oTreeConfig.add("useArrows", "true");
		oTreeConfig.add("autoScroll", "true");
		oTreeConfig.add("animate", "true");
		oTreeConfig.add("enableDD", "false");
		oTreeConfig.add("containerScroll", "true");
		oTreeConfig.add("rootVisible", "false");
		oTreeConfig.add("frame", "false");
		oTreeConfig.addJSString("layout", "fit");
		oTreeConfig.add("collapsed", "false");
		ExtConfig root = oTreeConfig.addChild("root");
		root.addJSString("nodeType", "async");
		oTreeConfig.add("dataUrl", "action");
		ExtConfigArray treeMenu = oTreeConfig.addChildArray("bbar");
		ExtConfig submenu = treeMenu.addChild();
		submenu.addJSString("xtype", "splitbutton");
		submenu.addJSString("icon", "ext-xeo/images/menus/logout.gif");
		submenu.addJSString("cls", "x-btn-text-icon");
		submenu.add("text", "ExtXeo.Messages.LOGOUT_BTN");
		submenu.add("tooltip", "ExtXeo.Messages.LOGOUT_BTN");
		submenu.add("handler",
				"function() { document.location.href='LogoutXVW.jsp'}");

		ExtConfigArray arrayMenu = submenu.addChildArray("menu");

		ExtConfig arrayChild1 = arrayMenu.addChild();
		arrayChild1.addJSString("xtype", "button");
		arrayChild1.addJSString("icon", "ext-xeo/images/menus/logout.gif");
		arrayChild1.addJSString("cls", "x-btn-text-icon");
		arrayChild1.add("text", "ExtXeo.Messages.LOGOUT_BTN");
		arrayChild1.add("handler",
				"function() { document.location.href='LogoutXVW.jsp'}");

		if (getRenderPropertiesBtn()){
			ExtConfig arrayChild2 = arrayMenu.addChild();// userProps
			arrayChild2.addJSString("xtype", "button");
			arrayChild2.addJSString("icon", "ext-xeo/admin/users.gif");
			arrayChild2.addJSString("cls", "x-btn-text-icon");
			arrayChild2.add("text", "ExtXeo.Messages.USER_PROPS");
			arrayChild2
					.add(
							"handler",
							"function() {XVW.AjaxCommand('formMain','showUserPropsCmd','showUserPropsCmd',2);}");
		}

		if ( getRenderProfilesList() ) {
			Collection< String > valuesColl = map.values();
			Iterator< String > valuesIt = valuesColl.iterator();
			Set< Object > keySet = map.keySet();
			Iterator< Object > keyIt = keySet.iterator();
			// repeat for profiles
			if ( valuesColl.size() > 1 ) {
				if ( valuesColl.size() == keySet.size() ) {
					while ( valuesIt.hasNext() ) {

						ExtConfig arrayChild3 = arrayMenu.addChild();
						arrayChild3.addJSString( "xtype" , "button" );
						arrayChild3.addJSString( "icon" ,
								"ext-xeo/admin/profiles.gif" );
						arrayChild3.addJSString( "cls" , "x-btn-text-icon" );
						arrayChild3.addJSString( "text" , valuesIt.next() );// //replace
						// with
						// profiles
						arrayChild3.add( "handler" ,
								"function() { document.location.href='Login.xvw?action=change_profile&boui="
										+ keyIt.next() + "'}" );

					}
				}
			}
		}
		return oTreeConfig;
	}

	public static class XEOHTMLRenderer extends XUIRenderer {
		public ExtConfig oExtToolBar;

		@Override
		public void encodeEnd(XUIComponentBase oComp) throws IOException {

			MainLayout oMainLayout = (MainLayout) oComp;

			/*
			 * <div id='topdownMenu' style="height:'50px'"></div> <div
			 * id='workArea'></div>
			 */
			XUIResponseWriter w = getResponseWriter();
			w.startElement(DIV, oMainLayout);
			w.writeAttribute(ID, "topdownMenu", null);
			w.endElement(DIV);
			w.startElement(DIV, oMainLayout);
			w.writeAttribute(ID, "workArea", null);
			w.endElement(DIV);

			w.getScriptContext().addIncludeAfter("ext-xeo",
					XUIScriptContext.POSITION_HEADER, "xeo-layouts",
					"ext-xeo/xeo-layouts.js");

			StringBuilder TreeScript = new StringBuilder();
			TreeScript.append("var XApp = null;");
			TreeScript.append("XEOLayoutInit = function() {");
			TreeScript.append("if( window.xeodmstate ) { }");
			TreeScript.append("else { xeodmstate = false; }");

			String actionStr = "var action = document.getElementsByName('formMain')[0].action;";
			actionStr += "action += \"?javax.faces.ViewState="
					+ getRequestContext().getViewRoot().getViewState()
					+ "&xvw.servlet=formMain:tree\";";

			TreeScript.append(actionStr);
			TreeScript.append("window.layoutTree =");

			// Build the tree
			String constructedTree = oMainLayout.buildTree().renderExtConfig()
					.toString();
			TreeScript.append(constructedTree);
			TreeScript.append(";");

			TreeScript.append("XApp = new XEOLayout();");
			TreeScript.append("XApp.desktop.syncSize();");
			TreeScript.append("window.onunload = XEOLayout.closeWindow;");
			TreeScript
					.append("window.setInterval(\"XVW.keepAlive( document.getElementsByTagName('form')[0] );\" ,7*60000);");
			TreeScript.append("}; Ext.onReady( XEOLayoutInit );");

			w.getScriptContext().add(XUIScriptContext.POSITION_HEADER,
					"xeo-dynamic-menu", TreeScript);

			//System.out.println(TreeScript);

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
