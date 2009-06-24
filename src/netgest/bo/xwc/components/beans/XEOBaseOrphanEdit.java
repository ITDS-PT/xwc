package netgest.bo.xwc.components.beans;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XEOBaseOrphanEdit extends XEOBaseBean {

    private String      sParentParentBeanId;
    private String      sParentComponentId;
    private XEOBaseBean	parentBean;

    private boolean 	bTransactionStarted = false;
    
    public void confirm() throws boRuntimeException {
        
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
         
    	processValidate();
    	if( this.isValid() ) {
    		processUpdate();
	        // Get the window in the viewer and close it!
	        Window oWndComp 		= (Window)getViewRoot().findComponent( Window.class );
	        oWndComp.destroy();
	
	        // Trigger parent view sync with server
	        XUIViewRoot oParentViewRoot = getParentView();
	        oParentViewRoot.syncClientView();
	        
	        oRequestContext.setViewRoot( oRequestContext.getSessionContext().createChildView( "dummy.xvw" ) );
    	}

    }

    @Override
	public boObject getXEOObject() {
		try {
			boObject currentObject = super.getXEOObject();
			if( currentObject != null && !this.bTransactionStarted ) {
				currentObject.poolSetStateFull();
				currentObject.transactionBegins();
				bTransactionStarted = true;
				Window wnd = (Window)XUIRequestContext.getCurrentContext().getViewRoot().findComponent( Window.class );
				if( wnd != null && wnd.getOnClose() == null ) {
					wnd.setOnClose( "#{viewBean.cancel}" );
				}
			}
			return currentObject;
		} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	public void processUpdate() throws boRuntimeException
    {
    	update();
    }
    
    public void update() throws boRuntimeException
    {
		// Commit changes on object
        boObject currentObject = getXEOObject();
        currentObject.transactionEnds( true );
        this.bTransactionStarted = false;
        XEOBaseBean oParentBean = getParentBean();

        if( oParentBean != null )
        {
            oParentBean.setOrphanEdit( this );
        }
        
    }
    
    public void processCancel() throws boRuntimeException
    {
    	cancel();
    }

    public void cancel() throws boRuntimeException
    {
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
        
        // Rollback object changes
        boObject currentObject = getXEOObject();
        currentObject.transactionEnds( false );

        
        // Get the window in the viewer and destroy it!
        Window oWndComp 		= (Window)getViewRoot().findComponent( Window.class );
        oWndComp.destroy();

        // Trigger parent view sync with server
        //XUIViewRoot oParentViewRoot = getParentView();
        //oParentViewRoot.syncClientView();
        
        this.bTransactionStarted = false;
        
        oRequestContext.setViewRoot( oRequestContext.getSessionContext().createChildView( "dummy.xvw" ) );

    }

    public void setParentBeanId(String sParentParentBeanId) {
        this.sParentParentBeanId = sParentParentBeanId;
    }

    public String getParentParentBeanId() {
        return sParentParentBeanId;
    }

    public void setParentComponentId(String sParentComponentId) {
        this.sParentComponentId = sParentComponentId;
    }

    public String getParentComponentId() {
        return sParentComponentId;
    }

    public void setParentBean( XEOBaseBean parentBean ) {
    	this.parentBean = parentBean;
    }
    
    public XEOBaseBean getParentBean() {
    	if( this.parentBean == null )
    		return (XEOBaseBean)getParentView().getBean( sParentParentBeanId );
    	else
    		return this.parentBean;
    }

}
