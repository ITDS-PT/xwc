package netgest.bo.xwc.xeo.beans;

import netgest.bo.xwc.components.classic.HTMLFileBrowse;
import netgest.bo.xwc.components.util.JavaScriptUtils;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIInput;
import netgest.bo.xwc.framework.components.XUIViewRoot;
public class FileBrowseBean extends netgest.bo.xwc.xeo.beans.XEOBaseBean {

	private String      parentParentBeanId;
    private String      parentComponentId;

	
	public String getParentParentBeanId() {
		return parentParentBeanId;
	}

	public void setParentBeanId(String parentParentBeanId) {
		this.parentParentBeanId = parentParentBeanId;
	}

	public String getParentComponentId() {
		return parentComponentId;
	}

	public void setParentComponentId(String parentComponentId) {
		this.parentComponentId = parentComponentId;
	}

	public void confirm() {
		
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		
		XUIViewRoot oViewRoot = oRequestContext.getViewRoot(); 
			
		HTMLFileBrowse 	oFileComp   = 
			(HTMLFileBrowse)oViewRoot.findComponent( HTMLFileBrowse.class );
		
		//Does not work as expexted we need to investigate why it's different
		//from getParentBean().getViewRoot()
		//XUIViewRoot parentView = getParentBean().getViewRoot();
		
		XUIViewRoot parentView = getParentView();
		
		XUIInput oParentInput = 
			(XUIInput)parentView.findComponent( parentComponentId );
		
		oRequestContext = oFileComp.getRequestContext();

		oRequestContext.setViewRoot( parentView );

		oParentInput.setValue( oFileComp.getSubmitedFile() );
		oParentInput.processUpdate();

        XUIForm		oParentForm = (XUIForm)parentView.findComponent( XUIForm.class );
        if( oParentForm != null ) {
	        oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, "attachSyncParentView", 
	                "Ext.onReady( function() { " +
	                "window.parent.setTimeout(\"XVW.syncView('" + oParentForm.getClientId() + "')\",0)" +
	                "});\n"
	        );
        }

        oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, "_closeAttachWindow", 
                "Ext.onReady( function() { " +
                "window.parent.setTimeout(\"XVW.lookupWindow.close()\",0)" +
                "});\n"
        );
        
        String id = oParentInput.getClientId();
        //If the file was indeed selected, add its name to the component in the previous viewer
        if (oFileComp.getSubmitedFile() != null){
        	oRequestContext.getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "_updateFile", 
	        		"Ext.onReady( function() { " +
	        		"window.parent.document.getElementById('" + id +"_ci').value='"+JavaScriptUtils.safeJavaScriptWrite( oFileComp.getSubmitedFile().getName() )+"';"+
	        		"});\n");
        }
		oRequestContext.setViewRoot(  
			oRequestContext.getSessionContext().createView( SystemViewer.DUMMY_VIEWER )
		);
		
		oRequestContext.renderResponse();
	}
	
	public void cancel() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
        oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, "_closeAttachWindow", 
                "Ext.onReady( function() { " +
                "window.parent.setTimeout(\"XVW.lookupWindow.close()\",0)" +
                "});\n"
        );
		oRequestContext.setViewRoot(  
				oRequestContext.getSessionContext().createView( SystemViewer.DUMMY_VIEWER )
			);
			
			oRequestContext.renderResponse();

	}

    public XEOEditBean getParentBean()
    {
        return (XEOEditBean)getParentView().getBean( parentParentBeanId );
    }

}
