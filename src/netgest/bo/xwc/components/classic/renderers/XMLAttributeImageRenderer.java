package netgest.bo.xwc.components.classic.renderers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.AttributeImage;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;

public class XMLAttributeImageRenderer extends XMLBasicRenderer {
	
	
	/* (non-Javadoc)
	 * @see netgest.bo.xwc.framework.XUIRenderer#encodeBegin(netgest.bo.xwc.framework.components.XUIComponentBase)
	 */
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		super.encodeBegin(component);
		
		XUIResponseWriter rw = getResponseWriter();
		
		String sViewState = getRequestContext().getViewRoot().getViewState();
        //xvw.servlet
        String sServletId = component.getClientId();
        
        String sActionUrl = getRequestContext().getAjaxURL();
        
        if( sActionUrl.indexOf('?') != -1 ) {
        	sActionUrl += "&";
        }
        else {
        	sActionUrl += "?";
        }
        sActionUrl += "javax.faces.ViewState=" + sViewState;
        sActionUrl += "&xvw.servlet=" + sServletId;
        
        //Write the image to disk
        
        String tmpFolder = netgest.bo.impl.document.Ebo_DocumentImpl.getTempDir();
        String imgFolder = "images";
		if(tmpFolder.endsWith("\\") || tmpFolder.endsWith("/"))
			tmpFolder =  tmpFolder + imgFolder + File.separator;
		else
			tmpFolder =  tmpFolder + File.separator + imgFolder;
		
		java.io.File tmpdir = new java.io.File(tmpFolder);
		if(!tmpdir.exists()) 
			tmpdir.mkdirs();
		
		String id = component.getClientId();
		id = id.replace(':','_');
		
		AttributeImage imgComp = (AttributeImage) component;
		DataFieldConnector oConnector = imgComp.getDataFieldConnector();
		String fileUrl = null;
		if( oConnector instanceof XEOObjectAttributeConnector ) {
    		XEOObjectAttributeConnector oXeoConnector = (XEOObjectAttributeConnector)oConnector;
    		try {
				iFile file = oXeoConnector.getAttributeHandler().getValueiFile();
    			if (file != null){
    				File tempFile = new File( tmpdir +File.separator + "image" + file.getName());
    				if (!tempFile.exists()){
    						
    					FileOutputStream fos = new FileOutputStream(tempFile);
    					byte[] buff = new byte[4096];
    					InputStream io = file.getInputStream();
    					while (io.read(buff) > 0){
    						fos.write(buff);
    					}
    					fos.close();
    					io.close();
    					fileUrl = tempFile.getAbsolutePath();
    				}
    				else
    					fileUrl = tempFile.getAbsolutePath();
    			}
    			
			}
    		catch (boRuntimeException e){
    			e.printStackTrace();
    		} catch (iFilePermissionDenied e) {
				e.printStackTrace();
			} 
		
		}
		
		rw.writeAttribute("url", sActionUrl, "url");
        if (fileUrl != null)
        	rw.writeAttribute("urlPdf", fileUrl, "urlPdf");
		
	}

	/* (non-Javadoc)
	 * @see netgest.bo.xwc.framework.XUIRenderer#encodeEnd(netgest.bo.xwc.framework.components.XUIComponentBase)
	 */
	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		super.encodeEnd(component);
	}

}
