package netgest.bo.xwc.components.classic.grid;


public interface WebRequest {

	public enum GridParameter implements WebParameter {
		  START("start")
		, LIMIT("limit")
		, SELECTED_ROWS("selectedRows")
		, ACTIVE_ROW("activeRow") 
		, GROUP_BY ("groupBy")
		, AGGREGATE ("aggregateField")
		, TOOLBAR_VISIBLE ("toolBarVisible")
		, GROUP_BY_LEVEL ("groupByLevel")
		, GROUP_BY_PARENT_VALUES ("groupByParentValues")
		, COLUMNS_CONFIG ("columnsConfig")  
		, EXPANDED_GROUPS ("expandedGroups")
		, FULL_TEXT ("fullText")
		, SORT ("sort")
		, FILTERS ("filters")
		, DATASOURCE_CHANGE ("dataSourceChange") //Whether the DataSource was changed or not
		, PAGES_ALL_SELECTED ("pagesAllSelected")
		;
		
		private String name;
		
		private GridParameter(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	}
	
	public String getParameter( String name );
	
	public String getParameter(WebParameter param);

	public String[] getParameterValues( String name );

	public String[] getParameterValues( WebParameter param );
	
}
