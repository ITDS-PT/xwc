package netgest.bo.xwc.components.template.xeo;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.runtime.boObject;
import netgest.bo.xwc.components.connectors.XEOObjectConnector;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.components.template.loader.TemplateLoaderFactory;
import netgest.bo.xwc.components.template.preprocessor.CommandsPreProcessor;
import netgest.bo.xwc.components.template.xeo.wrappers.TemplateDataRecordConnectorWrapper;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.utils.StringUtils;


public class Xeoobject extends XUIComponentBase {

	private XUIViewBindProperty<boObject> object = new XUIViewBindProperty<boObject>(
			"object", this, boObject.class);
	
	private XUIViewBindProperty<String> name = new XUIViewBindProperty<String>(
			"name", this, String.class);
	
	private XUIViewBindProperty<String> boui = new XUIViewBindProperty<String>(
			"boui", this, String.class);
		
	
	private boObject currentObject = null;
	
	public boObject getCurrentObject() {
		return currentObject;
	}

	public void setCurrentObject(boObject currentObject) {
		this.currentObject = currentObject;
	}

	public String getName() {		
		return name.getEvaluatedValue();		
	}
	
	public void setName(String name) {
		this.name.setValue(createValueExpression(name,
				String.class));
	}
	
	public String getBoui() {		
		return boui.getEvaluatedValue();		
	}
	
	public void setBoui(String boui) {
		this.boui.setValue(createValueExpression(boui,
				String.class));
	}
	
	public boObject getObject() {		
		return object.getEvaluatedValue();		
	}
	
	public void setObject(String object) {
		this.object.setValue(createValueExpression(object,
				boObject.class));
	}
	
	public TemplateDataRecordConnectorWrapper getXeoobject() {
		TemplateDataRecordConnectorWrapper recordWrapper=null;
		if (this.currentObject==null) {
			if (this.getObject()!=null)
				recordWrapper = new TemplateDataRecordConnectorWrapper(new XEOObjectConnector(this.getObject(), 0));
			else if (this.getBoui()!=null) {
				recordWrapper = new TemplateDataRecordConnectorWrapper(new XEOObjectConnector(Long.parseLong(this.getBoui()), 0));
			}
		}
		else {
			if (this.getObject()!=null) {
				if (this.currentObject.getBoui()==this.getObject().getBoui())
					recordWrapper = new TemplateDataRecordConnectorWrapper(new XEOObjectConnector(currentObject, 0));
				else
					recordWrapper = new TemplateDataRecordConnectorWrapper(new XEOObjectConnector(this.getObject(), 0));
			}
			else if (this.getBoui()!=null) {
				if (this.currentObject.getBoui()==Long.parseLong(this.getBoui()))
					recordWrapper = new TemplateDataRecordConnectorWrapper(new XEOObjectConnector(currentObject, 0));
				else
					recordWrapper = new TemplateDataRecordConnectorWrapper(new XEOObjectConnector(Long.parseLong(this.getBoui()), 0));
			}			
		}
		if (recordWrapper!=null) {
			XEOObjectConnector connector = (XEOObjectConnector)recordWrapper.getRecord();
			this.currentObject = connector.getXEOObject();
		}
		return recordWrapper;
	}
	
	@Override
	public void initComponent() {
		super.initComponent();
		if (!this.template.isDefaultValue( )){
			CommandsPreProcessor p = new CommandsPreProcessor( loadTemplate( getTemplate() ), this );
			List<UIComponent> list = p.createComponents( );
				getChildren( ).addAll( list );
		}
	}
	
	protected freemarker.template.Template loadTemplate(String name){
		try {
			return TemplateLoaderFactory.loadTemplate( name );
		} catch ( IOException e ) {
			throw new RuntimeException( String.format(" Could not load template %s ", name ) , e );
		}
	}
	
	public void preRender() {
		if (!StringUtils.isEmpty(this.getName())) {
			HttpServletRequest request = (HttpServletRequest)getRequestContext().getRequest();
			String boui=request.getParameter(this.getName()+"boui");
			if (!StringUtils.isEmpty(boui))
					this.setBoui(boui);
			
			boui=request.getParameter(this.getName());
			if (!StringUtils.isEmpty(boui))
					this.setBoui(boui);				
		}
	}
	
	public static class XEOObjectRenderer extends TemplateRenderer {

		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			Xeoobject oComp = (Xeoobject)component;							
			w.startElement("div");
			w.writeAttribute("id", oComp.getClientId());
			super.encodeBegin(component);
			w.endElement("div");
		}
		
	}
}
