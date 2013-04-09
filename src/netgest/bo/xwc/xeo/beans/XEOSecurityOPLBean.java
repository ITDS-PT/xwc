package netgest.bo.xwc.xeo.beans;

import java.math.BigDecimal;
import java.util.HashMap;

import netgest.bo.def.v2.boDefInterfaceImpl;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boBridgeRow;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.security.securityOPL;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

/**
 * 
 * Bean to help with the OPL Viewer
 * 
 * @author Pedro Pereira
 *
 */
public class XEOSecurityOPLBean extends XEOEditBean 
{

	/**
	 * The name of the bridge that manages securities
	 */
	private static final String KEYS_BRIDGE = "KEYS";
	/**
	 * The name of the bridge attribute that has the security code (sum of permissions)
	 */
	private static final String KEYS_ATT_SECURITY_CODE = "securityCode";
	/**
	 * The name of the bridge attribute that has the security type (design time/runtime)
	 */
	private static final String KEYS_ATT_SECURITY_TYPE = "securityType";
	
	//Security Values for the permissions
	private static final byte READ_PERMISSION = 0;
	private static final byte WRITE_PERMISSION = 1;
	private static final byte DELETE_PERMISSION = 2;
    private static final byte FULL_PERMISSION = 3;
	
	//Path to the Icons
	private static final String READ_PERMISSON_ICON = "ext-xeo/images/menus/read_permission.png";
	private static final String WRITE_PERMISSON_ICON = "ext-xeo/images/menus/write_permission.png";
	private static final String DELETE_PERMISSON_ICON = "ext-xeo/images/menus/delete_permission.jpg";
	private static final String FULL_CONTROL_PERMISSON_ICON = "ext-xeo/images/menus/full_control_permission.gif";
	
	
	
	/**
	 * The Current type of the object (must be a type of user/role/group)
	 */
	private String currentType;
	
	/**
	 * The object with the permissions
	 */
	private Number targetObject;
	
	private String display;
	
	/**
	 * The value for the read permission
	 */
	private String readPermission;
	
	/**
	 * The value for the write permission
	 */
	private String writePermission;
	
	/**
	 * The value for the delete permission
	 */
	private String deletePermission;
	
	/**
	 * The value for the fullControl permission
	 */
	private String fullControlPermission;
	
	
	
	/**
	 * The reference to the current object in the KEYS bridge
	 */
	private boObject currentTarget;
	
	/**
	 * Whether the action button should be visible or not
	 */
	@SuppressWarnings("unused")
	private boolean buttonVisible;
	
	/**
	 * Whether the add permission button should be disabled
	 */
	@SuppressWarnings("unused")
	private boolean disabledAddBtn;
	
	public void setCurrentType(String type)
	{
		try 
		{
			if (this.getXEOObject().exists())
				this.currentType = type;
			else
			{
				this.currentType = this.getXEOObject().getName();
			}
				
		} 
		catch (boRuntimeException e) 
		{
			this.currentType = type;
		}
	}
	
	/**
	 * 
	 * Checks whether the OPL is being created or being edited
	 * 
	 * @return True if the OPL is being edited or false if it is a new one
	 */
	public boolean getInEdition()
	{
		if (this.getCurrentTarget() != null)
			return true;
		
		return false;
	}
	
	/**
	 * 
	 * Sets the current target whose permissions are being edited
	 * 
	 * @param target The boObject that's the target or null if a new object is being created
	 */
	public void setCurrentTarget(boObject target)
	{
		this.currentTarget = target;
	}
	
	public boObject getCurrentTarget()
	{
		return this.currentTarget;
	}
	
	public void setButtonVisible(boolean val)
	{
		this.buttonVisible = val;
	}
	
	public boolean getButtonVisible()
	{
		if (getCanAddBtn())
			return false;
		else
			return true;
	}
	
	public void setInEdition(boolean val)
	{}
	
	public String getCurrentType()
	{
		try 
		{
			if (!this.getXEOObject().exists())
				return this.getXEOObject().getName();
		} 
		catch (boRuntimeException e) 
		{}
		return this.currentType;
	}

	
	public boolean getDisabledAddBtn()
	{
		return !getCanAddBtn();
	}
	
	public void setDisabledAddBtn(boolean val)
	{
		this.disabledAddBtn = val;
	}
	
	public void setTargetObject(Number targetObject)
	{
		this.targetObject = targetObject;
	}
	
	public Number getTargetObject()
	{
		return this.targetObject;
	}
	
	public void setDisplay(String disp)
	{
		this.display = disp;
	}
	
	/**
	 * 
	 * Gets the display String to show in the target
	 * object chosen in the viewer
	 * 
	 * @return A string with the cardID of an object
	 */
	public String getDisplay()
	{
		display = "";
		if (targetObject != null)
		{
			try 
			{
				boObject current = boObject.getBoManager().loadObject(getEboContext(), 
						this.targetObject.longValue());
				return current.getTextCARDID().toString();
			} 
			catch (boRuntimeException e) 
			{
				e.printStackTrace();
				return display;
			}
		}
		return display;
	}
	
	//Permissions setters and getters
	
	public String getReadPermission()
	{
		return this.readPermission;
	}
	
	public void setReadPermission(String val)
	{
		this.readPermission = val;
	}
	
	public String getWritePermission()
	{
		return this.writePermission;
	}
	
	public void setWritePermission(String val)
	{
		this.writePermission = val;
	}
	
	public String getDeletePermission()
	{
		return this.deletePermission;
	}
	
	public void setDeletePermission(String val)
	{
		this.deletePermission = val;
	}
	
	public String getFullControlPermission()
	{
		return this.fullControlPermission;
	}
	
	public void setFullControlPermission(String val)
	{
		this.fullControlPermission = val;
	}
	 // End of permission setter and getters
	
	
	/**
	 * 
	 * Creates a map with the BOUIs of all objects with system given securities
	 * 
	 * 
	 */
	/*private void createMapOfSystemSecurities()
	{
		boObject currentBeanObject = getXEOObject();
		
		if (currentBeanObject.getBoDefinition().getBoOPL() != null)
		{
			String[] readKeys = currentBeanObject.getBoDefinition().getBoOPL().getReadKeyAttributes();
			String[] writeKeys = currentBeanObject.getBoDefinition().getBoOPL().getWriteKeyAttributes();
			String[] deleteKeys = currentBeanObject.getBoDefinition().getBoOPL().getDeleteKeyAttributes();
			String[] fullControlKeys = currentBeanObject.getBoDefinition().getBoOPL().getFullControlKeyAttributes();
			
			addKeysToMap(readKeys, currentBeanObject);
			addKeysToMap(writeKeys, currentBeanObject);
			addKeysToMap(deleteKeys, currentBeanObject);
			addKeysToMap(fullControlKeys, currentBeanObject);
		}
	}*/
	
	
	
	/**
	 * 
	 * Checks whether or not the current user can share permissions with other users
	 * 
	 * @return True if the current user has full control permission on this object
	 */
	public boolean getCanAddBtn()
	{	
		try {
			if (securityOPL.hasFullControl(getXEOObject()))
				return true;
		} catch (boRuntimeException e) {
			//e.printStackTrace();
		}
		long bouiPerformer= getXEOObject().getEboContext().getBoSession().getPerformerBoui();
		if (getEboContext().getSysUser().getBoui() == bouiPerformer)
			return true;
		
		return false;
	}
	
	public boolean getCanRemoveBtn()
	{
		return this.getCanAddBtn();
	}
	
	public boolean addBtnDisabled()
	{
		return !getCanAddBtn();
	}
	
	private String getImageForPermission(String path, String type)
	{
		return "<img src=\""+path+"\" width='16' height='16' alt='"+type+"' title='"+type+"' />";
	}
	
	/**
	 * 
	 * Converts a String value (0 or 1) into a boolean value
	 * 
	 * @param val A string to convert to boolean
	 * 
	 * @return True if the string is "1", false if the string is "0" or any other thing
	 */
	private boolean getBooleanFromString(String val)
	{
		if (val.equalsIgnoreCase("1"))
			return true;
		else if (val.equalsIgnoreCase("0"))
			return false;
		else
			return false;
	}
	
	/**
	 * Save the current OPL permissions
	 */
	public void saveOPL()
	{
		XUIRequestContext oRequestContext = getRequestContext();
		boObject currentObjectOfPermissions = getXEOObject();
		
		if (getTargetObject() == null || getCurrentType() == null)
		{
			oRequestContext.addMessage(
	                "Bean",
	                new XUIMessage(XUIMessage.TYPE_POPUP_MESSAGE, XUIMessage.SEVERITY_INFO, 
	                    MessageLocalizer.getMessage("BAD"), 
	                    MessageLocalizer.getMessage("CANNOT_SAVE_REQUIRED_FIELDS") 
	                )
	            );
			oRequestContext.renderResponse();
			return;
		}
		
		boolean readVal = getBooleanFromString(this.getReadPermission());
		boolean writeVal = getBooleanFromString(this.getWritePermission());
		boolean deleteVal = getBooleanFromString(this.getDeletePermission());
		boolean fullVal = getBooleanFromString(this.getFullControlPermission());
		int securityCode = this.getValueSecurityCode(readVal, writeVal, deleteVal, fullVal);
		try
		{
			if (getCurrentTarget() != null) //We're updating
			{
				bridgeHandler oBridgeHandler = currentObjectOfPermissions.getBridge(KEYS_BRIDGE);
				boBridgeIterator it = oBridgeHandler.iterator();
				
				
				while (it.next())
				{
					boObject obj = it.currentRow().getObject();
					if (getCurrentTarget().getBoui() == obj.getBoui())
					{
						it.currentRow().getAttribute(KEYS_ATT_SECURITY_CODE)
							.setValueLong(securityCode);
						currentObjectOfPermissions.update();
						break;
					}
				}
				
			}
			else
			{	//Must create a new entry in the bridge and fill
				bridgeHandler oBridgeHandler = currentObjectOfPermissions.getBridge(KEYS_BRIDGE);
				Number target = this.getTargetObject();
				oBridgeHandler.add(target.longValue());
				boBridgeIterator it = oBridgeHandler.iterator();
				it.last();
				oBridgeHandler.getAttribute(KEYS_ATT_SECURITY_CODE).
					setValueLong(securityCode);
				oBridgeHandler.getAttribute(KEYS_ATT_SECURITY_TYPE).setValueLong(1);
				currentObjectOfPermissions.update();
				
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		//Close the view and show the message
		oRequestContext = XUIRequestContext.getCurrentContext();
		XVWScripts.closeView( oRequestContext.getViewRoot() );
    	XUIViewRoot viewRoot = oRequestContext.getSessionContext().
    		createView(SystemViewer.DUMMY_VIEWER);
    	
    	oRequestContext.addMessage(
                "sucessMessage",
                new XUIMessage(XUIMessage.TYPE_POPUP_MESSAGE, XUIMessage.SEVERITY_INFO, 
                    BeansMessages.TITLE_SUCCESS.toString(), 
                    BeansMessages.BEAN_SAVE_SUCESS.toString() 
                )
            );
    	getViewRoot().getParentView().syncClientView();
    	oRequestContext.setViewRoot( viewRoot );
		oRequestContext.renderResponse();
	}
	
	/**
	 * Removes the selected permission from the given instance object
	 */
	public void removePermission(){
		
		try {
			XUIRequestContext oRequestContext = getRequestContext();
			GridPanel permissionList = (GridPanel) getViewRoot().findComponent(GridPanel.class);
			DataRecordConnector selectedLine = permissionList.getActiveRow();
			
			if (selectedLine == null || permissionList.getSelectedRows().length == 0)
			{
				oRequestContext.addMessage(
		                "noRowsSelected",
		                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, 
		                    MessageLocalizer.getMessage("ERROR"), 
		                    MessageLocalizer.getMessage("PLEASE_SELECT_ONE_ROW_FROM_THE_PANEL") 
		                )
		            );
		    	oRequestContext.renderResponse();
		    	return;
			}
			
			DataRecordConnector[] rows = permissionList.getSelectedRows();
			
			DataFieldConnector currBoui = selectedLine.getAttribute("BOUI");
			Long val = new Long(currBoui.getValue().toString());
			
			boObject currentObjectOfPermissions = getXEOObject();
			
			bridgeHandler oBridgeHandler = currentObjectOfPermissions.getBridge(KEYS_BRIDGE);
			boBridgeRow selectedRow = oBridgeHandler.getRow(val);
			boolean showRemoved = false;
			if (selectedRow.getAttribute(KEYS_ATT_SECURITY_TYPE) != null){
				if (selectedRow.getAttribute(KEYS_ATT_SECURITY_TYPE).getValueLong() == 1)
				{
					oBridgeHandler.remove();
					showRemoved = true;
					currentObjectOfPermissions.setChanged(true);
					currentObjectOfPermissions.update();
					getViewRoot().syncClientView();
				}
			}
			
			if (showRemoved){
				oRequestContext.addMessage(
		                "removedSucess",
		                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, 
		                    MessageLocalizer.getMessage("SUCCESS"), 
		                    MessageLocalizer.getMessage("THE_SELECTED_PERMISSION_WAS_REMOVED")
		                )
		            );
		    	oRequestContext.renderResponse();
			}
			else
			{
				oRequestContext.addMessage(
		                "removeError",
		                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, 
		                    MessageLocalizer.getMessage("OPERATION_NOT_ALLOWED"), 
		                    MessageLocalizer.getMessage("SYSTEM_PERMISSIONS_CANNOT_BE_REMOVED") 
		                )
		            );
		    	oRequestContext.renderResponse();
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (boRuntimeException e) {
			throw new RuntimeException(ExceptionMessage.COULD_NOT_REMOVE_PERMISSIONS_FROM_CURRENT_OBJECT.toString(), e);
		}
	}
	
	/**
	 * Opens the viewer to add
	 */
	public void addPermission()
	{
		XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/XEOEditOPL.xvw");
        XEOSecurityOPLBean oSecurityBean = (XEOSecurityOPLBean)oViewRoot.getBean("viewBean");
        oSecurityBean.setCurrentObjectKey( Long.toString( getXEOObject().getBoui() ) );
        
        oSecurityBean.setCurrentTarget(null);
		oSecurityBean.setCurrentType(null);
		oSecurityBean.setTargetObject(null);
		
		
        oRequestContext.setViewRoot( oViewRoot );
        oRequestContext.renderResponse();
	}
	
	/**
	 * Opens the viewer to edit the permissions on a given target user/group/role
	 * or show a message if the user does not have permissions
	 */
	public void openDblClick()
	{
		
		XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot;
        
        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
		
		if (!getCanAddBtn())
		{
			oRequestContext.addMessage(
	                "notAllowedOperation",
	                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, 
	                    MessageLocalizer.getMessage("OPERATION_NOT_ALLOWED"), 
	                    MessageLocalizer.getMessage("YOU_DONT_HAVE_PERMISSIONS_TO_EDIT_THIS_TOPIC") 
	                )
	            );
	    	oRequestContext.renderResponse();
		}
		else
		{
			
	        oViewRoot = oSessionContext.createChildView("netgest/bo/xwc/xeo/viewers/XEOEditOPL.xvw");
	        XEOSecurityOPLBean oSecurityBean = (XEOSecurityOPLBean)oViewRoot.getBean("viewBean");
	        oSecurityBean.setCurrentObjectKey( Long.toString( getXEOObject().getBoui() ) );
	        
	        //Get the current active row  in the panel and passe it to the Bean
	        GridPanel oGridPanel = (GridPanel) getViewRoot().findComponent(GridPanel.class);
	        DataRecordConnector oRow= oGridPanel.getActiveRow();
	        
	        try 
	        {
	        	
		        BigDecimal boui = ((BigDecimal) oRow.getAttribute("BOUI").getValue());
		        bridgeHandler bridgeKeysHandler = getXEOObject().getBridge(KEYS_BRIDGE);
		        boBridgeRow bridgeRowToAnalyze = bridgeKeysHandler.getRow(boui.longValue());
		        boolean showMessageNoPermission = false;
		        Object securityTypeOfSelectedObject = bridgeRowToAnalyze.getAttribute(KEYS_ATT_SECURITY_TYPE).getValueObject();
		        if (securityTypeOfSelectedObject != null)
		        {
		        	BigDecimal securityTypeValue = (BigDecimal) securityTypeOfSelectedObject;
		        	if (securityTypeValue.longValue() == 0)
		        		showMessageNoPermission = true;
		        }
		        else
		        	showMessageNoPermission = true;
		        
		        if (showMessageNoPermission)
		        {
		        	oRequestContext.addMessage(
			                "Bean",
			                new XUIMessage(XUIMessage.TYPE_ALERT, XUIMessage.SEVERITY_ERROR, 
			                    MessageLocalizer.getMessage("OPERATION_NOT_ALLOWED"), 
			                    MessageLocalizer.getMessage("CANNOT_EDIT_SYSTEM_OPL") 
			                )
			            );
			    	oRequestContext.renderResponse();
			    	return;
		        }
	        
	        
	        	boObject oCurrTarget = boObject.getBoManager().loadObject(getEboContext(), 
	        			Long.valueOf(String.valueOf(oRow.getAttribute("BOUI").getValue())));
				oSecurityBean.setCurrentTarget(oCurrTarget);
				oSecurityBean.setCurrentType(oCurrTarget.getBoDefinition().getLabel());
				oSecurityBean.setTargetObject(oCurrTarget.getBoui());
				
				boBridgeRow oRowBridge = getXEOObject().getBridge(KEYS_BRIDGE).
					getRow(oCurrTarget.getBoui());
				String security = oRowBridge.getAttribute(KEYS_ATT_SECURITY_CODE).
					getValueString();
				Integer val = Integer.valueOf(security);
				
				if (hasPermission(val, READ_PERMISSION))
					oSecurityBean.setReadPermission("1");
				if (hasPermission(val, WRITE_PERMISSION))
					oSecurityBean.setWritePermission("1");
				if (hasPermission(val, DELETE_PERMISSION))
					oSecurityBean.setDeletePermission("1");
				if (hasPermission(val, FULL_PERMISSION))
					oSecurityBean.setFullControlPermission("1");
				
			} 
	        catch (Exception e) 
			{
	        	e.printStackTrace(); 
	        	oSecurityBean.setCurrentTarget(null);
	        }
	        
	        oRequestContext.setViewRoot( oViewRoot );
	        oRequestContext.renderResponse();
		}
		
	}
	
	
	@Override
	public void lookupAttribute( String sCompId ) throws boRuntimeException 
	{
		XUIRequestContext   oRequestContext;
        XUISessionContext   oSessionContext;
        XUIViewRoot         oViewRoot = getViewRoot();
        Window				oWnd;

        oRequestContext = XUIRequestContext.getCurrentContext();
        oSessionContext = oRequestContext.getSessionContext();
        
        AttributeBase oAtt = (AttributeBase)getViewRoot().findComponent( sCompId );
        
        String lookupViewerName = getCurrentType() + "_lookup.xvw";
        
        
    	
        XEOBaseLookupList   oBaseBean;
        oViewRoot = oSessionContext.createChildView( lookupViewerName );
        oBaseBean = (XEOBaseLookupList)oViewRoot.getBean( "viewBean" );

        oWnd = (Window)oViewRoot.findComponent(Window.class); 
        if( oWnd != null ) {
        	oWnd.setAnimateTarget( sCompId );
        }
        
        oBaseBean.setParentBean( this ); 
        oBaseBean.setParentAttributeName( KEYS_BRIDGE );
        oBaseBean.setLookupObjects( getValidTargets() );
        oBaseBean.setParentParentBeanId( "viewBean" );
        oBaseBean.setParentComponentId( oAtt.getClientId() );
        String sBoql = "select " + getCurrentType();
    	oBaseBean.executeBoql( sBoql );
        
        oRequestContext.setViewRoot( oViewRoot );
        
        oRequestContext.renderResponse();

	}
	
	
	/**
	 * 
	 * Returns a map (key = id, value = label= of the valid target objects
	 * for the KEYS bridge of the current object
	 * 
	 * @return A map with the values for the Lov
	 */
	public HashMap<String,String> getValidTargets()
	{
		boObject currentObject = getXEOObject();
		
		String[] objsOfBridge = currentObject.getBoDefinition().
			getAttributeRef(KEYS_BRIDGE).getObjectsName();
		HashMap<String, String> lovMap = new HashMap<String, String>();
		for (String sObjectName : objsOfBridge)
		{
			if (boDefInterfaceImpl.getBoDefinition(sObjectName) != null)
				lovMap.put(sObjectName, boDefInterfaceImpl.getBoDefinition(sObjectName).getLabel());
		}
		return lovMap;
	}
	
	public GridColumnRenderer getRenderSecurity()
	{
		return new GridColumnRenderer() 
		{
			
			@Override
			public String render(GridPanel grid, DataRecordConnector record,
					DataFieldConnector field) 
			{
				
				StringBuilder b = new StringBuilder();
				
				Object oVal = field.getValue();
				
				if (oVal != null)
				{
					Integer val = Integer.valueOf(field.getValue().toString());
					
					if (   val == 1 	|| val == 3 	|| val == 5 
						|| val == 7 	|| val == 9 	|| val == 11
						|| val == 13 	|| val == 15
					)
						b.append(getImageForPermission(READ_PERMISSON_ICON,
								BeansMessages.OPL_READ_PERMISSION.toString()));
					
					if (
							val == 2 	|| val == 3 	|| val == 6 
						||	val == 7 	|| val == 10 	|| val == 11
						||	val == 14 	|| val == 15 
					)
						b.append(getImageForPermission(WRITE_PERMISSON_ICON, 
								BeansMessages.OPL_WRITE_PERMISSION.toString()));
					
					if (
							val == 4 	|| val == 5 	|| val == 6 
						||	val == 7 	|| val == 12 	|| val == 14
						||	val == 15 
					)
						b.append(getImageForPermission(DELETE_PERMISSON_ICON, 
								BeansMessages.OPL_DELETE_PERMISSION.toString()));
					
					if (
							val == 8 	|| 	val == 9 	|| val == 10 
						||	val == 11 	|| 	val == 12 	|| val == 13
						||	val == 14 	||	val == 15 
					)
						b.append(getImageForPermission(FULL_CONTROL_PERMISSON_ICON, 
								BeansMessages.OPL_FULL_PERMISSION.toString()));
				}
				return b.toString();
			}
		};
	}
	
	
	/**
	 * 
	 * Given a set of permissions, returns the security code associated
	 * 
	 * @param read Read permission
	 * @param write Write permission
	 * @param delete Delete permission
	 * @param full Full control permission
	 * 
	 * @return the security code for the given permissions
	 */
	private int getValueSecurityCode(boolean read, boolean write, boolean delete, boolean full)
	{
		int val = 0;
		if (read)
			val += 1;
		if (write)
			val += 2;
		if (delete)
			val += 4;
		if (full)
			val += 8;
		
		return val;
	}
	
	/**
	 * 
	 * Checks if a given value represents a given security permission
	 * 
	 * @param val The security code to check
	 * @param type The type of permission to check
	 * 
	 * @return True if the security code has the given permission
	 */
	private boolean hasPermission(Integer val, byte type)
	{
		if (type == READ_PERMISSION)
		{
			if (   val == 1 	|| val == 3 	|| val == 5 
					|| val == 7 	|| val == 9 	|| val == 11
					|| val == 13 	|| val == 15
				)
				return true;
		}
		else if (type == WRITE_PERMISSION)
		{
			if (
					val == 2 	|| val == 3 	|| val == 6 
				||	val == 7 	|| val == 10 	|| val == 11
				||	val == 14 	|| val == 15 
			)
				return true;
		}
		else if (type == DELETE_PERMISSION)
		{
			if (
					val == 4 	|| val == 5 	|| val == 6 
				||	val == 7 	|| val == 12 	|| val == 14
				||	val == 15 
			)
				return true;
		}
		else if (type == FULL_PERMISSION)
		{
			if (
					val == 8 	|| 	val == 9 	|| val == 10 
				||	val == 11 	|| 	val == 12 	|| val == 13
				||	val == 14 	||	val == 15 
			)
			return true;
		}
		
		return false;
	}
	
	public GridColumnRenderer getRenderType()
	{
		return new GridColumnRenderer() {
			
			@Override
			public String render(GridPanel grid, DataRecordConnector record,
					DataFieldConnector field) {
				//Get the value of the current object
				Object value = null;
				value = field.getValue();
				if (value != null){
					Long val = Long.valueOf(field.getValue().toString());
					if (val == 0)
						return BeansMessages.OPL_DESIGN_TIME_PERMISSION.toString();
					else
						return BeansMessages.OPL_RUNTIME_PERMISSION.toString();
				}
				else
					return BeansMessages.OPL_DESIGN_TIME_PERMISSION.toString();
				
			}
		};
	}
	
	
	
}
