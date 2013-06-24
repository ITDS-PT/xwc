package netgest.bo.xwc.components.classic.grid.metadata;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.grid.GridPanelRequestParameters;
import netgest.bo.xwc.components.connectors.DataListConnector;

/**
 * Class that aids generating the correct JSON when a GridPanel should not display counter values
 * 
 */
public class GridPanelNormalJSONRendererMetadata
{
	
	private GridPanel oGrid;
	private DataListConnector dataConnector;
	private GridPanelRequestParameters reqParam;
	private long rowCount;
	
	public GridPanelNormalJSONRendererMetadata(GridPanel grid, DataListConnector conn, GridPanelRequestParameters reqParams, long rowCount){
		oGrid = grid;
		dataConnector = conn;
		this.reqParam = reqParams;
		this.rowCount = rowCount;
	}
	
	public String outputJSON(){
		
		int cursor = -1;
        
		GridPanelCounterMetadata metadata = new GridPanelCounterMetadata();
		
        String groupId = getRequestedGroupId();
        
        if (oGrid.getShowCounters()){
        	
        	metadata.setTotalCount( rowCount > -1?rowCount:dataConnector.getRecordCount() );
        	if (reqParam != null){
    			if (reqParam.isDataSourceChanged()){
    				cursor = 0;
    			}
    		}
	        
	        if (cursor > -1){
        		metadata.setCursor( cursor );
        	}
	        
        } else {
        	
        	if (!dataConnector.hasMorePages()){
        		oGrid.setRecordCount( groupId, calculateTotalRecords(dataConnector, oGrid, groupId) );
        	} else {
        		if (reqParam != null){
        			if (reqParam.isDataSourceChanged()){
        				oGrid.resetRecordCount();
        				cursor = 0;
        			}
        		}
        	}
        	
        	metadata.setTotalCount( calculateTotalRecords(dataConnector, oGrid, groupId) );
        	metadata.setHasMorePages( dataConnector.hasMorePages() );
        	if (cursor > -1){
        		metadata.setCursor( cursor );
        	}
        	if (!dataConnector.hasMorePages()){
        		metadata.setLastPage( true );
        		metadata.setLastPageNumber( dataConnector.getPage() );
        	} else {
        		if (oGrid.getRecordCount(groupId) != null){
        			metadata.setLastPageNumber( oGrid.getRecordCount(groupId) );
        		}
        	}
        	
        }
        
        return metadata.serialize();
	}

	protected int getCounterValue() {
		int cnt = 0;
        if (oGrid.getShowCounters())
        	cnt = dataConnector.getRecordCount();
		return cnt;
	}



	protected String getRequestedGroupId() {
		String groupId = oGrid.getClientId();
        if (reqParam.getParentValues() != null){
        	groupId = org.apache.commons.lang.StringUtils.join( reqParam.getParentValues() );
        }
		return groupId;
	}
	
	private int calculateTotalRecords(DataListConnector oDataSource, GridPanel grid, String groupdId) {
    	if (grid.getRecordCount(groupdId) != null && grid.getRecordCount(groupdId) > 0)
    		return grid.getRecordCount(groupdId);
    	if (!oDataSource.hasMorePages())
    		return ((oDataSource.getPage() - 1) * oDataSource.getPageSize()) + oDataSource.getRowCount();
    	else
			return 0;
	}
	
	

	
	
}
