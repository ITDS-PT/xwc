package netgest.bo.xwc.components.classic.theme;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSessionUser;
import netgest.bo.utils.XeoUserTheme;
import netgest.bo.utils.XeoUserThemeFile;
import netgest.bo.xeomodels.system.Theme;
import netgest.bo.xeomodels.system.ThemeIncludes;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStyleContext;
import netgest.bo.xwc.framework.XUITheme;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.localization.XUILocalization;

import netgest.utils.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.lowagie.text.html.HtmlTags;


/**
 * 
 * Implementation of the Theme interface for an XEO Application
 * using ExtJS components
 *
 */
public class ExtJsTheme implements XUITheme {
	
	/**
	 * The logger
	 */
	public static final Logger logger = Logger.getLogger(ExtJsTheme.class);
	
	
	public ExtJsTheme() {	
	}
	
	public ExtJsTheme(String buildVersion) {
		this.buildVersion = buildVersion;
	}
	
	/**
	 * The build version
	 */
	private String buildVersion = "";
	
	/**
	 * Retrieves the current builder version of XEO
	 * 
	 * @return
	 */
	protected String getCurrentBuildVersion(){
		if (StringUtils.isEmpty( buildVersion ))
			buildVersion = boApplication.getDefaultApplication().getBuildVersion(); 
		return buildVersion;	
	}

	public void addStyle(XUIStyleContext styleContext) {
		
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "extjs_css",
				composeUrl(getResourceBaseUri() + "resources/css/ext-all.css"));
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "extjs_css1",
				composeUrl("ext-xeo/css/ext-xeo.css"));
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER,
				"ext-xeo-nohtmleditor",
				composeUrl("ext-xeo/css/ext-xeo-nohtmleditor.css"));
		
		Map<String,String> filesToInclude = themeFilesToInclude();
		Set<Entry<String,String>> files = filesToInclude.entrySet();
		Iterator<Entry<String,String>> it = files.iterator();
		while (it.hasNext()){
			Entry<String,String> current = it.next();
			styleContext.addInclude(XUIStyleContext.POSITION_HEADER, 
					current.getKey(),
					composeUrl(current.getValue()));
		}
		

	}

	/**
	 * 
	 * Retrieves a list of files to include for the theme
	 * 
	 * @return An array with the files to include
	 */
	protected Map<String, String> themeFilesToInclude() {
		Map<String,String> result = new HashMap<String, String>();
		
		try{
			if (getEboContext() != null) {
				EboContext ctx = getEboContext();
				boSessionUser user = ctx.getBoSession().getUser();
					//User has a theme
					if (user.getThemeFiles().size() > 0 
							|| ctx.getBoSession().getProperty("theme") != null) { //This case is when there are no files in the theme (default blue)
						return  user.getThemeFiles();
					}
					//Retrieve default theme from runtime
					result = getDefaultThemeFiles(ctx);
			} 

			//Retrieve from boConfig
			XeoUserTheme defaultTheme = boApplication.getDefaultApplication()
					.getApplicationConfig().getDefaultTheme();
			if (defaultTheme != null) {
				XeoUserThemeFile[] files = defaultTheme.getFiles();
				for (XeoUserThemeFile f : files) {
					result.put(f.getId(), f.getPath());
				}
			}
		}
		catch (Exception e ){
			//Avoid errors just because we can't get theme files, default
			//will do
		}
		
		return result;
	}
	
	
	private Map<String,String> getDefaultThemeFiles(EboContext ctx){
		
		Map<String,String> result = new HashMap<String, String>();
		
		boObjectList list = boObjectList.list(ctx,
				"select Theme where defaultTheme = '1'");
		try {
			if (list.getRecordCount() == 1) {
				list.beforeFirst();
				list.next();
				boObject currentTheme = list.getObject();
				bridgeHandler bh = currentTheme.getBridge(Theme.FILES);
				boBridgeIterator iterator = bh.iterator();
				while (iterator.next()) {
					boObject currentFile = iterator.currentRow()
							.getObject();
					String id = currentFile.getAttribute(ThemeIncludes.ID).getValueString();
					String path = currentFile.getAttribute(ThemeIncludes.FILEPATH).getValueString();
					result.put(id, path);
				}
			}
		} catch (boRuntimeException e) {
			logger.warn("Could not read the default theme", e);
		}
		return result;
	}
	
	protected JavaScriptIncluder createScriptIncluder(XUIScriptContext ctx, String currentBuild){
		return new JavaScriptIncluder( ctx, currentBuild );
	}

	public void addScripts(XUIScriptContext scriptContext) {
		String lang = getApplicationLanguage();
		
		if (logger.isFinerEnabled())
			logger.finer("Using '%s' as language for the User ", lang);
		
		// Extjs
		JavaScriptIncluder scriptIncluder = createScriptIncluder( scriptContext, getCurrentBuildVersion() );
		scriptIncluder.includeRegular( "ext-base", getResourceBaseUri() + "adapter/ext/ext-base.js" );
		scriptIncluder.includeRegular( "ext-all", getResourceBaseUri() + "ext-all.js" );
		// xwc
		
		scriptIncluder.include( "xwc-core", "xwc/js/xwc-core.js" );
		scriptIncluder.include( "xwc-messages", "xwc/js/localization/xwc-messages_"+ lang + ".js" );
		
		// Grid Search
		
		
		scriptIncluder.include( "ext-xeo", "ext-xeo/js/ext-xeo.js" );
		scriptIncluder.include( "gridDrag", "ext-xeo/js/griddrag.js" );
		
		
		scriptIncluder.include( "ExtXeo.grid", "ext-xeo/js/GridPanel.js" );
		scriptIncluder.include( "xwc-components", "ext-xeo/js/xwc-components.js" );
		scriptIncluder.include( "ExtXeo.tabs", "ext-xeo/js/Tabs.js" );
		scriptIncluder.include( "ExtXeo.TreePanelJCR", "ext-xeo/js/TreePanelJCR.js" );
		// Grid Filters
		scriptIncluder.include( "xwc-grid-filter", "extjs/grid/GridFilters.js" );
		scriptIncluder.include( "xwc-grid-filter-filter", "extjs/grid/filter/Filter.js" );
		scriptIncluder.include( "xwc-grid-filter-boolean", "extjs/grid/filter/BooleanFilter.js" );
		scriptIncluder.include( "xwc-grid-filter-date", "extjs/grid/filter/DateFilter.js" );
		scriptIncluder.include( "xwc-grid-filter-list", "extjs/grid/filter/ListFilter.js" );
		scriptIncluder.include( "xwc-grid-filter-numeric", "extjs/grid/filter/NumericFilter.js" );
		scriptIncluder.include( "xwc-grid-filter-string", "extjs/grid/filter/StringFilter.js" );
		scriptIncluder.include( "xwc-grid-filter-object", "extjs/grid/filter/ObjectFilter.js" );
		scriptIncluder.include( "xwc-grid-menu-editable", "extjs/grid/menu/EditableItem.js" );
		scriptIncluder.include( "xwc-grid-menu-rangemenu", "extjs/grid/menu/RangeMenu.js" );
		// Language files
		scriptIncluder.include( "ext-all-lang", getResourceBaseUri() + "build/locale/ext-lang-" + lang + "-min.js" );
		scriptIncluder.include( "ext-xeo-messages", "ext-xeo/js/localization/ext-xeo-messages_" + lang+ ".js" );
		scriptIncluder.include( "ext-xeo-app", "ext-xeo/js/App.js" );
		
		// Utility Scrits
		scriptIncluder.addHeaderScript( "s.gif", "Ext.BLANK_IMAGE_URL = '" +
				ExtJsTheme.composeUrl("extjs/resources/images/default/s.gif") + "';" ); 
		
		scriptIncluder.addFooterScript( "ExtQuickTips", "if(!window.parent.App)  var App = new Ext.App({});" );
		scriptIncluder.addFooterScript( "Ext.App", "Ext.onReady( function() {Ext.QuickTips.init();} );" );
		
	}
	
	public String getResourceBaseUriJquery() {
		return "jquery-xeo/";
	}
	
	protected EboContext getEboContext(){
		return boApplication.currentContext().getEboContext();
	}

	protected String getApplicationLanguage() {
		
		Locale lang = XUILocalization.getCurrentLocale();
		
		String result = lang.getLanguage().toLowerCase();
		return result;
	}


	public String getBodyStyle() {
		return " ext-ie ext-ie7 x-aero; height:100%;width:100%";
	}

	public String getHtmlStyle() {
		return "height:100%;width:100%";
	}

	public static final String composeUrl(String relUri) {
		return getBaseUrl() + relUri;
	}

	public static final String getBaseUrl() {
		return "";
	}

	public String getResourceBaseUri() {
		return "extjs/";
	}
	
	protected static class JavaScriptIncluder{
		
		private XUIScriptContext scriptContext;
		private String builderVersion;
		
		public JavaScriptIncluder(XUIScriptContext ctx, String buildVersion){
			this.scriptContext = ctx;
			this.builderVersion = buildVersion;
		}
		
		public void include(String id, String url){
			scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, id,
					composeUrl(url + "?id=" + builderVersion));
		}
		
		public void includeRegular(String id, String url){
			scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, id,
					composeUrl(url));
		}
		
		public void addFooterScript(String id, String script){
			scriptContext.add(XUIScriptContext.POSITION_FOOTER,id,script);
		}
		
		public void addHeaderScript(String id, String script){
			scriptContext.add(XUIScriptContext.POSITION_HEADER,id,script);
		}
		
	}

	@Override
	public String getDocType() {
		return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n";
	}

	@Override
	public void writeHeader(XUIResponseWriter headerWriter) throws IOException  {
		
		headerWriter.startElement( HtmlTags.META);
		headerWriter.writeAttribute("http-equiv", "X-UA-Compatible");
		headerWriter.writeAttribute("content", "IE=EmulateIE7;chrome=IE10");
		headerWriter.endElement( HtmlTags.META );
		
	}

	@Override
	public void writePostBodyContent(XUIRequestContext context,
			XUIResponseWriter writer, XUIViewRoot viewRoot) throws IOException  {
		
		if (context.isPortletRequest()) {

			if (!context.isAjaxRequest()) {
				writer.getScriptContext().add(XUIScriptContext.POSITION_HEADER,
						"portalVar", "xvw_isPortal=true");
			}

			String sWidth = (String) ((HttpServletRequest) context
					.getRequest()).getAttribute("xvw.width");
			if (sWidth != null) {
				writer.startElement("div");
					writer.writeAttribute("id", viewRoot.getClientId());
					writer.writeAttribute("style", "width:" + sWidth);
			} else if (!context.isAjaxRequest()) {
				writer.startElement("div" );
					writer.writeAttribute("id", viewRoot.getClientId());

					// Nao sei se e necessario, foi criado por necessidade
					if (viewRoot.findComponent(Window.class) != null) {
						writer.writeAttribute(HTMLAttr.CLASS, "x-panel", "");
					}
					writer.writeAttribute("style", "width:100%;height:100%", null);
			}
		} else {

			if (!context.isAjaxRequest()) {
				writer.getScriptContext().add(XUIScriptContext.POSITION_HEADER,
						"portalVar", "xvw_isPortal=false");
			}

			writer.startElement("div" );
				writer.writeAttribute("id", viewRoot.getClientId());
	
				// Nao sei se e necessario, foi criado por necessidade
				if (viewRoot.findComponent(Window.class) != null) {
					writer.writeAttribute(HTMLAttr.CLASS, "x-panel", "");
				}
				writer.writeAttribute("style", "width:100%;height:100%");
		}
		
		
	}

	@Override
	public void writePreFooterContent(XUIRequestContext context,
			XUIResponseWriter writer, XUIViewRoot viewRoot) throws IOException {
		
		if (context.isPortletRequest()) {
			String sWidth = (String) ((HttpServletRequest) context
					.getRequest()).getAttribute("xvw.width");
			if (sWidth != null) {
				writer.endElement("div");
			}
		} else {
			writer.endElement("div");
		}
	}

}
