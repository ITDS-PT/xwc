package netgest.bo.xwc.components.classic.charts.datasets.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import netgest.bo.xwc.components.classic.charts.datasets.PieDataSet;

public class PieDataSetImpl implements PieDataSet {

	private HashMap<String,Number> dataSet = new HashMap<String, Number>();
	private String categoryLabel;
	private String valueLabel;
	
	public PieDataSetImpl() {
		super();
	}

	public PieDataSetImpl(String categoryLabel, String valueLabel) {
		super();
		this.categoryLabel = categoryLabel;
		this.valueLabel = valueLabel;
	}

	@Override
	public List<String> getCategories() {
		return new ArrayList<String>(this.dataSet.keySet());
	}
	
	public void addCategory(String key, Number value) {
		this.dataSet.put(key, value);
	}

	@Override
	public String getCategoryLabel() {
		return this.categoryLabel;
	}

	@Override
	public Number getValue(Comparable<String> key) {
		return this.dataSet.get(key);
	}

	@Override
	public String getValueLabel() {
		return this.valueLabel;
	}

	public void setCategoryLabel(String categoryLabel) {
		this.categoryLabel = categoryLabel;
	}

	public void setValueLabel(String valueLabel) {
		this.valueLabel = valueLabel;
	}
	
}
