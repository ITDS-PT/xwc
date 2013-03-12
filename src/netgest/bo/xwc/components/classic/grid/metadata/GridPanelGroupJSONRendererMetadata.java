package netgest.bo.xwc.components.classic.grid.metadata;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.grid.GridPanelRequestParameters;
import netgest.bo.xwc.components.connectors.DataGroupConnector;

/**
 * Class that aids generating the correct JSON when a GridPanel should not display counter values
 * 
 */
public class GridPanelGroupJSONRendererMetadata
{
	
	private GridPanel oGrid;
	private DataGroupConnector dataConnector;
	private GridPanelRequestParameters reqParam;
	
	public GridPanelGroupJSONRendererMetadata(GridPanel grid, DataGroupConnector conn, GridPanelRequestParameters reqParams){
		oGrid = grid;
		dataConnector = conn;
		this.reqParam = reqParams;
	}
	
	public String outputJSON(){
		
		int cnt = getCounterValue();
        String groupId = getRequestedGroupId();
		
        GridPanelCounterMetadata metadata = new GridPanelCounterMetadata();
        
		if (oGrid.getShowCounters()){
			
			metadata.setTotalCount( cnt );
			metadata.setCursor( (dataConnector.getPage() - 1 ) *  dataConnector.getPageSize() );
		    
        } else {
        	if (!dataConnector.hasMorePages()){
        		oGrid.setRecordCount( groupId, calculateTotalRecords(dataConnector, oGrid, groupId) );
        	} else {
        		if (reqParam.isDataSourceChanged()){
        			oGrid.resetRecordCount();
        		}
        	}
        	
        	metadata.setTotalCount( calculateTotalRecords(dataConnector, oGrid, groupId) );
        	metadata.setHasMorePages( dataConnector.hasMorePages() );
        	metadata.setCursor( (dataConnector.getPage() - 1 ) *  dataConnector.getPageSize() );
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
	
	private int calculateTotalRecords(DataGroupConnector oDataSource, GridPanel grid, String groupdId) {
    	if (grid.getRecordCount(groupdId) != null && grid.getRecordCount(groupdId) > 0)
    		return grid.getRecordCount(groupdId);
    	if (!oDataSource.hasMorePages())
    		return ((oDataSource.getPage() - 1) * oDataSource.getPageSize()) + oDataSource.getRowCount();
    	else
			return 0;
	}
	
	

	
	
}
