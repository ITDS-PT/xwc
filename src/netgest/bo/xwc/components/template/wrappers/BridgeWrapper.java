package netgest.bo.xwc.components.template.wrappers;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

/**
 * Wraps a bridge handler so that it can be used inside a FTL template
 *
 */
public class BridgeWrapper implements TemplateCollectionModel {

	private bridgeHandler handler;
	
	public BridgeWrapper(bridgeHandler handler){
		this.handler = handler;
	}
	
	@Override
	public TemplateModelIterator iterator() throws TemplateModelException {
		
		return new TemplateModelIterator() {
			
			@Override
			public TemplateModel next() throws TemplateModelException {
				try {
					return new ObjectWrapper(handler.getObject());
				} catch ( boRuntimeException e ) {
					e.printStackTrace();
				} return null;
			}
			
			@Override
			public boolean hasNext() throws TemplateModelException {
				return handler.next();
			}
		};
	}

}