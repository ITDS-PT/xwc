package netgest.bo.xwc.components.classic;

import netgest.bo.xwc.components.model.Column;
import netgest.bo.xwc.components.security.SecurityPermissions;
import netgest.bo.xwc.framework.XUIBaseProperty;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

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
    private XUIBindProperty<String>             lookupViewer= new XUIBindProperty<String>( "lookupViewer", this, String.class );
    
    private XUIBindProperty<GridColumnRenderer> renderer    = new XUIBindProperty<GridColumnRenderer>( "renderer", this, GridColumnRenderer.class );
    
	public void setHideable( String sBooleanText ) {
		this.hideable.setValue( Boolean.parseBoolean( sBooleanText ) );
	}
	
	public boolean isHideable() {
		return hideable.getValue();
	}
    
	public void setHidden( String sBooleanText ) {
		this.hidden.setValue( Boolean.parseBoolean( sBooleanText ) );
	}
	
	public boolean isHidden() {
		return hidden.getValue();
	}

	public void setResizable( String sBooleanText ) {
		this.resizable.setValue( Boolean.parseBoolean( sBooleanText ) );
	}
	
	public boolean isResizable() {
		return resizable.getValue();
	}
	
	public byte getSecurityPermissions() {
		return SecurityPermissions.FULL_CONTROL;
	}

	public void setSearchable(String searchable) {
		this.searchable.setValue( Boolean.parseBoolean( searchable )  );
	}

	public void setGroupable(String groupable) {
		this.groupable.setValue( Boolean.parseBoolean( groupable )  );
	}

	public void setSortable(String sortable) {
		this.sortable.setValue( Boolean.parseBoolean( sortable )  );
	}

	public void setDataField(String dataField) {
        this.dataField.setValue( dataField );
    }

    public void setLookupViewer( String lookupViewerExpr ) {
        this.lookupViewer.setExpressionText( lookupViewerExpr );
    }
    
    public String getLookupViewer( ) {
        return this.lookupViewer.getEvaluatedValue();
    }

    public String getDataField() {
        return dataField.getValue();
    }

    public void setLabel(String label) {
    	
        this.label.setExpressionText( label );
    }

    public String getLabel() {
        return label.getEvaluatedValue();
    }
    
    public void setWidth( String sWidth ) {
        this.width.setValue( sWidth );
    }

    public String getWidth() {
        return width.getValue();
    }

	public boolean isSearchable() {
		return searchable.getValue();
	}

	public boolean isSortable() {
		return sortable.getValue();
	}

	public boolean isGroupable() {
		return groupable.getValue();
	}

	public void setRenderer( String sRenderExpression ) {
		this.renderer.setExpressionText( sRenderExpression );
	}
	
	public GridColumnRenderer getRenderer() {
		return this.renderer.getEvaluatedValue();
	}
	
	
	
}
