package netgest.bo.xwc.components.template.xeo;

import java.io.IOException;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.template.base.TemplateRenderer;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;


public class Xeolist extends PaginatedList {

	private XUIViewStateBindProperty<String> boql = 
		new XUIViewStateBindProperty<String>("boql", this, String.class );

	private XUIBindProperty<XEOObjectListConnector> dataSource = new XUIBindProperty<XEOObjectListConnector>(
			"dataSource", this, XEOObjectListConnector.class);
	
	private int recordcount;
	private XEOObjectListConnector connector = null;

	public void setBoql( String boql ) {
		this.boql.setExpressionText(boql);
	}
	
	public String getBoql() {
		return this.boql.getEvaluatedValue();
	}
	
	
	public XEOObjectListConnector getDataSource() {
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
				XEOObjectListConnector.class));
	}
	
	private boolean hasDataSourceBeenEvaluated = false; 
	
	@Override
	public void preRender() {
		hasDataSourceBeenEvaluated = false;
		super.preRender();
	}

	@Override
	public DataListConnector getConnector() {
		return this.connector;
	}

	@Override
	public void initConnector() {
		EboContext ctx = null;		
		try {
			if (this.connector==null) {
				if (this.getDataSource()==null) {
					ctx = boApplication.currentContext().getEboContext();
					boObjectList list=boObjectList.list(ctx, getBoql(),
							new Integer(getPage()).intValue(), new Integer(getPagesize()).intValue());
					connector=new XEOObjectListConnector(list);
				}
				
				connector.setPage(new Integer(this.getPage()).intValue());
				connector.setPageSize(new Integer(this.getPagesize()).intValue());			
				connector.refresh();
				recordcount = connector.getRecordCount();
			}
			else if (connector.getPage()!=new Integer(this.getPage()).intValue() ||
					connector.getPageSize()!=new Integer(this.getPagesize()).intValue()
					|| (this.getDataSource()==null 
					&& !connector.getObjectList().getBOQL().equals(this.getBoql()))) {
				connector.setPage(new Integer(this.getPage()).intValue());
				connector.setPageSize(new Integer(this.getPagesize()).intValue());			
				connector.refresh();
				recordcount = connector.getRecordCount();
			}
		}
		finally {
			if (ctx!=null)
				ctx.close();
		}
		
	}

	@Override
	public int getRecordCount() {
		return this.recordcount;
	}
	
}
