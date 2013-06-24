package netgest.bo.xwc.components.classic.fileuploader;

import java.io.File;

public class UploadedFile implements AttachedFile {
	
	private String filename;
	private File file;
	
	public UploadedFile(final String filename, final File file){
		this.filename = filename;
		this.file = file;
	}
	
	public String toString() {
		return getName();
	}

	@Override
	public String getName() {
		return filename;
	}

	@Override
	public File getFile() {
		return file;
	}

	
}
