package netgest.bo.xwc.components.connectors;

import java.util.Map;

import netgest.bo.xwc.components.security.SecurityPermissions;


public class XEOObjectListAttributeCount implements DataFieldConnector {

	private XEOObjectListGroupConnector parent;
	private int							row;
	
		   
	public XEOObjectListAttributeCount( XEOObjectListGroupConnector parent, int row,String attName ) {
		this.parent = parent;
		this.row = row;
	}
	
	public DataListConnector getDataList() {
		return null;
	}

	public String[] getDependences() {
		return null;
	}

	public boolean getDisabled() {
		return false;
	}

	public String getDisplayValue() {
		return String.valueOf( parent.getDataSet().rows( row ).getString("count") );
	}

	public boolean getIsLovEditable() {
		return false;
	}

	public boolean getOnChangeSubmit() {
		return false;
	}

	public boolean getRecomended() {
		return false;
	}

	public boolean getRequired() {
		return false;
	}

	public boolean getValid() {
		return false;
	}

	public Object getValue() {
		return parent.getDataSet().rows( row ).getObject("count");
	}

	public boolean getVisible() {
		return false;
	}

	public void setValue(Object newValue) {

	}

	public byte getDataType() {
		return 0;
	}

	public int getDecimalPrecision() {
		return 0;
	}

	public byte getInputRenderType() {
		return 0;
	}

	public boolean getIsLov() {
		return false;
	}

	public String getLabel() {
		return null;
	}

	public Map<Object, String> getLovMap() {
		return null;
	}

	public int getMaxLength() {
		return 0;
	}

	public int getMinDecimals() {
		return 0;
	}

	public boolean getNumberGrouping() {
		return false;
	}

	public double getNumberMaxValue() {
		return 0;
	}

	public double getNumberMinValue() {
		return 0;
	}

	public byte getSecurityPermissions() {
		return SecurityPermissions.FULL_CONTROL;
	}

	@Override
	public String getInvalidMessage() {
		return null;
	}

	@Override
	public boolean validate() {
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String getToolTip() {
		return null;
	}

}
