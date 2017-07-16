package netgest.bo.xwc.components.classic.renderers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.boConfig;
import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.io.iFile;

/**
 * Column Renderer to create links to download a file to display in the GridPanel
 * 
 */
public class FileDownloadRenderer implements GridColumnRenderer {
	
	private String attName = null;
	
	public FileDownloadRenderer(String attName) {
		this.attName = attName;
	}
	
	@Override
	public String render(GridPanel grid, DataRecordConnector record, DataFieldConnector field) {
		String sRetValue = field.getDisplayValue();

		try {
			if (field instanceof XEOObjectAttributeConnector) {
				XEOObjectAttributeConnector connector = (XEOObjectAttributeConnector) field;
				AttributeHandler oAttHandler = connector.getAttributeHandler();

				//if (oAttHandler != null && oAttHandler.getDefAttribute().getAtributeDeclaredType() == boDefAttribute.ATTRIBUTE_BINARYDATA) {
					sRetValue = createDownloadLink(grid, oAttHandler);
		//		}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (sRetValue == null) {
			sRetValue = "";
		}

		return sRetValue;
	}

	/**
	 * 
	 * Generates a cardIdLink for a single object
	 * 
	 * @param objectToDisplayCardId
	 *            The object to cardIdlink-'ify'
	 * 
	 * @return A String with the inovcation
	 * 
	 * @throws boRuntimeException
	 * @throws UnsupportedEncodingException
	 */
	private String createDownloadLink(GridPanel grid, AttributeHandler oAttHandler) {
		String result = "";

		if (grid != null && oAttHandler != null) {
			try {
				boObject obj = oAttHandler.getParent();
				AttributeHandler binAttHandler = obj.getAttribute(this.attName);
				if (obj != null) {
					String objName = obj.getName();
					String objBoui = String.valueOf(obj.getBoui());
					String attName = binAttHandler.getName();
					iFile file = binAttHandler.getValueiFile();

					if (file != null) {						
						String name =oAttHandler.getValueString();
						String filename=file.getName();
						String fileid = file.getId();

						if (file.exists()) {
							HttpServletRequest req = (HttpServletRequest) grid.getRequestContext().getFacesContext().getExternalContext().getRequest();

							String fileUrl = boConfig.getApplicationConfig().getWebContextRoot() + "/file/" + objName + "/" + objBoui + "/" + attName + "/" + URLEncoder.encode(filename, "UTF-8") + "/" + fileid;
							String link = (req.isSecure() ? "https" : "http") + "://" + req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + fileUrl;
							String downloadScript = "XVW.downloadFile('" + link + "');";
							result = "<a class=\"gridColumnFile\" onclick=\"" + downloadScript + "\">" + name + "</a>";
						} else {
							result = name;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	public Object clone() {
		throw new RuntimeException("Cannot clone FileDownloadRenderer");
	}
}