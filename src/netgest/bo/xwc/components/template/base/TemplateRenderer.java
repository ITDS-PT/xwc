package netgest.bo.xwc.components.template.base;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import netgest.bo.xwc.components.template.css.XwcCssContext;
import netgest.bo.xwc.components.template.directives.ChildDirectiveProcessor;
import netgest.bo.xwc.components.template.directives.CssDirectiveProcessor;
import netgest.bo.xwc.components.template.directives.HeaderWriterDirectiveProcessor;
import netgest.bo.xwc.components.template.directives.JavaScriptDirectiveProcessor;
import netgest.bo.xwc.components.template.javascript.XwcScriptContext;
import netgest.bo.xwc.components.template.loader.TemplateLoaderFactory;
import netgest.bo.xwc.components.template.wrappers.XVWScriptsWrapper;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStyleContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;
import freemarker.core.ParseException;
import freemarker.template.Template;
public class TemplateRenderer extends XUIRenderer {

	enum ProcessorDirectives{
		SCRIPT("xvw_script"),
		CSS("xvw_css"),
		CHILDREN("xvw_facet"),
		HEADER("xvw_header");
		
		private String name;
		
		private ProcessorDirectives(String name) {
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		public String toString(){
			return name;
		}
	}
	
	public XUIResponseWriter getHeaderWriter() throws IOException{
		return getResponseWriter().getHeaderWriter();
	}
	
	public XUIStyleContext getStyleContext(){
		return getRequestContext().getStyleContext();
	}
	
	public XUIScriptContext getScriptContext(){
		return getRequestContext().getScriptContext();
	}
	
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		
		Template p = null;
		try{
			p = getTemplate( component );
			if (p == null){
				getResponseWriter().write("Could not load the template " + component.getTemplate() + " check syntax and file");
				return;
		}
		} catch (ParseException e ){
			getResponseWriter().write("Could not parse the template " + component.getTemplate() + " check syntax " + e.getMessage());
			return;
		} catch (FileNotFoundException e ){
			getResponseWriter().write("Could not find the template " + component.getTemplate());
			return;
		}
		
		//Create the special directives
		JavaScriptDirectiveProcessor script = new JavaScriptDirectiveProcessor(
				new XwcScriptContext( getScriptContext() ) );
		CssDirectiveProcessor css = new CssDirectiveProcessor(
				new XwcCssContext( getStyleContext() ) );
		ChildDirectiveProcessor children = new ChildDirectiveProcessor( component );
		HeaderWriterDirectiveProcessor header = 
				new HeaderWriterDirectiveProcessor( getHeaderWriter() );
		XVWScriptsWrapper wrapper = new XVWScriptsWrapper();
		//Put them in the context
		Map<String,Object> context = new HashMap<String, Object>();
		context.put(ProcessorDirectives.SCRIPT.getName(), script);
		context.put(ProcessorDirectives.CSS.getName(), css);
		context.put(ProcessorDirectives.CHILDREN.getName(), children);
		context.put(ProcessorDirectives.HEADER.getName(), header);
		context.put("XVWScripts", wrapper);
		
		//Export the component
		context.put( "this", component );
		try {
			p.process(context, getResponseWriter());
		} catch ( Exception e ) {
			reportErrorProcessingTemplate( component, e );
		}
	}


	private void reportErrorProcessingTemplate( XUIComponentBase base, Exception e ) throws IOException {
		String content = base.getTemplateContent();
		if (!StringUtils.isEmpty( content )){
			getResponseWriter().write( "Could not process the template("+content+") for component" + base.getId() + " reason: " + e.getMessage() );
		} else {
			getResponseWriter().write( "Could not process the template("+base.getTemplate()+") for component" + base.getId() + " reason: " +  e.getMessage()  );
		}
		e.printStackTrace();
	}
	
	
	/**
	 * 
	 * Retrieves the template associated with the component
	 * 
	 * @param template The template to load
	 * @return The loaded template
	 * @throws IOException If something goes wrong while reading the template
	 */
	private Template getTemplate( XUIComponentBase template ) throws IOException {
		String templateContent = template.getTemplateContent();
		if (StringUtils.isEmpty( templateContent ))
			return TemplateLoaderFactory.loadTemplate( template.getTemplate() );
		else
			return TemplateLoaderFactory.loadFromString( templateContent );
	}


	@Override
	public boolean getRendersChildren(){
		return true;
	}
	
	@Override
	public void encodeChildren( XUIComponentBase component ) throws IOException {
		//Do nothing on purpose, child encoding should be done through the
		//templateEncodeChildren(XUIComponentBase component) method
	}
	
	/**
	 * 
	 * Encodes children
	 * 
	 * @param component
	 * @throws IOException 
	 */
	public void templateEncodeChildren(XUIComponentBase component) throws IOException{
		super.encodeChildren( component );
	}
	

}