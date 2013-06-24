package netgest.bo.xwc.components.template.xeo.wrappers;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOBridgeRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListRowConnector;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class TemplateDataRecordConnectorWrapper implements TemplateHashModel {
	
	private DataRecordConnector record = null;
	
	public TemplateDataRecordConnectorWrapper(DataRecordConnector record){
		this.record = record;
	}
	
	public TemplateModel get(String name) {
		TemplateDataFieldConnectorWrapper wrapper=new TemplateDataFieldConnectorWrapper(this.record,record.getAttribute(name));
		
		if (isXEOObject() && (name.equals(TemplateDataFieldConnectorWrapper.SYSCARDID) || 
				name.equals(TemplateDataFieldConnectorWrapper.SYSCARDIDIMG))) {
			XEOObjectConnector obj=(XEOObjectConnector)this.record;
			try {
				if (name.toLowerCase().equals(TemplateDataFieldConnectorWrapper.SYSCARDIDIMG))
						return new SimpleScalar(obj.getXEOObject().getCARDID().toString());
					else
						return new SimpleScalar(obj.getXEOObject().getCARDIDwNoIMG().toString());
			}
			catch (boRuntimeException e) {
				
			}		
		}
		
		if (isXEOObject() && (name.equals(TemplateDataFieldConnectorWrapper.LABEL))) {
			XEOObjectConnector obj=(XEOObjectConnector)this.record;
			try {
				if (name.toLowerCase().equals(TemplateDataFieldConnectorWrapper.LABEL))
						return new SimpleScalar(obj.getXEOObject().getLabel());					
			}
			catch (boRuntimeException e) {
				
			}		
		}
		
		if (((isXEOObject() || 
				wrapper.getField()==null) && name.endsWith("$"))) {
			name=name.substring(0, name.length()-1);
			wrapper = new TemplateDataFieldConnectorWrapper(this.record,record.getAttribute(name));
			return wrapper.get("value");
		}
		
		return wrapper;
	}

	@Override
	public boolean isEmpty() throws TemplateModelException {
		return false;
	}

	public DataRecordConnector getRecord() {
		return record;
	}
	
	public boolean isXEOObject() {
		boolean toRet=false;
		if (this.record instanceof XEOObjectListRowConnector ||
				this.record instanceof XEOBridgeRecordConnector 
				|| this.record instanceof XEOObjectConnector)
			toRet=true;
		return toRet;
	}
	
}
