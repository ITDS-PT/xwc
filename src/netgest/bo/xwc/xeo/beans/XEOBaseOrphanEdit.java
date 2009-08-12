package netgest.bo.xwc.xeo.beans;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XEOBaseOrphanEdit extends XEOBaseBean {

    private boolean 	bTransactionStarted = false;
    
    public void confirm() throws boRuntimeException {
        
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
         
    	processValidate(); 
    	if( this.isValid() ) {
    		processUpdate();
    		
	        // Get the window in the viewer and close it!
	        Window oWndComp 		= (Window)getViewRoot().findComponent( Window.class );
    		if( oWndComp != null ) {
    			oWndComp.destroy();
    		}
    		else {
        		XVWScripts.closeView( oRequestContext.getViewRoot() );
        		oRequestContext.getViewRoot().setRendered( false );
        		oRequestContext.renderResponse();
    		}
    		
	        // Trigger parent view sync with server
	        XUIViewRoot oParentViewRoot = getParentView();
	        if( oParentViewRoot != null ) {
		        oParentViewRoot.syncClientView();
	        }
	        
	        oRequestContext.setViewRoot( oRequestContext.getSessionContext().createChildView( "netgest/bo/xwc/components/viewers/Dummy.xvw" ) );
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

        this.bTransactionStarted = false;
        oRequestContext.setViewRoot( oRequestContext.getSessionContext().createChildView( "netgest/bo/xwc/components/viewers/Dummy.xvw" ) );

    }

}
