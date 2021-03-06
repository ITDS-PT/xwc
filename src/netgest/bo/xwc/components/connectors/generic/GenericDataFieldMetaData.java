package netgest.bo.xwc.components.connectors.generic;

import java.util.Collections;
import java.util.Map;

import netgest.bo.xwc.components.connectors.DataFieldMetaData;
import netgest.bo.xwc.components.connectors.DataFieldTypes;

public class GenericDataFieldMetaData implements DataFieldMetaData {
	private Integer colWidth;
	private String label; 
	private byte dataType = DataFieldTypes.VALUE_CHAR; // TODO
	private byte inputRenderType = DataFieldTypes.RENDER_DEFAULT;// TODO
	
	
	public GenericDataFieldMetaData(String label) {
		super();
		this.label = label;
	}
	
	public GenericDataFieldMetaData(String label,byte dataType) {
		super();
		this.label = label;
		this.dataType = dataType;
	}
	
	public GenericDataFieldMetaData(String label, Integer colWidth) {
		super();
		this.label = label;
		this.colWidth = colWidth;
	}

	public GenericDataFieldMetaData(String label, Integer colWidth, byte dataType) {
		super();
		this.label = label;
		this.colWidth = colWidth;
		this.dataType = dataType;
	}

	
	public Integer getColWidth() {
		return colWidth;
	}

	public void setColWidth(Integer colWidth) {
		this.colWidth = colWidth;
	}

	@Override
	public byte getDataType() {
		return this.dataType;
	}

	@Override
	public int getDecimalPrecision() {
		return 0;
	}

	@Override
	public byte getInputRenderType() {  
        return this.inputRenderType;
	}

	@Override
	public boolean getIsLov() {
		return false;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public Map<Object, String> getLovMap() {
		return Collections.emptyMap();
	}
	
	@Override
	public Map<Object, String> getLovMapWithLimit(int maxRecords) {
		return Collections.emptyMap();
	}

	@Override
	public int getMaxLength() {
		return 0;
	}

	@Override
	public int getMinDecimals() {
		return 0;
	}

	@Override
	public boolean getNumberGrouping() {
		return false;
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
	public byte getSecurityPermissions() {
		return 0;
	}

	@Override
	public String getToolTip() {
		return null;
	}

}
