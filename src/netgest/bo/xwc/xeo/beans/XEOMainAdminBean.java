package netgest.bo.xwc.xeo.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import netgest.bo.data.DriverUtils;
import netgest.bo.def.boDefHandler;
import netgest.bo.ejb.boManagerLocal;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.localization.BeansMessages;


/**
 * 
 * Bean to support the main admin viewer
 *
 */
public class XEOMainAdminBean extends XEOExtendedMainBean {

	/**
	 * The logger
	 */
	private static final Logger logger = Logger.getLogger(XEOMainAdminBean.class);
	
	/**
	 * Keeps the generated list of favorites
	 */
	private String generatedFavorites = "";
	
	/**
	 * Keeps the generated list of history
	 */
	private String generatedHistory = "";
	
	
	/**
	 * 
	 * Generates an html list with the favorites of the user
	 * 
	 * @return
	 */
	public String getGeneratedFavorites(){
		if (generatedFavorites.length() == 0){
			
			StringBuilder b = new StringBuilder();
			b.append("<ul>");
			boObjectList list = boObjectList.list(getEboContext(), "select Ebo_UserPreferences.favorites where owner = CTX_PERFORMER_BOUI");
			while (list.next()){
				try {
					b.append("<li>");
					boObject currFavorite = list.getObject();
					final Long value = currFavorite.getAttribute("targetBouiObj").getValueLong();
					boObject theTarget = boApplication.getDefaultApplication().getObjectManager().loadObject(getEboContext(), value);
					String script = "XVW.openCommandTab( 'Frame_"+value+"','formMain','favorite','',null,true);";
					b.append("<a href='javascript:void(0)'");
					b.append(" onClick=\"document.getElementById('currentFavItem').value="+value+";"+script+"\"");
					b.append(">");
					b.append(theTarget.getCARDID().toString());
					b.append("</a>");
					b.append("</li>");
				} catch (boRuntimeException e) {
					e.printStackTrace();
					logger.warn(e);
				}
			}
			b.append("</ul>");
			generatedFavorites = b.toString();
		}
		return generatedFavorites;
	}
	
	/**
	 * 
	 * Generates an html list with the history of saved objects
	 * 
	 * @return
	 */
	public String getGeneratedHistory(){
		if (generatedHistory.length() == 0){
			
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			try{
				
				conn = getEboContext().getConnectionData(); 
				st = conn.createStatement();
				DriverUtils dbUtils = getEboContext().getDataBaseDriver().getDriverUtils();
				String name = boDefHandler.getBoDefinition("Ebo_Registry").getBoMasterTable();
				String sql = "select ui$,SYS_DTSAVE,name from "+ name +" WHERE SYS_USER = '" + getEboContext().getBoSession().getUser().getUserName()  
					+ "' AND " + dbUtils.getQueryLimitStatement(5);
				//sql += " AND name <> 'XVWAccessPolicy' ";
				sql += " ORDER BY SYS_DTSAVE DESC";
				rs = st.executeQuery(sql);
				
				StringBuilder b = new StringBuilder(500);
				b.append("<ul>");
				
				boManagerLocal objectManager = boApplication.getDefaultApplication().getObjectManager();
				
				//Retrieve the store skeleton and fill data
				int k = 0;
				while (rs.next()){
					b.append("<li>");
					boObject current = objectManager.loadObject(getEboContext(),rs.getLong("ui$"));
					k++;
					//Columns must be added in the same order
					//as declared in the DataStore
					b.append("<a href='javascript:void(0)'");
					String script = "XVW.openCommandTab( 'Frame_"+current.getBoui()+"','formMain','history','',null,true);";
					b.append(" onClick=\"document.getElementById('currentHistoryItem').value="+current.getBoui()+";"+script+"\"");
					b.append(">"+current.getCARDID().toString()+"</a>");
					b.append("</li>");
				}
				
				b.append("</ul>");
				
				if (k == 0)
					b.append(BeansMessages.HISTORY_BEAN_NO_RESULTS.toString());
				
				
				generatedHistory = b.toString();
				
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
		return generatedHistory;
	}
	
	/**
	 * Opens a viewer with a favorite
	 * @throws boRuntimeException 
	 */
	public void openFavorite() throws boRuntimeException{
		
		  Map<String,String> parameters = getRequestContext().getRequestParameterMap();
		  String valueSubmited = parameters.get("currentFavItem");
		  String objectBoui = "";
		  
		  //Sanityze the submited value, it must be a valid BOUI
		  try{
			  objectBoui = Long.valueOf(valueSubmited).toString();
		  }catch (NumberFormatException e){
			  objectBoui = "0";
		  }	
		  
		  boObject currentObjectToLoad = boApplication.getDefaultApplication().
		  	getObjectManager().loadObject(getEboContext(), Long.valueOf(objectBoui));
		  
		  String classname = currentObjectToLoad.getName();
		
		 //Create a XUIViewRoot representing the viewer (viewer can be located in the source code, or in the webapp of the application)
		  XUIViewRoot viewRoot = getSessionContext().createChildView("viewers/"+classname+"/edit.xvw");
		  //Set the newly created viewroot as the view root of the request  
		  getRequestContext().setViewRoot(viewRoot);  
		  
		  
		  
		  XEOEditBean bean = (XEOEditBean)viewRoot.getBean("viewBean");
		  bean.setCurrentObjectKey(objectBoui);
		  
		  //Render the response
		  getRequestContext().renderResponse();  
		
	}
	
	
	
	/**
	 * Opens a viewer with a favorite
	 * @throws boRuntimeException 
	 */
	public void openHistory() throws boRuntimeException{
		
		  Map<String,String> parameters = getRequestContext().getRequestParameterMap();
		  String valueSubmited = parameters.get("currentHistoryItem");
		  String objectBoui = "";
		  
		  //Sanityze the submited value, it must be a valid BOUI
		  try{
			  objectBoui = Long.valueOf(valueSubmited).toString();
		  }catch (NumberFormatException e){
			  objectBoui = "0";
		  }	
		  
		  boObject currentObjectToLoad = boApplication.getDefaultApplication().
		  	getObjectManager().loadObject(getEboContext(), Long.valueOf(objectBoui));
		  
		  String classname = currentObjectToLoad.getName();
		
		 //Create a XUIViewRoot representing the viewer (viewer can be located in the source code, or in the webapp of the application)
		  XUIViewRoot viewRoot = getSessionContext().createChildView("viewers/"+classname+"/edit.xvw");
		  //Set the newly created viewroot as the view root of the request  
		  getRequestContext().setViewRoot(viewRoot);  
		  
		  
		  
		  XEOEditBean bean = (XEOEditBean)viewRoot.getBean("viewBean");
		  bean.setCurrentObjectKey(objectBoui);
		  
		  //Render the response
		  getRequestContext().renderResponse();  
		
	}
	
	
}
