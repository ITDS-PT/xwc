package netgest.bo.xwc.xeo.advancedSearch;

import java.util.ArrayList;

import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.xeo.beans.AdvancedSearchBean;
import netgest.bo.xwc.xeo.beans.XEOBaseLookupList;

/**
 * 
 * Bean to support choosing a MOdel Instance (or several instances) by reusing a lookup viewer definition
 * but overriding the confirm method to allow seting the value in a different way
 *
 */
public class AdvancedSearchLookupBean extends XEOBaseLookupList {
	
	private String				selectedObject;
    
    public String getSelectedObject() {
		return selectedObject;
	}
	
	public void setSelectedObject(String selectedObject) {
		if( !selectedObject.equals( this.selectedObject ) ) {
			this.selectedObject = selectedObject;
				executeBoql( "select " + getSelectedObject() );
		}
	}
	
	//Sets the value in the parent component
	public void confirm(){
		
		
		Object oParentBean = getParentView().getBean( getParentParentBeanId() );
        
        if( oParentBean != null )
        {
            GridPanel oGridComp;
            oGridComp = (GridPanel) getRequestContext().getViewRoot().findComponent( GridPanel.class );
            DataRecordConnector[] oSelectedRows = ((GridPanel)oGridComp).getSelectedRows();

            XVWScripts.closeView( getViewRoot() );
            
            if (isMultiLookup()){
            	ArrayList<String> arr = new ArrayList<String>();
            	for (DataRecordConnector rec : oSelectedRows){
            		arr.add( rec.getAttribute( "BOUI" ).getValue().toString() );
            	}
            	((AdvancedSearchBean)oParentBean).setLookupValueResultBridge( getParentComponentId(), arr );
            }
            else
            	((AdvancedSearchBean)oParentBean).setLookupValueResult( getParentComponentId(), oSelectedRows[0].getAttribute( "BOUI" ).getValue().toString() );
        }
		
	}

}
