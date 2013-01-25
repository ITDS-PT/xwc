package netgest.bo.xwc.components.template.directives;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class XUICommandDirectiveProcessor implements TemplateDirectiveModel {
	public String toString() {
		return super.toString( );
	}

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		//Do nothing on purpose
	}

	
}
