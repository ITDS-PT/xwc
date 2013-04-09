package netgest.bo.xwc.xeo.workplaces.admin.charts;

import java.awt.Color;

import netgest.bo.xwc.components.classic.charts.configurations.IBarChartConfiguration;

public class JVMIBarCharConf implements IBarChartConfiguration {
	
	Color[] colours = {Color.blue,Color.orange,Color.green};

	@Override
	public Color getBackgroundColour() {
		return Color.white;
	}

	@Override
	public Color[] getColours() {
		return this.colours;
	}

	@Override
	public String getTooltipString() {
		return "$val MB";
	}

	@Override
	public boolean showChartTitle() {
		return false;
	}

}
