package netgest.bo.xwc.xeo.workplaces.admin.connectors;

import netgest.bo.system.boApplication;
import netgest.utils.ngtXMLHandler;


public class LogsDataListConnector extends GenericDataListConnector {

	public LogsDataListConnector() {
		super();
		this.createColumn("NAME", "Name");

	}

	@Override
	public void refresh() {
		super.refresh();


		ngtXMLHandler boConfDoc = new ngtXMLHandler(
				boApplication.currentContext().getEboContext().getApplication().getApplicationConfig().getXmldoc()
		);

		ngtXMLHandler logConfig = boConfDoc.getChildNode("bo-config").getChildNode("logConfig");

		if (logConfig != null) {
			ngtXMLHandler[] logs = logConfig.getChildNodes();

			for (int j=0;j<logs.length;j++){
				if (logs[j].getNodeName().equals("logger")) {
					this.createRow();
					
					this.createRowAttribute("NAME", logs[j].getAttribute("pattern"));
				}
			}
		}
	}

}
