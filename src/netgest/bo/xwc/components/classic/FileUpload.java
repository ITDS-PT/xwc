package netgest.bo.xwc.components.classic;

import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.json.JSONArray;

import netgest.bo.xwc.components.classic.fileuploader.FileUploaderApi;
import netgest.bo.xwc.components.classic.fileuploader.UploadValidation;
import netgest.bo.xwc.components.classic.fileuploader.XeoObjectAttributeAdapter;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.util.ComponentRenderUtils;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.messages.XUIMessageBuilder;
import netgest.io.iFile;

public class FileUpload extends AttributeBase {
	
	/**
	 * Command that removes a file
	 */
	private XUICommand removeCommand;
	
	/**
	 * Class that will validate the File Uploads
	 */
	protected XUIBindProperty< UploadValidation > validationUpload 
		= new XUIBindProperty< UploadValidation >( "validationUpload" , this , UploadValidation.class );
	
	public void setValidationUpload(String validationExpr) {
		validationUpload.setExpressionText( validationExpr );
	}
	
	public UploadValidation getValidationUpload(){
		return validationUpload.getEvaluatedValue();
	}
	
	/**
	 * Maximum Number of Files allowed for upload (defaults to one)
	 */
	protected XUIBaseProperty< Integer > maxFiles = 
			new XUIBaseProperty< Integer >( "maxFiles" , this );
	
	public Integer getMaxFiles() {
		if (maxFiles.isDefaultValue())
			return Integer.valueOf( 1 );
		return maxFiles.getValue();
	}
	
	public void setMaxFiles(int maxFiles) {
		this.maxFiles.setValue( maxFiles );
	}
	
	
	
	/**
	 * Maximum file size for upload in megabytes (defaults to 5 Megabytes)
	 * 0.5 is a valid option
	 */
	protected XUIBindProperty<Integer> maxFileSize = new
		XUIBindProperty< Integer >( "maxFileSize" , this, Integer.class, "5" );
	
	public String getMaxFileSize() {
		return String.valueOf( maxFileSize.getEvaluatedValue() * 1024 * 1024 );
	}
	
	public void setMaxFileSize(Integer maxFileSize) {
		this.maxFileSize.setValue( maxFileSize );
	}
	
	public void setMaxFileSize(String fileSizeExpr){
		maxFileSize.setExpressionText( fileSizeExpr );
	}
	
	/**
	 * Minimum file size for upload in megabytes (default is there's no minimum)
	 * Value is given in megabytes
	 * 
	 */
	protected XUIBindProperty<Integer> minFileSize = new
		XUIBindProperty< Integer >( "minFileSize" , this, Integer.class );
	
	public String getMinFileSize() {
		Integer value = minFileSize.getEvaluatedValue();
		if (value != null){
			return String.valueOf( minFileSize.getEvaluatedValue() * 1024 * 1024 );
		}
		return "";
	}
	
	public void setMixFileSize(Integer minFileSize) {
		this.minFileSize.setValue( minFileSize );
	}
	
	public void setMinFileSize(String fileSizeExpr){
		minFileSize.setExpressionText( fileSizeExpr );
	}
	
	/**
	 * Where to keep the uploaded files
	 */
	protected XUIBindProperty< FileUploaderApi > files = 
			new XUIBindProperty< FileUploaderApi >( "files" , this , FileUploaderApi.class );
	
	private FileUploaderApi api = null;
	
	/**
	 * 
	 * Retrieves the file api implementation
	 * 
	 * @return The file api implementation
	 */
	protected FileUploaderApi getFilesAPI() {
		if (api == null){
			api = files.getEvaluatedValue();
		} return api;
	}
	
	/**
	 * 
	 * Retrieves the list of filenames in the component
	 * 
	 * @return A list of filenames (not including "path" related information)
	 */
	public String[] getFilenames(){
		return getFilesAPI().getFilenames();
	}
	
	public void setFiles(String filesExpr) {
		this.files.setExpressionText( filesExpr );
	}
	
	public void setFiles(FileUploaderApi fileApi) {
		this.files.setValue( fileApi );
	}
	
	/**
	 * 
	 * Adds a new file to the component
	 * 
	 * @param file The file to add
	 */
	public void addFile(iFile file){
		getFilesAPI().addFile( file );
		
	}
	
	/**
	 * Removes a file given its name/id
	 * 
	 * @param filename The name/id of the file to remove
	 */
	public void removeFile(String filename){
		getFilesAPI().removeFile( filename );
	}
	
	/**
	 * 
	 * Retrieve a file given its identifier
	 * 
	 * @param filename The name of the file
	 * 
	 * @return The file
	 */
	public iFile getFile(String filename){
		return getFilesAPI().getFile( filename );
	}
	
	/**
	 * Retrieve the file count
	 * 
	 * @return
	 */
	public long getFileCount(){
		return getFilesAPI().getFileCount();
	}
	
	/**
	 * A list of the valid extensions (defaults to anything).
	 * Note that is only supported in HTML5 enabled browsers. IE7 and IE8 do not
	 * support this. IE9 supports but since it's used in IE7 standards mode it
	 * does not work
	 */
	protected XUIBindProperty< String[] > validExtensions = 
			new XUIBindProperty< String[] >( "validExtensions" , this , String.class );
			
	public String[] getValidExtensions(){
		return validExtensions.getEvaluatedValue();
	}
	
	public void setValidExtensions(String extensionsExpr){
		validExtensions.setExpressionText( extensionsExpr );
	}
	
	public void setValidExtensions(String... values){
		validExtensions.setValue( values );
	}
	
	/**
	 * 
	 * Serializes the list of valid extensions as a JSONArray
	 * 
	 * @return A string with the list of 
	 */
	public String getValidExtensionsSerialized(){
		String[] values = getValidExtensions();
		JSONArray extensions = new JSONArray();
		if (values != null){
			for (String extension : values){
				extensions.put( extension );
			}
		}
		return extensions.toString();
	}
	
	@Override
	public void initComponent() {
		super.initComponent();
		initializeTemplate( "templates/components/fileUpload.ftl" );
		
		includeJavascripts();
    	
    	if (getDataFieldConnector() != null && files.isDefaultValue()){
    		//Talvez devesse ficar noutro lado para não ter a referência directa
    		setFiles( new XeoObjectAttributeAdapter( (XEOObjectAttributeConnector)getDataFieldConnector() ) );
    	}
    	
    	initDefaultMessages();
    	createRemoveFileCommand();
    	
	}

	protected void includeJavascripts() {
		addCss( "fu_css" , "fineuploader.css" );
    	addScript( "fu_util" , "util.js" );
    	addScript( "fu_upbasic" , "uploader.basic.js" );
    	addScript( "fu_xwcupload" , "xwc-upload.js" );
    	addScript( "fu_button" , "button.js" );
    	addScript( "fu_dnd" , "dnd.js" );
    	addScript( "fu_hbase" , "handler.base.js" );
    	addScript( "fu_hform" , "handler.form.js" );
    	addScript( "fu_hxhr" , "handler.xhr.js" );
	}

	protected void createRemoveFileCommand() {
		if (findComponent( getClientId() + "_rmCmd" ) == null){
	    	removeCommand = new XUICommand();
	    	removeCommand.setId( getId() + "_rmCmd" );
	    	removeCommand.addActionListener(new RemoveActionListener());
	        getChildren().add( removeCommand );
    	}
	}

	protected void initDefaultMessages() {
		if (startMessage.isDefaultValue())
    		startMessage.setValue( ComponentMessages.UPLOAD_START_MESSAGE.toString() );
    	if (savingMessage.isDefaultValue())
    		savingMessage.setValue( ComponentMessages.UPLOAD_SAVING_MESSAGE.toString() );
    	if (uploadFailed.isDefaultValue())
    		uploadFailed.setValue( ComponentMessages.UPLOAD_FAILED_MESSAGE.toString() );
    	if (sendingMessage.isDefaultValue())
    		sendingMessage.setValue( ComponentMessages.UPLOAD_SENDING_MESSAGE.toString() );
    	if (progressMessage.isDefaultValue())	
    		progressMessage.setValue( ComponentMessages.UPLOAD_PROGRESS_MESSAGE.toString() );
	}
	
	public static class RemoveActionListener implements ActionListener {
        public void processAction(ActionEvent event) {
        	XUICommand command = (XUICommand)event.getSource();
        	Object argument = command.getCommandArgument();
        	if (argument != null){
        		String filename = argument.toString();
	            FileUpload file = ((FileUpload)(command).getParent());
	            //Prevents File Removal with Disabled/Hidden component
	            if (!file.isDisabled() && file.isVisible()){
		            if (file.getFile( filename ) != null){
		            	file.removeFile( filename );
		            	//Not the best wat, we should make somechange to a state property
		            	//so that the component would re-render but now we're not doing any
		            	file.forceRenderOnClient();
		            }
	            } else {
	            	new XUIMessageBuilder(file.getRequestContext()).message( "Upload Disabled" ).send();
	            }
        	}
        }
    }
	
	/**
	 * Whether or not the component accepts multiple files
	 */
	protected XUIBaseProperty<Boolean> multiple = new XUIBaseProperty< Boolean >( "multiple" , this, Boolean.FALSE );
	
	public Boolean getMultiple() {
		return multiple.getValue();
	}
	
	public void setMultiple(Boolean multiple) {
		this.multiple.setValue( multiple );
	}
	
	public String getServletUrl(){
		return ComponentRenderUtils.getCompleteServletURL( getRequestContext() , getClientId() );
	}
	
	void addScript(String id, String url){
    	getRequestContext().getScriptContext().addInclude( 
    			XUIScriptContext.POSITION_HEADER , id , "js/fileupload/" + url );
    }
    
    void addCss(String id, String url){
    	getRequestContext().getStyleContext().addInclude( 
    			XUIScriptContext.POSITION_HEADER , id , "js/fileupload/" + url );
    }
    
    
    public String getFormId(){
    	XUIForm form = (XUIForm) findParent( XUIForm.class );
    	if (form != null)
    		return form.getClientId();
    	return "";
    }
    
    @Override
    public void setRenderedOnClient(boolean renderedOnClient) {
    	super.setRenderedOnClient( renderedOnClient );
    }
    
    public boolean isVisibleChanged(){
    	return getStateProperty( "visible" ).wasChanged();
    }
    
    public boolean isDisabledChanged(){
    	return getStateProperty( "disabled" ).wasChanged();
    }
    
    public boolean isReadOnlyChanged(){
    	return getStateProperty( "readOnly" ).wasChanged();
    }
    
    public String getSizeError(){
    	return ComponentMessages.UPLOAD_SIZE_ERROR.toString();
    }
    
    public String getMinSizeError(){
    	return ComponentMessages.UPLOAD_MIN_SIZE_ERROR.toString();
    }
    
    public String getTypeError(){
    	return ComponentMessages.UPLOAD_TYPE_ERROR.toString();
    }
    
    public String getNoFilesError(){
    	return ComponentMessages.UPLOAD_NO_FILES_ERROR.toString();
    }
    
    public String getOnLeaveError(){
    	return ComponentMessages.UPLOAD_ON_LEAVE_ERROR.toString();
    }
    
    public String getEmptyError(){
    	return ComponentMessages.UPLOAD_EMPTY_ERROR.toString();
    }
    
    /**
     * Message to use when starting the upload
     */
    XUIBindProperty< String > startMessage = 
    		new XUIBindProperty<String>( "startMessage" , this, String.class );

	public String getStartMessage() {
		return startMessage.getEvaluatedValue();
	}

	public void setStartMessage(String newValExpr) {
		startMessage.setExpressionText( newValExpr );
	}
	
	/**
	 * Message to write when saving a file, can use {file} expression, that will be replaced
	 * with the filename
	 */
	XUIBindProperty< String > savingMessage = new XUIBindProperty< String >(
			"savingMessage" , this, String.class );

	public String getSavingMessage() {
		return savingMessage.getEvaluatedValue();
	}

	public void setSavingMessage(String newValExpr) {
		savingMessage.setExpressionText( newValExpr );
	}
	
	/**
	 * Message when the upload fails
	 */
	XUIBindProperty< String > uploadFailed = new XUIBindProperty< String >(
			"uploadFailed" , this , String.class );

	public String getUploadFailed() {
		return uploadFailed.getEvaluatedValue();
	}

	public void setUploadFailed(String newValExpr) {
		uploadFailed.setExpressionText( newValExpr );
	}
	
	
	/**
	 * Message to display when starting to send a file
	 */
	XUIBindProperty< String > sendingMessage = new XUIBindProperty< String >(
			"sendingMessage" , this , String.class );

	public String getSendingMessage() {
		return sendingMessage.getEvaluatedValue();
	}

	public void setSendingMessage(String newValExpr) {
		sendingMessage.setExpressionText( newValExpr );
	}
    
    /**
     * Message used when displaying the progress of a file
     * Can use the {progress} and {total} expressions to build the message
     * total is displayed in Megabytes
     */
    XUIBindProperty< String > progressMessage = new XUIBindProperty< String >(
			"progressMessage" , this , String.class );

	public String getprogressMessage() {
		return progressMessage.getEvaluatedValue();
	}

	public void setprogressMessage(String newValExpr) {
		progressMessage.setExpressionText( newValExpr );
	}
}
