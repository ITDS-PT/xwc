package netgest.bo.xwc.components.template.xeo.wrappers;

import netgest.bo.xwc.components.connectors.DataListIterator;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelIterator;

public class TemplateListIterator implements TemplateModelIterator {

	DataListIterator list;
	
	public TemplateListIterator(DataListIterator list){
		this.list = list;
	}
	
	public TemplateModel next() {
		return new TemplateDataRecordConnectorWrapper(list.next());
	}
	
	public boolean hasNext()  {
		return list.hasNext();
	}

}
