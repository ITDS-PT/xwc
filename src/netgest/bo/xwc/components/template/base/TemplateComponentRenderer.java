package netgest.bo.xwc.components.template.base;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import netgest.bo.xwc.components.template.TemplateCommand;
import netgest.bo.xwc.components.template.TemplateInput;
import netgest.bo.xwc.components.template.css.XwcCssContext;
import netgest.bo.xwc.components.template.directives.ChildDirectiveProcessor;
import netgest.bo.xwc.components.template.directives.CssDirectiveProcessor;
import netgest.bo.xwc.components.template.directives.HeaderWriterDirectiveProcessor;
import netgest.bo.xwc.components.template.directives.JavaScriptDirectiveProcessor;
import netgest.bo.xwc.components.template.directives.XUICommandDirectiveProcessor;
import netgest.bo.xwc.components.template.directives.XUIInputDirectiveProcessor;
import netgest.bo.xwc.components.template.javascript.XwcScriptContext;
import netgest.bo.xwc.components.template.loader.TemplateLoaderFactory;
import netgest.bo.xwc.components.template.resolver.TemplateContextVariables;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStyleContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.utils.StringUtils;
import freemarker.core.ParseException;
import freemarker.template.Template;
/**
 * 
 * Rendered for template based components
 * Reserved template keywords : all from the {@link ProcessorDirectives} enum and "XVWScripts", "bundles", "this"
 * 
 *
 */
public class TemplateComponentRenderer extends TemplateRenderer {

	
	@Override
	public StateChanged wasStateChanged(XUIComponentBase component, List<XUIBaseProperty<?>> updateProperties) {
	    updateProperties.add( component.getStateProperty("update") );
	    return super.wasStateChanged(component, updateProperties);
	}
	

}
