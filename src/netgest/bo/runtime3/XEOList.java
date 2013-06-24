package netgest.bo.runtime3;

import java.util.List;

import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.connectors.FilterTerms;
import netgest.bo.xwc.components.connectors.SortTerms;

public interface XEOList {

	public boObject getObject(int rowPos) throws boRuntimeException;

	public void execute() throws boRuntimeException;

	public boDefHandler getBoDefinition();

	public void setQuery(String boql, List<Object> boqlArguments);

	public void setQuery(String boql);

	public void setSortTerms(SortTerms sortTerms);

	public void setSearchText(String searchText);

	public void setFilterTerms(FilterTerms filterTerms);

	public void setPageSize(int pageSize);

	public void setPage(int pageNo);

	public void refresh() throws boRuntimeException;

	public int getRowCount();

	public long getRecordCount();

	public int getPageSize();

	public int getPage();

	public int findObject(long boui);
	
	public void execute( String query );

	public void execute( String query, List<Object> queryArguments );

	public void execute( String query, int page, int pageSize );

	public void execute( String query, List<Object> queryArguments, int page, int pageSize );
	
}
