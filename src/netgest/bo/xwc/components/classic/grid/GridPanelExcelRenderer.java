package netgest.bo.xwc.components.classic.grid;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.framework.XUIResponseWriter;

public class GridPanelExcelRenderer {

	public void getExcel(  ServletRequest oRequest, ServletResponse oResponse, GridPanel oGrid  ) {

		String sTitle = GridPanelRenderer.getExportTitle( oGrid );
			
		DataListConnector oDataSource = oGrid.getDataSource();
		oDataSource.setPage(1);
		oDataSource.setPageSize( Integer.MAX_VALUE );
		Iterator<DataRecordConnector> it = 
			GridPanelJSonRenderer.getDataListIterator(oGrid, oDataSource );

		oResponse.setContentType("application/vnd.ms-excel");
		
    	((HttpServletResponse)oResponse).setHeader("Content-Disposition","attachment; filename="+ sTitle+".xls"  );
    	
    	try {
        	PrintWriter pw = ((HttpServletResponse)oResponse).getWriter();
			XUIResponseWriter w = new XUIResponseWriter( pw, "application/vnd.ms-excel", "UTF-8" );
			// step2
			
			// step3
			w.startElement( HTMLTag.HTML, null );
			w.startElement( HTMLTag.BODY, null );
			w.startElement( HTMLTag.H3, null );
			w.writeText( sTitle, null );
			w.endElement( HTMLTag.H3 );
			
			Column[] oGridColumns = oGrid.getColumns();
			
			w.startElement( HTMLTag.TABLE, null );
			
			w.writeAttribute( HTMLAttr.BORDER,"1", null );
			
			
			String sLabel;
			w.startElement( HTMLTag.TR, null );
			for( int i=0;i < oGridColumns.length; i++ ) {
				if( !oGridColumns[i].isHidden() ) {
	    			w.startElement( HTMLTag.TH, null );
	    			sLabel = GridPanel.getColumnLabel( oDataSource, oGridColumns[i] );
	    			if( sLabel != null )
	    				w.writeText( HTMLEntityDecoder.htmlEntityToChar( sLabel ), null );
	    			else
	    				w.writeText( "", null );
	    				
	    			w.endElement( HTMLTag.TH );
				}
			}
			w.endElement( HTMLTag.TR );

			oDataSource.refresh();

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
			
			int row =0;
			String sValue;
			while( it.hasNext() ) {
    			w.startElement( HTMLTag.TR, null );
				DataRecordConnector oRecordConnector = it.next();
				row++;
    			for( int i=0;i < oGridColumns.length; i++ ) {
    				if( !oGridColumns[i].isHidden() ) {
	    				DataFieldConnector oAtt;
	    				oAtt = oRecordConnector.getAttribute( oGridColumns[i].getDataField() );
	        			w.startElement( HTMLTag.TD, null );
	        			GridColumnRenderer colRender = columnRenderer.get( oGridColumns[i].getDataField() ); 
	        			if( colRender != null ) {
	        				sValue = colRender.render( oGrid, oRecordConnector, oAtt );
	        			}
	        			else if( oAtt != null ) {
	            			sValue = oAtt.getDisplayValue();
	        			}
	        			else {
	        				sValue = null;
	        			}
	        		
	        			if( sValue != null ) {
	        				if( oGridColumns[i].isContentHtml() ) {
	        					sValue = sValue.replaceAll( "<[a-zA-Z\\/][^>]*>", "");
	        					sValue = HTMLEntityDecoder.htmlEntityToChar( sValue );
	        				}
	    					w.writeText( sValue, null );
	        			}
	        			w.endElement( HTMLTag.TD );
    				}
    			}
    			w.endElement( HTMLTag.TR );
			}
			w.endElement( HTMLTag.BODY );
			w.endElement( HTMLTag.HTML );

    		w.endDocument();
    		w.flush();
    	
    	} catch (Exception de) {
			de.printStackTrace();
		}
		// step5
		
    	
	}

}
