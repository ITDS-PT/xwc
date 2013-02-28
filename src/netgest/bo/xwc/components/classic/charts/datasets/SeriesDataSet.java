package netgest.bo.xwc.components.classic.charts.datasets;

import java.util.List;

/**
 * 
 * Represents a data set which can have a set of columns (xAxis) and for each value in the xAxis, a set
 * of series (yAxis) can exist. This interface represents the data set for bar/line charts. For instance if a bar chart showing
 * page views and visits to given website over the course of a three months (January to March), the DataSet would have
 * three columns (January, February and March) and two series (page views and visits), with a value for page view and visit for
 * each of the three months
 * 
 * @author Pedro Pereira
 *
 */
public interface SeriesDataSet 
{
	
	/**
	 * 
	 * Retrieves the list of series of this data set (must be at least one) 
	 * 
	 * @return The list of series
	 */
	public List<String> getSeriesKeys();
	
	/**
	 * 
	 * Retrieves the list of columns of this data set (must be at least one)
	 * 
	 * @return The list of columns of this data set
	 */
	public List<String> getColumnKeys();
	
	/**
	 * 
	 * For a given Column and a given series, retrieves the value associated
	 * 
	 * @param seriesKey The series key 
	 * @param columnKey The column key
	 * 
	 * @return The numeric value associated with the series/column pair, or null if there's no value
	 * for that specific pair
	 */
	public Number getValue(String seriesKey, String columnKey);
	
	
	
	/**
	 * 
	 * The label to show in the XAxis
	 * 
	 * @return The label for the XAxis
	 */
	public String getXAxisLabel();
	
	
	/**
	 * 
	 * The label to show in the YAxis
	 * 
	 * @return The label to show in the YAxis
	 */
	public String getYAxisLabel();
	
}
