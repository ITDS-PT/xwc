package netgest.bo.xwc.components.template.loader;

import java.io.IOException;

import freemarker.template.Template;

public interface TemplateLoader {
	
	public Template loadTemplate(String name) throws IOException ;

}
