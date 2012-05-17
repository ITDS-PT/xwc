package netgest.bo.xwc.components.classic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.preferences.Preference;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.xwc.components.annotations.ObjectAttribute;
import netgest.bo.xwc.components.annotations.Required;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.grid.GridPanelUtilities;
import netgest.bo.xwc.components.classic.grid.GridTreeSelectorEditBean;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.connectors.AggregableDataList;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.connectors.SortTerms;
import netgest.bo.xwc.components.connectors.SortTerms.SortTerm;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.model.Columns;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMethodBindProperty;
import netgest.bo.xwc.framework.XUIPreferenceManager;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIInput;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * A GridPanel is a XVW component that's used to display (in a tabular form) 
 * a list of items (provided by a data source). 
 * 
 * It provides built-in mechanisms to sort, group the data, etc. 
 * 
 * The items in a GridPanel are paginated by default (50 items per page)
 * Items in the table can be selected and clicked which can in turn trigger specific
 * actions (such as opening a new window when double clicking a given row in the table)
 * 
 * @author jcarreira
 *
 */
public class GridPanel extends ViewerInputSecurityBase {

	private String childViewers;

	public static final String SELECTION_ROW = "ROW";
	public static final String SELECTION_MULTI_ROW = "MULTI_ROW";
	public static final String SELECTION_CELL = "CELL";

	private GridPanelUtilities gridUtilities = new GridPanelUtilities( this );
	
	/**
	 * The list of items for the GridPanel (must be an implementation
	 * of the {@link DataListConnector} interface
	 */
	@Required
	private XUIBindProperty<DataListConnector> dataSource = new XUIBindProperty<DataListConnector>(
			"dataSource", this, DataListConnector.class);

	private XUIMethodBindProperty filterLookup = new XUIMethodBindProperty(
			"filterLookup", this);
	
	public void setFilterLookup( String lookupExpr ){
		this.filterLookup.setExpressionText( lookupExpr );
	}

	private XUIViewProperty<HashMap<String, ArrayList<String>>> aggregateFields = new XUIViewProperty<HashMap<String, ArrayList<String>>>(
			"aggregateFields", this, null);

	/**
	 * Determines how a user can select the rows in the grid panel. 
	 * Default (ROW) allows the user to select a single row; 
	 * (MULTI_ROW) allows the user to select multiple row and 
	 * CELL disables row selection. 
	 * 
	 * Users select multiple lines with the <i>Ctrl</i> button
	 */
	@Values({"ROW","MULTIROW","CELL"})
	public XUIViewStateBindProperty<String> rowSelectionMode = new XUIViewStateBindProperty<String>(
			"rowSelectionMode", this, String.class);
    
	/**
	 * The message to show when the component is waiting for a server action
	 * 
	 */
	@Values({"NONE","DIALOG","STATUS_MESSAGE"})
	public XUIBindProperty<String> 	serverActionWaitMode = 
    	new XUIBindProperty<String>( "serverActionWaitMode", this ,String.class );
    
	/**
	 * Binds the data of the GridPanel to an attribute of an object
	 * The attribute must be of type AttributeObjectCollection
	 */
	@ObjectAttribute
	private XUIStateBindProperty<String> objectAttribute = new XUIStateBindProperty<String>(
			"objectAttribute", this, String.class);

	/**
	 * The name of the column used to uniquely identity each row, 
	 * by default its the BOUI of a {@link boObject}
	 */
	private XUIStateProperty<String> rowUniqueIdentifier = new XUIStateProperty<String>(
			"rowUniqueIdentifier", this, "BOUI");

	/**
	 * The column that will auto expand to fill available space
	 */
	private XUIViewStateProperty<String> autoExpandColumn = new XUIViewStateProperty<String>(
			"autoExpandColumn", this);

	/**
	 * Forces the columns to fit the current available
	 *  space in the viewer where the GridPanel is defined	
	 */
	private XUIViewProperty<Boolean> forceColumnsFitWidth = new XUIViewProperty<Boolean>(
			"forceColumnsFitWidth", this, true);

	/**
	 * The number of items that are listed per page 
	 * (next items are shown in a different page)
	 */
	private XUIViewStateProperty<String> pageSize = new XUIViewStateProperty<String>(
			"pageSize", this, "50");

	/**
	 * The target for the action invoked when a row is double clicked
	 */
	@Values({"blank","window","tab","download","self","top"})
	private XUIBindProperty<String> rowDblClickTarget = new XUIBindProperty<String>(
			"rowDblClickTarget", this, "tab", String.class );

	/**
	 * The target for the action invoked when a row is clicked
	 */
	@Values({"blank","window","tab","download","self","top"})
	private XUIViewProperty<String> rowClickTarget = new XUIViewProperty<String>(
			"rowClickTarget", this, "");

	/**
	 * The currently selected row in the GridPanel
	 */
	private XUIBaseProperty<String> sActiveRow = new XUIBaseProperty<String>(
			"sActiveRow", this, null );;

	/**
	 * A JSON Object with the current filters for the columns, not to be set directly
	 * 
	 */
	private XUIBaseProperty<String> currentFilters = new XUIBaseProperty<String>(
			"currentFilters", this);
	
	/**
	 * A JSON Object with the filters for the advanced search 
	 */
	private XUIStateProperty<String> advancedFilters = new XUIStateProperty<String>(
			"advancedFilters", this );

	private XUIBaseProperty<String> currentExpandedGroups = new XUIBaseProperty<String>(
			"currentExpandedGroups", this);

	private String[] sSelectedRowsUniqueIdentifiers;

	private transient Column[] oGridColumns;

	private XUIViewProperty<String> layout = new XUIViewProperty<String>(
			"layout", this, "fit-parent");

	private XUIBaseProperty<String> region = new XUIBaseProperty<String>(
			"region", this);

	/**
	 * Defines the title of the GridPanel
	 */
	private XUIViewBindProperty<String> title = new XUIViewBindProperty<String>(
			"title", this, String.class );

	/**
	 * Allows to manually define the height of the GridPanel
	 */
	private XUIViewProperty<String> height = new XUIViewProperty<String>(
			"height", this, "250");

	/**
	 * Allows the GridPanel to resize to the space required to show all the data.
	 */
	private XUIViewProperty<Boolean> autoHeight = new XUIViewProperty<Boolean>(
			"autoHeight", this, false);

	/**
	 * The minimum height of the panel
	 */
	private XUIViewProperty<Integer> minHeight = new XUIViewProperty<Integer>(
			"minHeight", this, 60);

	/**
	 * Groups the results by a given column (must be the name of a given colum
	 * in the GridPanel, see the {@link ColumnAttribute} component
	 */
	private XUIViewStateBindProperty<String> groupBy = new XUIViewStateBindProperty<String>(
			"groupBy", this, String.class);

	/**
	 * Applies a CSS class to each row of the panel
	 */
	private XUIBindProperty<GridRowRenderClass> rowClass = new XUIBindProperty<GridRowRenderClass>(
			"rowClass", this, GridRowRenderClass.class);

	/**
	 * Whether or not the results in this GridPanel 
	 * can be grouped (only single column groups can be made at this point)
	 */
	private XUIViewBindProperty<Boolean> enableGroupBy = new XUIViewBindProperty<Boolean>(
			"enableGroupBy", this, false, Boolean.class);

	private XUIBindProperty<Boolean> autoSaveGridState = new XUIBindProperty<Boolean>(
			"autoSaveGridState", this, false, Boolean.class);
	
	private XUIBindProperty<String> gridStateName = new XUIBindProperty<String>(
			"gridStateName", this, null, String.class);
	
	/**
	 * Whether or not columns can be sorted
	 */
	private XUIViewBindProperty<Boolean> enableColumnSort = new XUIViewBindProperty<Boolean>(
			"enableColumnSort", this, true, Boolean.class);

	/**
	 * FIXME: Does not work, 
	 */
	private XUIViewBindProperty<Boolean> enableColumnFilter = new XUIViewBindProperty<Boolean>(
			"enableColumnFilter", this, true, Boolean.class);

	/**
	 * Whether or not the user can hide Columns in the GridPanel
	 */
	private XUIViewBindProperty<Boolean> enableColumnHide = new XUIViewBindProperty<Boolean>(
			"enableColumnHide", this, true, Boolean.class);

	/**
	 * 
	 * Whether or not the user can re-order columns in the GridPanel
	 * 
	 */
	private XUIViewBindProperty<Boolean> enableColumnMove = new XUIViewBindProperty<Boolean>(
			"enableColumnMove", this, true, Boolean.class);

	/**
	 * Whether or not the columns may be resized by the user
	 * 	
	 */
	private XUIViewBindProperty<Boolean> enableColumnResize = new XUIViewBindProperty<Boolean>(
			"enableColumnResize", this, true, Boolean.class);

	/**
	 * Defines whether or not the header menu appears in the GridPanel. 
	 * The Header Menu is situated on the top part of the GridPanel and it displays the title
	 * of the panel.
	 */
	private XUIViewBindProperty<Boolean> enableHeaderMenu = new XUIViewBindProperty<Boolean>(
			"enableHeaderMenu", this, true, Boolean.class);

	/**
	 * The current sort parameters, is used with a string in the form
	 * 'NAME_OF_COLUM|ORDER'. To order by the 'name' column in ascending order
	 * the value would be 'name|ASC' in descending order would be 'name|DESC'
	 * 
	 */
	private XUIViewProperty<String> currentSortTerms = new XUIViewProperty<String>(
			"currentSortTerms", this, null);

	private XUIViewProperty<String> currentColumnsConfig = new XUIViewProperty<String>(
			"currentColumnsConfig", this, null);

	/**
	 * Defines the method that's invoked 
	 * when a row of the GridPanel is doubled clicked.
	 * 
	 * If the property is used with value <code>""</code> (empty value) the
	 * row double click event is disabled
	 * 
	 */
	private XUIMethodBindProperty	onRowDoubleClick 	= new XUIMethodBindProperty("onRowDoubleClick", this );
	
	/**
	 * Defines the method that's invoked
	 * and acts when a row of the GridPanel is clicked.
	 */
	private XUIMethodBindProperty	onRowClick 			= new XUIMethodBindProperty("onRowClick", this );

	/**
	 * Allows the definition of an action when a row is selected
	 */
	private XUIMethodBindProperty	onSelectionChange	= new XUIMethodBindProperty("onSelectionChange", this );
	
	private XUIBindProperty<Boolean> autoReloadData = new XUIBindProperty<Boolean>(
			"autoReloadData", this, true, Boolean.class );
	
	private XUIBaseProperty<HashMap<String,String>> defaultSettings = 
		new XUIBaseProperty<HashMap<String,String>>("defaultSettings", this  );
	
	/**
	 * Whether or not to show the group toolbar (defaults to false) 
	 */
	private XUIBindProperty<Boolean> showGroupToolBar =
		new XUIBindProperty<Boolean>("showGroupToolBar", this, Boolean.FALSE , Boolean.class);
	
	public Boolean getShowGroupToolBar(){
		return this.showGroupToolBar.getEvaluatedValue();
	}
	
	public void setShowGroupToolBar(String newVal){
		this.showGroupToolBar.setExpressionText(newVal);
	}
	
	public void setShowGroupToolBar(Boolean newVal){
		this.showGroupToolBar.setValue(newVal);
	}
	
	private boolean forcedReloadData = false;
	
	private String currentFullTextSearch;

	private XUICommand filterLookupCommand;
	private XUICommand selectColumnsCommand;
	private XUICommand resetDefaultsCommand;
	
	private XUIInput filterLookupInput;
	
	private boolean updateClientView = true;

	private String currAggregateFieldSet;
	private String currAggregateFieldDescSet;
	private String currAggregateFieldOpSet;
	private String currAggregateFieldCheckSet;
	private String aggregateData;
	
	
	
	/**
	 * Represents the maximum number of rows that can be selected in the Grid
	 * if this property has a positive value a user can only select rows until it reaches
	 * that number. To select other rows he will have to de-select others first
	 * 
	 */
	private XUIBindProperty<Integer> maxSelections = 
		new XUIBindProperty<Integer>("maxSelections", this, Integer.class, "-1");
	
	public int getMaxSelections(){
		Integer value = maxSelections.getEvaluatedValue();
		if (value != null)
			return value.intValue();
		
		return -1;
	}

	public void setMaxSelections(String maxExpr){
		this.maxSelections.setExpressionText(maxExpr);
	}
	
	public void setMaxSelections(int maxSelections){
		this.maxSelections.setValue(Integer.valueOf(maxSelections));
	}
	
	/**
	 * Flag to reset any selections made in the grid
	 */
	private boolean clearSelections = false;
	
	/**
	 * Sets the grid to clear the existing selections (if any)
	 */
	public void clearSelections(){
		clearSelections = true;
	}
	
	/**
	 * Sets the grid to maintain selections (if any)
	 * (default behavior is to maintain)
	 */
	public void maintainSelections(){
		clearSelections = false;
	}
	
	public boolean getClearSelections(){
		return clearSelections;
	}
	
	
	/**
	 * Flag to tell the grid panel to maintain row selections between page
	 * changes
	 */
	private XUIViewProperty<Boolean> enableSelectionAcrossPages =
		new XUIViewProperty<Boolean>("enableSelectionAcrossPages", this, Boolean.FALSE);
	
	public Boolean getEnableSelectionAcrossPages(){
		return enableSelectionAcrossPages.getValue() && (getRowSelectionMode().equalsIgnoreCase(SELECTION_MULTI_ROW));
	}
	
	public void setEnableSelectionAcrossPages(boolean preserve){
		this.enableSelectionAcrossPages.setValue(Boolean.valueOf(preserve));
	}
	
	/**
	 * 
	 * Whether or not the GridPanel has multi selection
	 * 
	 * @return True if the grid has multi selection and false otherwise
	 */
	public boolean isMultiSelection(){
		return getRowSelectionMode().equalsIgnoreCase(SELECTION_MULTI_ROW);
	}
	
	/**
	 * 
	 * Whether or not the user can execute stats on numeric columns
	 * 
	 */
	private XUIViewBindProperty<Boolean> enableAggregate = new XUIViewBindProperty<Boolean>(
			"enableAggregate", this, true, Boolean.class);

	public String getCurrAggregateFieldSet() {
		return this.currAggregateFieldSet;
	}

	public String getCurrAggregateFieldDescSet() {
		return this.currAggregateFieldDescSet;
	}

	public String getCurrAggregateFieldOpSet() {
		return this.currAggregateFieldOpSet;
	}

	public String getCurrAggregateFieldCheckSet() {
		return this.currAggregateFieldCheckSet;
	}

	public String getAggregateData() {
		return this.aggregateData;
	}

	public void setCurrAggregateFieldSet(String currAggregateFieldSet) {
		this.currAggregateFieldSet = currAggregateFieldSet;
	}

	public void setCurrAggregateFieldDescSet(String currAggregateFieldDescSet) {
		this.currAggregateFieldDescSet = currAggregateFieldDescSet;
	}

	public void setCurrAggregateFieldOpSet(String currAggregateFieldOpSet) {
		this.currAggregateFieldOpSet = currAggregateFieldOpSet;
	}

	public void setCurrAggregateFieldCheckSet(String currAggregateFieldCheckSet) {
		this.currAggregateFieldCheckSet = currAggregateFieldCheckSet;
	}

	/**
	 * Return the enableNumericStats property of this Grid
	 * 
	 * @return true/false
	 */
	public boolean getEnableAggregate() {
		return this.enableAggregate.getEvaluatedValue();
	}

	/**
	 * Enable or disable numeric stats of the column
	 * 
	 * @param sExpressionText
	 *            true/false or {@link ValueExpression}
	 */
	public void setEnableAggregate(String sExpressionText) {
		this.enableAggregate.setExpressionText(sExpressionText);
	}

	public void setAggregateField(String fieldId, String fieldDesc,
			String aggregateType, String aggregateCheck) {
		this.currAggregateFieldSet = fieldId;
		this.currAggregateFieldDescSet = fieldDesc;
		this.currAggregateFieldOpSet = aggregateType;
		this.currAggregateFieldCheckSet = aggregateCheck;
	}

	public void removeAggregateField(String fieldId, String aggregateType) {
		this.currAggregateFieldSet = fieldId;
		this.currAggregateFieldOpSet = aggregateType;
	}

	public void setAggregateData(String aggregateData) {
		this.aggregateData = aggregateData;
	}

	/**
	 * Return the XUICommand associated with the filter actions
	 * @return Return the XUICommand associated with the filter actions
	 */
	public XUICommand getFilterLookupCommand() {
		return this.filterLookupCommand;
	}
	
	public XUICommand getSelectColumnsCommand() {
		return this.selectColumnsCommand;
	}
	
	public XUICommand getResetDefaultsCommand() {
		return this.resetDefaultsCommand;
	}
	
	/**
	 * Return the lookupInput associated with the filter actions
	 * @return {@link XUIInput}
	 */
	public XUIInput getFilterLookupInput() {
		return this.filterLookupInput;
	}
	
	/**
	 * Return if the component need's the rerendered of refreshed on the client side
	 */
	@Override
	public boolean wasStateChanged() {
		return this.updateClientView;
	}
	
	/**
	 * If the component is already rendered on the client, set this component
	 * to only refresh the grid data
	 * 
	 * @return true - if the grid only refresh data between server requests
	 */
	public boolean getOnlyRefreshData() {
		return !super.wasStateChanged();
	}
	
	/**
	 * Restore the component states
	 * 
	 */
	@Override
	public void restoreState(Object state) {
		super.restoreState(state);
		setRendered(true);
	}
	
	/**
	 * Return if the component is to be rendered on client
	 */
	@Override
	public boolean isRendered() {
		if (!getEffectivePermission(SecurityPermissions.READ)) {
			return false;
		}
		return super.isRendered();
	}
	
	@Override
	public void initComponent() {
		super.initComponent();
		
		if (filterLookup.isDefaultValue())
			setFilterLookup( "#{" + getBeanId() + ".lookupFilterObject}" );

		
		HashMap<String, String> defaults = new HashMap<String, String>();
		defaults.put("groupBy", getGroupBy() );
		defaults.put("currentSortTerms", this.currentSortTerms.getValue() );
		
		try {
			JSONArray jsonColsConfig = new JSONArray();
			int i = 0;
			for( Column c : getColumns() ) {
				JSONObject j = new JSONObject();
				j.put("position", ++i );
				j.put("dataField", c.getDataField() );
				j.put("hidden", c.isHidden() );
				j.put("width", c.getWidth() );
				jsonColsConfig.put(j);
			}
			defaults.put("currentColumnsConfig", jsonColsConfig.toString() );
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		this.defaultSettings.setValue(defaults);
		if( getAutoSaveGridState() ) {
			restoreUserState();
		}
		
	}

    public void setServerActionWaitMode( String waitModeName ) {
    	this.serverActionWaitMode.setExpressionText( waitModeName );
    }
    
    public XVWServerActionWaitMode getServerActionWaitMode() {
    	String value = this.serverActionWaitMode.getEvaluatedValue();
    	if( value != null ) {
    		return XVWServerActionWaitMode.valueOf( value );
    	}
    	return XVWServerActionWaitMode.NONE;
    }	
	
	/**
	 * Process a preRender Actions
	 */
	@Override
	public void preRender() {
		
		if( this.onRowDoubleClick.getValue() != null ) {
			XUICommand oRowDblClickComp = (XUICommand) this.findComponent(getId()
					+ "_rowDblClick");

			if (oRowDblClickComp == null) {
				oRowDblClickComp = new XUICommand();
				oRowDblClickComp.setId(getId() + "_rowDblClick");
				this.getChildren().add(oRowDblClickComp);
			}
			oRowDblClickComp
					.setActionExpression( this.onRowDoubleClick.getValue() );
		}
		
		if( this.onRowClick.getValue() != null ) {
			XUICommand oRowDblClickComp = (XUICommand) this.findComponent(getId()
					+ "_rowClick");

			if (oRowDblClickComp == null) {
				oRowDblClickComp = new XUICommand();
				oRowDblClickComp.setId(getId() + "_rowClick");
				this.getChildren().add(oRowDblClickComp);
			}
			oRowDblClickComp
					.setActionExpression( this.onRowClick.getValue() );
		}

		if( this.onSelectionChange.getValue() != null ) {
			XUICommand oRowDblClickComp = (XUICommand) this.findComponent(getId()
					+ "_selChange");

			if (oRowDblClickComp == null) {
				oRowDblClickComp = new XUICommand();
				oRowDblClickComp.setId(getId() + "_selChange");
				this.getChildren().add(oRowDblClickComp);
			}
			oRowDblClickComp
					.setActionExpression( this.onSelectionChange.getValue() );
		}
		
		// per component initializations...
		if (findComponent(getId() + "_lookupCommand") == null) {
			filterLookupCommand = new XUICommand();
			filterLookupCommand.setId(getId() + "_lookupCommand");
			filterLookupCommand.addActionListener(new FilterLookupListener());
			getChildren().add(filterLookupCommand);
		} else {
			filterLookupCommand = (XUICommand) findComponent(getId()
					+ "_lookupCommand");
		}

		if (findComponent(getId() + "_selectColumnsCommand") == null) {
			selectColumnsCommand = new XUICommand();
			selectColumnsCommand.setId(getId() + "_selectColumnsCommand");
			selectColumnsCommand.addActionListener(new SelectColumnsListener());
			getChildren().add(selectColumnsCommand);
		} else {
			selectColumnsCommand = (XUICommand) findComponent(getId()
					+ "_selectColumnsCommand");
		}

		if (findComponent(getId() + "_resetDefaultsCommand") == null) {
			resetDefaultsCommand = new XUICommand();
			resetDefaultsCommand.setId(getId() + "_resetDefaultsCommand");
			resetDefaultsCommand.addActionListener(new ResetDefaultsListener());
			getChildren().add(resetDefaultsCommand);
		} else {
			resetDefaultsCommand = (XUICommand) findComponent(getId()
					+ "_resetDefaultsCommand");
		}
		
		if (findComponent(getId() + "_lookupInput") == null) {
			filterLookupInput = new XUIInput();
			filterLookupInput.setId(getId() + "_lookupInput");
			getChildren().add(filterLookupInput);
		} else {
			filterLookupInput = (XUIInput) findComponent(getId()
					+ "_lookupInput");
		}

		String viewerSecurityId = getInstanceId();
		if (viewerSecurityId != null) {
			setViewerSecurityPermissions("#{viewBean.viewerPermissions."
					+ viewerSecurityId + "}");
		}
	}

	public static class FilterLookupListener implements ActionListener {

		public void processAction(ActionEvent event)
				throws AbortProcessingException {

			XUICommand cmd = (XUICommand) event.getComponent();
			cmd.setValue(((HttpServletRequest) cmd.getRequestContext()
					.getRequest()).getParameter(cmd.getClientId()));
			((GridPanel) cmd.getParent()).doFilterLookup();
		}
	}

	public static class ResetDefaultsListener implements ActionListener {
		@Override
		public void processAction(ActionEvent arg0)
				throws AbortProcessingException {
			
			GridPanel gridPanel = (GridPanel)arg0.getComponent().getParent();
			gridPanel.resetToDefaults();
			
		}
		
	}

	public void setCurrentAggregateField(String aggregateField) {
		try {
			if (aggregateField != null) {
				String[] tokens = aggregateField.split(":");
				currAggregateFieldCheckSet = tokens[0];
				currAggregateFieldOpSet = tokens[1];
				currAggregateFieldSet = tokens[2];
				currAggregateFieldDescSet = tokens[3];

				currAggregateFieldCheckSet = currAggregateFieldCheckSet != null
						&& currAggregateFieldCheckSet.length() > 0 ? currAggregateFieldCheckSet
						: null;
				currAggregateFieldOpSet = currAggregateFieldOpSet != null
						&& currAggregateFieldOpSet.length() > 0 ? currAggregateFieldOpSet
						: null;
				currAggregateFieldSet = currAggregateFieldSet != null
						&& currAggregateFieldSet.length() > 0 ? currAggregateFieldSet
						: null;
				currAggregateFieldDescSet = currAggregateFieldDescSet != null
						&& currAggregateFieldDescSet.length() > 0 ? currAggregateFieldDescSet
						: null;
			}
		} catch (Exception e) {
			currAggregateFieldCheckSet = null;
			currAggregateFieldOpSet = null;
			currAggregateFieldSet = null;
			currAggregateFieldDescSet = null;
		}
		
	}
	
	public static class SelectColumnsListener implements ActionListener {

		@Override
		public void processAction(ActionEvent arg0)
				throws AbortProcessingException {
			
			XUIRequestContext requestContext
				= XUIRequestContext.getCurrentContext();
			
			XUISessionContext sessionContext = 
				requestContext.getSessionContext();
			
			XUIViewRoot viewSelCols = 
					sessionContext.createChildView( "netgest/bo/xwc/components/classic/grid/GridTreeSelector.xvw" );
			
			GridTreeSelectorEditBean selectorBean = 
				(GridTreeSelectorEditBean)viewSelCols.getBean( "viewBean" );
			
			GridPanel gridPanel = (GridPanel)arg0.getComponent().getParent(); 

			selectorBean
				.setGridPanelId( 
						gridPanel.getClientId()  
			);
			
			selectorBean.setGridPanelId(gridPanel.getClientId());
			
			Column[] columns = 
					gridPanel.getColumns();

			
			DataListConnector gridDataSource = gridPanel.getDataSource(); 
			
			for( Column column : columns ) {
				((ColumnAttribute)column).setLabel( 
						GridPanel.getColumnLabel( 
								gridDataSource , column ) 
				);
			}
			selectorBean.setColumns( columns );

			requestContext.setViewRoot( viewSelCols );
			
		}
		
	}
	
	private void doFilterLookup() {
		this.filterLookup.invoke();
	}
	
	/**
	 * Specify a class implementing the interface {@link netgest.bo.xwc.components.classic.GridRowRenderClass} 
	 * @param rowClassExpressionText {@link ValueExpression} returning a {@link netgest.bo.xwc.components.classic.GridRowRenderClass}
	 */
	public void setRowClass(String rowClassExpressionText) {
		this.rowClass.setExpressionText(rowClassExpressionText);
	}
	
	/**
	 * Get the current {@link netgest.bo.xwc.components.classic.GridRowRenderClass} for this Grid
	 * @return {@link netgest.bo.xwc.components.classic.GridRowRenderClass}
	 */
	public GridRowRenderClass getRowClass() {
		return this.rowClass.getEvaluatedValue();
	}
	
	/**
	 * Set enableGroup by in this Grid
	 * @param rowClassExpressionText true/false or {@link ValueExpression}
	 */
	public void setEnableGroupBy(String rowClassExpressionText) {
		this.enableGroupBy.setExpressionText(rowClassExpressionText);
	}
	
	/**
	 * Returns the enableGroupBy property of the Grid
	 * @return true/false
	 */
	public boolean getEnableGroupBy() {
		return this.enableGroupBy.getEvaluatedValue();
	}

	
	public void setGridStateName(String gridStateName ) {
		this.gridStateName.setExpressionText( gridStateName );
	}
	
	public String getGridStateName() {
		String stateName = this.gridStateName.getEvaluatedValue();
		if( stateName == null || stateName.length() == 0 ) {
			stateName = XUIRequestContext.getCurrentContext().getViewRoot().getViewId();
		}
		return stateName;
	}
	
	public void setAutoSaveGridState(String autoSaveGridState) {
		this.autoSaveGridState.setExpressionText(autoSaveGridState);
	}
	
	public boolean getAutoSaveGridState() {
		return this.autoSaveGridState.getEvaluatedValue();
	}
	
	
	/**
	 * Enable or disable the column sort in this Grid
	 * @param rowClassExpressionText true/false or a {@link ValueExpression}
	 */
	public void setEnableColumnSort(String rowClassExpressionText) {
		this.enableColumnSort.setExpressionText(rowClassExpressionText);
	}

	/**
	 * Return the enableColumnSort property of this Grid
	 * @return true/false
	 */
	public boolean getEnableColumnSort() {
		return this.enableColumnSort.getEvaluatedValue();
	}

	/**
	 * Enable or disable the column filter in this Grid
	 * @param rowClassExpressionText true/false or a {@link ValueExpression}
	 */
	public void setEnableColumnFilter(String rowClassExpressionText) {
		this.enableColumnFilter.setExpressionText(rowClassExpressionText);
	}

	/**
	 * Return the enableColumnFilter property of this Grid
	 * @return true/false
	 */
	public boolean getEnableColumnFilter() {
		return this.enableColumnFilter.getEvaluatedValue();
	}
	
	/**
	 * Enable or disable the column hide by the user in this Grid
	 * @param sExpressionText true/false or a {@link ValueExpression}
	 */
	public void setEnableColumnHide(String sExpressionText) {
		this.enableColumnHide.setExpressionText(sExpressionText);
	}

	/**
	 * Return the enableColumnHide property of this Grid
	 * @return true/false
	 */
	public boolean getEnableColumnHide() {
		return this.enableColumnHide.getEvaluatedValue();
	}
	
	/**
	 * Enables or disable column move by the user in this grid 
	 * @param sExpressionText
	 */
	public void setEnableColumnMove(String sExpressionText) {
		this.enableColumnMove.setExpressionText(sExpressionText);
	}

	/**
	 * Return the enableColumnMove property of this Grid
	 * @return true/false
	 */
	public boolean getEnableColumnMove() {
		return this.enableColumnMove.getEvaluatedValue();
	}

	/**
	 * Return the enableColumnResize property of this Grid
	 * @return true/false
	 */
	public boolean getEnableColumnResize() {
		return this.enableColumnResize.getEvaluatedValue();
	}

	/**
	 * Enable or disable column resize by the user in this grid 
	 * @param sExpressionText true/false or {@link ValueExpression}
	 */
	public void setEnableColumnResize(String sExpressionText) {
		this.enableColumnResize.setExpressionText(sExpressionText);
	}

	/**
	 * Return the enableHeaderMenu property of this Grid
	 * @return true/false
	 */
	public boolean getEnableHeaderMenu() {
		return this.enableHeaderMenu.getEvaluatedValue();
	}
	
	/**
	 * Enable or disable the header menu of the column
	 * @param sExpressionText true/false or {@link ValueExpression}
	 */
	public void setEnableHeaderMenu(String sExpressionText) {
		this.enableHeaderMenu.setExpressionText(sExpressionText);
	}
	
	/**
	 * Return a JSON Object with the current filters
	 * @return String JSON Object
	 */
	public String getCurrentFilters() {
		return currentFilters.getValue();
	}
	
	/**
	 * 
	 * Return a JSON Object with advanced search filters
	 * 
	 * @return A JSON Object as String
	 */
	public String getAdvancedFilters(){
		return advancedFilters.getValue();
	}

	/**
	 * Set the current column filters with a JSON Object
	 * @param currentFilters String JSON Object
	 */
	public void setCurrentFilters(String currentFilters) {
		this.currentFilters.setValue(currentFilters);
	}
	
	/**
	 * Set the advanced filters with a JSON Object
	 * 
	 * @param advancedFilters
	 */
	public void setAdvancedFilters( String advancedFilters ){
		this.advancedFilters.setValue( advancedFilters );
	}

	/**
	 * Return a JSON Object with the current filters
	 * @return String JSON Object
	 */
	public String getCurrentColumnsConfig() {
		return currentColumnsConfig.getValue();
	}

	/**
	 * Set the current column filters with a JSON Object
	 * @param currentFilters String JSON Object
	 */
	public void setCurrentColumnsConfig(String currentColumnsConfig) {
		this.currentColumnsConfig.setValue(currentColumnsConfig);
	}
	
	/**
	 * Return a JSON Object with the current filters
	 * @return String JSON Object
	 */
	public String getCurrentExpandedGroups() {
		return currentExpandedGroups.getValue();
	}

	/**
	 * Set the current column filters with a JSON Object
	 * @param currentFilters String JSON Object
	 */
	public void setCurrentExpandedGroups(String currentExpandedGroups) {
		this.currentExpandedGroups.setValue(currentExpandedGroups);
	}
	
	/**
	 * Return a the current FilterTerms applied to the columns
	 * @return {@link FilterTerms}
	 */
	public FilterTerms getCurrentFilterTerms() {
		FilterTerms terms = gridUtilities.createSimpleFilterTerms( this.getCurrentFilters() );
		FilterTerms advancedTerms = gridUtilities.createAdvancedFilterTerms( this.getAdvancedFilters() );
		FilterTerms finalTerms = gridUtilities.mergeFilterTerms( terms, advancedTerms );
		
		return finalTerms;
	}

	
	/**
	 * Define the {@link DataListConnector} witch this grid i binding to
	 * @param dataSource {@link DataListConnector} to bind this grid
	 */
	public void setDataSource(String dataSource) {
		this.dataSource.setValue(createValueExpression(dataSource,
				DataListConnector.class));
	}
	
	/**
	 * The current layout associated to the LayoutManager to apply this Grid
	 * @return String layout name
	 */
	public String getLayout() {
		return layout.getValue();
	}
	
	/**
	 * Set's the current layout type name to this GridPanel
	 * @param layoutMan String with form/fit-parent/fit-window or empty String
	 */
	public void setLayout(String layoutMan) {
		this.layout.setValue(layoutMan);
	}
	
	/**
	 * @return the region
	 */
	public String getRegion() {
		return region.getValue();
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region.setValue( region );
	}

	/**
	 * @return the region
	 */
	public String getTitle() {
		return title.getEvaluatedValue();
	}

	/**
	 * @param region the region to set
	 */
	public void setTitle(String sTitleExpr ) {
		this.title.setExpressionText( sTitleExpr );
	}

	/**
	 * Get the default height of the GridPanel when the Layout is set to a EmptyString
	 * @return the height
	 */
	public String getHeight() {
		return height.getValue();
	}
	
	/**
	 * Get the default height of the GridPanel when the Layout is set to a EmptyString
	 * 
	 * @param height int representing the height in pixels
	 */ 
	public void setHeight(String height) {
		this.height.setValue(height);
	}
	
	/**
	 * Return the autoHeight property of this Grid
	 * @return boolean autoHeight  
	 */
	public boolean getAutoHeight() {
		return autoHeight.getValue();
	}
	
	/**
	 * Set the autoHeight of the GridPanel
	 * @param booleanAutoHeight true/false or {@link ValueExpression}
	 */
	public void setAutoHeight(String booleanAutoHeight) {
		this.autoHeight.setValue(Boolean.parseBoolean(booleanAutoHeight));
	}
	
	/**
	 * Set the min height of this GridPanel
	 * @return int with the min height in pixels
	 */
	public int getMinHeight() {
		return this.minHeight.getValue();
	}
	
	/**
	 * Set the minHeight int value in pixels for this GridPanel
	 * @param minHeight int in pixels formated as String
	 */
	public void setMinHeight(String minHeight) {
		this.minHeight.setValue(Integer.parseInt(minHeight));
	}

	/**
	 * Set the minHeight int value in pixels for this GridPanel
	 * @param minHeight int in pixels
	 */
	public void setMinHeight(int minHeight) {
		this.minHeight.setValue(minHeight);
	}
	
	/**
	 * Set the default group by dataFile ( Column ) name
	 * @param groupByExpr dataFile name specified in the columns or a {@link ValueExpression}
	 */
	public void setGroupBy(String groupByExpr) {
		this.groupBy.setExpressionText(groupByExpr);
	}
	
	/**
	 * Return the value of the property groupBy
	 * @return String with the dataField of the group by column
	 */
	public String getGroupBy() {
		return this.groupBy.getEvaluatedValue();
	}
	
	/**
	 * Return the current {@link DataListConnector} width this GridPanel is bind.
	 * @return {@link DataListConnector}
	 */
	public DataListConnector getDataSource() {
		try {
			if (dataSource.getValue() != null) {
				DataListConnector ret = dataSource.getEvaluatedValue();
				if( ret != null ) {
					return ret;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(ComponentMessages.GRID_DATASOURCE_ERROR
					.toString(dataSource.getExpressionString()), e);
		}
		throw new RuntimeException(ExceptionMessage.THERE_ARE_NO_DATASOURCE_DEFINED__.toString()+"!");
	}
	
	/**
	 * Set the objectAttribute ( bridgeName ) which this GridPanel is binding
	 * @param sObjectAttribute String with the attributeName (bridgeName) 
	 */
	public void setObjectAttribute(String sObjectAttribute) {
		this.objectAttribute.setValue(createValueExpression(sObjectAttribute,
				String.class));
		
		if( this.dataSource.isDefaultValue() )
			this.setDataSource("#{" + getBeanId() + ".currentData." + getObjectAttribute()
					+ ".dataList}");
		
		if( this.rowClass.isDefaultValue() )
			this.setRowClass("#{" + getBeanId() + ".rowClass}");
		
		if( this.onRowDoubleClick.isDefaultValue() )
			this.setOnRowDoubleClick("#{" + getBeanId() + ".editBridge}");
		
		if( this.rowDblClickTarget.isDefaultValue() )
			this.setRowDblClickTarget("self");
	}

	/**
	 * Return the value of the property objectAttribute
	 * @return objectAttribute property value
	 */
	public String getObjectAttribute() {
		if( this.objectAttribute.getValue() != null ) {
			if (this.objectAttribute.getValue().isLiteralText()) {
				return String.valueOf(this.objectAttribute.getValue()
						.getExpressionString());
			}
			return (String) this.objectAttribute.getValue()
					.getValue(getELContext());
		}
		return null;
	}

	/**
	 * Return the current rowDblClickTarget (tab/self/window)
	 * @return RowDblClickTarget property value
	 */
	public String getRowDblClickTarget() {
		return rowDblClickTarget.getEvaluatedValue();
	}
	
	/**
	 * Set the current double click target
	 * @param rowDblClickTarget String with one of this values (tab - New Tab,self - Ajax Submit to the same page,window - Popup Window)
	 */
	public void setRowDblClickTarget(String rowDblClickTarget) {
		this.rowDblClickTarget.setExpressionText(rowDblClickTarget);
	}

	/**
	 * Return the current rowClickTarget (tab/self/window)
	 * @return rowClickTarget property value
	 */
	public String getRowClickTarget() {
		return rowClickTarget.getValue();
	}

	/**
	 * Set the current rowClickTarget target
	 * @param rowDblClickTarget String with one of this values (tab - New Tab,self - Ajax Submit to the same page,window - Popup Window)
	 */
	public void setRowClickTarget(String rowDblClickTarget) {
		this.rowClickTarget.setValue(rowDblClickTarget);
	}
	
	/**
	 * A String Array with the all available dataColumns plus the uniqueIdentifier column if not present
	 * in the column list
	 * @return String Array with the dataField of the Column
	 */
	public String[] getDataColumns() {
		Column[] oColumns;
		String sDataColumn;
		String sRowUniqueId;
		
		sRowUniqueId = getRowUniqueIdentifier();
		List<String> dataColumns;
		boolean bAddUniqueIdentifier = true;

		dataColumns = new ArrayList<String>();
		oColumns = getColumns();
		for (int i = 0; i < oColumns.length; i++) {
			sDataColumn = oColumns[i].getDataField();
			dataColumns.add(sDataColumn);
			if ( sRowUniqueId != null && sRowUniqueId.equals(sDataColumn)) {
				bAddUniqueIdentifier = false;
			}
		}
		if (bAddUniqueIdentifier && sRowUniqueId != null ) {
			dataColumns.add(sRowUniqueId);
		}
		return dataColumns.toArray(new String[dataColumns.size()]);

	}
	
	/**
	 * Get a column by a dataField, return null if not found
	 * @param dataFieldName String with the dataField property of the column
	 * @return Column with the specified dataField
	 */
	public Column getColumn(String dataFieldName) {
		Column ret = null;
		if (dataFieldName != null) {
			Column[] columns = getColumns();
			for (Column column : columns) {
				if (dataFieldName.equals(column.getDataField())) {
					ret = column;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 * Return a Array of Column with all the columns specified in the Columns component
	 * @return Column[]
	 */
	public Column[] getColumns() {
		Iterator<UIComponent> oChildrenIt;
		Iterator<UIComponent> oColumnsIt;
		Columns oColumns;

		if (oGridColumns == null) {
			oChildrenIt = getChildren().iterator();
			while (oChildrenIt.hasNext()) {

				UIComponent oKid = oChildrenIt.next();
				if (oKid instanceof Columns) {
					oColumns = (Columns) oKid;

					ArrayList<Column> oRetColumns;

					oRetColumns = new ArrayList<Column>();
					oColumnsIt = oColumns.getChildren().iterator();

					for (; oColumnsIt.hasNext();) {
						oRetColumns.add((Column) oColumnsIt.next());

					}
					oGridColumns = oRetColumns.toArray(new Column[oRetColumns
							.size()]);
					break;
				}
			}
		}
		if (oGridColumns == null) {
			oGridColumns = new Column[0];
		}
		return oGridColumns;

	}
	
	/**
	 * Set the row selection type
	 * @param rowSelectionMode String with one of this values
	 * 				ROW - Create a checkbox and allow the user to select a single row
	 * 				MULTIROW - Create a checkbox and allow the user to select multiple rows
	 * 				CELL - Doesn't a render a check box to select the rows.
	 */
	public void setRowSelectionMode(String rowSelectionMode) {
		this.rowSelectionMode.setValue(createValueExpression(rowSelectionMode,
				String.class));
	}
	
	/**
	 * Return the current value of the property rowSelectionMode
	 * @return String with the rowSelectionMode
	 */
	public String getRowSelectionMode() {
		if (this.rowSelectionMode.getValue() != null) {
			ValueExpression oValExpr = this.rowSelectionMode.getValue();
			if (oValExpr.isLiteralText()) {
				return oValExpr.getExpressionString();
			} else {
				return (String) oValExpr.getValue(getELContext());
			}
		}

		return SELECTION_ROW;
	}

	/**
	 * Returns the current {@link DataRecordConnector} selected by the user
	 * @return {@link DataRecordConnector} 
	 */
	public DataRecordConnector getCurrentSelectedRow() {
		return null;
	}
	
	/**
	 * Set the row unique idenfier column name 
	 * @param sRowIdentifier String with the dataField which contain the unique identifier
	 */
	public void setRowUniqueIdentifier(String sRowIdentifier) {
		this.rowUniqueIdentifier.setValue(sRowIdentifier);
	}
	
	
	/**
	 * Return the current value of the property rowUniqueIdentifier
	 * @return String with the rowUniqueIdentifier
	 */
	public String getRowUniqueIdentifier() {
		return rowUniqueIdentifier.getValue();
	}
	
	/**
	 * Forces the selection a multiple rows with a String Array with the unique idenfiers
	 * @param sSelectedRowsUIndetifiers String Array
	 */
	public void setSelectedRowsByIdentifier(String[] sSelectedRowsUIndetifiers) {
		this.sSelectedRowsUniqueIdentifiers = sSelectedRowsUIndetifiers;
	}
	
	/**
	 * Set the current active row
	 * @param rowIdentifier String with the row unique identifier
	 */
	public void setActiveRowByIdentifier(String rowIdentifier) {
		this.sActiveRow.setValue(rowIdentifier);
	}

	/**
	 * Get the current active row ( The last row user clicked )
	 * @return String with the uniqueRowIdentifier
	 */
	public String getActiveRowIdentifier() {
		return this.sActiveRow.getValue();
	}
	
	/**
	 * Return a String Array with all the unique identifiers of the columns
	 * @return String Array
	 */
	private String[] getSelectedRowsIdentifiers() {
		return sSelectedRowsUniqueIdentifiers;
	}
	
	/**
	 * Returns the index position in the DataListConnector of the selected rows, first rows is 1
	 * @return Array of int with the indexes of the selected rows. Returns empty array if there are no selected rows.
	 */
	public int[] getSelectedRowsPos() {
		int[] selRows;
		DataRecordConnector[] selDr = getSelectedRows();
		selRows = new int[ selDr.length ];
		for( int i=0;i < selDr.length; i++ ) {
			selRows[ i ] = selDr[i].getRowIndex();
		}
		return selRows;
	}
	
	/**
	 * Returns the active row,  ( The last row user clicked )
	 * @return {@link DataRecordConnector}
	 */
	public DataRecordConnector getActiveRow() {

		if (this.sActiveRow != null) {
			String rowId = this.getActiveRowIdentifier();
			if (rowId != null && rowId.length() > 0) {
				return getDataSource().findByUniqueIdentifier(rowId);
			}
		}
		return null;

	}
	
	/**
	 * Return a {@link DataRecordConnector} which match to the uniqueIdentifier
	 * @param rowIdentifier String with the row unique identifier
	 * @return the DataRecord associated with the rowIdentifier
	 */
	public DataRecordConnector getRowByIdentifier(String rowIdentifier) {
		return getDataSource().findByUniqueIdentifier(rowIdentifier);
	}
	
	/**
	 * Return a {@link DataRecordConnector} Array with the selected rows   
	 * @return {@link DataRecordConnector} Array
	 */
	public DataRecordConnector[] getSelectedRows() {
		String sUniqueIdentifier;
		DataRecordConnector oCurrentRecord;

		List<DataRecordConnector> oRetSelectRows = new ArrayList<DataRecordConnector>();

		if (this.getSelectedRowsIdentifiers() != null) {
			sUniqueIdentifier = getRowUniqueIdentifier();

			for (int i = 0; i < sSelectedRowsUniqueIdentifiers.length; i++) {
				sUniqueIdentifier = sSelectedRowsUniqueIdentifiers[i];
				oCurrentRecord = getDataSource().findByUniqueIdentifier(
						sUniqueIdentifier);
				if (oCurrentRecord != null) {
					oRetSelectRows.add(oCurrentRecord);
				}
			}
		}
		return oRetSelectRows.toArray(new DataRecordConnector[oRetSelectRows
				.size()]);
	}
	
	/**
	 * When set to true the defaul, the grid reload the the data after a server request.
	 * When false the method reloadData must be called to force the data be refreshed
	 * 
	 * @param elexpression a {@link ValueExpression} return a boolean value
	 */
	public void setAutoReloadData( String elexpression ) {
		this.autoReloadData.setExpressionText( elexpression );
	}
	
	/**
	 * When set to true the defaul, the grid reload the the data after a server request.
	 * When false the method reloadData must be called to force the data be refreshed
	 * 
	 * @param autoReload true / false
	 */
	public void setAutoReloadData( boolean autoReload ) {
		this.autoReloadData.setValue( autoReload );
	}
	
	/**
	 * Reads the property autoReloadData of the GridPanel
	 * @return true if the grid was triggred to reload data
	 */
	public boolean getAutoReloadData() {
		return this.autoReloadData.getEvaluatedValue();
	}
	
	/**
	 * Mark the grid to reload the data after the server request
	 * 
	 */
	public void reloadData() {
		this.forcedReloadData = true;
	}
	
	public void setClientViewUpdate( boolean updateClientView ) {
		this.updateClientView = updateClientView;
	}
	
	
	/**
	 * Check if the method reloadData was called
	 * @return true / false
	 */
	public boolean isMarkedToReloadData() {
		return this.forcedReloadData;
	}
	
	/**
	 * Set the onRowDoubleClick action
	 * @param onRowDoubleClick {@link MethodExpression} expression 
	 */
	public void setOnRowClick(String onRowDoubleClick) {
		this.onRowClick.setExpressionText( onRowDoubleClick );
	}
	
	/**
	 * Get the current action for onRowClick
	 * @return String whith {@link MethodExpression} expression
	 */
	public String getOnRowClick() {
		return this.onRowClick.getExpressionString();
	}

	/**
	 * Set the onSelectionChange action
	 * @param onSelectionChange {@link MethodExpression} expression 
	 */
	public void setOnSelectionChange(String onSelectionChange) {
		this.onSelectionChange.setExpressionText( onSelectionChange );
	}
	
	/**
	 * Get the current action for onSelectionChange
	 * @return String whith {@link MethodExpression} expression
	 */
	public String getOnSelectionChange() {
		return this.onSelectionChange.getExpressionString();
	}
	
	/**
	 * Set the action for onRowDoubleClick
	 * @param onRowDoubleClickExpr {@link MethodExpression} expression
	 */
	public void setOnRowDoubleClick(String onRowDoubleClickExpr) {
	
		this.onRowDoubleClick.setExpressionText( onRowDoubleClickExpr );

	}
	
	/**
	 * Get the current {@link MethodExpression} expression for onRowDoubleClick
	 * @return {@link MethodExpression} expression String
	 */
	public String getOnRowDoubleClick() {
		return this.onRowDoubleClick.getExpressionString();
	}
	
	/**
	 * Set the autoExpandColumn property
	 * @param autoExpandColumn dataField in the (Column) to auto expand and fit the Grid width
	 */
	public void setAutoExpandColumn(String autoExpandColumn) {
		this.autoExpandColumn.setValue(autoExpandColumn);
	}
	
	/**
	 * Return the value the the autoExpandColumn
	 * @return String with the dataField name of the Column
	 */
	public String getAutoExpandColumn() {
		return autoExpandColumn.getValue();
	}
	
	/**
	 * Return if the columns are in force fit mode to the width of the GridPanel
	 * @return true/false
	 */
	public boolean getForceColumnsFitWidth() {
		return forceColumnsFitWidth.getValue();
	}
	
	/**
	 * Set the columns to ajust to the width of the GridPanel
	 * @param forceColumnsFitWidth true/fale 
	 */
	public void setForceColumnsFitWidth(String forceColumnsFitWidth) {
		this.forceColumnsFitWidth.setValue(Boolean
				.valueOf(forceColumnsFitWidth));
	}
	
	/**
	 * Set the number of records to show per page
	 * @param pageSize int with the page size. default's to 50
	 */
	public void setPageSize(String pageSize) {
		this.pageSize.setValue(pageSize);
	}
	
	/**
	 * Returns the current value of the pageSize property
	 * @return int formated as String
	 */
	public String getPageSize() {
		return pageSize.getValue();
	}
	
	/**
	 * Return the column label 
	 * @param dataList	The current {@link DataListConnector}
	 * @param col	The {@link Column}
	 * @return	String with the label of the column
	 */
	public static final String getColumnLabel(DataListConnector dataList,
			Column col) {
		String label = col.getLabel();
		if (label == null) {
			DataFieldMetaData fm = dataList.getAttributeMetaData(col
					.getDataField());
			if (fm != null) {
				label = fm.getLabel();
			}
		}
		return label;
	}

	//
	// Methods from SecurableComponent
	//

	@Override
	public COMPONENT_TYPE getViewerSecurityComponentType() {
		return SecurableComponent.COMPONENT_TYPE.GRID;
	}

	@Override
	public String getViewerSecurityId() {
		String securityId = null;
		if (getId() != null && getId().length() > 0) {
			securityId = getId();
		}
		return securityId;
	}

	@Override
	public String getViewerSecurityLabel() {
		String label = getViewerSecurityComponentType().toString();
		if (getViewerSecurityId() != null) {
			if( getObjectAttribute() != null && !"".equals( getObjectAttribute() ) ) {
				label += getObjectAttribute();
			}
			else {
				label += " " + getViewerSecurityId();
			}
		}
		return label;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public String getChildViewers() {
		return this.childViewers;
	}

	public void setChildViewers(String childViewers) {
		this.childViewers = childViewers;
	}

	public void setCurrentSortTerms(String sortQuery) {
		this.currentSortTerms.setValue(sortQuery);
	}

	public SortTerms getCurrentSortTerms() {
		String sSort = this.currentSortTerms.getValue();

		SortTerms st = new SortTerms();;
		if (sSort != null) {
			String[] sSortFields = sSort.split("\\,");
			for( String sortField : sSortFields ) {
				String[] sSortDef = sortField.split("\\|");
				if (sSortDef.length == 2) {
					String sSortField = sSortDef[0].trim();
					String sSortDir = sSortDef[1].trim();
					if( sSortField.length() > 0 ) {
						st.addSortTerm(sSortField,
								"DESC".equalsIgnoreCase(sSortDir) ? SortTerms.SORT_DESC
										: SortTerms.SORT_ASC);
					}
				}
			}
		}
		return st.isEmpty()?null:st;
	}

	public String getCurrentFullTextSearch() {
		return this.currentFullTextSearch;
	}

	public void setCurrentFullTextSearch(String fullTextSearch) {
		this.currentFullTextSearch = fullTextSearch;
	}

	public void resetToDefaults() {
		HashMap<String, String> defaults = this.defaultSettings.getValue();
		setGroupBy( defaults.get("groupBy") );
		setCurrentSortTerms( defaults.get("currentSortTerms") );
		setCurrentColumnsConfig( defaults.get("currentColumnsConfig") );
		setCurrentFilters( null );
		setAggregateData(null);
		setAggregateFieldsFromString(getAggregateData());
		forceRenderOnClient();
	}

	public void aggregateSum(boolean check, String fieldId, String fieldDesc) {
		if (check) {
			updateAggregateFieldsBean();
		} else {
			removeAggregateFieldsBean();
		}
	}

	public void aggregateMin(boolean check, String fieldId, String fieldDesc) {
		if (check) {
			updateAggregateFieldsBean();
		} else {
			removeAggregateFieldsBean();
		}
	}

	public void aggregateMax(boolean check, String fieldId, String fieldDesc) {
		if (check) {
			updateAggregateFieldsBean();
		} else {
			removeAggregateFieldsBean();
		}
		;
	}

	public void aggregateAvg(boolean check, String fieldId, String fieldDesc) {
		if (check) {
			updateAggregateFieldsBean();
		} else {
			removeAggregateFieldsBean();
		}
	}

	public void updateAggregateFieldsBean() {
		try {
			if (this.getDataSource() != null
					&& (this.getDataSource().dataListCapabilities() & DataListConnector.CAP_AGGREGABLE) > 0) {
				String aggregateFieldId = this.getCurrAggregateFieldSet();
				String aggregateFieldDesc = this.getCurrAggregateFieldDescSet();
				String aggregateFieldOp = this.getCurrAggregateFieldOpSet();

				if (aggregateFieldId != null && aggregateFieldId.length() > 0
						&& aggregateFieldDesc != null
						&& aggregateFieldDesc.length() > 0
						&& aggregateFieldOp != null
						&& aggregateFieldOp.length() > 0) {
					if (this.aggregateFields.getValue() == null) {
						this.aggregateFields
								.setValue(new HashMap<String, ArrayList<String>>());
					}

					ArrayList<String> listVals = this.aggregateFields
							.getValue()
							.get(aggregateFieldId + ":" + aggregateFieldDesc);

					if (listVals == null) {
						listVals = new ArrayList<String>();
					}

					if (!listVals.contains(aggregateFieldOp)) {
						listVals.add(aggregateFieldOp);
					}
					this.aggregateFields.getValue().put(
							aggregateFieldId + ":" + aggregateFieldDesc,
							listVals);
				}
				this.setAggregateField(null, null, null, null);
				((AggregableDataList) this.getDataSource())
						.setAggregateFields(this.aggregateFields.getValue());
			}
		} catch (Exception e) {
		}
	}

	public void removeAggregateFieldsBean() {
		try {
			if (this.getDataSource() != null
					&& (this.getDataSource().dataListCapabilities() & DataListConnector.CAP_AGGREGABLE) > 0) {
				String aggregateFieldId = this.getCurrAggregateFieldSet();
				String aggregateFieldOp = this.getCurrAggregateFieldOpSet();
				String aggregateFieldDesc = this.getCurrAggregateFieldDescSet();

				if (aggregateFieldId != null && aggregateFieldId.length() > 0
						&& aggregateFieldOp != null
						&& aggregateFieldOp.length() > 0) {
					if (this.aggregateFields.getValue() == null) {
						this.aggregateFields
								.setValue(new HashMap<String, ArrayList<String>>());
					}

					ArrayList<String> listVals = this.aggregateFields.getValue()
							.get(aggregateFieldId + ":" + aggregateFieldDesc);

					if (listVals != null && listVals.contains(aggregateFieldOp)) {
						listVals.remove(aggregateFieldOp);
						this.aggregateFields.getValue().put(aggregateFieldId + ":"
								+ aggregateFieldDesc, listVals);
					}

					if (listVals == null || listVals.isEmpty()) {
						this.aggregateFields.getValue().remove(aggregateFieldId + ":"
								+ aggregateFieldDesc);
					}
				}
				this.setAggregateField(null, null, null, null);
				((AggregableDataList) this.getDataSource())
						.setAggregateFields(this.aggregateFields.getValue());
			}
		} catch (Exception e) {
		}
	}

	public void loadAggregateFieldsBean() {
		try {
			String aggregateFields = this.getAggregateData();
			setAggregateFieldsFromString(aggregateFields);
			this.setAggregateData(null);
		} catch (Exception e) {
		}
	}

	public void setAggregateFieldsBean() {
		try {
			this.setAggregateData(getAggregateFieldsString());
		} catch (Exception e) {

		}
	}

	public Preference getUserSatePreference() {
		String stateName = getGridStateName();
		Preference p = XUIPreferenceManager.getUserPreference(
				GridPanel.class.getName() + ".state", 
				stateName
		);
		return p;
	}
	
	public void saveUserState() {
		Preference preference = getUserSatePreference();
		saveUserFilterState( preference );
		saveUserViewState( preference );
		saveUserExpandedGroupsState( preference );
		saveGroupToolBarVisibility( preference );
		preference.savePreference();
	}
	
	public void restoreUserState() {
		Preference preference = getUserSatePreference();
		restoreUserViewState( preference );
		restoreUserFilterState( preference );
		restoreUserExpandedGroupsState( preference );
		restoreGroupToolBarVisiblity( preference );
	}
	
	public void saveUserViewState( Preference preference ) {
		preference.setString("columnsConfig", getCurrentColumnsConfig() );
		preference.setString("groupBy", getGroupBy() );
		preference.setString("sortTerms", this.currentSortTerms.getValue() );
		
		/** ML: 07-10-2011 **/
		this.setAggregateData(getAggregateFieldsString());
		preference.setString("aggFields", this.getAggregateData());
		/** END ML: 07-10-2011 **/
	}

	public void saveUserExpandedGroupsState( Preference preference ) {
		preference.setString("currentExpandedGroups", this.currentExpandedGroups.getValue() );
	}
	
	public void saveGroupToolBarVisibility( Preference preference ){
		preference.setString("groupToolBarVisibility", String.valueOf(this.showGroupToolBar.getEvaluatedValue()) );
	}

	public void restoreUserExpandedGroupsState( Preference preference ) {
		if( getEnableGroupBy() ) {
			this.setCurrentExpandedGroups( preference.getString("currentExpandedGroups") );
		}
	}
	
	public void restoreUserViewState( Preference preference ) {
		String columnsConfig = preference.getString("columnsConfig");
		if( columnsConfig != null ) {
			this.setCurrentColumnsConfig( columnsConfig );
		}
		
		if( getEnableGroupBy() ) {
			this.setGroupBy( preference.getString("groupBy") );
		}
		
		if( getEnableColumnSort() ) {
			this.setCurrentSortTerms( preference.getString("sortTerms") );
		}

		/** ML: 07-10-2011 **/
		if (getEnableAggregate()) {
			this.setAggregateData(preference.getString("aggFields"));
			this.setAggregateFieldsFromString(getAggregateData());
			this.setAggregateData(null);
		}
		/** END ML: 07-10-2011 **/
	}
	
	public void restoreGroupToolBarVisiblity( Preference preference ){
		Boolean visibility  =  preference.getBoolean("groupToolBarVisibility");
		this.setShowGroupToolBar(visibility);
	}
	
	public void saveUserFilterState( Preference preference ) {
		String currentFilters = getCurrentFilters();
		preference.setString("currentFilters" , currentFilters );
	}
	
	public void restoreUserFilterState( Preference preference ) {
		String filters = preference.getString("currentFilters");
		if( filters != null ) {
			setCurrentFilters( filters );
		}
	}

	public void applyAggregate(DataListConnector listConnector) {
		if ((listConnector.dataListCapabilities() & DataListConnector.CAP_AGGREGABLE) > 0) {
			((AggregableDataList) listConnector)
					.setAggregateFields(this.aggregateFields.getValue());
		}
	}

	public void applyFilters(DataListConnector listConnector) {
		if ((listConnector.dataListCapabilities() & DataListConnector.CAP_FILTER) > 0) {
			FilterTerms filterTerms = getCurrentFilterTerms();
			listConnector.setFilterTerms(filterTerms);
		}
	}

	public void applySort(DataListConnector listConnector) {
		if ((listConnector.dataListCapabilities() & DataListConnector.CAP_SORT) > 0) {
			SortTerms sortTerms = getCurrentSortTerms();
			if (sortTerms != null)
				listConnector.setSortTerms(sortTerms);
			else
				listConnector.setSortTerms(SortTerms.EMPTY_SORT_TERMS);
		}
	}

	public void applyFullTextSearch(DataListConnector listConnector) {
		if ((listConnector.dataListCapabilities() & DataListConnector.CAP_FULLTEXTSEARCH) > 0) {
			String fullTextSearch = getCurrentFullTextSearch();
			listConnector.setSearchText(fullTextSearch);
		}
	}

	public void applySqlFields(DataListConnector listConnector ) {
		String groupBy = this.getGroupBy();
		String sortBy = this.currentSortTerms.getValue();
		if( groupBy == null ) groupBy = "";
		if( sortBy == null ) sortBy = "";
		
		List<boObjectList.SqlField> sqlFields = new ArrayList<boObjectList.SqlField>(1);
		for( Column col : getColumns() ) {
			if( !col.isHidden() || 
				sortBy.toUpperCase().indexOf( col.getDataField().toUpperCase() ) != -1 ||  
				groupBy.toUpperCase().indexOf( col.getDataField().toUpperCase() ) != -1  
			) {
				String sqlExpression = col.getSqlExpression();
				if( sqlExpression != null ) {
					sqlFields.add( new boObjectList.SqlField( sqlExpression, col.getDataField() ) );
				}
			}
		}
		if( sqlFields.size() > 0 ) {
			listConnector.setSqlFields( sqlFields );
		}
		else {
			listConnector.setSqlFields( null );
		}
	}

	public Iterator<DataRecordConnector> applyLocalSort( Iterator<DataRecordConnector> dataListIterator ) {
		return applyLocalSort( dataListIterator, getCurrentSortTerms() );
	}
	
	public Iterator<DataRecordConnector> applyLocalSort( Iterator<DataRecordConnector> dataListIterator, final SortTerms sortTerms ) {

		if (sortTerms != null && !sortTerms.isEmpty()) {

			List<DataRecordConnector> orderedList = new ArrayList<DataRecordConnector>();
			while (dataListIterator.hasNext()) {
				orderedList.add(dataListIterator.next());
			}
			Collections.sort(orderedList,
					new Comparator<DataRecordConnector>() {
						@SuppressWarnings("unchecked")
						public int compare(DataRecordConnector left,
								DataRecordConnector right) {
							Comparable<Comparable> sLeft, sRight;
							
							int ret = 0;
							
							Iterator<SortTerm> sortTermIterator = sortTerms.iterator();
							while( sortTermIterator.hasNext() ) {
								SortTerm term = sortTermIterator.next();
					
								final String sSort = term.getField();
								final int direction = term.getDirection();
							
							
								DataFieldConnector leftField = left
										.getAttribute(sSort);
								byte fieldType = leftField.getDataType();
	
								if (fieldType == DataFieldTypes.VALUE_DATE
										|| fieldType == DataFieldTypes.VALUE_DATETIME
										|| fieldType == DataFieldTypes.VALUE_NUMBER) {
	
									sLeft = (Comparable) leftField.getValue();
									sRight = (Comparable) right.getAttribute(sSort)
											.getValue();
	
								} else {
	
									sLeft = (Comparable) leftField
											.getDisplayValue();
									sRight = (Comparable) right.getAttribute(sSort)
											.getDisplayValue();
	
								}
	
								if (sLeft == null || sRight == null) {
									return sLeft == null ? 1 : -1;
								}
								ret = direction == SortTerms.SORT_ASC ? 
										sLeft.compareTo(sRight) : 
										sRight.compareTo(sLeft);
								
								if( ret != 0 ) {
									break;
								}
							}
							return ret;
						}
					});
			dataListIterator = orderedList.iterator();
		}
		return dataListIterator;
	}

	public Iterator<DataRecordConnector> applyLocalFilter(
			Iterator<DataRecordConnector> iterator ) {

		FilterTerms filterTerms;

		filterTerms = getCurrentFilterTerms();
		
		return applyLocalFilter( iterator, filterTerms );
	}
	
	public Iterator<DataRecordConnector> applyLocalFilter(
			Iterator<DataRecordConnector> iterator, FilterTerms filterTerms ) {

		List<DataRecordConnector> finalList = new ArrayList<DataRecordConnector>();

		Iterator<FilterTerms.FilterJoin> it;


		if (filterTerms == null) {
			return iterator;
		}

		try {
			while (iterator.hasNext()) {
				DataRecordConnector dataRecordConnector = iterator.next();
				it = filterTerms.iterator();
				boolean addLine = true;
				while (it.hasNext()) {
					
					if( !addLine )
						break;
					
					FilterTerms.FilterJoin filterJoin = it.next();
					FilterTerm filterTerm = filterJoin.getTerm();
					Object val = filterTerm.getValue();
					String column = filterTerm.getDataField();
					if (val != null) {
						if (val instanceof String) {
							String sVal = val == null ? "" : val.toString()
									.toUpperCase();
							
							String sDisplayValue = dataRecordConnector
									.getAttribute(column).getDisplayValue();
							
							String sColValue = (String)dataRecordConnector
							.getAttribute(column).getValue();
							
							String sColumnValue = sDisplayValue == null ? ""
									: sDisplayValue.toUpperCase();
							
							
							if (filterTerm.getOperator() == FilterTerms.OPERATOR_CONTAINS) {
								if (!sColumnValue.contains(sVal)) {
									addLine = false;
								}
							} else if (filterTerm.getOperator() == FilterTerms.OPERATOR_NOT_CONTAINS) {
								if (sColumnValue.contains(sVal)) {
									addLine = false;
								}
							} else if (filterTerm.getOperator() == FilterTerms.OPERATOR_EQUAL) {
								if (!val.equals( sColValue==null?"":sColValue )) {
									addLine = false;
								}
							} else {
								System.err
										.println(MessageLocalizer.getMessage("LOCAL_FILTER_UNSUPORTED_STRING_FILTER"));
							}
						} else if (val instanceof java.util.Date) {
							Date dVal = (Date) val;
							Date dColumnValue = (Date) dataRecordConnector
									.getAttribute(column).getValue();
							if (filterTerm.getOperator() == FilterTerms.OPERATOR_EQUAL) {
								if (dVal.compareTo(dColumnValue) != 0) {
									addLine = false;
								}
							} else if (filterTerm.getOperator() == FilterTerms.OPERATOR_GREATER_THAN) {
								if (dVal.compareTo(dColumnValue) >= 0) {
									addLine = false;
								}
							} else if (filterTerm.getOperator() == FilterTerms.OPERATOR_LESS_THAN) {
								if (dVal.compareTo(dColumnValue) <= 0) {
									addLine = false;
								}
							} else {
								System.err
										.println(MessageLocalizer.getMessage("LOCAL_FILTER_UNSUPORTED_DATE_FILTER"));
							}
						} else if (val instanceof Boolean) {
							// Only supports OPERATOR_EQUAL
							String sVal = ((Boolean) val).booleanValue() ? "1"
									: "0";
							String sColumnValue = (String) dataRecordConnector
									.getAttribute(column).getValue();
							if (!sVal.equals(sColumnValue)) {
								addLine = false;
							}
						} else if (val instanceof BigDecimal) {
							BigDecimal nVal = (BigDecimal) val;
							BigDecimal nColumnValue = (BigDecimal) dataRecordConnector
									.getAttribute(column).getValue();
							if (filterTerm.getOperator() == FilterTerms.OPERATOR_EQUAL) {
								if (nColumnValue == null || nVal.compareTo(nColumnValue) != 0) {
									addLine = false;
								}
							} else if (filterTerm.getOperator() == FilterTerms.OPERATOR_GREATER_THAN) {
								if (nVal.compareTo(nColumnValue) >= 0) {
									addLine = false;
								}
							} else if (filterTerm.getOperator() == FilterTerms.OPERATOR_LESS_THAN) {
								if (nVal.compareTo(nColumnValue) <= 0) {
									addLine = false;
								}
							} else {
								System.err
										.println(MessageLocalizer.getMessage("LOCAL_FILTER_UNSUPORTED_BIGDECIMAL_FILTER"));
							}
						} else if (val instanceof Object[]) {
							BigDecimal nColumnValue = (BigDecimal) dataRecordConnector
									.getAttribute(column).getValue();
							Set<BigDecimal> bouis = new HashSet<BigDecimal>();
							Object[] aVals = (Object[]) val;
							for (int i = 0; i < aVals.length; i++) {
								bouis.add(new BigDecimal(aVals[i].toString()));
							}

							if (!bouis.contains(nColumnValue)) {
								addLine = false;
							}
						}
					}
					else {
						Object colValue = dataRecordConnector.getAttribute(column).getValue();
						if( colValue == null ) {
							addLine = false;
						}
					}
				}
				if (addLine) {
					finalList.add(dataRecordConnector);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalList.iterator();
	}

	public void setAggregateFieldsFromString(String aggregateFieldsString) {
		DataListConnector gridDataSource = null; 
		try {
			gridDataSource = this.getDataSource();
		} catch (RuntimeException e){
			//No dataSource defined yet, but we can proceed	
			//Here we shouldn't try to access the data source
		}
		
		if (gridDataSource != null
				&& (gridDataSource.dataListCapabilities() & DataListConnector.CAP_AGGREGABLE) > 0) {
			if (aggregateFieldsString != null
					&& !"".equalsIgnoreCase(aggregateFieldsString)) {
				// Convert String to HashMap
				HashMap<String, ArrayList<String>> tempMap = new HashMap<String, ArrayList<String>>();

				String[] aggregateSplit = aggregateFieldsString.split(";");
				for (int i = 0; i < aggregateSplit.length; i++) {
					String[] aggregateSplitNext = aggregateSplit[i].split("=");

					String key = aggregateSplitNext[0];

					String tempdetail = aggregateSplitNext[1];
					tempdetail = tempdetail.replace("[", "");
					tempdetail = tempdetail.replace("]", "");
					tempdetail = tempdetail.replaceAll(" ", "");

					String[] aggregateDetailSplit = tempdetail.split(",");

					ArrayList<String> tempValues = new ArrayList<String>();
					for (int j = 0; j < aggregateDetailSplit.length; j++) {

						tempValues.add(aggregateDetailSplit[j]);
					}

					tempMap.put(key, tempValues);
				}
				this.aggregateFields.setValue(tempMap);
			} else {
				this.aggregateFields.setValue(null);
			}
			((AggregableDataList) this.getDataSource())
					.setAggregateFields(this.aggregateFields.getValue());
		}
	}

	public String getAggregateFieldsString() {
		String result = "";

		if (this.getDataSource() != null
				&& (this.getDataSource().dataListCapabilities() & DataListConnector.CAP_AGGREGABLE) > 0) {
			if (this.aggregateFields.getValue() != null) {
				// Convert the HashMap to String
				Iterator<String> it = this.aggregateFields.getValue().keySet().iterator();

				while (it.hasNext()) {
					String curr = it.next();
					ArrayList<String> currList = this.aggregateFields.getValue().get(curr);

					if (!result.equalsIgnoreCase("")) {
						result += ";" + curr + "=" + currList.toString();
					} else {
						result = curr + "=" + currList.toString();
					}
				}
			}
		}
		return result;
	}

}
