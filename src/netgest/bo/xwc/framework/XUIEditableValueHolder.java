package netgest.bo.xwc.framework;

import javax.faces.component.EditableValueHolder;

public interface XUIEditableValueHolder extends EditableValueHolder {
	
	public boolean isModelValid();
	public void setModelValid( boolean valid );
	
}
