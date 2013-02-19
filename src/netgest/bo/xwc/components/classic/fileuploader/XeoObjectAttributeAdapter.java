package netgest.bo.xwc.components.classic.fileuploader;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.io.iFile;

public class XeoObjectAttributeAdapter implements FileUploaderApi {
	
	private XEOObjectAttributeConnector connector;
	
	public XeoObjectAttributeAdapter(XEOObjectAttributeConnector con){
		connector = con;
	}
	
	@Override
	public void removeFile(String filename) {
		try{
			connector.getAttributeHandler().setValueiFile( null );
		} catch (boRuntimeException e){
			e.printStackTrace();
		}
	}

	@Override
	public void addFile(iFile file) {
		try{
		connector.getAttributeHandler().setValueiFile( file );
		} catch (boRuntimeException e){
			e.printStackTrace();
		}
	}

	@Override
	public iFile getFile(String filename) {
		iFile file;
		try {
			//FIXME: Isto não deveria estar aqui, provavelmente
			//deveria ser um carregamento algure
			EboContext ctx = boApplication.currentContext().getEboContext();
			boObject.getBoManager().loadObject( ctx , connector.getAttributeHandler().getParent().getBoui() );
			file = connector.getAttributeHandler().getValueiFile();
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
		return connector.toString();
	}

	@Override
	public String[] getFilenames() {
		iFile file;
		try {
			file = connector.getAttributeHandler().getValueiFile();
			if (file != null)
				return new String[]{file.getName()};
		} catch ( boRuntimeException e ) {
			e.printStackTrace();
		} 
		return new String[0];
	}
	
}
