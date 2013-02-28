package netgest.bo.xwc.components.classic.charts.configurations;

import java.awt.Color;

/**
 * 
 * The <code>IPieChartConfiguration</code> interface is a definition of 
 * configuration options for a pie chart, namely labeling, colours, fonts
 *  
 * 
 * @author Pedro Rio
 *
 */
public interface IPieChartConfiguration 
{
	/**
	 * 
	 * Returns the background color of the chart
	 * 
	 * @return The Color to use or null if the default colour should be used
	 */
	public Color getBackgroundColour();
	
	/**
	 * 
	 * The list of colours for the pie pieces
	 * 
	 * @return An array with a Color in each position
	 */
	public Color[] getColours();
	
	
	/**
	 * 
	 * Whether or not the pie chart should show the labels
	 * 
	 * @return True if the labels should be shown and false otherwise
	 */
	public boolean showLabels();
	
	
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
	 * $key = The name of the pie slice 
	 * $val = The value of the slice
	 * $percent = the percentage from total of the slice
	 * 
	 * Example: "$key has value $val which is $percent % of total",
	 *  which could output something like
	 * "Microsoft has value 30000 which is 30% of total"
	 * 
	 * @return A string expression for the tooltip (or null to use the default value)
	 */
	public String getTooltipString();
	
	
}
