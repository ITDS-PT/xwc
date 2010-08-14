package netgest.bo.xwc.xeo.workplaces.admin.charts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.classic.charts.configurations.IPieChartConfiguration;
import netgest.bo.xwc.components.classic.charts.datasets.impl.PieDataSetImpl;

public class PieChartDataSet extends PieDataSetImpl {

	private Date creationDate;
	private SimpleDateFormat format;
	private IPieChartConfiguration iPieChartConfiguration;
	
	public PieChartDataSet() {
		super();
		this.format = new SimpleDateFormat("HH:mm:ss");
		this.creationDate = new Date();
	}

	public PieChartDataSet(String sql, String attCategory, String attValues, Integer limit) throws SQLException {
		this();
		
		EboContext ctx = boApplication.currentContext().getEboContext();
		java.sql.Connection con = ctx.getConnectionData();
		Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
		ResultSet rs = stmt.executeQuery(sql);
		int count = 0;
		double othersTotal = 0;
		while (rs.next()) {
			count++;

			if (limit != null && count>limit)
				othersTotal += rs.getDouble(attValues);
			else
				this.addCategory(rs.getString(attCategory), rs.getDouble(attValues));

		}
		if (limit != null && count>limit)
			this.addCategory("Others", othersTotal);
	}
	
	public void setiPieChartConfiguration(IPieChartConfiguration iPieChartConfiguration) {
		this.iPieChartConfiguration = iPieChartConfiguration;
	}
	
	public IPieChartConfiguration getiPieChartConfiguration() {
		return this.iPieChartConfiguration;
	}
	
	public boolean isExpired() {
		return (System.currentTimeMillis() > this.creationDate.getTime()+5*60*1000) ? true : false;
	}
	
	public String getCreationDateString() {
		return format.format(this.creationDate);
	}
}