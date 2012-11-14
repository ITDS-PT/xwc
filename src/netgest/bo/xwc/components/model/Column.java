package netgest.bo.xwc.components.model;

public interface Column {
    public String getSqlExpression();
    public String getLabel();
    public String getDataField();
    public String getWidth();
    public String getAlign();
    public boolean isSortable();
    public boolean isSearchable();
    public boolean isGroupable();
    public boolean isHidden();
    public boolean wrapText();
    public void setHidden( String hiddenExpr );
    public boolean isHideable();
    public boolean isResizable();
    public byte getSecurityPermissions();
    public String getLookupViewer();
    public boolean isContentHtml();
    public String applyRenderTemplate( Object value );
    public boolean isEnableAggregate();
    /**
     * Whether or not the column should use the value for lov filtering
     * (if false it should use the label) 
     * 
     * @return True if the value of the lov should be used for filtering and false if
     * the label of the lov should be used
     */
    public boolean useValueOnLov();
    
}
