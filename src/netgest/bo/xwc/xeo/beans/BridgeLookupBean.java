package netgest.bo.xwc.xeo.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.PhaseEvent;

import netgest.bo.data.DataRow;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectListBuilder;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.AttributeBase;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.connectors.DataListConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectListConnector;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.xeo.components.Bridge;
import netgest.bo.xwc.xeo.components.utils.LookupFavorites;

/**
 * 
 * Bean to support the favorites of a BridgeLookup
 * 
 * @author PedroRio
 *
 */
public class BridgeLookupBean extends XEOBaseBean {

	private boolean multiSelect;
	
	/**
	 * @return the multiSelect
	 */
	public boolean isMultiSelect() {
		return multiSelect;
	}

	/**
	 * @param multiSelect the multiSelect to set
	 */
	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	public void beforeRenderView( PhaseEvent e ) {
		if( this.multiSelect ) {
			if( !getViewRoot().isPostBack() ) {
				// Disable click event for multi row selection
				GridPanel panel = (GridPanel)getViewRoot().findComponent( GridPanel.class );
				panel.setOnRowClick(null);
			}
		}
	}
	
	public String getGridSelectionMode() {
		
		if( this.multiSelect )
			return GridPanel.SELECTION_MULTI_ROW;
		
		return GridPanel.SELECTION_ROW;
		
	}
	
	/**
	 * 
	 * Retrieves the list of instances that should appear in the
	 * favorites list
	 * 
	 * @return 
	 * @throws boRuntimeException 
	 */
	public DataListConnector getDataSource() throws boRuntimeException{
		
		XUIComponentBase oComp = (XUIComponentBase)getParentView().findComponent(parentComponentId);
		long[] bouis = null;
		//Bouis are usally retrieved from the preference, but  user can override it
		boolean retrieveBouisFromDefaultSource = true;
		String attributeName = "";
		if (oComp instanceof AttributeBase){
			List<Long> listOfBouiWithFavorites = ((AttributeBase) oComp).getListFavorites();
			attributeName = ((AttributeBase) oComp).getObjectAttribute();
			if (listOfBouiWithFavorites != null){
				bouis = new long[listOfBouiWithFavorites.size()];
				int n = 0;
				for (Iterator<Long> it = listOfBouiWithFavorites.iterator() ; it.hasNext(); n++)
					bouis[n] = it.next().longValue();
				retrieveBouisFromDefaultSource = false;
			}
			
		} else if (oComp instanceof Bridge){
			List<Long> listOfBouiWithFavorites = ((Bridge) oComp).getListFavorites();
			attributeName = ((Bridge) oComp).getObjectAttribute();
			if (listOfBouiWithFavorites != null){
				bouis = new long[listOfBouiWithFavorites.size()];
				int n = 0;
				for (Iterator<Long> it = listOfBouiWithFavorites.iterator() ; it.hasNext(); n++)
					bouis[n] = it.next().longValue();
				retrieveBouisFromDefaultSource = false;
			}
		}
		
		if (retrieveBouisFromDefaultSource){
			String objectName = getObjectName();
			bouis = LookupFavorites.getBouisFromPreference( objectName, 
					getAttributeName(), getEboContext(),true, 10);
			
			bouis = checkFavoritesAgainstFilters( bouis, attributeName );
			
		}	
		
		if( bouis.length > 0 ) {
			boObjectList list = boObjectList.list(getEboContext(),"boObject",bouis);
			
			if (list.getRecordCount() == bouis.length)
				return new XEOObjectListConnector(list);
			else{
				//This situation is when non-orphans are added to the parent (but not saved)
				//I need to manually added them to the list (because a list with a boui[] will always
				//query the database, but the non-orphan is not yet in the database
				for (long currentBoui : bouis){
					if (!list.haveBoui(currentBoui)){
						DataRow row = list.getRslt().getDataSet().createRow();
						row.updateLong("BOUI", currentBoui);
						list.getRslt().getDataSet().insertRow(row);
					}
				}
				return new XEOObjectListConnector(list);
			}
		}
		else {
			boObjectList list = boObjectList.list(getEboContext(),"select iXeoUser where 0=1");
			return new XEOObjectListConnector( list );
		}
		
	}

	private long[] checkFavoritesAgainstFilters( long[] bouis, String attributeName ) throws boRuntimeException {
		List<Long> checkedBouis = new ArrayList<Long>();
		boObject object = boObject.getBoManager().loadObject( getEboContext(), boui );
		String query = object.getAttribute( attributeName ).getFilterBOQL_query();
		boObjectList list = new boObjectListBuilder( getEboContext(), query ).build();
		list.beforeFirst();
		
		for (long curr : bouis){
			if (list.haveBoui( curr )){
				checkedBouis.add( Long.valueOf( curr ) );
			}
		}
		
		bouis = new long[checkedBouis.size()];
		int k = 0;
		for (Long current : checkedBouis){
			bouis[k] = current.longValue();
			k++;
		}
		return bouis;
	}
	
	
	
	/**
	 * Close the view
	 */
	public void canCloseTab(){
		XUIRequestContext oRequestContext;
		oRequestContext = XUIRequestContext.getCurrentContext();
		XVWScripts.closeView( oRequestContext.getViewRoot() );
    	XUIViewRoot viewRoot = oRequestContext.getSessionContext().createView(SystemViewer.DUMMY_VIEWER);
    	oRequestContext.setViewRoot( viewRoot );
		oRequestContext.renderResponse();
	}
	
	public void selectSingle() {
		if( !this.isInvokedBridge ) {
			select();
		}
	}
	
	/**
	 * Adds the instances to the bridge lookup component that invoked this
	 */
	public void select(){
		XUIRequestContext oRequestContext;
    	oRequestContext = XUIRequestContext.getCurrentContext();
        Object oParentBean = getParentView().getBean( "viewBean" );
        
        if( oParentBean != null )
        {
            GridPanel oGridComp;
            oGridComp = (GridPanel)oRequestContext.getViewRoot().findComponent( GridPanel.class );
            DataRecordConnector[] oSelectedRows = ((GridPanel)oGridComp).getSelectedRows();
    
            if (isInvokedBridge)
            	((XEOEditBean)oParentBean).setLookupBridgeResults(parentComponentId, oSelectedRows );
            else
            	((XEOEditBean)oParentBean).setLookupAttributeResults(parentComponentId, oSelectedRows );
	    }
        getParentView().syncClientView();
        canCloseTab();
	}
	
	/**
	 * The distance from the top of the screen
	 */
	private int top = 0;
	
	/**
	 * The distance from the left of the screen
	 */
	private int left = 0;
	
	/**
	 * Whether the viewer was invoked from a regular bridge or not
	 */
	private boolean isInvokedBridge = false;
	
	/**
	 * The name of the bridge associated with this favorites
	 */
	private String attributeName = "";
	
	/**
	 * The XEO Model name associated
	 */
	private String objectName;
	
	/**
	 * 
	 * Retrieves the distance from the top
	 * 
	 * @return The distance in pixels
	 */
	public int getTop(){
		return top;
	}
	
	public void setObjectName(String name){
		this.objectName = name;
	}
	
	public String getObjectName(){
		return this.objectName;
	}
	
	/**
	 * 
	 * Sets the top distance
	 * 
	 * @param top
	 */
	public void setTop(int top){
		this.top = top;
	}
	
	/**
	 * 
	 * 
	 * Retrieves the distance from the left of the screen
	 * 
	 * @return int with the distance from the left ( must be bigger than 0)
	 * 
	 */
	public int getLeft(){
		return left;
	}
	
	/**
	 * 
	 * Set the distance from the left
	 * 
	 * @param left Distance from left (must be > 0)
	 */
	public void setLeft(int left){
		this.left = left;
	}
	
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public void setAttributeName(String name){
		this.attributeName = name;
	}
	
	public boolean isInvokedFromBridge(){
		return isInvokedBridge;
	}
	
	public void setInvokedFromBridge(boolean invoked){
		this.isInvokedBridge = invoked;
	}
	
	private String parentComponentId;
	
	public String getParentComponentId(){
		return parentComponentId;
	}
	
	public void setParentComponentId(String comp){
		this.parentComponentId = comp;
	}
	
	private long boui;

	public void setParentObject( long boui ) {
		this.boui = boui;
	}
	
	public long getParentObject(){
		return boui;
	}
}

