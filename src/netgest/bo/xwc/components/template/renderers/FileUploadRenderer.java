package netgest.bo.xwc.components.template.renderers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.impl.document.Ebo_DocumentImpl;
import netgest.bo.system.Logger;
import netgest.bo.xwc.components.classic.FileUpload;
import netgest.bo.xwc.components.classic.fileuploader.AttachedFileValidation;
import netgest.bo.xwc.components.classic.fileuploader.ReceiveFile;
import netgest.bo.xwc.components.classic.fileuploader.UploadValidation;
import netgest.bo.xwc.components.classic.fileuploader.UploadedFile;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIComponentBase.StateChanged;
import netgest.io.FSiFile;
import netgest.io.iFile;

public class FileUploadRenderer extends TemplateRenderer implements XUIRendererServlet{

	private static final Logger logger = Logger.getLogger( FileUploadRenderer.class );
	
	@Override
	public void service(ServletRequest oRequest, ServletResponse oResponse,
			XUIComponentBase oComp) throws IOException {

		FileUpload uploader = (FileUpload) oComp;
		
		HttpServletRequest request = (HttpServletRequest) oRequest;
		HttpServletResponse response = (HttpServletResponse) oResponse;
		
		if ( isDownload( request.getParameter( "download" ) ) ){
			if ( !uploader.isDisabled() ){
				String fileName = request.getParameter( "fileName" );
				if (netgest.utils.StringUtils.hasValue( fileName )){
					String decodedFilename = URLDecoder.decode( fileName , "UTF-8" );
					iFile file = uploader.getFile( decodedFilename );
					sendFileToUser( file , response );
				}
			}
		} else {
			
			File tmpDir = new File(getTempDir());
			ReceiveFile file = new ReceiveFile(tmpDir, tmpDir);
			File uploadedile = file.process( request , response , request.getSession().getServletContext() );
			
			//Prevent uploads when component is disabled or hidden
			if (!uploader.isDisabled() && uploader.isVisible()){
				//Create the uploaded file
				UploadValidation validator = uploader.getValidationUpload();
				if (validator != null){
					//Call validation on Files
					UploadedFile uploadedByUser = new UploadedFile( uploadedile.getName()	, uploadedile );
					AttachedFileValidation validation = validator.validate( uploadedByUser );
					if (validation.isValid()){
						setFileInComponent( uploader , uploadedile );
						writeSucessResponse( response.getWriter() );
						
					} else {
						writeFailureResponse(response.getWriter(), validation.getErrorMessage() );
					}
				} else {
					setFileInComponent( uploader , uploadedile );
					writeSucessResponse( response.getWriter() );
				}
			} else //Trick the component with a success response?
				writeFailureResponse( response.getWriter(), "Upload disabled" );
			
		}
		
		
	}
	
	protected void writeSucessResponse(PrintWriter writer){
		writer.print("{\"success\": true}");
	}
	
	protected void writeFailureResponse(PrintWriter writer, String failureReason){
		writer.print("{\"error\": \"" + failureReason + "\"}");
	}

	protected void setFileInComponent(FileUpload uploader, File uploadedile) {
		uploader.addFile( new FSiFile( uploadedile ) );
		uploader.setValue( uploadedile );
		uploader.updateModel();
	}
	
	@Override
	public StateChanged wasStateChanged(XUIComponentBase component,
			List< XUIBaseProperty< ? >> updateProperties) {
		updateProperties.add( component.getStateProperty( "visible" ) );
		updateProperties.add( component.getStateProperty( "disabled" ) );
		updateProperties.add( component.getStateProperty( "readOnly" ) );
		return super.wasStateChanged( component , updateProperties );
	}

	public void sendFileToUser(iFile file, HttpServletResponse resp) {
		
		if (file != null ){
			try{
				String sName = file.getName();
				ServletContext oCtx = (ServletContext)getFacesContext().getExternalContext().getContext();
				String mimetype = oCtx.getMimeType(sName.toLowerCase());
		
		        resp.setHeader("Cache-Control","private");               
		        resp.setHeader("Content-Disposition","attachment; filename="+sName);
		        OutputStream so = resp.getOutputStream(); 
		
		        resp.setContentType(mimetype);
		    	
		        int rb=0; 
		        InputStream is= null;
		        try { 
		            is = file.getInputStream();
		            byte[] a=new byte[4*1024];
		            while ((rb=is.read(a)) > 0) { 
		                so.write(a,0,rb); 
		            } 
		            is.close();
		        } 
		        catch (Exception e) 
		        {
		        	e.printStackTrace();
		        }
		        finally
		        {
		            if( is != null ) 
		            	is.close();
		        }
		        so.close();
				//Send the file to user
			}
	        catch (IOException e ){
	        	logger.warn( "Error Reading File %s" , e, file.getName() );
	        }
		}
		
	}

	public boolean isDownload(String uploadParameter){
		return "download".equalsIgnoreCase( uploadParameter );
				
	}
	
	protected String getTempDir(){
		return Ebo_DocumentImpl.getTempDir();
	}
	
}
