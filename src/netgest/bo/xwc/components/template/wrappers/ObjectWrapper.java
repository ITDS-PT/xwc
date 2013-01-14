package netgest.bo.xwc.components.template.wrappers;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Wraps a boObject so that it can be used inside a FTL template
 *
 */
public class ObjectWrapper implements TemplateHashModel, XeoWrapper {
	
	private boObject object;
	
	public ObjectWrapper(boObject obj){
		this.object = obj;
	}
	
	public TemplateModel get(String name){
		if (object.getBridge(name) != null){
			return new BridgeWrapper(object.getBridge(name));
		} else
			try {
				return new SimpleScalar( object.getAttribute(name).getValueString() );
			} catch ( boRuntimeException e ) {
				return new SimpleScalar( e.getMessage() );
			}
	}
	

	@Override
	public boolean isEmpty() throws TemplateModelException {
		return false;
	}
	
}
