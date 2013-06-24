package netgest.bo.xwc.components.template.xeo;

import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.template.xeo.wrappers.TemplateListWrapper;


public abstract class PaginatedConnectorList extends PaginatedList {
	
	
	public abstract DataListConnector getConnector();	
	
	public int getPages() {
		int pages=0;
		if (getConnector()!=null)
			if (getRecordCount()==0)
				pages=0;
			else {
				int psize=new Integer(getConnector().getPageSize()).intValue();
				if ((getRecordCount() % psize) == 0)
					pages=(getRecordCount()/psize);
				else
					pages=(getRecordCount()/psize)+1;
				if (pages==0)
					pages=1;
			}
		return pages;
	}
	
	public abstract int getRecordCount();
	
	
	public TemplateListWrapper getList() {
		//initConnector();
		return new TemplateListWrapper(getConnector().iterator());	
	}
		
}
