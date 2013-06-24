package netgest.bo.xwc.xeo.beans;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.security.securityRights;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boPoolOwner;
import netgest.bo.xwc.components.classic.GridExplorer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.classic.grid.GridDatesBetweenFilterBean;
import netgest.bo.xwc.components.classic.grid.GridPanelJSonFiltersBuilder;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterJoin;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeMetaData;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.framework.XUIActionEvent;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.XUIViewBean;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOViewerResolver.ViewerType;
import netgest.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class XEOBaseBean extends XEOSecurityBaseBean implements boPoolOwner, XUIViewBean {

	protected String sViewStateId;
    private String sParentBeanId;
	private String sTitle;
	private String id = "viewBean" ;
	
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final byte	  USER_ROLES = 1;
    private static final byte	  USER_WORKQUEUES = 2;
    private static final byte	  USER_GROUPS = 3;
    private static final byte	  USER_PROFILES = 4;
    
    private static Logger logger= Logger.getLogger( XEOBaseBean.class );
	
	private XEOViewerResolver viewerResolver = new XEOViewerResolver();
	
	public XEOViewerResolver getViewerResolver() {
		return viewerResolver;
	}
	
	public XEOBaseBean(EboContext ctx){
		super(ctx);
	}
	
	public XEOBaseBean() {
        super( boApplication.currentContext().getEboContext() );
    }
	
	public String getId(){
		return id;
	}
	
	public void setId( String newId ){
		this.id = newId;
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
        //XUIRequestContext oRequestContext;
        //oRequestContext = XUIRequestContext.getCurrentContext();
        return getViewRoot().getParentView();
    }
    
    public XUIViewRoot getViewRoot() {
		XUIRequestContext oRequestContext;
		
		oRequestContext = XUIRequestContext.getCurrentContext();
		
		//Check if view is the same as the context
		if( this.sViewStateId.equals( oRequestContext.getViewRoot().getViewState() ) )
			return oRequestContext.getViewRoot();
		//If not check recursively in that view 
		XUIViewRoot root = findRecursiveRoot( oRequestContext.getViewRoot() );
		if (root == null){
			//If the view was replaced in the context, try to load the view
			root = oRequestContext.getSessionContext( ).getView( sViewStateId );
		}
		return root;
		
		
	}
    
    private XUIViewRoot findRecursiveRoot(UIComponent component){
    	XUIViewRoot root = null;
    	Iterator<UIComponent> it = component.getFacetsAndChildren();
    	while (it.hasNext()){
    		UIComponent child = it.next();
    		if (child instanceof XUIViewRoot){
    			XUIViewRoot view = (XUIViewRoot) child;
    			if (sViewStateId.equals( view.getViewState( ))){
    				root = (XUIViewRoot)child;
    			} else {
    				root = findRecursiveRoot( child );
    				if (root != null)
    					return root;
    			}
    		} else {
    			root = findRecursiveRoot( child );
    			if (root != null)
    				return root;
    		}
    	}
    	return root;
    }

	public void setViewRoot( String viewRootStateId ) {
		this.sViewStateId = viewRootStateId;
	}
	
	@Override
    public void poolObjectPassivate() {
    }

	public void lookupFilterObject() {

        // Cria view
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

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
        
        if( sLookupViewer == null || sLookupViewer.length() == 0 ) {
	    	String className = oAttDef.getReferencedObjectName(); 
	    	if( "boObject".equals( oAttDef.getReferencedObjectName() ) ) {
	    		String[] objects = oAttDef.getObjectsName();
	    		if( objects != null && objects.length > 0 ) {
	    			className = objects[0];
	    		}
	    	}
			sLookupViewer = "viewers/" + className + "/lookup.xvw";
        }
		
        oViewRoot = oSessionContext.createChildView( sLookupViewer );

        XEOBaseLookupList   oBaseBean;
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
        String objectSelected = lookupObjs.keySet().iterator().next();
        oBaseBean.setSelectedObject( objectSelected );
    	oBaseBean.executeBoql( "select " + objectSelected );

        // Diz a que a view corrente � a criada.
        oRequestContext.setViewRoot( oViewRoot );
        
        oRequestContext.renderResponse();
		
		
        
/*
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
        */
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
    	return getUserBridge( USER_PROFILES );
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
				boObject xeoObject = boObject.getBoManager().loadObject( getEboContext(), boui );
				String sViewerName = getViewerResolver().getViewer( xeoObject, ViewerType.PREVIEW );
				XUIViewRoot newViewRoot = oSessionContext.createView( sViewerName );
				((XEOEditBean)newViewRoot.getBean("viewBean")).setCurrentObjectKey( Long.toString( boui ) );
				
				/*if( gridExplorer.getPreviewPanelMode() == GridExplorer.PreviewPanelMode.PREVIEW ) {
					newViewRoot.setRenderKitId("XEOXML");
					// After the request discard the view!
					newViewRoot.setTransient( true );
				}
				else {
					((XEOEditBean)newViewRoot.getBean("viewBean")).addEventListener(
								GridExplorer.XEOEDITBEAN
							);
				}*/
				oRequestContext.setViewRoot( newViewRoot );
				
				((HttpServletRequest)oRequestContext.getRequest())
					.setAttribute("xsltransform", "true");
				
				newViewRoot.setRenderKitId("XEOXML");
				newViewRoot.setTransient( true );
				oRequestContext.setViewRoot( newViewRoot );				
				//oRequestContext.setViewRoot( newViewRoot );
				// After the request discard the view!
				
			} catch (boRuntimeException e) {
				throw new RuntimeException( e );
			}
		}
	}
	
	/**
	 * 
	 * Open a URL in tab from the XVW.openURL javascript function
	 * 
	 * */
	public void openURLLink(){
		
		XUIForm f = (XUIForm)getViewRoot().findComponent(XUIForm.class);
		Map<String,String> params = getRequestContext().getRequestParameterMap();
		
		//Retrieve the URL
		String url = params.get(f.getClientId() + "_url");
		
		XUIRequestContext   oRequestContext;
        oRequestContext = XUIRequestContext.getCurrentContext();
    	try {
			((HttpServletResponse)oRequestContext.getResponse()).sendRedirect( url );
		} catch (IOException e) {
			logger.severe(e);
		}
    	oRequestContext.responseComplete();	
		
	}
	
	public void openCardIdLink(){
		
		XUIRequestContext   oRequestContext;
        XUIViewRoot			oViewRoot = null;
        XUISessionContext	oSessionContext;
        
        oRequestContext = getRequestContext();
        oSessionContext = oRequestContext.getSessionContext();

        ActionEvent oEvent = oRequestContext.getEvent();
        XUICommand oCommand = (XUICommand) oEvent.getComponent();
        
        //Retrieve the URL
		String bouiToOpen = oCommand.getCommandArgument().toString(); 
		if (bouiToOpen != null && !"".equalsIgnoreCase(bouiToOpen) ){
			try {
				boObject childObj = boObject.getBoManager().loadObject(
						getEboContext(), Long.valueOf(bouiToOpen));
				if (securityRights.canRead(getEboContext(), childObj.getName())) {
					boolean isOrphan = childObj.getBoDefinition().getBoCanBeOrphan();
					if (isOrphan){
						if (oRequestContext.isAjaxRequest()) {
							// Resubmit the to the command... 
							oCommand.setValue( bouiToOpen );
							oCommand.setCommandArgument( bouiToOpen );
							String frameId = "Frame_"+bouiToOpen;
							
							oRequestContext.getScriptContext().add(
									XUIScriptContext.POSITION_FOOTER,
									"cardIdLink_openTab",
									XVWScripts.getOpenCommandTab(frameId,oCommand, bouiToOpen , null ));
							
							oRequestContext.renderResponse();
						} else {
							boObject sObjectToOpen;
							try {
								sObjectToOpen = boObject
										.getBoManager()
										.loadObject(
												getEboContext(),
												Long
														.parseLong(bouiToOpen));
								oViewRoot = oSessionContext
										.createChildView(
								        		getViewerResolver().getViewer( sObjectToOpen, XEOViewerResolver.ViewerType.EDIT )
											);
								XEOEditBean oBaseBean = (XEOEditBean) oViewRoot
										.getBean("viewBean");
								oBaseBean.setCurrentObjectKey(bouiToOpen);
							} catch (NumberFormatException e) {
								throw new RuntimeException(e);
							} catch (boRuntimeException e) {
								throw new RuntimeException(e);
							}
						}
					} 
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		
			if( oViewRoot != null ) {
		        oRequestContext.setViewRoot( oViewRoot );
		        oRequestContext.getFacesContext().renderResponse();
	        }
		}
		
	}
	
	public void advancedSearch(){
		
		XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        Object value = ((XUICommand)oRequestContext.getEvent().getSource()).getValue();
        ToolBar tb = (ToolBar) ((XUICommand)oRequestContext.getEvent().getSource()).getParent();
        GridPanel explorer = (GridPanel) tb.getParent();
        String advancedFilters = explorer.getAdvancedFilters();
        
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/AdvancedSearch.xvw");
        AdvancedSearchBean bean = (AdvancedSearchBean) oViewRoot.getBean("viewBean");
        bean.setObjectName(value.toString());
        
        GridPanelJSonFiltersBuilder builder = new GridPanelJSonFiltersBuilder( advancedFilters );
        
        initializeAdvancedSearch( oRequestContext, oViewRoot, bean, builder );
        
        
	}

	/**
	 * 
	 * Initializes the advanced search bean
	 * 
	 * @param oRequestContext
	 * @param oViewRoot
	 * @param bean
	 * @param builder 
	 */
	private void initializeAdvancedSearch( XUIRequestContext oRequestContext, XUIViewRoot oViewRoot,
			AdvancedSearchBean bean, GridPanelJSonFiltersBuilder builder ) {
		oRequestContext.setViewRoot( oViewRoot );
		bean.initializeFilters( builder.getFilters() );
	}
	
	
	public void dummy(){
		//Just to bind
	}
	
	public void canCloseTab(){
		XUIRequestContext oRequestContext = getRequestContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.renderResponse();
	}
	
	
	/**
	 * Opens the viewer to filter grid data between two dates
	 */
	public void betweenDatesFilter() {

        // Cria view
        XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();

    	XUIActionEvent e = oRequestContext.getEvent();
    	XUICommand cmd = (XUICommand)e.getComponent();
    	
    	String column = (String)cmd.getValue();
    	GridPanel  oGridPanel = (GridPanel)cmd.getParent();
    	FilterTerms terms = oGridPanel.getCurrentFilterTerms();
    	
    	oViewRoot = oSessionContext.createChildView( "netgest/bo/xwc/components/classic/grid/GridBetweenDateSelector.xvw" );
    	GridDatesBetweenFilterBean bean = (GridDatesBetweenFilterBean) oViewRoot.getBean( "viewBean" );
    	bean.setColumn( column );
    	bean.setGridId( oGridPanel.getClientId() );

    	if (terms != null){
    		Iterator<FilterJoin> it = terms.iterator();
    		while (it.hasNext()){
    			FilterJoin join = it.next();
    			if (join != null){
    				FilterTerm term = join.getTerm();
    				if (term != null && StringUtils.hasValue( term.getDataField() ) ){
    					if (column.equals( term.getDataField() )){
    						byte operator = term.getOperator();
    						Object value = term.getValue();
    						Timestamp newTime = null;
    						if (value != null && value instanceof java.util.Date){
    							newTime = new Timestamp( ((java.util.Date) value).getTime() );
    							if (operator == FilterTerms.OPERATOR_GREATER_THAN){
    								bean.setStart( newTime );
    							}
    							else if (operator == FilterTerms.OPERATOR_LESS_THAN){
    								bean.setEnd( newTime );
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	
        oRequestContext.setViewRoot( oViewRoot );
        
    }
	
}

