package netgest.bo.xwc.xeo.workplaces.admin.connectors;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;


public class IndexQueueDataListConnector extends GenericDataListConnector {
	
	public IndexQueueDataListConnector() {
		super();
		
		this.createColumn("BOUI", "boui");
		this.createColumn("STATE", "State");
		this.createColumn("MESSAGE", "Message");
		this.createColumn("ENQUEUETIME", "Queue Time"); //TODO???
		this.createColumn("SYS_DTCREATE", "Creation Date");
		this.createColumn("SYS_DTSAVE", "Last Update");
	}

	@Override
	public void refresh() {
		super.refresh();
		
		try {
			String sql = "select BOUI,STATE,MESSAGE,ENQUEUETIME,SYS_DTCREATE,SYS_DTSAVE" +
					" from EBO_TEXTINDEX_QUEUE" +
					" order by SYS_DTSAVE desc";
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			EboContext ctx = boApplication.currentContext().getEboContext();
			java.sql.Connection con = ctx.getConnectionData();
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				
				this.createRow();
				
				this.createRowAttribute("BOUI", rs.getString("BOUI"));
				this.createRowAttribute("STATE", rs.getString("STATE"));
				this.createRowAttribute("MESSAGE", rs.getString("MESSAGE"));
				try {
					this.createRowAttribute("ENQUEUETIME", dateFormat.format(rs.getTimestamp("ENQUEUETIME")));
				} catch (NullPointerException e) {}
				try {
					this.createRowAttribute("SYS_DTCREATE", dateFormat.format(rs.getTimestamp("SYS_DTCREATE")));
				} catch (NullPointerException e) {}
				try {
					this.createRowAttribute("SYS_DTSAVE", dateFormat.format(rs.getTimestamp("SYS_DTSAVE")));
				} catch (NullPointerException e) {}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
	}
	
}
