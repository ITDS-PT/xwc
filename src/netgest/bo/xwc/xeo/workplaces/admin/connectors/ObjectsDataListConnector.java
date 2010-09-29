package netgest.bo.xwc.xeo.workplaces.admin.connectors;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;


public class ObjectsDataListConnector extends GenericDataListConnector {

	private String orderBy;

	public ObjectsDataListConnector(String orderBy) {
		super();
		this.orderBy = orderBy;
		this.createColumn("name", "Object Name");
		this.createColumn("SYS_DTCREATE", "Creation Date");
		this.createColumn("SYS_DTSAVE", "Last Update");
	}

	@Override
	public void refresh() {
		super.refresh();

		java.sql.Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select CLSID as name,SYS_DTCREATE,SYS_DTSAVE" +
			" from oebo_registry" +
			" where clsid <> 'Ebo_TextIndex'" +
			" and clsid <> 'Ebo_Login'" +
			" order by "+ this.orderBy + "";

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			EboContext ctx = boApplication.currentContext().getEboContext();
			con = ctx.getConnectionData();
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
			stmt.setMaxRows(12);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				this.createRow();

				this.createRowAttribute("name", rs.getString("name"));
				this.createRowAttribute("SYS_DTCREATE", dateFormat.format(rs.getTimestamp("SYS_DTCREATE")));
				this.createRowAttribute("SYS_DTSAVE", dateFormat.format(rs.getTimestamp("SYS_DTSAVE")));

			}
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

}
