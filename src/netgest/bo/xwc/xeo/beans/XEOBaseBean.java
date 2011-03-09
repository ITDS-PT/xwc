package netgest.bo.xwc.xeo.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boPoolOwner;
import netgest.bo.xwc.components.classic.GridExplorer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeMetaData;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.framework.XUIActionEvent;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.XUIViewBean;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIViewRoot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class XEOBaseBean extends XEOSecurityBaseBean implements boPoolOwner, XUIViewBean {

	private String sViewStateId;
    private String sParentBeanId;
	private String sTitle;
	
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final byte	  USER_ROLES = 1;
    private static final byte	  USER_WORKQUEUES = 2;
    private static final byte	  USER_GROUPS = 3;
    private static final byte	  USER_PROFILES = 4;
	
	private XEOViewerResolver viewerResolver = new XEOViewerResolver();
	
	public XEOViewerResolver getViewerResolver() {
		return viewerResolver;
	}
	
	public XEOBaseBean() {
        super( boApplication.currentContext().getEboContext() );
    }
	
    @Override
	public EboContext getEboContext() {
    	return boApplication.currentContext().getEboContext();
	}

	@Override
	public void setEboContext(EboContext boctx) {
	}



	@Override
    public void poolObjectActivate() {
    }

    public XUIViewRoot getParentView()
    {
        XUIRequestContext oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
        return oRequestContext.getViewRoot().getParentView();
    }
    
    public XUIViewRoot getViewRoot() {
		XUIRequestContext oRequestContext;
		
		oRequestContext = XUIRequestContext.getCurrentContext();
		
		if( this.sViewStateId.equals( oRequestContext.getViewRoot().getViewState() ) )
			return oRequestContext.getViewRoot();
		return XUIRequestContext.getCurrentContext().getSessionContext().getView( sViewStateId );
	}

	public void setViewRoot( String viewRootStateId ) {
		this.sViewStateId = viewRootStateId;
	}
	
	@Override
    public void poolObjectPassivate() {
    }

	public void lookupFilterObject() {
    	
    	XUIRequestContext oRequestContext;
    	XUISessionContext oSessionContext;
    	XUIViewRoot		  oViewRoot;
    	
    	
    	oRequestContext = XUIRequestContext.getCurrentContext();
    	oSessionContext = oRequestContext.getSessionContext();
    	
    	XUIActionEvent e = oRequestContext.getEvent();

    	XUICommand cmd = (XUICommand)e.getComponent();
    	String column = (String)cmd.getValue();
    	
    	GridPanel  oGridPanel = (GridPanel)cmd.getParent();
    	
        Column     oLookupColumn = oGridPanel.getColumn( column );
        
        String     sLookupViewer = oLookupColumn.getLookupViewer();
        
        
    	XEOObjectAttributeMetaData oFieldMeta = (XEOObjectAttributeMetaData)oGridPanel.getDataSource().
    		getAttributeMetaData( column );
    	
    	boDefAttribute oAttDef = oFieldMeta.getBoDefAttribute();
    	
        // Verifica o modo de edi��o do Objecto... se for orf�o
        // abre o edit para associar um novo
        XEOBaseLookupList   oBaseBean;
        
        if( sLookupViewer == null || sLookupViewer.length() == 0 ) {
            sLookupViewer =
            	getViewerResolver().getViewer( oAttDef.getReferencedObjectName(), XEOViewerResolver.ViewerType.LOOKUP );
        }
        
        oViewRoot = oSessionContext.createChildView( sLookupViewer );
        oBaseBean = (XEOBaseLookupList)oViewRoot.getBean( "viewBean" );
        
        oBaseBean.setAttribute( "lookupColumn" , column);
        
        Map<String, String> lookupObjs = getLookupObjectsMap( oAttDef );
        
        oBaseBean.setParentParentBeanId( "viewBean" );
        oBaseBean.setParentComponentId( cmd.getClientId() );
        oBaseBean.setMultiLookup( true );
        oBaseBean.setFilterLookup( true );
        oBaseBean.setLookupObjects( lookupObjs );
        oBaseBean.setParentParentBeanId( "viewBean" );
        oBaseBean.setParentAttributeName( column );
    	oBaseBean.executeBoql( "select " + lookupObjs.keySet().iterator().next() );

        // Diz a que a view corrente � a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        oRequestContext.renderResponse();
    }

	public String getLookupQuery( String attribute, String selectObject ) {
    	return "select " + selectObject;
	}
	
    public Map<String, String> getLookupObjectsMap( boDefAttribute defAtt ) {
    	Map<String, String> ret = new LinkedHashMap<String, String>(1);
    	boDefHandler[] relObjs = defAtt.getObjects();
    	if( relObjs != null && relObjs.length > 0 ) {
    		for( boDefHandler relObjDef : relObjs ) {
    			ret.put( relObjDef.getName(), relObjDef.getLabel() );
    		}
    	}
    	else {
    		boDefHandler relDefAtt = defAtt.getReferencedObjectDef();
    		if( relDefAtt != null && relDefAtt.getClassType() == boDefHandler.TYPE_INTERFACE ) {
    			String[] interfaceObjects = ((boDefInterface)relDefAtt).getImplObjects();
    			for( String interfaceObject : interfaceObjects ) {
    				boDefHandler intObj = boDefHandler.getBoDefinition( interfaceObject );
    				if( intObj != null ) {
    					ret.put( relDefAtt.getName(), relDefAtt.getLabel() );
    				}
    			}
			} else {
				ret.put( relDefAtt.getName(), relDefAtt.getLabel() );
			}
    	}
    	return ret;
    }
	
	
	public void setLookupFilterResults( XEOBaseLookupList lookupBean, DataRecordConnector[] selectedRows ) {
    	XUIViewRoot oRoot = this.getViewRoot();
    	XUICommand oCmd     = (XUICommand)oRoot.findComponent( ":" + lookupBean.getParentComponentId() );
    	GridPanel gridPanel = (GridPanel)oCmd.getParent();
    	
    	Collection<String> oRowsColl = new ArrayList<String>();
    	
    	for( int i=0; i < selectedRows.length; i++ ) {
    		oRowsColl.add( String.valueOf( selectedRows[i].getAttribute( "BOUI" ).getValue() ) );
    	}
    	
    	try {
    		String column = (String)lookupBean.getAttribute( "lookupColumn" );
    		JSONArray  values = new JSONArray ( oRowsColl ); 
    		
    		JSONObject jObj = new JSONObject( gridPanel.getCurrentFilters() );
    		jObj.getJSONObject( column ).put("active", oRowsColl.size() > 0?true:false );
    		jObj.getJSONObject( column ).put("value", values );
	    	gridPanel.setCurrentFilters( jObj.toString() );
			
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
    
		XUIRequestContext.getCurrentContext().setViewRoot( oRoot );
		oRoot.processInitComponents(); 

	}
	
	public boolean getIsChanged() {
		return false;
	}
    
	public void setTitle( String sTitle ) {
		this.sTitle = sTitle;
	}
	
	public String getTitle() {
		return this.sTitle;
	}

    public String[] getUserRoles() {
    	return getUserBridge( USER_ROLES );
    }

    public String[] getUserGroups() {
    	return getUserBridge( USER_GROUPS );
    }

    public String[] getUserWorkqueues() {
    	return getUserBridge( USER_WORKQUEUES );
    }

    public String[] getUserProfiles() {
    	return getUserBridge( USER_WORKQUEUES );
    }
    
    public boolean getIsAdministrator() {
    	return "SYSUSER".equals( getEboContext().getSysUser().getUserName() );
    }

    private String[] getUserBridge( byte type ) {
		try {
			String ret[] = null; //cacheUserBriges.get( type );
			if( ret== null ) {
				if( boApplication.currentContext() != null ) {
			    	EboContext ctx = boApplication.currentContext().getEboContext();
			    	if( ctx != null ) {
			    		
			    		boObject userObj = boObject.getBoManager().loadObject( ctx, ctx.getSysUser().getBoui() );
			    		
			    		switch( type ) {
			    			case USER_ROLES: 
			    	    		ret = decodeBridgeBouis( userObj, "Ebo_Role", "roles" );
			    	    		break;
			    			case USER_WORKQUEUES: 
			    	    		ret = decodeBridgeBouis( userObj, "workQueue", "queues" );
			    	    		break;
			    			case USER_GROUPS: 
			    	    		ret = decodeBridgeBouis( userObj, "Ebo_Group", "groups" );
			    	    		break;
			    			case USER_PROFILES: 
			    	    		ret = decodeBridgeBouis( userObj, "Ebo_Group", "iProfile" );
			    	    		break;
			    		}
			    		if( ret == null ) {
			    			ret = EMPTY_STRING_ARRAY;
			    		}
			    		//cacheUserBriges.put(  type,  ret );
			    	}
				}
			}
			return ret;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
    }
    
    private String[] decodeBridgeBouis( boObject userObj, String objectName, String bridgeName ) throws boRuntimeException  {
    	//StringBuilder sb = new StringBuilder( "select id from " );
    	//sb.append( objectName ).append( " WHERE BOUI in (" );
    	boBridgeIterator it = userObj.getBridge( bridgeName ).iterator();
    	ArrayList<String> t = new ArrayList<String>( it.getBridgeHandler().getRowCount() );
    	while( it.next() ) {
    		t.add(
				it.currentRow().getObject().getAttribute("id").getValueString()
    		);
    	}
    	return (String[])t.toArray( new String[ t.size() ] );
    }

	public String getParentBeanId() {
		return sParentBeanId;
	}

	public void setParentBeanId(String parentBeanId) {
		this.sParentBeanId = parentBeanId;
	}
    
	public XUIRequestContext getRequestContext() {
		return XUIRequestContext.getCurrentContext();
	}
	
	public XUISessionContext getSessionContext() {
		return getRequestContext().getSessionContext();
	}
	
	public void previewObject() {
		XUIRequestContext oRequestContext;
		XUISessionContext oSessionContext;
		
		oRequestContext = XUIRequestContext.getCurrentContext();
		oSessionContext = oRequestContext.getSessionContext();
		
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		GridExplorer gridExplorer = (GridExplorer)viewRoot.findComponent( GridExplorer.class );
		
		DataRecordConnector drc = gridExplorer.getActiveRow();
		
		if( drc != null ) {
			try {
				// Calculate the correct viewer
				long boui =((BigDecimal)drc.getAttribute("BOUI").getValue()).longValue();
				String objectName = boObject.getBoManager().getClassNameFromBOUI( getEboContext(), boui );
				String sViewerName = getViewerResolver().getViewer( objectName , XEOViewerResolver.ViewerType.EDIT );
				XUIViewRoot newViewRoot = oSessionContext.createView( sViewerName );
				((XEOEditBean)newViewRoot.getBean("viewBean")).setCurrentObjectKey( Long.toString( boui ) );
				
				if( gridExplorer.getPreviewPanelMode() == GridExplorer.PreviewPanelMode.PREVIEW ) {
					newViewRoot.setRenderKitId("XEOXML");
					// After the request discard the view!
					newViewRoot.setTransient( true );
				}
				else {
					((XEOEditBean)newViewRoot.getBean("viewBean")).addEventListener(
								GridExplorer.XEOEDITBEAN
							);
				}
				oRequestContext.setViewRoot( newViewRoot );
			} catch (boRuntimeException e) {
				throw new RuntimeException( e );
			}
		}
	}
	
	
	
	/**
	 * Opens an edit viewer for the XVW.openEditViewer javascript function
	 * 
	 *  
	 */
	public void openEditViewer(){
	
		XUISessionContext oSessionContext = getRequestContext().getSessionContext();
		XUIForm f = (XUIForm)getViewRoot().findComponent(XUIForm.class);
		Map<String,String> params = getRequestContext().getRequestParameterMap();
		
		//Retrieve the viewername, boui and object name from the request input (hidden) fields
		String viewName = params.get(f.getClientId() + "_viewerName");
		String boui = params.get(f.getClientId() + "_boui");
		String objectName = params.get(f.getClientId() + "_objectName");
		
		if (viewName != null){
			
			//Create the viewer
			XUIViewRoot oViewRoot = oSessionContext.createChildView(viewName);
			
			XEOEditBean oEditBean = (XEOEditBean) oViewRoot.getBean("viewBean");
			
			//If a BOUI is present, open the edit for that instance, otherwise create a new one
			if (boui != null && boui.length() > 0 )
				oEditBean.setCurrentObjectKey(boui);
			else
				oEditBean.createNew(objectName);
			
			//Send the response
			getRequestContext().setViewRoot(oViewRoot);
			getRequestContext().renderResponse();
		}	
	}
	
	/**
	 * Opens a list viewer for the XVW.openListViewer javascript function
	 */
	public void openListViewer(){
		XUISessionContext oSessionContext = getRequestContext().getSessionContext();
		XUIForm f = (XUIForm)getViewRoot().findComponent(XUIForm.class);
		Map<String,String> params = getRequestContext().getRequestParameterMap();
		
		//Retrieve the viewername, boui and object name from the request input (hidden) fields
		String viewName = params.get(f.getClientId() + "_viewerListName");
		String boql = params.get(f.getClientId() + "_boql");
		
		if (viewName != null){
			
			//Create the viewer
			XUIViewRoot oViewRoot = oSessionContext.createChildView(viewName);
			
			XEOBaseList oListBean = (XEOBaseList) oViewRoot.getBean("viewBean");
			if (boql != null && boql.length() > 0 )
			{
				oListBean.executeBoql(boql);
				
				//Send the response
				getRequestContext().setViewRoot(oViewRoot);
				getRequestContext().renderResponse();
			}
		}
	}
	
}

