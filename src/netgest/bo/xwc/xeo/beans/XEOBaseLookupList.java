package netgest.bo.xwc.xeo.beans;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.annotations.Visible;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.framework.XUIActionEvent;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.def.XUIViewerDefinition;
import netgest.bo.xwc.framework.def.XUIViewerDefinitionNode;
import netgest.bo.xwc.framework.def.XUIViewerDefinitonParser;
import netgest.bo.xwc.xeo.components.ColumnAttribute;
import netgest.bo.xwc.xeo.localization.BeansMessages;


public class XEOBaseLookupList extends XEOBaseList {
    
    private String      sParentParentBeanId;
    private String      sParentComponentId;
    private boolean     bMultiLookup;
    private boolean     bFilterLookup;
    private XEOEditBean	parentBean;
    private String		parentAttribute;
    
    Map<String,Object>	    	attributes;

    Map<String, String>			lookupObjectsMap;
    private String				selectedObject;
    
    public String getSelectedObject() {
		return selectedObject;
	}
    
    public void selectObject() {
    	//  Dummy
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
    @Visible
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
    @Visible
    public void processUpdate() {
    	update();
    }
    @Visible
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
	                ((XEOEditBean)oParentBean).setLookupBridgeResults( this, oSelectedRows );
	            }
	            else {
	            	((XEOEditBean)oParentBean).setLookupAttributeResults( this, oSelectedRows );    
	            }
            }
            else if ( oParentBean instanceof XEOBaseBean ) {
            	((XEOBaseBean)oParentBean).setLookupFilterResults( this, oSelectedRows );
            }
        }
    }
    @Visible
    public void processCancel() throws boRuntimeException
    {
    	cancel();
    }

    public void cancel()
    {
    	//TODO:
    }
    
    /**
     * 
     * Whether the Lookup is for a specific object of for a list of objects
     * 
     * @return True if the lookup is for a relation with boObject and false otherwise
     */
    public boolean getBoObjectLookup(){
    	return (lookupObjectsMap != null && lookupObjectsMap.size()  > 1);
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

    public void setParentBean( XEOEditBean parentBean ) {
    	this.parentBean = parentBean;
    }
    
    public XEOBaseBean getParentBean() {
    	if( this.parentBean == null )
    		return (XEOBaseBean)getParentView().getBean( sParentParentBeanId );
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
    @Visible
	public void canCloseTab() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.renderResponse();
	}
	
	@Override
	@Visible
	public void addNew() throws Exception {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot;
		XUIViewRoot parentViewRoot;
		
		viewRoot = oRequestContext.getViewRoot();
		parentViewRoot = getParentView();
		
		super.addNew();
			
		// View root changes... close this window!
		if( viewRoot != oRequestContext.getViewRoot() ) {
			((Window)viewRoot.findComponent(Window.class)).close();
			XUIViewRoot oViewRoot = oRequestContext.getViewRoot();
			XEOEditBean baseBean = (XEOEditBean)oViewRoot.getBean("viewBean");
			baseBean.setParentBean( (XEOEditBean)getParentBean() );
			baseBean.setParentComponentId( getParentComponentId() );
			oViewRoot.setParentView( parentViewRoot );
		}
	}
	
	public XUIComponentPlugIn getAttributesColPlugIn() {
		return new AttributesColPlugIn();
	}

	/**
	 * 
	 * Column Plugin that when using a boObject Lookup sets the columns of the list to the
	 * columns of the Lookup viewer for that
	 *
	 */
	private class AttributesColPlugIn extends XUIComponentPlugIn {

		@Override
		public void beforePreRender() {
			
				((XUIComponentBase)getComponent().getParent()).forceRenderOnClient();
				getComponent().getChildren().clear();
				if(selectedObject != null && !"".equalsIgnoreCase(selectedObject)){
					
					XUIViewerDefinitonParser parser = new XUIViewerDefinitonParser();
					
					InputStream is = parser.resolveViewer(selectedObject+"/lookup.xvw");
					if (is == null)
						is = parser.resolveViewer(selectedObject+"_lookup.xvw");
					
					XUIViewerDefinition viewer = parser.parse(is);
			
					List<XUIViewerDefinitionNode> cols = new FindLookupListCols(viewer.getRootComponent().
							getChildren()).getColns();
					for (int i = 0, n = cols.size(); i < n; i++){
						ColumnAttribute ca = new ColumnAttribute();
						XUIViewerDefinitionNode node = cols.get(i);
						ca.setDataField(node.getProperty("dataField"));
						ca.setWidth("100");
						if (node.getProperty("hidden") != null){
							ca.setHidden(node.getProperty("hidden"));
						}
						getComponent().getChildren().add(ca);
					}
			}
		}
	

	}
	
	/**
	 * 
	 * Finds the xvw:columns Components children in a viewer
	 *
	 */
	private class FindLookupListCols{
		
		private List<XUIViewerDefinitionNode> rootElements;
		
		public FindLookupListCols(List<XUIViewerDefinitionNode> node){
			this.rootElements = node;
		}
		
		public List<XUIViewerDefinitionNode> getColns(){
			return recursiveSearch(rootElements);
		}
		
		
		private List<XUIViewerDefinitionNode> recursiveSearch(List<XUIViewerDefinitionNode> nodeList){
			List<XUIViewerDefinitionNode> result = null;
			for (int i = 0, k = nodeList.size(); i < k ; i++){
				XUIViewerDefinitionNode curr = nodeList.get(i);
				if (curr.getName().equalsIgnoreCase("xvw:columns"))
					return curr.getChildren();
				else{
					result = recursiveSearch(curr.getChildren());
					if (result != null)
						return result;
				}
			}
			return null;
			
			
			
			
		}
	}
	
	
}
