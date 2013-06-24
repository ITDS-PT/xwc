package netgest.bo.xwc.components.template.xeo;

import java.io.IOException;
import java.util.List;

import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class Arraylist extends XUIComponentBase {

	private XUIBindProperty<List<Object>> dataSource = new XUIBindProperty<List<Object>>(
			"dataSource", this, List.class);
		
	public List<Object> getDataSource() {		
		return dataSource.getEvaluatedValue();		
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource.setValue(createValueExpression(dataSource,
				List.class));
	}
			
	public List<Object> getList() {
		return getDataSource();
	}
	
	public static class ListRenderer extends TemplateRenderer {

		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			Arraylist oComp = (Arraylist)component;
			w.startElement("div");
			w.writeAttribute("id", oComp.getClientId());
			super.encodeBegin(component);
			w.endElement("div");
		}
	}
}
