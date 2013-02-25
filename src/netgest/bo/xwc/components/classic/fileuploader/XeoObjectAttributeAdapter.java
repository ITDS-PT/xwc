package netgest.bo.xwc.components.classic.fileuploader;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.io.iFile;

public class XeoObjectAttributeAdapter implements FileUploaderApi {
	
	private long boui;
	private String attributeName;
	
	public XeoObjectAttributeAdapter(XEOObjectAttributeConnector con){
		boui = con.getAttributeHandler().getParent().getBoui();
		attributeName = con.getAttributeHandler().getDefAttribute().getName();
	}
	
	AttributeHandler getAttributeHandler() throws boRuntimeException {
		EboContext ctx = boApplication.currentContext().getEboContext();
		return boObject.getBoManager().loadObject( ctx , boui ).getAttribute( attributeName );
	}
	
	@Override
	public void removeFile(String filename) {
		try{
			getAttributeHandler().setValueiFile( null );
		} catch (boRuntimeException e){
			e.printStackTrace();
		}
	}

	@Override
	public void addFile(iFile file) {
		try{
			getAttributeHandler().setValueiFile( file );
		} catch (boRuntimeException e){
			e.printStackTrace();
		}
	}

	@Override
	public iFile getFile(String filename) {
		iFile file;
		try {
			file = getAttributeHandler().getValueiFile();
			if (file != null)
				return file;
		} catch ( boRuntimeException e ) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getFileCount() {
		return 1;
	}

	public String toString() {
		return boui + " " + attributeName;
	}

	@Override
	public String[] getFilenames() {
		iFile file;
		try {
			file = getAttributeHandler().getValueiFile();
			if (file != null)
				return new String[]{file.getName()};
		} catch ( boRuntimeException e ) {
			e.printStackTrace();
		} 
		return new String[0];
	}
	
}
