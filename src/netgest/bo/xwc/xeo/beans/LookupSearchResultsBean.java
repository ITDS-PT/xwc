package netgest.bo.xwc.xeo.beans;

import netgest.bo.runtime.boObjectList;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;

public class LookupSearchResultsBean extends XEOBaseLookupList {

	private String fullTexSearch;
	
	public void setFullTextSearch(String search){
		this.fullTexSearch = search;
		
	}
	
	public String getFullTextSearch(){
		return fullTexSearch;
	}
	
	public DataListConnector getDataSource(){
		String boql = "select " + getSelectedObject();
		boObjectList list = boObjectList.list(getEboContext(), boql, new Object[0],1,boObjectList.PAGESIZE_DEFAULT,"",fullTexSearch,null,"",true,true);
		XEOObjectListConnector connector = new XEOObjectListConnector(list);
		((GridPanel)getViewRoot().findComponent(GridPanel.class)).setCurrentFullTextSearch(fullTexSearch);
		connector.setSearchText(fullTexSearch);
		return connector;
		
	}
	
}
