package netgest.bo.xwc.components.template.xeo;

import java.io.IOException;

import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.generic.GenericDataListConnector;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class Genericlist extends PaginatedConnectorList {

	private XUIBindProperty<DataListConnector> dataSource = new XUIBindProperty<DataListConnector>(
			"dataSource", this, DataListConnector.class);
	
	private int recordCount=-1;
	
	private DataListConnector connector = null;
	
	public DataListConnector getDataSource() {
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
		if (this.connector == null)
			this.getDataSource();
		
		if (connector.getPage()!=new Integer(this.getPage()).intValue() ||
				connector.getPageSize()!=new Integer(this.getPagesize()).intValue()) {
			connector.setPage(new Integer(this.getPage()).intValue());
			connector.setPageSize(new Integer(this.getPagesize()).intValue());						
			//recordCount = connector.getRecordCount();
		}		
	}
	
	@Override
	public int getRecordCount() {
		if (this.connector!=null && this.recordCount==-1)
			this.recordCount = connector.getRecordCount();
		return this.recordCount;
	}
	
	public static class ListRenderer extends TemplateRenderer {

		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			Genericlist oComp = (Genericlist)component;
			oComp.initConnector();
			
			w.startElement("div");
			w.writeAttribute("id", oComp.getClientId());
			super.encodeBegin(component);
			w.endElement("div");
		}
	}	
}
