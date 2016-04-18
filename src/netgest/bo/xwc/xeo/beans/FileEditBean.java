package netgest.bo.xwc.xeo.beans;

import java.io.File;

import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.AttributeFile;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.io.FSiFile;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.utils.FileUtils;
import netgest.utils.TempFile;

import org.apache.commons.lang.StringUtils;

public class FileEditBean extends netgest.bo.xwc.xeo.beans.XEOBaseBean {
	private String parentComponentId;
	private String filename;

	public String getParentComponentId() {
		return parentComponentId;
	}

	public void setParentComponentId(String parentComponentId) {
		this.parentComponentId = parentComponentId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean validate() {
		return FileUtils.isFilenameValid(filename);
	}

	public void confirm() throws boRuntimeException {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();

		if (validate()) {
			XUIViewRoot parentView = getParentView();
			oRequestContext.setViewRoot(parentView);

			AttributeFile oParentInput = (AttributeFile) parentView.findComponent(parentComponentId);

			DataFieldConnector oConnector = oParentInput.getDataFieldConnector();

			if (oConnector instanceof XEOObjectAttributeConnector) {
				changeFilename(((XEOObjectAttributeConnector) oConnector).getAttributeHandler());
			}

			oParentInput.processUpdate();

			XUIForm oParentForm = (XUIForm) parentView.findComponent(XUIForm.class);

			if (oParentForm != null) {
				oRequestContext.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "attachSyncParentView", "Ext.onReady( function() { " + "window.parent.setTimeout(\"XVW.syncView('" + oParentForm.getClientId() + "')\",0)" + "});\n");
			}

			oRequestContext.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "_closeAttachWindow", "Ext.onReady( function() { " + "window.parent.setTimeout(\"XVW.lookupWindow.close()\",0)" + "});\n");
			oRequestContext.setViewRoot(oRequestContext.getSessionContext().createView(SystemViewer.DUMMY_VIEWER));
			oRequestContext.renderResponse();
		} else {
			oRequestContext.addMessage("invalidFilename", new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_INFO, BeansMessages.FILE_BROWSE_INVALID_FILENAME_TITLE.toString(), BeansMessages.FILE_BROWSE_INVALID_FILENAME.toString()));
		}
	}

	private void changeFilename(AttributeHandler attributeHandler) throws boRuntimeException {
		EboContext ctx = getEboContext();
		boolean commit = false;

		try {
			iFile file = attributeHandler.getValueiFile();

			try {
				if (file != null && file.isDirectory() && file.listFiles() != null && file.listFiles().length > 0) {
					file = file.listFiles()[0];
				}
			} catch (RuntimeException e1) {
			} catch (iFilePermissionDenied e1) {
			}

			if (file != null) {
				String fileStr = getNewFileStringAttribute(attributeHandler.getValueString(), filename);

				if (fileStr != null && !fileStr.isEmpty()) {
					attributeHandler.setValueString(fileStr);

					File tempFile = TempFile.createTempFile(filename);
					file.renameTo(new FSiFile(null, tempFile, null));

					commit = true;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (commit) {
				ctx.commitContainerTransaction();
			} else {
				ctx.rollbackContainerTransaction();
			}
		}
	}

	private String getNewFileStringAttribute(String filepath, String newFilename) {
		String result = null;

		if (FileUtils.isPathAnIfile(filepath) && newFilename != null && !newFilename.isEmpty()) {
			String separator = "/";
			String[] filepathSplit = filepath.split(separator);

			if (filepathSplit != null && filepathSplit.length > 1) {
				filepathSplit[filepathSplit.length - 1] = newFilename;

				result = StringUtils.join(filepathSplit, separator);
			}
		}

		return result;
	}

	public void cancel() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		oRequestContext.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "_closeAttachWindow", "Ext.onReady( function() { " + "window.parent.setTimeout(\"XVW.lookupWindow.close()\",0)" + "});\n");
		oRequestContext.setViewRoot(oRequestContext.getSessionContext().createView(SystemViewer.DUMMY_VIEWER));
		oRequestContext.renderResponse();
	}
}