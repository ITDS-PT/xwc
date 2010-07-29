package netgest.bo.xwc.components.classic.charts.datasets.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import netgest.bo.xwc.components.classic.charts.datasets.SeriesDataSet;

public class SeriesDataSetImpl implements SeriesDataSet {
	private LinkedHashMap<String, String> series = new LinkedHashMap<String, String>();
				//ColKey                        seriesKey
	private LinkedHashMap<String, LinkedHashMap<String, Number>> colSeries = new LinkedHashMap<String, LinkedHashMap<String,Number>>();
	private String xAxisLabel;
	private String yAxisLabel;
	
	public SeriesDataSetImpl() {
		super();
	}

	public SeriesDataSetImpl(String xAxisLabel, String yAxisLabel) {
		super();
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
	}
	
	public void addColumn(String key) {
		this.colSeries.put(key,new LinkedHashMap<String, Number>());
	}
	
	public void addSeries(String key) {
		this.series.put(key, key);
	}

	@Override
	public List<String> getColumnKeys() {
		return new ArrayList<String>(this.colSeries.keySet());
	}

	@Override
	public List<String> getSeriesKeys() {
		return new ArrayList<String>(this.series.keySet());
	}

	@Override
	public Number getValue(String seriesKey, String columnKey) {
		return this.colSeries.get(columnKey).get(seriesKey);
	}

	@Override
	public String getXAxisLabel() {
		return this.xAxisLabel;
	}

	@Override
	public String getYAxisLabel() {
		return this.yAxisLabel;
	}
	

	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	public void addValue(String seriesKey, String columnKey, Number value) throws Exception {
		
		if (this.colSeries.get(columnKey) == null)
			throw new Exception("No such column  "+columnKey);
		
		if (this.series.get(seriesKey) == null)
			throw new Exception("No such series  "+seriesKey);
		
		this.colSeries.get(columnKey).put(seriesKey, value);
	}

}
