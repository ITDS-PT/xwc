package netgest.bo.xwc.xeo.workplaces.admin.charts;

import java.awt.Color;

import netgest.bo.xwc.components.classic.charts.configurations.IPieChartConfiguration;

public class ObjectsPieChartConf implements IPieChartConfiguration {

	private String unit;
	
	public ObjectsPieChartConf(String unit) {
		this.unit = unit;
	}

	@Override
	public Color getBackgroundColour() {
		return Color.black;
	}

	@Override
	public Color[] getColours() {
		return null;
	}

	@Override
	public String getTooltipString() {
		return "$key $percent $val "+this.unit;
	}

	@Override
	public boolean showChartTitle() {
		return false;
	}

	@Override
	public boolean showLabels() {
		return true;
	}

}
