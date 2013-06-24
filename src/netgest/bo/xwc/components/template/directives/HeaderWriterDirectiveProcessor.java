package netgest.bo.xwc.components.template.directives;

import java.io.IOException;
import java.util.Map;

import netgest.bo.xwc.framework.XUIResponseWriter;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * Adds element to the head of a page being rendered
 *
 */
public class HeaderWriterDirectiveProcessor implements TemplateDirectiveModel {

	XUIResponseWriter writer;
	
	public HeaderWriterDirectiveProcessor(XUIResponseWriter headerWriter){
		this.writer = headerWriter;
	}
	
	@SuppressWarnings( "rawtypes" )
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
			body.render( writer );
	}

}
