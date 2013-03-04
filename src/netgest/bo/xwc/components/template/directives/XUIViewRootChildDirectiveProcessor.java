package netgest.bo.xwc.components.template.directives;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.faces.context.ResponseWriter;

import netgest.bo.xwc.framework.PackageIAcessor;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class XUIViewRootChildDirectiveProcessor implements TemplateDirectiveModel {

	private XUIViewRoot component;
	
	public XUIViewRootChildDirectiveProcessor(XUIViewRoot component){
		this.component = component;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		
		XUIResponseWriter oldWriter = null;
		XUIResponseWriter newWriter = null;
		Writer contentWriter = null;
		
		oldWriter = XUIRequestContext.getCurrentContext().getResponseWriter();
		contentWriter = new StringWriter();
		newWriter = new XUIResponseWriter(contentWriter, "text/html", "UTF-8");

		PackageIAcessor.setScriptContextToWriter( newWriter, oldWriter.getScriptContext() );
        
        // Maintain the same header and footer writers
        PackageIAcessor.setHeaderAndFooterToWriter( 
        		newWriter, 
        		oldWriter.getHeaderWriter(), 
        		oldWriter.getFooterWriter() 
        );
        
        try{
        	setResponseWriter( newWriter );
			//Set the content of this template to the content of the old writer
			oldWriter.write( contentWriter.toString() );
        }
		finally {
			//Restore the previous writer even if something does not go as expected
			setResponseWriter( oldWriter );
		}
	}
		
	/**
	 * 
	 * Sets the current response writer
	 * 
	 * @param writer The current response writer
	 */
	void setResponseWriter(ResponseWriter writer) {
		
		XUIRequestContext.getCurrentContext().getFacesContext().setResponseWriter( writer );
	}	
	
	
}
