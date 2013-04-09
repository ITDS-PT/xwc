package netgest.bo.xwc.components.classic.fileuploader.bridge;

import java.util.ArrayList;
import java.util.List;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.classic.fileuploader.FileUploaderApi;
import netgest.io.iFile;

/**
 * 
 * FileUploaderApi implementation using an Object's collection attribute
 *
 */
public class BridgeFileApi implements FileUploaderApi {
	
	private static final Logger logger = Logger.getLogger( BridgeFileApi.class );
	
	private long boui;
	private String bridgeName;
	private String attributeName;
	
	public String toString() {
		return super.toString();
	}
	
	public BridgeFileApi(long boui, String bridgeName, String attributeName) {
		assert boui > 0 : String.format( "Cannot load boui 0 for %s having attribute %s", bridgeName, attributeName);
		this.boui = boui;
		this.bridgeName = bridgeName;
		this.attributeName = attributeName;
	}
	
	protected bridgeHandler getHandler(){
		try {
			EboContext ctx = boApplication.currentContext().getEboContext();
			boObject object = boObject.getBoManager().loadObject( ctx , boui );
			return object.getBridge( bridgeName );
		} catch ( boRuntimeException e ) {
			logger.warn( "Could not load bridge %s for object %d" , e , bridgeName, boui );
			e.printStackTrace();
		}
		return null;
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	@Override
	public void removeFile(String filename) {
		boBridgeIterator it = getHandler().iterator();
		try {
			while (it.next()){
				boObject current = it.currentRow().getObject();
				iFile file = current.getAttribute( getAttributeName() ).getValueiFile();
				if (file != null){
					if (filename.equalsIgnoreCase( file.getName() )){
						current.getAttribute( getAttributeName() ).setValueObject( null );
						break;
					}
				}
			}
		} catch ( boRuntimeException e ) {
			logger.warn( "Could not get %s from bridge %s of object %s (%d)"
					, e, filename, getHandler().getName(), getHandler().getParent().getName(), getHandler().getParent().getBoui() );
		}
	}

	@Override
	public void addFile(iFile file) {
		try {
			boObject object = getHandler().addNewObject();
			object.getAttribute( getAttributeName() ).setValueiFile( file );
		} catch ( boRuntimeException e ) {
			logger.warn( "Could not add %s to bridge %s of object %s (%d)"
					, e, file.getName(), getHandler().getName(), getHandler().getParent().getName(), getHandler().getParent().getBoui() );
		}
	}

	@Override
	public iFile getFile(String filename) {
		boBridgeIterator it = getHandler().iterator();
		try {
			while (it.next()){
				boObject current = it.currentRow().getObject();
				iFile file = current.getAttribute( getAttributeName() ).getValueiFile();
				if (file != null){
					if (filename.equalsIgnoreCase( file.getName() ))
						return file;
				}
			}
		} catch ( boRuntimeException e ) {
			logger.warn( "Could not get %s from bridge %s of object %s (%d)"
					, e, filename, getHandler().getName(), getHandler().getParent().getName(), getHandler().getParent().getBoui() );
		}
		return null;
	}

	@Override
	public int getFileCount() {
		return getFilenames().length;
	}

	@Override
	public String[] getFilenames() {
		List<String> files = new ArrayList<String>();
		boBridgeIterator it = getHandler().iterator();
		try {
			while (it.next()){
				boObject current = it.currentRow().getObject();
				iFile file = current.getAttribute( getAttributeName() ).getValueiFile();
				if (file != null){
					files.add( file.getName() );
				}
			}
		} catch ( boRuntimeException e ) {
			logger.warn( "Could not get filenames from bridge %s of object %s (%d)"
					, e, getHandler().getName(), getHandler().getParent().getName(), getHandler().getParent().getBoui() );
		}
		return files.toArray( new String[files.size()] );
	}
	
}
