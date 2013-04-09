package netgest.bo.xwc.components.classic.charts.datasets;

import java.util.List;

/**
 * 
 * Represents a data set for a Pie Chart, key/value pairs
 * 
 * @author Pedro Rio
 * 
 *
 */
public interface PieDataSet 
{
	/**
	 * Retrieves a value, given a key
	 * 
	 * @param key The key corresponding to the value
	 * 
	 * @return A number representing the value
	 */
	public Number getValue(Comparable<String> key);
	
	/**
	 * 
	 * Retrieve the list of categories in the pie chart
	 * 
	 * @return A list of categories
	 */
	public List<String> getCategories();
	
	/**
	 * 
	 * The label for the category 
	 * 
	 * @return A string with the label for the key set
	 */
	public String getCategoryLabel();
	
	
	/**
	 * 
	 * The label for the value set
	 * 
	 * @return A string with the label for the value set
	 */
	public String getValueLabel();
	
	
}
