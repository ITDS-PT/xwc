package netgest.bo.xwc.components.model;

public interface Column {
    public String getSqlExpression();
    public String getLabel();
    public String getDataField();
    public String getWidth();
    public boolean isSortable();
    public boolean isSearchable();
    public boolean isGroupable();
    public boolean isHidden();
    public boolean isHideable();
    public boolean isResizable();
    public byte getSecurityPermissions();
    public String getLookupViewer();
    public boolean isContentHtml();
    public String applyRenderTemplate( Object value );
    
}
