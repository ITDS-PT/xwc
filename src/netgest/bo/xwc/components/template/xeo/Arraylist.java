package netgest.bo.xwc.components.template.xeo;

import java.io.IOException;
import java.util.ArrayList;

import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class Arraylist extends XUIComponentBase {

	private XUIBindProperty<ArrayList<Object>> dataSource = new XUIBindProperty<ArrayList<Object>>(
			"dataSource", this, ArrayList.class);
		
	public ArrayList<Object> getDataSource() {		
		return dataSource.getEvaluatedValue();		
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource.setValue(createValueExpression(dataSource,
				ArrayList.class));
	}
			
	public ArrayList<Object> getList() {
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
