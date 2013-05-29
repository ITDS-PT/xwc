package netgest.bo.xwc.components.security;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.xwc.xeo.beans.XEOEditBean;
import netgest.bo.xwc.xeo.beans.XEOBaseOrphanEdit;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectConnector;
import netgest.bo.xwc.components.localization.ViewersMessages;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIViewRoot;

public class ComponentSecurityBean extends XEOEditBean {
	
	private static final PermissionsColumnRenderer columnRenderer = new PermissionsColumnRenderer();
		
	protected Map<String,String> 	securityViewersMap;
	private String			  	selectedViewer;
	private String 				childViewer;
	private String			  	selectedComponent;
	private boolean			  	reloadTree = true;
	private List<Long>	  		editableObjects = new ArrayList<Long>();
	
	/** Filter to apply when selecting the components to display */
	private String filter = "";

	/**
	 * Builds the filter to apply when selecting the components to display.
	 * Returns an or between getAllowedTypes and getInclusionFilter.
	 * @return
	 */
	private String getFilter() {
		SecurableComponent.COMPONENT_TYPE[] allowedTypes = getAllowedTypes();
		String inclusionFilter = getInclusionFilter();
		if ( allowedTypes!=null && allowedTypes.length>0 
				&& filter!=null && "".equals(filter) ) {
			StringBuffer sb = new StringBuffer( "( componentType in (" );
			for (int i = 0; i < allowedTypes.length; i++) {
				sb.append( "'" );
				sb.append( allowedTypes[i].toString() );
				sb.append( "'" );
				sb.append( "," );
			}
			sb.deleteCharAt( sb.toString().length()-1 );
			sb.append( ") " );
			if ( inclusionFilter!=null && inclusionFilter.trim().length()>0 ) {
				sb.append( " or " );
				sb.append( inclusionFilter );
			}
			sb.append( " ) " );
			sb.insert( 0, " and " );
			filter = sb.toString();
		}
		return filter;
	}
	
	/** Returns the component types to display in the tree */
	protected SecurableComponent.COMPONENT_TYPE[] getAllowedTypes() {
		return new SecurableComponent.COMPONENT_TYPE[]{};
	}

	/** Overrides the behaviour of getAllowedTypes by selecting extra components */
	protected String getInclusionFilter() {
		return "";
	}
	
	protected String getLabelForComponent( boObject accessPolicy ) throws boRuntimeException {
		return accessPolicy.getAttribute("label").getValueString();
	}
	
	@Override
	public String getTitle() {
		return BeansMessages.VIEWER_SECURITY_TITLE.toString();
	}
	
	public void setChildViewer( String sChildViewer ) {
		this.childViewer = sChildViewer;
	}
	
	public String getChildViewer() {
		return this.childViewer;
	}
	
	public GridColumnRenderer getPermissionsRenderer() {
		return columnRenderer;
	}
	
	public boolean getReloadTree() {
		boolean ret =this.reloadTree;
		this.reloadTree = false;
		return ret;
	}
	
	public void setReloadTree( boolean reloadTree ) {
		this.reloadTree = reloadTree;
	}
	
	public Map<String,String> getViewersMap() {
		try {
			if( this.securityViewersMap == null ) {
				securityViewersMap = new LinkedHashMap<String,String>();
				
				List<String> viewersList = ViewerAccessPolicyBuilder.getRegisteredViewers();
				
				for( String viewerName : viewersList ) {
					this.securityViewersMap.put( viewerName , viewerName );
				}
			}
			return this.securityViewersMap;
		} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setSelectedViewer( String viewerName ) {
		setReloadTree( true );
		this.selectedViewer = viewerName;
	}
	
	public String getSelectedViewer() {
		if( this.selectedViewer == null ) {
			Map<String,String> viewers = getViewersMap();
			if ( viewers.size() > 0 ) {
				this.selectedViewer = viewers.get( viewers.keySet().iterator().next() ); 
			}
		}
		return this.selectedViewer;
	}	

	public void setSelectedComponent( String selectedComponent ) {
		this.selectedComponent = selectedComponent;
	}
	
	public String getSelectedComponent() {
		return this.selectedComponent;
	}
	
	public void selectComponent() {
		XUIRequestContext oRequestContext;
		
		oRequestContext = XUIRequestContext.getCurrentContext();
		Menu eventSrc = (Menu)oRequestContext.getEvent().getSource();
		
		String sValue = String.valueOf( eventSrc.getValue() );
		String[] sValues = sValue.split("\\|");
		
		String compViewer = sValues[0].length() == 0? getSelectedViewer():sValues[0];
		setSelectedComponent( sValues[1] );
		boObjectList list = boObjectList.list( 
					getEboContext(), 
					"select XVWAccessPolicy where viewer=? and id=?",
					new Object[] { compViewer, sValues[1] },
					1,
					1,
					""
				);
		
		if( list.next() ) {
			setCurrentObjectKey( Long.toString( list.getCurrentBoui() ) );
			if( !this.editableObjects.contains( list.getCurrentBoui() ) )
				this.editableObjects.add( list.getCurrentBoui() );
		}
		
	}
	
	public String getSegurancasTitle() {
		return this.selectedComponent;
	}
	
	public Menu getTree() {
		Menu root;
		
		root = new Menu();
		root.setText( BeansMessages.VIEWER_SECURITY_COMPONENTS.toString() );
		String viewer = getSelectedViewer();
		if( viewer != null && viewer.length() > 0 ) {
			try {
				boObjectList list = boObjectList.list(  
						getEboContext(), 
						"select XVWAccessPolicy where viewer=? "+getFilter()+" and referenced = '1' order by BOUI",
						new Object[] { this.selectedViewer },
						1,
						9999,
						true
				);
				
				HashMap<String, Menu> menuMap = new LinkedHashMap<String, Menu>();
				buildTree( "" ,root, list, menuMap);
				
			} catch (Exception e) {
				throw new RuntimeException( e );
			}
		}
		return root;
	}
	
	private void buildTree( String viewerName, Menu root, boObjectList list, Map<String, Menu> menuMap ) throws boRuntimeException {

		while( list.next() ) {
			
			boObject accessPolicy = list.getObject();

			String 		id 				= accessPolicy.getAttribute("id").getValueString();
			String 		label		 	= getLabelForComponent( accessPolicy );
			String 		childViewers    = accessPolicy.getAttribute("childViewers").getValueString();
			boObject 	parent			= accessPolicy.getAttribute("container").getObject();
			
			Menu folderMenu;
			
			if( parent == null ) {
				folderMenu = root;
			}
			else {
				String 	sParentBoui =  Long.toString( parent.getBoui() );
				folderMenu = menuMap.get( sParentBoui );
				if( folderMenu == null ) {
					menuMap.put( sParentBoui, folderMenu = new Menu() );
				}
			}
			String 	sBoui =  Long.toString( accessPolicy.getBoui() );
			Menu item = menuMap.get( sBoui );
			if( item == null ) {
				item = new Menu();
			}
			item.setText( label );
			item.setValue( viewerName + "|" + id );
			item.setServerAction( "#{" + getId() + ".selectComponent}" );
			folderMenu.getChildren().add( item );
			menuMap.put( sBoui, item );
			
			if( childViewers.length() > 0 ) {
				String[] sChildViewers = childViewers.split(";");
				for( String sViewerName : sChildViewers )  {
					boObjectList listChildViewer = boObjectList.list(  
							getEboContext(), 
							"select XVWAccessPolicy where viewer=? "+getFilter()+" order by BOUI",
							new Object[] { sViewerName },
							1,
							9999,
							true
					);
					Map<String, Menu> childMap = new LinkedHashMap<String,Menu>();
					buildTree( sViewerName, item , listChildViewer, childMap );
				}
			}
		}
		
	}
	
/*	
	public String getSelectedComponentPermissions() {
		return this.selectedComponent;
	}
*/	
	public boolean getShowComponentTree() {
		return true;
	}
	
	@Override
	public boObject getXEOObject() {
		try {
			boObject obj;
			
			if( getCurrentObjectKey() == null )  {
				obj = boObject.getBoManager().createObject(  getEboContext(), "XVWAccessPolicy" );
			}
			else {
				obj = super.getXEOObject();
			}
			return obj;
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
	}
	
	public void canCloseTab() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		Window xWnd = (Window)viewRoot.findComponent(Window.class);
		if( xWnd != null ) {
			if( xWnd.getOnClose() != null ) {
				xWnd.getOnClose().invoke( oRequestContext.getELContext(), null);
            }
		}
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.renderResponse();
	}
	
	public void addPermission() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUISessionContext oSessionContext = oRequestContext.getSessionContext();
		
		XUIViewRoot oViewRoot = oSessionContext.createChildView( "netgest/bo/xwc/components/security/ComponentSecurityPermissionsViewer.xvw" );
		
		ComponentSecurityPermissionsBean oBaseBean;
		
		oBaseBean = (ComponentSecurityPermissionsBean)oViewRoot.getBean("viewBean");
		oBaseBean.createNew( "XVWAccessPolicyDetail" );
		oRequestContext.setViewRoot( oViewRoot );
		oBaseBean.setParentBeanId( getId() );
		oBaseBean.load();
		oViewRoot.processInitComponents();
	}

	@Override
	public void setOrphanEdit(XEOEditBean orphanEditBean)
			throws boRuntimeException {
		
		boObject obj = getXEOObject();
		boObject orphanObj = orphanEditBean.getXEOObject();
		bridgeHandler h = obj.getBridge( "policyDetails" );
		
		/*
		if( !h.haveBoui( obj.getBoui() ) ) {
			h.add( orphanObj.getBoui() );
		}
		*/
		boolean add = true;
		boBridgeIterator iterator = h.iterator();
		iterator.beforeFirst();
		while( iterator.next() ) {
			if ( iterator.currentRow().getObject().getBoui()==orphanObj.getBoui() ) {
				add = false;
				break;
			}
		}
		if ( add ) {
			h.add( orphanObj.getBoui() );
		}
	}
	
	public void updateViewerComponents() {
		try {
			ViewerAccessPolicyBuilder viewerAccessBuilder = new ViewerAccessPolicyBuilder();
			viewerAccessBuilder.buildAccessPolicy( getEboContext(), XUIRequestContext.getCurrentContext().getSessionContext() );
			setReloadTree( true );
		} catch (boRuntimeException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void save() throws boRuntimeException {
		Object currentObj = getCurrentObjectKey();
		
		EboContext ctx = getEboContext();
		
		boolean commit = false;
		try {
			boolean ok = true;
			for( Long objBoui : this.editableObjects ) {
				boObject obj = getObject( objBoui );
				if( obj.isChanged() ) {
					setCurrentObjectKey( Long.toString( obj.getBoui() ) );
					validate();
					if( !isValid() ) {
						ok = false;
						break;
					}
					obj.update();
				}
			}
			if( ok ) {
				commit = true;
			}
		}
		finally {
			if( commit ) {
				ctx.commitContainerTransaction();
			}
			else {
				ctx.rollbackContainerTransaction();
			}
			setCurrentObjectKey( currentObj );
		}
		if( commit ) {
	        XUIRequestContext.getCurrentContext().addMessage(
	                "Bean",
	                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_INFO, 
	                    BeansMessages.TITLE_SUCCESS.toString(), 
	                    BeansMessages.BEAN_SAVE_SUCESS.toString() 
	                )
	            );
		}
	}
	
	public void editBridge() {
		editBridgePolicyDetails();
	}
	
	public void editBridgePolicyDetails() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUISessionContext oSessionContext = oRequestContext.getSessionContext();
		
        ActionEvent oEvent = oRequestContext.getEvent();
        
        // Get the src of the event
        XUICommand oCommand = (XUICommand)oEvent.getComponent();
        
        GridPanel oGrid = (GridPanel)oCommand.findParentComponent(GridPanel.class);

        DataRecordConnector oSelectedRow = oGrid.getActiveRow();
        if( oSelectedRow != null ) {
        	
			XUIViewRoot oViewRoot = oSessionContext.createChildView( "netgest/bo/xwc/components/security/ComponentSecurityPermissionsViewer.xvw" );
			
			ComponentSecurityPermissionsBean oBaseBean;
			
			oBaseBean = (ComponentSecurityPermissionsBean)oViewRoot.getBean("viewBean");
			
			Object editValue = ((XEOObjectConnector)oSelectedRow).getAttribute("BOUI").getValue();
			
			oRequestContext.setViewRoot( oViewRoot );
			oBaseBean.setCurrentObjectKey( String.valueOf( editValue ) );
			oBaseBean.setParentBeanId( getId() );
			oBaseBean.load();
			oViewRoot.processInitComponents();
			
        }
		
		
	}

	public static class PermissionsColumnRenderer implements GridColumnRenderer {

		public String render(GridPanel grid, DataRecordConnector record, DataFieldConnector field) {
			StringBuilder ret = new StringBuilder();
			BigDecimal value = (BigDecimal)field.getValue();
			if( value != null ) {
				int intValue = value.intValue();
				if( intValue >= 0 ) {
					ret.append( "<b><i>" );
					if( intValue == SecurityPermissions.FULL_CONTROL ) {
						ret.append( ViewersMessages.SECURITY_FULL_CONTROL.toString() );
					} else if( intValue == SecurityPermissions.NONE ) {
						ret.append( ViewersMessages.SECURITY_NO_ACCESS.toString() );
					} 
					else {
						boolean appendComma = false;
						if( (intValue & SecurityPermissions.READ) > 0 ) {
							ret.append( ViewersMessages.SECURITY_READ.toString() );
							appendComma = true;
						}

						if( (intValue & SecurityPermissions.WRITE) > 0 ) {
							if( appendComma )
								ret.append( ",&nbsp;" );
							ret.append( ViewersMessages.SECURITY_WRITE.toString() );
							appendComma = true;
						}

						if( (intValue & SecurityPermissions.DELETE) > 0 ) {
							if( appendComma )
								ret.append( ",&nbsp;" );
							ret.append( ViewersMessages.SECURITY_DELETE.toString() );
							appendComma = true;
						}

						if( (intValue & SecurityPermissions.EXECUTE) > 0 ) {
							if( appendComma )
								ret.append( ",&nbsp;" );
							ret.append( ViewersMessages.SECURITY_EXECUTE.toString() );
							appendComma = true;
						}
					}
					ret.append( "</i></b>" );
				}
			}
			return ret.toString();
		}
		
	}
	
}
