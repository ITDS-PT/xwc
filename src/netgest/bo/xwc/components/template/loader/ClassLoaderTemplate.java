package netgest.bo.xwc.components.template.loader;

import java.io.IOException;

import freemarker.cache.ClassTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ClassLoaderTemplate implements TemplateLoader {

	private ClassTemplateLoader classLoader = new ClassTemplateLoader( ClassLoaderTemplate.class, "/" );
	private Configuration cfg;
	
	public ClassLoaderTemplate(Configuration cfg){
		this.cfg = cfg;
		this.cfg.setTemplateLoader( classLoader );
	}
	
	
	@Override
	public Template loadTemplate( String name ) throws IOException {
		try{
			return cfg.getTemplate( name );
		} catch (ParseException e ) {
			throw e;
		} catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}

}
