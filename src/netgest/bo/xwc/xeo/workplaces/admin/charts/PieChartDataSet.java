package netgest.bo.xwc.xeo.workplaces.admin.charts;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.classic.charts.configurations.IPieChartConfiguration;
import netgest.bo.xwc.components.classic.charts.datasets.impl.PieDataSetImpl;

public class PieChartDataSet extends PieDataSetImpl {

	private int timeExpire; // minutes
	private Date creationDate;
	private SimpleDateFormat format;
	private IPieChartConfiguration iPieChartConfiguration;
	
	public PieChartDataSet(int timeExpire) {
		super();
		this.timeExpire = timeExpire; 
		this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.creationDate = new Date();
	}

	public PieChartDataSet(String sql, String attCategory, String attValues, Integer limit, String label, int timeExpire) {
		this(timeExpire);
		
		java.sql.Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			EboContext ctx = boApplication.currentContext().getEboContext();
			con = ctx.getConnectionData();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(sql);
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
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (rs!=null) rs.close();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
			try {
				if (stmt!=null) stmt.close();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
			try {
				if (con!=null) con.close();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}
	
	public void setiPieChartConfiguration(IPieChartConfiguration iPieChartConfiguration) {
		this.iPieChartConfiguration = iPieChartConfiguration;
	}
	
	public IPieChartConfiguration getiPieChartConfiguration() {
		return this.iPieChartConfiguration;
	}
	
	public boolean isExpired() {
		return (System.currentTimeMillis() > this.creationDate.getTime()+timeExpire*60*1000) ? true : false;
	}
	
	public String getCreationDateString() {
		return format.format(this.creationDate);
	}

	public String getLastUpdated() {
		return "Last updated "+getCreationDateString();
	}

	
}
