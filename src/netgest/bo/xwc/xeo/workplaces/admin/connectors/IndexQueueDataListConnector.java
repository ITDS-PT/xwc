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
		this.createColumn("object", "Object");
		this.createColumn("STATE", "State");
		this.createColumn("MESSAGE", "Message");
		this.createColumn("ENQUEUETIME", "Queue Time");
	}

	@Override
	public void refresh() {
		super.refresh();
		
		try {
			String sql = "select EBO_TEXTINDEX_QUEUE.BOUI as BOUI"
			+",OEEBO_REGISTRY.CLSID as object"
			+",EBO_TEXTINDEX_QUEUE.STATE as STATE"
			+",EBO_TEXTINDEX_QUEUE.MESSAGE as MESSAGE"
			+",EBO_TEXTINDEX_QUEUE.ENQUEUETIME as ENQUEUETIME"
			+" from EBO_TEXTINDEX_QUEUE,OEEBO_REGISTRY"
			+" where EBO_TEXTINDEX_QUEUE.STATE <> 1"
			+" and EBO_TEXTINDEX_QUEUE.BOUI = OEEBO_REGISTRY.BOUI"
			+" order by ENQUEUETIME desc";
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			EboContext ctx = boApplication.currentContext().getEboContext();
			java.sql.Connection con = ctx.getConnectionData();
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String state =  rs.getString("STATE");
				
				if ("0".equals(state))
					state = "To be processed";
				else if ("9".equals(state))
					state = "Index error";
				
				this.createRow();
				
				this.createRowAttribute("BOUI", rs.getString("BOUI"));
				this.createRowAttribute("object", rs.getString("object"));
				this.createRowAttribute("STATE", state);
				this.createRowAttribute("MESSAGE", rs.getString("MESSAGE"));
				try {
					this.createRowAttribute("ENQUEUETIME", dateFormat.format(rs.getTimestamp("ENQUEUETIME")));
				} catch (NullPointerException e) {}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
	}
	
}
