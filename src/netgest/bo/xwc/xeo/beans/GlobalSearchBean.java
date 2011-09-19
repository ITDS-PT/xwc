package netgest.bo.xwc.xeo.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import netgest.bo.data.DriverUtils;
import netgest.bo.def.boDefHandler;
import netgest.bo.ejb.boManagerLocal;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.classic.extjs.ExtConfig;
import netgest.bo.xwc.components.classic.extjs.ExtConfigArray;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.localization.BeansMessages;

/**
 * Bean to support the global search component
 *
 */
public class GlobalSearchBean extends XEOBaseBean {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(GlobalSearchBean.class);
	
	/**
	 * Stores the text the user wants to search
	 */
	private String textSearch;
	
	/**
	 * Stores the model to restrict searches to  
	 */
	private String restrictedModel;
	
	/**
	 * String with the no results message
	 */
	private String noResults = "";
	
	/**
	 * Search options
	 */
	private HashMap<String,String> searchOptions;
	
	/**
	 * 
	 * Retrieves the panel declaration for the results display
	 * 
	 * @return A string with an ExtJS panel (including a DataView)
	 */
	private static final String getPanelDeclaration(){
		ExtConfig c = new ExtConfig("Ext.Panel");
		c.addJSString("id", "searchResultsPanel");
		c.add("frame",false);
		c.add("border",true);
		c.add("autoHeight",true);
		c.add("collapsible",false);
		
		c.addJSString("renderTo", "searchResults");
		c.addJSString("layout", "fit");
		c.addJSString("title", "");
		
		ExtConfig dataview = new ExtConfig("Ext.DataView");
		dataview.add("store", "store");
		dataview.add("tpl", "tpl");
		dataview.add("autoHeight", true);
		dataview.addJSString("overClass", "x-view-over");
		dataview.addJSString("itemSelector", "div.searchWrap");
		dataview.addJSString("emptyText", "No result");
		
		c.add("items",dataview.renderExtConfig());
		
		return c.renderExtConfig().toString();
	}
	
	/**
	 * 
	 * Retrieves the list of search options
	 * 
	 * @return
	 */
	public HashMap<String,String> getSearchOptions(){
		return this.searchOptions;
	}
	
	public void setSearchOptions(HashMap<String,String> options){
		this.searchOptions = options;
	}
	
	/**
	 * 
	 * Get the text to search
	 * 
	 * @return
	 */
	public String getTextSearch(){
		return textSearch;
	}
	
	public String getRestrictedModel(){
		return restrictedModel;
	}
	
	/**
	 * 
	 * Retrieves a skeleton for a DataStore to use in the DataView
	 * 
	 * @return
	 */
	private ExtConfig getStoreSkeleton(){
		ExtConfig jsonStore = new ExtConfig("Ext.data.SimpleStore");
		
		ExtConfigArray fields = new ExtConfigArray();
		fields.addString("text");
		fields.addString("classname");
		fields.addString("target");
		fields.addString("updatedate");
		
		jsonStore.add("fields", fields);
		
		return jsonStore;
	}
	
	/**
	 * 
	 * 
	 * Executes the search and stores the results
	 * 
	 * @param textSearch The text to search
	 * @param classRestrictions The classes to restric the search to (XEO MOdel names)
	 */
	public void executeSearch(String textSearch, String[] classRestrictions){
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			
			conn = getEboContext().getConnectionData(); 
			st = conn.createStatement();
			DriverUtils dbUtils = getEboContext().getDataBaseDriver().getDriverUtils();
			String name = boDefHandler.getBoDefinition("Ebo_TextIndex").getBoMasterTable();
			
			String sql = "select ";
			if( dbUtils.getQueryLimitStatementPosition() == DriverUtils.QUERY_LIMIT_ON_SELECT_CLAUSE ) {
				sql += dbUtils.getQueryLimitStatement(50) + " ";
			}			
			sql += "ui$,text,boui,SYS_DTCREATE,uiclass from "+ name +" WHERE "+ dbUtils.getFullTextSearchWhere("text", "'"+textSearch+"'");
			
			if( dbUtils.getQueryLimitStatementPosition() == DriverUtils.QUERY_LIMIT_ON_WHERE_CLAUSE ) {
				sql += " AND " + dbUtils.getQueryLimitStatement(50) + " ";
			}			
			if (classRestrictions.length > 0 ){
				sql += "AND uiClass IN (";
				int k = 0;
				for (String classToRestrict : classRestrictions){
					sql += "'"+classToRestrict+"'";
					k++;
					if (k < classRestrictions.length )
						sql += ",";
				}
				sql += ")";
			}
			
			if( dbUtils.getQueryLimitStatementPosition() == DriverUtils.QUERY_LIMIT_ON_END_OF_STATEMENT ) {
				sql += " " +dbUtils.getQueryLimitStatement(50) + " ";
			}
			rs = st.executeQuery(sql);
			
			StringBuilder b = new StringBuilder(500);
			
			boManagerLocal objectManager = boApplication.getDefaultApplication().getObjectManager();
			
			//Retrieve the store skeleton and fill data
			ExtConfig store = getStoreSkeleton();
			ExtConfigArray data = store.addChildArray("data");
			int k = 0;
			while (rs.next()){
				ExtConfigArray line = new ExtConfigArray();
				boObject current = objectManager.loadObject(getEboContext(),rs.getLong("ui$"));
				k++;
				line.addString(current.getCARDID().toString());
				line.addString(rs.getString("uiclass"));
				line.addString(String.valueOf(rs.getLong("ui$")));
				line.addString(rs.getString("SYS_DTCREATE"));
				data.add(line.renderExtConfig());
			}
			
			if (k > 0)
				noResults = "";
			else
				noResults = "<h1>"+BeansMessages.GLOBAL_SEARCH_BEAN_NO_RESULTS.toString()+"<h1>";
			
			b.append("Ext.onReady(function(){ ");
			b.append("var store = " + store.renderExtConfig() + " ; ");
			b.append(getPanelDeclaration() + " ; ");
			b.append("	});  ");
			
			getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_FOOTER, "dataStore", b);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 catch (boRuntimeException e) {
				e.printStackTrace();
		}
		finally{
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					logger.severe("Could not close result set from global search",e);
				}
			if (st != null)
				try {
					st.close();
				} catch (SQLException e) {
					logger.severe("Could not close statement from global search",e);
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					logger.severe("Could not close connection from global search",e);
				}
		}
		
		
		
	}
	
	/**
	 * 
	 * Retrieves the search results to display
	 * 
	 * @return
	 */
	public String getResults(){
		return noResults;
	}
	
	/**
	 * 
	 * Restricts the search to given model
	 * 
	 * @param model
	 */
	public void setRestrictedModel(String model){
		this.restrictedModel = model;
	}
	
	/**
	 * 
	 * 
	 * Sets the text to search
	 * 
	 * @param text
	 */
	public void setTextSearch(String text){
		this.textSearch = text;
	}
	
	/**
	 * Opens a viewer of a search result
	 */
	public void openSearchResult(){
			
		Map<String,String> params = getRequestContext().getRequestParameterMap();
		String boui = params.get("bouiOpen");
		long bouiParsed = Long.valueOf(boui);
		
		if (!(bouiParsed > 0))
			throw new IllegalArgumentException("Not a valid BOUI to open the object");
		
		boObject toOpenInViewerResult = null;
		String className = "";
		try {
			toOpenInViewerResult = boApplication.getDefaultApplication().getObjectManager()
				.loadObject(getEboContext(), bouiParsed);
			className = toOpenInViewerResult.getName();
			
			//Create a XUIViewRoot representing the viewer (viewer can be located in the source code, or in the webapp of the application)
			XUIViewRoot viewRoot = getSessionContext().createChildView("viewers/"+className+"/edit.xvw");
			
			XEOEditBean editBean = (XEOEditBean) viewRoot.getBean("viewBean");
			editBean.setCurrentObjectKey(boui);
			
			//Set the newly created viewroot as the view root of the request  
			getRequestContext().setViewRoot(viewRoot);  
			//Render the response
			getRequestContext().renderResponse();  
		
		} catch (boRuntimeException e) {
			logger.severe("Could not load object with BOUI = " + bouiParsed, e);
			throw new IllegalArgumentException("Could not load object with BOUI " + bouiParsed);
		}	
		
	}
	
}
