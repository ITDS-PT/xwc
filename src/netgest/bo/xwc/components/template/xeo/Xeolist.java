package netgest.bo.xwc.components.template.xeo;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.components.template.loader.TemplateLoaderFactory;
import netgest.bo.xwc.components.template.preprocessor.CommandsPreProcessor;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;


public class Xeolist extends PaginatedConnectorList {

	private XUIViewStateBindProperty<String> boql = 
		new XUIViewStateBindProperty<String>("boql", this, String.class );

	private XUIBindProperty<XEOObjectListConnector> dataSource = new XUIBindProperty<XEOObjectListConnector>(
			"dataSource", this, XEOObjectListConnector.class);
	
	private XUIBindProperty<Boolean> showPagination = new XUIBindProperty<Boolean>(
			"showPagination", this, Boolean.class,"true");
	
	private int recordcount=-1;
	private XEOObjectListConnector connector = null;

	public void setBoql( String boql ) {
		this.boql.setExpressionText(boql);
	}
	
	public String getBoql() {
		return this.boql.getEvaluatedValue();
	}
	
	public void setShowPagination( String showPagination ) {
		this.showPagination.setExpressionText(showPagination);
	}
	
	public boolean isShowPagination() {
		return this.showPagination.getEvaluatedValue();
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
	public void initComponent() {
		super.initComponent();
		if (!this.template.isDefaultValue( )){
			CommandsPreProcessor p = new CommandsPreProcessor( loadTemplate( getTemplate() ), this );
			List<UIComponent> list = p.createComponents( );
				getChildren( ).addAll( list );
		}
	}
	
	protected freemarker.template.Template loadTemplate(String name){
		try {
			return TemplateLoaderFactory.loadTemplate( name );
		} catch ( IOException e ) {
			throw new RuntimeException( String.format(" Could not load template %s ", name ) , e );
		}
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
				//recordcount = connector.getRecordCount();
			}
			else if (connector.getPage()!=new Integer(this.getPage()).intValue() ||
					connector.getPageSize()!=new Integer(this.getPagesize()).intValue()
					|| (this.getDataSource()==null 
					&& !connector.getObjectList().getBOQL().equals(this.getBoql()))) {
				connector.setPage(new Integer(this.getPage()).intValue());
				connector.setPageSize(new Integer(this.getPagesize()).intValue());			
				connector.refresh();
				//recordcount = connector.getRecordCount();
			}
		}
		finally {
			if (ctx!=null)
				ctx.close();
		}	
	}

	@Override
	public int getRecordCount() {
		if (this.connector!=null && this.recordcount==-1)
			this.recordcount = connector.getRecordCount();
		return this.recordcount;
	}
	
}