package netgest.bo.xwc.components.template.wrappers;

import netgest.bo.runtime.boObjectList;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

/**
 * Wraps a {@link boObjectList} so that it can be used in a FTL Template
 *
 */
public class ListWrapper implements TemplateCollectionModel, XeoWrapper {

	private boObjectList list;
	
	public ListWrapper(boObjectList list){
		this.list = list;
	}
	
	@Override
	public TemplateModelIterator iterator() throws TemplateModelException {
		
		return new ListIterator( list );
	}
	

}
