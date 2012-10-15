package netgest.bo.xwc.components.template.loader;

import java.io.File;
import java.io.IOException;

import freemarker.cache.FileTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Loads templates from the web context
 *
 */
public class WebContextTemplateLoader implements TemplateLoader {

	private Configuration config;
	FileTemplateLoader fileLoader;
	
	public WebContextTemplateLoader(Configuration cfg, String contextPath) {
		this.config = cfg;
		try {
			fileLoader = new FileTemplateLoader( new File( contextPath ) );
			config.setTemplateLoader( fileLoader );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Template loadTemplate( String name ) throws IOException {

		try {
			return config.getTemplate( name );
		} catch (ParseException e ){
			throw e;
		}
		catch ( IOException e ) {
			//e.printStackTrace();
			return null;
		}
		
	}

}
