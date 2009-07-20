package netgest.bo.xwc.components.beans;

import java.util.HashMap;
import java.util.Map;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.localization.BeansMessages;
import netgest.bo.xwc.framework.XUIActionEvent;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class XEOBaseLookupList extends XEOBaseList {
    
    private String      sParentParentBeanId;
    private String      sParentComponentId;
    private boolean     bMultiLookup;
    private boolean     bFilterLookup;
    private XEOBaseBean	parentBean;
    private String		parentAttribute;
    
    Map<String,Object>	    	attributes;

    Map<String, String>			lookupObjectsMap;
    private String				selectedObject;
    
    public String getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(String selectedObject) {
		if( !selectedObject.equals( this.selectedObject ) ) {
			this.selectedObject = selectedObject;
	    	executeBoql( getParentBean().getLookupQuery( this.parentAttribute, this.selectedObject ) );
	    	
	    	
		}
	}

    public Map<String, String> getLookupObjects() {
    	return this.lookupObjectsMap;
    }
    
    public void setLookupObjects( Map<String, String> objectsMap ) {
    	this.lookupObjectsMap = objectsMap;
    }

    public void setAttribute( String key, Object value ) {
    	if( attributes == null )
    		attributes = new HashMap<String,Object>();
    	
    	attributes.put( key , value);
    	
    }
    
    public Object getAttribute( String key ) {
    	if( attributes != null )
    		return attributes.get( key );
    	
    	return null;
    	
    }
    
    public boolean getRenderSelectecObjectRows() {
    	
    	if( getLookupObjects() != null )
    		return getLookupObjects().size() > 1;
    	
    	return false;
    	
    }
    
    public void setParentAttributeName( String sAttrId ) {
    	this.parentAttribute = sAttrId;
    }
    
    public String getParentAttributeName( String sAttrId ) {
    	return this.parentAttribute;
    }
    
    public void setFilterLookup( boolean isFilterLookup ) {
    	this.bFilterLookup = isFilterLookup;
    }
    
    public boolean isFilterLookup() {
    	return this.bFilterLookup;
    }
    
    public void confirm() {
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();

        XUIViewRoot oViewRoot = oRequestContext.getViewRoot();

        // Update select values
        processUpdate();
         
        // Get the window in the viewer and close it!
        XUIActionEvent oEvent = oRequestContext.getEvent();
        Window oWndComp = (Window)oEvent.getComponent().findParentComponent( Window.class );
        oWndComp.destroy();
        if( getParentView() != null )
        	getParentView().syncClientView();
        
        // Only set the dummy view if is the same
        if( oRequestContext.getViewRoot() == oViewRoot )
        	oRequestContext.setViewRoot( oRequestContext.getSessionContext().createChildView( "netgest/bo/xwc/components/viewers/Dummy.xvw" ) );

    }
    
    public void processUpdate() {
    	update();
    }

    public void update() {

    	XUIRequestContext oRequestContext;
    	oRequestContext = XUIRequestContext.getCurrentContext();
        Object oParentBean = getParentView().getBean( sParentParentBeanId );
        
        if( oParentBean != null )
        {
            GridPanel oGridComp;
            oGridComp = (GridPanel)oRequestContext.getViewRoot().findComponent( GridPanel.class );
            DataRecordConnector[] oSelectedRows = ((GridPanel)oGridComp).getSelectedRows();
    
            if( !isFilterLookup() )
            {
	            if( isMultiLookup() ) {
	                ((XEOBaseBean)oParentBean).setLookupBridgeResults( this, oSelectedRows );
	            }
	            else {
	            	((XEOBaseBean)oParentBean).setLookupAttributeResults( this, oSelectedRows );    
	            }
            }
            else if ( oParentBean instanceof XEOBase ) {
            	((XEOBase)oParentBean).setLookupFilterResults( this, oSelectedRows );
            }
        }
    }
    
    public void processCancel() throws boRuntimeException
    {
    	cancel();
    }

    public void cancel() throws boRuntimeException
    {
    	//TODO:
    }
    
    
    public String getRowSelectionMode() {
        return isMultiLookup()?GridPanel.SELECTION_MULTI_ROW:GridPanel.SELECTION_ROW;
    }

    public void setParentComponentId(String sParentComponentId) {
        this.sParentComponentId = sParentComponentId;
    }

    public String getParentComponentId() {
        return sParentComponentId;
    }

    public void setParentParentBeanId(String sParentParentBeanId) {
        this.sParentParentBeanId = sParentParentBeanId;
    }

    public String getParentParentBeanId() {
        return sParentParentBeanId;
    }

    public void setParentBean( XEOBaseBean parentBean ) {
    	this.parentBean = parentBean;
    }
    
    public XEOBase getParentBean() {
    	if( this.parentBean == null )
    		return (XEOBase)getParentView().getBean( sParentParentBeanId );
    	else
    		return this.parentBean;
    }
    
    public void setMultiLookup(boolean bMultiLookup) {
        this.bMultiLookup = bMultiLookup;
    }

    public boolean isMultiLookup() {
        return bMultiLookup;
    }

    public String getTitle() {
    	try {
			return BeansMessages.LIST_OF.toString() + " " + getCurrentObjectList().getBoDef().getLabel();
		} catch (boRuntimeException e) {
			// Ignore
		}
		return ""; 
    }

	public void canCloseTab() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.renderResponse();
	}
}
