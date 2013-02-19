package netgest.bo.xwc.components.classic.fileuploader;

public class AttachedFileValidation {

	private String fileName;
	private String errorMessage;
	private boolean isValid;
	
	public AttachedFileValidation(final String fileName, final String errorMessage, final boolean valid) {
		this.fileName = fileName;
		this.errorMessage = errorMessage;
		this.isValid = valid;
	}
	
	public boolean isValid(){
		return isValid;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	

	
}
