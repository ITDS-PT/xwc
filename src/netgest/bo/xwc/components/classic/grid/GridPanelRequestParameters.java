package netgest.bo.xwc.components.classic.grid;

public class GridPanelRequestParameters {
	private String groupByValue;
	private int	   page;
	private int    pageSize;
	
	private int	   start;
	private int	   limit;
	
	public String getGroupByValue() {
		return groupByValue;
	}
	public void setGroupByValue(String groupByValue) {
		this.groupByValue = groupByValue;
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
	
	
}

