package netgest.bo.xwc.components.template.xeo.wrappers;

import netgest.bo.xwc.components.connectors.DataListIterator;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

public class TemplateListWrapper implements TemplateCollectionModel {

	private DataListIterator list;
	
	public TemplateListWrapper(DataListIterator list){
		this.list = list;
	}
	
	@Override
	public TemplateModelIterator iterator() throws TemplateModelException {
		
		return new TemplateListIterator( list );
	}

	public DataListIterator getList() {
		return list;
	}

	public void setList(DataListIterator list) {
		this.list = list;
	}
	

}