package netgest.bo.xwc.components.classic.fileuploader;

import java.io.File;

/**
 * Represents an attached file uploaded by the user
 *
 */
public interface AttachedFile {

	/**
	 * @return The file name
	 */
	public String getName();
	
	/**
	 * The file itself
	 */
	public File getFile();
	
}
