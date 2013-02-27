package netgest.bo.xwc.components.classic.fileuploader;

import netgest.io.iFile;

public interface FileUploaderApi {
	
	public void removeFile(String filename);
	
	public void addFile(iFile file);
	
	public iFile getFile(String filename);
	
	public int getFileCount();
	
	public String[] getFilenames();
	

}
