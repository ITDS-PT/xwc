package netgest.bo.xwc.components.beans;

import netgest.bo.xwc.components.classic.HTMLFileBrowse;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIInput;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class FileBrowseBean extends XEOBase {

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
		
		XEOBaseBean     oParentBean = getParentBean();
		
		XUIInput oParentInput = 
			(XUIInput)oParentBean.getViewRoot().findComponent( parentComponentId );
		
		oRequestContext = oFileComp.getRequestContext();

		oRequestContext.setViewRoot( oParentBean.getViewRoot() );

		oParentInput.setValue( oFileComp.getSubmitedFile() );
		oParentInput.processUpdate();

        XUIViewRoot oParentView = oParentBean.getViewRoot();
        XUIForm		oParentForm = (XUIForm)oParentView.findComponent( XUIForm.class );
        if( oParentForm != null ) {
	        oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, "attachSyncParentView", 
	                "Ext.onReady( function() { " +
	                "window.parent.setTimeout(\"XVW.syncView('" + oParentForm.getId() + "')\",0)" +
	                "});\n"
	        );
        }

        oRequestContext.getScriptContext().add( XUIScriptContext.POSITION_FOOTER, "_closeAttachWindow", 
                "Ext.onReady( function() { " +
                "window.parent.setTimeout(\"XVW.lookupWindow.close()\",0)" +
                "});\n"
        );
		
		oRequestContext.setViewRoot(  
			oRequestContext.getSessionContext().createView( "dummy.xvw" )
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
				oRequestContext.getSessionContext().createView( "dummy.xvw" )
			);
			
			oRequestContext.renderResponse();

	}

    public XEOBaseBean getParentBean()
    {
        return (XEOBaseBean)getParentView().getBean( parentParentBeanId );
    }

}
