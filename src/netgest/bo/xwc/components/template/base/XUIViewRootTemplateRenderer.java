package netgest.bo.xwc.components.template.base;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import netgest.bo.xwc.components.template.directives.XUIViewRootChildDirectiveProcessor;
import netgest.bo.xwc.components.template.loader.TemplateLoaderFactory;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import freemarker.core.ParseException;
import freemarker.template.Template;

public class XUIViewRootTemplateRenderer extends TemplateRenderer {

	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		//Can't come through here
		encodeBegin( getFacesContext(), component );
	}
	
	@Override
	@Deprecated
	public void encodeBegin(FacesContext facesContext, UIComponent view)
			throws IOException {

		XUIViewRoot component = (XUIViewRoot) view;
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
		XUIViewRootChildDirectiveProcessor children = new XUIViewRootChildDirectiveProcessor( component );
		//Put them in the context
		Map<String,Object> context =  new LinkedHashMap< String , Object >();
		context.put( ProcessorDirectives.CHILDREN.getName(), children );
		//Export the component
		context.put( "this", component );
		try {
			p.process(context, getResponseWriter());
		} catch ( Exception e ) {
			reportErrorProcessingTemplate( component, e );
		}
		
		
	}
	
	/**
	 * 
	 * Retrieves the template associated with the component
	 * 
	 * @param template The template to load
	 * @return The loaded template
	 * @throws IOException If something goes wrong while reading the template
	 */
	protected Template getTemplate( XUIViewRoot template ) throws IOException {
		return TemplateLoaderFactory.loadTemplate( template.getTemplate() );
	}
	
	protected void reportErrorProcessingTemplate( XUIViewRoot base, Exception e ) throws IOException {
		getResponseWriter().write( "Could not process the template("+base.getTemplate()+") for component" + base.getId() + " reason: " +  e.getMessage()  );
		e.printStackTrace();
	}
	
	public void templateEncodeChildren(XUIViewRoot component) throws IOException{
		super.encodeChildren( getFacesContext(), component );
	}
	
	
}
