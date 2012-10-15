package netgest.bo.xwc.components.template.wrappers;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class LovItemWrapper implements TemplateHashModel {

	private String value;
	private String description;
	
	public LovItemWrapper(String value, String description){
		this.value = value;
		this.description = description;
	}
	
	public String getValue(){
		return value;
	}
	
	public String getDescription(){
		return description;
	}

	@Override
	public TemplateModel get( String key ) throws TemplateModelException {
		if ("value".equalsIgnoreCase( key ))
			return new SimpleScalar(getValue());
		return new SimpleScalar(getDescription());
	}

	@Override
	public boolean isEmpty() throws TemplateModelException {
		return false;
	}
	
}
