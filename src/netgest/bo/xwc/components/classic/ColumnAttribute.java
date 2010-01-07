package netgest.bo.xwc.components.classic;

import javax.el.ValueExpression;

import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
/**
 * This component represents a Column in a Grid panel
 * It must reside inside of a Columns element in the viewer
 * 
 * @author jcarreira
 *
 */
public class ColumnAttribute extends XUIComponentBase implements Column {
	
    private XUIBaseProperty<String>         	dataField   = new XUIBaseProperty<String>( "dataField", this );
    private XUIStateBindProperty<String>    	label       = new XUIStateBindProperty<String>( "label", this, String.class );
    private XUIBaseProperty<String>    			width       = new XUIBaseProperty<String>( "width", this );
	private XUIBaseProperty<Boolean>    		searchable  = new XUIBaseProperty<Boolean>( "searchable", this, true );
    private XUIBaseProperty<Boolean>    		sortable    = new XUIBaseProperty<Boolean>( "sortable", this, true );
    private XUIBaseProperty<Boolean>    		groupable	= new XUIBaseProperty<Boolean>( "groupable", this, true );
	private XUIBaseProperty<Boolean>    		hideable 	= new XUIBaseProperty<Boolean>( "hideable", this, true );
    private XUIBaseProperty<Boolean>    		hidden  	= new XUIBaseProperty<Boolean>( "hidden", this, false );
	private XUIBaseProperty<Boolean>    		resizable   = new XUIBaseProperty<Boolean>( "resizable", this, true );
	private XUIBaseProperty<Boolean>    		contentHtml   = new XUIBaseProperty<Boolean>( "contentHtml", this, false );

    private XUIBindProperty<String>             lookupViewer= new XUIBindProperty<String>( "lookupViewer", this, String.class );

    private XUIBindProperty<GridColumnRenderer> renderer    = new XUIBindProperty<GridColumnRenderer>( "renderer", this, GridColumnRenderer.class );

    
    /**
     * Set if the column can be hidden by the user
     * @param sBooleanText true/false or a {@link ValueExpression}
     */
	public void setHideable( String sBooleanText ) {
		this.hideable.setValue( Boolean.parseBoolean( sBooleanText ) );
	}
	
	/**
	 * Getter for the property hideable
	 * 
	 * @return true/false
	 */
	public boolean isHideable() {
		return hideable.getValue();
	}
	
	/**
	 * Set the column is hidden but the user can select the column from the column list
	 * @param sBooleanText true/false
	 */
	public void setHidden( String sBooleanText ) {
		this.hidden.setValue( Boolean.parseBoolean( sBooleanText ) );
	}
	
	/**
	 * Getter for the property hidden
	 * 
	 * @return true/false
	 */
	public boolean isHidden() {
		return hidden.getValue();
	}

	/**
	 * Toggle the column value is Html or not.
	 * This to be used when exporting to a non HTML format, if this flag is set to true 
	 * the renderer will try to convert the html to the final format.
	 * Eg: If exporting to a PDF and this atribute is set to true the Html is converted to PDF
	 * @param sBooleanContentHtml true/false
	 */
	public void setContentHtml( String sBooleanContentHtml ) {
		this.contentHtml.setValue( Boolean.parseBoolean( sBooleanContentHtml ) );
	}

	/**
	 * Toggle the column value is Html or not.
	 * This to be used when exporting to a non HTML format, if this flag is set to true 
	 * the renderer will try to convert the html to the final format.
	 * Eg: If exporting to a PDF and this atribute is set to true the Html is converted to PDF
	 * @param sBooleanContentHtml true/false
	 */
	public void setContentHtml( boolean sBooleanContentHtml ) {
		this.contentHtml.setValue( sBooleanContentHtml );
	}
	
	/**
	 * Getter for the property contentHtml
	 * 
	 * @return true/false
	 */
	public boolean isContentHtml() {
		return contentHtml.getValue();
	}
	
	/**
	 * Set if the column can be resizable by the user
	 * @param sBooleanText true/false
	 */
	public void setResizable( String sBooleanText ) {
		this.resizable.setValue( Boolean.parseBoolean( sBooleanText ) );
	}
	
	/**
	 * Getter for the resizable property
	 * 
	 * @return true/false
	 */
	public boolean isResizable() {
		return resizable.getValue();
	}
	
	public byte getSecurityPermissions() {
		return SecurityPermissions.FULL_CONTROL;
	}
	
	/**
	 * Set if the column is searchable
	 * @param searchable true/false
	 */
	public void setSearchable(String searchable) {
		this.searchable.setValue( Boolean.parseBoolean( searchable )  );
	}
	
	/**
	 * Set if the column is groupable
	 * @param groupable true/false
	 */
	public void setGroupable(String groupable) {
		this.groupable.setValue( Boolean.parseBoolean( groupable )  );
	}
	
	/**
	 * Set if the column can be sorted by the user
	 * @param sortable true/false
	 */
	public void setSortable(String sortable) {
		this.sortable.setValue( Boolean.parseBoolean( sortable )  );
	}
	
	/**
	 * Set the column name of the {@link DataRecordConnector} to bind.
	 * @param dataField String with the column name
	 */
	public void setDataField(String dataField) {
        this.dataField.setValue( dataField );
    }

	/**
	 * Force the lookup viewer name when searching by this column
	 * @param lookupViewerExpr Name of the viewer to open when choosing values for this column
	 */
    public void setLookupViewer( String lookupViewerExpr ) {
        this.lookupViewer.setExpressionText( lookupViewerExpr );
    }
    
    /**
     * Get the current forced lookup viewer when searching by this column
     * 
     * @return Name of the viewer
     */
    
    public String getLookupViewer( ) {
        return this.lookupViewer.getEvaluatedValue();
    }
    
    /**
     * Get the current dataField name which this column is bind
     * 
     * @return name which this column is bind
     */
    public String getDataField() {
        return dataField.getValue();
    }
    
    /**
     * Label of the column
     * @param label literal String or a {@link ValueExpression}
     */
    public void setLabel(String label) {
    	
        this.label.setExpressionText( label );
    }
    
    /**
     * Returns the current Label of the column
     * 
     * @return String with the label
     */
    public String getLabel() {
        return label.getEvaluatedValue();
    }
    
    /**
     * Set the default column with in pixels
     * @param sWidth Integer with the default width value 
     */
    public void setWidth( String sWidth ) {
        this.width.setValue( sWidth );
    }
    
    /**
     * Return the default with of the Column
     * 
     * @param Integer String with the default width of the column
     */
    public String getWidth() {
        return width.getValue();
    }
    
    /**
     * Return the isSearchable property
     */
	public boolean isSearchable() {
		return searchable.getValue();
	}

    /**
     * Return the isSortable property
     */
	public boolean isSortable() {
		return sortable.getValue();
	}

    /**
     * Return the isGroupable property
     */
	public boolean isGroupable() {
		return groupable.getValue();
	}

    /**
     * Set a specific renderer to this column
     * 
     * @param A {@link ValueExpression} with a implementation of the interface
     * netgest.bo.xwc.components.classic.GridColumnRenderer to define a renderer to the column  
     */
	public void setRenderer( String sRenderExpression ) {
		this.renderer.setExpressionText( sRenderExpression );
	}
	
    /**
     * Return the current renderer class 
     */
	public GridColumnRenderer getRenderer() {
		return this.renderer.getEvaluatedValue();
	}
	
	
	
}
