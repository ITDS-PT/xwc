package netgest.bo.xwc.components.classic.theme;

import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStyleContext;
import netgest.bo.xwc.framework.XUITheme;
import netgest.bo.xwc.framework.localization.XUIMessagesLocalization;

public class ExtJsTheme implements XUITheme {
    public ExtJsTheme() { 
    }

    public void addStyle(XUIStyleContext styleContext) {
        styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "extjs_css", composeUrl( getResourceBaseUri() + "resources/css/ext-all.css" ) );
//        styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "extjs_css-gray", composeUrl( getResourceBaseUri() + "resources/css/xtheme-gray.css" ) );
        styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "extjs_css1", composeUrl( "ext-xeo/css/ext-xeo.css" ) );
        
//        if ( r.findComponent( AttributeHtmlEditor.class ) != null ) {
//            styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "ext-xeo-htmleditor", composeUrl( "ext-xeo/css/ext-xeo-htmleditor.css" ) );
//        }
//        else {
            styleContext.addInclude(XUIStyleContext.POSITION_HEADER, "ext-xeo-nohtmleditor", composeUrl( "ext-xeo/css/ext-xeo-nohtmleditor.css" ) );
//        }
    }

    public void addScripts(XUIScriptContext scriptContext) {
    	
    	// Current language...
    	// Choosing user language...
        String lang = XUIMessagesLocalization.getThreadCurrentLocale().getLanguage();
    	
        // Extjs
    	scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-base", composeUrl( getResourceBaseUri() + "adapter/ext/ext-base.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-all", composeUrl( getResourceBaseUri() + "ext-all-debug.js" ) );
        
        // xwc
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-core", composeUrl( "xwc/js/xwc-core.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-messages", composeUrl( "xwc/js/localization/xwc-messages_" + lang + ".js" ) );
        
        // Grid Searc
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-xeo", composeUrl( "ext-xeo/js/ext-xeo.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-xeo1", composeUrl( "ext-xeo/js/SearchField.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ExtXeo.grid", composeUrl( "ext-xeo/js/GridPanel.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-components", composeUrl( "ext-xeo/js/xwc-components.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ExtXeo.tabs", composeUrl( "ext-xeo/js/Tabs.js" ) );

        // Grid Filters
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter", ExtJsTheme.composeUrl( "extjs/grid/GridFilters.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-filter", ExtJsTheme.composeUrl( "extjs/grid/filter/Filter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-boolean", ExtJsTheme.composeUrl( "extjs/grid/filter/BooleanFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-date", ExtJsTheme.composeUrl( "extjs/grid/filter/DateFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-list", ExtJsTheme.composeUrl( "extjs/grid/filter/ListFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-numeric", ExtJsTheme.composeUrl( "extjs/grid/filter/NumericFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-string", ExtJsTheme.composeUrl( "extjs/grid/filter/StringFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-filter-object", ExtJsTheme.composeUrl( "extjs/grid/filter/ObjectFilter.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-menu-editable", ExtJsTheme.composeUrl( "extjs/grid/menu/EditableItem.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "xwc-grid-menu-rangemenu", ExtJsTheme.composeUrl( "extjs/grid/menu/RangeMenu.js" ) );
        
        
        // Language files
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-all-lang", composeUrl( getResourceBaseUri() + "build/locale/ext-lang-"+lang+"-min.js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-xeo-messages", composeUrl( "ext-xeo/js/localization/ext-xeo-messages_" + lang + ".js" ) );
        scriptContext.addInclude(XUIScriptContext.POSITION_HEADER, "ext-xeo-app", composeUrl( "ext-xeo/js/App.js" ) );
        
        // Utility Scrits
        scriptContext.add( XUIScriptContext.POSITION_HEADER , "s.gif", "Ext.BLANK_IMAGE_URL = '"+ ExtJsTheme.composeUrl("extjs/images/default/s.gif") + "';");
        scriptContext.add( XUIScriptContext.POSITION_FOOTER , "ExtQuickTips", "Ext.onReady( function() {Ext.QuickTips.init();} );" );

        
        scriptContext.add( XUIScriptContext.POSITION_FOOTER , "ExtQuickTips", "var App = new Ext.App({});" );
        
    }
    
    public String getBodyStyle() {
        return " ext-ie ext-ie7 x-aero";
    }
    
    public String getHtmlStyle() {
        return "height:100%;width:100%";
    }
    
    public static final String composeUrl( String relUri ) {
    	return getBaseUrl() + relUri;
    }
    
    public static final String getBaseUrl() {
    	return "";
    }
    
    public String getResourceBaseUri() {
        return "extjs/"; 
    }
    
}
