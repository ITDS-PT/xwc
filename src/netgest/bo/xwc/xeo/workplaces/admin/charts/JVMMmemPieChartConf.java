package netgest.bo.xwc.xeo.workplaces.admin.charts;

import java.awt.Color;

import netgest.bo.xwc.components.classic.charts.configurations.IPieChartConfiguration;

public class JVMMmemPieChartConf implements IPieChartConfiguration {

	Color[] colours = {Color.red,Color.green};
	
	public JVMMmemPieChartConf() {
		super();
	}

	@Override
	public Color getBackgroundColour() {
		return Color.black;
	}

	@Override
	public Color[] getColours() {
		return this.colours;
	}

	@Override
	public String getTooltipString() {
		return "$key $percent $val MB";
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
