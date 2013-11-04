package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.ID;
import static netgest.bo.xwc.components.HTMLTag.DIV;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.preferences.Preference;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.security.securityRights;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.components.classic.grid.GridPanelExtJsRenderer;
import netgest.bo.xwc.components.classic.grid.GridPanelRenderer;
import netgest.bo.xwc.components.classic.gridexplorer.XVWGridExplorerViewEditBean;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.components.util.ScriptBuilder;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMessage;
import netgest.bo.xwc.framework.XUIPreferenceManager;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIForm;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.components.List;
import netgest.bo.xwc.xeo.components.ListToolBar;
import netgest.bo.xwc.xeo.localization.XEOComponentMessages;
import netgest.utils.StringUtils;

public class GridExplorer extends List {
	
	
	@Override
	public String getRendererType() {
		return "gridExplorer";
	}
	
	public static enum PreviewPanelPosition {
		LEFT,
		RIGHT,
		BOTTOM,
		OFF
	}
	
	public static enum PreviewPanelMode {
		PREVIEW,
		EDIT
	}
	
	/**
	 * The Position of the Preview Panel ( OFF, LEFT, RIGHT, BOTTOM)
	 */
	@Values({"OFF","LEFT","RIGHT","BOTTOM"})
	private XUIViewProperty<PreviewPanelPosition> previewPanelPosition = 
		new XUIViewProperty<PreviewPanelPosition>("previewPanelPosition", this, PreviewPanelPosition.OFF );
	
	/**
	 * Whether or not to show the preview panel 
	 */
	private XUIViewProperty<Boolean> enablePreviewPanel = 
		new XUIViewProperty<Boolean>("enablePreviewPanel", this, true );
	
	/**
	 * Whether or not to show the advanced search button
	 */
	private XUIViewProperty<Boolean> enableAdvancedSearch = 
		new XUIViewProperty<Boolean>("enableAdvancedSearch", this, false );

	/**
	 * Whether or not to show the save view button
	 */
	private XUIViewProperty<Boolean> enableSaveView = 
		new XUIViewProperty<Boolean>("enableSaveView", this, true );

	/**
	 * Configures the height of the preview panel
	 */
	private XUIViewProperty<Integer> previewPanelHeight = 
		new XUIViewProperty<Integer>("previewPanelHeight", this, 250 );

	/**
	 * Sets the mode of the preview panel (currently only works in preview)
	 */
	private XUIViewProperty<PreviewPanelMode> previewPanelMode = 
		new XUIViewProperty<PreviewPanelMode>("previewPanelMode", this, PreviewPanelMode.PREVIEW );

	/**
	 * Configures the preview panel with
	 */
	private XUIViewProperty<Integer> previewPanelWidth = 
		new XUIViewProperty<Integer>("previewPanelWidth", this, 350 );
	
	private XUIViewStateProperty<String> currentSavedViewId =
		new XUIViewStateProperty<String>("currentSavedViewId", this );

	private XUIViewStateProperty<Long> currentSavedViewBOUI =
		new XUIViewStateProperty<Long>("currentSavedViewBOUI", this );
	
	/**
	 * Configures the command to create the preview
	 */
	private XUIBaseProperty<String> previewCommand = 
		new XUIBaseProperty<String>("previewCommand", this);
	
	/**
	 * The identifier of a toolbar in the viewer to merge with the gridexplorer
	 * toolbar
	 */
	private XUIViewStateProperty<String> renderExplorerOnToolBar =
		new XUIViewStateProperty<String>("renderExplorerOnToolBar", this );
	
	private XUIBindProperty<Boolean> renderViewPort = 
		new XUIBindProperty<Boolean>("renderViewPort", this, true, Boolean.class );
	
	public boolean getEnableAdvancedSearch(){
		return enableAdvancedSearch.getValue();
	}
	
	public void setEnableAdvancedSearch(boolean newValue){
		this.enableAdvancedSearch.setValue( newValue );
	}
	
	/**
	 * Whether or not to show the save view button
	 */
	private XUIViewProperty<Boolean> enableListSavedViews = 
		new XUIViewProperty<Boolean>("enableListSavedViews", this, true );
	
	public boolean getEnableListSavedViews(){
		return enableListSavedViews.getValue();
	}
	
	public void setEnableListSavedViews(boolean listSavedViews){
		enableListSavedViews.setValue( listSavedViews );
	}
	
	private boolean restoreFiltersState = false;
	private boolean restoreViewState = false;
	private XUIBaseProperty<Boolean> isSavedViewOwner = new XUIBaseProperty<Boolean>("isSavedViewOwner", this, true );
	
	private boolean isNew = false;
	
	public void setRenderExplorerOnToolBar( String toolbarId ) {
		this.renderExplorerOnToolBar.setValue( toolbarId ); 
	}
	
	public String getRenderExplorerOnToolBar() {
		return this.renderExplorerOnToolBar.getValue();
	}
	
	public void setPreviewPanelMode( String previewPanelMode ) {
		this.previewPanelMode.setValue( PreviewPanelMode.valueOf( previewPanelMode ) );
	}

	public void setPreviewPanelMode( PreviewPanelMode previewPanelMode ) {
		this.previewPanelMode.setValue(  previewPanelMode );
	}
	
	
	
	public PreviewPanelMode getPreviewPanelMode() {
		return this.previewPanelMode.getValue();
	}

	public void setPreviewPanelHeight( int height ) {
		this.previewPanelHeight.setValue( height );
	}

	public int getPreviewPanelHeight() {
		return this.previewPanelHeight.getValue();
	}

	public void setPreviewPanelWidth( int height ) {
		this.previewPanelWidth.setValue( height );
	}

	public int getPreviewPanelWidth() {
		return this.previewPanelWidth.getValue();
	}

	public void setEnableSaveView( boolean enableSaveView ) {
		this.enableSaveView.setValue( enableSaveView );
	}

	public boolean getEnableSaveView() {
		return this.enableSaveView.getValue();
	}
	
	public void setEnablePreviewPanel( boolean previewPanel ) {
		this.enablePreviewPanel.setValue( previewPanel );
	}

	public void setPreviewPanelPosition( String position ) {
		this.previewPanelPosition.setValue( PreviewPanelPosition.valueOf( position.toUpperCase() ) );
	}

	public void setPreviewPanelPosition( PreviewPanelPosition position ) {
		this.previewPanelPosition.setValue( position );
	}

	public PreviewPanelPosition getPreviewPanelPosition() {
		return this.previewPanelPosition.getValue();
	}
	
	public boolean getEnablePreviewPanel() {
		return this.enablePreviewPanel.getValue();
	}
	
	public String getPreviewCommand() {
		return this.previewCommand.getValue();
	}
	
	public void setPreviewCommand( String previewCommand ) {
		this.previewCommand.setValue( previewCommand );
	}
	
	public void setCurrentSavedViewId( String viewId ) {
		if( viewId != null && viewId.length() == 0 ) {
			viewId = null;
		}
		this.currentSavedViewId.setValue( viewId );
	}

	public String getCurrentSavedViewId() {
		return this.currentSavedViewId.getValue();
	}

	
	
	public void setCurrentSavedViewBOUI( Long boui ) {
		if( boui != null && boui == 0 ) {
			boui = null;
		}
		
		if( boui != null ) {
			try {
				boObject viewObj = boObject.getBoManager().loadObject(
						boApplication.currentContext().getEboContext(),
						boui
					);
				
				String name = viewObj.getAttribute("name").getValueString();
				
				this.isSavedViewOwner.setValue( viewObj.getAttribute("CREATOR").getValueLong() == 
						viewObj.getEboContext().getSysUser().getBoui() );
				
				this.restoreFiltersState = "1".equals( viewObj.getAttribute("saveFilters").getValueString() );
				this.restoreViewState = "1".equals( viewObj.getAttribute("saveView").getValueString() );
				
				setCurrentSavedViewId( name );
			} catch (boRuntimeException e) {
				throw new RuntimeException(e);
			}
		}
		else {
			setCurrentSavedViewId( null );
		}
		this.currentSavedViewBOUI.setValue( boui );
	}

	public Long getCurrentSavedViewBOUI() {
		return this.currentSavedViewBOUI.getValue();
	}
	
	public void setRenderViewPort( boolean renderViewPort ) {
		this.renderViewPort.setValue( renderViewPort );
	}

	public void setRenderViewPort( String renderViewPortExpr ) {
		this.renderViewPort.setValue( renderViewPortExpr );
	}
	
	public boolean getRenderViewPort() {
		return this.renderViewPort.getEvaluatedValue();
	}
	
	public String getExplorerId() {
		String sStateName = getGridStateName();
		if( "default".equals( sStateName ) ) {
			sStateName = XUIRequestContext.getCurrentContext().getViewRoot().getViewId() + "_" + getGridStateName();
		}
		return sStateName;
	}

	@Override
	public Preference getUserSatePreference() {
		String stateName 	= getGridStateName();
		String saveViewId	= getCurrentSavedViewId();
		if( saveViewId != null ) {
			stateName += "_" + saveViewId;
		}
		
		Preference p = XUIPreferenceManager.getUserPreference(
				GridPanel.class.getName() + ".state" , 
				stateName
		);
		
		if( saveViewId != null ) {
			Preference defaultPreferences = XUIPreferenceManager.getSystemPreference(
					GridPanel.class.getName() + ".state", 
					stateName
			);
			Iterator<String> keys = defaultPreferences.getKeys();
			while( keys.hasNext() ) {
				String key = keys.next();
				//if( p.containsPreference( key )  ) {
					p.put(key,  defaultPreferences.get(key) );
				//}
			}
		}
		return p;
	}

	public Preference getExplorerUserSatePreference() {
		String stateName 	= getGridStateName();
		Preference p = XUIPreferenceManager.getUserPreference(
				GridExplorer.class.getName() + ".state", 
				stateName
		);
		return p;
	}
	
	@Override
	public void saveUserState() {
		saveUserState( false );
	}
	
	public void saveUserState( boolean force ) {
		
		Preference preference = getUserSatePreference();
		
		super.saveUserState();
		
		if( getCurrentSavedViewId() == null || force ) {
			String stateName 	= getGridStateName();
			String saveViewId	= getCurrentSavedViewId();
			if( saveViewId != null ) {
				stateName += "_" + saveViewId;
			}
			Preference defaultPreferences = XUIPreferenceManager.getSystemPreference(
					GridPanel.class.getName() + ".state",
					stateName
			);
			Iterator<String> keys = preference.getKeys();
			while( keys.hasNext() ) {
				String key = keys.next();
				defaultPreferences.put(key,  preference.get(key) );
			}
			defaultPreferences.savePreference();
		}
		else {
			super.saveUserExpandedGroupsState(preference);
		}
		
		Preference expPref = getExplorerUserSatePreference();
		saveUserExplorerState( expPref );
		expPref.savePreference();
	}
	
	
	public static class RemoveSearchActionListener implements ActionListener {
        public void processAction(ActionEvent event) {
        	GridExplorer explorer = (GridExplorer)((XUICommand)event.getSource()).findParentComponent( GridExplorer.class ); 
        	explorer.setAdvancedFilters( null );
        	explorer.markAdvancedSearchInactive();
        }
    }
	
	@Override
	public void restoreUserState() {
		Long savedViewBoui = getCurrentSavedViewBOUI();
		if( savedViewBoui != null ) {
			try {
				boObject b = getUserStateObject( savedViewBoui );
				if( b != null ) {
					this.restoreFiltersState = "1".equals(b.getAttribute("saveFilters").getValueString() );
					this.restoreViewState = "1".equals(b.getAttribute("saveView").getValueString() );
				}
			} catch (boRuntimeException e) {
				throw new RuntimeException( e );
			}
		}
		super.restoreUserState();
	}
	
	@Override
	public void resetToDefaults() {
		setCurrentSavedViewBOUI(null);
		setAdvancedFilters( null );
		super.resetToDefaults();
	}
	
	public boObject _getUserStateObject( String savedViewId ) {
		try {
			EboContext ctx = boApplication.currentContext().getEboContext();
			boObjectList list = boObjectList.list(
					boApplication.currentContext().getEboContext(),
					"select XVWGridExplorerView where name=? and CREATOR=?",
					new Object[] { savedViewId, ctx.getSysUser().getBoui() }
			);
			list.poolDestroyObject();
			if( list.next() ) {
				boObject b = list.getObject();
				return b;
			}
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
		return null;
	}

	public boObject getUserStateObject( Long boui ) {
		try {
			EboContext ctx = boApplication.currentContext().getEboContext();
			boObjectList list = boObjectList.list(
					ctx,
					"select XVWGridExplorerView where BOUI=?",
					new Object[] { boui }
			);
			list.poolDestroyObject();
			if( list.next() ) {
				boObject b = list.getObject();
				return b;
			}
		} catch (boRuntimeException e) {
			throw new RuntimeException( e );
		}
		return null;
	}
	
	public boObjectList getUserStateObjectList( String explorerId ) {
		EboContext ctx = boApplication.currentContext().getEboContext();
		
		StringBuilder sbkeys = new StringBuilder();

		try {
			long[] keys = securityRights.getPerformerKeys(  
					boApplication.currentContext().getEboContext()
			);

			for (long key : keys) {
				if (sbkeys.length() > 1)
					sbkeys.append(',');
				sbkeys.append(key);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (sbkeys.length() == 0) {
			sbkeys.append('0');
		}
		
		boObjectList list = boObjectList.list( 
				ctx,
				"select XVWGridExplorerView where gridExplorerId=? and " +
				"( CREATOR = ? OR " +
				" share in (" + sbkeys + ") ) " +
				" AND 0 <  " + System.currentTimeMillis() + 
				" order by name",
				new Object[] { explorerId, ctx.getSysUser().getBoui() }
			);
		return list;
	}
	
	
	public void restoreUserExplorerState( Preference p ) {
		String previewPanelPos = p.getString( "PreviewPanelPosition" );
		if( previewPanelPos != null )
			this.setPreviewPanelPosition(  previewPanelPos );


		Long currentSavedViewBOUI = p.getLong( "CurrentSavedViewBOUI" );
		if( currentSavedViewBOUI != 0 )
			this.setCurrentSavedViewBOUI( currentSavedViewBOUI );

		String advancedFilters = p.getString( "AdvancedFilters" );
		this.setAdvancedFilters( advancedFilters );
		if (StringUtils.hasValue( advancedFilters ))
			markAdvancedSearchActive();
		else
			markAdvancedSearchInactive();
		
		if( currentSavedViewBOUI == 0 ) {
			String currentSavedViewId = p.getString( "CurrentSavedViewId" );
			if( currentSavedViewId != null && currentSavedViewId.length() > 0 ) {
				this.setCurrentSavedViewId( currentSavedViewId );
				boObject o = _getUserStateObject( currentSavedViewId );
				if( o != null ) {
					this.setCurrentSavedViewBOUI( o.getBoui() );
				}
				else {
					this.setCurrentSavedViewId( null );
					this.setCurrentSavedViewBOUI( null );
				}
			}
		}
	}
	
	public void saveUserExplorerState( Preference p ) {
		super.saveUserViewState(p);
		p.setString("PreviewPanelPosition", this.getPreviewPanelPosition().name() );
		p.setString("CurrentSavedViewId", this.getCurrentSavedViewId() );
		String advancedFilters = this.getAdvancedFilters();
		p.setString( "AdvancedFilters", advancedFilters );
		
		if( this.getCurrentSavedViewBOUI() != null )
			p.setLong("CurrentSavedViewBOUI", this.getCurrentSavedViewBOUI() );
		else
			p.setLong("CurrentSavedViewBOUI", 0 );
	}
	
	@Override
	public void initComponent() {
		
		if( previewCommand.isDefaultValue() )
			this.setPreviewCommand("#{" + getBeanId() + ".previewObject}");
		
		if( super.getStateProperty( "rowSelectionMode" ).isDefaultValue() )
			setRowSelectionMode( GridPanel.SELECTION_CELL );
		
		if( super.getStateProperty( "onRowDoubleClick" ).isDefaultValue() )
			setOnRowDoubleClick( "#{" + getBeanId() + ".rowDoubleClick}" );

//		if( super.getStateProperty( "forceColumnsFitWidth" ).isDefaultValue() ) {
//			setForceColumnsFitWidth( "true" );
//			setAutoExpandColumn(null);
//		}
		
		ErrorMessages errM = (ErrorMessages)findParentComponent( ErrorMessages.class );
		if( errM == null ) {
			getParent().getChildren().add( new ErrorMessages() );
		}
		
		setAutoReloadData(false);


		if ( super.getStateProperty("enableGroupBy").isDefaultValue() ) {
			super.setEnableGroupBy( Boolean.toString( true ) );
		}

		if ( super.getStateProperty("autoSaveGridState").isDefaultValue() ) {
			super.setAutoSaveGridState( Boolean.toString( true ) );
		}
		
		Preference expPref = getExplorerUserSatePreference();
		restoreUserExplorerState( expPref );
		
		super.initComponent();

		// Preview Command 
		XUICommand previewCmd = new XUICommand();
		previewCmd.setId( getId() + "_pvwcmd" );
		previewCmd.setActionExpression( createMethodBinding( previewCommand.getValue() ) );
		getChildren().add( previewCmd );
		
		XUICommand setViewCmd = new XUICommand();
		setViewCmd.setId( getId() + "_setviewcmd" );
		setViewCmd.addActionListener( new SetViewListener() );
		getChildren().add( setViewCmd );
		createToolBar();
		
		
		this.isNew = true;
	}
		
	@Override
	public void preRender() {
		super.preRender();
		if( this.currentSavedViewId.wasChanged() ) 
		{
			UIComponent m;
			int cmpIndex;
			ToolBar tb = getToolBar();

			m = findComponent( getId() + "_saveView" );
			cmpIndex = tb.getChildren().indexOf( m );
			tb.getChildren().remove( cmpIndex );
			tb.getChildren().add( cmpIndex, createSaveMenu() );
			
			m = findComponent( getId() + "_savedViews" );
			cmpIndex = tb.getChildren().indexOf( m );
			tb.getChildren().remove( cmpIndex );
			tb.getChildren().add( cmpIndex, createViewsCombo()  );
			
			if( !getRenderViewPort() ) {
				forceRenderOnClient();
			}
		}
		
	}
	
	
	
	public ToolBar getToolBar() {
		ToolBar tb;
		String toolBarId = getRenderExplorerOnToolBar();
		if( toolBarId != null ) {
			tb = (ToolBar)getRequestContext()
				.getViewRoot().findComponent( toolBarId );
		}
		else {
			tb =  (ToolBar)findComponent( getId() + "_tb" );
		}
		return tb;
	}
	
	
	@Override
	public void createToolBar(int pos) {
		// Disable default List toolbar
	}
	
	private void createToolBar() {
		
		ToolBar tb = null;
		
		String toolBarId = getRenderExplorerOnToolBar(); 
		if( toolBarId != null ) {
			tb = (ToolBar)getRequestContext()
				.getViewRoot().findComponent( toolBarId );
		}
		else {
			if( this.getRenderToolBar() ) {
				tb = new ListToolBar();
			}
			else {
				tb = new ToolBar();
			}
			tb.setId( getId() + "_tb"  );
		}
		
		// Menu Preview
		
		if( getEnablePreviewPanel() && getRenderViewPort() ) {
		
			Menu pvw = new PreviewPositionMenu();
			//pvw.setText("Painel de Leitura");
			pvw.setIcon("ext-xeo/images/gridexplorer/prev-bott.gif");
			pvw.setId( getId() + "_mpvw" );
			pvw.setToolTip( XEOComponentMessages.EXPLORER_TOOLBAR_PREVIEW_TOOLTIP.toString() );
			
			
			
			PreviewPanelPosition 	p = getPreviewPanelPosition();
			
			PreviewPositionMenu pvwb = new PreviewPositionMenu();
			pvwb.setText( XEOComponentMessages.EXPLORER_PREVIEW_BOTTOM.toString() );
			pvwb.setValue( PreviewPanelPosition.BOTTOM == p );
			pvwb.setGroup( "preview" );
			pvwb.setPosition( "BOTTOM" );
			pvwb.setId( getId() + "_mpvwb" );
			pvw.getChildren().add( pvwb );
	
			PreviewPositionMenu pvwr = new PreviewPositionMenu();
			pvwr.setText( XEOComponentMessages.EXPLORER_PREVIEW_RIGHT.toString() );
			pvwr.setValue( PreviewPanelPosition.RIGHT == p );
			pvwr.setGroup( "preview" );
			pvwr.setPosition( "RIGHT" );
			pvwr.setId( getId() + "_mpvwr" );
			pvw.getChildren().add( pvwr );
	
			PreviewPositionMenu pvwl = new PreviewPositionMenu();
			pvwl.setText( XEOComponentMessages.EXPLORER_PREVIEW_LEFT.toString() );
			pvwl.setValue( PreviewPanelPosition.LEFT == p );
			pvwl.setGroup( "preview" );
			pvwl.setPosition( "LEFT" );
			pvwl.setId( getId() + "_mpvwl" );
			pvw.getChildren().add( pvwl );
			
			
			tb.getChildren().add( pvw );
			tb.getChildren().add( Menu.getMenuSpacer() );
	
		}
		
		if (getEnableSaveView()){
			tb.getChildren().add( createSaveMenu() );
			tb.getChildren().add( Menu.getMenuSpacer() );
		}
		
		if (getEnableListSavedViews()){
			tb.getChildren().add( createViewsCombo() );
			tb.getChildren().add( Menu.getMenuSpacer() );
		}
		
		if (getEnableAdvancedSearch())
			createAdvancedSearchButton( tb );
		
		getChildren().add( tb );
	}
	
	public void markAdvancedSearchActive(){
		Menu m = (Menu) this.findComponent( getId() + "_advSearch" );
		if (m != null){
			m.setText("<span style=\"font-weight:bold;font-style:italic;\">"+XEOComponentMessages.EXPLORER_TOOLBAR_ADVANCED_SEARCH.toString()+"</span>");
			((Menu)m.getChild(0)).setDisabled( false );
		}
	}
	
	public void markAdvancedSearchInactive(){
		Menu m = (Menu) this.findComponent( getId() + "_advSearch" );
		if (m != null){
			m.setText(XEOComponentMessages.EXPLORER_TOOLBAR_ADVANCED_SEARCH.toString());
			((Menu)m.getChild(0)).setDisabled( true );
		}
	}
	

	private void createAdvancedSearchButton( ToolBar tb ) {
		DataListConnector connector = getDataSource();
		if (connector instanceof XEOObjectListConnector){
			Menu m = new Menu();
			
			m.setText( XEOComponentMessages.EXPLORER_TOOLBAR_ADVANCED_SEARCH.toString() );
			m.setId(getId() + "_advSearch");
			m.setServerAction("#{" + getBeanId() + ".advancedSearch}");
			m.setServerActionWaitMode(XVWServerActionWaitMode.DIALOG.toString());
			m.setIcon( "ext-xeo/images/gridexplorer/search.png" );
			
			Menu removeSearch = new Menu();
			removeSearch.setText( XEOComponentMessages.EXPLORER_TOOLBAR_REMOVE_ADVANCED_SEARCH.toString() );
			removeSearch.setIcon( "ext-xeo/images/gridexplorer/remove.png" );
			removeSearch.setServerActionWaitMode(XVWServerActionWaitMode.DIALOG.toString());
			removeSearch.addActionListener( new RemoveSearchActionListener() );
			m.getChildren().add( removeSearch );
			((Menu)m.getChild(0)).setDisabled( true );
			
			try{
				XEOObjectListConnector objConnector = (XEOObjectListConnector) connector;
				String name = objConnector.getObjectList().getBoDef().getName();
				m.setValue(name);
				tb.getChildren().add( m );
			} catch (boRuntimeException e){
				e.printStackTrace();
			}
		}
	}

	private Menu createSaveMenu( ) {
		// Menu Preview
		Menu save = new Menu();
		save.setId( getId() + "_saveView" );
		save.setIcon("ext-xeo/images/gridexplorer/gravar.gif");
		save.setToolTip(XEOComponentMessages.EXPLORER_SAVEVIEW_TOOLTIP.toString());
		
		save.addActionListener( new SaveViewListener() );

//		Menu autoSaveView = new Menu();
//		autoSaveView.setId( getId() + "_autoSaveView" );
//		autoSaveView.setText("Guardar Automaticamente");
//		autoSaveView.setValue( true );
//		autoSaveView.addActionListener( new SaveViewListener() );
		
		Menu deleteView = new Menu();
		deleteView.setId( getId() + "_deleteView" );
		deleteView.setIcon("ext-xeo/images/gridexplorer/delete.jpg");
		deleteView.setText(XEOComponentMessages.EXPLORER_DELETE_VIEW.toString());
		deleteView.addActionListener( new SaveViewListener() );
		if( getCurrentSavedViewId() == null || !isSavedViewOwner.getValue() ) {
			deleteView.setDisabled(true);
		}
		save.getChildren().add( deleteView );
			
		Menu editView = new Menu();
		editView.setId( getId() + "_editView" );
		editView.setText(XEOComponentMessages.EXPLORER_EDIT_VIEW.toString());
		editView.addActionListener( new SaveViewListener() );
		if( getCurrentSavedViewId() == null || !isSavedViewOwner.getValue() ) {
			editView.setDisabled(true);
		}
		save.getChildren().add( editView );

		if( getCurrentSavedViewId() != null ) {
			Menu saveNewView = new Menu();
			saveNewView.setId( getId() + "_saveNewView" );
			saveNewView.setText(XEOComponentMessages.EXPLORER_SAVENEW_VIEW.toString());
			saveNewView.addActionListener( new SaveViewListener() );
			save.getChildren().add( saveNewView );
		}
		
//		save.getChildren().add( autoSaveView );

		
		
		return save;
		
	}
	
	public static class SetViewListener implements ActionListener {

		@Override
		public void processAction(ActionEvent action) throws AbortProcessingException {
			GridExplorer explorer = (GridExplorer)action.getComponent().getParent();
			XUIRequestContext oRequestContext 
				= XUIRequestContext.getCurrentContext();
			//v135_j_id0:v135_j_id3_cvnh
			//v135_j_id0:v135_j_id3_cvnh			
			String hiddeName = explorer.getClientId() + "_cvnh";
			if( explorer.getRenderViewPort() ) {
				hiddeName = explorer.getClientId() + "_cvnh1";
			}
			HttpServletRequest request = (HttpServletRequest)oRequestContext.getRequest();
			String[] viewIdName = request.getParameterValues( hiddeName );
			String viewBoui = viewIdName[viewIdName.length-1];
			if( viewBoui != null && viewBoui.length() > 0 )
				explorer.setCurrentSavedViewBOUI( Long.parseLong(viewBoui) );
			else
				explorer.setCurrentSavedViewBOUI( null );
			
			explorer.restoreUserState();
		}
	}
	
	
	
	public static class SaveViewListener implements ActionListener {

		public void processAction(ActionEvent action)  {
			
			GridExplorer explorer = 
					(GridExplorer)((XUIComponentBase)action.getComponent()).
						findParentComponent(GridExplorer.class);
			
			XUIRequestContext oRequestContext 
				= XUIRequestContext.getCurrentContext();

			String menuId = action.getComponent().getId();
			
			if( menuId.endsWith( "_saveView" ) || menuId.endsWith("_saveNewView") ) {
				if( 
						(menuId.endsWith("_saveNewView") || explorer.getCurrentSavedViewId() == null)
						||
						(menuId.endsWith("_saveView") && explorer.getCurrentSavedViewId() != null && !explorer.isSavedViewOwner.getValue() )
				) {
					
					if( menuId.endsWith("_saveView") && explorer.getCurrentSavedViewId() != null && !explorer.isSavedViewOwner.getValue() ) {
						oRequestContext.addMessage( 
								explorer.getClientId(),
								new XUIMessage( 
										XUIMessage.TYPE_POPUP_MESSAGE, 
										XUIMessage.SEVERITY_INFO, 
										XEOComponentMessages.EXPLORER_SAVE_ERROR_TITLE.toString(), 
										XEOComponentMessages.EXPLORER_ERROR_VIEW_CREATED_BY_OTHER.toString()
								)
							);
					}
					XUIViewRoot saveView = oRequestContext.getSessionContext().
						createChildView("netgest/bo/xwc/components/classic/gridexplorer/XVWGridExplorerView_edit.xvw");
					XVWGridExplorerViewEditBean saveViewBean = (XVWGridExplorerViewEditBean)saveView.getBean("viewBean");
					saveViewBean.createExplorerView( explorer.getClientId(), explorer.getExplorerId() );
					saveViewBean.setEditInOrphanMode( true );
					oRequestContext.setViewRoot( saveView );
					
				}
				else {
					oRequestContext.addMessage( 
							explorer.getClientId(),
							new XUIMessage( 
									XUIMessage.TYPE_POPUP_MESSAGE, 
									XUIMessage.SEVERITY_INFO, 
									XEOComponentMessages.EXPLORER_SAVE_SUCCESS.toString(), 
									XEOComponentMessages.EXPLORER_VIEW_SAVED.toString()
							)
						);
					explorer.saveUserState( true );
				}
			}
			else if( menuId.endsWith( "_editView" ) ) {
				
				XUIViewRoot saveView = oRequestContext.getSessionContext().
					createChildView("netgest/bo/xwc/components/classic/gridexplorer/XVWGridExplorerView_edit.xvw");
			
				XVWGridExplorerViewEditBean saveViewBean = 
					(XVWGridExplorerViewEditBean)saveView.getBean("viewBean");
				
				saveViewBean.setCurrentObjectKey(
						Long.toString(
							explorer.getUserStateObject( explorer.getCurrentSavedViewBOUI() ).getBoui()
						)
					);
				
				saveViewBean.setExplorerComponentId( explorer.getClientId() );
				saveViewBean.setEditInOrphanMode( true );
				oRequestContext.setViewRoot( saveView );
				
			}
			else if( menuId.endsWith( "_autoSaveView" ) ) {
				
			}
			else if( menuId.endsWith( "_deleteView" ) ) {
				try {
					Long viewBoui = explorer.getCurrentSavedViewBOUI();
					
					boObject stateObj = explorer.getUserStateObject( viewBoui );
					if( stateObj != null ) {
						stateObj.destroy();
					}
					explorer.setCurrentSavedViewBOUI(null);
					explorer.restoreUserState();
					
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else {
				
			}
		}
	}
	
	public XUIComponentBase createViewsCombo() {
		try {
			
			XUIExtJsComponent extJsComp;
			extJsComp = new XUIExtJsComponent();	
			extJsComp.setId( getId() + "_savedViews" );
			
			ScriptBuilder valuesStore = new ScriptBuilder();
			valuesStore.w( "new Ext.data.SimpleStore(");
			ExtConfig valuesConfig = new ExtConfig();
			ExtConfigArray fields = valuesConfig.addChildArray( "fields");
			fields.addString( "i" );
			fields.addString( "d" );
			ExtConfigArray dataArray = valuesConfig.addChildArray( "data" );
			 
			String gridExplorerId = getExplorerId();
			
			boObjectList list = getUserStateObjectList( gridExplorerId );
			ExtConfigArray row = new ExtConfigArray();
			row.addString( "" );
			row.add( "\""+XEOComponentMessages.EXPLORER_VIEW_CURRENT.toString()+"\"" );
			dataArray.add( row );
			
			long userBoui = list.getEboContext().getSysUser().getBoui();

			while( list.next() ) {
				boObject viewObject = list.getObject();
				row = new ExtConfigArray();
				row.addString( viewObject.getAttribute("BOUI").getValueString() );
				
				if(	viewObject.getAttribute("CREATOR").getValueLong() != userBoui ) {
					row.addString( viewObject.getAttribute("name").getValueString() + " *" );
				}
				else {
					long sharedCount = viewObject.getBridge("share").getRowCount();
					
					row.addString( viewObject.getAttribute("name").getValueString() + (sharedCount>0?" +":"") );
				}
				dataArray.add( row );
			}
			
			valuesStore.w( valuesConfig.renderExtConfig() );
			valuesStore.w( ")" );
			
			ExtConfig viewsCombo = new ExtConfig( "Ext.form.ComboBox" );
			viewsCombo.addString("triggerAction", "all");
			viewsCombo.add("store", valuesStore );
			viewsCombo.addString("mode", "local" );
			viewsCombo.addString("blankText", XEOComponentMessages.EXPLORER_VIEW_LIST_NO_NAME.toString() );
			viewsCombo.add("width", 160 );
			viewsCombo.addJSString("tooltip", "Filtros" );
			viewsCombo.addString("displayField", "d" );
			viewsCombo.addString("valueField", "i" );
			String hiddenName =  getClientId() + "_cvnh";
			viewsCombo.addString("hiddenName", hiddenName );
			
			ExtConfig listeners = viewsCombo.addChild("listeners" );
			if( dataArray.size() > 1 ) {
				XUICommand setViewCmd = (XUICommand)findComponent( getClientId() + "_setviewcmd" );
            	listeners.add( "'select'" , 
            			"function(combo,record,index){\n" +
            				(getRenderViewPort()?
							"var h = document.getElementById('" + hiddenName + "1');" +
							"h.value = combo.getValue();":"") +
            			 	XVWScripts.getAjaxCommandScript( setViewCmd, XVWScripts.WAIT_STATUS_MESSAGE ) + 
            			 "}"
            	);
			}
			
			Long savedViewBoui =  getCurrentSavedViewBOUI();
			viewsCombo.addString( "value" , savedViewBoui!=null?Long.toString(savedViewBoui):"" );
			viewsCombo.add( "maxLength" , 255 );
			
			extJsComp.setExtConfig( viewsCombo );
			return extJsComp;
		}
		catch ( boRuntimeException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public static class PreviewModeMenu extends Menu {
		
		private XUIBaseProperty<String> mode = new XUIBaseProperty<String>("mode", this );
		
		@Override
		public void actionPerformed(ActionEvent event) {
			GridExplorer gexp = (GridExplorer)findParentComponent( GridExplorer.class );
			
			String mode = getMode();
			if( mode == null ) {
				// Toggle the position
				boolean nextSetTrue = false;
				for( UIComponent c : getChildren() ) {
					PreviewModeMenu pm = (PreviewModeMenu)c;
					
					if( (Boolean)pm.getValue() ) {
						nextSetTrue = true;
						pm.setValue( false );
					}
					else {
						if( nextSetTrue ) {
							pm.setValue( true );
							gexp.setPreviewPanelPosition( pm.getMode() );
							nextSetTrue = false;
						}
					}
				}
				if( nextSetTrue ) {
					((Menu)getChild(0)).setValue( true );
					gexp.setPreviewPanelPosition( ((PreviewModeMenu)getChild(0)).getMode() );
				}				
			}
//			else {
//				gexp.setPreviewPanelMode( mode );
//				// Update the menu flag
//				Menu m = (Menu)getParent();
//				for( UIComponent c : m.getChildren() ) {
//					if( c instanceof PreviewModeMenu ) {
//						PreviewModeMenu pm = (PreviewModeMenu)c;
//						if( pm != this ) {
//							pm.setValue( false );
//						}
//						else {
//							pm.setValue( true );
//						}
//					}
//				}
//				
//			}
		}
		
		public void setMode( String position ) {
			this.mode.setValue( position );
		}
		
		public String getMode() {
			return this.mode.getValue();
		}
	}
	
	
	public static class PreviewPositionMenu extends Menu {
		
		private XUIBaseProperty<String> position = new XUIBaseProperty<String>("position", this );
		
		@Override
		public void actionPerformed(ActionEvent event) {
			GridExplorer gexp = (GridExplorer)findParentComponent( GridExplorer.class );
			
			Menu   menu   = (Menu)event.getComponent();
			String menuId = menu.getId();
			Boolean value = (Boolean)menu.getValue();
			
			PreviewPanelPosition currentPosition = gexp.getPreviewPanelPosition();
			
			if( value != null && value ) {
				gexp.setPreviewPanelPosition( PreviewPanelPosition.OFF );
				menu.setValue( false );
				
			}
			else if( menuId.endsWith( "_mpvw" ) ) {
				// Rotate preview
				if( currentPosition == PreviewPanelPosition.OFF ) {
					gexp.setPreviewPanelPosition( PreviewPanelPosition.BOTTOM );
				}
				else if( currentPosition == PreviewPanelPosition.LEFT ) {
					gexp.setPreviewPanelPosition( PreviewPanelPosition.OFF );
				}
				else if( currentPosition == PreviewPanelPosition.RIGHT ) {
					gexp.setPreviewPanelPosition( PreviewPanelPosition.LEFT );
				}
				else if( currentPosition == PreviewPanelPosition.BOTTOM ) {
					gexp.setPreviewPanelPosition( PreviewPanelPosition.RIGHT );
				}
				menu = (Menu)menu.getChild(0);
			}
			else if ( menuId.endsWith( "_mpvwl" ) ) {
				gexp.setPreviewPanelPosition( PreviewPanelPosition.LEFT );
			}
			else if ( menuId.endsWith( "_mpvwr" ) ) {
				gexp.setPreviewPanelPosition( PreviewPanelPosition.RIGHT );
			}
			else if ( menuId.endsWith( "_mpvwb" ) ) {
				gexp.setPreviewPanelPosition( PreviewPanelPosition.BOTTOM );
			}
			
			PreviewPanelPosition currentPos = gexp.getPreviewPanelPosition();
			for ( UIComponent comp : menu.getParent().getChildren() ) {
				Menu m = ((Menu)comp);
				if( m.getId().endsWith( "_mpvwl" ) && currentPos == PreviewPanelPosition.LEFT ) {
					m.setValue(true);
				}
				else if( m.getId().endsWith( "_mpvwr" ) && currentPos == PreviewPanelPosition.RIGHT ) {
					m.setValue(true);
				}
				else if( m.getId().endsWith( "_mpvwb" ) && currentPos == PreviewPanelPosition.BOTTOM ) {
					m.setValue(true);
				}
				else {
					m.setValue( false );
				}
			}
			
		}
		
		public void setPosition( String position ) {
			this.position.setValue( position );
		}
		
		public String getPosition() {
			return this.position.getValue();
		}
		
	}
	
	
	
	
	protected XUICommand getPreviewCommandComponent() {
		return (XUICommand)findComponent( getId() + "_pvwcmd" );
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {
		
		ThreadLocal<GridPanelExtJsRenderer> 	gridRenderer = new ThreadLocal<GridPanelExtJsRenderer>() {
			@Override
			protected GridPanelExtJsRenderer initialValue() {
				return new GridPanelExtJsRenderer();
			}
		};
		GridPanelRenderer 		render = new GridPanelRenderer();
		
		private static final String decodePositionToRegion( PreviewPanelPosition p ) {
			switch( p ) {
				case BOTTOM:
					return "south";
				case LEFT:
					return "west";
				case RIGHT:
					return "east";
				default : 
					return "south";	
			}
		}
		
		
		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			//component.forceRenderOnClient();
			if( !((GridExplorer)component).getRenderViewPort() ) {
				gridRenderer.get().encodeBegin(component);
			}
		}
		
		
		@Override
		public boolean getRendersChildren() {
			return true;
		}
		
		@Override
		public void encodeChildren(XUIComponentBase component) throws IOException {
			if( !((GridExplorer)component).getRenderViewPort() ) {
				gridRenderer.get().encodeChildren( component );
			}
		}
		
		
		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
			
			XUIResponseWriter w = getResponseWriter();
			w.startElement(DIV);
			w.writeAttribute(ID, component.getClientId());
			
			gridRenderer.get().encodeGridHiddenInputs(component);
			
			GridExplorer exp = ((GridExplorer)component);
			if( !exp.getRenderViewPort() ) {
				gridRenderer.get().encodeEnd(component);
				
			}
			else {
				if( exp.isRenderedOnClient() ) {
					getResponseWriter().getScriptContext().add(
							XUIScriptContext.POSITION_HEADER, 
							"destroy" + exp.getId(), 
							"Ext.getCmp('" + exp.getId() + "_vp').destroy();" );
				}
				else {
					
					//At the moment a hack to circunvent the bug of not destroying the ViewPort when 
					//changing preview directions. The problem is with the isRenderedOnClient condition never being true
					getResponseWriter().getScriptContext().add(
							XUIScriptContext.POSITION_HEADER, 
							"destroyCheck" + exp.getId(), 
							"var c = Ext.getCmp('" + exp.getId() + "_vp'); if (c) {c.destroy();}");
					
					if( exp.isNew ) {
						String hiddenName =  component.getClientId() + "_cvnh1";
						w.startElement( HTMLTag.INPUT, component );
						w.writeAttribute( HTMLAttr.TYPE , "hidden", null );
						w.writeAttribute( HTMLAttr.NAME , hiddenName, null );
						w.writeAttribute( HTMLAttr.ID , hiddenName, null );
						w.endElement( HTMLTag.INPUT );
					}
				}
				
				
				// Force rerender of child components
				for( UIComponent comp : exp.getChildren() ) {
					if( comp instanceof XUIComponentBase ) {
						((XUIComponentBase)comp).forceRenderOnClient();
					}
				}
				
				exp.setRegion("center");
				exp.setLayout("");
				
				ExtConfig config = new ExtConfig("Ext.Viewport");
				config.add( "layout" , "'border'");
				config.addJSString( "id", exp.getId() + "_vp" );
				
				ExtConfigArray items = config.addChildArray("items");
				items.add( gridRenderer.get().extEncodeAll(component) );
				
				PreviewPanelMode previewMode = exp.getPreviewPanelMode();
				if( exp.getEnablePreviewPanel() && exp.getPreviewPanelPosition() != PreviewPanelPosition.OFF ) {  
					ExtConfig previewPanel = new ExtConfig();
					previewPanel.addJSString( "id" , exp.getClientId() + "_panel" );
					previewPanel.add( "split" , true );
					previewPanel.add( "height" , exp.getPreviewPanelHeight() );
					previewPanel.add( "width" , exp.getPreviewPanelWidth() );
					
					previewPanel.add( "hidden" , false );
					previewPanel.addJSString( "region" , decodePositionToRegion( exp.getPreviewPanelPosition() ) );
					//previewPanel.addJSString( "title" , "Preview" );
					previewPanel.addJSString( "bodyStyle" , "overflow:auto;" );
					
					if( previewMode == PreviewPanelMode.EDIT ) {
						previewPanel.addJSString( "html" , "<div id=\"" + exp.getClientId() + "_pvwdiv"  +  "\"/>" );
					}
					else {
						String sFrameName = exp.getClientId() + "_pvwfrm" ;
						previewPanel.add( "html" , 
								"\"<iframe name='"+sFrameName+"' id='"+sFrameName+"' src='about:blank' scrolling='yes' frameBorder='0' width='100%' height='100%'></iframe>\"" 
							);
					}
					
					// Default listeners
					ExtConfig listeners = new ExtConfig();
					listeners.add( "bodyresize" , "function(){ExtXeo.layoutMan.doLayout();}");
					previewPanel.add( "listeners" , listeners );
					items.add( previewPanel );
				}
				
				getResponseWriter().getScriptContext()
					.add(XUIScriptContext.POSITION_FOOTER, "viewPort" + component.getClientId() , 
							config.renderExtConfig()
						);
				
				
				if( exp.getPreviewPanelPosition() != PreviewPanelPosition.OFF ) {   
					String openCommandJs = "";
					String destryCompsJs = "";
					if( previewMode == PreviewPanelMode.EDIT ) {
						openCommandJs = "XVW.openViewOnElement(" +
						"'" + component.findParentComponent(XUIForm.class).getClientId() + "', '" + exp.getPreviewCommandComponent().getClientId() + "', '', '" + exp.getClientId() + "_pvwdiv'" +
						");";
						destryCompsJs = 
							"var e = document.getElementById('" + exp.getClientId() + "_pvwdiv');\n" +
							"ExtXeo.destroyComponents( e, true );";
						
					}
					else if ( previewMode == PreviewPanelMode.PREVIEW ) {
						openCommandJs = "XVW.OpenCommandFrame(" +
						"'" + exp.getClientId() + "_pvwfrm" + "','" + component.findParentComponent(XUIForm.class).getClientId() + "', '" + exp.getPreviewCommandComponent().getClientId() + "', ''" +
						");";
						destryCompsJs = "";
					}
	
					getResponseWriter().getScriptContext()
					.add(XUIScriptContext.POSITION_FOOTER, "previewBeforeRowSelect" + component.getClientId() , 
							"var c = Ext.getCmp('" + component.getClientId() + "');" +
							"c.getSelectionModel().on('beforerowselect',function( csmdl ){ " + 
								"if( csmdl.hasSelection() ) {" +
								"	return true;\n" +
								"}" +
								"return true;" +
							"\n });"
						); 
		
					getResponseWriter().getScriptContext()
					.add(XUIScriptContext.POSITION_FOOTER, "previewRowSelect" + component.getClientId() , 
							"var c = Ext.getCmp('" + component.getClientId() + "');" +
							"c.getSelectionModel().on('rowselect',function( csmdl ){ " + 
								"if( csmdl.hasSelection() ) {" +
								"	" +
								"	ExtXeo.destroyComponents(document.getElementById('" + exp.getClientId() + "_pvwdiv'));\n" +
								"}" +
								"window.setTimeout(function(){" + openCommandJs + "},100);" +
							"\n });"
						);
					
				}
			}
			
			w.endElement(DIV);
			
		}

		@Override
		public void service(ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp) throws IOException {
			this.render.service(oRequest, oResponse, oComp);
		}
		
		@Override
		public void decode(XUIComponentBase component) {
			super.decode(component);
			render.decode( component );
			gridRenderer.get().decode( component );
		}
		
	}
	
}
