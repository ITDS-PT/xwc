package netgest.bo.xwc.xeo.advancedSearch;

import java.util.HashMap;
import java.util.Map;

import netgest.bo.xwc.components.classic.CheckGroup;
import netgest.bo.xwc.components.classic.LovChooser;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.xeo.beans.AdvancedSearchBean;
import netgest.bo.xwc.xeo.beans.XEOBaseBean;

/**
 * Supports the process of the user choosing one or several values from a lov (used with ChooseLovValue.xvw)
 * 
 *
 */
public class AdvancedSearchLovValueChooserBean extends XEOBaseBean {

	/**
	 * The parent component id where to set the value 
	 */
	private String parentCompId = "";
	
	/**
	 * The name of the lov to display the value
	 */
	private String lovName = "";
	
	public void setParentComponentId(String componentId){
		this.parentCompId = componentId;
	}
	
	public void setLovName(String lovName){
		this.lovName = lovName;
	}
	
	/**
	 * The map with the choices to display in the viewer
	 */
	private Map<String,String> lovMap = new HashMap<String, String>();
	
	public Map<String,String> getLovMap(){
		return lovMap;
	}
	
	public void setLovMap(final Map<String,String> lovMap){
		this.lovMap = lovMap;
	}
	
	/**
	 * The values chosen by the user
	 */
	private String valuesChosen = "";
	
	public String getValuesChosen(){
		return valuesChosen;
	}
	
	public void setValuesChosen(String newValues){
		this.valuesChosen = newValues; 
	}
	
	/**
	 * Set the value in the parent component
	 */
	public void confirm(){
		
		AdvancedSearchBean parentBean = ( AdvancedSearchBean ) getParentView().getBean( "viewBean" );
		
		XVWScripts.closeView( getViewRoot() );
		
		CheckGroup checkGroupCmp = (CheckGroup) getViewRoot().findComponent( CheckGroup.class );
		Object value = checkGroupCmp.getValue();
		
		parentBean.setLovChoiceValueResult( parentCompId, value.toString() , lovName );
			
	}
	
	
	
}
