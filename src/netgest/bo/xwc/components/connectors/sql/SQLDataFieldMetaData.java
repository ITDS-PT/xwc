package netgest.bo.xwc.components.connectors.sql;

import java.util.Collections;
import java.util.Map;

import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;

public class SQLDataFieldMetaData implements DataFieldMetaData {

	private byte dataType = DataFieldTypes.VALUE_CHAR;
	private String label = null;
	private int maxLength = 0;
	private int decimalPrecision = 0;
	private String name = null;
	
	public SQLDataFieldMetaData(String label, String name, byte dataType,
			int maxLength, int decimalPrecision) {
		this.label = label;
		this.name=name;
		this.dataType = dataType;
		this.maxLength = maxLength;
		this.decimalPrecision = decimalPrecision;
	}
	
	@Override
	public byte getDataType() {
		return this.dataType;
	}

	@Override
	public byte getInputRenderType() {
		return DataFieldTypes.RENDER_DEFAULT;
	}

	@Override
	public int getMaxLength() {
		return this.maxLength;
	}

	@Override
	public int getDecimalPrecision() {
		return this.decimalPrecision;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getNumberGrouping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getIsLov() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public byte getSecurityPermissions() {
		return 0;
	}
	
	@Override
	public Map<Object, String> getLovMap() {
		return Collections.emptyMap();
	}

	@Override
	public Map<Object, String> getLovMapWithLimit(int maxRecords) {
		return Collections.emptyMap();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getToolTip() {
		return "";
	}
	

}
