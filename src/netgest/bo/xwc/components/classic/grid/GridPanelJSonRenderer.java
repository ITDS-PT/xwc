package netgest.bo.xwc.components.classic.grid;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.data.JavaScriptArrayProvider;
import netgest.bo.xwc.components.model.Column;

public class GridPanelJSonRenderer {
	
    public StringBuilder buildDataArray( GridPanel oGrid, DataListConnector oDataSource,Iterator<DataRecordConnector> dataIterator, int start, int limit ) {
        JavaScriptArrayProvider oJsArrayProvider;
        String[] oColumns;
        oColumns = oGrid.getDataColumns();
        
        Map<String,GridColumnRenderer> columnRenderer = new HashMap<String,GridColumnRenderer>();
        Column[] oAttributeColumns = oGrid.getColumns();
        for( Column gridCol : oAttributeColumns ) {
        	if( gridCol != null && gridCol instanceof ColumnAttribute ) {
        		GridColumnRenderer r = ((ColumnAttribute)gridCol).getRenderer();
        		if( r != null ) {
        			columnRenderer.put( gridCol.getDataField(), r );
        		}
        	}
        }
        
        String[] selRows = new String[] { oGrid.getActiveRowIdentifier() }; 
        
        oJsArrayProvider = new JavaScriptArrayProvider( dataIterator, oColumns, start, limit );
        
        StringBuilder s = new StringBuilder(200);
        s.append( '{' );
        s.append( oGrid.getId() ); 
        s.append( ":" );
        oJsArrayProvider.getJSONArray( s, oGrid, oGrid.getRowUniqueIdentifier(), selRows, columnRenderer );

        s.append(",totalCount:").append( oDataSource.getRecordCount() );

        s.append('}');
        
        return s;
        
    }
	
    public void getJsonData( ServletRequest oRequest, ServletResponse oResponse, 
    		GridPanelRequestParameters reqParam, GridPanel oGrid ) throws IOException {
    	
        Iterator<DataRecordConnector> dataIterator;
        oResponse.setContentType( "text/plain;charset=utf-8" );
        PrintWriter w = oResponse.getWriter();
        DataListConnector oDataCon = oGrid.getDataSource();
        if( oDataCon != null ) {
        	if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_PAGING) == DataListConnector.CAP_PAGING )  {
        		oDataCon.setPageSize( reqParam.getLimit() ); 
                oDataCon.setPage( reqParam.getPage() );
                dataIterator = getDataListIterator(oGrid, oDataCon);
                StringBuilder oStrBldr = buildDataArray( oGrid, oDataCon, dataIterator, 0, reqParam.getLimit() );
                w.print( oStrBldr );
        	} else {
                dataIterator = getDataListIterator(oGrid, oDataCon);
            	StringBuilder oStrBldr = buildDataArray( oGrid, oDataCon, dataIterator, reqParam.getStart(), reqParam.getLimit() );
                w.print( oStrBldr );
        	}
        }
        else {
            w.print( "Grid data source is invalid!" );
        }
        
    }
    
    protected static Iterator<DataRecordConnector> getDataListIterator( GridPanel oGrid, DataListConnector oDataCon ) {

		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_FULLTEXTSEARCH) > 0 )
			oGrid.applyFullTextSearch( oDataCon );
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_FILTER) > 0 )
				oGrid.applyFilters( oDataCon );
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_SORT) > 0 )
			oGrid.applySort( oDataCon );
		
        oDataCon.refresh();
        
        Iterator<DataRecordConnector> dataIterator;
        dataIterator = oDataCon.iterator();
        
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_FILTER) == 0 )
			dataIterator = oGrid.applyLocalFilter( dataIterator );
		if( (oDataCon.dataListCapabilities() & DataListConnector.CAP_SORT) == 0 )
			dataIterator = oGrid.applyLocalSort( dataIterator );
        
        return dataIterator;
    }

}
