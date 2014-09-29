package netgest.bo.xwc.components.connectors;

import java.util.Collections;
import java.util.Map;

public class NullFieldConnector implements DataFieldConnector {

	@Override
	public byte getDataType() {
		return 0;
	}

	@Override
	public byte getInputRenderType() {
		return 0;
	}

	@Override
	public int getMaxLength() {
		return 0;
	}

	@Override
	public int getDecimalPrecision() {
		return 0;
	}

	@Override
	public int getMinDecimals() {
		return 0;
	}

	@Override
	public double getNumberMaxValue() {
		return 0;
	}

	@Override
	public double getNumberMinValue() {
		return 0;
	}

	@Override
	public boolean getNumberGrouping() {
		return false;
	}

	@Override
	public boolean getIsLov() {
		return false;
	}

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public String getToolTip() {
		return "";
	}

	@Override
	public Map< Object, String > getLovMap() {
		return Collections.emptyMap();
	}

	@Override
	public Map< Object, String > getLovMapWithLimit( int maxRecords ) {
		return Collections.emptyMap();
	}

	@Override
	public Object getValue() {
		return "";
	}

	@Override
	public String getDisplayValue() {
		return "";
	}

	@Override
	public boolean getDisabled() {
		return false;
	}

	@Override
	public boolean getVisible() {
		return true;
	}

	@Override
	public boolean getRequired() {
		return false;
	}

	@Override
	public byte getSecurityPermissions() {
		return 0;
	}

	@Override
	public boolean getRecomended() {
		return false;
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String getInvalidMessage() {
		return "";
	}

	@Override
	public String[] getDependences() {
		return new String[0];
	}

	@Override
	public boolean getOnChangeSubmit() {
		return false;
	}

	@Override
	public void setValue( Object newValue ) {

	}

	@Override
	public boolean getIsLovEditable() {
		return false;
	}

	@Override
	public DataListConnector getDataList() {
		return null;
	}

}
