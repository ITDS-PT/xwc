package netgest.bo.xwc.components.template.renderers;

import java.io.File;
import java.util.List;

import netgest.bo.xwc.components.classic.FileUpload;
import netgest.bo.xwc.components.classic.fileuploader.BridgeUpload;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;
import netgest.io.FSiFile;

public class BridgeUploadRenderer extends FileUploadRenderer {

	@Override
	protected void setFileInComponent(FileUpload uploader, File uploadedile) {
		BridgeUpload bridge = (BridgeUpload) uploader;
		bridge.addFile( new FSiFile( uploadedile ) );
	}
	
	@Override
	public StateChanged wasStateChanged(XUIComponentBase component,
			List< XUIBaseProperty< ? >> updateProperties) {
		return super.wasStateChanged( component , updateProperties );
	}
	

	
	
}
