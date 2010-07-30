package netgest.bo.xwc.components.classic.renderers;

import static netgest.bo.xwc.components.HTMLAttr.WIDTH;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.COLGROUP;

import java.io.IOException;

import netgest.bo.xwc.components.classic.Rows;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.components.XUIComponentBase;

public class XMLRowsRenderer extends XMLBasicRenderer {

	@Override
	public void encodeBegin (XUIComponentBase comp) throws IOException{
		super.encodeBegin(comp);
		
		XUIResponseWriter w = getResponseWriter();
		Rows oRowsComponent = (Rows) comp;
		
		 int cols = Integer.parseInt( oRowsComponent.getColumns() );
         
         String colWidths = oRowsComponent.getColumnWidths();
         
         int perc = 100 / cols;
         
         w.startElement( COLGROUP, oRowsComponent);
         
         if( "auto".equals( colWidths ) ) {
	            for( int i=0; i < cols; i++ ) {
	                w.startElement(COL, comp );
		            w.writeAttribute(WIDTH, perc + "%", null );
		            w.endElement( COL );  
	            }
         } else {
         	String[] colWith = colWidths.split(",");
	            for( int i=0; i < cols; i++ ) {
	                w.startElement(COL, comp );
		            w.writeAttribute(WIDTH, colWith[i], null );
		            w.endElement( COL );  
	            }
         }
         w.endElement( COLGROUP );
         		
	}
	
	
	@Override
	public void encodeEnd (XUIComponentBase comp) throws IOException{
		super.encodeEnd(comp);
	}
	
}
