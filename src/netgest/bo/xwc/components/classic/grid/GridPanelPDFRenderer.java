package netgest.bo.xwc.components.classic.grid;

import java.awt.Color;
import java.util.Iterator;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataListIterator;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.model.Column;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.headerfooter.RtfHeaderFooter;

public class GridPanelPDFRenderer {
	
	public void render( ServletRequest oRequest, ServletResponse oResponse, GridPanel oGrid ) {
    	oResponse.setContentType("application/pdf");
    	int fragmentsize = 200;
    	Document document = new Document(PageSize.A4, 10, 10, 10, 10);

    	Font font = new Font(Font.HELVETICA, 10, Font.NORMAL); // 1
    	font.setColor(new Color(0x92, 0x90, 0x83));
    	
    	String sTitle = GridPanelRenderer.getExportTitle( oGrid );
    	
		DataListConnector oDataSource = oGrid.getDataSource();
		oDataSource.setPage(1);
		oDataSource.setPageSize( Integer.MAX_VALUE );
		Iterator<DataRecordConnector> it = 
			GridPanelJSonRenderer.getDataListIterator(oGrid, oDataSource );
		
    	try { 
			// step2
			PdfWriter.getInstance(document, oResponse.getOutputStream() );
			// step3
			
			// Header
        	HeaderFooter header = new HeaderFooter(new Phrase(new Chunk(sTitle, new Font(Font.HELVETICA, 10, Font.BOLD))),false);
        	header.setAlignment(Element.ALIGN_CENTER);
        	header.setBorder(0);
        	document.setHeader(header);
			
			document.addTitle( sTitle );
        	
        	
        	// Footer
        	// Create a new Paragraph for the footer
        	Paragraph par = new Paragraph("Page ");
        	par.setAlignment(Element.ALIGN_CENTER);

        	// Add the RtfPageNumber to the Paragraph
        	//par.add(new RtfPageNumber());
        	            
        	// Create an RtfHeaderFooter with the Paragraph and set it
        	// as a footer for the document
        	RtfHeaderFooter footer = new RtfHeaderFooter(par);
        	document.setFooter(footer);
        	
        	footer.setBorder(0);
        	
			document.open();
			// step4
			Font fontH = FontFactory.getFont("Verdana", 6, Font.BOLD,
					Color.BLACK);

			Font fontB = FontFactory.getFont("Verdana", 6, Font.NORMAL,
					Color.BLACK);

			Column[] oGridColumns = oGrid.getColumns();
			PdfPTable table = new PdfPTable( oGridColumns.length );
			//table.setWidthPercentage(100f);
			table.setHorizontalAlignment( Element.ALIGN_LEFT );

			int iWidths[] = new int[ oGridColumns.length ];
			int iTableWidth = 0;
			for( int i=0;i < oGridColumns.length; i++ ) {
				
				
				String s = oGridColumns[i].getWidth();
				try {
					int iWidth = Integer.parseInt( s );
					iWidths[i] = (int)(iWidth/1.8);
					iTableWidth += iWidths[i]; 
				} catch (Exception e) {
					// TODO: handle exception
				}
				
    			String sLabel = GridPanel.getColumnLabel( oDataSource, oGridColumns[i] );
				Paragraph p = new Paragraph( sLabel , fontH);
    			PdfPCell h1 = new PdfPCell(p);
    			h1.setBackgroundColor( Color.LIGHT_GRAY );
    			table.addCell(h1);
			}
			table.setWidths( iWidths );
			table.setHeaderRows(1);
			table.setLockedWidth(true);
			table.setTotalWidth( iTableWidth );

			int row =0;
			while( it.hasNext() ) {
				
				DataRecordConnector oRecordConnector = it.next();
				row++;
    			PdfPCell cell;
				if (row % fragmentsize == fragmentsize - 1) {
					document.add(table);
					table.deleteBodyRows();
					table.setSkipFirstHeader(true);
					
					System.out.println( "Generation PDF Row:" + row );
					
				}
    			for( int i=0;i < oGridColumns.length; i++ ) {
    				DataFieldConnector oAtt;
    				oAtt = oRecordConnector.getAttribute( oGridColumns[i].getDataField() );
    				if( oAtt != null ) {
        				cell = 
        					new PdfPCell( 
        							new Paragraph(
        								oAtt.getDisplayValue(), 
	        							fontB
        							)
        						);
    				}
    				else {
        				cell = 
        					new PdfPCell( 
        							new Paragraph(
        								"«« Invalid [" + oGridColumns[i].getDataField() + "] »»", 
	        							fontB
        							)
        						);
    				}
    				table.addCell(cell);
    			}
			}
			
			// Create a dummy line if the document is empty
			// Turn around of exception of itext. The document have no pages;
			if( row == 0 ) {
    			PdfPCell cell;
    			for( int i=0;i < oGridColumns.length; i++ ) {
    				cell = 
    					new PdfPCell( 
    							new Paragraph(
    								"", 
        							fontB
    							)
    						);
    				table.addCell(cell);
    			}
			}
			document.add(table);
			
		} catch (Exception de) {
			de.printStackTrace();
		}
		// step5
		document.close();

    }

}
