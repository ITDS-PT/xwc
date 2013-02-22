package netgest.bo.xwc.components.template.xeo;

import java.io.IOException;

import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.generic.GenericDataListConnector;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.components.template.xeo.wrappers.TemplateListWrapper;


public class Genericlist extends XUIComponentBase {

	private XUIBindProperty<GenericDataListConnector> dataSource = new XUIBindProperty<GenericDataListConnector>(
			"dataSource", this, GenericDataListConnector.class);
	
	private GenericDataListConnector connector = null;	
	public GenericDataListConnector getDataSource() {
		if (!hasDataSourceBeenEvaluated || connector == null){
			if (dataSource.getValue() != null) {
				connector = dataSource.getEvaluatedValue();
				if( connector != null )
					hasDataSourceBeenEvaluated = true;
			}
		} 
		return connector;
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource.setValue(createValueExpression(dataSource,
				GenericDataListConnector.class));
	}
	
	public DataListConnector getConnector() {
		return this.connector;
	}

	
	private boolean hasDataSourceBeenEvaluated = false; 
	
	@Override
	public void preRender() {
		hasDataSourceBeenEvaluated = false;
		super.preRender();
	}
	
	public void initConnector() {
		connector.refresh();				
	}
	
	public TemplateListWrapper getList() {
		initConnector();
		return new TemplateListWrapper(getConnector().iterator());	
	}
	
	public static class ListRenderer extends TemplateRenderer {

		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			Genericlist oComp = (Genericlist)component;
			
			
			w.startElement("div");
			w.writeAttribute("id", oComp.getClientId());
			super.encodeBegin(component);
			w.endElement("div");
		}
	}
}
