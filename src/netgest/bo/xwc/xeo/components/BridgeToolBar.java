package netgest.bo.xwc.xeo.components;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOBridgeListConnector;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.components.utils.XEOComponentStateLogic;
import netgest.bo.xwc.xeo.localization.XEOComponentMessages;
import netgest.utils.StringUtils;

/**
 * 
 * A default {@link ToolBar} for the {@link Bridge} component, includes
 * buttons to add an existing {@link boObject} instance to the bridge, remove an object from the bridge
 * and create a new object to add to the bridge
 * 
 * @author jcarreira
 *
 */
public class BridgeToolBar extends ToolBarMenuPositions {
	
	/**
	 * Allows to change the object that will be used to retrieve the bridge from
	 */
	private XUIBindProperty<boObject> 	targetObject 	= 
		new XUIBindProperty<boObject>("targetObject", this, boObject.class );

	/**
	 * The name of the bridge to where the toolbar is connected.
	 * By default it searches a parent {@link Bridge} component and uses the bridgeName
	 * from that parent
	 */
	private XUIBindProperty<String>  bridgeName    = 
		new XUIBindProperty<String>( "bridgeName", this, String.class );
	
	/**
	 * Whether or nor the default "Add" button should be rendered or not
	 */
	private XUIViewBindProperty<Boolean>  renderAddBtn    = 
		new XUIViewBindProperty<Boolean>( "renderAddBtn", this, true, Boolean.class );

	/**
	 * Whether or nor the default "Remove" button should be rendered or not
	 */
	private XUIViewBindProperty<Boolean>  renderRemoveBtn    = 
		new XUIViewBindProperty<Boolean>( "renderRemoveBtn", this, true, Boolean.class );
	
	/**
	 * Whether or nor the default "Create New" button should be rendered or not
	 * (only works in orphan-mode) 
	 */
	private XUIViewBindProperty<Boolean>  renderCreateNewBtn    = 
		new XUIViewBindProperty<Boolean>( "renderCreateNewBtn", this, true, Boolean.class );
	
	/**
	 * Whether to render or not the favorites button 
	 */
	private XUIViewBindProperty<Boolean>  renderFavoritesBtn    = 
		new XUIViewBindProperty<Boolean>( "renderFavoritesBtn", this, false, Boolean.class );

	
	/**
	 * Whether or nor the default reorder buttons should be rendered or not
	 */
	
	private XUIStateBindProperty<Boolean>  renderOrderBridgeBtn    = 
			new XUIStateBindProperty<Boolean>( "renderOrderBridgeBtn", this, "false", Boolean.class );

	
	public boolean getRenderAddBtn() {
		return renderAddBtn.getEvaluatedValue();
	}

	public void setRenderAddBtn( String expression ) {
		this.renderAddBtn.setExpressionText( expression );
	}

	public boolean getRenderRemoveBtn() {
		return renderRemoveBtn.getEvaluatedValue();
	}

	public void setRenderRemoveBtn(String expression) {
		this.renderRemoveBtn.setExpressionText( expression );
	}

	public boolean getRenderCreateNewBtn() {
		return renderCreateNewBtn.getEvaluatedValue();
	}

	public void setRenderCreateNewBtn(String expression ) {
		this.renderCreateNewBtn.setExpressionText( expression );
	}
	
	public boolean getRenderFavoritesBtn(){
		return this.renderFavoritesBtn.getEvaluatedValue();
	}
	
	public void setRenderFavoritesBtn(String favBtnExpr){
		this.renderFavoritesBtn.setExpressionText(favBtnExpr);
	}
	
	public boolean getRenderOrderBridgeBtn() {
		return renderOrderBridgeBtn.getEvaluatedValue();
	}

	public void setRenderOrderBridgeBtn(String expression ) {
		this.renderOrderBridgeBtn.setExpressionText( expression );
	}
	
	@Deprecated
	public void setRenderCreateNew(String expression ) {
		this.renderCreateNewBtn.setExpressionText( expression );
	}

	public void setTargetObject( String sExprText ) {
		this.targetObject.setExpressionText( sExprText );
	}
	
	public boObject getTargetObject() {
		return this.targetObject.getEvaluatedValue();
	}
	
	@Override
	public String getRendererType() {
		return "toolBar";
	}

    public void setBridgeName(String sExpressionText ) {
        this.bridgeName.setExpressionText( sExpressionText );
    }

    public String getBridgeName() {
    	if (this.bridgeName.getEvaluatedValue() == null)
    	{
    		Bridge bridge = (Bridge) this.getParent();
    		if (bridge != null){
    			if (bridge.getBridgeName() != null)
    				return bridge.getBridgeName();
    		}
    	}
    	return this.bridgeName.getEvaluatedValue();
    }
    
   public static class OrderBrigeUpActionListener implements ActionListener {
        public void processAction(ActionEvent event) {
            try {
            	BridgeToolBar bridgeToolbar=(BridgeToolBar)((XUICommand)event.getSource()).getParent();
            	if (bridgeToolbar.isOriginalQuery())
            		bridgeToolbar.orderUpBridge();
            	else
            		bridgeToolbar.getRequestContext().addMessage( bridgeToolbar.getClientId(), 
                    		new XUIMessage(
                                XUIMessage.TYPE_ALERT,
                                XUIMessage.SEVERITY_WARNING,
                                XEOComponentMessages.BRIDGETB_ORDERWARNTITLE.toString(),
                                XEOComponentMessages.BRIDGETB_ORDERWARNMSG.toString()
                           )
			        );
			} catch (boRuntimeException e) {
				 throw new RuntimeException(e);
			}
        }
    }
   
   public static class OrderBrigeDownActionListener implements ActionListener {
       public void processAction(ActionEvent event) {
           try {
        	   BridgeToolBar bridgeToolbar=(BridgeToolBar)((XUICommand)event.getSource()).getParent();
        	   if (bridgeToolbar.isOriginalQuery())
        		   bridgeToolbar.orderDownBridge();
        	   else
        		   bridgeToolbar.getRequestContext().addMessage( bridgeToolbar.getClientId(), 
                   		new XUIMessage(
                               XUIMessage.TYPE_ALERT,
                               XUIMessage.SEVERITY_WARNING,
                               XEOComponentMessages.BRIDGETB_ORDERWARNTITLE.toString(),
                               XEOComponentMessages.BRIDGETB_ORDERWARNMSG.toString()
                          )
			        );
			} catch (boRuntimeException e) {
				 throw new RuntimeException(e);
			}
       }
   }
	
	
    private void orderUpBridge() throws boRuntimeException {
        
        GridPanel oGrid = (GridPanel)this.findParentComponent(GridPanel.class);
        
        bridgeHandler oBridgeHandler  = ((XEOBridgeListConnector)oGrid.getDataSource()).getBridge();
        
        DataRecordConnector[] oSelectedRows = oGrid.getSelectedRows();
                
        if (oSelectedRows!=null && oSelectedRows.length>0)
        {
	        int[] arrayRowIndexes = new int[oSelectedRows.length];
	                
	        for (int i = 0; i < oSelectedRows.length; i++) 
	        {
	        	long rowBoui = ((BigDecimal)oSelectedRows[i].getAttribute("BOUI").getValue()).longValue();
	        	
	        	oBridgeHandler.beforeFirst();
	            
	            while (oBridgeHandler.next())
	            {
	                if (oBridgeHandler.getObject().getBoui() == rowBoui )
	                {
	                	arrayRowIndexes[i] = oBridgeHandler.getRow();
	                    break;
	                }                 
	            }
	        }
	        
	        Arrays.sort(arrayRowIndexes);
	        String script = new String();
	        for(int index=0; index < arrayRowIndexes.length ; index++)
	        {            
	            if(arrayRowIndexes[index] > 0 && arrayRowIndexes[index] -1 > 0)
	            {
	                oBridgeHandler.beforeFirst();
	            	while (oBridgeHandler.next())
	                {
	                    if (oBridgeHandler.getRow() == arrayRowIndexes[index] )
	                    {                  	
	                        oBridgeHandler.moveRowTo(arrayRowIndexes[index]-1);
	                                                
	                        script = script + "indexesArray[" + index + "] = " + (arrayRowIndexes[index]-2) + ";";
	                        break;
	                    }                 
	                }
	            }
	            else
	            {
	            	 script = script + "indexesArray[" + index + "] = " + (arrayRowIndexes[index]-1) + ";";
	            }
	        }
	        boObject currentObject = this.getTargetObject();
	        if(currentObject != null){
	        	currentObject.setChanged(true);
	        }
	        
	        getRequestContext().getScriptContext().add(  
					XUIScriptContext.POSITION_HEADER,
					"autoSelectRowsUp",
					"window.setTimeout(function(){var grid = Ext.getCmp('" + oGrid.getClientId() + "');" +
					" if(grid != null) { var indexesArray = new Array(); " + script + " grid.getSelectionModel().selectRows(indexesArray);}},400);"
			); 
        }
    }
    
    
    /**
     * @throws boRuntimeException
     */
    public void orderDownBridge() throws boRuntimeException {
        
        GridPanel oGrid = (GridPanel)this.findParentComponent(GridPanel.class);
        bridgeHandler oBridgeHandler  = ((XEOBridgeListConnector)oGrid.getDataSource()).getBridge();
        
        int rowCount = oBridgeHandler.getRowCount();
        
        DataRecordConnector[] oSelectedRows = oGrid.getSelectedRows();
        
        if (oSelectedRows!=null && oSelectedRows.length>0)
        {
        
	        int[] arrayRowIndexes = new int[oSelectedRows.length];
	        
	        for (int i = 0; i < oSelectedRows.length; i++) 
	        {
	        	long rowBoui = ((BigDecimal)oSelectedRows[i].getAttribute("BOUI").getValue()).longValue();
	        	
	        	oBridgeHandler.beforeFirst();
	            
	            while (oBridgeHandler.next())
	            {
	                if (oBridgeHandler.getObject().getBoui() == rowBoui )
	                {
	                	arrayRowIndexes[i] = oBridgeHandler.getRow();
	                    break;
	                }                 
	            }
	        }
	        
	        Arrays.sort(arrayRowIndexes);
	        String script = new String();
	        for(int index=arrayRowIndexes.length-1; index >= 0 ; index--)
	        {            
	            if(arrayRowIndexes[index] > 0 && arrayRowIndexes[index] + 1 <= rowCount)
	            {
	                oBridgeHandler.beforeFirst();
	            	while (oBridgeHandler.next())
	                {
	                    if (oBridgeHandler.getRow() == arrayRowIndexes[index] )
	                    {                  	
	                        oBridgeHandler.moveRowTo(arrayRowIndexes[index]+1);
	                        
	                        script = "indexesArray[" + index + "] = " + (arrayRowIndexes[index]) + ";" + script;
	                        break;
	                    }                 
	                }
	            }
	            else
	            {
	            	 script = "indexesArray[" + index + "] = " + (arrayRowIndexes[index]-1) + ";" + script;
	            }
	        }
	        
	        boObject currentObject = this.getTargetObject();
	        if(currentObject != null){
	        	currentObject.setChanged(true);
	        }
	                
	        getRequestContext().getScriptContext().add(  
					XUIScriptContext.POSITION_HEADER,
					"autoSelectRowsDown",
					"window.setTimeout(function(){var grid = Ext.getCmp('" + oGrid.getClientId() + "');" +
					" if(grid != null) { var indexesArray = new Array(); " + script + " grid.getSelectionModel().selectRows(indexesArray);}},400);"
			);  
        }
        
    }
    
	@Override
	public void initComponent() {
		
		if( targetObject.isDefaultValue() )
			this.setTargetObject("#{" + getBeanId() + ".XEOObject}");
		
		super.initComponent();
		createToolBar();
		
		//Set the beanId for all children that are dynamically created
		List<UIComponent> children = getChildren();
		for (UIComponent child : children){
			setBeanIdOnChildren((XUIComponentBase)child, getBeanId());
		}
		
		
	}
	
	private void setBeanIdOnChildren(XUIComponentBase comp, String beanId){
		comp.setBeanId(beanId);
		List<UIComponent> children = comp.getChildren();
		for (UIComponent child : children){
			if (child instanceof XUIComponentBase)
				setBeanIdOnChildren(((XUIComponentBase)child),beanId);
		}
	}
	
	public void createToolBar() {
		
		Menu menu;
		List<UIComponent> children = getChildren();
		
		menu = createOrderButton(  "orderUpBridge", 
				XEOComponentMessages.BRIDGETB_MOVEUP.toString(),
				"ext-xeo/images/menus/orderUp-bridge.png", 
				getParent().getClientId( getFacesContext() ), 
				new OrderBrigeUpActionListener(), "self");
		
		menu = createOrderButton(  "orderDownBridge", 
				XEOComponentMessages.BRIDGETB_MOVEDOWN.toString(),
				"ext-xeo/images/menus/orderDown-bridge.png", 
				getParent().getClientId( getFacesContext() ), 
				new OrderBrigeDownActionListener(), "self");
		
		menu = addAddMenu();
		if( menu != null ) {
			children.add( currentMenuPos++, menu );
			children.add( currentMenuPos++, Menu.getMenuSpacer() );
		}

		menu = createViewerBeanMethod(  "remove", 
			XEOComponentMessages.BRIDGETB_REMOVE.toString(), 
			XEOComponentMessages.BRIDGETB_REMOVE_SELECTED.toString(),
			"ext-xeo/images/menus/remover-bridge.gif", 
			getParent().getClientId( getFacesContext() ), 
			"removeFromBridge", "self")
		;
	
		menu = addCreateNew();
		if( menu != null ) {
			children.add( currentMenuPos++, Menu.getMenuSpacer() );
			children.add( currentMenuPos++, menu );
			children.add( currentMenuPos++, Menu.getMenuSpacer() );
		}
		
		if (getRenderFavoritesBtn()){
			menu = createViewerBeanMethod(  "showFavoritesBridge", 
					XEOComponentMessages.BRIDGETB_FAVORITE.toString(), 
					XEOComponentMessages.BRIDGETB_FAVORITE_TTIP.toString(),
					"ext-xeo/icons/favorite.png", 
					getParent().getClientId( getFacesContext() ), 
					"showFavorite", "self")
				;
		}
	}
	
	@Override
	public void preRender() {
		
		//boolean renderToolBar = false;
		
		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		boolean separatorRendered = false;
		boolean isMySeparator 	  = false;
		for( UIComponent comp : getChildren() ) {
			if( comp instanceof ViewerMethod ) {
				isMySeparator = true; 
				separatorRendered = false;
				ViewerMethod viewerMethod = (ViewerMethod)comp;
				if( "addNewToBridge".equals( viewerMethod.getTargetMethod() ) ) {
					try
					{
						if (!targetBridge.getSelectedBoDef().getBoCanBeOrphan())
							separatorRendered = getRenderAddBtn() && XEOComponentStateLogic.isBridgeNewVisible( targetBridge );
						else						
							separatorRendered = getRenderCreateNewBtn() && XEOComponentStateLogic.isBridgeNewVisible( targetBridge );
					}
					catch (boRuntimeException e)
					{
						separatorRendered = getRenderCreateNewBtn() && XEOComponentStateLogic.isBridgeNewVisible( targetBridge );
					}
					viewerMethod.setVisible( Boolean.toString( separatorRendered ));
					viewerMethod.setDisabled( XEOComponentStateLogic.isBridgeNewEnabled(targetBridge) );
				}				
				else if( "lookupBridge".equals( viewerMethod.getTargetMethod() ) ) {
					separatorRendered = getRenderAddBtn() && XEOComponentStateLogic.isBridgeAddVisible( targetBridge );
					viewerMethod.setVisible( Boolean.toString( separatorRendered ) );
					viewerMethod.setDisabled( XEOComponentStateLogic.isBridgeAddEnabled(targetBridge) );
				}
				else if( "removeFromBridge".equals( viewerMethod.getTargetMethod() ) ) {
					separatorRendered = getRenderRemoveBtn() && XEOComponentStateLogic.isBridgeRemoveVisible( targetBridge ); 
					viewerMethod.setVisible( Boolean.toString( separatorRendered ));
					viewerMethod.setDisabled( XEOComponentStateLogic.isBridgeRemoveEnabled(targetBridge) );
				}
			}
			else if (comp instanceof Menu)
			{
				Menu menu =(Menu)comp;
				if( menu.getId()!=null && menu.getId().endsWith("orderUpBridge") ) {
					separatorRendered = getRenderOrderBridgeBtn();
					menu.setVisible( Boolean.toString( separatorRendered ) );
					menu.setDisabled( XEOComponentStateLogic.isBridgeOrderEnabled(targetBridge) );
				}
				else if( menu.getId()!=null && menu.getId().endsWith("orderDownBridge") ) {
					
					separatorRendered = getRenderOrderBridgeBtn();
					menu.setVisible( Boolean.toString( separatorRendered ) );
					menu.setDisabled( XEOComponentStateLogic.isBridgeOrderEnabled(targetBridge)  );
				}
			}
			else if ( isMySeparator && comp instanceof Menu ) {
				if( "-".equals( ((Menu) comp).getText() ) ) {
					((Menu) comp).setVisible( Boolean.toString( separatorRendered ) );
				}
			}
			else {
				isMySeparator = false;
			}
		}
		//setRendered( renderToolBar );
		
	}
	
	public boolean isOriginalQuery()
	{
		boolean toRet=true;
		Bridge bridge=(Bridge)this.findParentComponent(Bridge.class);
		if (!StringUtils.isEmpty(bridge.getCurrAggregateFieldCheckSet()) || 
				!StringUtils.isEmpty(bridge.getCurrAggregateFieldDescSet()) ||
				!StringUtils.isEmpty(bridge.getCurrAggregateFieldOpSet()) ||
				!StringUtils.isEmpty(bridge.getCurrAggregateFieldSet()) ||
				bridge.getCurrentFilterTerms()!=null ||
				!StringUtils.isEmpty(bridge.getCurrentFullTextSearch()) ||
				bridge.getCurrentSortTerms()!=null ||
				!StringUtils.isEmpty(bridge.getGroupBy())
				)
			toRet=false;
		
		return toRet;
	}
	
	
	private Menu addCreateNew() {
		BridgeMethod rootMenu;
		boDefHandler subClasseDef;

		
		rootMenu = null;

		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		if( targetBridge.getDefAttribute().getChildIsOrphan() ) {
			
			List<boDefHandler> subClassesDef = getObjectsForNewMenu();
			
			if( subClassesDef.size() > 0 ) {
				subClasseDef = subClassesDef.get( 0 );
				rootMenu = new BridgeMethod();
				rootMenu.setText( XEOComponentMessages.BRIDGETB_NEW.toString("") );
				rootMenu.setToolTip(  XEOComponentMessages.BRIDGETB_NEW.toString( subClasseDef.getLabel() ) );
				rootMenu.setId( getId() + "_new_" + subClasseDef.getName() );
				rootMenu.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
				rootMenu.setValue( subClasseDef.getName() );
				if (subClasseDef.getClassType() == boDefHandler.TYPE_CLASS ){
					rootMenu.setTargetMethod("addNewToBridge");
					rootMenu.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
				} else {
					rootMenu.setTargetMethod("dummy");
				}
				
				if( subClassesDef.size() > 1 ) {
					BridgeMethod viewerMethod = new BridgeMethod();
					if (subClasseDef.getClassType() == boDefHandler.TYPE_CLASS ){
						viewerMethod.setText( XEOComponentMessages.BRIDGETB_NEW.toString( subClasseDef.getLabel() ) );
						viewerMethod.setToolTip(  XEOComponentMessages.BRIDGETB_NEW.toString( subClasseDef.getLabel() ) );
						viewerMethod.setId( getId() + "_new1_" + subClasseDef.getName() );
						viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
						viewerMethod.setValue( subClasseDef.getName() );
						viewerMethod.setTargetMethod("addNewToBridge");
						viewerMethod.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
						rootMenu.getChildren().add( viewerMethod );
					} 
					
					for( int i=1; i < subClassesDef.size(); i++ ) {
						subClasseDef = subClassesDef.get( i );
						viewerMethod = new BridgeMethod();
						viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
						viewerMethod.setText(  XEOComponentMessages.BRIDGETB_NEW.toString( subClasseDef.getLabel() ) );
						
						viewerMethod.setId( getId() + "_new_" + subClasseDef.getName() );
						if (subClasseDef.getClassType() == boDefHandler.TYPE_CLASS ){
							viewerMethod.setTargetMethod( "addNewToBridge" );
							viewerMethod.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
						} else {
							viewerMethod.setTargetMethod("dummy");
						}
						viewerMethod.setValue( subClasseDef.getName() );
						
						rootMenu.getChildren().add( viewerMethod );
					}
				}
			}
		}
		return rootMenu;
	}

	private Menu addAddMenu() {
		
		BridgeMethod rootMenu;
		
		rootMenu = null;
		
		List<boDefHandler> subClassesDef = getObjectsForAddMenu();
		
		boDefHandler subClasseDef;
		
		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		
		
		if( subClassesDef.size() > 0 ) {
			subClasseDef = subClassesDef.get( 0 );
			
			rootMenu = new BridgeMethod();
			rootMenu.setText( XEOComponentMessages.BRIDGETB_ADD.toString("") );
			rootMenu.setToolTip( XEOComponentMessages.BRIDGETB_ADD.toString( subClasseDef.getLabel() ) );
			rootMenu.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
			rootMenu.setValue( subClasseDef.getName() );
			rootMenu.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
			
			if ( targetBridge.getDefAttribute().getChildIsOrphan( subClasseDef.getName() ) )
			{
				rootMenu.setId( getId() + "_add_" + subClasseDef.getName() );
				rootMenu.setTargetMethod( "lookupBridge" );
			}
			else
			{			
				rootMenu.setId( getId() + "_new_" + subClasseDef.getName() );
				rootMenu.setTargetMethod("addNewToBridge");
			}
			
			if( subClassesDef.size() > 1 ) {
				
				
				BridgeMethod viewerMethod = new BridgeMethod();
				viewerMethod.setText( subClasseDef.getLabel() );
				viewerMethod.setToolTip(XEOComponentMessages.BRIDGETB_ADD.toString( subClasseDef.getLabel() ));
				viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
				viewerMethod.setValue( subClasseDef.getName() );
				viewerMethod.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
				if (subClasseDef.getBoCanBeOrphan())
				{
					viewerMethod.setId( getId() + "_add1_" + subClasseDef.getName() );
					viewerMethod.setTargetMethod( "lookupBridge" );
				}
				else
				{
					viewerMethod.setId( getId() + "_new1_" + subClasseDef.getName() );
					viewerMethod.setTargetMethod("addNewToBridge");					
				}

				rootMenu.getChildren().add( viewerMethod );

				for( int i=1; i < subClassesDef.size(); i++ ) {
					subClasseDef = subClassesDef.get( i );
					
					viewerMethod = new BridgeMethod();
					viewerMethod.setIcon( "resources/" + subClasseDef.getName() + "/ico16.gif" );
					viewerMethod.setText( subClasseDef.getLabel() );
					viewerMethod.setValue( subClasseDef.getName() );
					viewerMethod.setToolTip(XEOComponentMessages.BRIDGETB_ADD.toString( subClasseDef.getLabel() ));
					viewerMethod.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
					if (subClasseDef.getBoCanBeOrphan())
					{
						viewerMethod.setId( getId() + "_add_" + subClasseDef.getName() );
						viewerMethod.setTargetMethod( "lookupBridge" );
					}
					else
					{
						viewerMethod.setId( getId() + "_new_" + subClasseDef.getName() );
						viewerMethod.setTargetMethod( "addNewToBridge" );
					}
					rootMenu.getChildren().add( viewerMethod );
				}
			}
		}
		
		//Generate tooltip for minimum and maximum
		boDefAttribute	defAttribute = targetBridge.getDefAttribute();
		rootMenu.setToolTip(getTooltipForAddButton(rootMenu.getToolTip(),defAttribute));
		
		return rootMenu;
		
	}
	
	private String getTooltipForAddButton(String originalTooltip, boDefAttribute bridgeDefinition){
		
		boolean hasMaxOccurs = bridgeDefinition.getMaxOccurs() != Integer.MAX_VALUE;
		boolean hasMinOccurs = bridgeDefinition.getMinOccurs() > 0;
		
		StringBuilder tooltip = new StringBuilder();
		tooltip.append(originalTooltip);
		
		if (hasMaxOccurs  || hasMinOccurs){
			tooltip.append(" - ");
			
			if (hasMinOccurs){
				tooltip.append(" ");
				tooltip.append(XEOComponentMessages.BRIDGETB_ADD_MINIMUM.toString());
				tooltip.append(" ");
				tooltip.append(bridgeDefinition.getMinOccurs());
				if (hasMaxOccurs)
					tooltip.append(", ");
			}
			if (hasMaxOccurs){
				tooltip.append(XEOComponentMessages.BRIDGETB_ADD_MAXIMUM.toString());
				tooltip.append(" ");
				tooltip.append(bridgeDefinition.getMaxOccurs());
			}
		}	
		return tooltip.toString();
	}
	
	
	private List<boDefHandler> getObjectsForAddMenu() {
		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		assert targetBridge != null : targetObject.getName() + " does not have a bridge with name " + getBridgeName();
		
		boDefAttribute	defAttribute = targetBridge.getDefAttribute(); 
		boDefHandler refDef = defAttribute.getReferencedObjectDef();

		List<boDefHandler> subClassesDef = new ArrayList<boDefHandler>(1);

		if( defAttribute.getObjects() != null && defAttribute.getObjects().length > 0 ) {
			for( boDefHandler def : Arrays.asList( defAttribute.getObjects() ) ) {
				subClassesDef.add( def );
			}
		}
		else {
			boDefHandler[] subDefs = refDef.getTreeSubClasses();
			subClassesDef.add( refDef );
			for( boDefHandler subDef : subDefs ) { 
				subClassesDef.add( subDef );
			}
		}
		return subClassesDef;
	}

	private List<boDefHandler> getObjectsForNewMenu() {
		boObject 		targetObject = getTargetObject();
		bridgeHandler 	targetBridge = targetObject.getBridge( getBridgeName() );
		
		boDefAttribute	defAttribute = targetBridge.getDefAttribute(); 
		boDefHandler refDef = defAttribute.getReferencedObjectDef();

		List<boDefHandler> subClassesDef = new ArrayList<boDefHandler>(1);
		if( defAttribute.getChildIsOrphan() ) {
			if( defAttribute.getObjects() != null && defAttribute.getObjects().length > 0 ) {
				for( boDefHandler def : Arrays.asList( defAttribute.getObjects() ) ) {
					if( def.getBoCanBeOrphan() )
						subClassesDef.add( def );
				}
			}
			else {
				if (defAttribute.getChildIsOrphan()) {
					subClassesDef.add( refDef );
				}
				else {
					return ListToolBar.getObjectsForNewMenu( refDef );
				}
//				boDefHandler[] subDefs = refDef.getTreeSubClasses();
//				if( refDef.getBoCanBeOrphan() )
//					subClassesDef.add( refDef );
//				for( boDefHandler subDef : subDefs ) { 
//					if( subDef.getBoCanBeOrphan() )
//						subClassesDef.add( subDef );
//				}
			}
		}
		return subClassesDef;
	}
	
	private Menu createViewerBeanMethod( String id, String label, String toolTip, String icon, String value, String action, String target ) {
		ViewerMethod toolBarOpt;
		toolBarOpt = new ViewerMethod();
		toolBarOpt.setId( getId() + "_" + id );
		
		if( target != null ) {
			toolBarOpt.setTarget( target );
		}
		toolBarOpt.setValue( value );
		toolBarOpt.setText( label );
		toolBarOpt.setTargetMethod( action );
		toolBarOpt.setIcon( icon );
		toolBarOpt.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
		toolBarOpt.setValue( findParentComponent(GridPanel.class).getClientId() );
		getChildren().add( currentMenuPos++, toolBarOpt );
		return toolBarOpt;
	}
	
	private Menu createOrderButton( String id, String toolTip, String icon, String value, ActionListener action, String target ) {
		Menu menu;
		menu = new Menu();
		menu.setId( getId() + "_" + id );
		
		if( target != null ) {
			menu.setTarget( target );
		}
		menu.setValue( value );
		menu.addActionListener(action);
		menu.setToolTip(toolTip);
		menu.setIcon( icon );
		menu.setServerActionWaitMode( XVWServerActionWaitMode.DIALOG.toString() );
		getChildren().add( currentMenuPos++, menu );
		return menu;
	}
}
