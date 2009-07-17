package netgest.bo.xwc.components.classic;

import static netgest.bo.xwc.components.HTMLAttr.CELLPADDING;
import static netgest.bo.xwc.components.HTMLAttr.CELLSPACING;
import static netgest.bo.xwc.components.HTMLAttr.WIDTH;
import static netgest.bo.xwc.components.HTMLTag.COL;
import static netgest.bo.xwc.components.HTMLTag.COLGROUP;
import static netgest.bo.xwc.components.HTMLTag.TABLE;
import static netgest.bo.xwc.components.HTMLTag.TBODY;

import java.io.IOException;

import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class Rows extends XUIComponentBase
{
    XUIStateProperty<Integer> cellSpacing = new XUIStateProperty<Integer>( "cellSpacing", this, 5 );
    XUIStateProperty<Integer> cellPadding = new XUIStateProperty<Integer>( "cellPadding", this, 5 );

    XUIStateProperty<String> width 			= new XUIStateProperty<String>( "width", this, "99.9%" );
    XUIStateProperty<String> height 		= new XUIStateProperty<String>( "height", this, "99.9%" );

    XUIBaseProperty<String> columns 		= new XUIBaseProperty<String>( "columns", this, "2" );
    XUIBaseProperty<String> columnWidths 	= new XUIBaseProperty<String>( "columnWidths", this, "auto" );
    XUIBaseProperty<String> labelPosition 	= new XUIBaseProperty<String>( "labelPosition", this, "left" );
    XUIBaseProperty<Integer> labelWidth 	= new XUIBaseProperty<Integer>( "labelWidth", this, 100 );
    
    public boolean getRendersChildren()
    {
        return true;
    }

    public void setLabelWidth( int labelWidth ) {
        this.labelWidth.setValue( labelWidth );
    }

    public int getLabelWidth() {
        return labelWidth.getValue();
    }

    public void setLabelPosition(String labelPosition) {
        this.labelPosition.setValue( labelPosition );
    }

    public String getLabelPosition() {
        return labelPosition.getValue();
    }

    
    public void setCellSpacing(int cellSpacing) {
        this.cellSpacing.setValue( cellSpacing );;
    }

    public int getCellSpacing() {
        return cellSpacing.getValue();
    }

    public void setCellPadding(int cellPadding) {
        this.cellPadding.setValue( cellPadding );
    }

    public int getCellPadding() {
        return cellPadding.getValue();
    }

    public void setWidth(String width) {
        this.width.setValue( width );
    }

    public String getWidth() {
        return width.getValue();
    }

    public void setColumns(String width) {
        this.columns.setValue( width );
    }

    public String getColumnWidths() {
        return columnWidths.getValue();
    }
    
    public void setColumnWidths(String width) {
        this.columnWidths.setValue( width );
    }

    public String getColumns() {
        return columns.getValue();
    }
    
    
    public void setHeight(String height) {
        this.height.setValue( height );
    }

    public String getHeight() {
        return height.getValue();
    }

    public static final class XEOHTMLRenderer extends XUIRenderer {

        @Override
        public void encodeBegin(XUIComponentBase component) throws IOException {
            XUIResponseWriter w = getResponseWriter();
            
            Rows oRowsComponent = (Rows)component;
            w.startElement( TABLE,component);
            w.writeAttribute(HTMLAttr.ID, component.getClientId(), null);
            w.writeAttribute( CELLSPACING, oRowsComponent.getCellSpacing(), null );
            w.writeAttribute( CELLPADDING, oRowsComponent.getCellPadding(), null );
            w.writeAttribute( HTMLAttr.STYLE, "table-layout:fixed;width:" + oRowsComponent.getWidth(), null ); 

            
            int cols = Integer.parseInt( oRowsComponent.getColumns() );
            
            String colWidths = oRowsComponent.getColumnWidths();
            
            int perc = 100 / cols;
            
            w.startElement( COLGROUP, oRowsComponent);
            
            if( "auto".equals( colWidths ) ) {
	            for( int i=0; i < cols; i++ ) {
	                w.startElement(COL, component );
		            w.writeAttribute(WIDTH, perc + "%", null );
		            w.endElement( COL );  
	            }
            } else {
            	String[] colWith = colWidths.split(",");
	            for( int i=0; i < cols; i++ ) {
	                w.startElement(COL, component );
		            w.writeAttribute(WIDTH, colWith[i], null );
		            w.endElement( COL );  
	            }
            }
            w.endElement( COLGROUP );
            w.startElement( TBODY, component );

        }


        @Override
        public void encodeEnd(XUIComponentBase component) throws IOException {
            XUIResponseWriter w = getResponseWriter();
            w.endElement( "tbody" );
            w.endElement( "table" );

            Layouts.registerComponent(w, component, Layouts.LAYOUT_FORM_LAYOUT );
            
        }
    }

}
