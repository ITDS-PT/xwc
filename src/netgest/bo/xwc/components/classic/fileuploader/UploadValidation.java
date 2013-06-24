package netgest.bo.xwc.components.classic.fileuploader;

/**
 * Class that allows to validate an uploaded file 
 *
 */
public interface UploadValidation {

	public AttachedFileValidation validate(AttachedFile file); 
	
}
