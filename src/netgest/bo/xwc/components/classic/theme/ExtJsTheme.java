package netgest.bo.xwc.components.classic.theme;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSessionUser;
import netgest.bo.utils.XeoUserTheme;
import netgest.bo.utils.XeoUserThemeFile;
import netgest.bo.xeomodels.system.Theme;
import netgest.bo.xeomodels.system.ThemeIncludes;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStyleContext;
import netgest.bo.xwc.framework.XUITheme;
import netgest.bo.xwc.framework.localization.XUIMessagesLocalization;

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

	public void addStyle(XUIStyleContext styleContext) {
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "extjs_css",
				composeUrl(getResourceBaseUri() + "resources/css/ext-all.css"));
		// styleContext.addInclude(XUIStyleContext.POSITION_HEADER,
		// "extjs_css-gray", composeUrl( getResourceBaseUri() +
		// "resources/css/xtheme-slate.css" ) );
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "extjs_css1",
				composeUrl("ext-xeo/css/ext-xeo.css"));
		styleContext.addInclude(XUIStyleContext.POSITION_HEADER,
				"ext-xeo-nohtmleditor",
				composeUrl("ext-xeo/css/ext-xeo-nohtmleditor.css"));
		// styleContext.addInclude(XUIStyleContext.POSITION_HEADER,
		// "extjs_css-gray", composeUrl( getResourceBaseUri() +
		// "resources/css/xtheme-gray.css" ) );
		// Retrieve all theme files to include
		Map<String, String> filesToInclude = themeFilesToInclude();
		Iterator<String> idSet = filesToInclude.keySet().iterator();
		while (idSet.hasNext()) {
			String id = idSet.next();
			styleContext.addInclude(XUIStyleContext.POSITION_HEADER, id,
					composeUrl(filesToInclude.get(id)));
		}

		// if ( r.findComponent( AttributeHtmlEditor.class ) != null ) {
		// styleContext.addInclude(XUIStyleContext.POSITION_HEADER,
		// "ext-xeo-htmleditor", composeUrl(
		// "ext-xeo/css/ext-xeo-htmleditor.css" ) );
		// }
		// else {

		// }
	}

	/**
	 * 
	 * Retrieves a list of files to include for the theme
	 * 
	 * @return An array with the files to include
	 */
	private Map<String, String> themeFilesToInclude() {
		if (boApplication.currentContext().getEboContext() != null) {
			boSessionUser user = boApplication.currentContext().getEboContext()
					.getBoSession().getUser();
			try {
				
				//User has a theme
				if (user.getThemeFiles().size() > 0) {
					Map<String, String> result = user.getThemeFiles();
					return result;
				}
				//Retrieve default theme from runtime
				boObjectList list = boObjectList.list(boApplication
						.currentContext().getEboContext(),
						"select Theme where defaultTheme = 1");
				if (list.getRecordCount() == 1) {
					HashMap<String, String> result = new HashMap<String, String>();
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
			} catch (Exception e) {
				return new HashMap<String, String>(0);
			}
		}

		//Retrieve from boConfig
		HashMap<String, String> result = new HashMap<String, String>();
		XeoUserTheme defaultTheme = boApplication.getDefaultApplication()
				.getApplicationConfig().getDefaultTheme();
		if (defaultTheme != null) {
			XeoUserThemeFile[] files = defaultTheme.getFiles();
			for (XeoUserThemeFile f : files) {
				result.put(f.getId(), f.getPath());
			}
		}
		return result;
	}

	public void addScripts(XUIScriptContext scriptContext) {
		String lang = null;
		lang = boApplication.getDefaultApplication().getApplicationLanguage();

		if (boApplication.currentContext().getEboContext() != null) {
			boSessionUser user = boApplication.currentContext().getEboContext()
					.getBoSession().getUser();
			try {
				if (user.getLanguage() != null) {
					lang = user.getLanguage().toLowerCase();
					if (lang.length() > 3) {
						lang = (String) lang.subSequence(0, 2);
					}
				}
			} catch (Exception e) {
				lang = XUIMessagesLocalization.getThreadCurrentLocale()
						.getLanguage();
				logger.warn(" Could not retrieve the language, using system language '%s' ",e,lang);
			}
		}
		lang = lang.toLowerCase();

		logger.finer("Using '%s' as language for the User",lang);
		
		// Extjs
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-base",
				composeUrl(getResourceBaseUri() + "adapter/ext/ext-base.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-all",
				composeUrl(getResourceBaseUri() + "ext-all-debug.js"));

		// File[] x = new File(
		// "C:\\projects_eclipse\\xeo\\xeo_v3_xwc\\webapps\\default\\extjs\\pkgs"
		// ).listFiles();
		// scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
		// "ext-allext-dd-debug.js",
		// composeUrl( getResourceBaseUri() + "pkgs/ext-foundation-debug.js" )
		// );
		//
		// scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
		// "ext-allext-dd-debug1.js",
		// composeUrl( getResourceBaseUri() + "pkgs/ext-dd-debug.js" ) );

		// for( File y : x ) {
		// if( y.getName().indexOf("ext-") > -1 &&
		// y.getName().indexOf("-debug.js") > -1 ) {
		// scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-all"
		// + y.getName() ,
		// composeUrl( getResourceBaseUri() + "pkgs/" + y.getName() ) );
		// }
		// }

		// for( File y : x ) {
		// if( y.getName().indexOf("ext-") == -1 &&
		// y.getName().indexOf("-debug.js") > -1 &&
		// y.getName().indexOf("foundation") > -1) {
		// System.out.println( y.getName() );
		// scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-all"
		// + y.getName() ,
		// composeUrl( getResourceBaseUri() + "pkgs/" + y.getName() ) );
		// }
		// }
		//
		// for( File y : x ) {
		// if( y.getName().indexOf("grid-property") == -1 &&
		// y.getName().indexOf("editor-debug") == -1 &&
		// y.getName().indexOf("ext-") == -1 && y.getName().indexOf("-debug.js")
		// > -1 ) {
		// System.out.println( y.getName() );
		// scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-all"
		// + y.getName() ,
		// composeUrl( getResourceBaseUri() + "pkgs/" + y.getName() ) );
		// }
		// }
		//

		// xwc
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-core",
				composeUrl("xwc/js/xwc-core.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-messages", composeUrl("xwc/js/localization/xwc-messages_"
						+ lang + ".js"));

		// Grid Searc
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-xeo",
				composeUrl("ext-xeo/js/ext-xeo.js"));
		// scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
		// "ext-xeo1", composeUrl( "ext-xeo/js/SearchField.js" ) );
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"ExtXeo.grid", composeUrl("ext-xeo/js/GridPanel.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-components", composeUrl("ext-xeo/js/xwc-components.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"ExtXeo.tabs", composeUrl("ext-xeo/js/Tabs.js"));

		// Grid Filters
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-filter",
				ExtJsTheme.composeUrl("extjs/grid/GridFilters.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-filter-filter",
				ExtJsTheme.composeUrl("extjs/grid/filter/Filter.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-filter-boolean",
				ExtJsTheme.composeUrl("extjs/grid/filter/BooleanFilter.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-filter-date",
				ExtJsTheme.composeUrl("extjs/grid/filter/DateFilter.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-filter-list",
				ExtJsTheme.composeUrl("extjs/grid/filter/ListFilter.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-filter-numeric",
				ExtJsTheme.composeUrl("extjs/grid/filter/NumericFilter.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-filter-string",
				ExtJsTheme.composeUrl("extjs/grid/filter/StringFilter.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-filter-object",
				ExtJsTheme.composeUrl("extjs/grid/filter/ObjectFilter.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-menu-editable",
				ExtJsTheme.composeUrl("extjs/grid/menu/EditableItem.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"xwc-grid-menu-rangemenu",
				ExtJsTheme.composeUrl("extjs/grid/menu/RangeMenu.js"));

		// Language files
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"ext-all-lang", composeUrl(getResourceBaseUri()
						+ "build/locale/ext-lang-" + lang + "-min.js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"ext-xeo-messages",
				composeUrl("ext-xeo/js/localization/ext-xeo-messages_" + lang
						+ ".js"));
		scriptContext.addInclude(XUIScriptContext.POSITION_HEADER,
				"ext-xeo-app", composeUrl("ext-xeo/js/App.js"));

		// Utility Scrits
		XUIRequestContext oRequestContext;
		oRequestContext = XUIRequestContext.getCurrentContext();

		scriptContext
				.add(XUIScriptContext.POSITION_HEADER,
						"s.gif",
						"Ext.BLANK_IMAGE_URL = '"
								+ oRequestContext.getResourceUrl(ExtJsTheme
										.composeUrl("extjs/resources/images/default/s.gif"))
								+ "';");

		scriptContext.add(XUIScriptContext.POSITION_FOOTER, "ExtQuickTips",
				"Ext.onReady( function() {Ext.QuickTips.init();} );");
		scriptContext.add(XUIScriptContext.POSITION_FOOTER, "Ext.App",
				"if(!window.parent.App)  var App = new Ext.App({});");

	}

	public String getBodyStyle() {
		return " ext-ie ext-ie7 x-aero";
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

}
