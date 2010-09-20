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
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;


/**
 * 
 * The {@link Rows} components creates a tabular structure so that form 
 * elements can be placed inside. 
 * The purpose is to have those form components aligned in any way desired
 * 
 * @author João Carreira
 *
 */
public class Rows extends XUIComponentBase
{
    /**
     * The spacing between cells
     */
    XUIViewStateProperty<Integer> cellSpacing = new XUIViewStateProperty<Integer>( "cellSpacing", this, 5 );
    /**
     * THe padding of the text inside cells
     */
    XUIViewStateProperty<Integer> cellPadding = new XUIViewStateProperty<Integer>( "cellPadding", this, 5 );

    /**
     * The width of the table (in percentage or pixels)
     */
    XUIViewStateProperty<String> width 			= new XUIViewStateProperty<String>( "width", this, "99.9%" );
    /**
     * The height of the table (in percentage or pixels)
     */
    XUIViewStateProperty<String> height 		= new XUIViewStateProperty<String>( "height", this, "99.9%" );

    /**
     * The number of columns in this table (to allow better formating)
     */
    XUIViewProperty<String> columns 		= new XUIViewProperty<String>( "columns", this, "2" );
    /**
     * The width of the columns in this table (a list of comma-separated widths for
     * the columns, using the same number of columns as defined in the "columns" property.
     * 
     * eg: columnWidths='100px,150px,300px' or columnWidths='33%,33%,33%'
     */
    XUIViewProperty<String> columnWidths 	= new XUIViewProperty<String>( "columnWidths", this, "auto" );
    /**
     * The position of the labels inside the {@link Rows} component
     */
    @Values({"left","top"})
    XUIViewProperty<String> labelPosition 	= new XUIViewProperty<String>( "labelPosition", this, "left" );
    /**
     * The width of the rendered labels inside the {@link Rows} component
     */
    XUIViewProperty<Integer> labelWidth 	= new XUIViewProperty<Integer>( "labelWidth", this, 100 );
    
    @Override
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
