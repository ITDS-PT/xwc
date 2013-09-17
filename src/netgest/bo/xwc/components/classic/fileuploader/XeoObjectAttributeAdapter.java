package netgest.bo.xwc.components.classic.fileuploader;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;

import netgest.io.iFile;

/**
 * Adapts the {@link XEOObjectAttributeConnector} class to be able to serve
 * as a {@link FileUploaderApi} 
 *
 */
public class XeoObjectAttributeAdapter implements FileUploaderApi {
	
	private static final Logger logger = Logger.getLogger( XeoObjectAttributeAdapter.class );
	
	/**
	 * Identifier of the object to load
	 */
	private long boui;
	/**
	 * The name of the attribute keeping the binary content
	 */
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
			logger.warn( "Could not remove file %s from %d in attribute %s" , e , filename, boui,  attributeName );
		}
	}

	@Override
	public void addFile(iFile file) {
		try{
			getAttributeHandler().setValueiFile( file );
		} catch (boRuntimeException e){
			logger.warn( "Could not add file %s from %d in attribute %s" , e , file.getName(), boui,  attributeName );
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
			logger.warn( "Could not get file %s from %d in attribute %s" , e , filename, boui,  attributeName );
		}
		return null;
	}

	@Override
	public int getFileCount() {
		Object value;
		try {
			value = getAttributeHandler().getValueObject();
			if (value == null || "".equals( value )) {
				return 0;
			}
			return 1;
		} catch ( boRuntimeException e ) {
			logger.warn("Could not count the files ",e);
		}
		
		return 0;
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
			logger.warn( "Problem getting files from from %d in attribute %s" , e , boui,  attributeName );
		} 
		return new String[0];
	}
	
}
