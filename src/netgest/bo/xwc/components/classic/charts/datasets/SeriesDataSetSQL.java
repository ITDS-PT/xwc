package netgest.bo.xwc.components.classic.charts.datasets;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.xeo.workplaces.admin.localization.ExceptionMessage;

/**
 * 
 * An implementation of the SeriesDataSet so that it can accept an SQL ResultSet
 * and build the appropriate structures
 * 
 * @author Pedro Rio
 *
 */
public class SeriesDataSetSQL implements SeriesDataSet {

	/**
	 * The SQL result set to 
	 */
	private ResultSet data;
	
	/**
	 * The values of the data set
	 */
	private HashMap<ColumnSeriesPair, Number> dataValues;
	
	/**
	 * To keep the name of columns already used
	 */
	private HashSet<String> setColumns;
	
	/**
	 * The list of columns
	 */
	private LinkedList<String> listColumns;
	
	/**
	 * To keep the name of series already used
	 */
	private HashSet<String> setSeries;
	
	
	/**
	 * The list of existing series
	 */
	private LinkedList<String> listSeries;
	
	/**
	 * The name of the SQL result set attribute from 
	 * which to retrieve the column name 
	 */
	private String attributeColumn;
	
	/**
	 * The name of the SQL result set attribute from
	 * which to retrieve the series name 
	 */
	private String attributeSeries;
	
	/**
	 * The name of the SQL result set attribute from
	 * which to retrieve the value
	 */
	private String attributeValues;
	
	
	/**
	 * 
	 * Constructor from a SQL ResultSet
	 * 
	 * @param data The data for the chart
	 * @param attColumn The name of the attribute where the column name is
	 * @param attSeries The name of the attribute where the series name is
	 * @param attSeries The name of the attribute where the values are
	 */
	public SeriesDataSetSQL(ResultSet data, String attColumn, String attSeries, String attValues)
	{
		this.data = data;
		this.attributeColumn = attColumn;
		this.attributeSeries = attSeries;
		this.attributeValues = attValues;
		this.dataValues = new HashMap<ColumnSeriesPair, Number>();
		this.listColumns = new LinkedList<String>();
		this.setColumns = new HashSet<String>();
		this.listSeries = new LinkedList<String>();
		this.setSeries = new HashSet<String>();
		buildDataSet();
	}
	
	/**
	 * Builds the current data set
	 */
	private void buildDataSet()
	{
		try
		{
			while (data.next()) 
			{
		        String column = data.getString(attributeColumn);
		        String series = data.getString(attributeSeries);
		        float value = data.getFloat(attributeValues);
		        ColumnSeriesPair p = new ColumnSeriesPair(column, series);
		        if (!setColumns.contains(column)){
		        	this.setColumns.add(column);
		        	this.listColumns.add(column);
		        }
		        if (!setSeries.contains(series)){
		        	this.setSeries.add(series);
		        	this.listSeries.add(series);
		        }
		        this.dataValues.put(p, value);
		    }
		}
		catch (Exception e )
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * Constructor from an SQL Query which is executed by this implementation
	 * 
	 * @param context The context of the request
	 * @param sql the SQL expression to execute and retrieve a data set
	 * @param attColumn The name of the attribute where the column name is
	 * @param attSeries The name of the attribute where the series name is
	 * @param attValues The name of the attribute where the values are
	 */
	public SeriesDataSetSQL(EboContext context, String sqlExpression, String attColumn, String attSeries,
			String attValues, Object[] params)
	{
		try
		{
			EboContext ctx = boApplication.currentContext().getEboContext();
			java.sql.Connection conn = ctx.getConnectionData();
			PreparedStatement pstmt = conn.prepareStatement(sqlExpression,ResultSet.TYPE_SCROLL_SENSITIVE,
	                ResultSet.CONCUR_READ_ONLY);
			for (int paramIndex = 0; paramIndex < params.length ; paramIndex++){
				pstmt.setObject(paramIndex + 1, params[paramIndex]);
			}
			ResultSet srs = pstmt.executeQuery();
			this.data = srs;
			this.attributeColumn = attColumn;
			this.attributeSeries = attSeries;
			this.attributeValues = attValues;
			this.dataValues = new HashMap<ColumnSeriesPair, Number>();
			this.listColumns = new LinkedList<String>();
			this.setColumns = new HashSet<String>();
			this.listSeries = new LinkedList<String>();
			this.setSeries = new HashSet<String>();
			buildDataSet();
		}
		catch (Exception e)
		{
			throw new RuntimeException(ExceptionMessage.COULD_NOT_BUILD_THE_SQL_SERIES_DATASET_FOR_EXPRESSION.toString()+": " + sqlExpression,e);
		}
		
	}
	
	@Override
	public List<String> getColumnKeys() 
	{
		return new ArrayList<String>(this.listColumns);
	}

	@Override
	public List<String> getSeriesKeys() 
	{
		return new ArrayList<String>(this.listSeries);
	}

	@Override
	public Number getValue(String seriesKey, String columnKey) 
	{
		ColumnSeriesPair p = new ColumnSeriesPair(columnKey, seriesKey);
		return this.dataValues.get(p);
	}

	@Override
	public String getXAxisLabel() 
	{
		return "";
	}

	@Override
	public String getYAxisLabel() 
	{
		return "";
	}
	
	/**
	 * 
	 * A representation of a custom Key with two values (a pair) for use in a map
	 * 
	 * @author Pedro Rio
	 *
	 */
	private class ColumnSeriesPair
	{
		/**
		 * Column of the pair
		 */
		private String column;
		
		/**
		 * Series of the pair
		 */
		private String series;
		
		public ColumnSeriesPair(String column, String series)
		{
			this.column = column;
			this.series = series;
		}
		
		@Override
		public int hashCode()
		{
			return column.hashCode() + (31  * series.hashCode());
		}
		
		@Override
		public boolean equals(Object other)
		{
			if (other instanceof ColumnSeriesPair){
				ColumnSeriesPair p = (ColumnSeriesPair) other;
				return (this.column.equalsIgnoreCase(p.getColumn()) && this.series.equalsIgnoreCase(p.getSeries()));
			}
			return false;
		}

		@Override
		public String toString()
		{
			return this.column + " - " + this.series;
		}
		
		public String getColumn()
		{
			return this.column;
		}
		
		public String getSeries()
		{
			return this.series;
		}
		
		
	}

}
