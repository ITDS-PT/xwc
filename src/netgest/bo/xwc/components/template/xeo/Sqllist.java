package netgest.bo.xwc.components.template.xeo;

import netgest.bo.runtime.EboContext;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.sql.SQLDataListConnector;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;


public class Sqllist extends PaginatedConnectorList {

	private XUIViewStateBindProperty<String> sql = 
		new XUIViewStateBindProperty<String>("sql", this, String.class );

	private XUIBindProperty<SQLDataListConnector> dataSource = new XUIBindProperty<SQLDataListConnector>(
			"dataSource", this, SQLDataListConnector.class);
	
	
	private int recordCount=-1;
	private SQLDataListConnector connector = null;

	public void setSql( String sql ) {
		this.sql.setExpressionText(sql);
	}
	
	public String getSql() {
		return this.sql.getEvaluatedValue();
	}	
	
	public SQLDataListConnector getDataSource() {
		if (!hasDataSourceBeenEvaluated || connector == null){
			if (dataSource.getValue() != null) {
				connector = dataSource.getEvaluatedValue();
				if( connector != null )
					hasDataSourceBeenEvaluated = true;
			}
		} 
		return connector;
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource.setValue(createValueExpression(dataSource,
				SQLDataListConnector.class));
	}
	
	@Override
	public DataListConnector getConnector() {
		return this.connector;
	}

	@Override
	public int getRecordCount() {
		if (this.connector!=null && this.recordCount==-1)
			this.recordCount = connector.getRecordCount();
		return this.recordCount;
	}
	
	private boolean hasDataSourceBeenEvaluated = false; 
	
	@Override
	public void preRender() {
		hasDataSourceBeenEvaluated = false;
		super.preRender();
	}
	
	public void initConnector() {
		EboContext ctx = null;		
		try {
			if (this.connector==null) {
				if (this.getDataSource()==null)				
					connector = new SQLDataListConnector(this.getSql());
				
				connector.setPage(new Integer(this.getPage()).intValue());
				connector.setPageSize(new Integer(this.getPagesize()).intValue());			
				connector.refresh();
				//recordCount = connector.getRecordCount();
			}
			else if (connector.getPage()!=new Integer(this.getPage()).intValue() ||
					connector.getPageSize()!=new Integer(this.getPagesize()).intValue() 
					|| (this.getDataSource()==null 
					&& !connector.getSqlQuery().equals(this.getSql()))) {
				connector.setPage(new Integer(this.getPage()).intValue());
				connector.setPageSize(new Integer(this.getPagesize()).intValue());			
				connector.refresh();
				//recordCount = connector.getRecordCount();
			}
		}
		finally {
			if (ctx!=null)
				ctx.close();
		}
	}	
}
