package netgest.bo.xwc.components.classic;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.el.ValueExpression;

import netgest.bo.def.boDefAttribute;
import netgest.bo.xwc.components.annotations.Localize;
import netgest.bo.xwc.components.annotations.RequiredAlways;
import netgest.bo.xwc.components.annotations.Values;
import netgest.bo.xwc.components.classic.renderers.FileDownloadRenderer;
import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeMetaData;
import netgest.bo.xwc.components.connectors.XEOObjectConnector;
import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.xeo.components.Bridge;
import netgest.bo.xwc.xeo.components.List;
import netgest.bo.xwc.xeo.components.LookupList;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

/**
 * This component represents a Column in a Grid panel
 * It must reside inside of a Columns element in the viewer and allows to customize
 * how columns are rendered
 * 
 * @author jcarreira 
 *
 */
public class ColumnAttribute extends XUIComponentBase implements Column {
	
    /**
     * Name of the column of the DataListConnector / boObjectList from the parent {@link GridPanel}, 
     * {@link List} ,{@link Bridge} or  {@link LookupList} component. 
     * Can also be used as alias to SQL columns
     */
	@RequiredAlways
    private XUIBaseProperty<String>         	dataField   	= 
    	new XUIBaseProperty<String>( "dataField", this );
    /**
     * SQL query to execute to retrieve the values of this column
     */
    protected XUIBaseProperty<String>         	sqlExpression  	= 
    	new XUIBaseProperty<String>( "sqlExpression", this );
    /**
     * The label to show in the table header column
     */
    @Localize
    private XUIViewStateBindProperty<String>    	label       = 
    	new XUIViewStateBindProperty<String>( "label", this, String.class );
    /**
     * The width of this column (in pixels)
     */
    private XUIViewProperty<String>    			width       = 
    	new XUIViewProperty<String>( "width", this );
	/**
	 * If the results of the grid panel can be searched by this column
	 * 
	 */
	private XUIViewProperty<Boolean>    		searchable  = 
		new XUIViewProperty<Boolean>( "searchable", this, true );
    /**
     * If the rows of the panel can be sorted by this column (only has effect if the
     * {@link GridPanel#getEnableColumnSort()} method returns <code>True</code> )
     */
    private XUIViewProperty<Boolean>    		sortable    = 
    	new XUIViewProperty<Boolean>( "sortable", this, true );
    /**
     * If the results can be grouped by this column (only has effect if the
     * {@link GridPanel#getEnableGroupBy()} method returns <code>True</code> )
     */
    private XUIViewProperty<Boolean>    		groupable	= 
    	new XUIViewProperty<Boolean>( "groupable", this, true );
	/**
	 * If this column can be hidden (by the user)
	 */
	private XUIViewProperty<Boolean>    		hideable 	= 
		new XUIViewProperty<Boolean>( "hideable", this, true );
    /**
     * If the column is hidden or not by default
     */
    private XUIBindProperty<Boolean>    		hidden  	= 
    	new XUIBindProperty<Boolean>( "hidden", this, false, Boolean.class );
	/**
	 * If the column size (width) can be changed by the user (only has effect if the
     * {@link GridPanel#getEnableColumnResize()} method returns <code>True</code> )
	 */
	private XUIViewProperty<Boolean>    		resizable   = 
		new XUIViewProperty<Boolean>( "resizable", this, true );
	
	/**
	 * Marks the content of the column as HTML, this is used when 
	 * exporting the column values to another format (excel, pdf) 
	 * and it marks that the content is HTML (or not) and 
	 * will be converted to final format knowing this.
	 */
	private XUIViewProperty<Boolean>    		contentHtml   	= 
		new XUIViewProperty<Boolean>( "contentHtml", this, false );
	/**
	 * Render template, or JSON object with several render 
	 * templates according to the value for this column.
	 * 
	 * To render the value of the column in the template use "%s" (see example) 
	 * 
	 * Example (Single Render Template - Renders the column value in red and bold)
	 * 
	 * renderTemplate="&lt;div style=\'color:red;font-weight:bold\'>%s&lt;/div>"
	 * 
	 * Example (Multiple Render Templates, according to value)
	 * 
	 * renderTemplate="
     *       {
     *          'SYSUSER': '&lt;div style=\'color:red;font-weight:bold\'>%s&lt;/div>',
     *          '':'Null',
     *          'default': 'Other'
     *       }
     *    "
	 * 
	 */
	private XUIBaseProperty<String>    			renderTemplate 	= 
		new XUIBaseProperty<String>( "renderTemplate", this, null );

    /**
     * Force the lookup viewer name when searching by this column.
     */
    private XUIViewBindProperty<String>             lookupViewer=
    	new XUIViewBindProperty<String>( "lookupViewer", this, String.class );

    /**
     * An implementation of the {@link GridColumnRenderer} 
     * interface which allows the column attribute to rendered in a custom way
     */
    private XUIBindProperty<GridColumnRenderer> renderer    = 
    	new XUIBindProperty<GridColumnRenderer>( "renderer", this, GridColumnRenderer.class );

    /**
     * The alignment of the column text (left,right,center or justify)
     * defaults to left
     */
    @Values({"left","center","right","justify"})
    private XUIViewProperty<String> align = 
    	new XUIViewProperty<String>( "align", this, "" );
    
    /**
     * If the content of the column should be wrapped (defaults to false)
     */
    private XUIViewProperty<Boolean> wrapText =
    	new XUIViewProperty<Boolean>( "wrapText", this, false );
    
    /**
     * If the collumn enables aggregate method returns <code>True</code> )
     */
    private XUIViewProperty<Boolean>    		enableAggregate	= 
    	new XUIViewProperty<Boolean>( "enableAggregate", this, false );
    
    private XUIBindProperty<String> renderFileDownloadLink = new XUIBindProperty<String>("renderFileDownloadLink", this,String.class);
    
    /**
	 * Set the column enables Aggregate
	 * @param sBooleanText true/false
	 */
	public void setEnableAggregate(String enableAggregate) {
		this.enableAggregate.setValue( Boolean.parseBoolean( enableAggregate )  );
	}
	
	/**
	 * Getter for the property summary
	 * 
	 * @return true/false
	 */
	public boolean isEnableAggregate() {
		return enableAggregate.getValue();
	}
	
	public void setRenderFileDownloadLink(String renderFileDownloadLink) {
		this.renderFileDownloadLink.setExpressionText(renderFileDownloadLink);
	}
    
    public String getRenderFileDownloadLink() {
		return this.renderFileDownloadLink.getEvaluatedValue();
	}
    
    public void setSqlExpression( String sqlexpressionEl ) {
    	this.sqlExpression.setValue( sqlexpressionEl );
    }
    
    public String getSqlExpression() {
    	return this.sqlExpression.getValue();
    }
    
    public void setRenderTemplate( String renderTemplate ) {
    	this.renderTemplate.setValue( renderTemplate );
    }
    
    public String getRenderTemplate() {
    	return this.renderTemplate.getValue();
    }
    
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
		this.hidden.setExpressionText(sBooleanText);
	}
	
	/**
	 * Getter for the property hidden
	 * 
	 * @return true/false
	 */
	public boolean isHidden() {
		return hidden.getEvaluatedValue();
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
	 * 
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
		if (XEOObjectConnector.isSystemConstant(getDataField()))
			return false;
		return searchable.getValue();
	}

    /**
     * Return the isSortable property
     */
	public boolean isSortable() {
		if (XEOObjectConnector.isSystemConstant(getDataField()))
			return false;
		return sortable.getValue();
	}

    /**
     * Return the isGroupable property
     */
	public boolean isGroupable() {
		if (XEOObjectConnector.isSystemConstant(getDataField()))
			return false;
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
		GridColumnRenderer renderer = this.renderer.getEvaluatedValue();

		if (renderer == null && !StringUtils.isEmpty(getRenderFileDownloadLink())) {
			GridPanel grid = (GridPanel) findParentComponent(GridPanel.class);

			if (grid != null) {
				//DataListConnector listConnector = grid.getDataSource();
			//	DataFieldMetaData metadata = listConnector.getAttributeMetaData(getDataField());
				
				renderer = new FileDownloadRenderer(this.getRenderFileDownloadLink());
				
//				if (metadata != null && metadata instanceof XEOObjectAttributeMetaData) {
//					boDefAttribute attDef = ((XEOObjectAttributeMetaData) metadata).getBoDefAttribute();
//
//					if (attDef != null && boDefAttribute.ATTRIBUTE_BINARYDATA.equals(attDef.getAtributeDeclaredType())) {
//						renderer = new FileDownloadRenderer();
//					}
//				}
			}
		}

		return renderer;
	}
	
	PrintWriter 	templateWriter;
	CharArrayWriter templateBuffer;
	JSONObject		templateJson;
	
	public String applyRenderTemplate( Object value ) {
		String template = getRenderTemplate();
		if( template != null ) {
			
			if( value == null ) {
				value = "";
			}
			
			if( templateBuffer == null ) {
				templateBuffer = new CharArrayWriter();
				templateWriter = new PrintWriter( templateBuffer );
				try {
					templateJson = new JSONObject( template );
				}
				catch( Exception e ) {
					e.printStackTrace();
				}
			}
			else {
				templateBuffer.reset();
			}
			if( templateJson != null ) {
				template = templateJson.optString(  value.toString(), null );
				if( template == null ) {
					template = templateJson.optString( "default" );
				}
				if( template != null ) {
					templateWriter.printf( template, value );
				}
			}
			else {
				templateWriter.printf( template, value );
			}
			if( template != null ) {
				String ret = templateBuffer.toString();
				return ret;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * Retrieves the alignment of the column
	 * 
	 * @return A string with "left", "right", "center" or "justify"
	 */
	public String getAlign(){
		return align.getValue();
	}
	
	/**
	 * 
	 * Sets the column alignment
	 * 
	 * @param align A string "left"/"right"/"center" or "justify"
	 */
	public void setAlign(String align){
		this.align.setValue(align);
	}
	
	/**
	 * 
	 * Retrieves whether the content of the column should be wrapped or not
	 * 
	 * @return
	 */
	public boolean wrapText(){
		return this.wrapText.getValue().booleanValue();
	}
	
	/**
	 * 
	 * Sets the content of this column to be wrapped
	 * 
	 * @param wrap True or false
	 */
	public void setWrapText(String wrap){
		this.wrapText.setValue(Boolean.parseBoolean( wrap ));
	}

	@Override
	public boolean useValueOnLov() {
		return true;
	}
	
	@Override
	public String toString() {
		return getDataField();
	}
}
