package netgest.bo.xwc.components.template.directives;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import netgest.bo.xwc.components.template.css.CssContext;
import netgest.bo.xwc.components.template.javascript.JavaScriptContext.Position;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class CssDirectiveProcessor implements TemplateDirectiveModel {

	private CssContext cssContext;
	
	public CssDirectiveProcessor(CssContext ctx){
		this.cssContext = ctx;
	}
	
	@SuppressWarnings( "rawtypes" )
	@Override
	public void execute( Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body )
			throws TemplateException, IOException {
		
		String scriptId = "CSS" + (System.currentTimeMillis() *  Math.random());
		Position position = Position.HEADER;
	
		if (params.containsKey("id")){
			scriptId = params.get("id").toString();
		}
		
		if (params.containsKey("position")){
			position = Position.fromString( params.get( "position" ).toString() );
		}
		
		if (params.containsKey("src")){
			cssContext.include(params.get("src").toString(), scriptId , position);
		} else {
			if (body == null)
				throw new IllegalArgumentException( "Cannot have an empty script body when no include is being made: " + env.getTemplate().getName() );
		
			StringWriter w = new StringWriter();
			body.render(w);
			cssContext.add(w.toString(), scriptId , position);
		}

	}

}
