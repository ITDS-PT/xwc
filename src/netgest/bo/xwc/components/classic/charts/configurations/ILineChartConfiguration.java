package netgest.bo.xwc.components.classic.charts.configurations;

import java.awt.Color;

/**
 * 
 * An interface for the configurations of a Line Chart
 * 
 * @author Pedro Rio
 * @version 1
 *
 */
public interface ILineChartConfiguration 
{

	/**
	 * 
	 * Returns the background color of the chart as Java Color
	 * 
	 * @return A Color instance with the required color (or null to retain the default color)
	 */
	public Color getBackgroundColour();
	
	/**
	 * 
	 * The list of colours for the series of the chart
	 * 
	 * @return An array with a color in each position
	 */
	public Color[] getColours();
	
	/**
	 * 
	 * Whether or not the pie chart title should be shown
	 * 
	 * @return True if the chart should display the title and false otherwise
	 */
	public boolean showChartTitle();
	
	/**
	 * 
	 * Returns the string expression to use as tooltip, one can use the following
	 * special variables
	 * 
	 * $val = The value for the point
	 * 
	 * @return A string expression for the tooltip (or null to use the default value)
	 */
	public String getTooltipString();
	
	/**
	 * 
	 * Retrieves the stroke size for the lines
	 * 
	 * @return An integer with the stroke size (in pixels) for each line or 0 to use the default size
	 */
	public int getStrokeSize();
	
	
	
}
