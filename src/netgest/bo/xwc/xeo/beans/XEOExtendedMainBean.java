package netgest.bo.xwc.xeo.beans;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xeomodels.system.Theme;
import netgest.bo.xeomodels.system.ThemeIncludes;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

/**
 * 
 * Bean to support the global search component. Should be used to build a Main Layout
 * that required the xvw:globalSearch component
 * 
 */
public class XEOExtendedMainBean extends XEOMainBean {

	/**
	 * The logger
	 */
	private static final Logger logger = Logger.getLogger(XEOExtendedMainBean.class);
	
	/**
	 * Holds the text search of the user
	 */
	private String textSearch;
	
	/**
	 * Represents the selected theme
	 */
	private String theme = "";
	
	/**
	 * Retrieves the text the user submitted
	 * 
	 * @return A string with the text the user typed
	 */
	public String getTextSearch(){
		return textSearch;
	}
	
	/**
	 * 
	 * Sets the text the user wants to search
	 * 
	 * @param textSearch
	 */
	public void setTextSearch(String textSearch){
		this.textSearch = textSearch;
	}
	
	/**
	 * The set of models to restrict search
	 */
	private HashMap<String,Object> searchOptions = new HashMap<String,Object>();
	
	public HashMap<String,Object> getSearchOptions(){
		return searchOptions;
	}
	
	public void setSearchOptions(HashMap<String,Object> opts){
		searchOptions = opts;
	}
	
	/**
	 * The model to restrict searches to
	 */
	private String searchModel;
	
	/**
	 * 
	 * Sets the model to restrict searches
	 * 
	 * @param model The name of the model
	 */
	public void setSearchModel(String model){
		searchModel = model;
	}
	
	/**
	 * 
	 * Get the model to restrict searches
	 * 
	 * @return
	 */
	public String getSearchModel(){
		return searchModel;
	}
	
	//Global Search methods
	
	/**
	 * Executes the search method
	 */
	public void searchGlobal(){
		  XUIViewRoot viewRoot = getSessionContext().createChildView("netgest/bo/xwc/xeo/viewers/GlobalSearchResults.xvw");
		  //Set the newly created viewroot as the view root of the request  
		  getRequestContext().setViewRoot(viewRoot);
		  
		  GlobalSearchBean bean = (GlobalSearchBean) viewRoot.getBean("viewBean");
		  bean.setTextSearch(getTextSearch());
		  String[] results;
		  if (searchModel != null)
			  results = new String[]{searchModel};
		  else{
			  // Create an array containing the elements in a list
			  results = (String[]) searchOptions.keySet().toArray(new String[searchOptions.keySet().size()]);
		  }
			  
		  bean.executeSearch(getTextSearch(), results);
		  
		  //Render the response
		  getRequestContext().renderResponse(); 
	}
	
	
	
	
	//Theme related methods
	
	/**
	 * 
	 * Sets the identifier of the current theme
	 * 
	 * @param newTheme the boui of the new theme
	 */
	public void setTheme(String newTheme) {
		this.theme = newTheme;
		getEboContext().getBoSession().setProperty("theme", this.theme);
	}
	
	/**
	 * Changes the current theme with the selected one 
	 */
	public void changeTheme(){
		
		boObject themeObj;
		try {
			themeObj = boApplication.getDefaultApplication().getObjectManager().loadObject(getEboContext(), Long.parseLong(theme));
		 
			bridgeHandler filesIncludeHandler = themeObj.getBridge(Theme.FILES);
	    	Map<String,String> files = new HashMap<String, String>();
	    	filesIncludeHandler.beforeFirst();
	    	while(filesIncludeHandler.next()){
	    		boObject currentFileInclude = filesIncludeHandler.getObject();
	    		String id = currentFileInclude.getAttribute(ThemeIncludes.ID).getValueString();
	    		String path = currentFileInclude.getAttribute(ThemeIncludes.FILEPATH).getValueString();
	    		files.put(id, path);
	    	}
	    	getEboContext().getBoSession().getUser().setThemeFiles(files);
		}
    	catch (boRuntimeException e) {
			logger.severe(e);
		}
    	getRequestContext().getScriptContext().add(XUIScriptContext.POSITION_HEADER, "refreshPage", "location.reload(true);");
	}

	/**
	 * 
	 * Retrieves the identifier of the current theme
	 * 
	 * @return A string with the boui of the current theme
	 */
	public String getTheme() {
		Object themeTmp = getEboContext().getBoSession().getProperty("theme"); 
		if (themeTmp != null)
			return themeTmp.toString();
		return theme;
	}
	
	/**
	 * 
	 * Retrieves the map with all themes
	 * 
	 * @return A map with boui->Theme description
	 */
	public Map<Object, String> getThemeMap() {
		Map<Object, String> oThemeMap = new LinkedHashMap<Object, String>();

		boObjectList list = boObjectList.list(getEboContext(), "select Theme");
		list.beforeFirst();
		while (list.next()){
			boObject currTheme;
			try {
				currTheme = list.getObject();
				oThemeMap.put(currTheme.getBoui(), currTheme.getAttribute(Theme.DESCRIPTION).getValueString());
			} catch (boRuntimeException e) {
				logger.severe("Could not load Theme instances", e);
			}
			
		}
		return oThemeMap;
	}
	
}
