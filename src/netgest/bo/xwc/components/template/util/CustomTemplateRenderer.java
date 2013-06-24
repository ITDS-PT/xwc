package netgest.bo.xwc.components.template.util;

import java.io.StringWriter;
import java.util.Map;

import netgest.bo.xwc.components.template.loader.TemplateLoaderFactory;
import freemarker.template.Template;

/**
 * 
 * Class with utility methods for adhoc template rendering
 *
 */
public class CustomTemplateRenderer {
	
	
	
	/**
	 * Renders a template given its name
	 * 
	 * @param templateName The name of the template
	 * @param context The context variables for the template
	 * 
	 * @return A string with the template processed
	 */
	public static String processTemplateFile(String templateName, Map<String,Object> context) {
		StringWriter w = new StringWriter();
		try {
			Template template = TemplateLoaderFactory.loadTemplate( templateName );
			if (template == null)
				throw new IllegalArgumentException( "Could not find a template by the name " + templateName );
			template.process( context, w );
			return w.toString();
		}  catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}
	
	/**
	 * 
	 * Renders a template defined in a string
	 * 
	 * @param templateContent The content of the template
	 * @param context The context variables for the template
	 * 
	 * @return A string with the template processed
	 */
	public static String processTemplate(String templateContent, Map<String,Object> context) {
		StringWriter w = new StringWriter();
		try {
			TemplateLoaderFactory.loadFromString( templateContent ).process( context, w );
			return w.toString();
		}  catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

}
