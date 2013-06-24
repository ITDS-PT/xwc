package netgest.bo.xwc.components.template.directives;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import netgest.bo.xwc.components.template.javascript.JavaScriptContext;
import netgest.bo.xwc.components.template.javascript.JavaScriptContext.Position;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Processes Javascript directives included in the template (adding them
 * to the header/footer/inline of the document)
 * 
 * Each script can have the following parameters:
 * id = The id of the script to be added, if not present a random id will be used
 * position = The position to insert the script in (can have values header/footer/inline) default is header
 * src = path to the javascript to include
 * 
 *
 */
public class JavaScriptDirectiveProcessor implements TemplateDirectiveModel {

	private JavaScriptContext ctx;
	
	public JavaScriptDirectiveProcessor(JavaScriptContext context){
		this.ctx = context;
	}
	
	@Override
	public void execute(Environment env, @SuppressWarnings("rawtypes") Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		
			String scriptId = "I" + (System.currentTimeMillis() * Math.random());
			Position position = Position.HEADER;
		
			if (params.containsKey("id")){
				scriptId = params.get("id").toString();
			}
			
			if (params.containsKey("position")){
				position = Position.fromString( params.get( "position" ).toString() );
			}
			
			if (params.containsKey("src")){
				ctx.include(params.get("src").toString(), scriptId , position);
			} else {
				if (body == null)
					throw new IllegalArgumentException( "Cannot have an empty script body when no include is being made: " + env.getTemplate().getName() );
			
				StringWriter w = new StringWriter();
				body.render(w);
				ctx.add(w.toString(), scriptId , position);
			}
		
	}

}
