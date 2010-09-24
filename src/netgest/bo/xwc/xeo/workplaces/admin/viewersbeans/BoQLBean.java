package netgest.bo.xwc.xeo.workplaces.admin.viewersbeans;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.event.PhaseEvent;
import javax.servlet.http.HttpServletRequest;

import netgest.bo.data.DataResultSet;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.boObjectList;
import netgest.bo.utils.XEOQLModifier;
import netgest.bo.xwc.components.classic.ColumnAttribute;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

public class BoQLBean extends XEOBaseBean {

	private boolean enableSecurity = true;
	private boolean enableSqlResult = false;
	private boolean enableQueryForm = true;
	private String boQl;
	private String sqlResult;
	private String parserError;
	private String elapsedTime;
	private QLParser qlParser;

	//TODO  keep in session or user preferences
	private LinkedHashMap<String,String> lastQueries = new LinkedHashMap<String, String>(   );
	private String selectedLastQuery = null;


	public BoQLBean() {
		super();
		this.lastQueries.put(" ","");

		HttpServletRequest request = (HttpServletRequest) getRequestContext().getRequest();

		if (request.getParameter("boql")!=null) {
			this.enableQueryForm = false;
			this.boQl = request.getParameter("boql");

			if (request.getParameter("security")!=null)
				this.enableSecurity = Boolean.valueOf(request.getParameter("security"));

			if (request.getParameter("sql")!=null)
				this.enableSqlResult = Boolean.valueOf(request.getParameter("sql"));

			this.runQuery();
		}
	}

	public void setSelectedLastQuery( String query ) {
		this.selectedLastQuery = query;
	}
	
	public void beforeRender(  PhaseEvent e ) {
		if( this.selectedLastQuery!=null && this.selectedLastQuery.trim().length() > 0 ) {
			this.boQl = this.selectedLastQuery;
			runQuery();
		}
		this.selectedLastQuery = "";
	}
	

	public String getSelectedLastQuery() {
		return this.selectedLastQuery;
	}

	public Map<String,String> getLastQueries() {
		return this.lastQueries;
	}

	public boolean isEnableQueryForm() {
		return enableQueryForm;
	}

	public boolean isEnableSecurity() {
		return enableSecurity;
	}

	public void setEnableSecurity(boolean enableSecurity) {
		this.enableSecurity = enableSecurity;
	}

	public boolean isEnableSqlResult() {
		return enableSqlResult;
	}

	public void setEnableSqlResult(boolean enableSqlResult) {
		this.enableSqlResult = enableSqlResult;
	}

	public String getBoQl() {
		return this.boQl;
	}

	public void setBoQl(String boQl) {
		this.boQl = boQl;
	}
	
	public String getElapsedTime() {
		return elapsedTime;
	}

	public String getSqlResult() {
		return this.sqlResult;
	}

	public String getParserError() {
		return parserError;
	}

	public Boolean getQuerySuccess() {
		return this.qlParser!=null && this.qlParser.Sucess() && this.parserError == null;
	}

	public void runQuery() {
		if (this.boQl!=null) {
			try{
				this.elapsedTime = null;
				this.parserError = null;
				this.qlParser = new QLParser();
				this.sqlResult = this.qlParser.toSql(this.boQl,getEboContext(), this.enableSecurity);

				if (this.qlParser.Sucess()) {
					Calendar start = Calendar.getInstance();

					boObjectList result =  boObjectList.list(getEboContext(),this.boQl);

					// forces error
					result.next();
					
				    Calendar end = Calendar.getInstance();
					long time = end.getTimeInMillis() - start.getTimeInMillis();
					int minutes, seconds;
					minutes = (int)time/1000/60 ;
					seconds = (int)time/1000;
					seconds %=60;
				    
				    this.elapsedTime = "Time elapsed "
				    	+ (minutes==0? (seconds + " seconds") : minutes +":"+seconds+" minutes");
			
					LinkedHashMap<String,String> lastQueriesReversed = new LinkedHashMap<String, String>();
					lastQueriesReversed.put( this.boQl , this.boQl  );
					for (Entry<String, String> entry : this.lastQueries.entrySet()) {
						lastQueriesReversed.put(entry.getKey(), entry.getValue());
					}
					this.lastQueries = lastQueriesReversed;
				} else {
					this.parserError = "<b>Error:      </b>Parsing query";
				}
			} catch(netgest.bo.ql.boqlParserException e) {  
				this.parserError = "<b>Error:      </b>" +e.getMessage();
			} catch(Exception e) { 
				this.parserError = "<b>Error:      </b>" +e.getMessage();
			}
		}
	}

	public DataListConnector getResult() {
		return new XEOObjectListConnector(boObjectList.list(getEboContext(),this.boQl));

	}

	public void canCloseTab() {

		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		Window xWnd = (Window)viewRoot.findComponent(Window.class);
		if( xWnd != null ) {
			if( xWnd.getOnClose() != null ) {
				xWnd.getOnClose().invoke( oRequestContext.getELContext(), null);
			}
		}
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.getViewRoot().setTransient( true );
		oRequestContext.renderResponse();
	}

	public XUIComponentPlugIn getAttributesColPlugIn() {
		return new AttributesColPlugIn();
	}

	private class AttributesColPlugIn extends XUIComponentPlugIn {

		@Override
		public void beforePreRender() {
			if( getQuerySuccess() ) {
				((XUIComponentBase)getComponent().getParent()).forceRenderOnClient();
				getComponent().getChildren().clear();
				boDefHandler objDef = qlParser.getObjectDef();
				XEOQLModifier q = new XEOQLModifier( boQl, null );
				if( q.getFieldsPart().length() == 0 )
				{
					for (int i = 0; i < objDef.getAttributesDef().length; i++) {
						boDefAttribute attDef = objDef.getAttributesDef()[i];
						if ( boDefAttribute.ATTRIBUTE_LONGTEXT.equals(attDef.getAtributeDeclaredType()) 
								|| attDef.getAtributeDeclaredType() == null 
								|| boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equals(attDef.getAtributeDeclaredType()))
							continue;

						ColumnAttribute ca = new ColumnAttribute();
						ca.setDataField(attDef.getName());
						ca.setLabel(attDef.getLabel());
						ca.setWidth("100");
						getComponent().getChildren().add(ca);
					}
				}
				else {
					try {
						GridPanel p = ((GridPanel)getComponent().getParent());
						p.setRowUniqueIdentifier( null );

						XEOObjectListConnector c = (XEOObjectListConnector)p.getDataSource();
						DataResultSet 		d = c.getObjectList().getRslt();
						ResultSetMetaData	m = d.getMetaData();
						int colCount = m.getColumnCount();
						for( int i=1; i <= colCount; i++ ) {
							ColumnAttribute ca = new ColumnAttribute();
							ca.setDataField(m.getColumnName( i ));
							ca.setLabel(m.getColumnName( i ));
							ca.setWidth("100");
							getComponent().getChildren().add(ca);
						}
					} catch (SQLException e) {
						throw new RuntimeException(e.getMessage());
					}
				}
			}
		}
	}
}
