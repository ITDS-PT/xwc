package netgest.bo.xwc.components.classic.renderers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import bsh.StringUtil;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.AttributeImage;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.jsf.utils.Base64;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.utils.StringUtils;


public class XMLAttributeImageRenderer extends XMLBasicRenderer {
	
	
	/* (non-Javadoc)
	 * @see netgest.bo.xwc.framework.XUIRenderer#encodeBegin(netgest.bo.xwc.framework.components.XUIComponentBase)
	 */
	@Override
	public void encodeBegin(XUIComponentBase component) throws IOException {
		super.encodeBegin(component);
		
		XUIResponseWriter rw = getResponseWriter();
		
		String imageBase64 = encodeImageBase64((AttributeImage) component);
        
        //Write the image to disk
		if (StringUtils.hasValue( imageBase64 ))
			rw.writeAttribute("url", "data:image/png;base64,"+imageBase64);
		else
			rw.writeAttribute("url", "");
		
        
		
	}

	private String encodeImageBase64(AttributeImage component) {

		DataFieldConnector oConnector = component.getDataFieldConnector();
		if( oConnector instanceof XEOObjectAttributeConnector ) {
			XEOObjectAttributeConnector oXeoConnector = (XEOObjectAttributeConnector)oConnector;
			try {
				iFile file = oXeoConnector.getAttributeHandler().getValueiFile();
				if (file != null){
					InputStream fin = file.getInputStream();
					byte fileContent[] = new byte[(int)file.length()];
					int offset = 0;

					while ( offset < fileContent.length ) {
						int count = fin.read(fileContent, offset, fileContent.length - offset);
						offset += count;
					}

					//all chars in encoded are guaranteed to be 7-bit ASCII
					String encodedFile = Base64.encodeBytes( fileContent );
					return encodedFile;
				}
			} catch (IOException e){
				logger.warn( e );
				//Do nothing
			} catch ( boRuntimeException e ) {
				logger.warn( e );
			} catch ( iFilePermissionDenied e ) {
				logger.warn( e );
			}
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see netgest.bo.xwc.framework.XUIRenderer#encodeEnd(netgest.bo.xwc.framework.components.XUIComponentBase)
	 */
	@Override
	public void encodeEnd(XUIComponentBase component) throws IOException {
		super.encodeEnd(component);
	}

}
