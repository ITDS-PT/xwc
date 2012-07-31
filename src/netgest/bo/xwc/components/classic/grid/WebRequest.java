package netgest.bo.xwc.components.classic.grid;


public interface WebRequest {

	public enum GridParameter{
		  START("start")
		, LIMIT("limit")
		, SELECTED_ROWS("selectedRows")
		, ACTIVE_ROW("activeRow") 
		, GROUP_BY ("groupBy")
		, AGGREGATE ("aggregateField")
		, TOOLBAR_VISIBLE ("toolBarVisible")
		, GROUP_BY_LEVEL ("groupByLevel")
		, GROUP_BY_PARENT_LEVELS ("groupByParentValues")
		, COLUMNS_CONFIG ("columnsConfig")  
		, EXPANDED_GROUPS ("expandedGroups")
		, FULL_TEXT ("fullText")
		, SORT ("sort")
		, FILTERS ("filters")
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
	
	public String getParameter(GridParameter param);

	public String[] getParameterValues( String name );

	public String[] getParameterValues( GridParameter param );
	
}