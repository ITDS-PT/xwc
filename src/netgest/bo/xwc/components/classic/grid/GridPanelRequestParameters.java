package netgest.bo.xwc.components.classic.grid;

public class GridPanelRequestParameters {
	
	
	private int	   page;
	private int    pageSize;
	
	private int	   start;
	private int	   limit;
	
	private Object[] parentValues;
	private String[] groupBy;
	private String aggregateParameter;
	private int 	 groupByLevel;
	
	/**
	 * Whether the toolbar displaying the currents groups is hidden or not
	 */
	private Boolean groupToolBarVisible = null;
	
	public Boolean isGroupToolBarVisible(){
		return groupToolBarVisible;
	}
	
	public void setGroupToolBarVisible(boolean newValue){
		this.groupToolBarVisible = Boolean.valueOf(newValue);
	}
	
	
	/**
	 * @return the aggregateParameter
	 */
	public String getAggregateParameter() {
		return aggregateParameter;
	}
	
	/**
	 * @param summaryParameter the aggregateParameter to set
	 */
	public void setAggregateParameter(String aggregateParameter) {
		this.aggregateParameter = aggregateParameter;
	}
	
	/**
	 * @return the groupByLevel
	 */
	public int getGroupByLevel() {
		return groupByLevel;
	}

	/**
	 * @param groupByLevel the groupByLevel to set
	 */
	public void setGroupByLevel(int groupByLevel) {
		this.groupByLevel = groupByLevel;
	}

	public Object[] getGroupParentValues() {
		return this.parentValues;
	}

	/**
	 * @param groupBy the groupBy to set
	 */
	public void setGroupBy(String[] groupBy) {
		this.groupBy = groupBy;
	}

	/**
	 * @param parentValues the parentValues to set
	 */
	public void setParentValues(Object[] parentValues) {
		this.parentValues = parentValues;
	}

	/**
	 * @return the parentValues
	 */
	public Object[] getParentValues() {
		return parentValues;
	}

	/**
	 * @return the groupBy
	 */
	public String[] getGroupBy() {
		return groupBy;
	}

	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}

	private boolean dataSourceChanged = false;
	
	public void dataSourceChanged() {
		this.dataSourceChanged = true;
	}
	
	public boolean isDataSourceChanged(){
		return dataSourceChanged;
	}
	
	
}

