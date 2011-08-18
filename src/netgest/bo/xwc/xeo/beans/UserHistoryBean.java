package netgest.bo.xwc.xeo.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
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
 * 
 * A bean to support the user history viewer (the last set of instances the user added)
 *
 */
public class UserHistoryBean extends XEOBaseBean {

	
	/**
	 * The logger
	 */
	private static final Logger logger = Logger.getLogger(UserHistoryBean.class);
	
	/**
	 * String with the no results message
	 */
	private String noResults = "";
	
	/**
	 * Opens a viewer with a certain instance
	 */
	public void openHistoryResult(){
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
	
	/**
	 * Whether or not the SQL query with the search was performed
	 */
	private boolean searchPerformed = false;
	
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
	 * Retrieves a skeleton for a DataStore to use in the DataView
	 * 
	 * @return
	 */
	private ExtConfig getStoreSkeleton(){
		ExtConfig jsonStore = new ExtConfig("Ext.data.SimpleStore");
		
		ExtConfigArray fields = new ExtConfigArray();
		fields.addString("cardid");
		fields.addString("datesave");
		fields.addString("type");
		fields.addString("boui");
		
		
		jsonStore.add("fields", fields);
		
		return jsonStore;
	}
	
	public String getTitle(){
		return "<img src='ext-xeo/icons/history.png'>" + BeansMessages.HISTORY_BEAN_TITLE.toString();
	}
	
	/**
	 * 
	 * Execute the search and retrive the results (if the search was
	 * not yet performed)
	 * 
	 * @return A string with the html to publish
	 */
	public String getResults(){
		if (searchPerformed)
			return noResults;
		else{
			executeSearch(new String[0]);
			searchPerformed = true;
		}
		return noResults;
	}
	
	/**
	 * 
	 * Executes the search and sets the results
	 * 
	 * @param classRestrictions Restricts the search to specific models
	 */
	public void executeSearch(String[] classRestrictions){
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			
			conn = getEboContext().getConnectionData(); 
			st = conn.createStatement();
			DriverUtils dbUtils = getEboContext().getDataBaseDriver().getDriverUtils();
			String name = boDefHandler.getBoDefinition("Ebo_Registry").getBoMasterTable();
			String sql = "select ui$,SYS_DTSAVE,name from "+ name +" WHERE SYS_USER = '" + getEboContext().getBoSession().getUser().getUserName()  
				+ "' AND " + dbUtils.getQueryLimitStatement(20);
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
			sql += " ORDER BY SYS_DTSAVE DESC";
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
				//Columns must be added in the same order
				//as declared in the DataStore
				line.addString(current.getCARDID().toString());
				//Type:</span><img src="resources/{type}/ico16.gif"/> 
				line.addString( DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(current.getAttribute("SYS_DTSAVE").getValueDate())	);
				line.addString(boDefHandler.getBoDefinition(rs.getString("name")).getLabel());
				line.addString(String.valueOf(rs.getLong("ui$")));
				data.add(line.renderExtConfig());
			}
			
			if (k > 0)
				noResults = "";
			else
				noResults = "<h1>"+ BeansMessages.HISTORY_BEAN_NO_RESULTS.toString()+"</h1>";
			
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
					logger.severe("Could not close result set from history search",e);
				}
			if (st != null)
				try {
					st.close();
				} catch (SQLException e) {
					logger.severe("Could not close statement from history search",e);
				}
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					logger.severe("Could not close connection from history search",e);
				}
		}
		
		
		
	}
	
}
