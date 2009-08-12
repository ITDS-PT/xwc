package netgest.bo.xwc.xeo.components;


public class ColumnAttribute extends netgest.bo.xwc.components.classic.ColumnAttribute {
	@Override
	public String getRendererType() {
		return "columnAttribute";
	}
	
	public void setObjectAttribute( String objectAttribute ) {
		super.setDataField( objectAttribute );
	}
	
	public String getObjectAttribute() {
		return super.getDataField();
	}

}
