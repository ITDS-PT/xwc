package netgest.bo.xwc.framework;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import netgest.bo.system.Logger;
import netgest.bo.system.boApplicationConfig;
import netgest.bo.system.config.RenderKit;
import netgest.bo.xwc.components.classic.theme.ExtJsTheme;
import netgest.bo.xwc.components.classic.theme.JQueryTheme;
import netgest.bo.xwc.components.classic.theme.NullTheme;
import netgest.utils.StringUtils;

public class XUIApplicationConfig {
	
	private Map<String,String> renderKits = new HashMap< String, String >();
	private static final String DEFAULT_RENDER_KIT_ID = "XEOHTML";
	private String defaultApplicationRenderKit = null;
	private static final Logger logger = Logger.getLogger( XUIApplicationConfig.class );
	
	public XUIApplicationConfig(boApplicationConfig boConfig){
		initDefaults();
		if (StringUtils.hasValue( boConfig.getDefaultRenderKit() ))
			defaultApplicationRenderKit = boConfig.getDefaultRenderKit();
		else
			defaultApplicationRenderKit = "XEOHTML";
		Iterator<Entry<String,RenderKit>> entries = boConfig.getRenderKits().entrySet().iterator();
		while (entries.hasNext()){
			Entry<String,RenderKit> current = entries.next();
			String renderKitId = current.getKey();
			RenderKit kit = current.getValue();
			renderKits.put( renderKitId , kit.getThemeClass() );
		}
	}

	private void initDefaults() {
		renderKits.put( "XEOHTML" , ExtJsTheme.class.getName() );
		renderKits.put( "XEOJQUERY" , JQueryTheme.class.getName() );
		renderKits.put( "XEOXML" , NullTheme.class.getName() );
		renderKits.put( "XEOV2" , NullTheme.class.getName() );
	}
	
	public String getThemeForRenderKit(String renderKitId){
		if (renderKits.containsKey( renderKitId )){
			return renderKits.get( renderKitId );
		} else {
			logger.config( "Could not find Theme for RenderKit %s, returning NullTheme", renderKitId );
			return NullTheme.class.getName();
		}
	}
	
	public boolean hasThemeClass(String renderKitId){
		return renderKits.containsKey( renderKitId );
	}
	
	public String getDefaultRenderKitClass(){
		if (StringUtils.hasValue( defaultApplicationRenderKit )){
			return renderKits.get( defaultApplicationRenderKit );
		}
		return renderKits.get( DEFAULT_RENDER_KIT_ID );
	}
	
	public String getDefaultRenderKitId(){
		if (StringUtils.hasValue( defaultApplicationRenderKit )){
			return defaultApplicationRenderKit;
		}
		return DEFAULT_RENDER_KIT_ID;
	}
	
	public String toString() {
		return super.toString();
	}

	
}
