package netgest.bo.xwc.components.classic;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.el.MethodBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.SortTerms;
import netgest.bo.xwc.components.connectors.FilterTerms.FilterTerm;
import netgest.bo.xwc.components.connectors.SortTerms.SortTerm;
import netgest.bo.xwc.components.localization.ComponentMessages;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.model.Columns;
import netgest.bo.xwc.components.security.SecurableComponent;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMethodBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUICommand;
import netgest.bo.xwc.framework.components.XUIInput;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridPanel extends ViewerInputSecurityBase {

	private String childViewers;

	public static final String SELECTION_ROW = "ROW";
	public static final String SELECTION_MULTI_ROW = "MULTI_ROW";
	public static final String SELECTION_CELL = "CELL";

	private XUIBindProperty<DataListConnector> dataSource = new XUIBindProperty<DataListConnector>(
			"dataSource", this, DataListConnector.class);

	private XUIMethodBindProperty filterLookup = new XUIMethodBindProperty(
			"filterLookup", this, "#{viewBean.lookupFilterObject}");

	public XUIStateBindProperty<String> rowSelectionMode = new XUIStateBindProperty<String>(
			"rowSelectionMode", this, String.class);

	private XUIStateBindProperty<String> objectAttribute = new XUIStateBindProperty<String>(
			"objectAttribute", this, String.class);

	private XUIStateProperty<String> rowUniqueIdentifier = new XUIStateProperty<String>(
			"rowUniqueIdentifier", this, "BOUI");

	private XUIStateProperty<String> autoExpandColumn = new XUIStateProperty<String>(
			"autoExpandColumn", this);

	private XUIBaseProperty<Boolean> forceColumnsFitWidth = new XUIBaseProperty<Boolean>(
			"forceColumnsFitWidth", this, true);

	private XUIStateProperty<String> pageSize = new XUIStateProperty<String>(
			"pageSize", this, "50");

	private XUIBaseProperty<String> rowDblClickTarget = new XUIBaseProperty<String>(
			"rowDblClickTarget", this, "tab");

	private XUIBaseProperty<String> rowClickTarget = new XUIBaseProperty<String>(
			"rowClickTarget", this, "");

	private XUIBaseProperty<String> sActiveRow = new XUIBaseProperty<String>(
			"sActiveRow", this, null );;

	private XUIBaseProperty<String> currentFilters = new XUIBaseProperty<String>(
			"currentFilters", this);

	private String[] sSelectedRowsUniqueIdentifiers;

	private transient Column[] oGridColumns;

	private XUIStateProperty<String> layout = new XUIStateProperty<String>(
			"layout", this, "fit-parent");

	private XUIBaseProperty<String> height = new XUIBaseProperty<String>(
			"height", this, "250");

	private XUIBaseProperty<Boolean> autoHeight = new XUIBaseProperty<Boolean>(
			"autoHeight", this, false);

	private XUIBaseProperty<Integer> minHeight = new XUIBaseProperty<Integer>(
			"minHeight", this, 60);

	private XUIStateBindProperty<String> groupBy = new XUIStateBindProperty<String>(
			"groupBy", this, String.class);

	private XUIBindProperty<GridRowRenderClass> rowClass = new XUIBindProperty<GridRowRenderClass>(
			"rowClass", this, GridRowRenderClass.class);

	private XUIBindProperty<Boolean> enableGroupBy = new XUIBindProperty<Boolean>(
			"enableGroupBy", this, false, Boolean.class);

	private XUIBindProperty<Boolean> enableColumnSort = new XUIBindProperty<Boolean>(
			"enableColumnSort", this, true, Boolean.class);

	private XUIBindProperty<Boolean> enableColumnFilter = new XUIBindProperty<Boolean>(
			"enableColumnFilter", this, true, Boolean.class);

	private XUIBindProperty<Boolean> enableColumnHide = new XUIBindProperty<Boolean>(
			"enableColumnHide", this, true, Boolean.class);

	private XUIBindProperty<Boolean> enableColumnMove = new XUIBindProperty<Boolean>(
			"enableColumnMove", this, true, Boolean.class);

	private XUIBindProperty<Boolean> enableColumnResize = new XUIBindProperty<Boolean>(
			"enableColumnResize", this, true, Boolean.class);

	private XUIBindProperty<Boolean> enableHeaderMenu = new XUIBindProperty<Boolean>(
			"enableHeaderMenu", this, true, Boolean.class);

	private XUIBaseProperty<String> currentSortTerms = new XUIBaseProperty<String>(
			"currentSortTerms", this, null);

	private XUIMethodBindProperty	onRowDoubleClick 	= new XUIMethodBindProperty("onRowDoubleClick", this );
	
	private XUIMethodBindProperty	onRowClick 			= new XUIMethodBindProperty("onRowClick", this );

	private XUIMethodBindProperty	onSelectionChange	= new XUIMethodBindProperty("onSelectionChange", this );
	
	private XUIBindProperty<Boolean> autoReloadData = new XUIBindProperty<Boolean>(
			"autoReloadData", this, true,Boolean.class );
	
	private boolean forcedReloadData = false;
	
	private String currentFullTextSearch;

	private XUICommand filterLookupCommand;
	private XUIInput filterLookupInput;

	/**
	 * Return the XUICommand associated with the filter actions
	 * @return Return the XUICommand associated with the filter actions
	 */
	public XUICommand getFilterLookupCommand() {
		return this.filterLookupCommand;
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
		return true;
	}
	
	/**
	 * If the component is allready rendered on the client, set this component
	 * to only refresh the grid data
	 * 
	 * @return
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
	 * Set the current column filters with a JSON Object
	 * @param currentFilters String JSON Object
	 */
	public void setCurrentFilters(String currentFilters) {
		this.currentFilters.setValue(currentFilters);
	}

	/**
	 * Return a the current FilterTerms applied to the columns
	 * @return {@link FilterTerms}
	 */
	public FilterTerms getCurrentFilterTerms() {
		FilterTerms terms = null;

		String sCFilter = this.getCurrentFilters();

		try {
			JSONObject jFilters = new JSONObject(sCFilter);
			String[] names = JSONObject.getNames(jFilters);
			if (names != null) {
				for (String name : names) {

					JSONObject jsonColDef = jFilters.getJSONObject(name);
					JSONArray jsonColFilters = jsonColDef
							.getJSONArray("filters");

					String submitedType = jsonColDef.getString("type");

					boolean active = jsonColDef.getBoolean("active");

					for (int i = 0; active && i < jsonColFilters.length(); i++) {

						boolean bAddCodition = true;

						JSONObject jsonColFilter = jsonColFilters
								.getJSONObject(i);

						String submitedValue = jsonColFilter.optString("value");

						if (submitedValue != null) {
							Object value = null;
							Byte operator = null;

							if ("object".equals(submitedType)) {
								List<String> valuesList = new ArrayList<String>();

								JSONArray jArray = jsonColFilter
										.optJSONArray("value");
								if (jArray != null) {
									for (int z = 0; z < jArray.length(); z++) {
										valuesList.add(jArray.getString(z));
									}
									value = valuesList.toArray();
									operator = FilterTerms.OPERATOR_IN;
								}
								if (valuesList.size() == 0) {
									bAddCodition = false;
								}

							} else if ("list".equals(submitedType)) {
								List<String> valuesList = new ArrayList<String>();
								JSONArray jArray = jsonColFilter
										.getJSONArray("value");

								for (int z = 0; z < jArray.length(); z++) {
									valuesList.add(jArray.getString(z));
								}
								value = valuesList.toArray();
								operator = FilterTerms.OPERATOR_IN;

								if (valuesList.size() == 0) {
									bAddCodition = false;
								}

							} else if ("string".equals(submitedType)) {
								value = submitedValue;
								operator = FilterTerms.OPERATOR_CONTAINS;
							} else if ("date".equals(submitedType)) {
								SimpleDateFormat sdf = new SimpleDateFormat(
										"dd/MM/yyyy");
								try {
									value = sdf.parse(submitedValue);
								} catch (ParseException e) {
									e.printStackTrace();
									value = null;
								}
								String comp = jsonColFilter
										.getString("comparison");
								if ("lt".equals(comp))
									operator = FilterTerms.OPERATOR_LESS_THAN;
								else if ("eq".equals(comp))
									operator = FilterTerms.OPERATOR_EQUAL;
								else
									operator = FilterTerms.OPERATOR_GREATER_THAN;
							} else if ("boolean".equals(submitedType)) {
								value = Boolean.valueOf(submitedValue);
								operator = FilterTerms.OPERATOR_EQUAL;
							} else if ("numeric".equals(submitedType)) {
								String comp = jsonColFilter
										.getString("comparison");
								value = new BigDecimal(submitedValue);
								if ("lt".equals(comp))
									operator = FilterTerms.OPERATOR_LESS_THAN;
								else if ("eq".equals(comp))
									operator = FilterTerms.OPERATOR_EQUAL;
								else
									operator = FilterTerms.OPERATOR_GREATER_THAN;
							} else {
								value = null;
							}

							if (bAddCodition) {
								if (terms == null) {
									terms = new FilterTerms(new FilterTerm(
											name, operator, value));
								} else {
									terms.addTerm(FilterTerms.JOIN_AND, name,
											operator, value);
								}
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			// Error reading filters....
			e.printStackTrace();
		}
		return terms;
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
	 * Get the default height of the GridPanel when the Layout is set to a EmptyString
	 * @return 
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
	 * @return {@link boolean}  
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
		throw new RuntimeException("There are no dataSource defined. The GridPanel property dataSource is null or returned null!");
	}
	
	/**
	 * Set the objectAttribute ( bridgeName ) which this GridPanel is binding
	 * @param sObjectAttribute String with the attributeName (bridgeName) 
	 */
	public void setObjectAttribute(String sObjectAttribute) {
		this.objectAttribute.setValue(createValueExpression(sObjectAttribute,
				String.class));
		this.setDataSource("#{viewBean.currentData." + getObjectAttribute()
				+ ".dataList}");
		this.setRowClass("#{viewBean.rowClass}");
		this.setOnRowDoubleClick("#{viewBean.editBridge}");
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
		return rowDblClickTarget.getValue();
	}
	
	/**
	 * Set the current double click target
	 * @param rowDblClickTarget String with one of this values (tab - New Tab,self - Ajax Submit to the same page,window - Popup Window)
	 */
	public void setRowDblClickTarget(String rowDblClickTarget) {
		this.rowDblClickTarget.setValue(rowDblClickTarget);
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
	 * @param rowClickTarget String with one of this values (tab - New Tab,self - Ajax Submit to the same page,window - Popup Window)
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
		List<String> dataColumns;
		boolean bAddUniqueIdentifier = true;

		dataColumns = new ArrayList<String>();
		oColumns = getColumns();
		for (int i = 0; i < oColumns.length; i++) {
			sDataColumn = oColumns[i].getDataField();
			dataColumns.add(sDataColumn);
			if (getRowUniqueIdentifier().equals(sDataColumn)) {
				bAddUniqueIdentifier = false;
			}
		}
		if (bAddUniqueIdentifier) {
			dataColumns.add(getRowUniqueIdentifier());
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
	 * @return
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
	 * @return
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
	
	/**
	 * Check if the method reloadData was called
	 * @return true / false
	 */
	public boolean isMarkedToReloadData() {
		return this.forcedReloadData;
	}
	
	/**
	 * Set the onRowDoubleClick action
	 * @param onRowDoubleClick {@link MethodBinding} expression 
	 */
	public void setOnRowClick(String onRowDoubleClick) {
		this.onRowClick.setExpressionText( onRowDoubleClick );
	}
	
	/**
	 * Get the current action for onRowClick
	 * @return String whith {@link MethodBinding} expression
	 */
	public String getOnRowClick() {
		return this.onRowClick.getExpressionString();
	}

	/**
	 * Set the onSelectionChange action
	 * @param onSelectionChange {@link MethodBinding} expression 
	 */
	public void setOnSelectionChange(String onRowDoubleClick) {
		this.onSelectionChange.setExpressionText( onRowDoubleClick );
	}
	
	/**
	 * Get the current action for onSelectionChange
	 * @return String whith {@link MethodBinding} expression
	 */
	public String getOnSelectionChange() {
		return this.onSelectionChange.getExpressionString();
	}
	
	/**
	 * Set the action for onRowDoubleClick
	 * @param onRowDoubleClickExpr {@link MethodBinding} expression
	 */
	public void setOnRowDoubleClick(String onRowDoubleClickExpr) {
	
		this.onRowDoubleClick.setExpressionText( onRowDoubleClickExpr );

	}
	
	/**
	 * Get the current {@link MethodBinding} expression for onRowDoubleClick
	 * @return {@link MethodBinding} expression String
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

		SortTerms st = null;
		if (sSort != null) {
			String[] sSortDef = sSort.split("\\|");
			if (sSortDef.length == 2) {
				String sSortField = sSortDef[0];
				String sSortDir = sSortDef[1];
				st = new SortTerms();
				st.addSortTerm(sSortField,
						"DESC".equals(sSortDir) ? SortTerms.SORT_DESC
								: SortTerms.SORT_ASC);
			}
		}
		return st;
	}

	public String getCurrentFullTextSearch() {
		return this.currentFullTextSearch;
	}

	public void setCurrentFullTextSearch(String fullTextSearch) {
		this.currentFullTextSearch = fullTextSearch;
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

	public Iterator<DataRecordConnector> applyLocalSort(
			Iterator<DataRecordConnector> dataListIterator) {

		SortTerms sortTerms = getCurrentSortTerms();

		if (sortTerms != null && !sortTerms.isEmpty()) {

			List<DataRecordConnector> orderedList = new ArrayList<DataRecordConnector>();
			while (dataListIterator.hasNext()) {
				orderedList.add(dataListIterator.next());
			}

			SortTerm term = sortTerms.iterator().next();

			final String sSort = term.getField();
			final int direction = term.getDirection();

			Collections.sort(orderedList,
					new Comparator<DataRecordConnector>() {
						@SuppressWarnings("unchecked")
						public int compare(DataRecordConnector left,
								DataRecordConnector right) {
							Comparable<Comparable> sLeft, sRight;

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

							int ret = direction == SortTerms.SORT_ASC ? sLeft
									.compareTo(sRight) : sRight
									.compareTo(sLeft);
							return ret;
						}
					});
			dataListIterator = orderedList.iterator();
		}
		return dataListIterator;
	}

	public Iterator<DataRecordConnector> applyLocalFilter(
			Iterator<DataRecordConnector> iterator) {

		List<DataRecordConnector> finalList = new ArrayList<DataRecordConnector>();

		Iterator<FilterTerms.FilterJoin> it;

		FilterTerms filterTerms;

		filterTerms = getCurrentFilterTerms();

		if (filterTerms == null) {
			return iterator;
		}

		try {
			while (iterator.hasNext()) {
				DataRecordConnector dataRecordConnector = iterator.next();
				it = filterTerms.iterator();
				boolean addLine = true;
				while (it.hasNext()) {
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
							} else {
								System.err
										.println("Local Filter: Unsupported String filter");
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
										.println("Local Filter: Unsupported Date filter");
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
								if (nVal.compareTo(nColumnValue) != 0) {
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
										.println("Local Filter: Unsupported BigDecimal filter");
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

						if (addLine) {
							finalList.add(dataRecordConnector);
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalList.iterator();
	}

}
