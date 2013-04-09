package netgest.bo.xwc.components.template.wrappers;

import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

class ListIterator implements TemplateModelIterator {

	boObjectList list;
	
	ListIterator(boObjectList list){
		this.list = list;
	}
	
	@Override
	public TemplateModel next() throws TemplateModelException {
		try { 
			list.next();
			return new ObjectWrapper(list.getObject());
		} catch ( boRuntimeException e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean hasNext() throws TemplateModelException {
		return list.getRow() < list.getRowCount();
	}

}
