package netgest.bo.xwc.components.template.loader;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;

import netgest.bo.xwc.framework.XUIRequestContext;
import freemarker.template.Configuration;
import freemarker.template.Template;


/**
 * Factory that retrieves template loaders and can load a template directly
 *
 */
public class TemplateLoaderFactory {

	public enum TemplateType{
		FILE("file"),
		CLASSPATH("class"),
		STRING("string");
		
		private String name;
		
		private TemplateType(String name) {
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	}
	
	
	
	private static Map<String,TemplateLoader> loaders = new HashMap<String, TemplateLoader>();
	
	private static volatile TemplateLoader defaultLoader = getDefaultLoader();
	
	static {
		loaders.put( TemplateType.CLASSPATH.getName(), new ClassLoaderTemplate( new Configuration() ) );
		loaders.put( TemplateType.FILE.getName(), defaultLoader );
	}
	
	private static final String TEMPLATES_ROOT = "templates";
	
	
	public static TemplateLoader getLoader(String type){
		if (loaders.containsKey( type )){
			return loaders.get( type );
		}
		return getDefaultLoader();
	}
	
	public static TemplateLoader getLoader(TemplateType type){
		if (loaders.containsKey( type )){
			return loaders.get( type );
		}
		return getDefaultLoader();
	}
	
	/**
	 * 
	 * Attempts to load a template from every source available
	 * 
	 * @param name The name of the template
	 * 
	 * @return 
	 */
	public static Template loadTemplate(String name) throws IOException {
		Set<Entry<String,TemplateLoader>> allLoaders = loaders.entrySet();
		Iterator<Entry<String,TemplateLoader>> it = allLoaders.iterator();
		while (it.hasNext()){
			TemplateLoader loader = it.next().getValue();
			Template toReturn = loader.loadTemplate( name );
			if (toReturn != null)
				return toReturn;
		}
		return null;
	}
	
	
	
	/**
	 * Register a new loader
	 * 
	 * @param type The type (an identifier) to register this loader with 
	 * @param loader The loader to register
	 */
	public static void registerLoader(String type, TemplateLoader loader){
		synchronized ( loaders ) {
			loaders.put( type, loader );
		}
	}
	
	/**
	 * 
	 * Loads template from a string, does not cache the template
	 * 
	 * @param template
	 * @return
	 */
	public static Template loadFromString(String template){
		Template t = null;
		try {
			t = new Template("name" + System.currentTimeMillis(), new StringReader(template),new Configuration());
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return t;
	}
	
	/**
	 * Retrieves the default template loader
	 * 
	 * @return A template loader
	 */
	public static TemplateLoader getDefaultLoader(){
		if (defaultLoader == null){
			synchronized ( TemplateLoaderFactory.class ) {
				if (defaultLoader == null){
					XUIRequestContext oReqCtx = XUIRequestContext.getCurrentContext();
					if (oReqCtx != null){
				    	ServletContext servletContext = 
				    		(ServletContext)oReqCtx.getFacesContext().getExternalContext().getContext();
				    	Configuration cfg = new Configuration();
				    	defaultLoader = new WebContextTemplateLoader( cfg, servletContext.getRealPath( TEMPLATES_ROOT ) );
					}
			    }
			}
		}
		return defaultLoader;
	}
	
	
	/**
	 * Replace the default loader
	 * 
	 * @param newLoader The new default loader
	 */
	public static void setDefaultLoader(TemplateLoader newLoader){
		defaultLoader = newLoader;
	}
	
	

}
