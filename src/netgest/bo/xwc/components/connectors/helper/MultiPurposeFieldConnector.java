package netgest.bo.xwc.components.connectors.helper;

import java.util.Map;

import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataFieldTypes;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.security.SecurityPermissions;

public class MultiPurposeFieldConnector implements DataFieldConnector {
	
	private byte dataType;
	private String label;
	private Object value;
	
	public MultiPurposeFieldConnector( String label,Object value, byte dataType ) {
		this.label = label;
		this.value = value;
		this.dataType = dataType;
	}
	
	public byte getDataType() {
		return dataType;
	}

	public int getDecimalPrecision() {
		return 0;
	}

	public byte getInputRenderType() {
		return DataFieldTypes.RENDER_DEFAULT;
	}

	public boolean getIsLov() {
		return false;
	}

	public String getLabel() {
		return label;
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
		if (value == null)
			return "";
		return value.toString();
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
		return value;
	}

	public boolean getVisible() {
		return true;
	}

	public void setValue(Object newValue) {
		
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
