package netgest.bo.xwc.components.classic;

import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.json.JSONArray;

import netgest.bo.xwc.components.classic.fileuploader.FileUploaderApi;
import netgest.bo.xwc.components.classic.fileuploader.UploadValidation;
import netgest.bo.xwc.components.classic.fileuploader.XeoObjectAttributeAdapter;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.components.util.ComponentRenderUtils;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.messages.XUIMessageBuilder;
import netgest.io.iFile;

public class FileUpload extends AttributeBase {
	
	private XUICommand removeCommand;
	
	/**
	 * Class that will validate the File Uploads
	 */
	private XUIBindProperty< UploadValidation > validationUpload 
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
	private XUIBaseProperty< Integer > maxFiles = 
			new XUIBaseProperty< Integer >( "maxFiles" , this, Integer.valueOf( 1 ) );
	
	public Integer getMaxFiles() {
		return maxFiles.getValue();
	}
	
	public void setMaxFiles(Integer maxFiles) {
		this.maxFiles.setValue( maxFiles );
	}
	
	/**
	 * Maximum file size for upload in megabytes (defaults to 5 Megabytes)
	 * 0.5 is a valid option
	 */
	private XUIBindProperty<Integer> maxFileSize = new
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
	private XUIBindProperty<Integer> minFileSize = new
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
	 * Where to keep the files uploded
	 */
	private XUIBindProperty< FileUploaderApi > files = 
			new XUIBindProperty< FileUploaderApi >( "files" , this , FileUploaderApi.class );
	
	private FileUploaderApi api = null;
	
	FileUploaderApi getFilesAPI() {
		if (api == null){
			api = files.getEvaluatedValue();
		} return api;
	}
	
	public String[] getFilenames(){
		return getFilesAPI().getFilenames();
	}
	
	public void setFiles(String filesExpr) {
		this.files.setExpressionText( filesExpr );
	}
	
	public void setFiles(FileUploaderApi fileApi) {
		this.files.setValue( fileApi );
	}
	
	public void addFile(iFile file){
		getFilesAPI().addFile( file );
	}
	
	public void removeFile(String filename){
		getFilesAPI().removeFile( filename );
	}
	
	public iFile getFile(String filename){
		return getFilesAPI().getFile( filename );
	}
	
	public long getFileCount(){
		return getFilesAPI().getFileCount();
	}
	
	/**
	 * A list of the valid extensions (defaults to anything).
	 * Note that is only supported in HTML5 enabled browsers. IE7 and IE8 do not
	 * support this. IE9 supports but since it's used in IE7 standards mode it
	 * does not work
	 */
	private XUIBindProperty< String[] > validExtensions = 
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
		
		addCss( "fu_css" , "fineuploader.css" );
    	addScript( "fu_util" , "util.js" );
    	addScript( "fu_upbasic" , "uploader.basic.js" );
    	addScript( "fu_up" , "uploader.js" );
    	addScript( "fu_button" , "button.js" );
    	addScript( "fu_dnd" , "dnd.js" );
    	addScript( "fu_hbase" , "handler.base.js" );
    	addScript( "fu_hform" , "handler.form.js" );
    	addScript( "fu_hxhr" , "handler.xhr.js" );
    	addScript( "fu_jqplug" , "jquery-plugin.js" );
    	
    	if (getDataFieldConnector() != null && files.isDefaultValue()){
    		//Talvez devesse ficar noutro lado para não ter
    		//a referência directa
    		setFiles( new XeoObjectAttributeAdapter( (XEOObjectAttributeConnector)getDataFieldConnector() ) );
    	}
    	
    	if (findComponent( getClientId() + "_rmCmd" ) == null){
	    	removeCommand = new XUICommand();
	    	removeCommand.setId( getId() + "_rmCmd" );
	    	removeCommand.addActionListener(new RemoveActionListener());
	        getChildren().add( removeCommand );
    	}
    	
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
	private XUIBaseProperty<Boolean> multiple = new XUIBaseProperty< Boolean >( "multiple" , this, Boolean.FALSE );
	
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
	//@Valid File Extensions
	
}
