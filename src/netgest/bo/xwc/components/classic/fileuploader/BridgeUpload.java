package netgest.bo.xwc.components.classic.fileuploader;

import netgest.bo.xwc.components.annotations.Required;
import netgest.bo.xwc.components.classic.FileUpload;
import netgest.bo.xwc.components.classic.fileuploader.bridge.BridgeFileApi;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.framework.XUIBindProperty;

/**
 * Implements upload functionality using a bridge as storage
 *
 */
public class BridgeUpload extends FileUpload {
	
	/**
	 * The attribute in the target bridge object which contains the iFile
	 */
	@Required
	XUIBindProperty< String > fileAttribute = new XUIBindProperty< String >(
			"fileAttribute" , this , String.class );

	public String getFileAttribute() {
		return fileAttribute.getEvaluatedValue();
	}

	public void setFileAttribute(String newValExpr) {
		fileAttribute.setExpressionText( newValExpr );
	}
	
	
	@Override
	protected FileUploaderApi getFilesAPI() {
		return super.getFilesAPI();
	}
	
	@Override
	public void initComponent() {
		super.initComponent();
		DataFieldConnector connector = getDataFieldConnector();
		long boui = 0;
		if (connector != null){
			if (connector instanceof XEOObjectAttributeConnector){
				XEOObjectAttributeConnector attConnector = ( XEOObjectAttributeConnector ) connector;
				boui = attConnector.getAttributeHandler().getParent().getBoui();
			}
		}
		
		files.setValue( new BridgeFileApi( boui , getObjectAttribute() , getFileAttribute() ) );
		multiple.setValue( Boolean.TRUE );
		super.initComponent();
	}

}
